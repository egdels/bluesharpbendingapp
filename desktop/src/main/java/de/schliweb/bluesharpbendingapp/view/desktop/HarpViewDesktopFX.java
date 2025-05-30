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

import de.schliweb.bluesharpbendingapp.utils.I18nUtils;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.Getter;

import java.io.IOException;

/**
 * Represents the desktop version of the Harp view in a JavaFX application.
 * This class is responsible for loading and managing the FXML-based user interface
 * for the Harp component. It initializes the graphical root node and binds the
 * controller class to handle user interactions and events.
 */
@Getter
public class HarpViewDesktopFX {

    /**
     * Represents the controller instance responsible for handling the behavior and user interactions
     * of the Harp view within the JavaFX application. This controller is automatically bound to the
     * corresponding FXML file during the loading process and is used to manage the logic and
     * event handling for the associated user interface.
     */
    private final HarpViewDesktopFXController controller;

    /**
     * The root node of the FXML-based user interface for the Harp view in a JavaFX application.
     * This node serves as the top-level container for the graphical elements defined in the
     * corresponding FXML file. It is managed and made visible within the application's scene graph.
     * <p>
     * This root node is initialized during the construction of the HarpViewDesktopFX class
     * by loading the associated FXML file and binding it to the corresponding controller.
     * <p>
     * The root node is expected to define the primary structure of the Harp view's graphical
     * interface, which is subsequently used to render the view and handle user interactions.
     */
    private final Node root;

    /**
     * Initializes a new instance of the HarpViewDesktopFX class, which is responsible
     * for loading and managing the JavaFX FXML user interface for the Harp view.
     * <p>
     * This constructor performs the following tasks:
     * 1. Loads the FXML file located at the predefined path specific to the Harp view.
     * 2. Initializes and sets the root graphical node for the view.
     * 3. Associates and initializes the controller responsible for handling logic and user interaction.
     * 4. Sets the root node as managed and visible within the JavaFX scene graph.
     * <p>
     * If an error occurs during the FXML loading process, it logs the error message
     * through the LOGGER instance and throws a RuntimeException, preventing further execution.
     * <p>
     * Note: The FXML file must be located in the specified resource path and be valid,
     * or the constructor will fail with an exception.
     * <p>
     * Throws:
     * - RuntimeException if there is an issue loading the FXML file.
     */
    public HarpViewDesktopFX() {
        LoggingContext.setComponent("HarpViewDesktopFX");
        LoggingUtils.logInitializing("Harp View");
        try {
            // Load the FXML file for the "Harp" view with the resource bundle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/harp-view.fxml"), I18nUtils.getResourceBundle());

            // Save the root node of the FXML
            root = fxmlLoader.load();

            // Save the controller associated with the FXML
            controller = fxmlLoader.getController();

            // Bind the size directly to the parent container
            root.setManaged(true);
            root.setVisible(true);

            LoggingUtils.logInitialized("Harp View");
        } catch (IOException e) {
            LoggingUtils.logError("Failed to load Harp View FXML file", e);
            throw new FxmlLoadingException("Failed to load FXML file", e);
        }
    }


}
