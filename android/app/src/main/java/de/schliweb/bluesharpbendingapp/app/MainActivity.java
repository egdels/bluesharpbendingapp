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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.*;
import de.schliweb.bluesharpbendingapp.databinding.ActivityMainBinding;
import de.schliweb.bluesharpbendingapp.model.AndroidModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.mircophone.android.MicrophoneAndroid;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.view.*;
import de.schliweb.bluesharpbendingapp.view.android.*;

import java.io.*;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements MainWindow, AndroidSettingsHandler {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(MainActivity.class);
    /**
     * The constant TEMP_FILE.
     */
    private static final String TEMP_FILE = "Model.tmp";
    /**
     * The Permission granted.
     */
    private boolean permissionGranted = false;
    /**
     * The App bar configuration.
     */
    private AppBarConfiguration appBarConfiguration;
    /**
     * The Selected fragment view.
     */
    private FragmentView selectedFragmentView;
    /**
     * The Harp settings view handler.
     */
    private HarpSettingsViewHandler harpSettingsViewHandler;
    /**
     * The Harp view handler.
     */
    private HarpViewHandler harpViewHandler;
    /**
     * The Microphone settings view handler.
     */
    private MicrophoneSettingsViewHandler microphoneSettingsViewHandler;
    /**
     * The Android model.
     */
    private AndroidModel model;
    /**
     * The Request permission launcher.
     */
    protected final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
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
     * The Is paused.
     */
    private boolean isPaused = false;
    /**
     * The Main controller.
     */
    private MainController mainController;
    /**
     * The Is app bar hidden.
     */
    private boolean isAppBarHidden = false;
    /**
     * The Note settings view handler.
     */
    private NoteSettingsViewHandler noteSettingsViewHandler;
    /**
     * The Training view handler.
     */
    private TrainingViewHandler trainingViewHandler;

    /**
     * Store model.
     *
     * @param model the model
     */
    private void storeModel(AndroidModel model) {

        File directory = this.getApplicationContext().getCacheDir();
        File file = new File(directory, TEMP_FILE);


        try (
                FileWriter writer = new FileWriter(file)
        ) {
            writer.write(model.getString());
            writer.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

    }

    /**
     * Read model main model.
     *
     * @return the main model
     */
    protected AndroidModel readModel() {

        AndroidModel model = new AndroidModel();
        File directory = this.getApplicationContext().getCacheDir();
        File file = new File(directory, TEMP_FILE);

        try (
                BufferedReader bw = new BufferedReader(new FileReader(file))
        ) {

            String line = bw.readLine();
            if (line != null)
                model = AndroidModel.createFromString(line);


        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return model;
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.setDebug(false);
        Logger.setInfo(false);

        model = readModel();

        Microphone microphone = new MicrophoneAndroid();
        model.setMicrophone(microphone);
        model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex()));

        // check permission
        permissionGranted = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (!permissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }

        FragmentViewModel viewModel = new ViewModelProvider(this).get(FragmentViewModel.class);
        viewModel.getSelectedFragmentView().observe(this, item -> {
            // Perform an action with the latest item data.
            this.selectedFragmentView = item;
            if (item.getInstance() instanceof SettingsFragment settingsFragment) {
                settingsFragment.setHarpSettingsViewHandler(getHarpSettingsViewHandler());
                settingsFragment.setMicrophoneSettingsViewHandler(getMicrophoneSettingsViewHandler());
                settingsFragment.setNoteSettingsViewHandler(getNoteSettingsViewHandler());
                settingsFragment.setAndroidSettingsHandler(this);

                settingsFragment.getHarpSettingsViewHandler().initTuneList();
                settingsFragment.getHarpSettingsViewHandler().initKeyList();
                settingsFragment.getMicrophoneSettingsViewHandler().initAlgorithmList();
                settingsFragment.getMicrophoneSettingsViewHandler().initMicrophoneList();
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


        mainController = new MainController(this, model);

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

    /**
     * Gets training view handler.
     *
     * @return the training view handler
     */
    private TrainingViewHandler getTrainingViewHandler() {
        return this.trainingViewHandler;
    }

    /**
     * Sets training view handler.
     *
     * @param trainingViewHandler the training view handler
     */
    @Override
    public void setTrainingViewHandler(TrainingViewHandler trainingViewHandler) {
        this.trainingViewHandler = trainingViewHandler;
    }

    /**
     * Gets note settings view handler.
     *
     * @return the note settings view handler
     */
    private NoteSettingsViewHandler getNoteSettingsViewHandler() {
        return noteSettingsViewHandler;
    }

    /**
     * Sets note settings view handler.
     *
     * @param noteSettingsViewHandler the note settings view handler
     */
    @Override
    public void setNoteSettingsViewHandler(NoteSettingsViewHandler noteSettingsViewHandler) {
        this.noteSettingsViewHandler = noteSettingsViewHandler;
    }

    /**
     * Gets training view.
     *
     * @return the training view
     */
    @Override
    public TrainingView getTrainingView() {
        return (TrainingView) selectedFragmentView.getInstance();
    }

    /**
     * Hide app bar.
     */
    protected void hideAppBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
            isAppBarHidden = true;
        }
    }

    /**
     * Un hide app bar.
     */
    private void unHideAppBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.show();
            isAppBarHidden = false;
        }
    }

    /**
     * On touch event boolean.
     *
     * @param e the e
     * @return the boolean
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
     * Show permission information.
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

    /**
     * On create options menu boolean.
     *
     * @param menu the menu
     * @return the boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * On options item selected boolean.
     *
     * @param item the item
     * @return the boolean
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
     * On support navigate up boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Gets harp settings view.
     *
     * @return the harp settings view
     */
    @Override
    public HarpSettingsView getHarpSettingsView() {
        return (HarpSettingsView) selectedFragmentView.getInstance();
    }

    /**
     * Gets harp view.
     *
     * @return the harp view
     */
    @Override
    public HarpView getHarpView() {
        return (HarpView) selectedFragmentView.getInstance();
    }

    /**
     * Gets microphone settings view.
     *
     * @return the microphone settings view
     */
    @Override
    public MicrophoneSettingsView getMicrophoneSettingsView() {
        return (MicrophoneSettingsView) selectedFragmentView.getInstance();
    }

    /**
     * Is harp settings view active boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isHarpSettingsViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof SettingsFragment;
    }

    /**
     * Is harp view active boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isHarpViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof HarpFragment;
    }

    /**
     * Is microphone settings view active boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isMicrophoneSettingsViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof SettingsFragment;
    }

    /**
     * Is training view active boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isTrainingViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof TrainingFragment;
    }

    /**
     * Open.
     */
    @Override
    public void open() {
        // no need on android because mainController is not started.
    }

    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        storeModel(model);
    }

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
    @Override
    public void setHarpSettingsViewHandler(HarpSettingsViewHandler harpSettingsViewHandler) {
        this.harpSettingsViewHandler = harpSettingsViewHandler;
    }

    /**
     * Gets harp view handler.
     *
     * @return the harp view handler
     */
    public HarpViewHandler getHarpViewHandler() {
        return harpViewHandler;
    }

    /**
     * Sets harp view handler.
     *
     * @param harpViewHandler the harp view handler
     */
    @Override
    public void setHarpViewHandler(HarpViewHandler harpViewHandler) {
        this.harpViewHandler = harpViewHandler;
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
    @Override
    public void setMicrophoneSettingsViewHandler(MicrophoneSettingsViewHandler microphoneSettingsViewHandler) {
        this.microphoneSettingsViewHandler = microphoneSettingsViewHandler;
    }

    /**
     * Is note settings view active boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isNoteSettingsViewActive() {
        return selectedFragmentView != null && selectedFragmentView.getInstance() instanceof SettingsFragment;
    }

    /**
     * Gets note settings view.
     *
     * @return the note settings view
     */
    @Override
    public NoteSettingsView getNoteSettingsView() {
        return (NoteSettingsView) selectedFragmentView.getInstance();
    }

    /**
     * On resume.
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
     * On pause.
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
     * Handle look screen.
     *
     * @param isLookScreen the is look screen
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
}