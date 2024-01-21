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
import de.schliweb.bluesharpbendingapp.controller.Note;
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
    public void initNotes(Note[] notes) {
        for (Note note : notes) {
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
        TextView textView = null;
        if (channel == 1) {
            switch (note) {
                case -3:
                    textView = binding.channel1NoteM3;
                    break;
                case -2:
                    textView = binding.channel1NoteM2;
                    break;
                case -1:
                    textView = binding.channel1NoteM1;
                    break;
                case 0:
                    textView = binding.channel1Note0;
                    break;
                case 1:
                    textView = binding.channel1Note1;
                    break;
                case 2:
                    textView = binding.channel1Note2;
                    break;
                case 3:
                    textView = binding.channel1Note3;
                    break;
                case 4:
                    textView = binding.channel1Note4;
                    break;
            }
        }
        if (channel == 2) {
            switch (note) {
                case -3:
                    textView = binding.channel2NoteM3;
                    break;
                case -2:
                    textView = binding.channel2NoteM2;
                    break;
                case -1:
                    textView = binding.channel2NoteM1;
                    break;
                case 0:
                    textView = binding.channel2Note0;
                    break;
                case 1:
                    textView = binding.channel2Note1;
                    break;
                case 2:
                    textView = binding.channel2Note2;
                    break;
                case 3:
                    textView = binding.channel2Note3;
                    break;
                case 4:
                    textView = binding.channel2Note4;
                    break;
            }
        }
        if (channel == 3) {
            switch (note) {
                case -3:
                    textView = binding.channel3NoteM3;
                    break;
                case -2:
                    textView = binding.channel3NoteM2;
                    break;
                case -1:
                    textView = binding.channel3NoteM1;
                    break;
                case 0:
                    textView = binding.channel3Note0;
                    break;
                case 1:
                    textView = binding.channel3Note1;
                    break;
                case 2:
                    textView = binding.channel3Note2;
                    break;
                case 3:
                    textView = binding.channel3Note3;
                    break;
                case 4:
                    textView = binding.channel3Note4;
                    break;
            }
        }
        if (channel == 4) {
            switch (note) {
                case -3:
                    textView = binding.channel4NoteM3;
                    break;
                case -2:
                    textView = binding.channel4NoteM2;
                    break;
                case -1:
                    textView = binding.channel4NoteM1;
                    break;
                case 0:
                    textView = binding.channel4Note0;
                    break;
                case 1:
                    textView = binding.channel4Note1;
                    break;
                case 2:
                    textView = binding.channel4Note2;
                    break;
                case 3:
                    textView = binding.channel4Note3;
                    break;
                case 4:
                    textView = binding.channel4Note4;
                    break;
            }
        }
        if (channel == 5) {
            switch (note) {
                case -3:
                    textView = binding.channel5NoteM3;
                    break;
                case -2:
                    textView = binding.channel5NoteM2;
                    break;
                case -1:
                    textView = binding.channel5NoteM1;
                    break;
                case 0:
                    textView = binding.channel5Note0;
                    break;
                case 1:
                    textView = binding.channel5Note1;
                    break;
                case 2:
                    textView = binding.channel5Note2;
                    break;
                case 3:
                    textView = binding.channel5Note3;
                    break;
                case 4:
                    textView = binding.channel5Note4;
                    break;
            }
        }
        if (channel == 6) {
            switch (note) {
                case -3:
                    textView = binding.channel6NoteM3;
                    break;
                case -2:
                    textView = binding.channel6NoteM2;
                    break;
                case -1:
                    textView = binding.channel6NoteM1;
                    break;
                case 0:
                    textView = binding.channel6Note0;
                    break;
                case 1:
                    textView = binding.channel6Note1;
                    break;
                case 2:
                    textView = binding.channel6Note2;
                    break;
                case 3:
                    textView = binding.channel6Note3;
                    break;
                case 4:
                    textView = binding.channel6Note4;
                    break;
            }
        }
        if (channel == 7) {
            switch (note) {
                case -3:
                    textView = binding.channel7NoteM3;
                    break;
                case -2:
                    textView = binding.channel7NoteM2;
                    break;
                case -1:
                    textView = binding.channel7NoteM1;
                    break;
                case 0:
                    textView = binding.channel7Note0;
                    break;
                case 1:
                    textView = binding.channel7Note1;
                    break;
                case 2:
                    textView = binding.channel7Note2;
                    break;
                case 3:
                    textView = binding.channel7Note3;
                    break;
                case 4:
                    textView = binding.channel7Note4;
                    break;
            }
        }
        if (channel == 8) {
            switch (note) {
                case -3:
                    textView = binding.channel8NoteM3;
                    break;
                case -2:
                    textView = binding.channel8NoteM2;
                    break;
                case -1:
                    textView = binding.channel8NoteM1;
                    break;
                case 0:
                    textView = binding.channel8Note0;
                    break;
                case 1:
                    textView = binding.channel8Note1;
                    break;
                case 2:
                    textView = binding.channel8Note2;
                    break;
                case 3:
                    textView = binding.channel8Note3;
                    break;
                case 4:
                    textView = binding.channel8Note4;
                    break;
            }
        }
        if (channel == 9) {
            switch (note) {
                case -3:
                    textView = binding.channel9NoteM3;
                    break;
                case -2:
                    textView = binding.channel9NoteM2;
                    break;
                case -1:
                    textView = binding.channel9NoteM1;
                    break;
                case 0:
                    textView = binding.channel9Note0;
                    break;
                case 1:
                    textView = binding.channel9Note1;
                    break;
                case 2:
                    textView = binding.channel9Note2;
                    break;
                case 3:
                    textView = binding.channel9Note3;
                    break;
                case 4:
                    textView = binding.channel9Note4;
                    break;
            }
        }
        if (channel == 10) {
            switch (note) {
                case -3:
                    textView = binding.channel10NoteM3;
                    break;
                case -2:
                    textView = binding.channel10NoteM2;
                    break;
                case -1:
                    textView = binding.channel10NoteM1;
                    break;
                case 0:
                    textView = binding.channel10Note0;
                    break;
                case 1:
                    textView = binding.channel10Note1;
                    break;
                case 2:
                    textView = binding.channel10Note2;
                    break;
                case 3:
                    textView = binding.channel10Note3;
                    break;
                case 4:
                    textView = binding.channel10Note4;
                    break;
            }
        }
        return textView;
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