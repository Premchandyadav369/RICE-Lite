package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FertilizerPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: FertilizerPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<FertilizerPlanEntity>)

    @Query("SELECT * FROM fertilizer_plans ORDER BY createdAt DESC")
    fun getAllPlans(): Flow<List<FertilizerPlanEntity>>

    @Query("DELETE FROM fertilizer_plans WHERE id = :id")
    suspend fun deletePlan(id: Int)

    @Query("DELETE FROM fertilizer_plans")
    suspend fun clearAllPlans()
}
