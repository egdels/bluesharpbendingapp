package de.schliweb.bluesharpbendingapp.app

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import de.schliweb.bluesharpbendingapp.model.AndroidModel
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivityTest {
    @Test
    fun testReadModelFileExistsAndValidData() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempDir = context.cacheDir
        val tempFile = File(tempDir, "Model.tmp")
        tempFile.deleteOnExit() // Ensure cleanup of file

        val modelString =
            "[getStoredAlgorithmIndex:1, getStoredConcertPitchIndex:11, getStoredKeyIndex:4, getStoredLockScreenIndex:0, getStoredMicrophoneIndex:0, getStoredPrecisionIndex:0, getStoredTrainingIndex:0, getStoredTuneIndex:6]"
        val expectedModel = Mockito.mock(AndroidModel::class.java)

        // Create the dummy file with model data
        try {
            FileWriter(tempFile).use { writer ->
                writer.write(modelString)
            }
        } catch (e: IOException) {
            Assert.fail("Could not create or write to the temporary file: " + e.message)
        }

        // Mock the behavior of AndroidModel instance
        val mainActivity = Mockito.mock(MainActivity::class.java)
        val androidModelMock = Mockito.mock(AndroidModel::class.java)
        Mockito.`when`(androidModelMock.fromString(modelString)).thenReturn(expectedModel)
        Mockito.`when`(mainActivity.readModel()).thenReturn(expectedModel)

        ActivityScenario.launch<MainActivity>(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity: MainActivity ->
                val result = activity.readModel()
                Assert.assertEquals(
                    expectedModel.storedLockScreenIndex.toLong(),
                    result.storedLockScreenIndex.toLong()
                )
                Assert.assertNotNull(result)
            }
        }
    }

    @Test
    fun testHideAppBarAppBarHidden() {
        // Act and Assert
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity: MainActivity ->
                activity.hideAppBar()
                Assert.assertTrue(activity.isAppBarHidden)
            }
        }
    }

    @Test
    fun testHideAppBarAlreadyHidden() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity: MainActivity ->
                // Hide twice to ensure idempotency of hideAppBar()
                activity.hideAppBar()
                activity.hideAppBar()
                Assert.assertTrue(activity.isAppBarHidden)
            }
        }
    }

    @Test
    fun testReadModelFileDoesNotExist() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempDir = context.cacheDir
        val tempFile = File(tempDir, "Model.tmp")

        if (tempFile.exists()) {
            tempFile.delete()
        }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { mainActivity: MainActivity ->
                val result = mainActivity.readModel()
                Assert.assertEquals(AndroidModel::class.java, result.javaClass)
            }
        }
    }
}