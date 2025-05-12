package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetByMobileResponse(
    val success: Boolean,
    val profiles: List<Profile>
) {
    @Keep
    data class Profile(
        val id: String?,
        val personal: Personal?
    ) {
        @Keep
        data class Personal(
            val name: Name,
            val phone: Phone,
            val age: Age?,
            val gender: String,
            val onApp: Boolean,
            @SerializedName("health-ids") val healthIds: List<String>
        ) {
            @Keep
            data class Name(
                val f: String
            )

            @Keep
            data class Phone(
                val n: String,
                val c: String
            )

            @Keep
            data class Age(
                val dob: String?
            )
        }
    }
}
