package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Workspace
import com.example.data.models.WorkspaceRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteMemberDialog(
    workspace: Workspace,
    onDismiss: () -> Unit,
    onSendInvite: (name: String, email: String, role: WorkspaceRole, message: String) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(WorkspaceRole.EDITOR) }
    var personalMessage by remember { mutableStateOf("Hey! Join our creative workspace on Infinity Content AI to collaborate on viral scripts and campaigns.") }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = IndigoPrimaryLight)
                Column {
                    Text("Invite Team Member", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Workspace: ${workspace.name}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Member Work Email *") },
                    placeholder = { Text("colleague@agency.com") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = IndigoPrimaryLight) },
                    modifier = Modifier.fillMaxWidth().testTag("invite_email_input")
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name (Optional)") },
                    placeholder = { Text("e.g. Alex Rivera") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = CyanAccentLight) },
                    modifier = Modifier.fillMaxWidth().testTag("invite_name_input")
                )

                // Role Selector with info
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Assigned Role & Permissions *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                    ExposedDropdownMenuBox(
                        expanded = roleDropdownExpanded,
                        onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${selectedRole.displayName} — ${selectedRole.description}",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("invite_role_selector")
                        )

                        ExposedDropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false }
                        ) {
                            WorkspaceRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                RoleBadge(role = role)
                                                Text(role.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                            Text(role.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        selectedRole = role
                                        roleDropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("role_option_${role.name}")
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = personalMessage,
                    onValueChange = { personalMessage = it },
                    label = { Text("Custom Invitation Note") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("invite_note_input")
                )

                // Shareable link preview
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Workspace Invite Code", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(workspace.inviteCode, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccentLight)
                        }
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Invite Code", workspace.inviteCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Invite code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_invite_code_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = IndigoPrimaryLight)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onSendInvite(name, email, selectedRole, personalMessage)
                    Toast.makeText(context, "Invitation dispatched to $email!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_invite_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Send Invite", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_invite_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RoleBadge(role: WorkspaceRole, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (role) {
        WorkspaceRole.OWNER -> AmberWarning.copy(alpha = 0.2f) to AmberWarning
        WorkspaceRole.ADMIN -> PurpleAccent.copy(alpha = 0.2f) to PurpleAccentLight
        WorkspaceRole.EDITOR -> CyanAccent.copy(alpha = 0.2f) to CyanAccentLight
        WorkspaceRole.REVIEWER -> EmeraldSuccess.copy(alpha = 0.2f) to EmeraldSuccess
        WorkspaceRole.VIEWER -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = role.displayName.uppercase(),
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
