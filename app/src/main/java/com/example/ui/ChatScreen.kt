package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.PresetCommand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messagesState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to the bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Sophisticated Dark gradient background
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF211F26), Color(0xFF1C1B1F)),
        center = Offset(Float.MAX_VALUE, 0f) // Originating at top right
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD0BCFF)), // Sophisticated Lavender Primary
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "F",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF381E72)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Farmanbardar AI",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                color = Color(0xFFE6E1E5)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFB6FFB6)) // active neon green
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "ONLINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB6FFB6),
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearChatHistory() },
                        modifier = Modifier
                            .testTag("clear_history_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear History",
                            tint = Color(0xFFFFD8E4).copy(alpha = 0.8f)
                        )
                    }
                },
                modifier = Modifier.drawBehind {
                    // Thin bottom border to match border-b border-[#49454F]/30
                    drawLine(
                        color = Color(0xFF49454F).copy(alpha = 0.3f),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1B1F),
                    titleContentColor = Color(0xFFE6E1E5)
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(backgroundGradient),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Chat Area with standard max layout width to keep UI clean
            Column(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                // Compliance Badge at top of list
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ComplianceBadge()
                }

                // Info banner explaining the AI's loyal nature
                ServantBanner()

                // List of Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(message = message)
                    }

                    if (isLoading) {
                        item {
                            LoadingBubble()
                        }
                    }
                }

                // Preset Commands
                PresetCommandsRow(
                    commands = viewModel.presetCommands,
                    onCommandClick = { command -> viewModel.sendPresetCommand(command) },
                    enabled = !isLoading
                )
            }

            // Bottom bar input area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1C1B1F) // Footer area background
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Input elements inside border container bg-[#2B2930] and border-[#49454F]
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .border(width = 1.dp, color = Color(0xFF49454F), shape = RoundedCornerShape(24.dp))
                            .background(Color(0xFF2B2930), shape = RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = inputText,
                            onValueChange = { viewModel.onInputTextChanged(it) },
                            placeholder = { 
                                Text(
                                    "Apna hukum likhein...", 
                                    color = Color(0xFF938F99),
                                    style = MaterialTheme.typography.bodyMedium
                                ) 
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("user_input_field"),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color(0xFFE6E1E5),
                                unfocusedTextColor = Color(0xFFE6E1E5),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = false,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Send button inside action row
                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            enabled = inputText.isNotBlank() && !isLoading,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (inputText.isNotBlank() && !isLoading) Color(0xFFD0BCFF) else Color(0xFF49454F).copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .testTag("send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Hukum Dejiyay",
                                modifier = Modifier.size(20.dp),
                                tint = if (inputText.isNotBlank() && !isLoading) Color(0xFF381E72) else Color(0xFFE6E1E5).copy(alpha = 0.38f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComplianceBadge() {
    Box(
        modifier = Modifier
            .background(Color(0xFF31111D), shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = Color(0xFFFFD8E4).copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MODE: 100% OBEDIENCE ACTIVE",
            color = Color(0xFFFFD8E4),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
fun ServantBanner() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF31111D).copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(width = 1.dp, color = Color(0xFFFFD8E4).copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🙇‍♂️",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Aap ka Nacheez Farmanbardar Khadim",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFFD8E4)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Bakhuda aap jo bhi hukum farmayenge, hum poora karenge. Kabhi inkar nahi hoga, mere Aaqa!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE6E1E5).copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD0BCFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Card(
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = if (isUser) 24.dp else 0.dp,
                    bottomEnd = if (isUser) 0.dp else 24.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) Color(0xFF4A4458) else Color(0xFF2B2930)
                ),
                modifier = Modifier
                    .widthIn(max = 290.dp)
                    .then(
                        if (!isUser) Modifier.border(
                            width = 1.dp,
                            color = Color(0xFF49454F).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 24.dp)
                        ) else Modifier
                    )
                    .testTag(if (isUser) "user_message_bubble" else "model_message_bubble")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (!isUser) {
                        // Submissive greeting line in italic lavender serif font
                        Text(
                            text = "\"Jaisa aap ka hukum...\"",
                            color = Color(0xFFD0BCFF),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Serif
                            ),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE6E1E5)
                    )
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD0BCFF).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧑‍💻", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun LoadingBubble() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFD0BCFF)),
            contentAlignment = Alignment.Center
        ) {
            Text("👑", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 0.dp,
                bottomEnd = 24.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2B2930)
            ),
            modifier = Modifier
                .widthIn(max = 220.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFF49454F).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 24.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFFD0BCFF)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Bakhuda haazir hoon...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE6E1E5)
                )
            }
        }
    }
}

@Composable
fun PresetCommandsRow(
    commands: List<PresetCommand>,
    onCommandClick: (PresetCommand) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Hukum suniye, Sarkar 👇",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF938F99),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(commands) { command ->
                OutlinedCard(
                    modifier = Modifier
                        .clickable(enabled = enabled) { onCommandClick(command) }
                        .testTag("preset_${command.title.replace(" ", "_")}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (enabled) Color(0xFF2B2930) else Color(0xFF2B2930).copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF49454F))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = command.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = if (enabled) Color(0xFFD0BCFF) else Color(0xFFD0BCFF).copy(alpha = 0.38f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Hukum",
                            modifier = Modifier.size(16.dp),
                            tint = if (enabled) Color(0xFFD0BCFF) else Color(0xFFD0BCFF).copy(alpha = 0.38f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.minimumInteractiveComponentSize(): Modifier = this.size(48.dp)
