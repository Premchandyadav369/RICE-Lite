package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

data class DailyPricePoint(
    val dayIndex: Int,
    val dateLabel: String,
    val price: Int, // in Rs. per quintal
    val volumeQuintals: Int
)

data class CropTrendData(
    val cropName: String,
    val cropNameTe: String,
    val primaryMandi: String,
    val history30Days: List<DailyPricePoint>
)

object CropPriceTrendRepository {
    private fun generate30DayHistory(basePrice: Int, volatility: Int, trendBias: Int): List<DailyPricePoint> {
        val list = mutableListOf<DailyPricePoint>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -29)
        val dateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

        var current = basePrice
        val random = Random(42L + basePrice)

        for (i in 0 until 30) {
            val dateStr = dateFormat.format(calendar.time)
            val delta = (random.nextInt(volatility * 2) - volatility) + trendBias
            current = (current + delta).coerceAtLeast(basePrice / 2)
            val volume = 1200 + random.nextInt(3500)
            list.add(DailyPricePoint(i + 1, dateStr, current, volume))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    val trends = listOf(
        CropTrendData(
            cropName = "Chilli (Red Mirchi)",
            cropNameTe = "ఎర్ర మిరపకాయలు",
            primaryMandi = "Guntur Mirchi Yard (AP)",
            history30Days = generate30DayHistory(18500, 350, 80)
        ),
        CropTrendData(
            cropName = "Cotton (Patti)",
            cropNameTe = "పత్తి",
            primaryMandi = "Warangal APMC (Telangana)",
            history30Days = generate30DayHistory(7100, 120, 25)
        ),
        CropTrendData(
            cropName = "Paddy (Nellore Sona)",
            cropNameTe = "వరి ధాన్యం",
            primaryMandi = "Miryalaguda Mandi (TS)",
            history30Days = generate30DayHistory(2200, 35, 8)
        ),
        CropTrendData(
            cropName = "Turmeric (Pasupu)",
            cropNameTe = "పసుపు",
            primaryMandi = "Nizamabad APMC (Telangana)",
            history30Days = generate30DayHistory(12800, 280, 70)
        ),
        CropTrendData(
            cropName = "Groundnut",
            cropNameTe = "వేరుశనగ",
            primaryMandi = "Adoni APMC (AP)",
            history30Days = generate30DayHistory(6500, 90, 15)
        ),
        CropTrendData(
            cropName = "Tomato",
            cropNameTe = "టమోటా",
            primaryMandi = "Madanapalle Market (AP)",
            history30Days = generate30DayHistory(1600, 110, 40)
        )
    )
}

@Composable
fun PriceTrendVisualizationCard(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    var selectedCropIndex by remember { mutableStateOf(0) }
    val cropData = CropPriceTrendRepository.trends[selectedCropIndex.coerceIn(CropPriceTrendRepository.trends.indices)]
    val history = cropData.history30Days

    val minPrice = remember(history) { history.minOf { it.price } }
    val maxPrice = remember(history) { history.maxOf { it.price } }
    val avgPrice = remember(history) { history.map { it.price }.average().roundToInt() }
    val startPrice = remember(history) { history.first().price }
    val latestPrice = remember(history) { history.last().price }
    val priceChangePercent = remember(history) {
        ((latestPrice - startPrice).toFloat() / startPrice * 100)
    }

    var selectedPointIndex by remember { mutableStateOf<Int?>(29) } // Default to latest day

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు"))
                                "30 రోజుల ధర విశ్లేషణ గ్రాఫ్" else "30-Day APMC Market Rate Fluctuations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Historical price trends across AP & Telangana Mandis",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (priceChangePercent >= 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (priceChangePercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (priceChangePercent >= 0) Color(0xFF16A34A) else Color(0xFFDC2626),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f%%", priceChangePercent),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (priceChangePercent >= 0) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Crop Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CropPriceTrendRepository.trends.size) { idx ->
                    val crop = CropPriceTrendRepository.trends[idx]
                    val isSelected = idx == selectedCropIndex
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF15803D) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable {
                            selectedCropIndex = idx
                            selectedPointIndex = 29
                        }
                    ) {
                        Text(
                            text = if (selectedLanguage.contains("Telugu")) crop.cropNameTe else crop.cropName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Mandi & Selected Point Detail
            val currentPoint = history[selectedPointIndex ?: 29]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📍 ${cropData.primaryMandi}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0284C7)
                    )
                    Text(
                        text = "Date: ${currentPoint.dateLabel} (Day ${currentPoint.dayIndex})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${currentPoint.price} / quintal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF15803D)
                    )
                    Text(
                        text = "Arrival: ${currentPoint.volumeQuintals} q",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Line Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(history) {
                            detectTapGestures { offset ->
                                val canvasWidth = size.width
                                val stepX = canvasWidth / (history.size - 1)
                                val tappedIndex = (offset.x / stepX).roundToInt().coerceIn(0, history.size - 1)
                                selectedPointIndex = tappedIndex
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val priceRange = (maxPrice - minPrice).coerceAtLeast(10)
                    val stepX = canvasWidth / (history.size - 1)

                    // Helper to get Canvas Offset for point
                    fun getPointOffset(index: Int, price: Int): Offset {
                        val x = index * stepX
                        val normalizedY = (price - minPrice).toFloat() / priceRange
                        val y = canvasHeight - (normalizedY * (canvasHeight - 30f)) - 15f
                        return Offset(x, y)
                    }

                    // 1. Horizontal Reference Grid Lines (Avg, Min, Max)
                    val avgY = canvasHeight - (((avgPrice - minPrice).toFloat() / priceRange) * (canvasHeight - 30f)) - 15f
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(0f, avgY),
                        end = Offset(canvasWidth, avgY),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )

                    // 2. Build Bezier Curve Path
                    val path = Path()
                    val backgroundPath = Path()

                    val points = history.mapIndexed { idx, item ->
                        getPointOffset(idx, item.price)
                    }

                    if (points.isNotEmpty()) {
                        path.moveTo(points[0].x, points[0].y)
                        backgroundPath.moveTo(points[0].x, canvasHeight)
                        backgroundPath.lineTo(points[0].x, points[0].y)

                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2, p1.y)
                            val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2, p2.y)
                            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                            backgroundPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                        }

                        backgroundPath.lineTo(points.last().x, canvasHeight)
                        backgroundPath.close()

                        // Draw Gradient Area under Line
                        drawPath(
                            path = backgroundPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF22C55E).copy(alpha = 0.4f),
                                    Color(0xFF22C55E).copy(alpha = 0.0f)
                                )
                            )
                        )

                        // Draw Trendline
                        drawPath(
                            path = path,
                            color = Color(0xFF22C55E),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // 3. Highlight Selected Point Indicator
                        selectedPointIndex?.let { selIdx ->
                            val selPoint = points[selIdx]
                            // Vertical Indicator Dash
                            drawLine(
                                color = Color.White.copy(alpha = 0.5f),
                                start = Offset(selPoint.x, 0f),
                                end = Offset(selPoint.x, canvasHeight),
                                strokeWidth = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                            )

                            // Outer Pulse Circle
                            drawCircle(
                                color = Color(0xFF22C55E).copy(alpha = 0.35f),
                                radius = 10.dp.toPx(),
                                center = selPoint
                            )
                            // Inner Solid Circle
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = selPoint
                            )
                            drawCircle(
                                color = Color(0xFF16A34A),
                                radius = 3.dp.toPx(),
                                center = selPoint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 30-Day Summary Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricMiniCard(
                    title = "30D High",
                    value = "₹$maxPrice",
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "30D Average",
                    value = "₹$avgPrice",
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "30D Low",
                    value = "₹$minPrice",
                    color = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
