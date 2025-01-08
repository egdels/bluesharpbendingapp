package de.schliweb.bluesharpbendingapp.view.android

import android.widget.Spinner
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.schliweb.bluesharpbendingapp.R
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {
    @Test
    fun testSetKeys_withSingleKey() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
            }

            override fun handleTuneSelection(i: Int) {
            }

            override fun initKeyList() {
            }

            override fun initTuneList() {
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

    @Test
    fun testSetKeys_withDuplicateKeys() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
            }

            override fun handleTuneSelection(i: Int) {
            }

            override fun initKeyList() {
            }

            override fun initTuneList() {
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

    @Test
    fun testSetKeys_afterInitialization() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
            }

            override fun handleTuneSelection(i: Int) {
            }

            override fun initKeyList() {
            }

            override fun initTuneList() {
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

    @Test
    fun testSetKeys_populatesSpinnerWithKeys() {
        // FragmentContext mit einfachem MaterialDesign-Theme starten
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )

        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
            }

            override fun handleTuneSelection(i: Int) {
            }

            override fun initKeyList() {
            }

            override fun initTuneList() {
            }
        }

        // Prüflogik
        scenario.onFragment { fragment: SettingsFragment ->
            fragment.harpSettingsViewHandler = mockHandler
            // Testdaten festlegen
            val keys = arrayOf("C", "D", "E", "F")
            fragment.setKeys(keys)

            // Überprüfung
            val spinner = fragment.view!!.findViewById<Spinner>(R.id.settings_key_list)
            Assert.assertNotNull(spinner.adapter)
            Assert.assertEquals(4, spinner.adapter.count.toLong())
            Assert.assertEquals("C", spinner.adapter.getItem(0))
            Assert.assertEquals("D", spinner.adapter.getItem(1))
            Assert.assertEquals("E", spinner.adapter.getItem(2))
            Assert.assertEquals("F", spinner.adapter.getItem(3))
        }
    }

    @Test
    fun testSetKeys_clearsPreviousKeys() {
        val scenario = FragmentScenario.launchInContainer(
            SettingsFragment::class.java, null, R.style.Theme_BluesHarpBendingApp
        )


        val mockHandler: HarpSettingsViewHandler = object : HarpSettingsViewHandler {
            override fun handleKeySelection(id: Int) {
                // Mock implementation
            }

            override fun handleTuneSelection(i: Int) {
            }

            override fun initKeyList() {
            }

            override fun initTuneList() {
            }
        }

        // Prüflogik
        scenario.onFragment { fragment: SettingsFragment ->
            fragment.harpSettingsViewHandler = mockHandler
            // Testdaten festlegen
            val initialKeys = arrayOf("A", "B")
            val newKeys = arrayOf("X", "Y", "Z")

            // Überprüfung
            val spinner = fragment.binding.settingsKeyList
            fragment.setKeys(initialKeys)

            // Assert initial keys
            Assert.assertNotNull(spinner.adapter)
            Assert.assertEquals(2, spinner.adapter.count.toLong())
            Assert.assertEquals("A", spinner.adapter.getItem(0))
            Assert.assertEquals("B", spinner.adapter.getItem(1))

            fragment.setKeys(newKeys)

            // Assert initial keys
            Assert.assertNotNull(spinner.adapter)
            Assert.assertEquals(3, spinner.adapter.count.toLong())
            Assert.assertEquals("X", spinner.adapter.getItem(0))
            Assert.assertEquals("Y", spinner.adapter.getItem(1))
            Assert.assertEquals("Z", spinner.adapter.getItem(2))
        }
    }

    @Test
    fun testSetKeys_withEmptyArray_shouldClearSpinner() {
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

    @Test
    fun testSetKeys_withNull_shouldHandleGracefully() {
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