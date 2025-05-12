package eka.dr.intl.domain.repository

import eka.dr.intl.data.remote.dto.response.AccountConfigurationResponse

interface HubRepository {
    suspend fun getAccountConfiguration(): AccountConfigurationResponse?
}