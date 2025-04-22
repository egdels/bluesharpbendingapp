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
 * UI tests for the HarpFragment.
 *
 * This test class verifies that the HarpFragment displays correctly
 * and that user interactions with the harp notes work as expected.
 */
@RunWith(AndroidJUnit4::class)
class HarpFragmentTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)


    /**
     * Tests that the harp table is displayed when the fragment is launched.
     */
    @Test
    fun testHarpTableIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Verify that the harp table is displayed
            onView(withId(R.id.harp_table)).check(matches(isDisplayed()))
        }
    }

    /**
     * Tests that channel identifiers are displayed in the harp table.
     */
    @Test
    fun testChannelIdentifiersAreDisplayed() {
        // Launch the HarpFragment
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->

            // Verify that channel identifiers are displayed
            onView(withId(R.id.channel_1)).check(matches(isDisplayed()))
            onView(withId(R.id.channel_2)).check(matches(isDisplayed()))
            onView(withId(R.id.channel_3)).check(matches(isDisplayed()))

            // Add more channel checks as needed
        }
    }
    /**
     * Tests that clicking on a note displays the enlarged note overlay.
     */
    @Test
    fun testNoteEnlargementOnClick() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.overlay_note)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            // Click on a note (channel 1, note 0)
            onView(withId(R.id.channel_1_note_0))
                .check(matches(isDisplayed()))
                .perform(click())
            // Verify that the overlay note is now displayed
            onView(withId(R.id.overlay_note)).check(matches(isDisplayed()))
            // Click on the overlay to dismiss it
            onView(withId(R.id.overlay_note)).perform(click())
            // Verify that the overlay note is hidden again
            onView(withId(R.id.overlay_note)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }
}
