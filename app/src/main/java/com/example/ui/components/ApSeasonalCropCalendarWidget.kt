package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ApSeason(val code: String, val titleEn: String, val titleTe: String, val monthsEn: String, val icon: String, val color: Color) {
    ALL("ALL", "All Cycles", "అన్ని కాలాలు", "Jan - Dec", "🗓️", Color(0xFF475569)),
    KHARIF("KHARIF", "Kharif (Monsoon)", "ఖరీఫ్ (వర్షాకాలం)", "June - October", "🌧️", Color(0xFF15803D)),
    RABI("RABI", "Rabi (Winter)", "రబీ (చలికాలం)", "October - March", "🌾", Color(0xFFB45309)),
    ZAID("ZAID", "Zaid (Summer)", "జాయెద్ (ఎండాకాలం)", "March - June", "☀️", Color(0xFF0284C7))
}

data class CropStagePeriod(
    val monthIndex: Int, // 0..11 (Jan..Dec)
    val stage: CropStage // Sowing, Growing, Harvesting
)

enum class CropStage(val labelEn: String, val labelTe: String, val color: Color) {
    SOWING("Sowing / Nursery", "విత్తనాలు / నారు", Color(0xFF16A34A)),
    GROWTH("Growth / Flowering", "ఎదుగుదల / పూత", Color(0xFFEAB308)),
    HARVEST("Harvesting Phase", "కోత సమయం", Color(0xFFDC2626)),
    OFF_SEASON("Off-Season", "విరామ సమయం", Color(0xFFE2E8F0))
}

data class ApCropCalendarData(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val icon: String,
    val primarySeason: ApSeason,
    val targetDistrictsEn: String,
    val targetDistrictsTe: String,
    val sowingMonthsEn: String,
    val sowingMonthsTe: String,
    val harvestMonthsEn: String,
    val harvestMonthsTe: String,
    val durationDays: String,
    val keyAdvisoryEn: String,
    val keyAdvisoryTe: String,
    val monthlyStages: List<CropStage> // Size 12 for Jan to Dec
)

