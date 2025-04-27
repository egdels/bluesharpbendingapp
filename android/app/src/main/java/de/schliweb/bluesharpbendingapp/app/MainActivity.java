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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.*;
import de.schliweb.bluesharpbendingapp.databinding.ActivityMainBinding;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.view.*;
import de.schliweb.bluesharpbendingapp.view.android.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;


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
 * HarpFragment, AboutFragment, and TrainingFragment.
 * - Model Persistence: Reads and saves the application model state to local storage.
 * - Training, Settings, and UI Management: Provides specific handlers for UI elements like training views,
 * settings, and other configuration parameters.
 * - App Bar Control: Provides methods to dynamically show and hide the app bar based on user interactions
 * and conditions.
 * <p>
 * Implements:
 * - MainWindow: Facilitates communication with the Main Controller.
 * - AndroidSettingsHandler: Allows interaction for dynamic settings updates specific to Android requirements.
 * <p>
 * Activity Lifecycle:
 * - onCreate(Bundle savedInstanceState): Initializes the Activity and sets up the components.
 * - onOptionsItemSelected(MenuItem item): Handles user selections from the options menu and navigates to
 * respective fragments or functionalities.
 * - onTouchEvent(MotionEvent e): Handles touch interactions, toggling app bar visibility under certain conditions.
 * <p>
 * Permission Validation:
 * - Checks for RECORD_AUDIO permissions required for the app's microphone-based functionalities and requests
 * them if not granted.
 * <p>
 * Navigation:
 * - Configured using the Navigation UI library to allow smooth transitions between different fragments.
 */
