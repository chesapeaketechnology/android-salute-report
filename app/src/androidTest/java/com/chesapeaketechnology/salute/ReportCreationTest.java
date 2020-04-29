package com.chesapeaketechnology.salute;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReportCreationTest
{

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void reportCreationTest()
    {
        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.salute_report_name)).perform(
                replaceText("AUniqueReportNameThatShouldNeverExist"), closeSoftKeyboard());

        onView(withId(android.R.id.button1)).perform(scrollTo(), click());

        onView(withId(R.id.editText)).perform(
            scrollTo(), replaceText("Some size"), closeSoftKeyboard());

        clickNext();

        onView(withId(R.id.editText)).perform(
            scrollTo(), replaceText("Some activity"), closeSoftKeyboard());

        clickNext();

        onView(allOf(withId(R.id.switch_get_from_map), withText("Get from Map")))
                .perform(scrollTo(), click());

        onView(withId(R.id.map_view)).check(matches(isDisplayed()));

        clickNext();

        onView(withId(R.id.editText))
                .perform(scrollTo(), replaceText("some unit"), closeSoftKeyboard());

        clickNext();

        onView(withText("Select Date/Time")).perform(scrollTo(), click());

        onView(withText("OK")).perform(scrollTo(), click());
        onView(withText("OK")).perform(scrollTo(), click());

        clickNext();

        onView(withId(R.id.editText))
                .perform(scrollTo(), replaceText("Some equipment"), closeSoftKeyboard());

        clickNext();

        onView(withId(R.id.editText))
                .perform(scrollTo(), replaceText("Some remarks"), closeSoftKeyboard());

        onView(withText("Create Report")).perform(scrollTo(), click());

        onView(allOf(withText("AUniqueReportNameThatShouldNeverExist"), withId(R.id.report_name))).perform(longClick());

        onView(withId(R.id.action_delete)).perform(click());
    }

    private void clickNext()
    {
        onView(withText("Next")).perform(scrollTo(), click());
    }
}
