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
import com.example.ui.util.LanguageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndHistoryView(
    viewModel: KrishiViewModel,
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
                    text = { Text("Offline DB Cache", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (subTab) {
                0 -> ProfileSettingsSubView(viewModel = viewModel)
                1 -> HistoryScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                2 -> OfflineDiseaseView(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ProfileSettingsSubView(viewModel: KrishiViewModel) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val languages = LanguageUtils.SUPPORTED_LANGUAGES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Card
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF14532D),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("R", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Column {
                        Text("Farmer Rajesh Kumar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("+91 98765 43210 • PM-KISAN Verified", fontSize = 12.sp, color = Color(0xFF6B7280))
                    }
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Soil Health Card", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Text("SHC-2026-PB-991", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Aadhaar KYC", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Text("Verified ✓", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                }
            }
        }

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
