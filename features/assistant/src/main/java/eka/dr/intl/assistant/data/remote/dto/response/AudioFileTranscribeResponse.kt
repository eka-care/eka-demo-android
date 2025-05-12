package eka.dr.intl.assistant.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AudioFileTranscribeResponse(
    var text: String = "",
    @SerializedName("x_groq") val xGroq: XGroqInternal? // Use @SerializedName for matching exact JSON key names
)

@Keep
data class XGroqInternal(
    val id: String
)