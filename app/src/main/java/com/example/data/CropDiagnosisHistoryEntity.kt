package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_diagnosis_history")
data class CropDiagnosisHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropName: String,
    val fieldPlotName: String = "Main Plot",
    val diseaseName: String,
    val severityLevel: String = "Moderate",
    val recoveryStage: String = "Initial Diagnosis",
    val recoveryProgressPct: Int = 20, // 0 to 100%
    val geminiDiagnosisText: String,
    val organicRemedy: String = "",
    val chemicalRemedy: String = "",
    val imagePath: String? = null,
    val language: String = "English",
    val timestamp: Long = System.currentTimeMillis(),
    val treatmentNotes: String = ""
)
