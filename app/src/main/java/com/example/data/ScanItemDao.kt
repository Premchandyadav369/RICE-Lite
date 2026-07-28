package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanItemDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanItem>>

    @Query("SELECT * FROM scan_history WHERE scanType = 'CROP' ORDER BY timestamp DESC")
    fun getCropDiseaseScans(): Flow<List<ScanItem>>

    @Query("SELECT * FROM scan_history WHERE cropName LIKE '%' || :query || '%' OR detectedIssue LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanItem>>

    @Query("SELECT * FROM scan_history WHERE id = :id")
    suspend fun getScanById(id: Int): ScanItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scanItem: ScanItem): Long

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteScanById(id: Int)

    @Query("DELETE FROM scan_history")
    suspend fun clearHistory()
}
