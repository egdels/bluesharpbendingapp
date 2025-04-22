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
import de.schliweb.bluesharpbendingapp.controller.AndroidSettingsHandler;
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.databinding.FragmentSettingsBinding;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.view.AndroidSettingsView;
import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;
import lombok.Setter;



/**
 * SettingsFragment is responsible for managing the UI and interactions related to the app settings.
 * It implements various interfaces to handle specific settings functionalities, such as harp settings,
 * microphone settings, and note settings. This fragment uses view binding to interact with the UI components
 * and manages spinners for selecting options like concert pitch, algorithm, confidence, and more.
 * <p>
 * This class interacts with handlers for managing specific actions in the settings view, such as handling
 * key, tune, and algorithm selections. It also initializes settings state based on stored values and updates
 * corresponding UI elements accordingly.
 * <p>
 * Responsibilities:
 * - Loading and initializing data into spinner components for various settings.
 * - Handling user interactions (e.g., selections, button clicks) within the settings fragment.
 * - Synchronizing selected values with their respective handlers and managing the app's state.
 * - Configuring the lock screen setting as per stored configurations.
 * <p>
 * The fragment is tied to its associated ViewModel for fragment selection and relies on external handlers to
 * perform updates or operations on the selected settings.
 * <p>
 * Lifecycle methods:
 * - onCreateView: Inflates the fragment layout with data binding.
 * - onViewCreated: Sets up ViewModel, resets settings on button click, initializes spinners, and links handlers to user actions.
 * - onDestroyView: Cleans up references to the binding when the view is destroyed.
 * <p>
 * Implements functionalities for:
 * - Harp settings: Keys and tunes selection.
 * - Microphone settings: Algorithms and confidences selection.
 * - Note settings: Concert pitch management.
 * - General functionality: Screen lock control and instance retrieval.
 */
public class SettingsFragment extends Fragment implements HarpSettingsView, MicrophoneSettingsView, FragmentView, NoteSettingsView, AndroidSettingsView {


    /**
     * A protected field that represents the binding object for the settings fragment.
     * This binding provides access to the views defined in the associated layout file,
     * enabling interaction and data manipulation for UI components.
     */
    protected FragmentSettingsBinding binding;

    /**
     * Represents a handler for managing and interacting with harp settings within the settings view of the application.
     * This variable encapsulates logic associated with configuring and displaying specific harp-related settings.
     * It is part of the SettingsFragment class and works in conjunction with other settings handlers to provide a
     * comprehensive configuration interface.
     */
    @Setter
    private HarpSettingsViewHandler harpSettingsViewHandler;

    /**
     * A handler for managing the view and interactions related to microphone settings in the user interface.
     * This variable is used to interface with and control the microphone-specific settings available in the application.
     */
    @Setter
    private MicrophoneSettingsViewHandler microphoneSettingsViewHandler;

    /**
     * Handles the view logic and behaviors related to note settings within the SettingsFragment.
     * This variable is used to manage note-specific configuration options
     * and interactions in the user interface for customizing application behavior.
     */
    @Setter
    private NoteSettingsViewHandler noteSettingsViewHandler;


    /**
     * A handler for managing Android-specific settings within the SettingsFragment.
     * This variable is responsible for controlling and configuring Android settings
     * view behavior and interactions in the user interface. It is expected to
     * integrate with other setting handlers and manage Android-specific
     * configuration options.
     */
    @Setter
    private AndroidSettingsHandler androidSettingsViewHandler;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    /**
     * Called after the fragment's view has been created. This method initializes the ViewModel
     * and sets up the user interface functionality for button clicks and other actions.
     *
     * @param view               The view returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a
     *                           previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(FragmentViewModel.class);
        viewModel.selectFragmentView(this);

        binding.settingsResetButton.setOnClickListener(v -> {
            MainModel model = new MainModel();
            setSelectedConcertPitch(model.getStoredConcertPitchIndex());
            setSelectedKey(model.getStoredKeyIndex());
            setSelectedTune(model.getStoredTuneIndex());
            setSelectedAlgorithm(model.getStoredAlgorithmIndex());
            setSelectedConfidence(model.getStoredConfidenceIndex());
            setSelectedLockScreen(model.getStoredLockScreenIndex());
        });
        binding.settingsScreenlock.setOnClickListener(v -> androidSettingsViewHandler.handleLockScreenSelection(binding.settingsScreenlock.isChecked() ? 1 : 0));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void setKeys(String[] keys) {
        // Safeguard in case keys is null
        if (keys == null) {
            keys = new String[0]; // Use an empty array
        }
        Spinner spinner = binding.settingsKeyList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, keys);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                harpSettingsViewHandler.handleKeySelection((int) id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // no need
            }
        });

    }


    @Override
    public void setSelectedKey(int i) {
        Spinner spinner = binding.settingsKeyList;
        spinner.setSelection(i);
    }


    @Override
    public void setSelectedTune(int i) {
        Spinner spinner = binding.settingsTuneList;
        spinner.setSelection(i);
    }


    @Override
    public void setTunes(String[] tunes) {
        // Safeguard in case keys is null
        if (tunes == null) {
            tunes = new String[0]; // Use an empty array
        }
        Spinner spinner = binding.settingsTuneList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, tunes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                harpSettingsViewHandler.handleTuneSelection((int) id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // no need
            }
        });

    }


    @Override
    public void setAlgorithms(String[] algorithms) {
        // Safeguard in case algorithms is null
        if (algorithms == null) {
            algorithms = new String[0]; // Use an empty array
        }
        Spinner spinner = binding.settingsAlgoList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, algorithms);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                microphoneSettingsViewHandler.handleAlgorithmSelection(((int) id));
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // no need
            }
        });
    }


    @Override
    public void setFrequency(double frequency) {
        // on android do nothing
    }

    @Override
    public void setSelectedConfidence(int i) {
        Spinner spinner = binding.settingsConfList;
        spinner.setSelection(i);
    }

    @Override
    public void setConfidences(String[] confidences) {
        // Safeguard in case keys is null
        if (confidences == null) {
            confidences = new String[0]; // Use an empty array
        }
        Spinner spinner = binding.settingsConfList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, confidences);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                microphoneSettingsViewHandler.handleConfidenceSelection((int) id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // no need
            }
        });

    }


    @Override
    public void setMicrophones(String[] microphones) {
        // no need on android
    }


    @Override
    public void setSelectedAlgorithm(int i) {
        Spinner spinner = binding.settingsAlgoList;
        spinner.setSelection(i);
    }


    @Override
    public void setSelectedMicrophone(int i) {
        // no need on android
    }


    @Override
    public void setVolume(double volume) {
        // on android do nothing
    }


    @Override
    public Object getInstance() {
        return this;
    }


    @Override
    public void setConcertPitches(String[] pitches) {
        // Safeguard in case pitches is null
        if (pitches == null) {
            pitches = new String[0]; // Use an empty array
        }
        Spinner spinner = binding.settingsPitchesList;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_list, pitches);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noteSettingsViewHandler.handleConcertPitchSelection(((int) id));
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // no need
            }
        });

    }


    @Override
    public void setSelectedConcertPitch(int i) {
        Spinner spinner = binding.settingsPitchesList;
        spinner.setSelection(i);
    }

    @Override
    public void setSelectedLockScreen(int selectedLockScreenIndex) {
        binding.settingsScreenlock.setChecked(selectedLockScreenIndex > 0);
    }
}