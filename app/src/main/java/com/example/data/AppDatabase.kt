package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ScanItem::class, CropDiseaseEntity::class, FertilizerPlanEntity::class, NpkRequirementEntity::class, IrrigationScheduleEntity::class, FieldBoundaryEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanItemDao(): ScanItemDao
    abstract fun cropDiseaseDao(): CropDiseaseDao
    abstract fun fertilizerPlanDao(): FertilizerPlanDao
    abstract fun npkRequirementDao(): NpkRequirementDao
    abstract fun irrigationScheduleDao(): IrrigationScheduleDao
    abstract fun fieldBoundaryDao(): FieldBoundaryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "krishi_drishti_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.fertilizerPlanDao()?.insertPlans(initialFertilizerPlans)
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Prepopulate if database was already created but table is empty
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = INSTANCE?.fertilizerPlanDao()
                            if (dao != null) {
                                // Background check to ensure default ICAR Indian crop plans exist
                                val existingPlans = dao.getAllPlans()
                                // Prepopulate if needed
                            }
                        }
                    }
                })
                .build()

                // Trigger initial background check on first load to seed sample plans & requirements
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        instance.fertilizerPlanDao().insertPlans(initialFertilizerPlans)
                        instance.npkRequirementDao().insertRequirements(initialNpkRequirements)
                        instance.irrigationScheduleDao().insertSchedules(initialIrrigationSchedules)
                        instance.fieldBoundaryDao().insertBoundaries(initialFieldBoundaries)
                    } catch (_: Exception) {}
                }

                INSTANCE = instance
                instance
            }
        }

        private val initialFertilizerPlans = listOf(
            FertilizerPlanEntity(
                cropName = "Paddy (Rice)",
                soilType = "Loamy / Alluvial Soil",
                farmAreaAcres = 2.5,
                nitrogenN = 48.0,
                phosphorusP = 24.0,
                potassiumK = 24.0,
                basalDose = "At Transplanting: DAP 130 kg (2.6 bags) + MOP 50 kg + Urea 41 kg as basal dose.",
                firstTopDressing = "At 20-25 Days (Tillering): Urea 54 kg/acre + Zinc Sulphate 21% @ 10 kg/acre.",
                secondTopDressing = "At 45-50 Days (Panicle Initiation): Urea 41 kg/acre + MOP 50 kg.",
                micronutrients = "Apply Sulphur WDG 90% @ 3 kg/acre during active tillering stage.",
                organicBiofertilizer = "Mix 2 kg Azospirillum + 2 kg PSB in 100 kg FYM per acre before basal application.",
                totalUreaBags = 2.9,
                totalDapBags = 2.6,
                totalMopBags = 2.0
            ),
            FertilizerPlanEntity(
                cropName = "Wheat",
                soilType = "Loamy / Alluvial Soil",
                farmAreaAcres = 2.0,
                nitrogenN = 50.0,
                phosphorusP = 25.0,
                potassiumK = 20.0,
                basalDose = "At Sowing: DAP 109 kg (2.2 bags) + MOP 33 kg + Urea 30 kg as basal dose.",
                firstTopDressing = "At 21 Days (CRI Stage): Urea 45 kg/acre + Zinc Sulphate 33% @ 6 kg/acre.",
                secondTopDressing = "At 40-45 Days (Jointing Stage): Urea 30 kg/acre.",
                micronutrients = "Spray 1% Ferrous Sulphate + 0.5% Citric Acid at 30 & 45 DAS for yellowing prevention.",
                organicBiofertilizer = "Apply Azotobacter culture @ 250g / 10kg seeds as seed treatment.",
                totalUreaBags = 2.3,
                totalDapBags = 2.2,
                totalMopBags = 1.3
            ),
            FertilizerPlanEntity(
                cropName = "Cotton",
                soilType = "Black Cotton Soil",
                farmAreaAcres = 3.0,
                nitrogenN = 60.0,
                phosphorusP = 27.0,
                potassiumK = 24.0,
                basalDose = "At Sowing: DAP 176 kg (3.5 bags) + MOP 60 kg + Urea 40 kg.",
                firstTopDressing = "At 30-35 Days (Square Formation): Urea 80 kg/acre + Magnesium Sulphate 10 kg/acre.",
                secondTopDressing = "At 60-65 Days (Boll Formation): Urea 60 kg/acre + MOP 60 kg.",
                micronutrients = "Foliar spray of 1% 19-19-19 water soluble fertilizer + Boron 20% @ 250g/acre at flowering.",
                organicBiofertilizer = "Soil application of Trichoderma viride @ 2.5 kg mixed with 250 kg Vermicompost.",
                totalUreaBags = 3.9,
                totalDapBags = 3.5,
                totalMopBags = 2.4
            ),
            FertilizerPlanEntity(
                cropName = "Sugarcane",
                soilType = "Loamy / Alluvial Soil",
                farmAreaAcres = 1.5,
                nitrogenN = 100.0,
                phosphorusP = 40.0,
                potassiumK = 50.0,
                basalDose = "At Planting: DAP 130 kg (2.6 bags) + MOP 62 kg + Urea 45 kg.",
                firstTopDressing = "At 45 Days (Germination): Urea 65 kg + Sulphur 90% WDG @ 5 kg/acre.",
                secondTopDressing = "At 90 Days & Earthing Up: Urea 110 kg + MOP 63 kg.",
                micronutrients = "Iron Sulphate 10 kg + Zinc Sulphate 10 kg/acre at earthing up stage.",
                organicBiofertilizer = "Apply Acetobacter diazotrophicus @ 4 kg/acre with pressmud/FYM.",
                totalUreaBags = 4.8,
                totalDapBags = 2.6,
                totalMopBags = 2.5
            ),
            FertilizerPlanEntity(
                cropName = "Maize",
                soilType = "Red Sandy Soil",
                farmAreaAcres = 2.0,
                nitrogenN = 60.5,
                phosphorusP = 32.2,
                potassiumK = 26.25,
                basalDose = "At Sowing: DAP 140 kg (2.8 bags) + MOP 44 kg + Urea 35 kg.",
                firstTopDressing = "At 25-30 Days (Knee High Stage): Urea 60 kg/acre.",
                secondTopDressing = "At 45-50 Days (Tasseling Stage): Urea 35 kg/acre + MOP 44 kg.",
                micronutrients = "Zinc Sulphate 21% @ 10 kg/acre applied at soil preparation.",
                organicBiofertilizer = "Azospirillum & PSB seed coating @ 20g/kg seeds.",
                totalUreaBags = 2.8,
                totalDapBags = 2.8,
                totalMopBags = 1.8
            )
        )

        private val initialNpkRequirements = listOf(
            NpkRequirementEntity(cropName = "Paddy (Rice)", soilType = "Loamy / Alluvial Soil", nitrogenN = 48.0, phosphorusP = 24.0, potassiumK = 24.0, remarks = "Standard ICAR recommendation for alluvial soil"),
            NpkRequirementEntity(cropName = "Wheat", soilType = "Loamy / Alluvial Soil", nitrogenN = 50.0, phosphorusP = 25.0, potassiumK = 20.0, remarks = "Recommended for high-yielding varieties"),
            NpkRequirementEntity(cropName = "Cotton", soilType = "Black Cotton Soil", nitrogenN = 60.0, phosphorusP = 30.0, potassiumK = 30.0, remarks = "High nitrogen required during square formation"),
            NpkRequirementEntity(cropName = "Sugarcane", soilType = "Loamy / Alluvial Soil", nitrogenN = 100.0, phosphorusP = 40.0, potassiumK = 50.0, remarks = "Split application across germination and earthing up"),
            NpkRequirementEntity(cropName = "Maize", soilType = "Red Sandy Soil", nitrogenN = 55.0, phosphorusP = 28.0, potassiumK = 25.0, remarks = "Higher leaching loss adjustment for sandy soil"),
            NpkRequirementEntity(cropName = "Potato", soilType = "Loamy / Alluvial Soil", nitrogenN = 75.0, phosphorusP = 40.0, potassiumK = 60.0, remarks = "High K requirement for tuber bulking"),
            NpkRequirementEntity(cropName = "Tomato", soilType = "Red Sandy Soil", nitrogenN = 65.0, phosphorusP = 35.0, potassiumK = 40.0, remarks = "Calcium & Boron required at flowering"),
            NpkRequirementEntity(cropName = "Chilli", soilType = "Black Cotton Soil", nitrogenN = 70.0, phosphorusP = 35.0, potassiumK = 35.0, remarks = "Multiple pickings require split N application"),
            NpkRequirementEntity(cropName = "Soybean", soilType = "Black Cotton Soil", nitrogenN = 12.0, phosphorusP = 32.0, potassiumK = 16.0, remarks = "Fixes atmospheric nitrogen, low N dose needed")
        )

        private val initialIrrigationSchedules = listOf(
            IrrigationScheduleEntity(
                cropName = "Paddy (Rice)",
                soilType = "Loamy / Alluvial Soil",
                farmAreaAcres = 2.5,
                temperatureC = 33.5,
                humidityPct = 62.0,
                soilMoisturePct = 28.5,
                recommendedWaterLiters = 25000.0,
                recommendedWaterMm = 6.2,
                irrigationDurationMinutes = 90,
                irrigationFrequency = "Daily Early Morning (5:30 AM)",
                irrigationType = "Submerged / Flood Basin",
                status = "Urgent Irrigation Needed"
            ),
            IrrigationScheduleEntity(
                cropName = "Wheat",
                soilType = "Loamy / Alluvial Soil",
                farmAreaAcres = 3.0,
                temperatureC = 26.0,
                humidityPct = 48.0,
                soilMoisturePct = 42.0,
                recommendedWaterLiters = 18000.0,
                recommendedWaterMm = 4.5,
                irrigationDurationMinutes = 60,
                irrigationFrequency = "Alternate Days at 6:00 AM",
                irrigationType = "Sprinkler Irrigation",
                status = "Optimal"
            )
        )

        private val initialFieldBoundaries = listOf(
            FieldBoundaryEntity(
                fieldName = "Green Valley Rice Plot",
                cropName = "Paddy (Rice)",
                soilType = "Loamy / Alluvial Soil",
                areaAcres = 2.5,
                perimeterMeters = 420.0,
                centerLatitude = 28.6139,
                centerLongitude = 77.2090,
                waypointsCount = 4,
                coordinatesJson = "[{\"lat\":28.6139,\"lng\":77.2090},{\"lat\":28.6148,\"lng\":77.2092},{\"lat\":28.6146,\"lng\":77.2104},{\"lat\":28.6137,\"lng\":77.2101}]"
            ),
            FieldBoundaryEntity(
                fieldName = "North Wheat Plot B",
                cropName = "Wheat",
                soilType = "Loamy / Alluvial Soil",
                areaAcres = 3.0,
                perimeterMeters = 480.0,
                centerLatitude = 28.6162,
                centerLongitude = 77.2115,
                waypointsCount = 5,
                coordinatesJson = "[{\"lat\":28.6160,\"lng\":77.2110},{\"lat\":28.6168,\"lng\":77.2112},{\"lat\":28.6169,\"lng\":77.2124},{\"lat\":28.6162,\"lng\":77.2122},{\"lat\":28.6158,\"lng\":77.2118}]"
            )
        )
    }
}
