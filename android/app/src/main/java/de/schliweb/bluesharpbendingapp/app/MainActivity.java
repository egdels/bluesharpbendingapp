package de.schliweb.bluesharpbendingapp.app;
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

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.HarpViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MainController;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.TrainingViewHandler;
import de.schliweb.bluesharpbendingapp.databinding.ActivityMainBinding;
import de.schliweb.bluesharpbendingapp.model.AndroidModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.mircophone.android.MicrophoneAndroid;
import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import de.schliweb.bluesharpbendingapp.view.android.AboutFragment;
import de.schliweb.bluesharpbendingapp.view.android.AndroidSettingsHandler;
import de.schliweb.bluesharpbendingapp.view.android.FragmentView;
import de.schliweb.bluesharpbendingapp.view.android.FragmentViewModel;
import de.schliweb.bluesharpbendingapp.view.android.HarpFragment;
import de.schliweb.bluesharpbendingapp.view.android.SettingsFragment;
import de.schliweb.bluesharpbendingapp.view.android.TrainingFragment;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * MainActivity is the primary Activity for the application. It handles the main business logic and user interface
 * interactions. This class extends AppCompatActivity and implements interfaces MainWindow and AndroidSettingsHandler
 * for enhanced functionality.
 * <p>
 * Responsibilities:
 * - Manages and initializes necessary components such as microphone, model, and controllers.
 * - Handles Android activity lifecycle events and permissions.
 * - Provides fragment navigation using Navigation Component.
 * - Configures and handles behavior for both app bar visibility and screen locking.
 * - Persists and restores the application's model state across sessions.
 * <p>
 * Key Functionalities:
 * - Permission Request: Ensures proper handling of RECORD_AUDIO permission for microphone functionality.
 * - Fragment Interaction: Observes and configures behaviors for various fragments such as SettingsFragment,
 *   HarpFragment, AboutFragment, and TrainingFragment.
 * - Model Persistence: Reads and saves the application model state to local storage.
 * - Training, Settings, and UI Management: Provides specific handlers for UI elements like training views,
 *   settings, and other configuration parameters.
 * - App Bar Control: Provides methods to dynamically show and hide the app bar based on user interactions
 *   and conditions.
 * <p>
 * Implements:
 * - MainWindow: Facilitates communication with the Main Controller.
 * - AndroidSettingsHandler: Allows interaction for dynamic settings updates specific to Android requirements.
 * <p>
 * Activity Lifecycle:
 * - onCreate(Bundle savedInstanceState): Initializes the Activity and sets up the components.
 * - onOptionsItemSelected(MenuItem item): Handles user selections from the options menu and navigates to
 *   respective fragments or functionalities.
 * - onTouchEvent(MotionEvent e): Handles touch interactions, toggling app bar visibility under certain conditions.
 * <p>
 * Permission Validation:
 * - Checks for RECORD_AUDIO permissions required for the app's microphone-based functionalities and requests
 *   them if not granted.
 * <p>
 * Navigation:
 * - Configured using the Navigation UI library to allow smooth transitions between different fragments.
 */
@Slf4j
public class MainActivity extends AppCompatActivity implements MainWindow, AndroidSettingsHandler {


    /**
     * Represents the filename used for storing and retrieving a temporary model file.
     * This file is typically located in the application's cache directory.
     * It is used in methods such as {@code storeModel(AndroidModel model)} and {@code readModel()}
     * to serialize and deserialize the application's model data.
     */
    private static final String TEMP_FILE = "Model.tmp";

    /**
     * Indicates whether the necessary permissions have been granted.
     * This variable is used to track the current state of required
     * permissions within the application, which may include runtime
     * permissions such as camera, microphone, or storage access.
     * <p>
     * Default value is set to false; it is updated based on the
     * outcome of permission requests or checks at runtime.
     */
    private volatile boolean permissionGranted = false;

