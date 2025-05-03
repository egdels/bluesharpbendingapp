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

import de.schliweb.bluesharpbendingapp.service.VersionService;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.AboutView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.net.URI;
import java.util.Objects;

/**
 * The AboutViewDesktopFXController class serves as the controller for the "About" view in the desktop
 * JavaFX application. It provides functionality related to displaying application information, such as
 * the version, email support link, and donation options.
 * <p>
 * This controller implements the AboutView interface, ensuring that it abides by the contract for
 * displaying "About" information. The controller handles user interactions, such as clicking on links
 * to download the application or make donations through provided URLs.
 */
public class AboutViewDesktopFXController implements AboutView {

    /**
     * The DOWNLOAD constant holds the URL for downloading the application from the
     * official website. This URL is used within the application to direct users
     * to the specified download page when required.
     * <p>
     * It is a static, immutable string representing the download link, ensuring that
     * the URL remains unchanged throughout the application's lifecycle and can be
     * used globally within the AboutViewDesktopFXController class.
     */
    private static final String DOWNLOAD = "https://www.letsbend.de/";

    /**
     * A constant that represents a "mailto" URL string used to generate an email link.
     * This URL is preformatted to include the email address "christian.kierdorf@schliweb.de"
     * and automatically append a subject line that incorporates the implementation version
     * of the application based on the current package of the {@code AboutViewDesktopFXController} class.
     * <p>
     * This constant is primarily used to allow users to send emails with predefined context,
     * streamlining communication related to the application.
     */
    private static final String MAILTO = "mailto:christian.kierdorf@schliweb.de?subject=BluesHarpBendingApp%20" + AboutViewDesktopFXController.class.getPackage().getImplementationVersion();

    /**
     * Represents the PayPal donation link for the application.
     * This static constant holds the URL to the application's designated
     * PayPal.me page, allowing users to support development through donations.
     */
    private static final String PAYPAL = "https://paypal.me/egdels";

    /**
     * Represents the User-Guide link for the application.
     * This static constant holds the URL to the application's User-Guide page,
     * providing users with comprehensive documentation on how to use the application.
     */
    private static final String USER_GUIDE = "https://www.letsbend.de/user-guide";

    /**
     * FXML-bound JavaFX ImageView component used to display the "About" image
     * in the application's user interface. This field is linked to the
     * corresponding element in the FXML layout file for the AboutViewDesktopFX
     * controller.
     */
    @FXML
    private ImageView aboutImage;

    /**
     * A JavaFX Label component used to display the software version information in the GUI.
     * This label is part of the `AboutViewDesktopFXController` and is initialized in the corresponding FXML file.
     */
    @FXML
    private Label versionLabel;

    /**
     * Initializes the "About" view in the application.
     * <p>
     * This method performs the following tasks:
     * 1. Sets the "About" image by loading the appropriate resource from the application's files.
     * 2. Updates the version label with the current application version and compares it with the latest version from the host.
     * <p>
     * It is automatically invoked by the JavaFX framework when the "About" view is loaded.
     */
    @FXML
    public void initialize() {
        LoggingContext.setComponent("AboutViewDesktopFXController");
        LoggingUtils.logInitializing("About View Controller");

        // Load the image for the "About" view
        aboutImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ic_about.png"))));
        LoggingUtils.logDebug("About image loaded");

        // Set the application version in the label
        // The version is retrieved from the package implementation details and compared with the latest version from the host
        String latestVersion = VersionService.getVersionFromHost();
        if (latestVersion != null) {
            String versionText = "Version " + getClass().getPackage().getImplementationVersion() + " (Latest: " + VersionService.getVersionFromHost() + ")";
            versionLabel.setText(versionText);
            LoggingUtils.logDebug("Version label set", versionText);
        } else {
            String versionText = "Version " + getClass().getPackage().getImplementationVersion();
            versionLabel.setText(versionText);
            LoggingUtils.logDebug("Version label set", versionText);
        }

        LoggingUtils.logInitialized("About View Controller");
    }

    /**
     * Handles the click event for initiating a download action in the "About" view.
     * <p>
     * This method invokes the `openLink` method with a predefined download URL.
     * It utilizes the system's default web browser to open the specified link.
     * The link is expected to point to a resource where users can obtain the application update or related materials.
     * <p>
     * This method is triggered by a user interaction on the associated UI element defined in the FXML file.
     */
    @FXML
    private void handleDownloadClick() {
        LoggingContext.setComponent("AboutViewDesktopFXController");
        LoggingUtils.logUserAction("Download Click", "Opening download link");
        openLink(DOWNLOAD);
    }

    /**
     * Handles the click event for the PayPal button in the "About" view.
     * <p>
     * This method is invoked when a user clicks on the PayPal button, triggering an action to
     * open the predefined PayPal URL in the system's default web browser.
     * <p>
     * The implementation delegates the task of opening the web link to the {@code openLink} method.
     * Any issues encountered during this process, such as an invalid URL or problems with the
     * desktop environment, will be logged by the application's logging mechanism.
     * <p>
     * This method is bound to the respective UI element via the FXML file.
     */
    @FXML
    private void handlePaypalClick() {
        LoggingContext.setComponent("AboutViewDesktopFXController");
        LoggingUtils.logUserAction("PayPal Click", "Opening PayPal donation link");
        openLink(PAYPAL);
    }

    /**
     * Handles the click event for the mail action in the "About" view.
     * <p>
     * This method is triggered when a user clicks on the assigned UI element,
     * such as a link or button, to initiate a mail-related action. It opens the
     * default mail client on the user's system by delegating the task to the
     * {@code openLink} method with a predefined "mailto:" URL.
     * <p>
     * The "mailto:" URL is stored in a class field and must comply with the
     * expected format for mail URIs. If any issue occurs while trying to open
     * the mail client, such as an invalid URI or problems with the desktop
     * environment, it is logged by the application's logging mechanism.
     */
    @FXML
    private void handleMailClick() {
        LoggingContext.setComponent("AboutViewDesktopFXController");
        LoggingUtils.logUserAction("Mail Click", "Opening mail client");
        openLink(MAILTO);
    }

    /**
     * Handles the click event for the User-Guide link in the "About" view.
     * <p>
     * This method is triggered when a user clicks on the User-Guide link in the UI.
     * It opens the application's User-Guide in the system's default web browser
     * by delegating the task to the {@code openLink} method with the predefined
     * User-Guide URL.
     * <p>
     * If any issue occurs while trying to open the web browser, such as an invalid
     * URI or problems with the desktop environment, it is logged by the application's
     * logging mechanism.
     */
    @FXML
    private void handleUserGuideClick() {
        LoggingContext.setComponent("AboutViewDesktopFXController");
        LoggingUtils.logUserAction("User-Guide Click", "Opening User-Guide");
        openLink(USER_GUIDE);
    }

    /**
     * Opens a hyperlink in the system's default web browser.
     *
     * @param uri the URL to be opened in the browser. The URI must be a valid web address.
     *            Invalid URIs or issues with the system's desktop environment
     *            will result in logging an error.
     */
    private void openLink(String uri) {
        LoggingContext.setComponent("AboutViewDesktopFXController");
        LoggingUtils.logDebug("Opening link", uri);
        try {
            Desktop.getDesktop().browse(new URI(uri));
            LoggingUtils.logOperationCompleted("Link opened successfully");
        } catch (Exception ex) {
            LoggingUtils.logError("Error while opening the link", ex);
        }
    }


}
