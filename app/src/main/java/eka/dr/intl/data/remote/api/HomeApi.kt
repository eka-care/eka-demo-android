package eka.dr.intl.data.remote.api

import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.data.remote.dto.response.HomeDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("/homepage/get-homepage/")
    suspend fun getHome(
        @Query("us") source: String,
        @Query("uc") campaign: String,
        @Query("um") medium: String,
    ): NetworkResponse<HomeDto, HomeDto>
}