package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoElectricGreen
import com.example.ui.theme.EmoNeonPink
import com.example.ui.theme.EmoPurple
import com.example.ui.theme.SurfaceCard

@Composable
fun QuickActionRow(
    onChargeClick: () -> Unit,
    onPetClick: () -> Unit,
    onDanceClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard.copy(alpha = 0.75f))
            .border(1.dp, Color(0xFF23304E), RoundedCornerShape(20.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButtonItem(
            icon = Icons.Default.Bolt,
            label = "Cargar",
            gradient = listOf(EmoElectricGreen, Color(0xFF059669)),
            tag = "action_charge",
            onClick = onChargeClick
        )

        ActionButtonItem(
            icon = Icons.Default.Favorite,
            label = "Acariciar",
            gradient = listOf(EmoNeonPink, Color(0xFFBE185D)),
            tag = "action_pet",
            onClick = onPetClick
        )

        ActionButtonItem(
            icon = Icons.Default.MusicNote,
            label = "Bailar",
            gradient = listOf(EmoPurple, Color(0xFF6D28D9)),
            tag = "action_dance",
            onClick = onDanceClick
        )

        ActionButtonItem(
            icon = Icons.Default.ChatBubble,
            label = "Hablar",
            gradient = listOf(EmoCyan, Color(0xFF0284C7)),
            tag = "action_chat",
            onClick = onChatClick
        )
    }
}

@Composable
private fun ActionButtonItem(
    icon: ImageVector,
    label: String,
    gradient: List<Color>,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradient))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp
        )
    }
}
