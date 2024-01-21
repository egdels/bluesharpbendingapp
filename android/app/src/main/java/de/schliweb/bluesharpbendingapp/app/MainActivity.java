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
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Arrays;

import de.schliweb.bluesharpbendingapp.R;
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.HarpViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MainController;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.databinding.ActivityMainBinding;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.mircophone.android.MicrophoneAndroid;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;
import de.schliweb.bluesharpbendingapp.view.android.AboutFragment;
import de.schliweb.bluesharpbendingapp.view.android.FragmentView;
import de.schliweb.bluesharpbendingapp.view.android.FragmentViewModel;
import de.schliweb.bluesharpbendingapp.view.android.HarpFragment;
import de.schliweb.bluesharpbendingapp.view.android.SettingsFragment;

import java.io.*;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements MainWindow {

    /**
     * The constant REQUEST_CODE.
     */
    private static final int REQUEST_CODE = 200;

    /**
     * The constant PERMISSIONS.
     */
    private static final String[] PERMISSIONS = {android.Manifest.permission.RECORD_AUDIO};
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
     * The Main model.
     */
    private MainModel mainModel;
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
     * Store model.
     *
     * @param model the model
     */
    private void storeModel(MainModel model) {
        LOGGER.info("Enter with parameter " + model.toString());

        ArrayList<String> stringList = new ArrayList<>();

        stringList.add("getStoredKeyIndex" + ":" + model.getStoredKeyIndex());
        stringList.add("getStoredTuneIndex" + ":" + model.getStoredTuneIndex());
        stringList.add("getStoredConcertPitchIndex" + ":" + model.getStoredConcertPitchIndex());

        File directory = this.getApplicationContext().getCacheDir();
        File file = new File(directory, TEMP_FILE);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(stringList.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Leave");
    }

    /**
     * Read model main model.
     *
     * @return the main model
     */
    private MainModel readModel() {
        LOGGER.info("Enter");
        MainModel model = new MainModel();
        File directory = this.getApplicationContext().getCacheDir();
        File file = new File(directory, TEMP_FILE);

        try {
            BufferedReader bw = new BufferedReader(new FileReader(file));
            String line = bw.readLine();

            model = MainModel.createFromString(line);

            bw.close();
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
        LOGGER.info("Enter");
        super.onCreate(savedInstanceState);

        mainModel = readModel();

        Microphone microphone = new MicrophoneAndroid();
        mainModel.setMicrophone(microphone);
        mainModel.setHarmonica(AbstractHarmonica.create(mainModel.getStoredKeyIndex(), mainModel.getStoredTuneIndex()));

        // check permission
        permissionGranted = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (!permissionGranted) {
            requestPermissions(PERMISSIONS, REQUEST_CODE);
        }

        FragmentViewModel viewModel = new ViewModelProvider(this).get(FragmentViewModel.class);
        viewModel.getSelectedFragmentView().observe(this, item -> {
            // Perform an action with the latest item data.
            this.selectedFragmentView = item;
            if (item.getInstance() instanceof SettingsFragment) {
                SettingsFragment settingsFragment = (SettingsFragment) item.getInstance();
                settingsFragment.setHarpSettingsViewHandler(getHarpSettingsViewHandler());
                settingsFragment.setMicrophoneSettingsViewHandler(getMicrophoneSettingsViewHandler());
                settingsFragment.setNoteSettingsViewHandler(getNoteSettingsViewHandler());

                settingsFragment.getHarpSettingsViewHandler().initTuneList();
                settingsFragment.getHarpSettingsViewHandler().initKeyList();
                settingsFragment.getMicrophoneSettingsViewHandler().initAlgorithmList();
                settingsFragment.getMicrophoneSettingsViewHandler().initMicrophoneList();
                settingsFragment.getNoteSettingsViewHandler().initConcertPitchList();

                mainModel.getMicrophone().setMicrophoneHandler(mainController);

            }
            if (item.getInstance() instanceof HarpFragment) {
                HarpFragment harpFragment = (HarpFragment) item.getInstance();
                harpFragment.setHarpViewHandler(getHarpViewHandler());
                harpFragment.getHarpViewHandler().initNotes();
                mainModel.getMicrophone().setMicrophoneHandler(mainController);
            }
            if (item.getInstance() instanceof AboutFragment) {
                mainModel.getMicrophone().setMicrophoneHandler(null);
            }
        });


        mainController = new MainController(this, mainModel);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        hideAppBar();
        hideNavigationBar();

        if (permissionGranted) {
            microphone.open();
        }
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
     * Hide app bar.
     */
    private void hideAppBar() {
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
        if (!isHarpViewActive()) return true;
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
     * Hide navigation bar.
     */
    private void hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (getWindow().getInsetsController() != null) {
                getWindow().getInsetsController().hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
    }

    /**
     * On request permissions result.
     *
     * @param code        the code
     * @param permissions the permissions
     * @param results     the results
     */
    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] results) {
        LOGGER.info("Enter with parameters" + code + " " + Arrays.toString(permissions) + " " + Arrays.toString(results));
        super.onRequestPermissionsResult(code, permissions, results);
        if (code == REQUEST_CODE) {
            permissionGranted = PackageManager.PERMISSION_GRANTED == results[0];
        }
        Microphone microphone = mainModel.getMicrophone();
        if (permissionGranted && microphone != null) {
            mainModel.getMicrophone().open();
        }
        LOGGER.info("Leave");
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
        // onDestroy wird nicht immer ausgeführt, daher auch beim Wechsel der View Speichern
        storeModel(mainModel);

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
        LOGGER.info("Enter on Destroy");
        super.onDestroy();
        storeModel(mainModel);
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
        Microphone microphone = mainModel.getMicrophone();
        if (isPaused && permissionGranted && microphone != null) {
            mainModel.getMicrophone().open();
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
        Microphone microphone = mainModel.getMicrophone();
        if (permissionGranted && microphone != null) {
            mainModel.getMicrophone().close();
        }
    }

}