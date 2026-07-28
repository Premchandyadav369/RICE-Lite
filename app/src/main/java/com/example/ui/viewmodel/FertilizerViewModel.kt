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
    val cropStage: String,
    val agroRegion: String,
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
    val stageSpecificDosage: String,
    val stageUreaBags: Double,
    val stageDapBags: Double,
    val stageMopBags: Double,
    val soilTypeAdjustmentReason: String,
    val apTsExtensionAdvice: String,
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

    private val _selectedCropStage = MutableStateFlow("Vegetative / Tillering Stage (20-45 Days)")
    val selectedCropStage: StateFlow<String> = _selectedCropStage.asStateFlow()

    private val _selectedAgroRegion = MutableStateFlow("Krishna-Godavari Delta Belt (AP)")
    val selectedAgroRegion: StateFlow<String> = _selectedAgroRegion.asStateFlow()

    private val _farmAreaAcres = MutableStateFlow(2.5)
    val farmAreaAcres: StateFlow<Double> = _farmAreaAcres.asStateFlow()

    fun setSelectedCrop(crop: String) {
        _selectedCrop.value = crop
    }

    fun setSelectedSoil(soil: String) {
        _selectedSoil.value = soil
    }

    fun setSelectedCropStage(stage: String) {
        _selectedCropStage.value = stage
    }

    fun setSelectedAgroRegion(region: String) {
        _selectedAgroRegion.value = region
    }

    fun setFarmArea(acres: Double) {
        _farmAreaAcres.value = acres.coerceAtLeast(0.1)
    }

    val calculatedSchedule: StateFlow<CalculatedFertilizerSchedule> = combine(
        combine(_selectedCrop, _selectedSoil, _selectedCropStage, _selectedAgroRegion, _farmAreaAcres) { c, s, st, r, a ->
            Tuple5(c, s, st, r, a)
        },
        npkRequirements
    ) { (crop, soil, stage, region, acres), reqList ->
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

        // Total season commercial fertilizer bags (50kg bags)
        val dapBags = ((totalP / 0.46) / 50.0).roundTo(1)
        val nFromDap = totalP * (18.0 / 46.0)
        val remainingN = (totalN - nFromDap).coerceAtLeast(0.0)

        val ureaBags = ((remainingN / 0.46) / 50.0).roundTo(1)
        val mopBags = ((totalK / 0.60) / 50.0).roundTo(1)

        // Stage-Specific Dosage Splits
        val (stageNFrac, stagePFrac, stageKFrac, stageTitle) = when {
            stage.contains("Basal", ignoreCase = true) -> Quadruple(0.25, 1.0, 0.40, "Sowing / Basal Stage")
            stage.contains("Vegetative", ignoreCase = true) || stage.contains("Tillering", ignoreCase = true) -> Quadruple(0.45, 0.0, 0.20, "Active Tillering / Growth Stage")
            stage.contains("Flowering", ignoreCase = true) || stage.contains("Panicle", ignoreCase = true) -> Quadruple(0.30, 0.0, 0.40, "Panicle Initiation / Flowering Stage")
            else -> Quadruple(0.0, 0.0, 0.0, "Grain Filling / Harvesting Stage")
        }

        val stageUreaBags = (ureaBags * stageNFrac).roundTo(1)
        val stageDapBags = (dapBags * stagePFrac).roundTo(1)
        val stageMopBags = (mopBags * stageKFrac).roundTo(1)

        val stageSpecificText = when {
            stage.contains("Basal", ignoreCase = true) ->
                "👉 For $acres Acres at $stageTitle: Apply ${stageDapBags} Bags DAP (${(stageDapBags * 50).roundToInt()} kg) + ${stageUreaBags} Bags Urea (${(stageUreaBags * 50).roundToInt()} kg) + ${stageMopBags} Bags MOP (${(stageMopBags * 50).roundToInt()} kg) as basal dressing before transplanting."
            stage.contains("Vegetative", ignoreCase = true) || stage.contains("Tillering", ignoreCase = true) ->
                "👉 For $acres Acres at $stageTitle: Top dress ${stageUreaBags} Bags Neem-Coated Urea (${(stageUreaBags * 50).roundToInt()} kg) + ${stageMopBags} Bags MOP (${(stageMopBags * 50).roundToInt()} kg) + Zinc Sulphate 21% @ ${(10 * acres).roundToInt()} kg."
            stage.contains("Flowering", ignoreCase = true) || stage.contains("Panicle", ignoreCase = true) ->
                "👉 For $acres Acres at $stageTitle: Top dress final split of ${stageUreaBags} Bags Urea (${(stageUreaBags * 50).roundToInt()} kg) + ${stageMopBags} Bags MOP (${(stageMopBags * 50).roundToInt()} kg) + Foliar 13-0-45 @ ${(2 * acres).roundToInt()} kg."
            else ->
                "👉 For $acres Acres at $stageTitle: Fertilizer application completed. Spray 1% Potassium Nitrate (13-0-45) or Boron 20% if grain weight boost is required."
        }

        val soilReason = when {
            soil.contains("Sandy", ignoreCase = true) || soil.contains("Red", ignoreCase = true) ->
                "⚠️ $soil has high leaching potential! Urea dosage is split into 4 micro-applications with added Organic FYM to retain moisture."
            soil.contains("Black", ignoreCase = true) || soil.contains("Clay", ignoreCase = true) ->
                "ℹ️ $soil has high Potassium fixation and high water retention. Phosphatic fertilizer (DAP) is placed deep near root zone."
            else ->
                "✓ $soil offers balanced NPK absorption and optimal cation exchange capacity."
        }

        val extensionAdvice = when {
            region.contains("Guntur", ignoreCase = true) ->
                "🏛️ ANGRAU Guntur Chilli Recommendation: Incorporate Neem Cake @ 250 kg/acre to prevent Black Thrips and Nematode damage. Combine Urea split with Sulphur."
            region.contains("Delta", ignoreCase = true) ->
                "🏛️ ANGRAU Godavari Delta Paddy Advisory: Avoid excess Nitrogen application to prevent Rice Blast and Sheath Blight. Apply 21% Zinc Sulphate 10 kg/acre."
            region.contains("Telangana", ignoreCase = true) ->
                "🏛️ PJTSAU Telangana Cotton & Maize Protocol: Use 100% Neem-Coated Urea. Apply Magnesium Sulphate @ 10 kg/acre in Red Chalka soils."
            region.contains("Rayalaseema", ignoreCase = true) ->
                "🏛️ ANGRAU Rayalaseema Groundnut Protocol: Gypsum application @ 200 kg/acre at 45 DAS during pegging stage is essential for pod filling."
            else ->
                "🏛️ ANGRAU & PJTSAU Joint Recommendation: Follow Soil Health Card (SHC) recommendations and balance inorganic fertilizers with 2 tonnes Vermicompost/acre."
        }

        val basalDoseStr = "At Sowing/Transplanting: DAP ${(dapBags * 50).roundToInt()} kg (${dapBags} bags) + MOP ${(mopBags * 25).roundToInt()} kg + Urea ${(ureaBags * 15).roundToInt()} kg as basal dose."
        val firstTopStr = "At 20-25 Days (Tillering/Vegetative): Urea ${(ureaBags * 20).roundToInt()} kg/acre + Zinc Sulphate 21% @ 10 kg/acre."
        val secondTopStr = "At 45-50 Days (Panicle Initiation/Flowering): Remaining Urea ${(ureaBags * 15).roundToInt()} kg/acre + MOP ${(mopBags * 25).roundToInt()} kg."
        val microStr = "Apply Sulphur WDG 90% @ 3 kg/acre + Boron 20% @ 1 kg/acre during active tillering stage."
        val organicStr = "Mix 2 kg Azospirillum + 2 kg PSB in 100 kg well-decomposed FYM/Vermicompost per acre before basal application."

        CalculatedFertilizerSchedule(
            cropName = crop,
            soilType = soil,
            cropStage = stage,
            agroRegion = region,
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
            stageSpecificDosage = stageSpecificText,
            stageUreaBags = stageUreaBags,
            stageDapBags = stageDapBags,
            stageMopBags = stageMopBags,
            soilTypeAdjustmentReason = soilReason,
            apTsExtensionAdvice = extensionAdvice,
            basalDose = basalDoseStr,
            firstTopDressing = firstTopStr,
            secondTopDressing = secondTopStr,
            micronutrients = microStr,
            organicBiofertilizer = organicStr,
            remarks = reqFromDb?.remarks ?: "Calculated using ICAR & ANGRAU/PJTSAU soil recommendations"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculatedFertilizerSchedule(
            cropName = "Paddy (Rice)",
            soilType = "Loamy / Alluvial Soil",
            cropStage = "Vegetative / Tillering Stage (20-45 Days)",
            agroRegion = "Krishna-Godavari Delta Belt (AP)",
            acres = 2.5,
            perAcreN = 48.0, perAcreP = 24.0, perAcreK = 24.0,
            totalN = 120.0, totalP = 60.0, totalK = 60.0,
            ureaBags = 2.9, dapBags = 2.6, mopBags = 2.0,
            stageSpecificDosage = "Stage dosage...",
            stageUreaBags = 1.3, stageDapBags = 0.0, stageMopBags = 0.4,
            soilTypeAdjustmentReason = "Soil reason...",
            apTsExtensionAdvice = "Extension advice...",
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

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)


