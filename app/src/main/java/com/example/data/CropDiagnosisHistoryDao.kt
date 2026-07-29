package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CropDiagnosisHistoryDao {
    @Query("SELECT * FROM crop_diagnosis_history ORDER BY timestamp DESC")
    fun getAllDiagnosisHistory(): Flow<List<CropDiagnosisHistoryEntity>>

    @Query("SELECT * FROM crop_diagnosis_history WHERE cropName LIKE '%' || :crop || '%' ORDER BY timestamp DESC")
    fun getDiagnosisByCrop(crop: String): Flow<List<CropDiagnosisHistoryEntity>>

    @Query("SELECT * FROM crop_diagnosis_history WHERE fieldPlotName LIKE '%' || :plot || '%' ORDER BY timestamp DESC")
    fun getDiagnosisByPlot(plot: String): Flow<List<CropDiagnosisHistoryEntity>>

    @Query("SELECT * FROM crop_diagnosis_history WHERE id = :id")
    suspend fun getDiagnosisById(id: Int): CropDiagnosisHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(item: CropDiagnosisHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CropDiagnosisHistoryEntity>)

    @Update
    suspend fun updateDiagnosis(item: CropDiagnosisHistoryEntity)

    @Query("DELETE FROM crop_diagnosis_history WHERE id = :id")
    suspend fun deleteDiagnosisById(id: Int)

    @Query("DELETE FROM crop_diagnosis_history")
    suspend fun clearAllHistory()
}
