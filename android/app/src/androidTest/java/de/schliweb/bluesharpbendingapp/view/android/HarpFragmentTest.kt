package de.schliweb.bluesharpbendingapp.view.android

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A test class for verifying the behavior and functionality of the HarpFragment.
 * This class uses Android's instrumentation testing framework to test fragments.
 *
 * The test ensures that the HarpFragment is properly launched within a container
 * and verifies specific fragment-related properties and interactions, such as
 * the activity's action bar visibility.
 */
@RunWith(AndroidJUnit4::class)
class HarpFragmentTest {
    /**
     * Tests the functionality of the HarpFragment by verifying its integration and behavior
     * when launched within a container.
     *
     * This method ensures:
     * 1. The fragment is successfully associated with an activity.
     * 2. The activity's action bar is visible upon the fragment's creation.
     */
    @Test
    fun testEventFragment() {
        val scenario = launchFragmentInContainer<HarpFragment>()
        scenario.onFragment { fragment ->
            val activity = fragment.activity
            assert(activity != null)
            assert(activity!!.actionBar?.isShowing == true)
        }
    }
}