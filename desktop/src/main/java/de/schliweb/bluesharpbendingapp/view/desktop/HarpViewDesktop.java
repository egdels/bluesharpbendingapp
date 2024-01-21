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
import de.schliweb.bluesharpbendingapp.controller.Note;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * The type Harp view desktop.
 */
public class HarpViewDesktop implements HarpView {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(HarpViewDesktop.class);
    /**
     * The constant OVERBLOW_COLOR.
     */
    private static final Color OVERBLOW_COLOR = Color.MAGENTA;
    /**
     * The constant OVERDRAW_COLOR.
     */
    private static final Color OVERDRAW_COLOR = Color.MAGENTA;


    /**
     * The Channel 1 note 0.
     */
    private JPanel channel_1_note_0;
    /**
     * The Channel 2 note 0.
     */
    private JPanel channel_2_note_0;
    /**
     * The Channel 3 note 0.
     */
    private JPanel channel_3_note_0;
    /**
     * The Channel 4 note 0.
     */
    private JPanel channel_4_note_0;
    /**
     * The Channel 5 note 0.
     */
    private JPanel channel_5_note_0;
    /**
     * The Channel 6 note 0.
     */
    private JPanel channel_6_note_0;
    /**
     * The Channel 7 note 0.
     */
    private JPanel channel_7_note_0;
    /**
     * The Channel 8 note 0.
     */
    private JPanel channel_8_note_0;
    /**
     * The Channel 9 note 0.
     */
    private JPanel channel_9_note_0;
    /**
     * The Channel 10 note 0.
     */
    private JPanel channel_10_note_0;
    /**
     * The Channel 1 note m 1.
     */
    private JPanel channel_1_note_m1;
    /**
     * The Channel 1 note m 2.
     */
    private JPanel channel_1_note_m2;
    /**
     * The Channel 1 note m 3.
     */
    private JPanel channel_1_note_m3;
    /**
     * The Channel 1 note 1.
     */
    private JPanel channel_1_note_1;
    /**
     * The Channel 1 note 2.
     */
    private JPanel channel_1_note_2;
    /**
     * The Channel 1 note 3.
     */
    private JPanel channel_1_note_3;
    /**
     * The Channel 1 note 4.
     */
    private JPanel channel_1_note_4;
    /**
     * The Channel 2 note 1.
     */
    private JPanel channel_2_note_1;
    /**
     * The Channel 2 note m 1.
     */
    private JPanel channel_2_note_m1;
    /**
     * The Channel 2 note m 2.
     */
    private JPanel channel_2_note_m2;
    /**
     * The Channel 2 note m 3.
     */
    private JPanel channel_2_note_m3;
    /**
     * The Channel 2 note 2.
     */
    private JPanel channel_2_note_2;
    /**
     * The Channel 2 note 3.
     */
    private JPanel channel_2_note_3;
    /**
     * The Channel 2 note 4.
     */
    private JPanel channel_2_note_4;
    /**
     * The Channel 3 note 1.
     */
    private JPanel channel_3_note_1;
    /**
     * The Channel 3 note 2.
     */
    private JPanel channel_3_note_2;
    /**
     * The Channel 3 note 3.
     */
    private JPanel channel_3_note_3;
    /**
     * The Channel 3 note 4.
     */
    private JPanel channel_3_note_4;
    /**
     * The Channel 3 note m 1.
     */
    private JPanel channel_3_note_m1;
    /**
     * The Channel 3 note m 2.
     */
    private JPanel channel_3_note_m2;
    /**
     * The Channel 3 note m 3.
     */
    private JPanel channel_3_note_m3;
    /**
     * The Channel 4 note m 1.
     */
    private JPanel channel_4_note_m1;
    /**
     * The Channel 4 note m 2.
     */
    private JPanel channel_4_note_m2;
    /**
     * The Channel 4 note m 3.
     */
    private JPanel channel_4_note_m3;
    /**
     * The Channel 4 note 1.
     */
    private JPanel channel_4_note_1;
    /**
     * The Channel 4 note 2.
     */
    private JPanel channel_4_note_2;
    /**
     * The Channel 4 note 3.
     */
    private JPanel channel_4_note_3;
    /**
     * The Channel 4 note 4.
     */
    private JPanel channel_4_note_4;
    /**
     * The Channel 5 note 1.
     */
    private JPanel channel_5_note_1;
    /**
     * The Channel 5 note 2.
     */
    private JPanel channel_5_note_2;
    /**
     * The Channel 5 note 3.
     */
    private JPanel channel_5_note_3;
    /**
     * The Channel 5 note 4.
     */
    private JPanel channel_5_note_4;
    /**
     * The Channel 6 note 1.
     */
    private JPanel channel_6_note_1;
    /**
     * The Channel 6 note 2.
     */
    private JPanel channel_6_note_2;
    /**
     * The Channel 6 note 3.
     */
    private JPanel channel_6_note_3;
    /**
     * The Channel 6 note 4.
     */
    private JPanel channel_6_note_4;
    /**
     * The Channel 6 note m 1.
     */
    private JPanel channel_6_note_m1;
    /**
     * The Channel 6 note m 2.
     */
    private JPanel channel_6_note_m2;
    /**
     * The Channel 6 note m 3.
     */
    private JPanel channel_6_note_m3;
    /**
     * The Channel 5 note m 3.
     */
    private JPanel channel_5_note_m3;
    /**
     * The Channel 7 note 1.
     */
    private JPanel channel_7_note_1;
    /**
     * The Channel 7 note 2.
     */
    private JPanel channel_7_note_2;
    /**
     * The Channel 7 note 3.
     */
    private JPanel channel_7_note_3;
    /**
     * The Channel 7 note 4.
     */
    private JPanel channel_7_note_4;
    /**
     * The Channel 7 note m 1.
     */
    private JPanel channel_7_note_m1;
    /**
     * The Channel 7 note m 2.
     */
    private JPanel channel_7_note_m2;
    /**
     * The Channel 7 note m 3.
     */
    private JPanel channel_7_note_m3;
    /**
     * The Channel 8 note m 1.
     */
    private JPanel channel_8_note_m1;
    /**
     * The Channel 8 note m 2.
     */
    private JPanel channel_8_note_m2;
    /**
     * The Channel 8 note m 3.
     */
    private JPanel channel_8_note_m3;
    /**
     * The Channel 8 note 1.
     */
    private JPanel channel_8_note_1;
    /**
     * The Channel 8 note 2.
     */
    private JPanel channel_8_note_2;
    /**
     * The Channel 8 note 3.
     */
    private JPanel channel_8_note_3;
    /**
     * The Channel 8 note 4.
     */
    private JPanel channel_8_note_4;
    /**
     * The Channel 9 note 1.
     */
    private JPanel channel_9_note_1;
    /**
     * The Channel 9 note 2.
     */
    private JPanel channel_9_note_2;
    /**
     * The Channel 9 note 3.
     */
    private JPanel channel_9_note_3;
    /**
     * The Channel 9 note 4.
     */
    private JPanel channel_9_note_4;
    /**
     * The Channel 10 note 1.
     */
    private JPanel channel_10_note_1;
    /**
     * The Channel 10 note 2.
     */
    private JPanel channel_10_note_2;
    /**
     * The Channel 10 note 3.
     */
    private JPanel channel_10_note_3;
    /**
     * The Channel 10 note 4.
     */
    private JPanel channel_10_note_4;
    /**
     * The Channel 10 note m 1.
     */
    private JPanel channel_10_note_m1;
    /**
     * The Channel 10 note m 2.
     */
    private JPanel channel_10_note_m2;
    /**
     * The Channel 10 note m 3.
     */
    private JPanel channel_10_note_m3;
    /**
     * The Channel 9 note m 3.
     */
    private JPanel channel_9_note_m3;
    /**
     * The Channel 5 note m 2.
     */
    private JPanel channel_5_note_m2;
    /**
     * The Channel 9 note m 2.
     */
    private JPanel channel_9_note_m2;
    /**
     * The Channel 5 note m 1.
     */
    private JPanel channel_5_note_m1;
    /**
     * The Channel 9 note m 1.
     */
    private JPanel channel_9_note_m1;

