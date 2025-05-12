package eka.dr.intl.common.data.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChatContext(
    @SerializedName("patientId")
    val patientId: String = "",
    @SerializedName("patientName")
    val patientName: String = "",
)
