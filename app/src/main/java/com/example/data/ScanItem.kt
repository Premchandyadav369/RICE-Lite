package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scanType: String, // "CROP" or "RECEIPT"
    val cropName: String,
    val detectedIssue: String,
    val advice: String,
    val language: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String? = null // local file path to the saved image
)
