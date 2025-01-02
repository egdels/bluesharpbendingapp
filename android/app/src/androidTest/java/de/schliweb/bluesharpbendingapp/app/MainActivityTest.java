package de.schliweb.bluesharpbendingapp.app;

import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import de.schliweb.bluesharpbendingapp.model.AndroidModel;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class MainActivityTest {

    @Test
    public void testReadModel_FileExistsAndValidData() {
        // Arrange
        Context context = ApplicationProvider.getApplicationContext();
        File tempDir = context.getCacheDir();
        File tempFile = new File(tempDir, "Model.tmp");
        tempFile.deleteOnExit(); // Ensure cleanup of file

        String modelString = "[getStoredAlgorithmIndex:1, getStoredConcertPitchIndex:11, getStoredKeyIndex:4, getStoredLockScreenIndex:0, getStoredMicrophoneIndex:0, getStoredPrecisionIndex:0, getStoredTrainingIndex:0, getStoredTuneIndex:6]";
        AndroidModel expectedModel = mock(AndroidModel.class);

        // Create the dummy file with model data
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(modelString);
        } catch (IOException e) {
            fail("Could not create or write to the temporary file: " + e.getMessage());
        }

        // Mock the behavior of AndroidModel instance
        MainActivity mainActivity = mock(MainActivity.class);
        AndroidModel androidModelMock = mock(AndroidModel.class);
        Mockito.when(androidModelMock.fromString(modelString)).thenReturn(expectedModel);
        Mockito.when(mainActivity.readModel()).thenReturn(expectedModel);

        // Act and Assert
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                AndroidModel result = activity.readModel();
                assertEquals(expectedModel.getStoredLockScreenIndex(),result.getStoredLockScreenIndex());
                assertNotNull(result);
            });
        }
    }

    @Test
    public void testReadModel_FileDoesNotExist() {
        // Arrange
        Context context = ApplicationProvider.getApplicationContext();
        File tempDir = context.getCacheDir();
        File tempFile = new File(tempDir, "Model.tmp");

        if (tempFile.exists()) {
            tempFile.delete();
        }

        // Act and Assert
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(mainActivity -> {
                AndroidModel result = mainActivity.readModel();
                assertEquals(AndroidModel.class, result.getClass());
            });
        }
    }

}