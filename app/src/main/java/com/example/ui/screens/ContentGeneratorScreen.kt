package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admob.AdMobInterstitialHelper
import com.example.data.auth.AuthManager
import com.example.data.local.BrandKitEntity
import com.example.data.remote.GeneratedContentPackage
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentGeneratorScreen(viewModel: ContentViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedPkg by viewModel.generatedPackage.collectAsState()
    val activeBrandKit by viewModel.activeBrandKit.collectAsState()

    var topicOrUrl by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("CAPTION") } // CAPTION, PRODUCT_DESC, BLOG, EMAIL, AD_COPY, STORY
    var selectedPlatform by remember { mutableStateOf("TikTok") }
    var selectedTone by remember { mutableStateOf("Viral & Punchy") }
    var lastSavedText by remember { mutableStateOf("") }
    var applyBrandKit by remember { mutableStateOf(true) }

    // Auto-save draft mechanism
    val prefs = remember { context.getSharedPreferences("content_gen_drafts", Context.MODE_PRIVATE) }

    LaunchedEffect(Unit) {
        topicOrUrl = prefs.getString("saved_draft_topic", "") ?: ""
    }

    val userPlanTier by viewModel.userPlanTier.collectAsState()
    var previousPkg by remember { mutableStateOf<GeneratedContentPackage?>(null) }

    // Interstitial monetization trigger on generation completion for Free tier
    LaunchedEffect(generatedPkg) {
        if (generatedPkg != null && generatedPkg != previousPkg) {
            previousPkg = generatedPkg
            // Show interstitial ad for free tier monetization
            AdMobInterstitialHelper.showAdIfFreeTier(context, userPlanTier)
        }
    }

    LaunchedEffect(topicOrUrl) {
        if (topicOrUrl.isNotBlank()) {
            prefs.edit().putString("saved_draft_topic", topicOrUrl).apply()
            lastSavedText = "Draft auto-saved"
        }
    }

    val contentTypes = listOf(
        "CAPTION" to "Social Caption",
        "PRODUCT_DESC" to "Product Description",
        "BLOG" to "Blog Post",
        "EMAIL" to "Email Newsletter",
        "AD_COPY" to "Ad Copy",
        "STORY" to "Story Writer"
    )

    val platforms = listOf("TikTok", "Instagram", "YouTube", "Facebook", "X", "LinkedIn", "Pinterest", "Threads")
    val tones = listOf("Viral & Punchy", "Professional", "Funny & Witty", "High Urgency", "Educational", "Storytelling")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "🤖 AI Content Generator",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Generate high-converting captions, hooks, blog posts, and ad copy optimized for algorithms.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Content Format Selector
        item {
            Text("Content Type", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(contentTypes) { (typeKey, typeLabel) ->
                    FilterChip(
                        selected = selectedType == typeKey,
                        onClick = { selectedType = typeKey },
                        label = { Text(typeLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("chip_type_$typeKey")
                    )
                }
            }
        }

        // Topic / Link Input Field
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
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
                        Text(
                            text = "Topic or Product URL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (lastSavedText.isNotBlank()) {
                            Text(
                                text = lastSavedText,
                                fontSize = 11.sp,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    OutlinedTextField(
                        value = topicOrUrl,
                        onValueChange = { topicOrUrl = it },
                        placeholder = { Text("e.g. Wireless noise-canceling headphones review or Amazon product link...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("input_topic_or_url"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Target Platform Selector
                    Text("Target Platform", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(platforms) { platform ->
                            FilterChip(
                                selected = selectedPlatform == platform,
                                onClick = { selectedPlatform = platform },
                                label = { Text(platform, fontSize = 12.sp) },
                                modifier = Modifier.testTag("chip_platform_$platform")
                            )
                        }
                    }

                    // Tone Selector
                    Text("Brand Tone", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tones) { tone ->
                            FilterChip(
                                selected = selectedTone == tone,
                                onClick = { selectedTone = tone },
                                label = { Text(tone, fontSize = 12.sp) },
                                modifier = Modifier.testTag("chip_tone_$tone")
                            )
                        }
                    }

                    // Brand Kit Integration Card
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = IndigoPrimaryLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Brand Kit: ${activeBrandKit?.brandName ?: "Infinity AI"}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Palette, fonts & CTA voice injection",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = applyBrandKit,
                                    onCheckedChange = { applyBrandKit = it },
                                    modifier = Modifier.height(24.dp).testTag("toggle_apply_brand_kit_switch")
                                )
                            }

                            if (applyBrandKit) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Surface(
                                            color = IndigoPrimary.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "🔤 ${activeBrandKit?.primaryFont?.split("/")?.firstOrNull()?.trim() ?: "Inter"}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = IndigoPrimaryLight,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Surface(
                                            color = CyanAccent.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "📢 ${activeBrandKit?.voiceTone ?: "Viral"}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = CyanAccentLight,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "CTA injected ✓",
                                        fontSize = 9.sp,
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (topicOrUrl.isBlank()) {
                                Toast.makeText(context, "Please enter a topic or link first", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.generateContent(
                                    topicOrUrl = topicOrUrl,
                                    platform = selectedPlatform,
                                    tone = selectedTone,
                                    contentType = selectedType,
                                    useBrandKit = applyBrandKit
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_content_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating AI Package...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Content Package", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Generated Results View
        generatedPkg?.let { pkg ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("generated_results_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = EmeraldSuccess.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Viral Score: ${pkg.viralScore}/100",
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    color = IndigoPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = pkg.platform,
                                        color = IndigoPrimaryLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    val textToCopy = "${pkg.hooks.firstOrNull() ?: ""}\n\n${pkg.captionsAndBody}\n\n${pkg.cta}\n\n${pkg.hashtags.joinToString(" ")}"
                                    clipboardManager.setText(AnnotatedString(textToCopy))
                                    Toast.makeText(context, "Full package copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("copy_package_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                            }
                        }

                        // Hooks Section
                        Text("🔥 Viral Pattern-Interrupt Hooks", fontWeight = FontWeight.Bold, color = CyanAccentLight, fontSize = 14.sp)
                        pkg.hooks.forEachIndexed { idx, hook ->
                            Surface(
                                color = DarkSlateBg,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${idx + 1}. $hook",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // Caption Body
                        Text("📝 Formatted Caption & Body", fontWeight = FontWeight.Bold, color = IndigoPrimaryLight, fontSize = 14.sp)
                        Surface(
                            color = DarkSlateBg,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = pkg.captionsAndBody,
                                color = TextPrimaryDark,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        // CTA Section
                        Text("🎯 Call to Action (CTA)", fontWeight = FontWeight.Bold, color = PurpleAccentLight, fontSize = 14.sp)
                        Surface(
                            color = DarkSlateBg,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = pkg.cta,
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        // Hashtags
                        Text("# Hashtags & Keywords", fontWeight = FontWeight.Bold, color = TextSecondaryDark, fontSize = 13.sp)
                        Text(
                            text = pkg.hashtags.joinToString(" "),
                            color = CyanAccentLight,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.savePackageToHistory(pkg)
                                    Toast.makeText(context, "Saved to Content History!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_to_history_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Draft")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.scheduleNewPost(pkg.title, pkg.platform, "${pkg.captionsAndBody}\n\n${pkg.hashtags.joinToString(" ")}", System.currentTimeMillis() + 86400000)
                                    Toast.makeText(context, "Scheduled for tomorrow on ${pkg.platform}!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("schedule_post_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Schedule")
                            }
                        }
                    }
                }
            }
        }
    }
}
