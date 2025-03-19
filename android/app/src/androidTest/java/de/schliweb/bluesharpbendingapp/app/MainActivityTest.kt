package de.schliweb.bluesharpbendingapp.app

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import de.schliweb.bluesharpbendingapp.model.AndroidModel
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Test class for the MainActivity.
 * This class contains unit tests to verify the behavior and functionality of MainActivity's methods.
 */
class MainActivityTest {
    /**
     * Tests the file reading functionality of the `readModel` method in `MainActivity` to ensure
     * it correctly reads data from a file, assuming the file exists and contains valid model data.
     *
     * This test creates a temporary file in the cache directory of the application context
     * with valid model data. It ensures that:
     * - The contents of the file are accurately parsed into an instance of `AndroidModel`.
     * - The resulting `AndroidModel` object matches the expected values for all its properties.
     * - The method operates as intended without errors.
     *
     * The test performs the following steps:
     * 1. Creates a temporary file in the application's cache directory.
     * 2. Writes a valid model data string into the file.
     * 3. Launches the `MainActivity`.
     * 4. Validates that the `readModel` method reads and maps the data correctly into the expected model values.
     *
     * Assertions ensure the properties of the returned model object match the predefined values of the expected model.
     */
    @Test
    fun testReadModelFileExistsAndValidData() {
        // Arrange
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempDir = context.cacheDir
        val tempFile = File(tempDir, "Model.tmp")
        tempFile.deleteOnExit() // Ensure cleanup of file

        val modelString =
            "[getStoredAlgorithmIndex:1, getStoredConcertPitchIndex:11, getStoredKeyIndex:4, getStoredLockScreenIndex:0, getStoredMicrophoneIndex:0, getStoredPrecisionIndex:0, getStoredTrainingIndex:0, getStoredTuneIndex:6]"
        val expectedModel = AndroidModel.createFromString(modelString)

        // Create the dummy file with model data
        try {
            FileWriter(tempFile).use { writer ->
                writer.write(modelString)
            }
        } catch (e: IOException) {
            Assert.fail("Could not create or write to the temporary file: " + e.message)
        }

        ActivityScenario.launch<MainActivity>(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity: MainActivity ->
                val result = activity.readModel()
                Assert.assertEquals(
                    0, result.storedLockScreenIndex.toLong()
                )
                Assert.assertEquals(
                    expectedModel.storedAlgorithmIndex.toLong(),
                    result.storedAlgorithmIndex.toLong()
                )
                Assert.assertEquals(
                    expectedModel.storedKeyIndex.toLong(), result.storedKeyIndex.toLong()
                )
                Assert.assertEquals(
                    expectedModel.storedTuneIndex.toLong(), result.storedTuneIndex.toLong()
                )
                Assert.assertEquals(
                    expectedModel.storedMicrophoneIndex.toLong(),
                    result.storedMicrophoneIndex.toLong()
                )
                Assert.assertEquals(
                    expectedModel.storedTrainingIndex.toLong(), result.storedTrainingIndex.toLong()
                )
                Assert.assertNotNull(result)
            }
        }
    }

    /**
     * Tests the `hideAppBar` functionality of the `MainActivity` class to ensure that it successfully
     * hides the app bar and updates the associated `isAppBarHidden` property to reflect the change.
     *
     * This test performs the following steps:
     * 1. Launches the `MainActivity` using `ActivityScenario`.
     * 2. Invokes the `hideAppBar` method within the activity.
     * 3. Validates that the `isAppBarHidden` property is set to `true`, confirming that the app bar is
     *    successfully hidden as expected.
     *
     * Assertions are used to ensure the `hideAppBar` method operates correctly and has the intended effect.
     */
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

    /**
     * Tests the idempotency of the `hideAppBar` functionality in the `MainActivity` class
     * when it is called multiple times consecutively and the app bar is already hidden.
     *
     * This test ensures:
     * 1. The `hideAppBar` method does not cause errors or unexpected behavior when invoked
     *    multiple times in succession.
     * 2. The `isAppBarHidden` property remains `true` after multiple calls to `hideAppBar`,
     *    confirming that the app bar stays hidden.
     *
     * The test performs the following steps:
     * - Launches the `MainActivity` using `ActivityScenario`.
     * - Calls the `hideAppBar` method twice consecutively.
     * - Verifies via assertions that the `isAppBarHidden` property retains its `true` value,
     *   indicating the app bar remains hidden.
     */
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

    /**
     * Tests the behavior of the `readModel` method in the `MainActivity` class when the model file
     * does not exist in the application's cache directory.
     *
     * This test ensures:
     * 1. The `readModel` method handles the absence of the file gracefully.
     * 2. A default instance of `AndroidModel` is returned as expected when the file does not exist.
     *
     * The test performs the following steps:
     * 1. Retrieves the application cache directory context.
     * 2. Ensures that the temporary file representing the model file is deleted, if it exists.
     * 3. Launches the `MainActivity` using `ActivityScenario`.
     * 4. Invokes the `readModel` method and verifies that the returned model matches the
     *    expected class type (`AndroidModel`).
     *
     * Assertions confirm that the absence of the file does not cause runtime errors and the
     * default model behavior is correctly implemented.
     */
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