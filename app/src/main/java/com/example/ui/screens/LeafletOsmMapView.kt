package com.example.ui.screens

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.viewmodel.GpsWaypoint
import com.example.ui.viewmodel.MapLayerType
import org.json.JSONArray
import org.json.JSONObject

class LeafletJsBridge(private val onMapClickCallback: (Double, Double) -> Unit) {
    @JavascriptInterface
    fun onMapClick(lat: Double, lng: Double) {
        onMapClickCallback(lat, lng)
    }
}

@Composable
fun LeafletOsmMapView(
    waypoints: List<GpsWaypoint>,
    mapLayer: MapLayerType,
    centerLat: Double,
    centerLng: Double,
    onAddWaypoint: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val htmlContent = remember(centerLat, centerLng) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map { margin: 0; padding: 0; height: 100%; width: 100%; background: #0f172a; }
                .leaflet-container { font-family: sans-serif; }
                .leaflet-popup-content-wrapper { background: #1e293b; color: #f8fafc; border-radius: 8px; font-size: 12px; }
                .leaflet-popup-tip { background: #1e293b; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false }).setView([$centerLat, $centerLng], 16);
                L.control.zoom({ position: 'topright' }).addTo(map);

                var osmLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: 'OpenStreetMap'
                }).addTo(map);

                var satelliteLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
                    maxZoom: 19,
                    attribution: 'Esri World Imagery'
                });

                var currentPolygon = null;
                var markers = [];

                function setMapLayer(layerType) {
                    if (layerType === 'SATELLITE_GRID') {
                        map.removeLayer(osmLayer);
                        satelliteLayer.addTo(map);
                    } else {
                        map.removeLayer(satelliteLayer);
                        osmLayer.addTo(map);
                    }
                }

                function updateWaypoints(jsonStr) {
                    markers.forEach(function(m) { map.removeLayer(m); });
                    markers = [];
                    if (currentPolygon) map.removeLayer(currentPolygon);

                    try {
                        var waypoints = JSON.parse(jsonStr);
                        var latlngs = [];
                        waypoints.forEach(function(wp) {
                            var latlng = [wp.lat, wp.lng];
                            latlngs.push(latlng);
                            var marker = L.marker(latlng).addTo(map)
                                .bindPopup("<b>" + wp.label + "</b><br>Lat: " + wp.lat.toFixed(5) + "<br>Lng: " + wp.lng.toFixed(5));
                            markers.push(marker);
                        });

                        if (latlngs.length >= 3) {
                            currentPolygon = L.polygon(latlngs, {
                                color: '#22c55e',
                                fillColor: '#16a34a',
                                fillOpacity: 0.4,
                                weight: 3
                            }).addTo(map);
                            map.fitBounds(currentPolygon.getBounds().pad(0.15));
                        } else if (latlngs.length > 0) {
                            map.panTo(latlngs[latlngs.length - 1]);
                        }
                    } catch(e) {
                        console.error(e);
                    }
                }

                map.on('click', function(e) {
                    if (window.AndroidBridge) {
                        window.AndroidBridge.onMapClick(e.latlng.lat, e.latlng.lng);
                    }
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    LaunchedEffect(waypoints, isMapLoaded, mapLayer) {
        if (isMapLoaded && webViewRef != null) {
            val jsonArray = JSONArray()
            waypoints.forEach { wp ->
                val obj = JSONObject()
                obj.put("lat", wp.lat)
                obj.put("lng", wp.lng)
                obj.put("label", wp.label)
                jsonArray.put(obj)
            }
            val jsonStr = jsonArray.toString().replace("'", "\\'")
            val layerTypeStr = mapLayer.name
            webViewRef?.evaluateJavascript("setMapLayer('$layerTypeStr'); updateWaypoints('$jsonStr');", null)
        }
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                
                addJavascriptInterface(LeafletJsBridge { lat, lng ->
                    post {
                        onAddWaypoint(lat, lng)
                    }
                }, "AndroidBridge")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isMapLoaded = true
                        webViewRef = view
                        // Initial trigger
                        val jsonArray = JSONArray()
                        waypoints.forEach { wp ->
                            val obj = JSONObject()
                            obj.put("lat", wp.lat)
                            obj.put("lng", wp.lng)
                            obj.put("label", wp.label)
                            jsonArray.put(obj)
                        }
                        val jsonStr = jsonArray.toString().replace("'", "\\'")
                        val layerTypeStr = mapLayer.name
                        view?.evaluateJavascript("setMapLayer('$layerTypeStr'); updateWaypoints('$jsonStr');", null)
                    }
                }
                loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webViewRef = webView
        },
        modifier = modifier.fillMaxSize()
    )
}
