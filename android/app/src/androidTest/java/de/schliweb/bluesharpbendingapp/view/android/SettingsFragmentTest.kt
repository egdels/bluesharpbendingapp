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
 * UI tests for the SettingsFragment.
 *
 * This test class verifies that the SettingsFragment displays correctly
 * and that user interactions with the settings controls work as expected.
 */
@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    /**
     * Tests that all settings labels are displayed when the fragment is launched.
     */
    @Test
    fun testSettingsLabelsAreDisplayed() {
        // Launch the SettingsFragment
        launchFragmentInContainer<SettingsFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that all settings labels are displayed
        onView(withId(R.id.settings_alog_label)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_conf_label)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_key_label)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_tune_label)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_pitches_label)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_screenlock_label)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_reset_label)).check(matches(isDisplayed()))
    }

    /**
     * Tests that all settings controls are displayed when the fragment is launched.
     */
    @Test
    fun testSettingsControlsAreDisplayed() {
        // Launch the SettingsFragment
        launchFragmentInContainer<SettingsFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that all settings controls are displayed
        onView(withId(R.id.settings_algo_list)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_conf_list)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_key_list)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_tune_list)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_pitches_list)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_screenlock)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_reset_button)).check(matches(isDisplayed()))
    }

    /**
     * Tests that the reset button is clickable.
     */
    @Test
    fun testResetButtonIsClickable() {
        // Launch the SettingsFragment
        launchFragmentInContainer<SettingsFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that the reset button is clickable
        onView(withId(R.id.settings_reset_button)).check(matches(isClickable()))

        // Click the reset button (this will not verify the actual reset functionality,
        // as that would require mocking the handlers)
        onView(withId(R.id.settings_reset_button)).perform(click())
    }

    /**
     * Tests that the screen lock switch is clickable.
     */
    @Test
    fun testScreenLockSwitchIsClickable() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Open the options menu
            onView(withContentDescription("More options")).perform(click())
            onView(withText(R.string.action_settings)).perform(click())
            // Verify that the screen lock switch is clickable
            onView(withId(R.id.settings_screenlock)).check(matches(isClickable()))
            // Click the screen lock switch (this will not verify the actual functionality,
            // as that would require mocking the handlers)
            onView(withId(R.id.settings_screenlock)).perform(click())
        }
    }

}