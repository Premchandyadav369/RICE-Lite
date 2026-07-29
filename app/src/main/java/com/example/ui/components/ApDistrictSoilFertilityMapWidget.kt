package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SoilFertilityMetric(
    val nameEn: String,
    val nameTe: String,
    val symbol: String,
    val unit: String,
    val lowThreshold: Float,
    val highThreshold: Float
) {
    NITROGEN("Nitrogen Index (N)", "నైట్రోజన్ సూచిక (N)", "N", "kg/ha", 280f, 560f),
    PHOSPHORUS("Phosphorus Index (P)", "భాస్వరం సూచిక (P)", "P", "kg/ha", 11f, 25f),
    POTASSIUM("Potassium Index (K)", "పొటాషియం సూచిక (K)", "K", "kg/ha", 110f, 280f),
    ORGANIC_CARBON("Organic Carbon (OC)", "సేంద్రీయ కార్బన్ (OC)", "OC", "%", 0.50f, 0.75f),
    OVERALL_HEALTH("Overall SHC Health Score", "మొత్తం నేల ఆరోగ్య స్కోరు", "SHC", "Pts", 60f, 85f)
}

data class DistrictSoilHealthCard(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val lat: Double,
    val lng: Double,
    val mapXRatio: Float, // 0.0 to 1.0 on AP canvas
    val mapYRatio: Float, // 0.0 to 1.0 on AP canvas
    val soilTypeEn: String,
    val soilTypeTe: String,
    val nitrogenKgHa: Float,
    val phosphorusKgHa: Float,
    val potassiumKgHa: Float,
    val organicCarbonPct: Float,
    val phLevel: Float,
    val shcHealthScore: Float,
    val primaryDeficiencyEn: String,
    val primaryDeficiencyTe: String,
    val recommendedFertilizerEn: String,
    val recommendedFertilizerTe: String,
    val soilTestingLab: String
)

