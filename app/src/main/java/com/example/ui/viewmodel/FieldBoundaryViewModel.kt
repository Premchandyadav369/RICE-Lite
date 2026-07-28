package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FieldBoundaryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

data class GpsWaypoint(
    val lat: Double,
    val lng: Double,
    val label: String = ""
)

enum class MapLayerType {
    SATELLITE_GRID,
    VECTOR_TOPOGRAPHIC,
    BOUNDARY_ONLY
}

class FieldBoundaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.fieldBoundaryDao()

    val savedBoundaries: StateFlow<List<FieldBoundaryEntity>> = dao.getAllBoundaries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _pinnedWaypoints = MutableStateFlow<List<GpsWaypoint>>(
        listOf(
            GpsWaypoint(28.6139, 77.2090, "P1"),
            GpsWaypoint(28.6148, 77.2092, "P2"),
            GpsWaypoint(28.6146, 77.2104, "P3"),
            GpsWaypoint(28.6137, 77.2101, "P4")
        )
    )
    val pinnedWaypoints: StateFlow<List<GpsWaypoint>> = _pinnedWaypoints.asStateFlow()

    private val _currentFieldName = MutableStateFlow("Paddy Sector A")
    val currentFieldName: StateFlow<String> = _currentFieldName.asStateFlow()

    private val _currentCropName = MutableStateFlow("Paddy (Rice)")
    val currentCropName: StateFlow<String> = _currentCropName.asStateFlow()

    private val _currentSoilType = MutableStateFlow("Loamy / Alluvial Soil")
    val currentSoilType: StateFlow<String> = _currentSoilType.asStateFlow()

    private val _mapLayer = MutableStateFlow(MapLayerType.SATELLITE_GRID)
    val mapLayer: StateFlow<MapLayerType> = _mapLayer.asStateFlow()

    private val _selectedBoundaryForView = MutableStateFlow<FieldBoundaryEntity?>(null)
    val selectedBoundaryForView: StateFlow<FieldBoundaryEntity?> = _selectedBoundaryForView.asStateFlow()

    // Base Reference Center GPS (Delhi NCR agricultural test plot)
    val baseCenterLat = 28.6142
    val baseCenterLng = 77.2097

    fun setFieldName(name: String) { _currentFieldName.value = name }
    fun setCropName(crop: String) { _currentCropName.value = crop }
    fun setSoilType(soil: String) { _currentSoilType.value = soil }
    fun setMapLayer(layer: MapLayerType) { _mapLayer.value = layer }

    fun addWaypoint(lat: Double, lng: Double) {
        val current = _pinnedWaypoints.value.toMutableList()
        val label = "P${current.size + 1}"
        current.add(GpsWaypoint((lat * 10000.0).roundToInt() / 10000.0, (lng * 10000.0).roundToInt() / 10000.0, label))
        _pinnedWaypoints.value = current
    }

    fun addWaypointAtCurrentGps() {
        val current = _pinnedWaypoints.value.toMutableList()
        val count = current.size
        // Simulate GPS variance relative to base center
        val offsetLat = (sin(count * 1.5) * 0.0012)
        val offsetLng = (cos(count * 1.5) * 0.0012)
        val lat = (baseCenterLat + offsetLat).roundTo(4)
        val lng = (baseCenterLng + offsetLng).roundTo(4)
        current.add(GpsWaypoint(lat, lng, "GPS-${count + 1}"))
        _pinnedWaypoints.value = current
    }

    fun removeWaypoint(index: Int) {
        val current = _pinnedWaypoints.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _pinnedWaypoints.value = current
        }
    }

    fun clearWaypoints() {
        _pinnedWaypoints.value = emptyList()
    }

    fun selectBoundaryToInspect(boundary: FieldBoundaryEntity?) {
        _selectedBoundaryForView.value = boundary
        if (boundary != null) {
            val waypoints = parseJsonCoordinates(boundary.coordinatesJson)
            if (waypoints.isNotEmpty()) {
                _pinnedWaypoints.value = waypoints
                _currentFieldName.value = boundary.fieldName
                _currentCropName.value = boundary.cropName
                _currentSoilType.value = boundary.soilType
            }
        }
    }

    val calculatedAreaAcres: StateFlow<Double> = _pinnedWaypoints.map { waypoints ->
        calculatePolygonAreaAcres(waypoints)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2.5)

    val calculatedPerimeterMeters: StateFlow<Double> = _pinnedWaypoints.map { waypoints ->
        calculatePolygonPerimeterMeters(waypoints)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 420.0)

    fun saveBoundaryToRoom() {
        val waypoints = _pinnedWaypoints.value
        if (waypoints.size < 3) return

        val area = calculatePolygonAreaAcres(waypoints)
        val perimeter = calculatePolygonPerimeterMeters(waypoints)
        val centerLat = waypoints.map { it.lat }.average().roundTo(4)
        val centerLng = waypoints.map { it.lng }.average().roundTo(4)
        val json = encodeToJsonCoordinates(waypoints)

        val entity = FieldBoundaryEntity(
            fieldName = _currentFieldName.value.ifBlank { "Unassigned Plot" },
            cropName = _currentCropName.value,
            soilType = _currentSoilType.value,
            areaAcres = area,
            perimeterMeters = perimeter,
            centerLatitude = centerLat,
            centerLongitude = centerLng,
            waypointsCount = waypoints.size,
            coordinatesJson = json
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insertBoundary(entity)
        }
    }

    fun deleteBoundary(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteBoundary(id)
        }
    }

    private fun calculatePolygonAreaAcres(waypoints: List<GpsWaypoint>): Double {
        if (waypoints.size < 3) return 0.0
        // Convert Lat/Lng to local meters projection around reference point
        val points = waypoints.map {
            val dLat = (it.lat - baseCenterLat) * 111000.0 // meters per deg lat
            val dLng = (it.lng - baseCenterLng) * 111000.0 * cos(Math.toRadians(baseCenterLat))
            Pair(dLat, dLng)
        }
        // Shoelace Formula
        var areaM2 = 0.0
        val n = points.size
        for (i in 0 until n) {
            val j = (i + 1) % n
            areaM2 += (points[i].first * points[j].second)
            areaM2 -= (points[j].first * points[i].second)
        }
        areaM2 = abs(areaM2) / 2.0
        val acres = areaM2 / 4046.86
        return acres.roundTo(2)
    }

    private fun calculatePolygonPerimeterMeters(waypoints: List<GpsWaypoint>): Double {
        if (waypoints.size < 2) return 0.0
        var totalDist = 0.0
        val n = waypoints.size
        for (i in 0 until n) {
            val j = (i + 1) % n
            val p1 = waypoints[i]
            val p2 = waypoints[j]
            totalDist += haversineDistanceMeters(p1.lat, p1.lng, p2.lat, p2.lng)
        }
        return totalDist.roundTo(1)
    }

    private fun haversineDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun encodeToJsonCoordinates(waypoints: List<GpsWaypoint>): String {
        return waypoints.joinToString(prefix = "[", postfix = "]") {
            "{\"lat\":${it.lat},\"lng\":${it.lng}}"
        }
    }

    private fun parseJsonCoordinates(json: String): List<GpsWaypoint> {
        val list = mutableListOf<GpsWaypoint>()
        val regex = """\{"lat":([\d.]+),"lng":([\d.]+)\}""".toRegex()
        regex.findAll(json).forEachIndexed { index, result ->
            val lat = result.groupValues[1].toDoubleOrNull()
            val lng = result.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) {
                list.add(GpsWaypoint(lat, lng, "P${index + 1}"))
            }
        }
        return list
    }

    private fun Double.roundTo(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }
}
