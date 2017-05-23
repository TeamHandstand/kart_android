package us.handstand.kartwheel.mocks

import android.text.TextUtils
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito


object MockTextUtils {
    // TODO: Need to annotate test class with @RunWith(PowerMockRunner.class) and @PrepareForTest(TextUtils.class)
    fun setUp() {
        PowerMockito.`when`(TextUtils.isEmpty(Mockito.any(CharSequence::class.java))).then({
            val str = it.getArgument<CharSequence>(0)
            (str == null || str.length == 0)
        })
    }
}