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
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;

import javax.swing.*;
import java.awt.*;

/**
 * The type Harp view note element desktop.
 */
public class HarpViewNoteElementDesktop implements HarpViewNoteElement {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(HarpViewNoteElementDesktop.class);

    /**
     * The Note panel.
     */
    private final JPanel notePanel;

    /**
     * Instantiates a new Harp view note element desktop.
     *
     * @param notePanel the note panel
     */
    public HarpViewNoteElementDesktop(JPanel notePanel) {
        this.notePanel = notePanel;
    }

    @Override
    public void clear() {
        LOGGER.info("Enter");
        for (Component child : notePanel.getComponents()) {
            if (child instanceof NotePane oldPane) {
                NotePane newPane = new NotePane(oldPane.getNoteName(), oldPane.getColor());
                notePanel.remove(oldPane);
                notePanel.add(newPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
                notePanel.revalidate();
            }
        }
        LOGGER.info("Leave");
    }

    @Override
    public void update(double cents) {
        LOGGER.info("Enter with parameter " + cents);
        for (Component child : notePanel.getComponents()) {
            if (child instanceof NotePane oldPane) {
                NotePane newPane = new NotePane(oldPane.getNoteName(), oldPane.getColor(), cents);
                notePanel.remove(oldPane);
                notePanel.add(newPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
                notePanel.revalidate();
            }
        }
        LOGGER.info("Leave");
    }
}
