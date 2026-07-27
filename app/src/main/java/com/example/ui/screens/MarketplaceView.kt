package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KrishiViewModel

data class MarketplaceProduct(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val rating: String,
    val vendor: String,
    val isSubsidyEligible: Boolean = false
)

data class BuyerBid(
    val buyerName: String,
    val location: String,
    val offeredPrice: String,
    val quantityNeeded: String,
    val rating: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceView(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(0) } // 0: Buy Inputs & Machinery, 1: Sell Crop & Bids, 2: Cold Storage & Transport
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Seeds", "Fertilizers", "Pesticides", "Machinery", "Cold Storage")

    val sampleProducts = remember {
        listOf(
            MarketplaceProduct("1", "Hybrid Paddy Seed Pusa 1121", "Seeds", "₹850 / 10kg", "4.9 ★", "National Seeds Corp", true),
            MarketplaceProduct("2", "NPK 19:19:19 Water Soluble", "Fertilizers", "₹1,250 / 25kg", "4.8 ★", "IFFCO Agri", true),
            MarketplaceProduct("3", "Neem Oil 10,000 PPM Bio-Pesticide", "Pesticides", "₹480 / 1 Liter", "4.7 ★", "Organic Krishi", false),
            MarketplaceProduct("4", "Mahindra 575 DI Tractor (Rent/Day)", "Machinery", "₹1,800 / day", "4.9 ★", "Local AgriRent", true),
            MarketplaceProduct("5", "Laser Land Leveler Equipment", "Machinery", "₹2,200 / day", "5.0 ★", "Punjab Farm Tools", false),
            MarketplaceProduct("6", "Trichoderma Viride Bio-Fungicide", "Pesticides", "₹210 / 1kg", "4.6 ★", "Biotech Agri", true)
        )
    }

    val buyerBids = remember {
        listOf(
            BuyerBid("AgroCorp Exporters Ltd", "Ambala Mandi • 12 km", "₹2,320 / qtl", "250 Quintals", "4.9 ★"),
            BuyerBid("Punjab Food Processing Co", "Ludhiana • 24 km", "₹2,300 / qtl", "100 Quintals", "4.8 ★"),
            BuyerBid("GreenGrain Traders", "Jalandhar • 35 km", "₹2,290 / qtl", "500 Quintals", "4.7 ★")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Top Navigation Sub-Tabs ---
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
                    text = { Text("Buy Inputs", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    text = { Text("Sell Crops", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = subTab == 2,
                    onClick = { subTab = 2 },
                    text = { Text("Logistics", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        when (subTab) {
            0 -> {
                // BUY MARKETPLACE
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search seeds, fertilizers, tractor rental...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Category Filter Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Products Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val filtered = sampleProducts.filter {
                            (selectedCategory == "All" || it.category == selectedCategory) &&
                                    (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true))
                        }
                        items(filtered, key = { it.id }) { product ->
                            ProductCard(product = product)
                        }
                    }
                }
            }

            1 -> {
                // SELLER PORTAL & AI GRADING
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // AI Crop Grading Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF059669))
                                Text("AI Crop Quality Grading & Price Estimator", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF065F46))
                            }
                            Text(
                                text = "Scan or upload grain sample photos to calculate moisture level, grain length, purity %, and instant market grade (Grade A+).",
                                fontSize = 12.sp,
                                color = Color(0xFF047857)
                            )
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Grain Sample for AI Grade", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text("🏢 Direct Buyer Bids & Verified Procurement", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    buyerBids.forEach { bid ->
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
                                        Text(bid.buyerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(bid.location, fontSize = 12.sp, color = Color(0xFF6B7280))
                                    }
                                    Text(bid.offeredPrice, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF14532D))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFF3F4F6)
                                    ) {
                                        Text("Requirement: ${bid.quantityNeeded}", fontSize = 11.sp, modifier = Modifier.padding(6.dp))
                                    }

                                    Button(
                                        onClick = {},
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Accept Bid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // COLD STORAGE & TRANSPORT LOGISTICS
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0EA5E9))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AcUnit, contentDescription = null, tint = Color(0xFF0284C7))
                                Text("Nearby Cold Storage & Warehouse Booking", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0369A1))
                            }
                            Text(
                                text = "Reserve humidity-controlled storage space for potato, chilli, and fruits to avoid distress sales.",
                                fontSize = 12.sp,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }

                    Text("🚚 Book Verified Farm Logistics", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("10-Ton Eicher Truck (GPS Tracked)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹35 / km", fontWeight = FontWeight.Bold, color = Color(0xFF0EA5E9))
                            }
                            Text("Driver: Gurpreet Singh • Pickup ETA: 45 Mins", fontSize = 12.sp, color = Color(0xFF6B7280))
                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Schedule Transport Pickup")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: MarketplaceProduct) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Text(product.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Text(product.rating, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
            }

            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2)
            Text(product.vendor, fontSize = 10.sp, color = Color(0xFF6B7280))
            Text(product.price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF14532D))

            if (product.isSubsidyEligible) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text("Govt Subsidy 50%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding( horizontal = 4.dp, vertical = 2.dp))
                }
            }

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D))
            ) {
                Text("Add to Cart", fontSize = 11.sp)
            }
        }
    }
}
