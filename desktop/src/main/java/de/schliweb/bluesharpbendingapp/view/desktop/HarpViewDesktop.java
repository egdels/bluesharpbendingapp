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
import de.schliweb.bluesharpbendingapp.controller.NoteContainer;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The type Harp view desktop.
 */
public class HarpViewDesktop implements HarpView {
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
    private JPanel channel1Note0;
    /**
     * The Channel 2 note 0.
     */
    private JPanel channel2Note0;
    /**
     * The Channel 3 note 0.
     */
    private JPanel channel3Note0;
    /**
     * The Channel 4 note 0.
     */
    private JPanel channel4Note0;
    /**
     * The Channel 5 note 0.
     */
    private JPanel channel5Note0;
    /**
     * The Channel 6 note 0.
     */
    private JPanel channel6Note0;
    /**
     * The Channel 7 note 0.
     */
    private JPanel channel7Note0;
    /**
     * The Channel 8 note 0.
     */
    private JPanel channel8Note0;
    /**
     * The Channel 9 note 0.
     */
    private JPanel channel9Note0;
    /**
     * The Channel 10 note 0.
     */
    private JPanel channel10Note0;
    /**
     * The Channel 1 note m 1.
     */
    private JPanel channel1NoteM1;
    /**
     * The Channel 1 note m 2.
     */
    private JPanel channel1NoteM2;
    /**
     * The Channel 1 note m 3.
     */
    private JPanel channel1NoteM3;
    /**
     * The Channel 1 note 1.
     */
    private JPanel channel1Note1;
    /**
     * The Channel 1 note 2.
     */
    private JPanel channel1Note2;
    /**
     * The Channel 1 note 3.
     */
    private JPanel channel1Note3;
    /**
     * The Channel 1 note 4.
     */
    private JPanel channel1Note4;
    /**
     * The Channel 2 note 1.
     */
    private JPanel channel2Note1;
    /**
     * The Channel 2 note m 1.
     */
    private JPanel channel2NoteM1;
    /**
     * The Channel 2 note m 2.
     */
    private JPanel channel2NoteM2;
    /**
     * The Channel 2 note m 3.
     */
    private JPanel channel2NoteM3;
    /**
     * The Channel 2 note 2.
     */
    private JPanel channel2Note2;
    /**
     * The Channel 2 note 3.
     */
    private JPanel channel2Note3;
    /**
     * The Channel 2 note 4.
     */
    private JPanel channel2Note4;
    /**
     * The Channel 3 note 1.
     */
    private JPanel channel3Note1;
    /**
     * The Channel 3 note 2.
     */
    private JPanel channel3Note2;
    /**
     * The Channel 3 note 3.
     */
    private JPanel channel3Note3;
    /**
     * The Channel 3 note 4.
     */
    private JPanel channel3Note4;
    /**
     * The Channel 3 note m 1.
     */
    private JPanel channel3NoteM1;
    /**
     * The Channel 3 note m 2.
     */
    private JPanel channel3NoteM2;
    /**
     * The Channel 3 note m 3.
     */
    private JPanel channel3NoteM3;
    /**
     * The Channel 4 note m 1.
     */
    private JPanel channel4NoteM1;
    /**
     * The Channel 4 note m 2.
     */
    private JPanel channel4NoteM2;
    /**
     * The Channel 4 note m 3.
     */
    private JPanel channel4NoteM3;
    /**
     * The Channel 4 note 1.
     */
    private JPanel channel4Note1;
    /**
     * The Channel 4 note 2.
     */
    private JPanel channel4Note2;
    /**
     * The Channel 4 note 3.
     */
    private JPanel channel4Note3;
    /**
     * The Channel 4 note 4.
     */
    private JPanel channel4Note4;
    /**
     * The Channel 5 note 1.
     */
    private JPanel channel5Note1;
    /**
     * The Channel 5 note 2.
     */
    private JPanel channel5Note2;
    /**
     * The Channel 5 note 3.
     */
    private JPanel channel5Note3;
    /**
     * The Channel 5 note 4.
     */
    private JPanel channel5Note4;
    /**
     * The Channel 6 note 1.
     */
    private JPanel channel6Note1;
    /**
     * The Channel 6 note 2.
     */
    private JPanel channel6Note2;
    /**
     * The Channel 6 note 3.
     */
    private JPanel channel6Note3;
    /**
     * The Channel 6 note 4.
     */
    private JPanel channel6Note4;
    /**
     * The Channel 6 note m 1.
     */
    private JPanel channel6NoteM1;
    /**
     * The Channel 6 note m 2.
     */
    private JPanel channel6NoteM2;
    /**
     * The Channel 6 note m 3.
     */
    private JPanel channel6NoteM3;
    /**
     * The Channel 5 note m 3.
     */
    private JPanel channel5NoteM3;
    /**
     * The Channel 7 note 1.
     */
    private JPanel channel7Note1;
    /**
     * The Channel 7 note 2.
     */
    private JPanel channel7Note2;
    /**
     * The Channel 7 note 3.
     */
    private JPanel channel7Note3;
    /**
     * The Channel 7 note 4.
     */
    private JPanel channel7Note4;
    /**
     * The Channel 7 note m 1.
     */
    private JPanel channel7NoteM1;
    /**
     * The Channel 7 note m 2.
     */
    private JPanel channel7NoteM2;
    /**
     * The Channel 7 note m 3.
     */
    private JPanel channel7NoteM3;
    /**
     * The Channel 8 note m 1.
     */
    private JPanel channel8NoteM1;
    /**
     * The Channel 8 note m 2.
     */
    private JPanel channel8NoteM2;
    /**
     * The Channel 8 note m 3.
     */
    private JPanel channel8NoteM3;
    /**
     * The Channel 8 note 1.
     */
    private JPanel channel8Note1;
    /**
     * The Channel 8 note 2.
     */
    private JPanel channel8Note2;
    /**
     * The Channel 8 note 3.
     */
    private JPanel channel8Note3;
    /**
     * The Channel 8 note 4.
     */
    private JPanel channel8Note4;
    /**
     * The Channel 9 note 1.
     */
    private JPanel channel9Note1;
    /**
     * The Channel 9 note 2.
     */
    private JPanel channel9Note2;
    /**
     * The Channel 9 note 3.
     */
    private JPanel channel9Note3;
    /**
     * The Channel 9 note 4.
     */
    private JPanel channel9Note4;
    /**
     * The Channel 10 note 1.
     */
    private JPanel channel10Note1;
    /**
     * The Channel 10 note 2.
     */
    private JPanel channel10Note2;
    /**
     * The Channel 10 note 3.
     */
    private JPanel channel10Note3;
    /**
     * The Channel 10 note 4.
     */
    private JPanel channel10Note4;
    /**
     * The Channel 10 note m 1.
     */
    private JPanel channel10NoteM1;
    /**
     * The Channel 10 note m 2.
     */
    private JPanel channel10NoteM2;
    /**
     * The Channel 10 note m 3.
     */
    private JPanel channel10NoteM3;
    /**
     * The Channel 9 note m 3.
     */
    private JPanel channel9NoteM3;
    /**
     * The Channel 5 note m 2.
     */
    private JPanel channel5NoteM2;
    /**
     * The Channel 9 note m 2.
     */
    private JPanel channel9NoteM2;
    /**
     * The Channel 5 note m 1.
     */
    private JPanel channel5NoteM1;
    /**
     * The Channel 9 note m 1.
     */
    private JPanel channel9NoteM1;

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

    }

    /**
     * Hide notes.
     */
    private void hideNotes() {


        if (channel1NoteM3 != null) {
            channel1NoteM3.setVisible(false);
        }
        if (channel2NoteM3 != null) {
            channel2NoteM3.setVisible(false);
        }
        if (channel3NoteM3 != null) {
            channel3NoteM3.setVisible(false);
        }
        if (channel4NoteM3 != null) {
            channel4NoteM3.setVisible(false);
        }
        if (channel5NoteM3 != null) {
            channel5NoteM3.setVisible(false);
        }
        if (channel6NoteM3 != null) {
            channel6NoteM3.setVisible(false);
        }
        if (channel7NoteM3 != null) {
            channel7NoteM3.setVisible(false);
        }
        if (channel8NoteM3 != null) {
            channel8NoteM3.setVisible(false);
        }
        if (channel9NoteM3 != null) {
            channel9NoteM3.setVisible(false);
        }
        if (channel10NoteM3 != null) {
            channel10NoteM3.setVisible(false);
        }

        if (channel1NoteM2 != null) {
            channel1NoteM2.setVisible(false);
        }
        if (channel2NoteM2 != null) {
            channel2NoteM2.setVisible(false);
        }
        if (channel3NoteM2 != null) {
            channel3NoteM2.setVisible(false);
        }
        if (channel4NoteM2 != null) {
            channel4NoteM2.setVisible(false);
        }
        if (channel5NoteM2 != null) {
            channel5NoteM2.setVisible(false);
        }
        if (channel6NoteM2 != null) {
            channel6NoteM2.setVisible(false);
        }
        if (channel7NoteM2 != null) {
            channel7NoteM2.setVisible(false);
        }
        if (channel8NoteM2 != null) {
            channel8NoteM2.setVisible(false);
        }
        if (channel9NoteM2 != null) {
            channel9NoteM2.setVisible(false);
        }
        if (channel10NoteM2 != null) {
            channel10NoteM2.setVisible(false);
        }


        if (channel1NoteM1 != null) {
            channel1NoteM1.setVisible(false);
        }
        if (channel2NoteM1 != null) {
            channel2NoteM1.setVisible(false);
        }
        if (channel3NoteM1 != null) {
            channel3NoteM1.setVisible(false);
        }
        if (channel4NoteM1 != null) {
            channel4NoteM1.setVisible(false);
        }
        if (channel5NoteM1 != null) {
            channel5NoteM1.setVisible(false);
        }
        if (channel6NoteM1 != null) {
            channel6NoteM1.setVisible(false);
        }
        if (channel7NoteM1 != null) {
            channel7NoteM1.setVisible(false);
        }
        if (channel8NoteM1 != null) {
            channel8NoteM1.setVisible(false);
        }
        if (channel9NoteM1 != null) {
            channel9NoteM1.setVisible(false);
        }
        if (channel10NoteM1 != null) {
            channel10NoteM1.setVisible(false);
        }

        if (channel1Note0 != null) {
            channel1Note0.setVisible(false);
        }
        if (channel2Note0 != null) {
            channel2Note0.setVisible(false);
        }
        if (channel3Note0 != null) {
            channel3Note0.setVisible(false);
        }
        if (channel4Note0 != null) {
            channel4Note0.setVisible(false);
        }
        if (channel5Note0 != null) {
            channel5Note0.setVisible(false);
        }
        if (channel6Note0 != null) {
            channel6Note0.setVisible(false);
        }
        if (channel7Note0 != null) {
            channel7Note0.setVisible(false);
        }
        if (channel8Note0 != null) {
            channel8Note0.setVisible(false);
        }
        if (channel9Note0 != null) {
            channel9Note0.setVisible(false);
        }
        if (channel10Note0 != null) {
            channel10Note0.setVisible(false);
        }

        if (channel1Note1 != null) {
            channel1Note1.setVisible(false);
        }
        if (channel2Note1 != null) {
            channel2Note1.setVisible(false);
        }
        if (channel3Note1 != null) {
            channel3Note1.setVisible(false);
        }
        if (channel4Note1 != null) {
            channel4Note1.setVisible(false);
        }
        if (channel5Note1 != null) {
            channel5Note1.setVisible(false);
        }
        if (channel6Note1 != null) {
            channel6Note1.setVisible(false);
        }
        if (channel7Note1 != null) {
            channel7Note1.setVisible(false);
        }
        if (channel8Note1 != null) {
            channel8Note1.setVisible(false);
        }
        if (channel9Note1 != null) {
            channel9Note1.setVisible(false);
        }
        if (channel10Note1 != null) {
            channel10Note1.setVisible(false);
        }

        if (channel1Note2 != null) {
            channel1Note2.setVisible(false);
        }
        if (channel2Note2 != null) {
            channel2Note2.setVisible(false);
        }
        if (channel3Note2 != null) {
            channel3Note2.setVisible(false);
        }
        if (channel4Note2 != null) {
            channel4Note2.setVisible(false);
        }
        if (channel5Note2 != null) {
            channel5Note2.setVisible(false);
        }
        if (channel6Note2 != null) {
            channel6Note2.setVisible(false);
        }
        if (channel7Note2 != null) {
            channel7Note2.setVisible(false);
        }
        if (channel8Note2 != null) {
            channel8Note2.setVisible(false);
        }
        if (channel9Note2 != null) {
            channel9Note2.setVisible(false);
        }
        if (channel10Note2 != null) {
            channel10Note2.setVisible(false);
        }

        if (channel1Note3 != null) {
            channel1Note3.setVisible(false);
        }
        if (channel2Note3 != null) {
            channel2Note3.setVisible(false);
        }
        if (channel3Note3 != null) {
            channel3Note3.setVisible(false);
        }
        if (channel4Note3 != null) {
            channel4Note3.setVisible(false);
        }
        if (channel5Note3 != null) {
            channel5Note3.setVisible(false);
        }
        if (channel6Note3 != null) {
            channel6Note3.setVisible(false);
        }
        if (channel7Note3 != null) {
            channel7Note3.setVisible(false);
        }
        if (channel8Note3 != null) {
            channel8Note3.setVisible(false);
        }
        if (channel9Note3 != null) {
            channel9Note3.setVisible(false);
        }
        if (channel10Note3 != null) {
            channel10Note3.setVisible(false);
        }

        if (channel1Note4 != null) {
            channel1Note4.setVisible(false);
        }
        if (channel2Note4 != null) {
            channel2Note4.setVisible(false);
        }
        if (channel3Note4 != null) {
            channel3Note4.setVisible(false);
        }
        if (channel4Note4 != null) {
            channel4Note4.setVisible(false);
        }
        if (channel5Note4 != null) {
            channel5Note4.setVisible(false);
        }
        if (channel6Note4 != null) {
            channel6Note4.setVisible(false);
        }
        if (channel7Note4 != null) {
            channel7Note4.setVisible(false);
        }
        if (channel8Note4 != null) {
            channel8Note4.setVisible(false);
        }
        if (channel9Note4 != null) {
            channel9Note4.setVisible(false);
        }
        if (channel10Note4 != null) {
            channel10Note4.setVisible(false);
        }


    }

    @Override
    public HarpViewNoteElement getHarpViewElement(int channel, int note) {
        return HarpViewNoteElementDesktop.getInstance(getNotePanel(channel, note));
    }

    @Override
    public void initNotes(NoteContainer[] noteContainers) {

        hideNotes();
        for (NoteContainer noteContainer : noteContainers) {

            JPanel panel = getNotePanel(noteContainer.getChannel(), noteContainer.getNote());
            panel.setVisible(true);
            HarpViewNoteElementDesktop harpViewNoteElementDesktop = HarpViewNoteElementDesktop.getInstance(panel);

            Color color = panel.getBackground();
            if (noteContainer.isOverblow()) {
                color = OVERBLOW_COLOR;
                panel.setBackground(color);
            }
            if (noteContainer.isOverdraw()) {
                color = OVERDRAW_COLOR;
                panel.setBackground(color);
            }
            harpViewNoteElementDesktop.setColor(color);
            harpViewNoteElementDesktop.setNoteName(noteContainer.getNoteName());
            harpViewNoteElementDesktop.clear();
        }
    }

    /**
     * Gets note panel.
     *
     * @param channel the channel
     * @param note    the note
     * @return the note panel
     */
    private JPanel getNotePanel(int channel, int note) {
        JPanel[][] jPanels = new JPanel[10][8];

        jPanels[0][0] = channel1NoteM3;
        jPanels[0][1] = channel1NoteM2;
        jPanels[0][2] = channel1NoteM1;
        jPanels[0][3] = channel1Note0;
        jPanels[0][4] = channel1Note1;
        jPanels[0][5] = channel1Note2;
        jPanels[0][6] = channel1Note3;
        jPanels[0][7] = channel1Note4;

        jPanels[1][0] = channel2NoteM3;
        jPanels[1][1] = channel2NoteM2;
        jPanels[1][2] = channel2NoteM1;
        jPanels[1][3] = channel2Note0;
        jPanels[1][4] = channel2Note1;
        jPanels[1][5] = channel2Note2;
        jPanels[1][6] = channel2Note3;
        jPanels[1][7] = channel2Note4;

        jPanels[2][0] = channel3NoteM3;
        jPanels[2][1] = channel3NoteM2;
        jPanels[2][2] = channel3NoteM1;
        jPanels[2][3] = channel3Note0;
        jPanels[2][4] = channel3Note1;
        jPanels[2][5] = channel3Note2;
        jPanels[2][6] = channel3Note3;
        jPanels[2][7] = channel3Note4;

        jPanels[3][0] = channel4NoteM3;
        jPanels[3][1] = channel4NoteM2;
        jPanels[3][2] = channel4NoteM1;
        jPanels[3][3] = channel4Note0;
        jPanels[3][4] = channel4Note1;
        jPanels[3][5] = channel4Note2;
        jPanels[3][6] = channel4Note3;
        jPanels[3][7] = channel4Note4;

        jPanels[4][0] = channel5NoteM3;
        jPanels[4][1] = channel5NoteM2;
        jPanels[4][2] = channel5NoteM1;
        jPanels[4][3] = channel5Note0;
        jPanels[4][4] = channel5Note1;
        jPanels[4][5] = channel5Note2;
        jPanels[4][6] = channel5Note3;
        jPanels[4][7] = channel5Note4;

        jPanels[5][0] = channel6NoteM3;
        jPanels[5][1] = channel6NoteM2;
        jPanels[5][2] = channel6NoteM1;
        jPanels[5][3] = channel6Note0;
        jPanels[5][4] = channel6Note1;
        jPanels[5][5] = channel6Note2;
        jPanels[5][6] = channel6Note3;
        jPanels[5][7] = channel6Note4;

        jPanels[6][0] = channel7NoteM3;
        jPanels[6][1] = channel7NoteM2;
        jPanels[6][2] = channel7NoteM1;
        jPanels[6][3] = channel7Note0;
        jPanels[6][4] = channel7Note1;
        jPanels[6][5] = channel7Note2;
        jPanels[6][6] = channel7Note3;
        jPanels[6][7] = channel7Note4;

        jPanels[7][0] = channel8NoteM3;
        jPanels[7][1] = channel8NoteM2;
        jPanels[7][2] = channel8NoteM1;
        jPanels[7][3] = channel8Note0;
        jPanels[7][4] = channel8Note1;
        jPanels[7][5] = channel8Note2;
        jPanels[7][6] = channel8Note3;
        jPanels[7][7] = channel8Note4;

        jPanels[8][0] = channel9NoteM3;
        jPanels[8][1] = channel9NoteM2;
        jPanels[8][2] = channel9NoteM1;
        jPanels[8][3] = channel9Note0;
        jPanels[8][4] = channel9Note1;
        jPanels[8][5] = channel9Note2;
        jPanels[8][6] = channel9Note3;
        jPanels[8][7] = channel9Note4;

        jPanels[9][0] = channel10NoteM3;
        jPanels[9][1] = channel10NoteM2;
        jPanels[9][2] = channel10NoteM1;
        jPanels[9][3] = channel10Note0;
        jPanels[9][4] = channel10Note1;
        jPanels[9][5] = channel10Note2;
        jPanels[9][6] = channel10Note3;
        jPanels[9][7] = channel10Note4;

        return jPanels[channel - 1][note + 3];
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
        channel1NoteM3 = new JPanel();
        channel1NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel1NoteM3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10NoteM3 = new JPanel();
        channel10NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel10NoteM3, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9NoteM3 = new JPanel();
        channel9NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel9NoteM3, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8NoteM3 = new JPanel();
        channel8NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel8NoteM3, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7NoteM3 = new JPanel();
        channel7NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel7NoteM3, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6NoteM3 = new JPanel();
        channel6NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel6NoteM3, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5NoteM3 = new JPanel();
        channel5NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel5NoteM3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4NoteM3 = new JPanel();
        channel4NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel4NoteM3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3NoteM3 = new JPanel();
        channel3NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel3NoteM3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2NoteM3 = new JPanel();
        channel2NoteM3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2NoteM3.setBackground(new Color(-26624));
        contentPane.add(channel2NoteM3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2NoteM3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel1NoteM2 = new JPanel();
        channel1NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel1NoteM2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2NoteM2 = new JPanel();
        channel2NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel2NoteM2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3NoteM2 = new JPanel();
        channel3NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel3NoteM2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4NoteM2 = new JPanel();
        channel4NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel4NoteM2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5NoteM2 = new JPanel();
        channel5NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel5NoteM2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6NoteM2 = new JPanel();
        channel6NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel6NoteM2, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7NoteM2 = new JPanel();
        channel7NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel7NoteM2, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8NoteM2 = new JPanel();
        channel8NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel8NoteM2, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9NoteM2 = new JPanel();
        channel9NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel9NoteM2, new GridConstraints(1, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10NoteM2 = new JPanel();
        channel10NoteM2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10NoteM2.setBackground(new Color(-26624));
        contentPane.add(channel10NoteM2, new GridConstraints(1, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10NoteM2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel1NoteM1 = new JPanel();
        channel1NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel1NoteM1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2NoteM1 = new JPanel();
        channel2NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel2NoteM1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3NoteM1 = new JPanel();
        channel3NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel3NoteM1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4NoteM1 = new JPanel();
        channel4NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel4NoteM1, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5NoteM1 = new JPanel();
        channel5NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel5NoteM1, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6NoteM1 = new JPanel();
        channel6NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel6NoteM1, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7NoteM1 = new JPanel();
        channel7NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel7NoteM1, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8NoteM1 = new JPanel();
        channel8NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel8NoteM1, new GridConstraints(2, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9NoteM1 = new JPanel();
        channel9NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel9NoteM1, new GridConstraints(2, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10NoteM1 = new JPanel();
        channel10NoteM1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10NoteM1.setBackground(new Color(-26624));
        contentPane.add(channel10NoteM1, new GridConstraints(2, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10NoteM1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel1Note0 = new JPanel();
        channel1Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1Note0.setBackground(new Color(-6513759));
        contentPane.add(channel1Note0, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2Note0 = new JPanel();
        channel2Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2Note0.setBackground(new Color(-6513759));
        contentPane.add(channel2Note0, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3Note0 = new JPanel();
        channel3Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3Note0.setBackground(new Color(-6513759));
        contentPane.add(channel3Note0, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4Note0 = new JPanel();
        channel4Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4Note0.setBackground(new Color(-6513759));
        contentPane.add(channel4Note0, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5Note0 = new JPanel();
        channel5Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5Note0.setBackground(new Color(-6513759));
        contentPane.add(channel5Note0, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6Note0 = new JPanel();
        channel6Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6Note0.setBackground(new Color(-6513759));
        contentPane.add(channel6Note0, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7Note0 = new JPanel();
        channel7Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7Note0.setBackground(new Color(-6513759));
        contentPane.add(channel7Note0, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8Note0 = new JPanel();
        channel8Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8Note0.setBackground(new Color(-6513759));
        contentPane.add(channel8Note0, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9Note0 = new JPanel();
        channel9Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9Note0.setBackground(new Color(-6513759));
        contentPane.add(channel9Note0, new GridConstraints(3, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10Note0 = new JPanel();
        channel10Note0.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10Note0.setBackground(new Color(-6513759));
        contentPane.add(channel10Note0, new GridConstraints(3, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10Note0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
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
        channel1Note1 = new JPanel();
        channel1Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1Note1.setBackground(new Color(-6513759));
        contentPane.add(channel1Note1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2Note1 = new JPanel();
        channel2Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2Note1.setBackground(new Color(-6513759));
        contentPane.add(channel2Note1, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3Note1 = new JPanel();
        channel3Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3Note1.setBackground(new Color(-6513759));
        contentPane.add(channel3Note1, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4Note1 = new JPanel();
        channel4Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4Note1.setBackground(new Color(-6513759));
        contentPane.add(channel4Note1, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5Note1 = new JPanel();
        channel5Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5Note1.setBackground(new Color(-6513759));
        contentPane.add(channel5Note1, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6Note1 = new JPanel();
        channel6Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6Note1.setBackground(new Color(-6513759));
        contentPane.add(channel6Note1, new GridConstraints(5, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7Note1 = new JPanel();
        channel7Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7Note1.setBackground(new Color(-6513759));
        contentPane.add(channel7Note1, new GridConstraints(5, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8Note1 = new JPanel();
        channel8Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8Note1.setBackground(new Color(-6513759));
        contentPane.add(channel8Note1, new GridConstraints(5, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9Note1 = new JPanel();
        channel9Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9Note1.setBackground(new Color(-6513759));
        contentPane.add(channel9Note1, new GridConstraints(5, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10Note1 = new JPanel();
        channel10Note1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10Note1.setBackground(new Color(-6513759));
        contentPane.add(channel10Note1, new GridConstraints(5, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10Note1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel1Note2 = new JPanel();
        channel1Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1Note2.setBackground(new Color(-9503235));
        contentPane.add(channel1Note2, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2Note2 = new JPanel();
        channel2Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2Note2.setBackground(new Color(-9503235));
        contentPane.add(channel2Note2, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3Note2 = new JPanel();
        channel3Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3Note2.setBackground(new Color(-9503235));
        contentPane.add(channel3Note2, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4Note2 = new JPanel();
        channel4Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4Note2.setBackground(new Color(-9503235));
        contentPane.add(channel4Note2, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5Note2 = new JPanel();
        channel5Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5Note2.setBackground(new Color(-9503235));
        contentPane.add(channel5Note2, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6Note2 = new JPanel();
        channel6Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6Note2.setBackground(new Color(-9503235));
        contentPane.add(channel6Note2, new GridConstraints(6, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7Note2 = new JPanel();
        channel7Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7Note2.setBackground(new Color(-9503235));
        contentPane.add(channel7Note2, new GridConstraints(6, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8Note2 = new JPanel();
        channel8Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8Note2.setBackground(new Color(-9503235));
        contentPane.add(channel8Note2, new GridConstraints(6, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9Note2 = new JPanel();
        channel9Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9Note2.setBackground(new Color(-9503235));
        contentPane.add(channel9Note2, new GridConstraints(6, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10Note2 = new JPanel();
        channel10Note2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10Note2.setBackground(new Color(-9503235));
        contentPane.add(channel10Note2, new GridConstraints(6, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10Note2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel1Note3 = new JPanel();
        channel1Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1Note3.setBackground(new Color(-9503235));
        contentPane.add(channel1Note3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2Note3 = new JPanel();
        channel2Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2Note3.setBackground(new Color(-9503235));
        contentPane.add(channel2Note3, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3Note3 = new JPanel();
        channel3Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3Note3.setBackground(new Color(-9503235));
        contentPane.add(channel3Note3, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4Note3 = new JPanel();
        channel4Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4Note3.setBackground(new Color(-9503235));
        contentPane.add(channel4Note3, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5Note3 = new JPanel();
        channel5Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5Note3.setBackground(new Color(-9503235));
        contentPane.add(channel5Note3, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6Note3 = new JPanel();
        channel6Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6Note3.setBackground(new Color(-9503235));
        contentPane.add(channel6Note3, new GridConstraints(7, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7Note3 = new JPanel();
        channel7Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7Note3.setBackground(new Color(-9503235));
        contentPane.add(channel7Note3, new GridConstraints(7, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8Note3 = new JPanel();
        channel8Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8Note3.setBackground(new Color(-9503235));
        contentPane.add(channel8Note3, new GridConstraints(7, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9Note3 = new JPanel();
        channel9Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9Note3.setBackground(new Color(-9503235));
        contentPane.add(channel9Note3, new GridConstraints(7, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10Note3 = new JPanel();
        channel10Note3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10Note3.setBackground(new Color(-9503235));
        contentPane.add(channel10Note3, new GridConstraints(7, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10Note3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel1Note4 = new JPanel();
        channel1Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel1Note4.setBackground(new Color(-9503235));
        contentPane.add(channel1Note4, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel1Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel2Note4 = new JPanel();
        channel2Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel2Note4.setBackground(new Color(-9503235));
        contentPane.add(channel2Note4, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel2Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel3Note4 = new JPanel();
        channel3Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel3Note4.setBackground(new Color(-9503235));
        contentPane.add(channel3Note4, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel3Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel4Note4 = new JPanel();
        channel4Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel4Note4.setBackground(new Color(-9503235));
        contentPane.add(channel4Note4, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel4Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel5Note4 = new JPanel();
        channel5Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel5Note4.setBackground(new Color(-9503235));
        contentPane.add(channel5Note4, new GridConstraints(8, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel5Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel6Note4 = new JPanel();
        channel6Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel6Note4.setBackground(new Color(-9503235));
        contentPane.add(channel6Note4, new GridConstraints(8, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel6Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel7Note4 = new JPanel();
        channel7Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel7Note4.setBackground(new Color(-9503235));
        contentPane.add(channel7Note4, new GridConstraints(8, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel7Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel8Note4 = new JPanel();
        channel8Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel8Note4.setBackground(new Color(-9503235));
        contentPane.add(channel8Note4, new GridConstraints(8, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel8Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel9Note4 = new JPanel();
        channel9Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel9Note4.setBackground(new Color(-9503235));
        contentPane.add(channel9Note4, new GridConstraints(8, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel9Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        channel10Note4 = new JPanel();
        channel10Note4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        channel10Note4.setBackground(new Color(-9503235));
        contentPane.add(channel10Note4, new GridConstraints(8, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        channel10Note4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
