package com.example.data.model

import kotlinx.serialization.Serializable

// --- Common Gemini API Data Classes ---

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val responseSchema: ResponseSchema? = null,
    val temperature: Float? = null
)

@Serializable
data class ResponseSchema(
    val type: String,
    val properties: Map<String, SchemaProperty>? = null,
    val required: List<String>? = null,
    val items: ResponseSchema? = null
)

@Serializable
data class SchemaProperty(
    val type: String,
    val description: String? = null,
    val properties: Map<String, SchemaProperty>? = null,
    val items: ResponseSchema? = null,
    val required: List<String>? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val content: Content
)


// --- Crop Analysis Result Structure ---

@Serializable
data class CropDiagnosis(
    val crop_name: String,
    val health_status: String, // "Healthy" or "Diseased"
    val disease_name: String, // Name of disease, or "None"
    val confidence: Float, // e.g. 0.92
    val symptoms: List<String>,
    val causes: List<String>,
    val treatments: TreatmentPlan
)

@Serializable
data class TreatmentPlan(
    val immediate_actions: List<String>,
    val organic_control: List<String>,
    val chemical_control: List<String>,
    val preventive_measures: List<String>
)

// --- Market Price Prediction Structure ---

@Serializable
data class CropMarketPrediction(
    val crop_name: String,
    val current_price: String,
    val predicted_price_change: String,
    val trend_direction: String, // "Up", "Down", "Stable"
    val confidence: Float,
    val predictions_7_days: List<DailyPricePrediction>,
    val market_sentiment: String,
    val recommendation: String,
    val demand_supply_index: String
)

@Serializable
data class DailyPricePrediction(
    val day: String,
    val price: Double,
    val percentage_change: Double
)

// --- Soil & Precision Fertilizer Calculator Structure ---

@Serializable
data class SoilFertilizerPlan(
    val crop_name: String,
    val soil_type: String,
    val land_area_acres: Double,
    val urea_kg: Double,
    val dap_kg: Double,
    val mop_kg: Double,
    val organic_compost_kg: Double,
    val schedule: List<FertilizerStage>,
    val micronutrient_advice: String,
    val cost_estimate_inr: String
)

@Serializable
data class FertilizerStage(
    val stage_name: String,
    val timing: String,
    val recommended_dose: String
)

// --- Pest Risk Radar Structure ---

@Serializable
data class PestRiskAssessment(
    val crop_name: String,
    val risk_level: String,
    val weather_summary: String,
    val primary_threats: List<PestThreat>,
    val early_warning_advice: List<String>
)

@Serializable
data class PestThreat(
    val pest_or_fungus: String,
    val probability: String,
    val symptoms_to_watch: List<String>,
    val preventive_action: String
)

// --- Pest Identification & Organic Control Structure ---

@Serializable
data class PestIdentificationResult(
    val pest_name: String,
    val scientific_name: String = "",
    val crop_affected: String = "",
    val infestation_level: String = "Moderate", // "Low", "Moderate", "Severe", "Critical"
    val confidence: Float = 0.90f,
    val damage_symptoms: List<String> = emptyList(),
    val organic_controls: List<String> = emptyList(),
    val biological_controls: List<String> = emptyList(),
    val chemical_controls: List<String> = emptyList(),
    val preventive_measures: List<String> = emptyList()
)

// --- Telugu Government Document OCR Structure ---

@Serializable
data class TeluguDocOcrResult(
    val document_type: String = "Pattadar Passbook (పట్టాదారు పాస్ పుస్తకం)",
    val farmer_name_telugu: String = "",
    val farmer_name_english: String = "",
    val father_or_husband_name: String = "",
    val passbook_or_khata_number: String = "",
    val survey_numbers: List<String> = emptyList(),
    val district: String = "",
    val mandal_or_village: String = "",
    val total_land_acres: Double = 0.0,
    val crop_history: List<String> = emptyList(),
    val aadhaar_masked: String = "",
    val confidence_score: Float = 0.95f,
    val raw_telugu_text: String = "",
    val verification_status: String = "Verified Government Record"
)


