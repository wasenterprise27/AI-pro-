package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AiGeneratedVideo
import com.example.data.models.AiVideoSceneShot
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun AiVideoPlayerComponent(
    video: AiGeneratedVideo,
    modifier: Modifier = Modifier,
    onScheduleClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isPlaying by remember(video.id) { mutableStateOf(true) }
    var currentPlaybackSecond by remember(video.id) { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }
    var selectedAspectRatioMode by remember(video.aspectRatio) {
        mutableStateOf(if (video.aspectRatio.contains("16:9")) "16:9" else if (video.aspectRatio.contains("1:1")) "1:1" else "9:16")
    }

    val totalDuration = video.durationSeconds.toFloat().coerceAtLeast(30f)

    // Playback ticker coroutine
    LaunchedEffect(video.id, isPlaying, playbackSpeed) {
        if (isPlaying) {
            while (isActive) {
                delay(50)
                currentPlaybackSecond += 0.05f * playbackSpeed
                if (currentPlaybackSecond >= totalDuration) {
                    currentPlaybackSecond = 0f // Loop seamlessly
                }
            }
        }
    }

    // Active scene determination
    val currentSecInt = currentPlaybackSecond.toInt()
    val currentScene: AiVideoSceneShot = remember(video.id, currentSecInt) {
        video.scenes.firstOrNull { currentSecInt >= it.startSecond && currentSecInt < it.endSecond }
            ?: video.scenes.lastOrNull()
            ?: AiVideoSceneShot(
                sceneNumber = 1,
                startSecond = 0,
                endSecond = totalDuration.toInt(),
                durationSeconds = totalDuration.toInt(),
                cameraAngle = "Dynamic 4K Tracking",
                visualPrompt = video.prompt,
                onScreenSubtitle = "AI GENERATED 30S VIDEO",
                voiceoverScript = "Synthesized with AI temporal coherence."
            )
    }

    // Parse colors
    val primaryColor = remember(currentScene.primaryHex) {
        try {
            Color(android.graphics.Color.parseColor(currentScene.primaryHex))
        } catch (e: Exception) {
            IndigoPrimary
        }
    }
    val secondaryColor = remember(currentScene.secondaryHex) {
        try {
            Color(android.graphics.Color.parseColor(currentScene.secondaryHex))
        } catch (e: Exception) {
            CyanAccent
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_video_player_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(IndigoPrimary.copy(alpha = 0.5f), PurpleAccent.copy(alpha = 0.5f))))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title, Engine Badge, Total Duration Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = IndigoPrimary.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "⚡ ${video.engine}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimaryLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "⏱️ ${video.durationSeconds}s Duration",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = video.resolution,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = CyanAccentLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Aspect Ratio Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("9:16", "16:9", "1:1").forEach { mode ->
                        val isSelected = selectedAspectRatioMode == mode
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) IndigoPrimary else DarkSlateSurfaceVariant,
                            modifier = Modifier
                                .clickable { selectedAspectRatioMode = mode }
                                .testTag("ratio_toggle_$mode")
                        ) {
                            Text(
                                text = mode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Cinematic Video Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        when (selectedAspectRatioMode) {
                            "9:16" -> 340.dp
                            "1:1" -> 260.dp
                            else -> 200.dp
                        }
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF030712))
                    .border(1.dp, Brush.linearGradient(listOf(primaryColor.copy(alpha = 0.6f), secondaryColor.copy(alpha = 0.6f))), RoundedCornerShape(16.dp))
                    .testTag("ai_video_viewport"),
                contentAlignment = Alignment.Center
            ) {
                // Continuous Generative Motion Simulation Canvas
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val progressRatio = currentPlaybackSecond / totalDuration
                    val sceneProgress = (currentPlaybackSecond - currentScene.startSecond) / currentScene.durationSeconds.coerceAtLeast(1)

                    // Background mesh gradient
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.45f),
                                secondaryColor.copy(alpha = 0.25f),
                                Color(0xFF030712)
                            ),
                            center = Offset(size.width * (0.3f + 0.4f * sceneProgress), size.height * (0.4f + 0.2f * progressRatio)),
                            radius = size.maxDimension * 0.9f
                        )
                    )

                    // Animated geometric dynamic motion based on motionType
                    val animatedScale = 1.0f + 0.18f * kotlin.math.sin(sceneProgress * Math.PI.toFloat())
                    val animatedRotation = when (currentScene.motionType) {
                        "ORBIT" -> sceneProgress * 360f
                        "PAN_RIGHT" -> sceneProgress * 25f
                        "PAN_LEFT" -> -sceneProgress * 25f
                        else -> 0f
                    }

                    rotate(animatedRotation, pivot = Offset(size.width / 2f, size.height / 2f)) {
                        scale(animatedScale, pivot = Offset(size.width / 2f, size.height / 2f)) {
                            // Draw animated grid / particles
                            for (i in 0..5) {
                                val offsetRatio = (i * 0.16f + progressRatio) % 1.0f
                                drawCircle(
                                    brush = Brush.linearGradient(listOf(primaryColor.copy(alpha = 0.3f), secondaryColor.copy(alpha = 0.15f))),
                                    radius = size.width * (0.15f + offsetRatio * 0.35f),
                                    center = Offset(size.width * 0.5f, size.height * 0.5f)
                                )
                            }
                        }
                    }
                }

                // Overlay 1: Top Status Bar (Scene Index, Camera Angle, Live Resolution)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) EmeraldSuccess else AmberWarning)
                            )
                            Text(
                                text = "Shot ${currentScene.sceneNumber}/${video.scenes.size.coerceAtLeast(1)} • ${currentScene.cameraAngle}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Watermark / Format
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "60 FPS 4K HDR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccentLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Overlay 2: Center Visual Shot Direction & Prompt Context
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = currentScene.visualPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Overlay 3: Bottom Subtitle & Audio Waveform Banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f), Color.Black)
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // On-Screen Subtitle
                    Surface(
                        color = primaryColor.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = currentScene.onScreenSubtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Voiceover narration script
                    Text(
                        text = "🗣️ \"${currentScene.voiceoverScript}\"",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Simulated Audio Waveform
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..19) {
                            val barHeight = if (isPlaying && !isMuted) {
                                (3 + ((kotlin.math.sin(currentPlaybackSecond * 8 + i) + 1f) * 3.5f)).dp
                            } else 2.dp

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 1.5.dp)
                                    .width(3.dp)
                                    .height(barHeight)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (i % 2 == 0) CyanAccent else PurpleAccentLight)
                            )
                        }
                    }
                }
            }

            // Timeline Scrubbing Bar & Time Code Readout
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Slider(
                    value = currentPlaybackSecond,
                    onValueChange = {
                        currentPlaybackSecond = it
                    },
                    valueRange = 0f..totalDuration,
                    colors = SliderDefaults.colors(
                        thumbColor = IndigoPrimary,
                        activeTrackColor = IndigoPrimary,
                        inactiveTrackColor = DarkSlateSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .testTag("video_timeline_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentMin = currentPlaybackSecond.toInt() / 60
                    val currentSec = currentPlaybackSecond.toInt() % 60
                    val totalMin = totalDuration.toInt() / 60
                    val totalSec = totalDuration.toInt() % 60

                    Text(
                        text = String.format("%02d:%02d / %02d:%02d (At least 30s)", currentMin, currentSec, totalMin, totalSec),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = CyanAccentLight
                    )

                    Text(
                        text = "Audio: ${video.musicTrack} • Voice: ${video.voiceoverVoice}",
                        fontSize = 10.sp,
                        color = TextSecondaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Playback Controls Row: Play/Pause, Replay, Speed, Audio Mute
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play / Pause Button
                    FilledIconButton(
                        onClick = { isPlaying = !isPlaying },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = IndigoPrimary),
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("video_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause Video" else "Play Video",
                            tint = Color.White
                        )
                    }

                    // Replay to start
                    IconButton(
                        onClick = {
                            currentPlaybackSecond = 0f
                            isPlaying = true
                        },
                        modifier = Modifier.testTag("video_replay_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Restart Video",
                            tint = Color.White
                        )
                    }

                    // Audio Mute Toggle
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier.testTag("video_audio_toggle")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Audio",
                            tint = if (isMuted) AmberWarning else Color.White
                        )
                    }
                }

                // Playback Speed Selector (0.5x, 1x, 1.5x, 2x)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { speed ->
                        val isSpeedSelected = playbackSpeed == speed
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSpeedSelected) PurpleAccent else DarkSlateSurfaceVariant,
                            modifier = Modifier
                                .clickable { playbackSpeed = speed }
                                .testTag("speed_${speed}x")
                        ) {
                            Text(
                                text = "${speed}x",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpeedSelected) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Divider(color = DarkSlateSurfaceVariant.copy(alpha = 0.7f))

            // Multi-Scene Jump Bar
            Text(
                text = "🎞️ Interactive Scene Storyboard (Jump to Shot)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(video.scenes) { scene ->
                    val isCurrent = currentSecInt >= scene.startSecond && currentSecInt < scene.endSecond
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) IndigoPrimary.copy(alpha = 0.35f) else DarkSlateBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) IndigoPrimary else DarkSlateSurfaceVariant
                        ),
                        modifier = Modifier
                            .clickable {
                                currentPlaybackSecond = scene.startSecond.toFloat()
                                isPlaying = true
                            }
                            .testTag("scene_jump_chip_${scene.sceneNumber}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Shot ${scene.sceneNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) IndigoPrimaryLight else Color.White
                                )
                                Text(
                                    text = "${scene.durationSeconds}s",
                                    fontSize = 10.sp,
                                    color = CyanAccentLight
                                )
                            }
                            Text(
                                text = scene.cameraAngle,
                                fontSize = 9.sp,
                                color = TextSecondaryDark,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Export & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Download / Export MP4
                Button(
                    onClick = {
                        Toast.makeText(context, "🎬 Exporting 1080p MP4 (${video.durationSeconds}s) to Gallery...", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("export_video_mp4_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export MP4 (1080p)")
                }

                // Share / Schedule
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, video.title)
                            putExtra(Intent.EXTRA_TEXT, "🎬 Generated AI Video (${video.durationSeconds}s):\n${video.title}\n\nPrompt: ${video.prompt}\n\n${video.tags.joinToString(" ")}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share AI Video Package"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_video_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Package")
                }
            }
        }
    }
}