object ApCropCalendarRepository {
    val crops = listOf(
        ApCropCalendarData(
            id = "rice_paddy",
            nameEn = "Rice / Paddy",
            nameTe = "వరి (Paddy)",
            icon = "🌾",
            primarySeason = ApSeason.KHARIF,
            targetDistrictsEn = "Krishna, Guntur, Godavari Delta, Nellore",
            targetDistrictsTe = "కృష్ణా, గుంటూరు, గోదావరి డెల్టా, నెల్లూరు",
            sowingMonthsEn = "Jun - Jul (Kharif) / Dec - Jan (Rabi)",
            sowingMonthsTe = "జూన్ - జూలై (ఖరీఫ్) / డిసెంబర్ - జనవరి (రబీ)",
            harvestMonthsEn = "Nov - Dec (Kharif) / Apr - May (Rabi)",
            harvestMonthsTe = "నవంబర్ - డిసెంబర్ (ఖరీఫ్) / ఏప్రిల్ - మే (రబీ)",
            durationDays = "120 - 150 Days",
            keyAdvisoryEn = "Maintain 2-5cm standing water during tillering. Check for Stem Borer & Gall Midge during monsoon high humidity.",
            keyAdvisoryTe = "పిలకల దశలో 2-5 సెం.మీ నీరు ఉంచండి. వర్షాకాల తేమలో కాండం తొలిచే పురుగు నివారణ చర్యలు తీసుకోండి.",
            monthlyStages = listOf(
                CropStage.SOWING, // Jan (Rabi Sowing)
                CropStage.GROWTH, // Feb
                CropStage.GROWTH, // Mar
                CropStage.HARVEST, // Apr (Rabi Harvest)
                CropStage.HARVEST, // May
                CropStage.SOWING, // Jun (Kharif Sowing)
                CropStage.SOWING, // Jul
                CropStage.GROWTH, // Aug
                CropStage.GROWTH, // Sep
                CropStage.GROWTH, // Oct
                CropStage.HARVEST, // Nov (Kharif Harvest)
                CropStage.HARVEST  // Dec
            )
        ),
        ApCropCalendarData(
            id = "chilli",
            nameEn = "Chilli (Mirchi)",
            nameTe = "మిర్చి (Chilli)",
            icon = "🌶️",
            primarySeason = ApSeason.KHARIF,
            targetDistrictsEn = "Guntur, Prakasam, Kurnool, Khammam Border",
            targetDistrictsTe = "గుంటూరు, ప్రకాశం, కర్నూలు, ఖమ్మం సరిహద్దు",
            sowingMonthsEn = "Jul - Aug (Nursery & Transplanting)",
            sowingMonthsTe = "జూలై - ఆగస్టు (నారు పెంపకం & నాటు)",
            harvestMonthsEn = "Dec - Mar (Multiple Pickings)",
            harvestMonthsTe = "డిసెంబర్ - మార్చి (బహుళ కోతలు)",
            durationDays = "180 - 210 Days",
            keyAdvisoryEn = "High risk of Black Thrips & Wilt. Apply bio-pesticides and Neem oil spray early morning under Rythu Bharosa Kendra guidance.",
            keyAdvisoryTe = "నల్ల తామర పురుగు మరియు వాడితెగులు నుండి రక్షణకు వేప నూనె & జీవ క్రిమిసంహారకాలు వాడండి.",
            monthlyStages = listOf(
                CropStage.HARVEST, // Jan (Peak Picking)
                CropStage.HARVEST, // Feb
                CropStage.HARVEST, // Mar
                CropStage.OFF_SEASON, // Apr
                CropStage.OFF_SEASON, // May
                CropStage.OFF_SEASON, // Jun
                CropStage.SOWING, // Jul (Nursery)
                CropStage.SOWING, // Aug (Transplanting)
                CropStage.GROWTH, // Sep
                CropStage.GROWTH, // Oct (Flowering)
                CropStage.GROWTH, // Nov (Fruiting)
                CropStage.HARVEST  // Dec (First Picking)
            )
        ),
        ApCropCalendarData(
            id = "cotton",
            nameEn = "Cotton (Patti)",
            nameTe = "పత్తి (Cotton)",
            icon = "☁️",
            primarySeason = ApSeason.KHARIF,
            targetDistrictsEn = "Guntur, Kurnool, Anantapur, Nandyal",
            targetDistrictsTe = "గుంటూరు, కర్నూలు, అనంతపురం, నంద్యాల",
            sowingMonthsEn = "Jun - Jul (Monsoon Arrival)",
            sowingMonthsTe = "జూన్ - జూలై (వర్షారంభం)",
            harvestMonthsEn = "Nov - Jan (Boll Picking)",
            harvestMonthsTe = "నవంబర్ - జనవరి (పత్తి ఏరుత)",
            durationDays = "160 - 180 Days",
            keyAdvisoryEn = "Install pheromone traps for Pink Bollworm in Sep-Oct. Ensure strict ridge drainage to prevent wilt.",
            keyAdvisoryTe = "గులాబీ రంగు రంధ్ర పురుగు కొరకు ఫెరమోన్ ఉచ్చులు అమర్చండి. నీరు నిలవకుండా డ్రైనేజీ చూడండి.",
            monthlyStages = listOf(
                CropStage.HARVEST, // Jan (Final Picking)
                CropStage.OFF_SEASON, // Feb
                CropStage.OFF_SEASON, // Mar
                CropStage.OFF_SEASON, // Apr
                CropStage.OFF_SEASON, // May
                CropStage.SOWING, // Jun (Sowing)
                CropStage.SOWING, // Jul
                CropStage.GROWTH, // Aug
                CropStage.GROWTH, // Sep (Flowering)
                CropStage.GROWTH, // Oct (Boll Formation)
                CropStage.HARVEST, // Nov (First Picking)
                CropStage.HARVEST  // Dec (Second Picking)
            )
        ),
        ApCropCalendarData(
            id = "groundnut",
            nameEn = "Groundnut",
            nameTe = "వేరుశనగ (Groundnut)",
            icon = "🥜",
            primarySeason = ApSeason.KHARIF,
            targetDistrictsEn = "Anantapur, Chittoor, Kadapa, Kurnool",
            targetDistrictsTe = "అనంతపురం, చిత్తూరు, కడప, కర్నూలు",
            sowingMonthsEn = "Jun - Jul (Kharif) / Nov - Dec (Rabi)",
            sowingMonthsTe = "జూన్ - జూలై (ఖరీఫ్) / నవంబర్ - డిసెంబర్ (రబీ)",
            harvestMonthsEn = "Oct - Nov (Kharif) / Mar - Apr (Rabi)",
            harvestMonthsTe = "అక్టోబర్ - నవంబర్ (ఖరీఫ్) / మార్చి - ఏప్రిల్ (రబీ)",
            durationDays = "105 - 120 Days",
            keyAdvisoryEn = "Apply Gypsum @ 200 kg/acre during pegging stage (45 days after sowing) for pod development.",
            keyAdvisoryTe = "ఊడలు దిగే దశలో (45 రోజులకు) ఎకరాకు 200 కేజీల జిప్సమ్ వేస్తే కాయ తొడుగు బాగుంటుంది.",
            monthlyStages = listOf(
                CropStage.GROWTH, // Jan (Rabi Growth)
                CropStage.GROWTH, // Feb
                CropStage.HARVEST, // Mar (Rabi Harvest)
                CropStage.HARVEST, // Apr
                CropStage.OFF_SEASON, // May
                CropStage.SOWING, // Jun (Kharif Sowing)
                CropStage.SOWING, // Jul
                CropStage.GROWTH, // Aug
                CropStage.GROWTH, // Sep
                CropStage.HARVEST, // Oct (Kharif Harvest)
                CropStage.SOWING, // Nov (Rabi Sowing)
                CropStage.SOWING  // Dec
            )
        ),
        ApCropCalendarData(
            id = "sugarcane",
            nameEn = "Sugarcane",
            nameTe = "చెరకు (Sugarcane)",
            icon = "🎋",
            primarySeason = ApSeason.ZAID,
            targetDistrictsEn = "Visakhapatnam, East Godavari, Chittoor",
            targetDistrictsTe = "విశాఖపట్నం, తూర్పు గోదావరి, చిత్తూరు",
            sowingMonthsEn = "Jan - Mar (Spring Planting)",
            sowingMonthsTe = "జనవరి - మార్చి (నాటు కాలం)",
            harvestMonthsEn = "Dec - Feb (Crushing Season)",
            harvestMonthsTe = "డిసెంబర్ - ఫిబ్రవరి (ఫ్యాక్టరీ రష్ కాలం)",
            durationDays = "300 - 360 Days",
            keyAdvisoryEn = "Tie and prop sugarcane stalks before September to avoid lodging from coastal cyclones.",
            keyAdvisoryTe = "తుఫాను గాలులకు చెరకు పడిపోకుండా సెప్టెంబర్ ముందే జంట సుట్టలు చుట్టండి.",
            monthlyStages = listOf(
                CropStage.HARVEST, // Jan
                CropStage.SOWING, // Feb (Planting)
                CropStage.SOWING, // Mar
                CropStage.GROWTH, // Apr
                CropStage.GROWTH, // May
                CropStage.GROWTH, // Jun
                CropStage.GROWTH, // Jul
                CropStage.GROWTH, // Aug
                CropStage.GROWTH, // Sep
                CropStage.GROWTH, // Oct
                CropStage.GROWTH, // Nov
                CropStage.HARVEST  // Dec (Harvest)
            )
        )
    )
}

