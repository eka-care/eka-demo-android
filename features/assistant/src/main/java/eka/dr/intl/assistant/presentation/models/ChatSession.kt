package eka.dr.intl.assistant.presentation.models

import com.eka.conversation.data.local.db.entities.MessageEntity

data class ChatSession(
    val message: MessageEntity,
    val totalRecords: Int = 0,
    val totalConversations: Int = 0,
)
