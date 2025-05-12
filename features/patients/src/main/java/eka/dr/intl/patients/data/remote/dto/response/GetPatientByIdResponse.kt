package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetPatientByIdResponse(
    val oid: String,
    val type: Int,
    val uuid: String,
    val mobile: String? = null,
    val email: String,
    val fn: String,
    val mn: String,
    val ln: String,
    val fln: String,
    val gen: String,
    val dob: String,
    val dobValid: Boolean,
    @SerializedName("is-d")
    val isD: Boolean,
    @SerializedName("is-d-s")
    val isDS: Boolean,
    @SerializedName("is-p")
    val isP: Boolean,
    @SerializedName("health-ids")
    val healthIds: List<String>,
    val claims: Claims,
    val at: String,
    val uat: String,
    val extras: Extras
) {
    @Keep
    data class Claims(
        @SerializedName("b-id")
        val bId: String,
        @SerializedName("doc-id")
        val docId: String,
        @SerializedName("is-d")
        val isD: Boolean
    )

    @Keep
    data class Extras(
        val pincode: String,
        val pic: String
    )
}