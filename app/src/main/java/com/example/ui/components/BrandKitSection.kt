package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.BrandKitEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

data class BrandPreset(
    val name: String,
    val brandName: String,
    val tagline: String,
    val primaryHex: String,
    val secondaryHex: String,
    val accentHex: String,
    val bgHex: String,
    val primaryFont: String,
    val secondaryFont: String,
    val logoText: String,
    val logoIcon: String,
    val tone: String,
    val audience: String,
    val hashtags: String,
    val cta: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandKitSection(
    viewModel: ContentViewModel,
    onNavigateToGenerator: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val activeBrandKit by viewModel.activeBrandKit.collectAsState()

    // Form state
    var brandName by remember { mutableStateOf("Infinity AI") }
    var tagline by remember { mutableStateOf("Create Viral Content in Seconds") }
    var primaryColorHex by remember { mutableStateOf("#6366F1") }
    var secondaryColorHex by remember { mutableStateOf("#06B6D4") }
    var accentColorHex by remember { mutableStateOf("#EC4899") }
    var backgroundColorHex by remember { mutableStateOf("#0F172A") }
    var primaryFont by remember { mutableStateOf("Inter / Modern Sans") }
    var secondaryFont by remember { mutableStateOf("JetBrains Mono / Code Display") }
    var logoType by remember { mutableStateOf("MONOGRAM") } // MONOGRAM, ICON, TEXT_BADGE, IMAGE_URI
    var logoPlaceholderText by remember { mutableStateOf("⚡ INF") }
    var logoIconName by remember { mutableStateOf("Bolt") }
    var logoImageUri by remember { mutableStateOf<String?>(null) }
    var voiceTone by remember { mutableStateOf("Viral & High-Energy") }
    var targetAudience by remember { mutableStateOf("Creators, Marketers & E-Commerce Brands") }
    var defaultHashtags by remember { mutableStateOf("#InfinityAI #ViralContent #CreatorEconomy #MarketingPro") }
    var brandKeywords by remember { mutableStateOf("Innovation, Speed, High Retention, Scalable Growth") }
    var ctaTemplate by remember { mutableStateOf("Tap the link in bio to supercharge your reach today! 🚀") }

    var isExpanded by remember { mutableStateOf(true) }
    var activeTab by remember { mutableStateOf(0) } // 0: Visual & Colors, 1: Typography & Logo, 2: Voice & Strategy

    // Load active brand kit when available
    LaunchedEffect(activeBrandKit) {
        activeBrandKit?.let { kit ->
            brandName = kit.brandName
            tagline = kit.tagline
            primaryColorHex = kit.primaryColorHex
            secondaryColorHex = kit.secondaryColorHex
            accentColorHex = kit.accentColorHex
            backgroundColorHex = kit.backgroundColorHex
            primaryFont = kit.primaryFont
            secondaryFont = kit.secondaryFont
            logoType = kit.logoType
            logoPlaceholderText = kit.logoPlaceholderText
            logoIconName = kit.logoIconName
            logoImageUri = kit.logoImageUri
            voiceTone = kit.voiceTone
            targetAudience = kit.targetAudience
            defaultHashtags = kit.defaultHashtags
            brandKeywords = kit.brandKeywords
            ctaTemplate = kit.ctaTemplate
        }
    }

    // Image Picker Launcher for Logo upload
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            logoImageUri = uri.toString()
            logoType = "IMAGE_URI"
            Toast.makeText(context, "Brand logo asset uploaded successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Presets
    val presets = listOf(
        BrandPreset(
            name = "⚡ Cyber Slate",
            brandName = "Infinity AI",
            tagline = "Create Viral Content in Seconds",
            primaryHex = "#6366F1",
            secondaryHex = "#06B6D4",
            accentHex = "#EC4899",
            bgHex = "#0F172A",
            primaryFont = "Inter / Modern Sans",
            secondaryFont = "JetBrains Mono / Code Display",
            logoText = "⚡ INF",
            logoIcon = "Bolt",
            tone = "Viral & High-Energy",
            audience = "Creators, Marketers & E-Commerce Brands",
            hashtags = "#InfinityAI #ViralContent #CreatorEconomy #MarketingPro",
            cta = "Tap the link in bio to supercharge your reach today! 🚀"
        ),
        BrandPreset(
            name = "🔮 Neon Violet",
            brandName = "Aura Media",
            tagline = "High-Growth Social Storytelling",
            primaryHex = "#8B5CF6",
            secondaryHex = "#3B82F6",
            accentHex = "#F43F5E",
            bgHex = "#18181B",
            primaryFont = "Poppins / Bold Tech",
            secondaryFont = "Montserrat / Geometric",
            logoText = "🔮 AURA",
            logoIcon = "Sparkle",
            tone = "Witty & Sarcastic",
            audience = "Gen Z Creators & Tech Enthusiasts",
            hashtags = "#AuraMedia #GenZGrowth #ViralReels #CreatorHacks",
            cta = "Follow @AuraMedia & drop a 🔥 in the comments for part 2!"
        ),
        BrandPreset(
            name = "🌅 Sunset Amber",
            brandName = "Pulse Digital",
            tagline = "Direct-Response Conversion Machine",
            primaryHex = "#F59E0B",
            secondaryHex = "#EF4444",
            accentHex = "#8B5CF6",
            bgHex = "#1C1917",
            primaryFont = "Space Grotesk / Edgy",
            secondaryFont = "Roboto / Clean Neutral",
            logoText = "🔥 PULSE",
            logoIcon = "Fire",
            tone = "High Urgency & Sales-Driven",
            audience = "E-Commerce Founders & Affiliate Marketers",
            hashtags = "#PulseDigital #AffiliateMarketing #EcommerceGrowth #SalesStrategy",
            cta = "Claim 50% off during our flash launch! Link in bio ⏳"
        ),
        BrandPreset(
            name = "💎 Luxury Emerald",
            brandName = "Apex Studio",
            tagline = "Minimalist Elegance for Modern Brands",
            primaryHex = "#10B981",
            secondaryHex = "#F59E0B",
            accentHex = "#06B6D4",
            bgHex = "#064E3B",
            primaryFont = "Playfair / Luxury Serif",
            secondaryFont = "Lato / Friendly",
            logoText = "💎 APEX",
            logoIcon = "Diamond",
            tone = "Luxury & Minimalist",
            audience = "High-End Brands, Boutique Agencies & Founders",
            hashtags = "#ApexStudio #LuxuryBranding #EditorialDesign #HighEndAesthetics",
            cta = "Inquire via DM or link in bio for bespoke creative services."
        ),
        BrandPreset(
            name = "🖤 Monochrome Minimal",
            brandName = "Mono Agency",
            tagline = "Clean Visuals & Precision Copy",
            primaryHex = "#FFFFFF",
            secondaryHex = "#94A3B8",
            accentHex = "#38BDF8",
            bgHex = "#09090B",
            primaryFont = "Plus Jakarta / Clean UI",
            secondaryFont = "JetBrains Mono / Code Display",
            logoText = "◼ MONO",
            logoIcon = "Star",
            tone = "Authoritative & Educational",
            audience = "B2B Marketers, Tech Startups & SaaS Leaders",
            hashtags = "#MonoAgency #SaaSMarketing #B2BStrategy #CleanDesign",
            cta = "Download our 2026 Social Architecture whitepaper via the link in bio 📊"
        ),
        BrandPreset(
            name = "🌊 Ocean Tech",
            brandName = "Horizon AI",
            tagline = "Next-Gen Generative Workflows",
            primaryHex = "#0EA5E9",
            secondaryHex = "#3B82F6",
            accentHex = "#10B981",
            bgHex = "#0B132B",
            primaryFont = "Outfit / Energetic",
            secondaryFont = "Montserrat / Geometric",
            logoText = "🌊 HRZN",
            logoIcon = "Rocket",
            tone = "Empathetic & Storytelling",
            audience = "AI Builders, Product Designers & Indie Hackers",
            hashtags = "#HorizonAI #AIWorkflows #IndieHacker #FutureOfWork",
            cta = "Join 15,000+ creators on our weekly AI newsletter today 💌"
        )
    )

    val primaryColor = parseColorSafe(primaryColorHex, Color(0xFF6366F1))
    val secondaryColor = parseColorSafe(secondaryColorHex, Color(0xFF06B6D4))
    val accentColor = parseColorSafe(accentColorHex, Color(0xFFEC4899))
    val bgColor = parseColorSafe(backgroundColorHex, Color(0xFF0F172A))

    val fontHeadingOptions = listOf(
        "Inter / Modern Sans",
        "Poppins / Bold Tech",
        "Space Grotesk / Edgy",
        "Playfair / Luxury Serif",
        "Plus Jakarta / Clean UI",
        "Outfit / Energetic"
    )

    val fontBodyOptions = listOf(
        "JetBrains Mono / Code Display",
        "Roboto / Clean Neutral",
        "Montserrat / Geometric",
        "Lato / Friendly"
    )

    val toneOptions = listOf(
        "Viral & High-Energy",
        "Authoritative & Educational",
        "Witty & Sarcastic",
        "Luxury & Minimalist",
        "High Urgency & Sales-Driven",
        "Empathetic & Storytelling"
    )

    val iconOptions = listOf(
        "Bolt" to Icons.Default.Bolt,
        "Sparkle" to Icons.Default.AutoAwesome,
        "Diamond" to Icons.Default.Diamond,
        "Rocket" to Icons.Default.RocketLaunch,
        "Fire" to Icons.Default.LocalFireDepartment,
        "Star" to Icons.Default.Star,
        "Crown" to Icons.Default.WorkspacePremium,
        "Target" to Icons.Default.TrackChanges,
        "Brush" to Icons.Default.Brush,
        "Campaign" to Icons.Default.Campaign
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = IndigoPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = IndigoPrimaryLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "🎨 Brand Kit & Visual Identity Studio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Define logos, color palettes & fonts used across AI generations",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp).testTag("toggle_brand_kit_expand_btn")
                ) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand/Collapse",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // 1. LIVE BRAND PREVIEW MOCKUP CARD
                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                brush = Brush.horizontalGradient(listOf(primaryColor, secondaryColor, accentColor)),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Top Row: Logo Badge + Brand Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Brand Logo Preview Badge
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                Brush.linearGradient(listOf(primaryColor, secondaryColor))
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (logoType == "IMAGE_URI" && !logoImageUri.isNullOrBlank()) {
                                            AsyncImage(
                                                model = logoImageUri,
                                                contentDescription = "Brand Logo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else if (logoType == "ICON") {
                                            val vector = iconOptions.find { it.first == logoIconName }?.second ?: Icons.Default.Bolt
                                            Icon(
                                                vector,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Text(
                                                text = logoPlaceholderText.take(4),
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = brandName.ifBlank { "Brand Name" },
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = tagline.ifBlank { "Create Viral Content" },
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Voice Tone Pill
                                Surface(
                                    color = accentColor.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(50),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = voiceTone,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Color Palette Swatches Preview Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Color Palette Assets:",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ColorSwatchChip(name = "Primary", color = primaryColor, hex = primaryColorHex)
                                    ColorSwatchChip(name = "Secondary", color = secondaryColor, hex = secondaryColorHex)
                                    ColorSwatchChip(name = "Accent", color = accentColor, hex = accentColorHex)
                                    ColorSwatchChip(name = "Canvas", color = bgColor, hex = backgroundColorHex)
                                }
                            }

                            // Typography Specimen Preview
                            Surface(
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.TextFields, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "Heading: $primaryFont",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "Body: $secondaryFont",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Custom CTA Signature Sample
                            if (ctaTemplate.isNotBlank()) {
                                Surface(
                                    color = primaryColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Campaign, contentDescription = null, tint = primaryColor, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "CTA Signature: $ctaTemplate",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. QUICK PRESET THEMES CAROUSEL
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "⚡ Load 1-Click Brand Themes",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(presets) { preset ->
                                val isSelected = brandName == preset.brandName
                                Surface(
                                    color = if (isSelected) IndigoPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary) else null,
                                    modifier = Modifier
                                        .clickable {
                                            brandName = preset.brandName
                                            tagline = preset.tagline
                                            primaryColorHex = preset.primaryHex
                                            secondaryColorHex = preset.secondaryHex
                                            accentColorHex = preset.accentHex
                                            backgroundColorHex = preset.bgHex
                                            primaryFont = preset.primaryFont
                                            secondaryFont = preset.secondaryFont
                                            logoPlaceholderText = preset.logoText
                                            logoIconName = preset.logoIcon
                                            voiceTone = preset.tone
                                            targetAudience = preset.audience
                                            defaultHashtags = preset.hashtags
                                            ctaTemplate = preset.cta
                                            Toast.makeText(context, "Loaded ${preset.name} theme!", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("brand_preset_${preset.brandName}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(parseColorSafe(preset.primaryHex, IndigoPrimary))
                                        )
                                        Text(
                                            text = preset.name,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) IndigoPrimaryLight else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. TABBED CONFIGURATION STUDIO
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        contentColor = IndigoPrimaryLight,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("🎨 Colors & Logo", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("brand_tab_colors")
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("🔤 Typography", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("brand_tab_typography")
                        )
                        Tab(
                            selected = activeTab == 2,
                            onClick = { activeTab = 2 },
                            text = { Text("📢 Tone & CTA", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("brand_tab_strategy")
                        )
                    }

                    when (activeTab) {
                        0 -> {
                            // TAB 0: COLORS & LOGO ASSET STUDIO
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // Brand Name & Tagline
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = brandName,
                                        onValueChange = { brandName = it },
                                        label = { Text("Brand Name", fontSize = 11.sp) },
                                        placeholder = { Text("e.g. Infinity AI") },
                                        modifier = Modifier.weight(1f).testTag("input_brand_name"),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = tagline,
                                        onValueChange = { tagline = it },
                                        label = { Text("Tagline / Mission", fontSize = 11.sp) },
                                        placeholder = { Text("e.g. High-Retention Content") },
                                        modifier = Modifier.weight(1f).testTag("input_brand_tagline"),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )
                                }

                                // Logo Placeholder & Asset Uploader
                                Text("Logo Asset & Monogram Placeholder", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FilterChip(
                                        selected = logoType == "MONOGRAM",
                                        onClick = { logoType = "MONOGRAM" },
                                        label = { Text("Monogram", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("chip_logo_monogram")
                                    )
                                    FilterChip(
                                        selected = logoType == "ICON",
                                        onClick = { logoType = "ICON" },
                                        label = { Text("Vector Icon", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("chip_logo_icon")
                                    )
                                    FilterChip(
                                        selected = logoType == "IMAGE_URI",
                                        onClick = { imagePickerLauncher.launch("image/*") },
                                        label = { Text(if (logoImageUri != null) "Uploaded Logo ✓" else "Upload Image", fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(13.dp)) },
                                        modifier = Modifier.testTag("chip_logo_upload")
                                    )
                                }

                                if (logoType == "MONOGRAM") {
                                    OutlinedTextField(
                                        value = logoPlaceholderText,
                                        onValueChange = { logoPlaceholderText = it },
                                        label = { Text("Monogram Letters / Badge Text", fontSize = 11.sp) },
                                        placeholder = { Text("e.g. ⚡ INF or CG") },
                                        modifier = Modifier.fillMaxWidth().testTag("input_logo_text"),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )
                                } else if (logoType == "ICON") {
                                    Text("Select Brand Icon Badge:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(iconOptions) { (name, icon) ->
                                            val isSelected = logoIconName == name
                                            Surface(
                                                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clickable { logoIconName = name }
                                                    .testTag("icon_choice_$name"),
                                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyanAccent) else null
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        icon,
                                                        contentDescription = name,
                                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else if (logoType == "IMAGE_URI") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (logoImageUri != null) "Real logo image attached" else "No image chosen",
                                            fontSize = 11.sp,
                                            color = if (logoImageUri != null) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { imagePickerLauncher.launch("image/*") },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.testTag("change_logo_btn")
                                            ) {
                                                Text("Change Photo", fontSize = 10.sp)
                                            }

                                            if (logoImageUri != null) {
                                                OutlinedButton(
                                                    onClick = {
                                                        logoImageUri = null
                                                        logoType = "MONOGRAM"
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
                                                ) {
                                                    Text("Clear", fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                // 4-Color Hex Inputs
                                Text("Brand Color Palette Hex Codes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ColorHexInputField(
                                            label = "Primary Brand",
                                            value = primaryColorHex,
                                            onValueChange = { primaryColorHex = it },
                                            color = primaryColor,
                                            modifier = Modifier.weight(1f).testTag("input_primary_color")
                                        )
                                        ColorHexInputField(
                                            label = "Secondary Accent",
                                            value = secondaryColorHex,
                                            onValueChange = { secondaryColorHex = it },
                                            color = secondaryColor,
                                            modifier = Modifier.weight(1f).testTag("input_secondary_color")
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ColorHexInputField(
                                            label = "Highlight / CTA",
                                            value = accentColorHex,
                                            onValueChange = { accentColorHex = it },
                                            color = accentColor,
                                            modifier = Modifier.weight(1f).testTag("input_accent_color")
                                        )
                                        ColorHexInputField(
                                            label = "Dark Canvas Bg",
                                            value = backgroundColorHex,
                                            onValueChange = { backgroundColorHex = it },
                                            color = bgColor,
                                            modifier = Modifier.weight(1f).testTag("input_bg_color")
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            // TAB 1: TYPOGRAPHY STUDIO
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Primary Heading Font (Hooks, Titles, Video Text)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    fontHeadingOptions.forEach { fontOption ->
                                        val isSelected = primaryFont == fontOption
                                        Surface(
                                            color = if (isSelected) IndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary) else null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { primaryFont = fontOption }
                                                .testTag("font_heading_$fontOption")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = fontOption,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 12.sp,
                                                        color = if (isSelected) IndigoPrimaryLight else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "VIRAL HOOK: 10X YOUR ENGAGEMENT TODAY",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                Text("Secondary Body / Display Font (Captions, Code, Subtitles)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    fontBodyOptions.forEach { fontOption ->
                                        val isSelected = secondaryFont == fontOption
                                        Surface(
                                            color = if (isSelected) CyanAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyanAccent) else null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { secondaryFont = fontOption }
                                                .testTag("font_body_$fontOption")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = fontOption,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 12.sp,
                                                        color = if (isSelected) CyanAccentLight else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "Generated scene prompt & algorithmic caption text preview.",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // TAB 2: BRAND VOICE & STRATEGY
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Brand Voice & Persona", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(toneOptions) { tone ->
                                        FilterChip(
                                            selected = voiceTone == tone,
                                            onClick = { voiceTone = tone },
                                            label = { Text(tone, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("chip_tone_$tone")
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = targetAudience,
                                    onValueChange = { targetAudience = it },
                                    label = { Text("Target Audience Niche", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. Gen Z Creators, D2C Buyers, Tech Marketers") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_target_audience"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = brandKeywords,
                                    onValueChange = { brandKeywords = it },
                                    label = { Text("Core Brand Keywords / Pillars", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. Innovation, Speed, Scalability, High ROI") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_brand_keywords"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = ctaTemplate,
                                    onValueChange = { ctaTemplate = it },
                                    label = { Text("Custom CTA Template Signature", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. Tap link in bio to start scaling! 🚀") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_cta_template"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = defaultHashtags,
                                    onValueChange = { defaultHashtags = it },
                                    label = { Text("Mandatory Brand Hashtags", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. #InfinityAI #ViralContent #CreatorPro") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_default_hashtags"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Bottom Action Controls: Save & Apply
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val newKit = BrandKitEntity(
                                    id = activeBrandKit?.id ?: 0,
                                    brandName = brandName.ifBlank { "Infinity AI" },
                                    tagline = tagline,
                                    primaryColorHex = primaryColorHex,
                                    secondaryColorHex = secondaryColorHex,
                                    accentColorHex = accentColorHex,
                                    backgroundColorHex = backgroundColorHex,
                                    primaryFont = primaryFont,
                                    secondaryFont = secondaryFont,
                                    logoType = logoType,
                                    logoPlaceholderText = logoPlaceholderText,
                                    logoIconName = logoIconName,
                                    logoImageUri = logoImageUri,
                                    voiceTone = voiceTone,
                                    targetAudience = targetAudience,
                                    defaultHashtags = defaultHashtags,
                                    brandKeywords = brandKeywords,
                                    ctaTemplate = ctaTemplate,
                                    updatedAtMs = System.currentTimeMillis()
                                )
                                viewModel.saveBrandKit(newKit)
                                Toast.makeText(context, "✨ Brand Kit saved to local database & applied to AI models!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("save_brand_kit_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Brand Kit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (onNavigateToGenerator != null) {
                            OutlinedButton(
                                onClick = {
                                    val newKit = BrandKitEntity(
                                        id = activeBrandKit?.id ?: 0,
                                        brandName = brandName.ifBlank { "Infinity AI" },
                                        tagline = tagline,
                                        primaryColorHex = primaryColorHex,
                                        secondaryColorHex = secondaryColorHex,
                                        accentColorHex = accentColorHex,
                                        backgroundColorHex = backgroundColorHex,
                                        primaryFont = primaryFont,
                                        secondaryFont = secondaryFont,
                                        logoType = logoType,
                                        logoPlaceholderText = logoPlaceholderText,
                                        logoIconName = logoIconName,
                                        logoImageUri = logoImageUri,
                                        voiceTone = voiceTone,
                                        targetAudience = targetAudience,
                                        defaultHashtags = defaultHashtags,
                                        brandKeywords = brandKeywords,
                                        ctaTemplate = ctaTemplate,
                                        updatedAtMs = System.currentTimeMillis()
                                    )
                                    viewModel.saveBrandKit(newKit)
                                    onNavigateToGenerator()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("apply_and_create_content_btn")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate with Kit", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatchChip(name: String, color: Color, hex: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
        )
        Text(
            text = name.take(3),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ColorHexInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            val formatted = if (!input.startsWith("#") && input.isNotBlank()) "#$input" else input
            onValueChange(formatted.take(9))
        },
        label = { Text(label, fontSize = 10.sp) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

private fun parseColorSafe(hex: String, fallback: Color): Color {
    return try {
        val clean = hex.trim().removePrefix("#")
        if (clean.length == 6) {
            Color(android.graphics.Color.parseColor("#$clean"))
        } else if (clean.length == 8) {
            Color(android.graphics.Color.parseColor("#$clean"))
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}
