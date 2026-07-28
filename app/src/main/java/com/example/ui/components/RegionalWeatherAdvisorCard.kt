package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

data class ApTsDistrictWeather(
    val id: String,
    val districtName: String,
    val districtNameTe: String,
    val region: String, // AP or Telangana
    val majorCrops: String,
    val tempC: Int,
    val weatherCondition: String,
    val humidityPercent: Int,
    val rainProbability: Int,
    val windSpeedKmh: Int,
    val weatherAlertTe: String,
    val weatherAlertEn: String,
    val cropAdviceTe: String,
    val cropAdviceEn: String,
    val voiceSummaryTe: String,
    val voiceSummaryEn: String
)

object ApTsWeatherRepository {
    val districts = listOf(
        ApTsDistrictWeather(
            id = "guntur",
            districtName = "Guntur (Guntur Mirchi Yard Belt)",
            districtNameTe = "గుంటూరు (మిర్చి బెల్ట్)",
            region = "Andhra Pradesh",
            majorCrops = "Red Chilli (Mirchi), Cotton, Paddy",
            tempC = 34,
            weatherCondition = "Partly Cloudy • High Humidity",
            humidityPercent = 78,
            rainProbability = 65,
            windSpeedKmh = 14,
            weatherAlertTe = "⚠️ అధిక తేమ వాతావరణం - నల్ల తామర పురుగు (Black Thrips) ప్రమాదం!",
            weatherAlertEn = "⚠️ High Humidity Alert - Black Thrips & Anthracnose Risk on Chilli crops!",
            cropAdviceTe = "గుంటూరు మిర్చి రైతులకు సూచన: రాబోయే 24 గంటల్లో తేలికపాటి వర్ష సూచన ఉంది. మందుల పిచికారీని తాత్కాలికంగా వాయిదా వేయండి. పొలంలో నీరు నిల్వ ఉండకుండా డ్రైనేజీ కాలువలను సరిచేసుకోండి.",
            cropAdviceEn = "Advisory for Guntur Chilli Farmers: Light showers predicted in next 24 hours. Postpone chemical spraying until rain clears. Ensure adequate field drainage to prevent root rot.",
            voiceSummaryTe = "నమస్కారం గుంటూరు మిర్చి రైతు సోదరులకు! రాబోయే 24 గంటల్లో తేలికపాటి వర్షం మరియు అధిక తేమ నమోదయ్యే అవకాశం ఉంది. మందుల పిచికారీ వాయిదా వేయండి.",
            voiceSummaryEn = "Greetings Guntur Chilli farmers. High humidity and light rain expected in 24 hours. Delay pesticide spray and clean drainage channels."
        ),
        ApTsDistrictWeather(
            id = "krishna",
            districtName = "Krishna & West Godavari (Delta Belt)",
            districtNameTe = "కృష్ణా & పశ్చిమ గోదావరి (డెల్టా)",
            region = "Andhra Pradesh",
            majorCrops = "Paddy (Nellore Sona), Sugarcane, Maize",
            tempC = 32,
            weatherCondition = "Scattered Showers 🌧️",
            humidityPercent = 85,
            rainProbability = 80,
            windSpeedKmh = 18,
            weatherAlertTe = "🌧️ వర్ష సూచన - కోత కోసిన వరి ధాన్యం సురక్షిత ప్రాంతాలకు తరలించండి!",
            weatherAlertEn = "🌧️ Rain Forecast - Protect harvested Paddy & secure grain bags!",
            cropAdviceTe = "కృష్ణా డెల్టా వరి రైతులకు హెచ్చరిక: రాబోయే 12 గంటల్లో మోస్తరు వర్షాలు పడే అవకాశం ఉంది. కోత కోసిన ధాన్యాన్ని తడివకుండా టార్పాలిన్లతో కప్పండి. నత్రజని ఎరువుల వాడకాన్ని ఆపండి.",
            cropAdviceEn = "Krishna Delta Paddy Alert: Moderate rain expected in 12 hours. Cover harvested grain with tarpaulins. Suspend urea/nitrogen top-dressing temporarily.",
            voiceSummaryTe = "కృష్ణా డెల్టా రైతులకు ముఖ్య గమనిక: వర్ష సూచన ఉన్నందున కోసిన వరి కుప్పలను టార్పాలిన్ ప్లాస్టిక్లతో భద్రపరచండి.",
            voiceSummaryEn = "Krishna Delta farmers notice: Cover harvested paddy with tarpaulins due to incoming rainfall."
        ),
        ApTsDistrictWeather(
            id = "warangal",
            districtName = "Warangal & Khammam",
            districtNameTe = "వరంగల్ & ఖమ్మం",
            region = "Telangana",
            majorCrops = "Cotton (Patti), Chilli, Maize",
            tempC = 35,
            weatherCondition = "Sunny with Dry Winds ☀️",
            humidityPercent = 52,
            rainProbability = 15,
            windSpeedKmh = 12,
            weatherAlertTe = "☀️ పొడి గాలులు - గులాబీ రంగు పురుగు (Pink Bollworm) పర్యవేక్షణ అవసరం!",
            weatherAlertEn = "☀️ Hot Dry Winds - Monitor Cotton crops for Pink Bollworm activity!",
            cropAdviceTe = "వరంగల్ పత్తి రైతులకు ఉచిత సలహా: పొడి వాతావరణం అనుకూలంగా ఉంది. పత్తి చేనులో ఎల్లో మరియు పింక్ లింగాకర్షక బుట్టలు (Pheromone Traps) ఏర్పాటు చేసి పురుగుల ఉధృతిని గమనించండి.",
            cropAdviceEn = "Warangal Cotton Advisory: Dry weather prevails. Install Pheromone traps in cotton fields to monitor Pink Bollworm moths. Maintain light irrigation.",
            voiceSummaryTe = "వరంగల్ పత్తి రైతు సోదరులకు సలహా: వాతావరణం పొడిగా ఉంది, పత్తి పొలంలో లింగాకర్షక బుట్టలు అమర్చండి.",
            voiceSummaryEn = "Warangal Cotton farmers: Weather is sunny and dry. Set up pheromone traps for pink bollworm management."
        ),
        ApTsDistrictWeather(
            id = "nizamabad",
            districtName = "Nizamabad & Karimnagar",
            districtNameTe = "నిజామాబాద్ & కరీంనగర్",
            region = "Telangana",
            majorCrops = "Turmeric (Pasupu), Paddy, Maize",
            tempC = 33,
            weatherCondition = "Overcast Sky ☁️",
            humidityPercent = 70,
            rainProbability = 40,
            windSpeedKmh = 10,
            weatherAlertTe = "☁️ మబ్బులతో కూడిన ఆకాశం - పసుపు దుంప కుళ్లు తెగులు జాగరూకత!",
            weatherAlertEn = "☁️ Overcast Weather - High risk of Rhizome Rot in Turmeric!",
            cropAdviceTe = "నిజామాబాద్ పసుపు రైతులకు సూచన: ఆకాశం మేఘావృతమై ఉంది. పసుపు చేనులో నీరు నిలువకుండా చూడాలి. దుంప కుళ్లు నివారణకు కాపర్ ఆక్సీక్లోరైడ్ చల్లడం శ్రేయస్కరం.",
            cropAdviceEn = "Nizamabad Turmeric Advisory: Overcast conditions. Prevent water stagnation to curb Rhizome Rot. Apply Copper Oxychloride as preventative soil drenching.",
            voiceSummaryTe = "నిజామాబాద్ పసుపు రైతులకు సూచన: మేఘావృతమైన వాతావరణం వల్ల పసుపు దుంప కుళ్లు తెగులు రాకుండా డ్రైనేజీ సరిచూసుకోండి.",
            voiceSummaryEn = "Nizamabad Turmeric farmers: Prevent Rhizome Rot during cloudy days by draining standing field water."
        ),
        ApTsDistrictWeather(
            id = "kurnool",
            districtName = "Kurnool & Anantapur (Rayalaseema)",
            districtNameTe = "కర్నూలు & అనంతపురం (రాయలసీమ)",
            region = "Andhra Pradesh",
            majorCrops = "Groundnut (Verusanaga), Onion, Pomegranate",
            tempC = 37,
            weatherCondition = "Hot Dry Sunshine 🌤️",
            humidityPercent = 40,
            rainProbability = 10,
            windSpeedKmh = 16,
            weatherAlertTe = "🌤️ తీవ్ర వేడి - వేరుశనగ పంటకు సూక్ష్మ బిందు సేద్యం (Drip Irrigation) ఇవ్వండి!",
            weatherAlertEn = "🌤️ High Thermal Heat - Schedule Drip Irrigation for Groundnut!",
            cropAdviceTe = "రాయలసీమ వేరుశనగ రైతులకు సలహా: అధిక ఉష్ణోగ్రతల వల్ల ఆకుల వాడిపోవడం గమనించవచ్చు. ఉదయం లేదా సాయంత్రం వేళల్లో బిందు సేద్యం ద్వారా తడులు ఇవ్వండి.",
            cropAdviceEn = "Rayalaseema Groundnut Advisory: High heat wave conditions. Schedule drip or sprinkler irrigation during early morning or evening to reduce evapotranspiration.",
            voiceSummaryTe = "కర్నూలు మరియు అనంతపురం రైతులకు సూచన: తీవ్రమైన ఎండల కారణంగా వేరుశనగ పంటకు ఉదయం వేళలో డ్రిప్ తడులు ఇవ్వండి.",
            voiceSummaryEn = "Rayalaseema Groundnut farmers: Apply drip irrigation during cooler hours to protect crop from heat stress."
        )
    )
}

