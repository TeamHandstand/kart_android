package us.handstand.kartwheel.util


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val DATE_FORMAT_STRING = "yyyy-MM-dd\'T\'HH:mm:ss\'.\'SSSZ"
    private val BACKUP_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss\'.\'SSS"

    operator fun get(dateString: String): Date? {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
        try {
            return format.parse(dateString)
        } catch (e: ParseException) {
            return null
        }
    }

    fun getString(date: Date?): String {
        val format = SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH)
        return format.format(date)
    }
}
