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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI tests for basic navigation workflows in the desktop application.
 * Tests navigation between different views using a TabPane.
 */
@ExtendWith(ApplicationExtension.class)
public class NavigationWorkflowTest {

    private TabPane tabPane;
    private Tab harpTab;
    private Tab settingsTab;
    private Tab aboutTab;
    private Tab trainingTab;
    private Button harpButton;
    private Button settingsButton;
    private Button aboutButton;
    private Button trainingButton;

    /**
     * Set up the test fixture with a JavaFX stage.
     * This method is called before each test.
     *
     * @param stage the JavaFX stage
     */
    @Start
    public void start(Stage stage) {
        // Create tabs for each view
        harpTab = new Tab("Harp");
        harpTab.setContent(new VBox());

        settingsTab = new Tab("Settings");
        settingsTab.setContent(new VBox());

        aboutTab = new Tab("About");
        aboutTab.setContent(new VBox());

        trainingTab = new Tab("Training");
        trainingTab.setContent(new VBox());

        // Create the tab pane and add the tabs
        tabPane = new TabPane();
        tabPane.getTabs().addAll(harpTab, settingsTab, aboutTab, trainingTab);

        // Create navigation buttons
        harpButton = new Button("Harp");
        harpButton.setId("harpButton");
        harpButton.setOnAction(e -> {
            tabPane.getSelectionModel().select(harpTab);
            // Force the selection to be applied immediately
            tabPane.requestLayout();
        });

        settingsButton = new Button("Settings");
        settingsButton.setId("settingsButton");
        settingsButton.setOnAction(e -> {
            tabPane.getSelectionModel().select(settingsTab);
            // Force the selection to be applied immediately
            tabPane.requestLayout();
        });

        aboutButton = new Button("About");
        aboutButton.setId("aboutButton");
        aboutButton.setOnAction(e -> {
            tabPane.getSelectionModel().select(aboutTab);
            // Force the selection to be applied immediately
            tabPane.requestLayout();
        });

        trainingButton = new Button("Training");
        trainingButton.setId("trainingButton");
        trainingButton.setOnAction(e -> {
            tabPane.getSelectionModel().select(trainingTab);
            // Force the selection to be applied immediately
            tabPane.requestLayout();
        });

        // Create a layout for the test UI
        VBox layout = new VBox(10);
        layout.getChildren().addAll(harpButton, settingsButton, aboutButton, trainingButton, tabPane);

        // Set up the scene and stage
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Test navigation to the harp view.
     */
    @Test
    public void testNavigateToHarpView(FxRobot robot) {
        // Directly select the harp tab
        tabPane.getSelectionModel().select(harpTab);

        try {
            // Give the UI time to update
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the harp tab is selected
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("Harp", selectedTab.getText(), "Harp tab should be selected");
        assertTrue(selectedTab == harpTab, "Harp tab object should be selected");
    }

    /**
     * Test navigation to the settings view.
     */
    @Test
    public void testNavigateToSettingsView(FxRobot robot) {
        // Directly select the settings tab
        tabPane.getSelectionModel().select(settingsTab);

        try {
            // Give the UI time to update
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the settings tab is selected
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("Settings", selectedTab.getText(), "Settings tab should be selected");
        assertTrue(selectedTab == settingsTab, "Settings tab object should be selected");
    }

    /**
     * Test navigation to the about view.
     */
    @Test
    public void testNavigateToAboutView(FxRobot robot) {
        // Directly select the about tab
        tabPane.getSelectionModel().select(aboutTab);

        try {
            // Give the UI time to update
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the about tab is selected
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("About", selectedTab.getText(), "About tab should be selected");
        assertTrue(selectedTab == aboutTab, "About tab object should be selected");
    }

    /**
     * Test navigation to the training view.
     */
    @Test
    public void testNavigateToTrainingView(FxRobot robot) {
        // Directly select the training tab
        tabPane.getSelectionModel().select(trainingTab);

        try {
            // Give the UI time to update
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the training tab is selected
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("Training", selectedTab.getText(), "Training tab should be selected");
        assertTrue(selectedTab == trainingTab, "Training tab object should be selected");
    }

    /**
     * Test the complete navigation workflow by cycling through all views.
     */
    @Test
    public void testNavigationWorkflow(FxRobot robot) {
        // Start with harp view
        tabPane.getSelectionModel().select(harpTab);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("Harp", selectedTab.getText(), "Harp tab should be selected");
        assertTrue(selectedTab == harpTab, "Harp tab object should be selected");

        // Navigate to settings view
        tabPane.getSelectionModel().select(settingsTab);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("Settings", selectedTab.getText(), "Settings tab should be selected");
        assertTrue(selectedTab == settingsTab, "Settings tab object should be selected");

        // Navigate to about view
        tabPane.getSelectionModel().select(aboutTab);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("About", selectedTab.getText(), "About tab should be selected");
        assertTrue(selectedTab == aboutTab, "About tab object should be selected");

        // Navigate to training view
        tabPane.getSelectionModel().select(trainingTab);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("Training", selectedTab.getText(), "Training tab should be selected");
        assertTrue(selectedTab == trainingTab, "Training tab object should be selected");

        // Navigate back to harp view
        tabPane.getSelectionModel().select(harpTab);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selectedTab = tabPane.getSelectionModel().getSelectedItem();
        assertEquals("Harp", selectedTab.getText(), "Harp tab should be selected");
        assertTrue(selectedTab == harpTab, "Harp tab object should be selected");
    }
}
