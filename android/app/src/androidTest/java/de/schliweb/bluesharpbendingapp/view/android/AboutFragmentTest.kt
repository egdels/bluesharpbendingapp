package de.schliweb.bluesharpbendingapp.view.android

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test class for the AboutFragment.
 *
 * This class contains instrumented tests to verify the functionality
 * and behavior of the AboutFragment in an Android application.
 *
 * Utilizes the AndroidJUnit4 test runner along with FragmentScenario
 * to launch and test the fragment in isolation.
 */
@RunWith(AndroidJUnit4::class)
class AboutFragmentTest {

    /**
     * Tests the `AboutFragment` to ensure it is properly launched and its associated
     * activity has a visible action bar.
     *
     * This test uses `launchFragmentInContainer` to isolate the fragment and verify
     * that the fragment's activity is non-null and its action bar is displayed.
     */
    @Test
    fun testEventFragment() {
        val scenario = launchFragmentInContainer<AboutFragment>()
        scenario.onFragment { fragment ->
            val activity = fragment.activity
            assert(activity != null)
            assert(activity!!.actionBar?.isShowing == true)
        }
    }


}