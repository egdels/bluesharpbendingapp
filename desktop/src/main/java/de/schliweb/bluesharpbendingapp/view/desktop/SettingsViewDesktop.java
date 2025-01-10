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
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;


/**
 * The SettingsViewDesktop class provides functionality for managing and displaying desktop settings
 * for harp configurations, microphone settings, and note settings. It implements HarpSettingsView,
 * MicrophoneSettingsView, and NoteSettingsView interfaces to support various configuration
 * operations for musical instruments and audio input devices.
 */
public class SettingsViewDesktop implements HarpSettingsView, MicrophoneSettingsView, NoteSettingsView {


    /**
     * A JLabel component used to display a label for algorithms in the settings view.
     * This label is typically paired with a selection component (e.g., a dropdown menu)
     * to indicate the context or purpose of the selection related to algorithms.
     */
    private JLabel labelAlgorithms;
    /**
     * A JComboBox used to display and select different algorithm options
     * within the settings view of the application. The combo box contains
     * a list of strings representing available algorithms, which can be
     * dynamically updated through associated methods in the class.
     */
    private JComboBox<String> comboAlgorithms;
    /**
     * JLabel component used for displaying a descriptive label for the microphones selection.
     * This graphical user interface element is part of the settings view and is intended
     * to provide context for the associated microphone selection dropdown.
     */
    private JLabel labelMicrophones;
    /**
     * A combo box component designed for selecting available microphone options.
     * This graphical user interface element allows users to choose from a list of
     * microphones, typically populated by the application with supported or
     * detected devices.
     */
    private JComboBox<String> comboMicrophones;
    /**
     * Represents a label component used to display or annotate
     * information related to frequency in the user interface.
     * Typically part of the settings view for displaying text
     * or instructions alongside frequency values.
     */
    private JLabel labelFrequency;
    /**
     * Represents a visual label component in the SettingsViewDesktop class to display
     * or indicate the frequency value. This label is used as part of the user interface
     * related to frequency settings, providing a textual representation or descriptor
     * for frequency information within the application's settings.
     */
    private JLabel valueFrequency;
    /**
     * Represents a label component for displaying or identifying volume-related
     * information in the settings view. Typically used as a descriptor alongside
     * volume adjustment controls within the user interface.
     */
    private JLabel labelVolume;
    /**
     * Represents a graphical label component used to display the current
     * volume level in the settings view of the application.
     * This label is part of the user interface and is intended to show
     * non-editable volume-related information visually.
     */
    private JLabel valueVolume;
    /**
     * A JLabel instance used to display a label for the keys in the settings view.
     * It is part of the user interface of the SettingsViewDesktop class.
     * This label is likely associated with a component that allows the user
     * to choose or view musical keys in the application.
     */
    private JLabel labelKeys;
    /**
     * Represents a combo box containing selectable key options in the settings view.
     * This component allows users to choose from a list of musical keys to configure
     * settings related to key selection in the application.
     */
    protected JComboBox<String> comboKeys;
    /**
     * Represents a JLabel component in the `SettingsViewDesktop` class,
     * specifically used to display the label for selecting or displaying tunes.
     * It serves as a visual identifier for a related component such as a dropdown
     * or another UI element that interacts with musical tune selections.
     */
    private JLabel labelTunes;
    /**
     * The comboTunes field is a drop-down menu (JComboBox) designed to display and allow the selection
     * of various tuning options in the settings view. Each item in the combo box represents a specific
     * tuning configuration that can be selected by the user.
     * <p>
     * This component contributes to the graphical user interface, enabling users to configure
     * their preferred tuning setup within the application. Its contents and behavior can be
     * programmatically updated or modified through appropriate methods in the corresponding class.
     */
    protected JComboBox<String> comboTunes;
    /**
     * Represents the panel container for the graphical user interface of the
     * SettingsViewDesktop class. This panel serves as the root component for
     * organizing and displaying various UI elements and controls within the
     * settings view.
     */
    private JPanel contentPane;
    /**
     * Represents a label component for displaying the "Concert Pitch" text or relevant information
     * in the user interface of the application. Typically used for identifying a dropdown or
     * selection component related to concert pitch settings.
     */
    private JLabel labelConcertPitch;
    /**
     * A drop-down combo box component in the `SettingsViewDesktop` class that allows
     * users to select a concert pitch. The concert pitch refers to the reference
     * frequency used for tuning musical instruments (e.g., A440 Hz for standard tuning).
     * <p>
     * This variable is part of the graphical user interface and provides a
     * mechanism for selecting predefined tuning options or pitch standards.
     */
    private JComboBox<String> comboConcertPitches;
    /**
     * Represents a button in the SettingsViewDesktop class that resets all
     * settings to their default values when clicked. This button provides
     * users with an easy method to revert their configurations to the application's
     * original state.
     */
    private JButton resetSettingsButton;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Constructs a SettingsViewDesktop object to initialize the settings view
     * for the desktop application. Depending on the value of the isDonationWare
     * parameter, certain components of the interface may be hidden.
     *
     * @param isDonationWare a boolean indicating whether the application is being
     *                       used as donation-ware. If false, specific UI components
     *                       related to algorithms, frequency, and volume are disabled
     *                       and hidden.
     */
    public SettingsViewDesktop(boolean isDonationWare) {
        if (!isDonationWare) {
            labelAlgorithms.setVisible(false);
            comboAlgorithms.setVisible(false);
            labelFrequency.setVisible(false);
            valueFrequency.setVisible(false);
            labelVolume.setVisible(false);
            valueVolume.setVisible(false);
        }
        resetSettingsButton.addActionListener(new ResetAction());
    }

