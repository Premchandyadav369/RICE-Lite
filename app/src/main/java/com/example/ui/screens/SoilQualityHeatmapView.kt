package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SoilSampleEntity
import com.example.ui.KrishiViewModel

enum class HeatmapMetric(val displayName: String, val unit: String) {
    PH("Soil pH Level", "pH"),
    ORGANIC_CARBON("Organic Carbon", "%"),
    NITROGEN("Nitrogen (N)", "kg/Acre"),
    PHOSPHORUS("Phosphorus (P)", "kg/Acre"),
    POTASSIUM("Potassium (K)", "kg/Acre"),
    MOISTURE("Soil Moisture", "%")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilQualityHeatmapView(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val soilSamples by viewModel.soilSamples.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")

    var selectedMetric by remember { mutableStateOf(HeatmapMetric.PH) }
    var selectedSample by remember { mutableStateOf<SoilSampleEntity?>(null) }
    var showAddSampleDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Auto select first sample if none selected
    LaunchedEffect(soilSamples) {
        if (selectedSample == null && soilSamples.isNotEmpty()) {
            selectedSample = soilSamples.first()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Header Banner ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Soil Quality Heatmap",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTelugu) "నేల నాణ్యత హీట్‌మ్యాప్ (Soil Quality Heatmap)" else "Spatial Soil Health & Nutrient Heatmap",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isTelugu) "భూమిలోని పోషకాల సాంద్రత, pH మరియు తేమ వివరాల మ్యాప్" else "Real-time field parcel interpolation for pH, OC, N, P, K & Moisture",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- Metric Switcher Chips ---
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(HeatmapMetric.values()) { metric ->
                FilterChip(
                    selected = selectedMetric == metric,
                    onClick = { selectedMetric = metric },
                    label = { Text(metric.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // --- Canvas Spatial Heatmap Field Box ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Dark soil map canvas background
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FIELD CONTOUR PARCEL #4 - ${selectedMetric.displayName.uppercase()}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF334155)
                    ) {
                        Text(
                            text = "${soilSamples.size} Zone Probe Points",
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // --- CANVAS HEATMAP DRAWING ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(soilSamples, selectedMetric) {
                                detectTapGestures { tapOffset ->
                                    // Find closest sample point to tap location
                                    val canvasWidth = size.width
                                    val canvasHeight = size.height

                                    val closest = soilSamples.minByOrNull { sample ->
                                        val px = sample.gridXRatio * canvasWidth
                                        val py = sample.gridYRatio * canvasHeight
                                        val dx = px - tapOffset.x
                                        val dy = py - tapOffset.y
                                        (dx * dx + dy * dy)
                                    }

                                    if (closest != null) {
                                        selectedSample = closest
                                    }
                                }
                            }
                    ) {
                        val canvasW = size.width
                        val canvasH = size.height

                        // Draw Grid lines
                        val gridCols = 5
                        val gridRows = 5
                        val cellW = canvasW / gridCols
                        val cellH = canvasH / gridRows

                        for (i in 0..gridCols) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(i * cellW, 0f),
                                end = Offset(i * cellW, canvasH),
                                strokeWidth = 1f
                            )
                        }
                        for (j in 0..gridRows) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(0f, j * cellH),
                                end = Offset(canvasW, j * cellH),
                                strokeWidth = 1f
                            )
                        }

                        // Draw Radial Heat Spots for each soil sample point
                        soilSamples.forEach { sample ->
                            val cx = sample.gridXRatio * canvasW
                            val cy = sample.gridYRatio * canvasH

                            val value = getMetricValue(sample, selectedMetric)
                            val spotColor = getMetricColor(value, selectedMetric)

                            // Radial Heat Gradient
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(spotColor.copy(alpha = 0.75f), spotColor.copy(alpha = 0.25f), Color.Transparent),
                                    center = Offset(cx, cy),
                                    radius = 110.dp.toPx()
                                ),
                                center = Offset(cx, cy),
                                radius = 110.dp.toPx()
                            )

                            // Inner Core Marker
                            val isSelected = selectedSample?.id == sample.id
                            val markerRadius = if (isSelected) 10.dp.toPx() else 6.dp.toPx()

                            drawCircle(
                                color = if (isSelected) Color.White else spotColor,
                                center = Offset(cx, cy),
                                radius = markerRadius
                            )

                            if (isSelected) {
                                drawCircle(
                                    color = Color(0xFF38BDF8),
                                    center = Offset(cx, cy),
                                    radius = markerRadius + 4.dp.toPx(),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }
                }

                // Heatmap Color Legend Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFEF4444), CircleShape))
                        Text("Deficient / Low", color = Color.LightGray, fontSize = 10.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFEAB308), CircleShape))
                        Text("Moderate", color = Color.LightGray, fontSize = 10.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF22C55E), CircleShape))
                        Text("Optimal Fertility", color = Color.LightGray, fontSize = 10.sp)
                    }
                }
            }
        }

        // --- Selected Zone Diagnostics & Prescription Card ---
        selectedSample?.let { sample ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Zone Diagnostic: ${sample.zoneName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Soil Class: ${sample.soilType} • Tested: ${sample.testDate}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Probe ID #${sample.id}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Grid of 6 Soil Parameters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ParamPill("pH Level", "%.1f".format(sample.pH), getMetricColor(sample.pH, HeatmapMetric.PH), Modifier.weight(1f))
                        ParamPill("Organic C", "%.2f%%".format(sample.organicCarbonPct), getMetricColor(sample.organicCarbonPct, HeatmapMetric.ORGANIC_CARBON), Modifier.weight(1f))
                        ParamPill("Nitrogen N", "%.0f kg".format(sample.nitrogenKgPerAcre), getMetricColor(sample.nitrogenKgPerAcre, HeatmapMetric.NITROGEN), Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ParamPill("Phosphorus P", "%.0f kg".format(sample.phosphorusKgPerAcre), getMetricColor(sample.phosphorusKgPerAcre, HeatmapMetric.PHOSPHORUS), Modifier.weight(1f))
                        ParamPill("Potassium K", "%.0f kg".format(sample.potassiumKgPerAcre), getMetricColor(sample.potassiumKgPerAcre, HeatmapMetric.POTASSIUM), Modifier.weight(1f))
                        ParamPill("Moisture", "%.0f%%".format(sample.moisturePct), getMetricColor(sample.moisturePct, HeatmapMetric.MOISTURE), Modifier.weight(1f))
                    }

                    // Tailored Agronomist Soil Prescription
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Text(
                                text = getZoneRecommendation(sample, isTelugu),
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // --- Log Soil Sample Button ---
        Button(
            onClick = { showAddSampleDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isTelugu) "కొత్త మృత్తికా పరీక్ష నమోధు చేయండి (Log Soil Sample)" else "+ Record New Soil Test Lab Entry")
        }
    }

    // --- Add Soil Sample Dialog ---
    if (showAddSampleDialog) {
        AddSoilSampleDialog(
            isTelugu = isTelugu,
            onDismiss = { showAddSampleDialog = false },
            onSave = { newSample ->
                viewModel.addSoilSample(newSample)
                showAddSampleDialog = false
                Toast.makeText(context, "Soil Sample Zone Added!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ParamPill(
    label: String,
    value: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = statusColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = statusColor)
        }
    }
}

private fun getMetricValue(sample: SoilSampleEntity, metric: HeatmapMetric): Float {
    return when (metric) {
        HeatmapMetric.PH -> sample.pH
        HeatmapMetric.ORGANIC_CARBON -> sample.organicCarbonPct
        HeatmapMetric.NITROGEN -> sample.nitrogenKgPerAcre
        HeatmapMetric.PHOSPHORUS -> sample.phosphorusKgPerAcre
        HeatmapMetric.POTASSIUM -> sample.potassiumKgPerAcre
        HeatmapMetric.MOISTURE -> sample.moisturePct
    }
}

private fun getMetricColor(value: Float, metric: HeatmapMetric): Color {
    return when (metric) {
        HeatmapMetric.PH -> if (value in 6.2f..7.5f) Color(0xFF22C55E) else if (value < 6.0f) Color(0xFFEF4444) else Color(0xFF3B82F6)
        HeatmapMetric.ORGANIC_CARBON -> if (value >= 0.65f) Color(0xFF22C55E) else if (value >= 0.45f) Color(0xFFEAB308) else Color(0xFFEF4444)
        HeatmapMetric.NITROGEN -> if (value >= 100f) Color(0xFF22C55E) else if (value >= 75f) Color(0xFFEAB308) else Color(0xFFEF4444)
        HeatmapMetric.PHOSPHORUS -> if (value >= 20f) Color(0xFF22C55E) else if (value >= 14f) Color(0xFFEAB308) else Color(0xFFEF4444)
        HeatmapMetric.POTASSIUM -> if (value >= 140f) Color(0xFF22C55E) else if (value >= 100f) Color(0xFFEAB308) else Color(0xFFEF4444)
        HeatmapMetric.MOISTURE -> if (value in 25f..40f) Color(0xFF22C55E) else if (value < 20f) Color(0xFFEF4444) else Color(0xFF3B82F6)
    }
}

private fun getZoneRecommendation(sample: SoilSampleEntity, isTelugu: Boolean): String {
    return if (isTelugu) {
        when {
            sample.pH > 7.8f -> "ఈ జోన్‌లో క్షార గుణం ఎక్కువ. ఎకరాకు 200 కిలోల జిప్సం మరియు పశువుల ఎరువు వేయండి."
            sample.nitrogenKgPerAcre < 80f -> "నత్రజని లోపం ఉంది. ఎకరాకు 35 కిలోల యూరియా పిలక దశలో వేయండి."
            sample.organicCarbonPct < 0.5f -> "సేంద్రీయ కార్బన్ తక్కువగా ఉంది. ఎకరాకు 2 టన్నుల వర్మీకంపోస్ట్ చల్లండి."
            else -> "ఈ జోన్‌లో నేల సారవంతత బాగుంది. సాధారణ ఎరువుల పిచికారీ కొనసాగించండి."
        }
    } else {
        when {
            sample.pH > 7.8f -> "High alkalinity detected. Apply 200 kg Gypsum/acre + Farm Yard Manure."
            sample.nitrogenKgPerAcre < 80f -> "Nitrogen deficient zone. Split apply 35 kg Urea at tillering stage."
            sample.organicCarbonPct < 0.5f -> "Low organic carbon. Apply 2 Tons Vermicompost per acre."
            else -> "Soil fertility is optimal in this zone. Maintain standard split NPK doses."
        }
    }
}

@Composable
private fun AddSoilSampleDialog(
    isTelugu: Boolean,
    onDismiss: () -> Unit,
    onSave: (SoilSampleEntity) -> Unit
) {
    var zoneName by remember { mutableStateOf("New Plot Zone") }
    var phText by remember { mutableStateOf("6.8") }
    var ocText by remember { mutableStateOf("0.60") }
    var nText by remember { mutableStateOf("90") }
    var pText by remember { mutableStateOf("20") }
    var kText by remember { mutableStateOf("130") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isTelugu) "మృత్తికా పరీక్ష వివరాలు" else "Add Soil Lab Test Record", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(value = zoneName, onValueChange = { zoneName = it }, label = { Text("Zone / Plot Identifier") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = phText, onValueChange = { phText = it }, label = { Text("pH") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = ocText, onValueChange = { ocText = it }, label = { Text("OC %") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nText, onValueChange = { nText = it }, label = { Text("N (kg/A)") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = pText, onValueChange = { pText = it }, label = { Text("P (kg/A)") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = kText, onValueChange = { kText = it }, label = { Text("K (kg/A)") }, modifier = Modifier.weight(1f), singleLine = true)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sample = SoilSampleEntity(
                        zoneName = zoneName.ifBlank { "Zone Probe" },
                        gridXRatio = kotlin.random.Random.nextFloat() * 0.6f + 0.2f,
                        gridYRatio = kotlin.random.Random.nextFloat() * 0.6f + 0.2f,
                        pH = phText.toFloatOrNull() ?: 7.0f,
                        organicCarbonPct = ocText.toFloatOrNull() ?: 0.6f,
                        nitrogenKgPerAcre = nText.toFloatOrNull() ?: 90f,
                        phosphorusKgPerAcre = pText.toFloatOrNull() ?: 20f,
                        potassiumKgPerAcre = kText.toFloatOrNull() ?: 130f,
                        electricalConductivity = 0.8f,
                        moisturePct = 30f,
                        soilType = "Field Sample"
                    )
                    onSave(sample)
                }
            ) {
                Text(if (isTelugu) "సేవ్ చేయండి" else "Save Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
