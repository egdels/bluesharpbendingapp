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
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The HarpViewNoteElementDesktopFX class represents a graphical user interface
 * element for displaying and interacting with musical notes in a harp-related
 * application. It implements the HarpViewNoteElement interface, providing
 * functionality for dynamically updating and styling note-related panes in a
 * JavaFX application.
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
     * The method assumes that the Pane contains a Label as its second child, and it sets up bindings on the
     * Label's layoutX and layoutY properties to keep it centered within the Pane.
     *
     * @param pane The Pane containing the Label to be centered. The Pane is expected to have a specific
     *             structure where the second child is a Label element.
     */
    private void bindLabelToPane(Pane pane) {
        PaneUtils.bindLabelToPane(pane);
    }

    /**
     * Clears the visual state of the given {@code Pane}.
     * This method removes specific CSS class styles and hides visual elements associated
     * with the pane, effectively resetting its appearance. It ensures that the operation
     * is performed on the JavaFX Application Thread for thread safety.
     *
     * @param pane The {@code Pane} to be cleared. It is expected to have a specific structure
     *             where the first child is a {@code Line} element.
     */
    private void clearPane(Pane pane) {
        Platform.runLater(() -> {
            pane.getStyleClass().remove("highlight-chord");
            Line line = (Line) pane.getChildren().get(0);
            line.setVisible(false);
        });
    }

    /**
     * Clears the visual indicators in both the note pane and the enlarged pane (if present).
     * This method makes the line elements in both panes invisible, effectively resetting
     * the visual state of the note element.
     */
    @Override
    public void clear() {
        if (enlargedPane != null) {
            clearPane(enlargedPane);
        }
        clearPane(notePane);
    }

    /**
     * Updates the graphical representation of a given {@code Pane} based on the specified pitch offset in cents.
     * This method schedules the update to be executed on the JavaFX Application Thread to ensure thread safety.
     * The update involves coordinating changes to the pane and, if the pane is set as the enlarged pane,
     * updates the associated enlarged label to reflect the new pitch offset.
     *
     * @param pane  The {@code Pane} to be updated. It is expected to have specific structural characteristics
     *              required by the underlying utility methods invoked during the update process.
     * @param cents The pitch offset to be applied, expressed in cents. Positive values indicate a sharper pitch,
     *              while negative values represent a flatter pitch.
     */
    private void updatePane(Pane pane, double cents) {
        javafx.application.Platform.runLater(() -> {
            PaneUtils.updateLine(pane, cents);

            if (pane.equals(enlargedPane)) {
                updateEnlargedLabelCent(cents);
            }

        });
    }

    /**
     * Updates the visual representation of the note element based on the specified pitch offset.
     * This method updates both the note pane and the enlarged pane (if present) to reflect
     * the current pitch offset in cents. The visual update includes adjusting the position
     * of line indicators and updating any associated labels.
     *
     * @param cents the pitch offset in cents, where positive values indicate a sharper pitch
     *              and negative values indicate a flatter pitch
     */
    @Override
    public void update(double cents) {
        if (enlargedPane != null) {
            updatePane(enlargedPane, cents);
        }
        updatePane(notePane, cents);
    }

    /**
     * Highlights the note pane to visually represent a chord.
     * This method utilizes JavaFX's Platform.runLater to ensure thread safety
     * and adds the "highlight-chord" CSS class to the note pane's style class list.
     * The applied CSS class is responsible for configuring the visual appearance
     * of the note pane to indicate a chord highlight state.
     */
    @Override
    public void highlightAsChord() {
        Platform.runLater(() -> notePane.getStyleClass().add("highlight-chord"));
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
     *             structure, where the first child is a Line, the second child is a Label,
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
     * Updates the label text of the enlarged pane to reflect the specified pitch offset in cents.
     * This method utilizes {@code PaneUtils.updateLabelCent()} to dynamically adjust the label
     * text based on the provided {@code cents} value. The label to be updated is expected to be
     * the second child within the corresponding {@code notePane}.
     *
     * @param cents The pitch offset to be displayed on the label, expressed in cents. Positive values
     *              indicate a sharper pitch, while negative values represent a flatter pitch.
     */
    private void updateEnlargedLabelCent(double cents) {
        PaneUtils.updateLabelCent(enlargedPane, ((Label) notePane.getChildren().get(1)).getText(), cents);
    }
}
