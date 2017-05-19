package us.handstand.kartwheel.activity;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import us.handstand.kartwheel.KartWheel;
import us.handstand.kartwheel.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class TicketActivityTest {
    @Rule
    public ActivityTestRule<TicketActivity> testRule = new ActivityTestRule<>(TicketActivity.class);

    @After
    public void tearDown() {
        KartWheel.Companion.logout();
    }

    @Test
    public void show_codeEntry_whenFinishedReadingTOS() {
        onView(withId(R.id.tos_scroll_view)).perform(swipeUp(), swipeUp(), swipeUp(), swipeUp(), swipeUp(), swipeUp());
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.code_edit_text)).check(matches(isDisplayed()));
    }
}