    @Override
    public void setKeys(String[] keys) {
        comboKeys.setModel(new DefaultComboBoxModel<>(keys));
    }

    @Override
    public void setSelectedKey(int index) {
        comboKeys.getModel().setSelectedItem(comboKeys.getModel().getElementAt(index));
    }

    @Override
    public void setSelectedTune(int index) {
        comboTunes.getModel().setSelectedItem(comboTunes.getModel().getElementAt(index));
    }

    @Override
    public void setTunes(String[] tunes) {
        comboTunes.setModel(new DefaultComboBoxModel<>(tunes));
    }

    @Override
    public void setAlgorithms(String[] algorithms) {
        comboAlgorithms.setModel(new DefaultComboBoxModel<>(algorithms));
    }

    @Override
    public void setFrequency(double frequency) {
        valueFrequency.setText(String.valueOf(frequency));
    }

    @Override
    public void setMicrophones(String[] microphones) {
        comboMicrophones.setModel(new DefaultComboBoxModel<>(microphones));
    }

    @Override
    public void setSelectedAlgorithm(int index) {
        comboAlgorithms.getModel().setSelectedItem(comboAlgorithms.getModel().getElementAt(index));
    }

    @Override
    public void setSelectedMicrophone(int index) {
        comboMicrophones.getModel().setSelectedItem(comboMicrophones.getModel().getElementAt(index));
    }

    @Override
    public void setVolume(double volume) {
        valueVolume.setText(String.valueOf(volume));
    }

    @Override
    public void setConcertPitches(String[] pitches) {
        comboConcertPitches.setModel(new DefaultComboBoxModel<>(pitches));
    }

    @Override
    public void setSelectedConcertPitch(int index) {
        comboConcertPitches.getModel().setSelectedItem(comboConcertPitches.getModel().getElementAt(index));
    }

