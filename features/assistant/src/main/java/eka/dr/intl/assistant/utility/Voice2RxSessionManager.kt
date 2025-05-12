package eka.dr.intl.assistant.utility

import android.content.Context
import android.util.Log
import com.eka.conversation.ChatInit
import com.eka.conversation.common.Utils
import com.eka.conversation.data.local.db.entities.MessageEntity
import com.eka.voice2rx_sdk.common.ResponseState
import com.eka.voice2rx_sdk.common.models.VoiceError
import com.eka.voice2rx_sdk.data.local.models.Voice2RxType
import com.eka.voice2rx_sdk.sdkinit.AwsS3Configuration
import com.eka.voice2rx_sdk.sdkinit.Voice2Rx
import com.google.gson.Gson
import eka.dr.intl.common.presentation.component.ConsultationState
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.presentation.viewmodel.Voice2RxViewModel
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.listeners.Voice2RxSessionListener
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Voice2RxSessionManager(
    private val context: Context,
    private val voice2RxViewModel: Voice2RxViewModel,
    private val ekaChatViewModel: EkaChatViewModel,
    private val doctorStatusViewModel: DoctorStatusViewModel,
) : Voice2RxSessionListener {
    override fun onStartVoice2RxSession(sessionInfo: String, onError: (String) -> Unit) {
    }

    override fun stopVoice2RxSession() {
        (context.applicationContext as IAmCommon).onVoice2RxSessionCompleted(
            mode = ekaChatViewModel.consultationData?.mode ?: Voice2RxType.DICTATION
        )
    }

    override fun onStopVoice2RxSession(sessionInfo: String, recordedFiles: Int) {
        if (ekaChatViewModel.consultationData?.sessionId == null) {
            return
        }
        ekaChatViewModel.isVoice2RxRecording = false
        val sessionId = ekaChatViewModel.consultationData?.sessionId
        CoroutineScope(Dispatchers.IO).launch {
            doctorStatusViewModel.startAnalyzing()
            val recordingMsgId =
                ekaChatViewModel.getNewMsgId(ekaChatViewModel.sessionMessages.value.messageEntityResp)
            val consultationMsgId = recordingMsgId + 1
            val message = ekaChatViewModel.getMessageEntity(
                msgId = consultationMsgId,
                sessionInfo = sessionId.toString(),
                msgType = MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT
            )
            val audioMessage = ekaChatViewModel.getMessageEntity(
                msgId = recordingMsgId,
                sessionInfo = sessionId.toString(),
                msgType = MessageType.AUDIO
            )
            ChatInit.insertMessages(
                listOf(
                    message.copy(createdAt = Utils.getCurrentUTCEpochMillis()),
                    audioMessage.copy(createdAt = Utils.getCurrentUTCEpochMillis() + 1)
                )
            )
            voice2RxViewModel.processVoiceToRx(
                sessionId = sessionInfo,
                totalFiles = recordedFiles,
                onComplete = {
                    doctorStatusViewModel.onConsultationCompleted(ConsultationState.COMPLETED_ANALYSES)
                },
                onError = { error ->
                    doctorStatusViewModel.onConsultationCompleted(ConsultationState.ERROR_ANALYSING)
                }
            )
        }
    }

    override fun onProgressCompleted(sessionInfo: String) {
        TODO("Not yet implemented")
    }

    override fun onError(error: VoiceError) {
        ekaChatViewModel.isVoice2RxRecording = false
        doctorStatusViewModel.onConsultationCompleted(ConsultationState.ERROR_ANALYSING)
        if (error == VoiceError.MICROPHONE_PERMISSION_NOT_GRANTED) {
            ekaChatViewModel.showToast("Microphone Permission Not Granted!")
            return
        }
        if (ekaChatViewModel.consultationData?.sessionId == null) {
            return
        }
        val session = ekaChatViewModel.consultationData?.sessionId
        CoroutineScope(Dispatchers.IO).launch {
            val recordingMsgId =
                ekaChatViewModel.getNewMsgId(ekaChatViewModel.sessionMessages.value.messageEntityResp)
            val consultationMsgId = recordingMsgId + 1
            val message = ekaChatViewModel.getMessageEntity(
                msgId = consultationMsgId,
                sessionInfo = session.toString(),
                msgType = MessageType.VOICE_2_RX_ERROR
            )
            val audioMessage = ekaChatViewModel.getMessageEntity(
                msgId = recordingMsgId,
                sessionInfo = session.toString(),
                msgType = MessageType.AUDIO
            )
            ChatInit.insertMessages(
                listOf(
                    audioMessage,
                    message
                )
            )
        }
    }

    override fun retrySession(
        context: Context,
        sessionId: String,
        s3Config: AwsS3Configuration,
        messageJson: String
    ) {
        val message = Gson().fromJson(messageJson, MessageEntity::class.java)
        doctorStatusViewModel.startAnalyzing()
        Voice2Rx.retrySession(
            context = context,
            sessionId = sessionId,
            onResponse = {
                when (it) {
                    is ResponseState.Loading -> {
                        // Loading
                    }

                    is ResponseState.Success -> {
                        if (it.isCompleted) {
                            CoroutineScope(Dispatchers.IO).launch {
                                ChatInit.insertMessages(
                                    listOf(
                                        message.copy(
                                            msgType = MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT.stringValue
                                        )
                                    )
                                )
                            }
                            voice2RxViewModel.processVoiceToRx(
                                sessionId = sessionId,
                                totalFiles = 0,
                                onComplete = {
                                    doctorStatusViewModel.onConsultationCompleted(
                                        ConsultationState.COMPLETED_ANALYSES
                                    )
                                },
                                onError = { error ->
                                }
                            )
                        } else {
                        }
                    }

                    is ResponseState.Error -> {
                        doctorStatusViewModel.onConsultationCompleted(ConsultationState.ERROR_ANALYSING)
                    }

                    else -> {}
                }
            }
        )
    }

    override fun pauseVoice2RxSession() {
        Log.d("VoiceSession", "Paused")
        doctorStatusViewModel.pauseConsultation()
    }

    override fun resumeVoice2RxSession() {
        Log.d("VoiceSession", "Resumed")
        doctorStatusViewModel.resumeConsultation()
    }
}