    /**
     * Configuration object for managing the behavior of the app's AppBar.
     * Ensures proper navigation and UI behavior within the MainActivity.
     * Typically used to define top-level destinations and control navigation up actions.
     */
    private AppBarConfiguration appBarConfiguration;

    /**
     * Represents the currently selected view fragment in the application.
     * This field is used to keep track of the active fragment displayed
     * in the application, enabling specific behaviors and operations
     * depending on the fragment type.
     */
    private FragmentView selectedFragmentView;

    /**
     * Manages the view handler for the Harp settings screen of the application.
     * Controls the interactions and configurations related to the Harp settings UI.
     * Responsible for displaying, updating, and handling user actions within the Harp settings view.
     */
    @Getter
    @Setter
    private HarpSettingsViewHandler harpSettingsViewHandler;

    /**
     * Manages the behavior and representation of the harp view component within the application.
     * This variable is responsible for handling updates, interactions, and rendering related
     * to the harp view, ensuring seamless integration within the user interface.
     */
    @Getter
    @Setter
    private HarpViewHandler harpViewHandler;

    /**
     * Manages the MicrophoneSettingsViewHandler, which handles the user interface
     * and interactions related to the microphone settings within the application.
     * <p>
     * This variable serves as a link between the `MainActivity` and the
     * `MicrophoneSettingsView` component, facilitating operations such as
     * retrieving the microphone settings view, checking its state, and controlling
     * its behavior.
     */
    @Getter
    @Setter
    private MicrophoneSettingsViewHandler microphoneSettingsViewHandler;

    /**
     * Represents the Android model used for managing application state and data.
     * This variable stores an instance of the {@code AndroidModel} class.
     * It is used across various methods of the {@code MainActivity} to persist and retrieve the
     * state of the application, interact with views, and handle application logic.
     */
    private AndroidModel model;

