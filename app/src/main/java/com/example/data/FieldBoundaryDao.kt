package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldBoundaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoundary(boundary: FieldBoundaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoundaries(boundaries: List<FieldBoundaryEntity>)

    @Query("SELECT * FROM field_boundaries ORDER BY createdAt DESC")
    fun getAllBoundaries(): Flow<List<FieldBoundaryEntity>>

    @Query("DELETE FROM field_boundaries WHERE id = :id")
    suspend fun deleteBoundary(id: Int)

    @Query("DELETE FROM field_boundaries")
    suspend fun clearAllBoundaries()
}
