package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    // Saved Content Queries
    @Query("SELECT * FROM saved_content ORDER BY timestamp DESC")
    fun getAllSavedContent(): Flow<List<SavedContentEntity>>

    @Query("SELECT * FROM saved_content WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteSavedContent(): Flow<List<SavedContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedContent(item: SavedContentEntity): Long

    @Query("UPDATE saved_content SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM saved_content WHERE id = :id")
    suspend fun deleteSavedContent(id: Long)

    @Query("DELETE FROM saved_content")
    suspend fun clearAllSavedContent()

    // Scheduled Posts Queries
    @Query("SELECT * FROM scheduled_posts ORDER BY scheduledTimeMs ASC")
    fun getAllScheduledPosts(): Flow<List<ScheduledPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledPost(post: ScheduledPostEntity): Long

    @Query("UPDATE scheduled_posts SET status = :status WHERE id = :id")
    suspend fun updatePostStatus(id: Long, status: String)

    @Query("DELETE FROM scheduled_posts WHERE id = :id")
    suspend fun deleteScheduledPost(id: Long)

    @Query("DELETE FROM scheduled_posts")
    suspend fun clearAllScheduledPosts()

    // Brand Kit Queries
    @Query("SELECT * FROM brand_kits ORDER BY id DESC LIMIT 1")
    fun getActiveBrandKit(): Flow<BrandKitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrandKit(kit: BrandKitEntity): Long

    // Analytics Queries
    @Query("SELECT * FROM analytics_data ORDER BY recordDateMs DESC")
    fun getAllAnalytics(): Flow<List<AnalyticsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalytics(data: AnalyticsEntity)
}
