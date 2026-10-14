package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ScheduledPostEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialManagerScreen(viewModel: ContentViewModel) {
    val context = LocalContext.current
    val scheduledPosts by viewModel.allScheduledPosts.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Calendar & Posts, 1: Connected Accounts, 2: Campaigns & Holidays
    var showScheduleDialog by remember { mutableStateOf(false) }

    var postTitle by remember { mutableStateOf("") }
    var postBody by remember { mutableStateOf("") }
    var postPlatform by remember { mutableStateOf("TikTok") }

    val platforms = listOf("TikTok", "Instagram", "YouTube", "Facebook", "X", "LinkedIn", "Pinterest", "Threads")
    val connectedAccounts = remember {
        mutableStateListOf(
            "TikTok" to "@contentgen_pro (Verified)",
            "Instagram" to "@contentgen.ai (Connected)",
            "YouTube" to "ContentGen AI Channel",
            "Facebook" to "ContentGen AI Official Page"
        )
    }

    val upcomingHolidays = listOf(
        "July 28" to "World Nature Day (Trending: Eco Tech)",
        "August 1" to "National Creator Day (Viral Hook Opportunities)",
        "August 12" to "International Youth Day (Gen Z Trends)",
        "August 19" to "World Photography Day (Aesthetic Shots)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📱 Social Manager & Calendar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Schedule, auto-publish, and manage campaigns across all social channels.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FloatingActionButton(
                    onClick = { showScheduleDialog = true },
                    modifier = Modifier.size(44.dp).testTag("add_scheduled_post_fab"),
                    containerColor = IndigoPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Schedule Post")
                }
            }
        }

        // Segmented Tab Selector
        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = IndigoPrimary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Content Calendar", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Accounts (${connectedAccounts.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Campaigns & Trends", fontSize = 12.sp) }
                )
            }
        }

        if (activeTab == 0) {
            // Calendar Agenda View
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Scheduled Posts Agenda", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Auto-Posting Enabled",
                            color = EmeraldSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (scheduledPosts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(36.dp))
                            Text("No posts scheduled yet", fontWeight = FontWeight.SemiBold)
                            Text("Click '+' above or generate a post to schedule your campaign.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(scheduledPosts) { post ->
                    ScheduledPostCard(
                        post = post,
                        onPublishNow = {
                            viewModel.updatePostStatus(post.id, "PUBLISHED")
                            Toast.makeText(context, "Published to ${post.platform} successfully!", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = { viewModel.deleteScheduledPost(post.id) }
                    )
                }
            }
        } else if (activeTab == 1) {
            // Connected Accounts Manager
            item {
                Text("Manage Social Accounts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(connectedAccounts) { (plat, handle) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(plat, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(handle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        TextButton(
                            onClick = { Toast.makeText(context, "Account token refreshed", Toast.LENGTH_SHORT).show() }
                        ) {
                            Text("Connected", color = EmeraldSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { Toast.makeText(context, "OAuth login dialog opened", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.fillMaxWidth().testTag("connect_new_account_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Connect Another Social Channel")
                }
            }
        } else {
            // Campaigns & Trends
            item {
                Text("Viral Holiday Calendar & Trends", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(upcomingHolidays) { (date, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(date, fontWeight = FontWeight.Bold, color = CyanAccent, fontSize = 12.sp)
                            Text(desc, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = {
                                postTitle = "Holiday Campaign: $date"
                                postBody = desc
                                showScheduleDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text("Plan", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Schedule Dialog Modal
    if (showScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = { Text("Schedule Social Post") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = postTitle,
                        onValueChange = { postTitle = it },
                        label = { Text("Post Title / Campaign") },
                        modifier = Modifier.fillMaxWidth().testTag("modal_post_title")
                    )
                    OutlinedTextField(
                        value = postBody,
                        onValueChange = { postBody = it },
                        label = { Text("Post Caption & Hashtags") },
                        modifier = Modifier.fillMaxWidth().height(90.dp).testTag("modal_post_body")
                    )

                    Text("Select Platform", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(platforms) { p ->
                            FilterChip(
                                selected = postPlatform == p,
                                onClick = { postPlatform = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (postTitle.isNotBlank()) {
                            viewModel.scheduleNewPost(postTitle, postPlatform, postBody, System.currentTimeMillis() + 86400000)
                            showScheduleDialog = false
                            postTitle = ""
                            postBody = ""
                            Toast.makeText(context, "Post scheduled successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("confirm_schedule_btn")
                ) {
                    Text("Schedule Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ScheduledPostCard(
    post: ScheduledPostEntity,
    onPublishNow: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(post.scheduledTimeMs))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = IndigoPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = post.platform,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimaryLight
                        )
                    }

                    Surface(
                        color = if (post.status == "PUBLISHED") EmeraldSuccess.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = post.status,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (post.status == "PUBLISHED") EmeraldSuccess else AmberWarning
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                }
            }

            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (post.postBody.isNotBlank()) {
                Text(post.postBody, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏰ $formattedDate",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (post.status != "PUBLISHED") {
                    TextButton(onClick = onPublishNow) {
                        Text("Publish Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IndigoPrimaryLight)
                    }
                }
            }
        }
    }
}
