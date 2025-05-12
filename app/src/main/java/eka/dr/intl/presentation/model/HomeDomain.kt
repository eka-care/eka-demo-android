package eka.dr.intl.presentation.model

import eka.dr.intl.data.remote.dto.response.HomeDto


data class HomeDomain(
    val success: Boolean,
    val message: String? = null,
    val data: HomeDto.Data? = null,
    val hash: String? = null
)