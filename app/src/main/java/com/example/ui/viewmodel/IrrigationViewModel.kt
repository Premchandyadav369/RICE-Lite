package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.IrrigationScheduleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class CalculatedIrrigationSchedule(
    val cropName: String,
    val soilType: String,
    val farmAreaAcres: Double,
    val temperatureC: Double,
    val humidityPct: Double,
    val soilMoisturePct: Double,
    val rainfallForecastMm: Double,
    val irrigationType: String,
    val et0MmPerDay: Double,
    val kcFactor: Double,
    val etcMmPerDay: Double,
    val waterDepthMm: Double,
    val totalWaterLiters: Double,
    val durationMinutes: Int,
    val status: String,
    val statusColorHex: Long,
    val frequencyRecommendation: String,
    val adviceNotes: String
)

private data class EnvParams(
    val crop: String,
    val soil: String,
    val acres: Double,
    val method: String
)

private data class WeatherParams(
    val temp: Double,
    val hum: Double,
    val moist: Double,
    val rain: Double
)

class IrrigationViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.irrigationScheduleDao()

    val savedSchedules: StateFlow<List<IrrigationScheduleEntity>> = dao.getAllSchedules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCrop = MutableStateFlow("Paddy (Rice)")
    val selectedCrop: StateFlow<String> = _selectedCrop.asStateFlow()

    private val _selectedSoil = MutableStateFlow("Loamy / Alluvial Soil")
    val selectedSoil: StateFlow<String> = _selectedSoil.asStateFlow()

    private val _farmArea = MutableStateFlow(2.5)
    val farmArea: StateFlow<Double> = _farmArea.asStateFlow()

    private val _temperature = MutableStateFlow(32.0)
    val temperature: StateFlow<Double> = _temperature.asStateFlow()

    private val _humidity = MutableStateFlow(55.0)
    val humidity: StateFlow<Double> = _humidity.asStateFlow()

    private val _soilMoisture = MutableStateFlow(30.0)
    val soilMoisture: StateFlow<Double> = _soilMoisture.asStateFlow()

    private val _rainfallForecast = MutableStateFlow(0.0)
    val rainfallForecast: StateFlow<Double> = _rainfallForecast.asStateFlow()

    private val _irrigationType = MutableStateFlow("Drip Irrigation")
    val irrigationType: StateFlow<String> = _irrigationType.asStateFlow()

    fun setCrop(crop: String) { _selectedCrop.value = crop }
    fun setSoil(soil: String) { _selectedSoil.value = soil }
    fun setFarmArea(acres: Double) { _farmArea.value = acres.coerceAtLeast(0.1) }
    fun setTemperature(temp: Double) { _temperature.value = temp.coerceIn(10.0, 50.0) }
    fun setHumidity(hum: Double) { _humidity.value = hum.coerceIn(10.0, 100.0) }
    fun setSoilMoisture(moist: Double) { _soilMoisture.value = moist.coerceIn(0.0, 100.0) }
    fun setRainfallForecast(rain: Double) { _rainfallForecast.value = rain.coerceAtLeast(0.0) }
    fun setIrrigationType(type: String) { _irrigationType.value = type }

    private val envFlow = combine(
        _selectedCrop,
        _selectedSoil,
        _farmArea,
        _irrigationType
    ) { c, s, a, m -> EnvParams(c, s, a, m) }

    private val weatherFlow = combine(
        _temperature,
        _humidity,
        _soilMoisture,
        _rainfallForecast
    ) { t, h, sm, r -> WeatherParams(t, h, sm, r) }

    val calculatedSchedule: StateFlow<CalculatedIrrigationSchedule> = combine(
        envFlow,
        weatherFlow
    ) { env, w ->
        val crop = env.crop
        val soil = env.soil
        val acres = env.acres
        val method = env.method
        val temp = w.temp
        val hum = w.hum
        val moist = w.moist
        val rain = w.rain

        val kc = getCropKcFactor(crop)
        val (fieldCapacity, wiltingPoint) = getSoilMoistureLimits(soil)

        // ET0 Approximation (mm/day) using simplified temperature and humidity formula
        val humFactor = (100.0 - hum) / 100.0
        val et0 = (0.18 * (temp + 15.0) * sqrt(humFactor + 0.1)).roundTo(2)
        val etc = (et0 * kc).roundTo(2)

        // Calculate Soil Deficit vs Target Moisture (75% of Field Capacity)
        val targetMoisture = fieldCapacity * 0.75
        val moistureDeficit = (targetMoisture - moist).coerceAtLeast(0.0)

        // Net Water Depth Needed (mm) = Evapotranspiration + Deficit adjustment - Effective Rainfall
        val effectiveRain = rain * 0.8
        val netDepthMm = ((etc + (moistureDeficit * 0.25)) - effectiveRain).coerceAtLeast(0.0).roundTo(1)

        // Total Liters = mm * acres * 4046.86 L/acre/mm
        val totalLiters = (netDepthMm * acres * 4046.86).roundTo(0)

        // Irrigation Duration Minutes
        val dischargeLpmPerAcre = when (method) {
            "Drip Irrigation" -> 160.0 // L/min/acre
            "Sprinkler System" -> 320.0
            else -> 480.0 // Flood / Furrow
        }
        val durationMins = if (totalLiters > 0) {
            ((totalLiters / (dischargeLpmPerAcre * acres))).roundToInt().coerceIn(10, 360)
        } else 0

        // Status determination
        val (status, colorHex) = when {
            moist < wiltingPoint + 5.0 -> Pair("🚨 Urgent Watering Needed", 0xFFDC2626)
            moist < targetMoisture -> Pair("⚠️ Irrigation Recommended Today", 0xFFD97706)
            rain > 10.0 -> Pair("🌧️ Rainfall Expected - Skip Irrigation", 0xFF2563EB)
            else -> Pair("✅ Soil Moisture Optimal", 0xFF16A34A)
        }

        val freq = when {
            moist < wiltingPoint + 5.0 -> "Immediate 2 Sessions (Morning & Evening)"
            method == "Drip Irrigation" -> "Daily early morning session (5:30 AM)"
            method == "Sprinkler System" -> "Alternate Days at sunrise"
            else -> "Every 3-4 Days"
        }

        val advice = when {
            rain > 5.0 -> "Forecast indicates ${rain}mm rainfall. Pause artificial watering to prevent root rot."
            temp > 35.0 -> "High ambient temperature (${temp}°C) increases evaporation loss. Apply mulch and water before 7:00 AM."
            moist < 25.0 -> "Soil moisture is critically low. Water immediately to avoid crop wilting."
            else -> "Maintain consistent soil moisture levels to promote deep root growth."
        }

        CalculatedIrrigationSchedule(
            cropName = crop,
            soilType = soil,
            farmAreaAcres = acres,
            temperatureC = temp,
            humidityPct = hum,
            soilMoisturePct = moist,
            rainfallForecastMm = rain,
            irrigationType = method,
            et0MmPerDay = et0,
            kcFactor = kc,
            etcMmPerDay = etc,
            waterDepthMm = netDepthMm,
            totalWaterLiters = totalLiters,
            durationMinutes = durationMins,
            status = status,
            statusColorHex = colorHex,
            frequencyRecommendation = freq,
            adviceNotes = advice
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculatedIrrigationSchedule(
            cropName = "Paddy (Rice)",
            soilType = "Loamy / Alluvial Soil",
            farmAreaAcres = 2.5,
            temperatureC = 32.0,
            humidityPct = 55.0,
            soilMoisturePct = 30.0,
            rainfallForecastMm = 0.0,
            irrigationType = "Drip Irrigation",
            et0MmPerDay = 5.2,
            kcFactor = 1.15,
            etcMmPerDay = 6.0,
            waterDepthMm = 6.2,
            totalWaterLiters = 62726.0,
            durationMinutes = 90,
            status = "⚠️ Irrigation Recommended Today",
            statusColorHex = 0xFFD97706,
            frequencyRecommendation = "Daily early morning session (5:30 AM)",
            adviceNotes = "Maintain consistent soil moisture levels"
        )
    )

    fun saveScheduleToDb() {
        val calc = calculatedSchedule.value
        val entity = IrrigationScheduleEntity(
            cropName = calc.cropName,
            soilType = calc.soilType,
            farmAreaAcres = calc.farmAreaAcres,
            temperatureC = calc.temperatureC,
            humidityPct = calc.humidityPct,
            soilMoisturePct = calc.soilMoisturePct,
            recommendedWaterLiters = calc.totalWaterLiters,
            recommendedWaterMm = calc.waterDepthMm,
            irrigationDurationMinutes = calc.durationMinutes,
            irrigationFrequency = calc.frequencyRecommendation,
            irrigationType = calc.irrigationType,
            status = calc.status
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertSchedule(entity)
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteSchedule(id)
        }
    }

    private fun getCropKcFactor(crop: String): Double {
        return when {
            crop.contains("Paddy", true) || crop.contains("Rice", true) -> 1.15
            crop.contains("Sugarcane", true) -> 1.25
            crop.contains("Potato", true) || crop.contains("Tomato", true) -> 1.15
            crop.contains("Maize", true) || crop.contains("Wheat", true) -> 1.05
            crop.contains("Cotton", true) || crop.contains("Chilli", true) -> 1.0
            else -> 1.0
        }
    }

    private fun getSoilMoistureLimits(soil: String): Pair<Double, Double> { // Pair(FieldCapacity %, WiltingPoint %)
        return when {
            soil.contains("Clay", true) -> Pair(42.0, 20.0)
            soil.contains("Black", true) -> Pair(40.0, 18.0)
            soil.contains("Sandy", true) -> Pair(22.0, 8.0)
            else -> Pair(35.0, 15.0) // Loamy
        }
    }

    private fun Double.roundTo(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }
}
