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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.HarpViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


/**
 * The type Main window desktop.
 */
public class MainWindowDesktop extends JDialog implements MainWindow {
    /**
     * The Is donation ware.
     */
    private final boolean isDonationWare;
    /**
     * The Content pane.
     */
    private JPanel contentPane;
    /**
     * The Tool bar.
     */
    private JToolBar toolBar;
    /**
     * The Label lets bend.
     */
    private JLabel labelLetsBend;
    /**
     * The Label about.
     */
    private JLabel labelAbout;
    /**
     * The Label settings.
     */
    private JLabel labelSettings;
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
     * The Inner content pane.
     */
    private JPanel innerContentPane;
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
     * The Note settings view handler.
     */
    private NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * Instantiates a new Main window desktop.
     *
     * @param isDonationWare the is donation ware
     */
    public MainWindowDesktop(boolean isDonationWare) {
        this.isDonationWare = isDonationWare;
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        setDefaultLookAndFeelDecorated(true);

        setTitle("Let's Bend - BluesHarpBendingApp");

        ArrayList<Image> imageList = new ArrayList<>();
        imageList.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ic_launcher_32.png")));
        imageList.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ic_launcher_64.png")));
        imageList.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ic_launcher_128.png")));

        setIconImages(imageList);

        // fixed toolbar
        toolBar.setFloatable(false);

        innerContentHarpPane.setVisible(false);
        innerContentSettingsPane.setVisible(false);
        innerContentAboutPane.setVisible(false);


        this.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        super.componentShown(e);
                        setSize(500, 500);
                        innerContentHarpPane.setVisible(true);
                    }
                }
        );

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

        labelAbout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelAbout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                innerContentSettingsPane.setVisible(false);
                innerContentAboutPane.setVisible(true);
                innerContentHarpPane.setVisible(false);
            }
        });

        labelLetsBend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelLetsBend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                innerContentSettingsPane.setVisible(false);
                innerContentAboutPane.setVisible(false);
                innerContentHarpPane.setVisible(true);
            }
        });

        labelSettings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                innerContentSettingsPane.setVisible(true);
                innerContentAboutPane.setVisible(false);
                innerContentHarpPane.setVisible(false);
            }
        });


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        MainWindowDesktop dialog = new MainWindowDesktop(true);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    /**
     * On cancel.
     */
    private void onCancel() {
        dispose();
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
    public void open() {
        settingsView.addHarpSettingsViewHandler(harpSettingsViewHandler);
        settingsView.addMicrophoneSettingsViewHandler(microphoneSettingsViewHandler);
        settingsView.addNoteSettingsViewHandler(noteSettingsViewHandler);
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

    /**
     * Create ui components.
     */
    private void createUIComponents() {
        aboutView = new AboutViewDesktop(isDonationWare);
        settingsView = new SettingsViewDesktop(isDonationWare);
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
        toolBar = new JToolBar();
        toolBar.setBorderPainted(false);
        innerContentPane.add(toolBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final JToolBar toolBar1 = new JToolBar();
        toolBar.add(toolBar1);
        labelLetsBend = new JLabel();
        labelLetsBend.setText("Let's Bend");
        toolBar1.add(labelLetsBend);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator1);
        labelSettings = new JLabel();
        labelSettings.setHorizontalAlignment(10);
        labelSettings.setHorizontalTextPosition(11);
        labelSettings.setText("Settings");
        toolBar1.add(labelSettings);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator2);
        labelAbout = new JLabel();
        labelAbout.setHorizontalAlignment(10);
        labelAbout.setHorizontalTextPosition(11);
        labelAbout.setText("About");
        toolBar1.add(labelAbout);
        innerContentHarpPane = new JPanel();
        innerContentHarpPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        innerContentPane.add(innerContentHarpPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        harpView = new HarpViewDesktop();
        innerContentHarpPane.add(harpView.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentSettingsPane = new JPanel();
        innerContentSettingsPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        innerContentPane.add(innerContentSettingsPane, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentSettingsPane.add(settingsView.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentAboutPane = new JPanel();
        innerContentAboutPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        innerContentPane.add(innerContentAboutPane, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        innerContentAboutPane.add(aboutView.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
