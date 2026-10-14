package com.example.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admob.AdMobInterstitialHelper
import com.example.data.remote.AdobeAssetType
import com.example.data.remote.AdobeCcAsset
import com.example.data.remote.AdobeCcImportResult
import com.example.data.remote.AdobeCcLibrary
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ImageStudioScreen(viewModel: ContentViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTool by remember { mutableStateOf("PROMPT_GEN") } // PROMPT_GEN, LOGO, BANNER, THUMBNAIL, FLYER, MOCKUP, REMOVE_BG, ENHANCE
    var promptInput by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("3D Cyberpunk") }
    var generatedPromptResult by remember { mutableStateOf("") }
    var isGeneratingPrompt by remember { mutableStateOf(false) }
    val userPlanTier by viewModel.userPlanTier.collectAsState()

    // Adobe Creative Cloud Service Layer States
    val adobeCcLibraries by viewModel.adobeCcLibraries.collectAsState()
    val adobeCcAssets by viewModel.adobeCcAssets.collectAsState()
    val selectedCcAsset by viewModel.selectedCcAsset.collectAsState()
    val isCcLoading by viewModel.isCcLoading.collectAsState()
    val lastCcSyncStatus by viewModel.lastCcSyncStatus.collectAsState()

    var ccSearchQuery by remember { mutableStateOf("") }
    var selectedCcLibraryId by remember { mutableStateOf("ALL") }
    var selectedCcTypeFilter by remember { mutableStateOf<AdobeAssetType?>(null) }
    var showCcAssetBrowser by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            promptInput = "AI Vision Analysis of captured photo: High quality product/subject photo captured on device camera."
            Toast.makeText(context, "Photo captured! Added to AI image prompt.", Toast.LENGTH_SHORT).show()
        }
    }

    val tools = listOf(
        "PROMPT_GEN" to "AI Image Prompts",
        "LOGO" to "Logo Maker",
        "BANNER" to "Banner Creator",
        "THUMBNAIL" to "Thumbnail Creator",
        "FLYER" to "Flyer & Poster",
        "MOCKUP" to "Product Mockups",
        "REMOVE_BG" to "Background Remover",
        "ENHANCE" to "AI Enhancer"
    )

    val styles = listOf("3D Cyberpunk", "Studio Product Photography", "Minimalist Vector", "Warm Cinematic", "Neon Synthwave", "Photorealistic 8K")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "🎨 AI Image Studio",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Generate AI image prompts, logos, social banners, thumbnails, product mockups, and photo enhancement.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tools Tab Bar
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tools) { (key, label) ->
                    FilterChip(
                        selected = selectedTool == key,
                        onClick = { selectedTool = key },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanAccent,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("tool_chip_$key")
                    )
                }
            }
        }

        // Generator Input Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = when (selectedTool) {
                            "LOGO" -> "Describe Your Brand Logo"
                            "THUMBNAIL" -> "YouTube/TikTok Video Subject"
                            "MOCKUP" -> "Product Details for Mockup"
                            "REMOVE_BG" -> "Image Subject to Isolate"
                            "ENHANCE" -> "Photo to Upscale & Enhance"
                            else -> "Image Concept / Description"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("e.g. Sleek wireless earbuds floating over glowing cyan splash liquid, studio lighting, hyperrealistic...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("image_prompt_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Camera Capture Section for AI Tasks
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                                    Text("Capture Photo for AI Tasks", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (cameraPermissionState.status.isGranted) {
                                            takePictureLauncher.launch()
                                        } else if (cameraPermissionState.status.shouldShowRationale) {
                                            showPermissionRationaleDialog = true
                                        } else {
                                            cameraPermissionState.launchPermissionRequest()
                                        }
                                    },
                                    modifier = Modifier.testTag("capture_photo_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (cameraPermissionState.status.isGranted) "Take Photo" else "Grant Camera & Take",
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            capturedBitmap?.let { bitmap ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Captured Photo",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(Color.Black, RoundedCornerShape(8.dp))
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Photo Captured", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldSuccess)
                                        Text("Ready for AI vision & image generator tasks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { capturedBitmap = null }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear Photo", tint = RoseError)
                                    }
                                }
                            }
                        }
                    }

                    // Adobe Creative Cloud Asset Import Hub
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Brush, contentDescription = null, tint = RoseError, modifier = Modifier.size(18.dp))
                                    Text("Adobe Creative Cloud Assets", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { viewModel.syncAdobeCcLibrary() },
                                        modifier = Modifier.size(28.dp).testTag("sync_adobe_cc_icon_btn")
                                    ) {
                                        Icon(Icons.Default.Sync, contentDescription = "Sync CC Assets", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }
                                    TextButton(
                                        onClick = { showCcAssetBrowser = !showCcAssetBrowser },
                                        modifier = Modifier.testTag("toggle_adobe_cc_browser_btn"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (showCcAssetBrowser) "Hide Assets" else "Browse CC Assets (${adobeCcAssets.size})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoseError
                                        )
                                    }
                                }
                            }

                            selectedCcAsset?.let { importedAsset ->
                                Surface(
                                    color = RoseError.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Surface(
                                                    color = RoseError,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        importedAsset.fileFormat,
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                                Text(importedAsset.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Text(
                                                "Library: ${importedAsset.libraryName} • ${importedAsset.dimensions}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.selectCcAsset(null) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove imported asset", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            if (showCcAssetBrowser) {
                                // Search bar for CC assets
                                OutlinedTextField(
                                    value = ccSearchQuery,
                                    onValueChange = {
                                        ccSearchQuery = it
                                        viewModel.fetchAdobeCcLibrariesAndAssets(selectedCcLibraryId, selectedCcTypeFilter, it)
                                    },
                                    placeholder = { Text("Search CC photos, designs, PSDs, vectors...", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("adobe_cc_search_input"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                // Library Filter Row
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    item {
                                        FilterChip(
                                            selected = selectedCcLibraryId == "ALL",
                                            onClick = {
                                                selectedCcLibraryId = "ALL"
                                                viewModel.fetchAdobeCcLibrariesAndAssets(null, selectedCcTypeFilter, ccSearchQuery)
                                            },
                                            label = { Text("All CC Libraries", fontSize = 10.sp) }
                                        )
                                    }
                                    items(adobeCcLibraries) { lib ->
                                        FilterChip(
                                            selected = selectedCcLibraryId == lib.id,
                                            onClick = {
                                                selectedCcLibraryId = lib.id
                                                viewModel.fetchAdobeCcLibrariesAndAssets(lib.name, selectedCcTypeFilter, ccSearchQuery)
                                            },
                                            label = { Text("${lib.name} (${lib.assetCount})", fontSize = 10.sp) }
                                        )
                                    }
                                }

                                // Type Filter Row
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    item {
                                        FilterChip(
                                            selected = selectedCcTypeFilter == null,
                                            onClick = {
                                                selectedCcTypeFilter = null
                                                viewModel.fetchAdobeCcLibrariesAndAssets(selectedCcLibraryId, null, ccSearchQuery)
                                            },
                                            label = { Text("All Formats", fontSize = 10.sp) }
                                        )
                                    }
                                    items(AdobeAssetType.values()) { type ->
                                        FilterChip(
                                            selected = selectedCcTypeFilter == type,
                                            onClick = {
                                                selectedCcTypeFilter = type
                                                viewModel.fetchAdobeCcLibrariesAndAssets(selectedCcLibraryId, type, ccSearchQuery)
                                            },
                                            label = { Text(type.name, fontSize = 10.sp) }
                                        )
                                    }
                                }

                                if (isCcLoading) {
                                    LinearProgressIndicator(
                                        modifier = Modifier.fillMaxWidth().height(3.dp),
                                        color = RoseError
                                    )
                                }

                                // List of Adobe CC Assets
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    adobeCcAssets.forEach { asset ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Surface(
                                                    color = when (asset.type) {
                                                        AdobeAssetType.PHOTO -> CyanAccent.copy(alpha = 0.2f)
                                                        AdobeAssetType.DESIGN -> RoseError.copy(alpha = 0.2f)
                                                        AdobeAssetType.VECTOR -> IndigoPrimaryLight.copy(alpha = 0.2f)
                                                        AdobeAssetType.BRAND_KIT -> EmeraldSuccess.copy(alpha = 0.2f)
                                                        AdobeAssetType.ILLUSTRATION -> AmberWarning.copy(alpha = 0.2f)
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.size(44.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = when (asset.type) {
                                                                AdobeAssetType.PHOTO -> Icons.Default.Camera
                                                                AdobeAssetType.DESIGN -> Icons.Default.Brush
                                                                AdobeAssetType.VECTOR -> Icons.Default.FormatPaint
                                                                AdobeAssetType.BRAND_KIT -> Icons.Default.Style
                                                                AdobeAssetType.ILLUSTRATION -> Icons.Default.Palette
                                                            },
                                                            contentDescription = null,
                                                            tint = when (asset.type) {
                                                                AdobeAssetType.PHOTO -> CyanAccentLight
                                                                AdobeAssetType.DESIGN -> RoseError
                                                                AdobeAssetType.VECTOR -> IndigoPrimaryLight
                                                                AdobeAssetType.BRAND_KIT -> EmeraldSuccess
                                                                AdobeAssetType.ILLUSTRATION -> AmberWarning
                                                            },
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Text(asset.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                                        Surface(
                                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                                            shape = RoundedCornerShape(4.dp)
                                                        ) {
                                                            Text(
                                                                asset.fileFormat,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = RoseError,
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        "${asset.libraryName} • ${asset.dimensions} • ${asset.fileSizeFormatted}",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )

                                                    // Swatches
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    ) {
                                                        asset.colorPaletteHex.forEach { hex ->
                                                            val colorVal = try {
                                                                Color(android.graphics.Color.parseColor(hex))
                                                            } catch (e: Exception) {
                                                                Color.Gray
                                                            }
                                                            Surface(
                                                                color = colorVal,
                                                                shape = androidx.compose.foundation.shape.CircleShape,
                                                                modifier = Modifier.size(10.dp)
                                                            ) {}
                                                        }
                                                    }
                                                }

                                                Button(
                                                    onClick = {
                                                        viewModel.importCcAssetToStudio(asset) { result ->
                                                            promptInput = result.extractedPrompt
                                                            Toast.makeText(context, "Imported '${asset.name}' into AI Image Studio!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("import_cc_asset_btn_${asset.id}")
                                                ) {
                                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Import", fontSize = 11.sp, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text("Visual Style & Preset", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(styles) { style ->
                            FilterChip(
                                selected = selectedStyle == style,
                                onClick = { selectedStyle = style },
                                label = { Text(style, fontSize = 11.sp) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (promptInput.isBlank()) {
                                Toast.makeText(context, "Please enter a visual concept first", Toast.LENGTH_SHORT).show()
                            } else {
                                isGeneratingPrompt = true
                                generatedPromptResult = "/imagine prompt: $promptInput, $selectedStyle style, volumetric lighting, 8k resolution, octane render, trending on ArtStation, ray tracing, sharp focus, vibrant colors, cinematic composition --ar 16:9 --v 6.0"
                                isGeneratingPrompt = false
                                Toast.makeText(context, "Prompt formulated!", Toast.LENGTH_SHORT).show()
                                // Show interstitial ad event for free tier monetization
                                AdMobInterstitialHelper.showAdIfFreeTier(context, userPlanTier)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_image_prompt_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Formulate High-Precision Prompt", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Generated Prompt Result Card
        if (generatedPromptResult.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("generated_prompt_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
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
                            Text("✨ Formulated Prompt Output", fontWeight = FontWeight.Bold, color = CyanAccentLight, fontSize = 14.sp)
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(generatedPromptResult))
                                    Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("copy_prompt_btn")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Prompt", tint = Color.White)
                            }
                        }

                        Surface(
                            color = DarkSlateBg,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedPromptResult,
                                color = TextPrimaryDark,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Text(
                            text = "💡 Optimized for Midjourney v6, DALL-E 3, Flux Pro & Gemini Image Generation Models.",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { Toast.makeText(context, "Saved prompt to Google Drive!", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier.weight(1f).testTag("save_to_drive_btn"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyanAccentLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save to Google Drive", fontSize = 11.sp, color = CyanAccentLight)
                            }

                            Button(
                                onClick = { Toast.makeText(context, "Opening in Adobe Express Canvas...", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier.weight(1f).testTag("edit_in_adobe_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit in Adobe Express", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Quick Preset Templates Grid
        item {
            Text("Preset Template Canvas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TemplatePresetCard(
                        modifier = Modifier.weight(1f),
                        title = "YouTube Thumbnail",
                        aspect = "16:9",
                        icon = Icons.Default.OndemandVideo
                    ) {
                        promptInput = "High contrast expressive creator face pointing at glowing text '10X GROWTH' on dark gradient background"
                    }
                    TemplatePresetCard(
                        modifier = Modifier.weight(1f),
                        title = "Instagram Story/Reel",
                        aspect = "9:16",
                        icon = Icons.Default.PhoneIphone
                    ) {
                        promptInput = "Aesthetic product unboxing shot on warm wooden table with soft organic morning shadows"
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TemplatePresetCard(
                        modifier = Modifier.weight(1f),
                        title = "Square Product Ad",
                        aspect = "1:1",
                        icon = Icons.Default.ShoppingBag
                    ) {
                        promptInput = "Studio product photography of sleek cosmetic bottle surrounded by fresh tropical leaves"
                    }
                    TemplatePresetCard(
                        modifier = Modifier.weight(1f),
                        title = "Brand Vector Logo",
                        aspect = "1:1",
                        icon = Icons.Default.Brush
                    ) {
                        promptInput = "Minimalist geometric mascot logo, clean flat vector, gradient cyan and violet"
                    }
                }
            }
        }
    }

    if (showPermissionRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionRationaleDialog = false },
            title = { Text("Camera Permission Required", fontWeight = FontWeight.Bold) },
            text = { Text("Camera access is needed to capture photos for AI image vision tasks, product mockups, and background removal.") },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationaleDialog = false
                        cameraPermissionState.launchPermissionRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text("Grant Permission", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationaleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TemplatePresetCard(
    modifier: Modifier = Modifier,
    title: String,
    aspect: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("Aspect $aspect", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
