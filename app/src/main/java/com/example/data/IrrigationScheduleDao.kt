package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IrrigationScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: IrrigationScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<IrrigationScheduleEntity>)

    @Query("SELECT * FROM irrigation_schedules ORDER BY createdAt DESC")
    fun getAllSchedules(): Flow<List<IrrigationScheduleEntity>>

    @Query("DELETE FROM irrigation_schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Int)

    @Query("DELETE FROM irrigation_schedules")
    suspend fun clearAllSchedules()
}
