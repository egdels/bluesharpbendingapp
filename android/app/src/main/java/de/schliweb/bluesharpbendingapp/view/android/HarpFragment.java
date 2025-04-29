package de.schliweb.bluesharpbendingapp.view.android;
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.NoteContainer;
import de.schliweb.bluesharpbendingapp.databinding.FragmentHarpBinding;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.style.StyleUtils;

import java.util.Map;
import java.util.Set;


/**
 * The HarpFragment class is responsible for managing and displaying the user interface
 * of a virtual harp within a fragment. This class is a specialized fragment that handles
 * the creation and interaction of harp note elements and manages their states, including
 * the ability to display notes in an enlarged view mode.
 * <p>
 * HarpFragment inherits functionality from Android Fragment, HarpView, and FragmentView,
 * integrating both fragment lifecycle management and interactive harp note behaviors.
 * <p>
 * Fields:
 * - noteViews: An array used to store references to TextView elements representing notes.
 * - binding: Manages the View binding for the fragment layout.
 * - isNoteEnlarged: A flag indicating whether a note is currently displayed in enlarged view.
 * - enlargedTextView: The TextView that displays the enlarged version of a selected note.
 * <p>
 * Lifecycle Methods:
 * - onCreateView: Handles inflation of the fragment's layout.
 * - onViewCreated: Initializes UI elements, sets up note interaction behaviors, and manages
 * connections to the ViewModel for state handling.
 * - onDestroyView: Cleans up resources used by the fragment when its view is destroyed.
 * <p>
 * HarpView Methods:
 * - initNotes: Initializes the notes for the view using an array of NoteContainer objects.
 * - getHarpViewElement: Retrieves a HarpViewNoteElement corresponding to a specified channel
 * and note in the UI.
 * <p>
 * FragmentView Methods:
 * - getInstance: Provides the current instance of the fragment view for external references.
 * <p>
 * Additional Private Methods:
 * - showEnlargedTextView: Displays the selected note in an overlay in enlarged form.
 * - hideEnlargedTextView: Hides the overlay showing the enlarged note and resets state.
 * - createHarpTableLayout: Dynamically generates the layout for harp notes within a table.
 * - configureOverlayNoteView: Sets up the overlay TextView for enlarged note display.
 * - configureNoteClickListeners: Assigns click listeners to notes within the TableLayout to
 * handle note selection and enlargement.
 * - setNoteClickListenersOnRow: Adds click listeners specifically to the notes in a TableRow.
 * - isChannelIdentifier: Checks if a given view ID corresponds to a channel identifier.
 * - setNoteColor: Updates the appearance of a note by changing its background color.
 * - initNote: Displays and initializes a note in the UI with its name and appearance.
 * - getNote: Retrieves the TextView representing a note from the noteViews array.
 */
public class HarpFragment extends Fragment implements HarpView, FragmentView {

