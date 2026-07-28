package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.FieldBoundaryEntity
import com.example.ui.viewmodel.FieldBoundaryViewModel
import com.example.ui.viewmodel.GpsWaypoint
import com.example.ui.viewmodel.MapLayerType
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldBoundaryMapView(
    fieldViewModel: FieldBoundaryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val waypoints by fieldViewModel.pinnedWaypoints.collectAsState()
    val areaAcres by fieldViewModel.calculatedAreaAcres.collectAsState()
    val perimeterMeters by fieldViewModel.calculatedPerimeterMeters.collectAsState()
    val savedBoundaries by fieldViewModel.savedBoundaries.collectAsState()
    val fieldName by fieldViewModel.currentFieldName.collectAsState()
    val cropName by fieldViewModel.currentCropName.collectAsState()
    val soilType by fieldViewModel.currentSoilType.collectAsState()
    val mapLayer by fieldViewModel.mapLayer.collectAsState()
    val selectedBoundary by fieldViewModel.selectedBoundaryForView.collectAsState()

    var showSavedDrawer by remember { mutableStateOf(false) }

    val crops = listOf("Paddy (Rice)", "Wheat", "Cotton", "Sugarcane", "Maize", "Potato", "Tomato", "Chilli")
    val soils = listOf("Loamy / Alluvial Soil", "Heavy Clay Soil", "Sandy / Light Soil", "Black Cotton Soil")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Top Status & Layer Header Bar ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF16A34A).copy(alpha = 0.15f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.WifiOff, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                        }
                    }
                    Column {
                        Text("GPS Field Boundary Map", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF111827))
                        Text("🟢 Offline-Ready • Local Vector Rendering", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Medium)
                    }
                }

                // Map Layer Selector Chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(MapLayerType.SATELLITE_GRID, MapLayerType.VECTOR_TOPOGRAPHIC, MapLayerType.BOUNDARY_ONLY).forEach { layer ->
                        val isSelected = mapLayer == layer
                        val label = when (layer) {
                            MapLayerType.SATELLITE_GRID -> "Satellite"
                            MapLayerType.VECTOR_TOPOGRAPHIC -> "Topo"
                            MapLayerType.BOUNDARY_ONLY -> "Boundary"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { fieldViewModel.setMapLayer(layer) },
                            label = { Text(label, fontSize = 10.sp) },
                            modifier = Modifier.height(30.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF059669),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // --- Interactive OpenStreetMap Leaflet Map Canvas Container ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Real-Time OpenStreetMap Leaflet View
                LeafletOsmMapView(
                    waypoints = waypoints,
                    mapLayer = mapLayer,
                    centerLat = fieldViewModel.baseCenterLat,
                    centerLng = fieldViewModel.baseCenterLng,
                    onAddWaypoint = { lat, lng ->
                        fieldViewModel.addWaypoint(lat, lng)
                        Toast.makeText(context, "Added Corner Pin P${waypoints.size + 1} at ($lat, $lng)", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay Controls & Live Dimensions Badge
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Info Overlay
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.75f)
                        ) {
                            Text(
                                text = "🗺️ OpenStreetMap AP/TS: ${fieldViewModel.baseCenterLat}° N, ${fieldViewModel.baseCenterLng}° E",
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF16A34A)
                        ) {
                            Text(
                                text = "Tap map to add pin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Bottom Floating Dimension Metrics Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.8f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("FIELD AREA", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("$areaAcres Acres", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                            }
                            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFF475569))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PERIMETER", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("$perimeterMeters m", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }
                            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFF475569))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("CORNERS", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("${waypoints.size} Pins", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFACC15))
                            }
                        }
                    }
                }
            }
        }

        // --- Quick Action Toolbar for GPS Pinning ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    fieldViewModel.addWaypointAtCurrentGps()
                    Toast.makeText(context, "Added GPS Pin at current device position! ✓", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pin My Location", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    if (waypoints.isNotEmpty()) {
                        fieldViewModel.removeWaypoint(waypoints.size - 1)
                    }
                },
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Undo", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = { fieldViewModel.clearWaypoints() },
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear", fontSize = 11.sp)
            }
        }

        // --- Save Field Boundary & Metadata Section ---
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
                    Text("Field Boundary Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827))
                    
                    TextButton(onClick = { showSavedDrawer = !showSavedDrawer }) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Saved Fields (${savedBoundaries.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    }
                }

                // Field Name Input
                OutlinedTextField(
                    value = fieldName,
                    onValueChange = { fieldViewModel.setFieldName(it) },
                    label = { Text("Field / Plot Name", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Crop Choice Chips
                Text("Assigned Crop:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B5563))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(crops) { crop ->
                        FilterChip(
                            selected = cropName == crop,
                            onClick = { fieldViewModel.setCropName(crop) },
                            label = { Text(crop, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF15803D),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Save Boundary Button
                Button(
                    onClick = {
                        if (waypoints.size < 3) {
                            Toast.makeText(context, "Please pin at least 3 GPS corner waypoints to form a polygon!", Toast.LENGTH_LONG).show()
                        } else {
                            fieldViewModel.saveBoundaryToRoom()
                            Toast.makeText(context, "Field '$fieldName' ($areaAcres Acres) saved to Room Database! ✓", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Field Boundary to Room DB", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // --- Saved Field Boundaries Drawer / List ---
        AnimatedVisibility(visible = showSavedDrawer) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📁 Saved Offline Field Boundaries", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0369A1))

                    if (savedBoundaries.isEmpty()) {
                        Text("No saved field boundaries yet. Map a boundary above and save it.", fontSize = 12.sp, color = Color(0xFF6B7280))
                    } else {
                        savedBoundaries.forEach { boundary ->
                            SavedBoundaryRow(
                                boundary = boundary,
                                isSelected = selectedBoundary?.id == boundary.id,
                                onSelect = { fieldViewModel.selectBoundaryToInspect(boundary) },
                                onDelete = { fieldViewModel.deleteBoundary(boundary.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedBoundaryRow(
    boundary: FieldBoundaryEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFF0F9FF) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(boundary.fieldName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827))
                Text("${boundary.cropName} • ${boundary.areaAcres} Acres • ${boundary.perimeterMeters}m perimeter", fontSize = 11.sp, color = Color(0xFF4B5563))
                Text("Center GPS: ${boundary.centerLatitude}° N, ${boundary.centerLongitude}° E (${boundary.waypointsCount} Pins)", fontSize = 10.sp, color = Color(0xFF0284C7))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onSelect) {
                    Text("Load Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Boundary", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
