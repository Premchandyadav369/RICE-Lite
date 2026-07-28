package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KrishiViewModel
import java.util.*

data class IlliterateFarmerQuickAction(
    val id: String,
    val titleTe: String,
    val titleEn: String,
    val iconEmoji: String,
    val bgColor: Color,
    val audioAdviceTe: String,
    val audioAdviceEn: String
)

object IlliterateFarmerActionsRepository {
    val actions = listOf(
        IlliterateFarmerQuickAction(
            id = "weather",
            titleTe = "వర్షాలు & వాతావరణం",
            titleEn = "Weather & Rain Alert",
            iconEmoji = "🌧️",
            bgColor = Color(0xFF0284C7),
            audioAdviceTe = "నమస్కారం! మీ ప్రాంతంలో రాబోయే 24 గంటల్లో తేలికపాటి వర్షాలు కురిసే అవకాశం ఉంది. పంటలకు మందుల పిచికారీని తాత్కాలికంగా ఆపండి.",
            audioAdviceEn = "Greetings! Light rainfall is expected in your region in the next 24 hours. Postpone pesticide spraying temporarily."
        ),
        IlliterateFarmerQuickAction(
            id = "mandi",
            titleTe = "ధాన్యం & మిర్చి ధరలు",
            titleEn = "Mandi Market Prices",
            iconEmoji = "🌾",
            bgColor = Color(0xFF16A34A),
            audioAdviceTe = "గుంటూరు యార్డులో ఏ గ్రేడ్ మిర్చి ధర క్వింటాలుకు పదహారు వేల ఐదువందల రూపాయలు పలుకుతోంది. వరి ధర రెండు వేల రెండువందల రూపాయలు.",
            audioAdviceEn = "Guntur Yard A-Grade Chilli price is 16,500 rupees per quintal. Paddy price is 2,200 rupees."
        ),
        IlliterateFarmerQuickAction(
            id = "pests",
            titleTe = "పురుగు & వ్యాధి స్కాన్",
            titleEn = "Pest & Disease Scan",
            iconEmoji = "🐛",
            bgColor = Color(0xFFDC2626),
            audioAdviceTe = "ఆకు పై తెగులు గుర్తింపుకు కెమెరా గుర్తును నొక్కండి. ఫోటో తీయగానే వాయిస్ ద్వారా తెగులు పేరు మరియు మందు సలహా లభిస్తుంది.",
            audioAdviceEn = "Tap the camera button to scan crop leaf. Take a photo to receive spoken disease diagnosis and treatment."
        ),
        IlliterateFarmerQuickAction(
            id = "fertilizer",
            titleTe = "ఎరువుల సంచి లెక్క",
            titleEn = "Fertilizer Bag Dose",
            iconEmoji = "💊",
            bgColor = Color(0xFFD97706),
            audioAdviceTe = "ఎకరా వరి లేదా పత్తి పంటకు దుక్కిలో ఒక సంచి డి ఎ పి మరియు ఐదు ప్యాకెట్ల యూరియా వేయవలెను.",
            audioAdviceEn = "For 1 acre Paddy or Cotton, apply 1 bag DAP as basal dose and 2 bags Urea in split applications."
        ),
        IlliterateFarmerQuickAction(
            id = "water",
            titleTe = "నీటి పారుదల సలహా",
            titleEn = "Irrigation Water Advisory",
            iconEmoji = "💧",
            bgColor = Color(0xFF2563EB),
            audioAdviceTe = "నేలలో తేమ శాతం బాగా ఉంది. తదుపరి నీటి తడి రెండు రోజుల తరువాత ఉదయం వేళలో ఇవ్వండి.",
            audioAdviceEn = "Soil moisture level is optimum. Schedule your next field irrigation after 2 days during early morning."
        ),
        IlliterateFarmerQuickAction(
            id = "helpline",
            titleTe = "రైతు కాల్ సెంటర్ 1551",
            titleEn = "Farmer Helpline 1551",
            iconEmoji = "📞",
            bgColor = Color(0xFF7C3AED),
            audioAdviceTe = "ఉచిత వ్యవసాయ సలహా కొరకు లేదా శాస్త్రవేత్తలతో మాట్లాడేందుకు ఉచిత నంబరు 1 5 5 1 కు డయల్ చేయండి.",
            audioAdviceEn = "Dial toll-free number 1551 to speak directly with Krishi Vigyan Kendra agricultural scientists."
        )
    )
}

