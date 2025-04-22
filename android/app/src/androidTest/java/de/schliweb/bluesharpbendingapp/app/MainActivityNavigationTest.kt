package de.schliweb.bluesharpbendingapp.app
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import de.schliweb.bluesharpbendingapp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for MainActivity navigation.
 *
 * This test class verifies that the navigation between different fragments
 * in the MainActivity works correctly. It tests the menu options to navigate
 * to the Harp, Settings, Training, and About fragments.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityNavigationTest {

    // Grant microphone permission for these tests
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)


    /**
     * Tests navigation to the Settings fragment.
     *
     * This test verifies that clicking on the Settings menu item
     * correctly navigates to the Settings fragment.
     */
    @Test
    fun testNavigationToSettings() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Open the options menu
            onView(withContentDescription("More options")).perform(click())

            // Click on the Settings menu item
            onView(withText(R.string.action_settings)).perform(click())

            // Verify that we're on the Settings fragment by checking for a settings-specific view
            onView(withId(R.id.settings_container)).check(matches(isDisplayed()))
        }
    }

    /**
     * Tests navigation to the About fragment.
     *
     * This test verifies that clicking on the About menu item
     * correctly navigates to the About fragment.
     */
    @Test
    fun testNavigationToAbout() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Open the options menu
            onView(withContentDescription("More options")).perform(click())

            // Click on the About menu item
            onView(withText(R.string.action_about)).perform(click())

            // Verify that we're on the About fragment by checking for an about-specific view
            onView(withId(R.id.about_container)).check(matches(isDisplayed()))
        }
    }

    /**
     * Tests navigation to the Harp fragment.
     *
     * This test verifies that clicking on the Harp menu item
     * correctly navigates to the Harp fragment.
     */
    @Test
    fun testNavigationToHarp() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Open the options menu
            onView(withContentDescription("More options")).perform(click())

            // Click on the Harp menu item
            onView(withText(R.string.action_harp)).perform(click())

            // Verify that we're on the Harp fragment by checking for a harp-specific view
            onView(withId(R.id.harp_container)).check(matches(isDisplayed()))
        }
    }

    /**
     * Tests navigation to the Training fragment.
     *
     * This test verifies that clicking on the Training menu item
     * correctly navigates to the Training fragment.
     */
    @Test
    fun testNavigationToTraining() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Open the options menu
            onView(withContentDescription("More options")).perform(click())

            // Click on the Training menu item
            onView(withText(R.string.action_training)).perform(click())

            // Verify that we're on the Training fragment by checking for a training-specific view
            onView(withId(R.id.training_container)).check(matches(isDisplayed()))
        }
    }
}