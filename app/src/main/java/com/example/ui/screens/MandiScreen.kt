package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MandiPrice
import com.example.data.MandiPriceProvider
import com.example.ui.KrishiViewModel

@Composable
fun MandiScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStateFilter by remember { mutableStateOf("All States") }

    var livePrices by remember { mutableStateOf(MandiPriceProvider.samplePrices) }
    var isLiveTickerActive by remember { mutableStateOf(true) }
    var lastUpdatedCropName by remember { mutableStateOf("") }
    var lastUpdatedChangeAmount by remember { mutableStateOf(0) }

    // Live real-time market fluctuations simulator
    LaunchedEffect(isLiveTickerActive) {
        if (isLiveTickerActive) {
            while (true) {
                delay(3000)
                if (livePrices.isNotEmpty()) {
                    val randomIndex = (livePrices.indices).random()
                    val priceItem = livePrices[randomIndex]
                    val change = listOf(-40, -20, 30, 50, 75, -15, 25, 45).random()
                    
                    val updatedMin = (priceItem.minPrice + change / 2).coerceAtLeast(600)
                    val updatedMax = (priceItem.maxPrice + change).coerceAtLeast(updatedMin + 200)
                    val updatedModal = (priceItem.modalPrice + change).coerceIn(updatedMin, updatedMax)
                    
                    livePrices = livePrices.toMutableList().apply {
                        this[randomIndex] = priceItem.copy(
                            minPrice = updatedMin,
                            maxPrice = updatedMax,
                            modalPrice = updatedModal,
                            trend = if (change >= 0) "UP" else "DOWN",
                            date = "Live Now"
                        )
                    }
                    lastUpdatedCropName = priceItem.getLocalizedCropName(selectedLanguage)
                    lastUpdatedChangeAmount = change
                }
            }
        }
    }

    val allStates = remember(livePrices) {
        listOf("All States") + livePrices.map { it.state }.distinct().sorted()
    }

    val filteredPrices = remember(livePrices, searchQuery, selectedStateFilter, selectedLanguage) {
        livePrices.filter { price ->
            val matchesState = selectedStateFilter == "All States" || price.state == selectedStateFilter
            val localizedName = price.getLocalizedCropName(selectedLanguage)
            val matchesSearch = price.cropName.contains(searchQuery, ignoreCase = true) ||
                    price.cropNameHi.contains(searchQuery, ignoreCase = true) ||
                    price.cropNameTe.contains(searchQuery, ignoreCase = true) ||
                    price.marketName.contains(searchQuery, ignoreCase = true) ||
                    localizedName.contains(searchQuery, ignoreCase = true)
            matchesState && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (selectedLanguage == "Hindi (हिन्दी)") "लाइव मंडी भाव" else "Live Mandi Prices",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (selectedLanguage == "Hindi (हिन्दी)") "दैनिक APMC मंडी दर प्रति क्विंटल (100 kg)" else "Daily APMC Market rates per quintal (100 kg)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            // Real-Time Ticker Toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isLiveTickerActive) Color(0xFFE8F5E9) else Color(0xFFECEFF1)
                    )
                    .clickable { isLiveTickerActive = !isLiveTickerActive }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isLiveTickerActive) Color(0xFF2E7D32) else Color(0xFF78909C))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLiveTickerActive) "● LIVE TICKER" else "PAUSED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLiveTickerActive) Color(0xFF1B5E20) else Color(0xFF37474F)
                    )
                }
            }
        }

        // Ticker live update flash notification
        if (isLiveTickerActive && lastUpdatedCropName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (lastUpdatedChangeAmount >= 0) Color(0xFFE8F5E9).copy(alpha = 0.6f) else Color(0xFFFFEBEE).copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ Real-Time Update: $lastUpdatedCropName " +
                                (if (lastUpdatedChangeAmount >= 0) "rose by +₹$lastUpdatedChangeAmount" else "dropped by -₹${kotlin.math.abs(lastUpdatedChangeAmount)}") + " in APMC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lastUpdatedChangeAmount >= 0) Color(0xFF2E7D32) else Color(0xFFC2185B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // NATIONAL MANDI STATS INDEX ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Overall Index
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Mandi Sentiment",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📈 Bullish (+2.4%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            // Gainer
            Card(
                modifier = Modifier.weight(1.2f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Top Gainer",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tomato 🍅 +14% / q",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            // APMCs online
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "APMCs Active",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🏢 14 Online",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // INTERACTIVE MANDI ESTIMATE CALCULATOR CARD
        var showCalculator by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (showCalculator) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedLanguage == "Hindi (हिन्दी)") "📊 मंडी आय कैलकुलेटर" else "📊 Mandi Income Calculator",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { showCalculator = !showCalculator },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (showCalculator) Icons.Default.Close else Icons.Default.Info,
                            contentDescription = "Toggle Calculator",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (showCalculator) {
                    Spacer(modifier = Modifier.height(8.dp))
                    var calcCrop by remember { mutableStateOf("Wheat") }
                    var calcQuantity by remember { mutableStateOf("50") } // in Quintals
                    var isDropdownExpanded by remember { mutableStateOf(false) }
                    var isOrganicPremium by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Crop Dropdown
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable { isDropdownExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = calcCrop,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                val uniqueCrops = listOf("Wheat", "Paddy (Rice)", "Cotton", "Onion", "Potato", "Tomato")
                                uniqueCrops.forEach { crop ->
                                    DropdownMenuItem(
                                        text = { Text(crop, fontSize = 12.sp) },
                                        onClick = {
                                            calcCrop = crop
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Quantity input
                        OutlinedTextField(
                            value = calcQuantity,
                            onValueChange = { calcQuantity = it.filter { char -> char.isDigit() } },
                            placeholder = { Text("50") },
                            suffix = { Text("q", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Organic Premium Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isOrganicPremium) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .clickable { isOrganicPremium = !isOrganicPremium }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = if (isOrganicPremium) Color(0xFF2E7D32) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "🌱 जैविक उपज प्रीमियम (+35% भाव)" else "🌱 Organic Yield Premium (+35%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOrganicPremium) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "सर्टिफाइड जैविक फसलों पर 35% अतिरिक्त भाव जोड़ें" else "Estimate premium earnings for certified organic crops",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Icon(
                            imageVector = if (isOrganicPremium) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isOrganicPremium) Color(0xFF2E7D32) else Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val quantityNum = calcQuantity.toDoubleOrNull() ?: 0.0
                    val cropPriceMatch = remember(calcCrop, livePrices) {
                        livePrices.firstOrNull { it.cropName.contains(calcCrop, ignoreCase = true) }
                            ?: livePrices.firstOrNull()
                    }

                    if (cropPriceMatch != null) {
                        val minRate = cropPriceMatch.minPrice
                        val modalRate = cropPriceMatch.modalPrice
                        val maxRate = cropPriceMatch.maxPrice

                        val premiumMultiplier = if (isOrganicPremium) 1.35 else 1.0
                        val totalGross = modalRate * quantityNum * premiumMultiplier
                        val totalMin = minRate * quantityNum * premiumMultiplier
                        val totalMax = maxRate * quantityNum * premiumMultiplier
                        
                        val mandiFee = totalGross * 0.02 // 2% fee
                        val netEarnings = totalGross - mandiFee

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "कुल अनुमानित आय" else "Projected Payout (Average)",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹${String.format("%,.0f", totalGross)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "मंडी/परिवहन शुल्क (2%)" else "Mandi Fees (2%)",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹${String.format("%,.0f", mandiFee)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedLanguage == "Hindi (हिन्दी)") "💰 शुद्ध टेक-होम आय:" else "💰 Net Take-Home Earnings:",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "₹${String.format("%,.0f", netEarnings)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        if (isOrganicPremium) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .border(0.5.dp, Color(0xFF2E7D32).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (selectedLanguage == "Hindi (हिन्दी)") "💡 जैविक प्रीमियम कैसे प्राप्त करें:" else "💡 How to Secure Organic Premiums:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = if (selectedLanguage == "Hindi (हिन्दी)") {
                                            "1. APEDA जैविक प्रमाणीकरण (NPOP) प्राप्त करें।\n2. स्थानीय जैविक एफपीओ (FPO) या सीधे निर्यातकों के माध्यम से बेचें।\n3. खुदरा बाजारों में अपनी फसल पर '100% जैविक' लेबल का उपयोग करें।"
                                        } else {
                                            "1. Obtain APEDA organic certification (under NPOP scheme).\n2. Sell through local organic FPOs or direct-to-retail boutique buyers.\n3. Package with clear '100% Organic' labeling in specialty mandis."
                                        },
                                        fontSize = 10.sp,
                                        color = Color(0xFF2E7D32),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = if (selectedLanguage == "Hindi (हिन्दी)") "अपनी उपज दर्ज करें और लाइव दरों के आधार पर आय का तुरंत आकलन करें।" else "Enter crop quantity to quickly estimate your gross revenue and net profit.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search crop (Onion, పత్తి, गेहूं...) or market...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mandi_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // State filter list
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allStates) { stateName ->
                val isSelected = stateName == selectedStateFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .clickable { selectedStateFilter = stateName }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stateName,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of prices
        if (filteredPrices.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No matching market prices found.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPrices) { mandiPrice ->
                    MandiItemCard(mandiPrice = mandiPrice, language = selectedLanguage)
                }
            }
        }
    }
}

@Composable
fun MandiItemCard(
    mandiPrice: MandiPrice,
    language: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .border(1.dp, if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Crop Name & Trend Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = mandiPrice.getLocalizedCropName(language),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (language != "English") {
                        Text(
                            text = mandiPrice.cropName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                // Trend badge
                TrendBadge(trend = mandiPrice.trend)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price Details: Min - Max - Modal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriceField(label = if (language == "Hindi (हिन्दी)") "न्यूनतम भाव" else "Min Price", value = "₹${mandiPrice.minPrice}")
                PriceField(label = if (language == "Hindi (हिन्दी)") "मॉडल भाव" else "Model Rate", value = "₹${mandiPrice.modalPrice}", isBold = true)
                PriceField(label = if (language == "Hindi (हिन्दी)") "अधिकतम भाव" else "Max Price", value = "₹${mandiPrice.maxPrice}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: APMC Market and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${mandiPrice.marketName}, ${mandiPrice.state}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (mandiPrice.date == "Live Now") "🔴 Live" else mandiPrice.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (mandiPrice.date == "Live Now") Color(0xFFC62828) else Color.Gray,
                    fontWeight = if (mandiPrice.date == "Live Now") FontWeight.Bold else FontWeight.Normal
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    MandiTrendChart(cropName = mandiPrice.cropName, language = language)
                }
            }
        }
    }
}

@Composable
fun TrendBadge(trend: String) {
    val color = when (trend) {
        "UP" -> Color(0xFF2E7D32)
        "DOWN" -> Color(0xFFC62828)
        else -> Color(0xFF555555)
    }
    val text = when (trend) {
        "UP" -> "UP"
        "DOWN" -> "DOWN"
        else -> "STABLE"
    }
    val icon = when (trend) {
        "UP" -> Icons.Default.TrendingUp
        "DOWN" -> Icons.Default.TrendingDown
        else -> Icons.Default.TrendingFlat
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun PriceField(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = if (isBold) 16.sp else 14.sp,
            color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MandiTrendChart(cropName: String, language: String) {
    val pricePoints = remember(cropName) {
        if (cropName.contains("Onion", ignoreCase = true)) {
            listOf(1400f, 1650f, 1820f, 2100f, 1950f, 2180f)
        } else if (cropName.contains("Potato", ignoreCase = true)) {
            listOf(1100f, 1250f, 1420f, 1300f, 1480f, 1550f)
        } else if (cropName.contains("Wheat", ignoreCase = true)) {
            listOf(2100f, 2220f, 2180f, 2350f, 2400f, 2450f)
        } else if (cropName.contains("Paddy", ignoreCase = true) || cropName.contains("Rice", ignoreCase = true)) {
            listOf(1900f, 2050f, 2120f, 2240f, 2180f, 2300f)
        } else if (cropName.contains("Cotton", ignoreCase = true)) {
            listOf(6800f, 7200f, 7400f, 7150f, 7300f, 7550f)
        } else if (cropName.contains("Tomato", ignoreCase = true)) {
            listOf(1300f, 1500f, 2400f, 2800f, 2100f, 2600f)
        } else {
            listOf(2200f, 2400f, 2150f, 2380f, 2500f, 2420f)
        }
    }
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(
            text = if (language == "Hindi (हिन्दी)") "📈 6-महीने का मॉडल भाव रुझान (₹/क्विंटल)" else "📈 6-Month Modal Price Trend (₹/quintal)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.03f), RoundedCornerShape(10.dp))
                .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val width = size.width
            val height = size.height
            val maxVal = pricePoints.maxOrNull() ?: 1000f
            val minVal = pricePoints.minOrNull() ?: 0f
            val range = (maxVal - minVal).coerceAtLeast(1f)

            val points = pricePoints.mapIndexed { index, value ->
                val x = (width / (pricePoints.size - 1)) * index
                val y = height - ((value - minVal) / range) * (height * 0.7f) - (height * 0.15f)
                androidx.compose.ui.geometry.Offset(x, y)
            }

            // Draw connecting lines with primary green theme
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 5f
                )
            }

            // Draw point anchors
            points.forEach { point ->
                drawCircle(
                    color = androidx.compose.ui.graphics.Color(0xFF1B5E20),
                    radius = 10f,
                    center = point
                )
                drawCircle(
                    color = androidx.compose.ui.graphics.Color.White,
                    radius = 5f,
                    center = point
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Month Labels Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            months.forEachIndexed { index, m ->
                Text(
                    text = "$m (₹${pricePoints[index].toInt()})",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
