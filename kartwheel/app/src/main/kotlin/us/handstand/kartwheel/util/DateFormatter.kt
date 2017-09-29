package us.handstand.kartwheel.util


import android.text.TextUtils.isEmpty
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    val DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.SSS"
    val BACKUP_DATE_FORMAT_STRING = "yyyy-MM-dd\'T\'HH:mm:ss"
    val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH)
    val backupDateFormat = SimpleDateFormat(BACKUP_DATE_FORMAT_STRING, Locale.ENGLISH)
    val timeOfDayFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    const val millisecondsPerMinute = 60L * 1000L

    init {
        val utcTimeZone = TimeZone.getTimeZone("UTC")
        inputFormat.timeZone = utcTimeZone
        dateFormat.timeZone = utcTimeZone
        backupDateFormat.timeZone = utcTimeZone
    }

    operator fun get(dateString: String): Date? {
        try {
            return dateFormat.parse(dateString)
        } catch (e: Exception) {
            try {
                return backupDateFormat.parse(dateString)
            } catch (e: Exception) {
                return null
            }
        }
    }

    operator fun get(dateLong: Long): Date? {
        val dateString = getString(Date(dateLong))
        try {
            return dateFormat.parse(dateString)
        } catch (e: Exception) {
            try {
                return backupDateFormat.parse(dateString)
            } catch (e: Exception) {
                return null
            }
        }
    }

    fun getString(date: Date): String {
        return dateFormat.format(date)
    }

    fun getTimeOfDay(date: Date?): String {
        return if (date == null) "" else timeOfDayFormat.format(date)
    }

    fun getFromUserInput(input: String): String {
        return dateFormat.format(inputFormat.parse(input))
    }

    fun isValid(input: String): Boolean {
        try {
            val date = inputFormat.parse(input)
            return !isEmpty(input) && date != null
        } catch (e: Exception) {
            return false
        }
    }

}
