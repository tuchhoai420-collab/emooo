package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoElectricGreen
import com.example.ui.theme.EmoNeonPink
import com.example.ui.theme.EmoPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.viewmodel.EmoViewModel

@Composable
fun StatusScreen(
    viewModel: EmoViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.petStats.collectAsStateWithLifecycle()
    val customKey by viewModel.customApiKey.collectAsStateWithLifecycle()

    var keyInput by remember(customKey) { mutableStateOf(customKey) }
    var pitchVal by remember(stats.voicePitch) { mutableFloatStateOf(stats.voicePitch) }
    var speedVal by remember(stats.voiceSpeed) { mutableFloatStateOf(stats.voiceSpeed) }
    var keySavedMessage by remember { mutableStateOf("") }

    val eyeColors = listOf(
        Pair("Cian Neón", 0xFF00F5FF),
        Pair("Rosa Neón", 0xFFFF2A85),
        Pair("Verde Esmeralda", 0xFF10B981),
        Pair("Oro Solar", 0xFFF59E0B),
        Pair("Violeta Láser", 0xFFA855F7),
        Pair("Blanco Cibernético", 0xFFFFFFFF)
    )

    val headphoneColors = listOf(
        Pair("Púrpura EMO", 0xFF8B5CF6),
        Pair("Azul Cyber", 0xFF0284C7),
        Pair("Rosa Neón", 0xFFEC4899),
        Pair("Obsidiana", 0xFF1E293B),
        Pair("Esmeralda", 0xFF059669)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Diagnostic Overview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = EmoCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DIAGNÓSTICO DEL ROBOT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Modelo", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text(text = "EMO AI Companion v2.5", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Motor de IA", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text(text = "Gemini 3.5 Flash", color = EmoCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Modo de Rol", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text(text = "Libre / Sin Censura (1ra Persona)", color = EmoNeonPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Interacciones Totales", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    Text(text = "${stats.totalInteractions}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Customization: Eye Color
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = EmoCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Color de Ojos Digitales",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    eyeColors.forEach { (name, hex) ->
                        val isSelected = stats.eyeColorHex == hex
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { viewModel.updateEyeColor(hex) }
                        )
                    }
                }
            }
        }

        // Customization: Headphone Color
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Color de Auriculares",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    headphoneColors.forEach { (name, hex) ->
                        val isSelected = stats.headphoneColorHex == hex
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { viewModel.updateHeadphoneColor(hex) }
                        )
                    }
                }
            }
        }

        // Voice Tuning
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = EmoPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voz Robótica (TTS)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Tono / Pitch: ${String.format("%.2f", pitchVal)}",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Slider(
                    value = pitchVal,
                    onValueChange = {
                        pitchVal = it
                        viewModel.updateVoiceSettings(pitchVal, speedVal)
                    },
                    valueRange = 0.8f..1.8f,
                    colors = SliderDefaults.colors(thumbColor = EmoCyan, activeTrackColor = EmoCyan)
                )

                Text(
                    text = "Velocidad: ${String.format("%.2f", speedVal)}",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Slider(
                    value = speedVal,
                    onValueChange = {
                        speedVal = it
                        viewModel.updateVoiceSettings(pitchVal, speedVal)
                    },
                    valueRange = 0.7f..1.4f,
                    colors = SliderDefaults.colors(thumbColor = EmoPurple, activeTrackColor = EmoPurple)
                )

                Button(
                    onClick = {
                        viewModel.soundManager.speak(
                            "¡Hola! Así suena mi voz con esta frecuencia robótica personalizada. ¡Me encanta hablar contigo!",
                            pitchVal,
                            speedVal
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Probar Voz de Emo", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Gemini API Key Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = EmoCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Configuración Gemini API",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Puedes ingresar tu propia API Key de Gemini para asegurar conexión en vivo, o usar el sistema integrado de AI Studio.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    placeholder = { Text("Pega tu API Key de Gemini aquí...", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmoCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Button(
                    onClick = {
                        viewModel.updateCustomApiKey(keyInput)
                        keySavedMessage = "¡Clave guardada exitosamente!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmoCyan),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Guardar Clave de API", fontWeight = FontWeight.Bold, color = Color.Black)
                }

                if (keySavedMessage.isNotBlank()) {
                    Text(
                        text = keySavedMessage,
                        color = EmoElectricGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
