package de.schliweb.bluesharpbendingapp.view.android;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;

public class TunerFragment extends Fragment implements MicrophoneHandler, FragmentView {

    private TextView noteLabel;
    private TextView frequencyLabel;
    private TextView centsValueLabel;
    private TextView tuningStatus;
    private View tuningMeterNeedle;
    private Button startStopButton;
    private boolean isTuning = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tuner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        noteLabel = view.findViewById(R.id.note_label);
        frequencyLabel = view.findViewById(R.id.frequency_label);
        centsValueLabel = view.findViewById(R.id.cents_value);
        tuningStatus = view.findViewById(R.id.tuning_status);
        tuningMeterNeedle = view.findViewById(R.id.tuning_meter_needle);
        startStopButton = view.findViewById(R.id.start_stop_button);

        // Set up button click listener
        startStopButton.setOnClickListener(v -> {
            if (isTuning) {
                stopTuning();
                startStopButton.setText(getString(R.string.tuner_start_button));
                tuningStatus.setText(getString(R.string.tuner_stopped));
            } else {
                startTuning();
                startStopButton.setText(getString(R.string.tuner_stop_button));
                tuningStatus.setText(getString(R.string.tuner_listening));
            }
        });
    }

    private void startTuning() {
        isTuning = true;
    }

    private void stopTuning() {
        if (isTuning) {
            isTuning = false;
        }
    }

    @Override
    public void handle(double pitch, double volume) {
        if (isValidPitch(pitch)) {
            processPitchData(pitch);
        }
    }

    private boolean isValidPitch(double pitch) {
        return pitch > 0;
    }

    private void processPitchData(double pitch) {
        String note = NoteLookup.getNoteName(pitch);
        double referenceFrequency = NoteLookup.getNoteFrequency(note);
        double cents = NoteUtils.getCents(pitch, referenceFrequency);

        // Update UI on the main thread
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                noteLabel.setText(getString(R.string.tuner_note_format, note));
                frequencyLabel.setText(getString(R.string.tuner_frequency_format, Math.round(pitch)));
                updateTuningMeter(cents);
            });
        }
    }

    private void updateTuningMeter(double cents) {
        // Update cents value display
        centsValueLabel.setText(getString(R.string.tuner_cents_format, Math.round(cents)));

        // Calculate the position of the needle based on cents
        // Cents range from -50 to +50, with 0 being perfectly in tune
        // Normalize to 0-1 range and convert to screen position
        ViewGroup container = (ViewGroup) tuningMeterNeedle.getParent();
        float containerWidth = container.getWidth();
        float normalizedPosition = (float) ((cents + 50) / 100.0); // Convert to 0-1 range
        normalizedPosition = Math.max(0, Math.min(1, normalizedPosition)); // Clamp to 0-1

        // Calculate the absolute position in pixels
        float needleWidth = tuningMeterNeedle.getWidth();
        float leftMargin = normalizedPosition * (containerWidth - needleWidth);

        // Update the needle position
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tuningMeterNeedle.getLayoutParams();
        params.leftMargin = (int) leftMargin;
        tuningMeterNeedle.setLayoutParams(params);

        // Update needle color based on how close to being in tune
        int color;
        if (Math.abs(cents) < 5) {
            color = 0xFF00CC00; // Green
            tuningStatus.setText(getString(R.string.tuner_in_tune));
        } else if (Math.abs(cents) < 15) {
            color = 0xFFCCCC00; // Yellow
            tuningStatus.setText(cents < 0 ? getString(R.string.tuner_slightly_flat) : getString(R.string.tuner_slightly_sharp));
        } else {
            color = 0xFFCC0000; // Red
            tuningStatus.setText(cents < 0 ? getString(R.string.tuner_too_flat) : getString(R.string.tuner_too_sharp));
        }
        tuningMeterNeedle.setBackgroundColor(color);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTuning();
    }

    @Override
    public Object getInstance() {
        return this;
    }
}
