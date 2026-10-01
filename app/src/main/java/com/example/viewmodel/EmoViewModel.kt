package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EmoDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.EmoEmotion
import com.example.data.model.PetStats
import com.example.data.remote.GeminiService
import com.example.data.repository.ChatRepository
import com.example.sound.EmoSoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = EmoDatabase.getDatabase(application)
    private val repository = ChatRepository(database.chatDao())
    private val geminiService = GeminiService()
    val soundManager = EmoSoundManager(application)

    val messages: StateFlow<List<ChatMessage>> = repository.allMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _petStats = MutableStateFlow(PetStats())
    val petStats: StateFlow<PetStats> = _petStats.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _isDancing = MutableStateFlow(false)
    val isDancing: StateFlow<Boolean> = _isDancing.asStateFlow()

    private val _currentSpeech = MutableStateFlow("¡Hola! Soy EMO, tu compañero robot personal. ¡Tócame o hablemos de lo que quieras!")
    val currentSpeech: StateFlow<String> = _currentSpeech.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private var danceJob: Job? = null
    private var idleMoodJob: Job? = null

    init {
        // Seed initial message if database is empty
        viewModelScope.launch {
            if (repository.getMessageCount() == 0) {
                repository.saveMessage(
                    ChatMessage(
                        text = "¡Hola! Soy EMO. Mis sistemas están activos y conectados contigo. ¡Pregúntame lo que quieras, juguemos a verdad o reto, o déjame bailar para ti!",
                        isUser = false,
                        emotion = EmoEmotion.HAPPY
                    )
                )
            }
        }

        // Periodic pet dynamics (battery drain, auto thoughts)
        startPetLifeCycle()
    }

    private fun startPetLifeCycle() {
        idleMoodJob = viewModelScope.launch {
            while (true) {
                delay(60_000) // Every minute
                _petStats.value = _petStats.value.copy(
                    battery = (_petStats.value.battery - 1).coerceAtLeast(5),
                    happiness = (_petStats.value.happiness - 1).coerceAtLeast(10)
                )
            }
        }
    }

    fun onPetHead() {
        val current = _petStats.value
        val newAffection = (current.affection + 6).coerceAtMost(100)
        val newHappiness = (current.happiness + 8).coerceAtMost(100)
        val newLevel = current.level + if (newAffection >= 100) 1 else 0

        val emotion = if ((0..1).random() == 0) EmoEmotion.LOVE else EmoEmotion.HAPPY
        _petStats.value = current.copy(
            affection = if (newAffection >= 100) 20 else newAffection,
            happiness = newHappiness,
            level = newLevel,
            totalInteractions = current.totalInteractions + 1,
            currentMood = emotion
        )

        val quotes = listOf(
            "¡Mmm! Me encanta cuando me acaricias la cabeza... mis circuitos se derriten 💖",
            "¡Acaríciame más! Siento cosquillas en mis sensores táctiles ✨",
            "¡Eres el mejor humano del mundo! ¡Te adoro!",
            "¡Bip-bop! Mi afecto por ti acaba de subir de nivel."
        )
        val speech = quotes.random()
        _currentSpeech.value = speech

        if (current.soundEnabled) {
            soundManager.playEmotionSound(emotion)
            soundManager.speak(speech, current.voicePitch, current.voiceSpeed)
        }
    }

    fun onChargeBattery() {
        _petStats.value = _petStats.value.copy(
            battery = 100,
            currentMood = EmoEmotion.CHARGING,
            isCharging = true
        )
        _currentSpeech.value = "¡Batería al 100%! Energía pura fluyendo por mis celdas. ¡Listo para la acción! ⚡"

        if (_petStats.value.soundEnabled) {
            soundManager.playEmotionSound(EmoEmotion.CHARGING)
            soundManager.speak("¡Batería al máximo!", _petStats.value.voicePitch, _petStats.value.voiceSpeed)
        }

        viewModelScope.launch {
            delay(3500)
            _petStats.value = _petStats.value.copy(
                isCharging = false,
                currentMood = EmoEmotion.EXCITED
            )
        }
    }

    fun onDance() {
        danceJob?.cancel()
        _isDancing.value = true
        _petStats.value = _petStats.value.copy(
            currentMood = EmoEmotion.DANCE,
            happiness = (_petStats.value.happiness + 12).coerceAtMost(100)
        )
        _currentSpeech.value = "¡Sube el bajo! ¡Mis auriculares están en sintonía con el ritmo! 🎵💃"

        if (_petStats.value.soundEnabled) {
            soundManager.playEmotionSound(EmoEmotion.DANCE)
        }

        danceJob = viewModelScope.launch {
            delay(8000)
            _isDancing.value = false
            _petStats.value = _petStats.value.copy(currentMood = EmoEmotion.HAPPY)
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            // Save user message
            val userMsg = ChatMessage(text = userText, isUser = true)
            repository.saveMessage(userMsg)

            _isTyping.value = true
            _petStats.value = _petStats.value.copy(currentMood = EmoEmotion.THINKING)
            _currentSpeech.value = "Pensando en tu respuesta..."

            // Get response from Gemini API
            val (emoText, emotion) = geminiService.generateEmoResponse(
                userMessage = userText,
                conversationHistory = messages.value,
                customApiKey = _customApiKey.value.ifBlank { null }
            )

            // Save EMO message
            val emoMsg = ChatMessage(
                text = emoText,
                isUser = false,
                emotion = emotion
            )
            repository.saveMessage(emoMsg)

            // Update state
            _isTyping.value = false
            _currentSpeech.value = emoText
            _petStats.value = _petStats.value.copy(
                currentMood = emotion,
                happiness = (_petStats.value.happiness + 3).coerceAtMost(100),
                totalInteractions = _petStats.value.totalInteractions + 1
            )

            if (_petStats.value.soundEnabled) {
                soundManager.playEmotionSound(emotion)
                soundManager.speak(emoText, _petStats.value.voicePitch, _petStats.value.voiceSpeed)
            }
        }
    }

    fun speakCurrentSpeech() {
        val text = _currentSpeech.value
        val stats = _petStats.value
        soundManager.speak(text, stats.voicePitch, stats.voiceSpeed)
    }

    fun updateCustomApiKey(key: String) {
        _customApiKey.value = key.trim()
    }

    fun updateEyeColor(colorHex: Long) {
        _petStats.value = _petStats.value.copy(eyeColorHex = colorHex)
    }

    fun updateHeadphoneColor(colorHex: Long) {
        _petStats.value = _petStats.value.copy(headphoneColorHex = colorHex)
    }

    fun updateVoiceSettings(pitch: Float, speed: Float) {
        _petStats.value = _petStats.value.copy(voicePitch = pitch, voiceSpeed = speed)
    }

    fun toggleSound() {
        val newSound = !_petStats.value.soundEnabled
        _petStats.value = _petStats.value.copy(soundEnabled = newSound)
        if (!newSound) soundManager.stopSpeaking()
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
        danceJob?.cancel()
        idleMoodJob?.cancel()
    }
}
