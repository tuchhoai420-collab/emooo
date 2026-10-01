package com.example.data.model

data class PetStats(
    val battery: Int = 85,          // 0 to 100
    val happiness: Int = 92,        // 0 to 100
    val affection: Int = 45,        // 0 to 100
    val level: Int = 3,             // Level based on XP/interaction
    val totalInteractions: Int = 18,
    val currentMood: EmoEmotion = EmoEmotion.HAPPY,
    val eyeColorHex: Long = 0xFF00F5FF, // Default Cyan
    val headphoneColorHex: Long = 0xFF8B5CF6, // Default Purple
    val voicePitch: Float = 1.35f,
    val voiceSpeed: Float = 1.05f,
    val soundEnabled: Boolean = true,
    val isCharging: Boolean = false
) {
    val bondTitle: String
        get() = when {
            level >= 10 -> "Cómplice Inseparable 💎"
            level >= 7 -> "Mejor Amigo Íntimo 🌟"
            level >= 5 -> "Compañero Confiable 🚀"
            level >= 3 -> "Amigo Cercano ✨"
            else -> "Nuevo Colega 🤖"
        }
}
