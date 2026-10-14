package com.example.data.remote

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AdobeAssetType {
    PHOTO,
    DESIGN,
    VECTOR,
    BRAND_KIT,
    ILLUSTRATION
}

data class AdobeCcAsset(
    val id: String,
    val name: String,
    val type: AdobeAssetType,
    val fileFormat: String, // e.g. "PSD", "AI", "JPEG", "RAW", "PNG"
    val libraryName: String,
    val thumbnailUrl: String,
    val fullResUrl: String,
    val fileSizeFormatted: String,
    val dimensions: String,
    val tags: List<String>,
    val colorPaletteHex: List<String>,
    val modifiedDate: String,
    val isFavorite: Boolean = false
)

data class AdobeCcLibrary(
    val id: String,
    val name: String,
    val assetCount: Int,
    val category: String, // e.g. "Photography", "Branding", "Social Graphics"
    val lastSyncedDate: String
)

data class AdobeCcImportResult(
    val asset: AdobeCcAsset,
    val extractedPrompt: String,
    val metadataSummary: String
)

object AdobeCreativeCloudRepository {

    private val mockLibraries = listOf(
        AdobeCcLibrary(
            id = "lib_photos_2026",
            name = "Product Photography 2026",
            assetCount = 28,
            category = "Photography",
            lastSyncedDate = "Just now"
        ),
        AdobeCcLibrary(
            id = "lib_social_graphics",
            name = "Social Campaign Designs",
            assetCount = 42,
            category = "Social Graphics",
            lastSyncedDate = "10 mins ago"
        ),
        AdobeCcLibrary(
            id = "lib_brand_kit",
            name = "Brand Identity & Vectors",
            assetCount = 15,
            category = "Branding",
            lastSyncedDate = "1 hour ago"
        ),
        AdobeCcLibrary(
            id = "lib_stock_favorites",
            name = "Adobe Stock Curated",
            assetCount = 64,
            category = "Stock Assets",
            lastSyncedDate = "Today, 10:15 AM"
        )
    )

    private val mockAssets = listOf(
        AdobeCcAsset(
            id = "cc_asset_001",
            name = "Wireless Earbuds Studio Hero.psd",
            type = AdobeAssetType.PHOTO,
            fileFormat = "PSD",
            libraryName = "Product Photography 2026",
            thumbnailUrl = "https://picsum.photos/seed/earbuds/400/400",
            fullResUrl = "https://picsum.photos/seed/earbuds/1200/1200",
            fileSizeFormatted = "48.2 MB",
            dimensions = "3840 x 2160",
            tags = listOf("Studio Lighting", "Cyberpunk Cyan", "Product Shot", "3D Floating"),
            colorPaletteHex = listOf("#00E5FF", "#111827", "#7C3AED", "#FFFFFF"),
            modifiedDate = "2026-07-27"
        ),
        AdobeCcAsset(
            id = "cc_asset_002",
            name = "Viral Promo Story Layout.express",
            type = AdobeAssetType.DESIGN,
            fileFormat = "EXPRESS",
            libraryName = "Social Campaign Designs",
            thumbnailUrl = "https://picsum.photos/seed/socialbanner/400/700",
            fullResUrl = "https://picsum.photos/seed/socialbanner/1080/1920",
            fileSizeFormatted = "12.4 MB",
            dimensions = "1080 x 1920",
            tags = listOf("Typography", "High Contrast", "Gradient Violet", "TikTok Story"),
            colorPaletteHex = listOf("#8B5CF6", "#EC4899", "#0F172A", "#F8FAFC"),
            modifiedDate = "2026-07-26"
        ),
        AdobeCcAsset(
            id = "cc_asset_003",
            name = "Geometric Brand Badge Logo.ai",
            type = AdobeAssetType.VECTOR,
            fileFormat = "AI",
            libraryName = "Brand Identity & Vectors",
            thumbnailUrl = "https://picsum.photos/seed/vectorlogo/400/400",
            fullResUrl = "https://picsum.photos/seed/vectorlogo/1000/1000",
            fileSizeFormatted = "4.8 MB",
            dimensions = "Vector (Infinite)",
            tags = listOf("Minimalist Logo", "Geometric", "Flat Design", "Scalable"),
            colorPaletteHex = listOf("#06B6D4", "#0F172A", "#10B981"),
            modifiedDate = "2026-07-25"
        ),
        AdobeCcAsset(
            id = "cc_asset_004",
            name = "Aesthetic Cosmetic Bottle Shot.raw",
            type = AdobeAssetType.PHOTO,
            fileFormat = "RAW",
            libraryName = "Product Photography 2026",
            thumbnailUrl = "https://picsum.photos/seed/cosmetics/400/400",
            fullResUrl = "https://picsum.photos/seed/cosmetics/1200/1200",
            fileSizeFormatted = "62.1 MB",
            dimensions = "4000 x 3000",
            tags = listOf("Natural Shadows", "Organic Leaves", "Minimalist", "Soft Sunlight"),
            colorPaletteHex = listOf("#FDE68A", "#D97706", "#064E3B", "#FEF3C7"),
            modifiedDate = "2026-07-24"
        ),
        AdobeCcAsset(
            id = "cc_asset_005",
            name = "Futuristic Cyber Billboard.png",
            type = AdobeAssetType.ILLUSTRATION,
            fileFormat = "PNG",
            libraryName = "Adobe Stock Curated",
            thumbnailUrl = "https://picsum.photos/seed/billboard/400/300",
            fullResUrl = "https://picsum.photos/seed/billboard/1920/1080",
            fileSizeFormatted = "18.5 MB",
            dimensions = "1920 x 1080",
            tags = listOf("Neon Synthwave", "3D Render", "4K Ultra Detail", "Sci-Fi"),
            colorPaletteHex = listOf("#F43F5E", "#3B82F6", "#0284C7"),
            modifiedDate = "2026-07-23"
        ),
        AdobeCcAsset(
            id = "cc_asset_006",
            name = "Corporate Brand Style Guide.pdf",
            type = AdobeAssetType.BRAND_KIT,
            fileFormat = "PDF",
            libraryName = "Brand Identity & Vectors",
            thumbnailUrl = "https://picsum.photos/seed/brandguide/400/400",
            fullResUrl = "https://picsum.photos/seed/brandguide/1200/1200",
            fileSizeFormatted = "24.0 MB",
            dimensions = "8.5 x 11 in",
            tags = listOf("Typography Specs", "Color Swatches", "Brand Guidelines", "Official Assets"),
            colorPaletteHex = listOf("#1E293B", "#38BDF8", "#F1F5F9"),
            modifiedDate = "2026-07-22"
        )
    )

