package com.example.data.models

data class AiGeneratedVideo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val prompt: String,
    val title: String,
    val durationSeconds: Int = 30, // At least 30s
    val aspectRatio: String = "9:16 Vertical (Reels / TikTok / Shorts)",
    val resolution: String = "1080p Full HD (60 FPS)",
    val engine: String = "Veo 2 (Google DeepMind)",
    val style: String = "Cinematic 4K Ultra-Realism",
    val cameraMotion: String = "Dynamic Pan & Dolly Tracking",
    val musicTrack: String = "Upbeat Electronic Lo-Fi",
    val voiceoverVoice: String = "Kore (High-Energy Pro)",
    val scenes: List<AiVideoSceneShot> = emptyList(),
    val viralScore: Int = 96,
    val tags: List<String> = listOf("#AIVideo", "#PromptToVideo", "#Veo2", "#ViralReels"),
    val createdAtMs: Long = System.currentTimeMillis()
)

data class AiVideoSceneShot(
    val sceneNumber: Int,
    val startSecond: Int,
    val endSecond: Int,
    val durationSeconds: Int,
    val cameraAngle: String,
    val visualPrompt: String,
    val onScreenSubtitle: String,
    val voiceoverScript: String,
    val primaryHex: String = "#6366F1",
    val secondaryHex: String = "#06B6D4",
    val motionType: String = "ZOOM_IN" // ZOOM_IN, PAN_RIGHT, PAN_LEFT, ORBIT, DRONE_PULL
)
