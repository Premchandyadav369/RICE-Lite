package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "irrigation_schedules")
data class IrrigationScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cropName: String,
    val soilType: String,
    val farmAreaAcres: Double,
    val temperatureC: Double,
    val humidityPct: Double,
    val soilMoisturePct: Double,
    val recommendedWaterLiters: Double,
    val recommendedWaterMm: Double,
    val irrigationDurationMinutes: Int,
    val irrigationFrequency: String,
    val irrigationType: String,
    val status: String, // "Optimal", "Urgent Irrigation Needed", "Moisture Sufficient"
    val createdAt: Long = System.currentTimeMillis()
)