    /**
     * A static mapping of channel numbers to their corresponding view resource IDs.
     * This map is used to associate each channel with a specific UI element represented
     * by its resource identifier in the layout.
     * <p>
     * Key: Integer representing the channel number (1-based index).
     * Value: Integer representing the resource ID of the corresponding channel view.
     * <p>
     * The map includes predefined mappings for specific channels used within the application.
     */
    private static final Map<Integer, Integer> channelIds = Map.of(1, R.id.channel_1, 2, R.id.channel_2, 3, R.id.channel_3, 4, R.id.channel_4, 5, R.id.channel_5, 6, R.id.channel_6, 7, R.id.channel_8, 9, R.id.channel_9, 10, R.id.channel_10);
    /**
     * A static and final map that associates channel identifiers with their corresponding
     * notes and resource IDs. Each channel is represented by an integer key, and its value
     * is another map. The inner map pairs note offsets (integers) with resource IDs (integers).
     * <p>
     * The key in the outer map represents the channel number (e.g., 1 to 10).
     * The key in the inner map represents the note offset relative to a base note (e.g., -3 to 4).
     * The value in the inner map represents the resource ID associated with the respective note.
     * <p>
     * This structure is useful for managing and accessing view or other resource identifiers
     * based on the combination of a channel and a note offset.
     */
    private static final Map<Integer, Map<Integer, Integer>> channelNoteIds = Map.of(1, Map.of(-3, R.id.channel_1_note_minus3, -2, R.id.channel_1_note_minus2, -1, R.id.channel_1_note_minus1, 0, R.id.channel_1_note_0, 1, R.id.channel_1_note_1, 2, R.id.channel_1_note_2, 3, R.id.channel_1_note_3, 4, R.id.channel_1_note_4), 2, Map.of(-3, R.id.channel_2_note_minus3, -2, R.id.channel_2_note_minus2, -1, R.id.channel_2_note_minus1, 0, R.id.channel_2_note_0, 1, R.id.channel_2_note_1, 2, R.id.channel_2_note_2, 3, R.id.channel_2_note_3, 4, R.id.channel_2_note_4), 3, Map.of(-3, R.id.channel_3_note_minus3, -2, R.id.channel_3_note_minus2, -1, R.id.channel_3_note_minus1, 0, R.id.channel_3_note_0, 1, R.id.channel_3_note_1, 2, R.id.channel_3_note_2, 3, R.id.channel_3_note_3, 4, R.id.channel_3_note_4), 4, Map.of(-3, R.id.channel_4_note_minus3, -2, R.id.channel_4_note_minus2, -1, R.id.channel_4_note_minus1, 0, R.id.channel_4_note_0, 1, R.id.channel_4_note_1, 2, R.id.channel_4_note_2, 3, R.id.channel_4_note_3, 4, R.id.channel_4_note_4), 5, Map.of(-3, R.id.channel_5_note_minus3, -2, R.id.channel_5_note_minus2, -1, R.id.channel_5_note_minus1, 0, R.id.channel_5_note_0, 1, R.id.channel_5_note_1, 2, R.id.channel_5_note_2, 3, R.id.channel_5_note_3, 4, R.id.channel_5_note_4), 6, Map.of(-3, R.id.channel_6_note_minus3, -2, R.id.channel_6_note_minus2, -1, R.id.channel_6_note_minus1, 0, R.id.channel_6_note_0, 1, R.id.channel_6_note_1, 2, R.id.channel_6_note_2, 3, R.id.channel_6_note_3, 4, R.id.channel_6_note_4), 7, Map.of(-3, R.id.channel_7_note_minus3, -2, R.id.channel_7_note_minus2, -1, R.id.channel_7_note_minus1, 0, R.id.channel_7_note_0, 1, R.id.channel_7_note_1, 2, R.id.channel_7_note_2, 3, R.id.channel_7_note_3, 4, R.id.channel_7_note_4), 8, Map.of(-3, R.id.channel_8_note_minus3, -2, R.id.channel_8_note_minus2, -1, R.id.channel_8_note_minus1, 0, R.id.channel_8_note_0, 1, R.id.channel_8_note_1, 2, R.id.channel_8_note_2, 3, R.id.channel_8_note_3, 4, R.id.channel_8_note_4), 9, Map.of(-3, R.id.channel_9_note_minus3, -2, R.id.channel_9_note_minus2, -1, R.id.channel_9_note_minus1, 0, R.id.channel_9_note_0, 1, R.id.channel_9_note_1, 2, R.id.channel_9_note_2, 3, R.id.channel_9_note_3, 4, R.id.channel_9_note_4), 10, Map.of(-3, R.id.channel_10_note_minus3, -2, R.id.channel_10_note_minus2, -1, R.id.channel_10_note_minus1, 0, R.id.channel_10_note_0, 1, R.id.channel_10_note_1, 2, R.id.channel_10_note_2, 3, R.id.channel_10_note_3, 4, R.id.channel_10_note_4));
    /**
     * A two-dimensional array of TextView objects representing the notes
     * displayed in the user interface of the harp fragment. Each element in the
     * first dimension corresponds to a channel, and each element in the second
     * dimension corresponds to a specific note within that channel.
     * <p>
     * Channels: Represented by the first dimension of the array (0 to 9), where
     * each index corresponds to a harp channel.
     * <p>
     * Notes: Represented by the second dimension of the array (0 to 7),
     * corresponding to note positions within the channel.
     * <p>
     * The array is initialized with a fixed size and is populated with
     * TextView objects that are dynamically generated and bound to the UI.
     * These TextViews display note information and allow user interactions such
     * as clicking to enlarge or highlight individual notes.
     * <p>
     * This variable is immutable and cannot be reassigned after its initialization.
     */
    private final TextView[][] noteViews = new TextView[10][8];
    /**
     * Represents the binding for the HarpFragment layout, enabling access to UI elements defined in the corresponding XML layout file.
     * The binding provides an abstraction to access and manipulate views without directly interacting with `findViewById`.
     * It is used to manage and interact with the UI components within the fragment more efficiently.
     */
    private FragmentHarpBinding binding;
    /**
     * Tracks whether a musical note is currently enlarged in the UI.
     * <p>
     * This variable is toggled to true when a note is displayed in its enlarged state
     * (e.g., via an overlay or other highlighting mechanism) and toggled back to false
     * when the enlarged state is removed. It plays a central role in controlling the
     * visibility and interaction of the note enlargement feature.
     */
    private boolean isNoteEnlarged = false;
    /**
     * Stores a reference to the currently enlarged TextView element in the harp view.
     * This field is used to track the state of the note that is visually highlighted or emphasized.
     * It is updated when a note is shown or hidden in the enlarged view.
     * A null value indicates that no note is currently enlarged.
     */
    private TextView enlargedTextView = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHarpBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /**
     * Displays an enlarged view of the given note in the overlay note TextView.
     * Copies the text and background from the original note and displays it in the overlay view.
     * Updates the state to track the enlarged note and associates it with the overlay.
     * If a note is already enlarged, the method will return without any operations.
     *
     * @param note        The TextView representing the original note to be enlarged.
     * @param overlayNote The TextView that acts as the overlay for displaying the enlarged note.
     */
    private void showEnlargedTextView(TextView note, TextView overlayNote) {
        // Early return if a note is already enlarged
        if (isNoteEnlarged) return;

        // Copy the text from the original note to the overlay view
        overlayNote.setText(note.getText());

        // Copy the background from the original note to the overlay view
        overlayNote.setBackground(note.getBackground().getConstantState().newDrawable().mutate());

        // Make the overlay note card visible
        View overlayCard = requireView().findViewById(R.id.overlay_note_card);
        overlayCard.setVisibility(View.VISIBLE);

        // Get the original note element and link it with the enlarged view
        HarpViewNoteElementAndroid originalElement = HarpViewNoteElementAndroid.getInstance(note);
        originalElement.setEnlargedTextView(overlayNote);

        // Update state to track the enlarged view
        isNoteEnlarged = true;
        enlargedTextView = note;
    }

