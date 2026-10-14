package com.example.data.admob

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryLight
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

enum class NativeAdStyle {
    CARD,
    COMPACT
}

/**
 * AdMob Native Ad Component for Jetpack Compose.
 * Seamlessly integrates Google AdMob Native Ads into custom feed, tool, and dashboard layouts.
 */
@Composable
fun AdMobNativeAdCard(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.activeNativeId,
    style: NativeAdStyle = NativeAdStyle.CARD
) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadFailed by remember { mutableStateOf(false) }

    val isTestMode by AdMobManager.isTestMode.collectAsState()

    DisposableEffect(nativeAd) {
        onDispose {
            nativeAd?.destroy()
        }
    }

    LaunchedEffect(adUnitId, isTestMode) {
        isLoading = true
        loadFailed = false
        AdMobManager.loadNativeAd(
            context = context,
            adUnitId = adUnitId,
            onLoaded = { ad ->
                nativeAd?.destroy()
                nativeAd = ad
                isLoading = false
                loadFailed = false
            },
            onFailed = { error ->
                isLoading = false
                loadFailed = true
            }
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admob_native_ad_card"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Bar: Attribution, Network Name & Test Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = IndigoPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "AD",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IndigoPrimaryLight,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Icon(
                        Icons.Default.AdsClick,
                        contentDescription = null,
                        tint = IndigoPrimaryLight,
                        modifier = Modifier.size(13.dp)
                    )

                    Text(
                        text = "Google AdMob Native Sponsor",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = EmeraldSuccess.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isTestMode) "TEST AD" else "VERIFIED",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            if (isLoading) {
                NativeAdLoadingSkeleton(style = style)
            } else if (nativeAd != null) {
                // Live Google Mobile Ads NativeAdView
                key(nativeAd) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { ctx ->
                            createNativeAdView(ctx, nativeAd!!, style)
                        },
                        update = { view ->
                            populateNativeAdView(view, nativeAd!!, style)
                        }
                    )
                }
            } else {
                // Graceful fallback for offline / mock testing
                NativeAdFallbackCard(style = style)
            }
        }
    }
}

/**
 * Creates the NativeAdView hierarchy programmatically with dark-theme styling.
 */
private fun createNativeAdView(
    context: Context,
    nativeAd: NativeAd,
    style: NativeAdStyle
): NativeAdView {
    val nativeAdView = NativeAdView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    val rootLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    if (style == NativeAdStyle.CARD) {
        // --- CARD STYLE ---
        // Media View for visual ad assets (only attached when media is present)
        if (nativeAd.mediaContent != null) {
            try {
                val mediaView = MediaView(context).apply {
                    id = View.generateViewId()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(context, 160)
                    ).apply {
                        bottomMargin = dpToPx(context, 8)
                    }
                }
                rootLayout.addView(mediaView)
                nativeAdView.mediaView = mediaView
            } catch (_: Exception) {}
        }

        // Row with Icon, Headline & Advertiser
        val infoRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(context, 6)
            }
        }

        val iconView = ImageView(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(context, 38),
                dpToPx(context, 38)
            ).apply {
                marginEnd = dpToPx(context, 8)
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        infoRow.addView(iconView)
        nativeAdView.iconView = iconView

        val textCol = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val headlineView = TextView(context).apply {
            id = View.generateViewId()
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFFE2E8F0.toInt())
            maxLines = 1
        }
        textCol.addView(headlineView)
        nativeAdView.headlineView = headlineView

        val advertiserView = TextView(context).apply {
            id = View.generateViewId()
            textSize = 11f
            setTextColor(0xFF94A3B8.toInt())
            maxLines = 1
        }
        textCol.addView(advertiserView)
        nativeAdView.advertiserView = advertiserView

        infoRow.addView(textCol)
        rootLayout.addView(infoRow)

        // Body Text View
        val bodyView = TextView(context).apply {
            id = View.generateViewId()
            textSize = 12f
            setTextColor(0xFFCBD5E1.toInt())
            maxLines = 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(context, 8)
            }
        }
        rootLayout.addView(bodyView)
        nativeAdView.bodyView = bodyView

        // CTA Button
        val ctaButton = Button(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(context, 40)
            )
            setBackgroundColor(0xFF6366F1.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
        }
        rootLayout.addView(ctaButton)
        nativeAdView.callToActionView = ctaButton

    } else {
        // --- COMPACT STYLE ---
        val compactRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val iconView = ImageView(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(context, 36),
                dpToPx(context, 36)
            ).apply {
                marginEnd = dpToPx(context, 10)
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        compactRow.addView(iconView)
        nativeAdView.iconView = iconView

        val textCol = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginEnd = dpToPx(context, 8)
            }
        }

        val headlineView = TextView(context).apply {
            id = View.generateViewId()
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFFE2E8F0.toInt())
            maxLines = 1
        }
        textCol.addView(headlineView)
        nativeAdView.headlineView = headlineView

        val bodyView = TextView(context).apply {
            id = View.generateViewId()
            textSize = 11f
            setTextColor(0xFF94A3B8.toInt())
            maxLines = 1
        }
        textCol.addView(bodyView)
        nativeAdView.bodyView = bodyView

        compactRow.addView(textCol)

        val ctaButton = Button(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dpToPx(context, 36)
            )
            setBackgroundColor(0xFF6366F1.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
        }
        compactRow.addView(ctaButton)
        nativeAdView.callToActionView = ctaButton

        rootLayout.addView(compactRow)
    }

    nativeAdView.addView(rootLayout)
    populateNativeAdView(nativeAdView, nativeAd, style)
    return nativeAdView
}

