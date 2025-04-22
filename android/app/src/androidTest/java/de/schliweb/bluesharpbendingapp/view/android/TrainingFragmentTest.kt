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
 * UI tests for the TrainingFragment.
 *
 * This test class verifies that the TrainingFragment displays correctly
 * and that user interactions with the training controls work as expected.
 */
@RunWith(AndroidJUnit4::class)
class TrainingFragmentTest {

    // Grant microphone permission for these tests
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)


    /**
     * Tests that all training labels are displayed when the fragment is launched.
     */
    @Test
    fun testTrainingLabelsAreDisplayed() {
        // Launch the TrainingFragment
        launchFragmentInContainer<TrainingFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that all training labels are displayed
        onView(withId(R.id.training_trainings_label)).check(matches(isDisplayed()))
        onView(withId(R.id.training_precision_label)).check(matches(isDisplayed()))
        onView(withId(R.id.training_progress_label)).check(matches(isDisplayed()))
    }

    /**
     * Tests that all training controls are displayed when the fragment is launched.
     */
    @Test
    fun testTrainingControlsAreDisplayed() {
        // Launch the TrainingFragment
        launchFragmentInContainer<TrainingFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that all training controls are displayed
        onView(withId(R.id.training_trainings_list)).check(matches(isDisplayed()))
        onView(withId(R.id.training_precision_list)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.training_start_button)).check(matches(isDisplayed()))
    }

    /**
     * Tests that the training note is displayed when the fragment is launched.
     */
    @Test
    fun testTrainingNoteIsDisplayed() {
        // Launch the TrainingFragment
        launchFragmentInContainer<TrainingFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that the training note is displayed
        onView(withId(R.id.training_note)).check(matches(isDisplayed()))
    }

    /**
     * Tests that the start button is clickable and changes text when clicked.
     */
    @Test
    fun testStartButtonIsClickable() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Open the options menu
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_training)).perform(click())

            // Verify that the start button is clickable
            onView(withId(R.id.training_start_button)).check(matches(isClickable()))

            // Verify that the start button has the correct initial text
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_start_button)))

            // Click the start button (this will not verify the actual functionality,
            // as that would require mocking the handlers)
            onView(withId(R.id.training_start_button)).perform(click())

            // Verify that the button text changes to stop after clicking
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_stop_button)))

            // Click the stop button to toggle back
            onView(withId(R.id.training_start_button)).perform(click())

            // Verify that the button text changes back to start
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_start_button)))
        }

    }
}