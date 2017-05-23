package us.handstand.kartwheel.activity;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import us.handstand.kartwheel.KartWheel;
import us.handstand.kartwheel.R;
import us.handstand.kartwheel.layout.TOSScrollView;
import us.handstand.kartwheel.mocks.MockAPI;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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
        ((TOSScrollView) testRule.getActivity().findViewById(R.id.tos_scroll_view)).getListener().invoke();
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.code_edit_text)).check(matches(isDisplayed()));
    }

    // Best case scenario
    @Test
    public void show_criticalInfo_whenEnteredCode() {
        // Setup mock server
        MockAPI mockApi = new MockAPI();
        mockApi.getServer().enqueue(new MockResponse().setBody(MockAPI.teamResponse));
        mockApi.getServer().enqueue(new MockResponse().setBody(MockAPI.eventResponse));

        // Scroll TOS
        ((TOSScrollView) testRule.getActivity().findViewById(R.id.tos_scroll_view)).getListener().invoke();
        onView(withId(R.id.button)).perform(click());

        // Enter the code
        onView(withId(R.id.code_edit_text)).perform(replaceText("matt"));
        onView(withId(R.id.button)).perform(click());

        // Click on waffles
        onView(withId(R.id.left_image)).perform(click());
        onView(withId(R.id.button)).perform(click());

        // Click on squirtle
        onView(withId(R.id.right_image)).perform(click());
        onView(withId(R.id.button)).perform(click());


        mockApi.getServer().enqueue(new MockResponse().setBody(MockAPI.userUpdateResponse));
        // Enter information
        onView(withId(R.id.first_name)).perform(replaceText("Matthew"));
        onView(withId(R.id.last_name)).perform(replaceText("Ott"));
        onView(withId(R.id.email)).perform(replaceText("matthew.w.ott@gmail.com"));
        onView(withId(R.id.cell)).perform(replaceText("4083064285"));
        onView(withId(R.id.birth)).perform(replaceText("07251989"));
        onView(withId(R.id.nickname)).perform(replaceText("Matty Otter"));
        onView(withId(R.id.button)).perform(click());

        onView(withId(R.id.title)).check(matches(withText(R.string.onboarding_started_title)));
    }
}

