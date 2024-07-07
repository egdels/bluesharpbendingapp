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
            JPanel panel = getNotePanel(note.getChannel(), note.getNote());

            // clear panel
            for (Component child : panel.getComponents()) {
                if (child instanceof NotePane oldPane) {
                    panel.remove(oldPane);
                }
            }
            panel.setVisible(true);
            Color color = panel.getBackground();
            if (note.isOverblow()) {
                color = OVERBLOW_COLOR;
                panel.setBackground(color);
            }
            if (note.isOverdraw()) {
                color = OVERDRAW_COLOR;
                panel.setBackground(color);
            }

            NotePane notePane = new NotePane(note.getNoteName(), color);
            panel.add(notePane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
            panel.revalidate();
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
        contentPane.setLayout(new GridLayoutManager(9, 10, new Insets(0, 0, 0, 0), -1, -1, true, true));
        contentPane.setMaximumSize(new Dimension(-1, -1));
        contentPane.setMinimumSize(new Dimension(-1, -1));
        contentPane.setPreferredSize(new Dimension(500, 500));
        channel_1_note_m3 = new JPanel();
        channel_1_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_1_note_m3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_m3 = new JPanel();
        channel_10_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_10_note_m3, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_m3 = new JPanel();
        channel_9_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_9_note_m3, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_m3 = new JPanel();
        channel_8_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_8_note_m3, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_m3 = new JPanel();
        channel_7_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_7_note_m3, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_m3 = new JPanel();
        channel_6_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_6_note_m3, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_m3 = new JPanel();
        channel_5_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_5_note_m3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_m3 = new JPanel();
        channel_4_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_4_note_m3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_m3 = new JPanel();
        channel_3_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_3_note_m3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_m3 = new JPanel();
        channel_2_note_m3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_m3.setBackground(new Color(-26624));
        contentPane.add(channel_2_note_m3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_m3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_1_note_m2 = new JPanel();
        channel_1_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_1_note_m2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_m2 = new JPanel();
        channel_2_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_2_note_m2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_m2 = new JPanel();
        channel_3_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_3_note_m2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_m2 = new JPanel();
        channel_4_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_4_note_m2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_m2 = new JPanel();
        channel_5_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_5_note_m2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_m2 = new JPanel();
        channel_6_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_6_note_m2, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_m2 = new JPanel();
        channel_7_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_7_note_m2, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_m2 = new JPanel();
        channel_8_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_8_note_m2, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_m2 = new JPanel();
        channel_9_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_9_note_m2, new GridConstraints(1, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_m2 = new JPanel();
        channel_10_note_m2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_m2.setBackground(new Color(-26624));
        contentPane.add(channel_10_note_m2, new GridConstraints(1, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_m2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_1_note_m1 = new JPanel();
        channel_1_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_1_note_m1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_m1 = new JPanel();
        channel_2_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_2_note_m1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_m1 = new JPanel();
        channel_3_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_3_note_m1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_m1 = new JPanel();
        channel_4_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_4_note_m1, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_m1 = new JPanel();
        channel_5_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_5_note_m1, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_m1 = new JPanel();
        channel_6_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_6_note_m1, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_m1 = new JPanel();
        channel_7_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_7_note_m1, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_m1 = new JPanel();
        channel_8_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_8_note_m1, new GridConstraints(2, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_m1 = new JPanel();
        channel_9_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_9_note_m1, new GridConstraints(2, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_m1 = new JPanel();
        channel_10_note_m1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_m1.setBackground(new Color(-26624));
        contentPane.add(channel_10_note_m1, new GridConstraints(2, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_m1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_1_note_0 = new JPanel();
        channel_1_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_1_note_0, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_0 = new JPanel();
        channel_2_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_2_note_0, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_0 = new JPanel();
        channel_3_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_3_note_0, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_0 = new JPanel();
        channel_4_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_4_note_0, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_0 = new JPanel();
        channel_5_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_5_note_0, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_0 = new JPanel();
        channel_6_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_6_note_0, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_0 = new JPanel();
        channel_7_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_7_note_0, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_0 = new JPanel();
        channel_8_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_8_note_0, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_0 = new JPanel();
        channel_9_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_9_note_0, new GridConstraints(3, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_0 = new JPanel();
        channel_10_note_0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_0.setBackground(new Color(-6513759));
        contentPane.add(channel_10_note_0, new GridConstraints(3, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("1");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("2");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("3");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("4");
        panel4.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel5, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("5");
        panel5.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel6, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("6");
        panel6.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel7, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("7");
        panel7.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel8, new GridConstraints(4, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("8");
        panel8.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel9, new GridConstraints(4, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("9");
        panel9.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel10, new GridConstraints(4, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("10");
        panel10.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        channel_1_note_1 = new JPanel();
        channel_1_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_1_note_1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_1 = new JPanel();
        channel_2_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_2_note_1, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_1 = new JPanel();
        channel_3_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_3_note_1, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_1 = new JPanel();
        channel_4_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_4_note_1, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_1 = new JPanel();
        channel_5_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_5_note_1, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_1 = new JPanel();
        channel_6_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_6_note_1, new GridConstraints(5, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_1 = new JPanel();
        channel_7_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_7_note_1, new GridConstraints(5, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_1 = new JPanel();
        channel_8_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_8_note_1, new GridConstraints(5, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_1 = new JPanel();
        channel_9_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_9_note_1, new GridConstraints(5, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_1 = new JPanel();
        channel_10_note_1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_1.setBackground(new Color(-6513759));
        contentPane.add(channel_10_note_1, new GridConstraints(5, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_1_note_2 = new JPanel();
        channel_1_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_1_note_2, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_2 = new JPanel();
        channel_2_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_2_note_2, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_2 = new JPanel();
        channel_3_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_3_note_2, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_2 = new JPanel();
        channel_4_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_4_note_2, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_2 = new JPanel();
        channel_5_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_5_note_2, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_2 = new JPanel();
        channel_6_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_6_note_2, new GridConstraints(6, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_2 = new JPanel();
        channel_7_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_7_note_2, new GridConstraints(6, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_2 = new JPanel();
        channel_8_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_8_note_2, new GridConstraints(6, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_2 = new JPanel();
        channel_9_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_9_note_2, new GridConstraints(6, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_2 = new JPanel();
        channel_10_note_2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_2.setBackground(new Color(-9503235));
        contentPane.add(channel_10_note_2, new GridConstraints(6, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_1_note_3 = new JPanel();
        channel_1_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_1_note_3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_3 = new JPanel();
        channel_2_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_2_note_3, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_3 = new JPanel();
        channel_3_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_3_note_3, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_3 = new JPanel();
        channel_4_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_4_note_3, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_3 = new JPanel();
        channel_5_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_5_note_3, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_3 = new JPanel();
        channel_6_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_6_note_3, new GridConstraints(7, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_3 = new JPanel();
        channel_7_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_7_note_3, new GridConstraints(7, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_3 = new JPanel();
        channel_8_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_8_note_3, new GridConstraints(7, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_3 = new JPanel();
        channel_9_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_9_note_3, new GridConstraints(7, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_3 = new JPanel();
        channel_10_note_3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_3.setBackground(new Color(-9503235));
        contentPane.add(channel_10_note_3, new GridConstraints(7, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_1_note_4 = new JPanel();
        channel_1_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_1_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_1_note_4, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_1_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_2_note_4 = new JPanel();
        channel_2_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_2_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_2_note_4, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_2_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_3_note_4 = new JPanel();
        channel_3_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_3_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_3_note_4, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_3_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_4_note_4 = new JPanel();
        channel_4_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_4_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_4_note_4, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_4_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_5_note_4 = new JPanel();
        channel_5_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_5_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_5_note_4, new GridConstraints(8, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_5_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_6_note_4 = new JPanel();
        channel_6_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_6_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_6_note_4, new GridConstraints(8, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_6_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_7_note_4 = new JPanel();
        channel_7_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_7_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_7_note_4, new GridConstraints(8, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_7_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_8_note_4 = new JPanel();
        channel_8_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_8_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_8_note_4, new GridConstraints(8, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_8_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_9_note_4 = new JPanel();
        channel_9_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_9_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_9_note_4, new GridConstraints(8, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_9_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel_10_note_4 = new JPanel();
        channel_10_note_4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel_10_note_4.setBackground(new Color(-9503235));
        contentPane.add(channel_10_note_4, new GridConstraints(8, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel_10_note_4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
