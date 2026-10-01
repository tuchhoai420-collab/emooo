package com.example.data.model

data class ChatMessage(
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val emotion: EmoEmotion = EmoEmotion.HAPPY,
    val timestamp: Long = System.currentTimeMillis()
)
