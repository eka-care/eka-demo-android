package eka.dr.intl.assistant.presentation.models

import com.eka.conversation.data.local.db.entities.MessageEntity
import com.eka.voice2rx_sdk.data.local.db.entities.VToRxSession
import eka.care.documents.ui.presentation.model.RecordModel

data class ChatMessage(
    val message: MessageEntity,
    val voice2RxSession: VToRxSession? = null,
    val files: List<RecordModel> = emptyList()
)
