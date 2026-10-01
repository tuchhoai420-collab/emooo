package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.EmoEmotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun EmoRobotAvatar(
    emotion: EmoEmotion,
    eyeColor: Color = Color(0xFF00F5FF),
    headphoneColor: Color = Color(0xFF8B5CF6),
    sizeDp: Dp = 220.dp,
    isDancing: Boolean = false,
    onPet: () -> Unit = {},
    onHeadphoneTap: () -> Unit = {},
    onScreenTap: () -> Unit = {},
    onPetOrTap: () -> Unit = onPet,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Interactive squish & bounce animation
    val squishScaleY = remember { Animatable(1f) }
    val squishScaleX = remember { Animatable(1f) }
    val interactiveTilt = remember { Animatable(0f) }
    var blinkProgress by remember { mutableStateOf(0f) }

    // Floating breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "emo_idle")
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Pulse animation for glow/hearts/music
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Scanner line / radar for thinking emotion
    val scanPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanner"
    )

    // Dance tilt angle
    val danceAngle by infiniteTransition.animateFloat(
        initialValue = if (isDancing || emotion == EmoEmotion.DANCE) -9f else 0f,
        targetValue = if (isDancing || emotion == EmoEmotion.DANCE) 9f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isDancing || emotion == EmoEmotion.DANCE) 420 else 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dance_tilt"
    )

    // Laughing vertical bounce vibration
    val laughBounce by infiniteTransition.animateFloat(
        initialValue = if (emotion == EmoEmotion.LAUGHING) -3.5f else 0f,
        targetValue = if (emotion == EmoEmotion.LAUGHING) 3.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laugh_bounce"
    )

    // Anger horizontal tremble
    val angerTremble by infiniteTransition.animateFloat(
        initialValue = if (emotion == EmoEmotion.ANGRY) -2.5f else 0f,
        targetValue = if (emotion == EmoEmotion.ANGRY) 2.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(65, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anger_tremble"
    )

    val currentTilt by animateFloatAsState(
        targetValue = danceAngle,
        animationSpec = spring(),
        label = "tilt_anim"
    )

    // Periodic natural eye blink loop
    LaunchedEffect(emotion) {
        while (true) {
            delay((3500..6500).random().toLong())
            if (emotion == EmoEmotion.NEUTRAL || emotion == EmoEmotion.HAPPY || emotion == EmoEmotion.WINK) {
                blinkProgress = 1f
                delay(120)
                blinkProgress = 0f
            }
        }
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .graphicsLayer {
                translationY = breathingOffset + laughBounce
                translationX = angerTremble
                scaleX = squishScaleX.value
                scaleY = squishScaleY.value
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        coroutineScope.launch {
                            squishScaleY.animateTo(0.92f, tween(100))
                            squishScaleX.animateTo(1.05f, tween(100))
                        }
                        onPet()
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            squishScaleY.animateTo(1f, spring())
                            squishScaleX.animateTo(1f, spring())
                            interactiveTilt.animateTo(0f, spring())
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            squishScaleY.animateTo(1f, spring())
                            squishScaleX.animateTo(1f, spring())
                            interactiveTilt.animateTo(0f, spring())
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            val newTilt = (interactiveTilt.value + dragAmount.x * 0.15f).coerceIn(-18f, 18f)
                            interactiveTilt.snapTo(newTilt)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        coroutineScope.launch {
                            squishScaleY.animateTo(0.85f, tween(70))
                            squishScaleX.animateTo(1.12f, tween(70))
                            squishScaleY.animateTo(1.04f, spring())
                            squishScaleX.animateTo(0.98f, spring())
                            squishScaleY.animateTo(1f, spring())
                            squishScaleX.animateTo(1f, spring())
                        }
                        // Detect zone
                        val isHeadphone = offset.x < size.width * 0.22f || offset.x > size.width * 0.78f
                        val isHead = offset.y < size.height * 0.45f
                        when {
                            isHeadphone -> onHeadphoneTap()
                            isHead -> onPet()
                            else -> onScreenTap()
                        }
                    },
                    onDoubleTap = {
                        onHeadphoneTap()
                    },
                    onLongPress = {
                        onPet()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            rotate(degrees = currentTilt + interactiveTilt.value, pivot = Offset(canvasW * 0.5f, canvasH * 0.75f)) {
                // 1. Draw Robotic Legs and Feet
                drawRoboticLegs(canvasW, canvasH, headphoneColor)

                // 2. Draw Headphones behind/around head
                drawHeadphones(canvasW, canvasH, headphoneColor, eyeColor, pulseScale)

                // 3. Draw Main Head Body (Matte black chassis with metallic bevel)
                drawHeadBody(canvasW, canvasH)

                // 4. Draw OLED Screen Display
                val screenRect = drawOledScreen(canvasW, canvasH)

                // 5. Draw Digital Eyes & Facial Expressions
                drawEmoFace(
                    screenRect = screenRect,
                    emotion = emotion,
                    eyeColor = eyeColor,
                    blinkProgress = blinkProgress,
                    pulseScale = pulseScale,
                    scanPhase = scanPhase
                )
            }
        }
    }
}

