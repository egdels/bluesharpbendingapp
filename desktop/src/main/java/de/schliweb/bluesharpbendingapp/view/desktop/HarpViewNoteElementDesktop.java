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
 * The HarpViewNoteElementDesktop class is a concrete implementation of the
 * HarpViewNoteElement interface specific to the desktop environment. It manages
 * the display and state of individual note elements within a harp view, using
 * a JPanel as the visual container.
 */
public class HarpViewNoteElementDesktop implements HarpViewNoteElement {


    /**
     * A mapping of JPanel instances to their corresponding HarpViewNoteElementDesktop objects.
     * Ensures that there is a one-to-one relationship between a JPanel and its associated
     * HarpViewNoteElementDesktop instance, facilitating efficient management and reuse of
     * note element views in the user interface.
     */
    private static final HashMap<JPanel, HarpViewNoteElementDesktop> instances = new HashMap<>();
    /**
     * Represents a JPanel used as the visual container for the note element in the
     * graphical user interface. This panel is dynamically updated to reflect changes
     * in the state or properties of the note, such as its name, color, and visual
     * representation of tuning or pitch adjustment.
     * <p>
     * Acts as the primary display component within the HarpViewNoteElementDesktop
     * implementation, hosting and updating the NotePane instance according to the
     * current configuration or input parameters.
     * <p>
     * The notePanel is initialized with a BorderLayout and will contain a NotePane
     * instance as its primary child component, which handles the actual rendering
     * of the note's graphical state.
     */
    private final JPanel notePanel;
    /**
     * An instance of NotePane used to represent and display musical note information in the UI.
     * It utilizes a JPanel to visually represent a musical note with additional features
     * such as cent tuning indicators.
     */
    private NotePane notePane;

    /**
     * Represents the color associated with the note element in the harp view.
     * This field is used to define the visual appearance of the note element,
     * such as its background or graphical highlights, in the desktop view.
     */
    private Color color = Color.black;
    /**
     * Represents the name of the musical note associated with this element.
     * <p>
     * This variable is used to store and modify the name of the note
     * represented within the component. It can be updated dynamically
     * through the setNoteName method to reflect changes in the visual
     * note elements.
     */
    private String noteName = "";

    /**
     * Constructs an instance of HarpViewNoteElementDesktop.
     *
     * @param notePanel the JPanel to be used for displaying the note pane.
     */
    private HarpViewNoteElementDesktop(JPanel notePanel) {
        this.notePanel = notePanel;
        this.notePane = new NotePane(noteName, color);
        this.notePanel.setLayout(new BorderLayout());
        this.notePanel.add(notePane, BorderLayout.CENTER);
    }

    /**
     * Returns an instance of {@code HarpViewNoteElementDesktop} associated with the given
     * {@code JPanel}. If an instance does not already exist for the specified panel, a new
     * instance is created and associated with it.
     *
     * @param notePanel the {@code JPanel} to be used for displaying the note pane.
     * @return an instance of {@code HarpViewNoteElementDesktop} associated with the specified {@code JPanel}.
     */
    public static HarpViewNoteElementDesktop getInstance(JPanel notePanel) {
        return instances.computeIfAbsent(notePanel, HarpViewNoteElementDesktop::new);
    }

    /**
     * Sets the color for this instance.
     *
     * @param color the new color to be set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets the name of the note associated with this instance.
     *
     * @param noteName the name of the note to be set
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
