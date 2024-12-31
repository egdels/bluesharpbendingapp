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
import de.schliweb.bluesharpbendingapp.controller.TrainingContainer;
import de.schliweb.bluesharpbendingapp.controller.TrainingViewHandler;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * The TrainingViewDesktop class provides a graphical user interface for managing
 * training sessions. It implements the TrainingView interface, allowing functionality
 * such as setting training options, updating precision levels, and displaying progress
 * and other training details. This desktop representation uses swing components to render
 * the interface.
 */
public class TrainingViewDesktop implements TrainingView {
    /**
     * The contentPane variable represents the main container panel for the application's
     * graphical user interface. It acts as a root container to aggregate and manage
     * other UI components, such as buttons, labels, and other sub-panels.
     */
    private JPanel contentPane;
    /**
     * Represents the start button in the TrainingViewDesktop interface.
     * This button is used to initiate the training process.
     */
    private JButton startButton;
    /**
     * A private JLabel component used to display text related to trainings
     * in the TrainingViewDesktop class. This label typically serves as a
     * static or informative display element within the training interface.
     */
    private JLabel labelTrainings;
    /**
     * Represents a combo box in the TrainingViewDesktop that allows users to select
     * from a predefined list of training options. The combo box dynamically updates
     * based on the available training configurations and allows users to make a
     * selection for training purposes.
     */
    private JComboBox<String> comboTrainings;
    /**
     * The `labelPercentage` is a `JLabel` component used to display textual
     * information related to the percentage of a specific process or value
     * within the `TrainingViewDesktop` interface.
     *
     * This label serves as a visual indicator for presenting percentage-based
     * data, such as progress or precision metrics, in a clear and user-friendly manner.
     */
    private JLabel labelPercentage;
    /**
     * Represents the header panel of the content pane in the TrainingViewDesktop UI.
     * This JPanel is a graphical container that can be used to display components
     * or organize layouts specifically for the header section.
     */
    private JPanel contentPaneHeader;
    /**
     * Represents the footer section of the content pane in the TrainingViewDesktop.
     * This JPanel is likely used to structure or organize UI components specific
     * to the footer area of the application view.
     */
    private JPanel contentPaneFooter;
    /**
     * The `stopButton` variable represents a JButton component that provides
     * functionality to stop or terminate an operation within the TrainingViewDesktop UI.
     * It is used to handle user interaction for stopping an ongoing training process or activity.
     */
    private JButton stopButton;
    /**
     * Represents the main content pane of the TrainingViewDesktop UI.
     * This JPanel serves as the container for the primary layout and
     * components of the desktop training view.
     */
    private JPanel contentPaneMain;
    /**
     * Represents a JPanel that visualizes the current note being displayed
     * or played in the TrainingViewDesktop. This panel could potentially
     * interact with or display components such as NotePane to represent
     * musical notes graphically, along with their pitch deviations or
     * other related information.
     */
    private JPanel actualNote;
    /**
     * A JProgressBar used to visually indicate the progress of a task or operation
     * within the TrainingViewDesktop. This component dynamically updates to show
     * progress completion as a percentage.
     */
    private JProgressBar progressBar;
    /**
     * The JLabel instance used to display the precision level of the selected training or operation.
     * This label provides a representation or description of the precision option currently active
     * in the TrainingViewDesktop interface.
     */
    private JLabel labelPrecision;
    /**
     * A combo box that allows the user to select precision levels.
     * Commonly used within the Training View Desktop for choosing different
     * levels of precision for training configurations or tasks.
     */
    private JComboBox<String> comboPrecision;

    /**
     * Constructs a `TrainingViewDesktop` object and initializes its components.
     * This constructor disables the stop button by default, ensuring that it
     * cannot be interacted with until specific actions enable it.
     */
    public TrainingViewDesktop() {
        stopButton.setEnabled(false);
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
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPaneHeader = new JPanel();
        contentPaneHeader.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(contentPaneHeader, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelTrainings = new JLabel();
        labelTrainings.setText("Trainings");
        contentPaneHeader.add(labelTrainings, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboTrainings = new JComboBox();
        contentPaneHeader.add(comboTrainings, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelPercentage = new JLabel();
        labelPercentage.setText("Progress");
        contentPaneHeader.add(labelPercentage, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        contentPaneHeader.add(progressBar, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelPrecision = new JLabel();
        labelPrecision.setText("Precision");
        contentPaneHeader.add(labelPrecision, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboPrecision = new JComboBox();
        contentPaneHeader.add(comboPrecision, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contentPaneFooter = new JPanel();
        contentPaneFooter.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(contentPaneFooter, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startButton = new JButton();
        startButton.setText("Start");
        contentPaneFooter.add(startButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setText("Stop");
        stopButton.setVerticalAlignment(0);
        contentPaneFooter.add(stopButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contentPaneMain = new JPanel();
        contentPaneMain.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(contentPaneMain, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        actualNote = new JPanel();
        actualNote.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        actualNote.setBackground(new Color(-6513759));
        contentPaneMain.add(actualNote, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        actualNote.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-16777216)));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    @Override
    public void setTrainings(String[] trainings) {
        comboTrainings.setModel(new DefaultComboBoxModel<>(trainings));
    }

    @Override
    public void setPrecisions(String[] precisions) {
        comboPrecision.setModel(new DefaultComboBoxModel<>(precisions));
    }

    @Override
    public void setSelectedTraining(int selectedTrainingIndex) {
        comboTrainings.getModel().setSelectedItem(comboTrainings.getModel().getElementAt(selectedTrainingIndex));
    }

    @Override
    public void setSelectedPrecision(int selectedPrecisionIndex) {
        comboPrecision.getModel().setSelectedItem(comboPrecision.getModel().getElementAt(selectedPrecisionIndex));
    }

    @Override
    public HarpViewNoteElement getActualHarpViewElement() {
        return HarpViewNoteElementDesktop.getInstance(actualNote);
    }

    @Override
    public void initTrainingContainer(TrainingContainer trainingContainer) {
        HarpViewNoteElementDesktop actualNoteElementDesktop = HarpViewNoteElementDesktop.getInstance(actualNote);
        actualNoteElementDesktop.setColor(actualNote.getBackground());
        actualNoteElementDesktop.setNoteName(trainingContainer.getActualNoteName() != null ? trainingContainer.getActualNoteName() : "");
        progressBar.setValue(trainingContainer.getProgress());
        actualNoteElementDesktop.clear();
    }

    @Override
    public void toggleButton() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    /**
     * Registers a {@link TrainingViewHandler} to manage training view interactions and events.
     *
     * This method adds listeners to the UI components such as combo boxes and buttons,
     * delegating the handling of training and precision selection, as well as start and stop
     * button actions, to the provided {@link TrainingViewHandler}.
     *
     * @param trainingViewHandler the handler responsible for managing training view interactions,
     *                            including selection and action events for training and precision,
     *                            as well as handling the start and stop of training processes
     */
    public void addTrainingViewHandler(TrainingViewHandler trainingViewHandler) {
        comboTrainings.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                trainingViewHandler.handleTrainingSelection(comboTrainings.getSelectedIndex());
            }
        });
        comboPrecision.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                trainingViewHandler.handlePrecisionSelection(comboPrecision.getSelectedIndex());
            }
        });
        startButton.addActionListener(event -> {
            if (event.getSource() == startButton) {
                trainingViewHandler.handleTrainingStart();
                stopButton.setEnabled(true);
                startButton.setEnabled(false);
            }
        });
        stopButton.addActionListener(event -> {
            if (event.getSource() == stopButton) {
                trainingViewHandler.handleTrainingStop();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
    }

}
