package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FertilizerPlanEntity
import com.example.data.NpkRequirementEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class CalculatedFertilizerSchedule(
    val cropName: String,
    val soilType: String,
    val acres: Double,
    val perAcreN: Double,
    val perAcreP: Double,
    val perAcreK: Double,
    val totalN: Double,
    val totalP: Double,
    val totalK: Double,
    val ureaBags: Double,
    val dapBags: Double,
    val mopBags: Double,
    val basalDose: String,
    val firstTopDressing: String,
    val secondTopDressing: String,
    val micronutrients: String,
    val organicBiofertilizer: String,
    val remarks: String
)

class FertilizerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val npkRequirementDao = db.npkRequirementDao()
    private val fertilizerPlanDao = db.fertilizerPlanDao()

    val savedPlans: StateFlow<List<FertilizerPlanEntity>> = fertilizerPlanDao.getAllPlans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val npkRequirements: StateFlow<List<NpkRequirementEntity>> = npkRequirementDao.getAllRequirements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCrop = MutableStateFlow("Paddy (Rice)")
    val selectedCrop: StateFlow<String> = _selectedCrop.asStateFlow()

    private val _selectedSoil = MutableStateFlow("Loamy / Alluvial Soil")
    val selectedSoil: StateFlow<String> = _selectedSoil.asStateFlow()

    private val _farmAreaAcres = MutableStateFlow(2.5)
    val farmAreaAcres: StateFlow<Double> = _farmAreaAcres.asStateFlow()

    fun setSelectedCrop(crop: String) {
        _selectedCrop.value = crop
    }

    fun setSelectedSoil(soil: String) {
        _selectedSoil.value = soil
    }

    fun setFarmArea(acres: Double) {
        _farmAreaAcres.value = acres.coerceAtLeast(0.1)
    }

    val calculatedSchedule: StateFlow<CalculatedFertilizerSchedule> = combine(
        _selectedCrop,
        _selectedSoil,
        _farmAreaAcres,
        npkRequirements
    ) { crop, soil, acres, reqList ->
        // Find matching NPK requirement from Room DB
        val reqFromDb = reqList.find { it.cropName.contains(crop.take(4), ignoreCase = true) && it.soilType.contains(soil.take(5), ignoreCase = true) }
            ?: reqList.find { it.cropName.contains(crop.take(4), ignoreCase = true) }

        val baseN = reqFromDb?.nitrogenN ?: getFallbackBaseNpk(crop).first
        val baseP = reqFromDb?.phosphorusP ?: getFallbackBaseNpk(crop).second
        val baseK = reqFromDb?.potassiumK ?: getFallbackBaseNpk(crop).third

        val soilMult = getSoilMultiplier(soil)

        val perAcreN = (baseN * soilMult.first).roundTo(1)
        val perAcreP = (baseP * soilMult.second).roundTo(1)
        val perAcreK = (baseK * soilMult.third).roundTo(1)

        val totalN = (perAcreN * acres).roundTo(1)
        val totalP = (perAcreP * acres).roundTo(1)
        val totalK = (perAcreK * acres).roundTo(1)

        // Calculate Commercial Fertilizer Bags Required (50kg bags)
        val dapBags = ((totalP / 0.46) / 50.0).roundTo(1)
        val nFromDap = totalP * (18.0 / 46.0)
        val remainingN = (totalN - nFromDap).coerceAtLeast(0.0)

        val ureaBags = ((remainingN / 0.46) / 50.0).roundTo(1)
        val mopBags = ((totalK / 0.60) / 50.0).roundTo(1)

        val basalDoseStr = "At Sowing/Transplanting: DAP ${(dapBags * 50).roundToInt()} kg (${dapBags} bags) + MOP ${(mopBags * 25).roundToInt()} kg + Urea ${(ureaBags * 15).roundToInt()} kg as basal dose."
        val firstTopStr = "At 20-25 Days (Tillering/Vegetative): Urea ${(ureaBags * 20).roundToInt()} kg/acre + Zinc Sulphate 21% @ 10 kg/acre."
        val secondTopStr = "At 45-50 Days (Panicle Initiation/Flowering): Remaining Urea ${(ureaBags * 15).roundToInt()} kg/acre + MOP ${(mopBags * 25).roundToInt()} kg."
        val microStr = "Apply Sulphur WDG 90% @ 3 kg/acre + Boron 20% @ 1 kg/acre during active tillering stage."
        val organicStr = "Mix 2 kg Azospirillum + 2 kg PSB in 100 kg well-decomposed FYM/Vermicompost per acre before basal application."

        CalculatedFertilizerSchedule(
            cropName = crop,
            soilType = soil,
            acres = acres,
            perAcreN = perAcreN,
            perAcreP = perAcreP,
            perAcreK = perAcreK,
            totalN = totalN,
            totalP = totalP,
            totalK = totalK,
            ureaBags = ureaBags,
            dapBags = dapBags,
            mopBags = mopBags,
            basalDose = basalDoseStr,
            firstTopDressing = firstTopStr,
            secondTopDressing = secondTopStr,
            micronutrients = microStr,
            organicBiofertilizer = organicStr,
            remarks = reqFromDb?.remarks ?: "Calculated using ICAR standard soil recommendations"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculatedFertilizerSchedule(
            cropName = "Paddy (Rice)",
            soilType = "Loamy / Alluvial Soil",
            acres = 2.5,
            perAcreN = 48.0, perAcreP = 24.0, perAcreK = 24.0,
            totalN = 120.0, totalP = 60.0, totalK = 60.0,
            ureaBags = 2.9, dapBags = 2.6, mopBags = 2.0,
            basalDose = "Basal application...",
            firstTopDressing = "1st top dressing...",
            secondTopDressing = "2nd top dressing...",
            micronutrients = "Micronutrients...",
            organicBiofertilizer = "Organic mix...",
            remarks = "Standard recommendation"
        )
    )

    fun saveCurrentPlan() {
        val schedule = calculatedSchedule.value
        val plan = FertilizerPlanEntity(
            cropName = schedule.cropName,
            soilType = schedule.soilType,
            farmAreaAcres = schedule.acres,
            nitrogenN = schedule.perAcreN,
            phosphorusP = schedule.perAcreP,
            potassiumK = schedule.perAcreK,
            basalDose = schedule.basalDose,
            firstTopDressing = schedule.firstTopDressing,
            secondTopDressing = schedule.secondTopDressing,
            micronutrients = schedule.micronutrients,
            organicBiofertilizer = schedule.organicBiofertilizer,
            totalUreaBags = schedule.ureaBags,
            totalDapBags = schedule.dapBags,
            totalMopBags = schedule.mopBags
        )
        viewModelScope.launch(Dispatchers.IO) {
            fertilizerPlanDao.insertPlan(plan)
        }
    }

    fun deletePlan(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            fertilizerPlanDao.deletePlan(id)
        }
    }

    private fun getFallbackBaseNpk(crop: String): Triple<Double, Double, Double> {
        return when {
            crop.contains("Paddy", ignoreCase = true) || crop.contains("Rice", ignoreCase = true) -> Triple(48.0, 24.0, 24.0)
            crop.contains("Wheat", ignoreCase = true) -> Triple(50.0, 25.0, 20.0)
            crop.contains("Cotton", ignoreCase = true) -> Triple(60.0, 30.0, 30.0)
            crop.contains("Sugarcane", ignoreCase = true) -> Triple(100.0, 40.0, 50.0)
            crop.contains("Maize", ignoreCase = true) -> Triple(55.0, 28.0, 25.0)
            crop.contains("Potato", ignoreCase = true) -> Triple(75.0, 40.0, 60.0)
            crop.contains("Tomato", ignoreCase = true) -> Triple(65.0, 35.0, 40.0)
            crop.contains("Chilli", ignoreCase = true) -> Triple(70.0, 35.0, 35.0)
            crop.contains("Soybean", ignoreCase = true) -> Triple(12.0, 32.0, 16.0)
            else -> Triple(40.0, 20.0, 20.0)
        }
    }

    private fun getSoilMultiplier(soil: String): Triple<Double, Double, Double> {
        return when {
            soil.contains("Sandy", ignoreCase = true) -> Triple(1.15, 1.0, 1.1)
            soil.contains("Clay", ignoreCase = true) -> Triple(0.95, 1.05, 0.95)
            soil.contains("Black", ignoreCase = true) -> Triple(1.0, 0.9, 0.8)
            soil.contains("Red", ignoreCase = true) -> Triple(1.1, 1.15, 1.05)
            else -> Triple(1.0, 1.0, 1.0)
        }
    }

    private fun Double.roundTo(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }
}
