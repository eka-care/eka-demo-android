package eka.dr.intl.data.remote.api

import eka.dr.intl.data.remote.dto.response.AccountConfigurationResponse
import retrofit2.Response
import retrofit2.http.GET

interface HubService {
    @GET("/account/3/configuration/?format=json")
    suspend fun getAccountConfiguration(): Response<AccountConfigurationResponse>
}

