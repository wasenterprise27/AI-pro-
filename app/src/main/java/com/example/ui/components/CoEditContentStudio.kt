package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.models.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoEditContentStudio(
    project: SharedProject,
    userRole: WorkspaceRole,
    onBack: () -> Unit,
    onUpdateDraft: (updatedDraft: SharedContentDraft, changeSummary: String) -> Unit,
    onUpdateStatus: (ProjectStatus) -> Unit,
    onAddComment: (targetSection: String, message: String) -> Unit,
    onToggleCommentResolved: (commentId: String) -> Unit,
    onRestoreRevision: (revisionId: String) -> Unit,
    onSchedulePost: (title: String, platform: String, body: String) -> Unit
) {
    val context = LocalContext.current

    var hook1 by remember(project.id, project.contentDraft.versionNumber) { mutableStateOf(project.contentDraft.hook1) }
    var hook2 by remember(project.id, project.contentDraft.versionNumber) { mutableStateOf(project.contentDraft.hook2) }
    var hook3 by remember(project.id, project.contentDraft.versionNumber) { mutableStateOf(project.contentDraft.hook3) }
    var mainBody by remember(project.id, project.contentDraft.versionNumber) { mutableStateOf(project.contentDraft.mainBody) }
    var callToAction by remember(project.id, project.contentDraft.versionNumber) { mutableStateOf(project.contentDraft.callToAction) }
    var hashtags by remember(project.id, project.contentDraft.versionNumber) { mutableStateOf(project.contentDraft.hashtags) }
    var videoScriptNotes by remember(project.id, project.contentDraft.versionNumber) { mutableStateOf(project.contentDraft.videoScriptNotes) }

    var selectedStudioTab by remember { mutableIntStateOf(0) } // 0 = Co-Edit Editor, 1 = Team Comments (N), 2 = Revision History
    var showRevisionSheet by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    var newCommentSection by remember { mutableStateOf("General") }

    val canEdit = userRole.level >= WorkspaceRole.EDITOR.level
    val canApprove = userRole.level >= WorkspaceRole.REVIEWER.level

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Studio Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp).testTag("coedit_back_btn")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = IndigoPrimaryLight)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = project.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Surface(
                                    color = IndigoPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "v${project.contentDraft.versionNumber}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimaryLight,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Platform: ${project.platform} • Last edit by ${project.contentDraft.lastModifiedBy}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Save / Sync button
                    if (canEdit) {
                        Button(
                            onClick = {
                                val updated = project.contentDraft.copy(
                                    hook1 = hook1,
                                    hook2 = hook2,
                                    hook3 = hook3,
                                    mainBody = mainBody,
                                    callToAction = callToAction,
                                    hashtags = hashtags,
                                    videoScriptNotes = videoScriptNotes
                                )
                                onUpdateDraft(updated, "Manual team save & sync")
                                Toast.makeText(context, "Saved & synced to collaborative workspace! (v${project.contentDraft.versionNumber + 1})", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("coedit_save_sync_btn")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Draft", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Active Collaborators Real-time Presence Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Text("Active Now:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)

                        project.activeCollaborators.forEach { collaborator ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(collaborator.avatarHex))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(collaborator.name.take(1).uppercase(), fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        collaborator.name.substringBefore(" "),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (collaborator.isTyping) {
                                        Text("✏️", fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Project Status Badge
                    ProjectStatusBadge(status = project.status)
                }
            }
        }

        // Sub Tabs: [ ✏️ Co-Edit Content ] [ 💬 Comments (X) ] [ 📜 Revisions (Y) ]
        TabRow(
            selectedTabIndex = selectedStudioTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = IndigoPrimaryLight
        ) {
            Tab(
                selected = selectedStudioTab == 0,
                onClick = { selectedStudioTab = 0 },
                text = { Text("✏️ Co-Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_coedit_editor")
            )
            Tab(
                selected = selectedStudioTab == 1,
                onClick = { selectedStudioTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("💬 Comments", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        if (project.comments.isNotEmpty()) {
                            Badge(containerColor = AmberWarning) {
                                Text("${project.comments.count { !it.isResolved }}", fontSize = 10.sp, color = DarkSlateBg)
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("tab_coedit_comments")
            )
            Tab(
                selected = selectedStudioTab == 2,
                onClick = { selectedStudioTab = 2 },
                text = { Text("📜 History (${project.revisions.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_coedit_history")
            )
        }

        // Content Area based on Tab
        when (selectedStudioTab) {
            0 -> {
                // Co-Editing Editor & Approval Workflow
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Approval Workflow Bar
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                            shape = RoundedCornerShape(12.dp)
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
                                    Text("Workflow Status & Approval", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanAccentLight)
                                    Text("Your Role: ${userRole.displayName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Submit for Review
                                    if (project.status == ProjectStatus.DRAFT || project.status == ProjectStatus.CHANGES_REQUESTED) {
                                        Button(
                                            onClick = {
                                                onUpdateStatus(ProjectStatus.IN_REVIEW)
                                                Toast.makeText(context, "Submitted draft for team review!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f).testTag("btn_submit_for_review"),
                                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Submit Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Reviewer / Admin Actions
                                    if (canApprove) {
                                        if (project.status != ProjectStatus.APPROVED) {
                                            Button(
                                                onClick = {
                                                    onUpdateStatus(ProjectStatus.APPROVED)
                                                    Toast.makeText(context, "Content draft approved by ${userRole.displayName}!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.weight(1f).testTag("btn_approve_content"),
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Approve Draft", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    onUpdateStatus(ProjectStatus.CHANGES_REQUESTED)
                                                    Toast.makeText(context, "Changes requested sent to editors", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.weight(1f).testTag("btn_request_changes"),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                            ) {
                                                Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(13.dp), tint = AmberWarning)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Needs Change", fontSize = 11.sp)
                                            }
                                        }
                                    }

                                    // Schedule to Social Manager
                                    if (project.status == ProjectStatus.APPROVED) {
                                        Button(
                                            onClick = {
                                                val combinedBody = "$mainBody\n\n$callToAction\n\n$hashtags"
                                                onSchedulePost(project.title, project.platform, combinedBody)
                                                onUpdateStatus(ProjectStatus.SCHEDULED)
                                                Toast.makeText(context, "Draft scheduled to Content Calendar!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f).testTag("btn_push_to_calendar"),
                                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(13.dp), tint = DarkSlateBg)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Schedule Post", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkSlateBg)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Viral Hooks Section
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
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
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberWarning)
                                        Text("Viral Hooks (A/B Test Variations)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Surface(
                                        color = CyanAccent.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Co-editing active", fontSize = 10.sp, color = CyanAccentLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                OutlinedTextField(
                                    value = hook1,
                                    onValueChange = { hook1 = it },
                                    label = { Text("Hook Option A (Curiosity & Pattern-Interrupt)") },
                                    enabled = canEdit,
                                    modifier = Modifier.fillMaxWidth().testTag("coedit_hook_1")
                                )

                                OutlinedTextField(
                                    value = hook2,
                                    onValueChange = { hook2 = it },
                                    label = { Text("Hook Option B (Shock Factor & Urgency)") },
                                    enabled = canEdit,
                                    modifier = Modifier.fillMaxWidth().testTag("coedit_hook_2")
                                )

                                OutlinedTextField(
                                    value = hook3,
                                    onValueChange = { hook3 = it },
                                    label = { Text("Hook Option C (Relatable Pain Point)") },
                                    enabled = canEdit,
                                    modifier = Modifier.fillMaxWidth().testTag("coedit_hook_3")
                                )
                            }
                        }
                    }

                    // Main Body / Script Section
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
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
                                    Text("📝 Content Body & Voiceover Script", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${mainBody.length} chars", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                OutlinedTextField(
                                    value = mainBody,
                                    onValueChange = { mainBody = it },
                                    label = { Text("Captions Body / Story Arc") },
                                    minLines = 4,
                                    enabled = canEdit,
                                    modifier = Modifier.fillMaxWidth().testTag("coedit_main_body")
                                )

                                OutlinedTextField(
                                    value = videoScriptNotes,
                                    onValueChange = { videoScriptNotes = it },
                                    label = { Text("Shot List / Visual Cues & Audio Breakdown") },
                                    minLines = 3,
                                    enabled = canEdit,
                                    modifier = Modifier.fillMaxWidth().testTag("coedit_script_notes")
                                )
                            }
                        }
                    }

                    // Call To Action & Hashtags
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("🎯 Call To Action & SEO Hashtags", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                                OutlinedTextField(
                                    value = callToAction,
                                    onValueChange = { callToAction = it },
                                    label = { Text("Call to Action Directive") },
                                    enabled = canEdit,
                                    modifier = Modifier.fillMaxWidth().testTag("coedit_cta")
                                )

                                OutlinedTextField(
                                    value = hashtags,
                                    onValueChange = { hashtags = it },
                                    label = { Text("Target Hashtags") },
                                    enabled = canEdit,
                                    modifier = Modifier.fillMaxWidth().testTag("coedit_hashtags")
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Team Comments & Feedback
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Team Feedback & Suggestions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Add Comment Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Target Section:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                val sections = listOf("General", "Hook 1", "Hook 2", "Main Body", "Call To Action")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(sections) { s ->
                                        FilterChip(
                                            selected = newCommentSection == s,
                                            onClick = { newCommentSection = s },
                                            label = { Text(s, fontSize = 10.sp) },
                                            modifier = Modifier.testTag("comment_section_chip_$s")
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newCommentText,
                                    onValueChange = { newCommentText = it },
                                    placeholder = { Text("Leave feedback or ask for changes...", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f).testTag("new_comment_input"),
                                    maxLines = 2
                                )

                                Button(
                                    onClick = {
                                        if (newCommentText.isNotBlank()) {
                                            onAddComment(newCommentSection, newCommentText)
                                            newCommentText = ""
                                            Toast.makeText(context, "Feedback added!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("send_comment_btn")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Post", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Comments List
                    if (project.comments.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(top = 40.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No team feedback yet.", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Leave the first comment to collaborate on this draft.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(project.comments) { comment ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (comment.isResolved) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
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
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clip(CircleShape)
                                                        .background(IndigoPrimary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(comment.authorName.take(1).uppercase(), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                                Text(comment.authorName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                RoleBadge(role = comment.authorRole)
                                            }

                                            Surface(
                                                color = IndigoPrimary.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "On: ${comment.targetSection}",
                                                    fontSize = 10.sp,
                                                    color = CyanAccentLight,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = comment.message,
                                            fontSize = 13.sp,
                                            color = if (comment.isResolved) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val timeStr = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()).format(Date(comment.timestampMs))
                                            Text(timeStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                            TextButton(
                                                onClick = { onToggleCommentResolved(comment.id) },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(
                                                    if (comment.isResolved) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = if (comment.isResolved) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (comment.isResolved) "Resolved" else "Mark Resolved", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Revisions History
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Revision History & Snapshots", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("View earlier drafts and restore past versions if needed.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    items(project.revisions) { rev ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            color = IndigoPrimary.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("v${rev.versionNumber}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoPrimaryLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Text(rev.editorName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    }

                                    val dateStr = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()).format(Date(rev.timestampMs))
                                    Text(dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Text(rev.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            onRestoreRevision(rev.revisionId)
                                            Toast.makeText(context, "Restored version v${rev.versionNumber}!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.testTag("restore_revision_${rev.versionNumber}")
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Restore Version", fontSize = 10.sp)
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

@Composable
fun ProjectStatusBadge(status: ProjectStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        ProjectStatus.DRAFT -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        ProjectStatus.IN_REVIEW -> PurpleAccent.copy(alpha = 0.2f) to PurpleAccentLight
        ProjectStatus.CHANGES_REQUESTED -> AmberWarning.copy(alpha = 0.2f) to AmberWarning
        ProjectStatus.APPROVED -> EmeraldSuccess.copy(alpha = 0.2f) to EmeraldSuccess
        ProjectStatus.SCHEDULED -> CyanAccent.copy(alpha = 0.2f) to CyanAccentLight
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = status.label.uppercase(),
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
