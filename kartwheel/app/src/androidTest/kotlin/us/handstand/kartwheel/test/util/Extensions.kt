package us.handstand.kartwheel.test.util

import android.graphics.drawable.ShapeDrawable
import android.support.test.espresso.intent.Checks
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


fun withBackground(background: Int): Matcher<View> {
    Checks.checkNotNull(background)
    return object: TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("with background: $background")
        }

        override fun matchesSafely(item: View): Boolean =
                (item.background as ShapeDrawable).paint.color == background
    }
}