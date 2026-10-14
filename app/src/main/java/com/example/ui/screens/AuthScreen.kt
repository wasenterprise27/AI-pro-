package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthManager
import com.example.data.auth.AuthState
import com.example.ui.theme.*

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val authState by AuthManager.authState.collectAsState()

    var isSignUpMode by remember { mutableStateOf(true) } // true: Sign Up, false: Log In
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var selectedNiche by remember { mutableStateOf("Viral TikTok & Reels") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val creatorNiches = remember {
        listOf(
            "Viral TikTok & Reels",
            "YouTube Creator",
            "Affiliate Marketer",
            "E-Commerce & Brands",
            "Podcast & Agency",
            "Digital Products"
        )
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onAuthenticated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlateBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Logo & Branding
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(IndigoPrimary, PurpleAccent, CyanAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "ContentGen AI",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "Infinity Content AI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "The AI-Powered Viral Content Studio for TikTok, YouTube, Instagram & Facebook",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Auth Mode Toggle (Sign Up vs Log In)
            item {
                Surface(
                    color = DarkSlateSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().testTag("auth_mode_selector")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = if (isSignUpMode) IndigoPrimary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isSignUpMode = true
                                    errorMessage = null
                                }
                                .testTag("select_signup_tab_btn")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sign Up (Free)",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSignUpMode) Color.White else TextSecondaryDark
                                )
                            }
                        }

                        Surface(
                            color = if (!isSignUpMode) IndigoPrimary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isSignUpMode = false
                                    errorMessage = null
                                }
                                .testTag("select_login_tab_btn")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Log In",
                                    fontSize = 13.sp,
                                    fontWeight = if (!isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (!isSignUpMode) Color.White else TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }

            // Google Authentication Section (Primary 1-Tap Option)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                val currentActivity = activity ?: context.findActivity()
                                if (currentActivity != null) {
                                    isLoading = true
                                    errorMessage = null
                                    AuthManager.signInWithGoogle(
                                        activity = currentActivity,
                                        onSuccess = {
                                            isLoading = false
                                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                                try {
                                                    Toast.makeText(context.applicationContext, "🎉 Welcome, ${it.name}! (+100 Google Bonus Credits)", Toast.LENGTH_LONG).show()
                                                } catch (_: Exception) {}
                                            }
                                            onAuthenticated()
                                        },
                                        onError = { error ->
                                            isLoading = false
                                            errorMessage = error
                                        }
                                    )
                                } else {
                                    errorMessage = "Activity unavailable for Google Sign-In"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("google_auth_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    color = Color(0xFF4285F4),
                                    shape = CircleShape,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("G", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isSignUpMode) "Sign Up with Google" else "Continue with Google",
                                    color = Color(0xFF1F1F1F),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Instant access + 100 Free Pro AI Credits",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }

            // Divider "OR WITH EMAIL"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Text(
                        text = if (isSignUpMode) "OR REGISTER WITH EMAIL" else "OR LOG IN WITH EMAIL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryDark
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
            }

            // Error Message Banner if any
            if (errorMessage != null) {
                item {
                    Surface(
                        color = RoseError.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, RoseError.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = RoseError, modifier = Modifier.size(18.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = RoseError,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Sign Up or Log In Form Inputs
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Full Name (Only in Sign Up Mode)
                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name / Creator Name") },
                                placeholder = { Text("e.g. Stephen W.") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("auth_fullname_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("creator@gmail.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            placeholder = { Text("••••••••") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Done),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Creator Niche Selection (Sign Up only)
                        if (isSignUpMode) {
                            Text(
                                text = "Select Your Primary Content Focus",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(creatorNiches) { niche ->
                                    FilterChip(
                                        selected = selectedNiche == niche,
                                        onClick = { selectedNiche = niche },
                                        label = { Text(niche, fontSize = 11.sp) },
                                        leadingIcon = if (selectedNiche == niche) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null
                                    )
                                }
                            }
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                errorMessage = null
                                isLoading = true
                                if (isSignUpMode) {
                                    AuthManager.signUpWithEmail(
                                        name = fullName,
                                        email = email,
                                        password = password,
                                        creatorNiche = selectedNiche,
                                        onSuccess = { user ->
                                            isLoading = false
                                            Toast.makeText(context.applicationContext, "🎉 Account created! Welcome, ${user.name}!", Toast.LENGTH_SHORT).show()
                                            onAuthenticated()
                                        },
                                        onError = { err ->
                                            isLoading = false
                                            errorMessage = err
                                        }
                                    )
                                } else {
                                    AuthManager.signInWithEmail(
                                        email = email,
                                        password = password,
                                        onSuccess = { user ->
                                            isLoading = false
                                            Toast.makeText(context.applicationContext, "👋 Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                                            onAuthenticated()
                                        },
                                        onError = { err ->
                                            isLoading = false
                                            errorMessage = err
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        if (isSignUpMode) Icons.Default.PersonAdd else Icons.Default.Login,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isSignUpMode) "Create Creator Account" else "Log In to Dashboard",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Demo Creator Access / Guest Mode
            item {
                OutlinedButton(
                    onClick = {
                        isLoading = true
                        AuthManager.signInDemoUser { user ->
                            isLoading = false
                            Toast.makeText(context.applicationContext, "Logged in as Demo Creator: ${user.name}", Toast.LENGTH_SHORT).show()
                            onAuthenticated()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("auth_demo_access_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Instant Demo Creator Access (1-Click Test)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Footer Security & Terms
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(12.dp))
                        Text(
                            text = "256-Bit SSL Encrypted • Powered by Google Cloud & Firebase",
                            fontSize = 10.sp,
                            color = TextSecondaryDark
                        )
                    }
                    Text(
                        text = "By signing up, you agree to our Terms of Service & Privacy Policy.",
                        fontSize = 9.sp,
                        color = TextSecondaryDark.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
