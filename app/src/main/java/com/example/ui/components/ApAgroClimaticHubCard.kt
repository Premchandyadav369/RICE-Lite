package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KrishiViewModel

data class ApDistrictInfo(
    val name: String,
    val zone: String, // "Coastal Delta", "North Coastal", "Rayalaseema Arid", "South Horticulture"
    val hq: String,
    val primarySoils: String,
    val rainfallMm: Int,
    val majorCrops: List<String>,
    val mainIrrigation: String, // "Canal (44%)", "Tube-well", "Tanks"
    val mandiHub: String,
    val keyHighlight: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApAgroClimaticHubCard(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    var expandedTab by remember { mutableStateOf(0) } // 0: Agro-Climatic Zones, 1: Soil & Irrigation, 2: 26 District Explorer, 3: Mandi & Supply Chain
    var selectedZoneFilter by remember { mutableStateOf("All") }
    var selectedDistrictDetail by remember { mutableStateOf<ApDistrictInfo?>(null) }

    val districtsList = remember {
        listOf(
            ApDistrictInfo("Guntur", "Coastal Delta", "Guntur", "Alluvial & Black Cotton", 925, listOf("Chilli", "Cotton", "Paddy", "Tobacco"), "Canal & Well", "Guntur Mirchi Yard", "Asia's largest Chilli Market Yard"),
            ApDistrictInfo("East Godavari", "Coastal Delta", "Kakinada", "Deltaic Alluvial & Red", 1180, listOf("Paddy", "Sugarcane", "Coconut", "Prawns"), "Godavari Delta Canals", "Kakinada & Rajahmundry", "30% AP Rice & Seafood Exports"),
            ApDistrictInfo("West Godavari", "Coastal Delta", "Bhimavaram", "Rich Alluvial & Black", 1150, listOf("Paddy", "Aqua Shrimp/Carp", "Banana", "Oil Palm"), "Canals & Aquaculture Ponds", "Bhimavaram & Eluru", "Aquaculture Hub of India"),
            ApDistrictInfo("Krishna", "Coastal Delta", "Machilipatnam", "Delta Alluvial", 1020, listOf("Paddy", "Pulses", "Mango", "Sugarcane"), "Krishna Delta Canals", "Vijayawada APMC", "5.5 Lakh Soil Health Cards issued"),
            ApDistrictInfo("Anantapur", "Rayalaseema Arid", "Ananthapuramu", "Red Sandy (~80%) & Skeletal", 520, listOf("Groundnut", "Sweet Lime", "Pomegranate", "Millets"), "Tube-wells (85%) & Micro-Drip", "Anantapur Market Yard", "Arid Zone APMIP Drip Irrigation Pioneer"),
            ApDistrictInfo("Kurnool / Nandyal", "Rayalaseema Arid", "Kurnool", "Black Cotton & Red Soil", 670, listOf("Bengal Gram (Cicer)", "Cotton", "Paddy", "Onion"), "Canal & Borewells", "Nandyal & Kurnool Mandi", "Seed Capital of Andhra Pradesh"),
            ApDistrictInfo("Kadapa (YSR)", "Rayalaseema Arid", "Kadapa", "Red Loamy & Black Clay", 710, listOf("Turmeric", "Banana", "Chilli", "Groundnut"), "Tank Irrigation & Tube-wells", "Kadapa & Proddatur", "Jammalamadugu Tank Systems & Spices"),
            ApDistrictInfo("Chittoor / Tirupati", "South Horticulture", "Chittoor", "Red Lateritic & Sandy Loam", 890, listOf("Mango (Totapuri)", "Tomato", "Sugarcane", "Dairy Milk"), "Tube-wells & Farm Ponds", "Chittoor & Madanapalle", "Largest Dairy & Tomato Hub in South India"),
            ApDistrictInfo("Nellore (SPSR)", "Coastal Delta", "Nellore", "Coastal Sand & Red Loam", 1080, listOf("Paddy", "Aquaculture", "Citrus (Lemon)", "Sesame"), "Pennar Canals & Tanks", "Nellore APMC", "High Yielding Kharif/Rabi Paddy"),
            ApDistrictInfo("Prakasam", "Coastal Delta", "Ongole", "Black Cotton & Red Soil", 850, listOf("Tobacco", "Cotton", "Chickpea", "Chilli"), "Canals & Groundwater", "Ongole Market Yard", "Ongole Cattle Breed & Commercial Tobacco"),
            ApDistrictInfo("Visakhapatnam", "North Coastal", "Visakhapatnam", "Red Lateritic & Coastal", 1100, listOf("Paddy", "Sugarcane", "Cashew", "Marine Fish"), "Reservoirs & Rainfed", "Anakapalle Jaggery Mandi", "Anakapalle 2nd Largest Jaggery Market"),
            ApDistrictInfo("Srikakulam", "North Coastal", "Srikakulam", "Red Sandy & Alluvial", 1160, listOf("Paddy", "Coconut", "Cashew", "Pulses"), "Vamsadhara River Canals", "Srikakulam APMC", "High Rainfall & Marine Coastal Fisheries")
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Title Header with State Emblem & AP Agro Statistics
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
                        color = Color(0xFF047857),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Andhra Pradesh Agrarian Hub", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDCFCE7)) {
                                Text("36% GSDP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("26 Districts • Agro-Climatic Zones & Soil Profile", fontSize = 12.sp, color = Color(0xFF6B7280))
                    }
                }

                IconButton(onClick = { expandedTab = (expandedTab + 1) % 4 }) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Switch View", tint = Color(0xFF047857))
                }
            }

            // Quick State Level Key Metric Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricMiniChip(title = "Annual Rain", value = "882 mm", subtitle = "SW+Post Monsoon", color = Color(0xFF0284C7), modifier = Modifier.weight(1f))
                MetricMiniChip(title = "Red/Laterite", value = "66%", subtitle = "Upland Soil", color = Color(0xFFB45309), modifier = Modifier.weight(1f))
                MetricMiniChip(title = "Irrigation", value = "2.85 Mha", subtitle = "44% Canals / 42% Wells", color = Color(0xFF047857), modifier = Modifier.weight(1f))
            }

            // Tab Selector
            ScrollableTabRow(
                selectedTabIndex = expandedTab,
                edgePadding = 0.dp,
                containerColor = Color(0xFFF8FAFC),
                contentColor = Color(0xFF047857),
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(selected = expandedTab == 0, onClick = { expandedTab = 0 }) {
                    Text("Agro-Climatic Zones", modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = expandedTab == 1, onClick = { expandedTab = 1 }) {
                    Text("Soils & Water", modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = expandedTab == 2, onClick = { expandedTab = 2 }) {
                    Text("26 District Explorer", modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = expandedTab == 3, onClick = { expandedTab = 3 }) {
                    Text("Supply Chain & Mandis", modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // TAB 0: Agro-Climatic Zones Summary
            if (expandedTab == 0) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🌾 Coastal Godavari & Krishna Deltas", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF15803D))
                            Text("Heavy rainfed alluvial delta belts growing double-crop rice, sugarcane, chilli, turmeric, pulses, and high-density inland prawn/carp aquaculture.", fontSize = 12.sp, color = Color(0xFF166534))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("☀️ Interior Rayalaseema Arid Zone", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFB45309))
                            Text("Arid & drought-prone uplands (<550mm rain, >44°C summer). Dominated by red sandy soils, rainfed groundnut, millets, pulses, and APMIP micro-drip tube-well horticulture.", fontSize = 12.sp, color = Color(0xFF78350F))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0F9FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🥭 Southern Horticulture & Sericulture Zone", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0369A1))
                            Text("Chittoor, Tirupati & Nellore fruit belt producing Totapuri mangoes, citrus lemon, tomatoes, dairy cattle (799g/day per capita milk), and sericulture.", fontSize = 12.sp, color = Color(0xFF075985))
                        }
                    }
                }
            }

            // TAB 1: Soil Composition & Irrigation Infrastructure
            if (expandedTab == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Soil Distribution in Andhra Pradesh:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF374151))

                    // Soil distribution progress bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Red & Lateritic Soil (66%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            Text("Black Cotton (25%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text("Alluvial (5%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                        }
                        LinearProgressIndicator(
                            progress = 0.66f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = Color(0xFFD97706),
                            trackColor = Color(0xFF334155)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE5E7EB))

                    Text("Irrigation Source Split (~2.85 Mha Total):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF374151))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IrrigationSourceCard("Canals (44%)", "1.259 Mha", "Godavari & Krishna Deltas", Color(0xFF0284C7), Modifier.weight(1f))
                        IrrigationSourceCard("Tube-Wells (42%)", "1.197 Mha", "Rayalaseema Borewells", Color(0xFF059669), Modifier.weight(1f))
                        IrrigationSourceCard("Tanks (10%)", "0.276 Mha", "Minor Tanks & Ponds", Color(0xFFD97706), Modifier.weight(1f))
                    }
                }
            }

            // TAB 2: Interactive 26 AP District Explorer
            if (expandedTab == 2) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Zone Filters
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val filters = listOf("All", "Coastal Delta", "Rayalaseema Arid", "North Coastal", "South Horticulture")
                        items(filters) { f ->
                            FilterChip(
                                selected = selectedZoneFilter == f,
                                onClick = { selectedZoneFilter = f },
                                label = { Text(f, fontSize = 11.sp) }
                            )
                        }
                    }

                    val filteredDistricts = if (selectedZoneFilter == "All") districtsList else districtsList.filter { it.zone == selectedZoneFilter }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredDistricts.forEach { dist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDistrictDetail = dist },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(dist.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE0E7FF)) {
                                                Text(dist.zone, fontSize = 10.sp, color = Color(0xFF3730A3), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                        Text("HQ: ${dist.hq} • Soils: ${dist.primarySoils}", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text("Crops: ${dist.majorCrops.joinToString(", ")}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${dist.rainfallMm} mm", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0284C7))
                                        Text("Avg Rain", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 3: Mandi Network & Agri Supply Chain Flow
            if (expandedTab == 3) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("AP Agri-Supply Chain Architecture:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))

                    // Visual Supply Chain Flow Steps
                    val supplySteps = listOf(
                        Triple("1. Farmer Field", "Kharif/Rabi harvest (Paddy, Chilli, Groundnut)", Icons.Default.Agriculture),
                        Triple("2. FPO / Collection", "Aggregated at Rythu Bharosa Kendra (RBK)", Icons.Default.Store),
                        Triple("3. APMC Mandi / e-NAM", "Auctioned at Guntur, Nandyal, Vijayawada yards", Icons.Default.Gavel),
                        Triple("4. Cold Storage / Mill", "901 kt cold capacity for Chillies, Bananas, Dairy", Icons.Default.AcUnit),
                        Triple("5. Retail / Export", "Seafood & Rice exported via Vizag/Kakinada ports", Icons.Default.LocalShipping)
                    )

                    supplySteps.forEach { (title, desc, icon) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Icon(icon, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(20.dp))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                Text(desc, fontSize = 11.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }
            }
        }
    }

    // Selected District Detail Modal Dialog
    selectedDistrictDetail?.let { dist ->
        AlertDialog(
            onDismissRequest = { selectedDistrictDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF047857))
                    Text("${dist.name} District Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Agro-Climatic Zone: ${dist.zone}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF047857))
                    Text("HQ / Admin: ${dist.hq}", fontSize = 12.sp)
                    Text("Primary Soil Profile: ${dist.primarySoils}", fontSize = 12.sp)
                    Text("Annual Average Rainfall: ${dist.rainfallMm} mm", fontSize = 12.sp)
                    Text("Main Irrigation Source: ${dist.mainIrrigation}", fontSize = 12.sp)
                    Text("Major Crops: ${dist.majorCrops.joinToString(", ")}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Key Mandi Market: ${dist.mandiHub}", fontSize = 12.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFEF3C7), modifier = Modifier.fillMaxWidth()) {
                        Text("🌟 Highlight: ${dist.keyHighlight}", fontSize = 11.sp, color = Color(0xFF92400E), modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDistrictDetail = null }) {
                    Text("Close", fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                }
            }
        )
    }
}

@Composable
fun MetricMiniChip(title: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, fontSize = 10.sp, color = Color(0xFF6B7280))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(subtitle, fontSize = 9.sp, color = Color(0xFF4B5563))
        }
    }
}

@Composable
fun IrrigationSourceCard(title: String, area: String, detail: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Text(area, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
            Text(detail, fontSize = 9.sp, color = Color(0xFF6B7280))
        }
    }
}
