package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.data.models.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessToolsScreen(viewModel: ContentViewModel) {
    val context = LocalContext.current

    val workspaces by viewModel.workspaces.collectAsState()
    val currentWorkspaceId by viewModel.currentWorkspaceId.collectAsState()
    val allMembers by viewModel.workspaceMembers.collectAsState()
    val allProjects by viewModel.workspaceProjects.collectAsState()
    val activeCoEditProjectId by viewModel.activeCoEditProjectId.collectAsState()

    val currentWorkspace = workspaces.firstOrNull { it.id == currentWorkspaceId } ?: workspaces.firstOrNull() ?: Workspace("ws-default", "Default Workspace", "Primary Team")
    val currentMembers = allMembers.filter { it.workspaceId == currentWorkspace.id }
    val currentProjects = allProjects.filter { it.workspaceId == currentWorkspace.id }

    // If a project is currently open in the Co-Edit Studio, render it full screen!
    if (activeCoEditProjectId != null) {
        val openProject = allProjects.firstOrNull { it.id == activeCoEditProjectId }
        if (openProject != null) {
            CoEditContentStudio(
                project = openProject,
                userRole = currentWorkspace.currentUserRole,
                onBack = { viewModel.closeCoEditProject() },
                onUpdateDraft = { updatedDraft, summary ->
                    viewModel.updateProjectDraft(openProject.id, updatedDraft, summary)
                },
                onUpdateStatus = { newStatus ->
                    viewModel.updateProjectStatus(openProject.id, newStatus)
                },
                onAddComment = { targetSection, message ->
                    viewModel.addProjectComment(openProject.id, targetSection, message)
                },
                onToggleCommentResolved = { commentId ->
                    viewModel.toggleCommentResolved(openProject.id, commentId)
                },
                onRestoreRevision = { revisionId ->
                    viewModel.restoreProjectRevision(openProject.id, revisionId)
                },
                onSchedulePost = { title, platform, body ->
                    viewModel.scheduleNewPost(
                        title = title,
                        platform = platform,
                        body = body,
                        scheduledTimeMs = System.currentTimeMillis() + (86400000L * 2),
                        campaign = currentWorkspace.name
                    )
                }
            )
            return
        }
    }

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0 = Collaborative Workspaces, 1 = Brand Kit, 2 = Cloud & Integrations

    // Dialog states
    var showInviteDialog by remember { mutableStateOf(false) }
    var showRoleMatrixDialog by remember { mutableStateOf(false) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var showCreateWorkspaceDialog by remember { mutableStateOf(false) }
    var sharingProjectTarget by remember { mutableStateOf<SharedProject?>(null) }
    var memberToEditRole by remember { mutableStateOf<WorkspaceMember?>(null) }

    // Brand Kit Editor state
    val activeBrandKit by viewModel.activeBrandKit.collectAsState()
    var brandName by remember(activeBrandKit) { mutableStateOf(activeBrandKit?.brandName ?: "Infinity AI") }
    var tagline by remember(activeBrandKit) { mutableStateOf(activeBrandKit?.tagline ?: "Automate Social Growth") }
    var voiceTone by remember(activeBrandKit) { mutableStateOf(activeBrandKit?.voiceTone ?: "Viral & High-Energy") }
    var audience by remember(activeBrandKit) { mutableStateOf(activeBrandKit?.targetAudience ?: "Creators & Marketers") }
    var hashtags by remember(activeBrandKit) { mutableStateOf(activeBrandKit?.defaultHashtags ?: "#InfinityAI #ViralContent") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Navigation Tabs
        TabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = IndigoPrimaryLight
        ) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("👥 Workspaces", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_workspaces")
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("🎨 Brand Kit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_brand_kit")
            )
            Tab(
                selected = selectedMainTab == 2,
                onClick = { selectedMainTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("🌐 Cloud & Tools", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_cloud_tools")
            )
        }

        when (selectedMainTab) {
            0 -> {
                // COLLABORATIVE WORKSPACE TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Card with Active Workspace Selector
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                            shape = RoundedCornerShape(16.dp)
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = currentWorkspace.name,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            RoleBadge(role = currentWorkspace.currentUserRole)
                                        }
                                        Text(
                                            text = currentWorkspace.description,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        color = IndigoPrimary.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            currentWorkspace.planTier,
                                            color = IndigoPrimaryLight,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                // Workspace selector chip row + Create button
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    items(workspaces) { ws ->
                                        FilterChip(
                                            selected = ws.id == currentWorkspace.id,
                                            onClick = { viewModel.switchWorkspace(ws.id) },
                                            label = { Text(ws.name, fontSize = 11.sp) },
                                            leadingIcon = if (ws.id == currentWorkspace.id) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                            } else null,
                                            modifier = Modifier.testTag("ws_chip_${ws.id}")
                                        )
                                    }

                                    item {
                                        FilledTonalButton(
                                            onClick = { showCreateWorkspaceDialog = true },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("create_workspace_btn")
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("New Workspace", fontSize = 11.sp)
                                        }
                                    }
                                }

                                // Workspace Collaboration Stats Row
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StatCol(title = "Team", value = "${currentMembers.size}", color = IndigoPrimaryLight)
                                        StatCol(title = "Online", value = "${currentMembers.count { it.isOnline }}", color = EmeraldSuccess)
                                        StatCol(title = "Projects", value = "${currentProjects.size}", color = CyanAccentLight)
                                        StatCol(title = "In Review", value = "${currentProjects.count { it.status == ProjectStatus.IN_REVIEW }}", color = PurpleAccentLight)
                                    }
                                }
                            }
                        }
                    }

                    // Workspace Quick Action Buttons
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showInviteDialog = true },
                                modifier = Modifier.weight(1f).testTag("action_invite_member_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Invite Member", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showRoleMatrixDialog = true },
                                modifier = Modifier.weight(1f).testTag("action_role_matrix_btn"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Roles & Access", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showCreateProjectDialog = true },
                                modifier = Modifier.weight(1f).testTag("action_new_shared_proj_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkSlateBg)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Project", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkSlateBg)
                            }
                        }
                    }

                    // SECTION 1: TEAM MEMBERS & ASSIGNED ROLES
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = CyanAccentLight, modifier = Modifier.size(18.dp))
                                Text("Team Members & Assigned Roles", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Text("${currentMembers.size} collaborators", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    items(currentMembers) { member ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Avatar with Online dot
                                    Box {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(android.graphics.Color.parseColor(member.avatarColorHex))),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                member.name.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (member.isOnline) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(EmeraldSuccess)
                                                    .align(Alignment.BottomEnd)
                                            )
                                        }
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            RoleBadge(role = member.role)
                                        }
                                        Text(member.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            if (member.status == MemberStatus.INVITED) "Status: Invitation Pending" else "Last active: ${member.lastActive}",
                                            fontSize = 10.sp,
                                            color = if (member.status == MemberStatus.INVITED) AmberWarning else CyanAccentLight
                                        )
                                    }
                                }

                                // Actions Menu Button
                                var memberMenuExpanded by remember { mutableStateOf(false) }
                                Box {
                                    IconButton(
                                        onClick = { memberMenuExpanded = true },
                                        modifier = Modifier.testTag("member_menu_${member.id}")
                                    ) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Member Actions", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    DropdownMenu(
                                        expanded = memberMenuExpanded,
                                        onDismissRequest = { memberMenuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Assign / Change Role", fontWeight = FontWeight.SemiBold) },
                                            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = IndigoPrimaryLight) },
                                            onClick = {
                                                memberMenuExpanded = false
                                                memberToEditRole = member
                                            },
                                            modifier = Modifier.testTag("action_change_role_${member.id}")
                                        )
                                        if (member.status == MemberStatus.INVITED) {
                                            DropdownMenuItem(
                                                text = { Text("Resend Invitation Email") },
                                                leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = CyanAccentLight) },
                                                onClick = {
                                                    memberMenuExpanded = false
                                                    viewModel.resendWorkspaceInvite(member.id)
                                                    Toast.makeText(context, "Invitation resent to ${member.email}!", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                        if (member.role != WorkspaceRole.OWNER) {
                                            DropdownMenuItem(
                                                text = { Text("Remove from Workspace", color = RoseError) },
                                                leadingIcon = { Icon(Icons.Default.PersonRemove, contentDescription = null, tint = RoseError) },
                                                onClick = {
                                                    memberMenuExpanded = false
                                                    viewModel.removeWorkspaceMember(member.id)
                                                    Toast.makeText(context, "${member.name} removed from workspace", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.testTag("action_remove_member_${member.id}")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 2: SHARED PROJECTS & CO-EDITING HUB
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.FolderShared, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(18.dp))
                                Text("Shared Projects & Co-Editing Hub", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Text("${currentProjects.size} projects", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    items(currentProjects) { project ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp)
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(project.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(project.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    ProjectStatusBadge(status = project.status)
                                }

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
                                            Text(project.platform, fontSize = 10.sp, color = IndigoPrimaryLight, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }

                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("v${project.contentDraft.versionNumber}", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }

                                        Text(
                                            "Edit: ${project.contentDraft.lastModifiedBy}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Collaborator presence avatars
                                    Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                                        project.activeCollaborators.take(3).forEach { c ->
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(android.graphics.Color.parseColor(c.avatarHex))),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(c.name.take(1).uppercase(), fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                                // Project Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.openCoEditProject(project.id) },
                                        modifier = Modifier.weight(1.2f).testTag("open_coedit_btn_${project.id}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Co-Edit Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { sharingProjectTarget = project },
                                        modifier = Modifier.weight(1f).testTag("share_project_btn_${project.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", fontSize = 11.sp)
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteWorkspaceProject(project.id)
                                            Toast.makeText(context, "Project removed", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp).testTag("delete_project_btn_${project.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseError, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // BRAND GUIDELINES & VOICE KIT TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
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
                                    Text("Brand Guidelines & Voice Kit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Icon(Icons.Default.Tune, contentDescription = null, tint = IndigoPrimaryLight)
                                }

                                OutlinedTextField(
                                    value = brandName,
                                    onValueChange = { brandName = it },
                                    label = { Text("Brand / Business Name") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_brand_name")
                                )

                                OutlinedTextField(
                                    value = tagline,
                                    onValueChange = { tagline = it },
                                    label = { Text("Brand Tagline") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = voiceTone,
                                    onValueChange = { voiceTone = it },
                                    label = { Text("Voice Tone (e.g. Witty, Urgent, Professional)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = audience,
                                    onValueChange = { audience = it },
                                    label = { Text("Target Audience Archetype") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = hashtags,
                                    onValueChange = { hashtags = it },
                                    label = { Text("Default Brand Hashtags") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        viewModel.updateBrandKit(brandName, tagline, "#6366F1", "#06B6D4", voiceTone, audience, hashtags)
                                        Toast.makeText(context, "Brand Kit saved successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("save_brand_kit_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save Brand Kit Configuration", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // CLOUD ASSETS & INTEGRATIONS TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
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
                                    Text("🌐 Cloud Assets: Google Drive & Adobe CC", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Surface(
                                        color = EmeraldSuccess.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Connected",
                                            color = EmeraldSuccess,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Usage: 2.4 GB / 100 GB Cloud Storage", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Pro Plan", fontSize = 11.sp, color = IndigoPrimaryLight, fontWeight = FontWeight.Bold)
                                }

                                LinearProgressIndicator(
                                    progress = { 0.024f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                    color = IndigoPrimary
                                )

                                // Google Workspace Quick Actions
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Cloud, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(16.dp))
                                            Text("Google Workspace Suite", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { Toast.makeText(context, "Google Drive asset picker opened", Toast.LENGTH_SHORT).show() },
                                                modifier = Modifier.weight(1f).testTag("google_drive_picker_btn"),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Google Drive", fontSize = 11.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { Toast.makeText(context, "Synced tasks to Google Tasks", Toast.LENGTH_SHORT).show() },
                                                modifier = Modifier.weight(1f).testTag("google_tasks_sync_btn"),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Google Tasks", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                // Adobe Creative Cloud Quick Actions
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Brush, contentDescription = null, tint = RoseError, modifier = Modifier.size(16.dp))
                                            Text("Adobe Creative Cloud & Express", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { Toast.makeText(context, "Launching Adobe Express Editor SDK...", Toast.LENGTH_SHORT).show() },
                                                modifier = Modifier.weight(1f).testTag("adobe_express_btn"),
                                                colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Adobe Express", fontSize = 11.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { Toast.makeText(context, "Exported Brand Kit to Adobe CC Library", Toast.LENGTH_SHORT).show() },
                                                modifier = Modifier.weight(1f).testTag("adobe_cc_library_btn"),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Adobe CC Library", fontSize = 11.sp)
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
    }

    // DIALOG: Invite Member
    if (showInviteDialog) {
        InviteMemberDialog(
            workspace = currentWorkspace,
            onDismiss = { showInviteDialog = false },
            onSendInvite = { name, email, role, message ->
                viewModel.inviteTeamMember(name, email, role, message)
            }
        )
    }

    // DIALOG: Roles & Permissions Matrix
    if (showRoleMatrixDialog) {
        RoleMatrixDialog(
            onDismiss = { showRoleMatrixDialog = false }
        )
    }

    // DIALOG: Share Project
    sharingProjectTarget?.let { projectToShare ->
        ShareProjectDialog(
            project = projectToShare,
            workspaceMembers = currentMembers,
            onDismiss = { sharingProjectTarget = null },
            onSaveSharing = { accessLevel, selectedMemberIds ->
                viewModel.updateProjectAccess(projectToShare.id, accessLevel, selectedMemberIds)
                sharingProjectTarget = null
            }
        )
    }

    // DIALOG: Change Member Role
    memberToEditRole?.let { member ->
        ChangeRoleDialog(
            member = member,
            onDismiss = { memberToEditRole = null },
            onConfirmRole = { newRole ->
                viewModel.updateMemberRole(member.id, newRole)
                Toast.makeText(context, "${member.name}'s role updated to ${newRole.displayName}", Toast.LENGTH_SHORT).show()
                memberToEditRole = null
            }
        )
    }

    // DIALOG: Create Workspace
    if (showCreateWorkspaceDialog) {
        CreateWorkspaceDialog(
            onDismiss = { showCreateWorkspaceDialog = false },
            onCreateWorkspace = { name, desc, tier ->
                viewModel.createWorkspace(name, desc, tier)
                Toast.makeText(context, "Workspace '$name' created!", Toast.LENGTH_SHORT).show()
                showCreateWorkspaceDialog = false
            }
        )
    }

    // DIALOG: Create Project
    if (showCreateProjectDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateProjectDialog = false },
            onCreateProject = { title, platform, desc ->
                val newProj = viewModel.createWorkspaceProject(title, desc, platform)
                Toast.makeText(context, "Project '$title' created! Opening Co-Edit Studio...", Toast.LENGTH_SHORT).show()
                showCreateProjectDialog = false
                viewModel.openCoEditProject(newProj.id)
            }
        )
    }
}

@Composable
private fun StatCol(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeRoleDialog(
    member: WorkspaceMember,
    onDismiss: () -> Unit,
    onConfirmRole: (WorkspaceRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(member.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Security, contentDescription = null, tint = IndigoPrimaryLight)
                Column {
                    Text("Change Role: ${member.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(member.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkspaceRole.values().forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = role }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            modifier = Modifier.testTag("radio_role_${role.name}")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        RoleBadge(role = role)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(role.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(role.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmRole(selectedRole) },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_role_change_btn")
            ) {
                Text("Update Role")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CreateWorkspaceDialog(
    onDismiss: () -> Unit,
    onCreateWorkspace: (name: String, desc: String, tier: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var tier by remember { mutableStateOf("Enterprise Team") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Workspace", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Workspace Name") },
                    placeholder = { Text("e.g. Media Agency Client") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_ws_name_input")
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description & Mission") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("new_ws_desc_input")
                )
                OutlinedTextField(
                    value = tier,
                    onValueChange = { tier = it },
                    label = { Text("Plan Tier") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreateWorkspace(name, desc, tier)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_create_ws_btn")
            ) {
                Text("Create Workspace")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreateProject: (title: String, platform: String, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("TikTok") }
    var desc by remember { mutableStateOf("") }
    val platforms = listOf("TikTok", "YouTube Shorts", "Instagram Reels", "Multi-Channel")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Collaborative Project", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project / Campaign Title *") },
                    placeholder = { Text("e.g. Summer Promo Hook Blitz") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_project_title_input")
                )

                Text("Target Social Platform", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(platforms) { p ->
                        FilterChip(
                            selected = platform == p,
                            onClick = { platform = p },
                            label = { Text(p, fontSize = 11.sp) },
                            modifier = Modifier.testTag("proj_platform_chip_$p")
                        )
                    }
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Campaign Objective / Notes") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("new_project_desc_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreateProject(title, platform, desc)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_create_project_btn")
            ) {
                Text("Create & Co-Edit", fontWeight = FontWeight.Bold, color = DarkSlateBg)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