    /**
     * The Content pane.
     */
    private JPanel contentPane;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Instantiates a new Harp view desktop.
     */
    public HarpViewDesktop() {
        hideNotes();
        LOGGER.info("Created");
    }

    /**
     * Hide notes.
     */
    private void hideNotes() {
        LOGGER.info("Enter");

        channel_1_note_m3.setVisible(false);
        channel_2_note_m3.setVisible(false);
        channel_3_note_m3.setVisible(false);
        channel_4_note_m3.setVisible(false);
        channel_5_note_m3.setVisible(false);
        channel_6_note_m3.setVisible(false);
        channel_7_note_m3.setVisible(false);
        channel_8_note_m3.setVisible(false);
        channel_9_note_m3.setVisible(false);
        channel_10_note_m3.setVisible(false);

        channel_1_note_m2.setVisible(false);
        channel_2_note_m2.setVisible(false);
        channel_3_note_m2.setVisible(false);
        channel_4_note_m2.setVisible(false);
        channel_5_note_m2.setVisible(false);
        channel_6_note_m2.setVisible(false);
        channel_7_note_m2.setVisible(false);
        channel_8_note_m2.setVisible(false);
        channel_9_note_m2.setVisible(false);
        channel_10_note_m2.setVisible(false);


        channel_1_note_m1.setVisible(false);
        channel_2_note_m1.setVisible(false);
        channel_3_note_m1.setVisible(false);
        channel_4_note_m1.setVisible(false);
        channel_5_note_m1.setVisible(false);
        channel_6_note_m1.setVisible(false);
        channel_7_note_m1.setVisible(false);
        channel_8_note_m1.setVisible(false);
        channel_9_note_m1.setVisible(false);
        channel_10_note_m1.setVisible(false);

        channel_1_note_0.setVisible(false);
        channel_2_note_0.setVisible(false);
        channel_3_note_0.setVisible(false);
        channel_4_note_0.setVisible(false);
        channel_5_note_0.setVisible(false);
        channel_6_note_0.setVisible(false);
        channel_7_note_0.setVisible(false);
        channel_8_note_0.setVisible(false);
        channel_9_note_0.setVisible(false);
        channel_10_note_0.setVisible(false);

        channel_1_note_1.setVisible(false);
        channel_2_note_1.setVisible(false);
        channel_3_note_1.setVisible(false);
        channel_4_note_1.setVisible(false);
        channel_5_note_1.setVisible(false);
        channel_6_note_1.setVisible(false);
        channel_7_note_1.setVisible(false);
        channel_8_note_1.setVisible(false);
        channel_9_note_1.setVisible(false);
        channel_10_note_1.setVisible(false);

        channel_1_note_2.setVisible(false);
        channel_2_note_2.setVisible(false);
        channel_3_note_2.setVisible(false);
        channel_4_note_2.setVisible(false);
        channel_5_note_2.setVisible(false);
        channel_6_note_2.setVisible(false);
        channel_7_note_2.setVisible(false);
        channel_8_note_2.setVisible(false);
        channel_9_note_2.setVisible(false);
        channel_10_note_2.setVisible(false);

        channel_1_note_3.setVisible(false);
        channel_2_note_3.setVisible(false);
        channel_3_note_3.setVisible(false);
        channel_4_note_3.setVisible(false);
        channel_5_note_3.setVisible(false);
        channel_6_note_3.setVisible(false);
        channel_7_note_3.setVisible(false);
        channel_8_note_3.setVisible(false);
        channel_9_note_3.setVisible(false);
        channel_10_note_3.setVisible(false);

        channel_1_note_4.setVisible(false);
        channel_2_note_4.setVisible(false);
        channel_3_note_4.setVisible(false);
        channel_4_note_4.setVisible(false);
        channel_5_note_4.setVisible(false);
        channel_6_note_4.setVisible(false);
        channel_7_note_4.setVisible(false);
        channel_8_note_4.setVisible(false);
        channel_9_note_4.setVisible(false);
        channel_10_note_4.setVisible(false);

        LOGGER.info("Leave");
    }

