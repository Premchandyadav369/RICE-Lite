package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
    val farmArea by fertilizerViewModel.farmAreaAcres.collectAsState()
    val schedule by fertilizerViewModel.calculatedSchedule.collectAsState()
    val savedPlans by fertilizerViewModel.savedPlans.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Calculate New Plan, 1: Saved Plans (Room DB)
    var acresInputText by remember { mutableStateOf(farmArea.toString()) }

    val crops = listOf(
        "Paddy (Rice)", "Wheat", "Cotton", "Sugarcane",
        "Maize", "Potato", "Tomato", "Chilli", "Soybean"
    )

    val soilTypes = listOf(
        "Loamy / Alluvial Soil",
        "Heavy Clay Soil",
        "Sandy / Light Soil",
        "Black Cotton Soil",
        "Red Sandy Soil"
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
                            Text("Fertilizer Planner & NPK Calculator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
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

                        // Farm Area Acres Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("3. Farm Area (Acres):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
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
