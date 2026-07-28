package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.FertilizerPlanEntity
import com.example.ui.KrishiViewModel
import com.example.ui.viewmodel.FertilizerViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FertilizerPlannerView(
    viewModel: KrishiViewModel? = null,
    fertilizerViewModel: FertilizerViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val selectedCrop by fertilizerViewModel.selectedCrop.collectAsState()
    val selectedSoil by fertilizerViewModel.selectedSoil.collectAsState()
    val selectedStage by fertilizerViewModel.selectedCropStage.collectAsState()
    val selectedRegion by fertilizerViewModel.selectedAgroRegion.collectAsState()
    val farmArea by fertilizerViewModel.farmAreaAcres.collectAsState()
    val schedule by fertilizerViewModel.calculatedSchedule.collectAsState()
    val savedPlans by fertilizerViewModel.savedPlans.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Calculate New Plan, 1: Saved Plans (Room DB)
    var acresInputText by remember { mutableStateOf(farmArea.toString()) }

    val crops = listOf(
        "Paddy (Rice)", "Cotton (Patti)", "Chilli (Mirchi)", "Turmeric (Pasupu)",
        "Groundnut (Verusanaga)", "Maize (Mokka Jonna)", "Sugarcane", "Tomato"
    )

    val soilTypes = listOf(
        "Loamy / Alluvial Soil",
        "Black Cotton Soil (Regur)",
        "Red Sandy Soil (Chalka)",
        "Heavy Clay Soil",
        "Sandy / Light Soil"
    )

    val cropStages = listOf(
        "Basal / Sowing Stage (0-15 Days)",
        "Vegetative / Tillering Stage (20-45 Days)",
        "Flowering / Panicle Stage (45-75 Days)",
        "Grain Filling / Maturity Stage (75-110+ Days)"
    )

    val agroRegions = listOf(
        "Krishna-Godavari Delta Belt (AP)",
        "Guntur & Prakasam Chilli Belt (AP)",
        "Warangal & Khammam Cotton Belt (TS)",
        "Rayalaseema Red Soil Belt (AP)",
        "Nizamabad Turmeric Belt (TS)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Top Tab Selector ---
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White
        ) {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Calculate Schedule", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Saved Plans (${savedPlans.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        if (activeTab == 0) {
            // CALCULATE FERTILIZER PLAN
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Input Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Grass, contentDescription = null, tint = Color(0xFF14532D))
                            Text("AP & Telangana Fertilizer Dosage Calculator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
                        }

                        // Crop Selector Chips
                        Text("1. Select Crop:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(crops) { crop ->
                                FilterChip(
                                    selected = selectedCrop == crop,
                                    onClick = { fertilizerViewModel.setSelectedCrop(crop) },
                                    label = { Text(crop, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF14532D),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Soil Type Selector Chips
                        Text("2. Select Soil Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(soilTypes) { soil ->
                                FilterChip(
                                    selected = selectedSoil == soil,
                                    onClick = { fertilizerViewModel.setSelectedSoil(soil) },
                                    label = { Text(soil, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0EA5E9),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Crop Stage Selector Chips (CRITICAL)
                        Text("3. Current Crop Growth Stage:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(cropStages) { stage ->
                                FilterChip(
                                    selected = selectedStage == stage,
                                    onClick = { fertilizerViewModel.setSelectedCropStage(stage) },
                                    label = { Text(stage, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFD97706),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // AP / TS Agro-Climatic Belt Selector
                        Text("4. AP / Telangana Agricultural Belt:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(agroRegions) { region ->
                                FilterChip(
                                    selected = selectedRegion == region,
                                    onClick = { fertilizerViewModel.setSelectedAgroRegion(region) },
                                    label = { Text(region, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF7C3AED),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Farm Area Acres Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("5. Farm Area (Acres):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                                Text("Calculates total bag quantities", fontSize = 10.sp, color = Color(0xFF6B7280))
                            }
                            OutlinedTextField(
                                value = acresInputText,
                                onValueChange = { input ->
                                    acresInputText = input
                                    input.toDoubleOrNull()?.let { fertilizerViewModel.setFarmArea(it) }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(100.dp),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                // Stage-Specific Immediate Dosage Action Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFFD97706))
                            Text("🎯 Immediate Stage-Specific Dosage Recommendation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF92400E))
                        }

                        Text(
                            text = schedule.stageSpecificDosage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF78350F),
                            lineHeight = 18.sp
                        )

                        HorizontalDivider(color = Color(0xFFFCD34D))

                        // Immediate Stage Bags Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FertilizerBagItem(name = "Stage Urea", bags = "${schedule.stageUreaBags} Bags", kg = "${(schedule.stageUreaBags * 50).roundToInt()} kg", color = Color(0xFF16A34A))
                            FertilizerBagItem(name = "Stage DAP", bags = "${schedule.stageDapBags} Bags", kg = "${(schedule.stageDapBags * 50).roundToInt()} kg", color = Color(0xFFD97706))
                            FertilizerBagItem(name = "Stage MOP", bags = "${schedule.stageMopBags} Bags", kg = "${(schedule.stageMopBags * 50).roundToInt()} kg", color = Color(0xFF2563EB))
                        }
                    }
                }

                // Soil Type & ANGRAU/PJTSAU Advisory Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFA)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0D9488))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Terrain, contentDescription = null, tint = Color(0xFF0D9488))
                            Text("Soil & Regional Extension Guidance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF115E59))
                        }

                        Text(schedule.soilTypeAdjustmentReason, fontSize = 12.sp, color = Color(0xFF134E4A), lineHeight = 16.sp)
                        Text(schedule.apTsExtensionAdvice, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0F766E), lineHeight = 16.sp)
                    }
                }

                // NPK Requirements Card (sourcing Room DB ratios)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF059669))
                                Text("Room DB N-P-K Doses", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF065F46))
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFD1FAE5)) {
                                Text("${schedule.acres} Acres Total", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(6.dp))
                            }
                        }

                        Text(schedule.remarks, fontSize = 11.sp, color = Color(0xFF047857), fontWeight = FontWeight.Medium)

                        // NPK Triple Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NpkMetricCard(label = "Nitrogen (N)", value = "${schedule.totalN} kg", sub = "${schedule.perAcreN} kg/acre", color = Color(0xFF2563EB), modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            NpkMetricCard(label = "Phosphorus (P)", value = "${schedule.totalP} kg", sub = "${schedule.perAcreP} kg/acre", color = Color(0xFFD97706), modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            NpkMetricCard(label = "Potassium (K)", value = "${schedule.totalK} kg", sub = "${schedule.perAcreK} kg/acre", color = Color(0xFF7C3AED), modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Interactive NPK Nutrient Ratio Visualizer Chart
                InteractiveNpkRatioChart(
                    totalN = schedule.totalN,
                    totalP = schedule.totalP,
                    totalK = schedule.totalK,
                    perAcreN = schedule.perAcreN,
                    perAcreP = schedule.perAcreP,
                    perAcreK = schedule.perAcreK,
                    ureaBags = schedule.ureaBags,
                    dapBags = schedule.dapBags,
                    mopBags = schedule.mopBags,
                    cropName = schedule.cropName
                )

                // Commercial Fertilizer Bags Requirement Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🛒 Commercial Fertilizer Purchase Requirement", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FertilizerBagItem(name = "Urea (46% N)", bags = "${schedule.ureaBags} Bags", kg = "${(schedule.ureaBags * 50).roundToInt()} kg", color = Color(0xFF16A34A))
                            FertilizerBagItem(name = "DAP (18-46-0)", bags = "${schedule.dapBags} Bags", kg = "${(schedule.dapBags * 50).roundToInt()} kg", color = Color(0xFFD97706))
                            FertilizerBagItem(name = "MOP (60% K)", bags = "${schedule.mopBags} Bags", kg = "${(schedule.mopBags * 50).roundToInt()} kg", color = Color(0xFF2563EB))
                        }
                    }
                }

                // Customized Step-by-Step Schedule Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("📅 Custom Application Schedule", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827))

                        ScheduleStageItem(stage = "1. Basal Application", text = schedule.basalDose, icon = Icons.Default.Agriculture)
                        ScheduleStageItem(stage = "2. 1st Top Dressing (Tillering)", text = schedule.firstTopDressing, icon = Icons.Default.ElectricBolt)
                        ScheduleStageItem(stage = "3. 2nd Top Dressing (Panicle / Bloom)", text = schedule.secondTopDressing, icon = Icons.Default.LocalFlorist)
                        ScheduleStageItem(stage = "4. Micronutrients & Boosters", text = schedule.micronutrients, icon = Icons.Default.Biotech)
                        ScheduleStageItem(stage = "5. Organic & Bio-Fertilizer Mix", text = schedule.organicBiofertilizer, icon = Icons.Default.Eco)
                    }
                }

                // Save Schedule to Room DB Button
                Button(
                    onClick = {
                        fertilizerViewModel.saveCurrentPlan()
                        Toast.makeText(context, "Saved ${schedule.cropName} Fertilizer Plan to Room Database! ✓", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D))
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Plan to Room Database", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        } else {
            // SAVED PLANS FROM ROOM DATABASE
            if (savedPlans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(48.dp))
                        Text("No Saved Fertilizer Plans Yet", fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                        Text("Calculate customized NPK schedules and tap 'Save Plan' to store them locally.", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedPlans, key = { it.id }) { plan ->
                        SavedPlanCard(
                            plan = plan,
                            onDelete = { fertilizerViewModel.deletePlan(plan.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NpkMetricCard(
    label: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Text(sub, fontSize = 9.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
fun FertilizerBagItem(
    name: String,
    bags: String,
    kg: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Text(bags, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Text(kg, fontSize = 10.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
fun ScheduleStageItem(
    stage: String,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF14532D), modifier = Modifier.size(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stage, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
            Text(text, fontSize = 11.sp, color = Color(0xFF374151))
        }
    }
}

@Composable
fun SavedPlanCard(
    plan: FertilizerPlanEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(plan.cropName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF14532D))
                    Text("${plan.soilType} • ${plan.farmAreaAcres} Acres", fontSize = 12.sp, color = Color(0xFF6B7280))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Plan", tint = Color(0xFFEF4444))
                }
            }

            Divider(color = Color(0xFFF3F4F6))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Urea: ${plan.totalUreaBags} Bags", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                Text("DAP: ${plan.totalDapBags} Bags", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                Text("MOP: ${plan.totalMopBags} Bags", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF3F4F6)
            ) {
                Text(plan.basalDose, fontSize = 11.sp, color = Color(0xFF374151), modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun InteractiveNpkRatioChart(
    totalN: Double,
    totalP: Double,
    totalK: Double,
    perAcreN: Double,
    perAcreP: Double,
    perAcreK: Double,
    ureaBags: Double,
    dapBags: Double,
    mopBags: Double,
    cropName: String
) {
    var chartMode by remember { mutableStateOf(0) } // 0: NPK Ratio %, 1: Total Dosage (kg), 2: Fertilizer Bags
    var selectedNutrientIndex by remember { mutableStateOf<Int?>(null) } // 0: N, 1: P, 2: K

    // Calculate Ratio Normalization against P
    val minVal = listOf(perAcreP, perAcreK, 1.0).filter { it > 0 }.minOrNull() ?: 1.0
    val ratioN = (perAcreN / minVal).roundTo(1)
    val ratioP = (perAcreP / minVal).roundTo(1)
    val ratioK = (perAcreK / minVal).roundTo(1)

    val grandTotalNpk = (totalN + totalP + totalK).coerceAtLeast(1.0)
    val pctN = (totalN / grandTotalNpk * 100).roundTo(1)
    val pctP = (totalP / grandTotalNpk * 100).roundTo(1)
    val pctK = (totalK / grandTotalNpk * 100).roundTo(1)

    // Animated Heights
    val animN by animateFloatAsState(
        targetValue = when (chartMode) {
            0 -> (pctN / 100f).toFloat()
            1 -> (totalN / 300.0).coerceAtMost(1.0).toFloat()
            else -> (ureaBags / 10.0).coerceAtMost(1.0).toFloat()
        },
        animationSpec = tween(600),
        label = "animN"
    )

    val animP by animateFloatAsState(
        targetValue = when (chartMode) {
            0 -> (pctP / 100f).toFloat()
            1 -> (totalP / 300.0).coerceAtMost(1.0).toFloat()
            else -> (dapBags / 10.0).coerceAtMost(1.0).toFloat()
        },
        animationSpec = tween(600),
        label = "animP"
    )

    val animK by animateFloatAsState(
        targetValue = when (chartMode) {
            0 -> (pctK / 100f).toFloat()
            1 -> (totalK / 300.0).coerceAtMost(1.0).toFloat()
            else -> (mopBags / 10.0).coerceAtMost(1.0).toFloat()
        },
        animationSpec = tween(600),
        label = "animK"
    )

    val nutrientInfos = listOf(
        Triple("Nitrogen (N)", "Essential for vigorous vegetative growth, leaf development, and chlorophyll production.", Color(0xFF2563EB)),
        Triple("Phosphorus (P)", "Promotes deep root branching, early flowering, seed formation, and energy transfer.", Color(0xFFD97706)),
        Triple("Potassium (K)", "Enhances drought tolerance, disease resistance, stalk strength, and grain filling.", Color(0xFF7C3AED))
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF14532D))
                    Text("Interactive NPK Ratio Chart", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        "Ratio: $ratioN : $ratioP : $ratioK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Mode Selector Segmented Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Nutrient %", "Dosage (kg)", "Bags Required").forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chartMode == index) Color.White else Color.Transparent)
                            .clickable { chartMode = index }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            fontSize = 11.sp,
                            fontWeight = if (chartMode == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (chartMode == index) Color(0xFF14532D) else Color(0xFF6B7280)
                        )
                    }
                }
            }

            // Interactive Bar Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFF9FAFB), shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE5E7EB), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Bar 1: Nitrogen
                    NpkBarColumn(
                        label = "N (Nitrogen)",
                        animProgress = animN,
                        displayVal = when (chartMode) {
                            0 -> "$pctN%"
                            1 -> "$totalN kg"
                            else -> "$ureaBags Bags"
                        },
                        subText = "$perAcreN kg/ac",
                        color = Color(0xFF2563EB),
                        isSelected = selectedNutrientIndex == 0,
                        onClick = { selectedNutrientIndex = if (selectedNutrientIndex == 0) null else 0 },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Bar 2: Phosphorus
                    NpkBarColumn(
                        label = "P (Phosphorus)",
                        animProgress = animP,
                        displayVal = when (chartMode) {
                            0 -> "$pctP%"
                            1 -> "$totalP kg"
                            else -> "$dapBags Bags"
                        },
                        subText = "$perAcreP kg/ac",
                        color = Color(0xFFD97706),
                        isSelected = selectedNutrientIndex == 1,
                        onClick = { selectedNutrientIndex = if (selectedNutrientIndex == 1) null else 1 },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Bar 3: Potassium
                    NpkBarColumn(
                        label = "K (Potassium)",
                        animProgress = animK,
                        displayVal = when (chartMode) {
                            0 -> "$pctK%"
                            1 -> "$totalK kg"
                            else -> "$mopBags Bags"
                        },
                        subText = "$perAcreK kg/ac",
                        color = Color(0xFF7C3AED),
                        isSelected = selectedNutrientIndex == 2,
                        onClick = { selectedNutrientIndex = if (selectedNutrientIndex == 2) null else 2 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Expandable Nutrient Agronomic Info Sheet
            AnimatedVisibility(visible = selectedNutrientIndex != null) {
                selectedNutrientIndex?.let { idx ->
                    val info = nutrientInfos[idx]
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = info.third.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, info.third.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = info.third)
                            Column {
                                Text(info.first, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = info.third)
                                Text(info.second, fontSize = 11.sp, color = Color(0xFF374151))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NpkBarColumn(
    label: String,
    animProgress: Float,
    displayVal: String,
    subText: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(displayVal, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .weight(1f, fill = false),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width
                val maxHeight = size.height
                val barHeight = maxHeight * animProgress.coerceIn(0.1f, 1f)

                // Background Bar Track
                drawRoundRect(
                    color = color.copy(alpha = 0.15f),
                    size = Size(barWidth, maxHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Filled Animated Active Bar
                drawRoundRect(
                    color = if (isSelected) color else color.copy(alpha = 0.85f),
                    topLeft = Offset(0f, maxHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        Text(subText, fontSize = 9.sp, color = Color(0xFF6B7280))
    }
}

private fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}
