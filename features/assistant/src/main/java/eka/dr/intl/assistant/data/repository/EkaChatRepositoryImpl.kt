package eka.dr.intl.assistant.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.assistant.BuildConfig
import eka.dr.intl.assistant.data.remote.api.ChatService
import eka.dr.intl.assistant.data.remote.dto.response.AudioFileTranscribeResponse
import eka.dr.intl.assistant.data.remote.dto.response.UserHashResponse
import eka.dr.intl.assistant.domain.repository.EkaChatRepository
import eka.dr.intl.assistant.utility.EkaChatBotConstants
import eka.dr.intl.assistant.utility.EkaResponse
import eka.dr.intl.network.Networking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EkaChatRepositoryImpl : EkaChatRepository {
    private val remoteDataSource: ChatService =
        Networking.create(ChatService::class.java, BuildConfig.AI_CHAT_USER_HASH_URL)

    override suspend fun getUserHash(
        oid: String,
        uuid: String
    ): EkaResponse<UserHashResponse> {
        val headersMap = hashMapOf<String, String>()
        headersMap.put("jwt-payload", "{\"oid\": \"${oid}\",\"uuid\":\"${uuid}\"}")
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.getUserHash(headersMap)
                when (response) {
                    is NetworkResponse.NetworkError -> {
                        EkaResponse.Error("Network error!")
                    }

                    is NetworkResponse.Success -> {
                        EkaResponse.Success(response.body)
                    }

                    is NetworkResponse.UnknownError -> {
                        EkaResponse.Error("Unknown error!")
                    }

                    is NetworkResponse.ServerError -> {
                        EkaResponse.Error("Server error!")
                    }

                    else -> {
                        EkaResponse.Error("Something went wrong!")
                    }
                }
            } catch (_: Exception) {
                EkaResponse.Error("Something went wrong!")
            }
        }
    }

    override suspend fun getTranscribeDataFromAudioFile(audioFile: File): EkaResponse<AudioFileTranscribeResponse> {
        var url = EkaChatBotConstants.EKA_CHAT_VOICE_TRANSCRIBE_URL
        val headersMap = hashMapOf<String, String>()
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.getTranscribedDataFromAudioFile(
                    url = url,
                    file = MultipartBody.Part
                        .createFormData(
                            "file",
                            audioFile.name,
                            audioFile.asRequestBody()
                        ),
                    model = MultipartBody.Part
                        .createFormData(
                            "model",
                            "whisper-large-v3"
                        ),
                    language = MultipartBody.Part
                        .createFormData(
                            "language",
                            "en"
                        ),
                    map = headersMap
                )
                when (response) {
                    is NetworkResponse.NetworkError -> {
                        EkaResponse.Error("Network error!")
                    }

                    is NetworkResponse.Success -> {
                        EkaResponse.Success(response.body)
                    }

                    is NetworkResponse.UnknownError -> {
                        EkaResponse.Error("Unknown error!")
                    }

                    is NetworkResponse.ServerError -> {
                        EkaResponse.Error("Server error!")
                    }

                    else -> {
                        EkaResponse.Error("Something went wrong!")
                    }
                }
            } catch (_: Exception) {
                EkaResponse.Error("Something went wrong!")
            }
        }
    }

//    override suspend fun getAwsS3Config(): Response<AwsS3ConfigResponse> {
//        val url = "https://cog.eka.care/credentials"
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = remoteDataSource.getS3Config(url)
//                when (response) {
//                    is NetworkResponse.NetworkError -> {
//                        Response.Error("Network error!")
//                    }
//
//                    is NetworkResponse.Success -> {
//                        Response.Success(response.body)
//                    }
//
//                    is NetworkResponse.UnknownError -> {
//                        Response.Error("Unknown error!")
//                    }
//
//                    is NetworkResponse.ServerError -> {
//                        Response.Error("Server error!")
//                    }
//
//                    else -> {
//                        Response.Error("Something went wrong!")
//                    }
//                }
//            } catch (_: Exception) {
//                Response.Error("Something went wrong!")
//            }
//        }
//    }
}