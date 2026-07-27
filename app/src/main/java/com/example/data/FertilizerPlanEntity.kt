package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fertilizer_plans")
data class FertilizerPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cropName: String,
    val soilType: String,
    val farmAreaAcres: Double,
    val nitrogenN: Double, // kg per acre
    val phosphorusP: Double, // kg per acre
    val potassiumK: Double, // kg per acre
    val basalDose: String,
    val firstTopDressing: String,
    val secondTopDressing: String,
    val micronutrients: String,
    val organicBiofertilizer: String,
    val totalUreaBags: Double,
    val totalDapBags: Double,
    val totalMopBags: Double,
    val createdAt: Long = System.currentTimeMillis()
)
