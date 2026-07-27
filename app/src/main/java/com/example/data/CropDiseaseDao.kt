package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CropDiseaseDao {
    @Query("SELECT * FROM crop_diseases_cache ORDER BY cropName ASC, diseaseName ASC")
    fun getAllDiseases(): Flow<List<CropDiseaseEntity>>

    @Query("SELECT * FROM crop_diseases_cache WHERE cropName LIKE '%' || :crop || '%' ORDER BY diseaseName ASC")
    fun getDiseasesByCrop(crop: String): Flow<List<CropDiseaseEntity>>

    @Query("SELECT * FROM crop_diseases_cache WHERE diseaseName LIKE '%' || :query || '%' OR visualSymptoms LIKE '%' || :query || '%' OR cropName LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY diseaseName ASC")
    fun searchDiseases(query: String): Flow<List<CropDiseaseEntity>>

    @Query("SELECT * FROM crop_diseases_cache WHERE id = :id LIMIT 1")
    suspend fun getDiseaseById(id: Int): CropDiseaseEntity?

    @Query("SELECT COUNT(*) FROM crop_diseases_cache")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diseases: List<CropDiseaseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(disease: CropDiseaseEntity): Long

    @Query("DELETE FROM crop_diseases_cache")
    suspend fun clearCache()
}
