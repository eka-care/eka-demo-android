package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep

@Keep
data class GenerateUHIDResponse(
    val success: Boolean,
    val uhid: String
)