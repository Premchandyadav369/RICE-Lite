package com.example.data

import kotlinx.coroutines.flow.Flow

class CropDiseaseRepository(private val cropDiseaseDao: CropDiseaseDao) {

    val allDiseases: Flow<List<CropDiseaseEntity>> = cropDiseaseDao.getAllDiseases()

    fun getDiseasesByCrop(cropName: String): Flow<List<CropDiseaseEntity>> {
        return cropDiseaseDao.getDiseasesByCrop(cropName)
    }

    fun searchDiseases(query: String): Flow<List<CropDiseaseEntity>> {
        return cropDiseaseDao.searchDiseases(query)
    }

    suspend fun getDiseaseById(id: Int): CropDiseaseEntity? {
        return cropDiseaseDao.getDiseaseById(id)
    }

    suspend fun ensureInitialCache() {
        if (cropDiseaseDao.getCount() == 0) {
            val initialList = getSeedDiseases()
            cropDiseaseDao.insertAll(initialList)
        }
    }

    private fun getSeedDiseases(): List<CropDiseaseEntity> {
        return listOf(
            CropDiseaseEntity(
                cropName = "Tomato",
                diseaseName = "Early Blight",
                scientificName = "Alternaria solani",
                category = "Fungal",
                visualSymptoms = "Dark brown/black spots with concentric target-like rings on mature leaves, yellow halos around spots, defoliation.",
                organicTreatment = "Spray Neem Oil 10,000 ppm @ 3ml/L water + Trichoderma viride bio-fungicide @ 5g/L.",
                chemicalTreatment = "Spray Mancozeb 75% WP @ 2g/L or Difenoconazole 25% EC @ 0.5ml/L at 10-day intervals.",
                dosageInstruction = "Apply 200 Liters spray solution per acre using hollow cone nozzle.",
                preventiveMeasures = "Maintain 60cm row spacing, avoid overhead irrigation, rotate crops with non-solanaceous crops.",
                severity = "High"
            ),
            CropDiseaseEntity(
                cropName = "Tomato",
                diseaseName = "Late Blight",
                scientificName = "Phytophthora infestans",
                category = "Fungal",
                visualSymptoms = "Water-soaked dark brown spots starting from leaf tips/margins, white powdery growth on undersides during wet weather.",
                organicTreatment = "Spray Copper Oxychloride 50% WP @ 2.5g/L + Trichoderma harzianum @ 5g/L.",
                chemicalTreatment = "Spray Cymoxanil 8% + Mancozeb 64% WP @ 2g/L or Azoxystrobin 23% SC @ 1ml/L.",
                dosageInstruction = "Apply 200 Liters per acre immediately upon first symptom appearance under foggy/humid conditions.",
                preventiveMeasures = "Remove infected crop debris, ensure good field drainage, avoid late evening watering.",
                severity = "Critical"
            ),
            CropDiseaseEntity(
                cropName = "Tomato",
                diseaseName = "Tomato Leaf Curl Virus (ToLCV)",
                scientificName = "Begomovirus (Whitefly Vector)",
                category = "Viral",
                visualSymptoms = "Severe upward curling, yellowing, dwarfing of leaves, stunted plant growth with no flower setting.",
                organicTreatment = "Yellow Sticky Traps @ 15 traps/acre for whitefly control + Neem Seed Kernel Extract (NSKE) 5% spray.",
                chemicalTreatment = "Spray Imidacloprid 17.8% SL @ 0.5ml/L or Acetamiprid 20% SP @ 0.2g/L to eliminate vector whiteflies.",
                dosageInstruction = "Spray early morning or late evening. Alternate chemicals to avoid whitefly resistance.",
                preventiveMeasures = "Use insect-proof nursery mesh (40-60 mesh), spray vector early in seedling phase.",
                severity = "High"
            ),
            CropDiseaseEntity(
                cropName = "Rice / Paddy",
                diseaseName = "Rice Blast",
                scientificName = "Magnaporthe oryzae",
                category = "Fungal",
                visualSymptoms = "Spindle or diamond-shaped lesions with grey centers and reddish-brown borders on leaf blades, node blast, neck blast.",
                organicTreatment = "Spray Pseudomonas fluorescens 1% WP @ 10g/L or Neem Oil @ 5ml/L.",
                chemicalTreatment = "Spray Tricyclazole 75% WP @ 0.6g/L or Isoprothiolane 40% EC @ 1.5ml/L.",
                dosageInstruction = "Apply 200 Liters solution per acre at tillering and panicle initiation stage.",
                preventiveMeasures = "Avoid excessive Nitrogen fertilizer application, use blast-resistant seeds like PR 126 or Pusa 1121.",
                severity = "Critical"
            ),
            CropDiseaseEntity(
                cropName = "Rice / Paddy",
                diseaseName = "Bacterial Leaf Blight (BLB)",
                scientificName = "Xanthomonas oryzae",
                category = "Bacterial",
                visualSymptoms = "Water-soaked lesions turning yellow-white along leaf margins with wavy edges, 'Kresek' wilt in seedlings.",
                organicTreatment = "Spray fresh Cow Dung slurry (20kg cow dung + 5kg neem cake in 200L water) filtered solution.",
                chemicalTreatment = "Spray Copper Hydroxide 77% WP @ 2g/L + Streptocycline (Streptomycin) @ 0.1g/L.",
                dosageInstruction = "Dissolve 18g Streptocycline pouch in 150-200 Liters water per acre.",
                preventiveMeasures = "Drain standing water temporarily, refrain from clipping seedling leaf tips during transplanting.",
                severity = "High"
            ),
            CropDiseaseEntity(
                cropName = "Wheat",
                diseaseName = "Yellow / Stripe Rust",
                scientificName = "Puccinia striiformis",
                category = "Fungal",
                visualSymptoms = "Bright yellow pustules arranged in linear stripes parallel to leaf veins, yellow powder rub off on fingers.",
                organicTreatment = "Apply Neem Cake @ 100kg/acre to soil + Bio-agent Bacillus subtilis spray.",
                chemicalTreatment = "Spray Propiconazole 25% EC (Tilt) @ 1ml/L or Tebuconazole 25.9% EC @ 1ml/L.",
                dosageInstruction = "Apply 150-200 Liters per acre at flag leaf emergence stage.",
                preventiveMeasures = "Grow resistant varieties (HD 3086, DBW 187, HD 3226), monitor field weekly in cold foggy weather.",
                severity = "Critical"
            ),
            CropDiseaseEntity(
                cropName = "Cotton",
                diseaseName = "Pink Bollworm",
                scientificName = "Pectinophora gossypiella",
                category = "Insect Pest",
                visualSymptoms = "Rosetted un-opened flowers, bored green bolls with entry holes plugged with frass, pink caterpillar inside.",
                organicTreatment = "Install Pink Bollworm Pheromone Traps @ 5/acre for monitoring, release Trichogramma chilonis @ 60,000/acre.",
                chemicalTreatment = "Spray Emamectin Benzoate 5% SG @ 0.4g/L or Spinetoram 11.7% SC @ 0.9ml/L.",
                dosageInstruction = "Spray at 60-90 days after sowing when moth catch exceeds 8 moths/trap/night for 3 consecutive days.",
                preventiveMeasures = "Maintain crop holiday, do not extend cotton crop beyond December, shred crop stalks post harvest.",
                severity = "Critical"
            ),
            CropDiseaseEntity(
                cropName = "Potato",
                diseaseName = "Black Scurf & Stem Canker",
                scientificName = "Rhizoctonia solani",
                category = "Fungal",
                visualSymptoms = "Hard dark brown or black dirt-like sclerotia spots on tuber skin that do not wash off, reddish brown stem lesions.",
                organicTreatment = "Seed tuber treatment with Trichoderma viride @ 10g/kg tuber prior to planting.",
                chemicalTreatment = "Spray/Drench Azoxystrobin 23% SC @ 1ml/L or Pencycuron 250 FS @ 2ml/L of water.",
                dosageInstruction = "Treat seed tubers by dipping for 10 minutes or direct furrow soil application.",
                preventiveMeasures = "Use certified disease-free tubers, shallow planting in warm soil, field crop rotation with mustard.",
                severity = "Medium"
            ),
            CropDiseaseEntity(
                cropName = "Chilli",
                diseaseName = "Chilli Anthracnose / Die-Back",
                scientificName = "Colletotrichum capsici",
                category = "Fungal",
                visualSymptoms = "Circular dark sunken necrotic lesions on ripening chilli pods with acervuli rings, drying of branches from top down.",
                organicTreatment = "Spray Pseudomonas fluorescens @ 10g/L or Trichoderma viride @ 5g/L at flowering.",
                chemicalTreatment = "Spray Azoxystrobin 18.2% + Difenoconazole 11.4% SC @ 1ml/L or Copper Oxychloride @ 3g/L.",
                dosageInstruction = "Apply 200 Liters spray per acre at pod development stage.",
                preventiveMeasures = "Collect and burn infected fallen fruits, avoid dense canopy by balanced pruning.",
                severity = "High"
            )
        )
    }
}
