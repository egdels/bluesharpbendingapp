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

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * The TrainingViewDesktopFX class is responsible for initializing and managing the "Training"
 * view in a JavaFX application. It loads the associated FXML file, retrieves its root node
 * and controller, and ensures proper behavior and layout of the GUI components.
 * <p>
 * This class provides the necessary entry point for interaction with the view
 * represented by the FXML file and its associated controller.
 */
@Getter
@Slf4j
public class TrainingViewDesktopFX {

    /**
     * The root node of the "Training" view in the JavaFX application. This is the top-level
     * graphical component loaded from the associated FXML file, representing the entire
     * UI structure for the "Training" view.
     * <p>
     * The root node serves as the entry point for interaction and rendering of its
     * child components in the scene graph. It is initialized during the construction
     * of the {@code TrainingViewDesktopFX} class using the FXMLLoader and holds a
     * hierarchical representation of the GUI as defined in the FXML resource.
     * <p>
     * The {@code root} node is also managed and visible by default, ensuring proper
     * behavior and integration into the JavaFX application.
     */
    private final Node root;


    /**
     * The `controller` variable represents an instance of the `TrainingViewDesktopFXController` class.
     * It serves as the main controller for managing and updating the Training View in the DesktopFX
     * application. This controller coordinates user interactions, updates the UI, and communicates with
     * the underlying business logic associated with training functionalities.
     */
    private final TrainingViewDesktopFXController controller;

    /**
     * Constructor for the TrainingViewDesktopFX class.
     * <p>
     * This constructor initializes the "Training" view by loading the corresponding
     * FXML file, ensuring proper layout control, and associating the FXML controller.
     * It also handles any potential I/O exceptions during the FXML loading process.
     * <p>
     * Key Operations:
     * - Loads the FXML file for the defined "Training" view.
     * - Retrieves and saves the root node and the associated controller from the FXML file.
     * - Ensures the root node is managed and visible within the UI framework.
     * - Properly handles errors by logging them and throwing a RuntimeException
     *   to indicate failure during the FXML loading process.
     * <p>
     * Throws:
     * - RuntimeException if loading the FXML file fails due to an IOException.
     */
    public TrainingViewDesktopFX() {
        try {
            // Load the FXML file for the "Training" view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/training-view.fxml"));

            // Save the root element defined in the FXML file
            root = fxmlLoader.load();

            // Retrieve and save the controller instance associated with the FXML file
            controller = fxmlLoader.getController();

            // Bind the size of the root node to the parent container to ensure proper layout management
            root.setManaged(true);

            // Set the root node visibility to true
            root.setVisible(true);
        } catch (IOException e) {
            throw new FxmlLoadingException("Failed to load FXML file", e);
        }
    }

}
