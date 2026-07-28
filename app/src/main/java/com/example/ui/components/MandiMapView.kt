package com.example.ui.components

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

data class ApTsMandiLocation(
    val name: String,
    val state: String,
    val lat: Double,
    val lng: Double,
    val mainCrop: String,
    val livePriceQuintal: String
)

object ApTsMandiMapData {
    val mandis = listOf(
        ApTsMandiLocation("Guntur Mirchi Yard", "Andhra Pradesh", 16.3067, 80.4365, "Red Chilli Mirchi", "₹21,200/q"),
        ApTsMandiLocation("Warangal APMC", "Telangana", 17.9784, 79.5941, "Cotton (Patti)", "₹7,900/q"),
        ApTsMandiLocation("Nizamabad APMC", "Telangana", 18.6725, 78.0941, "Turmeric (Pasupu)", "₹14,900/q"),
        ApTsMandiLocation("Kurnool Market", "Andhra Pradesh", 15.8281, 78.0373, "Onion & Groundnut", "₹2,150/q"),
        ApTsMandiLocation("Miryalaguda Mandi", "Telangana", 16.8667, 79.5667, "Paddy (Nellore Sona)", "₹2,450/q"),
        ApTsMandiLocation("Khammam Market", "Telangana", 17.2473, 80.1514, "Maize (Corn)", "₹2,220/q"),
        ApTsMandiLocation("Vijayawada Rythu Bazar", "Andhra Pradesh", 16.5062, 80.6480, "Mango (Banganapalli)", "₹4,800/q"),
        ApTsMandiLocation("Adoni APMC", "Andhra Pradesh", 15.6322, 77.2728, "Groundnut & Cotton", "₹7,100/q"),
        ApTsMandiLocation("Madanapalle Market", "Andhra Pradesh", 13.5504, 78.5027, "Tomato", "₹2,900/q"),
        ApTsMandiLocation("Eluru APMC", "Andhra Pradesh", 16.7107, 81.1042, "Sugarcane", "₹3,350/q")
    )
}

class MandiMapJsBridge(private val onMandiSelected: (String) -> Unit) {
    @JavascriptInterface
    fun onMandiClick(mandiName: String) {
        onMandiSelected(mandiName)
    }
}

@Composable
fun MandiOpenStreetMapCard(
    selectedLanguage: String,
    onMandiClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMandiName by remember { mutableStateOf("Guntur Mirchi Yard") }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (selectedLanguage.contains("Telugu")) "ఆంధ్ర ప్రదేశ్ & తెలంగాణ లైవ్ మండి మ్యాప్" else "Live Mandi & Rythu Bazar OpenStreetMap",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "OpenStreetMap interactive AP & Telangana market yards",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0F2FE)
                ) {
                    Text(
                        text = "10 Mandis",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Map WebView
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                val htmlContent = remember {
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                        <style>
                            body, html, #map { margin: 0; padding: 0; height: 100%; width: 100%; background: #0f172a; }
                            .leaflet-container { font-family: system-ui, sans-serif; }
                            .mandi-popup { background: #1e293b; color: #f8fafc; border-radius: 8px; font-size: 11px; padding: 4px; }
                            .mandi-title { font-weight: bold; color: #38bdf8; font-size: 13px; }
                            .mandi-price { color: #4ade80; font-weight: bold; font-size: 12px; }
                        </style>
                    </head>
                    <body>
                        <div id="map"></div>
                        <script>
                            var map = L.map('map', { zoomControl: false }).setView([16.5, 79.8], 7);
                            L.control.zoom({ position: 'topright' }).addTo(map);

                            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                maxZoom: 18,
                                attribution: 'OpenStreetMap'
                            }).addTo(map);

                            var mandis = [
                                { name: "Guntur Mirchi Yard", lat: 16.3067, lng: 80.4365, crop: "Red Chilli", price: "₹21,200/q" },
                                { name: "Warangal APMC", lat: 17.9784, lng: 79.5941, crop: "Cotton", price: "₹7,900/q" },
                                { name: "Nizamabad APMC", lat: 18.6725, lng: 78.0941, crop: "Turmeric", price: "₹14,900/q" },
                                { name: "Kurnool Market", lat: 15.8281, lng: 78.0373, crop: "Onion", price: "₹2,150/q" },
                                { name: "Miryalaguda Mandi", lat: 16.8667, lng: 79.5667, crop: "Paddy Sona", price: "₹2,450/q" },
                                { name: "Khammam Market", lat: 17.2473, lng: 80.1514, crop: "Maize", price: "₹2,220/q" },
                                { name: "Vijayawada Rythu Bazar", lat: 16.5062, lng: 80.6480, crop: "Mango", price: "₹4,800/q" },
                                { name: "Adoni APMC", lat: 15.6322, lng: 77.2728, crop: "Groundnut", price: "₹7,100/q" },
                                { name: "Madanapalle Market", lat: 13.5504, lng: 78.5027, crop: "Tomato", price: "₹2,900/q" },
                                { name: "Eluru APMC", lat: 16.7107, lng: 81.1042, crop: "Sugarcane", price: "₹3,350/q" }
                            ];

                            mandis.forEach(function(m) {
                                var marker = L.marker([m.lat, m.lng]).addTo(map);
                                var popupContent = "<div class='mandi-popup'>" +
                                    "<div class='mandi-title'>" + m.name + "</div>" +
                                    "<div>Main Crop: " + m.crop + "</div>" +
                                    "<div class='mandi-price'>" + m.price + "</div>" +
                                    "</div>";
                                marker.bindPopup(popupContent);
                                marker.on('click', function() {
                                    if (window.MandiBridge) {
                                        window.MandiBridge.onMandiClick(m.name);
                                    }
                                });
                            });
                        </script>
                    </body>
                    </html>
                    """.trimIndent()
                }

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            addJavascriptInterface(MandiMapJsBridge { name ->
                                post {
                                    selectedMandiName = name
                                    onMandiClick(name)
                                }
                            }, "MandiBridge")
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Mandi Indicator Footer
            val activeMandi = ApTsMandiMapData.mandis.find { it.name == selectedMandiName } ?: ApTsMandiMapData.mandis[0]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0FDF4), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(activeMandi.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                        Text("${activeMandi.state} • ${activeMandi.mainCrop}", fontSize = 10.sp, color = Color(0xFF166534))
                    }
                }

                Text(
                    text = activeMandi.livePriceQuintal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF15803D)
                )
            }
        }
    }
}
