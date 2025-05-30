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
 * The AboutViewDesktopFX class is responsible for initializing and managing the
 * desktop "About" view within a JavaFX application. It utilizes an FXML file for
 * the layout and a corresponding controller to manage view-related logic.
 */
@Getter
public class
AboutViewDesktopFX {

    /**
     * Represents the root Node of the "About" view in a JavaFX application. This node is
     * the top-level container for all UI elements associated with the "About" section,
     * loaded from an FXML file.
     * <p>
     * The `root` node is initialized during the construction of the `AboutViewDesktopFX` class
     * by loading the FXML file and obtaining its root element. It is also configured to be
     * managed and visible within the application. This node serves as the entry point for
     * adding the "About" view to a JavaFX scene or layout.
     * <p>
     * The `root` field is immutable and ensures the proper lifecycle and integrity of the
     * associated view within the application's user interface.
     */
    private final Node root;

    /**
     * Represents the controller responsible for managing the user interactions and logic
     * associated with the "About" view in a JavaFX application. This controller integrates
     * with the FXML-defined UI and defines actions for various events, such as button clicks
     * to perform tasks like downloading resources, navigating to a specific URL, or sending an email.
     * <p>
     * This field is initialized when the FXML file for the "About" view is loaded and corresponds to
     * the controller defined in the FXML. It facilitates communication between the UI and the
     * backend logic for the "About" view.
     * <p>
     * The controller is immutable, ensuring a consistent relationship between the view
     * and its behavior within the application lifecycle.
     */
    private final AboutViewDesktopFXController controller;

    /**
     * Initializes the AboutViewDesktopFX instance by loading the FXML layout
     * and its associated controller for the "About" view of the application.
     * <p>
     * The constructor attempts to load the FXML file located at "/fxml/about-view.fxml"
     * and sets up the root node and controller for the user interface. The loaded
     * root node is enabled for automatic management and visibility.
     * <p>
     * If the FXML file fails to load due to an IOException, an error message is logged
     * and a RuntimeException is thrown to indicate the failure.
     * <p>
     * This constructor is intended for creating the "About" section of the application's
     * user interface in a JavaFX environment.
     */
    public AboutViewDesktopFX() {
        LoggingContext.setComponent("AboutViewDesktopFX");
        LoggingUtils.logInitializing("About View");
        try {
            // Load the FXML file for the "About" view with the resource bundle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/about-view.fxml"), I18nUtils.getResourceBundle());

            // Save the root node of the FXML
            root = fxmlLoader.load();

            // Save the controller associated with the FXML
            controller = fxmlLoader.getController();

            // Enable automatic management and visibility of the root container
            root.setManaged(true);
            root.setVisible(true);

            LoggingUtils.logInitialized("About View");
        } catch (IOException e) {
            LoggingUtils.logError("Failed to load About View FXML file", e);
            throw new FxmlLoadingException("Failed to load FXML file", e);
        }
    }
}