    @Override
    public HarpViewNoteElement getHarpViewElement(int channel, int note) {
        LOGGER.info("Enter with parameters " + channel + " " + note);
        HarpViewNoteElementDesktop harpViewNoteElementDesktop = new HarpViewNoteElementDesktop(getNotePanel(channel, note));
        LOGGER.info("Return " + harpViewNoteElementDesktop);
        return new HarpViewNoteElementDesktop(getNotePanel(channel, note));
    }

    @Override
    public void initNotes(Note[] notes) {
        LOGGER.info("Enter with parameter " + Arrays.toString(notes));
        hideNotes();
        for (Note note : notes) {

            initNote(note.getChannel(), note.getNote(), note.getNoteName());
            if (note.isOverblow()) {
                setNoteColor(note.getChannel(), note.getNote(), OVERBLOW_COLOR);
            }
            if (note.isOverdraw()) {
                setNoteColor(note.getChannel(), note.getNote(), OVERDRAW_COLOR);
            }

        }
        LOGGER.info("Leave");
    }

    /**
     * Sets note color.
     *
     * @param channel   the channel
     * @param note      the note
     * @param noteColor the note color
     */
    private void setNoteColor(int channel, int note, Color noteColor) {
        LOGGER.info("Enter with parameters " + channel + " " + note + " " + noteColor.toString());
        JPanel panel = getNotePanel(channel, note);
        panel.setBackground(noteColor);
        LOGGER.info("Leave");
    }

    /**
     * Init note.
     *
     * @param channel the channel
     * @param note    the note
     * @param key     the key
     */
    private void initNote(int channel, int note, String key) {
        LOGGER.info("Enter with parameters " + channel + " " + note + " " + key);
        JPanel panel = getNotePanel(channel, note);
        panel.setVisible(true);

        Component[] components = panel.getComponents();


        for (Component component : components) {
            if (component instanceof JLabel) {
                ((JLabel) component).setText(key);
                panel.setComponentZOrder(component, 0);
            }
        }

        LOGGER.info("Leave");
    }

