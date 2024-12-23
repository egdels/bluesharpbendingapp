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
 * The type Settings view desktop.
 */
public class SettingsViewDesktop implements HarpSettingsView, MicrophoneSettingsView, NoteSettingsView {


    /**
     * The Label algorithms.
     */
    private JLabel labelAlgorithms;
    /**
     * The Combo algorithms.
     */
    private JComboBox<String> comboAlgorithms;
    /**
     * The Label microphones.
     */
    private JLabel labelMicrophones;
    /**
     * The Combo microphones.
     */
    private JComboBox<String> comboMicrophones;
    /**
     * The Label frequency.
     */
    private JLabel labelFrequency;
    /**
     * The Value frequency.
     */
    private JLabel valueFrequency;
    /**
     * The Label volume.
     */
    private JLabel labelVolume;
    /**
     * The Value volume.
     */
    private JLabel valueVolume;
    /**
     * The Label keys.
     */
    private JLabel labelKeys;
    /**
     * The Combo keys.
     */
    private JComboBox<String> comboKeys;
    /**
     * The Label tunes.
     */
    private JLabel labelTunes;
    /**
     * The Combo tunes.
     */
    private JComboBox<String> comboTunes;
    /**
     * The Content pane.
     */
    private JPanel contentPane;
    /**
     * The Label probability.
     */
    private JLabel labelProbability;
    /**
     * The Value probability.
     */
    private JLabel valueProbability;
    /**
     * The Label concert pitch.
     */
    private JLabel labelConcertPitch;
    /**
     * The Combo concert pitches.
     */
    private JComboBox<String> comboConcertPitches;
    /**
     * The Reset settings button.
     */
    private JButton resetSettingsButton;

    /**
     * Instantiates a new Settings view desktop.
     *
     * @param isDonationWare the is donation ware
     */
    public SettingsViewDesktop(boolean isDonationWare) {
        if (!isDonationWare) {
            labelAlgorithms.setVisible(false);
            comboAlgorithms.setVisible(false);
            labelFrequency.setVisible(false);
            valueFrequency.setVisible(false);
            labelVolume.setVisible(false);
            valueVolume.setVisible(false);
            labelProbability.setVisible(false);
            valueProbability.setVisible(false);
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
    public void setProbability(double probability) {
        valueProbability.setText(String.valueOf(probability));
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
     * Add harp settings view handler.
     *
     * @param harpSettingsViewHandler the harp settings view handler
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
     * Add microphone settings view handler.
     *
     * @param microphoneSettingsViewHandler the microphone settings view handler
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
     * Add note settings view handler.
     *
     * @param noteSettingsViewHandler the note settings view handler
     */
    public void addNoteSettingsViewHandler(NoteSettingsViewHandler noteSettingsViewHandler) {
        comboConcertPitches.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                noteSettingsViewHandler.handleConcertPitchSelection(comboConcertPitches.getSelectedIndex());
            }
        });
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
        contentPane.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
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
        contentPane.add(labelKeys, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboKeys = new JComboBox();
        contentPane.add(comboKeys, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelTunes = new JLabel();
        labelTunes.setText("Tunes");
        contentPane.add(labelTunes, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboTunes = new JComboBox();
        contentPane.add(comboTunes, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelProbability = new JLabel();
        labelProbability.setText("Probability");
        contentPane.add(labelProbability, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        valueProbability = new JLabel();
        valueProbability.setText("xxxxxxxxxxxxxxxx");
        contentPane.add(valueProbability, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelConcertPitch = new JLabel();
        labelConcertPitch.setText("Concert Pitch (Hz)");
        contentPane.add(labelConcertPitch, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboConcertPitches = new JComboBox();
        contentPane.add(comboConcertPitches, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resetSettingsButton = new JButton();
        resetSettingsButton.setText("Reset Settings");
        contentPane.add(resetSettingsButton, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelAlgorithms.setLabelFor(comboMicrophones);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    /**
     * The type Reset action.
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
