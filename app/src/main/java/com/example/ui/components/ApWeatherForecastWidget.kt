package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ApRegionWeatherResponse
import com.example.data.api.MockApWeatherApiService
import kotlinx.coroutines.launch

@Composable
fun ApWeatherForecastWidget(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")
    var selectedRegionId by remember { mutableStateOf("guntur") }
    var weatherData by remember { mutableStateOf<ApRegionWeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showExtendedForecast by remember { mutableStateOf(false) }
    var showRegionMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Fetch weather data asynchronously from Mock Weather API
    fun loadWeather(regionId: String) {
        scope.launch {
            isLoading = true
            try {
                weatherData = MockApWeatherApiService.fetchWeatherForecast(regionId)
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedRegionId) {
        loadWeather(selectedRegionId)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ap_weather_dashboard_widget"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar: Location Inferred Region + Refresh Mock API Button
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
                            .size(36.dp)
                            .background(Color(0xFF0EA5E9).copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isTelugu) "ఆంధ్ర ప్రదేశ్ వాతావరణ నివేదిక" else "AP Live Weather & Rain Forecast",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Mock API Live",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Region Switcher Chip
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showRegionMenu = true }
                                    .testTag("ap_region_selector"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = weatherData?.let { if (isTelugu) it.districtNameTe else it.districtName } ?: "Guntur District",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Region",
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showRegionMenu,
                                onDismissRequest = { showRegionMenu = false }
                            ) {
                                MockApWeatherApiService.apRegions.forEach { (id, name) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = name,
                                                fontSize = 13.sp,
                                                fontWeight = if (selectedRegionId == id) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            selectedRegionId = id
                                            showRegionMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { loadWeather(selectedRegionId) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                        .testTag("weather_api_refresh_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF0284C7),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Weather",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            weatherData?.let { data ->
                // Primary Weather Hero Card: Temperature & Rain Probability Gauge
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = when {
                                        data.rainProbability >= 70 -> listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                                        data.rainProbability >= 40 -> listOf(Color(0xFF0EA5E9), Color(0xFF0284C7))
                                        else -> listOf(Color(0xFF0D9488), Color(0xFF0F766E))
                                    }
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "${data.currentTempC}°C",
                                            fontSize = 38.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Feels like ${data.feelsLikeC}°C",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                    }
                                    Text(
                                        text = data.weatherCondition,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }

                                // Rainfall Probability % Badge (Targeted Requirement)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White.copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                    modifier = Modifier.testTag("rain_prob_badge")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.WaterDrop,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "${data.rainProbability}%",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = if (isTelugu) "వర్ష సంభావ్యత" else "Rain Probability",
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }

                            // Agricultural Impact Banner
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.20f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isTelugu) data.apAgriAlertTe else data.apAgriAlertEn,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Microclimate Parameters Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherMetricPill(icon = Icons.Default.WaterDrop, label = if (isTelugu) "తేమ" else "Humidity", value = "${data.humidityPercent}%")
                    WeatherMetricPill(icon = Icons.Default.Air, label = if (isTelugu) "గాలి వేగం" else "Wind", value = "${data.windSpeedKmh} km/h")
                    WeatherMetricPill(icon = Icons.Default.WbSunny, label = "UV Index", value = data.uvIndex.split(" ").first())
                    WeatherMetricPill(icon = Icons.Default.Opacity, label = if (isTelugu) "మంచు చుక్క" else "Dew Point", value = "${data.dewPointC}°C")
                }

                Divider(color = Color(0xFFE2E8F0))

                // 24-Hour Hourly Weather & Rain Forecast (Scrollable)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTelugu) "24 గంటల వర్షం & ఉష్ణోగ్రత అంచనా" else "24-Hour Rain & Temp Forecast",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        TextButton(
                            onClick = { showExtendedForecast = !showExtendedForecast }
                        ) {
                            Text(
                                text = if (showExtendedForecast) (if (isTelugu) "సంక్షిప్తం" else "Hide 5-Day") else (if (isTelugu) "5 రోజుల నివేదిక" else "5-Day Forecast"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(data.hourlyForecast) { item ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.testTag("hourly_forecast_item")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = item.hour,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = item.condition.split(" ").firstOrNull() ?: "☀️",
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "${item.tempC}°C",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (item.rainProbability >= 50) Color(0xFF0284C7).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "💧 ${item.rainProbability}%",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.rainProbability >= 50) Color(0xFF0369A1) else Color(0xFF047857),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Collapsible 5-Day Extended Weather Forecast
                AnimatedVisibility(visible = showExtendedForecast) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isTelugu) "5 రోజుల విస్తృత వాతావరణ అంచనా & సాగు సూచనలు" else "5-Day Forecast & Crop Advisory",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        data.dailyForecast.forEach { daily ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${daily.dayName} (${daily.dateStr})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "• ${daily.condition}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                        Text(
                                            text = if (isTelugu) daily.agAdviceTe else daily.agAdviceEn,
                                            fontSize = 11.sp,
                                            color = Color(0xFF334155),
                                            lineHeight = 15.sp
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = "${daily.maxTempC}° / ${daily.minTempC}°C",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF0284C7).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "Rain: ${daily.rainProbability}%",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0369A1),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
private fun WeatherMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF64748B)
        )
    }
}
