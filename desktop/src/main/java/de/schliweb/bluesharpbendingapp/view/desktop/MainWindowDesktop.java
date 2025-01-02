package de.schliweb.bluesharpbendingapp.view.desktop;
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

import com.formdev.flatlaf.util.SystemInfo;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import de.schliweb.bluesharpbendingapp.app.MainDesktop;
import de.schliweb.bluesharpbendingapp.controller.*;
import de.schliweb.bluesharpbendingapp.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


/**
 * The MainWindowDesktop class represents the main user interface window for a desktop
 * application. It provides functionality for managing and displaying views such as
 * HarpView, Settings, Training, and About sections. It extends JFrame and serves
 * as the central controller for handling these views and their interactions.
 * <p>
 * Fields in the class are used for maintaining a structured UI and handling the
 * different components of the application.
 */
public class MainWindowDesktop extends JFrame implements MainWindow {
    /**
     * Indicates whether the software is classified as donationware.
     * Donationware refers to software that is free to use but encourages
     * users to voluntarily donate to support its development or maintenance.
     */
    private final boolean isDonationWare;
    /**
     * Represents the main content pane of the application window. This panel
     * serves as the container for various UI components and is used as the
     * root panel for the layout and organization of the desktop application's
     * interface. The `contentPane` is typically initialized and managed within
     * the main application window class, ensuring the proper display and interaction
     * of the application's graphical elements.
     */
    private JPanel contentPane;

    /**
     * Represents a JPanel that serves as an inner container for the harp view content within the MainWindowDesktop.
     * This panel is used to encapsulate and manage the layout and display of harp-related components or views.
     */
    private JPanel innerContentHarpPane;
    /**
     * Represents the main view component for interacting with and displaying
     * information about the "Harp" functionality within the MainWindowDesktop class.
     * This variable references a desktop-specific implementation of the HarpView interface.
     * <p>
     * It is used to manage and visualize the harp-related functions or data, typically
     * through graphical user interface elements in the application.
     */
    private HarpViewDesktop harpView;
    /**
     * A JPanel representing the settings pane of the inner content in the main window.
     * This component is part of the main window's layout and is used for displaying or managing
     * settings-related functionality within the application.
     */
    private JPanel innerContentSettingsPane;
    /**
     * A JPanel instance that represents the inner content area for the "About" section of the application.
     * This panel is used to display information about the application, such as details about the software,
     * developer credits, or usage guidelines.
     */
    private JPanel innerContentAboutPane;
    /**
     * Represents the settings view specific to the desktop implementation of the application.
     * Acts as a visual and interactive interface to manage and configure various application settings.
     * It is a key component within the MainWindowDesktop class structure.
     */
    private SettingsViewDesktop settingsView;
    /**
     * Represents the AboutViewDesktop component within the MainWindowDesktop.
     * This variable is responsible for managing and displaying the "About" section
     * of the application within the desktop interface.
     */
    private AboutViewDesktop aboutView;

    /**
     * Represents the training view component utilized within the desktop version of the main application window.
     * This view is responsible for presenting the training interface to the user.
     */
    private TrainingViewDesktop trainingView;

    /**
     * Represents the inner content pane of the main window in the desktop application.
     * This JPanel serves as a container for other components or panes that make up
     * the main user interface. The `innerContentPane` is likely used for dynamically
     * displaying and managing different views or panels, such as settings, training,
     * or harp-related components.
     */
    private JPanel innerContentPane;
    /**
     * Represents a JPanel that serves as the container for the training view's content
     * within the MainWindowDesktop application. This panel organizes and displays
     * components related to the training functionality, enabling users to interact
     * with training features.
     */
    private JPanel innerContentTrainingPane;
    /**
     * Represents the handler responsible for managing interactions and functionality
     * related to the harp settings view within the application. It serves as a mediator
     * between user interactions in the UI and the underlying application logic concerning
     * harp settings, such as key and tuning management.
     * <p>
     * This field is set using the `setHarpSettingsViewHandler` method and is utilized
     * by the `MainWindowDesktop` class to delegate harp settings-related actions to the
     * appropriate implementation of the `HarpSettingsViewHandler` interface.
     */
    private HarpSettingsViewHandler harpSettingsViewHandler;
    /**
     * The {@code harpViewHandler} is an instance of the {@link HarpViewHandler} interface
     * responsible for handling interactions and managing functionalities specific to the
     * "Harp View" within the MainWindowDesktop class.
     * <p>
     * This variable facilitates initialization, communication, and updates related to
     * the harp application's visual representation and its associated actions.
     */
    private HarpViewHandler harpViewHandler;
    /**
     * Represents a handler for managing the microphone settings view in the application.
     * This variable is responsible for handling interactions, user inputs, and settings
     * related to the microphone and algorithm configuration.
     *
     * <ul>
     *     <li>Facilitates initialization of the microphone and algorithm lists.</li>
     *     <li>Handles user selection of specific microphones and algorithms.</li>
     *     <li>Acts as a bridge between the microphone settings view and the application logic.</li>
     * </ul>
     * <p>
     * Used within the MainWindowDesktop class to connect the microphone settings view
     * with its corresponding logic.
     */
    private MicrophoneSettingsViewHandler microphoneSettingsViewHandler;
    /**
     * A handler for managing interactions and operations related to the Note Settings view
     * within the user interface. This variable is responsible for linking the interface logic
     * and functionality of the NoteSettingsViewHandler with the corresponding view in the application.
     * <p>
     * The `noteSettingsViewHandler` is utilized to initialize the concert pitch list and
     * handle user interactions such as selecting a specific concert pitch. It facilitates
     * the communication between the view and the underlying application logic.
     */
    private NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * Field to manage the interaction and logic associated with the training view.
     * <p>
     * The `trainingViewHandler` is responsible for coordinating the behaviors,
     * events, and updates related to the training view in the application. It acts
     * as a bridge between the visual presentation of the training view and the
     * underlying business logic, ensuring that user actions are properly managed
     * and reflected in the view.
     * <p>
     * This field is expected to hold an implementation of the `TrainingViewHandler`
     * interface, which defines methods for handling training initialization,
     * user interactions, and the start/stop processes of training sessions.
     */
    private TrainingViewHandler trainingViewHandler;