    /**
     * Gets note panel.
     *
     * @param channel the channel
     * @param note    the note
     * @return the note panel
     */
    private JPanel getNotePanel(int channel, int note) {
        LOGGER.info("Enter with parameters " + channel + " " + note);
        JPanel jPanel = null;
        if (channel == 1) {
            switch (note) {
                case -3 -> jPanel = channel_1_note_m3;
                case -2 -> jPanel = channel_1_note_m2;
                case -1 -> jPanel = channel_1_note_m1;
                case 0 -> jPanel = channel_1_note_0;
                case 1 -> jPanel = channel_1_note_1;
                case 2 -> jPanel = channel_1_note_2;
                case 3 -> jPanel = channel_1_note_3;
                case 4 -> jPanel = channel_1_note_4;
            }
        }
        if (channel == 2) {
            jPanel = switch (note) {
                case -3 -> channel_2_note_m3;
                case -2 -> channel_2_note_m2;
                case -1 -> channel_2_note_m1;
                case 0 -> channel_2_note_0;
                case 1 -> channel_2_note_1;
                case 2 -> channel_2_note_2;
                case 3 -> channel_2_note_3;
                case 4 -> channel_2_note_4;
                default -> null;
            };
        }
        if (channel == 3) {
            jPanel = switch (note) {
                case -3 -> channel_3_note_m3;
                case -2 -> channel_3_note_m2;
                case -1 -> channel_3_note_m1;
                case 0 -> channel_3_note_0;
                case 1 -> channel_3_note_1;
                case 2 -> channel_3_note_2;
                case 3 -> channel_3_note_3;
                case 4 -> channel_3_note_4;
                default -> null;
            };
        }
        if (channel == 4) {
            jPanel = switch (note) {
                case -3 -> channel_4_note_m3;
                case -2 -> channel_4_note_m2;
                case -1 -> channel_4_note_m1;
                case 0 -> channel_4_note_0;
                case 1 -> channel_4_note_1;
                case 2 -> channel_4_note_2;
                case 3 -> channel_4_note_3;
                case 4 -> channel_4_note_4;
                default -> null;
            };
        }
        if (channel == 5) {
            jPanel = switch (note) {
                case -3 -> channel_5_note_m3;
                case -2 -> channel_5_note_m2;
                case -1 -> channel_5_note_m1;
                case 0 -> channel_5_note_0;
                case 1 -> channel_5_note_1;
                case 2 -> channel_5_note_2;
                case 3 -> channel_5_note_3;
                case 4 -> channel_5_note_4;
                default -> null;
            };
        }
        if (channel == 6) {
            jPanel = switch (note) {
                case -3 -> channel_6_note_m3;
                case -2 -> channel_6_note_m2;
                case -1 -> channel_6_note_m1;
                case 0 -> channel_6_note_0;
                case 1 -> channel_6_note_1;
                case 2 -> channel_6_note_2;
                case 3 -> channel_6_note_3;
                case 4 -> channel_6_note_4;
                default -> null;
            };
        }
        if (channel == 7) {
            jPanel = switch (note) {
                case -3 -> channel_7_note_m3;
                case -2 -> channel_7_note_m2;
                case -1 -> channel_7_note_m1;
                case 0 -> channel_7_note_0;
                case 1 -> channel_7_note_1;
                case 2 -> channel_7_note_2;
                case 3 -> channel_7_note_3;
                case 4 -> channel_7_note_4;
                default -> null;
            };
        }
        if (channel == 8) {
            jPanel = switch (note) {
                case -3 -> channel_8_note_m3;
                case -2 -> channel_8_note_m2;
                case -1 -> channel_8_note_m1;
                case 0 -> channel_8_note_0;
                case 1 -> channel_8_note_1;
                case 2 -> channel_8_note_2;
                case 3 -> channel_8_note_3;
                case 4 -> channel_8_note_4;
                default -> null;
            };
        }
        if (channel == 9) {
            jPanel = switch (note) {
                case -3 -> channel_9_note_m3;
                case -2 -> channel_9_note_m2;
                case -1 -> channel_9_note_m1;
                case 0 -> channel_9_note_0;
                case 1 -> channel_9_note_1;
                case 2 -> channel_9_note_2;
                case 3 -> channel_9_note_3;
                case 4 -> channel_9_note_4;
                default -> null;
            };
        }
        if (channel == 10) {
            jPanel = switch (note) {
                case -3 -> channel_10_note_m3;
                case -2 -> channel_10_note_m2;
                case -1 -> channel_10_note_m1;
                case 0 -> channel_10_note_0;
                case 1 -> channel_10_note_1;
                case 2 -> channel_10_note_2;
                case 3 -> channel_10_note_3;
                case 4 -> channel_10_note_4;
                default -> null;
            };
        }
        LOGGER.info("Return " + jPanel);
        return jPanel;
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
        contentPane.setLayout(new GridLayoutManager(9, 10, new Insets(0, 0, 0, 0), -1, -1, true, true));
        contentPane.setMaximumSize(new Dimension(-1, -1));
        contentPane.setMinimumSize(new Dimension(-1, -1));
        contentPane.setPreferredSize(new Dimension(500, 500));
        channel_1_note_m3 = new JPanel();
        channel_1_note_m3.setLayout(new GridBagLayout());
        channel_1_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_1_note_m3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-16777216));
        label1.setText("Label");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_1_note_m3.add(label1, gbc);
        channel_10_note_m3 = new JPanel();
        channel_10_note_m3.setLayout(new GridBagLayout());
        channel_10_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_10_note_m3, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label2 = new JLabel();
        label2.setForeground(new Color(-16777216));
        label2.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_m3.add(label2, gbc);
        channel_9_note_m3 = new JPanel();
        channel_9_note_m3.setLayout(new GridBagLayout());
        channel_9_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_9_note_m3, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label3 = new JLabel();
        label3.setForeground(new Color(-16777216));
        label3.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_m3.add(label3, gbc);
        channel_8_note_m3 = new JPanel();
        channel_8_note_m3.setLayout(new GridBagLayout());
        channel_8_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_8_note_m3, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label4 = new JLabel();
        label4.setForeground(new Color(-16777216));
        label4.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_m3.add(label4, gbc);
        channel_7_note_m3 = new JPanel();
        channel_7_note_m3.setLayout(new GridBagLayout());
        channel_7_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_7_note_m3, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label5 = new JLabel();
        label5.setForeground(new Color(-16777216));
        label5.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_m3.add(label5, gbc);
        channel_6_note_m3 = new JPanel();
        channel_6_note_m3.setLayout(new GridBagLayout());
        channel_6_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_6_note_m3, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label6 = new JLabel();
        label6.setForeground(new Color(-16777216));
        label6.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_m3.add(label6, gbc);
        channel_5_note_m3 = new JPanel();
        channel_5_note_m3.setLayout(new GridBagLayout());
        channel_5_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_5_note_m3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label7 = new JLabel();
        label7.setForeground(new Color(-16777216));
        label7.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_m3.add(label7, gbc);
        channel_4_note_m3 = new JPanel();
        channel_4_note_m3.setLayout(new GridBagLayout());
        channel_4_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_4_note_m3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label8 = new JLabel();
        label8.setForeground(new Color(-16777216));
        label8.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_m3.add(label8, gbc);
        channel_3_note_m3 = new JPanel();
        channel_3_note_m3.setLayout(new GridBagLayout());
        channel_3_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_3_note_m3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label9 = new JLabel();
        label9.setForeground(new Color(-16777216));
        label9.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_m3.add(label9, gbc);
        channel_2_note_m3 = new JPanel();
        channel_2_note_m3.setLayout(new GridBagLayout());
        channel_2_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_2_note_m3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label10 = new JLabel();
        label10.setForeground(new Color(-16777216));
        label10.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_2_note_m3.add(label10, gbc);
        channel_1_note_m2 = new JPanel();
        channel_1_note_m2.setLayout(new GridBagLayout());
        channel_1_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_1_note_m2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label11 = new JLabel();
        label11.setForeground(new Color(-16777216));
        label11.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_1_note_m2.add(label11, gbc);
        channel_2_note_m2 = new JPanel();
        channel_2_note_m2.setLayout(new GridBagLayout());
        channel_2_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_2_note_m2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label12 = new JLabel();
        label12.setForeground(new Color(-16777216));
        label12.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_2_note_m2.add(label12, gbc);
        channel_3_note_m2 = new JPanel();
        channel_3_note_m2.setLayout(new GridBagLayout());
        channel_3_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_3_note_m2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label13 = new JLabel();
        label13.setForeground(new Color(-16777216));
        label13.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_m2.add(label13, gbc);
        channel_4_note_m2 = new JPanel();
        channel_4_note_m2.setLayout(new GridBagLayout());
        channel_4_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_4_note_m2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label14 = new JLabel();
        label14.setForeground(new Color(-16777216));
        label14.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_m2.add(label14, gbc);
        channel_5_note_m2 = new JPanel();
        channel_5_note_m2.setLayout(new GridBagLayout());
        channel_5_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_5_note_m2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label15 = new JLabel();
        label15.setForeground(new Color(-16777216));
        label15.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_m2.add(label15, gbc);
        channel_6_note_m2 = new JPanel();
        channel_6_note_m2.setLayout(new GridBagLayout());
        channel_6_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_6_note_m2, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label16 = new JLabel();
        label16.setForeground(new Color(-16777216));
        label16.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_m2.add(label16, gbc);
        channel_7_note_m2 = new JPanel();
        channel_7_note_m2.setLayout(new GridBagLayout());
        channel_7_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_7_note_m2, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label17 = new JLabel();
        label17.setForeground(new Color(-16777216));
        label17.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_m2.add(label17, gbc);
        channel_8_note_m2 = new JPanel();
        channel_8_note_m2.setLayout(new GridBagLayout());
        channel_8_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_8_note_m2, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label18 = new JLabel();
        label18.setForeground(new Color(-16777216));
        label18.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_m2.add(label18, gbc);
        channel_9_note_m2 = new JPanel();
        channel_9_note_m2.setLayout(new GridBagLayout());
        channel_9_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_9_note_m2, new GridConstraints(1, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label19 = new JLabel();
        label19.setForeground(new Color(-16777216));
        label19.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_m2.add(label19, gbc);
        channel_10_note_m2 = new JPanel();
        channel_10_note_m2.setLayout(new GridBagLayout());
        channel_10_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_10_note_m2, new GridConstraints(1, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label20 = new JLabel();
        label20.setForeground(new Color(-16777216));
        label20.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_m2.add(label20, gbc);
        channel_1_note_m1 = new JPanel();
        channel_1_note_m1.setLayout(new GridBagLayout());
        channel_1_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_1_note_m1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label21 = new JLabel();
        label21.setForeground(new Color(-16777216));
        label21.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_1_note_m1.add(label21, gbc);
        channel_2_note_m1 = new JPanel();
        channel_2_note_m1.setLayout(new GridBagLayout());
        channel_2_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_2_note_m1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label22 = new JLabel();
        label22.setForeground(new Color(-16777216));
        label22.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_2_note_m1.add(label22, gbc);
        channel_3_note_m1 = new JPanel();
        channel_3_note_m1.setLayout(new GridBagLayout());
        channel_3_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_3_note_m1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label23 = new JLabel();
        label23.setForeground(new Color(-16777216));
        label23.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_m1.add(label23, gbc);
        channel_4_note_m1 = new JPanel();
        channel_4_note_m1.setLayout(new GridBagLayout());
        channel_4_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_4_note_m1, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label24 = new JLabel();
        label24.setForeground(new Color(-16777216));
        label24.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_m1.add(label24, gbc);
        channel_5_note_m1 = new JPanel();
        channel_5_note_m1.setLayout(new GridBagLayout());
        channel_5_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_5_note_m1, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label25 = new JLabel();
        label25.setForeground(new Color(-16777216));
        label25.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_m1.add(label25, gbc);
        channel_6_note_m1 = new JPanel();
        channel_6_note_m1.setLayout(new GridBagLayout());
        channel_6_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_6_note_m1, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label26 = new JLabel();
        label26.setForeground(new Color(-16777216));
        label26.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_m1.add(label26, gbc);
        channel_7_note_m1 = new JPanel();
        channel_7_note_m1.setLayout(new GridBagLayout());
        channel_7_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_7_note_m1, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label27 = new JLabel();
        label27.setForeground(new Color(-16777216));
        label27.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_m1.add(label27, gbc);
        channel_8_note_m1 = new JPanel();
        channel_8_note_m1.setLayout(new GridBagLayout());
        channel_8_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_8_note_m1, new GridConstraints(2, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label28 = new JLabel();
        label28.setForeground(new Color(-16777216));
        label28.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_m1.add(label28, gbc);
        channel_9_note_m1 = new JPanel();
        channel_9_note_m1.setLayout(new GridBagLayout());
        channel_9_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_9_note_m1, new GridConstraints(2, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label29 = new JLabel();
        label29.setForeground(new Color(-16777216));
        label29.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_m1.add(label29, gbc);
        channel_10_note_m1 = new JPanel();
        channel_10_note_m1.setLayout(new GridBagLayout());
        channel_10_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_10_note_m1, new GridConstraints(2, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label30 = new JLabel();
        label30.setForeground(new Color(-16777216));
        label30.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_m1.add(label30, gbc);
        channel_1_note_0 = new JPanel();
        channel_1_note_0.setLayout(new GridBagLayout());
        channel_1_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_1_note_0, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label31 = new JLabel();
        label31.setForeground(new Color(-16777216));
        label31.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        channel_1_note_0.add(label31, gbc);
        channel_2_note_0 = new JPanel();
        channel_2_note_0.setLayout(new GridBagLayout());
        channel_2_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_2_note_0, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label32 = new JLabel();
        label32.setForeground(new Color(-16777216));
        label32.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        channel_2_note_0.add(label32, gbc);
        channel_3_note_0 = new JPanel();
        channel_3_note_0.setLayout(new GridBagLayout());
        channel_3_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_3_note_0, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label33 = new JLabel();
        label33.setForeground(new Color(-16777216));
        label33.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_0.add(label33, gbc);
        channel_4_note_0 = new JPanel();
        channel_4_note_0.setLayout(new GridBagLayout());
        channel_4_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_4_note_0, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label34 = new JLabel();
        label34.setForeground(new Color(-16777216));
        label34.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_0.add(label34, gbc);
        channel_5_note_0 = new JPanel();
        channel_5_note_0.setLayout(new GridBagLayout());
        channel_5_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_5_note_0, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label35 = new JLabel();
        label35.setForeground(new Color(-16777216));
        label35.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_0.add(label35, gbc);
        channel_6_note_0 = new JPanel();
        channel_6_note_0.setLayout(new GridBagLayout());
        channel_6_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_6_note_0, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label36 = new JLabel();
        label36.setForeground(new Color(-16777216));
        label36.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_0.add(label36, gbc);
        channel_7_note_0 = new JPanel();
        channel_7_note_0.setLayout(new GridBagLayout());
        channel_7_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_7_note_0, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label37 = new JLabel();
        label37.setForeground(new Color(-16777216));
        label37.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_0.add(label37, gbc);
        channel_8_note_0 = new JPanel();
        channel_8_note_0.setLayout(new GridBagLayout());
        channel_8_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_8_note_0, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label38 = new JLabel();
        label38.setForeground(new Color(-16777216));
        label38.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_0.add(label38, gbc);
        channel_9_note_0 = new JPanel();
        channel_9_note_0.setLayout(new GridBagLayout());
        channel_9_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_9_note_0, new GridConstraints(3, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label39 = new JLabel();
        label39.setForeground(new Color(-16777216));
        label39.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_0.add(label39, gbc);
        channel_10_note_0 = new JPanel();
        channel_10_note_0.setLayout(new GridBagLayout());
        channel_10_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_10_note_0, new GridConstraints(3, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label40 = new JLabel();
        label40.setForeground(new Color(-16777216));
        label40.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_0.add(label40, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label41 = new JLabel();
        label41.setText("1");
        panel1.add(label41, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label42 = new JLabel();
        label42.setText("2");
        panel2.add(label42, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label43 = new JLabel();
        label43.setText("3");
        panel3.add(label43, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label44 = new JLabel();
        label44.setText("4");
        panel4.add(label44, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel5, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label45 = new JLabel();
        label45.setText("5");
        panel5.add(label45, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel6, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label46 = new JLabel();
        label46.setText("6");
        panel6.add(label46, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel7, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label47 = new JLabel();
        label47.setText("7");
        panel7.add(label47, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel8, new GridConstraints(4, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label48 = new JLabel();
        label48.setText("8");
        panel8.add(label48, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel9, new GridConstraints(4, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label49 = new JLabel();
        label49.setText("9");
        panel9.add(label49, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel10, new GridConstraints(4, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label50 = new JLabel();
        label50.setText("10");
        panel10.add(label50, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        channel_1_note_1 = new JPanel();
        channel_1_note_1.setLayout(new GridBagLayout());
        channel_1_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_1_note_1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label51 = new JLabel();
        label51.setForeground(new Color(-16777216));
        label51.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_1_note_1.add(label51, gbc);
        channel_2_note_1 = new JPanel();
        channel_2_note_1.setLayout(new GridBagLayout());
        channel_2_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_2_note_1, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label52 = new JLabel();
        label52.setForeground(new Color(-16777216));
        label52.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_2_note_1.add(label52, gbc);
        channel_3_note_1 = new JPanel();
        channel_3_note_1.setLayout(new GridBagLayout());
        channel_3_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_3_note_1, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label53 = new JLabel();
        label53.setForeground(new Color(-16777216));
        label53.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_1.add(label53, gbc);
        channel_4_note_1 = new JPanel();
        channel_4_note_1.setLayout(new GridBagLayout());
        channel_4_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_4_note_1, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label54 = new JLabel();
        label54.setForeground(new Color(-16777216));
        label54.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_1.add(label54, gbc);
        channel_5_note_1 = new JPanel();
        channel_5_note_1.setLayout(new GridBagLayout());
        channel_5_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_5_note_1, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label55 = new JLabel();
        label55.setForeground(new Color(-16777216));
        label55.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_1.add(label55, gbc);
        channel_6_note_1 = new JPanel();
        channel_6_note_1.setLayout(new GridBagLayout());
        channel_6_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_6_note_1, new GridConstraints(5, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label56 = new JLabel();
        label56.setForeground(new Color(-16777216));
        label56.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_1.add(label56, gbc);
        channel_7_note_1 = new JPanel();
        channel_7_note_1.setLayout(new GridBagLayout());
        channel_7_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_7_note_1, new GridConstraints(5, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label57 = new JLabel();
        label57.setForeground(new Color(-16777216));
        label57.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_1.add(label57, gbc);
        channel_8_note_1 = new JPanel();
        channel_8_note_1.setLayout(new GridBagLayout());
        channel_8_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_8_note_1, new GridConstraints(5, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label58 = new JLabel();
        label58.setForeground(new Color(-16777216));
        label58.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_1.add(label58, gbc);
        channel_9_note_1 = new JPanel();
        channel_9_note_1.setLayout(new GridBagLayout());
        channel_9_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_9_note_1, new GridConstraints(5, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label59 = new JLabel();
        label59.setForeground(new Color(-16777216));
        label59.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_1.add(label59, gbc);
        channel_10_note_1 = new JPanel();
        channel_10_note_1.setLayout(new GridBagLayout());
        channel_10_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_10_note_1, new GridConstraints(5, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label60 = new JLabel();
        label60.setForeground(new Color(-16777216));
        label60.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_1.add(label60, gbc);
        channel_1_note_2 = new JPanel();
        channel_1_note_2.setLayout(new GridBagLayout());
        channel_1_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_1_note_2, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label61 = new JLabel();
        label61.setForeground(new Color(-16777216));
        label61.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_1_note_2.add(label61, gbc);
        channel_2_note_2 = new JPanel();
        channel_2_note_2.setLayout(new GridBagLayout());
        channel_2_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_2_note_2, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label62 = new JLabel();
        label62.setForeground(new Color(-16777216));
        label62.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_2_note_2.add(label62, gbc);
        channel_3_note_2 = new JPanel();
        channel_3_note_2.setLayout(new GridBagLayout());
        channel_3_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_3_note_2, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label63 = new JLabel();
        label63.setForeground(new Color(-16777216));
        label63.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_2.add(label63, gbc);
        channel_4_note_2 = new JPanel();
        channel_4_note_2.setLayout(new GridBagLayout());
        channel_4_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_4_note_2, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label64 = new JLabel();
        label64.setForeground(new Color(-16777216));
        label64.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_2.add(label64, gbc);
        channel_5_note_2 = new JPanel();
        channel_5_note_2.setLayout(new GridBagLayout());
        channel_5_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_5_note_2, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label65 = new JLabel();
        label65.setForeground(new Color(-16777216));
        label65.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_2.add(label65, gbc);
        channel_6_note_2 = new JPanel();
        channel_6_note_2.setLayout(new GridBagLayout());
        channel_6_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_6_note_2, new GridConstraints(6, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label66 = new JLabel();
        label66.setForeground(new Color(-16777216));
        label66.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_2.add(label66, gbc);
        channel_7_note_2 = new JPanel();
        channel_7_note_2.setLayout(new GridBagLayout());
        channel_7_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_7_note_2, new GridConstraints(6, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label67 = new JLabel();
        label67.setForeground(new Color(-16777216));
        label67.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_2.add(label67, gbc);
        channel_8_note_2 = new JPanel();
        channel_8_note_2.setLayout(new GridBagLayout());
        channel_8_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_8_note_2, new GridConstraints(6, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label68 = new JLabel();
        label68.setForeground(new Color(-16777216));
        label68.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_2.add(label68, gbc);
        channel_9_note_2 = new JPanel();
        channel_9_note_2.setLayout(new GridBagLayout());
        channel_9_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_9_note_2, new GridConstraints(6, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label69 = new JLabel();
        label69.setForeground(new Color(-16777216));
        label69.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_2.add(label69, gbc);
        channel_10_note_2 = new JPanel();
        channel_10_note_2.setLayout(new GridBagLayout());
        channel_10_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_10_note_2, new GridConstraints(6, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label70 = new JLabel();
        label70.setForeground(new Color(-16777216));
        label70.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_2.add(label70, gbc);
        channel_1_note_3 = new JPanel();
        channel_1_note_3.setLayout(new GridBagLayout());
        channel_1_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_1_note_3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label71 = new JLabel();
        label71.setForeground(new Color(-16777216));
        label71.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_1_note_3.add(label71, gbc);
        channel_2_note_3 = new JPanel();
        channel_2_note_3.setLayout(new GridBagLayout());
        channel_2_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_2_note_3, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label72 = new JLabel();
        label72.setForeground(new Color(-16777216));
        label72.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_2_note_3.add(label72, gbc);
        channel_3_note_3 = new JPanel();
        channel_3_note_3.setLayout(new GridBagLayout());
        channel_3_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_3_note_3, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label73 = new JLabel();
        label73.setForeground(new Color(-16777216));
        label73.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_3.add(label73, gbc);
        channel_4_note_3 = new JPanel();
        channel_4_note_3.setLayout(new GridBagLayout());
        channel_4_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_4_note_3, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label74 = new JLabel();
        label74.setForeground(new Color(-16777216));
        label74.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_3.add(label74, gbc);
        channel_5_note_3 = new JPanel();
        channel_5_note_3.setLayout(new GridBagLayout());
        channel_5_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_5_note_3, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label75 = new JLabel();
        label75.setForeground(new Color(-16777216));
        label75.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_3.add(label75, gbc);
        channel_6_note_3 = new JPanel();
        channel_6_note_3.setLayout(new GridBagLayout());
        channel_6_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_6_note_3, new GridConstraints(7, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label76 = new JLabel();
        label76.setForeground(new Color(-16777216));
        label76.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_3.add(label76, gbc);
        channel_7_note_3 = new JPanel();
        channel_7_note_3.setLayout(new GridBagLayout());
        channel_7_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_7_note_3, new GridConstraints(7, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label77 = new JLabel();
        label77.setForeground(new Color(-16777216));
        label77.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_3.add(label77, gbc);
        channel_8_note_3 = new JPanel();
        channel_8_note_3.setLayout(new GridBagLayout());
        channel_8_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_8_note_3, new GridConstraints(7, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label78 = new JLabel();
        label78.setForeground(new Color(-16777216));
        label78.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_3.add(label78, gbc);
        channel_9_note_3 = new JPanel();
        channel_9_note_3.setLayout(new GridBagLayout());
        channel_9_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_9_note_3, new GridConstraints(7, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label79 = new JLabel();
        label79.setForeground(new Color(-16777216));
        label79.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_3.add(label79, gbc);
        channel_10_note_3 = new JPanel();
        channel_10_note_3.setLayout(new GridBagLayout());
        channel_10_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_10_note_3, new GridConstraints(7, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label80 = new JLabel();
        label80.setForeground(new Color(-16777216));
        label80.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_3.add(label80, gbc);
        channel_1_note_4 = new JPanel();
        channel_1_note_4.setLayout(new GridBagLayout());
        channel_1_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_1_note_4, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label81 = new JLabel();
        label81.setForeground(new Color(-16777216));
        label81.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_1_note_4.add(label81, gbc);
        channel_2_note_4 = new JPanel();
        channel_2_note_4.setLayout(new GridBagLayout());
        channel_2_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_2_note_4, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label82 = new JLabel();
        label82.setForeground(new Color(-16777216));
        label82.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_2_note_4.add(label82, gbc);
        channel_3_note_4 = new JPanel();
        channel_3_note_4.setLayout(new GridBagLayout());
        channel_3_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_3_note_4, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label83 = new JLabel();
        label83.setForeground(new Color(-16777216));
        label83.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_3_note_4.add(label83, gbc);
        channel_4_note_4 = new JPanel();
        channel_4_note_4.setLayout(new GridBagLayout());
        channel_4_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_4_note_4, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label84 = new JLabel();
        label84.setForeground(new Color(-16777216));
        label84.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_4_note_4.add(label84, gbc);
        channel_5_note_4 = new JPanel();
        channel_5_note_4.setLayout(new GridBagLayout());
        channel_5_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_5_note_4, new GridConstraints(8, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label85 = new JLabel();
        label85.setForeground(new Color(-16777216));
        label85.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_5_note_4.add(label85, gbc);
        channel_6_note_4 = new JPanel();
        channel_6_note_4.setLayout(new GridBagLayout());
        channel_6_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_6_note_4, new GridConstraints(8, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label86 = new JLabel();
        label86.setForeground(new Color(-16777216));
        label86.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_6_note_4.add(label86, gbc);
        channel_7_note_4 = new JPanel();
        channel_7_note_4.setLayout(new GridBagLayout());
        channel_7_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_7_note_4, new GridConstraints(8, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label87 = new JLabel();
        label87.setForeground(new Color(-16777216));
        label87.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_7_note_4.add(label87, gbc);
        channel_8_note_4 = new JPanel();
        channel_8_note_4.setLayout(new GridBagLayout());
        channel_8_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_8_note_4, new GridConstraints(8, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label88 = new JLabel();
        label88.setForeground(new Color(-16777216));
        label88.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_8_note_4.add(label88, gbc);
        channel_9_note_4 = new JPanel();
        channel_9_note_4.setLayout(new GridBagLayout());
        channel_9_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_9_note_4, new GridConstraints(8, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label89 = new JLabel();
        label89.setForeground(new Color(-16777216));
        label89.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_9_note_4.add(label89, gbc);
        channel_10_note_4 = new JPanel();
        channel_10_note_4.setLayout(new GridBagLayout());
        channel_10_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_10_note_4, new GridConstraints(8, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label90 = new JLabel();
        label90.setForeground(new Color(-16777216));
        label90.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        channel_10_note_4.add(label90, gbc);
    }

    /**
     * $$$ get root component $$$ j component.
     *
     * @return the j component
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
