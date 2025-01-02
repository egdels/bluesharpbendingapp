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

/**
 * The type Training fragment.
 */
public class TrainingFragment extends Fragment implements TrainingView, FragmentView {

    /**
     * The Binding.
     */
    private FragmentTrainingBinding binding;
    /**
     * The Training view handler.
     */
    private TrainingViewHandler trainingViewHandler;

    /**
     * On create view view.
     *
     * @param inflater           the inflater
     * @param container          the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(@NonNull
                             LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {

        binding = FragmentTrainingBinding.inflate(inflater, container, false);
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

        binding.trainingStartButton.setOnClickListener(v -> toggleButton());

    }

    /**
     * Sets trainings.
     *
     * @param trainings the trainings
     */
    @Override
    public void setTrainings(String[] trainings) {
        Spinner spinner = binding.trainingTrainingsList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, trainings);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        trainingViewHandler.handleTrainingSelection((int) id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // no need
                    }
                }
        );
    }

    /**
     * Sets precisions.
     *
     * @param precisions the precisions
     */
    @Override
    public void setPrecisions(String[] precisions) {
        Spinner spinner = binding.trainingPrecisionList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, precisions);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        trainingViewHandler.handlePrecisionSelection((int) id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        // no need
                    }
                }
        );
    }

    /**
     * Sets selected training.
     *
     * @param i the
     */
    @Override
    public void setSelectedTraining(int i) {
        Spinner spinner = binding.trainingTrainingsList;
        spinner.setSelection(i);
    }

    /**
     * Sets selected precision.
     *
     * @param i the
     */
    @Override
    public void setSelectedPrecision(int i) {
        Spinner spinner = binding.trainingPrecisionList;
        spinner.setSelection(i);
    }

    /**
     * Gets actual harp view element.
     *
     * @return the actual harp view element
     */
    @Override
    public HarpViewNoteElement getActualHarpViewElement() {
        return HarpViewNoteElementAndroid.getInstance(binding.trainingNote);
    }

    /**
     * Init training container.
     *
     * @param trainingContainer the training container
     */
    @Override
    public void initTrainingContainer(TrainingContainer trainingContainer) {
        TextView textView = binding.trainingNote;
        textView.setText(trainingContainer.getActualNoteName() != null ? trainingContainer.getActualNoteName() : "");
        textView.setVisibility(View.VISIBLE);
        LayerDrawable layout = (LayerDrawable) textView.getBackground();
        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
        line.setAlpha(0); // hide line
        binding.progressBar.setProgress(trainingContainer.getProgress());
        HarpViewNoteElementAndroid.getInstance(textView).clear();
    }

    /**
     * Toggle button.
     */
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
     * On destroy view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Gets training view handler.
     *
     * @return the training view handler
     */
    public TrainingViewHandler getTrainingViewHandler() {
        return this.trainingViewHandler;
    }

    /**
     * Sets training view handler.
     *
     * @param trainingViewHandler the training view handler
     */
    public void setTrainingViewHandler(TrainingViewHandler trainingViewHandler) {
        this.trainingViewHandler = trainingViewHandler;
    }
}
