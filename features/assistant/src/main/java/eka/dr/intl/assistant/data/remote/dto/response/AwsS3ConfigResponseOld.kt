package eka.dr.intl.assistant.data.remote.dto.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AwsS3ConfigResponseOld(
    @SerializedName("credentials")
    var credentials: CredentialsOld?,
    @SerializedName("expiry")
    var expiry: Int?,
    @SerializedName("identity_id")
    var identityId: String?,
    @SerializedName("token")
    var token: String?
)