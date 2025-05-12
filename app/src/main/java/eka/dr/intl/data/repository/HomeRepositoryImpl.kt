package eka.dr.intl.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.BuildConfig
import eka.dr.intl.data.remote.api.HomeApi
import eka.dr.intl.data.remote.dto.response.HomeDto
import eka.dr.intl.domain.repository.HomeRepository
import eka.dr.intl.network.Networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepositoryImpl : HomeRepository {

    private val service = Networking.create(HomeApi::class.java, BuildConfig.NEEV_URL)

    override suspend fun getHome(
        source: String,
        campaign: String,
        medium: String,
    ): HomeDto? {
        return withContext(Dispatchers.IO) {
            val response =
                when (
                    val response = service.getHome(
                        source = source,
                        campaign = campaign,
                        medium = medium
                    )
                ) {
                    is NetworkResponse.Success -> response.body
                    is NetworkResponse.ServerError -> {
                        if (response.code in 400..499) {
                            response.body
                        } else {
                            null
                        }
                    }

                    is NetworkResponse.NetworkError -> null
                    is NetworkResponse.UnknownError -> null
                }
            response
        }
    }
}