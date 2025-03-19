package de.schliweb.bluesharpbendingapp.view.desktop;

import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import lombok.Setter;

import java.util.HashMap;

/**
 * TrainingViewNoteElementDesktopFX is a class that provides a desktop-specific implementation
 * of the HarpViewNoteElement interface for rendering and managing visual representations
 * of musical notes in a training view context. It uses JavaFX panes to manage and update
 * its visual elements.
 * <p>
 * This class ensures a single instance is associated with each Pane, employing a singleton
 * pattern scoped to the provided Pane. It also provides operations to clear, update,
 * and bind note-related visual elements to the pane for rendering.
 */
public class TrainingViewNoteElementDesktopFX implements HarpViewNoteElement {


    /**
     * A static map that maintains a one-to-one association between JavaFX {@link Pane}
     * objects and instances of {@link TrainingViewNoteElementDesktopFX}.
     * This serves to enforce a singleton pattern scoped to each {@link Pane}, ensuring
     * that each pane has a single, dedicated instance of {@link TrainingViewNoteElementDesktopFX}.
     * The map is used internally by the class to manage the creation and retrieval
     * of instances based on the associated pane.
     */
    private static final HashMap<Pane, TrainingViewNoteElementDesktopFX> instances = new HashMap<>();


    /**
     * The {@code notePane} is a final JavaFX {@link Pane} instance representing the graphical
     * container that holds and manages visual elements associated with a specific musical note.
     * It serves as the main visual pane tied to the life cycle and rendering of note-related
     * components in the context of the {@link TrainingViewNoteElementDesktopFX} class.
     * <p>
     * This pane is used for rendering dynamic elements, such as lines or labels, which update
     * in real-time based on musical note data, such as pitch and cent information. Additionally,
     * it's bound to helper methods to manage and update these elements efficiently through thread-safe
     * mechanisms provided by JavaFX's Platform class.
     * <p>
     * As a final field, the {@code notePane} is initialized via the constructor and remains
     * immutable, ensuring that the graphic association between this pane and its corresponding
     * {@link TrainingViewNoteElementDesktopFX} instance is fixed throughout its life cycle.
     */
    private final Pane notePane;


    /**
     * Represents the name of the note displayed in the TrainingViewNoteElementDesktopFX.
     * This variable holds the textual representation of the musical note.
     */
    @Setter
    private String noteName;

    /**
     * Constructs a new TrainingViewNoteElementDesktopFX object with the provided note pane.
     * Initializes the instance by associating the note pane and binding a label to the pane.
     *
     * @param notePane the Pane object representing the visual component for the note
     */
    private TrainingViewNoteElementDesktopFX(Pane notePane) {
        this.notePane = notePane;
        bindLabelToPane(notePane);
    }

    /**
     * Retrieves an existing instance of TrainingViewNoteElementDesktopFX associated with the given Pane,
     * or creates a new one if it does not already exist.
     *
     * @param notePane the Pane object representing the visual component for the note
     * @return an instance of TrainingViewNoteElementDesktopFX associated with the specified notePane
     */
    public static TrainingViewNoteElementDesktopFX getInstance(Pane notePane) {
        return instances.computeIfAbsent(notePane, TrainingViewNoteElementDesktopFX::new);
    }

    @Override
    public void clear() {
        Platform.runLater(() -> {
            Line line = (Line) notePane.getChildren().get(0);
            line.setVisible(false);
            updateLabelCent(0);
        });
    }

    @Override
    public void update(double cents) {
        javafx.application.Platform.runLater(() -> {
            PaneUtils.updateLine(notePane, cents);
            updateLabelCent(cents);
        });
    }

    /**
     * Binds a label to the specified pane, linking the label's behavior or display properties
     * to the given pane to ensure coordinated responsiveness and representation.
     *
     * @param pane the Pane object to which the label is bound
     */
    private void bindLabelToPane(Pane pane) {
        PaneUtils.bindLabelToPane(pane);
    }

    /**
     * Updates the label for the note pane with the provided cent value.
     * This method invokes the utility function to update the visual representation
     * of the cent deviation for the corresponding note.
     *
     * @param cents the cent deviation to be displayed on the note pane
     */
    private void updateLabelCent(double cents) {
        PaneUtils.updateLabelCent(notePane, noteName, cents);
    }
}
