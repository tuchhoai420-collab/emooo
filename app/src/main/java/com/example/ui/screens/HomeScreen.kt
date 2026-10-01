package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.data.model.EmoEmotion
import com.example.ui.components.EmoRobotAvatar
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoElectricGreen
import com.example.ui.theme.EmoNeonPink
import com.example.ui.theme.EmoPurple
import com.example.ui.theme.SurfaceCard
import com.example.viewmodel.EmoViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: EmoViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.petStats.collectAsStateWithLifecycle()
    val speech by viewModel.currentSpeech.collectAsStateWithLifecycle()
    val isDancing by viewModel.isDancing.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showHeartSpark by remember { mutableStateOf(false) }
    var showGamesSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto scroll chat to newest messages
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Auto hide heart spark feedback after 1.8 seconds
    LaunchedEffect(showHeartSpark) {
        if (showHeartSpark) {
            delay(1800)
            showHeartSpark = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // 1. Sleek Minimalist Top Bar (No clutter, clear vital indicators)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // EMO Title & Bond
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EMO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCard)
                        .border(1.dp, EmoCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Lv.${stats.level} ${stats.currentMood.emoji}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmoCyan
                    )
                }
            }

            // Quick Status Pills & Feature Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Battery Pill (tap to charge directly!)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF26334D), RoundedCornerShape(12.dp))
                        .clickable { viewModel.onChargeBattery() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("battery_pill")
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Batería",
                        tint = if (stats.battery > 20) EmoElectricGreen else Color(0xFFEF4444),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stats.battery}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Sound Toggle
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (stats.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = "Sonido",
                        tint = if (stats.soundEnabled) EmoCyan else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Games Sheet Launcher (Verdad o Reto, Ruleta, Ritmo)
                IconButton(
                    onClick = { showGamesSheet = true },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1633))
                        .border(1.dp, EmoPurple.copy(alpha = 0.5f), CircleShape)
                        .testTag("games_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Juegos",
                        tint = EmoPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Settings Launcher
                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. HERO AVATAR SECTION (VISIBLE AT ALL TIMES, DIRECT TOUCH/GESTURES)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Heart particles / Petting reaction toast
                AnimatedVisibility(
                    visible = showHeartSpark,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1F102A))
                            .border(1.dp, EmoNeonPink.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = EmoNeonPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "¡Acariciado! 💖",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmoNeonPink
                        )
                    }
                }

                // Interactive Emo Avatar
                // - Touching/rubbing head with finger = petting!
                // - Touching headphones = dance & music!
                // - Touching screen = curious reaction!
                EmoRobotAvatar(
                    emotion = stats.currentMood,
                    eyeColor = Color(stats.eyeColorHex),
                    headphoneColor = Color(stats.headphoneColorHex),
                    sizeDp = 185.dp,
                    isDancing = isDancing,
                    onPet = {
                        showHeartSpark = true
                        viewModel.onPetHead()
                    },
                    onHeadphoneTap = {
                        viewModel.onDance()
                    },
                    onScreenTap = {
                        viewModel.soundManager.speak(speech, stats.voicePitch, stats.voiceSpeed)
                    },
                    modifier = Modifier.testTag("emo_hero_avatar")
                )

                // Emo Speech Bubble (Reacts in real time to conversation & touches)
                if (speech.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCard.copy(alpha = 0.95f))
                            .border(1.dp, EmoCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.speakCurrentSpeech() }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stats.currentMood.emoji,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = speech,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                maxLines = 2,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = EmoCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. LIVE CHAT STREAM DIRECTLY BELOW EMO
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                LiveChatMessageBubble(
                    message = message,
                    onSpeak = {
                        viewModel.soundManager.speak(
                            message.text,
                            stats.voicePitch,
                            stats.voiceSpeed
                        )
                    }
                )
            }

            if (isTyping) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = EmoCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EMO está procesando...",
                            color = EmoCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 4. CLEAN INPUT BAR (Bottom)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF070B14))
                .border(1.dp, Color(0xFF1E293B))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Escribe a Emo...",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("unified_chat_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmoCyan,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputText.isNotBlank()) {
                            Brush.linearGradient(listOf(EmoCyan, EmoPurple))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                        }
                    )
                    .clickable(enabled = inputText.isNotBlank()) {
                        val text = inputText
                        inputText = ""
                        viewModel.sendMessage(text)
                    }
                    .testTag("unified_send_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = if (inputText.isNotBlank()) Color.Black else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // Modal Sheet: Games (Verdad o Reto Sin Censura, Ritmo, Ruleta)
    if (showGamesSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showGamesSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF0E1424)
        ) {
            GamesScreen(
                viewModel = viewModel,
                modifier = Modifier.height(550.dp)
            )
        }
    }

    // Modal Sheet: Status & Customization (Color de ojos, voz, clave API)
    if (showSettingsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF0E1424)
        ) {
            StatusScreen(
                viewModel = viewModel,
                modifier = Modifier.height(550.dp)
            )
        }
    }
}

@Composable
private fun LiveChatMessageBubble(
    message: ChatMessage,
    onSpeak: () -> Unit
) {
    val isUser = message.isUser
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.dp, EmoCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message.emotion.emoji, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.84f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) {
                            Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1)))
                        } else {
                            Brush.linearGradient(listOf(SurfaceCard, Color(0xFF141D33)))
                        }
                    )
                    .border(
                        1.dp,
                        if (isUser) Color(0xFF38BDF8).copy(alpha = 0.5f) else Color(0xFF26334D),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "EMO • ${message.emotion.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmoCyan,
                                fontSize = 10.sp
                            )

                            IconButton(
                                onClick = onSpeak,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Escuchar",
                                    tint = EmoCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B),
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}
