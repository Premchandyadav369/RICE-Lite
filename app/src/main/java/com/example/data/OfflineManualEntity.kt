package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "offline_manuals")
data class OfflineManualEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleTe: String,
    val cropCategory: String, // e.g. Paddy, Cotton, Chilli, Turmeric, Soil & Water
    val type: String, // MANUAL, VIDEO, INFOGRAPHIC
    val descriptionEn: String,
    val descriptionTe: String,
    val fileSizeMb: Double,
    val isCachedOffline: Boolean = true,
    val lastUpdated: String = "2026-07-28",
    val contentMarkdownEn: String,
    val contentMarkdownTe: String,
    val infographicUrlOrAsset: String = "",
    val videoDurationMinutes: Int = 0
)

@Dao
interface OfflineManualDao {
    @Query("SELECT * FROM offline_manuals ORDER BY isCachedOffline DESC, id ASC")
    fun getAllManuals(): Flow<List<OfflineManualEntity>>

    @Query("SELECT * FROM offline_manuals WHERE cropCategory = :category OR :category = 'All'")
    fun getManualsByCategory(category: String): Flow<List<OfflineManualEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuals(manuals: List<OfflineManualEntity>)

    @Query("UPDATE offline_manuals SET isCachedOffline = :isCached WHERE id = :id")
    suspend fun updateCacheStatus(id: String, isCached: Boolean)
}
