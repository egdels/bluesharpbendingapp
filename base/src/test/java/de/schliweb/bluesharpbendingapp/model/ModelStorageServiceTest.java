package de.schliweb.bluesharpbendingapp.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

class ModelStorageServiceTest {

    /**
     * Tests the storeModel method of the ModelStorageService class.
     * This method is responsible for storing the data of a MainModel instance into a file.
     */
    @Test
    void shouldStoreModelSuccessfullyWhenDirectoryAndFileExist() throws IOException {
        // Arrange
        String tempDirectory = "testDirectory";
        String tempFile = "testFile.txt";
        ModelStorageService service = new ModelStorageService(tempDirectory, tempFile);

        MainModel mockModel = Mockito.mock(MainModel.class);
        when(mockModel.getString()).thenReturn("getSelectedLockScreenIndex:1, getSelectedAlgorithmIndex:2");

        new File(tempDirectory).mkdirs(); // Ensures the directory exists
        new File(tempDirectory, tempFile).delete(); // Ensures file does not already exist

        // Act
        service.storeModel(mockModel);

        // Assert
        File file = new File(tempDirectory, tempFile);
        assert file.exists(); // Verify file is created
        String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        assert fileContent.equals("getSelectedLockScreenIndex:1, getSelectedAlgorithmIndex:2");

        // Cleanup
        file.delete(); // Cleanup the created file
        new File(tempDirectory).delete(); // Cleanup the directory
    }

    @Test
    void shouldCreateDirectoryWhenItDoesNotExist() {
        // Arrange
        String tempDirectory = "newTestDirectory";
        String tempFile = "newTestFile.txt";
        ModelStorageService service = new ModelStorageService(tempDirectory, tempFile);

        MainModel mockModel = Mockito.mock(MainModel.class);
        when(mockModel.getString()).thenReturn("getSelectedKeyIndex:3, getSelectedMicrophoneIndex:4");

        new File(tempDirectory).delete(); // Ensures the directory does not exist
        new File(tempDirectory, tempFile).delete(); // Ensures file does not already exist

        // Act
        service.storeModel(mockModel);

        // Assert
        File directory = new File(tempDirectory);
        assert directory.exists(); // Verify directory is created
        File file = new File(tempDirectory, tempFile);
        assert file.exists(); // Verify file is created

        // Cleanup
        file.delete(); // Cleanup the created file
        directory.delete(); // Cleanup the directory
    }

    @Test
    void shouldNotThrowExceptionWhenIOExceptionOccurs() {
        // Arrange
        String tempDirectory = "nonWritableDirectory";
        String tempFile = "testFile.txt";
        ModelStorageService service = new ModelStorageService(tempDirectory, tempFile);

        MainModel mockModel = Mockito.mock(MainModel.class);
        when(mockModel.getString()).thenReturn("getSelectedTuneIndex:5, getSelectedConcertPitchIndex:6");

        File directory = new File(tempDirectory);
        directory.mkdirs();
        directory.setWritable(false); // Make the directory non-writable

        // Act
        try {
            service.storeModel(mockModel);
        } catch (Exception e) {
            assert false; // No exception should be thrown
        }

        // Cleanup
        directory.setWritable(true);
        directory.delete();
    }
}