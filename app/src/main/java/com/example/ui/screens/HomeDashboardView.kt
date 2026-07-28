package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KrishiViewModel
import com.example.ui.components.RegionalWeatherAdvisorCard
import com.example.ui.components.SeasonalThemeCard
import com.example.ui.components.AccessibilityViewCard
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardView(
    viewModel: KrishiViewModel,
    onNavigateToTab: (Int) -> Unit,
    onOpenAiScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Regional Weather & AP/TS Weather-Linked Crop Advisory with Voice ---
        RegionalWeatherAdvisorCard(
            selectedLanguage = selectedLanguage
        )

        // --- Accessibility & Farmer Voice Read Aloud Card ---
        AccessibilityViewCard(
            viewModel = viewModel
        )

        // --- Dynamic AP & Telangana Agricultural Seasonal Theme Card ---
        SeasonalThemeCard(
            viewModel = viewModel
        )
        // --- 1. User Header & Greeting Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF14532D), Color(0xFF0F3E21))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF22C55E),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Avatar",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Namaste, Farmer Rajesh 👋",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Green Valley Organic Farm • 12.5 Acres",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = { onNavigateToTab(4) }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Satellite AI Health Badge Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f))
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SatelliteAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Sentinel Satellite NDVI: 0.82 (Excellent)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Live Sync",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22C55E)
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Live Weather & Rain Forecast Card ---
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Ludhiana, Punjab • 28°C",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = "Partly Cloudy • Rain Probability: 12%",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0EA5E9).copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Good Irrigation Day",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0EA5E9),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Divider(color = Color(0xFFF3F4F6))

                // Weather Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherMetricItem(icon = Icons.Default.WaterDrop, label = "Humidity", value = "68%")
                    WeatherMetricItem(icon = Icons.Default.Air, label = "Wind Speed", value = "12 km/h")
                    WeatherMetricItem(icon = Icons.Default.WbSunny, label = "UV Index", value = "5.8 (Mod)")
                    WeatherMetricItem(icon = Icons.Default.Opacity, label = "Dew Point", value = "19°C")
                }
            }
        }

        // --- 3. Quick Action Grid Buttons ---
        Text(
            text = "⚡ Quick Smart Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF111827)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                icon = Icons.Default.CameraAlt,
                title = "AI Disease Scan",
                subtitle = "Camera Diagnosis",
                containerColor = Color(0xFF22C55E),
                contentColor = Color.White,
                modifier = Modifier.weight(1f),
                onClick = onOpenAiScan
            )
            QuickActionButton(
                icon = Icons.Default.Storefront,
                title = "Sell Produce",
                subtitle = "Instant Mandi Bids",
                containerColor = Color(0xFFF59E0B),
                contentColor = Color.White,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(2) }
            )
            QuickActionButton(
                icon = Icons.Default.AccountBalanceWallet,
                title = "Micro-Loan",
                subtitle = "Apply in 2 Mins",
                containerColor = Color(0xFF0EA5E9),
                contentColor = Color.White,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(3) }
            )
        }

        // --- 4. Smart Irrigation & Soil Moisture Control Card ---
        Text(
            text = "💧 Smart Irrigation & Soil Moisture",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF111827)
        )

        HomeIrrigationSchedulerCard(
            onOpenFullScheduler = { onNavigateToTab(1) }
        )

        // --- 5. Market Commodity Price Ticker ---
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF10B981))
                        Text("Today's Mandi Market Prices", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    TextButton(onClick = { onNavigateToTab(2) }) {
                        Text("View All Markets →", fontSize = 12.sp, color = Color(0xFF14532D))
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { MarketPriceItem(crop = "Paddy (Rice)", price = "₹2,280 / qtl", change = "+₹45", isUp = true) }
                    item { MarketPriceItem(crop = "Wheat (Sharbati)", price = "₹2,150 / qtl", change = "+₹20", isUp = true) }
                    item { MarketPriceItem(crop = "Cotton (Long)", price = "₹7,100 / qtl", change = "-₹30", isUp = false) }
                    item { MarketPriceItem(crop = "Potato (Jyoti)", price = "₹1,420 / qtl", change = "+₹15", isUp = true) }
                }
            }
        }

        // --- 6. AI Agronomist Actionable Recommendation Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(28.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Gemma AI Crop Advisory",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = "Nitrogen level is 14% lower in Sector C. Apply 15kg Urea per acre before expected light showers tomorrow to maximize tillering.",
                        fontSize = 12.sp,
                        color = Color(0xFF047857)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(20.dp))
        Text(text = label, fontSize = 10.sp, color = Color(0xFF6B7280))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = contentColor, modifier = Modifier.size(22.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = contentColor)
            Text(text = subtitle, fontSize = 10.sp, color = contentColor.copy(alpha = 0.85f))
        }
    }
}

@Composable
fun MarketPriceItem(
    crop: String,
    price: String,
    change: String,
    isUp: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = crop, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
            Text(text = price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Text(
                text = change,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUp) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }
    }
}

@Composable
fun HomeIrrigationSchedulerCard(
    onOpenFullScheduler: () -> Unit
) {
    val context = LocalContext.current
    var isPumpActive by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with Soil Moisture gauge & Full Scheduler Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0EA5E9).copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(20.dp))
                        }
                    }
                    Column {
                        Text("Current Soil Moisture", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Text("28% Vol (Deficit)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    }
                }

                TextButton(onClick = onOpenFullScheduler) {
                    Text("Full Planner →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                }
            }

            // Moisture Progress Indicator
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = 0.28f,
                    color = Color(0xFF0284C7),
                    trackColor = Color(0xFFE0F2FE),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Wilting (15%)", fontSize = 9.sp, color = Color(0xFFEF4444))
                    Text("Target: 35% FC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    Text("Saturation (100%)", fontSize = 9.sp, color = Color(0xFF6B7280))
                }
            }

            Divider(color = Color(0xFFF3F4F6))

            // Next Scheduled Event Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0F9FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                            Text("NEXT SCHEDULED EVENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                        }
                        Text("Paddy (Rice) • Field A Drip", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                        Text("Today at 05:30 AM • 25,000 Liters (6.2 mm)", fontSize = 11.sp, color = Color(0xFF4B5563))
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFD97706).copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Due Soon",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Quick-Run Button & Live Pump Status
            if (isPumpActive) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFDCFCE7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Waves, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(20.dp))
                            Column {
                                Text("Pump Active • 15-Min Quick Run", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                                Text("Watering Paddy Field A (Flow: 160 L/min)", fontSize = 10.sp, color = Color(0xFF15803D))
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                isPumpActive = false
                                Toast.makeText(context, "Quick-Run Pump Stopped manually.", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        isPumpActive = true
                        Toast.makeText(context, "⚡ Quick-Run Started: Watering Paddy Field A for 15 mins!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("⚡ Quick-Run Pump (15 Mins)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
