package us.handstand.kartwheel.util


object StringUtil {
    const val msInDay = 24 * 60 * 60
    const val msInHour = 60 * 60
    const val msInMinute = 60
    const val msInSecond = 100

    fun daysFromTotalTime(time: Long): Long {
        return time / msInDay
    }

    fun hoursFromTotalTime(time: Long): Float {
        val hours = time * (1f / 1000f) * (1f / 60f) * (1f / 60f)
        return (hours % 60) - (hours % 1)
    }

    fun minutesFromTotalTime(time: Long): Float {
        val minutes = time * (1f / 1000f) * (1f / 60f)
        return (minutes % 60) - (minutes % 1)
    }

    fun secondsFromTotalTime(time: Long): Float {
        val seconds = time * (1f / 1000f)
        return (seconds % 60) - (seconds % 1)
    }

    fun millisecondsFromTotalTime(time: Double): Double {
        val remainder = (time % 1)
        return if (remainder > 99.5) 99.4 else remainder //only want 2 decimal points, and the String formatting rounds up from 99.5
    }

    fun minSecFromSeconds(time: Long): String {
        val minutes = minutesFromTotalTime(time)
        val seconds = secondsFromTotalTime(time)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun minSecMilliFromMs(time: Double?): String {
        if (time == null) {
            return "--:--.--"
        }
        val minutes = minutesFromTotalTime(time.toLong())
        val seconds = secondsFromTotalTime(time.toLong())
        val milliSeconds = millisecondsFromTotalTime(time)
        return String.format("%02d:%02d.%02d", minutes, seconds, milliSeconds)
    }

    fun hourMinSecFromMs(time: Long?): String {
        if (time == null) {
            return "--:--:--"
        }
        val hours = hoursFromTotalTime(time).toLong()
        val minutes = minutesFromTotalTime(time).toLong()
        val seconds = secondsFromTotalTime(time).toLong()
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}