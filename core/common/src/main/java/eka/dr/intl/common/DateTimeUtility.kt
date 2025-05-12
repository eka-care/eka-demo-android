package eka.dr.intl.common

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

class DateTimeUtility {

    companion object {

        const val DD_MM_YYYY = "dd-MM-yyyy"
        const val YYYY_MM_DD = "yyyy-MM-dd"
        const val YYYY_MM_DD_HH = "yyyy-MM-dd'T'HH"

        fun isDatesEqual(timeToVerfiy: Long, referenceTime: Long): Boolean {
            // DD:MM:YY format
            val referenceTimeInStr = getDateOfTime(referenceTime)
            val referenceTimeTokens = referenceTimeInStr.split(":").toTypedArray()
            val verifyTimeInStr = getDateOfTime(timeToVerfiy)
            val verifyTimeTokens = verifyTimeInStr.split(":").toTypedArray()
            /* Log.d("log", "referenceTimeInStr is " + referenceTimeInStr +
                         "and verifyTimeInStr is " + verifyTimeInStr
             )*/
            // skip year to handle year movement
            return referenceTimeTokens[0] == verifyTimeTokens[0] && referenceTimeTokens[1] == verifyTimeTokens[1]
        }

        fun getDateOfTime(time: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            val mYear = calendar[Calendar.YEAR]
            val mMonth = calendar[Calendar.MONTH]
            val mDay = calendar[Calendar.DAY_OF_MONTH]
            return "$mDay:$mMonth:$mYear"
        }

        fun getLocalTimeFromUTCTime(utcTime: String): Long {
            var localTime: Long = 0
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val c = GregorianCalendar()
                c.time = inputFormat.parse(utcTime)
                localTime = c.time.time
            } catch (ex: Exception) {
                Log.e("log", "Exception in getLocalTimeFromUTCTime() = ", ex)
            }
            return localTime
        }

        fun getLocalTimeFromGMTTime(gmtTime: String): Long {
            var localTime: Long = 0
            try {
                val inputFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+05:30", Locale.getDefault())
                val c = GregorianCalendar()
                c.time = inputFormat.parse(gmtTime)
                localTime = c.time.time
            } catch (ex: Exception) {
                Log.e("log", "Exception in getLocalTimeFromGMTTime() = ", ex)

            }
            return localTime
        }

