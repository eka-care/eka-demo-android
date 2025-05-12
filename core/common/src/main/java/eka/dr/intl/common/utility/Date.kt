package eka.dr.intl.common.utility

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtils {
    companion object {
        fun getCurrentEpochTime(): Long {
            return Date().time
        }

        fun getUTCDateFromEpoch(epoch: Long?): String {
            if (epoch == null || epoch == 0L) return ""
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date(epoch))
        }

        fun getAgeFromYYYYMMDD(yyyyMMDD: String?): String {
            return try {
                if (yyyyMMDD?.trim().isNullOrEmpty()) return ""

                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val birthDate = formatter.parse(yyyyMMDD) ?: return ""
                val currentDate = Date()

                val diff = currentDate.time - birthDate.time
                val diffDays = diff / (24 * 60 * 60 * 1000)
                val diffYears = diffDays / 365
                val diffMonths = (diffDays % 365) / 30

                val years = diffYears
                val months = diffMonths

                buildString {
                    if (years != 0L) {
                        append("${years}y")
                    }
                    if (months != 0L) {
                        append("${if (years == 0L) "" else ", "}${months}m")
                    }
                }.trim()
            } catch (e: Exception) {
                ""
            }
        }


        fun getAgeFromYYYYMMDDNumber(yyyyMMDD: String?): Long {
            if (yyyyMMDD.isNullOrEmpty()) return 0L

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birthDate = formatter.parse(yyyyMMDD)
            val currentDate = Date()

            val diff = currentDate.time - birthDate.time
            val diffDays = diff / (24 * 60 * 60 * 1000)
            val diffYears = diffDays / 365
            val diffMonths = (diffDays % 365) / 30

            val months = diffMonths


            return diffYears
        }


        fun convertLongToDateString(time: Long?): String {
            if (time == null) return ""
            val date = Date(time)
            val format =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return format.format(date)
        }


        fun convertDateTimeWithZone(dateTimeString: String): String {
            if (dateTimeString.isNullOrEmpty()) {
                return ""
            }
            // Define the input date format including milliseconds and time zone
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            // Define the output date format
            val outputFormat = SimpleDateFormat("dd MMM’yy, hh:mm a", Locale.getDefault())

            return try {
                // Parse the input string to a Date object
                val date: Date = inputFormat.parse(dateTimeString)!!

                // Format the Date object to the desired output format
                outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        fun calculateAge(birthTimestamp: Long): Int {
            val currentTimeMillis = System.currentTimeMillis()
            val ageInMillis = currentTimeMillis - birthTimestamp

            val millisInYear = 365.25 * 24 * 60 * 60 * 1000
            val age = (ageInMillis / millisInYear).toInt()

            return age
        }

        fun getTimeDiff(inputString: String?): String {
            if (inputString.isNullOrEmpty()) {
                return ""
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val targetDateTime = formatter.parse(inputString)
            val currentDateTime = Date()
            val duration = currentDateTime.time - targetDateTime.time

            val minutes = duration / (1000 * 60) % 60
            val hours = duration / (1000 * 60 * 60) % 24
            val days = duration / (1000 * 60 * 60 * 24)

            var diffStr = ""
            if (days > 0) {
                diffStr += "$days days, "
            }
            if (hours > 0) {
                diffStr += "$hours hr, "
            }
            if (minutes > 0) {
                diffStr += "$minutes min"
            }

            return diffStr
        }


        fun getAgeInMonths(age: String?): Int {
            if (age.isNullOrEmpty()) return 0
            val dob = age.split("-")
            val year = dob[0].toInt()
            val month = dob[1].toInt()
            val day = dob[2].toInt()
            // initially i was using local date time which is not supported by older version but
            // Calender instance is accepted widely
            val currentCalendar = Calendar.getInstance()
            val currentYear = currentCalendar.get(Calendar.YEAR)
            val currentMonth = currentCalendar.get(Calendar.MONTH) + 1
            val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)

            var ageInMonths = (currentYear - year) * 12 + (currentMonth - month)
            if (currentDay < day) {
                ageInMonths--
            }
            return ageInMonths
        }

        fun convertTimeStampToHHMMA(epochSeconds: Long?): String {
            if (epochSeconds == null) return ""
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = Date(epochSeconds * 1000)
            return sdf.format(date)
        }
    }

}

