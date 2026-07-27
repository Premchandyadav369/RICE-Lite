package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
fun FinanceDairyView(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(0) } // 0: Agri Finance & Loans, 1: Dairy & Livestock

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Sub-Tab Switcher
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
                    text = { Text("Finance & Loans", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    text = { Text("Dairy & Livestock", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        when (subTab) {
            0 -> {
                // AGRI FINANCE, WALLET & LOANS
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Wallet Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("RICE Agri Pay Wallet", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                                    Text("₹42,500.00", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF22C55E).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                                ) {
                                    Text("KCC Credit Score 780 (Excellent)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E), modifier = Modifier.padding(8.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Money", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {},
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Scan UPI", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Micro-Loan & Kisan Credit Card (KCC) Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF0EA5E9))
                                Text("Pre-Approved KCC Kisan Credit Line", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Text("Instant low-interest credit line @ 4% p.a. for seed purchase & harvest operations.", fontSize = 12.sp, color = Color(0xFF6B7280))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Limit Approved", fontSize = 10.sp, color = Color(0xFF6B7280))
                                    Text("₹1,50,000", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                                }
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Disburse to Bank Now")
                                }
                            }
                        }
                    }

                    // PMFBY Crop Insurance & Government Schemes
                    Text("🏛️ Govt Schemes & Crop Insurance (PMFBY)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PM-KISAN Samman Nidhi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDCFCE7)) {
                                    Text("Active • 16th Installment Credited", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(6.dp))
                                }
                            }
                            Text("Annual ₹6,000 direct benefit transfer status verified.", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PM Kusum Solar Pump Subsidy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEF3C7)) {
                                    Text("60% Subsidy Eligible", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(6.dp))
                                }
                            }
                            Text("Solar pump installation application ready for online submission.", fontSize = 12.sp, color = Color(0xFF6B7280))
                        }
                    }
                }
            }

            1 -> {
                // DAIRY & LIVESTOCK MANAGEMENT
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Daily Milk Yield Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White)
                            }
                            Column {
                                Text("Today's Milk Collection", fontSize = 12.sp, color = Color(0xFFB45309))
                                Text("48.5 Liters (Fat: 4.2% • SNF: 8.8%)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                Text("Estimated Daily Income: ₹2,180", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFB45309))
                            }
                        }
                    }

                    Text("🐄 Cattle Profiles & Health Reminders", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    // Cattle 1
                    CattleProfileCard(
                        tagId = "RFID #IN-8892",
                        breed = "Sahiwal Cow (Gauri)",
                        yield = "16.2 Liters/day",
                        healthStatus = "Vaccination Due in 4 Days (FMD)",
                        isHealthy = true
                    )

                    // Cattle 2
                    CattleProfileCard(
                        tagId = "RFID #IN-9014",
                        breed = "Murrah Buffalo (Lakshmi)",
                        yield = "18.5 Liters/day",
                        healthStatus = "Deworming Completed • Healthy",
                        isHealthy = true
                    )
                }
            }
        }
    }
}

@Composable
fun CattleProfileCard(
    tagId: String,
    breed: String,
    yield: String,
    healthStatus: String,
    isHealthy: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(tagId, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                    Text(breed, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF22C55E).copy(alpha = 0.15f)
                ) {
                    Text(yield, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(6.dp))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(16.dp))
                Text(healthStatus, fontSize = 12.sp, color = Color(0xFF374151))
            }
        }
    }
}
