package eka.dr.intl.data.repository

import eka.dr.intl.BuildConfig
import eka.dr.intl.data.remote.api.HubService
import eka.dr.intl.data.remote.dto.response.AccountConfigurationResponse
import eka.dr.intl.domain.repository.HubRepository
import eka.dr.intl.network.Networking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HubRepositoryImpl : HubRepository {
    private val hubService: HubService =
        Networking.create(HubService::class.java, BuildConfig.HUB_URL)

    override suspend fun getAccountConfiguration(): AccountConfigurationResponse? {
        return withContext(Dispatchers.IO) {
            val response = hubService.getAccountConfiguration()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        }
    }
}