package eka.dr.intl.common.data.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VerifyOTPEmrResponseV1(
    @SerializedName("response")
    val response: VerifyOTPEmrResponse
)

@Keep
data class VerifyOTPEmrResponse(
    @SerializedName("data")
    val data: DataOTPVerify,
    @SerializedName("error")
    val error: ErrorEmr?
)

@Keep
data class DataOTPVerify(
    @SerializedName("tokens")
    val tokens: Tokens,
    val profile: TransactionProfile
)

@Keep
data class TransactionProfile(
    val fln: String,
    val fn: String,
    val gen: String,
    val dob: String,
    val mobile: String,
    val uuid: String,
    val pic: String,
    @SerializedName("is_d")
    val isD: Boolean,
    @SerializedName("is_p")
    val isP: Boolean,
    @SerializedName("health-ids")
    val healthIds: List<String>? = emptyList<String>(),
    val oid: String,
    val type: Int
)

@Keep
data class Profile(
    @SerializedName("as")
    val asField: String?,
    @SerializedName("at")
    val at: String?,
    @SerializedName("bloodgroup")
    val bloodgroup: String?,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("dob-valid")
    val dobValid: Boolean?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("fln")
    val fln: String,
    @SerializedName("fn")
    val fn: String,
    @SerializedName("gen")
    val gen: String,
    @SerializedName("health-ids")
    val healthIds: List<String>,
    @SerializedName("is-biz")
    val isBiz: Boolean?,
    @SerializedName("is-d")
    val isD: Boolean?,
    @SerializedName("is-d-s")
    val isDS: Boolean?,
    @SerializedName("is-p")
    val isP: Boolean?,
    @SerializedName("ln")
    val ln: String?,
    @SerializedName("mn")
    val mn: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("oid")
    val oid: String,
    @SerializedName("pic")
    val pic: String?,
    @SerializedName("pincode")
    val pincode: String?,
    @SerializedName("type")
    val type: Int,
    @SerializedName("uuid")
    val uuid: String
)

@Keep
data class Tokens(
    @SerializedName("refresh")
    val refresh: String,
    @SerializedName("refresh_exp")
    val refreshExp: Int,
    @SerializedName("sess")
    val sess: String,
    @SerializedName("sess_exp")
    val sessExp: Int
)

@Keep
data class ErrorEmr(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String
)