/**
 * Binds NativeAd data to the registered views in NativeAdView.
 */
private fun populateNativeAdView(
    nativeAdView: NativeAdView,
    nativeAd: NativeAd,
    style: NativeAdStyle
) {
    (nativeAdView.headlineView as? TextView)?.text = nativeAd.headline

    val body = nativeAd.body
    (nativeAdView.bodyView as? TextView)?.apply {
        if (!body.isNullOrBlank()) {
            text = body
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }

    val cta = nativeAd.callToAction
    (nativeAdView.callToActionView as? Button)?.apply {
        if (!cta.isNullOrBlank()) {
            text = cta
            visibility = View.VISIBLE
        } else {
            text = "Learn More"
            visibility = View.VISIBLE
        }
    }

    val icon = nativeAd.icon
    (nativeAdView.iconView as? ImageView)?.apply {
        if (icon?.drawable != null) {
            setImageDrawable(icon.drawable)
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }

    val advertiser = nativeAd.advertiser
    (nativeAdView.advertiserView as? TextView)?.apply {
        if (!advertiser.isNullOrBlank()) {
            text = advertiser
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }

    if (style == NativeAdStyle.CARD) {
        val mediaContent = nativeAd.mediaContent
        (nativeAdView.mediaView as? MediaView)?.apply {
            if (mediaContent != null) {
                try {
                    setMediaContent(mediaContent)
                    visibility = View.VISIBLE
                } catch (_: Exception) {
                    visibility = View.GONE
                }
            } else {
                visibility = View.GONE
            }
        }
    }

    nativeAdView.setNativeAd(nativeAd)
}

@Composable
private fun NativeAdLoadingSkeleton(style: NativeAdStyle) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (style == NativeAdStyle.CARD) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = IndigoPrimaryLight,
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface
            ) {}

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(12.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {}
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(10.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {}
            }
        }
    }
}

@Composable
private fun NativeAdFallbackCard(style: NativeAdStyle) {
    if (style == NativeAdStyle.CARD) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                "VidStudio AI Creator Suite",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Partnered viral sound generator & editor",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    AdMobManager.recordClick()
                },
                modifier = Modifier.fillMaxWidth().height(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Try Creator Partner Tool", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = EmeraldSuccess.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    Text(
                        "VidStudio AI Partner",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Trending Audio Generator",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = { AdMobManager.recordClick() },
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text("Visit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun dpToPx(context: Context, dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}
