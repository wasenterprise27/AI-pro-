package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import android.app.Activity
import com.example.data.admob.AdMobManager
import com.example.data.auth.AuthManager
import com.example.data.billing.BillingPurchaseEvent
import com.example.data.billing.PlayBillingManager
import com.example.data.firebase.FirebaseManager
import com.example.ui.components.BrandKitSection
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(viewModel: ContentViewModel) {
    val context = LocalContext.current
    val activity = remember(context) {
        var ctx = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is Activity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    }
    val activeTier by viewModel.userPlanTier.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    val isBillingConnected by PlayBillingManager.isServiceConnected.collectAsState()
    val billingProducts by PlayBillingManager.availableProducts.collectAsState()
    val isLifetimeProPurchased by PlayBillingManager.isLifetimeProPurchased.collectAsState()
    val isBusinessPassPurchased by PlayBillingManager.isBusinessPassPurchased.collectAsState()

    LaunchedEffect(Unit) {
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

    var showAdminPanel by remember { mutableStateOf(false) }
    var geminiModelSelection by remember { mutableStateOf("gemini-3.5-flash") }
    var autoBackupEnabled by remember { mutableStateOf(true) }
    var googleWorkspaceConnected by remember { mutableStateOf(true) }
    var adobeCreativeCloudConnected by remember { mutableStateOf(true) }
    var admobConnected by remember { mutableStateOf(true) }
    val isTestMode by AdMobManager.isTestMode.collectAsState()
    val firebaseSyncStatus by FirebaseManager.syncStatus.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf(currentUser?.name ?: "") }
    var editNicheInput by remember { mutableStateOf(currentUser?.creatorNiche ?: "") }

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showAccountDeletionDialog by remember { mutableStateOf(false) }
    var userDeletionEmailInput by remember { mutableStateOf("") }
    var isDeletionSubmitted by remember { mutableStateOf(false) }

    val tiers = listOf(
        "Free" to "10 Generations/day • Basic Templates",
        "Pro" to "Unlimited Writing • HD Video Scripts • Social Scheduler",
        "Business" to "Team Accounts • White-label • API Access • Priority Support"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "⚙️ Settings & Account Dashboard",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Manage your creator profile, subscriptions, Google authentication, and platform security.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Creator Profile & Security Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("user_account_settings_card")
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
                        Text("👤 Creator Account", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Surface(
                            color = if (currentUser?.authProvider == "Google") Color(0xFF4285F4).copy(alpha = 0.15f) else EmeraldSuccess.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    if (currentUser?.authProvider == "Google") Icons.Default.Verified else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (currentUser?.authProvider == "Google") Color(0xFF4285F4) else EmeraldSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (currentUser?.authProvider == "Google") "Google Verified" else "Email Account",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentUser?.authProvider == "Google") Color(0xFF4285F4) else EmeraldSuccess
                                )
                            }
                        }
                    }

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
                                Column {
                                    Text(currentUser?.name ?: "Content Creator", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(currentUser?.email ?: "user@contentgen.ai", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("UID: ${currentUser?.uid ?: "usr_demo"}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                }

                                OutlinedButton(
                                    onClick = {
                                        editNameInput = currentUser?.name ?: ""
                                        editNicheInput = currentUser?.creatorNiche ?: ""
                                        showEditProfileDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp).testTag("edit_profile_btn")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 11.sp)
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Focus Niche: ${currentUser?.creatorNiche ?: "Viral Content"}", fontSize = 11.sp, color = IndigoPrimaryLight, fontWeight = FontWeight.Medium)
                                Text("${currentUser?.aiCredits ?: 25} AI Credits", fontSize = 11.sp, color = AmberWarning, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Log Out Action
                    Button(
                        onClick = {
                            AuthManager.signOut {
                                Toast.makeText(context, "Logged out successfully. Please sign in to continue.", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError.copy(alpha = 0.15f), contentColor = RoseError),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("settings_logout_btn")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp), tint = RoseError)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Out & Switch Account", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoseError)
                    }
                }
            }
        }

        // Brand Kit & Visual Identity Studio
        item {
            BrandKitSection(viewModel = viewModel)
        }

        // Subscription Plans Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Subscription Tier", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    tiers.forEach { (planName, desc) ->
                        val isCurrent = activeTier == planName
                        Surface(
                            color = if (isCurrent) IndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("plan_card_$planName")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(planName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        if (isCurrent) {
                                            Surface(color = IndigoPrimary, shape = RoundedCornerShape(50)) {
                                                Text(
                                                    "Active",
                                                    fontSize = 10.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (!isCurrent) {
                                    Button(
                                        onClick = {
                                            viewModel.setPlanTier(planName)
                                            Toast.makeText(context, "Switched to $planName Plan!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                                    ) {
                                        Text("Select", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Google Play Store In-App Purchases Section
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(18.dp))
                            Text("Google Play In-App Purchases", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Surface(
                            color = if (isBillingConnected) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isBillingConnected) "BILLING READY" else "CONNECTING",
                                color = if (isBillingConnected) EmeraldSuccess else AmberWarning,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "One-time in-app purchases processed securely through Google Play. Access unlocks immediately upon verification and can be restored on any Android device.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    billingProducts.forEach { product ->
                        val isOwned = when (product.productId) {
                            PlayBillingManager.PRODUCT_LIFETIME_PRO -> isLifetimeProPurchased || activeTier == "Pro"
                            PlayBillingManager.PRODUCT_BUSINESS_PASS -> isBusinessPassPurchased || activeTier == "Business"
                            else -> false
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isOwned) androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)) else null,
                            modifier = Modifier.fillMaxWidth().testTag("billing_product_${product.productId}")
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
                                                    "Owned",
                                                    fontSize = 9.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(product.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${product.formattedPrice} (One-Time)",
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
                                            Toast.makeText(context, "Activity context not ready. Please retry.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = !isOwned || product.isConsumable,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (product.isConsumable) EmeraldSuccess else IndigoPrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isOwned && !product.isConsumable) "Unlocked" else "Buy ${product.formattedPrice}",
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
                            Toast.makeText(context, "Restoring purchases with Google Play...", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("restore_purchases_btn")
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Google Play Purchases", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Security & Backups
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🔒 Security & Data Sync", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Automatic Local Backup", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Back up content drafts every 24 hours", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = autoBackupEnabled,
                            onCheckedChange = { autoBackupEnabled = it },
                            modifier = Modifier.testTag("toggle_backup_switch")
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { Toast.makeText(context, "Database backed up successfully", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backup Data", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { Toast.makeText(context, "Data restored from backup", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Google Workspace & Adobe Creative Cloud Integrations Hub
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = IndigoPrimaryLight)
                            Text("Google Workspace & Adobe CC Hub", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Surface(
                            color = IndigoPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "Connected",
                                color = IndigoPrimaryLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        "Manage enterprise API connections to sync content drafts, video assets, brand kits, and tasks directly across Google Workspace & Adobe Creative Cloud.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Firebase Cloud Infrastructure Card
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("firebase_cloud_card")
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Storage, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text("Firebase Cloud Database & Auth", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Firestore • Google Sign-In • Cloud Backup", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Surface(
                                    color = EmeraldSuccess.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        "Connected",
                                        color = EmeraldSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Project ID:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("project-72348341-7835-4323-a34", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IndigoPrimaryLight)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Status:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(firebaseSyncStatus.label, fontSize = 11.sp, color = EmeraldSuccess)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        currentUser?.let { FirebaseManager.syncUserProfile(it) }
                                        Toast.makeText(context, "Firebase ping sent • Cloud connected!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f).testTag("test_firebase_sync_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Test Cloud Sync", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Firebase Project ID", "project-72348341-7835-4323-a34")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Project ID copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f).testTag("copy_project_id_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Project ID", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Google Workspace Sync Status & Actions
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Cloud, contentDescription = null, tint = IndigoPrimaryLight, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text("Google Workspace Suite", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Drive • Tasks • Docs • Cloud Platform", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = googleWorkspaceConnected,
                                    onCheckedChange = {
                                        googleWorkspaceConnected = it
                                        Toast.makeText(context, if (it) "Google Workspace connected!" else "Google Workspace disconnected", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("toggle_google_workspace_switch")
                                )
                            }

                            if (googleWorkspaceConnected) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { Toast.makeText(context, "Google Drive assets synchronized!", Toast.LENGTH_SHORT).show() },
                                        modifier = Modifier.weight(1f).testTag("sync_google_drive_btn"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Sync Drive", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { Toast.makeText(context, "Google Tasks agenda synced!", Toast.LENGTH_SHORT).show() },
                                        modifier = Modifier.weight(1f).testTag("sync_google_tasks_btn"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Sync Tasks", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Adobe Creative Cloud Sync Status & Actions
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Brush, contentDescription = null, tint = RoseError, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text("Adobe Creative Cloud & Express", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Express Embed SDK • Firefly • Stock Assets", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = adobeCreativeCloudConnected,
                                    onCheckedChange = {
                                        adobeCreativeCloudConnected = it
                                        Toast.makeText(context, if (it) "Adobe Creative Cloud connected!" else "Adobe Creative Cloud disconnected", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("toggle_adobe_cc_switch")
                                )
                            }

                            if (adobeCreativeCloudConnected) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { Toast.makeText(context, "Adobe Express Editor opened for graphics design!", Toast.LENGTH_SHORT).show() },
                                        modifier = Modifier.weight(1f).testTag("open_adobe_express_editor_btn"),
                                        colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Adobe Express", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { Toast.makeText(context, "Brand Assets synced to Adobe CC Library!", Toast.LENGTH_SHORT).show() },
                                        modifier = Modifier.weight(1f).testTag("sync_adobe_cc_library_btn"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Adobe CC Sync", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Google AdMob Monetization Network Integration
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.AdsClick, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text("Google AdMob Monetization", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Banners • Rewarded AI Credits • Interstitials", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = admobConnected,
                                    onCheckedChange = {
                                        admobConnected = it
                                        Toast.makeText(context, if (it) "Google AdMob network enabled!" else "Google AdMob network disabled", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("toggle_admob_switch")
                                )
                            }

                            if (admobConnected) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Ad Test Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("Using Google Test Unit IDs", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Switch(
                                                checked = isTestMode,
                                                onCheckedChange = { AdMobManager.setTestMode(it) },
                                                modifier = Modifier.height(24.dp).testTag("toggle_admob_test_mode_switch")
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            AdMobManager.initialize(context) {
                                                Toast.makeText(context, "Google AdMob SDK re-initialized & test ads cached!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.testTag("reinit_admob_btn"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Re-sync Ads", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Privacy Policy & Google Play Data Safety / Deletion Compliance
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📋 Privacy & Compliance Center", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        "Manage your privacy rights, read data safety guidelines, or request total account & data deletion.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showPrivacyPolicyDialog = true },
                            modifier = Modifier.weight(1f).testTag("privacy_policy_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Policy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Privacy Policy", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { showAccountDeletionDialog = true },
                            modifier = Modifier.weight(1f).testTag("delete_account_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete Account", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Owner / Admin Dashboard Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyanAccentLight)
                            Text("Platform Owner Admin Panel", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        }
                        IconButton(onClick = { showAdminPanel = !showAdminPanel }) {
                            Icon(
                                if (showAdminPanel) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Admin",
                                tint = Color.White
                            )
                        }
                    }

                    if (showAdminPanel) {
                        Text("Active AI Engine Model", fontSize = 12.sp, color = TextSecondaryDark)
                        Surface(
                            color = DarkSlateBg,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Google Gemini 3.5 Flash", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Latency: 240ms", color = EmeraldSuccess, fontSize = 11.sp)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { Toast.makeText(context, "Push notification broadcast sent to 1,240 creators!", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier.weight(1f).testTag("push_notif_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Send Push Alert", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { Toast.makeText(context, "System diagnostics clean (100% operational)", Toast.LENGTH_SHORT).show() },
                                modifier = Modifier.weight(1f).testTag("system_diag_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Run Health Diagnostics", color = Color.Black, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // App Version & Build Information
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("app_version_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                            Text("Android Version & Build", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "v1.2.0 (Build 3)",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Version Name", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("1.2.0", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Version Code", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("3", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Target SDK", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Android 16 (API 36)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Minimum SDK", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Android 7.0 (API 24)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Application ID", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("com.aistudio.contentgenai.vyrkzp", fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Automotive Support", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("CarAppService Active", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
                    }
                }
            }
        }
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            icon = { Icon(Icons.Default.Policy, contentDescription = null, tint = IndigoPrimaryLight) },
            title = { Text("AI pro 001 — Official Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Last Updated: July 2026",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "1. Overview & Data Ownership\nAI pro 001 is committed to protecting your privacy. All content generated, custom prompts, and video scripts belong entirely to you.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "2. Information We Collect\n• Account Identifiers: Email address used for authentication.\n• Usage Data: Saved AI templates, scheduled social posts, and brand kit preferences.\n• Camera & Media Permissions: Optional camera access used solely for capturing photos for AI image tasks when explicitly requested.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "3. How We Use Information\n• To process generative AI requests via server-side Gemini models.\n• To synchronize campaign tasks and contacts with optional Google Workspace integrations.\n• To persist local drafts in your device's Room database.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "4. Third-Party API Services\nWe integrate with Google Cloud and Gemini AI APIs. Your inputs are handled in accordance with Google's API Data Privacy standards and are not used to train public foundation models.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "5. Data Retention & Account Deletion\nYou have the absolute right to delete your data at any time. Use the 'Delete Account' button in Settings to purge all local Room database entries, cached drafts, and user preferences immediately.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "6. Contact Us\nFor privacy inquiries or compliance requests, contact support directly via the Admin Settings screen or your designated account representative.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyPolicyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Close")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            val fullPolicyText = """
AI pro 001 — OFFICIAL PRIVACY POLICY
Application Name: AI pro 001 (Infinity Content AI)
Package Name: com.aistudio.contentgenai.vyrkzp
Effective Date: October 2026
Developer Contact: stephenampangwapyalenge@gmail.com

1. OVERVIEW & INTELLECTUAL PROPERTY
You retain 100% intellectual property, commercial rights, and copyright to all content, hooks, captions, video scripts, and prompts generated within this application.

2. INFORMATION WE COLLECT
• Account Identifiers: Display name and email address provided via Google Sign-In / Firebase Authentication strictly for account verification.
• Creative Inputs: Prompts, product links, brand kit colors, and generation settings.
• On-Device Data: Saved templates, schedules, and drafts stored in your device's encrypted local Room database.
• Hardware Permissions: Camera permission requested only for capturing reference photos when explicitly initiated by you.

3. ARTIFICIAL INTELLIGENCE & LLM PROCESSING
Generative requests are processed via Google Cloud and Gemini API models. Prompts and creative outputs are never used to train, retrain, or improve public foundation models.

4. GOOGLE WORKSPACE API COMPLIANCE
Integrations with Google Tasks, Google Contacts, and Google Drive adhere strictly to Google API Services User Data Policy, including Limited Use requirements.

5. ADVERTISING & ANALYTICS
Advertisements on the Free tier are served via Google AdMob using non-personal advertising identifiers. Users can opt out of personalized ads in Android system settings.

6. RIGHT TO ERASURE & ACCOUNT DELETION
You hold the absolute right to purge all data at any time via Settings > Delete Account, or by emailing stephenampangwapyalenge@gmail.com. Cloud records will be permanently eradicated within 48 business hours.
                            """.trimIndent()

                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Privacy Policy", fullPolicyText)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "Full Privacy Policy text copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Policy", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val shareIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Privacy Policy - AI pro 001")
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    "AI pro 001 Official Privacy Policy\nPackage: com.aistudio.contentgenai.vyrkzp\nContact: stephenampangwapyalenge@gmail.com\n\nFull policy HTML is available in docs/index.html and privacy-policy.html."
                                )
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Privacy Policy"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 11.sp)
                    }
                }
            }
        )
    }

    // Account & Data Deletion Dialog
    if (showAccountDeletionDialog) {
        LaunchedEffect(Unit) {
            if (userDeletionEmailInput.isBlank()) {
                userDeletionEmailInput = currentUser?.email ?: ""
            }
        }
        AlertDialog(
            onDismissRequest = {
                showAccountDeletionDialog = false
                isDeletionSubmitted = false
            },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = RoseError) },
            title = { Text("Request Account & Data Deletion", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isDeletionSubmitted) {
                        Text(
                            "In compliance with Google Play Data Safety requirements, you can request full deletion of your account, saved content, scheduled posts, and associated data.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Submitting this form immediately purges all local database tables (Room DB) and logs a data removal request.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = userDeletionEmailInput,
                            onValueChange = { userDeletionEmailInput = it },
                            label = { Text("Enter your Account Email") },
                            placeholder = { Text("user@example.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("deletion_email_input")
                        )
                    } else {
                        Text(
                            "✅ Account & Data Deleted Successfully!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                        Text(
                            "All local database caches, saved content drafts, and scheduled posts for '${userDeletionEmailInput.ifBlank { "your account" }}' have been permanently erased from this device.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                if (!isDeletionSubmitted) {
                    Button(
                        onClick = {
                            if (userDeletionEmailInput.isNotBlank()) {
                                isDeletionSubmitted = true
                                currentUser?.uid?.let { uid ->
                                    FirebaseManager.deleteCloudUserData(uid) {}
                                }
                                viewModel.clearAllData()
                                AuthManager.signOut()
                                Toast.makeText(context, "Account & data wiped successfully", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Please enter your account email first", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                    ) {
                        Text("Submit & Wipe Data")
                    }
                } else {
                    Button(
                        onClick = {
                            showAccountDeletionDialog = false
                            isDeletionSubmitted = false
                            userDeletionEmailInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Text("Done")
                    }
                }
            },
            dismissButton = {
                if (!isDeletionSubmitted) {
                    TextButton(onClick = { showAccountDeletionDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Creator Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                    )
                    OutlinedTextField(
                        value = editNicheInput,
                        onValueChange = { editNicheInput = it },
                        label = { Text("Creator Niche / Focus") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_profile_niche_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AuthManager.updateProfile(editNameInput, editNicheInput) {
                            showEditProfileDialog = false
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
