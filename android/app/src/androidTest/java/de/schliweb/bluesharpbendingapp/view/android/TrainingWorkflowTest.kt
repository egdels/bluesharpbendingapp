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
 * UI tests for a complete training workflow.
 *
 * This test class simulates a user going through a complete training workflow:
 * 1. Navigate to the Training fragment
 * 2. Select a training type
 * 3. Select a precision level
 * 4. Start the training
 * 5. Verify the training is running
 * 6. Stop the training
 * 7. Verify the training has stopped
 */
@RunWith(AndroidJUnit4::class)
class TrainingWorkflowTest {

    // Grant microphone permission for these tests
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    /**
     * Tests a complete training workflow.
     */
    @Test
    fun testCompleteTrainingWorkflow() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Navigate to the Training fragment
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_training)).perform(click())

            // Verify that we're on the Training fragment by checking for training-specific views
            onView(withId(R.id.training_start_button)).check(matches(isDisplayed()))
            onView(withId(R.id.training_note)).check(matches(isDisplayed()))

            // Select the first training type (assuming it's a spinner or list)
            onView(withId(R.id.training_trainings_list)).perform(click())
            // This might need adjustment based on how the list is implemented
            onView(withText("DUMMY")).perform(click())

            // Select the first precision level (assuming it's a spinner or list)
            onView(withId(R.id.training_precision_list)).perform(click())
            // This might need adjustment based on how the list is implemented
            onView(withText("15")).perform(click())

            // Start the training
            onView(withId(R.id.training_start_button)).perform(click())

            // Verify that the training is running (button text should change to "Stop")
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_stop_button)))

            // Verify that the training note is displayed
            onView(withId(R.id.training_note)).check(matches(isDisplayed()))

            // Stop the training
            onView(withId(R.id.training_start_button)).perform(click())

            // Verify that the training has stopped (button text should change back to "Start")
            onView(withId(R.id.training_start_button)).check(matches(withText(R.string.training_start_button)))
        }
    }
}
