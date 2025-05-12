package eka.dr.intl.common.presentation.viewmodel


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import eka.dr.intl.common.presentation.component.ConsultationState
import eka.dr.intl.common.presentation.component.NetworkOverlay
import eka.dr.intl.common.presentation.component.NetworkState
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.data.dto.response.ConsultationData
import eka.dr.intl.common.networkStatusFlow
import eka.dr.intl.common.presentation.state.DocStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class DoctorStatusViewModel(val app: Application) : AndroidViewModel(app) {

    private var _networkState = MutableStateFlow(NetworkState.ONLINE)
    val networkState = _networkState.asStateFlow()

    private val _elapsedSeconds = mutableIntStateOf(0)
    val elapsedSeconds: State<Int> get() = _elapsedSeconds

    var consultationData by mutableStateOf<ConsultationData?>(null)

    private var _status = MutableStateFlow<DocStatus>(DocStatus.NoneStatus)
    val status = _status.asStateFlow()

    private var timerJob: Job? = null
    private var errorCancellationJob: Job? = null

    private var _networkOverlay = MutableStateFlow(NetworkOverlay())
    val networkOverlay = _networkOverlay.asStateFlow()

    init {
        viewModelScope.launch {
            app.networkStatusFlow().collect { isConnected ->
                when {
                    isConnected && _networkState.value == NetworkState.OFFLINE -> {
                        _networkState.value = NetworkState.OFFLINE
                        // Show reconnected overlay
                        _networkOverlay.value = NetworkOverlay(true, NetworkState.RECONNECTED)
                        delay(3000)
                        _networkState.value = NetworkState.ONLINE
                        // Show online overlay briefly
                        _networkOverlay.value = NetworkOverlay(true, NetworkState.ONLINE)
                        delay(3000)
                        // Hide overlay
                        _networkOverlay.value = NetworkOverlay(false)
                    }

                    !isConnected -> {
                        _networkState.value = NetworkState.OFFLINE
                        // Show offline overlay
                        _networkOverlay.value = NetworkOverlay(true, NetworkState.OFFLINE)
                    }
                }
            }
        }
    }

    fun resetElapsedSeconds() {
        if (timerJob != null) {
            timerJob?.cancel()
            timerJob = null
        }
        _elapsedSeconds.intValue = 0
    }

    fun startRecordingTimer() {
        if (timerJob != null) {
            timerJob?.cancel()
            timerJob = null
        }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (isRecording()) {
                    _elapsedSeconds.intValue += 1
                }
            }
        }
        timerJob?.start()
    }

    private fun isRecording(): Boolean {
        val currentStatus = _status.value
        return ((currentStatus is DocStatus.ConsultationStatus) && (currentStatus.state == ConsultationState.CONVERSATION))
    }

    fun onClickRefresh() {
        val connectivityManager =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected = connectivityManager.activeNetwork != null &&
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        if (isConnected) {
            viewModelScope.launch {
                _networkState.value = NetworkState.RECONNECTED
                delay(3000)
                _networkState.value = NetworkState.ONLINE
            }
        } else {
            _networkState.value = NetworkState.OFFLINE
        }
    }

    fun updateConsultationState(state: DocStatus) {
        _status.value = state
    }

    private fun onNoResponse() {
        if (errorCancellationJob != null) {
            errorCancellationJob?.cancel()
        }
        errorCancellationJob = viewModelScope.launch {
            delay(120_000)
            val currentStatus = _status.value
            if (!((currentStatus is DocStatus.ConsultationStatus) && (currentStatus.state == ConsultationState.WAITING_STATE))) {
                return@launch
            }
            val params = JSONObject()
            params.put("type", "no_response_firebase")
            _status.value = DocStatus.NoneStatus
            this.cancel()
        }
    }

    fun onConsultationCompleted(state: ConsultationState) {
        viewModelScope.launch {
            val currentStatus = _status.value
            if (!((currentStatus is DocStatus.ConsultationStatus) && (currentStatus.state == ConsultationState.WAITING_STATE))) {
                return@launch
            }
            _status.value =
                DocStatus.ConsultationStatus(state = state)
            delay(3000)
            _status.value = DocStatus.NoneStatus
            errorCancellationJob?.cancel()
        }
    }

    fun pauseConsultation() {
        viewModelScope.launch {
            _status.value = DocStatus.ConsultationStatus(state = ConsultationState.PAUSED)
            (app as IAmCommon).pauseVoice2RxSession()
        }
    }

    fun resumeConsultation() {
        viewModelScope.launch {
            _status.value = DocStatus.ConsultationStatus(state = ConsultationState.CONVERSATION)
            (app as IAmCommon).resumeVoice2RxSession()
        }
    }

    fun stopConsultation() {
        viewModelScope.launch {
            (app as IAmCommon).stopVoice2RxSession()
        }
    }

    fun startAnalyzing() {
        viewModelScope.launch {
            _status.value =
                DocStatus.ConsultationStatus(state = ConsultationState.STARTED_ANALYSING)
            delay(2000)
            _status.value =
                DocStatus.ConsultationStatus(state = ConsultationState.GETTING_SMART_NOTES)
            delay(2000)
            _status.value = DocStatus.ConsultationStatus(state = ConsultationState.WAITING_STATE)
            onNoResponse()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (timerJob != null) {
            timerJob?.cancel()
            timerJob = null
        }
    }
}