@Composable
fun ApSeasonalCropCalendarWidget(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")
    var selectedSeason by remember { mutableStateOf(ApSeason.ALL) }
    var expandedCropId by remember { mutableStateOf<String?>("rice_paddy") }

    val filteredCrops = remember(selectedSeason) {
        if (selectedSeason == ApSeason.ALL) {
            ApCropCalendarRepository.crops
        } else {
            ApCropCalendarRepository.crops.filter { it.primarySeason == selectedSeason }
        }
    }

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val monthNamesTe = listOf("జన", "ఫిబ్ర", "మార్చి", "ఏప్రి", "మే", "జూన్", "జూలై", "ఆగ", "సెప్టె", "అక్టో", "నవం", "డిసెం")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ap_seasonal_crop_calendar_widget"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
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
                            imageVector = Icons.Default.CalendarMonth,
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
                                text = if (isTelugu) "ఆంధ్ర ప్రదేశ్ పంటల క్యాలెండర్" else "AP Seasonal Crop Calendar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Text(
                            text = if (isTelugu) "ఖరీఫ్, రబీ & జాయెద్ విత్తనాల మరియు కోతల సమయ పట్టిక" else "Optimal Planting & Harvesting Cycles (Kharif & Rabi)",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDC2626).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (isTelugu) "ప్రస్తుతం: ఖరీఫ్" else "Active: Kharif",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB91C1C),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Season Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ApSeason.values()) { season ->
                    val isSelected = selectedSeason == season
                    Surface(
                        onClick = { selectedSeason = season },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) season.color else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isSelected) season.color else Color(0xFFE2E8F0)),
                        modifier = Modifier.testTag("season_filter_${season.code.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(season.icon, fontSize = 13.sp)
                            Text(
                                text = if (isTelugu) season.titleTe else season.titleEn,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            // Stage Color Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = CropStage.SOWING.color, label = if (isTelugu) "విత్తనాలు/నాటు" else "Sowing")
                LegendItem(color = CropStage.GROWTH.color, label = if (isTelugu) "ఎదుగుదల" else "Growth")
                LegendItem(color = CropStage.HARVEST.color, label = if (isTelugu) "కోతలు" else "Harvest")
            }

            // List of Crop Cards with Interactive Month Matrix
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredCrops.forEach { crop ->
                    val isExpanded = expandedCropId == crop.id

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isExpanded) Color(0xFF16A34A) else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("crop_calendar_card_${crop.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { expandedCropId = if (isExpanded) null else crop.id }
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(crop.icon, fontSize = 24.sp)
                                    Column {
                                        Text(
                                            text = if (isTelugu) crop.nameTe else crop.nameEn,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = if (isTelugu) crop.targetDistrictsTe else crop.targetDistrictsEn,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = crop.primarySeason.color.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = crop.durationDays,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = crop.primarySeason.color,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // 12-Month Compact Timeline Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                crop.monthlyStages.forEachIndexed { idx, stage ->
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(stage.color)
                                        )
                                        Text(
                                            text = if (isTelugu) monthNamesTe[idx] else monthNames[idx],
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }

                            // Expanded Detailed Advisory Panel
                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Divider(color = Color(0xFFE2E8F0))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isTelugu) "🌱 విత్తనాల సమయం" else "🌱 Sowing Period",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF16A34A)
                                            )
                                            Text(
                                                text = if (isTelugu) crop.sowingMonthsTe else crop.sowingMonthsEn,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isTelugu) "🌾 కోత కాలం" else "🌾 Harvesting Window",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626)
                                            )
                                            Text(
                                                text = if (isTelugu) crop.harvestMonthsTe else crop.harvestMonthsEn,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                        }
                                    }

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
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = Color(0xFF15803D),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = if (isTelugu) crop.keyAdvisoryTe else crop.keyAdvisoryEn,
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
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF475569)
        )
    }
}
