package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.AppDatabase
import com.example.data.ScanItem
import com.example.data.ScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

sealed interface ScanUiState {
    object Idle : ScanUiState
    object Loading : ScanUiState
    data class Success(val resultText: String, val scanItem: ScanItem) : ScanUiState
    data class Error(val errorMessage: String) : ScanUiState
}

class KrishiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ScanRepository(database.scanItemDao())

    // Language state
    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // Scan result state
    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    // History state
    val scanHistory: StateFlow<List<ScanItem>> = repository.allScans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun resetState() {
        _scanUiState.value = ScanUiState.Idle
    }

    // Save Bitmap to internal storage and return the absolute path
    private suspend fun saveImageToInternalStorage(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val context = getApplication<Application>().applicationContext
        val filename = "scan_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        file.absolutePath
    }

    // Convert Bitmap to Base64 String
    private suspend fun Bitmap.toBase64(): String = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun deleteScan(scanItem: ScanItem) {
        viewModelScope.launch(Dispatchers.IO) {
            // Delete the local file if it exists
            scanItem.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
            repository.deleteScanById(scanItem.id)
        }
    }

    fun saveBitmapAndInsertScan(bitmap: Bitmap?, scanType: String, cropName: String, detectedIssue: String, advice: String, language: String) {
        viewModelScope.launch {
            val localImagePath = bitmap?.let { saveImageToInternalStorage(it) }
            val scanItem = ScanItem(
                scanType = scanType,
                cropName = cropName,
                detectedIssue = detectedIssue,
                advice = advice,
                language = language,
                imagePath = localImagePath
            )
            withContext(Dispatchers.IO) {
                repository.insertScan(scanItem)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            // Delete all saved image files
            val context = getApplication<Application>().applicationContext
            context.filesDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("scan_") && file.name.endsWith(".jpg")) {
                    file.delete()
                }
            }
            repository.clearHistory()
        }
    }

    fun performScan(bitmap: Bitmap?, customText: String, isReceiptScan: Boolean) {
        viewModelScope.launch {
            _scanUiState.value = ScanUiState.Loading

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _scanUiState.value = ScanUiState.Error(
                        "API Key not configured. Please add your GEMINI_API_KEY in the Secrets panel."
                    )
                    return@launch
                }

                // Save image path if bitmap is present
                val localImagePath = bitmap?.let { saveImageToInternalStorage(it) }

                // Build Prompt parts
                val parts = mutableListOf<Part>()
                
                // Add textual prompt based on scan type
                val defaultPrompt = if (isReceiptScan) {
                    "Scan and parse this Mandi/APMC transaction receipt. Extract the Crop Name, Quantity/Weight, Price per quintal, Date, Total payout, and any fees. Deliver the extracted details in a clean tabular format, followed by brief financial tips."
                } else {
                    "Examine this crop leaf image. Identify: 1. Crop Name, 2. Disease/Deficiency Name, 3. Causes, 4. Step-by-step Organic remedies, 5. Standard Chemical remedies. Format the output with clear bullet points, titles, and highlight key terms."
                }

                val finalPrompt = if (customText.trim().isNotEmpty()) {
                    "$defaultPrompt Additional User Question: $customText"
                } else {
                    defaultPrompt
                }

                parts.add(Part(text = finalPrompt))

                // Add base64 image if present
                if (bitmap != null) {
                    val base64Data = bitmap.toBase64()
                    parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data)))
                } else if (!isReceiptScan) {
                    // For crop scanner, if no image is uploaded, let's treat it as an informational query
                    parts.add(Part(text = "The user is asking a crop-related query without an image. Question: $customText"))
                }

                // Build System Instruction based on selected language
                val languageStr = _selectedLanguage.value
                val systemInstructionText = """
                    You are "KrishiDrishti" (Smart Farmer Assistant), an expert agricultural AI advisor.
                    Your goal is to provide deep, detailed, and highly practical agricultural advisory, crop health diagnosis, and mandi market bill analysis for Indian farmers.
                    
                    CRITICAL INSTRUCTION:
                    You MUST respond completely and fluently in the requested language: $languageStr.
                    Even if technical terms (like chemical names) are kept in English or transliterated, all instructions, explanations, remedies, and descriptions must be in the native script of $languageStr.
                    Use clear Material-design styled headings, clean lists, and an encouraging, highly professional tone.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = parts)),
                    generationConfig = GenerationConfig(
                        temperature = 1.0f,
                        topP = 0.95f,
                        topK = 64
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(
                        model = "gemini-3.5-flash",
                        apiKey = apiKey,
                        request = request
                    )
                }

                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    val scanItem = ScanItem(
                        scanType = if (isReceiptScan) "RECEIPT" else "CROP",
                        cropName = if (isReceiptScan) "Mandi Receipt" else extractCropNameFromResponse(responseText),
                        detectedIssue = if (isReceiptScan) "Parsed Bill" else extractIssueFromResponse(responseText),
                        advice = responseText,
                        language = languageStr,
                        imagePath = localImagePath
                    )

                    withContext(Dispatchers.IO) {
                        val insertedId = repository.insertScan(scanItem)
                        val savedItem = scanItem.copy(id = insertedId.toInt())
                        _scanUiState.value = ScanUiState.Success(responseText, savedItem)
                    }
                } else {
                    _scanUiState.value = ScanUiState.Error("No response received from KrishiDrishti AI. Please try again.")
                }

            } catch (e: Exception) {
                _scanUiState.value = ScanUiState.Error("Failed to analyze: ${e.localizedMessage ?: "Unknown Error"}")
            }
        }
    }

    private fun extractCropNameFromResponse(response: String): String {
        // Simple extraction logic for displaying in history header
        val lines = response.lines()
        for (line in lines) {
            if (line.contains("Crop", ignoreCase = true) || line.contains("పంట", ignoreCase = true) || line.contains("फसल", ignoreCase = true)) {
                val clean = line.replace(Regex("[#*:\\-]"), "").trim()
                if (clean.length in 3..25) return clean
            }
        }
        return "Crop Leaf Scan"
    }

    private fun extractIssueFromResponse(response: String): String {
        val lines = response.lines()
        for (line in lines) {
            if (line.contains("Disease", ignoreCase = true) || line.contains("Issue", ignoreCase = true) || line.contains("సమస్య", ignoreCase = true) || line.contains("रोग", ignoreCase = true)) {
                val clean = line.replace(Regex("[#*:\\-]"), "").trim()
                if (clean.length in 3..35) return clean
            }
        }
        return "Leaf Spots / Healthy"
    }
}