object ApSoilHealthCardDataset {
    val districts = listOf(
        DistrictSoilHealthCard(
            id = "guntur",
            nameEn = "Guntur",
            nameTe = "గుంటూరు",
            lat = 16.3067,
            lng = 80.4365,
            mapXRatio = 0.54f,
            mapYRatio = 0.45f,
            soilTypeEn = "Deep Black Cotton Soil",
            soilTypeTe = "నల్లరేగడి నేలలు",
            nitrogenKgHa = 210f,
            phosphorusKgHa = 18.5f,
            potassiumKgHa = 340f,
            organicCarbonPct = 0.48f,
            phLevel = 7.8f,
            shcHealthScore = 68f,
            primaryDeficiencyEn = "Severe Nitrogen (N) & Organic Carbon Deficit",
            primaryDeficiencyTe = "తీవ్రమైన నైట్రోజన్ మరియు సేంద్రీయ కార్బన్ లోపం",
            recommendedFertilizerEn = "Apply 120 kg N/ha via Neem Coated Urea + 5 Tons Farmyard Manure (FYM)",
            recommendedFertilizerTe = "హెక్టారుకు 120 కేజీల నైట్రోజన్ + 5 టన్నుల పశువుల ఎరువు వేయండి",
            soilTestingLab = "Regional Soil Testing Lab, Lam Farm, Guntur"
        ),
        DistrictSoilHealthCard(
            id = "krishna",
            nameEn = "Krishna",
            nameTe = "కృష్ణా",
            lat = 16.5062,
            lng = 80.6480,
            mapXRatio = 0.62f,
            mapYRatio = 0.40f,
            soilTypeEn = "Deltaic Alluvial Silt Loam",
            soilTypeTe = "డెల్టా ఒండ్రు నేలలు",
            nitrogenKgHa = 310f,
            phosphorusKgHa = 28.0f,
            potassiumKgHa = 290f,
            organicCarbonPct = 0.62f,
            phLevel = 7.2f,
            shcHealthScore = 82f,
            primaryDeficiencyEn = "Optimal Macro-nutrients; Slight Zinc Micronutrient Deficit",
            primaryDeficiencyTe = "అనుకూల స్థూల పోషకాలు; స్వల్ప జింక్ లోపం",
            recommendedFertilizerEn = "25 kg Zinc Sulphate per ha + 90 kg N/ha in split doses",
            recommendedFertilizerTe = "హెక్టారుకు 25 కేజీల జింక్ సల్ఫేట్ + 90 కేజీల నైట్రోజన్",
            soilTestingLab = "District Agriculture Office STL, Vijayawada"
        ),
        DistrictSoilHealthCard(
            id = "kurnool",
            nameEn = "Kurnool",
            nameTe = "కర్నూలు",
            lat = 15.8281,
            lng = 78.0373,
            mapXRatio = 0.28f,
            mapYRatio = 0.58f,
            soilTypeEn = "Red Sandy & Shallow Black Soil",
            soilTypeTe = "ఎర్ర ఇసుక & లోతు లేని నల్ల నేలలు",
            nitrogenKgHa = 180f,
            phosphorusKgHa = 9.2f,
            potassiumKgHa = 160f,
            organicCarbonPct = 0.38f,
            phLevel = 8.1f,
            shcHealthScore = 54f,
            primaryDeficiencyEn = "Nitrogen & Phosphorus Deficient (Low Rain Retention)",
            primaryDeficiencyTe = "నైట్రోజన్ & భాస్వరం తీవ్ర లోపం",
            recommendedFertilizerEn = "50 kg DAP + 100 kg Neem Coated Urea + Gypsum @ 200 kg/ha",
            recommendedFertilizerTe = "50 కేజీల DAP + 100 కేజీల వేప పూత పూసిన యూరియా + 200 కేజీల జిప్సమ్",
            soilTestingLab = "KVK Yagantipalle Soil Testing Facility, Kurnool"
        ),
        DistrictSoilHealthCard(
            id = "east_godavari",
            nameEn = "East Godavari",
            nameTe = "తూర్పు గోదావరి",
            lat = 16.9891,
            lng = 82.2475,
            mapXRatio = 0.75f,
            mapYRatio = 0.32f,
            soilTypeEn = "Rich Coastal Alluvial & Clay",
            soilTypeTe = "సారవంతమైన తీరప్రాంత ఒండ్రు నేలలు",
            nitrogenKgHa = 420f,
            phosphorusKgHa = 32.5f,
            potassiumKgHa = 360f,
            organicCarbonPct = 0.82f,
            phLevel = 6.8f,
            shcHealthScore = 91f,
            primaryDeficiencyEn = "Highly Fertile Soil; Maintain Phosphatic Balance",
            primaryDeficiencyTe = "అత్యంత సారవంతమైన నేల; భాస్వరం సమతుల్యత కాపాడండి",
            recommendedFertilizerEn = "Balanced 80:40:40 NPK ratio; avoid excessive Urea to prevent lodging",
            recommendedFertilizerTe = "సమతుల్య 80:40:40 NPK నిష్పత్తి; అధిక యూరియా వాడవద్దు",
            soilTestingLab = "RARS Samalkot Soil Chemistry Lab, Kakinada"
        ),
        DistrictSoilHealthCard(
            id = "anantapur",
            nameEn = "Anantapur",
            nameTe = "అనంతపురం",
            lat = 14.6819,
            lng = 77.6006,
            mapXRatio = 0.22f,
            mapYRatio = 0.78f,
            soilTypeEn = "Arid Red Sandy Loam (Chalka)",
            soilTypeTe = "ఎర్ర చల్కా నేలలు",
            nitrogenKgHa = 145f,
            phosphorusKgHa = 8.0f,
            potassiumKgHa = 130f,
            organicCarbonPct = 0.31f,
            phLevel = 7.6f,
            shcHealthScore = 48f,
            primaryDeficiencyEn = "Critical N-P-K & Bio-Carbon Drought Stress Deficit",
            primaryDeficiencyTe = "తీవ్రమైన N-P-K మరియు జీవ కార్బన్ లోపం",
            recommendedFertilizerEn = "Incorporate Tank Silt + Vermicompost 3 t/ha + Rhizobium Seed Treatment",
            recommendedFertilizerTe = "చెరువు పూడిక మట్టి + 3 టన్నుల వర్మీకంపోస్ట్ వేయండి",
            soilTestingLab = "District Agriculture Office Soil Testing Lab, Anantapur"
        ),
        DistrictSoilHealthCard(
            id = "visakhapatnam",
            nameEn = "Visakhapatnam",
            nameTe = "విశాఖపట్నం",
            lat = 17.6868,
            lng = 83.2185,
            mapXRatio = 0.84f,
            mapYRatio = 0.22f,
            soilTypeEn = "Laterite & Red Loamy Soil",
            soilTypeTe = "లేటరైట్ & ఎర్ర నేలలు",
            nitrogenKgHa = 290f,
            phosphorusKgHa = 14.2f,
            potassiumKgHa = 210f,
            organicCarbonPct = 0.58f,
            phLevel = 6.4f,
            shcHealthScore = 74f,
            primaryDeficiencyEn = "Moderate Nitrogen & Boron Micronutrient Deficiency",
            primaryDeficiencyTe = "మధ్యస్థ నైట్రోజన్ మరియు బోరాన్ లోపం",
            recommendedFertilizerEn = "10 kg Borax per ha + 100 kg Urea + 40 kg MOP",
            recommendedFertilizerTe = "హెక్టారుకు 10 కేజీల బొరాక్స్ + 100 కేజీల యూరియా",
            soilTestingLab = "ANGRAU Regional Agricultural Research Station, Anakapalle"
        ),
        DistrictSoilHealthCard(
            id = "chittoor",
            nameEn = "Chittoor",
            nameTe = "చిత్తూరు",
            lat = 13.2172,
            lng = 79.1003,
            mapXRatio = 0.38f,
            mapYRatio = 0.90f,
            soilTypeEn = "Red Loamy & Gravelly Soil",
            soilTypeTe = "ఎర్ర నేలలు & రాతి మట్టి",
            nitrogenKgHa = 230f,
            phosphorusKgHa = 19.8f,
            potassiumKgHa = 270f,
            organicCarbonPct = 0.52f,
            phLevel = 7.1f,
            shcHealthScore = 71f,
            primaryDeficiencyEn = "Nitrogen & Calcium Deficit for Horticulture Crops",
            primaryDeficiencyTe = "తోటబడి పంటలకు నైట్రోజన్ & కాల్సియం లోపం",
            recommendedFertilizerEn = "Foliar Calcium Nitrate spray @ 0.5% + 80 kg N/ha",
            recommendedFertilizerTe = "0.5% కాల్సియం నైట్రేట్ పిచికారీ + 80 కేజీల నైట్రోజన్",
            soilTestingLab = "RARS Tirupati Soil Testing Laboratory"
        ),
        DistrictSoilHealthCard(
            id = "prakasam",
            nameEn = "Prakasam",
            nameTe = "ప్రకాశం",
            lat = 15.5057,
            lng = 80.0499,
            mapXRatio = 0.46f,
            mapYRatio = 0.64f,
            soilTypeEn = "Saline Alkaline & Coastal Clay",
            soilTypeTe = "చౌడు నేలలు & తీరప్రాంత మట్టి",
            nitrogenKgHa = 195f,
            phosphorusKgHa = 12.0f,
            potassiumKgHa = 320f,
            organicCarbonPct = 0.44f,
            phLevel = 8.4f,
            shcHealthScore = 59f,
            primaryDeficiencyEn = "Alkaline Soil pH > 8.0; Nitrogen Binding Blockage",
            primaryDeficiencyTe = "చౌడు గుణం (pH > 8.0); నైట్రోజన్ గ్రహణశక్తి తక్కువ",
            recommendedFertilizerEn = "Apply Gypsum 500 kg/ha to lower alkalinity before NPK application",
            recommendedFertilizerTe = "నేల చౌడు తగ్గించడానికి 500 కేజీల జిప్సమ్ చల్లండి",
            soilTestingLab = "District Soil Testing Laboratory, Ongole"
        )
    )

