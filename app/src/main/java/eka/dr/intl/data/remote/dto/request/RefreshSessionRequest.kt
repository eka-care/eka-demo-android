package eka.dr.intl.data.remote.dto.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RefreshSessionRequest(
    @SerializedName("refresh")
    val refresh: String,
    @SerializedName("sess")
    val sessionToken: String
)