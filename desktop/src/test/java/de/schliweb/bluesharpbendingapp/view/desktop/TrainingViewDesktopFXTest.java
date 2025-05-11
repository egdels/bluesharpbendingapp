package de.schliweb.bluesharpbendingapp.view.desktop;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UI tests for the TrainingViewDesktopFX component.
 * Tests basic UI interactions with the training view.
 */
@ExtendWith(ApplicationExtension.class)
class TrainingViewDesktopFXTest {

    private TrainingViewDesktopFX trainingView;

    /**
     * Set up the test fixture with a JavaFX stage.
     * This method is called before each test.
     *
     * @param stage the JavaFX stage
     */
    @Start
    public void start(Stage stage) {
        // Create the training view
        trainingView = new TrainingViewDesktopFX();

        // Set up the scene and stage
        VBox layout = new VBox();
        layout.getChildren().add(trainingView.getRoot());
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Test that the training view is properly initialized.
     */
    @Test
    void testTrainingViewInitialization() {
        // Verify that the training view is not null
        assertNotNull(trainingView, "Training view should not be null");
        assertNotNull(trainingView.getRoot(), "Training view root should not be null");
        assertNotNull(trainingView.getController(), "Training view controller should not be null");
    }

    /**
     * Test that the training view can be displayed.
     */
    @Test
    void testTrainingViewDisplay(FxRobot robot) {
        // Verify that the training view is visible
        assertNotNull(trainingView.getRoot().getScene(), "Training view should be in a scene");
        assertNotNull(trainingView.getRoot().getScene().getWindow(), "Training view scene should be in a window");
    }
}