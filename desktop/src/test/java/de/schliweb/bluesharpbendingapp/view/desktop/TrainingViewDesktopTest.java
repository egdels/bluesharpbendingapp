package de.schliweb.bluesharpbendingapp.view.desktop;

import de.schliweb.bluesharpbendingapp.controller.TrainingContainer;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingViewDesktopTest {

    /**
     * Tests if the `initTrainingContainer` method correctly sets the progress bar value.
     */
    @Test
    void testInitTrainingContainerSetsProgressBarValue() {
        // Arrange
        TrainingViewDesktop trainingViewDesktop = new TrainingViewDesktop();
        TrainingContainer trainingContainerMock = mock(TrainingContainer.class);
        when(trainingContainerMock.getProgress()).thenReturn(75);

        // Act
        trainingViewDesktop.initTrainingContainer(trainingContainerMock);
        JProgressBar progressBar = getProgressBar(trainingViewDesktop);

        // Assert
        assertEquals(75, progressBar.getValue());
    }

    /**
     * Helper method to get the progress bar from TrainingViewDesktop.
     */
    private JProgressBar getProgressBar(TrainingViewDesktop trainingViewDesktop) {
        return trainingViewDesktop.progressBar;
    }

}