    /**
     * Hides the enlarged view of a note displayed in the overlay TextView.
     * Resets the state, clears references, and hides the overlay view.
     *
     * @param overlayNote The TextView that displays the overlay view of the enlarged note.
     */
    private void hideEnlargedTextView(TextView overlayNote) {
        // Early return if no note is currently enlarged
        if (!isNoteEnlarged) return;

        // Get the original note element associated with the enlarged view
        HarpViewNoteElementAndroid originalElement = HarpViewNoteElementAndroid.getInstance(enlargedTextView);

        // Remove the reference to enlarged view in the original element
        originalElement.setEnlargedTextView(null);

        // Hide the overlay note card
        View overlayCard = requireView().findViewById(R.id.overlay_note_card);
        overlayCard.setVisibility(View.GONE);

        // Reset enlarged view state
        isNoteEnlarged = false;
        enlargedTextView = null;
    }

    /**
     * Called after the fragment's view has been created. This method initializes the UI elements
     * within the harp table, sets up click listeners for note interactions, and handles the state
     * of the enlarged view when notes are selected. The method also establishes a connection with
     * the ViewModel for fragment interaction.
     *
     * @param view               The root view of the fragment's layout.
     * @param savedInstanceState A Bundle containing any saved state information for the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TableLayout tableLayout = view.findViewById(R.id.harp_table);
        TextView overlayNote = view.findViewById(R.id.overlay_note);

        // Create the harp table layout
        createHarpTableLayout(tableLayout, overlayNote);

        configureOverlayNoteView(overlayNote);
        configureNoteClickListeners(tableLayout, overlayNote);

        FragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(FragmentViewModel.class);
        viewModel.selectFragmentView(this);
    }

    /**
     * Creates the harp table layout programmatically.
     *
     * @param tableLayout The TableLayout to populate with rows and cells
     * @param overlayNote The TextView to use for the overlay note display
     */
    private void createHarpTableLayout(TableLayout tableLayout, TextView overlayNote) {
        // Create rows for notes (-3 to 0)
        for (int noteIndex = -3; noteIndex <= 0; noteIndex++) {
            TableRow row = new TableRow(requireContext());
            row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

            // Create cells for channels (1 to 10)
            for (int channel = 1; channel <= 10; channel++) {
                TextView noteView = createNoteTextView(noteIndex, channel);

                row.addView(noteView);

                // Store the view for later access
                noteViews[channel - 1][noteIndex + 3] = noteView;
            }

            tableLayout.addView(row);
        }

        // Create the channel label row
        TableRow labelRow = new TableRow(requireContext());
        labelRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        for (int channel = 1; channel <= 10; channel++) {
            TextView labelView = new TextView(requireContext());
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            int margin = StyleUtils.getNoteMargin();
            params.setMargins(margin, margin, margin, margin); // Add margins for better spacing
            labelView.setLayoutParams(params);

            labelView.setGravity(android.view.Gravity.CENTER);
            labelView.setText(String.valueOf(channel));

            // Use Material Design text appearance
            labelView.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle1);
            labelView.setTypeface(labelView.getTypeface(), android.graphics.Typeface.BOLD);

            // Use fixed IDs for channel labels to support testing
            int channelId;
            if (channelIds.containsKey(channel)) {
                channelId = channelIds.get(channel);
            } else {
                channelId = View.generateViewId();
            }
            labelView.setId(channelId);

            // Add a subtle background
            labelView.setBackgroundResource(R.drawable.label_channel);

            // Add padding for better appearance
            int padding = StyleUtils.getNotePadding();
            labelView.setPadding(padding, padding, padding, padding);

            labelRow.addView(labelView);
        }

