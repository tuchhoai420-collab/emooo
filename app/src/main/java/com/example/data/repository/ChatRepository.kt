package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.ChatEntity
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(private val chatDao: ChatDao) {

    val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages().map { entities ->
        entities.map { it.toChatMessage() }
    }

    suspend fun saveMessage(message: ChatMessage): Long {
        return chatDao.insertMessage(ChatEntity.fromChatMessage(message))
    }

    suspend fun clearHistory() {
        chatDao.deleteAllMessages()
    }

    suspend fun getMessageCount(): Int {
        return chatDao.getMessageCount()
    }
}
