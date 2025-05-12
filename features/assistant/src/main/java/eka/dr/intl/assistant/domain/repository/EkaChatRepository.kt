package eka.dr.intl.assistant.domain.repository

import eka.dr.intl.assistant.data.remote.dto.response.AudioFileTranscribeResponse
import eka.dr.intl.assistant.data.remote.dto.response.UserHashResponse
import eka.dr.intl.assistant.utility.EkaResponse
import java.io.File

interface EkaChatRepository {
    suspend fun getUserHash(oid: String, uuid: String): EkaResponse<UserHashResponse>
    suspend fun getTranscribeDataFromAudioFile(audioFile: File): EkaResponse<AudioFileTranscribeResponse>

//    suspend fun getAwsS3Config(): Response<AwsS3ConfigResponse>
}