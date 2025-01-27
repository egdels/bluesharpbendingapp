package de.schliweb.bluesharpbendingapp.view.desktop;

import javax.swing.*;
import java.awt.*;

/**
 * The TuningMeter class extends JPanel and represents a graphical component
 * designed to visualize the pitch deviation (in cents) of a musical note from
 * its reference pitch. It displays a semi-circular meter with a pointer, tick
 * marks, and labels for -50, 0, and +50 cent deviations.
 */
public class TuningMeter extends JPanel {
    /**
     * Represents the current deviation in cents from a reference pitch.
     * This value is used to measure the tuning offset and ranges from -50 to +50,
     * where negative values indicate the pitch is flat, positive values indicate the
     * pitch is sharp, and 0 represents a perfect tuning.
     */
    private double cents = 0.0; // Current offset in cents (-50 to +50)

    /**
     * Constructs a TuningMeter object which extends JPanel and sets a default
     * preferred size of 200x120 pixels. The TuningMeter provides a graphical
     * representation of tuning deviations, useful in applications such as
     * music-related tools for visualizing pitch accuracy.
     */
    public TuningMeter() {
        setPreferredSize(new Dimension(200, 120)); // Default size of the meter
    }

    /**
     * Sets the cent deviation value for tuning measurement and limits it within the
     * range of -50 to 50. This value is used for graphical representation of tuning
     * accuracy.
     *
     * @param cents the cent deviation value to be set, representing tuning deviation
     *              from a reference pitch, constrained between -50 and 50
     */
    public void setCents(double cents) {
        this.cents = Math.max(-50, Math.min(50, cents)); // Limit the value within the range
        repaint(); // Refresh the UI
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int centerX = width / 2;
        int centerY = 20;
        int radius = Math.min(width, height) / 2;

        // Draw the background arc
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 180, 180);

        // Draw tick marks and labels in 5-step increments
        for (int i = -50; i <= 50; i += 5) {
            double angle = Math.toRadians(270 + i * 1.8);
            int tickStartX;
            int tickStartY;
            int tickEndX;
            int tickEndY;

            if (i % 10 == 0) {
                // 10-step increments: longer tick marks
                tickStartX = centerX + (int) ((radius - 10) * Math.cos(angle));
                tickStartY = centerY - (int) ((radius - 10) * Math.sin(angle));
                tickEndX = centerX + (int) (radius * Math.cos(angle));
                tickEndY = centerY - (int) (radius * Math.sin(angle));

                g2.setColor(Color.LIGHT_GRAY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(tickStartX, tickStartY, tickEndX, tickEndY);

                // Draw labels only for -50, 0, and +50
                if (i == -50 || i == 0 || i == 50) {
                    String label = String.valueOf(i);
                    int labelX = centerX + (int) ((radius - 25) * Math.cos(angle)) - g2.getFontMetrics().stringWidth(label) / 2;
                    int labelY = centerY - (int) ((radius - 25) * Math.sin(angle)) + 5;
                    g2.drawString(label, labelX, labelY);
                }
            } else {
                // 5-step increments: shorter tick marks without labels
                tickStartX = centerX + (int) ((radius - 5) * Math.cos(angle));
                tickStartY = centerY - (int) ((radius - 5) * Math.sin(angle));
                tickEndX = centerX + (int) (radius * Math.cos(angle));
                tickEndY = centerY - (int) (radius * Math.sin(angle));

                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(tickStartX, tickStartY, tickEndX, tickEndY);
            }
        }

        // Draw the pointer
        double pointerAngle = Math.toRadians(270 + cents * 1.8);
        int pointerX = centerX + (int) (radius * Math.cos(pointerAngle));
        int pointerY = centerY - (int) (radius * Math.sin(pointerAngle));

        Color color = new Color(
                (int) (250.0 * Math.abs(cents / 50.0)),
                (int) (250.0 * (1.0 - Math.abs(cents / 50.0))),
                0
        );

        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(centerX, centerY, pointerX, pointerY);
    }
}
