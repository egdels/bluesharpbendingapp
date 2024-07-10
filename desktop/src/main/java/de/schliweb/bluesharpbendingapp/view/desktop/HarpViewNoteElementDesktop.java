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

import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * The type Harp view note element desktop.
 */
public class HarpViewNoteElementDesktop implements HarpViewNoteElement {


    /**
     * The constant instances.
     */
    private static final HashMap<JPanel, HarpViewNoteElementDesktop> instances = new HashMap<>();
    /**
     * The Note panel.
     */
    private final JPanel notePanel;
    /**
     * The Note pane.
     */
    private NotePane notePane;

    /**
     * The Color.
     */
    private Color color = Color.black;
    /**
     * The Note name.
     */
    private String noteName = "";

    /**
     * Instantiates a new Harp view note element desktop.
     *
     * @param notePanel the note panel
     */
    private HarpViewNoteElementDesktop(JPanel notePanel) {
        this.notePanel = notePanel;
        this.notePane = new NotePane(noteName, color);
        this.notePanel.setLayout(new BorderLayout());
        this.notePanel.add(notePane, BorderLayout.CENTER);
    }

    /**
     * Gets instance.
     *
     * @param notePanel the note panel
     * @return the instance
     */
    public static HarpViewNoteElementDesktop getInstance(JPanel notePanel) {
        if (!instances.containsKey(notePanel)) {
            instances.put(notePanel, new HarpViewNoteElementDesktop(notePanel));
        }
        return instances.get(notePanel);
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets note name.
     *
     * @param noteName the note name
     */
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    @Override
    public void clear() {
        notePanel.remove(notePane);
        notePane = new NotePane(noteName, color);
        notePanel.setLayout(new BorderLayout());
        notePanel.add(notePane, BorderLayout.CENTER);
        notePanel.revalidate();
    }

    @Override
    public void update(double cents) {
        notePanel.remove(notePane);
        notePane = new NotePane(noteName, color, cents);
        notePanel.setLayout(new BorderLayout());
        notePanel.add(notePane, BorderLayout.CENTER);
        notePanel.revalidate();
    }
}
