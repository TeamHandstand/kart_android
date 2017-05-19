package us.handstand.kartwheel.util

import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Test

class StringUtilTest {
    val timeInMs = 1495178800000L
    val startTimeInMs = 1495183110000L
    @Test
    fun getHoursFromStartTime() {
        val hours = StringUtil.hoursFromTotalTime(startTimeInMs - timeInMs)
        assertThat(hours, CoreMatchers.`is`(1f))
    }

    @Test
    fun getMinutesFromStartTime() {
        val minutes = StringUtil.minutesFromTotalTime(startTimeInMs - timeInMs)
        assertThat(minutes, CoreMatchers.`is`(11f))
    }

    @Test
    fun getSecondsFromStartTime() {
        val seconds = StringUtil.secondsFromTotalTime(startTimeInMs - timeInMs)
        assertThat(seconds, CoreMatchers.`is`(50f))
    }

    @Test
    fun getHourMinuteSecondFromStartTime() {
        val result = StringUtil.hourMinSecFromMs(startTimeInMs - timeInMs)
        assertThat(result, CoreMatchers.`is`("01:11:50"))
    }
}

