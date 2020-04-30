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
        String sizeText = "Some size";
        String activityText = "Some activity";
        String unitText = "Some unit";
        String equipmentText = "Some equipment";
        String remarksText = "Some remarks";

        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.salute_report_name)).perform(
                replaceText("AUniqueReportNameThatShouldNeverExist"), closeSoftKeyboard());

        onView(withId(android.R.id.button1)).perform(scrollTo(), click());

        onView(withId(R.id.editText)).perform(
            scrollTo(), replaceText(sizeText), closeSoftKeyboard());

        clickNext();

        onView(withId(R.id.editText)).perform(
            scrollTo(), replaceText(activityText), closeSoftKeyboard());

        clickNext();

        onView(allOf(withId(R.id.switch_get_from_map), withText("Get from Map")))
                .perform(scrollTo(), click());

        onView(withId(R.id.map_view)).check(matches(isDisplayed()));

        clickNext();

        onView(withId(R.id.editText))
                .perform(scrollTo(), replaceText(unitText), closeSoftKeyboard());

        clickNext();

        onView(withText("Select Date/Time")).perform(scrollTo(), click());

        onView(withText("OK")).perform(scrollTo(), click());
        onView(withText("OK")).perform(scrollTo(), click());

        clickNext();

        onView(withId(R.id.editText))
                .perform(scrollTo(), replaceText(equipmentText), closeSoftKeyboard());

        clickNext();

        onView(withId(R.id.editText))
                .perform(scrollTo(), replaceText(remarksText), closeSoftKeyboard());

        onView(withText("Create Report")).perform(scrollTo(), click());

        // Navigate into report details
        onView(allOf(withText("AUniqueReportNameThatShouldNeverExist"), withId(R.id.report_name))).perform(click());

        // Check that text fields are what they should be.
        // Time and location are difficult to assert here so are left out.
        onView(withId(R.id.size)).check(matches(withText(sizeText)));
        onView(withId(R.id.activity)).check(matches(withText(activityText)));
        onView(withId(R.id.unit)).check(matches(withText(unitText)));
        onView(withId(R.id.equipment)).check(matches(withText(equipmentText)));
        onView(withId(R.id.remarks)).check(matches(withText(remarksText)));

        onView(withContentDescription("Navigate up")).perform(click());
        onView(withText("AUniqueReportNameThatShouldNeverExist")).check(matches(isDisplayed()));

        onView(allOf(withText("AUniqueReportNameThatShouldNeverExist"), withId(R.id.report_name))).perform(longClick());

        // Delete report
        onView(withId(R.id.action_delete)).perform(click());
    }

    private void clickNext()
    {
        onView(withText("Next")).perform(scrollTo(), click());
    }
}
