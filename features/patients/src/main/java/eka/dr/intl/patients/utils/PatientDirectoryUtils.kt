package eka.dr.intl.patients.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.utility.DateUtils
import eka.dr.intl.common.utility.DateUtils.Companion.convertLongToDateString
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.request.AddPatientRequest
import eka.dr.intl.patients.data.remote.dto.request.UpdatePatientRequest
import eka.dr.intl.patients.data.remote.dto.response.PatientDirectoryResponse
import eka.dr.intl.typography.touchHeadlineBold

class PatientDirectoryUtils {

    companion object {
        val gson = Gson()

        fun formatPatientEntityToAddPatientRequest(
            patientEntity: PatientEntity,
        ): AddPatientRequest {
            val extras = mapOf<String, Any?>(
                "formData" to gson.fromJson(
                    patientEntity.formData,
                    object : TypeToken<List<PatientDirectoryResponse.Patient.FormData?>>() {}.type
                ),
                "onApp" to patientEntity.onApp,
                "uhid" to patientEntity.uhid
            )
            return AddPatientRequest(
                bloodGroup = Converters().formBloodGroupToString(patientEntity.bloodGroup),
                dob = convertLongToDateString(patientEntity.age),
                email = if (!patientEntity.email.isNullOrEmpty()) patientEntity.email else null,
                username = patientEntity.uhid,
                uuid = patientEntity.uuid,
                fn = patientEntity.name,
                gen = (Converters().fromGenderToString(patientEntity.gender) ?: "U").uppercase()
                    .first().toString(),
                mobile = if (patientEntity.phone != null) "+${patientEntity.countryCode}${patientEntity.phone}" else null,
                oid = patientEntity.oid,
                extras = extras
            )
        }

        fun formatPatientEntityToUpdatePatientRequest(
            patientEntity: PatientEntity,
        ): UpdatePatientRequest {
            val extras = mapOf<String, Any?>(
                "formData" to gson.fromJson(
                    patientEntity.formData,
                    object : TypeToken<List<PatientDirectoryResponse.Patient.FormData?>>() {}.type
                ),
                "onApp" to patientEntity.onApp,
                "uhid" to patientEntity.uhid
            )
            return UpdatePatientRequest(
                bloodGroup = Converters().formBloodGroupToString(patientEntity.bloodGroup),
                dob = convertLongToDateString(patientEntity.age),
                email = if (!patientEntity.email.isNullOrEmpty()) patientEntity.email else null,
                username = patientEntity.uhid,
                fn = patientEntity.name,
                gen = (Converters().fromGenderToString(patientEntity.gender) ?: "U").uppercase()
                    .first().toString(),
                mobile = if (patientEntity.phone != null) "+${patientEntity.countryCode}${patientEntity.phone}" else null,
                extras = extras
            )
        }

        fun formatter(data: PatientDirectoryResponse.Patient): PatientEntity? {
            try {
                val regex = Regex("[^0-9]")
                val gson = Gson()
                val uhid = data.formData.find { it?.key == "uhid" }?.value as String?
                val email =
                    data.formData.find { it?.key == DynamicFormKeys.EMAIL.type }?.value as String?
                val id = data.id!!
                val uuid = data.uuid
                val name = data.profile?.personal?.name!!
                val businessId = OrbiUserManager.getSelectedBusiness() ?: ""

                val age = Conversions.formYYYYMMDDToLong(data.profile?.personal?.age?.dob)
                val phone = regex.replace(data.profile?.personal?.phone?.n!!, "").toLongOrNull()
                val gender = data.profile?.personal?.gender
                val bloodGroup = data.profile?.personal?.bloodgroup
                val onApp = data.profile?.personal?.onApp == true
                val archived = data.archived == true
                val referredBy = data.referredBy
                val createdAt =
                    Conversions.fromTimestampToLong(data.createdAt) ?: System.currentTimeMillis()
                val updatedAt = Conversions.fromTimestampToLong(data.updatedAt) ?: createdAt
                val formData =
                    data.formData.ifEmpty { emptyList() }
                val followUp = Conversions.fromTimestampToLong(data.followup)
                val lastVisit = Conversions.fromTimestampToLong(data.lastVisit)
                val referId = data.referId

                val countryCode =
                    regex.replace(data.profile?.personal?.phone?.c ?: "91", "").toIntOrNull()

                val genderEnum = Converters().toGenderFromString(gender)
                val bloodGroupEnum = Converters().toBloodGroupFromString(bloodGroup)

                return PatientEntity(
                    oid = id,
                    uuid = uuid,
                    name = name,
                    businessId = businessId,
                    age = age,
                    phone = phone,
                    gender = genderEnum,
                    email = email,
                    bloodGroup = bloodGroupEnum,
                    onApp = onApp,
                    links = data.link,
                    archived = archived,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    referredBy = referredBy,
                    formData = gson.toJson(
                        formData,
                        object :
                            TypeToken<List<PatientDirectoryResponse.Patient.FormData?>>() {}.type
                    ),
                    uhid = uhid,
                    followUp = followUp,
                    lastVisit = lastVisit,
                    referId = referId,
                    countryCode = countryCode
                )
            } catch (e: Exception) {
                return null
            }
        }


        fun getLastUpdatedPatientDirectoryISOString(lastUpdateEpocTime: Long?): String {
            return DateUtils.getUTCDateFromEpoch(lastUpdateEpocTime)
        }

        fun getAnnotationString(
            realString: String,
            searchText: String,
            style: TextStyle = touchHeadlineBold
        ): AnnotatedString {
            val text = searchText.trim()
            if (text.isEmpty()) {
                return AnnotatedString(realString)
            }

            val index = realString.indexOf(text, ignoreCase = true)
            if (index == -1) {
                return AnnotatedString(realString)
            }

            val builder = AnnotatedString.Builder()
            builder.append(realString.substring(0, index))

            val highlightStyle = style.toSpanStyle() // Calculate style only once
            builder.withStyle(highlightStyle) {
                builder.append(realString.substring(index, index + text.length))
            }

            builder.append(realString.substring(index + text.length))

            return builder.toAnnotatedString()
        }

        fun getProfileImageByOid(oid: String): String {
            return "https://a.eka.care/user-avatar/${oid}"
        }
    }

}

@Stable
data class RandomColor(
    val text: Color,
    val background: Color,
)