    /**
     * Constructs the MainWindowDesktop class, sets up the main window of the application,
     * initializes UI components, and configures event listeners for different panes and menu options.
     *
     * @param isDonationWare a boolean indicating if the application is donation-based (true) or not (false)
     */
    public MainWindowDesktop(boolean isDonationWare) {
        this.isDonationWare = isDonationWare;
        $$$setupUI$$$();
        setContentPane(contentPane);
        setDefaultLookAndFeelDecorated(true);

        setTitle(null);

        ArrayList<Image> imageList = new ArrayList<>();
        imageList.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ic_launcher_32.png")));
        imageList.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ic_launcher_64.png")));
        imageList.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ic_launcher_128.png")));

        setIconImages(imageList);

        innerContentHarpPane.setVisible(false);
        innerContentSettingsPane.setVisible(false);
        innerContentAboutPane.setVisible(false);
        innerContentTrainingPane.setVisible(false);


        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                setSize(500, 500);
                innerContentHarpPane.setVisible(true);
            }
        });

        innerContentHarpPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                harpViewHandler.initNotes();
            }
        });

        innerContentSettingsPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                microphoneSettingsViewHandler.initMicrophoneList();
                microphoneSettingsViewHandler.initAlgorithmList();
                harpSettingsViewHandler.initKeyList();
                harpSettingsViewHandler.initTuneList();
                noteSettingsViewHandler.initConcertPitchList();
            }
        });

        innerContentTrainingPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                trainingViewHandler.initTrainingList();
                trainingViewHandler.initTrainingContainer();
                trainingViewHandler.initPrecisionList();
            }
        });

        JMenu menuAbout = new JMenu("About");
        menuAbout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuAbout.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                innerContentSettingsPane.setVisible(false);
                innerContentAboutPane.setVisible(true);
                innerContentHarpPane.setVisible(false);
                innerContentTrainingPane.setVisible(false);
            }
        });


        JMenu menuLetsBend = new JMenu("Let's Bend");
        menuLetsBend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuLetsBend.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                innerContentSettingsPane.setVisible(false);
                innerContentAboutPane.setVisible(false);
                innerContentHarpPane.setVisible(true);
                innerContentTrainingPane.setVisible(false);
            }
        });

        JMenu menuTraining = new JMenu("Training");
        menuTraining.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuTraining.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                innerContentSettingsPane.setVisible(false);
                innerContentAboutPane.setVisible(false);
                innerContentHarpPane.setVisible(false);
                innerContentTrainingPane.setVisible(true);
            }
        });

        JMenu menuSettings = new JMenu("Settings");
        menuSettings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                innerContentSettingsPane.setVisible(true);
                innerContentAboutPane.setVisible(false);
                innerContentHarpPane.setVisible(false);
                innerContentTrainingPane.setVisible(false);
            }
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuLetsBend);
        menuBar.add(menuTraining);
        menuBar.add(menuSettings);
        menuBar.add(menuAbout);

        setJMenuBar(menuBar);

        if (SystemInfo.isMacOS) {
            // hide menu items that are in macOS application menu
            // menuLetsBend.setVisible(false);
            // menuSettings.setVisible(false);
            // menuAbout.setVisible(false);

            // do not use HTML text in menu items because this is not supported in macOS screen menu
            // htmlMenuItem.setText( "some text" );

            if (SystemInfo.isMacFullWindowContentSupported) {
                // expand window content into window title bar and make title bar transparent
                getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
                getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);

                // hide window title
                if (SystemInfo.isJava_17_orLater)
                    getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
                else setTitle(null);

                // add gap to left side of toolbar
                menuBar.add(Box.createHorizontalStrut(70), 0);
            }

            // enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
            if (!SystemInfo.isJava_11_orLater) getRootPane().putClientProperty("apple.awt.fullscreenable", true);
        }


        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
            desktop.setAboutHandler(e -> {
                innerContentSettingsPane.setVisible(false);
                innerContentAboutPane.setVisible(true);
                innerContentHarpPane.setVisible(false);
                innerContentTrainingPane.setVisible(false);
            });
        }

        if (desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
            desktop.setPreferencesHandler(e -> {
                innerContentSettingsPane.setVisible(true);
                innerContentAboutPane.setVisible(false);
                innerContentHarpPane.setVisible(false);
                innerContentTrainingPane.setVisible(false);
            });
        }

        if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
            desktop.setQuitHandler((e, response) -> close());
        }

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    /**
     * Closes the main window of the application.
     * <p>
     * This method is responsible for disposing of the current window's resources
     * and triggering the centralized close operation via MainDesktop.close().
     */
    private void close() {
        dispose();
        MainDesktop.close();
    }

    @Override
    public HarpSettingsView getHarpSettingsView() {
        return settingsView;
    }

    @Override
    public HarpView getHarpView() {
        return harpView;
    }

    @Override
    public MicrophoneSettingsView getMicrophoneSettingsView() {
        return settingsView;
    }

    @Override
    public boolean isHarpSettingsViewActive() {
        return innerContentSettingsPane.isVisible();
    }

    @Override
    public boolean isHarpViewActive() {
        return innerContentHarpPane.isVisible();
    }

    @Override
    public boolean isMicrophoneSettingsViewActive() {
        return innerContentSettingsPane.isVisible();
    }

    @Override
    public boolean isTrainingViewActive() {
        return innerContentTrainingPane.isVisible();
    }

    @Override
    public void open() {
        settingsView.addHarpSettingsViewHandler(harpSettingsViewHandler);
        settingsView.addMicrophoneSettingsViewHandler(microphoneSettingsViewHandler);
        settingsView.addNoteSettingsViewHandler(noteSettingsViewHandler);
        trainingView.addTrainingViewHandler(trainingViewHandler);
        pack();
        setVisible(true);
    }

    @Override
    public void setHarpSettingsViewHandler(HarpSettingsViewHandler harpSettingsViewHandler) {
        this.harpSettingsViewHandler = harpSettingsViewHandler;
    }

    @Override
    public void setHarpViewHandler(HarpViewHandler harpViewHandler) {
        this.harpViewHandler = harpViewHandler;
    }

    @Override
    public void setTrainingViewHandler(TrainingViewHandler trainingViewHandler) {
        this.trainingViewHandler = trainingViewHandler;
    }

    @Override
    public void setMicrophoneSettingsViewHandler(MicrophoneSettingsViewHandler microphoneSettingsViewHandler) {
        this.microphoneSettingsViewHandler = microphoneSettingsViewHandler;
    }

    @Override
    public boolean isNoteSettingsViewActive() {
        return innerContentSettingsPane.isVisible();
    }

    @Override
    public NoteSettingsView getNoteSettingsView() {
        return settingsView;
    }

    @Override
    public void setNoteSettingsViewHandler(NoteSettingsViewHandler noteSettingsViewHandler) {
        this.noteSettingsViewHandler = noteSettingsViewHandler;
    }

    @Override
    public TrainingView getTrainingView() {
        return trainingView;
    }

    /**
     * Create ui components.
     */
    private void createUIComponents() {
        aboutView = new AboutViewDesktop(isDonationWare);
        settingsView = new SettingsViewDesktop(isDonationWare);
        trainingView = new TrainingViewDesktop();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMinimumSize(new Dimension(500, 500));
        contentPane.setName("BluesHarpBendingApp");
        innerContentPane = new JPanel();
        innerContentPane.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(innerContentPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentHarpPane = new JPanel();
        innerContentHarpPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        innerContentPane.add(innerContentHarpPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        harpView = new HarpViewDesktop();
        innerContentHarpPane.add(harpView.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentSettingsPane = new JPanel();
        innerContentSettingsPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        innerContentPane.add(innerContentSettingsPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentSettingsPane.add(settingsView.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentAboutPane = new JPanel();
        innerContentAboutPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        innerContentPane.add(innerContentAboutPane, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentAboutPane.add(aboutView.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentTrainingPane = new JPanel();
        innerContentTrainingPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        innerContentPane.add(innerContentTrainingPane, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentTrainingPane.add(trainingView.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * $$$ get root component $$$ j component.
     *
     * @return the j component
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
