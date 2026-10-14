package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.firebase.FirebaseManager
import com.example.data.firebase.FirebaseSyncStatus
import com.example.data.local.*
import com.example.data.models.*
import com.example.data.remote.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.contentDao()

    val allSavedContent: StateFlow<List<SavedContentEntity>> = dao.getAllSavedContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allScheduledPosts: StateFlow<List<ScheduledPostEntity>> = dao.getAllScheduledPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBrandKit: StateFlow<BrandKitEntity?> = dao.getActiveBrandKit()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val analyticsList: StateFlow<List<AnalyticsEntity>> = dao.getAllAnalytics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedPackage = MutableStateFlow<GeneratedContentPackage?>(null)
    val generatedPackage: StateFlow<GeneratedContentPackage?> = _generatedPackage.asStateFlow()

    private val _userPlanTier = MutableStateFlow("Pro") // Free, Pro, Business
    val userPlanTier: StateFlow<String> = _userPlanTier.asStateFlow()

    // AI Video Prompt-to-Video State (30s+ Duration Engine)
    private val _isGeneratingVideo = MutableStateFlow(false)
    val isGeneratingVideo: StateFlow<Boolean> = _isGeneratingVideo.asStateFlow()

    private val _videoGenProgress = MutableStateFlow(0f)
    val videoGenProgress: StateFlow<Float> = _videoGenProgress.asStateFlow()

    private val _videoGenStage = MutableStateFlow("")
    val videoGenStage: StateFlow<String> = _videoGenStage.asStateFlow()

    private val _currentGeneratedVideo = MutableStateFlow<AiGeneratedVideo?>(null)
    val currentGeneratedVideo: StateFlow<AiGeneratedVideo?> = _currentGeneratedVideo.asStateFlow()

    private val _savedVideosList = MutableStateFlow<List<AiGeneratedVideo>>(emptyList())
    val savedVideosList: StateFlow<List<AiGeneratedVideo>> = _savedVideosList.asStateFlow()

    // Adobe Creative Cloud Service Layer State
    private val _adobeCcLibraries = MutableStateFlow<List<AdobeCcLibrary>>(emptyList())
    val adobeCcLibraries: StateFlow<List<AdobeCcLibrary>> = _adobeCcLibraries.asStateFlow()

    private val _adobeCcAssets = MutableStateFlow<List<AdobeCcAsset>>(emptyList())
    val adobeCcAssets: StateFlow<List<AdobeCcAsset>> = _adobeCcAssets.asStateFlow()

    private val _selectedCcAsset = MutableStateFlow<AdobeCcAsset?>(null)
    val selectedCcAsset: StateFlow<AdobeCcAsset?> = _selectedCcAsset.asStateFlow()

    private val _isCcLoading = MutableStateFlow(false)
    val isCcLoading: StateFlow<Boolean> = _isCcLoading.asStateFlow()

    private val _lastCcSyncStatus = MutableStateFlow<String?>(null)
    val lastCcSyncStatus: StateFlow<String?> = _lastCcSyncStatus.asStateFlow()

    private val _assistantChat = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "model" to "👋 Hello! I am your ContentGen AI Copilot. Ask me for viral content ideas, SEO keywords, TikTok trends, or campaign strategies!"
        )
    )
    val assistantChat: StateFlow<List<Pair<String, String>>> = _assistantChat.asStateFlow()

    init {
        // Initial load for Adobe Creative Cloud Assets & Libraries
        fetchAdobeCcLibrariesAndAssets()

        // Ensure default brand kit exists if none present
        viewModelScope.launch {
            val currentKit = dao.getActiveBrandKit().firstOrNull()
            if (currentKit == null) {
                dao.insertBrandKit(
                    BrandKitEntity(
                        brandName = "Infinity AI",
                        tagline = "Create Viral Content in Seconds",
                        primaryColorHex = "#6366F1",
                        secondaryColorHex = "#06B6D4",
                        accentColorHex = "#EC4899",
                        backgroundColorHex = "#0F172A",
                        primaryFont = "Inter / Modern Sans",
                        secondaryFont = "JetBrains Mono / Code Display",
                        logoType = "MONOGRAM",
                        logoPlaceholderText = "⚡ INF",
                        logoIconName = "Bolt",
                        voiceTone = "Viral & High-Energy",
                        targetAudience = "Creators, Marketers & E-Commerce Brands",
                        defaultHashtags = "#InfinityAI #ViralContent #CreatorEconomy #MarketingPro",
                        brandKeywords = "Innovation, Speed, High Retention, Scalable Growth",
                        ctaTemplate = "Tap the link in bio to supercharge your reach today! 🚀"
                    )
                )
            }
        }

        // Pre-populate mock analytics if empty
        viewModelScope.launch {
            dao.getAllAnalytics().firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    dao.insertAnalytics(AnalyticsEntity(platform = "TikTok", totalViews = 124500, totalLikes = 18900, totalShares = 3200, totalFollowers = 4800, engagementRatePercent = 8.8f, estimatedRevenueUsd = 420.50, bestPostingTime = "7:00 PM"))
                    dao.insertAnalytics(AnalyticsEntity(platform = "YouTube Shorts", totalViews = 98200, totalLikes = 14100, totalShares = 2100, totalFollowers = 3100, engagementRatePercent = 9.4f, estimatedRevenueUsd = 680.00, bestPostingTime = "5:30 PM"))
                    dao.insertAnalytics(AnalyticsEntity(platform = "Instagram Reels", totalViews = 76400, totalLikes = 11200, totalShares = 1800, totalFollowers = 2900, engagementRatePercent = 7.6f, estimatedRevenueUsd = 310.20, bestPostingTime = "8:00 PM"))
                }
            }
        }
    }

    fun fetchAdobeCcLibrariesAndAssets(
        libraryId: String? = null,
        typeFilter: AdobeAssetType? = null,
        searchQuery: String = ""
    ) {
        viewModelScope.launch {
            _isCcLoading.value = true
            if (_adobeCcLibraries.value.isEmpty()) {
                _adobeCcLibraries.value = AdobeCreativeCloudRepository.getLibraries()
            }
            _adobeCcAssets.value = AdobeCreativeCloudRepository.fetchUserAssets(libraryId, typeFilter, searchQuery)
            _isCcLoading.value = false
        }
    }

    fun selectCcAsset(asset: AdobeCcAsset?) {
        _selectedCcAsset.value = asset
    }

    fun importCcAssetToStudio(
        asset: AdobeCcAsset,
        onSuccess: (AdobeCcImportResult) -> Unit
    ) {
        viewModelScope.launch {
            _isCcLoading.value = true
            val result = AdobeCreativeCloudRepository.importAssetToStudio(asset)
            _selectedCcAsset.value = asset
            _isCcLoading.value = false
            onSuccess(result)
        }
    }

    fun syncAdobeCcLibrary() {
        viewModelScope.launch {
            _isCcLoading.value = true
            val status = AdobeCreativeCloudRepository.syncCreativeCloud()
            _lastCcSyncStatus.value = status
            _adobeCcLibraries.value = AdobeCreativeCloudRepository.getLibraries()
            _adobeCcAssets.value = AdobeCreativeCloudRepository.fetchUserAssets()
            _isCcLoading.value = false
        }
    }

    fun generateContent(
        topicOrUrl: String,
        platform: String,
        tone: String,
        contentType: String,
        useBrandKit: Boolean = true
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            val brandKit = if (useBrandKit) activeBrandKit.value else null
            val pkg = GeminiRepository.generateSocialContent(
                topicOrUrl = topicOrUrl,
                platform = platform,
                tone = tone,
                contentType = contentType,
                brandKit = brandKit
            )
            _generatedPackage.value = pkg
            _isGenerating.value = false
            AuthManager.recordGeneration()
        }
    }

    fun generateVideoScript(
        topic: String,
        platform: String,
        durationSeconds: Int,
        useBrandKit: Boolean = true
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            val brandKit = if (useBrandKit) activeBrandKit.value else null
            val pkg = GeminiRepository.generateVideoScript(
                topic = topic,
                platform = platform,
                durationSeconds = durationSeconds,
                brandKit = brandKit
            )
            _generatedPackage.value = pkg
            _isGenerating.value = false
            AuthManager.recordGeneration()
        }
    }

    fun sendAssistantMessage(userMsg: String) {
        if (userMsg.isBlank()) return
        val currentList = _assistantChat.value.toMutableList()
        currentList.add("user" to userMsg)
        _assistantChat.value = currentList

        viewModelScope.launch {
            val response = GeminiRepository.chatWithAiAssistant(userMsg, currentList)
            val updatedList = _assistantChat.value.toMutableList()
            updatedList.add("model" to response)
            _assistantChat.value = updatedList
        }
    }

    fun savePackageToHistory(pkg: GeneratedContentPackage) {
        viewModelScope.launch {
            val entity = SavedContentEntity(
                title = pkg.title,
                contentType = pkg.contentType,
                targetPlatform = pkg.platform,
                topicOrUrl = pkg.title,
                hookText = pkg.hooks.joinToString("\n• "),
                mainBodyText = pkg.captionsAndBody,
                ctaText = pkg.cta,
                hashtags = pkg.hashtags.joinToString(" "),
                scriptJson = pkg.videoScenes.joinToString("\n") { "Scene ${it.sceneNumber} (${it.durationSeconds}s): ${it.voiceoverText}" }
            )
            dao.insertSavedContent(entity)
        }
    }

    fun scheduleNewPost(title: String, platform: String, body: String, scheduledTimeMs: Long, campaign: String = "General") {
        viewModelScope.launch {
            dao.insertScheduledPost(
                ScheduledPostEntity(
                    title = title,
                    platform = platform,
                    postBody = body,
                    scheduledTimeMs = scheduledTimeMs,
                    campaignName = campaign
                )
            )
        }
    }

    fun updatePostStatus(id: Long, status: String) {
        viewModelScope.launch {
            dao.updatePostStatus(id, status)
        }
    }

    fun deleteScheduledPost(id: Long) {
        viewModelScope.launch {
            dao.deleteScheduledPost(id)
        }
    }

    fun deleteSavedContent(id: Long) {
        viewModelScope.launch {
            dao.deleteSavedContent(id)
        }
    }

    fun toggleFavorite(id: Long, currentFav: Boolean) {
        viewModelScope.launch {
            dao.updateFavorite(id, !currentFav)
        }
    }

    fun saveBrandKit(brandKit: BrandKitEntity) {
        viewModelScope.launch {
            dao.insertBrandKit(brandKit)
        }
    }

    fun updateBrandKit(
        name: String,
        tagline: String,
        primaryColor: String,
        secondaryColor: String,
        tone: String,
        audience: String,
        hashtags: String
    ) {
        viewModelScope.launch {
            val existing = dao.getActiveBrandKit().firstOrNull()
            dao.insertBrandKit(
                existing?.copy(
                    brandName = name,
                    tagline = tagline,
                    primaryColorHex = primaryColor,
                    secondaryColorHex = secondaryColor,
                    voiceTone = tone,
                    targetAudience = audience,
                    defaultHashtags = hashtags,
                    updatedAtMs = System.currentTimeMillis()
                ) ?: BrandKitEntity(
                    brandName = name,
                    tagline = tagline,
                    primaryColorHex = primaryColor,
                    secondaryColorHex = secondaryColor,
                    voiceTone = tone,
                    targetAudience = audience,
                    defaultHashtags = hashtags
                )
            )
        }
    }

    fun generateAiVideo(
        prompt: String,
        targetDurationSeconds: Int = 30, // Minimum 30s
        aspectRatio: String = "9:16 Vertical (Reels / TikTok / Shorts)",
        style: String = "Cinematic 4K Ultra-Realism",
        cameraMotion: String = "Dynamic Pan & Dolly Tracking",
        engine: String = "Veo 2 (Google DeepMind)",
        musicTrack: String = "Upbeat Electronic Lo-Fi",
        voiceoverVoice: String = "Kore (High-Energy Pro)",
        useBrandKit: Boolean = true
    ) {
        val finalDuration = targetDurationSeconds.coerceAtLeast(30)
        _isGeneratingVideo.value = true
        _videoGenProgress.value = 0.1f
        _videoGenStage.value = "1/4: Storyboarding continuous ${finalDuration}s multi-shot timeline with Gemini..."

        viewModelScope.launch {
            try {
                val brandKit = if (useBrandKit) dao.getActiveBrandKit().firstOrNull() else null
                
                delay(800)
                _videoGenProgress.value = 0.35f
                _videoGenStage.value = "2/4: Generative video diffusion & 60fps inbetweening ($engine)..."

                val video = GeminiRepository.generatePromptToAiVideo(
                    userPrompt = prompt,
                    targetDurationSeconds = finalDuration,
                    aspectRatio = aspectRatio,
                    style = style,
                    cameraMotion = cameraMotion,
                    engine = engine,
                    musicTrack = musicTrack,
                    voiceoverVoice = voiceoverVoice,
                    brandKit = brandKit
                )

                delay(900)
                _videoGenProgress.value = 0.7f
                _videoGenStage.value = "3/4: Multiplexing voiceover audio & dynamic subtitles..."

                delay(700)
                _videoGenProgress.value = 0.95f
                _videoGenStage.value = "4/4: Color grading & 1080p final render compilation..."

                delay(500)
                _videoGenProgress.value = 1.0f
                _videoGenStage.value = "✅ Video Generation Complete (${finalDuration}s)!"

                _currentGeneratedVideo.value = video

                // Add to saved videos list in memory
                val currentSaved = _savedVideosList.value.toMutableList()
                currentSaved.add(0, video)
                _savedVideosList.value = currentSaved

                // Also persist to Room DB
                val sceneSummary = video.scenes.joinToString("\n") { "Shot ${it.sceneNumber} (${it.durationSeconds}s) [${it.cameraAngle}]: ${it.onScreenSubtitle} - ${it.voiceoverScript}" }
                dao.insertSavedContent(
                    SavedContentEntity(
                        title = video.title,
                        contentType = "AI_VIDEO_30S",
                        targetPlatform = if (aspectRatio.contains("9:16")) "TikTok / Reels" else "YouTube",
                        topicOrUrl = prompt,
                        hookText = video.scenes.firstOrNull()?.onScreenSubtitle ?: "AI Video",
                        mainBodyText = "Style: $style • Engine: $engine • Duration: ${video.durationSeconds}s\n\n$prompt",
                        ctaText = video.scenes.lastOrNull()?.voiceoverScript ?: "Link in bio",
                        hashtags = video.tags.joinToString(" "),
                        scriptJson = sceneSummary
                    )
                )
            } catch (e: Exception) {
                _videoGenStage.value = "Error generating video: ${e.message}"
            } finally {
                _isGeneratingVideo.value = false
            }
        }
    }

    fun selectGeneratedVideo(video: AiGeneratedVideo) {
        _currentGeneratedVideo.value = video
    }

    fun deleteAiVideo(id: String) {
        val updated = _savedVideosList.value.filter { it.id != id }
        _savedVideosList.value = updated
        if (_currentGeneratedVideo.value?.id == id) {
            _currentGeneratedVideo.value = updated.firstOrNull()
        }
    }

    fun setPlanTier(tier: String) {
        _userPlanTier.value = tier
    }

    // Collaborative Workspace & Team Features
    val workspaces: StateFlow<List<Workspace>> = WorkspaceRepository.workspaces
    val currentWorkspaceId: StateFlow<String> = WorkspaceRepository.currentWorkspaceId
    val workspaceMembers: StateFlow<List<WorkspaceMember>> = WorkspaceRepository.members
    val workspaceProjects: StateFlow<List<SharedProject>> = WorkspaceRepository.projects

    private val _activeCoEditProjectId = MutableStateFlow<String?>(null)
    val activeCoEditProjectId: StateFlow<String?> = _activeCoEditProjectId.asStateFlow()

    fun switchWorkspace(workspaceId: String) {
        WorkspaceRepository.switchWorkspace(workspaceId)
    }

    fun createWorkspace(name: String, description: String, planTier: String = "Enterprise Team"): Workspace {
        return WorkspaceRepository.createWorkspace(name, description, planTier)
    }

    fun inviteTeamMember(name: String, email: String, role: WorkspaceRole, message: String = "") {
        WorkspaceRepository.inviteTeamMember(name, email, role, message)
    }

    fun updateMemberRole(memberId: String, newRole: WorkspaceRole) {
        WorkspaceRepository.updateMemberRole(memberId, newRole)
    }

    fun removeWorkspaceMember(memberId: String) {
        WorkspaceRepository.removeMember(memberId)
    }

    fun resendWorkspaceInvite(memberId: String) {
        WorkspaceRepository.resendInvite(memberId)
    }

    fun createWorkspaceProject(title: String, description: String, platform: String): SharedProject {
        return WorkspaceRepository.createProject(title, description, platform)
    }

    fun updateProjectAccess(projectId: String, accessLevel: ProjectAccessLevel, memberIds: List<String>) {
        WorkspaceRepository.updateProjectAccess(projectId, accessLevel, memberIds)
    }

    fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        WorkspaceRepository.updateProjectStatus(projectId, newStatus)
    }

    fun openCoEditProject(projectId: String) {
        _activeCoEditProjectId.value = projectId
    }

    fun closeCoEditProject() {
        _activeCoEditProjectId.value = null
    }

    fun updateProjectDraft(projectId: String, updatedDraft: SharedContentDraft, summary: String = "Updated copy") {
        WorkspaceRepository.updateProjectDraft(projectId, updatedDraft, changeSummary = summary)
    }

    fun addProjectComment(projectId: String, targetSection: String, message: String) {
        WorkspaceRepository.addComment(
            projectId = projectId,
            authorName = "Stephen (You)",
            authorRole = WorkspaceRole.OWNER,
            targetSection = targetSection,
            message = message
        )
    }

    fun toggleCommentResolved(projectId: String, commentId: String) {
        WorkspaceRepository.toggleCommentResolved(projectId, commentId)
    }

    fun restoreProjectRevision(projectId: String, revisionId: String) {
        WorkspaceRepository.restoreRevision(projectId, revisionId)
    }

    fun deleteWorkspaceProject(projectId: String) {
        if (_activeCoEditProjectId.value == projectId) {
            _activeCoEditProjectId.value = null
        }
        WorkspaceRepository.deleteProject(projectId)
    }

    fun clearAllData() {
        viewModelScope.launch {
            dao.clearAllSavedContent()
            dao.clearAllScheduledPosts()
        }
    }
}
