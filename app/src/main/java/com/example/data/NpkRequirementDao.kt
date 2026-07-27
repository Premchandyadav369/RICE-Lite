package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NpkRequirementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(requirement: NpkRequirementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirements(requirements: List<NpkRequirementEntity>)

    @Query("SELECT * FROM npk_requirements ORDER BY cropName ASC")
    fun getAllRequirements(): Flow<List<NpkRequirementEntity>>

    @Query("SELECT * FROM npk_requirements WHERE cropName = :cropName AND soilType = :soilType LIMIT 1")
    suspend fun getRequirementForCropAndSoil(cropName: String, soilType: String): NpkRequirementEntity?

    @Query("DELETE FROM npk_requirements WHERE id = :id")
    suspend fun deleteRequirement(id: Int)
}
