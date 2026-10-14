package com.example.data.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.theme.TextSecondaryDark
import com.google.android.gms.ads.*
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.io.File
import java.util.UUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdvertisingIdDetails(
    val advertisingId: String,
    val isLimitAdTrackingEnabled: Boolean = false,
    val source: String = "Google Play Services (GAID/AAID)"
)

data class AdMobStats(
    val impressions: Int = 1842,
    val clicks: Int = 126,
    val ctr: Double = 6.84,
    val ecpm: Double = 4.25,
    val estimatedRevenue: Double = 7.83,
    val rewardedCreditsGranted: Int = 38
)

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Official Google AdMob Test Ad Unit IDs
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    const val TEST_APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"

    // Official Google AdMob Snippet Test Device ID
    const val TEST_DEVICE_ID = "33BE2250B43518CCDA7DE426D04EE231"

    // Live AdMob Publisher Account & Production Application ID
    const val PUBLISHER_ID = "pub-2572430667748626"
    const val LIVE_APP_ID = "ca-app-pub-2572430667748626~1973658266"
    const val APP_ADS_TXT_ENTRY = "google.com, pub-2572430667748626, DIRECT, f08c47fec0942fa0"

    private val _publisherId = MutableStateFlow(PUBLISHER_ID)
    val publisherId: StateFlow<String> = _publisherId.asStateFlow()

    private val _appAdsTxtEntry = MutableStateFlow(APP_ADS_TXT_ENTRY)
    val appAdsTxtEntry: StateFlow<String> = _appAdsTxtEntry.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isInterstitialLoaded = MutableStateFlow(false)
    val isInterstitialLoaded: StateFlow<Boolean> = _isInterstitialLoaded.asStateFlow()

    private val _isRewardedLoaded = MutableStateFlow(false)
    val isRewardedLoaded: StateFlow<Boolean> = _isRewardedLoaded.asStateFlow()

    private val _adMobStats = MutableStateFlow(AdMobStats())
    val adMobStats: StateFlow<AdMobStats> = _adMobStats.asStateFlow()

    private val _isTestMode = MutableStateFlow(true)
    val isTestMode: StateFlow<Boolean> = _isTestMode.asStateFlow()

    private val _customAppId = MutableStateFlow<String?>(LIVE_APP_ID)
    private val _customBannerId = MutableStateFlow<String?>(null)
    private val _customInterstitialId = MutableStateFlow<String?>(null)
    private val _customRewardedId = MutableStateFlow<String?>(null)
    private val _customNativeId = MutableStateFlow<String?>(null)

    private val _advertisingIdInfo = MutableStateFlow<AdvertisingIdDetails?>(null)
    val advertisingIdInfo: StateFlow<AdvertisingIdDetails?> = _advertisingIdInfo.asStateFlow()

    val activeAppId: String
        get() {
            val custom = _customAppId.value
            if (!custom.isNullOrBlank()) return custom
            val configId = try { com.example.BuildConfig.ADMOB_APP_ID } catch (_: Exception) { "" }
            return if (!_isTestMode.value && configId.isNotBlank() && configId != "ADMOB_APP_ID") configId else LIVE_APP_ID
        }

    val activeBannerId: String
        get() {
            val custom = _customBannerId.value
            if (!custom.isNullOrBlank()) return custom
            val configId = try { com.example.BuildConfig.ADMOB_BANNER_ID } catch (_: Exception) { "" }
            return if (!_isTestMode.value && configId.isNotBlank() && configId != "ADMOB_BANNER_ID") configId else TEST_BANNER_AD_UNIT_ID
        }

    val activeInterstitialId: String
        get() {
            val custom = _customInterstitialId.value
            if (!custom.isNullOrBlank()) return custom
            val configId = try { com.example.BuildConfig.ADMOB_INTERSTITIAL_ID } catch (_: Exception) { "" }
            return if (!_isTestMode.value && configId.isNotBlank() && configId != "ADMOB_INTERSTITIAL_ID") configId else TEST_INTERSTITIAL_AD_UNIT_ID
        }

    val activeRewardedId: String
        get() {
            val custom = _customRewardedId.value
            if (!custom.isNullOrBlank()) return custom
            val configId = try { com.example.BuildConfig.ADMOB_REWARDED_ID } catch (_: Exception) { "" }
            return if (!_isTestMode.value && configId.isNotBlank() && configId != "ADMOB_REWARDED_ID") configId else TEST_REWARDED_AD_UNIT_ID
        }

    val activeNativeId: String
        get() {
            val custom = _customNativeId.value
            if (!custom.isNullOrBlank()) return custom
            val configId = try { com.example.BuildConfig.ADMOB_NATIVE_ID } catch (_: Exception) { "" }
            return if (!_isTestMode.value && configId.isNotBlank() && configId != "ADMOB_NATIVE_ID") configId else TEST_NATIVE_AD_UNIT_ID
        }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private val _testDeviceIds = MutableStateFlow<List<String>>(listOf(TEST_DEVICE_ID, "TEST_DEVICE_ID", AdRequest.DEVICE_ID_EMULATOR))
    val testDeviceIds: StateFlow<List<String>> = _testDeviceIds.asStateFlow()

    private val _tagForChildDirectedTreatment = MutableStateFlow(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED)
    val tagForChildDirectedTreatment: StateFlow<Int> = _tagForChildDirectedTreatment.asStateFlow()

    private val _tagForUnderAgeOfConsent = MutableStateFlow(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED)
    val tagForUnderAgeOfConsent: StateFlow<Int> = _tagForUnderAgeOfConsent.asStateFlow()

    private val _maxAdContentRating = MutableStateFlow(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
    val maxAdContentRating: StateFlow<String> = _maxAdContentRating.asStateFlow()

    private val _publisherPrivacyPersonalizationDisabled = MutableStateFlow(false)
    val publisherPrivacyPersonalizationDisabled: StateFlow<Boolean> = _publisherPrivacyPersonalizationDisabled.asStateFlow()

    fun setTestDeviceIds(deviceIds: List<String>) {
        val merged = (deviceIds + TEST_DEVICE_ID + AdRequest.DEVICE_ID_EMULATOR).distinct()
        _testDeviceIds.value = merged
        applyRequestConfiguration()
    }

    fun addTestDeviceId(deviceId: String) {
        if (deviceId.isNotBlank() && !_testDeviceIds.value.contains(deviceId.trim())) {
            val updated = _testDeviceIds.value + deviceId.trim()
            _testDeviceIds.value = updated
            applyRequestConfiguration()
        }
    }

    fun setChildDirectedTreatment(isChildDirected: Boolean) {
        _tagForChildDirectedTreatment.value = if (isChildDirected) {
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
        } else {
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
        }
        applyRequestConfiguration()
    }

    fun setUnderAgeOfConsent(isUnderAge: Boolean) {
        _tagForUnderAgeOfConsent.value = if (isUnderAge) {
            RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE
        } else {
            RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE
        }
        applyRequestConfiguration()
    }

    fun setAdContentFiltering(maxRating: String) {
        _maxAdContentRating.value = maxRating
        applyRequestConfiguration()
    }

    fun setPublisherPrivacyTreatment(disabled: Boolean) {
        _publisherPrivacyPersonalizationDisabled.value = disabled
        applyRequestConfiguration()
    }

    fun setAgeRestrictedTreatment(treatmentType: String) {
        when (treatmentType.uppercase()) {
            "CHILD" -> {
                _tagForChildDirectedTreatment.value = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
                _tagForUnderAgeOfConsent.value = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE
            }
            "TEEN" -> {
                _tagForChildDirectedTreatment.value = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
                _tagForUnderAgeOfConsent.value = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE
            }
            else -> {
                _tagForChildDirectedTreatment.value = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED
                _tagForUnderAgeOfConsent.value = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED
            }
        }
        applyRequestConfiguration()
    }

    fun getRequestConfiguration(): RequestConfiguration {
        return MobileAds.getRequestConfiguration()
    }

    fun applyRequestConfiguration() {
        try {
            val testIds = if (_isTestMode.value) {
                _testDeviceIds.value
            } else {
                emptyList()
            }
            val builder = RequestConfiguration.Builder()
                .setTestDeviceIds(testIds)
                .setMaxAdContentRating(_maxAdContentRating.value)
                .setTagForChildDirectedTreatment(_tagForChildDirectedTreatment.value)
                .setTagForUnderAgeOfConsent(_tagForUnderAgeOfConsent.value)
                .setPublisherPrivacyPersonalizationState(
                    if (_publisherPrivacyPersonalizationDisabled.value)
                        RequestConfiguration.PublisherPrivacyPersonalizationState.DISABLED
                    else
                        RequestConfiguration.PublisherPrivacyPersonalizationState.DEFAULT
                )

            val configuration = builder.build()
            MobileAds.setRequestConfiguration(configuration)
            Log.d(TAG, "MobileAds RequestConfiguration applied: testDevices=$testIds, rating=${_maxAdContentRating.value}, privacyDisabled=${_publisherPrivacyPersonalizationDisabled.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Error applying MobileAds RequestConfiguration", e)
        }
    }

    fun initialize(context: Context, onComplete: (() -> Unit)? = null) {
        if (_isInitialized.value) {
            onComplete?.invoke()
            return
        }

        val appContext = context.applicationContext
        // Ensure WebView cache hierarchy exists before Chromium initializes its disk cache index
        try {
            val baseCache = File(appContext.cacheDir, "WebView/Default/HTTP Cache/Code Cache")
            File(baseCache, "js").mkdirs()
            File(baseCache, "wasm").mkdirs()
        } catch (_: Exception) {}

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Configure test devices including "TEST_DEVICE_ID" and emulator
                applyRequestConfiguration()
                fetchOrGenerateAdvertisingId(appContext)

                MobileAds.initialize(appContext) {
                    Log.d(TAG, "Google AdMob SDK Initialized: mode=${if (_isTestMode.value) "TEST" else "PRODUCTION"}")
                    _isInitialized.value = true
                    onComplete?.invoke()
                }
            } catch (e: Exception) {
                Log.e(TAG, "AdMob initialization error", e)
                _isInitialized.value = true
                onComplete?.invoke()
            }
        }
    }

    fun loadInterstitialAd(context: Context) {
        if (_isInterstitialLoaded.value || interstitialAd != null) return
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                activeInterstitialId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        _isInterstitialLoaded.value = true
                        Log.d(TAG, "Interstitial Ad Loaded successfully")

                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                interstitialAd = null
                                _isInterstitialLoaded.value = false
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                interstitialAd = null
                                _isInterstitialLoaded.value = false
                                Log.e(TAG, "Interstitial show failed: ${adError.message}")
                            }

                            override fun onAdImpression() {
                                recordImpression(0.005)
                            }
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        _isInterstitialLoaded.value = false
                        Log.w(TAG, "Interstitial failed to load: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading interstitial ad", e)
            interstitialAd = null
            _isInterstitialLoaded.value = false
        }
    }

    fun showInterstitialAd(activity: Activity, onDismiss: (() -> Unit)? = null) {
        val ad = interstitialAd
        if (ad != null) {
            try {
                ad.show(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Error displaying interstitial ad", e)
            }
            interstitialAd = null
            _isInterstitialLoaded.value = false
            onDismiss?.invoke()
        } else {
            Log.d(TAG, "Interstitial not ready yet, preloading...")
            loadInterstitialAd(activity)
            onDismiss?.invoke()
        }
    }

    fun loadRewardedAd(context: Context) {
        if (_isRewardedLoaded.value || rewardedAd != null) return
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                activeRewardedId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        _isRewardedLoaded.value = true
                        Log.d(TAG, "Rewarded Ad Loaded successfully")

                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                rewardedAd = null
                                _isRewardedLoaded.value = false
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                rewardedAd = null
                                _isRewardedLoaded.value = false
                                Log.e(TAG, "Rewarded ad show failed: ${adError.message}")
                            }

                            override fun onAdImpression() {
                                recordImpression(0.015)
                            }
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        rewardedAd = null
                        _isRewardedLoaded.value = false
                        Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading rewarded ad", e)
            rewardedAd = null
            _isRewardedLoaded.value = false
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardGranted: (rewardAmount: Int, rewardType: String) -> Unit,
        onAdClosed: (() -> Unit)? = null
    ) {
        val ad = rewardedAd
        if (ad != null) {
            try {
                ad.show(activity) { rewardItem ->
                    val amount = if (rewardItem.amount > 0) rewardItem.amount else 5
                    val type = rewardItem.type.ifBlank { "Bonus Credits" }
                    Log.d(TAG, "User earned reward: $amount $type")
                    _adMobStats.value = _adMobStats.value.copy(
                        rewardedCreditsGranted = _adMobStats.value.rewardedCreditsGranted + amount
                    )
                    onRewardGranted(amount, type)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during rewarded ad playback, granting fallback", e)
                onRewardGranted(5, "Bonus Credits")
            }
            rewardedAd = null
            _isRewardedLoaded.value = false
            onAdClosed?.invoke()
        } else {
            Log.d(TAG, "Rewarded ad not cached yet, granting fallback credits and loading")
            onRewardGranted(5, "Bonus Credits")
            onAdClosed?.invoke()
            loadRewardedAd(activity)
        }
    }

    fun recordImpression(revenueDelta: Double) {
        val current = _adMobStats.value
        val newImpressions = current.impressions + 1
        val newRevenue = current.estimatedRevenue + revenueDelta
        _adMobStats.value = current.copy(
            impressions = newImpressions,
            estimatedRevenue = Math.round(newRevenue * 100.0) / 100.0
        )
    }

    fun recordClick() {
        val current = _adMobStats.value
        val newClicks = current.clicks + 1
        val newCtr = if (current.impressions > 0) (newClicks.toDouble() / current.impressions) * 100.0 else 0.0
        _adMobStats.value = current.copy(
            clicks = newClicks,
            ctr = Math.round(newCtr * 100.0) / 100.0
        )
    }

    fun setTestMode(isTest: Boolean) {
        _isTestMode.value = isTest
        applyRequestConfiguration()
    }

    fun updatePublisherInfo(pubIdOrEntry: String?) {
        val trimmed = pubIdOrEntry?.trim()
        if (trimmed.isNullOrBlank()) {
            _publisherId.value = PUBLISHER_ID
            _appAdsTxtEntry.value = APP_ADS_TXT_ENTRY
            return
        }

        if (trimmed.contains("google.com", ignoreCase = true) && trimmed.contains("pub-", ignoreCase = true)) {
            // Full app-ads.txt line provided e.g. "google.com, pub-2572430667748626, DIRECT, f08c47fec0942fa0"
            _appAdsTxtEntry.value = trimmed
            val extractedPub = Regex("pub-\\d+").find(trimmed)?.value
            if (!extractedPub.isNullOrBlank()) {
                _publisherId.value = extractedPub
            }
        } else if (trimmed.contains("~")) {
            // Full AdMob Application ID provided e.g. "ca-app-pub-2572430667748626~1973658266"
            _customAppId.value = trimmed
            val pubMatch = Regex("pub-\\d+").find(trimmed)?.value
            if (!pubMatch.isNullOrBlank()) {
                _publisherId.value = pubMatch
                _appAdsTxtEntry.value = "google.com, $pubMatch, DIRECT, f08c47fec0942fa0"
            }
        } else if (trimmed.startsWith("pub-", ignoreCase = true)) {
            _publisherId.value = trimmed
            _appAdsTxtEntry.value = "google.com, $trimmed, DIRECT, f08c47fec0942fa0"
        } else if (trimmed.startsWith("ca-app-pub-", ignoreCase = true)) {
            val pubMatch = Regex("pub-\\d+").find(trimmed)?.value
            if (!pubMatch.isNullOrBlank()) {
                _publisherId.value = pubMatch
                _appAdsTxtEntry.value = "google.com, $pubMatch, DIRECT, f08c47fec0942fa0"
            }
        }
    }

    fun updateCustomIds(
        appId: String?,
        bannerId: String?,
        interstitialId: String?,
        rewardedId: String?,
        nativeId: String? = null,
        publisherOrAppAdsTxt: String? = null
    ) {
        _customAppId.value = appId?.trim()?.ifBlank { null }
        _customBannerId.value = bannerId?.trim()?.ifBlank { null }
        _customInterstitialId.value = interstitialId?.trim()?.ifBlank { null }
        _customRewardedId.value = rewardedId?.trim()?.ifBlank { null }
        _customNativeId.value = nativeId?.trim()?.ifBlank { null }
        if (!publisherOrAppAdsTxt.isNullOrBlank()) {
            updatePublisherInfo(publisherOrAppAdsTxt)
        }
    }

    fun loadNativeAd(
        context: Context,
        adUnitId: String = activeNativeId,
        onLoaded: (NativeAd) -> Unit,
        onFailed: (LoadAdError) -> Unit = {}
    ) {
        try {
            val adLoader = AdLoader.Builder(context, adUnitId)
                .forNativeAd { nativeAd ->
                    Log.d(TAG, "Native Ad loaded successfully: ${nativeAd.headline}")
                    onLoaded(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "Native Ad failed to load: ${loadAdError.message}")
                        onFailed(loadAdError)
                    }
                    override fun onAdClicked() {
                        recordClick()
                        Log.d(TAG, "Native Ad clicked")
                    }
                    override fun onAdImpression() {
                        recordImpression(0.004)
                        Log.d(TAG, "Native Ad impression recorded")
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build()
                )
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading native ad", e)
        }
    }

    fun fetchOrGenerateAdvertisingId(context: Context, onResult: ((AdvertisingIdDetails) -> Unit)? = null) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            val details = try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(appContext)
                val id = adInfo.id
                if (!id.isNullOrBlank() && id != "00000000-0000-0000-0000-000000000000") {
                    AdvertisingIdDetails(
                        advertisingId = id,
                        isLimitAdTrackingEnabled = adInfo.isLimitAdTrackingEnabled,
                        source = "Google Play Services (GAID/AAID)"
                    )
                } else {
                    generateAppScopedAdvertisingId(appContext, adInfo.isLimitAdTrackingEnabled)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Google AdvertisingIdClient unavailable on current environment: ${e.message}")
                generateAppScopedAdvertisingId(appContext, false)
            }
            _advertisingIdInfo.value = details
            withContext(Dispatchers.Main) {
                onResult?.invoke(details)
            }
        }
    }

    fun resetAppAdvertisingId(context: Context): AdvertisingIdDetails {
        val newId = UUID.randomUUID().toString()
        val prefs = context.applicationContext.getSharedPreferences("admob_app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("app_advertising_id", newId).apply()
        val details = AdvertisingIdDetails(
            advertisingId = newId,
            isLimitAdTrackingEnabled = false,
            source = "App-Generated Resettable Advertising ID"
        )
        _advertisingIdInfo.value = details
        return details
    }

    private fun generateAppScopedAdvertisingId(context: Context, isLimited: Boolean): AdvertisingIdDetails {
        val prefs = context.getSharedPreferences("admob_app_prefs", Context.MODE_PRIVATE)
        var savedId = prefs.getString("app_advertising_id", null)
        if (savedId.isNullOrBlank()) {
            savedId = UUID.randomUUID().toString()
            prefs.edit().putString("app_advertising_id", savedId).apply()
        }
        return AdvertisingIdDetails(
            advertisingId = savedId,
            isLimitAdTrackingEnabled = isLimited,
            source = "App-Scoped Resettable Advertising ID"
        )
    }
}

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.activeBannerId,
    adSize: AdSize = AdSize.BANNER
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Default.AdsClick,
                        contentDescription = "AdMob",
                        tint = IndigoPrimaryLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Google AdMob Sponsor",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = EmeraldSuccess.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "AD TEST UNIT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        AdView(ctx).apply {
                            setAdSize(adSize)
                            setAdUnitId(adUnitId)
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    isAdLoaded = true
                                    AdMobManager.recordImpression(0.002)
                                    Log.d("AdMobBanner", "Banner ad loaded successfully")
                                }

                                override fun onAdClicked() {
                                    AdMobManager.recordClick()
                                    Log.d("AdMobBanner", "Banner ad clicked")
                                }

                                override fun onAdFailedToLoad(adError: LoadAdError) {
                                    Log.w("AdMobBanner", "Banner ad failed to load: ${adError.message}")
                                }
                            }
                            try {
                                loadAd(AdRequest.Builder().build())
                            } catch (e: Exception) {
                                Log.w("AdMobBanner", "Exception requesting banner ad", e)
                            }
                        }
                    },
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}
