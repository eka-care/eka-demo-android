package eka.dr.intl.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.presentation.model.HomeDomain
import kotlinx.android.parcel.RawValue

@Keep
data class HomeDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: Data?
) {
    @Keep
    data class Data(
        @SerializedName("sections")
        val section: Section?,
        @SerializedName("track")
        val track: Track?
    ) {
        @Keep
        data class Section(
            @SerializedName("feature_card")
            val featureCard: FeatureCard?,
            @SerializedName("action")
            val action: Action?,
            @SerializedName("recommendation")
            val recommendation: Recommendation?,
            @SerializedName("announcement")
            val announcement: Announcement?
        ) {
            @Keep
            data class FeatureCard(
                @SerializedName("components")
                val components: @RawValue JsonElement?
            )

            @Keep
            data class Action(
                val data: Any?
            )

            @Keep
            data class Recommendation(
                @SerializedName("components")
                val components: @RawValue JsonElement?
            )

            @Keep
            data class Announcement(
                @SerializedName("image")
                val image: String? = null,
                @SerializedName("title")
                val title: String? = null,
                @SerializedName("sub_title")
                val subTitle: String? = null,
                @SerializedName("cta")
                val cta: CTA? = null,
                @SerializedName("cta_text")
                val ctaText: String? = null,
                @SerializedName("block")
                val isBlock: Boolean? = false,
            )
        }

        @Keep
        data class Track(
            @SerializedName("hash")
            val hash: String?
        )
    }
}

fun HomeDto.toHomeDomain(): HomeDomain {
    if(!success) {
        return HomeDomain(
            success = false,
            message = message
        )
    }

    return HomeDomain(
        success = true,
        data = data,
        hash = data?.track?.hash
    )
}