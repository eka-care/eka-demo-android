package eka.dr.intl.assistant.data.remote.dto.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserHashResponse(
    @SerializedName("user_hash")
    val userHash: String?
)