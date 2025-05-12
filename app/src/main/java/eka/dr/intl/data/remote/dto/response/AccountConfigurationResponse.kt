package eka.dr.intl.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AccountConfigurationResponse(
    val id: String,
    val abha: Boolean = false,
    @SerializedName("business_name") val businessName: String? = "",
    val clinics: List<ClinicData> = emptyList(),
    val doctors: List<Doctor> = emptyList(),
    val tags: List<HubTag> = emptyList(),
    val labels: List<HubTag> = emptyList(),
    @SerializedName("denial_list") val denialList: List<String> = emptyList(),
    @SerializedName("loginmeta") val loginMeta: LoginMeta,
    val groups: List<Groups> = emptyList()
)

@Keep
data class Groups(
    val doctors: List<String> = emptyList(),
    val name: String
)

@Keep
data class ClinicData(
    val id: String,
    val name: String,
    val city: String,
    val pin: String,
    val address: String
)

@Keep
data class Doctor(
    val id: String,
    val personal: PersonalInfo,
    val professional: ProfessionalInfo
)

@Keep
data class PersonalInfo(
    val name: DoctorName,
    val email: String? = null,
    val gender: String,
    val dob: String
)

@Keep
data class DoctorName(
    val l: String,
    val f: String
)

@Keep
data class ProfessionalInfo(
    @SerializedName("major_speciality") val majorSpeciality: MajorSpeciality
)

@Keep
data class MajorSpeciality(
    val name: String,
    val code: String
)

@Keep
data class HubTag(
    val hex: String?,
    @SerializedName("bg_hex") val bgHex: String?,
    val archive: Boolean,
    @SerializedName("tag_id") val tagId: String,
    val label: String
)

@Keep
data class LoginMeta(
    val role: String
)
