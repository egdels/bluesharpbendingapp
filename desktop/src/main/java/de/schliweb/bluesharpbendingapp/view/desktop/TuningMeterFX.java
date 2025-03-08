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

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;



/**
 * A JavaFX custom control that visualizes tuning deviations in cents.
 * The TuningMeterFX is represented as a semicircular meter with a
 * movable needle indicating the deviation from perfect tuning.
 */
public class TuningMeterFX extends Pane {

    private final Canvas canvas; // The canvas used for drawing the meter
    private double cents = 0.0;  // Current deviation in cents (-50 to +50)


    /**
     * Constructs a new instance of TuningMeterFX, a custom JavaFX Pane designed to display
     * a tuning meter visualization. The tuning meter uses a canvas to render a graphical
     * representation of tuning values and dynamically adjusts its size in response to changes
     * in the Pane's dimensions.
     * <p>
     * This constructor initializes the following:
     * - A Canvas object for custom drawing.
     * - Listeners to handle dynamic resizing of the canvas in response to changes in the width
     *   and height properties of the Pane.
     * - A preferred size for the Pane set to 200x120 pixels.
     * - An initial graphical rendering of the tuning meter.
     * <p>
     * The pane reacts to resizing events and redraws its graphical content accordingly. The
     * visual components include a background arc, tick marks, labels, and a needle that moves
     * based on tuning values.
     */
    public TuningMeterFX() {
        canvas = new Canvas(200, 120);
        getChildren().add(canvas);

        // Update canvas size whenever the Pane's size changes
        widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            draw(); // Redraw the meter on resize
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            draw(); // Redraw the meter on resize
        });

        setPrefSize(200, 120); // Set the preferred size of this Pane
        draw(); // Initial drawing of the meter
    }


    /**
     * Sets the value of cents for the tuning meter, clamping it to the range
     * between -50 and 50. This method also triggers a redraw of the tuning meter
     * on the JavaFX Application thread to reflect the updated value.
     *
     * @param cents the tuning offset in cents. Values outside the range [-50, 50]
     *              will be clamped to this range.
     */
    public void setCents(double cents) {
        // Clamp the value between -50 and 50
        this.cents = Math.max(-50, Math.min(50, cents));

        // Schedule a redraw on the JavaFX Application thread
        Platform.runLater(this::draw);
    }


    /**
     * Draws the graphical representation of the tuning meter on the canvas.
     * The meter includes a background arc, tick marks, labels, and a needle.
     * <p>
     * This method handles the following:
     * - Clears any previous drawing from the canvas.
     * - Draws a semi-circular background meter arc using light gray.
     * - Renders tick marks at intervals, with larger ticks for significant labels.
     * - Displays labels for -50, 0, and +50 cents values.
     * - Draws a needle indicating the current tuning offset, with its color
     *   dynamically changing based on the deviation from the center.
     * <p>
     * The needle and tick positions are calculated based on the width and height
     * of the canvas, and adjusted dynamically for resizing.
     */
    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Clear the previous drawing
        gc.clearRect(0, 0, width, height);

        // Calculate geometry for the meter
        double centerX = width / 2;
        double centerY = 20; // Center positioned closer to the top
        double radius = Math.min(width, height) / 2;

        // Draw the background arc
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeArc(centerX - radius, centerY - radius,
                radius * 2, radius * 2,
                180, 180, javafx.scene.shape.ArcType.OPEN);

        // Draw tick marks
        gc.setLineWidth(1);
        for (int i = -50; i <= 50; i += 5) {
            // Convert cent to angle (1.8 degrees per cent)
            double angle = Math.toRadians(270 + (i * 1.8));
            double startRadius = (i % 10 == 0) ? radius - 15 : radius - 10;

            // Calculate start and end coordinates for tick marks
            double startX = centerX + Math.cos(angle) * startRadius;
            double startY = centerY - Math.sin(angle) * startRadius;
            double endX = centerX + Math.cos(angle) * radius;
            double endY = centerY - Math.sin(angle) * radius;

            // Draw the tick line
            gc.strokeLine(startX, startY, endX, endY);
        }

        // Draw labels
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font(12));
        gc.setTextAlign(TextAlignment.CENTER);

        // Label for -50 cents
        gc.fillText("-50", centerX - radius - 15, centerY);
        // Label for 0 cents
        gc.fillText("0", centerX, centerY + radius + 15);
        // Label for +50 cents
        gc.fillText("+50", centerX + radius + 15, centerY);

        // Draw the needle
        double angle = Math.toRadians(270 + (cents * 1.8));

        // Calculate color for the needle based on the absolute deviation
        double absValue = Math.abs(cents / 50.0);
        Color lineColor = Color.rgb(
                (int) (250.0 * absValue),          // Red component (increases with deviation)
                (int) (250.0 * (1.0 - absValue)),  // Green component (decreases with deviation)
                0                                  // Blue component (constant)
        );
        gc.setStroke(lineColor);
        gc.setLineWidth(2);

        // Draw the needle from the center to the calculated angle
        gc.strokeLine(centerX, centerY,
                centerX + Math.cos(angle) * radius,
                centerY - Math.sin(angle) * radius);
    }
}
