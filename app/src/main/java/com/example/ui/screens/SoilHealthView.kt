package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.model.FertilizerStage
import com.example.data.model.SoilFertilizerPlan
import com.example.ui.viewmodel.ScannerViewModel
import com.example.ui.viewmodel.SoilUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilHealthView(
    viewModel: ScannerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.soilUiState.collectAsState()
    val gemmaThinking by viewModel.gemmaThinkingSoil.collectAsState()

    var cropName by remember { mutableStateOf("Rice / Paddy") }
    var landAreaAcres by remember { mutableStateOf(2.5f) }
    var soilType by remember { mutableStateOf("Alluvial Soil") }
    var npkStatus by remember { mutableStateOf("Nitrogen Deficient") }

    var expandedCropMenu by remember { mutableStateOf(false) }
    var expandedSoilMenu by remember { mutableStateOf(false) }
    var showThinking by remember { mutableStateOf(false) }

    val crops = listOf("Rice / Paddy", "Wheat", "Cotton", "Sugarcane", "Tomato", "Potato", "Chilli", "Maize", "Mustard")
    val soilTypes = listOf("Alluvial Soil", "Black Cotton Soil", "Red & Yellow Soil", "Sandy Loam", "Clay Soil", "Laterite Soil")
    val npkOptions = listOf("Nitrogen Deficient", "Phosphorus Deficient", "Potash Deficient", "Low Organic Carbon", "Balanced / Optimal")

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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
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
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Precision Agronomy",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Precision Soil & NPK Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Gemma 4 stoichiometry engine • Exact NPK split doses",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- Input Controls Card ---
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
                    text = "Field Parameters",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                // Crop Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCropMenu,
                    onExpandedChange = { expandedCropMenu = !expandedCropMenu }
                ) {
                    OutlinedTextField(
                        value = cropName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selected Crop") },
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

                // Land Area Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Land Area (Acres):",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "%.1f Acres".format(landAreaAcres),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    Slider(
                        value = landAreaAcres,
                        onValueChange = { landAreaAcres = it },
                        valueRange = 0.5f..20f,
                        steps = 39,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Soil Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedSoilMenu,
                    onExpandedChange = { expandedSoilMenu = !expandedSoilMenu }
                ) {
                    OutlinedTextField(
                        value = soilType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Soil Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSoilMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSoilMenu,
                        onDismissRequest = { expandedSoilMenu = false }
                    ) {
                        soilTypes.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    soilType = selection
                                    expandedSoilMenu = false
                                }
                            )
                        }
                    }
                }

                // NPK Soil Status Chips
                Text(
                    text = "Soil Fertility Status:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    npkOptions.take(3).forEach { option ->
                        FilterChip(
                            selected = npkStatus == option,
                            onClick = { npkStatus = option },
                            label = { Text(option.replace(" Deficient", ""), fontSize = 11.sp) }
                        )
                    }
                }

                // Action Button
                Button(
                    onClick = {
                        viewModel.calculateSoilPlan(
                            cropName = cropName,
                            landAreaAcres = landAreaAcres.toDouble(),
                            soilType = soilType,
                            npkStatus = npkStatus
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculate Fertilizer Doses with Gemma 4", fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- Gemma 4 Reasoning Thinking Toggle ---
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
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Gemma 4 Internal Agronomy Reasoning",
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

        // --- State Results Display ---
        when (uiState) {
            is SoilUiState.Loading -> {
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
                        CircularProgressIndicator()
                        Text("Calculating stoichiometry & nutrient release curves...", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            is SoilUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = (uiState as SoilUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is SoilUiState.Success -> {
                val plan = (uiState as SoilUiState.Success).plan
                SoilResultCard(plan = plan)
            }
            SoilUiState.Idle -> {
                // Initial prompt
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
                        Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        Text(
                            text = "Get Precise Nitrogen, Phosphorus & Potash Doses",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Select your crop and field size above. Gemma 4 will generate exact bag calculations and application stages.",
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
fun SoilResultCard(plan: SoilFertilizerPlan) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${plan.crop_name} Fertilizer Plan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "For ${plan.land_area_acres} Acres in ${plan.soil_type}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = plan.cost_estimate_inr,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Divider()

            Text(
                text = "Total Required Fertilizer Amounts:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            // Nutrient Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientBadge("Urea (N)", "%.1f kg".format(plan.urea_kg), Color(0xFF2E7D32), Modifier.weight(1f))
                NutrientBadge("DAP (P)", "%.1f kg".format(plan.dap_kg), Color(0xFF1565C0), Modifier.weight(1f))
                NutrientBadge("MOP (K)", "%.1f kg".format(plan.mop_kg), Color(0xFFD84315), Modifier.weight(1f))
            }

            // Organic compost
            if (plan.organic_compost_kg > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Compost, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text(
                        text = "Organic Compost / Farm Yard Manure: %.1f kg".format(plan.organic_compost_kg),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Stage Schedule Timeline
            Text(
                text = "Application Timing Schedule:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            plan.schedule.forEachIndexed { index, stage ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stage.stage_name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "Timing: ${stage.timing}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = stage.recommended_dose, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Micronutrient Advice
            if (plan.micronutrient_advice.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Column {
                            Text("Micronutrient & Soil Booster:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(plan.micronutrient_advice, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientBadge(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
