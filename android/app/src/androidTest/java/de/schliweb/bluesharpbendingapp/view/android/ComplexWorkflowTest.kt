package de.schliweb.bluesharpbendingapp.view.android
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
import de.schliweb.bluesharpbendingapp.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for complex user workflows that span multiple fragments.
 *
 * This test class verifies that users can navigate between different parts of the application
 * and that the application behaves correctly during these workflows.
 */
@RunWith(AndroidJUnit4::class)
class ComplexWorkflowTest {

    // Grant microphone permission for these tests
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)


    /**
     * Tests a workflow where the user navigates from the Harp fragment to the Settings fragment,
     * changes a setting, and then returns to the Harp fragment.
     */
    @Test
    fun testHarpToSettingsAndBackWorkflow() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Navigate to the Harp fragment if not already there
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_harp)).perform(click())

            // Verify that we're on the Harp fragment
            onView(withId(R.id.harp_table)).check(matches(isDisplayed()))

            // Navigate to the Settings fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_settings)).perform(click())

            // Verify that we're on the Settings fragment
            onView(withId(R.id.settings_screenlock)).check(matches(isDisplayed()))

            // Toggle the screen lock setting
            onView(withId(R.id.settings_screenlock)).perform(click())

            // Navigate back to the Harp fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_harp)).perform(click())

            // Verify that we're back on the Harp fragment
            onView(withId(R.id.harp_table)).check(matches(isDisplayed()))
        }
    }

    /**
     * Tests a workflow where the user navigates from the Training fragment to the Settings fragment,
     * changes a setting, and then returns to the Training fragment.
     */
    @Test
    fun testTrainingToSettingsAndBackWorkflow() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Navigate to the Training fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_training)).perform(click())

            // Verify that we're on the Training fragment
            onView(withId(R.id.training_note)).check(matches(isDisplayed()))

            // Navigate to the Settings fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_settings)).perform(click())

            // Verify that we're on the Settings fragment
            onView(withId(R.id.settings_screenlock)).check(matches(isDisplayed()))

            // Toggle the screen lock setting
            onView(withId(R.id.settings_screenlock)).perform(click())

            // Navigate back to the Training fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_training)).perform(click())

            // Verify that we're back on the Training fragment
            onView(withId(R.id.training_note)).check(matches(isDisplayed()))
        }
    }

    /**
     * Tests a workflow where the user navigates through all main fragments in the application.
     */
    @Test
    fun testNavigateThroughAllFragments() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Navigate to the Harp fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_harp)).perform(click())

            // Verify that we're on the Harp fragment
            onView(withId(R.id.harp_table)).check(matches(isDisplayed()))

            // Navigate to the Training fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_training)).perform(click())

            // Verify that we're on the Training fragment
            onView(withId(R.id.training_note)).check(matches(isDisplayed()))

            // Navigate to the Settings fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_settings)).perform(click())

            // Verify that we're on the Settings fragment
            onView(withId(R.id.settings_screenlock)).check(matches(isDisplayed()))

            // Navigate to the About fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_about)).perform(click())

            // Verify that we're on the About fragment
            onView(withId(R.id.about_title)).check(matches(isDisplayed()))
        }
    }
}
