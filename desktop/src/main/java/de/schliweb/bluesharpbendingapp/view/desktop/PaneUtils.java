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

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.*;

/**
 * Utility class that provides helper methods to manipulate and bind components
 * within a {@link Pane}, including labels and graphical elements like lines.
 * <p>
 * This class is meant to be used with pre-configured {@link Pane} containing
 * specific child components such as {@link Line} and {@link Label}. It provides
 * methods to adjust the layout, appearance, and properties of these elements.
 */
public class PaneUtils {

    /**
     * Represents the default padding value applied within the context of
     * layout adjustments and graphical updates in JavaFX panes.
     * <p>
     * This constant is primarily used for setting or constraining the spacing
     * or offsets in graphical components, such as lines or labels, ensuring
     * consistent margins and layout positioning.
     * <p>
     * The value is fixed at 1.0, which acts as a uniform unit of measurement
     * for padding across associated utility methods in the PaneUtils class.
     */
    private static final double PADDING = 1.0;

    /**
     * Utility class for managing layout and graphical updates within a JavaFX Pane.
     * The PaneUtils class provides static methods to support dynamic updates
     * and bindings for graphical components, such as labels and lines, in JavaFX
     * Pane objects. These methods are particularly useful for managing the visual
     * appearance of tuning-related graphics.
     * <p>
     * This class is not instantiable, as all methods are static.
     */
    private PaneUtils() {
    }

    /**
     * Clamps a value between a minimum and maximum value.
     * This is a replacement for Math.clamp which is only available in Java 21+.
     *
     * @param value the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return the clamped value
     */
    private static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Binds the second child `Label` within the given `Pane` to the center of the `Pane`.
     * The method dynamically adjusts the label's position by binding its `layoutX` and `layoutY`
     * properties to the center coordinates of the `Pane`, based on its width and height.
     * <p>
     * This ensures that the label remains centered even when the size of the `Pane` changes.
     * The method assumes that the second child of the `Pane` is of type `Label`, and if it is
     * not, a `ClassCastException` will be thrown at runtime.
     *
     * @param pane the `Pane` to which the label will be bound. The `Pane` must have at least
     *             two children, and the second child must be an instance of `Label`.
     */
    public static void bindLabelToPane(Pane pane) {
        Label label = (Label) pane.getChildren().get(1);
        label.layoutXProperty().unbind();
        label.layoutYProperty().unbind();
        label.layoutXProperty().bind(pane.widthProperty().subtract(label.widthProperty()).divide(2));
        label.layoutYProperty().bind(pane.heightProperty().subtract(label.heightProperty()).divide(2));
    }

    /**
     * Updates the visual properties of a line within the provided `Pane` based on the given `cents` value.
     * This method adjusts the position, thickness, and color of the line dynamically. The line's horizontal
     * extent is constrained within the `Pane`, with optional padding applied.
     *
     * @param pane  the JavaFX `Pane` containing the line to be updated. The first child of the `Pane` must be
     *              an instance of `Line`, as this method assumes the presence of a `Line` object at index 0.
     * @param cents the offset value, typically representing a tuning deviation, used to determine the vertical
     *              position and color of the line. Values are clamped between -50 and 50.
     */
    public static void updateLine(Pane pane, double cents) {
        updateLine(pane, cents, PADDING);
    }

