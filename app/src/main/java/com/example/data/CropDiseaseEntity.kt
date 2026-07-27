package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_diseases_cache")
data class CropDiseaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropName: String,
    val diseaseName: String,
    val scientificName: String,
    val category: String, // Fungal, Bacterial, Viral, Insect Pest, Nutrient Deficiency
    val visualSymptoms: String,
    val organicTreatment: String,
    val chemicalTreatment: String,
    val dosageInstruction: String,
    val preventiveMeasures: String,
    val severity: String, // Low, Medium, High, Critical
    val isOfflineCached: Boolean = true
)
