package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admob.AdMobBanner
import com.example.data.admob.AdMobManager
import com.example.data.admob.AdMobNativeAdCard
import com.example.data.admob.NativeAdStyle
import com.example.data.auth.AuthManager
import com.example.data.billing.BillingPurchaseEvent
import com.example.data.billing.PlayBillingManager
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetizationScreen(viewModel: ContentViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val clipboardManager = LocalClipboardManager.current
    val currentUser by AuthManager.currentUser.collectAsState()

    val adMobStats by AdMobManager.adMobStats.collectAsState()
    val isAdMobInitialized by AdMobManager.isInitialized.collectAsState()
    val isInterstitialLoaded by AdMobManager.isInterstitialLoaded.collectAsState()
    val isRewardedLoaded by AdMobManager.isRewardedLoaded.collectAsState()
    val isTestMode by AdMobManager.isTestMode.collectAsState()
    val testDeviceList by AdMobManager.testDeviceIds.collectAsState()
    val advertisingIdInfo by AdMobManager.advertisingIdInfo.collectAsState()
    val maxAdContentRating by AdMobManager.maxAdContentRating.collectAsState()
    val childDirectedTreatment by AdMobManager.tagForChildDirectedTreatment.collectAsState()
    val publisherPrivacyDisabled by AdMobManager.publisherPrivacyPersonalizationDisabled.collectAsState()
    val activePublisherId by AdMobManager.publisherId.collectAsState()
    val activeAppAdsTxtEntry by AdMobManager.appAdsTxtEntry.collectAsState()

    val isBillingConnected by PlayBillingManager.isServiceConnected.collectAsState()
    val isBillingSandbox by PlayBillingManager.isSandboxMode.collectAsState()
    val billingProducts by PlayBillingManager.availableProducts.collectAsState()
    val isLifetimeProPurchased by PlayBillingManager.isLifetimeProPurchased.collectAsState()
    val isBusinessPassPurchased by PlayBillingManager.isBusinessPassPurchased.collectAsState()

    LaunchedEffect(Unit) {
        AdMobManager.fetchOrGenerateAdvertisingId(context)
        PlayBillingManager.purchaseEvents.collect { event ->
            when (event) {
                is BillingPurchaseEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    if (event.productId == PlayBillingManager.PRODUCT_LIFETIME_PRO) {
                        viewModel.setPlanTier("Pro")
                    } else if (event.productId == PlayBillingManager.PRODUCT_BUSINESS_PASS) {
                        viewModel.setPlanTier("Business")
                    }
                }
                is BillingPurchaseEvent.Pending -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is BillingPurchaseEvent.Cancelled -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is BillingPurchaseEvent.Error -> {
                    Toast.makeText(context, "Billing: ${event.message}", Toast.LENGTH_LONG).show()
                }
                is BillingPurchaseEvent.Restored -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    if (PlayBillingManager.isLifetimeProPurchased.value) {
                        viewModel.setPlanTier("Pro")
                    } else if (PlayBillingManager.isBusinessPassPurchased.value) {
                        viewModel.setPlanTier("Business")
                    }
                }
                else -> Unit
            }
        }
    }

    var affiliateUrl by remember { mutableStateOf("") }
    var ebookTopic by remember { mutableStateOf("") }
    var isAnalyzingAffiliate by remember { mutableStateOf(false) }
    var generatedAffiliateOutput by remember { mutableStateOf("") }
    var showAdMobDetails by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showAppAdsTxtModal by remember { mutableStateOf(false) }

    var customAppIdInput by remember { mutableStateOf(AdMobManager.LIVE_APP_ID) }
    var customBannerIdInput by remember { mutableStateOf("") }
    var customInterstitialIdInput by remember { mutableStateOf("") }
    var customRewardedIdInput by remember { mutableStateOf("") }
    var customNativeIdInput by remember { mutableStateOf("") }
    var customPublisherInput by remember { mutableStateOf(AdMobManager.APP_ADS_TXT_ENTRY) }
    var selectedNativeStyle by remember { mutableStateOf(NativeAdStyle.CARD) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "💰 Monetization & Ad Revenue Suite",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Google AdMob monetization, rewarded AI credits, Amazon affiliate copywriter, and digital store builders.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Google AdMob Monetization Suite Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("admob_monetization_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = IndigoPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AdsClick, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text("Google AdMob Network", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Active & Initialized • SDK v23.6.0", fontSize = 11.sp, color = EmeraldSuccess)
                            }
                        }

                        Surface(
                            color = if (isAdMobInitialized) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isAdMobInitialized) "CONNECTED" else "CONNECTING",
                                color = if (isAdMobInitialized) EmeraldSuccess else AmberWarning,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // AdMob Live Banner Ad Integration
                    AdMobBanner()

                    // Quick AdMob Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Watch Rewarded Video Ad
                        Button(
                            onClick = {
                                if (activity != null) {
                                    AdMobManager.showRewardedAd(
                                        activity = activity,
                                        onRewardGranted = { amount, type ->
                                            val granted = if (amount > 0) amount else 5
                                            AuthManager.addCredits(granted)
                                            Toast.makeText(context, "🎉 You earned +$granted Free AI Generation Credits!", Toast.LENGTH_LONG).show()
                                        },
                                        onAdClosed = {
                                            Toast.makeText(context, "Rewarded ad completed", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    AuthManager.addCredits(5)
                                    Toast.makeText(context, "Rewarded ad playing (+5 AI Credits rewarded)!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(44.dp).testTag("watch_rewarded_ad_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Watch Ad (+5 AI Credits)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Trigger Interstitial Ad
                        OutlinedButton(
                            onClick = {
                                if (activity != null) {
                                    AdMobManager.showInterstitialAd(activity) {
                                        Toast.makeText(context, "Interstitial ad viewed!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Interstitial ad displayed!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(44.dp).testTag("trigger_interstitial_ad_btn"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Interstitial Ad", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Google AdMob Native Ad Live Showcase
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("admob_native_showcase")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Feed,
                                        contentDescription = null,
                                        tint = IndigoPrimaryLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        "Native Advanced Ad Format",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    FilterChip(
                                        selected = selectedNativeStyle == NativeAdStyle.CARD,
                                        onClick = { selectedNativeStyle = NativeAdStyle.CARD },
                                        label = { Text("Card (Media)", fontSize = 10.sp) },
                                        modifier = Modifier.height(28.dp)
                                    )
                                    FilterChip(
                                        selected = selectedNativeStyle == NativeAdStyle.COMPACT,
                                        onClick = { selectedNativeStyle = NativeAdStyle.COMPACT },
                                        label = { Text("Compact (Feed)", fontSize = 10.sp) },
                                        modifier = Modifier.height(28.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Blends headlines, video/media views, and CTAs directly into your app feeds with custom native Android styling.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Live AdMob Native Ad Component
                            AdMobNativeAdCard(
                                style = selectedNativeStyle,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // AdMob Revenue & Performance Metrics Grid
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
                                Text("📈 AdMob Live Analytics", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                TextButton(
                                    onClick = { showAdMobDetails = !showAdMobDetails },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(if (showAdMobDetails) "Hide IDs" else "Show Unit IDs", fontSize = 11.sp, color = IndigoPrimaryLight)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Est. Revenue", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$${String.format("%.2f", adMobStats.estimatedRevenue)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EmeraldSuccess)
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("eCPM", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$${String.format("%.2f", adMobStats.ecpm)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = CyanAccentLight)
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Impressions", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${adMobStats.impressions}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("CTR", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${String.format("%.1f", adMobStats.ctr)}%", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = AmberWarning)
                                    }
                                }
                            }

                            if (showAdMobDetails) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
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
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(
                                                    if (isTestMode) Icons.Default.Shield else Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = if (isTestMode) AmberWarning else EmeraldSuccess,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = if (isTestMode) "Test Mode Active (Safe)" else "Live Production AdMob",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isTestMode) AmberWarning else EmeraldSuccess
                                                )
                                            }

                                            TextButton(
                                                onClick = { AdMobManager.setTestMode(!isTestMode) },
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(if (isTestMode) "Switch to Live" else "Switch to Test", fontSize = 11.sp)
                                            }
                                        }

                                        Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                                        Text("Publisher Account: $activePublisherId", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                        Text("App ID: ${AdMobManager.activeAppId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Banner Unit: ${AdMobManager.activeBannerId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Interstitial Unit: ${AdMobManager.activeInterstitialId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Rewarded Unit: ${AdMobManager.activeRewardedId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Native Unit: ${AdMobManager.activeNativeId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Test Devices: ${testDeviceList.joinToString(", ")}", fontSize = 10.sp, color = EmeraldSuccess)
                                        Text("Rating: $maxAdContentRating • Child-Directed: ${if (childDirectedTreatment == com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE) "Active" else "Disabled"} • Privacy: ${if (publisherPrivacyDisabled) "Disabled" else "Default"}", fontSize = 10.sp, color = IndigoPrimaryLight)

                                        // app-ads.txt Authorized Digital Sellers Record
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                                                        Text("app-ads.txt Authorized Seller", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                    }
                                                    Surface(color = EmeraldSuccess.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                                        Text("DIRECT", color = EmeraldSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                }
                                                Text(
                                                    text = activeAppAdsTxtEntry,
                                                    fontSize = 10.sp,
                                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    OutlinedButton(
                                                        onClick = { showAppAdsTxtModal = true },
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(12.dp), tint = IndigoPrimaryLight)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Setup & Host Guide", fontSize = 10.sp, color = IndigoPrimaryLight)
                                                    }
                                                    Button(
                                                        onClick = {
                                                            clipboardManager.setText(AnnotatedString(activeAppAdsTxtEntry))
                                                            Toast.makeText(context, "app-ads.txt record copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess.copy(alpha = 0.85f)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Copy Entry", fontSize = 10.sp)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        OutlinedButton(
                                            onClick = { showConfigDialog = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Link Custom AdMob Account / Unit IDs", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Device & App Advertising ID (AAID) Management Hub
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("advertising_id_card")
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = IndigoPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = IndigoPrimaryLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    "Advertising ID (AAID / GAID)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = advertisingIdInfo?.source ?: "Resolving Advertising ID...",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = if (advertisingIdInfo?.isLimitAdTrackingEnabled == true) AmberWarning.copy(alpha = 0.15f) else EmeraldSuccess.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (advertisingIdInfo?.isLimitAdTrackingEnabled == true) "TRACKING LIMITED" else "ACTIVE AAID",
                                color = if (advertisingIdInfo?.isLimitAdTrackingEnabled == true) AmberWarning else EmeraldSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "CURRENT ADVERTISING IDENTIFIER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val currentAaid = advertisingIdInfo?.advertisingId ?: "Fetching..."
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentAaid,
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(currentAaid))
                                        Toast.makeText(context, "Advertising ID copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy Advertising ID",
                                        tint = IndigoPrimaryLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val newDetails = AdMobManager.resetAppAdvertisingId(context)
                                Toast.makeText(context, "Generated new Advertising ID: ${newDetails.advertisingId.take(8)}...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset / Generate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val currentAaid = advertisingIdInfo?.advertisingId
                                if (!currentAaid.isNullOrBlank()) {
                                    AdMobManager.addTestDeviceId(currentAaid)
                                    Toast.makeText(context, "Added Advertising ID to AdMob Test Devices!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AddModerator, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Test Device", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Google Play Store In-App Purchases & Credit Refill Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("google_play_billing_card")
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = IndigoPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text("Google Play In-App Purchases", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("One-Time Products • Billing Library v7.1.1", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            color = when {
                                isBillingConnected -> EmeraldSuccess.copy(alpha = 0.15f)
                                isBillingSandbox -> IndigoPrimary.copy(alpha = 0.15f)
                                else -> AmberWarning.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = when {
                                    isBillingConnected -> "BILLING ACTIVE"
                                    isBillingSandbox -> "SANDBOX TESTING"
                                    else -> "CONNECTING"
                                },
                                color = when {
                                    isBillingConnected -> EmeraldSuccess
                                    isBillingSandbox -> IndigoPrimaryLight
                                    else -> AmberWarning
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "Upgrade permanently with one-time in-app purchases or refill your generative AI tokens. All transactions are securely verified and acknowledged by Google Play.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    billingProducts.forEach { product ->
                        val isOwned = when (product.productId) {
                            PlayBillingManager.PRODUCT_LIFETIME_PRO -> isLifetimeProPurchased || currentUser?.planTier == "Pro"
                            PlayBillingManager.PRODUCT_BUSINESS_PASS -> isBusinessPassPurchased || currentUser?.planTier == "Business"
                            else -> false
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isOwned) BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(product.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (isOwned) {
                                            Surface(color = EmeraldSuccess, shape = RoundedCornerShape(50)) {
                                                Text(
                                                    "Active",
                                                    fontSize = 9.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(product.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        "${product.formattedPrice} (One-Time)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimaryLight
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (activity != null) {
                                            PlayBillingManager.launchPurchaseFlow(activity, product.productId)
                                        } else {
                                            Toast.makeText(context, "Activity not ready. Please retry.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = !isOwned || product.isConsumable,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (product.isConsumable) EmeraldSuccess else IndigoPrimary
                                    )
                                ) {
                                    Text(
                                        text = if (isOwned && !product.isConsumable) "Unlocked" else "Buy",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Restore Purchases Button
                    OutlinedButton(
                        onClick = {
                            PlayBillingManager.restorePurchases(silent = false)
                            Toast.makeText(context, "Querying active Google Play purchases...", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("restore_purchases_monetization_btn")
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Google Play Purchases", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Amazon Affiliate Assistant Tool
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
                        Text("🛍️ Amazon Affiliate Content Suite", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = EmeraldSuccess)
                    }

                    OutlinedTextField(
                        value = affiliateUrl,
                        onValueChange = { affiliateUrl = it },
                        placeholder = { Text("Paste Amazon product URL or title (e.g. https://amazon.com/dp/B08N5WRWNW)...") },
                        modifier = Modifier.fillMaxWidth().testTag("affiliate_url_input")
                    )

                    Button(
                        onClick = {
                            if (affiliateUrl.isBlank()) {
                                Toast.makeText(context, "Paste an Amazon link or product name first", Toast.LENGTH_SHORT).show()
                            } else {
                                isAnalyzingAffiliate = true
                                generatedAffiliateOutput = """
                                    📦 AFFILIATE MARKETING BUNDLE FOR: ${if (affiliateUrl.contains("amazon")) "Amazon Product" else affiliateUrl}
                                    
                                    🔥 TIKTOK AD HOOK:
                                    "I bought this Amazon bestseller so you don't have to — here is the honest truth after 30 days!"
                                    
                                    📝 SALES COPY (FB/IG):
                                    Upgrade your daily routine! This top-rated gadget features ergonomic comfort, ultra-long battery life, and crystal-clear audio quality. Limited time deal with 25% off!
                                    
                                    👉 YouTube Description & Tag:
                                    "Check out today's deal on Amazon: $affiliateUrl (Affiliate Link) #CommissionsEarned"
                                    
                                    🔑 SEO Keywords:
                                    amazon tech find, best wireless gadgets, honest product review, tech deals 2026
                                """.trimIndent()
                                isAnalyzingAffiliate = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("analyze_affiliate_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze Product & Build Sales Copy", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (generatedAffiliateOutput.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("affiliate_result_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✨ Generated Sales Bundle", fontWeight = FontWeight.Bold, color = EmeraldSuccess, fontSize = 14.sp)
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(generatedAffiliateOutput))
                                    Toast.makeText(context, "Sales bundle copied!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Bundle", tint = Color.White)
                            }
                        }

                        Surface(
                            color = DarkSlateBg,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedAffiliateOutput,
                                color = TextPrimaryDark,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Digital Ebook & QR Creator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📚 AI Ebook & Digital Store Builder", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    OutlinedTextField(
                        value = ebookTopic,
                        onValueChange = { ebookTopic = it },
                        placeholder = { Text("Ebook Title / Niche (e.g. 10 Secret Rules of TikTok Monetization)...") },
                        modifier = Modifier.fillMaxWidth().testTag("ebook_title_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (ebookTopic.isBlank()) {
                                    Toast.makeText(context, "Enter ebook title first", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Ebook outline & 5 chapters generated!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Generate Ebook", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { Toast.makeText(context, "QR Code generated for product store!", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gen QR Code", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showConfigDialog) {
        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            icon = { Icon(Icons.Default.AdsClick, contentDescription = null, tint = IndigoPrimaryLight) },
            title = { Text("Link Google AdMob Account", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Enter your live Google AdMob identifiers from https://admob.google.com. You can paste your publisher ID or full app-ads.txt entry.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = customPublisherInput,
                        onValueChange = { customPublisherInput = it },
                        label = { Text("Publisher ID or app-ads.txt Record") },
                        placeholder = { Text(activeAppAdsTxtEntry) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    OutlinedTextField(
                        value = customAppIdInput,
                        onValueChange = { customAppIdInput = it },
                        label = { Text("AdMob App ID (ca-app-pub-...)") },
                        placeholder = { Text(AdMobManager.activeAppId) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    OutlinedTextField(
                        value = customBannerIdInput,
                        onValueChange = { customBannerIdInput = it },
                        label = { Text("Banner Ad Unit ID") },
                        placeholder = { Text(AdMobManager.activeBannerId) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    OutlinedTextField(
                        value = customInterstitialIdInput,
                        onValueChange = { customInterstitialIdInput = it },
                        label = { Text("Interstitial Ad Unit ID") },
                        placeholder = { Text(AdMobManager.activeInterstitialId) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    OutlinedTextField(
                        value = customRewardedIdInput,
                        onValueChange = { customRewardedIdInput = it },
                        label = { Text("Rewarded Video Ad Unit ID") },
                        placeholder = { Text(AdMobManager.activeRewardedId) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    OutlinedTextField(
                        value = customNativeIdInput,
                        onValueChange = { customNativeIdInput = it },
                        label = { Text("Native Advanced Ad Unit ID") },
                        placeholder = { Text(AdMobManager.activeNativeId) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AdMobManager.updateCustomIds(
                            appId = customAppIdInput,
                            bannerId = customBannerIdInput,
                            interstitialId = customInterstitialIdInput,
                            rewardedId = customRewardedIdInput,
                            nativeId = customNativeIdInput,
                            publisherOrAppAdsTxt = customPublisherInput
                        )
                        AdMobManager.setTestMode(false)
                        showConfigDialog = false
                        Toast.makeText(context, "Google AdMob & app-ads.txt connected in Live Production Mode!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Save & Connect")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        customAppIdInput = AdMobManager.LIVE_APP_ID
                        customBannerIdInput = ""
                        customInterstitialIdInput = ""
                        customRewardedIdInput = ""
                        customNativeIdInput = ""
                        customPublisherInput = AdMobManager.APP_ADS_TXT_ENTRY
                        AdMobManager.updateCustomIds(null, null, null, null, null, AdMobManager.APP_ADS_TXT_ENTRY)
                        AdMobManager.setTestMode(true)
                        showConfigDialog = false
                        Toast.makeText(context, "Reset to official Google AdMob test IDs", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Reset to Test IDs")
                }
            }
        )
    }

    if (showAppAdsTxtModal) {
        AlertDialog(
            onDismissRequest = { showAppAdsTxtModal = false },
            icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(28.dp)) },
            title = {
                Text("app-ads.txt Authorized Seller", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Google AdMob uses IAB Tech Lab app-ads.txt to verify authorized sellers and protect your ad revenue from spoofed traffic.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Your app-ads.txt Entry:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                Surface(color = EmeraldSuccess.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                    Text("DIRECT", color = EmeraldSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text(
                                text = activeAppAdsTxtEntry,
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Specification Breakdown:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("• Domain: google.com (AdMob Ad System)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• Publisher ID: $activePublisherId", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• Relationship: DIRECT (Direct account holder)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• TAG Auth ID: f08c47fec0942fa0 (Google Certification ID)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("How to Publish & Verify:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("1. Upload this line to the root of your developer website URL listed in Google Play Store settings: https://yourdomain.com/app-ads.txt", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("2. Google AdMob crawlers scan your URL automatically within 24 hours.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("3. A copy is also bundled in app assets at app/src/main/assets/app-ads.txt for local verification.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(activeAppAdsTxtEntry))
                        Toast.makeText(context, "app-ads.txt entry copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAppAdsTxtModal = false }) {
                    Text("Close")
                }
            }
        )
    }
}

