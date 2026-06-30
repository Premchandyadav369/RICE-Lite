package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Slider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.KrishiViewModel
import com.example.ui.ScanUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val uiState by viewModel.scanUiState.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var customQuery by remember { mutableStateOf("") }
    var scanTypeIsReceipt by remember { mutableStateOf(false) } // false = Crop Leaf, true = Mandi Receipt

    val languages = listOf(
        "English", "Hindi (हिन्दी)", "Telugu (తెలుగు)", "Tamil (தமிழ்)",
        "Marathi (మరాठी)", "Bengali (বাংলা)", "Kannada (ಕನ್ನಡ)",
        "Gujarati (ગુજરાતી)", "Malayalam (മലയാളം)", "Punjabi (ਪੰਜਾਬੀ)", "Odia (ଓଡ଼ିଆ)"
    )
    var languageExpanded by remember { mutableStateOf(false) }

    // Launcher for selecting an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            }
        }
    }

    // --- AI SMART AGRICULTURE TOOLS STATE ---
    var activeTool by remember { mutableStateOf<String?>(null) }

    // Crop Sowing fields
    var sowingState by remember { mutableStateOf("Maharashtra") }
    var soilType by remember { mutableStateOf("Black Clayey Soil") }
    var sowingSeason by remember { mutableStateOf("Kharif (Monsoon)") }
    var stateMenuExpanded by remember { mutableStateOf(false) }
    var soilMenuExpanded by remember { mutableStateOf(false) }
    var seasonMenuExpanded by remember { mutableStateOf(false) }

    val indianStates = listOf(
        "Andhra Pradesh", "Telangana", "Maharashtra", "Uttar Pradesh", "Punjab",
        "Gujarat", "Karnataka", "Madhya Pradesh", "Rajasthan", "Haryana",
        "Tamil Nadu", "Bihar", "West Bengal"
    )
    val soilTypes = listOf(
        "Black Clayey Soil", "Red Sandy Soil", "Alluvial Loam Soil", "Laterite Soil", "Sandy Desert Soil"
    )
    val sowingSeasons = listOf(
        "Kharif (Monsoon)", "Rabi (Winter)", "Zaid (Summer)"
    )

    // Fertilizer fields
    var fertilizerCrop by remember { mutableStateOf("Wheat") }
    var soilCondition by remember { mutableStateOf("Medium / Average") }
    var soilConditionMenuExpanded by remember { mutableStateOf(false) }
    val soilConditions = listOf(
        "Poor / Low Nutrients", "Medium / Average", "Rich / Well Fertilized"
    )

    // Pest Formulation fields
    var pestCrop by remember { mutableStateOf("Cotton") }
    var targetPest by remember { mutableStateOf("Pink Bollworm") }

    // Market Forecast fields
    var forecastCrop by remember { mutableStateOf("Onion") }

    // --- TEXT TO SPEECH (TTS) LIFE-CYCLE SETUP ---
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        tts = speech
        onDispose {
            speech.stop()
            speech.shutdown()
        }
    }

    fun speakAdvice(text: String, language: String) {
        val speech = tts ?: return
        if (!isTtsReady) return
        
        val locale = when (language) {
            "Hindi (हिन्दी)" -> Locale("hi", "IN")
            "Telugu (తెలుగు)" -> Locale("te", "IN")
            "Tamil (தமிழ்)" -> Locale("ta", "IN")
            "Marathi (మరాठी)" -> Locale("mr", "IN")
            "Bengali (বাংলা)" -> Locale("bn", "IN")
            "Kannada (ಕನ್ನಡ)" -> Locale("kn", "IN")
            "Gujarati (ગુજરાતી)" -> Locale("gu", "IN")
            "Malayalam (മലയാളം)" -> Locale("ml", "IN")
            "Punjabi (ਪੰਜਾਬੀ)" -> Locale("pa", "IN")
            "Odia (ଓଡ଼ିଆ)" -> Locale("or", "IN")
            else -> Locale.ENGLISH
        }
        
        speech.language = locale
        isSpeaking = true

        speech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }
            override fun onDone(utteranceId: String?) {
                isSpeaking = false
            }
            override fun onError(utteranceId: String?) {
                isSpeaking = false
            }
        })

        if (Build.VERSION.SDK_INT >= 21) {
            speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "KrishiDrishtiTTS")
        } else {
            @Suppress("DEPRECATION")
            speech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        isSpeaking = false
    }

    // --- SPEECH TO TEXT (STT) LAUNCHER ---
    var voiceTargetField by remember { mutableStateOf("") } // "customQuery", "pestCrop", "targetPest", "fertilizerCrop", "forecastCrop"
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (spokenText != null) {
                when (voiceTargetField) {
                    "customQuery" -> customQuery = spokenText
                    "pestCrop" -> pestCrop = spokenText
                    "targetPest" -> targetPest = spokenText
                    "fertilizerCrop" -> fertilizerCrop = spokenText
                    "forecastCrop" -> forecastCrop = spokenText
                }
            }
        }
    }

    fun startVoiceInput(targetField: String) {
        voiceTargetField = targetField
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, when (selectedLanguage) {
                "Hindi (हिन्दी)" -> "hi-IN"
                "Telugu (తెలుగు)" -> "te-IN"
                "Tamil (தமிழ்)" -> "ta-IN"
                "Marathi (మరాठी)" -> "mr-IN"
                "Bengali (বাংলা)" -> "bn-IN"
                "Kannada (ಕನ್ನಡ)" -> "kn-IN"
                "Gujarati (ગુજરાતી)" -> "gu-IN"
                "Malayalam (മലയാളം)" -> "ml-IN"
                "Punjabi (ਪੰਜਾਬੀ)" -> "pa-IN"
                "Odia (ଓଡ଼ିଆ)" -> "or-IN"
                else -> "en-IN"
            })
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your query...")
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Voice input is not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- TOP BRAND HEADER BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circular logo with letter 'K'
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16A34A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "K",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Column {
                    Text(
                        text = "KrishiDrishti",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF14532D), // text-green-900
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "GEMMA POWERED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color(0xFF16A34A), // text-green-600
                        letterSpacing = 1.sp
                    )
                }
            }

            // Quick language display button/indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFF0FDF4))
                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(50.dp))
                    .clickable { languageExpanded = !languageExpanded }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (selectedLanguage.contains("(")) {
                            selectedLanguage.substringAfter("(").substringBefore(")")
                        } else {
                            "EN"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- PRIMARY ACTION CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF22C55E), Color(0xFF047857))
                        )
                    )
                    .padding(24.dp)
            ) {
                // Sun accent graphic at top-right
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFACC15).copy(alpha = 0.9f))
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).align(Alignment.Center)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (scanTypeIsReceipt) "Scan Mandi Invoice" else "Scan Your Crop",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (scanTypeIsReceipt) {
                            "Extract crop rates, quantities, and total payout instantly from bills."
                        } else {
                            "Identify diseases and get expert remedies in your local language."
                        },
                        color = Color(0xFFF0FDF4),
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF15803D)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Open Photo Gallery",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- QUICK STATS / GRID ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mandi Prices Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F2D1))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Small circular icon container
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = Color(0xFF166534),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "MANDI PRICES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534),
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "₹2,450",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF052E16)
                        )
                        Text(
                            text = "/qtl",
                            fontSize = 10.sp,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
            }

            // Schemes Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFFFEDD5), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E0))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Small circular icon container
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color(0xFF9A3412),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "SCHEMES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9A3412),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "PM-Kisan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF431407)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LIVE ASSISTANT BUBBLE ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gemma AI Assistant • Just now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    val welcomeMsg = when (selectedLanguage) {
                        "Hindi (हिन्दी)" -> "नमस्ते! अपनी फसल की तस्वीर अपलोड करें या कोई सवाल पूछें, मैं तुरंत आपकी सहायता करूँगा।"
                        "Telugu (తెలుగు)" -> "నమస్తే! మీ పంట చిత్రాన్ని అప్‌లోడ్ చేయండి లేదా ప్రశ్న అడగండి, నేను మీకు సహాయం చేస్తాను."
                        "Tamil (தமிழ்)" -> "வணக்கம்! உங்கள் பயிர் புகைப்படத்தைப் பதிவேற்றவும் அல்லது ஏதேனும் கேள்விகளைக் கேட்கவும்."
                        "Marathi (మరాठी)" -> "नमस्कार! तुमच्या पिकाचा फोटो अपलोड करा किंवा प्रश्न विचारा, मी मदत करेन."
                        "Bengali (বাংলা)" -> "নমস্কার! আপনার ফসলের ছবি আপলোড করুন বা যেকোনো প্রশ্ন জিজ্ঞাসা করুন।"
                        "Kannada (ಕನ್ನಡ)" -> "ನಮಸ್ಕಾರ! ನಿಮ್ಮ ಬೆಳೆಯ ಚಿತ್ರವನ್ನು ಅಪ್‌ಲೋಡ್ ಮಾಡಿ ಅಥವಾ ಪ್ರಶ್ನೆ ಕೇಳಿ."
                        "Gujarati (ગુજરાતી)" -> "નમસ્તે! તમારા પાકનો ફોટો અપલોડ કરો અથવા પ્રશ્ન પૂછો, હું મદદ કરીશ."
                        "Malayalam (മലയാളം)" -> "നമസ്കാരം! നിങ്ങളുടെ വിളയുടെ ചിത്രം അപ്‌ലോഡ് ചെയ്യുക അല്ലെങ്കിൽ ചോദ്യങ്ങൾ ചോദിക്കുക."
                        "Punjabi (ਪੰਜਾਬੀ)" -> "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ! ਆਪਣੀ ਫ਼ਸਲ ਦੀ ਤਸਵੀਰ ਅੱਪਲੋਡ ਕਰੋ ਜਾਂ ਕੋਈ ਸਵਾਲ ਪੁੱਛੋ।"
                        "Odia (ଓଡ଼ିଆ)" -> "ନମସ୍କାର! ଆପଣଙ୍କ ଫସଲର ଛବਿ ଅପଲୋଡ୍ କରନ୍ତು କିମ୍ବା ପ୍ରଶ୍ន ପଚାରନ୍ତು।"
                        else -> "Welcome to KrishiDrishti! Upload your crop photo or write details below for immediate AI expert analysis."
                    }
                    Text(
                        text = welcomeMsg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1E293B),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- AI SMART AGRICULTURE SUITE ---
        Text(
            text = "AI Smart Agriculture Suite",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Advanced planning & analysis powered entirely by Gemma 4",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tool action buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tools = listOf(
                Triple("sowing", "Planner", Icons.Default.Assignment),
                Triple("fertilizer", "Fertilizer", Icons.Default.Science),
                Triple("pest", "Pest DIY", Icons.Default.BugReport),
                Triple("forecast", "Forecast", Icons.Default.TrendingUp)
            )

            tools.forEach { (id, name, icon) ->
                val isSelected = activeTool == id
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            activeTool = if (isSelected) null else id
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = name,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Expanded input container for the active tool
        AnimatedVisibility(visible = activeTool != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (activeTool) {
                        "sowing" -> {
                            Text(
                                text = "🌾 Crop Planner & Sowing Advisor",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // State selection
                            var stateMenuExpandedLocal by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = stateMenuExpandedLocal,
                                onExpandedChange = { stateMenuExpandedLocal = !stateMenuExpandedLocal }
                            ) {
                                OutlinedTextField(
                                    value = sowingState,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Select Region/State") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateMenuExpandedLocal) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = stateMenuExpandedLocal,
                                    onDismissRequest = { stateMenuExpandedLocal = false }
                                ) {
                                    indianStates.forEach { state ->
                                        DropdownMenuItem(
                                            text = { Text(state) },
                                            onClick = {
                                                sowingState = state
                                                stateMenuExpandedLocal = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Soil selection
                            var soilMenuExpandedLocal by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = soilMenuExpandedLocal,
                                onExpandedChange = { soilMenuExpandedLocal = !soilMenuExpandedLocal }
                            ) {
                                OutlinedTextField(
                                    value = soilType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Soil Type") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = soilMenuExpandedLocal) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = soilMenuExpandedLocal,
                                    onDismissRequest = { soilMenuExpandedLocal = false }
                                ) {
                                    soilTypes.forEach { soil ->
                                        DropdownMenuItem(
                                            text = { Text(soil) },
                                            onClick = {
                                                soilType = soil
                                                soilMenuExpandedLocal = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Season selection
                            var seasonMenuExpandedLocal by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = seasonMenuExpandedLocal,
                                onExpandedChange = { seasonMenuExpandedLocal = !seasonMenuExpandedLocal }
                            ) {
                                OutlinedTextField(
                                    value = sowingSeason,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Sowing Season") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seasonMenuExpandedLocal) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = seasonMenuExpandedLocal,
                                    onDismissRequest = { seasonMenuExpandedLocal = false }
                                ) {
                                    sowingSeasons.forEach { season ->
                                        DropdownMenuItem(
                                            text = { Text(season) },
                                            onClick = {
                                                sowingSeason = season
                                                seasonMenuExpandedLocal = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val prompt = """
                                        [CROP SOWING BLUEPRINT]
                                        State: $sowingState
                                        Soil Type: $soilType
                                        Season: $sowingSeason
                                        Provide a highly customized sowing advisory. Recommend 3-4 best crops to grow under these conditions in $sowingState. For each crop, detail:
                                        1. Recommended high-yielding varieties.
                                        2. Perfect sowing depth and spacing.
                                        3. Expected maturity period.
                                        4. Estimated income potential.
                                    """.trimIndent()
                                    viewModel.performScan(null, prompt, false)
                                    activeTool = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Generate Crop Plan with Gemma 4", fontWeight = FontWeight.Bold)
                            }
                        }
                        "fertilizer" -> {
                            Text(
                                text = "🧪 Fertilizer Dose Doctor",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Target Crop
                            OutlinedTextField(
                                value = fertilizerCrop,
                                onValueChange = { fertilizerCrop = it },
                                label = { Text("Target Crop Name") },
                                trailingIcon = {
                                    IconButton(onClick = { startVoiceInput("fertilizerCrop") }) {
                                        Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Soil Nutrient status selection
                            var soilConditionMenuExpandedLocal by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = soilConditionMenuExpandedLocal,
                                onExpandedChange = { soilConditionMenuExpandedLocal = !soilConditionMenuExpandedLocal }
                            ) {
                                OutlinedTextField(
                                    value = soilCondition,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Soil Nutrient Condition") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = soilConditionMenuExpandedLocal) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = soilConditionMenuExpandedLocal,
                                    onDismissRequest = { soilConditionMenuExpandedLocal = false }
                                ) {
                                    soilConditions.forEach { cond ->
                                        DropdownMenuItem(
                                            text = { Text(cond) },
                                            onClick = {
                                                soilCondition = cond
                                                soilConditionMenuExpandedLocal = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val prompt = """
                                        [FERTILIZER DOSE BLUEPRINT]
                                        Target Crop: $fertilizerCrop
                                        Soil Condition: $soilCondition
                                        Provide a comprehensive nutrient and fertilizer schedule for growing $fertilizerCrop under $soilCondition soil health. Detail:
                                        1. N-P-K requirement (kg per acre).
                                        2. Basal dressing (at sowing time) vs Top dressing (later stages) schedule.
                                        3. Organic alternatives (Compost, Vermicompost, Biofertilizers).
                                        4. Micronutrient tips.
                                    """.trimIndent()
                                    viewModel.performScan(null, prompt, false)
                                    activeTool = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                enabled = fertilizerCrop.trim().isNotEmpty()
                            ) {
                                Text("Diagnose Fertilizer Schedule", fontWeight = FontWeight.Bold)
                            }
                        }
                        "pest" -> {
                            Text(
                                text = "🐜 Organic Pest DIY Formulation Maker",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Crop
                            OutlinedTextField(
                                value = pestCrop,
                                onValueChange = { pestCrop = it },
                                label = { Text("Crop Name") },
                                trailingIcon = {
                                    IconButton(onClick = { startVoiceInput("pestCrop") }) {
                                        Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Pest / Symptom
                            OutlinedTextField(
                                value = targetPest,
                                onValueChange = { targetPest = it },
                                label = { Text("Pest Name or Symptoms") },
                                trailingIcon = {
                                    IconButton(onClick = { startVoiceInput("targetPest") }) {
                                        Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val prompt = """
                                        [DIY ORGANIC PESTICIDE]
                                        Target Crop: $pestCrop
                                        Target Pest/Symptom: $targetPest
                                        Provide a precise step-by-step DIY recipe to make a local organic pesticide/repellent to cure $targetPest on $pestCrop. Detail:
                                        1. Ingredients needed (such as Neem leaves, ginger, garlic, chili, cow urine).
                                        2. Step-by-step preparation and fermentation process.
                                        3. Dilution ratio and spraying frequency.
                                        4. Safety and storage guidelines.
                                    """.trimIndent()
                                    viewModel.performScan(null, prompt, false)
                                    activeTool = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                enabled = pestCrop.trim().isNotEmpty() && targetPest.trim().isNotEmpty()
                            ) {
                                Text("Generate Organic Formula Recipe", fontWeight = FontWeight.Bold)
                            }
                        }
                        "forecast" -> {
                            Text(
                                text = "📊 Market Price Trend Forecast",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Crop
                            OutlinedTextField(
                                value = forecastCrop,
                                onValueChange = { forecastCrop = it },
                                label = { Text("Crop Name") },
                                trailingIcon = {
                                    IconButton(onClick = { startVoiceInput("forecastCrop") }) {
                                        Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val prompt = """
                                        [MANDI MARKET PRICE FORECAST]
                                        Crop: $forecastCrop
                                        Based on current agricultural market trends in India, provide a detailed price forecast and trade advisory for $forecastCrop. Detail:
                                        1. Short-term price movement forecast (Next 30 days).
                                        2. Sell vs. Hold Strategy: Should the farmer sell now or store the crop to sell later?
                                        3. Key factors driving prices (monsoon, export policy, production volumes).
                                        4. Best premium markets/states in India for this crop.
                                    """.trimIndent()
                                    viewModel.performScan(null, prompt, false)
                                    activeTool = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                enabled = forecastCrop.trim().isNotEmpty()
                            ) {
                                Text("Generate Market Trend Report", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- ORGANIC SUGGESTION ENGINE & OFFLINE PORTAL ---
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "🇮🇳 Organic Suggestion Engine & Advisor",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Vedic organic bio-formulation advisor & 100% offline crop diagnostics companion",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Portal Sub-tabs selector - 3 premium tabs!
        var portalTab by remember { mutableStateOf("suggest_engine") } // "suggest_engine", "organic", "offline_doctor"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(4.dp)
        ) {
            val tabs = listOf(
                Triple("suggest_engine", if (selectedLanguage == "Hindi (हिन्दी)") "सुझाव इंजन" else "Advisor Engine", Icons.Default.Spa),
                Triple("organic", if (selectedLanguage == "Hindi (हिन्दी)") "DIY रेसिपी" else "DIY Recipes", Icons.Default.Science),
                Triple("offline_doctor", if (selectedLanguage == "Hindi (हिन्दी)") "ऑफ़लाइन डॉक्टर" else "Offline Doctor", Icons.Default.BugReport)
            )

            tabs.forEach { (id, label, icon) ->
                val isSelected = portalTab == id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { portalTab = id }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (portalTab == "suggest_engine") {
            // BRAND NEW ORGANIC SUGGESTION ENGINE
            var suggestCrop by remember { mutableStateOf("Paddy (Rice)") }
            var suggestProblem by remember { mutableStateOf("Sucking Pests") }
            var suggestSeverity by remember { mutableStateOf("Low / Preventative") }

            var isSuggestDropdownCrop by remember { mutableStateOf(false) }
            var isSuggestDropdownProblem by remember { mutableStateOf(false) }
            var isSuggestDropdownSeverity by remember { mutableStateOf(false) }

            // Dynamic calculation of organic advisor parameters
            val advice = remember(suggestCrop, suggestProblem, suggestSeverity) {
                val name: String
                val nameHi: String
                val desc: String
                val descHi: String
                val ingredients: List<String>
                val ingredientsHi: List<String>
                val dilution: String
                val dilutionHi: String
                val costOrganic: String
                val costChemical: String
                val soilImpact: String
                val soilImpactHi: String
                val savingsPct: String

                when (suggestProblem) {
                    "Sucking Pests" -> {
                        name = "Neem-astra"
                        nameHi = "नीमास्त्र (Neem-astra)"
                        desc = "A powerful neem-based bio-spray designed to repel small sucking insects, aphids, thrips, and whiteflies without chemicals."
                        descHi = "एक शक्तिशाली नीम-आधारित जैव-स्प्रे जिसे रसायनों के बिना छोटे रस चूसने वाले कीड़ों, एफिड्स, थ्रिप्स और सफेद मक्खियों को भगाने के लिए डिज़ाइन किया गया है।"
                        ingredients = listOf("10 kg Neem Leaves (crushed)", "2 kg Fresh Cow Dung", "10 Liters Cow Urine", "200 Liters Water")
                        ingredientsHi = listOf("10 किलो नीम के पत्ते (कुचले हुए)", "2 किलो ताजा गाय का गोबर", "10 लीटर गोमूत्र", "200 लीटर पानी")
                        dilution = when (suggestSeverity) {
                            "Low / Preventative" -> "Mix 1 Liter of Neem-astra with 20 Liters of water. Spray once every 14 days."
                            "Moderate Spread" -> "Mix 1 Liter of Neem-astra with 15 Liters of water. Spray twice with a 7-day interval."
                            else -> "Mix 1 Liter of Neem-astra with 10 Liters of water. Spray immediately and repeat every 5 days."
                        }
                        dilutionHi = when (suggestSeverity) {
                            "Low / Preventative" -> "1 लीटर नीमास्त्र को 20 लीटर पानी में मिलाएं। हर 14 दिन में एक बार छिड़काव करें।"
                            "Moderate Spread" -> "1 लीटर नीमास्त्र को 15 लीटर पानी में मिलाएं। 7 दिनों के अंतराल पर दो बार छिड़काव करें।"
                            else -> "1 लीटर नीमास्त्र को 10 लीटर पानी में मिलाएं। तुरंत छिड़काव करें और हर 5 दिन में दोहराएं।"
                        }
                        costOrganic = "₹70"
                        costChemical = "₹750"
                        savingsPct = "90%"
                        soilImpact = "100% Eco-Safe. Harmless to bees, butterflies, and earthworms. Zero residual chemicals."
                        soilImpactHi = "100% पर्यावरण-सुरक्षित। मधुमक्खियों, तितलियों और केंचुओं के लिए हानिरहित। शून्य अवशेष रसायन।"
                    }
                    "Borers & Caterpillars" -> {
                        val useBrahm = suggestSeverity == "Severe Outbreak"
                        name = if (useBrahm) "Brahm-astra" else "Agni-astra"
                        nameHi = if (useBrahm) "ब्रह्मास्त्र (Brahm-astra)" else "अग्न्यास्त्र (Agni-astra)"
                        desc = if (useBrahm) {
                            "A highly concentrated botanical formulation using 5 varieties of bitter leaves. Best for severe caterpillar outbreaks."
                        } else {
                            "A hot botanical liquid made with chili, garlic, tobacco, and cow urine to combat stem borers, leaf rollers, and bollworms."
                        }
                        descHi = if (useBrahm) {
                            "5 प्रकार की कड़वी पत्तियों का उपयोग करके एक अत्यधिक संकेंद्रीय वनस्पति फॉर्मूलेशन। गंभीर कैटरपिलर प्रकोप के लिए सर्वोत्तम।"
                        } else {
                            "तना छेदक, पत्ती मोड़क और सुंडी से लड़ने के लिए मिर्च, लहसुन, तंबाकू और गोमूत्र से बना एक तीखा वनस्पति तरल।"
                        }
                        ingredients = if (useBrahm) {
                            listOf("10L Cow Urine", "3 kg Neem leaves", "2 kg Custard Apple leaves", "2 kg Papaya leaves", "2 kg Pomegranate leaves", "2 kg Guava leaves")
                        } else {
                            listOf("100L Cow Urine", "5 kg Neem leaves", "500g Hot Chili paste", "500g Garlic paste", "250g Dried Tobacco")
                        }
                        ingredientsHi = if (useBrahm) {
                            listOf("10 लीटर गोमूत्र", "3 किलो नीम के पत्ते", "2 किलो शरीफा के पत्ते", "2 किलो पपीता के पत्ते", "2 किलो अनार के पत्ते", "2 किलो अमरूद के पत्ते")
                        } else {
                            listOf("100 लीटर गोमूत्र", "5 किलो नीम के पत्ते", "500 ग्राम तीखी मिर्च का पेस्ट", "500 ग्राम लहसुन का पेस्ट", "250 ग्राम सूखा तंबाकू")
                        }
                        dilution = when (suggestSeverity) {
                            "Low / Preventative" -> "Dilute 2 Liters of mixture in 100 Liters of water. Spray early morning."
                            "Moderate Spread" -> "Dilute 3 Liters of mixture in 100 Liters of water. Spray twice, 7 days apart."
                            else -> "Dilute 5 Liters of mixture in 100 Liters of water. Spray thoroughly over leaves and stems."
                        }
                        dilutionHi = when (suggestSeverity) {
                            "Low / Preventative" -> "100 लीटर पानी में 2 लीटर मिश्रण मिलाएं। सुबह जल्दी छिड़काव करें।"
                            "Moderate Spread" -> "100 लीटर पानी में 3 लीटर मिश्रण मिलाएं। 7 दिनों के अंतराल पर दो बार छिड़काव करें।"
                            else -> "100 लीटर पानी में 5 लीटर मिश्रण मिलाएं। पत्तियों और तनों पर अच्छी तरह से छिड़काव करें।"
                        }
                        costOrganic = if (useBrahm) "₹180" else "₹110"
                        costChemical = "₹1200"
                        savingsPct = if (useBrahm) "87%" else "91%"
                        soilImpact = "Biodegradable repellent. Strong pungent smell drives insects away. Promotes rapid leaf recovery."
                        soilImpactHi = "बायोडिग्रेडेबल विकर्षक। तेज तीखी गंध कीड़ों को दूर भगाती है। तेजी से पत्तियों के सुधार को बढ़ावा देती है।"
                    }
                    "Fungal / Blights" -> {
                        name = "Sour Buttermilk spray"
                        nameHi = "खट्टी छाछ स्प्रे (Sour Buttermilk)"
                        desc = "Fermented buttermilk reacts to release copper-lacto fungicidal enzymes, clearing rusts, powdery mildews, and leaf blights."
                        descHi = "किण्वित छाछ कॉपर-लैक्टो कवकनाशी एंजाइमों को मुक्त करने के लिए प्रतिक्रिया करती है, जिससे गेरुआ, ख़स्ता फफूंदी और पत्ती झुलसा ठीक होता है।"
                        ingredients = listOf("5 Liters Sour Buttermilk (Fermented in a copper vessel for 10-15 days)", "100 Liters Water")
                        ingredientsHi = listOf("5 लीटर खट्टी छाछ (तांबे के बर्तन में 10-15 दिनों के लिए किण्वित की गई)", "100 लीटर पानी")
                        dilution = when (suggestSeverity) {
                            "Low / Preventative" -> "Mix 3 Liters of sour buttermilk in 100 Liters of water. Spray every 15 days."
                            "Moderate Spread" -> "Mix 5 Liters of sour buttermilk in 100 Liters of water. Spray every 7 days."
                            else -> "Mix 7-8 Liters of sour buttermilk in 100 Liters of water. Apply immediately at first sight of leaf spots."
                        }
                        dilutionHi = when (suggestSeverity) {
                            "Low / Preventative" -> "100 लीटर पानी में 3 लीटर खट्टी छाछ मिलाएं। हर 15 दिन में छिड़काव करें।"
                            "Moderate Spread" -> "100 लीटर पानी में 5 लीटर खट्टी छाछ मिलाएं। हर 7 दिन में छिड़काव करें।"
                            else -> "100 लीटर पानी में 7-8 लीटर खट्टी छाछ मिलाएं। पत्तों पर धब्बे दिखते ही तुरंत छिड़काव करें।"
                        }
                        costOrganic = "₹30"
                        costChemical = "₹850"
                        savingsPct = "96%"
                        soilImpact = "Lactic acid bacteria act as micro-shield on the leaf surface. Suppresses pathogenic fungal spores."
                        soilImpactHi = "लैक्टिक एसिड बैक्टीरिया पत्ती की सतह पर सूक्ष्म-कवच के रूप में कार्य करते हैं। रोगजनक कवक बीजाणुओं को दबाता है।"
                    }
                    "Soil Microbes & Root Booster" -> {
                        name = "Jeevamrutha"
                        nameHi = "जीवामृत (Jeevamrutha)"
                        desc = "A rich microbial inoculum that multiplies beneficial soil bacteria and fungi, unlocking trapped phosphorus and potash."
                        descHi = "एक समृद्ध सूक्ष्मजीव इनोकुलम जो मिट्टी के अनुकूल बैक्टीरिया और कवक को कई गुना बढ़ाता है, जिससे लॉक फास्फोरस और पोटाश अनलॉक होता है।"
                        ingredients = listOf("10 kg Cow Dung", "10 Liters Cow Urine", "1 kg Jaggery (Gur)", "1 kg Gram Flour (Besan)", "Handful of Forest/Undisturbed Soil", "200 Liters Water")
                        ingredientsHi = listOf("10 किलो गाय का गोबर", "10 लीटर गोमूत्र", "1 किलो गुड़", "1 किलो बेसन", "मुट्ठी भर जंगल की मिट्टी", "200 लीटर पानी")
                        dilution = when (suggestSeverity) {
                            "Low / Preventative" -> "Apply 200 Liters of Jeevamrutha per acre along with normal flood irrigation once a month."
                            "Moderate Spread" -> "Apply 400 Liters of Jeevamrutha per acre via irrigation or root drenching twice a month."
                            else -> "Drench roots of affected crops directly with a 1:10 dilution of Jeevamrutha every week."
                        }
                        dilutionHi = when (suggestSeverity) {
                            "Low / Preventative" -> "महीने में एक बार सामान्य सिंचाई के साथ प्रति एकड़ 200 लीटर जीवामृत का प्रयोग करें।"
                            "Moderate Spread" -> "सिंचाई या जड़ सिंचन के माध्यम से प्रति एकड़ 400 लीटर जीवामृत का महीने में दो बार प्रयोग करें।"
                            else -> "प्रभावित फसलों की जड़ों को हर हफ्ते जीवामृत के 1:10 घोल से सीधे सींचें।"
                        }
                        costOrganic = "₹120"
                        costChemical = "₹1800"
                        savingsPct = "93%"
                        soilImpact = "Adds billions of beneficial microbes per ml. Enhances earthworm activity and increases soil carbon."
                        soilImpactHi = "प्रति मिलीलीटर अरबों अनुकूल रोगाणुओं को जोड़ता है। केंचुओं की गतिविधि को बढ़ाता है और मिट्टी की कार्बन को बढ़ाता है।"
                    }
                    else -> {
                        name = "Panchagavya"
                        nameHi = "पंचगव्य (Panchagavya)"
                        desc = "An organic liquid growth promoter prepared from 5 cow products. Acts as organic growth hormone and disease immunizer."
                        descHi = "गाय के 5 उत्पादों से तैयार एक जैविक तरल विकास प्रमोटर। जैविक विकास हार्मोन और रोग प्रतिरक्षक के रूप में कार्य करता है।"
                        ingredients = listOf("5 kg Cow Dung", "3 Liters Cow Urine", "2 Liters Cow Milk", "2 Liters Sour Curd", "1 kg Cow Ghee", "3 Liters Sugarcane juice", "12 ripe Bananas")
                        ingredientsHi = listOf("5 किलो गाय का गोबर", "3 लीटर गोमूत्र", "2 लीटर गाय का दूध", "2 लीटर खट्टा दही", "1 किलो गाय का घी", "3 लीटर गन्ने का रस", "12 पके केले")
                        dilution = when (suggestSeverity) {
                            "Low / Preventative" -> "Mix 3 Liters of Panchagavya in 100 Liters of water (3% solution). Spray every 21 days."
                            "Moderate Spread" -> "Mix 4 Liters of Panchagavya in 100 Liters of water. Spray every 14 days."
                            else -> "Mix 5 Liters of Panchagavya in 100 Liters of water. Spray every 7 days during critical growth stages."
                        }
                        dilutionHi = when (suggestSeverity) {
                            "Low / Preventative" -> "100 लीटर पानी में 3 लीटर पंचगव्य (3% घोल) मिलाएं। हर 21 दिन में छिड़काव करें।"
                            "Moderate Spread" -> "100 लीटर पानी में 4 लीटर पंचगव्य मिलाएं। हर 14 दिन में छिड़काव करें।"
                            else -> "100 लीटर पानी में 5 लीटर पंचगव्य मिलाएं। महत्वपूर्ण विकास चरणों के दौरान हर 7 दिन में छिड़काव करें।"
                        }
                        costOrganic = "₹350"
                        costChemical = "₹1600"
                        savingsPct = "78%"
                        soilImpact = "Drastic increase in chlorophyll, thicker leaves, more branching, higher flower retention, and sweeter taste."
                        soilImpactHi = "क्लोरोफिल में भारी वृद्धि, मोटे पत्ते, अधिक शाखाएं, अधिक फूल प्रतिधारण और फलों का मीठा स्वाद।"
                    }
                }

                mapOf(
                    "name" to name,
                    "nameHi" to nameHi,
                    "desc" to desc,
                    "descHi" to descHi,
                    "ingredients" to ingredients,
                    "ingredientsHi" to ingredientsHi,
                    "dilution" to dilution,
                    "dilutionHi" to dilutionHi,
                    "costOrganic" to costOrganic,
                    "costChemical" to costChemical,
                    "savingsPct" to savingsPct,
                    "soilImpact" to soilImpact,
                    "soilImpactHi" to soilImpactHi
                )
            }

            val advName = advice["name"] as String
            val advNameHi = advice["nameHi"] as String
            val advDesc = advice["desc"] as String
            val advDescHi = advice["descHi"] as String
            val advDilution = advice["dilution"] as String
            val advDilutionHi = advice["dilutionHi"] as String
            val advCostOrganic = advice["costOrganic"] as String
            val advCostChemical = advice["costChemical"] as String
            val advSavingsPct = advice["savingsPct"] as String
            val advSoilImpact = advice["soilImpact"] as String
            val advSoilImpactHi = advice["soilImpactHi"] as String

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector 1: Crop Select
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .clickable { isSuggestDropdownCrop = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") "फ़सल (Target Crop)" else "Target Crop",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)" && suggestCrop == "Paddy (Rice)") "धान (Paddy)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestCrop == "Wheat") "गेहूं (Wheat)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestCrop == "Cotton") "कपास (Cotton)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestCrop == "Tomato") "टमाटर (Tomato)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestCrop == "Chili") "मिर्च (Chili)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestCrop == "Potato") "आलू (Potato)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestCrop == "Onion") "प्याज़ (Onion)"
                                       else suggestCrop,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Icon(Icons.Default.Spa, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = isSuggestDropdownCrop,
                        onDismissRequest = { isSuggestDropdownCrop = false }
                    ) {
                        val crops = listOf("Paddy (Rice)", "Wheat", "Cotton", "Tomato", "Chili", "Potato", "Onion")
                        crops.forEach { crop ->
                            DropdownMenuItem(
                                text = { Text(crop) },
                                onClick = {
                                    suggestCrop = crop
                                    isSuggestDropdownCrop = false
                                }
                            )
                        }
                    }
                }

                // Selector 2: Problem Category
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .clickable { isSuggestDropdownProblem = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") "समस्या का प्रकार (Issue Type)" else "Crop Health Goal / Problem",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)" && suggestProblem == "Sucking Pests") "रस चूसने वाले कीट (Sucking Pests)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestProblem == "Borers & Caterpillars") "तना छेदक / सुंडी (Borers)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestProblem == "Fungal / Blights") "फफूंद / झुलसा (Fungal Blights)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestProblem == "Soil Microbes & Root Booster") "मिट्टी के जीवाणु और जड़ विकास"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestProblem == "Plant Growth, Immunity & Vitality") "पौधे की वृद्धि और रोग प्रतिरोध"
                                       else suggestProblem,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Icon(Icons.Default.BugReport, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = isSuggestDropdownProblem,
                        onDismissRequest = { isSuggestDropdownProblem = false }
                    ) {
                        val problems = listOf(
                            "Sucking Pests",
                            "Borers & Caterpillars",
                            "Fungal / Blights",
                            "Soil Microbes & Root Booster",
                            "Plant Growth, Immunity & Vitality"
                        )
                        problems.forEach { prob ->
                            DropdownMenuItem(
                                text = { Text(prob) },
                                onClick = {
                                    suggestProblem = prob
                                    isSuggestDropdownProblem = false
                                }
                            )
                        }
                    }
                }

                // Selector 3: Severity Level
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .clickable { isSuggestDropdownSeverity = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") "प्रकोप की तीव्रता (Severity)" else "Severity of Infection",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)" && suggestSeverity == "Low / Preventative") "कम / एहतियाती (Low)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestSeverity == "Moderate Spread") "मध्यम फैलाव (Moderate)"
                                       else if (selectedLanguage == "Hindi (हिन्दी)" && suggestSeverity == "Severe Outbreak") "गंभीर प्रकोप (Severe)"
                                       else suggestSeverity,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Icon(Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = isSuggestDropdownSeverity,
                        onDismissRequest = { isSuggestDropdownSeverity = false }
                    ) {
                        val severities = listOf("Low / Preventative", "Moderate Spread", "Severe Outbreak")
                        severities.forEach { sev ->
                            DropdownMenuItem(
                                text = { Text(sev) },
                                onClick = {
                                    suggestSeverity = sev
                                    isSuggestDropdownSeverity = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ADVISOR RESULT MATCH DISPLAY
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, Color(0xFF2E7D32).copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "🌱 अनुशंसित बायो-फॉर्मूलेशन:" else "🌱 Matched Bio-Formulation",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") advNameHi else advName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val textToSpeak = if (selectedLanguage == "Hindi (हिन्दी)") {
                                        "फ़सल: $suggestCrop. समस्या: $suggestProblem. सुझाई गई विधि: $advNameHi. विवरण: $advDescHi. खुराक और छिड़काव विधि: $advDilutionHi"
                                    } else {
                                        "For crop $suggestCrop with $suggestProblem. Recommending $advName. $advDesc Dilution instruction: $advDilution"
                                    }
                                    if (isSpeaking) {
                                        stopSpeaking()
                                    } else {
                                        speakAdvice(textToSpeak, selectedLanguage)
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeaking) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFFE8F5E9))
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Read Aloud",
                                    tint = if (isSpeaking) MaterialTheme.colorScheme.error else Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedLanguage == "Hindi (हिन्दी)") advDescHi else advDesc,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Ingredients needed
                        Text(
                            text = if (selectedLanguage == "Hindi (हिन्दी)") "🧪 आवश्यक सामग्री (Ingredients):" else "🧪 Raw Materials Needed:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        val ingredientsList = if (selectedLanguage == "Hindi (हिन्दी)") {
                            advice["ingredientsHi"] as List<String>
                        } else {
                            advice["ingredients"] as List<String>
                        }
                        ingredientsList.forEach { ing ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(ing, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dilution
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8F5E9).copy(alpha = 0.4f))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "💦 छिड़काव और खुराक विधि:" else "💦 Dilution & Spray Directions:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") advDilutionHi else advDilution,
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32),
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Cost savings card comparing organic vs chemical
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "💰 बजट और बचत विश्लेषण:" else "💰 Economic Savings Analysis:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(if (selectedLanguage == "Hindi (हिन्दी)") "जैविक लागत" else "Organic Cost", fontSize = 9.sp, color = Color.Gray)
                                        Text(advCostOrganic, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                                    }
                                    Column {
                                        Text(if (selectedLanguage == "Hindi (हिन्दी)") "रासायनिक लागत" else "Chemical Cost", fontSize = 9.sp, color = Color.Gray)
                                        Text(advCostChemical, fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 12.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF2E7D32))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (selectedLanguage == "Hindi (हिन्दी)") "बचत: $advSavingsPct" else "Save $advSavingsPct",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Safety and Eco impact chip
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") "पारिस्थितिक सुरक्षा: $advSoilImpactHi" else "Eco Safety: $advSoilImpact",
                                fontSize = 9.5.sp,
                                color = Color.DarkGray,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Trigger Gemma 4 customized analysis
                        Button(
                            onClick = {
                                val prompt = """
                                    [ADVANCED COMPREHENSIVE ORGANIC ADVICE]
                                    Crop: $suggestCrop
                                    Issue / Problem Category: $suggestProblem
                                    Severity: $suggestSeverity
                                    Matched Formulation: $advName
                                    Provide a detailed agronomy advice report on implementing $advName for $suggestCrop. Include:
                                    1. Active biochemical compounds in the mixture (e.g., Azadirachtin, Lactic acid, gibberellins).
                                    2. Exact timeline of application based on crop lifecycle stages.
                                    3. How to store the formulation to maximize potency.
                                    4. Warning signs where organic remedies must be supplemented by other eco-safe methods.
                                """.trimIndent()
                                viewModel.performScan(null, prompt, false)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Science, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "Gemma 4 से वैज्ञानिक विश्लेषण प्राप्त करें" else "Let Gemma 4 Scientific-Analyze",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        } else if (portalTab == "organic") {
            // DIY RECIPE HUB (Upgraded existing feature)
            var selectedRecipeName by remember { mutableStateOf("Neem-astra") }
            var checkedSteps by remember(selectedRecipeName) { mutableStateOf(setOf<Int>()) }

            val scope = rememberCoroutineScope()
            var isSimulatingPrep by remember(selectedRecipeName) { mutableStateOf(false) }
            var simPrepProgress by remember(selectedRecipeName) { mutableStateOf(0f) }
            var simPrepStatus by remember(selectedRecipeName) { mutableStateOf("") }

            var daysSincePrep by remember(selectedRecipeName) { mutableStateOf(0f) }

            // Horizontal scroll row of recipe titles
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(VedicKrishiData.recipes) { rec ->
                    val isSelected = rec.name == selectedRecipeName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable { selectedRecipeName = rec.name }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hindi (हिन्दी)") rec.nameHi.split(" ")[0] else rec.name,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val currentRecipe = remember(selectedRecipeName) {
                VedicKrishiData.recipes.firstOrNull { it.name == selectedRecipeName } ?: VedicKrishiData.recipes.first()
            }

            val expiryLimit = remember(selectedRecipeName) {
                when (selectedRecipeName) {
                    "Neem-astra" -> 3f // 3 days
                    "Jeevamrutha" -> 12f // 12 days
                    "Agni-astra" -> 90f // 3 months
                    "Brahm-astra" -> 180f // 6 months
                    else -> 180f
                }
            }
            val isExpired = daysSincePrep > expiryLimit

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with name and audio play
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") currentRecipe.nameHi else currentRecipe.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") "🎯 उपयोग: " + currentRecipe.primaryUseHi else "🎯 Uses: " + currentRecipe.primaryUse,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 16.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                val textToSpeak = if (selectedLanguage == "Hindi (हिन्दी)") {
                                    val ingredientsText = currentRecipe.ingredientsHi.joinToString(", ")
                                    val prepText = currentRecipe.preparationHi.joinToString(". ")
                                    "${currentRecipe.nameHi}. सामग्री: $ingredientsText. विधि: $prepText. उपयोग कैसे करें: ${currentRecipe.applicationHi}"
                                } else {
                                    val ingredientsText = currentRecipe.ingredients.joinToString(", ")
                                    val prepText = currentRecipe.preparation.joinToString(". ")
                                    "${currentRecipe.name}. Ingredients: $ingredientsText. Preparation steps: $prepText. Application method: ${currentRecipe.application}"
                                }
                                if (isSpeaking) {
                                    stopSpeaking()
                                } else {
                                    speakAdvice(textToSpeak, selectedLanguage)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Listen to Guide",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ingredients list
                    Text(
                        text = if (selectedLanguage == "Hindi (हिन्दी)") "🧪 आवश्यक सामग्री (Ingredients):" else "🧪 Required Ingredients:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val currentIngredients = if (selectedLanguage == "Hindi (हिन्दी)") currentRecipe.ingredientsHi else currentRecipe.ingredients
                    currentIngredients.forEach { ing ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(
                                text = ing,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // UPGRADE 1: STEP CHECKLIST FOR PREPARATION
                    Text(
                        text = if (selectedLanguage == "Hindi (हिन्दी)") "🥣 तैयार करने की विधि (DIY Checklist):" else "🥣 Step-by-Step DIY Preparation checklist:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val currentPrepList = if (selectedLanguage == "Hindi (हिन्दी)") currentRecipe.preparationHi else currentRecipe.preparation
                    currentPrepList.forEachIndexed { idx, step ->
                        val isStepChecked = checkedSteps.contains(idx)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    checkedSteps = if (isStepChecked) checkedSteps - idx else checkedSteps + idx
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = isStepChecked,
                                	onCheckedChange = {
                                    checkedSteps = if (isStepChecked) checkedSteps - idx else checkedSteps + idx
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isStepChecked) Color.Gray else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (isStepChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                modifier = Modifier.weight(1f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // UPGRADE 2: COUTDOWN PREP SIMULATION
                    Button(
                        onClick = {
                            if (!isSimulatingPrep) {
                                isSimulatingPrep = true
                                scope.launch {
                                    val steps = listOf(
                                        "Crushing raw neem and leaves...",
                                        "Blending with organic cow manure...",
                                        "Adding water and organic urine base...",
                                        "Aerating mixture clockwise (increasing microbes)...",
                                        "Fermenting under direct shade...",
                                        "Filtering pure organic liquid extract...",
                                        "Preparation Completed! Formulation is fully potent."
                                    )
                                    for (i in 0..100) {
                                        simPrepProgress = i / 100f
                                        val idx = (i / 15).coerceAtMost(steps.size - 1)
                                        simPrepStatus = steps[idx]
                                        delay(35)
                                    }
                                    delay(1000)
                                    isSimulatingPrep = false
                                    simPrepProgress = 0f
                                    simPrepStatus = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                            Text(if (isSimulatingPrep) "Simulating DIY Process..." else "⏱️ Simulate 48h Fermentation Process", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }

                    if (isSimulatingPrep) {
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { simPrepProgress },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(simPrepStatus, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // UPGRADE 3: POTENCY & EXPIRY STORAGE MONITOR
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isExpired) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⏱️ Potency Shelf-life Monitor",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isExpired) Color(0xFFC62828) else Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Age of mix: ${daysSincePrep.toInt()} days", fontSize = 10.sp, color = Color.DarkGray)
                                Text("Potency Limit: ${expiryLimit.toInt()} days", fontSize = 10.sp, color = Color.DarkGray)
                            }
                            Slider(
                                value = daysSincePrep,
                                onValueChange = { daysSincePrep = it },
                                valueRange = 0f..180f,
                                colors = androidx.compose.material3.SliderDefaults.colors(
                                    thumbColor = if (isExpired) Color(0xFFC62828) else Color(0xFF2E7D32),
                                    activeTrackColor = if (isExpired) Color(0xFFEF9A9A) else Color(0xFFA5D6A7)
                                )
                            )
                            Text(
                                text = if (isExpired) {
                                    "⚠️ EXPIRED! Active microbial cultures have degraded. Spraying this will be ineffective. Please prepare a fresh mix."
                                } else {
                                    "✅ POTENT & ACTIVE: Ideal chemical composition. Potency is at 100%. (Shelf-life remaining: ${(expiryLimit - daysSincePrep).toInt()} days)."
                                },
                                fontSize = 9.sp,
                                lineHeight = 12.sp,
                                color = if (isExpired) Color(0xFFC62828) else Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Application Instructions
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") "💦 छिड़काव की विधि:" else "💦 Spray & Application:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (selectedLanguage == "Hindi (हिन्दी)") currentRecipe.applicationHi else currentRecipe.application,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        } else {
            // OFFLINE CROP DOCTOR DIAGNOSIS MANUAL (Upgraded existing feature)
            var selectedCropName by remember { mutableStateOf("Paddy (Rice)") }
            val crops = listOf("Paddy (Rice)", "Wheat", "Cotton", "Tomato")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                crops.forEach { crp ->
                    val isSelected = crp == selectedCropName
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .clickable { selectedCropName = crp }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hindi (हिन्दी)" && crp == "Paddy (Rice)") "धान (Paddy)"
                                   else if (selectedLanguage == "Hindi (हिन्दी)" && crp == "Wheat") "गेहूं (Wheat)"
                                   else if (selectedLanguage == "Hindi (हिन्दी)" && crp == "Cotton") "कपास (Cotton)"
                                   else if (selectedLanguage == "Hindi (हिन्दी)" && crp == "Tomato") "टमाटर (Tomato)"
                                   else crp,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // UPGRADE 4: WEATHER-INFORMED SPRAYING ENVIRONMENT STABILITY CARD
            var simTemp by remember { mutableStateOf(27) }
            var simHumidity by remember { mutableStateOf(75) }
            var simWind by remember { mutableStateOf(10) }

            val spraySafetyScore = remember(simTemp, simHumidity, simWind) {
                var score = 100
                if (simWind > 15) score -= 35
                if (simTemp > 35) score -= 30
                if (simHumidity > 85) score -= 15
                score.coerceAtLeast(10)
            }

            val safetyAdvice = remember(simTemp, simHumidity, simWind) {
                if (simWind > 15) {
                    "⚠️ WIND DRIFT RISK: Current wind speed of $simWind km/h is above the safe threshold of 15 km/h. Spraying now will cause high formulation drift. Please wait for calmer early morning winds."
                } else if (simTemp > 35) {
                    "⚠️ LEAF BURN WARNING: Midday temperature of $simTemp°C is too hot. Spraying organic acidic formulations now will cause instant leaf scorching. Postpone spray until late evening."
                } else if (simHumidity > 85) {
                    "🌧️ RAIN WASH-OFF RISK: High humidity ($simHumidity%) indicates imminent rainfall. Mix a natural starch adhesive (rice gruel / cooked starch) with your spray to prevent wash-off."
                } else {
                    "✅ OPTIMAL ENVIRONMENT: Spraying safety score is excellent! Wind is calm ($simWind km/h) and temperature is mild ($simTemp°C). Perfect for maximum leaf absorption."
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2E7D32).copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "☀️ Live Spraying Environment Safety Checks",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20)
                        )
                        Button(
                            onClick = {
                                simTemp = (18..39).random()
                                simHumidity = (40..95).random()
                                simWind = (3..23).random()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Refresh Sensors", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(
                                    if (spraySafetyScore > 75) Color(0xFF2E7D32)
                                    else if (spraySafetyScore > 45) Color(0xFFF57C00)
                                    else Color(0xFFD32F2F)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$spraySafetyScore%", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Temp: ${simTemp}°C", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                Text("Humidity: $simHumidity%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                Text("Wind: $simWind km/h", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = safetyAdvice,
                                fontSize = 9.sp,
                                lineHeight = 12.sp,
                                color = if (spraySafetyScore > 75) Color(0xFF1B5E20) else if (spraySafetyScore > 45) Color(0xFFE65100) else Color(0xFFB71C1C),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val currentDiagnoses = remember(selectedCropName) {
                VedicKrishiData.diagnoses.filter { it.crop == selectedCropName }
            }

            currentDiagnoses.forEach { diag ->
                var isExpanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.clickable { isExpanded = !isExpanded }.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "Symptom: " + diag.symptomsHi else "Symptom: " + diag.symptoms,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "🔍 पहचान: " + diag.diseaseHi else "🔍 Disease: " + diag.disease,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Info,
                                contentDescription = if (isExpanded) "Collapse" else "Expand Details",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)))
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Causes
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "❓ कारण:" else "❓ Primary Cause:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") diag.causeHi else diag.cause,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // UPGRADE 5: ECOLOGICAL COMPARATIVE GRID
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "♻️ Organic vs Synthetic Impact Ratings",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val rows = listOf(
                                            Triple("Soil Carbon Impact", "⭐⭐⭐⭐⭐ (Improves)", "⭐ (Acidifies)"),
                                            Triple("Bee/Insect Safety", "⭐⭐⭐⭐⭐ (100% Safe)", "⭐ (High Toxicity)"),
                                            Triple("Prep/Purchase Cost", "⭐⭐⭐⭐⭐ (Near Free)", "⭐ (Highly Pricey)")
                                        )
                                        rows.forEach { (title, org, chem) ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(title, modifier = Modifier.weight(1.2f), fontSize = 9.sp, color = Color.Gray)
                                                Text(org, modifier = Modifier.weight(1.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                Text(chem, modifier = Modifier.weight(1.5f), fontSize = 9.sp, color = Color(0xFFC62828))
                                            }
                                        }
                                    }
                                }

                                // Organic Remedy
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "🌱 जैविक समाधान (Organic Remedy):" else "🌱 Organic Remedy:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") diag.remedyOrganicHi else diag.remedyOrganic,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    lineHeight = 16.sp
                                )

                                // Chemical Remedy
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "🧪 रासायनिक उपचार (Chemical Remedy):" else "🧪 Chemical Remedy:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFC62828)
                                )
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") diag.remedyChemicalHi else diag.remedyChemical,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // TTS Action button
                                Button(
                                    onClick = {
                                        val textToSpeak = if (selectedLanguage == "Hindi (हिन्दी)") {
                                            "लक्षण: ${diag.symptomsHi}. बीमारी की पहचान: ${diag.diseaseHi}. मुख्य कारण: ${diag.causeHi}. जैविक समाधान: ${diag.remedyOrganicHi}. रासायनिक उपचार: ${diag.remedyChemicalHi}"
                                        } else {
                                            "Symptoms: ${diag.symptoms}. Disease identified: ${diag.disease}. Cause: ${diag.cause}. Organic remedy: ${diag.remedyOrganic}. Chemical remedy: ${diag.remedyChemical}"
                                        }
                                        if (isSpeaking) {
                                            stopSpeaking()
                                        } else {
                                            speakAdvice(textToSpeak, selectedLanguage)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = if (isSpeaking) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isSpeaking) "Stop Speaking" else "Listen to Diagnosis",
                                        color = if (isSpeaking) Color.White else MaterialTheme.colorScheme.primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language & Category Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language selector
            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = !languageExpanded },
                modifier = Modifier.weight(1.2f)
            ) {
                OutlinedTextField(
                    value = selectedLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Language") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("language_selector")
                )
                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, fontWeight = FontWeight.Medium) },
                            onClick = {
                                viewModel.setLanguage(lang)
                                languageExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Scan Category (Leaf vs Receipt)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Scanner Mode",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!scanTypeIsReceipt) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { scanTypeIsReceipt = false }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Spa,
                            contentDescription = "Crop Leaf",
                            tint = if (!scanTypeIsReceipt) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (scanTypeIsReceipt) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { scanTypeIsReceipt = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = "Mandi Receipt",
                            tint = if (scanTypeIsReceipt) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Image Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable {
                                    bitmap = null
                                    imageUri = null
                                }
                                .padding(6.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Choose from Photo Gallery",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (scanTypeIsReceipt) "Upload Mandi invoice or receipt photo" else "Upload leaf photo with visible disease symptoms",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Query Input
                OutlinedTextField(
                    value = customQuery,
                    onValueChange = { customQuery = it },
                    label = { Text(if (scanTypeIsReceipt) "Enter Receipt Details (Optional)" else "Describe Crop / Ask Question") },
                    placeholder = {
                        Text(
                            if (scanTypeIsReceipt) "e.g., Extract weight & price from APMC..."
                            else "e.g., Black spots spreading on my cotton crop leaves..."
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { startVoiceInput("customQuery") }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_query_input"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Button(
                    onClick = {
                        viewModel.performScan(bitmap, customQuery, scanTypeIsReceipt)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = (bitmap != null || customQuery.trim().isNotEmpty()) && uiState !is ScanUiState.Loading
                ) {
                    Icon(
                        if (scanTypeIsReceipt) Icons.Default.Description else Icons.Default.Spa,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (scanTypeIsReceipt) "Parse Mandi Receipt" else "Diagnose Crop Health",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result Section
        AnimatedVisibility(
            visible = uiState !is ScanUiState.Idle,
            enter = fadeIn(animationSpec = spring()),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KrishiDrishti AI Advisor Report",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (uiState is ScanUiState.Success) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Saved successfully",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (val state = uiState) {
                        is ScanUiState.Loading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Analyzing with logical thinking active...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        is ScanUiState.Success -> {
                            Column {
                                // TTS Control Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isSpeaking) "Speaking..." else "Listen to Advisor Report",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    Button(
                                        onClick = {
                                            if (isSpeaking) {
                                                stopSpeaking()
                                            } else {
                                                speakAdvice(state.resultText, state.scanItem.language)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = if (isSpeaking) "Stop Speaking" else "Play Voice",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSpeaking) "Stop" else "Listen",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Text(
                                    text = state.resultText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Report generated in: ${state.scanItem.language}. Automatically saved to scan history.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                        is ScanUiState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = state.errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.resetState() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Text("Reset & Try Again", color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

// --- VEDIC KRISHI OFFLINE DATABASE ---

data class OrganicRecipe(
    val name: String,
    val nameHi: String,
    val primaryUse: String,
    val primaryUseHi: String,
    val ingredients: List<String>,
    val ingredientsHi: List<String>,
    val preparation: List<String>,
    val preparationHi: List<String>,
    val application: String,
    val applicationHi: String
)

data class OfflineCropDiagnosis(
    val crop: String,
    val symptoms: String,
    val symptomsHi: String,
    val disease: String,
    val diseaseHi: String,
    val cause: String,
    val causeHi: String,
    val remedyOrganic: String,
    val remedyOrganicHi: String,
    val remedyChemical: String,
    val remedyChemicalHi: String
)

object VedicKrishiData {
    val recipes = listOf(
        OrganicRecipe(
            name = "Neem-astra",
            nameHi = "नीमास्त्र (Neem-astra)",
            primaryUse = "Best for sucking pests, thrips, aphids, whiteflies, and small insects.",
            primaryUseHi = "रस चूसने वाले कीट, थ्रिप्स, एफिड्स, सफेद मक्खी और छोटे कीड़ों के नियंत्रण के लिए सर्वोत्तम।",
            ingredients = listOf(
                "10 kg Neem leaves (crushed/ground)",
                "2 kg Fresh Cow Dung",
                "10 Liters Cow Urine",
                "200 Liters Water"
            ),
            ingredientsHi = listOf(
                "10 किलो नीम के पत्ते (कुचले या पिसे हुए)",
                "2 किलो ताजा गाय का गोबर",
                "10 लीटर गोमूत्र",
                "200 लीटर पानी"
            ),
            preparation = listOf(
                "Step 1: Crush neem leaves thoroughly to release active Azadirachtin.",
                "Step 2: In a big barrel/drum, add 200 liters of water. Dissolve cow dung and cow urine in it.",
                "Step 3: Add the crushed neem leaves and stir well with a wooden stick.",
                "Step 4: Keep in deep shade to ferment for 48 hours. Stir clockwise twice every day.",
                "Step 5: Filter the liquid using a clean muslin or cotton cloth. Your Neem-astra is ready!"
            ),
            preparationHi = listOf(
                "चरण 1: नीम के पत्तों को अच्छी तरह कुचल लें ताकि सक्रिय तत्व बाहर आ सके।",
                "चरण 2: एक बड़े ड्रम में 200 लीटर पानी लें। उसमें गाय का गोबर और गोमूत्र डालकर घोलें।",
                "चरण 3: कुचले हुए नीम के पत्तों को इस मिश्रण में मिलाएं और डंडे से अच्छी तरह चलाएं।",
                "चरण 4: ड्रम को छायादार स्थान पर रखें और 48 घंटे तक किण्वन (ferment) होने दें। सुबह-शाम चलाएं।",
                "चरण 5: 48 घंटे के बाद मिश्रण को सूती कपड़े से छान लें। आपका नीमास्त्र तैयार है!"
            ),
            application = "Spray directly on the leaves. No further dilution with water is required. Re-spray every 10 days.",
            applicationHi = "पत्तियों पर सीधा छिड़काव करें। पानी मिलाने की आवश्यकता नहीं है। हर 10 दिनों में छिड़काव दोहराएं।"
        ),
        OrganicRecipe(
            name = "Agni-astra",
            nameHi = "अग्न्यास्त्र (Agni-astra)",
            primaryUse = "Effective against leaf rollers, stem borers, and large caterpillars.",
            primaryUseHi = "पत्ती लपेटक, तना छेदक और बड़े कैटरपिलर/इल्लियों के खिलाफ अत्यधिक प्रभावी।",
            ingredients = listOf(
                "5 kg Crushed Neem Leaves",
                "500g Tobacco Leaves",
                "500g Crushed Green Hot Chillies",
                "250g Crushed Garlic",
                "15 Liters Cow Urine"
            ),
            ingredientsHi = listOf(
                "5 किलो पिसे हुए नीम के पत्ते",
                "500 ग्राम तंबाकू के पत्ते",
                "500 ग्राम कुचली हुई तीखी हरी मिर्च",
                "250 ग्राम कुचला हुआ लहसुन",
                "15 लीटर गोमूत्र"
            ),
            preparation = listOf(
                "Step 1: Mix all ingredients (neem, tobacco, chili, garlic) into 15 liters of cow urine.",
                "Step 2: Heat the mixture on a stove. Let it come to a rolling boil 5 times.",
                "Step 3: Remove from heat. Keep in shade and let it cool down and ferment for 48 hours.",
                "Step 4: Stir twice daily during fermentation.",
                "Step 5: Filter with a thick cotton cloth. Storable for up to 3 months."
            ),
            preparationHi = listOf(
                "चरण 1: 15 लीटर गोमूत्र में नीम के पत्ते, तंबाकू, हरी मिर्च और लहसुन का पेस्ट अच्छी तरह मिलाएं।",
                "चरण 2: मिश्रण को आंच पर गर्म करें और इसे 5 बार उबालें।",
                "चरण 3: ठंडा होने के लिए छायादार स्थान पर रखें और 48 घंटे तक किण्वित होने दें।",
                "चरण 4: किण्वन के दौरान रोजाना दो बार लकड़ी के डंडे से चलाएं।",
                "चरण 5: सूती कपड़े से छान लें। इसे 3 महीने तक सुरक्षित रख सकते हैं।"
            ),
            application = "Dilution: Mix 2-3 Liters of Agni-astra in 100 Liters of water. Spray on affected foliage.",
            applicationHi = "घोल का अनुपात: 100 लीटर पानी में 2 से 3 लीटर अग्न्यास्त्र मिलाएं। प्रभावित पत्तियों पर छिड़काव करें।"
        ),
        OrganicRecipe(
            name = "Brahm-astra",
            nameHi = "ब्रह्मास्त्र (Brahm-astra)",
            primaryUse = "Ultimate protection against hidden larvae, pod/shoot borers, and leaf miners.",
            primaryUseHi = "छिपे हुए लार्वा, फली छेदक, तना छेदक और लीफ माइनर कीटों के खिलाफ अचूक जैविक हथियार।",
            ingredients = listOf(
                "10 Liters Cow Urine",
                "3 kg Neem Leaves (crushed)",
                "2 kg Custard Apple Leaves",
                "2 kg Papaya Leaves",
                "2 kg Guava Leaves"
            ),
            ingredientsHi = listOf(
                "10 लीटर गोमूत्र",
                "3 किलो नीम के पत्ते (कुचले हुए)",
                "2 किलो शरीफा (सीताफल) के पत्ते",
                "2 किलो पपीते के पत्ते",
                "2 किलो अमरूद के पत्ते"
            ),
            preparation = listOf(
                "Step 1: Grind all types of leaves into a fine paste.",
                "Step 2: Add leaf paste into 10 liters of cow urine.",
                "Step 3: Boil the mixture on medium heat until the liquid volume reduces to half (~5 liters).",
                "Step 4: Allow it to cool and ferment for 24 hours.",
                "Step 5: Filter through double-layered cloth. Storable up to 6 months."
            ),
            preparationHi = listOf(
                "चरण 1: सभी पत्तों को पीसकर बारीक पेस्ट बना लें।",
                "चरण 2: एक बर्तन में 10 लीटर गोमूत्र लें और उसमें पत्तियों का पेस्ट मिलाएं।",
                "चरण 3: मिश्रण को मध्यम आंच पर तब तक उबालें जब तक कि तरल आधा (लगभग 5 लीटर) न रह जाए।",
                "चरण 4: ठंडा होने दें और 24 घंटे के लिए छाया में रख दें।",
                "चरण 5: सूती कपड़े से छान लें। इसे 6 महीने तक स्टोर किया जा सकता है।"
            ),
            application = "Dilution: Mix 2-2.5 Liters of Brahm-astra in 100 Liters of water. Spray thoroughly.",
            applicationHi = "घोल का अनुपात: 100 लीटर पानी में 2 से 2.5 लीटर ब्रह्मास्त्र मिलाकर छिड़काव करें।"
        ),
        OrganicRecipe(
            name = "Jeevamrutha",
            nameHi = "जीवामृत (Jeevamrutha)",
            primaryUse = "Soil microbial tonic. Rapidly improves organic carbon, aeration, and root growth.",
            primaryUseHi = "मृदा जीवाणु टॉनिक। मिट्टी में जैविक कार्बन, हवा के संचरण और जड़ों के विकास में तेजी से सुधार करता है।",
            ingredients = listOf(
                "10 kg Fresh Cow Dung",
                "10 Liters Cow Urine",
                "1 kg Organic Jaggery (Gur)",
                "1 kg Pulse Flour (Besan)",
                "1 Handful of Uncontaminated Forest Soil",
                "200 Liters Water"
            ),
            ingredientsHi = listOf(
                "10 किलो ताजा गाय का गोबर",
                "10 लीटर गोमूत्र",
                "1 किलो जैविक गुड़",
                "1 किलो बेसन",
                "1 मुट्ठी खेत के मेड़ या पेड़ के नीचे की मिट्टी",
                "200 लीटर पानी"
            ),
            preparation = listOf(
                "Step 1: Dissolve 10kg cow dung and 10L cow urine in 200 liters of water inside a drum.",
                "Step 2: Melt 1kg jaggery and mix 1kg besan in water, ensuring no lumps, then add to the drum.",
                "Step 3: Add the handful of soil (provides native local beneficial bacteria/fungi).",
                "Step 4: Stir clockwise with a wooden pole twice daily. Keep covered in shade.",
                "Step 5: Ready in 7 days. Apply within 12 days."
            ),
            preparationHi = listOf(
                "चरण 1: ड्रम में 200 लीटर पानी लें। उसमें 10 किलो गोबर और 10 लीटर गोमूत्र डालकर घोलें।",
                "चरण 2: अलग से 1 किलो गुड़ और 1 किलो बेसन पानी में घोल लें, फिर ड्रम में डालें।",
                "चरण 3: ड्रम में एक मुट्ठी मिट्टी डालें (यह मिट्टी स्थानीय जीवाणुओं का स्रोत है)।",
                "चरण 4: मिश्रण को दिन में दो बार घड़ी की दिशा में घुमाएं और छाया में ढककर रखें।",
                "चरण 5: 7 दिनों में जीवामृत तैयार हो जाता है। 7 से 12 दिनों के भीतर उपयोग करें।"
            ),
            application = "Root Application: Apply directly near root zones via irrigation water. For foliar spray, dilute 1:10 with water and filter.",
            applicationHi = "उपयोग: सीधे पौधों की जड़ों में पानी के साथ डालें। पत्तियों पर छिड़काव के लिए इसे 1:10 के अनुपात में पानी में मिलाकर छान लें।"
        ),
        OrganicRecipe(
            name = "Panchagavya",
            nameHi = "पंचगव्य (Panchagavya)",
            primaryUse = "Premium growth regulator and immunity enhancer. Produces larger, greener leaves.",
            primaryUseHi = "पौध विकास नियामक (Growth Regulator) और रोग प्रतिरोधक टॉनिक। पत्तियों को बड़ा और चमकीला बनाता है।",
            ingredients = listOf(
                "7 kg Fresh Cow Dung",
                "1 kg Desi Cow Ghee",
                "3 Liters Cow Urine",
                "10 Liters Water",
                "3 Liters Cow Milk",
                "2 Liters Cow Curd",
                "3 Liters Tender Coconut Water",
                "3 Liters Sugarcane Juice / Jaggery Water",
                "12 Ripe Bananas"
            ),
            ingredientsHi = listOf(
                "7 किलो ताजा गाय का गोबर",
                "1 किलो गाय का शुद्ध घी",
                "3 लीटर गोमूत्र",
                "10 लीटर पानी",
                "3 लीटर गाय का दूध",
                "2 लीटर गाय का दही",
                "3 लीटर कच्चा नारियल पानी",
                "3 लीटर गन्ने का रस या गुड़ का पानी",
                "12 पके हुए केले"
            ),
            preparation = listOf(
                "Step 1: Mix cow dung and ghee inside a container. Let it sit for 3 days, stirring twice daily.",
                "Step 2: On Day 4, add cow urine and 10 liters of water. Let it ferment for 15 days, stirring twice daily.",
                "Step 3: On Day 19, add milk, curd, tender coconut water, sugarcane juice, and mashed ripe bananas.",
                "Step 4: Keep fermenting in shade for another 15 days, stirring twice daily.",
                "Step 5: Filter the liquid on Day 34. The storable liquid is ready."
            ),
            preparationHi = listOf(
                "चरण 1: 7 किलो गोबर और 1 किलो घी अच्छी तरह मिला लें। इसे 3 दिनों के लिए छोड़ दें, दिन में दो बार चलाएं।",
                "चरण 2: चौथे दिन गोमूत्र और 10 लीटर पानी डालें। इसे 15 दिनों तक छाया में रखें और रोज सुबह-शाम चलाएं।",
                "चरण 3: 19वें दिन इसमें दूध, दही, नारियल पानी, गन्ने का रस और पके केले डालें।",
                "चरण 4: अगले 15 दिनों तक इसे छाया में रखें और रोज सुबह-शाम लकड़ी से हिलाएं।",
                "चरण 5: 34वें दिन आपका पंचगव्य तैयार हो जाएगा। इसे छान लें।"
            ),
            application = "Dilution: Mix 3 Liters of Panchagavya in 100 Liters of water (3% solution) and spray on crop foliage.",
            applicationHi = "घोल का अनुपात: 100 लीटर पानी में 3 लीटर पंचगव्य (3% घोल) मिलाएं और पत्तियों पर स्प्रे करें।"
        )
    )

    val diagnoses = listOf(
        OfflineCropDiagnosis(
            crop = "Paddy (Rice)",
            symptoms = "Brown/rusty eye-shaped spots on leaves",
            symptomsHi = "पत्तियों पर भूरे/लाल आंख के आकार के धब्बे",
            disease = "Paddy Leaf Blast (Fungal)",
            diseaseHi = "धान का झोंका रोग / ब्लास्ट (फंगल)",
            cause = "Magnaporthe oryzae fungus, triggered by excess nitrogen and humid weather.",
            causeHi = "मैग्नापोर्टे ओरिजाई कवक, अत्यधिक नाइट्रोजन और आर्द्र मौसम के कारण फैलता है।",
            remedyOrganic = "Spray 10-day fermented sour buttermilk (diluted 1:10 with water) or 5% Neem Oil spray mixed with liquid soap emulsifier.",
            remedyOrganicHi = "10 दिन पुरानी खट्टी छाछ (1:10 पानी में घोल) का छिड़काव करें, या साबुन के घोल के साथ 5% नीम के तेल का स्प्रे करें।",
            remedyChemical = "Spray Tricyclazole 75% WP @ 120 grams per acre in 200 liters of water.",
            remedyChemicalHi = "प्रति एकड़ 200 लीटर पानी में 120 ग्राम ट्राइसाइक्लाजोल 75% डब्ल्यूपी का छिड़काव करें।"
        ),
        OfflineCropDiagnosis(
            crop = "Paddy (Rice)",
            symptoms = "Long water-soaked streaks turning yellow-white on margins",
            symptomsHi = "पत्तियों के किनारों पर लंबे पीले-सफेद धब्बे",
            disease = "Bacterial Leaf Blight (BLB)",
            diseaseHi = "जीवाणु जनित झुलसा रोग (BLB)",
            cause = "Xanthomonas oryzae bacteria, spread through heavy monsoon winds and rain splashes.",
            causeHi = "जैन्थोमोनास ओरिजाई जीवाणु, भारी मानसूनी हवाओं और बारिश की बूंदों के माध्यम से फैलता है।",
            remedyOrganic = "Spray Fresh Cow Dung Extract (20g/L, filtered through cotton) or apply Dashparni Ark at 5% dilution. Avoid excess nitrogen.",
            remedyOrganicHi = "ताजा गाय के गोबर का अर्क (20 ग्राम प्रति लीटर, छानकर) छिड़कें, या 5% दशपर्णी अर्क का उपयोग करें। नाइट्रोजन कम दें।",
            remedyChemical = "Spray Streptocycline @ 6g + Copper Oxychloride @ 500g in 200 liters of water per acre.",
            remedyChemicalHi = "प्रति एकड़ 200 लीटर पानी में 6 ग्राम स्ट्रेप्टोसाइक्लिन + 500 ग्राम कॉपर ऑक्सीक्लोराइड का छिड़काव करें।"
        ),
        OfflineCropDiagnosis(
            crop = "Wheat",
            symptoms = "Orange-brown powdery pustules forming stripes on leaves",
            symptomsHi = "पत्तियों पर कतार में पीले-भूरे रंग के पाउडर जैसे धब्बे",
            disease = "Yellow/Stripe Rust (Fungus)",
            diseaseHi = "पीला रतुआ रोग (Yellow Rust)",
            cause = "Puccinia striiformis fungus, thriving in cool wet winter climates with heavy dew.",
            causeHi = "पुक्सिनिया स्ट्रइफोर्मिस कवक, सर्दियों में ठंडे और ओस वाले मौसम में तेजी से फैलता है।",
            remedyOrganic = "Spray 5% Neem-astra or fermented sour buttermilk spray (1:10 ratio) to inhibit spore germination.",
            remedyOrganicHi = "5% नीमास्त्र या खट्टी छाछ के घोल (1:10) का स्प्रे करें ताकि फंगस के बीजाणु विकसित न हो सकें।",
            remedyChemical = "Spray Propiconazole 25% EC @ 200ml per acre in 200 liters of water.",
            remedyChemicalHi = "प्रति एकड़ 200 लीटर पानी में 200 मिली प्रोपिकोनाजोल 25% ईसी का छिड़काव करें।"
        ),
        OfflineCropDiagnosis(
            crop = "Cotton",
            symptoms = "Boring holes in flower buds/bolls, pink worm inside",
            symptomsHi = "फूलों और डोडों में छेद, भीतर गुलाबी रंग की सूंडी/इल्ली",
            disease = "Pink Bollworm Infestation",
            diseaseHi = "गुलाबी सुंडी प्रकोप (Pink Bollworm)",
            cause = "Pectinophora gossypiella larvae feeding inside the cotton bolls, destroying fiber.",
            causeHi = "पेक्टिनोफोरा गॉसिपिएला लार्वा, जो कपास के डोडों के भीतर घुसकर रेशे और बीजों को खाता है।",
            remedyOrganic = "Install 5-8 pheromone traps per acre. Spray Agni-astra or Brahm-astra at 5% dilution.",
            remedyOrganicHi = "खेत में प्रति एकड़ 5-8 फेरोमोन ट्रैप लगाएं। 5% अग्न्यास्त्र या ब्रह्मास्त्र का छिड़काव करें।",
            remedyChemical = "Spray Profenofos 50% EC @ 400ml or Emamectin Benzoate 5% SG @ 80g per acre.",
            remedyChemicalHi = "प्रति एकड़ 400 मिली प्रोफेनोफॉस 50% ईसी या 80 ग्राम एमामेक्टिन बेंजोएट 5% एसजी का छिड़काव करें।"
        ),
        OfflineCropDiagnosis(
            crop = "Tomato",
            symptoms = "Concentric rings like a bullseye on older leaves",
            symptomsHi = "पुरानी पत्तियों पर चक्राकार रिंग/धब्बे (सांड की आंख जैसे)",
            disease = "Early Blight Fungus",
            diseaseHi = "अगेती झुलसा रोग (Early Blight)",
            cause = "Alternaria solani fungus, favored by warm climates and high humidity.",
            causeHi = "अल्टरनेरिया सोलानी कवक, गर्म मौसम और हवा में नमी होने पर फैलता है।",
            remedyOrganic = "Spray Trichoderma viride bio-fungicide @ 5g/L of water. Prune lower diseased leaves.",
            remedyOrganicHi = "ट्राइकोडेर्मा विरिडी जैव-कवकनाशी @ 5 ग्राम प्रति लीटर पानी का छिड़काव करें। निचली रोगग्रस्त पत्तियों को काटकर हटा दें।",
            remedyChemical = "Spray Mancozeb 75% WP @ 600g per acre or Azoxystrobin 23% SC @ 200ml per acre.",
            remedyChemicalHi = "प्रति एकड़ 600 ग्राम मैंकोजेब 75% डब्ल्यूपी या 200 मिली एजॉक्सीस्ट्रोबिन 23% एससी का छिड़काव करें।"
        )
    )
}

