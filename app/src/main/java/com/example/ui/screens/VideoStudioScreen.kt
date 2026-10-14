package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admob.AdMobInterstitialHelper
import com.example.data.models.AiGeneratedVideo
import com.example.data.remote.GeneratedContentPackage
import com.example.data.remote.VideoScene
import com.example.ui.components.AiVideoPlayerComponent
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoStudioScreen(viewModel: ContentViewModel) {
    val context = LocalContext.current
    var studioTab by remember { mutableIntStateOf(0) } // 0 = Prompt to AI Video (30s+), 1 = Script Studio, 2 = Video Gallery

    // AI Video State
    val isGeneratingVideo by viewModel.isGeneratingVideo.collectAsState()
    val videoGenProgress by viewModel.videoGenProgress.collectAsState()
    val videoGenStage by viewModel.videoGenStage.collectAsState()
    val currentGeneratedVideo by viewModel.currentGeneratedVideo.collectAsState()
    val savedVideosList by viewModel.savedVideosList.collectAsState()
    val activeBrandKit by viewModel.activeBrandKit.collectAsState()

    // Video Generator Inputs (Enforcing at least 30 seconds)
    var videoPrompt by remember { mutableStateOf("") }
    var selectedVideoDuration by remember { mutableIntStateOf(30) } // 30s (Min), 45s, 60s, 90s
    var selectedEngine by remember { mutableStateOf("Veo 2 (Google DeepMind)") }
    var selectedAspectRatio by remember { mutableStateOf("9:16 Vertical (Reels / TikTok / Shorts)") }
    var selectedStyle by remember { mutableStateOf("Cinematic 4K Ultra-Realism") }
    var selectedCameraMotion by remember { mutableStateOf("Dynamic Pan & Dolly Tracking") }
    var applyBrandKit by remember { mutableStateOf(true) }

    // Script Studio State
    val isGeneratingScript by viewModel.isGenerating.collectAsState()
    val generatedPkg by viewModel.generatedPackage.collectAsState()
    val userPlanTier by viewModel.userPlanTier.collectAsState()
    var previousPkg by remember { mutableStateOf<GeneratedContentPackage?>(null) }
    var previousVideoId by remember { mutableStateOf<String?>(null) }

    // Interstitial monetization trigger on script generation completion for Free tier
    LaunchedEffect(generatedPkg) {
        if (generatedPkg != null && generatedPkg?.contentType == "VIDEO_SCRIPT" && generatedPkg != previousPkg) {
            previousPkg = generatedPkg
            AdMobInterstitialHelper.showAdIfFreeTier(context, userPlanTier)
        }
    }

    // Interstitial monetization trigger on AI video generation completion for Free tier
    LaunchedEffect(currentGeneratedVideo) {
        val video = currentGeneratedVideo
        if (video != null && video.id != previousVideoId) {
            previousVideoId = video.id
            AdMobInterstitialHelper.showAdIfFreeTier(context, userPlanTier)
        }
    }

    var scriptTopic by remember { mutableStateOf("") }
    var selectedScriptFormat by remember { mutableStateOf(30) }
    var selectedPlatform by remember { mutableStateOf("TikTok") }
    var selectedVoice by remember { mutableStateOf("Kore (Energetic)") }
    var selectedMusic by remember { mutableStateOf("Upbeat Tech Lo-Fi") }
    var selectedAvatar by remember { mutableStateOf("Marcus AI (Pro Presenter)") }
    var isPlayingTts by remember { mutableStateOf(false) }

    val videoDurations = listOf(
        30 to "30-Sec Full Ad (Minimum)",
        45 to "45-Sec Extended Cinematic",
        60 to "60-Sec Viral Short",
        90 to "90-Sec Product Narrative"
    )

    val engines = listOf(
        "Veo 2 (Google DeepMind)",
        "Sora AI (OpenAI)",
        "Runway Gen-3 Alpha",
        "Pika 2.0 Motion"
    )

    val aspectRatios = listOf(
        "9:16 Vertical (Reels / TikTok / Shorts)",
        "16:9 Widescreen (YouTube / Landscape)",
        "1:1 Square (Feed / Instagram)"
    )

    val styles = listOf(
        "Cinematic 4K Ultra-Realism",
        "Cyberpunk Volumetric Neon",
        "Luxury Studio Commercial",
        "Aerial Drone Natural Sunlight",
        "Anime 3D Hyper-Vibrant"
    )

    val cameraMotions = listOf(
        "Dynamic Pan & Dolly Tracking",
        "360 Orbital Camera Rotation",
        "FPV Drone Fast Flythrough",
        "Macro Slow-Motion Push-In"
    )

    val samplePrompts = listOf(
        "Cyberpunk futuristic sports car racing through neon-lit Tokyo rain in 8K HDR",
        "Luxury Swiss smartwatch exploding into floating golden gears with water ripples",
        "Gourmet smashed beef burger sizzling on cast iron in slow-motion 60fps",
        "AI humanoid robot unveiling a holographic crystal globe in a minimalist studio",
        "Dramatic mountain peak flythrough during golden hour sunrise with volumetric clouds"
    )

    val voices = listOf("Kore (Energetic)", "Onyx (Deep Voice)", "Puck (Playful)", "Fenrir (Authoritative)", "Aoede (Warm)")
    val musicTracks = listOf("Upbeat Tech Lo-Fi", "Cinematic Orchestral", "Chill Acoustic Beat", "High-Urgency Trap")
    val avatars = listOf("Marcus AI (Pro Presenter)", "Sophia Tech (Modern)", "Leo Creator (Casual)", "Chloe Brand (Luxury)")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎬 AI Video Studio",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = IndigoPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "⏱️ Min 30s Engine",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimaryLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = "Generate full 30s+ AI videos from text prompts, or draft scene-by-scene script storyboards.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Studio Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = studioTab,
                containerColor = DarkSlateSurface,
                contentColor = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("video_studio_tab_row")
            ) {
                Tab(
                    selected = studioTab == 0,
                    onClick = { studioTab = 0 },
                    text = { Text("Prompt to Video (30s+)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_prompt_to_video")
                )
                Tab(
                    selected = studioTab == 1,
                    onClick = { studioTab = 1 },
                    text = { Text("Script Storyboard", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_script_studio")
                )
                Tab(
                    selected = studioTab == 2,
                    onClick = { studioTab = 2 },
                    text = { Text("Gallery (${savedVideosList.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_video_gallery")
                )
            }
        }

        // ================= TAB 0: PROMPT TO AI VIDEO (30s+ ENGINE) =================
        if (studioTab == 0) {
            // Duration Selector (Enforcing at least 30 seconds)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⏱️ Target Video Duration (At Least 30 Seconds)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${selectedVideoDuration}s Selected", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(videoDurations) { (sec, label) ->
                                val isSelected = selectedVideoDuration == sec
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedVideoDuration = sec },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndigoPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("chip_video_duration_$sec")
                                )
                            }
                        }
                    }
                }
            }

            // Prompt Input Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Cinematic Prompt", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        OutlinedTextField(
                            value = videoPrompt,
                            onValueChange = { videoPrompt = it },
                            placeholder = { Text("Describe the full scene, lighting, subject motion, and camera actions for your 30s+ video...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("input_video_prompt"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Quick inspiration prompts
                        Text("💡 Quick Prompt Inspiration", fontSize = 11.sp, color = TextSecondaryDark, fontWeight = FontWeight.Medium)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(samplePrompts) { p ->
                                Surface(
                                    color = DarkSlateSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .clickable { videoPrompt = p }
                                        .testTag("sample_prompt_chip")
                                ) {
                                    Text(
                                        text = p.take(30) + "...",
                                        fontSize = 11.sp,
                                        color = TextPrimaryDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Divider(color = DarkSlateSurfaceVariant)

                        // AI Video Generation Engine
                        Text("AI Video Engine", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(engines) { engine ->
                                FilterChip(
                                    selected = selectedEngine == engine,
                                    onClick = { selectedEngine = engine },
                                    label = { Text(engine, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PurpleAccent,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Aspect Ratio & Style Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Aspect Ratio", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val nextIdx = (aspectRatios.indexOf(selectedAspectRatio) + 1) % aspectRatios.size
                                            selectedAspectRatio = aspectRatios[nextIdx]
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(selectedAspectRatio.split(" ").first(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        Icon(Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanAccentLight)
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Visual Style", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val nextIdx = (styles.indexOf(selectedStyle) + 1) % styles.size
                                            selectedStyle = styles[nextIdx]
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(selectedStyle.split(" ").first(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp), tint = PurpleAccentLight)
                                    }
                                }
                            }
                        }

                        // Camera Motion
                        Column {
                            Text("Camera Motion Dynamics", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val nextIdx = (cameraMotions.indexOf(selectedCameraMotion) + 1) % cameraMotions.size
                                        selectedCameraMotion = cameraMotions[nextIdx]
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(selectedCameraMotion, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp), tint = AmberWarning)
                                }
                            }
                        }

                        // Brand Kit Toggle
                        if (activeBrandKit != null) {
                            Surface(
                                color = IndigoPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("🎨 Brand Kit: ${activeBrandKit?.brandName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = IndigoPrimaryLight)
                                        Text("Inject brand colors (${activeBrandKit?.primaryColorHex}), logo & CTA into video", fontSize = 10.sp, color = TextSecondaryDark)
                                    }
                                    Switch(
                                        checked = applyBrandKit,
                                        onCheckedChange = { applyBrandKit = it },
                                        modifier = Modifier.testTag("toggle_brand_kit_video")
                                    )
                                }
                            }
                        }

                        // Generate Button
                        Button(
                            onClick = {
                                if (videoPrompt.isBlank()) {
                                    Toast.makeText(context, "Please enter a video prompt first", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.generateAiVideo(
                                        prompt = videoPrompt,
                                        targetDurationSeconds = selectedVideoDuration.coerceAtLeast(30),
                                        aspectRatio = selectedAspectRatio,
                                        style = selectedStyle,
                                        cameraMotion = selectedCameraMotion,
                                        engine = selectedEngine,
                                        musicTrack = selectedMusic,
                                        voiceoverVoice = selectedVoice,
                                        useBrandKit = applyBrandKit
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("generate_ai_video_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isGeneratingVideo
                        ) {
                            if (isGeneratingVideo) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Generating ${selectedVideoDuration}s AI Video...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate ${selectedVideoDuration}s AI Video", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Real-Time Generative Rendering Multi-Stage Progress
            if (isGeneratingVideo) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("video_generation_progress_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚡ AI Video Synthesis Pipeline", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyanAccentLight)
                                Text("${(videoGenProgress * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.White)
                            }

                            LinearProgressIndicator(
                                progress = { videoGenProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = IndigoPrimary,
                                trackColor = DarkSlateSurfaceVariant
                            )

                            Text(
                                text = videoGenStage,
                                fontSize = 12.sp,
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldSuccess))
                                Text("Target duration: at least ${selectedVideoDuration}s • 60 FPS • $selectedEngine", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                        }
                    }
                }
            }

            // Rendered AI Video Player (Interactive 30s+ Playback Engine)
            if (currentGeneratedVideo != null) {
                item {
                    Text(
                        text = "🎥 Active Rendered AI Video (${currentGeneratedVideo?.durationSeconds}s)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                item {
                    currentGeneratedVideo?.let { video ->
                        AiVideoPlayerComponent(
                            video = video,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // ================= TAB 1: SCRIPT & STORYBOARD STUDIO =================
        if (studioTab == 1) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Script Duration & Storyboard Format", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(15 to "15s TikTok", 30 to "30s Ad Script", 60 to "60s Shorts", 180 to "3-Min Longform")) { (sec, label) ->
                                FilterChip(
                                    selected = selectedScriptFormat == sec,
                                    onClick = { selectedScriptFormat = sec },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PurpleAccent,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Text("Script Topic / Hook Concept", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        OutlinedTextField(
                            value = scriptTopic,
                            onValueChange = { scriptTopic = it },
                            placeholder = { Text("e.g. 3 AI tools that replace entire marketing teams...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("input_video_topic"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Avatar & Voice Selection
                        Text("AI Presenter Avatar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(avatars) { avatar ->
                                FilterChip(
                                    selected = selectedAvatar == avatar,
                                    onClick = { selectedAvatar = avatar },
                                    label = { Text(avatar, fontSize = 11.sp) }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (scriptTopic.isBlank()) {
                                    Toast.makeText(context, "Please enter a topic first", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.generateVideoScript(scriptTopic, selectedPlatform, selectedScriptFormat)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_script_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isGeneratingScript
                        ) {
                            if (isGeneratingScript) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Drafting Script Timeline...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Script Breakdown (${selectedScriptFormat}s)")
                            }
                        }
                    }
                }
            }

            // Display Generated Script Package
            if (generatedPkg != null && generatedPkg?.contentType == "VIDEO_SCRIPT") {
                item {
                    val pkg = generatedPkg!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("video_script_output_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pkg.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimaryLight,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { isPlayingTts = !isPlayingTts },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isPlayingTts) AmberWarning else IndigoPrimary)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        if (isPlayingTts) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play TTS",
                                        tint = Color.White
                                    )
                                }
                            }

                            Divider(color = DarkSlateSurfaceVariant)

                            Text("🎬 Timeline Scene Breakdown", fontWeight = FontWeight.Bold, color = CyanAccentLight, fontSize = 14.sp)

                            pkg.videoScenes.forEach { scene ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkSlateBg),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Surface(
                                                color = PurpleAccent.copy(alpha = 0.25f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "Scene ${scene.sceneNumber} (${scene.durationSeconds}s)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PurpleAccentLight,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = "TEXT: ${scene.onScreenText}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AmberWarning
                                            )
                                        }

                                        Text(
                                            text = "📹 Visual: ${scene.visualDescription}",
                                            fontSize = 12.sp,
                                            color = TextSecondaryDark
                                        )

                                        Text(
                                            text = "🗣️ Voiceover: \"${scene.voiceoverText}\"",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimaryDark
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.savePackageToHistory(pkg)
                                    Toast.makeText(context, "Script saved to project workspace!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().testTag("save_video_script_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Script to Project")
                            }
                        }
                    }
                }
            }
        }

        // ================= TAB 2: RENDERED VIDEO GALLERY =================
        if (studioTab == 2) {
            if (savedVideosList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(48.dp))
                            Text("No AI Videos Generated Yet", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text("Switch to 'Prompt to Video' tab to generate your first 30-second AI video.", fontSize = 12.sp, color = TextSecondaryDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { studioTab = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Create 30s Video Now")
                            }
                        }
                    }
                }
            } else {
                items(savedVideosList) { video ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectGeneratedVideo(video)
                                studioTab = 0 // Switch back to view in player
                            }
                            .testTag("gallery_video_item_${video.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = video.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${video.durationSeconds}s",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Prompt: ${video.prompt}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark,
                                maxLines = 2
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ ${video.engine} • ${video.resolution}",
                                    fontSize = 10.sp,
                                    color = CyanAccentLight
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = {
                                            viewModel.selectGeneratedVideo(video)
                                            studioTab = 0
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = IndigoPrimaryLight)
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteAiVideo(video.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseError)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
