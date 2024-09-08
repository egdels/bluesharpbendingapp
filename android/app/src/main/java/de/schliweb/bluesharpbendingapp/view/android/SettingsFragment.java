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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.databinding.FragmentSettingsBinding;
import de.schliweb.bluesharpbendingapp.model.AndroidModel;

import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;

/**
 * The type Settings fragment.
 */
public class SettingsFragment extends Fragment implements HarpSettingsView, MicrophoneSettingsView, FragmentView, NoteSettingsView {

    /**
     * The Binding.
     */
    private FragmentSettingsBinding binding;
    /**
     * The Harp settings view handler.
     */
    private HarpSettingsViewHandler harpSettingsViewHandler;
    /**
     * The Microphone settings view handler.
     */
    private MicrophoneSettingsViewHandler microphoneSettingsViewHandler;

    /**
     * The Note settings view handler.
     */
    private NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * The Android settings view handler.
     */
    private AndroidSettingsHandler androidSettingsViewHandler;

    /**
     * Gets harp settings view handler.
     *
     * @return the harp settings view handler
     */
    public HarpSettingsViewHandler getHarpSettingsViewHandler() {
        return harpSettingsViewHandler;
    }

    /**
     * Sets harp settings view handler.
     *
     * @param harpSettingsViewHandler the harp settings view handler
     */
    public void setHarpSettingsViewHandler(HarpSettingsViewHandler harpSettingsViewHandler) {
        this.harpSettingsViewHandler = harpSettingsViewHandler;
    }

    /**
     * Gets microphone settings view handler.
     *
     * @return the microphone settings view handler
     */
    public MicrophoneSettingsViewHandler getMicrophoneSettingsViewHandler() {
        return microphoneSettingsViewHandler;
    }

    /**
     * Sets microphone settings view handler.
     *
     * @param microphoneSettingsViewHandler the microphone settings view handler
     */
    public void setMicrophoneSettingsViewHandler(MicrophoneSettingsViewHandler microphoneSettingsViewHandler) {
        this.microphoneSettingsViewHandler = microphoneSettingsViewHandler;
    }

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

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
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

        binding.settingsResetButton.setOnClickListener(v -> {
            AndroidModel model = new AndroidModel();
            setSelectedConcertPitch(model.getStoredConcertPitchIndex());
            setSelectedKey(model.getStoredKeyIndex());
            setSelectedTune(model.getStoredTuneIndex());
            setSelectedAlgorithm(model.getStoredAlgorithmIndex());
            initScreenLock(model.getStoredLockScreenIndex());
        });

        binding.settingsScreenlock.setOnClickListener(v -> androidSettingsViewHandler.handleLookScreen(binding.settingsScreenlock.isChecked()));
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
     * Sets keys.
     *
     * @param keys the keys
     */
    @Override
    public void setKeys(String[] keys) {

        Spinner spinner = binding.settingsKeyList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, keys);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        harpSettingsViewHandler.handleKeySelection((int) id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // no need
                    }
                }
        );

    }

    /**
     * Sets selected key.
     *
     * @param i the
     */
    @Override
    public void setSelectedKey(int i) {
        Spinner spinner = binding.settingsKeyList;
        spinner.setSelection(i);
    }

    /**
     * Sets selected tune.
     *
     * @param i the
     */
    @Override
    public void setSelectedTune(int i) {
        Spinner spinner = binding.settingsTuneList;
        spinner.setSelection(i);
    }

    /**
     * Sets tunes.
     *
     * @param tunes the tunes
     */
    @Override
    public void setTunes(String[] tunes) {

        Spinner spinner = binding.settingsTuneList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, tunes);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        harpSettingsViewHandler.handleTuneSelection((int) id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // no need
                    }
                }
        );

    }

    /**
     * Sets algorithms.
     *
     * @param algorithms the algorithms
     */
    @Override
    public void setAlgorithms(String[] algorithms) {
        Spinner spinner = binding.settingsAlgoList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, algorithms);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        microphoneSettingsViewHandler.handleAlgorithmSelection(((int) id));
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // no need
                    }
                }
        );
    }

    /**
     * Sets frequency.
     *
     * @param frequency the frequency
     */
    @Override
    public void setFrequency(double frequency) {
        // on android do nothing
    }

    /**
     * Sets microphones.
     *
     * @param microphones the microphones
     */
    @Override
    public void setMicrophones(String[] microphones) {
        // no need on android
    }

    /**
     * Sets selected algorithm.
     *
     * @param i the
     */
    @Override
    public void setSelectedAlgorithm(int i) {
        Spinner spinner = binding.settingsAlgoList;
        spinner.setSelection(i);
    }

    /**
     * Sets selected microphone.
     *
     * @param i the
     */
    @Override
    public void setSelectedMicrophone(int i) {
        // no need on android
    }

    /**
     * Sets volume.
     *
     * @param volume the volume
     */
    @Override
    public void setVolume(double volume) {
        // on android do nothing
    }

    /**
     * Sets probability.
     *
     * @param v the v
     */
    @Override
    public void setProbability(double v) {
        // on android do nothing
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
     * Sets concert pitches.
     *
     * @param pitches the pitches
     */
    @Override
    public void setConcertPitches(String[] pitches) {

        Spinner spinner = binding.settingsPitchesList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, pitches);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        noteSettingsViewHandler.handleConcertPitchSelection(((int) id));
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // no need
                    }
                }
        );

    }

    /**
     * Sets selected concert pitch.
     *
     * @param i the
     */
    @Override
    public void setSelectedConcertPitch(int i) {
        Spinner spinner = binding.settingsPitchesList;
        spinner.setSelection(i);
    }

    /**
     * Gets note settings view handler.
     *
     * @return the note settings view handler
     */
    public NoteSettingsViewHandler getNoteSettingsViewHandler() {
        return noteSettingsViewHandler;
    }

    /**
     * Sets note settings view handler.
     *
     * @param noteSettingsViewHandler the note settings view handler
     */
    public void setNoteSettingsViewHandler(NoteSettingsViewHandler noteSettingsViewHandler) {
        this.noteSettingsViewHandler = noteSettingsViewHandler;
    }

    /**
     * Sets android settings handler.
     *
     * @param androidSettingsHandler the android settings handler
     */
    public void setAndroidSettingsHandler(AndroidSettingsHandler androidSettingsHandler) {
        this.androidSettingsViewHandler = androidSettingsHandler;
    }

    /**
     * Init screen lock.
     *
     * @param storedLockScreenIndex the stored lock screen index
     */
    public void initScreenLock(int storedLockScreenIndex) {
        binding.settingsScreenlock.setChecked(false);
        if (storedLockScreenIndex > 0) {
            binding.settingsScreenlock.setChecked(true);
        }
    }
}