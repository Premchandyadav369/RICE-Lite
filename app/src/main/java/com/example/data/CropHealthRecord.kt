package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_health_records")
data class CropHealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropName: String,
    val fieldPlotName: String = "Plot 1",
    val healthStatus: String = "Healthy",
    val diagnosedDisease: String = "None",
    val geminiDiagnosisText: String = "",
    val recoveryStage: String = "Monitoring",
    val recoveryProgressPct: Int = 100,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val recommendedAction: String = ""
)
