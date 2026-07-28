package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KrishiViewModel
import com.example.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrecisionFarmView(
    krishiViewModel: KrishiViewModel,
    scannerViewModel: ScannerViewModel,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(0) } // 0: Satellite NDVI Map, 1: Fertilizer Planner, 2: Soil NPK Analysis, 3: Pest Radar Outbreaks

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Sub-Tab Navigation Bar
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White
        ) {
            ScrollableTabRow(
                selectedTabIndex = subTab,
                containerColor = Color.White,
                edgePadding = 8.dp
            ) {
                Tab(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    text = { Text("Field Map", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    text = { Text("Satellite NDVI", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.SatelliteAlt, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 2,
                    onClick = { subTab = 2 },
                    text = { Text("Fertilizer Planner", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Grass, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 3,
                    onClick = { subTab = 3 },
                    text = { Text("Irrigation Scheduler", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 4,
                    onClick = { subTab = 4 },
                    text = { Text("Soil Heatmap", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 5,
                    onClick = { subTab = 5 },
                    text = { Text("Soil NPK", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 6,
                    onClick = { subTab = 6 },
                    text = { Text("Offline Manuals", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.CloudOff, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 7,
                    onClick = { subTab = 7 },
                    text = { Text("Pest Radar", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (subTab) {
                0 -> FieldBoundaryMapView(modifier = Modifier.fillMaxSize())
                1 -> SatelliteNdviSubView()
                2 -> FertilizerPlannerView(viewModel = krishiViewModel, modifier = Modifier.fillMaxSize())
                3 -> IrrigationSchedulerView(modifier = Modifier.fillMaxSize())
                4 -> SoilQualityHeatmapView(viewModel = krishiViewModel, modifier = Modifier.fillMaxSize())
                5 -> SoilHealthView(viewModel = scannerViewModel, modifier = Modifier.fillMaxSize())
                6 -> OfflineRepositoryView(viewModel = krishiViewModel, modifier = Modifier.fillMaxSize())
                7 -> PestRadarView(viewModel = scannerViewModel, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun SatelliteNdviSubView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Satellite Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D))
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.SatelliteAlt, contentDescription = null, tint = Color(0xFF22C55E))
                        Text("Sentinel-2 Live Crop Canopy NDVI", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                    Text("31002 Sector A", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                }

                // Simulated Satellite Imagery Graphic Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(48.dp))
                            Text("Green Valley Rice Plot Boundary Active", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("GPS Polygon: 28.6139° N, 77.2090° E • 12.5 Acres", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Chlorophyll Index: 0.82 (High Density)", fontSize = 12.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                    Text("Water Stress: 0.12 (Normal)", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // Irrigation Planner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Water, contentDescription = null, tint = Color(0xFF0EA5E9))
                    Text("AI Precision Irrigation Schedule", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Text("Next Irrigation: Tomorrow 06:00 AM (Recommended volume: 2,500 L/acre)", fontSize = 12.sp, color = Color(0xFF374151))
            }
        }
    }
}
