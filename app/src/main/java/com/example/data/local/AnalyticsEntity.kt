package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_data")
data class AnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String,
    val totalViews: Long,
    val totalLikes: Long,
    val totalShares: Long,
    val totalFollowers: Long,
    val engagementRatePercent: Float,
    val estimatedRevenueUsd: Double,
    val bestPostingTime: String,
    val recordDateMs: Long = System.currentTimeMillis()
)
