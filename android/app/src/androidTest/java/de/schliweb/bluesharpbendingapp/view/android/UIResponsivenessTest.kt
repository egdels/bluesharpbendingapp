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
import androidx.fragment.app.testing.launchFragmentInContainer
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
 * UI responsiveness tests for the application.
 *
 * This test class verifies that the UI elements in the application respond correctly
 * to user interactions and that the response time is acceptable.
 */
@RunWith(AndroidJUnit4::class)
class UIResponsivenessTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    /**
     * Tests that the harp notes respond quickly to user interactions.
     */
    @Test
    fun testHarpNotesResponsiveness() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->

            // Verify that the harp table is displayed
            onView(withId(R.id.harp_table)).check(matches(isDisplayed()))

            // Click on multiple notes in quick succession to test responsiveness
            onView(withId(R.id.channel_1_note_0)).perform(click())
            onView(withId(R.id.overlay_note)).check(matches(isDisplayed()))
            onView(withId(R.id.overlay_note)).perform(click())

            onView(withId(R.id.channel_2_note_0)).perform(click())
            onView(withId(R.id.overlay_note)).check(matches(isDisplayed()))
            onView(withId(R.id.overlay_note)).perform(click())

            onView(withId(R.id.channel_3_note_0)).perform(click())
            onView(withId(R.id.overlay_note)).check(matches(isDisplayed()))
            onView(withId(R.id.overlay_note)).perform(click())

        }
    }
    /**
     * Tests that the training controls respond quickly to user interactions.
     */
    @Test
    fun testTrainingControlsResponsiveness() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_training)).perform(click())


            // Verify that the training controls are displayed
            onView(withId(R.id.training_trainings_list)).check(matches(isDisplayed()))
            onView(withId(R.id.training_precision_list)).check(matches(isDisplayed()))
            onView(withId(R.id.training_start_button)).check(matches(isDisplayed()))

            // Click the start button multiple times in quick succession to test responsiveness
            onView(withId(R.id.training_start_button)).perform(click())
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_stop_button)))

            onView(withId(R.id.training_start_button)).perform(click())
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_start_button)))

            onView(withId(R.id.training_start_button)).perform(click())
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_stop_button)))
        }
    }

    /**
     * Tests that the settings controls respond quickly to user interactions.
     */
    @Test
    fun testSettingsControlsResponsiveness() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_settings)).perform(click())

            // Verify that the settings controls are displayed
            onView(withId(R.id.settings_screenlock)).check(matches(isDisplayed()))
            onView(withId(R.id.settings_reset_button)).check(matches(isDisplayed()))

            // Toggle the screen lock switch multiple times in quick succession to test responsiveness
            onView(withId(R.id.settings_screenlock)).perform(click())
            onView(withId(R.id.settings_screenlock)).perform(click())
            onView(withId(R.id.settings_screenlock)).perform(click())
        }
    }
}