@Slf4j
public class MainActivity extends AppCompatActivity implements MainWindow {

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
     * This launcher is used to request a single runtime permission from the user.
     * It uses the {@link ActivityResultContracts.RequestPermission} contract to handle the permission request
     * and receive the result in the lambda expression.
     * <p>
     * The result is a boolean indicating whether the permission was granted or not.
     * This result is then stored in the {@link #permissionGranted} field.
     * <p>
     * Example:
     * <pre>
     *     if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
     *         // Permission already granted
     *     } else {
     *         requestPermissionLauncher.launch(Manifest.permission.CAMERA);
     *     }
     * </pre>
     * </p>
     *
     * @see ActivityResultContracts.RequestPermission
     * @see ActivityResultLauncher
     * @see Manifest.permission
     */
    protected final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> permissionGranted = isGranted);
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
    @Inject
    /* package */ HarpSettingsViewHandler harpSettingsViewHandler;
    /**
     * Manages the behavior and representation of the harp view component within the application.
     * This variable is responsible for handling updates, interactions, and rendering related
     * to the harp view, ensuring seamless integration within the user interface.
     */
    @Inject
    /* package */ HarpViewHandler harpViewHandler;
    /**
     * Manages the MicrophoneSettingsViewHandler, which handles the user interface
     * and interactions related to the microphone settings within the application.
     * <p>
     * This variable serves as a link between the `MainActivity` and the
     * `MicrophoneSettingsView` component, facilitating operations such as
     * retrieving the microphone settings view, checking its state, and controlling
     * its behavior.
     */
    @Inject
    /* package */ MicrophoneSettingsViewHandler microphoneSettingsViewHandler;

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
    @Inject
    /* package */ NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * The TrainingViewHandler instance responsible for managing and handling
     * the training view within the MainActivity. This field provides the
     * necessary functionality to interact with and control elements specific
     * to the training view, such as rendering, lifecycle management, and
     * user inputs.
     */
    @Inject
    /* package */ TrainingViewHandler trainingViewHandler;

    /**
     * Represents the handler responsible for managing Android-specific settings within the application.
     * This field is used to encapsulate and manipulate settings related to the Android framework,
     * such as configurations, preferences, and runtime behavioral adjustments.
     * <p>
     * The AndroidSettingsHandler interacts with other components of the application to provide
     * a centralized management solution for Android-related settings, ensuring consistency and
     * reducing redundancy in managing these settings across the application.
     * <p>
     * This property can be accessed and modified using the associated getter and setter methods.
     */
    @Inject
    /* package */ AndroidSettingsHandler androidSettingsHandler;

    /**
     * The main controller of the application.
     * This controller is responsible for coordinating the interaction
     * between the different parts of the application, handling user input,
     * and updating the application's state. It acts as a central hub
     * for application logic and manages the flow of data.
     */
    @Inject
    /* package */ MainController mainController;

    /**
     * Provides functionality related to storing and retrieving model data required by the application.
     * This service acts as an intermediary for accessing, managing, and persisting application-specific
     * models such as configuration or settings data. It facilitates consistent and centralized
     * management of data, ensuring easy access throughout the application lifecycle.
     */
    @Inject
    /* package */ ModelStorageService modelStorageService;


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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            showPermissionInformation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }

        // Initialize dependency injection
        BlueSharpBendingApplication app = (BlueSharpBendingApplication) getApplication();

        // First initialize with just the MainActivity as MainWindow
        app.initializeDependencyInjection(this);

        // Get the AppComponent and inject dependencies
        app.getAppComponent().inject(this);

        FragmentViewModel viewModel = new ViewModelProvider(this).get(FragmentViewModel.class);
        viewModel.getSelectedFragmentView().observe(this, item -> {
            // Perform an action with the latest item data.
            this.selectedFragmentView = item;
            if (item.getInstance() instanceof SettingsFragment settingsFragment) {
                settingsFragment.setHarpSettingsViewHandler(harpSettingsViewHandler);
                settingsFragment.setMicrophoneSettingsViewHandler(microphoneSettingsViewHandler);
                settingsFragment.setNoteSettingsViewHandler(noteSettingsViewHandler);
                settingsFragment.setAndroidSettingsViewHandler(androidSettingsHandler);
                harpSettingsViewHandler.initKeyList();
                harpSettingsViewHandler.initTuneList();
                microphoneSettingsViewHandler.initAlgorithmList();
                microphoneSettingsViewHandler.initMicrophoneList();
                microphoneSettingsViewHandler.initConfidenceList();
                noteSettingsViewHandler.initConcertPitchList();
                androidSettingsHandler.initLockScreen();
            }
            if (item.getInstance() instanceof HarpFragment harpFragment) {
                harpViewHandler.initNotes();
            }
            if (item.getInstance() instanceof TrainingFragment trainingFragment) {
                trainingFragment.setTrainingViewHandler(trainingViewHandler);
                trainingViewHandler.initTrainingContainer();
                trainingViewHandler.initTrainingList();
                trainingViewHandler.initPrecisionList();
            }
            handleLookScreen();
        });

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavHostFragment navhostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navhostFragment != null;
        NavController navController = navhostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        if (permissionGranted) {
            mainController.start();
        }
    }

    @Override
    public TrainingView getTrainingView() {
        return (TrainingView) selectedFragmentView.getInstance();
    }

    @Override
    public AndroidSettingsView getAndroidSettingsView() {
        return (AndroidSettingsView) selectedFragmentView.getInstance();
    }

    @Override
    public boolean isAndroidSettingsViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof SettingsFragment;
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
     * Always returns true in this implementation.
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
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
     * Returns true if the action was processed, otherwise defer to
     * the superclass implementation.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
     * Returns true if the navigation was handled, otherwise returns the result
     * of the superclass implementation.
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
        // no need on android
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mainController.stop();
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
        if (isPaused && permissionGranted) {
            mainController.start();
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
     * the microphone is closed to release the resource and save battery.
     * <p>
     * Always calls the superclass implementation to ensure proper behavior of the activity's
     * lifecycle.
     */
    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        if (permissionGranted) {
            mainController.stop();
        }
    }

    /**
     * Handles the screen's "keep awake" behavior based on the stored lock screen index in the model.
     * <p>
     * This method reads the current model from the model storage service. If the stored lock screen index
     * in the model is greater than 0, it sets the `FLAG_KEEP_SCREEN_ON` flag on the window, preventing
     * the screen from dimming or turning off automatically. If the index is not greater than 0, it clears
     * the `FLAG_KEEP_SCREEN_ON` flag, allowing the screen to behave according to the system's default
     * power management settings.
     * </p>
     * <p>
     * In essence, a `storedLockScreenIndex` greater than 0 signifies that a lock screen or some other
     * activity requiring the screen to stay on is active.
     * </p>
     */
    private void handleLookScreen() {
        MainModel model = modelStorageService.readModel();
        if (model.getStoredLockScreenIndex() > 0) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

}
