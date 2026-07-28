package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeluguDocOcrResult
import com.example.ui.KrishiViewModel
import com.example.ui.UserProfileData
import com.example.ui.viewmodel.DocOcrUiState
import com.example.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeluguDocOcrScannerCard(
    krishiViewModel: KrishiViewModel,
    scannerViewModel: ScannerViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by krishiViewModel.userProfileData.collectAsState()
    val ocrUiState by scannerViewModel.docOcrUiState.collectAsState()
    val gemmaThinking by scannerViewModel.gemmaThinkingDocOcr.collectAsState()

    var showScanDialog by remember { mutableStateOf(false) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var ocrSuccessMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                selectedBitmap = bitmap
                if (bitmap != null) {
                    scannerViewModel.analyzeTeluguDocument(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            selectedBitmap = it
            scannerViewModel.analyzeTeluguDocument(it)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Summary Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF047857),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userProfile.farmerName.firstOrNull()?.toString() ?: "R",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = userProfile.farmerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(0xFF111827)
                                )
                            }
                            if (userProfile.farmerNameTelugu.isNotBlank()) {
                                Text(
                                    text = userProfile.farmerNameTelugu,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF047857)
                                )
                            }
                            Text(
                                text = "${userProfile.phoneNumber} • AP/TS Registered Farmer",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    // Verification Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (userProfile.isOcrVerified) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (userProfile.isOcrVerified) Icons.Default.Verified else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (userProfile.isOcrVerified) Color(0xFF15803D) else Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (userProfile.isOcrVerified) "Govt OCR Verified" else "Unverified Doc",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userProfile.isOcrVerified) Color(0xFF15803D) else Color(0xFFD97706)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Grid of Key Profile Fields
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ProfileFieldCol(label = "Passbook / Khata No.", value = userProfile.passbookKhataNumber)
                        ProfileFieldCol(label = "District", value = userProfile.district)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ProfileFieldCol(label = "Mandal / Village", value = userProfile.mandalVillage)
                        ProfileFieldCol(label = "Survey Numbers", value = userProfile.surveyNumbers)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ProfileFieldCol(label = "Land Area", value = "${userProfile.landAreaAcres} Acres (ఎకరాలు)")
                        ProfileFieldCol(label = "Aadhaar KYC", value = userProfile.aadhaarStatus)
                    }
                }

                if (ocrSuccessMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                            Text(ocrSuccessMessage ?: "", fontSize = 12.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Action Card to launch Telugu Script OCR Scanner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showScanDialog = true
                    scannerViewModel.resetDocOcrState()
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF047857))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Scan Telugu Govt Document",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF59E0B)
                            ) {
                                Text(
                                    text = "Telugu OCR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "తెలుగు రికార్డు స్కాన్ (పట్టాదారు పాస్ పుస్తకం / అడంగల్ / PM-KISAN)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "Extracts Telugu text & populates user profile instantly",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // Modal Sheet / Dialog for Telugu Document OCR Scanner
    if (showScanDialog) {
        AlertDialog(
            onDismissRequest = { showScanDialog = false },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Telugu Government Document OCR", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
                            Text("తెలుగు ప్రభుత్వ భూమి పత్రాల OCR రీడర్", fontSize = 12.sp, color = Color(0xFF047857), fontWeight = FontWeight.SemiBold)
                        }

                        IconButton(onClick = { showScanDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE5E7EB))

                    // Scan Controls / Document Selection
                    if (ocrUiState is DocOcrUiState.Idle) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Supported Telugu Documents:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF374151)
                            )

                            val docTypes = listOf(
                                "📘 Pattadar Passbook (పట్టాదారు పాస్ పుస్తకం)",
                                "📄 Adangal / Pahani (అడంగల్ / పహాణీ)",
                                "🪪 Rythu Bharosa / Rythu Bandhu ID Card",
                                "📜 PM-KISAN Farmer Certificate",
                                "🧪 Soil Health Card (భూసార పరీక్ష పత్రం)"
                            )

                            docTypes.forEach { doc ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = doc,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Action Buttons: Capture, Upload, or Demo Passbook
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Take Photo of Document", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload Image from Gallery", fontWeight = FontWeight.Bold)
                            }

                            // Sample AP/TS Pattadar Passbook Generator
                            FilledTonalButton(
                                onClick = {
                                    val demoBitmap = generateSampleTeluguDocumentBitmap()
                                    selectedBitmap = demoBitmap
                                    scannerViewModel.analyzeTeluguDocument(demoBitmap)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFFEF3C7), contentColor = Color(0xFFB45309))
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Scan Sample AP Pattadar Passbook (డెమో)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Selected Image Preview & Processing State
                    selectedBitmap?.let { bitmap ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Document Scan Preview",
                                modifier = Modifier.fillMaxSize()
                            )

                            if (ocrUiState is DocOcrUiState.Loading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(color = Color(0xFF34D399), strokeWidth = 3.dp)
                                        Text("Gemma 4 Telugu Script OCR Engine Active...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Gemma 4 OCR Thinking Log
                    if (!gemmaThinking.isNullOrEmpty() && ocrUiState is DocOcrUiState.Loading) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                    Text("Telugu Unicode Ligature Extraction Log", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = gemmaThinking ?: "",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 15.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    // OCR Success Result View
                    if (ocrUiState is DocOcrUiState.Success) {
                        val result = (ocrUiState as DocOcrUiState.Success).ocrResult

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Status Banner
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D))
                                        Column {
                                            Text(result.document_type, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF15803D))
                                            Text("${result.verification_status} • Precision: ${(result.confidence_score * 100).toInt()}%", fontSize = 11.sp, color = Color(0xFF166534))
                                        }
                                    }
                                }
                            }

                            // Parsed Fields List
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OcrFieldRow(label = "Telugu Farmer Name (తెలుగు పేరు)", value = result.farmer_name_telugu.ifBlank { "N/A" }, isHighlight = true)
                                    OcrFieldRow(label = "English Farmer Name", value = result.farmer_name_english.ifBlank { "N/A" })
                                    OcrFieldRow(label = "Father/Husband Name", value = result.father_or_husband_name.ifBlank { "N/A" })
                                    OcrFieldRow(label = "Khata / Passbook No.", value = result.passbook_or_khata_number.ifBlank { "N/A" })
                                    OcrFieldRow(label = "Survey Numbers", value = result.survey_numbers.joinToString(", ").ifBlank { "N/A" })
                                    OcrFieldRow(label = "District & Mandal", value = "${result.district} | ${result.mandal_or_village}")
                                    OcrFieldRow(label = "Land Area (Acres)", value = "${result.total_land_acres} Acres")
                                    OcrFieldRow(label = "Aadhaar Card ID", value = result.aadhaar_masked.ifBlank { "XXXX-4321" })
                                }
                            }

                            // Raw Transcribed Telugu Script Preview
                            if (result.raw_telugu_text.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFEF3C7),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Transcribed Raw Telugu Script (తెలుగు లిపి) Text:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = result.raw_telugu_text, fontSize = 11.sp, color = Color(0xFF78350F), lineHeight = 16.sp)
                                    }
                                }
                            }

                            // Apply to User Profile Button
                            Button(
                                onClick = {
                                    krishiViewModel.applyOcrResultToProfile(result)
                                    ocrSuccessMessage = "Successfully updated User Profile with scanned Telugu document data!"
                                    showScanDialog = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Auto-Populate User Profile Data (ప్రొఫైల్‌కి వర్తించు)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    // Error View
                    if (ocrUiState is DocOcrUiState.Error) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFDC2626))
                                Text(
                                    text = (ocrUiState as DocOcrUiState.Error).message,
                                    color = Color(0xFFB91C1C),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun ProfileFieldCol(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = Color(0xFF6B7280))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
    }
}

