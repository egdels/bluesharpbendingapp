package de.schliweb.bluesharpbendingapp.view.android;

import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;

import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import lombok.Setter;

/**
 * Represents a UI element that handles displaying and updating specific note-related information
 * within an Android application. This class is implemented to manage visual updates for a note's
 * display, including modifying its background and text appearance to reflect cent adjustments.
 * <p>
 * This class provides methods to clear visual indications of adjustments or update the note's
 * display based on cent values in a musical context. It ensures that all visual changes occur
 * on the UI thread to properly modify UI components.
 * <p>
 * Implements the {@code HarpViewNoteElement} interface.
 */
public class TrainingViewNoteElementAndroid implements HarpViewNoteElement {

    /**
     * A static mapping that associates {@link TextView} instances with their corresponding
     * {@link TrainingViewNoteElementAndroid} objects. This allows the system to maintain a single
     * instance of {@link TrainingViewNoteElementAndroid} for each {@link TextView}, supporting efficient
     * reuse and ensuring correct association between a visual TextView and its functional data.
     */
    private static final HashMap<TextView, TrainingViewNoteElementAndroid> instances = new HashMap<>();
    /**
     * A TextView used to display the current musical note and its associated pitch offset (cents).
     * This TextView is formatted dynamically to display the note in bold and larger font size,
     * along with the pitch offset in a distinct monospace style and adjusted size.
     * It is intended to represent pitch accuracy feedback during training sessions.
     */
    private final TextView noteTextView;
    /**
     * Holds a reference to the associated FragmentActivity.
     * This variable is initialized in the constructor of the containing class
     * by retrieving the context of a provided TextView and casting it to a FragmentActivity.
     * Used to manage and interact with the activity context where the containing class operates.
     */
    private final FragmentActivity activity;

    /**
     * Represents the name of the note displayed within the associated TextView.
     * <p>
     * This variable stores the textual representation of the note, which may be used
     * for display and formatting purposes. It is expected to be dynamically updated
     * based on the application's context and the current musical note being processed.
     */
    @Setter
    private String noteName;

    /**
     * Constructor for the TrainingViewNoteElementAndroid class.
     * This initializes the noteTextView and the associated activity context.
     *
     * @param textView The TextView instance used to display the note element.
     */
    private TrainingViewNoteElementAndroid(TextView textView) {
        this.noteTextView = textView;
        this.activity = (FragmentActivity) textView.getContext();
    }

    /**
     * Retrieves an instance of the TrainingViewNoteElementAndroid associated with the given TextView.
     * The method ensures that the same instance is reused for a specific TextView.
     *
     * @param textView The TextView instance for which the TrainingViewNoteElementAndroid instance is to be retrieved.
     * @return An instance of TrainingViewNoteElementAndroid associated with the specified TextView.
     */
    public static TrainingViewNoteElementAndroid getInstance(TextView textView) {
        return instances.computeIfAbsent(textView, TrainingViewNoteElementAndroid::new);
    }

    @Override
    public void clear() {
        // Execute on UI thread since we're modifying UI elements
        activity.runOnUiThread(() -> {
            TextViewUtils.clearTextViewLine(noteTextView);
            TextViewUtils.updateTextViewCent(noteTextView, noteName, 0);
        });
    }

    @Override
    public void update(double cents) {
        activity.runOnUiThread(() -> {
            TextViewUtils.updateEnlargedTextViewLine(noteTextView, cents);
            TextViewUtils.updateTextViewCent(noteTextView, noteName, cents);
        });
    }

}
