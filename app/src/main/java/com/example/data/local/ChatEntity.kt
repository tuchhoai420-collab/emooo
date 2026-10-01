package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ChatMessage
import com.example.data.model.EmoEmotion

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val emotionName: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toChatMessage(): ChatMessage {
        val emotion = try {
            EmoEmotion.valueOf(emotionName)
        } catch (e: Exception) {
            EmoEmotion.HAPPY
        }
        return ChatMessage(
            id = id,
            text = text,
            isUser = isUser,
            emotion = emotion,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromChatMessage(msg: ChatMessage): ChatEntity {
            return ChatEntity(
                id = msg.id,
                text = msg.text,
                isUser = msg.isUser,
                emotionName = msg.emotion.name,
                timestamp = msg.timestamp
            )
        }
    }
}
