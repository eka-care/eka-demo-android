package eka.dr.intl.common.data.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

// sess cookie data
@Keep
data class UserTokenData(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("oid")
    val oid: String,
    @SerializedName("fn")
    val name: String?,
    @SerializedName("gen")
    val gender: String?,
    @SerializedName("s")
    val salutation: String?,
    @SerializedName("is-p")
    val isP: Boolean?,
    @SerializedName("is-d")
    val isD: Boolean?,
    @SerializedName("dob")
    val dob: String?,
    @SerializedName("mob")
    val mob: String?,
    @SerializedName("type")
    val type: Int?,
    @SerializedName("doc-id")
    val docId: String?,
    @SerializedName("b-id")
    val businessId: String?,
    @SerializedName("p")
    val passType: String?,
    @SerializedName("pp")
    val passDetails: PassDetails?,
    @SerializedName("exp")
    val exp: Int?,
    @SerializedName("iat")
    val iat: Int?,
)

@Keep
data class PassDetails(
    @SerializedName("c")
    val c: String,
    @SerializedName("e")
    val e: String,
    @SerializedName("t")
    val t: String,
)