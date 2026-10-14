package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(viewModel: ContentViewModel) {
    val chatMessages by viewModel.assistantChat.collectAsState()
    var userPromptText by remember { mutableStateOf("") }

    val promptSuggestions = listOf(
        "Give me 5 viral video hooks for AI tech niche",
        "What are the top SEO keywords for fitness creators?",
        "Analyze competitor strategy for short-form video",
        "How do I write high-converting Instagram Reel captions?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(28.dp))
            Column {
                Text(
                    text = "🧠 AI Copilot & Advisor",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Marketing strategy, SEO keywords, trend discovery, and competitor auditing.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Suggestions Bar
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Quick Prompts", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    promptSuggestions.forEach { suggestion ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "💡 $suggestion",
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .testTag("suggestion_${suggestion.take(10)}")
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Chat Messages Feed
            items(chatMessages) { (sender, msg) ->
                val isUser = sender == "user"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) IndigoPrimary else DarkSlateSurface
                        ),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) "You" else "ContentGen Copilot",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) Color.White.copy(alpha = 0.8f) else AmberWarning
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userPromptText,
                onValueChange = { userPromptText = it },
                placeholder = { Text("Ask Copilot for viral ideas or SEO advice...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("copilot_chat_input"),
                shape = RoundedCornerShape(20.dp)
            )

            IconButton(
                onClick = {
                    if (userPromptText.isNotBlank()) {
                        viewModel.sendAssistantMessage(userPromptText)
                        userPromptText = ""
                    }
                },
                modifier = Modifier
                    .background(IndigoPrimary, shape = RoundedCornerShape(20.dp))
                    .testTag("send_copilot_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}
