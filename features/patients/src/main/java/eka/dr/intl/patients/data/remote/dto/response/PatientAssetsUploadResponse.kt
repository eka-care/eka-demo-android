package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep

@Keep
data class PatientAssetsUploadResponse(
    val success: Boolean,
    val url: String
)