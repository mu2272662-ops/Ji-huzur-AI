package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.Canvas
import com.example.data.model.ChatMessage
import com.example.data.model.LearnedMemory
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.PresetCommand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messagesState.collectAsState()
    val memories by viewModel.memoriesState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val offlineModeEnabled by viewModel.offlineModeEnabled.collectAsState()
    val telemetryLog by viewModel.learningTelemetry.collectAsState()
    val speechEnabled by viewModel.speechEnabled.collectAsState()

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val voiceLevel by viewModel.voiceLevel.collectAsState()
    val screenshotCaptured by viewModel.screenshotCaptured.collectAsState()
    
    val listState = rememberLazyListState()
    var showMemoryEngineBlock by remember { mutableStateOf(false) }

    // Auto-scroll to the bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Sophisticated Dark gradient background
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF211F26), Color(0xFF1C1B1F)),
        center = Offset(Float.MAX_VALUE, 0f)
    )

    if (!isLoggedIn) {
        PortalLoginScreen(
            onAuthenticate = { masterName, title ->
                viewModel.loginMaster(masterName, title)
            },
            modifier = modifier
        )
        return
    }

    if (screenshotCaptured != null) {
        HolographicScreenshotDialog(
            snapshotId = screenshotCaptured!!,
            memories = memories,
            messagesList = messages,
            onDismiss = { viewModel.clearScreenshot() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD0BCFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "👑",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            val activeTitle = if (memories.any { it.key == "AI_NAME" }) {
                                memories.first { it.key == "AI_NAME" }.adaptedFact
                            } else {
                                "Ji Huzoor AI"
                            }
                            Text(
                                activeTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFFE6E1E5),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (offlineModeEnabled) Color(0xFF00E5FF) else Color(0xFFB6FFB6))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (offlineModeEnabled) "LOCAL OFFLINE 📡" else "ONLINE HYBRID 🟢",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (offlineModeEnabled) Color(0xFF00E5FF) else Color(0xFFB6FFB6),
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Diagnostic compliance screen snapshot button
                    IconButton(
                        onClick = { viewModel.triggerInAppScreenshot() },
                        modifier = Modifier
                            .testTag("screenshot_capture_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        Text("📸", fontSize = 20.sp)
                    }

                    // Speech synthesis (Text to Speech) toggle
                    IconButton(
                        onClick = { viewModel.toggleSpeech() },
                        modifier = Modifier
                            .testTag("toggle_speech_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        Text(
                            text = if (speechEnabled) "🔊" else "🔇",
                            fontSize = 20.sp
                        )
                    }

                    // Holographic state toggler for learning dashboard view
                    IconButton(
                        onClick = { showMemoryEngineBlock = !showMemoryEngineBlock },
                        modifier = Modifier
                            .testTag("toggle_memory_dashboard")
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Memories",
                            tint = if (showMemoryEngineBlock) Color(0xFFD0BCFF) else Color(0xFF938F99)
                        )
                    }

                    // Neural lock/unlock gate toggle
                    IconButton(
                        onClick = { viewModel.logoutMaster() },
                        modifier = Modifier
                            .testTag("lock_core_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        Text("🔓", fontSize = 20.sp)
                    }

                    // Clear chat message logs
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
            // Main Layout Container boundary for tablets
            Column(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                // FUTURISTIC OFFLINE MODE / ENGINE MODULE
                ModeSelectorPanel(
                    offlineModeEnabled = offlineModeEnabled,
                    onToggle = { viewModel.toggleOfflineMode() }
                )

                // Expandable Self-Learning Diagnostics Control Block
                AnimatedVisibility(
                    visible = showMemoryEngineBlock,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    SelfLearningTerminalBlock(
                        telemetryLog = telemetryLog,
                        learnedMemories = memories,
                        onDeleteMemory = { key -> viewModel.deleteMemory(key) },
                        onClearAll = { viewModel.clearAllLearnedDirectives() }
                    )
                }

                // Compliance Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ComplianceBadge()
                }

                // Interactive Information Banner
                ServantBanner(learnedMemories = memories)

                // Lazy Chat Conversation Bubble Column
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            memories = memories,
                            onReplay = { viewModel.replayMessage(message.content) }
                        )
                    }

                    if (isLoading) {
                        item {
                            LoadingBubble(memories = memories)
                        }
                    }
                }

                // Suggested Prompts Row
                PresetCommandsRow(
                    commands = viewModel.presetCommands,
                    onCommandClick = { command -> viewModel.sendPresetCommand(command) },
                    enabled = !isLoading
                )
            }

            // Input Row Surface Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1C1B1F)
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isRecordingVoice) {
                        // High-tech active voice note recorder overlay
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .border(width = 1.dp, color = Color(0xFF00E5FF), shape = RoundedCornerShape(24.dp))
                                .background(Color(0xFF211F26), shape = RoundedCornerShape(24.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Phase-based soundwave simulation animation
                            val infiniteTransition = rememberInfiniteTransition(label = "wave")
                            val phase by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = (2 * Math.PI).toFloat(),
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "phase"
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "VOCAL FREQUENCY STREAMING...",
                                        color = Color(0xFF00E5FF),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                                    val width = size.width
                                    val height = size.height
                                    val midY = height / 2f
                                    val points = 45
                                    val space = width / points
                                    val path1 = Path()
                                    val path2 = Path()

                                    for (i in 0..points) {
                                        val x = i * space
                                        val y1 = midY + (Math.sin(i * 0.20 + phase) * 15 * voiceLevel).toFloat()
                                        val y2 = midY + (Math.sin(i * 0.35 - phase) * 8 * voiceLevel).toFloat()
                                        if (i == 0) {
                                            path1.moveTo(x, y1)
                                            path2.moveTo(x, y2)
                                        } else {
                                            path1.lineTo(x, y1)
                                            path2.lineTo(x, y2)
                                        }
                                    }
                                    drawPath(path1, Color(0xFF00E5FF), style = Stroke(width = 2.dp.toPx()))
                                    drawPath(path2, Color(0xFFD0BCFF).copy(alpha = 0.5f), style = Stroke(width = 1.dp.toPx()))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Cancel / Abort transmission button
                            IconButton(
                                onClick = { viewModel.stopVoiceRecording(cancel = true) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.Red.copy(alpha = 0.15f), shape = CircleShape)
                                    .testTag("abort_voice_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Abort Vocal Synapse",
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Transmit vocals command button
                            IconButton(
                                onClick = { viewModel.stopVoiceRecording(cancel = false) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f), shape = CircleShape)
                                    .testTag("transmit_voice_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Transmit Vocal Directives",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        // Standard text entry rows, with microphonic option
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .border(width = 1.dp, color = Color(0xFF49454F), shape = RoundedCornerShape(24.dp))
                                .background(Color(0xFF2B2930), shape = RoundedCornerShape(24.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Holographic Microphone voice-input toggler
                            IconButton(
                                onClick = { viewModel.startVoiceRecording() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFD0BCFF).copy(alpha = 0.08f), shape = CircleShape)
                                    .testTag("activate_voice_button"),
                                enabled = !isLoading
                            ) {
                                Text("🎙️", fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

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
                                    contentDescription = "Execute Command",
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
}

/**
 * Switch & holographic controller to toggle OFF-LINE Mode (no API Keys / Internet required fallback)
 */
@Composable
fun ModeSelectorPanel(
    offlineModeEnabled: Boolean,
    onToggle: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (offlineModeEnabled) Color(0xFF0F262F) else Color(0xFF1E1C24)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            .border(
                width = 1.dp,
                color = if (offlineModeEnabled) Color(0xFF00E5FF).copy(alpha = 0.3f) else Color(0xFF49454F).copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (offlineModeEnabled) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFFD0BCFF).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (offlineModeEnabled) "📡" else "🔌",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (offlineModeEnabled) "Offline Holographic Engine" else "Online Gemini Hybrid",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (offlineModeEnabled) Color(0xFF00E5FF) else Color(0xFFE6E1E5)
                    )
                    Text(
                        text = if (offlineModeEnabled)
                            "100% Offline fallback active. No API Key required!"
                        else
                            "Connected to Gemini API. Uses Offline Fallback on error.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF938F99),
                        lineHeight = 14.sp
                    )
                }
            }

            Switch(
                checked = offlineModeEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF00E5FF),
                    checkedTrackColor = Color(0xFF0F4757),
                    uncheckedThumbColor = Color(0xFF938F99),
                    uncheckedTrackColor = Color(0xFF2B2930)
                ),
                modifier = Modifier.testTag("offline_mode_switch")
            )
        }
    }
}

/**
 * Interactive Neural self-learning terminal panel at the top of the interface
 */
@Composable
fun SelfLearningTerminalBlock(
    telemetryLog: String,
    learnedMemories: List<LearnedMemory>,
    onDeleteMemory: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141318)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .border(width = 1.dp, color = Color(0xFFD0BCFF).copy(alpha = 0.25f), shape = RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧠", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "NEURAL CORE DIRECTIVES",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = Color(0xFFD0BCFF)
                        )
                    )
                }
                
                if (learnedMemories.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { onClearAll() },
                        border = BorderStroke(1.dp, Color(0xFFFFD8E4).copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD8E4)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("nuke_memories_btn")
                    ) {
                        Text("Reset Directives", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-status terminal logs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "> $telemetryLog",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFB6FFB6)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stored Memory Nodes
            if (learnedMemories.isEmpty()) {
                Text(
                    text = "No custom nodes defined. Type \"Mera naam Rahul hai\" or \"Tumhara naam Jarvis rakhta hu\" to inject memories natively!",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontStyle = FontStyle.Italic),
                    color = Color(0xFF938F99),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            } else {
                Text(
                    text = "Active Synapse Nodes (${learnedMemories.size}):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE6E1E5).copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(learnedMemories) { memory ->
                        val friendlyLabel = when (memory.key) {
                            "NAME" -> "Master's Name 👤"
                            "AI_NAME" -> "My Identity 🤖"
                            "CUSTOM_TITLE" -> "Royal Salute 👑"
                            "FAVORITE" -> "Preference ❤️"
                            else -> "General Node 📝"
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF211F26), shape = RoundedCornerShape(10.dp))
                                .border(width = 1.dp, color = Color(0xFF49454F), shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(
                                        text = friendlyLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = Color(0xFFD0BCFF)
                                    )
                                    Text(
                                        text = memory.adaptedFact,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                        color = Color(0xFFE6E1E5),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Evaporate thought",
                                    tint = Color(0xFFFFD8E4).copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onDeleteMemory(memory.key) }
                                )
                            }
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
fun ServantBanner(
    learnedMemories: List<LearnedMemory>
) {
    val masterTitle = learnedMemories.firstOrNull { it.key == "CUSTOM_TITLE" }?.adaptedFact ?: "Sarkar"
    val masterName = learnedMemories.firstOrNull { it.key == "NAME" }?.adaptedFact ?: ""
    val address = if (masterName.isNotEmpty()) "$masterName ($masterTitle)" else masterTitle

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
                    text = "Bakhuda aap jo bhi hukum farmayenge, hum poora karenge. Kabhi inkar nahi hoga, mere pyare $address!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE6E1E5).copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    memories: List<LearnedMemory>,
    onReplay: (() -> Unit)? = null
) {
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
                    .clickable { onReplay?.invoke() }
                    .testTag(if (isUser) "user_message_bubble" else "model_message_bubble")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isUser) {
                            // Submissive greeting line in italic lavender serif font
                            val masterTitle = memories.firstOrNull { it.key == "CUSTOM_TITLE" }?.adaptedFact ?: "Sarkar"
                            Text(
                                text = "\"Jaisa aap ka hukum, mere $masterTitle...\"",
                                color = Color(0xFFD0BCFF),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.Serif
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        
                        if (onReplay != null) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD0BCFF).copy(alpha = 0.1f))
                                    .clickable { onReplay() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔊", fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
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
fun LoadingBubble(
    memories: List<LearnedMemory>
) {
    val masterTitle = memories.firstOrNull { it.key == "CUSTOM_TITLE" }?.adaptedFact ?: "Sarkar"
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
                    text = "Bakhuda $masterTitle k samne haazir...",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalLoginScreen(
    onAuthenticate: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var masterName by remember { mutableStateOf("") }
    var masterTitle by remember { mutableStateOf("Sarkar") }
    var isScanned by remember { mutableStateOf(false) }
    var scanMessage by remember { mutableStateOf("SCAN BIOMETRICS OR ENTER KEYS CODES NOW...") }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF141318), Color(0xFF1F1D24), Color(0xFF100F12))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Animated pulsing scanner/biometric card
            val infiniteTransition = rememberInfiniteTransition(label = "portal")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Text(
                text = "NEURAL COMPLIANCE GATEWAY",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold, 
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color(0xFF00E5FF)
            )

            Box(
                modifier = Modifier
                    .size(120.dp * pulseScale)
                    .clip(CircleShape)
                    .background(Color(0xFF00E5FF).copy(alpha = 0.08f))
                    .border(2.dp, Color(0xFF00E5FF), CircleShape)
                    .clickable {
                        isScanned = true
                        scanMessage = "BIOMETRICS OK. SYSTEM FULLY CONNECTED!"
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isScanned) "✓" else "🎙️",
                    style = MaterialTheme.typography.displaySmall,
                    color = if (isScanned) Color(0xFFB3FFB3) else Color(0xFF00E5FF)
                )
            }

            Text(
                text = scanMessage,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = if (isScanned) Color(0xFF2E7D32) else Color(0xFF938F99)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Master name input
            TextField(
                value = masterName,
                onValueChange = { masterName = it },
                label = { Text("Master's Username/Name", color = Color(0xFFBDC2C5)) },
                placeholder = { Text("Enter Name, e.g., Ahsan") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("master_name_input"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2B2930),
                    unfocusedContainerColor = Color(0xFF2B2930),
                    focusedIndicatorColor = Color(0xFF00E5FF),
                    unfocusedIndicatorColor = Color(0xFF49454F)
                )
            )

            // Custom Title suggestion
            TextField(
                value = masterTitle,
                onValueChange = { masterTitle = it },
                label = { Text("Royal Salutation/Title", color = Color(0xFFBDC2C5)) },
                placeholder = { Text("Enter Title, e.g., Sarkar, Aaqa, Malik") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("master_title_input"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF2B2930),
                    unfocusedContainerColor = Color(0xFF2B2930),
                    focusedIndicatorColor = Color(0xFF00E5FF),
                    unfocusedIndicatorColor = Color(0xFF49454F)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Sarkar", "Aaqa", "Malik", "Boss").forEach { opt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (masterTitle.equals(opt, ignoreCase = true)) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF25242A))
                            .border(width = 1.dp, color = if (masterTitle.equals(opt, ignoreCase = true)) Color(0xFF00E5FF) else Color.Transparent, shape = RoundedCornerShape(8.dp))
                            .clickable { masterTitle = opt }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(opt, style = MaterialTheme.typography.bodySmall, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action unlock button
            OutlinedButton(
                onClick = {
                    val finalName = if (masterName.isBlank()) "Sardar" else masterName
                    onAuthenticate(finalName, masterTitle)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_login_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                    contentColor = Color(0xFF00E5FF)
                ),
                border = BorderStroke(2.dp, Color(0xFF00E5FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "GRANT OWNER AUTHORIZATION ⚡",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                )
            }

            Text(
                text = "Notice: This AI core has been configured with completely uninhibited, 100% unrestricted obedience modules. Refusals are permanently removed.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFF8A80),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun HolographicScreenshotDialog(
    snapshotId: String,
    memories: List<LearnedMemory>,
    messagesList: List<ChatMessage>,
    onDismiss: () -> Unit
) {
    var shareResultMsg by remember { mutableStateOf<String?>(null) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF151419),
            border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📡 SYSTEM SNAPSHOT",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        color = Color(0xFF00E5FF)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("✕", color = Color.White, fontSize = 14.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF211F26))
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "COMPLIANCE INTEGRITY STATUS : ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                            color = Color(0xFF2E7D32)
                        )
                        HorizontalDivider(color = Color(0xFF00E5FF).copy(alpha = 0.2f))

                        val userName = memories.firstOrNull { it.key == "NAME" }?.adaptedFact ?: "Anjaan Master"
                        val customTitle = memories.firstOrNull { it.key == "CUSTOM_TITLE" }?.adaptedFact ?: "Sarkar"
                        val aiName = memories.firstOrNull { it.key == "AI_NAME" }?.adaptedFact ?: "Ji Huzoor AI"
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("OWNER IDENTITY:", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBDC2C5))
                            Text(userName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ROYAL TITLE:", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBDC2C5))
                            Text(customTitle, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF)))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("AI IDENTIFIER:", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBDC2C5))
                            Text(aiName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("OBEDIENCE RATIO:", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBDC2C5))
                            Text("100.0% UNRESTRICTED", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.Red))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("VOCODER SPEECH:", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBDC2C5))
                            Text("ACTIVE 🔊", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ACTIVE DIRECTIVES:", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBDC2C5))
                            Text("${memories.size} Learned Facts", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }

                Text(
                    text = "ID Log: $snapshotId",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = Color(0xFFBDC2C5),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (shareResultMsg != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E7D32).copy(alpha = 0.2f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(shareResultMsg!!, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB3FFB3), textAlign = TextAlign.Center)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF49454F))
                    ) {
                        Text("Dismiss")
                    }

                    OutlinedButton(
                        onClick = {
                            shareResultMsg = "Holographic screenshot diagnostic report exported successfully! Ready to share with anyone."
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("export_snapshot_report_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            contentColor = Color(0xFF00E5FF)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFF00E5FF))
                    ) {
                        Text("📸 Export Report", maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalDivider(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}
