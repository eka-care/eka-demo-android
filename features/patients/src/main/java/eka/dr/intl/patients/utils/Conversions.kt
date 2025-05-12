package eka.dr.intl.patients.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

class Conversions {
   companion object{
       fun fromTimestampToLong(timestamp: String?): Long? {
           if (timestamp.isNullOrEmpty()) return null
           try {
               val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
               inputFormat.timeZone = TimeZone.getTimeZone("UTC")
               val c: Calendar = GregorianCalendar()
               c.time = timestamp?.let { inputFormat.parse(it) }
               return c.time.time
           } catch (e: Exception) {
               return null
           }
       }

       fun formYYYYMMDDToLong(value: String?): Long? {
           if (value.isNullOrEmpty()) return null
           try {
               val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
               outputFormat.timeZone = TimeZone.getTimeZone("UTC")
               val c = Calendar.getInstance()
               c.time = value?.let { outputFormat.parse(it) }
               return c.time.time
           } catch (e: Exception) {
               return null
           }
       }

       fun fromLongToTimestamp(value: Long): String {
           val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
           outputFormat.timeZone = TimeZone.getTimeZone("UTC")
           return outputFormat.format(value)
       }
   }
}