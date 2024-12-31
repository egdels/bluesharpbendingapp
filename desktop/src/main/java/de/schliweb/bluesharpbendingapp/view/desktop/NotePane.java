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

import javax.swing.*;
import java.awt.*;


/**
 * The NotePane class extends JPanel and represents a graphical component
 * designed to display a note name along with a visual representation.
 * The visual representation is influenced by the current cent value.
 */
public class NotePane extends JPanel {

    /**
     * Represents the name of the musical note to be displayed in the NotePane.
     * This variable is a constant (immutable) and provides the text that will
     * appear as the note label in the graphical representation.
     */
    private final String noteName;
    /**
     * Represents the deviation in cents from a reference pitch, used for visualizing
     * the tuning or pitch accuracy of the musical note in the NotePane component.
     */
    private double cents;

    /**
     * Constructs a NotePane object with the specified note name, background color,
     * and cent deviation. This constructor allows initializing the instance with a
     * specific cent value in addition to the note name and color.
     *
     * @param noteName the name of the musical note to display
     * @param color    the background color of the NotePane
     * @param cents    the deviation in cents from a reference pitch for the note
     */
    public NotePane(String noteName, Color color, double cents) {
        this(noteName, color);
        this.cents = cents;
    }

    /**
     * Constructs a NotePane object with the specified note name and background color.
     * This constructor initializes the NotePane with a default cent deviation of -1000.
     *
     * @param noteName the name of the musical note to display
     * @param color    the background color of the NotePane
     */
    public NotePane(String noteName, Color color) {
        this.noteName = noteName;
        this.cents = -1000;
        setBackground(color);
    }

    /**
     * Paints the component by drawing a filled rectangle representing the cent deviation
     * and displaying the note name centered within the panel. This method is responsible
     * for the custom rendering of the NotePane based on its current state.
     *
     * @param gr the Graphics object used for drawing on the component
     */
    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        if (cents >= -50 && cents <= 50) {
            int lineHeight = Math.max((int) (getHeight() / 10.0), 5);

            // between - (lineHeight/2) and getHeight - (lineHeight/2)
            double position = 0.5 * (getHeight()) * (1.0 - (cents / 50.0)) - ((double) lineHeight / 2);
            Color color = new Color((int) (250.0 * Math.abs(cents / 50.0)),
                    (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0);
            gr.setColor(color);
            if (position < 0) {
                position = 0;
            }
            if (position + lineHeight > getHeight()) {
                position = getHeight() - (double) lineHeight;
            }
            gr.fillRect(0, (int) position, getWidth(), lineHeight);
        }

        // Determine the center of the panel
        int cntrX = getWidth() / 2;
        int cntrY = getHeight() / 2;


        // Set Font
        Font font = new Font(getFont().getFontName(), Font.BOLD, 15);
        gr.setFont(font);

        // Get the FontMetrics
        FontMetrics metrics = gr.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = cntrX - (metrics.stringWidth(noteName)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = cntrY - (metrics.getHeight() / 2) + metrics.getAscent();


        gr.setColor(Color.BLACK);
        gr.drawString(noteName, x, y);
    }
}

