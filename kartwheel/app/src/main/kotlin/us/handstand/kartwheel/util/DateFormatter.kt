package us.handstand.kartwheel.util


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    val DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss z"
    val BACKUP_DATE_FORMAT_STRING = "yyyy-MM-dd\'T\'HH:mm:ss z"
    val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH)
    val backupDateFormat = SimpleDateFormat(BACKUP_DATE_FORMAT_STRING, Locale.ENGLISH)

    init {
        val utcTimeZone = TimeZone.getTimeZone("UTC")
        inputFormat.timeZone = utcTimeZone
        dateFormat.timeZone = utcTimeZone
        backupDateFormat.timeZone = utcTimeZone
    }

    operator fun get(dateString: String): Date {
        try {
            return inputFormat.parse(dateString)
        } catch (e: ParseException) {
            try {
                return dateFormat.parse(dateString)
            } catch (e: ParseException) {
                try {
                    return backupDateFormat.parse(dateString)
                } catch (e: ParseException) {
                    return Date()
                }
            }
        }
    }

    fun getString(date: Date): String {
        return dateFormat.format(date)
    }
}
