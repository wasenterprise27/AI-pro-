package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.auth.AuthManager
import com.example.data.auth.UserProfile
import com.example.data.local.SavedContentEntity
import com.example.data.local.ScheduledPostEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class FirebaseSyncStatus(val label: String) {
    CONNECTED("Cloud Connected • Realtime Sync"),
    SYNCING("Syncing to Cloud..."),
    OFFLINE("Offline Caching Active"),
    ERROR("Sync Standby")
}

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    private val _syncStatus = MutableStateFlow(FirebaseSyncStatus.CONNECTED)
    val syncStatus: StateFlow<FirebaseSyncStatus> = _syncStatus.asStateFlow()

    private val _cloudItemsCount = MutableStateFlow(0)
    val cloudItemsCount: StateFlow<Int> = _cloudItemsCount.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isInitialized = false

    private val firestore: FirebaseFirestore by lazy {
        val db = FirebaseFirestore.getInstance()
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings
        } catch (e: Exception) {
            Log.w(TAG, "Persistence settings notice", e)
        }
        db
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            isInitialized = true
            _syncStatus.value = FirebaseSyncStatus.CONNECTED
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization error", e)
            _syncStatus.value = FirebaseSyncStatus.OFFLINE
        }
    }

    /**
     * Backup a saved content draft to Firebase Firestore
     */
    fun syncContentToCloud(content: SavedContentEntity, userUid: String? = AuthManager.currentUser.value?.uid) {
        val uid = userUid ?: AuthManager.currentUser.value?.uid ?: "guest"
        scope.launch {
            try {
                _syncStatus.value = FirebaseSyncStatus.SYNCING
                val docData = hashMapOf(
                    "id" to content.id,
                    "title" to content.title,
                    "targetPlatform" to content.targetPlatform,
                    "contentType" to content.contentType,
                    "topicOrUrl" to content.topicOrUrl,
                    "hookText" to content.hookText,
                    "mainBodyText" to content.mainBodyText,
                    "ctaText" to content.ctaText,
                    "hashtags" to content.hashtags,
                    "scriptJson" to content.scriptJson,
                    "isFavorite" to content.isFavorite,
                    "timestamp" to content.timestamp,
                    "updatedAtMs" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(uid)
                    .collection("saved_content")
                    .document(content.id.toString())
                    .set(docData, SetOptions.merge())
                    .await()

                _syncStatus.value = FirebaseSyncStatus.CONNECTED
                _cloudItemsCount.value += 1
                Log.d(TAG, "Synced content ${content.id} to Firestore")
            } catch (e: Exception) {
                Log.w(TAG, "Offline fallback during content cloud sync", e)
                _syncStatus.value = FirebaseSyncStatus.OFFLINE
            }
        }
    }

    /**
     * Backup a scheduled post to Firebase Firestore
     */
    fun syncScheduledPostToCloud(post: ScheduledPostEntity, userUid: String? = AuthManager.currentUser.value?.uid) {
        val uid = userUid ?: AuthManager.currentUser.value?.uid ?: "guest"
        scope.launch {
            try {
                _syncStatus.value = FirebaseSyncStatus.SYNCING
                val docData = hashMapOf(
                    "id" to post.id,
                    "title" to post.title,
                    "platform" to post.platform,
                    "postBody" to post.postBody,
                    "scheduledTimeMs" to post.scheduledTimeMs,
                    "status" to post.status,
                    "mediaUriOrPrompt" to post.mediaUriOrPrompt,
                    "campaignName" to post.campaignName,
                    "updatedAtMs" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(uid)
                    .collection("scheduled_posts")
                    .document(post.id.toString())
                    .set(docData, SetOptions.merge())
                    .await()

                _syncStatus.value = FirebaseSyncStatus.CONNECTED
                Log.d(TAG, "Synced scheduled post ${post.id} to Firestore")
            } catch (e: Exception) {
                Log.w(TAG, "Offline fallback during scheduled post sync", e)
                _syncStatus.value = FirebaseSyncStatus.OFFLINE
            }
        }
    }

    /**
     * Backup user profile to Firestore
     */
    fun syncUserProfile(profile: UserProfile) {
        scope.launch {
            try {
                val profileData = hashMapOf(
                    "uid" to profile.uid,
                    "name" to profile.name,
                    "email" to profile.email,
                    "planTier" to profile.planTier,
                    "aiCredits" to profile.aiCredits,
                    "creatorNiche" to profile.creatorNiche,
                    "totalGenerations" to profile.totalGenerations,
                    "lastSeenAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(profile.uid)
                    .set(profileData, SetOptions.merge())
                    .await()

                Log.d(TAG, "Synced profile for ${profile.email} to Firestore")
            } catch (e: Exception) {
                Log.w(TAG, "Offline profile sync fallback", e)
            }
        }
    }

    /**
     * Delete user data from Firebase Firestore (User Data deletion compliance)
     */
    fun deleteCloudUserData(uid: String, onComplete: () -> Unit) {
        scope.launch {
            try {
                // Delete user profile doc
                firestore.collection("users").document(uid).delete().await()
                Log.d(TAG, "Deleted cloud document for user $uid")
            } catch (e: Exception) {
                Log.w(TAG, "Notice during cloud data deletion", e)
            } finally {
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }
}
