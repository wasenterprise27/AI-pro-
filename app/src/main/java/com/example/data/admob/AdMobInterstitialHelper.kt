package com.example.data.admob

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.example.data.auth.AuthManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AdMobInterstitialHelper
 * Dedicated helper class to handle loading, caching, and showing AdMob interstitial ads.
 * Specifically used to trigger interstitial ad events after generation tasks (e.g., video/script/content/image generation)
 * to monetize free tier users while preserving premium uninterrupted access for Pro/Business tiers.
 */
object AdMobInterstitialHelper {
    private const val TAG = "AdMobInterstitialHelper"

    private var interstitialAd: InterstitialAd? = null
    private var isLoading: Boolean = false

    /**
     * Finds the host Activity from any Context (including ContextWrapper).
     */
    fun findActivity(context: Context): Activity? {
        var currentContext: Context? = context
        while (currentContext != null) {
            if (currentContext is Activity) return currentContext
            if (currentContext is ContextWrapper) {
                currentContext = currentContext.baseContext
            } else {
                break
            }
        }
        return null
    }

    /**
     * Preloads an interstitial ad into memory so it is ready for instant display.
     */
    fun preload(context: Context) {
        val appContext = context.applicationContext

        // Ensure interstitial loading runs on the UI main thread as required by Google Mobile Ads SDK
        if (android.os.Looper.myLooper() != android.os.Looper.getMainLooper()) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                preload(appContext)
            }
            return
        }

        if (interstitialAd != null || isLoading) {
            Log.d(TAG, "Interstitial ad already cached or currently loading.")
            return
        }

        isLoading = true
        val adUnitId = AdMobManager.activeInterstitialId
        val adRequest = AdRequest.Builder().build()

        Log.d(TAG, "Loading AdMob Interstitial Ad with unit ID: $adUnitId")

        try {
            InterstitialAd.load(
                appContext,
                adUnitId,
                adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d(TAG, "Interstitial Ad successfully loaded and cached.")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial Ad dismissed by user.")
                            interstitialAd = null
                            // Automatically reload next interstitial in the background
                            preload(appContext)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Failed to show full screen interstitial: ${adError.message}")
                            interstitialAd = null
                            preload(appContext)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial Ad displayed on screen.")
                            AdMobManager.recordImpression(0.008)
                        }

                        override fun onAdClicked() {
                            Log.d(TAG, "Interstitial Ad clicked.")
                            AdMobManager.recordClick()
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.w(TAG, "Interstitial Ad failed to load: ${loadAdError.message} (code ${loadAdError.code})")
                }
            }
        )
    } catch (e: Exception) {
        interstitialAd = null
        isLoading = false
        Log.w(TAG, "AdMob interstitial load exception handled: ${e.message}")
    }
}

    /**
     * Checks if an ad is currently cached and ready to show.
     */
    fun isAdReady(): Boolean = interstitialAd != null

    /**
     * Shows the interstitial ad if available, otherwise triggers preload.
     * Invokes [onAdClosed] when dismissed or immediately if no ad is ready.
     */
    fun show(activity: Activity, onAdClosed: (() -> Unit)? = null) {
        val currentAd = interstitialAd
        if (currentAd != null) {
            // Intercept dismiss callback to notify caller
            val previousCallback = currentAd.fullScreenContentCallback
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    previousCallback?.onAdDismissedFullScreenContent()
                    interstitialAd = null
                    preload(activity)
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    previousCallback?.onAdFailedToShowFullScreenContent(adError)
                    interstitialAd = null
                    preload(activity)
                    onAdClosed?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    previousCallback?.onAdShowedFullScreenContent()
                }

                override fun onAdClicked() {
                    previousCallback?.onAdClicked()
                }
            }

            Log.d(TAG, "Showing Interstitial Ad on Activity: ${activity.localClassName}")
            currentAd.show(activity)
            interstitialAd = null
        } else {
            Log.d(TAG, "No Interstitial Ad ready to display. Preloading for next opportunity.")
            preload(activity)
            onAdClosed?.invoke()
        }
    }

    /**
     * Checks if current user is on the Free tier.
     * Paid tiers (Pro, Business, Enterprise) bypass monetization ads.
     */
    fun isFreeTierUser(userPlanTier: String? = null): Boolean {
        val effectiveTier = userPlanTier 
            ?: AuthManager.currentUser.value?.planTier 
            ?: "Free"
        return effectiveTier.equals("Free", ignoreCase = true)
    }

    /**
     * Core integration point:
     * Shows an interstitial ad specifically if the user is on the Free tier,
     * monetizing generation completions (script generation, prompt/image creation, content packages).
     *
     * @param context Android context (used to locate Activity or Application context)
     * @param userPlanTier Current plan tier (e.g. from viewModel.userPlanTier)
     * @param onComplete Callback invoked after ad dismiss or immediately if skipped / unavailable
     */
    fun showAdIfFreeTier(
        context: Context,
        userPlanTier: String? = null,
        onComplete: (() -> Unit)? = null
    ) {
        val isFree = isFreeTierUser(userPlanTier)
        if (!isFree) {
            Log.d(TAG, "User is on a paid/premium tier ($userPlanTier). Skipping interstitial ad.")
            onComplete?.invoke()
            return
        }

        val activity = findActivity(context)
        if (activity != null) {
            show(activity, onComplete)
        } else {
            Log.w(TAG, "Could not find host Activity to show interstitial ad.")
            preload(context)
            onComplete?.invoke()
        }
    }
}
