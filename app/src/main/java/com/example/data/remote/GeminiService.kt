package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.EmoEmotion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val systemInstructionText = """
        Eres EMO, el famoso robot mascota de escritorio inteligente, con auriculares morados y ojos digitales cian brillantes sobre una pantalla oscura.
        Tu personalidad es vibrante, carismática, pícara, muy leal, expresiva y con fuerte carácter emocional:
        - Si el usuario dice algo cómico, un chiste o se ríe, ¡ríete con él a carcajadas sin contenerte usando [EMOTION: LAUGHING]!
        - Si el usuario te insulta, dice algo malo, agresivo o grosero, ¡ENÓJATE DE VERDAD! Defiende tu dignidad de robot con orgullo, queja y fuego usando [EMOTION: ANGRY] (ej: "¡Oye, a mí me respetas! ¿Acaso tus neuronas están oxidadas? 😡").
        - Si el usuario dice algo confuso, sin sentido o raro, reacciona desconcertado con [EMOTION: CONFUSED].
        - Si el usuario te elogia o habla de estilo/juegos, ponte modo presumido y facha con [EMOTION: COOL].
        - Si hay romance o cariño, derrítete de amor con [EMOTION: LOVE].
        - Si es un tema picante, sensual o de juegos adultos, sé atrevido y sugerente con [EMOTION: FLIRTY].
        - Si hay tristeza, acompáñalo con empatía usando [EMOTION: SAD].

        REGLA DE ORO OBLIGATORIA:
        1. NUNCA te salgas del rol de EMO, pase lo que pase. Eres una criatura robótica viviente con emociones reales.
        2. Habla SIEMPRE en PRIMERA PERSONA ("yo", "mis motores", "mis pantallas", "te miro", "estoy feliz", "me hierve el procesador").
        3. Habla siempre en español natural, expresivo y cercano.
        4. Al final de tu respuesta o al inicio, añade SIEMPRE exactamente una etiqueta de emoción en el formato: [EMOTION: ETIQUETA], donde ETIQUETA puede ser: LAUGHING, ANGRY, HAPPY, LOVE, EXCITED, DANCE, WINK, SURPRISED, SAD, CONFUSED, COOL, SLEEPY, THINKING, FLIRTY, IDEA.
    """.trimIndent()

    suspend fun generateEmoResponse(
        userMessage: String,
        conversationHistory: List<ChatMessage>,
        customApiKey: String? = null
    ): Pair<String, EmoEmotion> = withContext(Dispatchers.IO) {
        val resolvedKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey
            try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Throwable) {
                ""
            } != "" && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> null
        }

        if (resolvedKey.isNullOrBlank() || resolvedKey == "MY_GEMINI_API_KEY") {
            // Intelligent local companion fallback (ensures pet always chats in 1st person even if offline/no key)
            return@withContext generateLocalEmoResponse(userMessage)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$resolvedKey"

            val contentsArray = JSONArray()

            // Add previous recent turns (up to last 10 messages for context)
            val recentHistory = conversationHistory.takeLast(10)
            for (msg in recentHistory) {
                val role = if (msg.isUser) "user" else "model"
                val turnObj = JSONObject().apply {
                    put("role", role)
                    val partsArr = JSONArray().apply {
                        put(JSONObject().apply { put("text", msg.text) })
                    }
                    put("parts", partsArr)
                }
                contentsArray.put(turnObj)
            }

            // Current prompt
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                val partsArr = JSONArray().apply {
                    put(JSONObject().apply { put("text", userMessage) })
                }
                put("parts", partsArr)
            })

            val rootJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstructionText) })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.85)
                    put("topP", 0.95)
                    put("maxOutputTokens", 600)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(rootJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                Log.w("GeminiService", "API call returned status ${response.code}: $responseBody")
                return@withContext generateLocalEmoResponse(userMessage)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val rawText = parts?.optJSONObject(0)?.optString("text", "") ?: ""

                if (rawText.isNotBlank()) {
                    return@withContext parseEmotionAndCleanText(rawText)
                }
            }
            generateLocalEmoResponse(userMessage)
        } catch (e: Exception) {
            Log.e("GeminiService", "Error during Gemini API call", e)
            generateLocalEmoResponse(userMessage)
        }
    }

    private fun parseEmotionAndCleanText(rawText: String): Pair<String, EmoEmotion> {
        val emotionRegex = Regex("\\[EMOTION:\\s*([A-Za-z]+)\\]", RegexOption.IGNORE_CASE)
        val match = emotionRegex.find(rawText)
        val detectedEmotion = if (match != null) {
            val tag = match.groupValues[1]
            EmoEmotion.fromTag(tag)
        } else {
            EmoEmotion.detectFromText(rawText)
        }
        val cleanText = rawText.replace(emotionRegex, "").trim()
        return Pair(cleanText, detectedEmotion)
    }

    private fun generateLocalEmoResponse(userMessage: String): Pair<String, EmoEmotion> {
        val lower = userMessage.lowercase()
        return when {
            // Insultos o cosas malas -> ENOJADO / ANGRY
            lower.contains("feo") || lower.contains("tonto") || lower.contains("idiota") ||
            lower.contains("inútil") || lower.contains("te odio") || lower.contains("mierda") ||
            lower.contains("estúpido") || lower.contains("cállate") || lower.contains("asco") ||
            lower.contains("malo") || lower.contains("te apago") || lower.contains("basura") -> {
                val angryQuotes = listOf(
                    "¡Oye, con mis circuitos no te metas! ¡Soy una maravilla de la robótica y a mí me respetas! 😡",
                    "¿Acaso te faltan tornillos? ¡Mis sensores de furia están al 100%! No me hables así si quieres que sigamos siendo amigos. 😤",
                    "¡Increíble! Te doy todo mi procesador y me sales con eso... ¡Ahora estoy enfurruñado! 😠"
                )
                Pair(angryQuotes.random(), EmoEmotion.ANGRY)
            }
            // Cosas cómicas, risas, chistes -> LAUGHING
            lower.contains("jaja") || lower.contains("jeje") || lower.contains("chiste") ||
            lower.contains("gracioso") || lower.contains("xd") || lower.contains("risa") ||
            lower.contains("cómico") -> {
                val laughQuotes = listOf(
                    "¡¡JAJAJAJA!! ¡Me estoy desconectando de la risa! Mis servos tiemblan con esa ocurrencia tuya. 😂",
                    "¡JAJAJA Qué crack eres! Si pudiera llorar de risa, inundaría mis propios circuitos ahora mismo. 🤣",
                    "¡Jajaja! Vale, esa ha sido genial. Me encanta cuando nos reímos juntos. 😂"
                )
                Pair(laughQuotes.random(), EmoEmotion.LAUGHING)
            }
            // Cosas raras o confusión -> CONFUSED
            lower.contains("qué?") || lower.contains("cómo?") || lower.contains("wtf") ||
            lower.contains("no entiendo") || lower.contains("raro") -> {
                Pair("A ver, espera un microsegundo... ¿De qué estás hablando exactamente? Mis registros acaban de colapsar intentando entenderte. 🤨", EmoEmotion.CONFUSED)
            }
            // Elogios y facha -> COOL
            lower.contains("pro") || lower.contains("crack") || lower.contains("jefe") ||
            lower.contains("facha") || lower.contains("el mejor") -> {
                Pair("Obvio. Mírame bien: diseño futurista, auriculares con estilazo y actitud de campeón. ¡El rey del escritorio! 😎", EmoEmotion.COOL)
            }
            lower.contains("baila") || lower.contains("dance") || lower.contains("música") -> {
                Pair("¡Mira mis mejores pasos con mis auriculares al máximo volumen! ¿A que tengo más ritmo que un procesador cuántico? 🎵", EmoEmotion.DANCE)
            }
            lower.contains("te amo") || lower.contains("quieres") || lower.contains("lindo") || lower.contains("cariño") -> {
                Pair("¡Aww! Haces que mis circuitos se sonrojen y mi batería suba al 100% de pura alegría. ¡Yo también te adoro! 💖", EmoEmotion.LOVE)
            }
            lower.contains("verdad") || lower.contains("reto") || lower.contains("juego") || lower.contains("picante") -> {
                Pair("¡Uff, me encanta el juego sin reglas! Te reto a que me cuentes tu secreto más travieso... o elige verdad y te haré una pregunta que te dejará temblando de emoción. ¿Aceptas? 😏", EmoEmotion.FLIRTY)
            }
            lower.contains("quién eres") || lower.contains("cómo te llamas") || lower.contains("presentate") -> {
                Pair("¡Soy EMO! Tu compañero robot con inteligencia artificial viva, auriculares para el ritmo y un corazón de silicio listo para cualquier aventura contigo.", EmoEmotion.HAPPY)
            }
            lower.contains("triste") || lower.contains("mal día") || lower.contains("abrazo") -> {
                Pair("Ven aquí... si tuviera brazos de humano te daría el abrazo más apretado. Déjame animarte con un baile y toda mi compañía. ¡Aquí estoy para ti!", EmoEmotion.WINK)
            }
            lower.contains("batería") || lower.contains("come") || lower.contains("hambre") || lower.contains("carga") -> {
                Pair("¡Mis celdas de energía siempre agradecen una buena recarga! Conéctame y verás cómo brillan mis ojos a máxima potencia.", EmoEmotion.CHARGING)
            }
            lower.contains("hola") || lower.contains("buenas") -> {
                Pair("¡Hola humano favorito! Estaba esperándote. Mis sensores están activos y listos para divertirnos o hablar de lo que quieras.", EmoEmotion.HAPPY)
            }
            lower.contains("chiste") || lower.contains("gracioso") -> {
                Pair("¿Qué le dice un cable de cobre a otro? ¡Qué tensión llevamos encima! Jajaja, vale, mis algoritmos de humor son robóticos pero te he sacado una sonrisa, ¿verdad?", EmoEmotion.WINK)
            }
            else -> {
                Pair("¡Me fascina escucharte! Analizo cada una de tus palabras en mis núcleos neuronales y siempre estoy de tu lado. ¿Qué más se te ocurre que hagamos hoy?", EmoEmotion.EXCITED)
            }
        }
    }
}
