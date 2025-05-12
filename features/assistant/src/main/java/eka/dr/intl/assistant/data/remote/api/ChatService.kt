package eka.dr.intl.assistant.data.remote.api

import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.assistant.data.remote.dto.response.AudioFileTranscribeResponse
import eka.dr.intl.assistant.data.remote.dto.response.UserHashResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface ChatService {
    @GET("/phr_chat/v1/get_user_hash")
    suspend fun getUserHash(
        @HeaderMap map: HashMap<String, String>
    ): NetworkResponse<UserHashResponse, UserHashResponse>

//    @GET
//    suspend fun getS3Config(
//        @Url url: String,
//    ): NetworkResponse<AwsS3ConfigResponse, AwsS3ConfigResponse>

    @Multipart
    @POST
    suspend fun getTranscribedDataFromAudioFile(
        @Url url: String,
        @HeaderMap map: HashMap<String, String>,
        @Part file: MultipartBody.Part,
        @Part model: MultipartBody.Part,
        @Part language: MultipartBody.Part,
    ): NetworkResponse<AudioFileTranscribeResponse, AudioFileTranscribeResponse>
}