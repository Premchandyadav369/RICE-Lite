package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "npk_requirements")
data class NpkRequirementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cropName: String,
    val soilType: String,
    val nitrogenN: Double, // kg per acre
    val phosphorusP: Double, // kg per acre
    val potassiumK: Double, // kg per acre
    val remarks: String = ""
)