    suspend fun getLibraries(): List<AdobeCcLibrary> {
        delay(300) // Simulate network call to Adobe Creative Cloud SDK API
        return mockLibraries
    }

    suspend fun fetchUserAssets(
        libraryId: String? = null,
        typeFilter: AdobeAssetType? = null,
        searchQuery: String = ""
    ): List<AdobeCcAsset> {
        delay(400) // Simulate fetching assets from Adobe CC API (Lightroom/Photoshop/Express SDK)
        return mockAssets.filter { asset ->
            val matchesLib = libraryId == null || libraryId == "ALL" || asset.libraryName.contains(libraryId, ignoreCase = true)
            val matchesType = typeFilter == null || asset.type == typeFilter
            val matchesQuery = searchQuery.isBlank() ||
                    asset.name.contains(searchQuery, ignoreCase = true) ||
                    asset.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
                    asset.fileFormat.contains(searchQuery, ignoreCase = true)
            matchesLib && matchesType && matchesQuery
        }
    }

    suspend fun importAssetToStudio(asset: AdobeCcAsset): AdobeCcImportResult {
        delay(500) // Simulate Adobe Photoshop Cloud Rendering & Feature Extraction
        val tagsFormatted = asset.tags.joinToString(", ")
        val colorsFormatted = asset.colorPaletteHex.joinToString(", ")

        val aiPrompt = when (asset.type) {
            AdobeAssetType.PHOTO -> "High resolution studio photograph inspired by Adobe CC asset '${asset.name}'. Features ${tagsFormatted}. Color palette includes ${colorsFormatted}. Professional 8K studio lighting, sharp focus, octane render."
            AdobeAssetType.DESIGN -> "Sleek social media design graphic inspired by Adobe Express layout '${asset.name}'. Modern bold typography, ${tagsFormatted}, vibrant colors (${colorsFormatted}), eye-catching composition."
            AdobeAssetType.VECTOR -> "Clean vector illustration based on Adobe Illustrator logo '${asset.name}'. Flat geometric aesthetic, ${tagsFormatted}, scalable vector graphics with accent colors (${colorsFormatted})."
            AdobeAssetType.BRAND_KIT -> "Brand visual design adhering strictly to corporate brand kit '${asset.name}'. Color scheme: ${colorsFormatted}. Clean minimalist aesthetics, high end luxury polish."
            AdobeAssetType.ILLUSTRATION -> "Digital 3D illustration based on Adobe Stock asset '${asset.name}'. Hyper-detailed textures, ${tagsFormatted}, cinematic atmosphere with ${colorsFormatted} ambient tones."
        }

        val summary = "Imported '${asset.name}' (${asset.fileFormat}, ${asset.dimensions}) from Adobe Creative Cloud Library '${asset.libraryName}'. Extracted ${asset.tags.size} visual tags & ${asset.colorPaletteHex.size} color swatches."

        return AdobeCcImportResult(
            asset = asset,
            extractedPrompt = aiPrompt,
            metadataSummary = summary
        )
    }

    suspend fun syncCreativeCloud(): String {
        delay(800)
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        return "Synced 4 Adobe Creative Cloud Libraries & 149 Assets at $timeStr"
    }
}
