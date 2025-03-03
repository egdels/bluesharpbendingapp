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
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * Represents the initial set of CSS classes assigned to the note pane.
     * This list is used to store the default CSS styles that define the
     * visual appearance of the note pane upon initialization. It ensures
     * that the default styling can be restored when needed.
     */
    private final List<String> initialCssClasses;


    /**
     * Represents a pane used to display an enlarged view of a note element within the
     * context of the harp visualization interface. This pane is dynamically styled
     * and configured to hold a specific structure, typically including a Label
     * element and a Line element, among other components. It visually enhances the
     * presentation of the note element, offering a more detailed or magnified view.
     * <p>
     * This field is managed within the lifecycle of the containing class and is updated
     * through defined methods to reflect the state or interactions related to the note element.
     */
    private Pane enlargedPane;


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
        bindLabelToPane(notePane);
        initialCssClasses = new ArrayList<>(notePane.getStyleClass());

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

    /**
     * Binds a Label element within the provided Pane to be centered dynamically as the Pane's size changes.
     * The method assumes that the Pane contains a Label as its first child, and it sets up bindings on the
     * Label's layoutX and layoutY properties to keep it centered within the Pane.
     *
     * @param pane The Pane containing the Label to be centered. The Pane is expected to have a specific
     *             structure where the first child is a Label element.
     */
    private void bindLabelToPane(Pane pane) {
        Label label = (Label) pane.getChildren().get(1);
        label.layoutXProperty().unbind();
        label.layoutYProperty().unbind();
        label.layoutXProperty().bind(pane.widthProperty().subtract(label.widthProperty()).divide(2));
        label.layoutYProperty().bind(pane.heightProperty().subtract(label.heightProperty()).divide(2));
    }

    /**
     * Clears the specified {@code Pane} by making its second child,
     * which is expected to be a {@code Line}, invisible.
     * This operation is scheduled to run on the JavaFX Application Thread.
     *
     * @param pane The {@code Pane} to be cleared. It is expected to have
     *             a specific structure where the second child is a {@code Line}.
     */
    private void clearPane(Pane pane) {
        Platform.runLater(() -> {
            Line line = (Line) pane.getChildren().get(0);
            line.setVisible(false);
        });
    }

    @Override
    public void clear() {
        if(enlargedPane != null) {
            clearPane(enlargedPane);
        }
        clearPane(notePane);
    }

    /**
     * Updates the display properties of the given {@code Pane} based on the provided cents value.
     * This method adjusts the appearance and position of visual elements in the pane, such as a line
     * and label, to reflect the specified cents input. The function ensures that the updates
     * are performed on the JavaFX Application Thread.
     *
     * @param pane  The {@code Pane} to be updated. It is expected to contain a specific structure:
     *              the second child is a {@code Line} element, and the third child (if present) is a {@code Label}.
     * @param cents The value representing the pitch offset in cents, clamped between -50 and 50. This value
     *              determines the line's position, thickness, and color as well as the text displayed in the label.
     */
    private void updatePane(Pane pane, double cents) {
        final int PADDING = 1; // Padding to account for the border of the pane

        javafx.application.Platform.runLater(() -> {
            // Get the height of the parent pane
            double height = pane.getHeight();

            // Clamp the cents value between -50 and 50 to ensure valid input
            double clampedCents = Math.max(-50, Math.min(50, cents));

            // Calculate the line thickness, ensuring it is at least 5 pixels
            int lineHeight = Math.max((int) (height / 10.0), 5);

            // Calculate the adjusted Y-position for the line
            // Subtract the line thickness from the available height and position proportionally
            double effectiveHeight = height - lineHeight;
            // double yPosition = (effectiveHeight * ((clampedCents + 50) / 100.0)) + ((double) lineHeight / 2);
            double yPosition = (effectiveHeight * ((-clampedCents + 50) / 100.0)) + ((double) lineHeight / 2);

            // Calculate the color of the line
            // The color transitions between red and green based on the absolute value of `cents`
            double absValue = Math.abs(cents / 50.0);
            Color lineColor = Color.rgb((int) (250.0 * absValue),          // Red component increases with abs value of cents
                    (int) (250.0 * (1.0 - absValue)),  // Green component decreases with abs value of cents
                    0                                  // Blue component is always 0
            );

            Line line = (Line) pane.getChildren().get(0);
            // Set the line thickness
            line.setStrokeWidth(lineHeight);

            // Set the calculated color as the stroke color
            line.setStroke(lineColor);

            // Bind the start and end X-coordinates of the line to fit within the pane, considering the stroke width and padding
            line.startXProperty().bind(line.strokeWidthProperty().divide(2).add(PADDING));
            line.endXProperty().bind(pane.widthProperty().subtract(PADDING).subtract(line.strokeWidthProperty().divide(2)));

            // Set the calculated Y-position of the line for proper vertical alignment
            line.setTranslateY(yPosition);

            if(pane.equals(enlargedPane)) {
                updateEnlargedLabelCent(cents);
            }

            // Make the line visible
            line.setVisible(true);
        });
    }

    @Override
    public void update(double cents) {
        if (enlargedPane != null) {
            updatePane(enlargedPane, cents);
        }
        updatePane(notePane, cents);
    }


    /**
     * Sets the name of the note and updates the text display of the associated label.
     *
     * @param noteName The name of the note to be displayed on the label.
     */
    public void setNoteName(String noteName) {
        Label label = (Label) notePane.getChildren().get(1);
        label.setText(noteName);
    }

    /**
     * Updates the style of the note pane to reflect an "overdraw" state.
     * This method adds the "overDrawNote" CSS class to the note pane and
     * removes the "drawNote" CSS class if it is present. The applied CSS
     * classes determine the visual representation of the note pane when
     * the "overdraw" state is active.
     */
    public void setOverdraw() {
        notePane.getStyleClass().add("overDrawNote");
        notePane.getStyleClass().remove("drawNote");
    }

    /**
     * Updates the style of the note pane to reflect an "overblow" state.
     * This method adds the "overBlowNote" CSS class to the note pane and
     * removes the "blowNote" CSS class if it is present. The applied CSS
     * classes influence the visual representation of the note pane to
     * indicate the "overblow" state.
     */
    public void setOverblow() {
        notePane.getStyleClass().add("overBlowNote");
        notePane.getStyleClass().remove("blowNote");
    }

    /**
     * Initializes the style of the note pane by clearing all existing CSS classes
     * and reapplying the initial set of CSS classes. This method ensures that the
     * note pane returns to its default visual state as defined by the initial CSS
     * configuration.
     */
    public void init() {
        notePane.getStyleClass().clear();
        notePane.getStyleClass().addAll(initialCssClasses);
    }

    /**
     * Sets the given Pane as the enlarged pane and updates its style, content, and bindings.
     * This method initializes the provided pane with the necessary visual and functional
     * adjustments to display an enlarged version of a note element.
     *
     * @param pane The Pane to be set as the enlarged pane. It is expected to have a specific
     *             structure, where the first child is a Label, the second child is a Line,
     *             and subsequent children may include other necessary components.
     */
    public void setEnlargedPane(Pane pane) {
        this.enlargedPane = pane;
        if (enlargedPane != null) {
            enlargedPane.getStyleClass().clear();
            enlargedPane.getStyleClass().addAll(notePane.getStyleClass());
            enlargedPane.getStyleClass().add("enlarged-cell");
            enlargedPane.setStyle(notePane.getStyle());
            updateEnlargedLabelCent(0);
            bindLabelToPane(enlargedPane);
            clearPane(enlargedPane);
        }
    }

    /**
     * Updates the graphical content of the enlarged label to display the note name and the pitch offset in cents.
     * The method formats the display to include the note name in bold font and the offset in monospaced font,
     * ensuring proper alignment and scaling of the content.
     *
     * @param cents The pitch offset to be displayed, expressed in cents. Positive values indicate a sharper pitch,
     *              while negative values represent a flatter pitch.
     */
    private void updateEnlargedLabelCent(double cents) {
        // Create a new Text object that displays the note name.
        // The note name is retrieved from the second child (index 1) of the notePane.
        // The text is styled with bold font and a font size of 40.
        Text noteTextNode = new Text(((Label)notePane.getChildren().get(1)).getText());
        noteTextNode.setFont(Font.font(null, FontWeight.BOLD, 40));

        // Add a new line to separate the note name from the cents display.
        Text newLine = new Text("\n");

        // Format the cents value as a string in the format "Cents:+/-xxx".
        // The "+" or "-" sign indicates whether the pitch is sharper or flatter.
        String centsString = String.format("Cents:%+3d", (int)cents);
        Text centsTextNode = new Text(centsString);
        centsTextNode.setFont(Font.font("Monospace", 18)); // Use monospaced font for clarity.

        // Combine the note name, new line, and cents value into a single TextFlow element.
        // This layout ensures proper alignment and structure for displaying the text elements.
        TextFlow textFlow = new TextFlow(noteTextNode, newLine, centsTextNode);
        textFlow.setTextAlignment(TextAlignment.CENTER); // Center-align the contents horizontally.
        textFlow.setMinWidth(100); // Set a minimum width to ensure consistent layout.

        // Retrieve the Label element from the enlargedPane,
        // which is responsible for displaying the graphical representation of the note.
        Label label = (Label) enlargedPane.getChildren().get(1);

        // Bind the width of the TextFlow to the width of the Label,
        // ensuring the layout adapts to the Label's size dynamically.
        textFlow.prefWidthProperty().bind(label.widthProperty());

        // Set the TextFlow as the graphical content of the Label.
        // This replaces any existing content with the combined note and cents display.
        label.setGraphic(textFlow);

        // Center the Label's alignment and its graphical content both vertically and horizontally.
        label.setAlignment(Pos.CENTER);
        label.setContentDisplay(ContentDisplay.CENTER);
    }
}
