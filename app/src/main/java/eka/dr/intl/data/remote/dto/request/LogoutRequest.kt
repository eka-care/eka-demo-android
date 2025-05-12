package eka.dr.intl.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("refresh")
    val refresh: String,
    @SerializedName("sess")
    val sess: String,
)
