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


/**
 * The type Harp fragment.
 */
public class HarpFragment extends Fragment implements HarpView, FragmentView {

    /**
     * The Binding.
     */
    private FragmentHarpBinding binding;
    /**
     * The Harp view handler.
     */
    private HarpViewHandler harpViewHandler;

    /**
     * On create view view.
     *
     * @param inflater           the inflater
     * @param container          the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHarpBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /**
     * On view created.
     *
     * @param view               the view
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(FragmentViewModel.class);
        viewModel.selectFragmentView(this);

    }

    /**
     * On destroy view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Init notes.
     *
     * @param notes the notes
     */
    @Override
    public void initNotes(NoteContainer[] notes) {
        for (NoteContainer note : notes) {
            initNote(note.getChannel(), note.getNote(), note.getNoteName());
            if (note.isOverblow() || note.isOverdraw()) {
                setNoteColor(note.getChannel(), note.getNote());
            }
        }
    }

    /**
     * Sets note color.
     *
     * @param channel the channel
     * @param note    the note
     */
    private void setNoteColor(int channel, int note) {
        TextView textView = getNote(channel, note);
        textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.note_overblowoverdraw, requireContext().getTheme()));
    }

    /**
     * Init note.
     *
     * @param channel  the channel
     * @param note     the note
     * @param noteName the note name
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
     * Gets note.
     *
     * @param channel the channel
     * @param note    the note
     * @return the note
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


    /**
     * Gets instance.
     *
     * @return the instance
     */
    @Override
    public Object getInstance() {
        return this;
    }

    /**
     * Gets harp view handler.
     *
     * @return the harp view handler
     */
    public HarpViewHandler getHarpViewHandler() {
        return this.harpViewHandler;
    }

    /**
     * Sets harp view handler.
     *
     * @param harpViewHandler the harp view handler
     */
    public void setHarpViewHandler(HarpViewHandler harpViewHandler) {
        this.harpViewHandler = harpViewHandler;
    }

    /**
     * Gets harp view element.
     *
     * @param channel the channel
     * @param note    the note
     * @return the harp view element
     */
    @Override
    public HarpViewNoteElement getHarpViewElement(int channel, int note) {
        return new HarpViewNoteElementAndroid(getNote(channel, note), getActivity());
    }
}