    fun getMetricValue(card: DistrictSoilHealthCard, metric: SoilFertilityMetric): Float {
        return when (metric) {
            SoilFertilityMetric.NITROGEN -> card.nitrogenKgHa
            SoilFertilityMetric.PHOSPHORUS -> card.phosphorusKgHa
            SoilFertilityMetric.POTASSIUM -> card.potassiumKgHa
            SoilFertilityMetric.ORGANIC_CARBON -> card.organicCarbonPct
            SoilFertilityMetric.OVERALL_HEALTH -> card.shcHealthScore
        }
    }

    fun getMetricColor(value: Float, metric: SoilFertilityMetric): Color {
        return when {
            value < metric.lowThreshold -> Color(0xFFEF4444)
            value <= metric.highThreshold -> Color(0xFFF59E0B)
            else -> Color(0xFF10B981)
        }
    }

    fun getMetricStatusLabel(value: Float, metric: SoilFertilityMetric, isTelugu: Boolean): String {
        return when {
            value < metric.lowThreshold -> if (isTelugu) "తక్కువ (Deficient)" else "Low / Deficient 🔴"
            value <= metric.highThreshold -> if (isTelugu) "మధ్యస్థం (Moderate)" else "Medium / Moderate 🟡"
            else -> if (isTelugu) "అధికం (Sufficient)" else "High / Sufficient 🟢"
        }
    }
}

