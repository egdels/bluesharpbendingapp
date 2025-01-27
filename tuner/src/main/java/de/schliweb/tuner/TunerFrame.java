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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import de.schliweb.bluesharpbendingapp.view.desktop.TuningMeter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The TunerFrame class represents the graphical user interface (GUI) for the tuner application.
 * It provides a visual interface for displaying the current frequency, note, and tuning status
 * to the user. The class extends JFrame and uses Swing components for building the UI.
 * <p>
 * Responsibilities of TunerFrame:
 * - Displays information about the current frequency and corresponding musical note.
 * - Shows tuning accuracy as a percentage using a progress bar with color-coded indicators.
 * - Provides window management, including opening, closing, and resizing the application frame.
 */
public class TunerFrame extends JFrame {

    private TuningMeter tuningMeter; // Replaces the old JProgressBar

    /**
     * The root content panel of the {@code TunerFrame} GUI.
     * <p>
     * This field is used to encapsulate all components of the
     * {@code TunerFrame} window. It serves as the container
     * for the progress bar, frequency label, and note label.
     * <p>
     * The layout and structure of the components within the
     * panel are defined in the corresponding IntelliJ IDEA
     * GUI Designer form file (`TunerFrame.form`).
     * <p>
     * The panel uses a grid layout where each component occupies
     * specific rows and columns as defined in the layout manager.
     * This field is automatically bound and initialized during the
     * construction of the {@code TunerFrame} class.
     */
    private JPanel contentPane;

    /**
     * Represents a JLabel to display the current frequency in the user interface.
     * <p>
     * This label is part of the GUI for the tuner application and is used to dynamically
     * provide frequency information to the user. It is updated in real-time as part of the
     * tuning process to reflect the frequency being detected or analyzed.
     * <p>
     * The label is initialized with an empty value and is updated programmatically
     * through the application's logic.
     */
    private JLabel frequencyLabel;

    /**
     * The JLabel component used to display the name of the musical note corresponding
     * to the detected frequency. This label is updated dynamically during the tuning
     * process to provide feedback to the user about the current note being played.
     * <p>
     * It is included in the user interface of the TunerFrame and is intended
     * to assist users in identifying tuning accuracy.
     */
    private JLabel noteLabel;

    /**
     * Constructs a new TunerFrame, initializing the user interface components and
     * setting up default properties and behaviors for the frame.
     * <p>
     * The frame contains:
     * - A label to display the current frequency information.
     * - A label to display the detected musical note.
     * - A progress bar to represent additional data visually.
     * <p>
     * Initialization Behavior:
     * - Sets the content pane to `contentPane`.
     * - Configures the window title to `null`.
     * - Displays placeholders for frequency and note information.
     * - Enables string display on the progress bar, initially set to "0.0".
     * - Sets the default close operation to do nothing, deferring control to a custom method.
     * - Adjusts the frame size when the window is shown, to predetermined dimensions (250x150).
     * <p>
     * Event Handling:
     * - Adds a listener to handle the window's close operation by invoking the `close` method to
     * properly dispose of resources and further close the application.
     * - Uses a component listener to adjust frame properties when the component is shown.
     */
    public TunerFrame() {
        $$$setupUI$$$();
        setContentPane(contentPane);
        setDefaultLookAndFeelDecorated(true);
        setTitle(null);

        frequencyLabel.setText("Frequency: -- Hz");
        noteLabel.setText("Note: --");


        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                setSize(250, 150);
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    /**
     * Updates the user interface to display the current frequency, musical note, and tuning offset in cents.
     * <p>
     * This method updates the text of the labels and progress bar to reflect the provided values.
     * It also adjusts the progress bar color and value based on the cents offset.
     *
     * @param frequency The frequency value to display, in Hz.
     * @param note      The detected musical note corresponding to the frequency.
     * @param cents     The tuning offset in cents, where -50 indicates the note is flat, +50 indicates sharp, and 0 is in tune.
     */
    public void updateUI(double frequency, String note, double cents) {
        frequencyLabel.setText(String.format("Frequency: %.0f Hz", frequency));
        noteLabel.setText("Note: " + note);

        tuningMeter.setCents(cents);
    }

    /**
     * Displays the TunerFrame window by packing its components into the frame
     * and making it visible to the user.
     * <p>
     * Responsibilities:
     * - Ensures that all components of the frame are compactly arranged by
     * invoking the {@code pack()} method.
     * - Makes the frame visible on the screen by calling {@code setVisible(true)}.
     * <p>
     * Usage Context:
     * Typically used to initialize and display the main application window
     * after the frame components and layout are configured.
     */
    public void open() {
        pack();
        setVisible(true);
    }

    /**
     * Handles the proper shutdown of the TunerFrame window and application.
     * <p>
     * This method ensures that all resources associated with the current frame
     * and the application are released and the application is terminated.
     * <p>
     * Responsibilities:
     * - Disposes of the TunerFrame instance to release its allocated resources.
     * - Delegates to the TunerApp's static `close` method to stop real-time
     * tuning operations and terminate the application.
     * <p>
     * Usage Context:
     * Typically invoked when the user closes the TunerFrame window to ensure
     * a graceful shutdown of the application and its resources.
     */
    private void close() {
        dispose();
        TunerApp.close();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        frequencyLabel = new JLabel();
        frequencyLabel.setText("");
        contentPane.add(frequencyLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        noteLabel = new JLabel();
        noteLabel.setText("");
        contentPane.add(noteLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contentPane.add(tuningMeter, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        tuningMeter = new TuningMeter();
    }
}
