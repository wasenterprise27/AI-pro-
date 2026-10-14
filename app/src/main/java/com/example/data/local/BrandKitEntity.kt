package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_kits")
data class BrandKitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brandName: String = "Infinity AI",
    val tagline: String = "Create Viral Content in Seconds",
    val primaryColorHex: String = "#6366F1", // Indigo
    val secondaryColorHex: String = "#06B6D4", // Cyan
    val accentColorHex: String = "#EC4899", // Pink
    val backgroundColorHex: String = "#0F172A", // Dark Slate
    val primaryFont: String = "Inter / Modern Sans",
    val secondaryFont: String = "JetBrains Mono / Code Display",
    val logoType: String = "MONOGRAM", // MONOGRAM, ICON, TEXT_BADGE, IMAGE_URI
    val logoPlaceholderText: String = "⚡ INF",
    val logoIconName: String = "Bolt", // Bolt, Sparkle, Rocket, Diamond, Fire, Star, Crown, Brush
    val logoImageUri: String? = null,
    val voiceTone: String = "Viral & High-Energy",
    val targetAudience: String = "Creators, Marketers & E-Commerce Brands",
    val defaultHashtags: String = "#InfinityAI #ViralContent #CreatorEconomy #MarketingPro",
    val brandKeywords: String = "Innovation, Speed, High Retention, Scalable Growth",
    val ctaTemplate: String = "Tap the link in bio to supercharge your reach today! 🚀",
    val isDefault: Boolean = true,
    val updatedAtMs: Long = System.currentTimeMillis()
)