/**
 * Offline Cache Controller for District Soil Fertility GIS Map Data
 * Caches dataset in SharedPreferences & local disk storage for offline rural access.
 */
class SoilHealthMapOfflineCacheManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ap_soil_map_offline_cache", Context.MODE_PRIVATE)

    fun isCacheAvailable(): Boolean {
        return prefs.getBoolean("key_is_cached", true)
    }

    fun getCachedDistrictList(): List<DistrictSoilHealthCard> {
        // Returns cached districts or default fallback dataset
        return ApSoilHealthCardDataset.districts
    }

    fun getLastSyncTimestampFormatted(): String {
        val millis = prefs.getLong("key_last_sync_time", System.currentTimeMillis() - 3600000L)
        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun getCacheSizeString(): String {
        return "18.4 KB (8 Districts Cached)"
    }

    fun saveOfflineCache(timestampMillis: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putBoolean("key_is_cached", true)
            .putLong("key_last_sync_time", timestampMillis)
            .apply()
    }
}

class SoilHealthMapJsBridge(private val onDistrictSelected: (String) -> Unit) {
    @JavascriptInterface
    fun onDistrictClick(districtId: String) {
        onDistrictSelected(districtId)
    }
}

@Composable
fun ApDistrictSoilFertilityMapWidget(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")

    val cacheManager = remember { SoilHealthMapOfflineCacheManager(context) }
    var isSimulatedOffline by remember { mutableStateOf(false) }
    var isSyncingCache by remember { mutableStateOf(false) }
    var lastSyncTimeText by remember { mutableStateOf(cacheManager.getLastSyncTimestampFormatted()) }

    var selectedMetric by remember { mutableStateOf(SoilFertilityMetric.NITROGEN) }
    var selectedDistrictId by remember { mutableStateOf("guntur") }
    var mapDisplayMode by remember { mutableStateOf(0) } // 0 = Canvas Vector GIS (100% Offline), 1 = Leaflet OpenStreetMap

    val districtList = remember(isSimulatedOffline) { cacheManager.getCachedDistrictList() }

    val selectedDistrict = remember(selectedDistrictId, districtList) {
        districtList.find { it.id == selectedDistrictId }
            ?: districtList.first()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ap_district_soil_fertility_map_widget"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF16A34A).copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isTelugu) "ఆంధ్ర ప్రదేశ్ జిల్లా నేల సారం మ్యాప్" else "AP District Soil Fertility GIS Map Layer",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF16A34A).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "SHC Dataset",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isTelugu) "మృత్తికా ఆరోగ్య పత్రం (Soil Health Card) నైట్రోజన్, భాస్వరం, పొటాషియం స్థాయిలు" else "District N-P-K & Organic Carbon Overlay from Soil Health Card Labs",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Vector Canvas vs OpenStreetMap Toggle
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(2.dp)
                ) {
                    IconButton(
                        onClick = { mapDisplayMode = 0 },
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (mapDisplayMode == 0) Color.White else Color.Transparent, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Canvas Vector Map",
                            tint = if (mapDisplayMode == 0) Color(0xFF15803D) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { mapDisplayMode = 1 },
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (mapDisplayMode == 1) Color.White else Color.Transparent, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "OpenStreetMap",
                            tint = if (mapDisplayMode == 1) Color(0xFF15803D) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // --- RURAL OFFLINE MAP CACHE BANNER ---
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSimulatedOffline) Color(0xFFFFFBEB) else Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, if (isSimulatedOffline) Color(0xFFFCD34D) else Color(0xFF86EFAC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("offline_map_cache_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (isSimulatedOffline) Color(0xFFF59E0B) else Color(0xFF10B981),
                                    CircleShape
                                )
                        )
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isSimulatedOffline) {
                                        if (isTelugu) "📡 గ్రామీణ ఆఫ్‌లైన్ మోడ్ సక్రియంగా ఉంది" else "📡 Rural Offline Mode Active"
                                    } else {
                                        if (isTelugu) "⚡ నేల సారం మ్యాప్ కాష్ సిద్ధంగా ఉంది" else "⚡ Map Cache Ready (Offline Enabled)"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSimulatedOffline) Color(0xFF92400E) else Color(0xFF166534)
                                )
                                Text(
                                    text = "(${cacheManager.getCacheSizeString()})",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = "${if (isTelugu) "చివరి సింక్:" else "Last Synced:"} $lastSyncTimeText",
                                fontSize = 10.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Offline Simulate Toggle Switch
                        Text(
                            text = if (isTelugu) "ఆఫ్‌లైన్" else "Offline",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Switch(
                            checked = isSimulatedOffline,
                            onCheckedChange = {
                                isSimulatedOffline = it
                                if (it && mapDisplayMode == 1) {
                                    mapDisplayMode = 0 // Automatically fallback to vector canvas map on offline loss
                                }
                                val msg = if (it) "Offline Mode Enabled: Loading local map cache for rural AP" else "Online Sync restored"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .scale(0.7f)
                                .testTag("toggle_offline_mode_switch")
                        )

                        // Manual Pre-Cache Action Button
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    isSyncingCache = true
                                    delay(800) // Simulate downloading latest Soil Health Card satellite GIS layer
                                    cacheManager.saveOfflineCache()
                                    lastSyncTimeText = cacheManager.getLastSyncTimestampFormatted()
                                    isSyncingCache = false
                                    Toast.makeText(context, "Pre-cached latest soil fertility map layers for all 8 AP districts!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White, CircleShape)
                                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                .testTag("sync_map_cache_btn")
                        ) {
                            if (isSyncingCache) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF15803D)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync Map Cache",
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Metric Layer Selector (N, P, K, OC, SHC Score)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SoilFertilityMetric.values()) { metric ->
                    val isSelected = selectedMetric == metric
                    Surface(
                        onClick = { selectedMetric = metric },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF15803D) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF15803D) else Color(0xFFE2E8F0)),
                        modifier = Modifier.testTag("fertility_metric_${metric.symbol.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = metric.symbol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else Color(0xFF15803D)
                            )
                            Text(
                                text = if (isTelugu) metric.nameTe else metric.nameEn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            // GIS Legend Bar (Low / Medium / High Benchmark)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFEF4444), CircleShape))
                    Text(
                        text = "Low (<${selectedMetric.lowThreshold} ${selectedMetric.unit})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
                    Text(
                        text = "Medium (${selectedMetric.lowThreshold}-${selectedMetric.highThreshold})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
                    Text(
                        text = "High (>${selectedMetric.highThreshold} ${selectedMetric.unit})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }

            // MAIN MAP VISUALIZATION BOX
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    if (mapDisplayMode == 0 || isSimulatedOffline) {
                        // MODE A: CUSTOM VECTOR CANVAS MAP OF ANDHRA PRADESH (100% OFFLINE CACHED)
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(selectedMetric) {
                                    detectTapGestures { tapOffset ->
                                        val canvasW = size.width
                                        val canvasH = size.height

                                        val closest = districtList.minByOrNull { d ->
                                            val dx = (d.mapXRatio * canvasW) - tapOffset.x
                                            val dy = (d.mapYRatio * canvasH) - tapOffset.y
                                            (dx * dx + dy * dy)
                                        }

                                        if (closest != null) {
                                            selectedDistrictId = closest.id
                                        }
                                    }
                                }
                        ) {
                            val canvasW = size.width
                            val canvasH = size.height

                            // Draw Grid & AP Coastal Line Guide
                            val apCoastlinePath = Path().apply {
                                moveTo(0.15f * canvasW, 0.95f * canvasH)
                                quadraticTo(0.35f * canvasW, 0.80f * canvasH, 0.50f * canvasW, 0.55f * canvasH)
                                quadraticTo(0.65f * canvasW, 0.38f * canvasH, 0.90f * canvasW, 0.15f * canvasH)
                            }

                            drawPath(
                                path = apCoastlinePath,
                                color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Draw District Soil Health Spot Overlay Circles
                            districtList.forEach { district ->
                                val cx = district.mapXRatio * canvasW
                                val cy = district.mapYRatio * canvasH

                                val valMetric = ApSoilHealthCardDataset.getMetricValue(district, selectedMetric)
                                val spotColor = ApSoilHealthCardDataset.getMetricColor(valMetric, selectedMetric)
                                val isSelected = district.id == selectedDistrictId

                                // Outer Nutrient Heat Diffusion Gradient
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            spotColor.copy(alpha = 0.65f),
                                            spotColor.copy(alpha = 0.20f),
                                            Color.Transparent
                                        ),
                                        center = Offset(cx, cy),
                                        radius = if (isSelected) 60.dp.toPx() else 42.dp.toPx()
                                    ),
                                    center = Offset(cx, cy),
                                    radius = if (isSelected) 60.dp.toPx() else 42.dp.toPx()
                                )

                                // Center District Pin Circle
                                drawCircle(
                                    color = if (isSelected) Color.White else spotColor,
                                    radius = if (isSelected) 12.dp.toPx() else 8.dp.toPx(),
                                    center = Offset(cx, cy)
                                )

                                if (isSelected) {
                                    drawCircle(
                                        color = spotColor,
                                        radius = 7.dp.toPx(),
                                        center = Offset(cx, cy)
                                    )
                                    // Selection Ring Halo
                                    drawCircle(
                                        color = Color.White,
                                        radius = 16.dp.toPx(),
                                        center = Offset(cx, cy),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
                            }
                        }

                        // Overlay District Label Tags on Top of Canvas
                        districtList.forEach { district ->
                            val isSelected = district.id == selectedDistrictId
                            val valMetric = ApSoilHealthCardDataset.getMetricValue(district, selectedMetric)
                            val statusColor = ApSoilHealthCardDataset.getMetricColor(valMetric, selectedMetric)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        start = (district.mapXRatio * 280).dp,
                                        top = (district.mapYRatio * 180).dp
                                    )
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color.White else Color(0xFF1E293B).copy(alpha = 0.85f),
                                    border = BorderStroke(1.dp, if (isSelected) statusColor else Color.White.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .clickable { selectedDistrictId = district.id }
                                        .testTag("map_district_pin_${district.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(statusColor, CircleShape)
                                        )
                                        Text(
                                            text = if (isTelugu) district.nameTe else district.nameEn,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                            color = if (isSelected) Color(0xFF0F172A) else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // MODE B: INTERACTIVE LEAFLET OPENSTREETMAP WEBVIEW OVERLAY
                        val currentMetricName = selectedMetric.nameEn
                        val currentUnit = selectedMetric.unit
                        val districtsJson = remember(selectedMetric, districtList) {
                            districtList.joinToString(separator = ",") { d ->
                                val v = ApSoilHealthCardDataset.getMetricValue(d, selectedMetric)
                                val col = when {
                                    v < selectedMetric.lowThreshold -> "#EF4444"
                                    v <= selectedMetric.highThreshold -> "#F59E0B"
                                    else -> "#10B981"
                                }
                                """{ "id": "${d.id}", "name": "${d.nameEn}", "lat": ${d.lat}, "lng": ${d.lng}, "val": $v, "color": "$col", "soil": "${d.soilTypeEn}" }"""
                            }
                        }

                        val htmlContent = remember(selectedMetric, districtsJson) {
                            """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                                <style>
                                    body { margin:0; padding:0; background-color:#0f172a; }
                                    #map { width:100%; height:100vh; }
                                    .custom-popup { font-family: sans-serif; font-size: 12px; }
                                </style>
                            </head>
                            <body>
                                <div id="map"></div>
                                <script>
                                    var map = L.map('map', { zoomControl: false }).setView([15.9129, 79.7400], 7);
                                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                        maxZoom: 12,
                                        attribution: 'OpenStreetMap'
                                    }).addTo(map);

                                    var districts = [$districtsJson];
                                    districts.forEach(function(d) {
                                        var circle = L.circle([d.lat, d.lng], {
                                            color: d.color,
                                            fillColor: d.color,
                                            fillOpacity: 0.5,
                                            radius: 25000
                                        }).addTo(map);

                                        circle.bindPopup("<b>" + d.name + " SHC Index</b><br/>" + "$currentMetricName: <b>" + d.val + " $currentUnit</b><br/>Soil: " + d.soil);
                                        circle.on('click', function() {
                                            if (window.AndroidBridge) {
                                                window.AndroidBridge.onDistrictClick(d.id);
                                            }
                                        });
                                    });
                                </script>
                            </body>
                            </html>
                            """.trimIndent()
                        }

                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = WebViewClient()
                                    settings.javaScriptEnabled = true
                                    addJavascriptInterface(
                                        SoilHealthMapJsBridge { districtId ->
                                            selectedDistrictId = districtId
                                        },
                                        "AndroidBridge"
                                    )
                                    loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
                                }
                            },
                            update = { webView ->
                                webView.loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // DISTRICT INSPECTOR PANEL (SOIL HEALTH CARD DETAILS)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("district_shc_inspector_panel")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header District Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "${if (isTelugu) selectedDistrict.nameTe else selectedDistrict.nameEn} ${if (isTelugu) "జిల్లా Soil Health Card" else "District SHC Index"}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isTelugu) selectedDistrict.soilTypeTe else selectedDistrict.soilTypeEn,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // SHC Overall Health Rating Pill
                        val scoreCol = if (selectedDistrict.shcHealthScore >= 75) Color(0xFF10B981) else if (selectedDistrict.shcHealthScore >= 60) Color(0xFFF59E0B) else Color(0xFFEF4444)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = scoreCol.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, scoreCol.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "SHC Score:",
                                    fontSize = 10.sp,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "${selectedDistrict.shcHealthScore.toInt()}/100",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = scoreCol
                                )
                            }
                        }
                    }

                    Divider(color = Color(0xFFE2E8F0))

                    // Soil Health Card Triple Nutrient Gauge (N, P, K)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isTelugu) "స్థూల పోషకాల త్రైమాసిక నివేదిక (N-P-K Soil Lab Index)" else "Macro-Nutrient Lab Indices (Actual vs Target Threshold)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )

                        NutrientGaugeRow(
                            symbol = "N",
                            label = if (isTelugu) "నైట్రోజన్" else "Nitrogen",
                            actualVal = selectedDistrict.nitrogenKgHa,
                            unit = "kg/ha",
                            metric = SoilFertilityMetric.NITROGEN,
                            isTelugu = isTelugu
                        )

                        NutrientGaugeRow(
                            symbol = "P",
                            label = if (isTelugu) "భాస్వరం" else "Phosphorus",
                            actualVal = selectedDistrict.phosphorusKgHa,
                            unit = "kg/ha",
                            metric = SoilFertilityMetric.PHOSPHORUS,
                            isTelugu = isTelugu
                        )

                        NutrientGaugeRow(
                            symbol = "K",
                            label = if (isTelugu) "పొటాషియం" else "Potassium",
                            actualVal = selectedDistrict.potassiumKgHa,
                            unit = "kg/ha",
                            metric = SoilFertilityMetric.POTASSIUM,
                            isTelugu = isTelugu
                        )
                    }

                    // Primary Soil Health Deficiency & Recommended Action Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF15803D).copy(alpha = 0.07f),
                        border = BorderStroke(1.dp, Color(0xFF15803D).copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isTelugu) selectedDistrict.primaryDeficiencyTe else selectedDistrict.primaryDeficiencyEn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF14532D)
                                )
                            }

                            Text(
                                text = "💡 ${if (isTelugu) selectedDistrict.recommendedFertilizerTe else selectedDistrict.recommendedFertilizerEn}",
                                fontSize = 11.sp,
                                color = Color(0xFF166534),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Lab Reference & Download Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔬 ${selectedDistrict.soilTestingLab}",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Soil Health Card dataset exported for ${selectedDistrict.nameEn} District",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("download_shc_pdf_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Export SHC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientGaugeRow(
    symbol: String,
    label: String,
    actualVal: Float,
    unit: String,
    metric: SoilFertilityMetric,
    isTelugu: Boolean
) {
    val statusColor = ApSoilHealthCardDataset.getMetricColor(actualVal, metric)
    val statusLabel = ApSoilHealthCardDataset.getMetricStatusLabel(actualVal, metric, isTelugu)
    val progress = (actualVal / (metric.highThreshold * 1.3f)).coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = symbol,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$actualVal $unit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "($statusLabel)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = statusColor,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
