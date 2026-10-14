package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: ContentViewModel) {
    val analyticsList by viewModel.analyticsList.collectAsState()

    var selectedPlatformFilter by remember { mutableStateOf("All") }
    val platforms = listOf("All", "TikTok", "YouTube Shorts", "Instagram Reels")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "📊 Performance Analytics",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Track views, likes, shares, followers, engagement rate, and estimated creator revenue.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Summary Revenue & Conversion Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("analytics_revenue_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("30-Day Estimated Revenue", fontSize = 12.sp, color = TextSecondaryDark)
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "+18.4% vs last month",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "$1,410.70 USD",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Divider(color = DarkSlateSurfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Views", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("299.1K", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Total Likes", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("44.2K", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Followers Gained", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("+10.8K", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }
                }
            }
        }

        // AI Recommendations Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "AI Smart Recommendation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Your TikTok short-form videos perform 34% better when published on Wednesdays between 6:00 PM - 8:00 PM.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Platform Filter Chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                platforms.forEach { platform ->
                    FilterChip(
                        selected = selectedPlatformFilter == platform,
                        onClick = { selectedPlatformFilter = platform },
                        label = { Text(platform, fontSize = 12.sp) }
                    )
                }
            }
        }

        // Platform Breakdown Cards
        val filteredList = if (selectedPlatformFilter == "All") analyticsList else analyticsList.filter { it.platform.contains(selectedPlatformFilter, ignoreCase = true) }

        items(filteredList) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            text = item.platform,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = IndigoPrimaryLight
                        )
                        Surface(
                            color = CyanAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Best Time: ${item.bestPostingTime}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccentLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(title = "Views", value = "${item.totalViews / 1000}k")
                        MetricItem(title = "Likes", value = "${item.totalLikes / 1000}k")
                        MetricItem(title = "Shares", value = "${item.totalShares}")
                        MetricItem(title = "Engagement", value = "${item.engagementRatePercent}%")
                        MetricItem(title = "Revenue", value = "$${item.estimatedRevenueUsd}")
                    }
                }
            }
        }
    }
}

@Composable
fun MetricItem(title: String, value: String) {
    Column {
        Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
