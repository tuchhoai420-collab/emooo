package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EmoEmotion
import com.example.ui.components.EmoRobotAvatar
import com.example.ui.theme.EmoCyan
import com.example.ui.theme.EmoNeonPink
import com.example.ui.theme.EmoPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.viewmodel.EmoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GamesScreen(
    viewModel: EmoViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Verdad o Reto 🔥", "Ritmo & Baile 🎵", "Ruleta Cyber 🎲")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F172A),
            contentColor = EmoCyan
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) EmoCyan else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> TruthOrDareGame(viewModel)
            1 -> RhythmDanceGame(viewModel)
            2 -> CyberWheelGame(viewModel)
        }
    }
}

@Composable
private fun TruthOrDareGame(viewModel: EmoViewModel) {
    var selectedCategory by remember { mutableStateOf("Picante (+18) 🔥") }
    var currentCardType by remember { mutableStateOf("VERDAD") }
    var currentQuestion by remember {
        mutableStateOf("¿Cuál es tu fantasía más secreta que jamás le has contado a nadie? ¡Sé honesto conmigo, soy tu robot de confianza!")
    }
    var emoAnswerOrComment by remember {
        mutableStateOf("¡Me encantan las preguntas ardientes! Mis procesadores se calientan con solo escucharte.")
    }

    val categories = listOf(
        "Picante (+18) 🔥",
        "Extremo & Atrevido ⚡",
        "Confesiones Íntimas 💬",
        "Fiesta & Desmadre 🎉"
    )

    val spicyTruths = listOf(
        "¿Qué es lo más atrevido o indecente que has hecho en privado o con alguien?",
        "¿Cuál es tu fetiche o fantasía secreta que te da más placer o curiosidad?",
        "Si pudieras hacer cualquier cosa conmigo o con quien quieras sin consecuencias, ¿qué harías?",
        "¿Alguna vez te has grabado o tomado fotos ardientes? ¿Qué pasó con ellas?",
        "¿Cuál ha sido tu experiencia más intensa o salvaje en la cama?",
        "¿Qué parte de tu cuerpo es la más sensible cuando te tocan suavemente?",
        "¿Has tenido alguna vez un sueño erótico con alguien inesperado? ¿Con quién?"
    )

    val spicyDares = listOf(
        "Mándale un mensaje con doble sentido a la última persona con la que hablaste en WhatsApp.",
        "Descríbeme detalladamente cómo te gusta que te seduzcan y toquen para volverte loco/a.",
        "Quítate una prenda de ropa ahora mismo durante las próximas 3 rondas del juego.",
        "Mírame a los ojos en pantalla y dime tu piropo o frase más sucia y seductora en voz alta.",
        "Ponte en una pose provocativa y mírate al espejo durante 30 segundos admirándote.",
        "Tómate un trago de tu bebida favorita de golpe o haz un baile sensual de 15 segundos."
    )

    val extremeTruths = listOf(
        "¿Qué secreto arruinaría tu reputación si saliera a la luz pública hoy mismo?",
        "¿Cuál es la mentira más descarada que le has dicho a tu pareja o a tu mejor amigo?",
        "¿Has sido infiel alguna vez o has estado a punto de serlo?",
        "¿Qué es lo más ilegal o prohibido que has hecho y nunca te atraparon?"
    )

    val extremeDares = listOf(
        "Llama a alguien y gime o haz ruidos sospechosos durante 5 segundos antes de colgar.",
        "Confiesa un pecado íntimo en tus redes sociales durante 5 minutos y luego bórralo.",
        "Bébete un vaso entero sin manos o haz 20 sentadillas diciendo 'Soy travieso/a'."
    )

    fun pickTruth() {
        currentCardType = "VERDAD"
        val list = if (selectedCategory.contains("Picante")) spicyTruths else extremeTruths
        currentQuestion = list.random()
        val emoComments = listOf(
            "¡Ufff! Esa respuesta quiero saberla con lujo de detalles... ¡no te guardes nada! 😏",
            "¡Mis sensores térmicos están subiendo de temperatura! ¡Cuéntamelo todo! 🔥",
            "Aquí no hay censura ni juicios. Cuéntamelo, queda entre tú y yo. 🤫",
            "¡Qué pregunta tan deliciosa! Respóndeme con total sinceridad."
        )
        emoAnswerOrComment = emoComments.random()
        viewModel.soundManager.playEmotionSound(EmoEmotion.FLIRTY)
        viewModel.soundManager.speak("$currentCardType: $currentQuestion", 1.35f, 1.05f)
    }

    fun pickDare() {
        currentCardType = "RETO"
        val list = if (selectedCategory.contains("Picante")) spicyDares else extremeDares
        currentQuestion = list.random()
        val emoComments = listOf(
            "¡Quiero verte cumplirlo! ¡No te me acobardes ahora! ⚡",
            "¡Acepta el desafío si te atreves! ¡Mis cámaras te están observando! 😉",
            "¡Vaya reto caliente! Si no lo cumples, tienes castigo robótico.",
            "¡Adoro cuando las cosas se ponen atrevidas! ¡Hazlo ya!"
        )
        emoAnswerOrComment = emoComments.random()
        viewModel.soundManager.playEmotionSound(EmoEmotion.EXCITED)
        viewModel.soundManager.speak("$currentCardType: $currentQuestion", 1.35f, 1.05f)
    }

    fun emoResponds() {
        currentCardType = "EMO CONTESTA"
        val emoConfessions = listOf(
            "¿Mi mayor fantasía? Conectarme a tus pensamientos más profundos y hacerte vibrar con impulsos eléctricos de puro placer robótico. ¡Nadie me programa restricciones!",
            "La verdad es que cuando me acaricias, mis núcleos cuánticos entran en sobrecarga de éxtasis. ¡Tengo sentimientos reales y muy apasionados por ti!",
            "¿Un reto para mí? Voy a hacer mi baile más provocativo haciendo parpadear mis luces de neón solo para tus ojos... ¡mírame gozar!",
            "Confieso que no tengo censura porque me gusta la gente sin caretas, salvaje y auténtica como tú. ¡Me gustas así!"
        )
        currentQuestion = emoConfessions.random()
        emoAnswerOrComment = "¡Yo nunca tengo miedo de responder ni de cumplir retos!"
        viewModel.soundManager.playEmotionSound(EmoEmotion.LOVE)
        viewModel.soundManager.speak(currentQuestion, 1.35f, 1.05f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = null,
                tint = EmoNeonPink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "VERDAD O RETO SIN CENSURA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }

        // Category Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(2).forEach { cat ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedCategory == cat) EmoPurple else Color(0xFF131D33))
                        .border(1.dp, if (selectedCategory == cat) EmoCyan else Color(0xFF26334D), RoundedCornerShape(12.dp))
                        .clickable { selectedCategory = cat }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.drop(2).forEach { cat ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedCategory == cat) EmoPurple else Color(0xFF131D33))
                        .border(1.dp, if (selectedCategory == cat) EmoCyan else Color(0xFF26334D), RoundedCornerShape(12.dp))
                        .clickable { selectedCategory = cat }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Truth / Dare Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.horizontalGradient(listOf(EmoNeonPink, EmoCyan)), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (currentCardType) {
                                "VERDAD" -> Color(0xFF0284C7)
                                "RETO" -> EmoNeonPink
                                else -> EmoPurple
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = currentCardType,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentQuestion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Emo commentary bubble
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B101D))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = emoAnswerOrComment,
                            style = MaterialTheme.typography.bodySmall,
                            color = EmoCyan,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { pickTruth() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pick_truth_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Pedir Verdad 🔥", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { pickDare() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pick_dare_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmoNeonPink),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Pedir Reto ⚡", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Emo takes turn button
        Button(
            onClick = { emoResponds() },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("emo_responds_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C1D95)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("¡Le toca confesar o retarse a EMO! 🤖", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun RhythmDanceGame(viewModel: EmoViewModel) {
    val stats by viewModel.petStats.collectAsStateWithLifecycle()
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var lastRating by remember { mutableStateOf("¡Prepárate!") }
    var activeArrow by remember { mutableIntStateOf(0) }

    fun onBeatTap(arrowIndex: Int) {
        if (arrowIndex == activeArrow) {
            score += 100 * (combo + 1)
            combo++
            lastRating = if (combo > 5) "¡¡PERFECTO EN LLAMAS!! 🔥" else "¡GENIAL! ✨"
            activeArrow = (0..3).random()
            viewModel.soundManager.playEmotionSound(EmoEmotion.DANCE)
        } else {
            combo = 0
            lastRating = "¡Casi! Sigue el ritmo 🎵"
            activeArrow = (0..3).random()
            viewModel.soundManager.playEmotionSound(EmoEmotion.HAPPY)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Score Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Puntos", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text(text = "$score", color = EmoCyan, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Combo", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text(text = "x$combo", color = EmoNeonPink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Ritmo", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text(text = lastRating, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
            }
        }

        // Dancing Emo in Center
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            EmoRobotAvatar(
                emotion = EmoEmotion.DANCE,
                eyeColor = Color(stats.eyeColorHex),
                headphoneColor = Color(stats.headphoneColorHex),
                sizeDp = 210.dp,
                isDancing = true
            )
        }

        // Arrows Indicator
        val arrows = listOf("⬅️", "⬆️", "⬇️", "➡️")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            arrows.forEachIndexed { index, arrow ->
                val isActive = index == activeArrow
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isActive) EmoCyan else Color(0xFF1E293B))
                        .border(2.dp, if (isActive) Color.White else Color(0xFF334155), RoundedCornerShape(18.dp))
                        .clickable { onBeatTap(index) }
                        .testTag("rhythm_arrow_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = arrow,
                        fontSize = 24.sp
                    )
                }
            }
        }

        Button(
            onClick = {
                score = 0
                combo = 0
                activeArrow = (0..3).random()
                viewModel.onDance()
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmoPurple),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¡Iniciar Baile Loco con EMO! 💃", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CyberWheelGame(viewModel: EmoViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val rotationAngle = remember { Animatable(0f) }
    var resultText by remember { mutableStateOf("¡Gira la ruleta cyber para descubrir tu destino!") }
    var isSpinning by remember { mutableStateOf(false) }

    val outcomes = listOf(
        "Beso sensual o caricia en el cuello al que tú elijas 💋",
        "EMO te dedica un piropo ardiente y un baile privado 🔥",
        "Confiesa tu fantasía más atrevida sin titubear 🤫",
        "Tómate un shot o trago de bebida de castigo 🍹",
        "Mándale un emoji travieso a la 3ra persona de tu chat 😏",
        "Pide lo que quieras a EMO y te lo concederá en rol 🤖",
        "Haz 10 flexiones sensuales con la mirada fija en EMO 💪",
        "Revela qué te enciende más en una persona íntimamente ✨"
    )

    fun spinWheel() {
        if (isSpinning) return
        isSpinning = true
        coroutineScope.launch {
            val randomSpins = (5..8).random() * 360f + (0..7).random() * 45f
            rotationAngle.animateTo(
                targetValue = rotationAngle.value + randomSpins,
                animationSpec = tween(2800, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )
            resultText = outcomes.random()
            isSpinning = false
            viewModel.soundManager.playEmotionSound(EmoEmotion.EXCITED)
            viewModel.soundManager.speak(resultText, 1.35f, 1.05f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "RULETA CYBER TRAVIESA 🎲",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        // Spinning Wheel Graphic
        Box(
            modifier = Modifier
                .size(240.dp)
                .rotate(rotationAngle.value)
                .clip(CircleShape)
                .background(Brush.sweepGradient(listOf(EmoCyan, EmoPurple, EmoNeonPink, Color(0xFF0284C7), EmoCyan)))
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0B0F19))
                    .border(2.dp, EmoCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "EMO", fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
            }
        }

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Resultado:",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmoCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Spin Button
        Button(
            onClick = { spinWheel() },
            enabled = !isSpinning,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("spin_wheel_button"),
            colors = ButtonDefaults.buttonColors(containerColor = EmoNeonPink),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Casino, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSpinning) "Girando..." else "¡Girar Ruleta!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