    /**
     * Updates the visual properties of a line within the provided `Pane` based on the given `cents` value and `padding`.
     * The method adjusts the position, thickness, and color of the line dynamically. The line's horizontal extent is
     * constrained within the `Pane`, with the specified padding applied.
     *
     * @param pane    the JavaFX `Pane` containing the line to be updated. The first child of the `Pane` must be an instance
     *                of `Line`, as this method assumes the presence of a `Line` object at index 0.
     * @param cents   the offset value, typically representing a tuning deviation, used to determine the vertical position
     *                and color of the line. Values are clamped between -50 and 50.
     * @param padding the amount of horizontal padding to apply on each side of the line within the `Pane`.
     */
    public static void updateLine(Pane pane, double cents, double padding) {
        double height = pane.getHeight();

        // Clamp the cents value between -50 and 50 to ensure valid input
        double clampedCents = clamp(cents, -50, 50);

        // Calculate the line thickness, ensuring it is at least 5 pixels
        int lineHeight = Math.max((int) (height / 10.0), 5);

        // Calculate the adjusted Y-position for the line
        double effectiveHeight = height - lineHeight;
        double yPosition = (effectiveHeight * ((-clampedCents + 50) / 100.0)) + ((double) lineHeight / 2);

        // Calculate the color of the line
        double absValue = Math.abs(cents / 50.0);
        Color lineColor = Color.rgb((int) (250.0 * absValue),          // Red component increases with abs value of cents
                (int) (250.0 * (1.0 - absValue)),  // Green component decreases with abs value of cents
                0                                  // Blue component is always 0
        );

        // Get the line from the pane
        Line line = (Line) pane.getChildren().get(0);

        // Set the line thickness
        line.setStrokeWidth(lineHeight);

        // Set the calculated color as the stroke color
        line.setStroke(lineColor);

        // Bind the start and end X-coordinates of the line within the pane
        line.startXProperty().bind(line.strokeWidthProperty().divide(2).add(padding));
        line.endXProperty().bind(pane.widthProperty().subtract(padding).subtract(line.strokeWidthProperty().divide(2)));

        // Set the calculated Y-position for proper vertical alignment
        line.setTranslateY(yPosition);

        // Make the line visible
        line.setVisible(true);
    }

    /**
     * Updates the graphical content of a `Label` within the given `Pane` to display
     * a musical note name and its corresponding tuning offset in cents. The method
     * modifies the second child of the `Pane`, assumed to be a `Label`, by setting
     * a centered `TextFlow` composed of the note name and cents value as its graphic.
     * <p>
     * The note name is displayed in bold font, while the cents value is formatted
     * as "Cents:+/-xxx" and displayed in a monospaced font for clarity. Text elements
     * are dynamically aligned and bound to the `Label`'s width to ensure layout
     * consistency.
     *
     * @param pane     the JavaFX `Pane` containing the target `Label`. It is assumed
     *                 that the second child of the `Pane` is a `Label`.
     * @param noteName the name of the musical note to display. For example, "A", "B♭", etc.
     * @param cents    the tuning offset value in cents to display. Positive or negative
     *                 values represent deviations from the standard pitch.
     */
    public static void updateLabelCent(Pane pane, String noteName, double cents) {
        Text noteTextNode = new Text(noteName);
        // Set the font of the note text node
        noteTextNode.setFont(Font.font(null, FontWeight.BOLD, 40));

        // Add a new line to separate the note name from the cents display
        Text newLine = new Text("\n");

        // Format the cents value as a string in the format "Cents:+/-xxx"
        String centsString = String.format("Cents:%+3d", (int) cents);
        Text centsTextNode = new Text(centsString);
        centsTextNode.setFont(Font.font("Monospace", 18)); // Use monospaced font for clarity.

        // Combine the text nodes into a single TextFlow element
        TextFlow textFlow = new TextFlow(noteTextNode, newLine, centsTextNode);
        textFlow.setTextAlignment(TextAlignment.CENTER); // Center-align the text horizontally
        textFlow.setMinWidth(100); // Set a minimum width for consistent layout

        // Retrieve the Label element from the pane
        Label label = (Label) pane.getChildren().get(1);

        // Bind the width of the TextFlow to the Label's width property
        textFlow.prefWidthProperty().bind(label.widthProperty());

        // Set the TextFlow as the graphical content of the label
        label.setGraphic(textFlow);

        // Configure the alignment of the label
        label.setAlignment(Pos.CENTER);
        label.setContentDisplay(ContentDisplay.CENTER);
    }
}
