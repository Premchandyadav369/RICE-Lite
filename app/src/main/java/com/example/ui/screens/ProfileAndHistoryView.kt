package com.example.ui.screens

import androidx.compose.foundation.background
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
    val languages = listOf("English", "हिन्दी (Hindi)", "తెలుగు (Telugu)", "தமிழ் (Tamil)")

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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF14532D))
                    Text("App Regional Language", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                languages.forEach { lang ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(lang, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        RadioButton(
                            selected = selectedLanguage.startsWith(lang.take(5)),
                            onClick = { viewModel.setLanguage(lang) }
                        )
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
