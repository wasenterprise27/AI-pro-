package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.WorkspaceRole
import com.example.ui.theme.*

data class RolePermissionItem(
    val permissionName: String,
    val owner: Boolean,
    val admin: Boolean,
    val editor: Boolean,
    val reviewer: Boolean,
    val viewer: Boolean
)

@Composable
fun RoleMatrixDialog(onDismiss: () -> Unit) {
    val permissions = listOf(
        RolePermissionItem("Workspace Billing & Deletion", owner = true, admin = false, editor = false, reviewer = false, viewer = false),
        RolePermissionItem("Invite & Remove Team Members", owner = true, admin = true, editor = false, reviewer = false, viewer = false),
        RolePermissionItem("Assign & Change Roles", owner = true, admin = true, editor = false, reviewer = false, viewer = false),
        RolePermissionItem("Create New Projects & Campaigns", owner = true, admin = true, editor = true, reviewer = false, viewer = false),
        RolePermissionItem("Co-Edit Content, Scripts & Hooks", owner = true, admin = true, editor = true, reviewer = false, viewer = false),
        RolePermissionItem("Leave Comments & Suggestions", owner = true, admin = true, editor = true, reviewer = true, viewer = false),
        RolePermissionItem("Approve Content / Request Changes", owner = true, admin = true, editor = false, reviewer = true, viewer = false),
        RolePermissionItem("Publish / Schedule to Social Calendar", owner = true, admin = true, editor = true, reviewer = false, viewer = false),
        RolePermissionItem("View Brand Kits & Asset Libraries", owner = true, admin = true, editor = true, reviewer = true, viewer = true)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = IndigoPrimaryLight)
                Column {
                    Text("Workspace Role Permissions", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Role hierarchy and access levels", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Role summaries
                WorkspaceRole.values().forEach { role ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleBadge(role = role)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(role.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(role.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Permission Breakdown", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = IndigoPrimaryLight)

                // Permission items
                permissions.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(item.permissionName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RoleIndicator("Own", item.owner, AmberWarning)
                                RoleIndicator("Adm", item.admin, PurpleAccentLight)
                                RoleIndicator("Edt", item.editor, CyanAccentLight)
                                RoleIndicator("Rev", item.reviewer, EmeraldSuccess)
                                RoleIndicator("Vw", item.viewer, Color.Gray)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("close_role_matrix_btn")
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun RoleIndicator(label: String, hasPermission: Boolean, activeColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (hasPermission) {
            Icon(Icons.Default.Check, contentDescription = "Yes", tint = activeColor, modifier = Modifier.size(14.dp))
        } else {
            Icon(Icons.Default.Close, contentDescription = "No", tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
        }
    }
}
