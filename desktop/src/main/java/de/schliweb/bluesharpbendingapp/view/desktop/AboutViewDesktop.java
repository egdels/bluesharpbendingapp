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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import de.schliweb.bluesharpbendingapp.app.MainDesktop;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.view.AboutView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Represents the desktop version of the About View for the BluesHarpBendingApp.
 * This class provides a graphical interface for displaying application information
 * including version details, links for donations, contacting the developer,
 * and downloading the latest version.
 *
 * This implementation leverages {@link Desktop} APIs for handling external
 * actions such as opening links in a browser or composing an email.
 */
public class AboutViewDesktop implements AboutView {

    /**
     * A constant URL string that represents the download link for the application.
     * This URL is used to direct users to a web page where they can download the software.
     */
    private static final String DOWNLOAD = "https://www.letsbend.de/download";

    /**
     * A static final logger instance used for logging messages specific to the
     * AboutViewDesktop class. This provides a mechanism for producing debug,
     * informational, or error-level messages. The logger is associated with
     * the AboutViewDesktop class to include relevant contextual information
     * in its output.
     */
    private static final Logger LOGGER = new Logger(AboutViewDesktop.class);

    /**
     * A constant string representing a mailto URI for sending an email to a specific recipient.
     * The URI includes the recipient's email address and a prefilled subject line. The subject
     * line includes the application's version, which is dynamically obtained from the
     * implementation version of the `AboutViewDesktop` package.
     *
     * This value is primarily used to provide users with a preformatted email link
     * for contacting the developer or support team.
     */
    private static final String MAILTO = "mailto:christian.kierdorf@schliweb.de?subject=BluesHarpBendingApp%20" + AboutViewDesktop.class.getPackage().getImplementationVersion();

    /**
     * A constant URL string pointing to the PayPal.me page for receiving donations.
     * This URL is used to provide users with a way to support the application or
     * its development through monetary contributions.
     */
    private static final String PAYPAL = "https://paypal.me/egdels";

    /**
     * Represents the version information of the application.
     * The value is dynamically constructed by retrieving the implementation version
     * of the current package containing the {@link AboutViewDesktop} class.
     * This version information is used to display or log the current application version.
     */
    private static final String VERSION = "Version " + AboutViewDesktop.class.getPackage().getImplementationVersion();

    /**
     * Represents the main content panel of the `AboutViewDesktop` class.
     * This variable is used as the primary container for the graphical components
     * and layout of the about view in the desktop application.
     */
    private JPanel contentPane;
    /**
     * Represents a graphical label used for displaying the title information
     * in the "About" view of the desktop application. This label is intended
     * to visually denote or emphasize the title section of the AboutViewDesktop
     * interface.
     */
    private JLabel labelAboutTitle;
    /**
     * JLabel used to display version information in the About view of the desktop application.
     * Serves as a GUI component to inform the user about the current software version.
     */
    private JLabel labelAboutVersion;
    /**
     * The labelAboutPaypal field represents a Swing JLabel component that likely serves
     * as a visual element to display information related to PayPal.
     * It is a part of the AboutViewDesktop class, used within a desktop application.
     * This label could include text, a hyperlink, or a prompt associated with PayPal,
     * potentially for displaying donation or payment-related information in the UI.
     */
    private JLabel labelAboutPaypal;
    /**
     * Represents a JLabel component in the AboutViewDesktop class that is specifically
     * associated with displaying information or metadata about the application's download.
     * This label may include download-related details, such as links to resources or
     * instructions for downloading.
     */
    private JLabel labelAboutDownload;
    /**
     * JLabel component for displaying information about contacting or reaching
     * out via email, specific to the About section of the software.
     * This label typically contains text or links related to emailing the software
     * author or support team, and is part of the AboutViewDesktop interface.
     */
    private JLabel labelAboutMailMe;
    /**
     * A JLabel component used to display an informative image in the "About" view.
     * The image typically serves as a visual representation related to the application,
     * such as a logo or an about-related graphic.
     * This field is part of the graphical user interface for presenting application details.
     */
    private JLabel aboutImage;

    /**
     * Constructs the AboutViewDesktop, initializing the UI components and their actions for the "About" view of the
     * Desktop version. This includes setting up the about image, labels for donation, email, download links, and handling
     * their respective mouse click events. Certain elements can be hidden based on the provided donation-ware flag.
     *
     * @param isDonationWare a boolean flag indicating whether the application is considered donation-ware. If {@code false},
     *                        the donation and related options (e.g., "Mail me", "Buy me a coffee", "Download latest version")
     *                        are disabled and not visible within the UI.
     */
    public AboutViewDesktop(boolean isDonationWare) {

        aboutImage.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("ic_about.png"))));
        aboutImage.setText(null);

        labelAboutPaypal.setText("<html><u>Buy me a coffee</u></html>");
        labelAboutPaypal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelAboutPaypal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Desktop desktop;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        URI url;
                        try {
                            url = new URI(PAYPAL);
                            desktop.browse(url);
                        } catch (URISyntaxException | IOException exception) {
                            LOGGER.error(exception.getMessage());
                        }
                    }
                } else {
                    LOGGER.error("desktop doesn't support browse");
                }
            }
        });


        labelAboutMailMe.setText("<html><u>Mail me</u></html>");
        labelAboutMailMe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelAboutMailMe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Desktop desktop;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.MAIL)) {
                        URI mailto;
                        try {
                            mailto = new URI(MAILTO);
                            desktop.mail(mailto);
                        } catch (URISyntaxException | IOException exception) {
                            LOGGER.error(exception.getMessage());
                        }
                    }
                } else {
                    LOGGER.error("desktop doesn't support mailto");
                }
            }
        });

        String versionFromHost = MainDesktop.getVersionFromHost();
        if (versionFromHost != null && !versionFromHost.equals(AboutViewDesktop.class.getPackage().getImplementationVersion())) {
            labelAboutDownload.setText("<html><u>Download latest version (Release " + versionFromHost + " available)</u></html>");
        } else {
            labelAboutDownload.setText("<html><u>Download latest version</u></html>");
        }
        labelAboutDownload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelAboutDownload.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Desktop desktop;
                if (Desktop.isDesktopSupported() && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.BROWSE)) {
                    URI download;
                    try {
                        download = new URI(DOWNLOAD);
                        desktop.browse(download);
                    } catch (URISyntaxException | IOException exception) {
                        LOGGER.error(exception.getMessage());
                    }
                } else {
                    LOGGER.error("desktop doesn't support download");
                }
            }
        });

        labelAboutTitle.setText("Desktop version of Let's Bend - BluesHarpBendingApp");
        labelAboutVersion.setText(VERSION);

        if (!isDonationWare) {
            labelAboutMailMe.setVisible(false);
            labelAboutPaypal.setVisible(false);
            labelAboutDownload.setVisible(false);
        }

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        labelAboutTitle = new JLabel();
        labelAboutTitle.setText("Simple prototype of BluesHarpBendingApp");
        contentPane.add(labelAboutTitle, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutVersion = new JLabel();
        labelAboutVersion.setText("Version");
        contentPane.add(labelAboutVersion, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutPaypal = new JLabel();
        labelAboutPaypal.setText("Buy me a coffee");
        contentPane.add(labelAboutPaypal, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutMailMe = new JLabel();
        labelAboutMailMe.setText("Mail me");
        contentPane.add(labelAboutMailMe, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutImage = new JLabel();
        aboutImage.setText("aboutImage");
        contentPane.add(aboutImage, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutDownload = new JLabel();
        labelAboutDownload.setText("Download latest version");
        contentPane.add(labelAboutDownload, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
