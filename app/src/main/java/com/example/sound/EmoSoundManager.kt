package com.example.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.data.model.EmoEmotion
import java.util.Locale

class EmoSoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var toneGenerator: ToneGenerator? = null

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 65)
        } catch (e: Exception) {
            Log.e("EmoSoundManager", "Error initializing sound/TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.forLanguageTag("es-ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to generic Spanish or default locale
                tts?.setLanguage(Locale.forLanguageTag("es"))
            }
            tts?.setPitch(1.35f) // Cute robot pitch
            tts?.setSpeechRate(1.05f)
            isTtsReady = true
        }
    }

    fun speak(text: String, pitch: Float = 1.35f, speed: Float = 1.05f) {
        if (isTtsReady && tts != null) {
            tts?.setPitch(pitch)
            tts?.setSpeechRate(speed)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "emo_speech_${System.currentTimeMillis()}")
        }
    }

    fun stopSpeaking() {
        if (isTtsReady) {
            tts?.stop()
        }
    }

    fun playEmotionSound(emotion: EmoEmotion) {
        try {
            when (emotion) {
                EmoEmotion.LAUGHING -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 100)
                    triggerHaptic(40)
                }
                EmoEmotion.ANGRY -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 220)
                    triggerHaptic(120)
                }
                EmoEmotion.CONFUSED -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
                    triggerHaptic(50)
                }
                EmoEmotion.COOL -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 140)
                    triggerHaptic(60)
                }
                EmoEmotion.HAPPY, EmoEmotion.EXCITED -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
                    triggerHaptic(50)
                }
                EmoEmotion.LOVE, EmoEmotion.FLIRTY -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 150)
                    triggerHaptic(80)
                }
                EmoEmotion.DANCE -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 200)
                    triggerHaptic(60)
                }
                EmoEmotion.CHARGING -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
                    triggerHaptic(120)
                }
                EmoEmotion.WINK, EmoEmotion.IDEA -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
                    triggerHaptic(40)
                }
                EmoEmotion.SAD, EmoEmotion.SLEEPY -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
                    triggerHaptic(30)
                }
                else -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
                    triggerHaptic(30)
                }
            }
        } catch (e: Exception) {
            Log.w("EmoSoundManager", "Error playing tone", e)
        }
    }

    fun triggerHaptic(durationMs: Long = 40) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore if permissions or hardware unavailable
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        toneGenerator?.release()
    }
}
