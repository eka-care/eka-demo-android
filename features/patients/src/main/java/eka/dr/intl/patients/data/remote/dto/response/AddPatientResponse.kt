package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddPatientResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("as")
    val asField: String? = null,
    val at: String? = null,
    val uuid: String? = null,
    @SerializedName("_custom_uuid")
    val customUuid: String? = null,
)