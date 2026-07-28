package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KrishiViewModel
import com.example.ui.components.TeluguDocOcrScannerCard
import com.example.ui.util.LanguageUtils
import com.example.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndHistoryView(
    viewModel: KrishiViewModel,
    scannerViewModel: ScannerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(0) } // 0: User Profile & Settings, 1: Saved Scans, 2: Room Offline DB Cache

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Sub-Tab Row
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White
        ) {
            TabRow(
                selectedTabIndex = subTab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    text = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    text = { Text("Saved Scans", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 2,
                    onClick = { subTab = 2 },
                    text = { Text("Offline Manuals", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.CloudOff, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 3,
                    onClick = { subTab = 3 },
                    text = { Text("Offline Diseases", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (subTab) {
                0 -> ProfileSettingsSubView(viewModel = viewModel, scannerViewModel = scannerViewModel)
                1 -> HistoryScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                2 -> OfflineRepositoryView(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                3 -> OfflineDiseaseView(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ProfileSettingsSubView(
    viewModel: KrishiViewModel,
    scannerViewModel: ScannerViewModel
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val languages = LanguageUtils.SUPPORTED_LANGUAGES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Telugu Document OCR & Live User Profile Card
        TeluguDocOcrScannerCard(
            krishiViewModel = viewModel,
            scannerViewModel = scannerViewModel
        )

        // Accessibility Settings Card
        AccessibilitySettingsCard(viewModel = viewModel)

        // Seasonal Theme Switcher Card
        SeasonalThemeCard(viewModel = viewModel)

        // Language Preferences
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF14532D))
                        Text("App & AI Analysis Language", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF16A34A).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = LanguageUtils.getLanguageOption(selectedLanguage).flag + " " + selectedLanguage,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Switching language adapts app navigation titles and forces AI leaf scans & Mandi receipt analyses to respond in your preferred native script.",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )

                languages.forEach { langOpt ->
                    val fullLangName = "${langOpt.name} (${langOpt.nativeName})"
                    val isSelected = selectedLanguage.contains(langOpt.name, ignoreCase = true) ||
                            selectedLanguage.contains(langOpt.nativeName, ignoreCase = true) ||
                            selectedLanguage.equals(langOpt.code, ignoreCase = true)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFDCFCE7) else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF16A34A) else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setLanguage(fullLangName) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(langOpt.flag, fontSize = 18.sp)
                                Column {
                                    Text(langOpt.nativeName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                                    Text(langOpt.name, fontSize = 11.sp, color = Color(0xFF6B7280))
                                }
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setLanguage(fullLangName) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF16A34A))
                            )
                        }
                    }
                }
            }
        }

        // App Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("RICE AgriTech Super App", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Version 3.2.0 • Powered by Gemma AI & Room DB", fontSize = 12.sp, color = Color(0xFF6B7280))
                Text("Revolution in Cultivating Excellence", fontSize = 11.sp, color = Color(0xFF14532D), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SeasonalThemeCard(viewModel: KrishiViewModel) {
    val currentSeason by viewModel.currentAgriSeason.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("AP & TS Agricultural Season Theme", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${currentSeason.icon} ${currentSeason.name}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Dynamic seasonal color palette reflecting active farming cycles in Andhra Pradesh & Telangana.",
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )

            com.example.ui.theme.AgriSeason.values().forEach { season ->
                val isSelected = currentSeason == season

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setAgriSeason(season) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(season.icon, fontSize = 20.sp)
                            Column {
                                Text(season.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                                Text(season.titleTe, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Text(season.months, fontSize = 11.sp, color = Color(0xFF6B7280))
                            }
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setAgriSeason(season) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccessibilitySettingsCard(viewModel: KrishiViewModel) {
    val isHighContrast by viewModel.isHighContrast.collectAsState()
    val isLargeText by viewModel.isLargeText.collectAsState()
    val isIlliterateFarmerMode by viewModel.isIlliterateFarmerMode.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AccessibilityNew, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Accessibility & Illiterate Farmer View", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Text(
                text = "Tailor the interface for low vision, illiterate, or senior farmers with speech output in local languages.",
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )

            // High Contrast Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("High Contrast Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Enhances visual outlines and text readability", fontSize = 11.sp, color = Color(0xFF6B7280))
                }
                Switch(
                    checked = isHighContrast,
                    onCheckedChange = { viewModel.toggleHighContrast() }
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Large Text Scale Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Large Text Size", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Increases font size across all screens", fontSize = 11.sp, color = Color(0xFF6B7280))
                }
                Switch(
                    checked = isLargeText,
                    onCheckedChange = { viewModel.toggleLargeText() }
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Illiterate Farmer Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Illiterate Farmer Voice Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("🔊", fontSize = 12.sp)
                    }
                    Text("Large audio action cards & local speech assistant in Telugu, Urdu, & English", fontSize = 11.sp, color = Color(0xFF6B7280))
                }
                Switch(
                    checked = isIlliterateFarmerMode,
                    onCheckedChange = { viewModel.toggleIlliterateFarmerMode() }
                )
            }
        }
    }
}
