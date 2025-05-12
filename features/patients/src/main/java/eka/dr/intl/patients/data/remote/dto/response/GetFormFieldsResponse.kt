package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetFormFieldsResponse(
    val status: String,
    val form: Form,
    @SerializedName("updated_at") val updatedAt: String
) {
    @Keep
    data class Form(
        @SerializedName("addp") val addPatient: List<AddPatientEntity>
    ) {
        @Keep
        data class AddPatientEntity(
            val label: String,
            val type: String,
            val key: String
        )
    }
}
