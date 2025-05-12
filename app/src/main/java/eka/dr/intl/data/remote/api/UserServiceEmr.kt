package eka.dr.intl.data.remote.api

import eka.dr.intl.data.remote.dto.request.LogoutRequest
import eka.dr.intl.data.remote.dto.request.RefreshSessionRequest
import eka.dr.intl.data.remote.dto.response.LogoutResponse
import eka.dr.intl.data.remote.dto.response.RefreshSessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserServiceEmr {
    @POST("/emr/v3/auth/refresh")
    suspend fun refresh(@Body refreshSessionRequest: RefreshSessionRequest): Response<RefreshSessionResponse>

    @POST("/v2/auth/logout")
    suspend fun logoutUser(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>
}