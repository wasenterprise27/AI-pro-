package com.example.data.local

import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object WorkspaceRepository {

    private val defaultWorkspaces = listOf(
        Workspace(
            id = "ws-alpha",
            name = "Marketing Team Alpha",
            description = "Multi-platform viral content team managing TikTok, Shorts, and Reels campaigns.",
            planTier = "Enterprise Team Pro",
            currentUserRole = WorkspaceRole.OWNER,
            inviteCode = "WS-ALPHA-2026"
        ),
        Workspace(
            id = "ws-ecommerce",
            name = "E-Commerce Growth Hub",
            description = "Direct response creative team crafting Amazon affiliate and DTC TikTok ad scripts.",
            planTier = "Team Standard",
            currentUserRole = WorkspaceRole.ADMIN,
            inviteCode = "WS-ECOM-8831"
        ),
        Workspace(
            id = "ws-personal",
            name = "Personal Creator Suite",
            description = "Solo creator workspace for personal branding and rapid experiment drafts.",
            planTier = "Pro Solo",
            currentUserRole = WorkspaceRole.OWNER,
            inviteCode = "WS-SOLO-0012"
        )
    )

    private val defaultMembers = listOf(
        WorkspaceMember(
            id = "m-0",
            workspaceId = "ws-alpha",
            name = "Stephen (You)",
            email = "stephenampangwapyalenge@gmail.com",
            role = WorkspaceRole.OWNER,
            status = MemberStatus.ACTIVE,
            avatarColorHex = "#6366F1",
            isOnline = true,
            lastActive = "Active now"
        ),
        WorkspaceMember(
            id = "m-1",
            workspaceId = "ws-alpha",
            name = "Sarah Jenkins",
            email = "sarah.j@growthcreative.co",
            role = WorkspaceRole.ADMIN,
            status = MemberStatus.ACTIVE,
            avatarColorHex = "#8B5CF6",
            isOnline = true,
            lastActive = "5m ago"
        ),
        WorkspaceMember(
            id = "m-2",
            workspaceId = "ws-alpha",
            name = "David Kim",
            email = "david.kim@studioflow.io",
            role = WorkspaceRole.EDITOR,
            status = MemberStatus.ACTIVE,
            avatarColorHex = "#06B6D4",
            isOnline = true,
            lastActive = "Editing Hook 2"
        ),
        WorkspaceMember(
            id = "m-3",
            workspaceId = "ws-alpha",
            name = "Elena Rostova",
            email = "elena.r@brandhouse.agency",
            role = WorkspaceRole.REVIEWER,
            status = MemberStatus.ACTIVE,
            avatarColorHex = "#10B981",
            isOnline = true,
            lastActive = "Reviewing CTA"
        ),
        WorkspaceMember(
            id = "m-4",
            workspaceId = "ws-alpha",
            name = "Marcus Vance",
            email = "marcus.v@contentops.org",
            role = WorkspaceRole.EDITOR,
            status = MemberStatus.ACTIVE,
            avatarColorHex = "#F59E0B",
            isOnline = false,
            lastActive = "2 hours ago"
        ),
        WorkspaceMember(
            id = "m-5",
            workspaceId = "ws-alpha",
            name = "Chloe Taylor",
            email = "chloe.t@brandclient.com",
            role = WorkspaceRole.VIEWER,
            status = MemberStatus.INVITED,
            avatarColorHex = "#EC4899",
            isOnline = false,
            lastActive = "Invitation Pending"
        )
    )

    private val defaultProjects = listOf(
        SharedProject(
            id = "proj-1",
            workspaceId = "ws-alpha",
            title = "Q4 Black Friday Viral Campaign",
            description = "High-converting 30s TikTok & Reels ad scripts targeting high-intent impulse buyers.",
            platform = "TikTok & Reels",
            status = ProjectStatus.IN_REVIEW,
            accessLevel = ProjectAccessLevel.WORKSPACE_WIDE,
            assignedMemberIds = listOf("m-0", "m-1", "m-2", "m-3"),
            shareLink = "https://contentgen.ai/share/proj-bf-viral-2026",
            contentDraft = SharedContentDraft(
                title = "Q4 Black Friday Viral Campaign",
                platform = "TikTok",
                hook1 = "Stop scrolling if you hate paying full price for creator gear! 🔥",
                hook2 = "The 3 secret AI tools that 7-figure creators never talk about...",
                hook3 = "I tested 10 productivity apps for 30 days so you don't have to.",
                mainBody = "Black Friday is around the corner, and the biggest creators are already stockpiling high-converting hooks. If you run an agency or sell products on TikTok Shop, having your visual assets and video scripts pre-rendered gives you an unfair advantage.\n\nHere is our exact 3-step script workflow that generated 2.4M organic views last quarter without paid ads.",
                callToAction = "Drop 'CONTENT' in the comments or tap the link in bio to clone this complete campaign template before prices jump!",
                hashtags = "#BlackFridayDeals #CreatorEconomy #TikTokMadeMeBuyIt #ViralMarketing #AIProductivity",
                videoScriptNotes = "Shot 1 (0-3s): Close up shot holding gear. Sudden sound glitch.\nShot 2 (3-12s): Screen recording showing metrics skyrocketing.\nShot 3 (12-25s): Dynamic split screen demoing 3 AI features.\nShot 4 (25-30s): Pointing down to bio link with urgent countdown timer.",
                lastModifiedBy = "David Kim",
                lastModifiedAtMs = System.currentTimeMillis() - (1000 * 60 * 12),
                versionNumber = 3
            ),
            revisions = listOf(
                ContentRevision(
                    revisionId = "rev-3",
                    versionNumber = 3,
                    editorName = "David Kim",
                    editorRole = WorkspaceRole.EDITOR,
                    timestampMs = System.currentTimeMillis() - (1000 * 60 * 12),
                    summary = "Refined Hook 1 urgency and added visual audio cues to Video Script.",
                    draftSnapshot = SharedContentDraft(
                        title = "Q4 Black Friday Viral Campaign",
                        platform = "TikTok",
                        hook1 = "Stop scrolling if you hate paying full price for creator gear! 🔥",
                        hook2 = "The 3 secret AI tools that 7-figure creators never talk about...",
                        hook3 = "I tested 10 productivity apps for 30 days so you don't have to.",
                        mainBody = "Black Friday is around the corner, and the biggest creators are already stockpiling high-converting hooks. If you run an agency or sell products on TikTok Shop, having your visual assets and video scripts pre-rendered gives you an unfair advantage.",
                        callToAction = "Drop 'CONTENT' in the comments or tap the link in bio to clone this complete campaign template before prices jump!",
                        hashtags = "#BlackFridayDeals #CreatorEconomy #TikTokMadeMeBuyIt",
                        videoScriptNotes = "Shot 1: Close up. Shot 2: Metrics. Shot 3: Features.",
                        lastModifiedBy = "David Kim",
                        versionNumber = 3
                    )
                ),
                ContentRevision(
                    revisionId = "rev-2",
                    versionNumber = 2,
                    editorName = "Sarah Jenkins",
                    editorRole = WorkspaceRole.ADMIN,
                    timestampMs = System.currentTimeMillis() - (1000 * 60 * 65),
                    summary = "Added conversion-focused CTA with keyword trigger 'CONTENT'.",
                    draftSnapshot = SharedContentDraft(
                        title = "Q4 Black Friday Viral Campaign",
                        platform = "TikTok",
                        hook1 = "Don't buy creator gear at full price.",
                        hook2 = "3 secret AI tools.",
                        hook3 = "Tested 10 apps.",
                        mainBody = "Black Friday is approaching. Prepare your scripts in advance.",
                        callToAction = "Drop 'CONTENT' in comments to clone template.",
                        hashtags = "#BlackFriday #CreatorTips",
                        lastModifiedBy = "Sarah Jenkins",
                        versionNumber = 2
                    )
                ),
                ContentRevision(
                    revisionId = "rev-1",
                    versionNumber = 1,
                    editorName = "Stephen (You)",
                    editorRole = WorkspaceRole.OWNER,
                    timestampMs = System.currentTimeMillis() - (1000 * 60 * 180),
                    summary = "Initial campaign draft generated from AI Brand Kit.",
                    draftSnapshot = SharedContentDraft(
                        title = "Q4 Black Friday Viral Campaign",
                        platform = "TikTok",
                        hook1 = "Check this Black Friday offer.",
                        hook2 = "Tools you need to know.",
                        hook3 = "Apps review.",
                        mainBody = "Initial copy brief.",
                        callToAction = "Link in bio.",
                        hashtags = "#Deals",
                        lastModifiedBy = "Stephen (You)",
                        versionNumber = 1
                    )
                )
            ),
            comments = listOf(
                ContentComment(
                    id = "c-1",
                    projectId = "proj-1",
                    authorName = "Elena Rostova",
                    authorRole = WorkspaceRole.REVIEWER,
                    authorAvatarHex = "#10B981",
                    targetSection = "Hook 1",
                    message = "Hook 1 is super strong! Let's ensure the font overlay is in high-contrast yellow so it pops on dark TikTok backgrounds.",
                    timestampMs = System.currentTimeMillis() - (1000 * 60 * 45),
                    isResolved = false
                ),
                ContentComment(
                    id = "c-2",
                    projectId = "proj-1",
                    authorName = "Sarah Jenkins",
                    authorRole = WorkspaceRole.ADMIN,
                    authorAvatarHex = "#8B5CF6",
                    targetSection = "Call To Action",
                    message = "Tested comment automation with 'CONTENT' trigger. Ready to approve once video audio cuts are confirmed.",
                    timestampMs = System.currentTimeMillis() - (1000 * 60 * 20),
                    isResolved = false
                )
            ),
            activeCollaborators = listOf(
                ActiveCollaborator("Stephen (You)", WorkspaceRole.OWNER, "#6366F1", "Editing", isTyping = false),
                ActiveCollaborator("David Kim", WorkspaceRole.EDITOR, "#06B6D4", "Editing Hook 2", isTyping = true),
                ActiveCollaborator("Elena Rostova", WorkspaceRole.REVIEWER, "#10B981", "Reviewing CTA", isTyping = false)
            )
        ),
        SharedProject(
            id = "proj-2",
            workspaceId = "ws-alpha",
            title = "YouTube Shorts AI Growth Blueprint",
            description = "Educational breakdown explaining prompt engineering for video generation in under 60 seconds.",
            platform = "YouTube Shorts",
            status = ProjectStatus.APPROVED,
            accessLevel = ProjectAccessLevel.WORKSPACE_WIDE,
            assignedMemberIds = listOf("m-0", "m-1"),
            shareLink = "https://contentgen.ai/share/proj-yt-ai-growth",
            contentDraft = SharedContentDraft(
                title = "YouTube Shorts AI Growth Blueprint",
                platform = "YouTube Shorts",
                hook1 = "Most people use AI prompts completely backwards. Here is how pros do it.",
                hook2 = "Why 99% of AI video prompts fail within the first 3 seconds.",
                hook3 = "The exact prompt syntax that unlocks photorealistic Sora & Veo generations.",
                mainBody = "When generating video with modern AI diffusion models, your camera motion keywords matter twice as much as the subject description.\n\nStructure your prompt in 4 layers: Camera Rig, Subject Lighting, Micro-movement, and Lens Distortion.",
                callToAction = "Subscribe for daily AI creator secrets and grab our prompt cheat sheet in the pinned comment!",
                hashtags = "#YouTubeShorts #AIVideo #PromptEngineering #TechTutorials #ContentGenAI",
                videoScriptNotes = "0-5s: Kinetic typography intro. 5-35s: Live prompt terminal breakdown. 35-50s: Before vs after output. 50-60s: Subscribe CTA badge.",
                lastModifiedBy = "Stephen (You)",
                lastModifiedAtMs = System.currentTimeMillis() - (1000 * 60 * 360),
                versionNumber = 2
            ),
            revisions = emptyList(),
            comments = emptyList(),
            activeCollaborators = listOf(
                ActiveCollaborator("Stephen (You)", WorkspaceRole.OWNER, "#6366F1", "Viewing", isTyping = false)
            )
        ),
        SharedProject(
            id = "proj-3",
            workspaceId = "ws-alpha",
            title = "Instagram Carousel: 7 Brand Archetypes",
            description = "High-save aesthetic carousel post outlining typography and color psychology for social agencies.",
            platform = "Instagram",
            status = ProjectStatus.DRAFT,
            accessLevel = ProjectAccessLevel.RESTRICTED_TEAM,
            assignedMemberIds = listOf("m-0", "m-4"),
            shareLink = "https://contentgen.ai/share/proj-ig-brand-archetypes",
            contentDraft = SharedContentDraft(
                title = "Instagram Carousel: 7 Brand Archetypes",
                platform = "Instagram",
                hook1 = "Your brand font is losing you clients. Fix it in 5 slides.",
                hook2 = "Which brand archetype does your creator business belong to?",
                hook3 = "The psychology behind luxury dark-mode aesthetics.",
                mainBody = "Swipe through for the 7 dominant visual archetypes used by top 1% brands in 2026. Save this post for your next rebrand brainstorming session.",
                callToAction = "Save this carousel so you don't lose it later, and share it with your design team! 🔖",
                hashtags = "#BrandStrategy #GraphicDesign #CreativeAgency #VisualIdentity #InstagramMarketing",
                lastModifiedBy = "Marcus Vance",
                lastModifiedAtMs = System.currentTimeMillis() - (1000 * 60 * 1200),
                versionNumber = 1
            )
        )
    )

    private val _workspaces = MutableStateFlow(defaultWorkspaces)
    val workspaces: StateFlow<List<Workspace>> = _workspaces.asStateFlow()

    private val _currentWorkspaceId = MutableStateFlow("ws-alpha")
    val currentWorkspaceId: StateFlow<String> = _currentWorkspaceId.asStateFlow()

    private val _members = MutableStateFlow(defaultMembers)
    val members: StateFlow<List<WorkspaceMember>> = _members.asStateFlow()

    private val _projects = MutableStateFlow(defaultProjects)
    val projects: StateFlow<List<SharedProject>> = _projects.asStateFlow()

    fun switchWorkspace(workspaceId: String) {
        if (_workspaces.value.any { it.id == workspaceId }) {
            _currentWorkspaceId.value = workspaceId
        }
    }

    fun createWorkspace(name: String, description: String, planTier: String = "Enterprise Team"): Workspace {
        val newWs = Workspace(
            id = "ws-" + UUID.randomUUID().toString().take(8),
            name = name,
            description = description,
            planTier = planTier,
            currentUserRole = WorkspaceRole.OWNER,
            createdAtMs = System.currentTimeMillis(),
            inviteCode = "WS-" + name.take(4).uppercase() + "-" + (1000..9999).random()
        )
        _workspaces.value = _workspaces.value + newWs
        _currentWorkspaceId.value = newWs.id

        // Add current user as Owner
        val ownerMember = WorkspaceMember(
            id = "m-" + UUID.randomUUID().toString().take(6),
            workspaceId = newWs.id,
            name = "Stephen (You)",
            email = "stephenampangwapyalenge@gmail.com",
            role = WorkspaceRole.OWNER,
            status = MemberStatus.ACTIVE,
            avatarColorHex = "#6366F1",
            isOnline = true
        )
        _members.value = _members.value + ownerMember
        return newWs
    }

    fun inviteTeamMember(
        name: String,
        email: String,
        role: WorkspaceRole,
        personalMessage: String = ""
    ): WorkspaceMember {
        val currentWsId = _currentWorkspaceId.value
        val avatarPalette = listOf("#6366F1", "#06B6D4", "#8B5CF6", "#10B981", "#F59E0B", "#EC4899")
        val newMember = WorkspaceMember(
            id = "m-" + UUID.randomUUID().toString().take(6),
            workspaceId = currentWsId,
            name = name.ifBlank { email.substringBefore("@") },
            email = email,
            role = role,
            status = MemberStatus.INVITED,
            avatarColorHex = avatarPalette.random(),
            isOnline = false,
            lastActive = "Invitation Sent"
        )
        _members.value = _members.value + newMember
        return newMember
    }

    fun updateMemberRole(memberId: String, newRole: WorkspaceRole) {
        _members.value = _members.value.map { member ->
            if (member.id == memberId) member.copy(role = newRole) else member
        }
    }

    fun removeMember(memberId: String) {
        _members.value = _members.value.filter { it.id != memberId }
    }

    fun resendInvite(memberId: String) {
        _members.value = _members.value.map { member ->
            if (member.id == memberId) member.copy(lastActive = "Invite Resent Just Now") else member
        }
    }

    fun createProject(
        title: String,
        description: String,
        platform: String,
        initialDraft: SharedContentDraft? = null
    ): SharedProject {
        val currentWsId = _currentWorkspaceId.value
        val draft = initialDraft ?: SharedContentDraft(
            title = title,
            platform = platform,
            hook1 = "Stop scrolling! Here is what you need to know about $title",
            hook2 = "The secret trick to mastering $title in 2026...",
            hook3 = "I tried $title for 7 days, and this happened.",
            mainBody = "Collaborative draft for $title.\n\nType your high-retention script or caption body here. Co-editors can view edits in real-time.",
            callToAction = "Follow for daily tips and drop a comment below!",
            hashtags = "#${platform.replace(" ", "")} #ViralContent #AIPro #ContentGen",
            lastModifiedBy = "Stephen (You)",
            versionNumber = 1
        )

        val newProject = SharedProject(
            id = "proj-" + UUID.randomUUID().toString().take(8),
            workspaceId = currentWsId,
            title = title,
            description = description,
            platform = platform,
            status = ProjectStatus.DRAFT,
            accessLevel = ProjectAccessLevel.WORKSPACE_WIDE,
            assignedMemberIds = listOf("m-0"),
            shareLink = "https://contentgen.ai/share/proj-" + UUID.randomUUID().toString().take(6),
            contentDraft = draft,
            revisions = listOf(
                ContentRevision(
                    revisionId = "rev-1",
                    versionNumber = 1,
                    editorName = "Stephen (You)",
                    editorRole = WorkspaceRole.OWNER,
                    timestampMs = System.currentTimeMillis(),
                    summary = "Initial project creation",
                    draftSnapshot = draft
                )
            ),
            comments = emptyList(),
            activeCollaborators = listOf(
                ActiveCollaborator("Stephen (You)", WorkspaceRole.OWNER, "#6366F1", "Editing", false)
            )
        )

        _projects.value = listOf(newProject) + _projects.value
        return newProject
    }

    fun updateProjectAccess(projectId: String, accessLevel: ProjectAccessLevel, memberIds: List<String>) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                proj.copy(accessLevel = accessLevel, assignedMemberIds = memberIds, updatedAtMs = System.currentTimeMillis())
            } else proj
        }
    }

    fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                proj.copy(status = newStatus, updatedAtMs = System.currentTimeMillis())
            } else proj
        }
    }

    fun updateProjectDraft(
        projectId: String,
        updatedDraft: SharedContentDraft,
        editorName: String = "Stephen (You)",
        editorRole: WorkspaceRole = WorkspaceRole.OWNER,
        changeSummary: String = "Updated content draft"
    ) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                val newVersion = proj.contentDraft.versionNumber + 1
                val stampedDraft = updatedDraft.copy(
                    lastModifiedBy = editorName,
                    lastModifiedAtMs = System.currentTimeMillis(),
                    versionNumber = newVersion
                )
                val newRevision = ContentRevision(
                    revisionId = "rev-$newVersion",
                    versionNumber = newVersion,
                    editorName = editorName,
                    editorRole = editorRole,
                    timestampMs = System.currentTimeMillis(),
                    summary = changeSummary,
                    draftSnapshot = stampedDraft
                )
                proj.copy(
                    contentDraft = stampedDraft,
                    revisions = listOf(newRevision) + proj.revisions,
                    updatedAtMs = System.currentTimeMillis()
                )
            } else proj
        }
    }

    fun restoreRevision(projectId: String, revisionId: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                val rev = proj.revisions.find { it.revisionId == revisionId }
                if (rev != null) {
                    val restoredDraft = rev.draftSnapshot.copy(
                        lastModifiedBy = "Restored by Stephen",
                        lastModifiedAtMs = System.currentTimeMillis(),
                        versionNumber = proj.contentDraft.versionNumber + 1
                    )
                    val newRev = ContentRevision(
                        revisionId = "rev-${restoredDraft.versionNumber}",
                        versionNumber = restoredDraft.versionNumber,
                        editorName = "Stephen (You)",
                        editorRole = WorkspaceRole.OWNER,
                        timestampMs = System.currentTimeMillis(),
                        summary = "Restored version v${rev.versionNumber}",
                        draftSnapshot = restoredDraft
                    )
                    proj.copy(
                        contentDraft = restoredDraft,
                        revisions = listOf(newRev) + proj.revisions,
                        updatedAtMs = System.currentTimeMillis()
                    )
                } else proj
            } else proj
        }
    }

    fun addComment(
        projectId: String,
        authorName: String,
        authorRole: WorkspaceRole,
        targetSection: String,
        message: String
    ) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                val comment = ContentComment(
                    id = "c-" + UUID.randomUUID().toString().take(6),
                    projectId = projectId,
                    authorName = authorName,
                    authorRole = authorRole,
                    authorAvatarHex = "#6366F1",
                    targetSection = targetSection,
                    message = message,
                    timestampMs = System.currentTimeMillis()
                )
                proj.copy(comments = proj.comments + comment)
            } else proj
        }
    }

    fun toggleCommentResolved(projectId: String, commentId: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                val updatedComments = proj.comments.map { comment ->
                    if (comment.id == commentId) comment.copy(isResolved = !comment.isResolved) else comment
                }
                proj.copy(comments = updatedComments)
            } else proj
        }
    }

    fun deleteProject(projectId: String) {
        _projects.value = _projects.value.filter { it.id != projectId }
    }
}
