package eka.dr.intl.common

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

object V2RxUtils {
    fun showToast(context: Context, msg: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
    fun getOwnerId(): String {
        var ownerId = ""
        OrbiUserManager.getUserTokenData()?.let {
            ownerId += it.businessId
            ownerId += "_"
            ownerId += it.oid
        }
        return ownerId
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
}

data class RandomColor(
    val text: Color,
    val background: Color,
)