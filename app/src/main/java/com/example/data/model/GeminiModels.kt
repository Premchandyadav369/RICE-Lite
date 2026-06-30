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
