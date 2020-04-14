package com.chesapeaketechnology.salute;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;

@RunWith(AndroidJUnit4.class)
public class NavigationTest
{
    @Test
    public void testNavigationToCreateReport()
    {
        // Create a TestNavHostController
        TestNavHostController navController = new TestNavHostController(ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.nav_graph);

        // Create a graphical FragmentScenario for the HomeFragment
        FragmentScenario<HomeFragment> titleScenario = FragmentScenario.launchInContainer(HomeFragment.class);

        // Set the NavController property on the fragment
        titleScenario.onFragment(fragment -> Navigation.setViewNavController(fragment.requireView(), navController));

        // Verify that performing a click changes the NavController’s state
        onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click());

        Assert.assertEquals(R.id.FirstFragmentSize, navController.getCurrentDestination().getId());
    }
}
