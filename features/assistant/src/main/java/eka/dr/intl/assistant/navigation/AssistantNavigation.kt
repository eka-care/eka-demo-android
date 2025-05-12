package eka.dr.intl.assistant.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Serializable

@Serializable
object AssistantNavModel

@Serializable
data class ChatBotPatientSessionNavModel(
    val patientId: String? = null,
    val visitId: String? = null,
    val mode: String? = null
)

@Serializable
data class ChatScreenNavModel(
    val sessionId: String? = null,
    val patientId: String? = null,
    val visitId: String? = null,
    val chatContext: String? = null,
    val openType: String? = null
)


@Serializable
data class ChatTranscriptNavModel(
    val sessionId : String,
    val transcript : String? = null
)

fun NavController.navigateToDocAssist(navOptions: NavOptions? = null) {
    navigate(AssistantNavModel, navOptions)
}

fun NavController.navigateToChatBotPatientSession(
    patientId: String? = null,
    visitId: String? = null,
    mode: String? = null,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(ChatBotPatientSessionNavModel(patientId, visitId, mode)) {
        navOptions()
    }
}

fun NavController.navigateToChatScreen(
    sessionId: String? = null,
    patientId: String? = null,
    visitId: String? = null,
    chatContext: String? = null,
    openType: String? = null,
    navOptions: NavOptions? = null
) {
    navigate(ChatScreenNavModel(sessionId, patientId, visitId, chatContext, openType), navOptions)
}

fun NavController.navigateToChatTranscript(
    sessionId: String,
    transcript: String? = null,
    navOptions: NavOptions? = null
) {
    navigate(ChatTranscriptNavModel(sessionId, transcript), navOptions)
}