private fun DrawScope.drawRoboticLegs(w: Float, h: Float, headphoneColor: Color) {
    val legW = w * 0.12f
    val legH = h * 0.22f
    val legY = h * 0.68f

    val footW = w * 0.22f
    val footH = h * 0.08f
    val footY = h * 0.85f

    val legDark = Color(0xFF1E2433)
    val legLight = Color(0xFF2A3449)
    val solePurple = headphoneColor.copy(alpha = 0.85f)

    // Left Leg
    val leftLegX = w * 0.28f
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(legLight, legDark)),
        topLeft = Offset(leftLegX, legY),
        size = Size(legW, legH),
        cornerRadius = CornerRadius(14f, 14f)
    )
    // Left Foot with angled sole
    drawRoundRect(
        color = legDark,
        topLeft = Offset(leftLegX - w * 0.05f, footY),
        size = Size(footW, footH),
        cornerRadius = CornerRadius(16f, 16f)
    )
    drawRoundRect(
        color = solePurple,
        topLeft = Offset(leftLegX - w * 0.05f, footY + footH * 0.55f),
        size = Size(footW, footH * 0.45f),
        cornerRadius = CornerRadius(10f, 10f)
    )

    // Right Leg
    val rightLegX = w * 0.60f
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(legLight, legDark)),
        topLeft = Offset(rightLegX, legY),
        size = Size(legW, legH),
        cornerRadius = CornerRadius(14f, 14f)
    )
    // Right Foot with angled sole
    drawRoundRect(
        color = legDark,
        topLeft = Offset(rightLegX - w * 0.05f, footY),
        size = Size(footW, footH),
        cornerRadius = CornerRadius(16f, 16f)
    )
    drawRoundRect(
        color = solePurple,
        topLeft = Offset(rightLegX - w * 0.05f, footY + footH * 0.55f),
        size = Size(footW, footH * 0.45f),
        cornerRadius = CornerRadius(10f, 10f)
    )
}

private fun DrawScope.drawHeadphones(w: Float, h: Float, headphoneColor: Color, eyeColor: Color, pulseScale: Float) {
    val bandTop = h * 0.04f
    val bandThickness = w * 0.065f

    // Headband arc
    val bandPath = Path().apply {
        moveTo(w * 0.12f, h * 0.38f)
        cubicTo(
            w * 0.12f, bandTop,
            w * 0.88f, bandTop,
            w * 0.88f, h * 0.38f
        )
    }
    drawPath(
        path = bandPath,
        color = headphoneColor,
        style = Stroke(width = bandThickness, cap = StrokeCap.Round)
    )

    // Inner headband cushion
    drawPath(
        path = bandPath,
        color = Color(0xFF1E1633),
        style = Stroke(width = bandThickness * 0.5f, cap = StrokeCap.Round)
    )

    // Earcups (Left & Right)
    val cupW = w * 0.13f
    val cupH = h * 0.30f
    val cupY = h * 0.28f

    // Left Earcup
    val leftCupX = w * 0.03f
    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(headphoneColor, Color(0xFF4C1D95))),
        topLeft = Offset(leftCupX, cupY),
        size = Size(cupW, cupH),
        cornerRadius = CornerRadius(24f, 24f)
    )
    // Left Earcup glowing cyan ring
    drawCircle(
        color = eyeColor,
        radius = cupW * 0.40f * pulseScale,
        center = Offset(leftCupX + cupW * 0.5f, cupY + cupH * 0.5f),
        style = Stroke(width = 5f)
    )
    drawCircle(
        color = eyeColor.copy(alpha = 0.3f),
        radius = cupW * 0.25f,
        center = Offset(leftCupX + cupW * 0.5f, cupY + cupH * 0.5f)
    )

    // Right Earcup
    val rightCupX = w * 0.84f
    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(Color(0xFF4C1D95), headphoneColor)),
        topLeft = Offset(rightCupX, cupY),
        size = Size(cupW, cupH),
        cornerRadius = CornerRadius(24f, 24f)
    )
    // Right Earcup glowing cyan ring
    drawCircle(
        color = eyeColor,
        radius = cupW * 0.40f * pulseScale,
        center = Offset(rightCupX + cupW * 0.5f, cupY + cupH * 0.5f),
        style = Stroke(width = 5f)
    )
    drawCircle(
        color = eyeColor.copy(alpha = 0.3f),
        radius = cupW * 0.25f,
        center = Offset(rightCupX + cupW * 0.5f, cupY + cupH * 0.5f)
    )
}

