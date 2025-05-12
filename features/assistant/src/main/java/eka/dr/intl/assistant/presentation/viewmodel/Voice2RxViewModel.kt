package eka.dr.intl.assistant.presentation.viewmodel

import android.app.Application
import android.speech.tts.Voice
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.eka.voice2rx_sdk.data.local.db.entities.VToRxSession
import com.eka.voice2rx_sdk.sdkinit.Voice2Rx
import eka.dr.intl.assistant.data.remote.dto.response.AwsS3ConfigResponseOld
import eka.dr.intl.assistant.presentation.states.Voice2RxStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Voice2RxViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val _s3ConfigResponse =
        MutableStateFlow<AwsS3ConfigResponseOld?>(null)
    val s3ConfigResponse = _s3ConfigResponse.asStateFlow()

    private val _voice2rxStatus = MutableStateFlow(Voice2RxStatus.ONGOING)
    val voice2rxStatus = _voice2rxStatus.asStateFlow()

    private val _getTranscript = MutableStateFlow<String?>(null)
    val getTranscript = _getTranscript.asStateFlow()

    private var currentSessionId: String? = null
    private var pollingJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private fun fetchTranscriptForSession(sessionId: String,onComplete: () -> Unit,
                                          onError: (String) -> Unit,) {
        _getTranscript.value = null
        currentSessionId = sessionId

        _voice2rxStatus.value = Voice2RxStatus.PROCESSING
        startPolling(sessionId, onComplete, onError)
    }

    private fun startPolling(sessionId: String,onComplete: () -> Unit,
                             onError: (String) -> Unit,) {
        stopPolling()

        pollingJob = coroutineScope.launch {
            while (isActive) {
                try {
                    val session = pollData(sessionId)

                    val hasTranscript = processSessionResult(session)

                    if (hasTranscript) {
                        onComplete()
                        Log.d("Voice2RxViewModel", "Got transcript, stopping polling")
                        break
                    }
                    delay(5000)
                } catch (e: Exception) {
                    onError(e.message.toString())
                    Log.e("Voice2RxViewModel", "Error during polling", e)
                    break
                }
            }
        }
    }
    private fun processSessionResult(session: VToRxSession?): Boolean {
        var foundTranscript = false

        session?.let {
            it.transcript?.let { transcript ->
                if (transcript.isNotEmpty()) {
                    Log.d("Voice2RxViewModel", "Setting transcript: $transcript")
                    _getTranscript.value = transcript
                    foundTranscript = true
                }
            }
        } ?: run {
            Log.d("Voice2RxViewModel", "No session data received yet")
        }

        return foundTranscript
    }

    private suspend fun pollData(sessionId: String): VToRxSession? {
        return withContext(Dispatchers.IO) {
            Voice2Rx.updateAllSessions()
            val session = Voice2Rx.getSessionBySessionId(sessionId)
            Log.d("Voice2RxViewModel", "Session: $session")
            session
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }


    fun processVoiceToRx(
        sessionId: String,
        totalFiles: Int,
        onComplete: () -> Unit,
        onError: (String) -> Unit,
    ) {
        _voice2rxStatus.value = Voice2RxStatus.PROCESSING
        fetchTranscriptForSession(sessionId, onComplete, onError)
    }
}