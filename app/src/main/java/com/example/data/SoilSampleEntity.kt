package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "soil_samples")
data class SoilSampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zoneName: String, // e.g. Zone A1, Zone A2, North-East Corner
    val gridXRatio: Float, // 0.0f to 1.0f relative to field map
    val gridYRatio: Float, // 0.0f to 1.0f relative to field map
    val pH: Float, // e.g. 6.8f
    val organicCarbonPct: Float, // e.g. 0.65%
    val nitrogenKgPerAcre: Float, // e.g. 95 kg/acre
    val phosphorusKgPerAcre: Float, // e.g. 18 kg/acre
    val potassiumKgPerAcre: Float, // e.g. 140 kg/acre
    val electricalConductivity: Float, // e.g. 0.8 dS/m
    val moisturePct: Float, // e.g. 32%
    val soilType: String, // e.g. Regur Black Soil, Red Chalka
    val testDate: String = "2026-07-28"
)

@Dao
interface SoilSampleDao {
    @Query("SELECT * FROM soil_samples ORDER BY id ASC")
    fun getAllSamples(): Flow<List<SoilSampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSample(sample: SoilSampleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSamples(samples: List<SoilSampleEntity>)

    @Query("DELETE FROM soil_samples WHERE id = :id")
    suspend fun deleteSample(id: Long)
}
