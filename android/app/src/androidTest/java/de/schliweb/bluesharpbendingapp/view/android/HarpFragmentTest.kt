package de.schliweb.bluesharpbendingapp.view.android

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HarpFragmentTest {
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