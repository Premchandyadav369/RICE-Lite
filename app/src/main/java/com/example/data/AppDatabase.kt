package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ScanItem::class, CropDiseaseEntity::class, FertilizerPlanEntity::class, NpkRequirementEntity::class, IrrigationScheduleEntity::class, FieldBoundaryEntity::class, OfflineManualEntity::class, SoilSampleEntity::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanItemDao(): ScanItemDao
    abstract fun cropDiseaseDao(): CropDiseaseDao
    abstract fun fertilizerPlanDao(): FertilizerPlanDao
    abstract fun npkRequirementDao(): NpkRequirementDao
    abstract fun irrigationScheduleDao(): IrrigationScheduleDao
    abstract fun fieldBoundaryDao(): FieldBoundaryDao
    abstract fun offlineManualDao(): OfflineManualDao
    abstract fun soilSampleDao(): SoilSampleDao

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
                            INSTANCE?.offlineManualDao()?.insertManuals(initialOfflineManuals)
                            INSTANCE?.soilSampleDao()?.insertSamples(initialSoilSamples)
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                INSTANCE?.offlineManualDao()?.insertManuals(initialOfflineManuals)
                                INSTANCE?.soilSampleDao()?.insertSamples(initialSoilSamples)
                            } catch (_: Exception) {}
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
                        instance.offlineManualDao().insertManuals(initialOfflineManuals)
                        instance.soilSampleDao().insertSamples(initialSoilSamples)
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

        private val initialOfflineManuals = listOf(
            OfflineManualEntity(
                id = "man_cotton_pink_bollworm",
                titleEn = "Cotton Pink Bollworm Organic Management Guide",
                titleTe = "పత్తి పంటలో గులాబీ రంగు పురుగు సమగ్ర యాజమాన్యం",
                cropCategory = "Cotton",
                type = "MANUAL",
                descriptionEn = "ANGRAU IPM guidelines for pheromone trap installation and neem oil spray schedules.",
                descriptionTe = "లింగ ఆకర్షణ బుట్టల అమరిక మరియు వేప నూనె పిచికారీ విధానాలు.",
                fileSizeMb = 2.4,
                isCachedOffline = true,
                lastUpdated = "2026-07-28",
                contentMarkdownEn = "# Pink Bollworm Prevention\n\n1. Install 8-10 Pheromone Traps per acre at 45 DAS.\n2. Spray Neem Oil 10,000 PPM @ 2ml/litre at evening hours.\n3. Release Trichogramma parasitoid wasps @ 50,000/acre.",
                contentMarkdownTe = "# గులాబీ రంగు పురుగు నివారణ\n\n1. ఎకరాకు 8 నుండి 10 లింగ ఆకర్షణ బుట్టలు అమర్చండి.\n2. లీటరు నీటికి 2 మి.లీ వేప నూనె కలిపి సాయంత్రం వేళ పిచికారీ చేయండి.\n3. ఎకరాకు 50,000 ట్రైకోగ్రామా కార్డులు విడుదల చేయండి."
            ),
            OfflineManualEntity(
                id = "vid_chilli_black_thrips",
                titleEn = "Chilli Black Thrips Symptoms & Bio-Pesticide Spray Video",
                titleTe = "మిర్చి నల్ల తామర పురుగు నివారణ వీడియో గైడ్",
                cropCategory = "Chilli",
                type = "VIDEO",
                descriptionEn = "Detailed visual video showing leaf curling vs thrips infestation and Beauveria bassiana spray.",
                descriptionTe = "మిర్చి ఆకుల ముడుత, నల్ల తామర పురుగు గుర్తింపు మరియు జీవ సిలీంధ్ర మందుల పిచికారీ వీడియో.",
                fileSizeMb = 12.8,
                isCachedOffline = true,
                videoDurationMinutes = 6,
                lastUpdated = "2026-07-28",
                contentMarkdownEn = "Video Tutorial: Learn how to identify early black thrips under leaves. Mix Beauveria bassiana @ 5g/litre with sticky wetting agent.",
                contentMarkdownTe = "వీడియో గైడ్: ఆకుల అడుగున తామర పురుగుల గుర్తింపు మరియు బ్యూవేరియా బేసియానా లీటరుకు 5 గ్రాముల చొప్పున కలిపి పిచికారీ చేయుట."
            ),
            OfflineManualEntity(
                id = "inf_paddy_fertilizer_calendar",
                titleEn = "Paddy Split NPK Fertilizer Schedule Infographic",
                titleTe = "వరి పంట నత్రజని, భాస్వరం, పొటాష్ మోతాదు ఇన్ఫోగ్రాఫిక్",
                cropCategory = "Paddy",
                type = "INFOGRAPHIC",
                descriptionEn = "Visual step-by-step chart showing exact bag quantities for Basal, Tillering, and Panicle stages.",
                descriptionTe = "దుక్కిలో, పిలక దశలో మరియు పొట్ట దశలో యూరియా, డిఎపి, పొటాష్ సంచుల చార్ట్.",
                fileSizeMb = 1.5,
                isCachedOffline = true,
                lastUpdated = "2026-07-28",
                contentMarkdownEn = "Infographic Chart: Basal (DAP 1 Bag + MOP 0.5 Bag) -> 25 DAS (Urea 1 Bag + Zinc) -> 50 DAS (Urea 0.75 Bag + MOP 0.5 Bag).",
                contentMarkdownTe = "ఇన్ఫోగ్రాఫిక్ చార్ట్: దుక్కిలో డిఎపి 1 సంచి + పొటాష్ అర సంచి -> 25 రోజులకు యూరియా 1 సంచి -> 50 రోజులకు యూరియా ముప్పావు సంచి."
            ),
            OfflineManualEntity(
                id = "man_turmeric_rhizome_rot",
                titleEn = "Turmeric Rhizome Rot Prevention & Soil Drenching",
                titleTe = "పసుపు కొమ్ము కుళ్ళు తెగులు మరియు భూమి తడిపే విధానం",
                cropCategory = "Turmeric",
                type = "MANUAL",
                descriptionEn = "PJTSAU agricultural advisory for Trichoderma soil treatment and raised bed ridge drainage.",
                descriptionTe = "ట్రైకోడెర్మా విరిడే భూమి శుద్ధి మరియు బోదెల సాగు ద్వారా నీటి నిల్వ నివారణ.",
                fileSizeMb = 3.1,
                isCachedOffline = true,
                lastUpdated = "2026-07-28",
                contentMarkdownEn = "1. Avoid water stagnation in turmeric beds.\n2. Mix 2.5 kg Trichoderma in 100 kg FYM.\n3. Drench soil with Copper Oxychloride @ 3g/litre.",
                contentMarkdownTe = "1. పసుపు మడులలో నీరు నిల్వ ఉండకుండా చూడండి.\n2. 2.5 కిలోల ట్రైకోడెర్మాను 100 కిలోల పశువుల ఎరువులో కలిపి వేయండి.\n3. లీటరు నీటికి 3 గ్రాముల కాపర్ ఆక్సిక్లోరైడ్ కలిపి మొదళ్ల వద్ద తడపండి."
            ),
            OfflineManualEntity(
                id = "vid_drip_fertigation_maintenance",
                titleEn = "Drip Irrigation Acid Flushing & Filter Cleaning Video",
                titleTe = "బిందు సేద్యం ల్యాటరల్స్ క్లీనింగ్ మరియు యాసిడ్ ఫ్లషింగ్",
                cropCategory = "Soil & Water",
                type = "VIDEO",
                descriptionEn = "Step-by-step guide on hydrochloric acid flushing to clear calcium clogging in drippers.",
                descriptionTe = "డ్రిప్పర్ల డ్రిప్ రంధ్రాల ఉప్పు పేరుకుపోవడం నివారణకు యాసిడ్ ఫ్లషింగ్ వీడియో.",
                fileSizeMb = 14.2,
                isCachedOffline = true,
                videoDurationMinutes = 8,
                lastUpdated = "2026-07-28",
                contentMarkdownEn = "Flushing Protocol: Use 0.2% Hydrochloric Acid (HCl). Run drip for 30 minutes, inject acid, seal system for 12 hours, then flush with clean water.",
                contentMarkdownTe = "ఫ్లషింగ్ విధానం: 0.2% హైడ్రోక్లోరిక్ యాసిడ్ వాడండి. 12 గంటల పాటు పైపులలో ఉంచి తరువాత మంచినీటితో ఫ్లష్ చేయండి."
            )
        )

        private val initialSoilSamples = listOf(
            SoilSampleEntity(zoneName = "North-West Zone A1", gridXRatio = 0.2f, gridYRatio = 0.25f, pH = 6.8f, organicCarbonPct = 0.72f, nitrogenKgPerAcre = 110f, phosphorusKgPerAcre = 24f, potassiumKgPerAcre = 160f, electricalConductivity = 0.6f, moisturePct = 34f, soilType = "Regur Black Cotton Soil"),
            SoilSampleEntity(zoneName = "North-East Zone A2", gridXRatio = 0.75f, gridYRatio = 0.2f, pH = 7.4f, organicCarbonPct = 0.45f, nitrogenKgPerAcre = 72f, phosphorusKgPerAcre = 14f, potassiumKgPerAcre = 110f, electricalConductivity = 1.2f, moisturePct = 26f, soilType = "Red Chalka Soil"),
            SoilSampleEntity(zoneName = "Central Zone B1", gridXRatio = 0.5f, gridYRatio = 0.5f, pH = 7.0f, organicCarbonPct = 0.68f, nitrogenKgPerAcre = 98f, phosphorusKgPerAcre = 20f, potassiumKgPerAcre = 145f, electricalConductivity = 0.7f, moisturePct = 30f, soilType = "Alluvial Loam"),
            SoilSampleEntity(zoneName = "South-West Zone C1", gridXRatio = 0.25f, gridYRatio = 0.8f, pH = 8.1f, organicCarbonPct = 0.38f, nitrogenKgPerAcre = 60f, phosphorusKgPerAcre = 10f, potassiumKgPerAcre = 90f, electricalConductivity = 1.8f, moisturePct = 22f, soilType = "Alkaline Heavy Clay"),
            SoilSampleEntity(zoneName = "South-East Zone C2", gridXRatio = 0.8f, gridYRatio = 0.75f, pH = 6.5f, organicCarbonPct = 0.82f, nitrogenKgPerAcre = 125f, phosphorusKgPerAcre = 28f, potassiumKgPerAcre = 175f, electricalConductivity = 0.5f, moisturePct = 38f, soilType = "Fertile Silt Loam")
        )
    }
}
