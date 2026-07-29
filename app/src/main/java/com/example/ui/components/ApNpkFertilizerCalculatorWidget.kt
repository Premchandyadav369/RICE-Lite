package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.roundToInt

enum class NutrientStatus(val labelEn: String, val labelTe: String, val factor: Float, val color: Color) {
    LOW("Low (Deficient +25%)", "తక్కువ (+25% అదనంగా)", 1.25f, Color(0xFFEF4444)),
    MEDIUM("Medium (Standard 100%)", "మధ్యస్థం (సాధారణ మోతాదు)", 1.00f, Color(0xFFF59E0B)),
    HIGH("High (Sufficient -25%)", "అధికం (-25% తగ్గించండి)", 0.75f, Color(0xFF10B981))
}

data class CropFertilizerGuideline(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val icon: String,
    val baseNPerAcreKg: Float, // Nitrogen in kg/acre
    val basePPerAcreKg: Float, // Phosphorus in kg/acre
    val baseKPerAcreKg: Float, // Potassium in kg/acre
    val fymTonsPerAcre: Float, // Recommended Organic FYM tons/acre
    val splitApplicationEn: List<String>,
    val splitApplicationTe: List<String>,
    val organicBioFertilizerEn: String,
    val organicBioFertilizerTe: String
)

object ApFertilizerGuidelineRepository {
    val crops = listOf(
        CropFertilizerGuideline(
            id = "rice_paddy",
            nameEn = "Rice / Paddy",
            nameTe = "వరి (Paddy)",
            icon = "🌾",
            baseNPerAcreKg = 48f, // 120 kg/ha -> ~48 kg/acre
            basePPerAcreKg = 24f, // 60 kg/ha -> ~24 kg/acre
            baseKPerAcreKg = 24f, // 60 kg/ha -> ~24 kg/acre
            fymTonsPerAcre = 4.0f,
            splitApplicationEn = listOf(
                "Basal Dose (Sowing): 50% P (DAP), 25% N (Urea), 50% K (MOP)",
                "Tillering Stage (21-25 Days): 50% N (Urea top dress)",
                "Panicle Initiation (45 Days): 25% N (Urea) + 50% K (MOP)"
            ),
            splitApplicationTe = listOf(
                "నాటు సమయంలో: 50% భాస్వరం (DAP), 25% నైట్రోజన్, 50% పొటాషియం",
                "పిలక దశలో (21-25 రోజులు): 50% మేలైన నైట్రోజన్ (యూరియా చల్లుట)",
                "చిరుపొట్ట దశలో (45 రోజులు): 25% నైట్రోజన్ + 50% పొటాషియం"
            ),
            organicBioFertilizerEn = "Apply Azospirillum & Phosphobacteria @ 2 kg/acre + 25 kg Zinc Sulphate once per year.",
            organicBioFertilizerTe = "ఎకరాకు 2 కేజీల అజోస్పిరిల్లమ్, ఫాస్ఫోబాక్టీరియా మరియు 25 కేజీల జింక్ సల్ఫేట్ వేయండి."
        ),
        CropFertilizerGuideline(
            id = "chilli",
            nameEn = "Chilli (Mirchi)",
            nameTe = "మిర్చి (Chilli)",
            icon = "🌶️",
            baseNPerAcreKg = 120f,
            basePPerAcreKg = 60f,
            baseKPerAcreKg = 80f,
            fymTonsPerAcre = 8.0f,
            splitApplicationEn = listOf(
                "Basal Dose: 100% P (Single Super Phosphate/DAP) + 20% N + 20% K",
                "30 Days After Transplanting: 30% N + 30% K",
                "60 Days (Peak Flowering): 30% N + 30% K",
                "90 Days (Fruiting Pickings): 20% N + 20% K"
            ),
            splitApplicationTe = listOf(
                "నాటు ముందే: 100% భాస్వరం (SSP/DAP) + 20% నైట్రోజన్ + 20% పొటాషియం",
                "30 రోజుల నాటు తర్వాత: 30% నైట్రోజన్ + 30% పొటాషియం",
                "60 రోజుల వద్ద (పూత దశ): 30% నైట్రోజన్ + 30% పొటాషియం",
                "90 రోజుల వద్ద (కోత కాలం): 20% నైట్రోజన్ + 20% పొటాషియం"
            ),
            organicBioFertilizerEn = "Incorporate 500 kg Neem Cake/acre to suppress soil-borne Nematodes and Wilt.",
            organicBioFertilizerTe = "వేరు వ్యవస్థ మరియు తెగుళ్ల నివారణకు ఎకరాకు 500 కేజీల వేప పిండి చల్లండి."
        ),
        CropFertilizerGuideline(
            id = "cotton",
            nameEn = "Cotton (Patti)",
            nameTe = "పత్తి (Cotton)",
            icon = "☁️",
            baseNPerAcreKg = 60f,
            basePPerAcreKg = 30f,
            baseKPerAcreKg = 30f,
            fymTonsPerAcre = 5.0f,
            splitApplicationEn = listOf(
                "Basal (At Sowing): 100% P + 20% N",
                "Square Formation (35 Days): 40% N + 50% K",
                "Boll Development (65 Days): 40% N + 50% K"
            ),
            splitApplicationTe = listOf(
                "విత్తేటప్పుడు: 100% భాస్వరం + 20% నైట్రోజన్",
                "కాయ పూత దశలో (35 రోజులు): 40% నైట్రోజన్ + 50% పొటాషియం",
                "పత్తి కాయ ఊరే దశలో (65 రోజులు): 40% నైట్రోజన్ + 50% పొటాషియం"
            ),
            organicBioFertilizerEn = "Foliar spray of 1% Magnesium Sulphate + 1% DAP during boll filling to prevent leaf reddening.",
            organicBioFertilizerTe = "ఆకులు ఎర్రబడకుండా 1% మెగ్నీషియం సల్ఫేట్ + 1% DAP పిచికారీ చేయండి."
        ),
        CropFertilizerGuideline(
            id = "groundnut",
            nameEn = "Groundnut",
            nameTe = "వేరుశనగ (Groundnut)",
            icon = "🥜",
            baseNPerAcreKg = 12f,
            basePPerAcreKg = 24f,
            baseKPerAcreKg = 20f,
            fymTonsPerAcre = 3.0f,
            splitApplicationEn = listOf(
                "Basal (At Sowing): 100% N, 100% P, 100% K",
                "Pegging Stage (40-45 Days): Broadcast Gypsum @ 200 kg/acre"
            ),
            splitApplicationTe = listOf(
                "విత్తే ముందే: 100% నైట్రోజన్, 100% భాస్వరం, 100% పొటాషియం పూర్తిగా వేయాలి",
                "ఊడలు దిగే దశ (40-45 రోజులు): ఎకరాకు 200 కేజీల జిప్సమ్ చల్లాలి"
            ),
            organicBioFertilizerEn = "Treat seeds with Rhizobium culture (200g per 10kg seeds) for natural nitrogen fixation.",
            organicBioFertilizerTe = "విత్తన రక్షణకు రైజోబియం కల్చర్ తో విత్తన శుద్ధి చేయండి."
        ),
        CropFertilizerGuideline(
            id = "sugarcane",
            nameEn = "Sugarcane",
            nameTe = "చెరకు (Sugarcane)",
            icon = "🎋",
            baseNPerAcreKg = 112f,
            basePPerAcreKg = 40f,
            baseKPerAcreKg = 48f,
            fymTonsPerAcre = 10.0f,
            splitApplicationEn = listOf(
                "Basal Planting: 100% P + 25% N",
                "30 Days After Planting: 25% N + 35% K",
                "60 Days After Planting: 25% N + 35% K",
                "90 Days (Earthing Up): 25% N + 30% K"
            ),
            splitApplicationTe = listOf(
                "నాటు సమయంలో: 100% భాస్వరం + 25% నైట్రోజన్",
                "30 రోజుల వద్ద: 25% నైట్రోజన్ + 35% పొటాషియం",
                "60 రోజుల వద్ద: 25% నైట్రోజన్ + 35% పొటాషియం",
                "90 రోజుల వద్ద (మట్టి తోయుట): 25% నైట్రోజన్ + 30% పొటాషియం"
            ),
            organicBioFertilizerEn = "Apply Acetobacter diazotrophicus @ 4 kg/acre to reduce 25% chemical Nitrogen requirement.",
            organicBioFertilizerTe = "అసిటోబాక్టర్ జీవకోశాలు వాడి 25% రసాయన యూరియా తగ్గించవచ్చు."
        ),
        CropFertilizerGuideline(
            id = "maize",
            nameEn = "Maize (Mokkajonna)",
            nameTe = "జొన్న / మొక్కజొన్న",
            icon = "🌽",
            baseNPerAcreKg = 80f,
            basePPerAcreKg = 32f,
            baseKPerAcreKg = 32f,
            fymTonsPerAcre = 4.0f,
            splitApplicationEn = listOf(
                "Basal: 100% P + 25% N + 50% K",
                "Knee High Stage (30 Days): 50% N",
                "Tasseling Stage (50 Days): 25% N + 50% K"
            ),
            splitApplicationTe = listOf(
                "విత్తే సమయంలో: 100% భాస్వరం + 25% నైట్రోజన్ + 50% పొటాషియం",
                "మోకాలు ఎత్తు దశ (30 రోజులు): 50% నైట్రోజన్",
                "కంకి ఈనే దశ (50 రోజులు): 25% నైట్రోజన్ + 50% పొటాషియం"
            ),
            organicBioFertilizerEn = "Spray 0.5% Zinc Sulphate at 30 days to fix white bud disorder.",
            organicBioFertilizerTe = "తెల్ల ఆకు లోపం నివారణకు 30 రోజులకు 0.5% జింక్ సల్ఫేట్ స్ప్రే చేయండి."
        )
    )
}

