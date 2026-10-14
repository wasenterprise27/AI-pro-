package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_posts")
data class ScheduledPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val platform: String, // TikTok, YouTube, Instagram, Facebook, X, LinkedIn, Pinterest, Threads
    val postBody: String,
    val scheduledTimeMs: Long,
    val status: String = "SCHEDULED", // DRAFT, SCHEDULED, PUBLISHED
    val mediaUriOrPrompt: String = "",
    val campaignName: String = "General"
)
