package eka.dr.intl.patients.data.remote.dto.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddPatientRequest(
    @SerializedName("bloodgroup")
    val bloodGroup: String? = null,
    val dob: String,
    val email: String? = null,
    val fn: String,
    val gen: String,
    val ln: String? = null,
    val mn: String? = null,
    val mobile: String? = null,
    val oid: String,
    val username: String? = null,
    val uuid: String? = null,
    val extras: Map<String, Any?>? = null,
)