@Composable
fun ApNpkFertilizerCalculatorWidget(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")

    var selectedCropId by remember { mutableStateOf("rice_paddy") }
    var acreageInput by remember { mutableStateOf("2.5") }
    var nStatus by remember { mutableStateOf(NutrientStatus.MEDIUM) }
    var pStatus by remember { mutableStateOf(NutrientStatus.MEDIUM) }
    var kStatus by remember { mutableStateOf(NutrientStatus.MEDIUM) }
    var showSplitDetails by remember { mutableStateOf(true) }

    val crop = remember(selectedCropId) {
        ApFertilizerGuidelineRepository.crops.find { it.id == selectedCropId }
            ?: ApFertilizerGuidelineRepository.crops.first()
    }

    val acres = acreageInput.toFloatOrNull() ?: 1.0f

    // Recommended Pure N, P, K in kg for total farm area
    val reqN = crop.baseNPerAcreKg * acres * nStatus.factor
    val reqP = crop.basePPerAcreKg * acres * pStatus.factor
    val reqK = crop.baseKPerAcreKg * acres * kStatus.factor

    // Calculate Commercial Fertilizer Bags Required
    // 1 Bag of Urea (45 kg) contains 46% N = 20.7 kg pure N
    val ureaBags = ceil(reqN / 20.7f).toInt().coerceAtLeast(1)

    // 1 Bag of DAP (50 kg) contains 46% P2O5 = 23 kg pure P (and 9 kg N)
    val dapBags = ceil(reqP / 23.0f).toInt().coerceAtLeast(1)

    // 1 Bag of MOP (50 kg) contains 60% K2O = 30 kg pure K
    val mopBags = ceil(reqK / 30.0f).toInt().coerceAtLeast(1)

    // Organic FYM
    val fymTons = crop.fymTonsPerAcre * acres

    // Estimated Input Cost Calculation in ₹ (Govt Subsidized Rates approx)
    // Urea ~ ₹266/bag, DAP ~ ₹1350/bag, MOP ~ ₹1700/bag
    val estimatedCostInr = (ureaBags * 266) + (dapBags * 1350) + (mopBags * 1700)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ap_npk_fertilizer_calculator_widget"),
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
                            imageVector = Icons.Default.Calculate,
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
                                text = if (isTelugu) "ఎరువుల రసాయన లెక్కింపు క్యాలిక్యులేటర్" else "AP N-P-K Fertilizer Calculator",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Text(
                            text = if (isTelugu) "నేల పరీక్ష ఫలితాలు & ఎకరాల ఆధారంగా ANGRAU మోతాదు సిఫార్సు" else "ANGRAU Guidelines & Soil Health Card Dose Precision Engine",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF15803D).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Rythu Bharosa",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // --- STEP 1: SELECT CROP ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isTelugu) "1. పంటను ఎంచుకోండి (Select Crop):" else "1. Select Crop Type:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ApFertilizerGuidelineRepository.crops) { item ->
                        val isSelected = selectedCropId == item.id
                        Surface(
                            onClick = { selectedCropId = item.id },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFF15803D) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF15803D) else Color(0xFFE2E8F0)),
                            modifier = Modifier.testTag("crop_calc_select_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(item.icon, fontSize = 16.sp)
                                Text(
                                    text = if (isTelugu) item.nameTe else item.nameEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }

            // --- STEP 2: FARM ACREAGE INPUT ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "2. పొలం విస్తీర్ణం (Farm Size in Acres):" else "2. Farm Size / Acreage:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    // Quick Acre Preset Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("1", "2.5", "5", "10").forEach { preset ->
                            Surface(
                                onClick = { acreageInput = preset },
                                shape = RoundedCornerShape(10.dp),
                                color = if (acreageInput == preset) Color(0xFF15803D).copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (acreageInput == preset) Color(0xFF15803D) else Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = "$preset Ac",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (acreageInput == preset) Color(0xFF15803D) else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = acreageInput,
                    onValueChange = { acreageInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.SquareFoot, contentDescription = null, tint = Color(0xFF15803D))
                    },
                    trailingIcon = {
                        Text("Acres", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(end = 12.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calc_acreage_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF15803D),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )
            }

            // --- STEP 3: SOIL TEST RESULTS ADJUSTMENT (N, P, K STATUS) ---
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                            Text(
                                text = if (isTelugu) "3. నేల పరీక్ష ఫలితాలు (Soil Health Card Status)" else "3. Current Soil Test Status:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        // Preset Auto-fill from Lam Farm Lab
                        TextButton(
                            onClick = {
                                nStatus = NutrientStatus.LOW
                                pStatus = NutrientStatus.MEDIUM
                                kStatus = NutrientStatus.HIGH
                                Toast.makeText(context, "Loaded Soil Health Card Preset for Guntur District", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(if (isTelugu) "SHC లోడ్ చేయి" else "Auto-fill SHC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }

                    // Nitrogen (N) Selector
                    NutrientStatusSelectorRow(
                        symbol = "N",
                        name = if (isTelugu) "నైట్రోజన్ (Nitrogen)" else "Nitrogen (N)",
                        currentStatus = nStatus,
                        onStatusSelected = { nStatus = it },
                        isTelugu = isTelugu
                    )

                    // Phosphorus (P) Selector
                    NutrientStatusSelectorRow(
                        symbol = "P",
                        name = if (isTelugu) "భాస్వరం (Phosphorus)" else "Phosphorus (P)",
                        currentStatus = pStatus,
                        onStatusSelected = { pStatus = it },
                        isTelugu = isTelugu
                    )

                    // Potassium (K) Selector
                    NutrientStatusSelectorRow(
                        symbol = "K",
                        name = if (isTelugu) "పొటాషియం (Potassium)" else "Potassium (K)",
                        currentStatus = kStatus,
                        onStatusSelected = { kStatus = it },
                        isTelugu = isTelugu
                    )
                }
            }

            // --- RESULT PANEL: RECOMMENDED BAGS & COSTS ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fertilizer_calc_results_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        Column {
                            Text(
                                text = if (isTelugu) "సిఫార్సు చేయబడిన రసాయన ఎరువుల సంఖ్య" else "Recommended Fertilizer Bag Quantities",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${if (isTelugu) crop.nameTe else crop.nameEn} • $acres ${if (isTelugu) "ఎకరాలు" else "Acres"}",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Text(
                                text = "Est. ₹$estimatedCostInr",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Triple Bag Quantity Grid Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FertilizerBagTile(
                            name = "Urea",
                            subtext = "45kg Bag (46% N)",
                            bags = ureaBags,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f)
                        )
                        FertilizerBagTile(
                            name = "DAP",
                            subtext = "50kg Bag (P2O5)",
                            bags = dapBags,
                            color = Color(0xFFFBBF24),
                            modifier = Modifier.weight(1f)
                        )
                        FertilizerBagTile(
                            name = "MOP",
                            subtext = "50kg Bag (60% K)",
                            bags = mopBags,
                            color = Color(0xFFF87171),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Pure N-P-K Kg Breakdown Summary Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pure N: ${reqN.roundToInt()} kg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "Pure P: ${reqP.roundToInt()} kg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFBBF24)
                        )
                        Text(
                            text = "Pure K: ${reqK.roundToInt()} kg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF87171)
                        )
                    }

                    // Recommended FYM Organic Manure
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🪵", fontSize = 16.sp)
                            Text(
                                text = if (isTelugu) "పశువుల ఎరువు (FYM / Vermicompost):" else "Farmyard Manure (FYM Organic):",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Text(
                            text = "$fymTons Tons",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4ADE80)
                        )
                    }
                }
            }

            // --- STAGE-WISE SPLIT APPLICATION SCHEDULE (EXPANDABLE) ---
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSplitDetails = !showSplitDetails },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                            Text(
                                text = if (isTelugu) "విడతల వారీ ఎరువుల వేసే విధానం (Split Application Timeline)" else "Stage-wise Split Application Schedule",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Icon(
                            imageVector = if (showSplitDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF64748B)
                        )
                    }

                    AnimatedVisibility(
                        visible = showSplitDetails,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val splitList = if (isTelugu) crop.splitApplicationTe else crop.splitApplicationEn
                            splitList.forEachIndexed { idx, stageDesc ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(Color(0xFF15803D), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Text(
                                        text = stageDesc,
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Organic Bio-fertilizer Advisory Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF15803D).copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, Color(0xFF15803D).copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Nature, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = if (isTelugu) crop.organicBioFertilizerTe else crop.organicBioFertilizerEn,
                                        fontSize = 11.sp,
                                        color = Color(0xFF14532D),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Save & Share Action Button
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "Saved Fertilizer Plan ($acres Acres ${crop.nameEn}) to Rythu Bharosa Records!",
                        Toast.LENGTH_LONG
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_fertilizer_plan_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
            ) {
                Icon(imageVector = Icons.Default.Bookmark, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTelugu) "లెక్కింపు నివేదిక భద్రపరచుము (Save Plan)" else "Save Fertilizer Plan to Farm Profile",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NutrientStatusSelectorRow(
    symbol: String,
    name: String,
    currentStatus: NutrientStatus,
    onStatusSelected: (NutrientStatus) -> Unit,
    isTelugu: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )

            Text(
                text = if (isTelugu) currentStatus.labelTe else currentStatus.labelEn,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = currentStatus.color
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NutrientStatus.values().forEach { status ->
                val isSelected = currentStatus == status
                Surface(
                    onClick = { onStatusSelected(status) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) status.color else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isSelected) status.color else Color(0xFFCBD5E1)),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (status) {
                                NutrientStatus.LOW -> if (isTelugu) "తక్కువ" else "Low"
                                NutrientStatus.MEDIUM -> if (isTelugu) "మధ్యస్థం" else "Medium"
                                NutrientStatus.HIGH -> if (isTelugu) "అధికం" else "High"
                            },
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FertilizerBagTile(
    name: String,
    subtext: String,
    bags: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )

            Text(
                text = "$bags Bags",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = subtext,
                fontSize = 8.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
