package com.example

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admob.AdMobManager
import com.example.data.auth.AuthManager
import com.example.data.firebase.FirebaseManager
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

import java.io.File

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val tag: String
)

class MainActivity : ComponentActivity() {
    private val contentViewModel: ContentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Pre-create WebView HTTP Cache directories to avoid Chromium opendir ENOENT error on first run
        // and sanitize stale/unsigned variations seed files to prevent variations_seed_loader.cc errors
        try {
            val baseCache = File(cacheDir, "WebView/Default/HTTP Cache/Code Cache")
            File(baseCache, "js").mkdirs()
            File(baseCache, "wasm").mkdirs()

            val webViewDirs = listOf(
                File(dataDir, "app_webview"),
                File(dataDir, "app_webview/Default"),
                File(cacheDir, "WebView"),
                File(cacheDir, "WebView/Default")
            )
            val seedNames = listOf(
                "Variations Seed",
                "variations_seed",
                "Variations Stamp",
                "variations_stamp",
                "variations_seed_new",
                "Variations Seed.tmp"
            )
            for (dir in webViewDirs) {
                if (dir.exists()) {
                    for (name in seedNames) {
                        val file = File(dir, name)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        // Global uncaught exception safety wrapper to prevent unexpected background thread crashes
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("MainActivity", "Uncaught exception on thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // Initialize Authentication, Firebase, and Google Play Billing SDKs
        AuthManager.init(this)
        FirebaseManager.initialize(this)
        com.example.data.billing.PlayBillingManager.initialize(this)

        // Defer AdMob SDK initialization to after the initial frame renders
        // This prevents Chromium/Mesa graphics driver contention during window surface creation
        window.decorView.post {
            AdMobManager.initialize(this@MainActivity) {
                // Preload interstitial, rewarded, and app open ads after UI rendering has settled
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    com.example.data.admob.AdMobInterstitialHelper.preload(this@MainActivity)
                    AdMobManager.loadRewardedAd(this@MainActivity)
                    AdMobManager.loadAppOpenAd(this@MainActivity)
                }, 1500)
            }
        }

        setContent {
            MyApplicationTheme {
                val currentUser by AuthManager.currentUser.collectAsState()

                AnimatedContent(
                    targetState = currentUser != null,
                    label = "auth_transition"
                ) { isAuthenticated ->
                    if (isAuthenticated) {
                        ContentGenApp(viewModel = contentViewModel)
                    } else {
                        AuthScreen(
                            onAuthenticated = {
                                // Transition handled reactively by currentUser state
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentGenApp(viewModel: ContentViewModel) {
    val context = LocalContext.current
    val currentUser by AuthManager.currentUser.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showUserMenu by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }

    val navItems = remember {
        listOf(
            NavItem("Dashboard", Icons.Outlined.Home, Icons.Filled.Home, "nav_dashboard"),
            NavItem("AI Generator", Icons.Outlined.EditNote, Icons.Filled.EditNote, "nav_generator"),
            NavItem("Video Studio", Icons.Outlined.Videocam, Icons.Filled.Videocam, "nav_video"),
            NavItem("Image Studio", Icons.Outlined.Palette, Icons.Filled.Palette, "nav_image"),
            NavItem("Social Manager", Icons.Outlined.CalendarToday, Icons.Filled.CalendarToday, "nav_social"),
            NavItem("Analytics", Icons.Outlined.BarChart, Icons.Filled.BarChart, "nav_analytics"),
            NavItem("Workspaces", Icons.Outlined.BusinessCenter, Icons.Filled.BusinessCenter, "nav_business"),
            NavItem("Affiliate Suite", Icons.Outlined.ShoppingBag, Icons.Filled.ShoppingBag, "nav_monetization"),
            NavItem("AI Copilot", Icons.Outlined.SmartToy, Icons.Filled.SmartToy, "nav_copilot"),
            NavItem("Settings", Icons.Outlined.Settings, Icons.Filled.Settings, "nav_settings")
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Infinity Content AI",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = navItems[selectedTab].title,
                                fontSize = 11.sp,
                                color = IndigoPrimaryLight
                            )
                        }
                    }
                },
                actions = {
                    // AI Credits Badge
                    currentUser?.let { user ->
                        Surface(
                            color = AmberWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .clickable {
                                    showCreditsDialog = true
                                }
                                .testTag("top_app_bar_credits_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(13.dp))
                                Text(
                                    text = "${user.aiCredits}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberWarning
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { selectedTab = 8 }, // AI Copilot
                        modifier = Modifier.testTag("top_app_bar_copilot_btn")
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = "AI Copilot", tint = CyanAccentLight)
                    }

                    // User Profile Avatar / Dropdown Button
                    Box {
                        IconButton(
                            onClick = { showUserMenu = !showUserMenu },
                            modifier = Modifier.testTag("top_app_bar_user_profile_btn")
                        ) {
                            Surface(
                                color = if (currentUser?.authProvider == "Google") Color(0xFF4285F4) else IndigoPrimary,
                                shape = CircleShape,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = (currentUser?.name?.take(1) ?: "U").uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(currentUser?.name ?: "User", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(currentUser?.email ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Via ${currentUser?.authProvider ?: "Email"} • ${currentUser?.planTier ?: "Pro"}", fontSize = 10.sp, color = EmeraldSuccess)
                                    }
                                },
                                onClick = {
                                    showUserMenu = false
                                    selectedTab = 0 // Go to Dashboard
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = IndigoPrimaryLight)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("User Dashboard", fontSize = 13.sp) },
                                onClick = {
                                    showUserMenu = false
                                    selectedTab = 0
                                },
                                leadingIcon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Account Settings", fontSize = 13.sp) },
                                onClick = {
                                    showUserMenu = false
                                    selectedTab = 9
                                },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Log Out", color = RoseError, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    showUserMenu = false
                                    AuthManager.signOut {
                                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null, tint = RoseError) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(navItems) { index, item ->
                        val isSelected = selectedTab == index
                        Surface(
                            color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { selectedTab = index }
                                .testTag(item.tag)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel = viewModel, onNavigateToSection = { selectedTab = it })
                1 -> ContentGeneratorScreen(viewModel = viewModel)
                2 -> VideoStudioScreen(viewModel = viewModel)
                3 -> ImageStudioScreen(viewModel = viewModel)
                4 -> SocialManagerScreen(viewModel = viewModel)
                5 -> AnalyticsScreen(viewModel = viewModel)
                6 -> BusinessToolsScreen(viewModel = viewModel)
                7 -> MonetizationScreen(viewModel = viewModel)
                8 -> AiAssistantScreen(viewModel = viewModel)
                9 -> AdminSettingsScreen(viewModel = viewModel)
            }
        }
    }

    if (showCreditsDialog && currentUser != null) {
        val user = currentUser!!
        val activity = context as? Activity
        AlertDialog(
            onDismissRequest = { showCreditsDialog = false },
            icon = {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(32.dp))
            },
            title = {
                Text("AI Generation Credits", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Current Balance: ${user.aiCredits} Credits",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberWarning
                    )
                    Text(
                        text = "Credits are used for generating viral captions, video scripts, marketing copy, and AI image prompts.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        color = IndigoPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AdsClick, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Powered by Google Mobile Ads SDK. Watch a sponsored video ad anytime to earn +5 free credits!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCreditsDialog = false
                        if (activity != null) {
                            AdMobManager.showRewardedAd(
                                activity = activity,
                                onRewardGranted = { amount, _ ->
                                    val granted = if (amount > 0) amount else 5
                                    AuthManager.addCredits(granted)
                                    Toast.makeText(context, "🎉 You earned +$granted Free AI Credits!", Toast.LENGTH_LONG).show()
                                },
                                onAdClosed = {
                                    Toast.makeText(context, "Rewarded ad closed", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            AuthManager.addCredits(5)
                            Toast.makeText(context, "Added +5 AI Credits!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    modifier = Modifier.testTag("watch_rewarded_ad_dialog_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Watch Ad (+5 Credits)")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreditsDialog = false
                        selectedTab = 7 // Navigate to Affiliate / Monetization Suite
                    }
                ) {
                    Text("Upgrade Plan")
                }
            }
        )
    }
}
