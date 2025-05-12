package eka.dr.intl.patients.data.remote.dto.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UpdatePatientRequest(
    @SerializedName("bloodgroup")
    val bloodGroup: String? = null,
    val dob: String,
    val email: String? = null,
    val fn: String,
    val gen: String,
    val ln: String? = null,
    val mn: String? = null,
    val mobile: String? = null,
    val username: String? = null,
    val extras: Map<String, Any?>? = null,
)