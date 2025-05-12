package eka.dr.intl.domain.repository

import eka.dr.intl.data.remote.dto.response.HomeDto

interface HomeRepository {

    suspend fun getHome(
        source: String,
        campaign: String,
        medium: String,
    ): HomeDto?

}