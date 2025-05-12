package eka.dr.intl.assistant.presentation.states

import eka.dr.intl.assistant.presentation.models.ChatMessage

data class SessionMessagesState(
    val isLoading : Boolean = false,
    val messageEntityResp: List<ChatMessage>,
    val error : String? = null
)