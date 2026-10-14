package com.example.data.models

enum class WorkspaceRole(val displayName: String, val description: String, val level: Int) {
    OWNER("Owner", "Full access to workspace, billing, role management, and settings", 5),
    ADMIN("Admin", "Can invite members, assign roles, create and delete projects", 4),
    EDITOR("Editor", "Can co-edit scripts, captions, create drafts, and update copy", 3),
    REVIEWER("Reviewer", "Can review content drafts, leave feedback, and approve/reject", 2),
    VIEWER("Viewer", "Read-only access to brand kit guidelines, assets, and projects", 1)
}

enum class MemberStatus(val label: String) {
    ACTIVE("Active"),
    INVITED("Invited"),
    PENDING("Pending")
}

enum class ProjectStatus(val label: String) {
    DRAFT("Draft"),
    IN_REVIEW("In Review"),
    CHANGES_REQUESTED("Changes Requested"),
    APPROVED("Approved"),
    SCHEDULED("Scheduled")
}

enum class ProjectAccessLevel(val label: String) {
    WORKSPACE_WIDE("Workspace Members"),
    RESTRICTED_TEAM("Restricted Team"),
    PUBLIC_LINK("Anyone with Link")
}

data class Workspace(
    val id: String,
    val name: String,
    val description: String,
    val planTier: String = "Enterprise Team",
    val currentUserRole: WorkspaceRole = WorkspaceRole.OWNER,
    val createdAtMs: Long = System.currentTimeMillis() - (86400000L * 30),
    val inviteCode: String = "WS-INF-7729"
)

data class WorkspaceMember(
    val id: String,
    val workspaceId: String,
    val name: String,
    val email: String,
    val role: WorkspaceRole,
    val status: MemberStatus = MemberStatus.ACTIVE,
    val avatarColorHex: String = "#6366F1",
    val isOnline: Boolean = false,
    val lastActive: String = "Just now",
    val joinedAtMs: Long = System.currentTimeMillis() - 86400000L * 7
)

data class SharedContentDraft(
    val title: String,
    val platform: String,
    val hook1: String,
    val hook2: String,
    val hook3: String,
    val mainBody: String,
    val callToAction: String,
    val hashtags: String,
    val videoScriptNotes: String = "",
    val lastModifiedBy: String = "You",
    val lastModifiedAtMs: Long = System.currentTimeMillis(),
    val versionNumber: Int = 1
)

data class ContentRevision(
    val revisionId: String,
    val versionNumber: Int,
    val editorName: String,
    val editorRole: WorkspaceRole,
    val timestampMs: Long,
    val summary: String,
    val draftSnapshot: SharedContentDraft
)

data class ContentComment(
    val id: String,
    val projectId: String,
    val authorName: String,
    val authorRole: WorkspaceRole,
    val authorAvatarHex: String,
    val targetSection: String, // e.g. "Hook 1", "Body Copy", "Call To Action", "General"
    val message: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

data class ActiveCollaborator(
    val name: String,
    val role: WorkspaceRole,
    val avatarHex: String,
    val activeSection: String, // e.g. "Editing Hook 2", "Reviewing Call To Action", "Viewing"
    val isTyping: Boolean = false
)

data class SharedProject(
    val id: String,
    val workspaceId: String,
    val title: String,
    val description: String,
    val platform: String, // "TikTok", "YouTube Shorts", "Instagram Reels", "Multi-Channel"
    val status: ProjectStatus = ProjectStatus.DRAFT,
    val accessLevel: ProjectAccessLevel = ProjectAccessLevel.WORKSPACE_WIDE,
    val assignedMemberIds: List<String> = emptyList(),
    val shareLink: String = "https://contentgen.ai/project/p-94812",
    val contentDraft: SharedContentDraft,
    val revisions: List<ContentRevision> = emptyList(),
    val comments: List<ContentComment> = emptyList(),
    val activeCollaborators: List<ActiveCollaborator> = emptyList(),
    val createdAtMs: Long = System.currentTimeMillis() - (86400000L * 3),
    val updatedAtMs: Long = System.currentTimeMillis()
)
