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
 * The type About view desktop.
 */
public class AboutViewDesktop implements AboutView {

    /**
     * The constant DOWNLOAD.
     */
    private static final String DOWNLOAD = "https://www.letsbend.de/download";

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(AboutViewDesktop.class);

    /**
     * The constant MAILTO.
     */
    private static final String MAILTO = "mailto:christian.kierdorf@schliweb.de?subject=BluesHarpBendingApp%20" + AboutViewDesktop.class.getPackage().getImplementationVersion();

    /**
     * The constant PAYPAL.
     */
    private static final String PAYPAL = "https://paypal.me/egdels";

    /**
     * The constant VERSION.
     */
    private static final String VERSION = "Version " + AboutViewDesktop.class.getPackage().getImplementationVersion();

    /**
     * The Content pane.
     */
    private JPanel contentPane;
    /**
     * The Label about title.
     */
    private JLabel labelAboutTitle;
    /**
     * The Label about version.
     */
    private JLabel labelAboutVersion;
    /**
     * The Label about thank you.
     */
    private JLabel labelAboutThankYou;
    /**
     * The Label about paypal.
     */
    private JLabel labelAboutPaypal;
    /**
     * The Label about download.
     */
    private JLabel labelAboutDownload;
    /**
     * The Label about mail me.
     */
    private JLabel labelAboutMailMe;
    /**
     * The About image.
     */
    private JLabel aboutImage;

    /**
     * Instantiates a new About view desktop.
     *
     * @param isDonationWare the is donation ware
     */
    public AboutViewDesktop(boolean isDonationWare) {

        aboutImage.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("ic_about.png"))));
        aboutImage.setText(null);

        labelAboutThankYou.setText("Thanks to https://github.com/JorenSix/TarsosDSP");

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
            labelAboutThankYou.setVisible(false);
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
        contentPane.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        labelAboutTitle = new JLabel();
        labelAboutTitle.setText("Simple prototype of BluesHarpBendingApp");
        contentPane.add(labelAboutTitle, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutVersion = new JLabel();
        labelAboutVersion.setText("Version");
        contentPane.add(labelAboutVersion, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutThankYou = new JLabel();
        labelAboutThankYou.setText("Thanks to");
        contentPane.add(labelAboutThankYou, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutPaypal = new JLabel();
        labelAboutPaypal.setText("Buy me a coffee");
        contentPane.add(labelAboutPaypal, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutMailMe = new JLabel();
        labelAboutMailMe.setText("Mail me");
        contentPane.add(labelAboutMailMe, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aboutImage = new JLabel();
        aboutImage.setText("aboutImage");
        contentPane.add(aboutImage, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAboutDownload = new JLabel();
        labelAboutDownload.setText("Download latest version");
        contentPane.add(labelAboutDownload, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