        tableLayout.addView(labelRow);

        // Create rows for notes (1 to 4)
        for (int noteIndex = 1; noteIndex <= 4; noteIndex++) {
            TableRow row = new TableRow(requireContext());
            row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

            // Create cells for channels (1 to 10)
            for (int channel = 1; channel <= 10; channel++) {
                TextView noteView = createNoteTextView(noteIndex, channel);

                row.addView(noteView);

                // Store the view for later access
                noteViews[channel - 1][noteIndex + 3] = noteView;
            }

            tableLayout.addView(row);
        }

        // Set up click listeners for the notes
        configureNoteClickListeners(tableLayout, overlayNote);
    }

    private TextView createNoteTextView(int note, int channel) {
        TextView textView = new TextView(requireContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        int margin = StyleUtils.getNoteMargin();
        params.setMargins(margin, margin, margin, margin); // Add margins for better spacing
        textView.setLayoutParams(params);

        // Set the background based on the note type
        int backgroundResId;
        if (note < 0) {
            backgroundResId = R.drawable.note_blow;
        } else if (note == 0 || note == 1) {
            backgroundResId = R.drawable.note_channel;
        } else {
            backgroundResId = R.drawable.note_draw;
        }
        textView.setBackgroundResource(backgroundResId);

        // Use Material Design text appearance
        // textView.setTextColor(getResources().getColor(R.color.black, null));
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1);
        textView.setTextColor(getResources().getColor(R.color.black, null));
        textView.setVisibility(View.INVISIBLE);
        int id;
        if (channelNoteIds.containsKey(channel) && channelNoteIds.get(channel).containsKey(note)) {
            id = channelNoteIds.get(channel).get(note);
        } else {
            id = View.generateViewId();
        }
        textView.setId(id);

        // Add padding for better touch target size
        int padding = StyleUtils.getNotePadding();
        textView.setPadding(padding, padding, padding, padding);
        // textView.setPadding(8,8,8,8);
        // Make text bold for better readability
        textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);

        return textView;
    }

    /**
     * Configures the overlay note view by adding behavior for hiding the view
     * when it is clicked. This method also ensures the overlay note is hidden initially.
     *
     * @param overlayNote The TextView used to display the overlay view for enlarged notes.
     */
    private void configureOverlayNoteView(TextView overlayNote) {
        hideEnlargedTextView(overlayNote);

        // Set click listener on the card instead of just the TextView
        View overlayCard = requireView().findViewById(R.id.overlay_note_card);
        overlayCard.setOnClickListener(v -> hideEnlargedTextView(overlayNote));
    }

    /**
     * Configures click listeners for individual notes within a TableLayout.
     * Each note is represented as a TextView within a TableRow.
     * When a note is clicked, it triggers the display of an enlarged view in the overlay TextView.
     *
     * @param tableLayout The TableLayout containing rows of notes (TextViews).
     * @param overlayNote The TextView used as an overlay to display an enlarged view of the clicked note.
     */
    private void configureNoteClickListeners(TableLayout tableLayout, TextView overlayNote) {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View row = tableLayout.getChildAt(i);
            if (row instanceof TableRow tableRow) {
                setNoteClickListenersOnRow(tableRow, overlayNote);
            }
        }
    }

    /**
     * Sets click listeners for all TextView elements within a given TableRow,
     * allowing notes to be enlarged when clicked. If a TextView is determined
     * to represent a note (and not a channel identifier), the method assigns
     * a click listener that triggers the display of the note in an overlay
     * TextView for enlarged viewing.
     *
     * @param tableRow    The TableRow containing child Views, including the notes as TextViews.
     * @param overlayNote The TextView used as an overlay to display an enlarged view of a clicked note.
     */
    private void setNoteClickListenersOnRow(TableRow tableRow, TextView overlayNote) {
        for (int j = 0; j < tableRow.getChildCount(); j++) {
            View noteView = tableRow.getChildAt(j);
            if (noteView instanceof TextView && !isChannelIdentifier(noteView.getId())) {
                noteView.setOnClickListener(v -> {
                    TextView note = (TextView) v;
                    if (!isNoteEnlarged) {
                        showEnlargedTextView(note, overlayNote);
                    }
                });
            }
        }
    }

    /**
     * Determines if the given view ID corresponds to a channel identifier.
     *
     * @param viewId The ID of the view to check.
     * @return true if the view ID corresponds to a channel identifier; false otherwise.
     */
    private boolean isChannelIdentifier(int viewId) {
        // Use a Set for faster lookups
        Set<Integer> ids = Set.of(R.id.channel_1, R.id.channel_2, R.id.channel_3, R.id.channel_4, R.id.channel_5, R.id.channel_6, R.id.channel_7, R.id.channel_8, R.id.channel_9, R.id.channel_10);
        return ids.contains(viewId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void initNotes(NoteContainer[] notes) {
        for (NoteContainer note : notes) {
            initNote(note.getChannel(), note.getNote(), note.getNoteName());
            if (note.isOverblow() || note.isOverdraw()) {
                setNoteColor(note.getChannel(), note.getNote());
            }
            getHarpViewElement(note.getChannel(), note.getNote()).clear();
        }
    }


    /**
     * Updates the background color of a specified note in the harp UI.
     * This method retrieves the corresponding TextView for the note based
     * on the given channel and note index, and applies a specific drawable
     * resource as its background.
     *
     * @param channel The channel of the note to be updated (1-based index).
     * @param note    The note's position within the channel (-3 to 4, where 0 represents the central note).
     */
    private void setNoteColor(int channel, int note) {
        TextView textView = getNote(channel, note);
        // Use the appropriate background resource for overblow/overdraw notes
        textView.setBackgroundResource(R.drawable.note_overblowoverdraw);
    }


    /**
     * Initializes and displays a note on the UI by setting the provided name
     * and making the note visible. It also modifies the note's background
     * appearance.
     *
     * @param channel  The channel identifier for the note (1-based index).
     * @param note     The position of the note within the channel (-3 to 4, where 0 is the central note position).
     * @param noteName The name to be displayed on the note.
     */
    private void initNote(int channel, int note, String noteName) {
        TextView textView = getNote(channel, note);

        textView.setText(noteName);
        textView.setVisibility(View.VISIBLE);
    }


    /**
     * Retrieves the TextView corresponding to a specific note and channel in the noteViews array.
     *
     * @param channel The 1-based index of the channel containing the note.
     * @param note    The positional index of the note within the channel, relative to -3.
     * @return The TextView representing the specified note within the given channel.
     */
    private TextView getNote(int channel, int note) {
        return noteViews[channel - 1][note + 3];
    }

    @Override
    public Object getInstance() {
        return this;
    }


    @Override
    public HarpViewNoteElement getHarpViewElement(int channel, int note) {
        return HarpViewNoteElementAndroid.getInstance(getNote(channel, note));
    }
}
