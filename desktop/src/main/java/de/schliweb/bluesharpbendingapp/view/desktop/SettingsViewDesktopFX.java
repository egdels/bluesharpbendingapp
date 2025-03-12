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
 * The SettingsViewDesktopFX class manages the loading and initialization of the "Settings" user interface
 * defined in an FXML file. It provides access to the root node of the interface and its associated controller.
 */
@Getter
@Slf4j
public class SettingsViewDesktopFX {




    /**
     * The root node of the "Settings" user interface, loaded from the associated FXML file.
     * This node serves as the base of the UI hierarchy for the "Settings" view.
     * It is used as the primary entry point for rendering and managing the layout and components
     * defined within the view.
     */
    private final Node root;


    /**
     * The controller associated with the "Settings" view defined in the FXML file.
     * This instance provides access to the business logic and event handling for
     * the user interface components in the "Settings" view. It facilitates communication
     * between the visual elements and the underlying application logic, ensuring that
     * changes in the UI are properly handled and propagated.
     * <p>
     * The controller is initialized and managed by the FXMLLoader during the loading
     * of the FXML file. It encapsulates features for managing user interactions, updating
     * data bindings, and invoking appropriate handlers for various settings operations.
     */
    private final SettingsViewDesktopFXController controller;

    /**
     * Constructs a new instance of the SettingsViewDesktopFX class by loading the "Settings" view
     * from its corresponding FXML file and initializing its root node and controller.
     * <p>
     * Specifically, the constructor performs the following steps:
     * - Loads the FXML file located at "/fxml/settings-view.fxml" using an FXMLLoader instance.
     * - Retrieves and assigns the root node of the UI defined in the FXML file.
     * - Retrieves and assigns the controller associated with the FXML file.
     * - Configures the root node to be managed and visible.
     * <p>
     * If an error occurs during the FXML loading process, the constructor logs the error and propagates
     * a RuntimeException to indicate the failure.
     * <p>
     * The associated controller instance provides handlers and business logic for managing the "Settings" view
     * and its user interactions. The root node serves as the base UI component that can be integrated
     * into the broader application layout or displayed independently.
     * <p>
     * Throws:
     * - RuntimeException if the FXML file cannot be loaded.
     */
    public SettingsViewDesktopFX() {
        try {
            // Load the FXML file for the "Settings" view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/settings-view.fxml"));

            // Save the root element defined in the FXML
            root = fxmlLoader.load();

            // Save the controller instance associated with the FXML
            controller = fxmlLoader.getController();

            // Bind the size of the root element to the parent container
            root.setManaged(true);
            root.setVisible(true);

        } catch (IOException e) {
            throw new FxmlLoadingException("Failed to load FXML file", e);
        }
    }



}

