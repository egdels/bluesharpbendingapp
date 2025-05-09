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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.TrainingContainer;
import de.schliweb.bluesharpbendingapp.controller.TrainingViewHandler;
import de.schliweb.bluesharpbendingapp.databinding.FragmentTrainingBinding;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import lombok.Setter;


/**
 * This class represents the TrainingFragment, which is a fragment implementing the TrainingView
 * and FragmentView interfaces. It provides functionality for handling UI components and user
 * interactions related to training processes.
 * <p>
 * Responsibilities:
 * - Initializes and manages the fragment view using data bindings.
 * - Implements methods from TrainingView to handle setting and updating UI components such as
 * spinners and progress indicators.
 * - Facilitates interaction with the training view handler to manage training-related actions.
 * - Provides lifecycle support for initialization and cleanup of the fragment.
 */
public class TrainingFragment extends Fragment implements TrainingView, FragmentView {


    /**
     * Binding object for the TrainingFragment, generated by View Binding.
     * This variable provides direct access to the UI components in the associated XML layout.
     * It is used to interact with and manipulate the views within the fragment.
     * The binding should be initialized in onCreateView and cleared in onDestroyView
     * to avoid memory leaks.
     */
    private FragmentTrainingBinding binding;

    /**
     * Represents a handler responsible for managing the interactions between the training view
     * and the underlying logic in the TrainingFragment. This field facilitates communication and
     * operation control within the training workflow, including starting and stopping of trainings.
     * <p>
     * This variable is used in methods that control the training state, such as toggling the training
     * start/stop button and other related functionalities.
     */
    @Setter
    private TrainingViewHandler trainingViewHandler;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTrainingBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    /**
     * Called after the fragment's view has been created.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(FragmentViewModel.class);
        viewModel.selectFragmentView(this);

        binding.trainingStartButton.setOnClickListener(v -> toggleButton());

    }


    @Override
    public void setTrainings(String[] trainings) {
        Spinner spinner = binding.trainingTrainingsList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, trainings);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trainingViewHandler.handleTrainingSelection((int) id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // no need
            }
        });
    }


    @Override
    public void setPrecisions(String[] precisions) {
        Spinner spinner = binding.trainingPrecisionList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, precisions);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trainingViewHandler.handlePrecisionSelection((int) id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // no need
            }
        });
    }


    @Override
    public void setSelectedTraining(int i) {
        Spinner spinner = binding.trainingTrainingsList;
        spinner.setSelection(i);
    }


    @Override
    public void setSelectedPrecision(int i) {
        Spinner spinner = binding.trainingPrecisionList;
        spinner.setSelection(i);
    }


    @Override
    public HarpViewNoteElement getActualHarpViewElement() {
        return TrainingViewNoteElementAndroid.getInstance(binding.trainingNote);
    }


    @Override
    public void initTrainingContainer(TrainingContainer trainingContainer) {
        TextView textView = binding.trainingNote;
        // Set auto-size with min and max text sizes to ensure readability
        textView.setAutoSizeTextTypeUniformWithConfiguration(
                16, // Min text size in SP
                100, // Max text size in SP
                2, // Step granularity in SP
                TypedValue.COMPLEX_UNIT_SP // Unit of measurement
        );
        textView.setVisibility(View.VISIBLE);
        LayerDrawable layout = (LayerDrawable) textView.getBackground();
        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
        line.setAlpha(0); // hide line
        binding.progressBar.setProgress(trainingContainer.getProgress());
        TrainingViewNoteElementAndroid.getInstance(textView).setNoteName(trainingContainer.getActualNoteName() != null ? trainingContainer.getActualNoteName() : "");
        TrainingViewNoteElementAndroid.getInstance(textView).clear();
    }


    @Override
    public void toggleButton() {
        String buttonText = binding.trainingStartButton.getText().toString();
        if (buttonText.equals(getString(R.string.training_start_button))) {
            trainingViewHandler.handleTrainingStart();
            binding.trainingStartButton.setText(R.string.training_stop_button);
        }
        if (buttonText.equals(getString(R.string.training_stop_button))) {
            trainingViewHandler.handleTrainingStop();
            binding.trainingStartButton.setText(R.string.training_start_button);
        }
    }


    @Override
    public Object getInstance() {
        return this;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