        // this function removes the time info and returns only date
        fun getLocalDateFromUTCTime(utcTime: String, format: String): Long {
            val inputFormat = SimpleDateFormat(format, Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val c = GregorianCalendar()
            c.time = inputFormat.parse(utcTime)
            c[Calendar.HOUR_OF_DAY] = 0
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 0
            c[Calendar.MILLISECOND] = 0
            return c.time.time
        }

        fun getLocalCalendarFromUTCTime(utcTime: String): GregorianCalendar {

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val c = GregorianCalendar()
            c.time = inputFormat.parse(utcTime)
            return c
        }

        fun getCurrentTime(): Long {
            val c = GregorianCalendar()
            c.time = Date()
            return c.time.time
        }

        fun getTimeAtHourOfDay(hour: Int): Long {
            val c: Calendar = GregorianCalendar()
            // c.add(Calendar.DAY_OF_MONTH, 1)
            c[Calendar.HOUR_OF_DAY] = hour //anything 0 - 23
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 0
            return c.time.time
        }

        fun getDateTimeInGivenFormat(timeStamp: Long, format: String): String {
            val dateFormat = SimpleDateFormat(format)
            return dateFormat.format(timeStamp)
        }

        fun getMonthString(month: Int): String {
            return when (month) {
                0 -> "Jan"
                1 -> "Feb"
                2 -> "Mar"
                3 -> "Apr"
                4 -> "May"
                5 -> "Jun"
                6 -> "Jul"
                7 -> "Aug"
                8 -> "Sep"
                9 -> "Oct"
                10 -> "Nov"
                11 -> "Dec"
                else -> ""
            }
        }

        fun checkIfTodayDate(utcTime: String): Boolean {
            val dateToCheck = getLocalTimeFromUTCTime(utcTime)
            val refDate = getCurrentTime()
            return isDatesEqual(dateToCheck, refDate)

        }

        fun checkIfTomorrowDate(utcTime: String): Boolean {
            val dateToCheck = getLocalTimeFromUTCTime(utcTime)
            val refDate = getCurrentTime()
            return isDatesEqual(dateToCheck, refDate)
        }

        fun getAgeFromDob(dob: String): String {
            if (dob.isNullOrEmpty()) return "NA"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val birthdate = dateFormat.parse(dob)
            val currentDate = dateFormat.parse(getCurrentTimeAsISO())
            return if (birthdate != null && currentDate != null) {
                val ageInMillis = currentDate.time - birthdate.time
                val ageInYears = ageInMillis / (1000L * 60 * 60 * 24 * 365)
                ageInYears.toString()
            } else {
                ""
            }

        }

        fun getFormattedDate(time: Long, defaultDateFormat: String = "dd/MM/yyyy"): String? {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = time
            val now = Calendar.getInstance()
            return when {
                now[Calendar.DATE] === smsTime[Calendar.DATE] -> {
                    "Today"
                }

                now[Calendar.DATE] - smsTime[Calendar.DATE] === 1 -> {
                    "Yesterday"
                }

                else -> {
                    getDateTimeInGivenFormat(time, defaultDateFormat)
                }
            }
        }

        fun getCurrentTimeAsISO(): String {
            val tz = TimeZone.getTimeZone("UTC")
            val df: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") // Quoted "Z" to indicate UTC, no timezone offset
            df.timeZone = tz

            return df.format(Date())
        }


        fun formatTime(hourOfDay: Int, minute: Int): String {
            val formattedTime: String = when {
                hourOfDay == 0 -> {
                    if (minute < 10) {
                        "${hourOfDay + 12}:0${minute} am"
                    } else {
                        "${hourOfDay + 12}:${minute} am"
                    }
                }

                hourOfDay > 12 -> {
                    if (minute < 10 && hourOfDay - 12 < 10) {
                        "0${hourOfDay - 12}:0${minute} pm"
                    } else if (minute < 10) {
                        "${hourOfDay - 12}:0${minute} pm"
                    } else if (hourOfDay - 12 < 10) {
                        "0${hourOfDay - 12}:${minute} pm"
                    } else {
                        "${hourOfDay - 12}:${minute} pm"
                    }
                }

                hourOfDay == 12 -> {
                    if (minute < 10) {
                        "${hourOfDay}:0${minute} pm"
                    } else {
                        "${hourOfDay}:${minute} pm"
                    }
                }

                else -> {
                    if (minute < 10 && hourOfDay < 10) {
                        "0${hourOfDay}:0${minute} am"
                    } else if (minute < 10) {
                        "${hourOfDay}:0${minute} am"
                    } else if (hourOfDay < 10) {
                        "0${hourOfDay}:${minute} am"
                    } else {
                        "${hourOfDay}:${minute} am"
                    }
                }
            }
            return formattedTime
        }

        fun convertDateFormat(
            dateString: String,
            currentFormat: String,
            targetFormat: String,
        ): String? {
            return try {
                val currentDateFormat = SimpleDateFormat(currentFormat)
                val targetDateFormat = SimpleDateFormat(targetFormat)

                val date = currentDateFormat.parse(dateString)
                targetDateFormat.format(date)
            } catch (_: Exception) {
                null
            }
        }

        fun convertDate(time: String): String {
            return try {
                val df = SimpleDateFormat("HH:mm")
                val pf = SimpleDateFormat("hh:mm a")
                val date = pf.parse(time)
                df.format(date)
            } catch (e: java.lang.Exception) {
                ""
            }
        }
    }
}