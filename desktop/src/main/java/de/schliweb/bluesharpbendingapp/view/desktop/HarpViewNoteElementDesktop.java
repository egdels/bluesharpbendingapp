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
            if (child instanceof Label && notePanel.getComponentZOrder(child) > 0) {
                notePanel.remove(child);
            }
        }
        LOGGER.info("Leave");
    }

    @Override
    public void update(double cents) {
        LOGGER.info("Enter with parameter " + cents);
        // Vor dem Update bestehende Labels entfernen
        for (Component child : notePanel.getComponents()) {
            if (child instanceof Label && notePanel.getComponentZOrder(child) > 0) {
                notePanel.remove(child);
            }
        }

        Label noteLabel = new Label();
        int lineHeight = (notePanel.getBounds().height / 10);
        double position = 0.5 * (notePanel.getBounds().height) * (1.0 - (cents / 50.0));
        if ((int) position >= 0 && (int) position <= notePanel.getBounds().height - lineHeight) {
            noteLabel.setBounds(0, (int) position, notePanel.getBounds().width, lineHeight);
            Color color = new Color((int) (250.0 * Math.abs(cents / 50.0)),
                    (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0);
            noteLabel.setBackground(color);
            notePanel.add(noteLabel);
        }
        LOGGER.info("Leave");
    }
}
