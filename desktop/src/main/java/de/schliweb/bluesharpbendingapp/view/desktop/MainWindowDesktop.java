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
 * The type Main window desktop.
 */
public class MainWindowDesktop extends JFrame implements MainWindow {
    /**
     * The Is donation ware.
     */
    private final boolean isDonationWare;
    /**
     * The Content pane.
     */
    private JPanel contentPane;

    /**
     * The Inner content harp pane.
     */
    private JPanel innerContentHarpPane;
    /**
     * The Harp view.
     */
    private HarpViewDesktop harpView;
    /**
     * The Inner content settings pane.
     */
    private JPanel innerContentSettingsPane;
    /**
     * The Inner content about pane.
     */
    private JPanel innerContentAboutPane;
    /**
     * The Settings view.
     */
    private SettingsViewDesktop settingsView;
    /**
     * The About view.
     */
    private AboutViewDesktop aboutView;

    /**
     * The Training view.
     */
    private TrainingViewDesktop trainingView;

    /**
     * The Inner content pane.
     */
    private JPanel innerContentPane;
    /**
     * The Inner content training pane.
     */
    private JPanel innerContentTrainingPane;
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
     * The NoteContainer settings view handler.
     */
    private NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * The Training view handler.
     */
    private TrainingViewHandler trainingViewHandler;

    /**
     * Instantiates a new Main window desktop.
     *
     * @param isDonationWare the is donation ware
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
     * Close.
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
