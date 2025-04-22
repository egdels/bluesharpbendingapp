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

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.HarpViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteContainer;
import de.schliweb.bluesharpbendingapp.databinding.FragmentHarpBinding;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


/**
 * The HarpFragment class represents a fragment responsible for visualizing and interacting
 * with a virtual harp layout in the application. This class is primarily responsible
 * for rendering the notes of the harp, handling user interactions such as touch events
 * on the notes, and updating the UI accordingly.
 * <p>
 * HarpFragment integrates with other components, specifically via the HarpView
 * and FragmentView interfaces, and interacts with a ViewModel for sharing state
 * and data between fragments.
 * <p>
 * Key functionality includes:
 * - Rendering a grid of notes represented as TextViews.
 * - Enlarging a pressed note for better visibility and reverting it back upon overlay click.
 * - Initializing and updating note properties such as text and background color.
 * - Managing visibility states for UI elements tied to note interactions.
 * <p>
 * This class also includes lifecycle methods such as onCreateView and onDestroyView
 * to manage view-related operations, cleaning up resources and binding references when necessary.
 * <p>
 * Implements:
 * - HarpView: For interacting with the harp's data model and updating the UI.
 * - FragmentView: For integrating with ViewModel to manage fragment-specific state and behaviors.
 */
public class HarpFragment extends Fragment implements HarpView, FragmentView {


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
     * Copies the text from the original note and displays it in the overlay view.
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

        // Make the overlay note visible
        overlayNote.setVisibility(View.VISIBLE);

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

        // Hide the overlay note view
        overlayNote.setVisibility(View.GONE);

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

        configureOverlayNoteView(overlayNote);
        configureNoteClickListeners(tableLayout, overlayNote);

        FragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(FragmentViewModel.class);
        viewModel.selectFragmentView(this);
    }

    /**
     * Configures the overlay note TextView by adding behavior for hiding the view
     * when it is clicked. This method also ensures the overlay note is hidden initially.
     *
     * @param overlayNote The TextView used to display the overlay view for enlarged notes.
     */
    private void configureOverlayNoteView(TextView overlayNote) {
        hideEnlargedTextView(overlayNote);
        overlayNote.setOnClickListener(v -> hideEnlargedTextView(overlayNote));
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
        Set<Integer> channelIds = Set.of(R.id.channel_1, R.id.channel_2, R.id.channel_3, R.id.channel_4, R.id.channel_5, R.id.channel_6, R.id.channel_7, R.id.channel_8, R.id.channel_9, R.id.channel_10);
        return channelIds.contains(viewId);
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
        textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.note_overblowoverdraw, requireContext().getTheme()));
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
        LayerDrawable layout = (LayerDrawable) textView.getBackground();
        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
        line.setAlpha(0); // hide line
    }


    /**
     * Retrieves the TextView corresponding to the specified channel and note.
     *
     * @param channel The channel number from which to retrieve the note (1-based index).
     * @param note    The position of the note within the channel (-3 to 4, where 0 is the central note position).
     * @return The TextView that represents the specified note in the specified channel.
     */
    private TextView getNote(int channel, int note) {
        TextView[][] textViews = new TextView[10][8];
        textViews[0][0] = binding.channel1NoteM3;
        textViews[0][1] = binding.channel1NoteM2;
        textViews[0][2] = binding.channel1NoteM1;
        textViews[0][3] = binding.channel1Note0;
        textViews[0][4] = binding.channel1Note1;
        textViews[0][5] = binding.channel1Note2;
        textViews[0][6] = binding.channel1Note3;
        textViews[0][7] = binding.channel1Note4;

        textViews[1][0] = binding.channel2NoteM3;
        textViews[1][1] = binding.channel2NoteM2;
        textViews[1][2] = binding.channel2NoteM1;
        textViews[1][3] = binding.channel2Note0;
        textViews[1][4] = binding.channel2Note1;
        textViews[1][5] = binding.channel2Note2;
        textViews[1][6] = binding.channel2Note3;
        textViews[1][7] = binding.channel2Note4;

        textViews[2][0] = binding.channel3NoteM3;
        textViews[2][1] = binding.channel3NoteM2;
        textViews[2][2] = binding.channel3NoteM1;
        textViews[2][3] = binding.channel3Note0;
        textViews[2][4] = binding.channel3Note1;
        textViews[2][5] = binding.channel3Note2;
        textViews[2][6] = binding.channel3Note3;
        textViews[2][7] = binding.channel3Note4;

        textViews[3][0] = binding.channel4NoteM3;
        textViews[3][1] = binding.channel4NoteM2;
        textViews[3][2] = binding.channel4NoteM1;
        textViews[3][3] = binding.channel4Note0;
        textViews[3][4] = binding.channel4Note1;
        textViews[3][5] = binding.channel4Note2;
        textViews[3][6] = binding.channel4Note3;
        textViews[3][7] = binding.channel4Note4;

        textViews[4][0] = binding.channel5NoteM3;
        textViews[4][1] = binding.channel5NoteM2;
        textViews[4][2] = binding.channel5NoteM1;
        textViews[4][3] = binding.channel5Note0;
        textViews[4][4] = binding.channel5Note1;
        textViews[4][5] = binding.channel5Note2;
        textViews[4][6] = binding.channel5Note3;
        textViews[4][7] = binding.channel5Note4;

        textViews[5][0] = binding.channel6NoteM3;
        textViews[5][1] = binding.channel6NoteM2;
        textViews[5][2] = binding.channel6NoteM1;
        textViews[5][3] = binding.channel6Note0;
        textViews[5][4] = binding.channel6Note1;
        textViews[5][5] = binding.channel6Note2;
        textViews[5][6] = binding.channel6Note3;
        textViews[5][7] = binding.channel6Note4;

        textViews[6][0] = binding.channel7NoteM3;
        textViews[6][1] = binding.channel7NoteM2;
        textViews[6][2] = binding.channel7NoteM1;
        textViews[6][3] = binding.channel7Note0;
        textViews[6][4] = binding.channel7Note1;
        textViews[6][5] = binding.channel7Note2;
        textViews[6][6] = binding.channel7Note3;
        textViews[6][7] = binding.channel7Note4;

        textViews[7][0] = binding.channel8NoteM3;
        textViews[7][1] = binding.channel8NoteM2;
        textViews[7][2] = binding.channel8NoteM1;
        textViews[7][3] = binding.channel8Note0;
        textViews[7][4] = binding.channel8Note1;
        textViews[7][5] = binding.channel8Note2;
        textViews[7][6] = binding.channel8Note3;
        textViews[7][7] = binding.channel8Note4;

        textViews[8][0] = binding.channel9NoteM3;
        textViews[8][1] = binding.channel9NoteM2;
        textViews[8][2] = binding.channel9NoteM1;
        textViews[8][3] = binding.channel9Note0;
        textViews[8][4] = binding.channel9Note1;
        textViews[8][5] = binding.channel9Note2;
        textViews[8][6] = binding.channel9Note3;
        textViews[8][7] = binding.channel9Note4;

        textViews[9][0] = binding.channel10NoteM3;
        textViews[9][1] = binding.channel10NoteM2;
        textViews[9][2] = binding.channel10NoteM1;
        textViews[9][3] = binding.channel10Note0;
        textViews[9][4] = binding.channel10Note1;
        textViews[9][5] = binding.channel10Note2;
        textViews[9][6] = binding.channel10Note3;
        textViews[9][7] = binding.channel10Note4;

        return textViews[channel - 1][note + 3];
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