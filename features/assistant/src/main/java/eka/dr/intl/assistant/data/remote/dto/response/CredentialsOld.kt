package eka.dr.intl.assistant.data.remote.dto.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CredentialsOld(
    @SerializedName("AccessKeyId")
    var accessKeyId: String?,
    @SerializedName("Expiration")
    var expiration: String?,
    @SerializedName("SecretKey")
    var secretKey: String?,
    @SerializedName("SessionToken")
    var sessionToken: String?
)