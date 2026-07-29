package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.CropDiagnosisHistoryEntity
import com.example.ui.KrishiViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseRecoveryTrackerCard(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val diagnosisHistory by viewModel.cropDiagnosisHistory.collectAsState()
    var selectedPlotFilter by remember { mutableStateOf("All Plots") }
    var selectedRecordForDetail by remember { mutableStateOf<CropDiagnosisHistoryEntity?>(null) }
    var showAddLogDialog by remember { mutableStateOf(false) }

    // Unique list of plots
    val availablePlots = remember(diagnosisHistory) {
        listOf("All Plots") + diagnosisHistory.map { it.fieldPlotName }.distinct()
    }

    val filteredList = remember(diagnosisHistory, selectedPlotFilter) {
        if (selectedPlotFilter == "All Plots") {
            diagnosisHistory
        } else {
            diagnosisHistory.filter { it.fieldPlotName == selectedPlotFilter }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0D9488),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Healing, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }

                    Column {
                        Text("Disease Recovery Tracker", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                        Text("Room DB • Gemini AI Diagnosis & Crop Photo History", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }

                Button(
                    onClick = { showAddLogDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_recovery_log_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Follow-Up", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Overview Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val avgProgress = if (diagnosisHistory.isNotEmpty()) diagnosisHistory.map { it.recoveryProgressPct }.average().toInt() else 0
                val activeCount = diagnosisHistory.count { it.recoveryProgressPct < 90 }

                StatMiniCard("Total Scans", "${diagnosisHistory.size}", "Tracked in Room", Color(0xFF0284C7), Modifier.weight(1f))
                StatMiniCard("Active Diseases", "$activeCount", "In Recovery", Color(0xFFD97706), Modifier.weight(1f))
                StatMiniCard("Avg Recovery", "$avgProgress%", "Health Score", Color(0xFF059669), Modifier.weight(1f))
            }

            // Plot Filter Chips
            if (availablePlots.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(availablePlots) { plot ->
                        FilterChip(
                            selected = selectedPlotFilter == plot,
                            onClick = { selectedPlotFilter = plot },
                            label = { Text(plot, fontSize = 11.sp) }
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No diagnosis records found for this plot.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filteredList.forEach { record ->
                        RecoveryRecordRow(
                            record = record,
                            onDetailClick = { selectedRecordForDetail = record },
                            onDeleteClick = { viewModel.deleteDiagnosisHistoryRecord(record.id) }
                        )
                    }
                }
            }
        }
    }

    // Record Detail Dialog
    selectedRecordForDetail?.let { record ->
        AlertDialog(
            onDismissRequest = { selectedRecordForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Biotech, contentDescription = null, tint = Color(0xFF0D9488))
                    Text("${record.cropName} - ${record.diseaseName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Photo if available
                    record.imagePath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            Image(
                                painter = rememberAsyncImagePainter(file),
                                contentDescription = "Crop Scan Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Plot: ${record.fieldPlotName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0369A1))
                        Text("Stage: ${record.recoveryStage}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF059669))
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Recovery Progress", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("${record.recoveryProgressPct}%", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D9488))
                        }
                        LinearProgressIndicator(
                            progress = record.recoveryProgressPct / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = Color(0xFF0D9488),
                            trackColor = Color(0xFFCCFBF1)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🤖 Gemini AI Diagnosis & Recommendation:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF0F172A))
                            Text(record.geminiDiagnosisText, fontSize = 11.sp, color = Color(0xFF334155))
                        }
                    }

                    if (record.organicRemedy.isNotBlank()) {
                        Text("🌱 Organic Treatment: ${record.organicRemedy}", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Medium)
                    }

                    if (record.chemicalRemedy.isNotBlank()) {
                        Text("🧪 Chemical Treatment: ${record.chemicalRemedy}", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Medium)
                    }

                    if (record.treatmentNotes.isNotBlank()) {
                        Text("📝 Farmer Notes: ${record.treatmentNotes}", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Normal)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedRecordForDetail = null }) {
                    Text("Close", fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                }
            }
        )
    }

    // Add Follow-up Recovery Log Dialog
    if (showAddLogDialog) {
        AddRecoveryLogModal(
            viewModel = viewModel,
            onDismiss = { showAddLogDialog = false }
        )
    }
}

@Composable
fun RecoveryRecordRow(
    record: CropDiagnosisHistoryEntity,
    onDetailClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateStr = remember(record.timestamp) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(record.timestamp))
    }

    val statusColor = when {
        record.recoveryProgressPct >= 80 -> Color(0xFF059669)
        record.recoveryProgressPct >= 40 -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Thumbnail
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE2E8F0),
                modifier = Modifier.size(52.dp)
            ) {
                val file = record.imagePath?.let { File(it) }
                if (file != null && file.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = statusColor, modifier = Modifier.size(26.dp))
                    }
                }
            }

            // Record Details
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${record.cropName} • ${record.fieldPlotName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Text(dateStr, fontSize = 10.sp, color = Color(0xFF94A3B8))
                }

                Text(record.diseaseName, fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(4.dp), color = statusColor.copy(alpha = 0.15f)) {
                        Text(record.recoveryStage, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }

                    Text("${record.recoveryProgressPct}% Recovered", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = statusColor)
                }
            }

            IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun AddRecoveryLogModal(
    viewModel: KrishiViewModel,
    onDismiss: () -> Unit
) {
    var cropName by remember { mutableStateOf("Chilli (Mirchi)") }
    var plotName by remember { mutableStateOf("Guntur East Plot A") }
    var diseaseName by remember { mutableStateOf("Chilli Black Thrips & Leaf Curl") }
    var stageName by remember { mutableStateOf("Day 7 Follow-Up Scan") }
    var progressPct by remember { mutableFloatStateOf(70f) }
    var farmerNotes by remember { mutableStateOf("Applied Beauveria bassiana bio-spray. Leaves looking healthier.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Healing, contentDescription = null, tint = Color(0xFF0D9488))
                Text("Log Disease Recovery Update", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    label = { Text("Crop Name", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = plotName,
                    onValueChange = { plotName = it },
                    label = { Text("Field / Plot Name", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = diseaseName,
                    onValueChange = { diseaseName = it },
                    label = { Text("Disease / Issue", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stageName,
                    onValueChange = { stageName = it },
                    label = { Text("Recovery Stage Tag", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Recovery Progress: ${progressPct.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0D9488))
                Slider(
                    value = progressPct,
                    onValueChange = { progressPct = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = farmerNotes,
                    onValueChange = { farmerNotes = it },
                    label = { Text("Farmer Treatment Notes", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val record = CropDiagnosisHistoryEntity(
                        cropName = cropName,
                        fieldPlotName = plotName,
                        diseaseName = diseaseName,
                        severityLevel = if (progressPct >= 80) "Recovered" else "Improving",
                        recoveryStage = stageName,
                        recoveryProgressPct = progressPct.toInt(),
                        geminiDiagnosisText = "Farmer logged follow-up recovery progress update in Room DB.",
                        organicRemedy = "Followed recommended organic bio-spray protocol.",
                        chemicalRemedy = "N/A",
                        timestamp = System.currentTimeMillis(),
                        treatmentNotes = farmerNotes
                    )
                    viewModel.addDiagnosisHistoryRecord(record)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
            ) {
                Text("Save to Room DB", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StatMiniCard(title: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, color = Color(0xFF64748B))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(subtitle, fontSize = 9.sp, color = Color(0xFF475569))
        }
    }
}
