package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.models.ProjectAccessLevel
import com.example.data.models.SharedProject
import com.example.data.models.WorkspaceMember
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProjectDialog(
    project: SharedProject,
    workspaceMembers: List<WorkspaceMember>,
    onDismiss: () -> Unit,
    onSaveSharing: (accessLevel: ProjectAccessLevel, selectedMemberIds: List<String>) -> Unit
) {
    val context = LocalContext.current
    var accessLevel by remember { mutableStateOf(project.accessLevel) }
    var selectedMembers by remember { mutableStateOf(project.assignedMemberIds.toSet()) }
    var linkCanEdit by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = CyanAccentLight)
                Column {
                    Text("Share Project & Co-Edit", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(project.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Shareable Link Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
                            Text("Collaboration Link", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccentLight)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (linkCanEdit) "Can Co-Edit" else "View Only", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = linkCanEdit,
                                    onCheckedChange = { linkCanEdit = it },
                                    modifier = Modifier.size(width = 34.dp, height = 20.dp).testTag("toggle_link_edit_permission")
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = project.shareLink,
                                fontSize = 11.sp,
                                maxLines = 1,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Project Link", project.shareLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Collaboration link copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp).testTag("copy_project_link_btn")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = IndigoPrimaryLight, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Access Scope Radios
                Text("General Access Scope", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                ProjectAccessLevel.values().forEach { level ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { accessLevel = level }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = accessLevel == level,
                            onClick = { accessLevel = level },
                            modifier = Modifier.testTag("access_radio_${level.name}")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(level.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            val desc = when (level) {
                                ProjectAccessLevel.WORKSPACE_WIDE -> "All members in this workspace can access and co-edit"
                                ProjectAccessLevel.RESTRICTED_TEAM -> "Only assigned team members can access this project"
                                ProjectAccessLevel.PUBLIC_LINK -> "Anyone with the collaboration link can view/co-edit"
                            }
                            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Team Members Selector (if Restricted Team or for assigning specific members)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Assigned Team Collaborators", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                workspaceMembers.forEach { member ->
                    val isChecked = selectedMembers.contains(member.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedMembers = if (isChecked) {
                                    selectedMembers - member.id
                                } else {
                                    selectedMembers + member.id
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                selectedMembers = if (checked) {
                                    selectedMembers + member.id
                                } else {
                                    selectedMembers - member.id
                                }
                            },
                            modifier = Modifier.testTag("member_checkbox_${member.id}")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(member.name.take(1).uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(member.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(member.email, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        RoleBadge(role = member.role)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSharing(accessLevel, selectedMembers.toList())
                    Toast.makeText(context, "Project sharing permissions updated!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_sharing_btn")
            ) {
                Text("Save Sharing Settings", fontWeight = FontWeight.Bold, color = DarkSlateBg)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_sharing_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
