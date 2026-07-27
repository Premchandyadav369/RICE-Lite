package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.IrrigationScheduleEntity
import com.example.ui.viewmodel.IrrigationViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IrrigationSchedulerView(
    irrigationViewModel: IrrigationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val selectedCrop by irrigationViewModel.selectedCrop.collectAsState()
    val selectedSoil by irrigationViewModel.selectedSoil.collectAsState()
    val farmArea by irrigationViewModel.farmArea.collectAsState()
    val temp by irrigationViewModel.temperature.collectAsState()
    val hum by irrigationViewModel.humidity.collectAsState()
    val moisture by irrigationViewModel.soilMoisture.collectAsState()
    val rain by irrigationViewModel.rainfallForecast.collectAsState()
    val method by irrigationViewModel.irrigationType.collectAsState()
    val schedule by irrigationViewModel.calculatedSchedule.collectAsState()
    val savedSchedules by irrigationViewModel.savedSchedules.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Calculate Schedule, 1: Saved Logs (Room DB)
    var acresInputText by remember { mutableStateOf(farmArea.toString()) }

    val crops = listOf(
        "Paddy (Rice)", "Wheat", "Cotton", "Sugarcane",
        "Maize", "Potato", "Tomato", "Chilli"
    )

    val soilTypes = listOf(
        "Loamy / Alluvial Soil",
        "Heavy Clay Soil",
        "Sandy / Light Soil",
        "Black Cotton Soil"
    )

    val irrigationMethods = listOf(
        "Drip Irrigation",
        "Sprinkler System",
        "Flood / Furrow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Top Selector Tab Bar ---
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
                    icon = { Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Saved Logs (${savedSchedules.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        if (activeTab == 0) {
            // CALCULATE SMART IRRIGATION SCHEDULE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(schedule.statusColorHex).copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(schedule.statusColorHex))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Waves, contentDescription = null, tint = Color(schedule.statusColorHex))
                            Text(schedule.status, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(schedule.statusColorHex))
                        }
                        Text(schedule.adviceNotes, fontSize = 12.sp, color = Color(0xFF374151))
                    }
                }

                // Farm & Crop Configuration Card
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
                        Text("1. Crop & Field Parameters", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827))

                        // Crop Choice Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(crops) { crop ->
                                FilterChip(
                                    selected = selectedCrop == crop,
                                    onClick = { irrigationViewModel.setCrop(crop) },
                                    label = { Text(crop, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0284C7),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Soil Choice Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(soilTypes) { soil ->
                                FilterChip(
                                    selected = selectedSoil == soil,
                                    onClick = { irrigationViewModel.setSoil(soil) },
                                    label = { Text(soil, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0EA5E9),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Irrigation Method Choice Chips
                        Text("Irrigation System Type:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B5563))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            irrigationMethods.forEach { meth ->
                                FilterChip(
                                    selected = method == meth,
                                    onClick = { irrigationViewModel.setIrrigationType(meth) },
                                    label = { Text(meth, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF16A34A),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Area Acres Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Farm Size (Acres):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                            OutlinedTextField(
                                value = acresInputText,
                                onValueChange = { input ->
                                    acresInputText = input
                                    input.toDoubleOrNull()?.let { irrigationViewModel.setFarmArea(it) }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(100.dp),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                // Weather & Soil Moisture Live Controls Card
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
                        Text("2. Environmental & Soil Sensors", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827))

                        // Soil Moisture Threshold Slider
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Current Soil Moisture:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                                Text("${moisture.roundToInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                            }
                            Slider(
                                value = moisture.toFloat(),
                                onValueChange = { irrigationViewModel.setSoilMoisture(it.toDouble()) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF0284C7),
                                    activeTrackColor = Color(0xFF0284C7)
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Wilting Point (15%)", fontSize = 9.sp, color = Color(0xFFEF4444))
                                Text("Field Capacity (35%)", fontSize = 9.sp, color = Color(0xFF16A34A))
                                Text("Saturation (100%)", fontSize = 9.sp, color = Color(0xFF2563EB))
                            }
                        }

                        Divider(color = Color(0xFFF3F4F6))

                        // Weather Controls (Temperature & Humidity)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Temp Slider
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Temperature: ${temp.roundToInt()}°C", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = temp.toFloat(),
                                    onValueChange = { irrigationViewModel.setTemperature(it.toDouble()) },
                                    valueRange = 10f..50f
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            // Humidity Slider
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Humidity: ${hum.roundToInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = hum.toFloat(),
                                    onValueChange = { irrigationViewModel.setHumidity(it.toDouble()) },
                                    valueRange = 10f..100f
                                )
                            }
                        }

                        // Rainfall Forecast Control
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rainfall Forecast (24h): ${rain.roundToInt()} mm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = rain.toFloat(),
                                onValueChange = { irrigationViewModel.setRainfallForecast(it.toDouble()) },
                                valueRange = 0f..50f,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }

                // Calculation Outcome Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💧 Calculated Daily Water Schedule", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0369A1))
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE0F2FE)) {
                                Text("ETc: ${schedule.etcMmPerDay} mm/day", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1), modifier = Modifier.padding(6.dp))
                            }
                        }

                        // 3 Primary Metric Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WaterMetricCard(
                                label = "Total Volume",
                                value = "${schedule.totalWaterLiters.roundToInt()} L",
                                sub = "for ${schedule.farmAreaAcres} Acres",
                                icon = Icons.Default.Water,
                                color = Color(0xFF0284C7),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            WaterMetricCard(
                                label = "Water Depth",
                                value = "${schedule.waterDepthMm} mm",
                                sub = "Net crop deficit",
                                icon = Icons.Default.Layers,
                                color = Color(0xFF059669),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            WaterMetricCard(
                                label = "Pump Duration",
                                value = "${schedule.durationMinutes} mins",
                                sub = schedule.irrigationType,
                                icon = Icons.Default.Timer,
                                color = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Recommended Frequency & Time Slot
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF0284C7))
                                Column {
                                    Text("Recommended Frequency", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                    Text(schedule.frequencyRecommendation, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                                }
                            }
                        }
                    }
                }

                // Save Log to Room DB Button
                Button(
                    onClick = {
                        irrigationViewModel.saveScheduleToDb()
                        Toast.makeText(context, "Saved ${schedule.cropName} Irrigation Schedule to Room DB! ✓", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Irrigation Log to Room Database", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        } else {
            // SAVED LOGS FROM ROOM DATABASE
            if (savedSchedules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(48.dp))
                        Text("No Saved Irrigation Logs Yet", fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                        Text("Calculate water schedules and save them to track daily irrigation history.", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedSchedules, key = { it.id }) { log ->
                        SavedIrrigationCard(
                            schedule = log,
                            onDelete = { irrigationViewModel.deleteSchedule(log.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WaterMetricCard(
    label: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Text(sub, fontSize = 9.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
fun SavedIrrigationCard(
    schedule: IrrigationScheduleEntity,
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
                    Text(schedule.cropName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0369A1))
                    Text("${schedule.soilType} • ${schedule.farmAreaAcres} Acres", fontSize = 12.sp, color = Color(0xFF6B7280))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Log", tint = Color(0xFFEF4444))
                }
            }

            Divider(color = Color(0xFFF3F4F6))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Water: ${schedule.recommendedWaterLiters.roundToInt()} L", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                Text("Depth: ${schedule.recommendedWaterMm} mm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                Text("Duration: ${schedule.irrigationDurationMinutes} mins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF0F9FF)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                    Text(schedule.irrigationFrequency, fontSize = 11.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
