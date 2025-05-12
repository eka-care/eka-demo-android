package eka.dr.intl.common.data.dto.response

import androidx.annotation.Keep
import com.eka.voice2rx_sdk.data.local.models.Voice2RxType

@Keep
data class ConsultationData(
    val voiceContext: String,
    val mode: Voice2RxType,
    val sessionId: String,
    val sessionIdentity: String? = null,
)
