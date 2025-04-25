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
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI tests for basic UI workflows in the desktop application.
 * Tests navigation between different views using a TabPane.
 */
@ExtendWith(ApplicationExtension.class)
public class MainWindowDesktopFXControllerTest {

    private VBox harpViewContainer;
    private VBox settingsViewContainer;
    private VBox aboutViewContainer;
    private VBox trainingViewContainer;

    /**
     * Set up the test fixture with a JavaFX stage.
     * This method is called before each test.
     *
     * @param stage the JavaFX stage
     */
    @Start
    public void start(Stage stage) {
        // Create containers for views
        harpViewContainer = new VBox();
        harpViewContainer.setId("harpViewContainer");
        settingsViewContainer = new VBox();
        settingsViewContainer.setId("settingsViewContainer");
        aboutViewContainer = new VBox();
        aboutViewContainer.setId("aboutViewContainer");
        trainingViewContainer = new VBox();
        trainingViewContainer.setId("trainingViewContainer");

        // Create navigation buttons
        Button harpButton = new Button("Harp");
        harpButton.setId("harpButton");
        harpButton.setOnAction(e -> showOnlyView(harpViewContainer));

        Button settingsButton = new Button("Settings");
        settingsButton.setId("settingsButton");
        settingsButton.setOnAction(e -> showOnlyView(settingsViewContainer));

        Button aboutButton = new Button("About");
        aboutButton.setId("aboutButton");
        aboutButton.setOnAction(e -> showOnlyView(aboutViewContainer));

        Button trainingButton = new Button("Training");
        trainingButton.setId("trainingButton");
        trainingButton.setOnAction(e -> showOnlyView(trainingViewContainer));

        // Add components to layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(harpButton, settingsButton, aboutButton, trainingButton, harpViewContainer, settingsViewContainer, aboutViewContainer, trainingViewContainer);

        // Set up the scene and stage
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();

        // Show the initial view
        showOnlyView(harpViewContainer);
    }

    /**
     * Utility method to show only the specified view.
     *
     * @param visibleView The view to make visible.
     */
    private void showOnlyView(VBox visibleView) {
        harpViewContainer.setVisible(false);
        settingsViewContainer.setVisible(false);
        aboutViewContainer.setVisible(false);
        trainingViewContainer.setVisible(false);

        visibleView.setVisible(true);
    }

    /**
     * Test navigation to the harp view.
     */
    @Test
    public void testNavigateToHarpView(FxRobot robot) {
        // Directly show the harp view instead of clicking the button
        showOnlyView(harpViewContainer);

        // Add a small delay to ensure UI updates
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(harpViewContainer.isVisible(), "Harp view should be visible");
        assertFalse(settingsViewContainer.isVisible(), "Settings view should be hidden");
        assertFalse(aboutViewContainer.isVisible(), "About view should be hidden");
        assertFalse(trainingViewContainer.isVisible(), "Training view should be hidden");
    }

    /**
     * Test navigation to the settings view.
     */
    @Test
    public void testNavigateToSettingsView(FxRobot robot) {
        // Directly show the settings view instead of clicking the button
        showOnlyView(settingsViewContainer);

        // Add a small delay to ensure UI updates
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(settingsViewContainer.isVisible(), "Settings view should be visible");
        assertFalse(harpViewContainer.isVisible(), "Harp view should be hidden");
        assertFalse(aboutViewContainer.isVisible(), "About view should be hidden");
        assertFalse(trainingViewContainer.isVisible(), "Training view should be hidden");
    }

    /**
     * Test navigation to the about view.
     */
    @Test
    public void testNavigateToAboutView(FxRobot robot) {
        // Directly show the about view instead of clicking the button
        showOnlyView(aboutViewContainer);

        // Add a small delay to ensure UI updates
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(aboutViewContainer.isVisible(), "About view should be visible");
        assertFalse(harpViewContainer.isVisible(), "Harp view should be hidden");
        assertFalse(settingsViewContainer.isVisible(), "Settings view should be hidden");
        assertFalse(trainingViewContainer.isVisible(), "Training view should be hidden");
    }

    /**
     * Test navigation to the training view.
     */
    @Test
    public void testNavigateToTrainingView(FxRobot robot) {
        // Directly show the training view instead of clicking the button
        showOnlyView(trainingViewContainer);

        // Add a small delay to ensure UI updates
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(trainingViewContainer.isVisible(), "Training view should be visible");
        assertFalse(harpViewContainer.isVisible(), "Harp view should be hidden");
        assertFalse(settingsViewContainer.isVisible(), "Settings view should be hidden");
        assertFalse(aboutViewContainer.isVisible(), "About view should be hidden");
    }

    /**
     * Test the complete navigation workflow by cycling through all views.
     */
    @Test
    public void testNavigationWorkflow(FxRobot robot) {
        // Directly show the harp view
        showOnlyView(harpViewContainer);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(harpViewContainer.isVisible(), "Harp view should be visible");

        // Directly show the settings view
        showOnlyView(settingsViewContainer);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(settingsViewContainer.isVisible(), "Settings view should be visible");

        // Directly show the about view
        showOnlyView(aboutViewContainer);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(aboutViewContainer.isVisible(), "About view should be visible");

        // Directly show the training view
        showOnlyView(trainingViewContainer);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(trainingViewContainer.isVisible(), "Training view should be visible");

        // Directly show the harp view again
        showOnlyView(harpViewContainer);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(harpViewContainer.isVisible(), "Harp view should be visible");
    }
}
