package eka.dr.intl.patients.data.local.convertors

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eka.dr.intl.common.utility.BloodGroup
import eka.dr.intl.common.utility.Gender

class Converters {
    private val gson = Gson()
    @TypeConverter
    fun formBloodGroupToString(bloodGroup: BloodGroup?): String? {
        if (bloodGroup == null) return null
        return bloodGroup.value
    }

    @TypeConverter
    fun toBloodGroupFromString(name: String?): BloodGroup? {
        if (name.isNullOrEmpty()) return null
        return when (name.uppercase()) {
            BloodGroup.A_POSITIVE.value -> BloodGroup.A_POSITIVE
            BloodGroup.A_NEGATIVE.value -> BloodGroup.A_NEGATIVE
            BloodGroup.B_POSITIVE.value -> BloodGroup.B_POSITIVE
            BloodGroup.B_NEGATIVE.value -> BloodGroup.B_NEGATIVE
            BloodGroup.O_POSITIVE.value -> BloodGroup.O_POSITIVE
            BloodGroup.O_NEGATIVE.value -> BloodGroup.O_NEGATIVE
            BloodGroup.AB_POSITIVE.value -> BloodGroup.AB_POSITIVE
            BloodGroup.AB_NEGATIVE.value -> BloodGroup.AB_NEGATIVE
            else -> null
        }
    }

    @TypeConverter
    fun fromGenderToString(gender: Gender?): String? {
        if (gender == null) return null
        return gender.value
    }

    @TypeConverter
    fun toGenderFromString(name: String?): Gender {
        if (name.isNullOrEmpty()) return Gender.UNKNOWN
        val gender = name.lowercase()
        return when (gender) {
            Gender.MALE.value, "MALE", "M", "m" -> Gender.MALE
            Gender.FEMALE.value, "FEMALE", "F", "f" -> Gender.FEMALE
            Gender.OTHER.value, "OTHER", "O", "o" -> Gender.OTHER
            else -> Gender.UNKNOWN
        }
    }

    @TypeConverter
    fun fromParamsMap(params: Map<String, Any>?): String? {
        return gson.toJson(params)
    }

    @TypeConverter
    fun toParamsMap(paramsString: String?): Map<String, Any>? {
        if (paramsString == null) return null
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(paramsString, type)
    }

    @TypeConverter
    fun fromTags(tags: List<String>?): String? {
        return tags?.joinToString(",")
    }

    @TypeConverter
    fun toTags(tagsString: String?): List<String>? {
        return tagsString?.split(",")
    }
}