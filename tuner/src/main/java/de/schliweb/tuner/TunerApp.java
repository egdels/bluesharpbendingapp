package de.schliweb.tuner;
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

import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.util.SystemInfo;

import javax.swing.*;


/**
 * The TunerApp class serves as the entry point for the tuner application. It handles
 * initialization of platform-specific system properties and look-and-feel settings,
 * and starts the real-time tuning process.
 * <p>
 * This class is responsible for:
 * - Configuring system properties specific to macOS and Linux for UI adjustments.
 * - Setting up the look-and-feel theme for the application's user interface.
 * - Instantiating and managing the lifecycle of the RealTimeTuner.
 */
public class TunerApp {

    /**
     * A static variable representing the instance of {@code RealTimeTuner} used for managing
     * the application's real-time tuning functionality. This object is responsible for
     * handling tuning processes during the application's lifecycle.
     * <p>
     * The {@code realTimeTuner} is initialized during the application's startup and is used
     * to start and stop tuning as part of the application's primary operations.
     * <p>
     * It is statically managed to ensure that only one instance exists throughout the
     * application's lifecycle.
     */
    private static RealTimeTuner realTimeTuner;


    /**
     * The main method serves as the application's entry point, initializing platform-specific
     * configurations, setting up the application's look-and-feel, and starting the real-time
     * tuning process.
     * <p>
     * The method performs the following tasks:
     * - Configures macOS-specific properties for menu bar behavior, application name, and window appearance.
     * - Enables custom window decorations for Linux platforms.
     * - Sets various UI properties related to the FlatLaf theme.
     * - Instantiates the RealTimeTuner object and starts the tuning process.
     *
     * @param args Command-line arguments passed when the application is executed.
     */
    public static void main(String[] args) {
        if (SystemInfo.isMacOS) {
            // enable screen menu bar
            // (moves menu bar from JFrame window to top of screen)
            System.setProperty("apple.laf.useScreenMenuBar", "false");

            // application name used in screen menu bar
            // (in first menu after the "apple" menu)
            System.setProperty("apple.awt.application.name", "Tuner");

            // appearance of window title bars
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (must be set on main thread and before AWT/Swing is initialized;
            //  setting it on AWT thread does not work)
            System.setProperty("apple.awt.application.appearance", "system");
        }

        System.setProperty("flatlaf.menuBarEmbedded", "true");
        System.setProperty("flatlaf.useWindowDecorations", "true");
        FlatArcDarkOrangeIJTheme.setup();

        if (SystemInfo.isLinux) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        realTimeTuner = new RealTimeTuner(new TunerFrame());
        realTimeTuner.startTuning();
    }


    /**
     * Shuts down the application.
     * <p>
     * This method stops the real-time tuning process and terminates the application.
     * <p>
     * Responsibilities:
     * - Invokes the `stopTuning` method on the `realTimeTuner` instance to safely halt real-time tuning operations.
     * - Exits the application by calling `System.exit(0)`.
     * <p>
     * Usage Context:
     * Typically called when the user exits the application to ensure all resources are released and
     * the application is properly terminated.
     */
    public static void close() {
        realTimeTuner.stopTuning();
        System.exit(0);
    }
}