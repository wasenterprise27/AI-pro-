package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admob.AdMobNativeAdCard
import com.example.data.admob.NativeAdStyle
import com.example.data.auth.AuthManager
import com.example.data.local.SavedContentEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ContentViewModel,
    onNavigateToSection: (Int) -> Unit
) {
    val currentUser by AuthManager.currentUser.collectAsState()
    val savedContent by viewModel.allSavedContent.collectAsState()
    val planTier by viewModel.userPlanTier.collectAsState()
    val analyticsList by viewModel.analyticsList.collectAsState()
    val scheduledPosts by viewModel.allScheduledPosts.collectAsState()

    var isHighDensityMode by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Authenticated Creator Profile & Account Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("user_profile_dashboard_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = if (currentUser?.authProvider == "Google") Color(0xFF4285F4) else IndigoPrimary,
                                shape = CircleShape,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = (currentUser?.name?.take(1) ?: "C").uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentUser?.name ?: "Content Creator",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Surface(
                                        color = EmeraldSuccess.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (currentUser?.authProvider == "Google") "GOOGLE AUTH" else "VERIFIED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EmeraldSuccess,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = currentUser?.email ?: "creator@contentgen.ai",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Niche: ${currentUser?.creatorNiche ?: "Viral Content"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = IndigoPrimaryLight
                                )
                            }
                        }

                        IconButton(
                            onClick = { onNavigateToSection(9) }, // Go to Settings
                            modifier = Modifier.testTag("dashboard_profile_settings_btn")
                        ) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = "Manage Account", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Account Stats Metric Ribbon
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("AI Credits", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(13.dp))
                                    Text("${currentUser?.aiCredits ?: 25}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AmberWarning)
                                }
                            }
                            VerticalDivider(modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Generations", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${currentUser?.totalGenerations ?: 0}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            VerticalDivider(modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Saved Drafts", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${savedContent.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = IndigoPrimaryLight)
                            }
                            VerticalDivider(modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("Tier", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(currentUser?.planTier ?: "Pro", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyanAccentLight)
                            }
                        }
                    }
                }
            }
        }

        // Compact Hero / Status Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    IndigoPrimary.copy(alpha = 0.85f),
                                    PurpleAccent.copy(alpha = 0.75f),
                                    CyanAccent.copy(alpha = 0.6f)
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccentLight, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "$planTier Active Plan",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    color = if (isHighDensityMode) IndigoPrimaryLight.copy(alpha = 0.3f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable { isHighDensityMode = !isHighDensityMode }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            if (isHighDensityMode) Icons.Default.GridView else Icons.Default.ViewAgenda,
                                            contentDescription = "Toggle Grid Density",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isHighDensityMode) "Compact Grid" else "Standard View",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onNavigateToSection(9) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("admin_settings_quick_button")
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Text(
                            text = "AI pro 001 Dashboard",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Viral campaign metrics matrix & multi-channel social performance at a glance.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Button(
                                onClick = { onNavigateToSection(1) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("hero_generate_content_btn")
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Post", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { onNavigateToSection(2) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("hero_video_studio_btn")
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Video", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { onNavigateToSection(5) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Multi-Platform Performance Grid Breakdown
        item {
            Text(
                text = "Multi-Channel Performance Grid",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactPlatformCard(
                        modifier = Modifier.weight(1f),
                        platform = "TikTok",
                        views = "124.5K",
                        likes = "18.9K",
                        engagement = "8.8%",
                        revenue = "$420.50",
                        accentColor = IndigoPrimaryLight,
                        icon = Icons.Default.Movie
                    )
                    CompactPlatformCard(
                        modifier = Modifier.weight(1f),
                        platform = "YouTube Shorts",
                        views = "98.2K",
                        likes = "14.1K",
                        engagement = "9.4%",
                        revenue = "$680.00",
                        accentColor = RoseError,
                        icon = Icons.Default.PlayCircle
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactPlatformCard(
                        modifier = Modifier.weight(1f),
                        platform = "Instagram Reels",
                        views = "76.4K",
                        likes = "11.2K",
                        engagement = "7.6%",
                        revenue = "$310.20",
                        accentColor = PurpleAccent,
                        icon = Icons.Default.CameraAlt
                    )
                    CompactPlatformCard(
                        modifier = Modifier.weight(1f),
                        platform = "Facebook Video",
                        views = "42.1K",
                        likes = "6.8K",
                        engagement = "6.2%",
                        revenue = "$185.00",
                        accentColor = CyanAccent,
                        icon = Icons.Default.Share
                    )
                }
            }
        }

        // High-Density AI Launchpad
        item {
            Text(
                text = "Compact AI Launchpad",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CompactLaunchpadTile(
                        title = "Copywriter",
                        subtitle = "Captions & Hooks",
                        icon = Icons.Default.EditNote,
                        color = IndigoPrimary,
                        onClick = { onNavigateToSection(1) },
                        tag = "launchpad_copywriter"
                    )
                }
                item {
                    CompactLaunchpadTile(
                        title = "Video Studio",
                        subtitle = "TikTok Scripts",
                        icon = Icons.Default.MovieFilter,
                        color = PurpleAccent,
                        onClick = { onNavigateToSection(2) },
                        tag = "launchpad_video"
                    )
                }
                item {
                    CompactLaunchpadTile(
                        title = "Image Studio",
                        subtitle = "Logos & Banners",
                        icon = Icons.Default.Palette,
                        color = CyanAccent,
                        onClick = { onNavigateToSection(3) },
                        tag = "launchpad_image"
                    )
                }
                item {
                    CompactLaunchpadTile(
                        title = "Affiliate Suite",
                        subtitle = "Amazon Links",
                        icon = Icons.Default.ShoppingBag,
                        color = EmeraldSuccess,
                        onClick = { onNavigateToSection(7) },
                        tag = "launchpad_affiliate"
                    )
                }
                item {
                    CompactLaunchpadTile(
                        title = "AI Copilot",
                        subtitle = "Viral Strategy",
                        icon = Icons.Default.SmartToy,
                        color = AmberWarning,
                        onClick = { onNavigateToSection(8) },
                        tag = "launchpad_copilot"
                    )
                }
            }
        }

        // AdMob Native Ad Sponsored Placement in Creator Dashboard
        item {
            AdMobNativeAdCard(
                style = NativeAdStyle.COMPACT,
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        // Recent Saved Content / History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Drafts & Output",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = { onNavigateToSection(1) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("view_all_content_btn")
                ) {
                    Text("Create New", color = IndigoPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (savedContent.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = IndigoPrimaryLight
                        )
                        Text(
                            text = "No saved content yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Use the AI Content Generator or Video Studio to create your first viral post!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { onNavigateToSection(1) },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            modifier = Modifier.padding(top = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Generate First Post", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(savedContent.take(4)) { item ->
                SavedContentCard(
                    item = item,
                    onDelete = { viewModel.deleteSavedContent(item.id) },
                    onFavoriteToggle = { viewModel.toggleFavorite(item.id, item.isFavorite) }
                )
            }
        }
    }
}

@Composable
fun CompactMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    trend: String,
    isPositiveTrend: Boolean,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    if (isPositiveTrend) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isPositiveTrend) EmeraldSuccess else RoseError,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = trend,
                    fontSize = 9.sp,
                    color = if (isPositiveTrend) EmeraldSuccess else RoseError,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CompactPlatformCard(
    modifier: Modifier = Modifier,
    platform: String,
    views: String,
    likes: String,
    engagement: String,
    revenue: String,
    accentColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Text(
                        text = platform,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = engagement,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Views", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(views, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Likes", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(likes, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Est. Rev", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(revenue, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                }
            }
        }
    }
}

@Composable
fun CompactLaunchpadTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
            .testTag(tag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = EmeraldSuccess,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LaunchpadCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
            .testTag(tag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SavedContentCard(
    item: SavedContentEntity,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = IndigoPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.targetPlatform,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimaryLight
                        )
                    }
                    Text(
                        text = item.contentType,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            if (item.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) RoseError else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.hookText.isNotBlank()) {
                Text(
                    text = item.hookText,
                    fontSize = 12.sp,
                    color = CyanAccentLight,
                    maxLines = 2
                )
            }

            if (item.mainBodyText.isNotBlank()) {
                Text(
                    text = item.mainBodyText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }
        }
    }
}