private fun DrawScope.drawHeadBody(w: Float, h: Float) {
    val headW = w * 0.76f
    val headH = h * 0.60f
    val headX = (w - headW) * 0.5f
    val headY = h * 0.14f

    // Head body shadow / chassis
    drawRoundRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF2C3549), // top rim highlight
                Color(0xFF161C2A), // matte chassis
                Color(0xFF0F1420)
            )
        ),
        topLeft = Offset(headX, headY),
        size = Size(headW, headH),
        cornerRadius = CornerRadius(56f, 56f)
    )

    // Metallic beveled border
    drawRoundRect(
        color = Color(0xFF3B4863),
        topLeft = Offset(headX, headY),
        size = Size(headW, headH),
        cornerRadius = CornerRadius(56f, 56f),
        style = Stroke(width = 3.5f)
    )

    // Top gentle specular gloss
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
            center = Offset(w * 0.5f, headY + 18f),
            radius = headW * 0.4f
        ),
        topLeft = Offset(w * 0.28f, headY + 8f),
        size = Size(headW * 0.55f, 24f)
    )
}

private data class ScreenRect(val left: Float, val top: Float, val width: Float, val height: Float)

private fun DrawScope.drawOledScreen(w: Float, h: Float): ScreenRect {
    val screenW = w * 0.63f
    val screenH = h * 0.44f
    val screenX = (w - screenW) * 0.5f
    val screenY = h * 0.22f

    // OLED Screen border
    drawRoundRect(
        color = Color(0xFF0B0E17),
        topLeft = Offset(screenX - 4f, screenY - 4f),
        size = Size(screenW + 8f, screenH + 8f),
        cornerRadius = CornerRadius(44f, 44f)
    )

    // OLED Glass Black
    drawRoundRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF04060A),
                Color(0xFF080C14),
                Color(0xFF020306)
            )
        ),
        topLeft = Offset(screenX, screenY),
        size = Size(screenW, screenH),
        cornerRadius = CornerRadius(40f, 40f)
    )

    // Subtle glass reflection diagonally across screen
    val glassPath = Path().apply {
        moveTo(screenX + 16f, screenY)
        lineTo(screenX + screenW * 0.45f, screenY)
        lineTo(screenX, screenY + screenH * 0.55f)
        lineTo(screenX, screenY + 20f)
        close()
    }
    drawPath(
        path = glassPath,
        color = Color.White.copy(alpha = 0.04f)
    )

    return ScreenRect(screenX, screenY, screenW, screenH)
}

