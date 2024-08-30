package de.schliweb.bluesharpbendingapp.view.desktop;

import javax.swing.*;
import java.awt.*;


/**
 * The type NoteContainer pane.
 */
public class NotePane extends JPanel {

    /**
     * The Cents.
     */
    private double cents;
    /**
     * The NoteContainer name.
     */
    private final String noteName;

    /**
     * Instantiates a new NoteContainer pane.
     *
     * @param noteName the note name
     * @param color    the color
     * @param cents    the cents
     */
    public NotePane(String noteName, Color color, double cents) {
        this(noteName, color);
        this.cents = cents;
    }

    /**
     * Instantiates a new NoteContainer pane.
     *
     * @param noteName the note name
     * @param color    the color
     */
    public NotePane(String noteName, Color color) {
        this.noteName = noteName;
        this.cents = -1000;
        setBackground(color);
    }

    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        if (cents >= -50 && cents <= 50) {
            int lineHeight = Math.max((int) (getHeight() / 10.0), 5);

            // between - (lineHeight/2) and getHeight - (lineHeight/2)
            double position = 0.5 * (getHeight()) * (1.0 - (cents / 50.0)) - ((double) lineHeight /2);
            Color color = new Color((int) (250.0 * Math.abs(cents / 50.0)),
                    (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0);
            gr.setColor(color);
            if(position<0) {
                position = 0;
            }
            if(position+lineHeight>getHeight()) {
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
        int y = cntrY - ( metrics.getHeight() / 2) + metrics.getAscent();


        gr.setColor(Color.BLACK);
        gr.drawString(noteName, x, y);
    }
}

