package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_boundaries")
data class FieldBoundaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fieldName: String,
    val cropName: String,
    val soilType: String,
    val areaAcres: Double,
    val perimeterMeters: Double,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val waypointsCount: Int,
    val coordinatesJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
