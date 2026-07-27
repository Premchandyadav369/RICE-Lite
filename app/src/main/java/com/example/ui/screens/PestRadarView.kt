package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PestRiskAssessment
import com.example.data.model.PestThreat
import com.example.ui.viewmodel.PestUiState
import com.example.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestRadarView(
    viewModel: ScannerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.pestUiState.collectAsState()
    val gemmaThinking by viewModel.gemmaThinkingPest.collectAsState()

    var cropName by remember { mutableStateOf("Tomato") }
    var temperatureCelsius by remember { mutableStateOf(28f) }
    var humidityPercent by remember { mutableStateOf(82f) }
    var moistureCondition by remember { mutableStateOf("Recent Rainfall & High Moisture") }

    var expandedCropMenu by remember { mutableStateOf(false) }
    var expandedMoistureMenu by remember { mutableStateOf(false) }
    var showThinking by remember { mutableStateOf(false) }

    val crops = listOf("Tomato", "Rice / Paddy", "Cotton", "Potato", "Wheat", "Chilli", "Sugarcane", "Mustard")
    val moistureConditions = listOf(
        "Recent Rainfall & High Moisture",
        "Dry & Arid Sunshine",
        "Heavy Morning Fog / Dew",
        "Waterlogged Standing Water"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Banner ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Pest Radar",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Micro-Climate Pest Risk Radar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Gemma 4 Epidemiological Spore & Pest Vectors",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- Climate Inputs Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Micro-Climate Conditions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                // Crop Selection
                ExposedDropdownMenuBox(
                    expanded = expandedCropMenu,
                    onExpandedChange = { expandedCropMenu = !expandedCropMenu }
                ) {
                    OutlinedTextField(
                        value = cropName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Crop") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCropMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCropMenu,
                        onDismissRequest = { expandedCropMenu = false }
                    ) {
                        crops.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    cropName = selection
                                    expandedCropMenu = false
                                }
                            )
                        }
                    }
                }

                // Temperature Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Temperature (°C):", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("${temperatureCelsius.toInt()}°C", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = temperatureCelsius,
                        onValueChange = { temperatureCelsius = it },
                        valueRange = 10f..45f,
                        steps = 34,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Humidity Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Relative Humidity (% RH):", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("${humidityPercent.toInt()}% RH", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = humidityPercent,
                        onValueChange = { humidityPercent = it },
                        valueRange = 20f..100f,
                        steps = 79,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Moisture Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMoistureMenu,
                    onExpandedChange = { expandedMoistureMenu = !expandedMoistureMenu }
                ) {
                    OutlinedTextField(
                        value = moistureCondition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Leaf Wetness / Rainfall") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMoistureMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMoistureMenu,
                        onDismissRequest = { expandedMoistureMenu = false }
                    ) {
                        moistureConditions.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    moistureCondition = selection
                                    expandedMoistureMenu = false
                                }
                            )
                        }
                    }
                }

                // Evaluate Button
                Button(
                    onClick = {
                        viewModel.evaluatePestRisk(
                            cropName = cropName,
                            temperatureCelsius = temperatureCelsius.toInt(),
                            humidityPercent = humidityPercent.toInt(),
                            rainfallStatus = moistureCondition
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Evaluate Outbreak Risk with Gemma 4", fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- Gemma 4 Internal Reasoning Drawer ---
        if (!gemmaThinking.isNullOrEmpty()) {
            OutlinedCard(
                onClick = { showThinking = !showThinking },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text(
                            text = "Gemma 4 Biological Spore Reasoning",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = if (showThinking) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
                AnimatedVisibility(visible = showThinking) {
                    Text(
                        text = gemmaThinking ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        // --- Risk Display State ---
        when (uiState) {
            is PestUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.error)
                        Text("Simulating pest propagation vectors...", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            is PestUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = (uiState as PestUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is PestUiState.Success -> {
                val assessment = (uiState as PestUiState.Success).assessment
                PestResultCard(assessment = assessment)
            }
            PestUiState.Idle -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.BugReport, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                        Text(
                            text = "Predict Fungal & Insect Outbreaks Before They Occur",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Set current field temperature and humidity above. Gemma 4 will evaluate spore germination thresholds and give early warning recommendations.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PestResultCard(assessment: PestRiskAssessment) {
    val riskColor = when (assessment.risk_level.uppercase()) {
        "SEVERE", "HIGH" -> Color(0xFFD32F2F)
        "MODERATE" -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row with Risk Level Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${assessment.crop_name} Outbreak Risk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Micro-climate Epidemiological Model",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = riskColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, riskColor)
                ) {
                    Text(
                        text = "${assessment.risk_level.uppercase()} RISK",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = riskColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Summary
            Text(
                text = assessment.weather_summary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Divider()

            // Primary Threat Cards
            Text(
                text = "Primary Pathogen / Pest Threats:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            assessment.primary_threats.forEach { threat ->
                ThreatCard(threat = threat)
            }

            // Early Warning Advice
            if (assessment.early_warning_advice.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Preventive Micro-climate Action Plan:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        assessment.early_warning_advice.forEach { advice ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(advice, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThreatCard(threat: PestThreat) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = threat.pest_or_fungus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = "Probability: ${threat.probability}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Symptoms: ${threat.symptoms_to_watch.joinToString(", ")}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Preventive Action: ${threat.preventive_action}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