@Composable
fun AccessibilityViewCard(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isHighContrast by viewModel.isHighContrast.collectAsState()
    val isLargeText by viewModel.isLargeText.collectAsState()
    val isIlliterateFarmerMode by viewModel.isIlliterateFarmerMode.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")

    // Text To Speech
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var currentlySpeakingId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val teLocale = Locale("te", "IN")
                if (ttsEngine?.setLanguage(teLocale) == TextToSpeech.LANG_MISSING_DATA) {
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

    // Voice Input Speech Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!spokenText.isNullOrBlank()) {
            Toast.makeText(context, "🎤 $spokenText", Toast.LENGTH_LONG).show()
            // Match spoken query with action
            val matchedAction = IlliterateFarmerActionsRepository.actions.find {
                it.titleTe.contains(spokenText) || it.titleEn.lowercase().contains(spokenText.lowercase()) ||
                spokenText.contains("వర్షం") || spokenText.lowercase().contains("weather")
            } ?: IlliterateFarmerActionsRepository.actions.first()

            val textToSpeak = if (isTelugu) matchedAction.audioAdviceTe else matchedAction.audioAdviceEn
            if (isTelugu) ttsEngine?.language = Locale("te", "IN") else ttsEngine?.language = Locale.ENGLISH
            ttsEngine?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "ACC_SPEAK")
            currentlySpeakingId = matchedAction.id
        }
    }

    // Dynamic Font & High Contrast Styling
    val titleFontSize = if (isLargeText) 20.sp else 15.sp
    val bodyFontSize = if (isLargeText) 16.sp else 12.sp

    val cardBgColor = if (isHighContrast) Color(0xFF000000) else MaterialTheme.colorScheme.surface
    val cardContentColor = if (isHighContrast) Color(0xFFFFFF00) else MaterialTheme.colorScheme.onSurface
    val cardBorderColor = if (isHighContrast) Color(0xFFFFFF00) else MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor, contentColor = cardContentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(if (isHighContrast) 3.dp else 1.dp, cardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isHighContrast) Color(0xFFFFFF00) else MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessibilityNew,
                            contentDescription = "Accessibility",
                            tint = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isTelugu) "సులభ వినియోగం & రైతు శ్రవణ సహాయకం" else "Accessibility & Farmer Voice Assistance",
                            fontWeight = FontWeight.Bold,
                            fontSize = titleFontSize,
                            color = cardContentColor
                        )
                        Text(
                            text = if (isTelugu) "పెద్ద అక్షరాలు, విజువల్ చిహ్నాలు & వాయిస్ చదువు" else "Large fonts, high contrast & Telugu voice read aloud",
                            fontSize = bodyFontSize,
                            color = if (isHighContrast) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Toggle Controls (High Contrast, Large Text, Farmer Voice Mode)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // High Contrast Button
                FilterChip(
                    selected = isHighContrast,
                    onClick = { viewModel.toggleHighContrast() },
                    label = {
                        Text(
                            text = if (isHighContrast) "👁️ కాంట్రాస్ట్: ఆన్" else "👁️ కాంట్రాస్ట్",
                            fontSize = if (isLargeText) 14.sp else 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFFF00),
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Large Text Button
                FilterChip(
                    selected = isLargeText,
                    onClick = { viewModel.toggleLargeText() },
                    label = {
                        Text(
                            text = if (isLargeText) "🔍 పెద్ద అక్షరాలు" else "🔍 టెక్స్ట్ సైజ్",
                            fontSize = if (isLargeText) 14.sp else 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF16A34A),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Illiterate Farmer Mode Toggle
                FilterChip(
                    selected = isIlliterateFarmerMode,
                    onClick = { viewModel.toggleIlliterateFarmerMode() },
                    label = {
                        Text(
                            text = if (isIlliterateFarmerMode) "🗣️ శ్రవణం: ఆన్" else "🗣️ బొమ్మలు & శ్రవణం",
                            fontSize = if (isLargeText) 14.sp else 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC2626),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Visual Symbol Cards for Low Literacy / Illiterate Farmers
            AnimatedVisibility(visible = isIlliterateFarmerMode) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Instruction Banner
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isHighContrast) Color(0xFF1F2937) else Color(0xFFFEF2F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTelugu) "రైతు సోదరులకు వాయిస్ సహాయం:" else "Audio Assistance for Farmers:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isLargeText) 16.sp else 13.sp,
                                    color = if (isHighContrast) Color(0xFFFFFF00) else Color(0xFF991B1B)
                                )
                                Text(
                                    text = if (isTelugu) "ఏ బొమ్మపై నొక్కినా తెలుగులో వినిపిస్తుంది. లేదా మైక్ నొక్కి మాట్లాడండి." else "Tap any icon to listen in Telugu voice. Or tap the mic to speak.",
                                    fontSize = if (isLargeText) 14.sp else 11.sp,
                                    color = if (isHighContrast) Color.White else Color(0xFF7F1D1D)
                                )
                            }

                            // Big Speak Mic Button
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDC2626),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (isTelugu) "te-IN" else "en-IN")
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, if (isTelugu) "ఏదైనా సమాచారం కొరకు మాట్లాడండి..." else "Speak what you want to know...")
                                        }
                                        try {
                                            speechRecognizerLauncher.launch(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Voice recognition unavailable", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Speak Now",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 2x3 Visual Tile Grid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IlliterateFarmerActionsRepository.actions.chunked(2).forEach { rowActions ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowActions.forEach { action ->
                                    val isSpeaking = currentlySpeakingId == action.id
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isHighContrast) Color(0xFF111827) else action.bgColor.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSpeaking) 3.dp else 1.5.dp,
                                            color = if (isSpeaking) Color(0xFF22C55E) else if (isHighContrast) Color(0xFFFFFF00) else action.bgColor
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                val textToSpeak = if (isTelugu) action.audioAdviceTe else action.audioAdviceEn
                                                if (isTelugu) {
                                                    ttsEngine?.language = Locale("te", "IN")
                                                } else {
                                                    ttsEngine?.language = Locale.ENGLISH
                                                }
                                                ttsEngine?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "ACC_SPEAK")
                                                currentlySpeakingId = action.id
                                                Toast.makeText(context, "🔊 ${action.titleTe}", Toast.LENGTH_SHORT).show()
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = action.iconEmoji, fontSize = if (isLargeText) 36.sp else 30.sp)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = if (isTelugu) action.titleTe else action.titleEn,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = if (isLargeText) 15.sp else 12.sp,
                                                textAlign = TextAlign.Center,
                                                color = if (isHighContrast) Color(0xFFFFFF00) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = action.bgColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = if (isSpeaking) "వినిపిస్తోంది..." else "వినండి (Speak)",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = action.bgColor
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
}