@Composable
fun RegionalWeatherAdvisorCard(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDistrictIndex by remember { mutableStateOf(0) }
    val district = ApTsWeatherRepository.districts[selectedDistrictIndex.coerceIn(ApTsWeatherRepository.districts.indices)]

    // Text-To-Speech Engine State
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    // Initialize TTS Engine
    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try setting Telugu if selected or fallback to English
                val teLocale = Locale("te", "IN")
                val result = ttsEngine?.setLanguage(teLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsEngine?.language = Locale.ENGLISH
                }
            }
        }
        ttsEngine = tts

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")

    // Speech Recognition Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!spokenText.isNullOrBlank()) {
            Toast.makeText(context, "🎤 Recognized: \"$spokenText\"", Toast.LENGTH_LONG).show()
            
            // Check if spoken text matches any region
            val matchedIndex = ApTsWeatherRepository.districts.indexOfFirst {
                it.districtName.lowercase().contains(spokenText.lowercase()) ||
                it.districtNameTe.contains(spokenText) ||
                spokenText.lowercase().contains("guntur") || spokenText.contains("గుంటూరు")
            }
            if (matchedIndex != -1) {
                selectedDistrictIndex = matchedIndex
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Voice Assistant Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isTelugu) "ఆంధ్ర ప్రదేశ్ & తెలంగాణ రైతు సలహా కేంద్రీయ" else "AP & Telangana Regional Agri-Weather Center",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isTelugu) "వాతావరణం ఆధారిత పంట పర్యవేక్షణ & వాయిస్ అసిస్టెంట్" else "Micro-climate & Crop Management Advisor",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Voice Mic Assistant Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF16A34A).copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (isTelugu) "te-IN" else "en-IN")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, if (isTelugu) "మీ జిల్లా లేదా పంట పేరు చెప్పండి..." else "Speak your district name (e.g., Guntur, Warangal)...")
                        }
                        try {
                            speechRecognizerLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Voice input not supported on this device", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // District/Region Selector Bar
            Text(
                text = if (isTelugu) "మీ జిల్లా లేదా వ్యవసాయ బెల్ట్ ఎంచుకోండి:" else "Select Agricultural Belt / District:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ApTsWeatherRepository.districts.size) { idx ->
                    val dist = ApTsWeatherRepository.districts[idx]
                    val isSelected = idx == selectedDistrictIndex
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF0284C7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable {
                            selectedDistrictIndex = idx
                            // Stop speaking if playing previous advice
                            if (isSpeaking) {
                                ttsEngine?.stop()
                                isSpeaking = false
                            }
                        }
                    ) {
                        Text(
                            text = if (isTelugu) dist.districtNameTe else dist.districtName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Microclimate Weather Stats Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isTelugu) district.districtNameTe else district.districtName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Major Crops: ${district.majorCrops}",
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${district.tempC}°C",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFACC15)
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    // Live Weather Parameters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherParamPill(icon = "💧", label = "Humidity", value = "${district.humidityPercent}%")
                        WeatherParamPill(icon = "🌧️", label = "Rain Risk", value = "${district.rainProbability}%")
                        WeatherParamPill(icon = "💨", label = "Wind", value = "${district.windSpeedKmh} km/h")
                        WeatherParamPill(icon = "🌤️", label = "Sky", value = district.weatherCondition.split(" ").first())
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weather Alert & Actionable Crop Advice Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF3C7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (isTelugu) district.weatherAlertTe else district.weatherAlertEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTelugu) district.cropAdviceTe else district.cropAdviceEn,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF78350F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Text-To-Speech Audio Playback Bar ("తెలుగు శ్రవణం / Read Advice Aloud")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSpeaking) Color(0xFFDCFCE7) else Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSpeaking) {
                            ttsEngine?.stop()
                            isSpeaking = false
                        } else {
                            val textToSpeak = if (isTelugu) district.voiceSummaryTe else district.voiceSummaryEn
                            if (isTelugu) {
                                ttsEngine?.language = Locale("te", "IN")
                            } else {
                                ttsEngine?.language = Locale.ENGLISH
                            }
                            ttsEngine?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "KRISHI_VOICE")
                            isSpeaking = true
                            Toast
                                .makeText(
                                    context,
                                    if (isTelugu) "🔊 తెలుగు సలహా వినిపిస్తోంది..." else "🔊 Reading advisory aloud...",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSpeaking) Color(0xFF16A34A) else Color(0xFF22C55E),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isSpeaking) {
                                    if (isTelugu) "🔊 వాయిస్ వినిపిస్తోంది (ఆపుటకు నొక్కండి)..." else "🔊 Speaking Advisory..."
                                } else {
                                    if (isTelugu) "🔊 వాయిస్ రూపంలో వినండి (తెలుగు శ్రవణం)" else "🔊 Listen to Voice Advisory (TTS)"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF14532D)
                            )
                            Text(
                                text = if (isTelugu) "రైతు సోదరుల సౌకర్యార్థం ఆడియో సలహా" else "Audio guidance for hands-free farming",
                                fontSize = 10.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF16A34A)
                    ) {
                        Text(
                            text = if (isSpeaking) "STOP" else "PLAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherParamPill(
    icon: String,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 9.sp, color = Color.Gray)
    }
}
