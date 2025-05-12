package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep

@Keep
data class ArchivePatientResponse(
    val message: String,
    val code: String? = null
)