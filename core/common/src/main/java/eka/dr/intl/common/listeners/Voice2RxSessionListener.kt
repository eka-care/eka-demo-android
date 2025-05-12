package eka.dr.intl.common.listeners

import android.content.Context
import com.eka.voice2rx_sdk.common.models.VoiceError
import com.eka.voice2rx_sdk.sdkinit.AwsS3Configuration

interface Voice2RxSessionListener {
    fun onStartVoice2RxSession(sessionInfo: String, onError: (String) -> Unit)
    fun stopVoice2RxSession()
    fun onStopVoice2RxSession(sessionInfo: String, recordedFiles: Int)
    fun onProgressCompleted(sessionInfo: String)
    fun onError(error: VoiceError)
    fun retrySession(
        context: Context,
        sessionId: String,
        s3Config: AwsS3Configuration,
        messageJson: String
    )
    fun pauseVoice2RxSession()
    fun resumeVoice2RxSession()
}