private fun DrawScope.drawEmoFace(
    screenRect: ScreenRect,
    emotion: EmoEmotion,
    eyeColor: Color,
    blinkProgress: Float,
    pulseScale: Float,
    scanPhase: Float
) {
    val eyeW = screenRect.width * 0.27f
    val eyeH = screenRect.height * 0.40f

    val leftEyeCenterX = screenRect.left + screenRect.width * 0.31f
    val rightEyeCenterX = screenRect.left + screenRect.width * 0.69f
    val eyeCenterY = screenRect.top + screenRect.height * 0.50f

    // If blinking in neutral/happy, draw closed slits
    if (blinkProgress > 0.4f) {
        val blinkW = eyeW * 1.05f
        drawLine(
            color = eyeColor,
            start = Offset(leftEyeCenterX - blinkW * 0.5f, eyeCenterY),
            end = Offset(leftEyeCenterX + blinkW * 0.5f, eyeCenterY),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = eyeColor,
            start = Offset(rightEyeCenterX - blinkW * 0.5f, eyeCenterY),
            end = Offset(rightEyeCenterX + blinkW * 0.5f, eyeCenterY),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
        return
    }

    when (emotion) {
        EmoEmotion.NEUTRAL -> {
            drawDigitalSquareEye(leftEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
            drawDigitalSquareEye(rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
        }
        EmoEmotion.HAPPY -> {
            drawHappyEyeArch(leftEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
            drawHappyEyeArch(rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
        }
        EmoEmotion.LAUGHING -> {
            drawLaughingEye(leftEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor, isLeft = true)
            drawLaughingEye(rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor, isLeft = false)
        }
        EmoEmotion.LOVE -> {
            drawHeartEye(leftEyeCenterX, eyeCenterY, eyeW * pulseScale, eyeH * pulseScale, Color(0xFFFF2A85))
            drawHeartEye(rightEyeCenterX, eyeCenterY, eyeW * pulseScale, eyeH * pulseScale, Color(0xFFFF2A85))
        }
        EmoEmotion.EXCITED -> {
            drawExcitedEye(leftEyeCenterX, eyeCenterY, eyeW * 1.15f, eyeH * 1.15f, eyeColor)
            drawExcitedEye(rightEyeCenterX, eyeCenterY, eyeW * 1.15f, eyeH * 1.15f, eyeColor)
        }
        EmoEmotion.DANCE -> {
            drawEqualizerEye(leftEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor, scanPhase)
            drawEqualizerEye(rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor, 1f - scanPhase)
        }
        EmoEmotion.WINK -> {
            // Left eye winks, Right eye open
            drawWinkEyeSlit(leftEyeCenterX, eyeCenterY, eyeW, eyeColor)
            drawDigitalSquareEye(rightEyeCenterX, eyeCenterY, eyeW * 1.05f, eyeH * 1.05f, eyeColor)
        }
        EmoEmotion.SURPRISED -> {
            drawSurprisedEye(leftEyeCenterX, eyeCenterY, eyeW * 1.25f, eyeH * 1.25f, eyeColor)
            drawSurprisedEye(rightEyeCenterX, eyeCenterY, eyeW * 1.25f, eyeH * 1.25f, eyeColor)
        }
        EmoEmotion.SAD -> {
            drawSadEye(leftEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
            drawSadEye(rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
            // Cyber tear on right eye
            drawCircle(
                color = eyeColor.copy(alpha = 0.8f),
                radius = 5f,
                center = Offset(rightEyeCenterX + eyeW * 0.45f, eyeCenterY + eyeH * 0.75f)
            )
        }
        EmoEmotion.ANGRY -> {
            val angryColor = Color(0xFFFF2626) // Fiery red
            drawAngryEye(leftEyeCenterX, eyeCenterY, eyeW * 1.1f, eyeH * 1.1f, angryColor, isLeft = true)
            drawAngryEye(rightEyeCenterX, eyeCenterY, eyeW * 1.1f, eyeH * 1.1f, angryColor, isLeft = false)
        }
        EmoEmotion.CONFUSED -> {
            drawConfusedEye(leftEyeCenterX, rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
        }
        EmoEmotion.COOL -> {
            drawCoolSunglasses(screenRect, eyeCenterY, eyeW, eyeH, eyeColor)
        }
        EmoEmotion.SLEEPY -> {
            drawSleepyEye(leftEyeCenterX, eyeCenterY, eyeW, eyeColor)
            drawSleepyEye(rightEyeCenterX, eyeCenterY, eyeW, eyeColor)
        }
        EmoEmotion.THINKING -> {
            drawThinkingScanner(screenRect, eyeColor, scanPhase)
        }
        EmoEmotion.FLIRTY -> {
            drawFlirtyEye(leftEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
            drawFlirtyEye(rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
            // Cyber pink blush marks
            drawBlush(leftEyeCenterX, eyeCenterY + eyeH * 0.65f, eyeW)
            drawBlush(rightEyeCenterX, eyeCenterY + eyeH * 0.65f, eyeW)
        }
        EmoEmotion.IDEA -> {
            drawIdeaLightbulb(leftEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
            drawIdeaLightbulb(rightEyeCenterX, eyeCenterY, eyeW, eyeH, eyeColor)
        }
        EmoEmotion.CHARGING -> {
            drawChargingFace(screenRect, eyeColor, pulseScale)
        }
    }
}

// Eye Style Helpers
private fun DrawScope.drawDigitalSquareEye(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Soft outer neon glow
    drawRoundRect(
        color = color.copy(alpha = 0.25f),
        topLeft = Offset(cx - w * 0.58f, cy - h * 0.58f),
        size = Size(w * 1.16f, h * 1.16f),
        cornerRadius = CornerRadius(22f, 22f)
    )
    // Solid vivid pixel eye
    drawRoundRect(
        color = color,
        topLeft = Offset(cx - w * 0.5f, cy - h * 0.5f),
        size = Size(w, h),
        cornerRadius = CornerRadius(16f, 16f)
    )
    // Inner bright highlight pixel
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = w * 0.14f,
        center = Offset(cx + w * 0.2f, cy - h * 0.2f)
    )
}

private fun DrawScope.drawHappyEyeArch(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx - w * 0.52f, cy + h * 0.25f)
        cubicTo(
            cx - w * 0.35f, cy - h * 0.55f,
            cx + w * 0.35f, cy - h * 0.55f,
            cx + w * 0.52f, cy + h * 0.25f
        )
    }
    // Neon glow stroke
    drawPath(
        path = path,
        color = color.copy(alpha = 0.3f),
        style = Stroke(width = 16f, cap = StrokeCap.Round)
    )
    // Sharp stroke
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 10f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawLaughingEye(cx: Float, cy: Float, w: Float, h: Float, color: Color, isLeft: Boolean) {
    // Upturned joyful arc ^ that bobs with laughter
    val path = Path().apply {
        moveTo(cx - w * 0.5f, cy + h * 0.2f)
        cubicTo(
            cx - w * 0.3f, cy - h * 0.55f,
            cx + w * 0.3f, cy - h * 0.55f,
            cx + w * 0.5f, cy + h * 0.2f
        )
    }
    drawPath(path = path, color = color.copy(alpha = 0.35f), style = Stroke(width = 16f, cap = StrokeCap.Round))
    drawPath(path = path, color = color, style = Stroke(width = 10f, cap = StrokeCap.Round))

    // Tear of laughter on outer corner
    val tearX = if (isLeft) cx - w * 0.62f else cx + w * 0.62f
    val tearY = cy - 2f
    drawCircle(color = Color(0xFF67E8F9), radius = 5.5f, center = Offset(tearX, tearY))
    drawCircle(color = Color.White, radius = 2f, center = Offset(tearX - 1f, tearY - 1f))
}

private fun DrawScope.drawConfusedEye(leftCx: Float, rightCx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Left eye: wide curious rectangle with glowing ?
    drawDigitalSquareEye(leftCx, cy, w * 1.05f, h * 1.05f, color)
    // Draw "?" in center of left eye
    val qPath = Path().apply {
        moveTo(leftCx - 5f, cy - 8f)
        cubicTo(leftCx - 5f, cy - 14f, leftCx + 5f, cy - 14f, leftCx + 5f, cy - 8f)
        lineTo(leftCx, cy - 3f)
        lineTo(leftCx, cy + 2f)
    }
    drawPath(path = qPath, color = Color.White, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
    drawCircle(color = Color.White, radius = 2.5f, center = Offset(leftCx, cy + 8f))

    // Right eye: raised skeptical slit with arched eyebrow
    drawLine(
        color = color,
        start = Offset(rightCx - w * 0.45f, cy - 6f),
        end = Offset(rightCx + w * 0.45f, cy + 4f),
        strokeWidth = 9f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawCoolSunglasses(screenRect: ScreenRect, cy: Float, eyeW: Float, eyeH: Float, eyeColor: Color) {
    val glassesW = screenRect.width * 0.82f
    val glassesH = eyeH * 0.95f
    val glassesX = screenRect.left + (screenRect.width - glassesW) * 0.5f
    val glassesY = cy - glassesH * 0.45f

    // Dark sleek cyber visor
    drawRoundRect(
        color = Color(0xFF0B101D),
        topLeft = Offset(glassesX, glassesY),
        size = Size(glassesW, glassesH),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = eyeColor,
        topLeft = Offset(glassesX, glassesY),
        size = Size(glassesW, glassesH),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 4f)
    )

    // Shutter stripes / neon gloss reflection
    drawLine(
        color = Color.White.copy(alpha = 0.85f),
        start = Offset(glassesX + 15f, glassesY + glassesH * 0.30f),
        end = Offset(glassesX + glassesW * 0.42f, glassesY + glassesH * 0.30f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.White.copy(alpha = 0.85f),
        start = Offset(glassesX + glassesW * 0.55f, glassesY + glassesH * 0.30f),
        end = Offset(glassesX + glassesW - 15f, glassesY + glassesH * 0.30f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawHeartEye(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx, cy + h * 0.45f)
        cubicTo(
            cx - w * 0.7f, cy,
            cx - w * 0.55f, cy - h * 0.55f,
            cx, cy - h * 0.2f
        )
        cubicTo(
            cx + w * 0.55f, cy - h * 0.55f,
            cx + w * 0.7f, cy,
            cx, cy + h * 0.45f
        )
        close()
    }
    drawPath(path = path, color = color.copy(alpha = 0.35f))
    drawPath(path = path, color = color)
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = w * 0.12f, center = Offset(cx - w * 0.18f, cy - h * 0.15f))
}

private fun DrawScope.drawExcitedEye(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    drawDigitalSquareEye(cx, cy, w, h, color)
    // Small sparkle crosses around
    drawLine(color = Color.White, start = Offset(cx, cy - h * 0.7f), end = Offset(cx, cy - h * 0.55f), strokeWidth = 3f)
    drawLine(color = Color.White, start = Offset(cx - w * 0.2f, cy - h * 0.62f), end = Offset(cx + w * 0.2f, cy - h * 0.62f), strokeWidth = 3f)
}

private fun DrawScope.drawEqualizerEye(cx: Float, cy: Float, w: Float, h: Float, color: Color, phase: Float) {
    val barCount = 4
    val barW = w / (barCount * 1.5f)
    val startX = cx - w * 0.5f

    for (i in 0 until barCount) {
        val barH = h * (0.35f + 0.65f * kotlin.math.abs(sin((phase * 6.28f + i * 1.2f).toDouble())).toFloat())
        val x = startX + i * (barW * 1.5f)
        drawRoundRect(
            color = color,
            topLeft = Offset(x, cy + h * 0.5f - barH),
            size = Size(barW, barH),
            cornerRadius = CornerRadius(6f, 6f)
        )
    }
}

private fun DrawScope.drawWinkEyeSlit(cx: Float, cy: Float, w: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx - w * 0.5f, cy + 4f)
        lineTo(cx, cy - 8f)
        lineTo(cx + w * 0.5f, cy + 4f)
    }
    drawPath(path = path, color = color, style = Stroke(width = 9f, cap = StrokeCap.Round))
}

private fun DrawScope.drawSurprisedEye(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val radius = w * 0.52f
    drawCircle(color = color.copy(alpha = 0.3f), radius = radius * 1.25f, center = Offset(cx, cy))
    drawCircle(color = color, radius = radius, center = Offset(cx, cy), style = Stroke(width = 9f))
    drawCircle(color = color, radius = radius * 0.45f, center = Offset(cx, cy))
    drawCircle(color = Color.White, radius = radius * 0.2f, center = Offset(cx + radius * 0.2f, cy - radius * 0.2f))
}

private fun DrawScope.drawSadEye(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx - w * 0.5f, cy - h * 0.2f)
        lineTo(cx + w * 0.5f, cy + h * 0.2f)
        lineTo(cx + w * 0.4f, cy + h * 0.5f)
        lineTo(cx - w * 0.4f, cy + h * 0.5f)
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawAngryEye(cx: Float, cy: Float, w: Float, h: Float, color: Color, isLeft: Boolean) {
    val path = Path().apply {
        if (isLeft) {
            moveTo(cx - w * 0.5f, cy - h * 0.45f)
            lineTo(cx + w * 0.5f, cy + h * 0.1f)
            lineTo(cx + w * 0.4f, cy + h * 0.5f)
            lineTo(cx - w * 0.5f, cy + h * 0.4f)
        } else {
            moveTo(cx + w * 0.5f, cy - h * 0.45f)
            lineTo(cx - w * 0.5f, cy + h * 0.1f)
            lineTo(cx - w * 0.4f, cy + h * 0.5f)
            lineTo(cx + w * 0.5f, cy + h * 0.4f)
        }
        close()
    }
    // Fiery outer glow
    drawPath(path = path, color = color.copy(alpha = 0.35f), style = Stroke(width = 14f, cap = StrokeCap.Round))
    drawPath(path = path, color = color)

    // Angry cyber slanted brow slashing down
    if (isLeft) {
        drawLine(
            color = Color(0xFFFFF0B3),
            start = Offset(cx - w * 0.65f, cy - h * 0.6f),
            end = Offset(cx + w * 0.55f, cy - h * 0.1f),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
    } else {
        drawLine(
            color = Color(0xFFFFF0B3),
            start = Offset(cx + w * 0.65f, cy - h * 0.6f),
            end = Offset(cx - w * 0.55f, cy - h * 0.1f),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawSleepyEye(cx: Float, cy: Float, w: Float, color: Color) {
    drawLine(
        color = color,
        start = Offset(cx - w * 0.48f, cy + 5f),
        end = Offset(cx + w * 0.48f, cy + 5f),
        strokeWidth = 8f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawThinkingScanner(screenRect: ScreenRect, color: Color, phase: Float) {
    val scanX = screenRect.left + 20f + (screenRect.width - 40f) * phase
    drawLine(
        brush = Brush.verticalGradient(
            listOf(Color.Transparent, color, Color.Transparent),
            startY = screenRect.top,
            endY = screenRect.top + screenRect.height
        ),
        start = Offset(scanX, screenRect.top + 10f),
        end = Offset(scanX, screenRect.top + screenRect.height - 10f),
        strokeWidth = 6f
    )
    // Small glowing radar rings in center
    val centerX = screenRect.left + screenRect.width * 0.5f
    val centerY = screenRect.top + screenRect.height * 0.5f
    drawCircle(color = color.copy(alpha = 0.4f), radius = 24f * phase + 8f, center = Offset(centerX, centerY), style = Stroke(width = 3f))
    drawCircle(color = color, radius = 6f, center = Offset(centerX, centerY))
}

private fun DrawScope.drawFlirtyEye(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Seductive half-lidded eyes
    val path = Path().apply {
        moveTo(cx - w * 0.5f, cy + 2f)
        cubicTo(
            cx - w * 0.25f, cy - h * 0.35f,
            cx + w * 0.25f, cy - h * 0.35f,
            cx + w * 0.5f, cy + 2f
        )
        lineTo(cx + w * 0.45f, cy + h * 0.4f)
        lineTo(cx - w * 0.45f, cy + h * 0.4f)
        close()
    }
    drawPath(path = path, color = color)
    drawCircle(color = Color.White, radius = 4f, center = Offset(cx + w * 0.15f, cy))
}

private fun DrawScope.drawBlush(cx: Float, cy: Float, w: Float) {
    val blushW = w * 0.45f
    drawLine(
        color = Color(0xFFFF2A85).copy(alpha = 0.8f),
        start = Offset(cx - blushW * 0.5f, cy),
        end = Offset(cx + blushW * 0.5f, cy),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFFFF2A85).copy(alpha = 0.8f),
        start = Offset(cx - blushW * 0.35f, cy + 8f),
        end = Offset(cx + blushW * 0.35f, cy + 8f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawIdeaLightbulb(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val radius = w * 0.35f
    drawCircle(color = color, radius = radius, center = Offset(cx, cy - h * 0.1f))
    drawRect(color = color, topLeft = Offset(cx - radius * 0.5f, cy + radius * 0.5f), size = Size(radius, h * 0.25f))
    drawCircle(color = Color.White, radius = 4f, center = Offset(cx, cy - h * 0.1f))
}

private fun DrawScope.drawChargingFace(screenRect: ScreenRect, color: Color, pulseScale: Float) {
    val cx = screenRect.left + screenRect.width * 0.5f
    val cy = screenRect.top + screenRect.height * 0.5f

    // Battery outline
    val batW = screenRect.width * 0.55f
    val batH = screenRect.height * 0.45f
    drawRoundRect(
        color = color,
        topLeft = Offset(cx - batW * 0.5f, cy - batH * 0.5f),
        size = Size(batW, batH),
        cornerRadius = CornerRadius(14f, 14f),
        style = Stroke(width = 6f)
    )

    // Lightning bolt in center
    val boltPath = Path().apply {
        moveTo(cx + 4f, cy - batH * 0.35f)
        lineTo(cx - 16f, cy + 2f)
        lineTo(cx - 2f, cy + 2f)
        lineTo(cx - 6f, cy + batH * 0.35f)
        lineTo(cx + 14f, cy - 2f)
        lineTo(cx + 2f, cy - 2f)
        close()
    }
    drawPath(path = boltPath, color = Color(0xFF10B981).copy(alpha = (pulseScale - 0.1f).coerceIn(0.6f, 1f)))
}
