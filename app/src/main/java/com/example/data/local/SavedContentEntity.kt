package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_content")
data class SavedContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val contentType: String, // CAPTION, VIDEO_SCRIPT, IMAGE_PROMPT, AFFILIATE, BLOG, EMAIL, AD_COPY, STORY
    val targetPlatform: String, // TikTok, YouTube, Instagram, Facebook, X, LinkedIn, Pinterest, Threads
    val topicOrUrl: String,
    val hookText: String = "",
    val mainBodyText: String = "",
    val ctaText: String = "",
    val hashtags: String = "",
    val scriptJson: String = "", // Scene breakdown for video scripts
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