@Composable
fun OcrFieldRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = Color(0xFF4B5563))
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = if (isHighlight) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isHighlight) Color(0xFF047857) else Color(0xFF111827)
        )
    }
}

// Generates a mock bitmap simulating an official Andhra Pradesh Revenue Department Pattadar Passbook
fun generateSampleTeluguDocumentBitmap(): Bitmap {
    val width = 600
    val height = 400
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background - Off-white paper texture
    val bgPaint = Paint().apply { color = android.graphics.Color.rgb(253, 252, 240) }
    canvas.drawRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), bgPaint)

    // Border
    val borderPaint = Paint().apply {
        color = android.graphics.Color.rgb(4, 120, 87)
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    canvas.drawRect(RectF(16f, 16f, width - 16f, height - 16f), borderPaint)

    val textPaint = Paint().apply {
        color = android.graphics.Color.rgb(17, 24, 39)
        textSize = 22f
        isAntiAlias = true
    }

    val headerPaint = Paint().apply {
        color = android.graphics.Color.rgb(4, 120, 87)
        textSize = 28f
        isFakeBoldText = true
        isAntiAlias = true
    }

    canvas.drawText("ఆంధ్రప్రదేశ్ ప్రభుత్వం - రెవెన్యూ శాఖ", 80f, 60f, headerPaint)
    canvas.drawText("పట్టాదారు పాస్‌పుస్తకం (PATTADAR PASSBOOK)", 70f, 95f, textPaint)

    val detailPaint = Paint().apply {
        color = android.graphics.Color.rgb(31, 41, 55)
        textSize = 20f
        isAntiAlias = true
    }

    canvas.drawText("ఖాతా సంఖ్య: 10482 / 2026", 40f, 140f, detailPaint)
    canvas.drawText("పట్టాదారు పేరు: కె. రాజేష్ కుమార్ (K. Rajesh Kumar)", 40f, 180f, detailPaint)
    canvas.drawText("తండ్రి పేరు: వెంకటేశ్వర్లు", 40f, 220f, detailPaint)
    canvas.drawText("జిల్లా: గుంటూరు | మండలం: తేనాలి", 40f, 260f, detailPaint)
    canvas.drawText("సర్వే నంబరు: 142/1B, 143/2A | విస్తీర్ణం: 4.50 ఎకరాలు", 40f, 300f, detailPaint)

    val stampPaint = Paint().apply {
        color = android.graphics.Color.rgb(220, 38, 38)
        textSize = 18f
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("MRO SEAL & VERIFIED ✓", 320f, 350f, stampPaint)

    return bitmap
}