    /**
     * Adds a handler for the harp settings view. This method associates the provided
     * HarpSettingsViewHandler with the combo boxes for key and tuning selection.
     * The handler is notified when a new key or tuning is selected.
     *
     * @param harpSettingsViewHandler the handler responsible for managing key and tuning-related
     *                                actions in the harp settings view
     */
    public void addHarpSettingsViewHandler(HarpSettingsViewHandler harpSettingsViewHandler) {
        comboKeys.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                harpSettingsViewHandler.handleKeySelection(comboKeys.getSelectedIndex());
            }
        });

        comboTunes.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                harpSettingsViewHandler.handleTuneSelection(comboTunes.getSelectedIndex());
            }
        });
    }

    /**
     * Adds a handler for the microphone settings view. This method associates the provided
     * MicrophoneSettingsViewHandler with the combo boxes for algorithm and microphone selection.
     * The handler is notified when a new algorithm or microphone is selected.
     *
     * @param microphoneSettingsViewHandler the handler responsible for managing actions related
     *                                      to algorithm and microphone selection in the settings
     *                                      view
     */
    public void addMicrophoneSettingsViewHandler(MicrophoneSettingsViewHandler microphoneSettingsViewHandler) {
        comboAlgorithms.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                microphoneSettingsViewHandler.handleAlgorithmSelection(comboAlgorithms.getSelectedIndex());
            }
        });

        comboMicrophones.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                microphoneSettingsViewHandler.handleMicrophoneSelection(comboMicrophones.getSelectedIndex());
            }
        });
    }

    /**
     * Adds a handler for the note settings view. The provided NoteSettingsViewHandler
     * is notified when a concert pitch is selected from the combo box of concert pitches.
     *
     * @param noteSettingsViewHandler the handler responsible for managing note settings-related
     *                                actions, such as handling the selection of a concert pitch
     *                                from the user interface
     */
    public void addNoteSettingsViewHandler(NoteSettingsViewHandler noteSettingsViewHandler) {
        comboConcertPitches.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                noteSettingsViewHandler.handleConcertPitchSelection(comboConcertPitches.getSelectedIndex());
            }
        });
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
        contentPane.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        labelAlgorithms = new JLabel();
        labelAlgorithms.setText("Algorithms");
        contentPane.add(labelAlgorithms, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboAlgorithms = new JComboBox();
        contentPane.add(comboAlgorithms, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelMicrophones = new JLabel();
        labelMicrophones.setText("Microphones");
        contentPane.add(labelMicrophones, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboMicrophones = new JComboBox();
        contentPane.add(comboMicrophones, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelFrequency = new JLabel();
        labelFrequency.setText("Frequency");
        contentPane.add(labelFrequency, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(68, 32), null, 0, false));
        labelVolume = new JLabel();
        labelVolume.setText("Volume");
        contentPane.add(labelVolume, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        valueFrequency = new JLabel();
        valueFrequency.setText("xxxxxxxxxxxxxxxx");
        contentPane.add(valueFrequency, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(112, 32), null, 0, false));
        valueVolume = new JLabel();
        valueVolume.setText("xxxxxxxxxxxxxxxx");
        contentPane.add(valueVolume, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelKeys = new JLabel();
        labelKeys.setText("Keys");
        contentPane.add(labelKeys, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboKeys = new JComboBox();
        contentPane.add(comboKeys, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelTunes = new JLabel();
        labelTunes.setText("Tunes");
        contentPane.add(labelTunes, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboTunes = new JComboBox();
        contentPane.add(comboTunes, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelConcertPitch = new JLabel();
        labelConcertPitch.setText("Concert Pitch (Hz)");
        contentPane.add(labelConcertPitch, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboConcertPitches = new JComboBox();
        contentPane.add(comboConcertPitches, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resetSettingsButton = new JButton();
        resetSettingsButton.setText("Reset Settings");
        contentPane.add(resetSettingsButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAlgorithms.setLabelFor(comboMicrophones);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    /**
     * ResetAction is a private inner class that implements the ActionListener interface.
     * It is responsible for listening to reset actions triggered by the associated reset
     * button in the SettingsViewDesktop class. Upon receiving an action event from the reset
     * button, it resets the application settings to their default values stored in the MainModel.
     */
    private class ResetAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == resetSettingsButton) {
                MainModel model = new MainModel();
                setSelectedConcertPitch(model.getStoredConcertPitchIndex());
                setSelectedKey(model.getStoredKeyIndex());
                setSelectedAlgorithm(model.getStoredAlgorithmIndex());
                setSelectedMicrophone(model.getStoredMicrophoneIndex());
                setSelectedTune(model.getStoredTuneIndex());
            }
        }
    }

}
