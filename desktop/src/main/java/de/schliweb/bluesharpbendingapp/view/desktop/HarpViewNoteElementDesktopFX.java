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
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.HashMap;

/**
 * HarpViewNoteElementDesktopFX is a concrete implementation of the HarpViewNoteElement interface,
 * tailored for JavaFX desktop applications. It represents a visual element in the harp view
 * that dynamically displays a note's name and provides visual feedback using a line indicator.
 * The element is associated with a specific JavaFX {@code Pane}, which acts as its container.
 */
public class HarpViewNoteElementDesktopFX implements HarpViewNoteElement {

    /**
     * A static map storing instances of HarpViewNoteElementDesktopFX objects associated
     * with their respective Pane objects. This ensures that a single instance is created
     * and reused for a specific Pane, implementing the Singleton design pattern per Pane.
     */
    private static final HashMap<Pane, HarpViewNoteElementDesktopFX> instances = new HashMap<>();

    /**
     * Represents a JavaFX Pane used within the HarpViewNoteElementDesktopFX class
     * to display note-related graphical elements.
     * This Pane serves as the primary container for rendering visual components
     * associated with a note in the application.
     * It provides the visual layout and structure required for note representation.
     */
    private final Pane notePane;

    /**
     * Represents a graphical line component used as part of the visual
     * representation for the harp view note element in a desktop JavaFX application.
     * This line is likely associated with the graphical rendering or layout
     * within the harp note pane.
     */
    private final Line line;

    /**
     * Represents a graphical label element within the HarpViewNoteElementDesktopFX class.
     * This label is likely used to display textual information, such as the name or
     * description of a musical note or other relevant information related to the harp view.
     * The label is immutable and encapsulated as a final instance variable.
     */
    private final Label label;

    /**
     * Constructs an instance of the HarpViewNoteElementDesktopFX with the specified Pane.
     * This constructor initializes the provided Pane, retrieves its child components,
     * and dynamically centers a label element within the Pane.
     *
     * @param notePane The Pane containing the note display elements. It is expected to have
     *                 a specific structure where the second child is a Line element and
     *                 the first child is a Label element.
     */
    private HarpViewNoteElementDesktopFX(Pane notePane) {
        // Initialize the notePane
        this.notePane = notePane;

        // Retrieve and store the line element (assumes it's the second child in the pane)
        line = (Line) notePane.getChildren().get(1);

        // Retrieve and store the label element (assumes it's the first child in the pane)
        label = (Label) notePane.getChildren().get(0);

        // Dynamically center the label within the pane
        // Horizontal centering: bind the label's X position to the pane's width minus the label's width, divided by 2
        label.layoutXProperty().bind(notePane.widthProperty().subtract(label.widthProperty()).divide(2));

        // Vertical centering: bind the label's Y position to the pane's height minus the label's height, divided by 2
        label.layoutYProperty().bind(notePane.heightProperty().subtract(label.heightProperty()).divide(2));
    }


    /**
     * Retrieves an instance of {@code HarpViewNoteElementDesktopFX} associated with the given {@code Pane}.
     * If an instance does not already exist for the provided {@code Pane}, a new one is created.
     *
     * @param notePane The {@code Pane} containing the note display elements. It is expected to have
     *                 a specific structure where the first child is a {@code Label} and the second child is a {@code Line}.
     * @return An instance of {@code HarpViewNoteElementDesktopFX} associated with the specified {@code Pane}.
     */
    public static HarpViewNoteElementDesktopFX getInstance(Pane notePane) {
        return instances.computeIfAbsent(notePane, HarpViewNoteElementDesktopFX::new);
    }

    @Override
    public void clear() {
        Platform.runLater(() -> line.setVisible(false));
    }


    @Override
    public void update(double cents) {
        final int PADDING = 1; // Padding to account for the border of the pane

        javafx.application.Platform.runLater(() -> {
            // Get the height of the parent pane
            double height = notePane.getHeight();

            // Clamp the cents value between -50 and 50 to ensure valid input
            double clampedCents = Math.max(-50, Math.min(50, cents));

            // Calculate the line thickness, ensuring it is at least 5 pixels
            int lineHeight = Math.max((int) (height / 10.0), 5);

            // Calculate the adjusted Y-position for the line
            // Subtract the line thickness from the available height and position proportionally
            double effectiveHeight = height - lineHeight;
            double yPosition = (effectiveHeight * ((clampedCents + 50) / 100.0)) + ((double) lineHeight / 2);

            // Calculate the color of the line
            // The color transitions between red and green based on the absolute value of `cents`
            double absValue = Math.abs(cents / 50.0);
            Color lineColor = Color.rgb(
                    (int) (250.0 * absValue),          // Red component increases with abs value of cents
                    (int) (250.0 * (1.0 - absValue)),  // Green component decreases with abs value of cents
                    0                                  // Blue component is always 0
            );

            // Set the line thickness
            line.setStrokeWidth(lineHeight);

            // Set the calculated color as the stroke color
            line.setStroke(lineColor);

            // Bind the start and end X-coordinates of the line to fit within the pane, considering the stroke width and padding
            line.startXProperty().bind(line.strokeWidthProperty().divide(2).add(PADDING));
            line.endXProperty().bind(notePane.widthProperty().subtract(PADDING).subtract(line.strokeWidthProperty().divide(2)));

            // Set the calculated Y-position of the line for proper vertical alignment
            line.setTranslateY(yPosition);

            // Make the line visible
            line.setVisible(true);
        });
    }


    /**
     * Sets the name of the note and updates the text display of the associated label.
     *
     * @param noteName The name of the note to be displayed on the label.
     */
    public void setNoteName(String noteName) {
        label.setText(noteName);
    }
}
