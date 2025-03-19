package de.schliweb.bluesharpbendingapp.view.android

import android.widget.Spinner
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.schliweb.bluesharpbendingapp.R
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test class for the SettingsFragment, focusing on the functionality related to setting and managing keys
 * within a Spinner widget.
 *
 * This test class verifies various behaviors of the `setKeys` method in the `SettingsFragment`, including:
 * - Setting keys when the list contains unique or duplicate keys.
 * - Re-initializing the keys after the list has already been populated.
 * - Properly clearing existing keys when new keys are set.
 * - Populating the spinner widget with the supplied key list.
 * - Handling empty or null input lists gracefully.
 *
 * Each test case launches the `SettingsFragment` in an isolated test environment using `FragmentScenario`,
 * enabling independent validation of the fragment's behavior.
 *
 * Mocks for `HarpSettingsViewHandler` are used where necessary to ensure only the spinner-related functionality is tested.
 *
 * Dependencies:
 * - AndroidJUnit4
 * - FragmentScenario from AndroidX test
 * - Assertions from JUnit
 */
@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {
    /**
     * Tests the functionality of the `SettingsFragment` method `setKeys` with a single key input.
     *
     * Ensures the following:
     * - The spinner adapter is correctly initialized.
     * - The adapter contains only one item corresponding to the given key.
     * - The key in the spinner is set to the provided single key value.
     *
     * This test utilizes a `FragmentScenario` to launch the `SettingsFragment` and a mocked
     * implementation of `HarpSettingsViewHandler` to interact with the fragment.
     */
    @Test
    fun testSetKeyswithSingleKey() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
                // no need
            }

            override fun handleTuneSelection(i: Int) {
                // no need
            }

            override fun initKeyList() {
                // no need
            }

            override fun initTuneList() {
                // no need
            }
        }

        scenario.onFragment { fragment: SettingsFragment ->
            fragment.harpSettingsViewHandler = mockHandler
            fragment.setKeys(arrayOf("G"))
            val spinner = fragment.binding.settingsKeyList

            Assert.assertNotNull(spinner.adapter)
            Assert.assertEquals(1, spinner.adapter.count.toLong())
            Assert.assertEquals("G", spinner.adapter.getItem(0))
        }
    }

    /**
     * Tests the functionality of the `SettingsFragment` method `setKeys` when provided with duplicate keys.
     *
     * Ensures the following:
     * - The spinner adapter is correctly initialized.
     * - The adapter contains all the provided keys, including duplicates.
     * - The correct sequence of duplicate keys is preserved in the spinner adapter.
     *
     * This test utilizes a `FragmentScenario` to launch the `SettingsFragment` and a mocked
     * implementation of `HarpSettingsViewHandler` to interact with the fragment. It verifies
     * the state of the spinner adapter after duplicate keys are set through the `setKeys` method.
     */
    @Test
    fun testSetKeyswithDuplicateKeys() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
                // no need
            }

            override fun handleTuneSelection(i: Int) {
                // no need
            }

            override fun initKeyList() {
                // no need
            }

            override fun initTuneList() {
                // no need
            }
        }

        scenario.onFragment { fragment: SettingsFragment ->
            fragment.harpSettingsViewHandler = mockHandler
            fragment.setKeys(arrayOf("A", "B", "A"))
            val spinner = fragment.binding.settingsKeyList

            Assert.assertNotNull(spinner.adapter)
            Assert.assertEquals(3, spinner.adapter.count.toLong())
            Assert.assertEquals("A", spinner.adapter.getItem(0))
            Assert.assertEquals("B", spinner.adapter.getItem(1))
            Assert.assertEquals("A", spinner.adapter.getItem(2))
        }
    }

    /**
     * Tests the `setKeys` method of the `SettingsFragment` after the fragment has been initialized.
     *
     * This test ensures the following:
     * - When a new list of keys is set using `setKeys`, the spinner adapter updates correctly.
     * - The spinner adapter correctly reflects the new list of keys in the provided order.
     * - Multiple calls to `setKeys` replace the previous keys in the spinner with the new set.
     *
     * The test uses a `FragmentScenario` to launch the `SettingsFragment` and a mocked
     * implementation of `HarpSettingsViewHandler` to simulate interaction. It verifies
     * the state of the spinner adapter after consecutive calls to `setKeys`.
     */
    @Test
    fun testSetKeysafterInitialization() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
                // no need
            }

            override fun handleTuneSelection(i: Int) {
                // no need
            }

            override fun initKeyList() {
                // no need
            }

            override fun initTuneList() {
                // no need
            }
        }

        scenario.onFragment { fragment: SettingsFragment ->
            fragment.harpSettingsViewHandler = mockHandler
            fragment.setKeys(arrayOf("C", "D"))
            val spinner = fragment.binding.settingsKeyList

            Assert.assertEquals(2, spinner.adapter.count.toLong())
            Assert.assertEquals("C", spinner.adapter.getItem(0))
            Assert.assertEquals("D", spinner.adapter.getItem(1))

            fragment.setKeys(arrayOf("X", "Y", "Z"))

            Assert.assertEquals(3, spinner.adapter.count.toLong())
            Assert.assertEquals("X", spinner.adapter.getItem(0))
            Assert.assertEquals("Y", spinner.adapter.getItem(1))
            Assert.assertEquals("Z", spinner.adapter.getItem(2))
        }
    }

    /**
     * Tests the functionality of the `SettingsFragment` method `setKeys` when provided with a list of keys.
     *
     * This test ensures the following:
     * - The spinner adapter is properly initialized when `setKeys` is called with a non-empty list of keys.
     * - The spinner adapter contains all the keys passed to the `setKeys` method in the correct order.
     * - The spinner displays each key accurately in the adapter.
     *
     * The test uses a `FragmentScenario` to launch the `SettingsFragment` and assigns a mocked implementation
     * of the `HarpSettingsViewHandler` interface to simulate interaction. The state of the spinner adapter
     * is verified after the `setKeys` method is invoked with an array of test keys.
     */
    @Test
    fun testSetKeysPopulatesSpinnerWithKeys() {
        // Launch the SettingsFragment in a container with a simple Material Design theme
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        // Create a mock implementation of the HarpSettingsViewHandler interface
        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
                // Not needed for this test
            }

            override fun handleTuneSelection(i: Int) {
                // Not needed for this test
            }

            override fun initKeyList() {
                // Not needed for this test
            }

            override fun initTuneList() {
                // Not needed for this test
            }
        }

        // Test logic
        scenario.onFragment { fragment: SettingsFragment ->
            // Assign the mock handler to the fragment
            fragment.harpSettingsViewHandler = mockHandler

            // Provide test data for the setKeys method
            val keys = arrayOf("C", "D", "E", "F")
            fragment.setKeys(keys)

            // Verify that the Spinner has been populated with the test data
            val spinner = fragment.view!!.findViewById<Spinner>(R.id.settings_key_list)
            Assert.assertNotNull(spinner.adapter) // Ensure the adapter is set
            Assert.assertEquals(
                4, spinner.adapter.count.toLong()
            ) // Verify the correct number of items
            Assert.assertEquals("C", spinner.adapter.getItem(0)) // Verify the 1st key
            Assert.assertEquals("D", spinner.adapter.getItem(1)) // Verify the 2nd key
            Assert.assertEquals("E", spinner.adapter.getItem(2)) // Verify the 3rd key
            Assert.assertEquals("F", spinner.adapter.getItem(3)) // Verify the 4th key
        }
    }

    /**
     * Tests the functionality of the `SettingsFragment` method `setKeys` to ensure it clears
     * previously set keys in the spinner adapter and replaces them with a new list of keys.
     *
     * Ensures the following:
     * - When a new list of keys is set using `setKeys`, the previously populated keys in the spinner
     *   adapter are cleared.
     * - The spinner adapter correctly reflects the new set of keys passed to the `setKeys` method.
     * - The order and contents of the newly set keys in the spinner adapter match the provided list.
     *
     * The test uses a `FragmentScenario` to launch the `SettingsFragment` and assigns a mocked
     * implementation of the `HarpSettingsViewHandler` interface. The spinner's state is verified
     * before and after invoking the `setKeys` method with different sets of keys.
     */
    @Test
    fun testSetKeysclearsPreviousKeys() {
        // Launch the SettingsFragment in a container with a specified Material Design theme
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        // Create a mock implementation of the HarpSettingsViewHandler interface
        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
                // Mock implementation, not used in this test
            }

            override fun handleTuneSelection(i: Int) {
                // Not needed for this test
            }

            override fun initKeyList() {
                // Not needed for this test
            }

            override fun initTuneList() {
                // Not needed for this test
            }
        }

        // Test logic
        scenario.onFragment { fragment: SettingsFragment ->
            // Assign the mock handler to the fragment
            fragment.harpSettingsViewHandler = mockHandler

            // Test data for the two sets of keys
            val initialKeys = arrayOf("A", "B") // Initial test keys
            val newKeys = arrayOf("X", "Y", "Z") // New test keys

            // Find the Spinner to verify the populated keys
            val spinner = fragment.binding.settingsKeyList

            // Set the initial keys and verify they are correctly populated
            fragment.setKeys(initialKeys)
            Assert.assertNotNull(spinner.adapter) // Ensure the adapter is set
            Assert.assertEquals(
                2, spinner.adapter.count.toLong()
            ) // Verify the adapter contains 2 items
            Assert.assertEquals("A", spinner.adapter.getItem(0)) // Verify the 1st key
            Assert.assertEquals("B", spinner.adapter.getItem(1)) // Verify the 2nd key

            // Set the new keys to verify the previous keys are cleared and replaced with the new ones
            fragment.setKeys(newKeys)
            Assert.assertNotNull(spinner.adapter) // Ensure the adapter is still set
            Assert.assertEquals(
                3, spinner.adapter.count.toLong()
            ) // Verify the adapter now contains 3 items
            Assert.assertEquals("X", spinner.adapter.getItem(0)) // Verify the 1st new key
            Assert.assertEquals("Y", spinner.adapter.getItem(1)) // Verify the 2nd new key
            Assert.assertEquals("Z", spinner.adapter.getItem(2)) // Verify the 3rd new key
        }
    }

    /**
     * Tests the functionality of the `SettingsFragment` method `setKeys` when provided with an empty array.
     *
     * This test ensures the following:
     * - The spinner adapter is correctly initialized when `setKeys` is called with an empty array.
     * - The adapter contains no items after the method is invoked.
     *
     * The test uses a `FragmentScenario` to launch the `SettingsFragment`. The state of the spinner adapter
     * is verified after calling `setKeys` with an empty array, ensuring the adapter is empty and properly updated.
     */
    @Test
    fun testSetKeyswithEmptyArrayshouldClearSpinner() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        scenario.onFragment { fragment: SettingsFragment ->
            fragment.setKeys(arrayOf())
            val spinner = fragment.binding.settingsKeyList

            Assert.assertNotNull(spinner.adapter)
            Assert.assertEquals(0, spinner.adapter.count.toLong())
        }
    }

    /**
     * Tests the functionality of the `SettingsFragment` method `setKeys` when `null` is passed as input.
     *
     * This test ensures the following:
     * - The spinner adapter is initialized correctly even when `null` is provided.
     * - The adapter contains no items after the method is invoked with `null`.
     *
     * The test uses a `FragmentScenario` to launch the `SettingsFragment` and verifies
     * that the spinner adapter's state is properly updated when `setKeys` is called with `null`.
     */
    @Test
    fun testSetKeyswithNullshouldHandleGracefully() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        scenario.onFragment { fragment: SettingsFragment ->
            fragment.setKeys(null)
            val spinner = fragment.binding.settingsKeyList

            Assert.assertNotNull(spinner.adapter)
            Assert.assertEquals(0, spinner.adapter.count.toLong())
        }
    }
}