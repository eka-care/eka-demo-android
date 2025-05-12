package eka.dr.intl.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RefreshSessionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("sess")
    val sessionToken: String,
    @SerializedName("refresh")
    val refresh: String,
    @SerializedName("error")
    val error: Error
)