package eka.dr.intl.assistant.utility

import android.media.MediaMetadataRetriever
import androidx.compose.ui.graphics.Color
import com.eka.conversation.common.Utils
import com.google.gson.Gson
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.RandomColor
import eka.dr.intl.common.data.dto.response.ChatContext
import eka.dr.intl.ekatheme.color.DarwinTouchGreen
import eka.dr.intl.ekatheme.color.DarwinTouchGreenBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.ekatheme.color.DarwinTouchRedBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchYellowBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchYellowDark
import eka.dr.intl.patients.data.local.entity.PatientEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

object ChatUtils {
    fun getTimeStampString(utcTimestamp: Long): String {
        val now = Calendar.getInstance()
        val today = now.clone() as Calendar
        val yesterday = now.clone() as Calendar

        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        yesterday.timeInMillis = today.timeInMillis
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        val timestampDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        timestampDate.timeInMillis = utcTimestamp
        timestampDate.timeZone = TimeZone.getDefault()

        return when {
            timestampDate.after(today) -> {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                timeFormat.format(timestampDate.time).uppercase(Locale.getDefault())
            }

            timestampDate.after(yesterday) -> {
                "Yesterday"
            }

            else -> {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.format(timestampDate.time)
            }
        }
    }

    fun generateRandomColor(name: String): Pair<Color, Color> {
        var lighterColor: Color
        var darkerColor: Color

        val hash = name.hashCode()

        val redVal = abs(hash and 0xFF0000 shr 16) % 200
        val greenVal = abs(hash and 0x00FF00 shr 8) % 200
        val blueVal = abs(hash and 0x0000FF) % 200

        val darkerShade = 10
        val redDark = (redVal + darkerShade).coerceAtMost(255)
        val greenDark = (greenVal + darkerShade).coerceAtMost(255)
        val blueDark = (blueVal + darkerShade).coerceAtMost(255)

        darkerColor = Color(red = redDark, green = greenDark, blue = blueDark, alpha = 200)
        lighterColor = darkerColor.copy(alpha = 0.2f)

        return Pair(lighterColor, darkerColor)
    }

    fun getChatSessionIdentity(patientId: String, docId: String): String? {
        if (patientId.isNullOrEmpty()) {
            return null
        }
        return "${patientId}_${docId}"
    }

    fun getOwnerId(): String {
        var ownerId = ""
        OrbiUserManager.getUserTokenData()?.let {
            ownerId = it.oid + "_" + it.businessId
        }
        return ownerId
    }

    fun getNewSessionId(): String {
        return Utils.getNewSessionId()
    }

    fun buildChatContext(patient: PatientEntity?): ChatContext? {
        if (patient == null) {
            return null
        }
        val chatContext = ChatContext(
            patientId = patient.oid,
            patientName = patient.name
        )
        return chatContext
    }

    fun getChatContextFromString(chatContextString: String?): ChatContext? {
        if (chatContextString.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(chatContextString, ChatContext::class.java)
    }

    fun getAudioFileDuration(filePath: String): Long {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            return durationStr?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        } finally {
            retriever.release()
        }
    }

    fun formatDuration(durationMs: Long): String {
        val minutes = (durationMs / 1000) / 60
        val seconds = (durationMs / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getMonthStickyHeaderLabel(utc: Long?): String {
        if (utc == null) {
            return ""
        }
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        return sdf.format(Date(utc))
    }

    val listOfRandomColors = listOf(
        RandomColor(DarwinTouchYellowDark, DarwinTouchYellowBgDark),
        RandomColor(DarwinTouchGreen, DarwinTouchGreenBgDark),
        RandomColor(DarwinTouchPrimary, DarwinTouchPrimaryBgDark),
        RandomColor(DarwinTouchRed, DarwinTouchRedBgDark),
        RandomColor(DarwinTouchNeutral800, DarwinTouchNeutral100)
    )
}