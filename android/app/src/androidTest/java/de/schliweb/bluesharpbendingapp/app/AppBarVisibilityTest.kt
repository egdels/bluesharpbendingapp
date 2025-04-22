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
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the app bar visibility toggling functionality.
 *
 * This test class verifies that the app bar can be hidden and shown by tapping on the screen,
 * as implemented in the MainActivity.java file.
 */
@RunWith(AndroidJUnit4::class)
class AppBarVisibilityTest {

    // Grant microphone permission for these tests
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)


    /**
     * Tests that tapping on the screen toggles the app bar visibility.
     */
    @Test
    fun testAppBarVisibilityToggle() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Navigate to the Harp fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_harp)).perform(click())

            // Verify that the app bar is initially visible
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

            // Tap on the screen to hide the app bar
            onView(withId(R.id.harp_table)).perform(click())

            // Verify that the app bar is now hidden
            // Note: Since the app bar is hidden by calling hide() on the ActionBar,
            // we can't directly check its visibility. Instead, we'll check if the
            // options menu is no longer accessible.
            onView(withContentDescription("More options")).check(matches(not(isDisplayed())))

            // Tap on the screen again to show the app bar
            onView(withId(R.id.harp_table)).perform(click())

            // Verify that the app bar is visible again
            onView(withContentDescription("More options")).check(matches(isDisplayed()))
        }
    }

    /**
     * Tests that the app bar visibility toggle works in the Training fragment as well.
     */
    @Test
    fun testAppBarVisibilityToggleInTrainingFragment() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Navigate to the Training fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_training)).perform(click())

            // Verify that the app bar is initially visible
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

            // Tap on the screen to hide the app bar
            onView(withId(R.id.training_note)).perform(click())

            // Verify that the app bar is now hidden
            onView(withContentDescription("More options")).check(matches(not(isDisplayed())))

            // Tap on the screen again to show the app bar
            onView(withId(R.id.training_note)).perform(click())

            // Verify that the app bar is visible again
            onView(withContentDescription("More options")).check(matches(isDisplayed()))
        }
    }
}