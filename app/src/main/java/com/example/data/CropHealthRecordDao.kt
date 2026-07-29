package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CropHealthRecordDao {
    @Query("SELECT * FROM crop_health_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<CropHealthRecord>>

    @Query("SELECT * FROM crop_health_records WHERE cropName LIKE '%' || :crop || '%' ORDER BY timestamp DESC")
    fun getRecordsByCrop(crop: String): Flow<List<CropHealthRecord>>

    @Query("SELECT * FROM crop_health_records WHERE id = :id")
    suspend fun getRecordById(id: Int): CropHealthRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CropHealthRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<CropHealthRecord>)

    @Update
    suspend fun updateRecord(record: CropHealthRecord)

    @Query("DELETE FROM crop_health_records WHERE id = :id")
    suspend fun deleteRecordById(id: Int)

    @Query("DELETE FROM crop_health_records")
    suspend fun clearAllRecords()
}
