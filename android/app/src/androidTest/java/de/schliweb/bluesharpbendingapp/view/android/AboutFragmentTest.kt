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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.schliweb.bluesharpbendingapp.R
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the AboutFragment.
 *
 * This test class verifies that the AboutFragment displays correctly.
 * Since the AboutFragment is a simple display-only fragment with no user interactions,
 * these tests focus on ensuring that all text elements are displayed.
 */
@RunWith(AndroidJUnit4::class)
class AboutFragmentTest {

    /**
     * Tests that all text elements are displayed when the fragment is launched.
     */
    @Test
    fun testAboutElementsAreDisplayed() {
        // Launch the AboutFragment
        launchFragmentInContainer<AboutFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that all text elements are displayed
        onView(withId(R.id.about_title)).check(matches(isDisplayed()))
        onView(withId(R.id.about_description)).check(matches(isDisplayed()))
        onView(withId(R.id.about_version)).check(matches(isDisplayed()))
    }

    /**
     * Tests that the text elements contain the expected text.
     */
    @Test
    fun testAboutElementsHaveCorrectText() {
        // Launch the AboutFragment
        launchFragmentInContainer<AboutFragment>(themeResId = R.style.Theme_BluesHarpBendingApp)

        // Verify that the title has the correct text
        onView(withId(R.id.about_title)).check(matches(withText(R.string.about_title)))

        // Verify that the description has the correct text
        onView(withId(R.id.about_description)).check(matches(withText(R.string.about_description)))

        // Note: We can't easily check the version text as it's dynamically generated,
        // but we can verify it's not empty
        onView(withId(R.id.about_version)).check(matches(not(withText(""))))
    }

}