    /**
     * Manages runtime permission requests for a specific permission and handles
     * the result of the request action. This variable is a registered launcher using
     * the ActivityResultContracts.RequestPermission contract.
     * <p>
     * When a permission is granted:
     * - If a microphone instance is available in the model, it is opened for use.
     * <p>
     * When a permission is denied:
     * - Displays an informational dialog to the user about the permission requirement.
     * <p>
     * This variable uses a lambda function to handle the result of the permission request.
     * Updates the `permissionGranted` field based on whether the requested permission
     * was granted or denied.
     */
    protected final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        permissionGranted = isGranted;
        if (!permissionGranted) {
            showPermissionInformation();
        }
        Microphone microphone = model.getMicrophone();
        if (permissionGranted && microphone != null) {
            model.getMicrophone().open();
        }
    });

    /**
     * Indicates whether the application is currently in a paused state.
     * This variable is typically used to manage or track the state of
     * the application's main activity, ensuring appropriate behavior
     * during pause and resume lifecycle events.
     */
    private boolean isPaused = false;

    /**
     * Indicates whether the app bar is currently hidden.
     * <p>
     * This variable is used to track the visibility state of the app bar in the
     * application. It is set to true when the app bar is hidden (e.g., via the
     * {@code hideAppBar()} method) and false when the app bar is visible
     * (e.g., via the {@code unHideAppBar()} method).
     */
    private boolean isAppBarHidden = false;

    /**
     * The NoteSettingsViewHandler instance responsible for managing
     * and handling the behavior and interactions of the note settings
     * view within the application.
     */
    @Getter
    @Setter
    private NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * The TrainingViewHandler instance responsible for managing and handling
     * the training view within the MainActivity. This field provides the
     * necessary functionality to interact with and control elements specific
     * to the training view, such as rendering, lifecycle management, and
     * user inputs.
     */
    @Getter
    @Setter
    private TrainingViewHandler trainingViewHandler;


    /**
     * Saves the provided model to a temporary file in the application's cache directory.
     *
     * @param model The AndroidModel instance to be stored. This is serialized into
     *              a string format and saved to a cache file for later retrieval.
     */
    private void storeModel(AndroidModel model) {

        File directory = this.getApplicationContext().getCacheDir();
        File file = new File(directory, TEMP_FILE);


        try (FileWriter writer = new FileWriter(file)) {
            writer.write(model.getString());
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    /**
     * Reads and restores the AndroidModel instance from a cached file in the application's
     * cache directory. If the file exists and is valid, it is deserialized back into an
     * AndroidModel object. If any error occurs while reading or parsing the file,
     * a new default AndroidModel instance is returned.
     *
     * @return The restored AndroidModel instance if the cache file exists and is valid,
     *         otherwise a new default AndroidModel instance.
     */
    protected AndroidModel readModel() {

        AndroidModel androidModel = new AndroidModel();
        File directory = this.getApplicationContext().getCacheDir();
        File file = new File(directory, TEMP_FILE);

        try (BufferedReader bw = new BufferedReader(new FileReader(file))) {

            String line = bw.readLine();
            if (line != null) androidModel = AndroidModel.createFromString(line);


        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return androidModel;
    }


    /**
     * Initializes the main activity, setting up the user interface, managing permissions,
     * configuring fragments, and initializing application settings.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this bundle contains the most recent data
     *                           supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        model = readModel();

        Microphone microphone = new MicrophoneAndroid();
        model.setMicrophone(microphone);
        model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex()));
        microphone.setConfidence(model.getStoredConfidenceIndex());

        // check permission
        permissionGranted = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (!permissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }

        MainController mainController = new MainController(this, model);

        FragmentViewModel viewModel = new ViewModelProvider(this).get(FragmentViewModel.class);
        viewModel.getSelectedFragmentView().observe(this, item -> {
            // Perform an action with the latest item data.
            this.selectedFragmentView = item;
            if (item.getInstance() instanceof SettingsFragment settingsFragment) {
                settingsFragment.setHarpSettingsViewHandler(getHarpSettingsViewHandler());
                settingsFragment.setMicrophoneSettingsViewHandler(getMicrophoneSettingsViewHandler());
                settingsFragment.setNoteSettingsViewHandler(getNoteSettingsViewHandler());
                settingsFragment.setAndroidSettingsViewHandler(this);

                settingsFragment.getHarpSettingsViewHandler().initTuneList();
                settingsFragment.getHarpSettingsViewHandler().initKeyList();
                settingsFragment.getMicrophoneSettingsViewHandler().initAlgorithmList();
                settingsFragment.getMicrophoneSettingsViewHandler().initMicrophoneList();
                settingsFragment.getMicrophoneSettingsViewHandler().initConfidenceList();
                settingsFragment.getNoteSettingsViewHandler().initConcertPitchList();
                settingsFragment.initScreenLock(model.getStoredLockScreenIndex());

                model.getMicrophone().setMicrophoneHandler(mainController);

            }
            if (item.getInstance() instanceof HarpFragment harpFragment) {
                harpFragment.setHarpViewHandler(getHarpViewHandler());
                harpFragment.getHarpViewHandler().initNotes();
                model.getMicrophone().setMicrophoneHandler(mainController);
            }
            if (item.getInstance() instanceof AboutFragment) {
                model.getMicrophone().setMicrophoneHandler(null);
            }
            if (item.getInstance() instanceof TrainingFragment trainingFragment) {
                trainingFragment.setTrainingViewHandler(getTrainingViewHandler());
                trainingFragment.getTrainingViewHandler().initTrainingContainer();
                trainingFragment.getTrainingViewHandler().initTrainingList();
                trainingFragment.getTrainingViewHandler().initPrecisionList();
                model.getMicrophone().setMicrophoneHandler(mainController);
            }
        });




        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavHostFragment navhostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navhostFragment != null;
        NavController navController = navhostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        hideAppBar();

        handleLookScreen(model.getStoredLockScreenIndex() > 0);

        if (permissionGranted) {
            microphone.open();
        }
    }

    @Override
    public TrainingView getTrainingView() {
        return (TrainingView) selectedFragmentView.getInstance();
    }


    /**
     * Hides the application's primary app bar (ActionBar) if it is currently visible.
     * <p>
     * This method checks if the `ActionBar` instance is not null and, if so, calls
     * the `hide()` method to make it invisible. The internal state of the activity
     * is adjusted by setting the `isAppBarHidden` flag to true to track the visibility
     * of the app bar. This method is typically used to create a more immersive UI experience.
     */
    protected void hideAppBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
            isAppBarHidden = true;
        }
    }


    /**
     * Restores the application's primary app bar (ActionBar) visibility if it is hidden.
     * <p>
     * This method checks if the `ActionBar` instance is not null and, if so, calls
     * the `show()` method to make it visible. The internal state of the activity
     * is updated by setting the `isAppBarHidden` flag to false to reflect the current
     * visibility of the app bar. This method is typically used to revert to a
     * standard UI configuration after an immersive experience.
     */
    private void unHideAppBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.show();
            isAppBarHidden = false;
        }
    }


    /**
     * Handles touch events for the activity. This method toggles the visibility
     * of the app bar based on certain conditions and the type of touch event.
     * Specifically, it checks if the harp view or training view is active, and if the
     * touch event corresponds to the action "MotionEvent.ACTION_UP".
     * <p>
     * If the app bar is currently hidden, it un-hides the app bar. Otherwise, it hides the app bar.
     *
     * @param e The MotionEvent object containing details of the touch event.
     *          This includes information such as the type of action
     *          (e.g., ACTION_DOWN, ACTION_UP), the position of the touch, etc.
     * @return A boolean indicating whether the event was handled.
     *         Always returns true in this implementation.
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if ((isHarpViewActive() || isTrainingViewActive()) && (e.getAction() == MotionEvent.ACTION_UP)) {
            if (isAppBarHidden) {
                unHideAppBar();
            } else {
                hideAppBar();
            }
        }
        return true;
    }


    /**
     * Displays an informational dialog to the user about specific permissions required
     * by the application.
     * <p>
     * The dialog is non-cancelable and includes a message detailing the purpose of
     * permissions, along with a title. It has a single button that allows the user
     * to dismiss the dialog.
     * <p>
     * This method is typically used to inform the user when permissions are critical
     * for the app's functionality and requires their acknowledgment.
     * <p>
     * The dialog's content is defined through predefined string resources for
     * the message, title, and button label.
     */
    private void showPermissionInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(R.string.permission_information_message);

        builder.setTitle(R.string.permission_information_title);

        builder.setCancelable(false);

        builder.setNegativeButton(R.string.permission_information_button_label, (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Handles selection of action bar menu items. Based on the item selected,
     * performs specific actions such as navigation or updating the app state.
     * The method also ensures the current model is saved before handling any navigation.
     *
     * @param item The selected menu item from the action bar. Provides details
     *             about the selected action including its ID.
     * @return A boolean indicating whether the event was successfully handled.
     *         Returns true if the action was processed, otherwise defer to
     *         the superclass implementation.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // onDestroy is not always called, so save also when switching the view
        storeModel(model);

        if (id == R.id.action_settings) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_to_SettingsFragment);
            return true;
        }
        if (id == R.id.action_about) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_to_AboutFragment);
            return true;
        }
        if (id == R.id.action_harp) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_to_HarpFragment);
            return true;
        }
        if (id == R.id.action_training) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_to_TrainingFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Handles the navigation when the "Up" button in the app bar is pressed.
     * This method navigates up in the application's navigation hierarchy. It attempts to
     * handle the navigation by delegating to the NavigationUI utility, passing the
     * NavController and the app's AppBarConfiguration. If the navigation cannot be
     * handled by the NavigationUI, it delegates the navigation behavior to the superclass.
     *
     * @return A boolean indicating whether the navigation event was successfully handled.
     *         Returns true if the navigation was handled, otherwise returns the result
     *         of the superclass implementation.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    public HarpSettingsView getHarpSettingsView() {
        return (HarpSettingsView) selectedFragmentView.getInstance();
    }


    @Override
    public HarpView getHarpView() {
        return (HarpView) selectedFragmentView.getInstance();
    }


    @Override
    public MicrophoneSettingsView getMicrophoneSettingsView() {
        return (MicrophoneSettingsView) selectedFragmentView.getInstance();
    }


    @Override
    public boolean isHarpSettingsViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof SettingsFragment;
    }


    @Override
    public boolean isHarpViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof HarpFragment;
    }


    @Override
    public boolean isMicrophoneSettingsViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof SettingsFragment;
    }


    @Override
    public boolean isTrainingViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof TrainingFragment;
    }


    @Override
    public void open() {
        // no need on android because mainController is not started.
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        storeModel(model);
    }


    @Override
    public boolean isNoteSettingsViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof SettingsFragment;
    }


    @Override
    public NoteSettingsView getNoteSettingsView() {
        return (NoteSettingsView) selectedFragmentView.getInstance();
    }


    /**
     * Called when the activity transitions from the "paused" state to the "resumed" state.
     * This method ensures that the application's microphone functionality is properly
     * restored if certain conditions are met. It is often used to refresh or reinitialize
     * resources that were released during the onPause state.
     * <p>
     * Specifically, if the activity was paused, the necessary permissions have been granted,
     * and the microphone instance is available, the microphone is opened for use.
     * Additionally, the `isPaused` flag is updated to reflect the current state.
     * <p>
     * This method should always call the superclass implementation to ensure
     * proper functioning of the activity lifecycle.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Microphone microphone = model.getMicrophone();
        if (isPaused && permissionGranted && microphone != null) {
            model.getMicrophone().open();
        }
        isPaused = false;
    }


    /**
     * Handles the activity's transition to the "paused" state in the Android lifecycle.
     * <p>
     * This method is called when the activity is temporarily halted, but not yet destroyed.
     * It is used to release resources that should not remain active while the activity
     * is not in the foreground. Specifically:
     * <p>
     * - The `isPaused` flag is set to `true` to reflect the paused state of the activity.
     * - If the microphone permission has been granted and a valid microphone instance exists,
     *   the microphone is closed to release the resource and save battery.
     * <p>
     * Always calls the superclass implementation to ensure proper behavior of the activity's
     * lifecycle.
     */
    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        Microphone microphone = model.getMicrophone();
        if (permissionGranted && microphone != null) {
            model.getMicrophone().close();
        }
    }


    /**
     * Handles the behavior of the screen lock functionality based on the provided flag.
     * <p>
     * This method adjusts internal model state and screen lock behavior.
     * If the screen is to be locked, it ensures the screen remains on by adding
     * the {@code FLAG_KEEP_SCREEN_ON} flag to the window. When the screen is not to be locked,
     * it clears the same flag, allowing the screen to turn off as per the system's default behavior.
     *
     * @param isLookScreen A boolean indicating whether the screen lock should be activated.
     *                      {@code true} to keep the screen on (lock activated).
     *                      {@code false} to allow the screen to turn off (lock deactivated).
     */
    @Override
    public void handleLookScreen(boolean isLookScreen) {
        model.setStoredLockScreenIndex(isLookScreen ? 1 : 0);
        if (isLookScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * Checks whether the application's app bar (ActionBar) is currently hidden.
     *
     * @return A boolean value indicating the visibility state of the app bar.
     *         Returns true if the app bar is hidden, otherwise false.
     */
    protected boolean isAppBarHidden() {
        return isAppBarHidden;
    }
}