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


import de.schliweb.bluesharpbendingapp.controller.NoteContainer;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * The HarpViewDesktopFXController class provides functionalities for managing and interacting
 * with the visual representation of a harp view in a desktop application using JavaFX. It extends
 * the capabilities of the HarpView interface and manages note elements associated with multiple
 * channels and their visual notes.
 */
public class HarpViewDesktopFXController implements HarpView {

    @FXML
    public Pane channel1;

    @FXML
    public Pane channel2;

    @FXML
    public Pane channel3;

    @FXML
    public Pane channel4;

    @FXML
    public Pane channel10;

    @FXML
    public Pane channel9;

    @FXML
    public Pane channel8;

    @FXML
    public Pane channel7;

    @FXML
    public Pane channel6;

    @FXML
    public Pane channel5;

    @FXML
    private Pane channel1NoteM3;
    @FXML
    private Pane channel2NoteM3;
    @FXML
    private Pane channel3NoteM3;
    @FXML
    private Pane channel4NoteM3;
    @FXML
    private Pane channel5NoteM3;
    @FXML
    private Pane channel6NoteM3;
    @FXML
    private Pane channel7NoteM3;
    @FXML
    private Pane channel8NoteM3;
    @FXML
    private Pane channel9NoteM3;
    @FXML
    private Pane channel10NoteM3;

    @FXML
    private Pane channel1NoteM2;
    @FXML
    private Pane channel2NoteM2;
    @FXML
    private Pane channel3NoteM2;
    @FXML
    private Pane channel4NoteM2;
    @FXML
    private Pane channel5NoteM2;
    @FXML
    private Pane channel6NoteM2;
    @FXML
    private Pane channel7NoteM2;
    @FXML
    private Pane channel8NoteM2;
    @FXML
    private Pane channel9NoteM2;
    @FXML
    private Pane channel10NoteM2;

    @FXML
    private Pane channel1NoteM1;
    @FXML
    private Pane channel2NoteM1;
    @FXML
    private Pane channel3NoteM1;
    @FXML
    private Pane channel4NoteM1;
    @FXML
    private Pane channel5NoteM1;
    @FXML
    private Pane channel6NoteM1;
    @FXML
    private Pane channel7NoteM1;
    @FXML
    private Pane channel8NoteM1;
    @FXML
    private Pane channel9NoteM1;
    @FXML
    private Pane channel10NoteM1;

    @FXML
    private Pane channel1Note0;
    @FXML
    private Pane channel2Note0;
    @FXML
    private Pane channel3Note0;
    @FXML
    private Pane channel4Note0;
    @FXML
    private Pane channel5Note0;
    @FXML
    private Pane channel6Note0;
    @FXML
    private Pane channel7Note0;
    @FXML
    private Pane channel8Note0;
    @FXML
    private Pane channel9Note0;
    @FXML
    private Pane channel10Note0;

    @FXML
    private Pane channel1Note1;
    @FXML
    private Pane channel2Note1;
    @FXML
    private Pane channel3Note1;
    @FXML
    private Pane channel4Note1;
    @FXML
    private Pane channel5Note1;
    @FXML
    private Pane channel6Note1;
    @FXML
    private Pane channel7Note1;
    @FXML
    private Pane channel8Note1;
    @FXML
    private Pane channel9Note1;
    @FXML
    private Pane channel10Note1;

    @FXML
    private Pane channel1Note2;
    @FXML
    private Pane channel2Note2;
    @FXML
    private Pane channel3Note2;
    @FXML
    private Pane channel4Note2;
    @FXML
    private Pane channel5Note2;
    @FXML
    private Pane channel6Note2;
    @FXML
    private Pane channel7Note2;
    @FXML
    private Pane channel8Note2;
    @FXML
    private Pane channel9Note2;
    @FXML
    private Pane channel10Note2;

    @FXML
    private Pane channel1Note3;
    @FXML
    private Pane channel2Note3;
    @FXML
    private Pane channel3Note3;
    @FXML
    private Pane channel4Note3;
    @FXML
    private Pane channel5Note3;
    @FXML
    private Pane channel6Note3;
    @FXML
    private Pane channel7Note3;
    @FXML
    private Pane channel8Note3;
    @FXML
    private Pane channel9Note3;
    @FXML
    private Pane channel10Note3;

    @FXML
    private Pane channel1Note4;
    @FXML
    private Pane channel2Note4;
    @FXML
    private Pane channel3Note4;
    @FXML
    private Pane channel4Note4;
    @FXML
    private Pane channel5Note4;
    @FXML
    private Pane channel6Note4;
    @FXML
    private Pane channel7Note4;
    @FXML
    private Pane channel8Note4;
    @FXML
    private Pane channel9Note4;
    @FXML
    private Pane channel10Note4;


    /**
     * Retrieves the pane corresponding to the specified channel and note.
     *
     * @param channel the channel number (1-10) for which the Pane is to be retrieved
     * @param note the note value (-3 to 4) for which the Pane is to be retrieved
     * @return the Pane associated with the specified channel and note
     */
    private Pane getNotePanel(int channel, int note) {
        Pane[][] Panels = new Pane[10][8];

        Panels[0][0] = channel1NoteM3;
        Panels[0][1] = channel1NoteM2;
        Panels[0][2] = channel1NoteM1;
        Panels[0][3] = channel1Note0;
        Panels[0][4] = channel1Note1;
        Panels[0][5] = channel1Note2;
        Panels[0][6] = channel1Note3;
        Panels[0][7] = channel1Note4;

        Panels[1][0] = channel2NoteM3;
        Panels[1][1] = channel2NoteM2;
        Panels[1][2] = channel2NoteM1;
        Panels[1][3] = channel2Note0;
        Panels[1][4] = channel2Note1;
        Panels[1][5] = channel2Note2;
        Panels[1][6] = channel2Note3;
        Panels[1][7] = channel2Note4;

        Panels[2][0] = channel3NoteM3;
        Panels[2][1] = channel3NoteM2;
        Panels[2][2] = channel3NoteM1;
        Panels[2][3] = channel3Note0;
        Panels[2][4] = channel3Note1;
        Panels[2][5] = channel3Note2;
        Panels[2][6] = channel3Note3;
        Panels[2][7] = channel3Note4;

        Panels[3][0] = channel4NoteM3;
        Panels[3][1] = channel4NoteM2;
        Panels[3][2] = channel4NoteM1;
        Panels[3][3] = channel4Note0;
        Panels[3][4] = channel4Note1;
        Panels[3][5] = channel4Note2;
        Panels[3][6] = channel4Note3;
        Panels[3][7] = channel4Note4;

        Panels[4][0] = channel5NoteM3;
        Panels[4][1] = channel5NoteM2;
        Panels[4][2] = channel5NoteM1;
        Panels[4][3] = channel5Note0;
        Panels[4][4] = channel5Note1;
        Panels[4][5] = channel5Note2;
        Panels[4][6] = channel5Note3;
        Panels[4][7] = channel5Note4;

        Panels[5][0] = channel6NoteM3;
        Panels[5][1] = channel6NoteM2;
        Panels[5][2] = channel6NoteM1;
        Panels[5][3] = channel6Note0;
        Panels[5][4] = channel6Note1;
        Panels[5][5] = channel6Note2;
        Panels[5][6] = channel6Note3;
        Panels[5][7] = channel6Note4;

        Panels[6][0] = channel7NoteM3;
        Panels[6][1] = channel7NoteM2;
        Panels[6][2] = channel7NoteM1;
        Panels[6][3] = channel7Note0;
        Panels[6][4] = channel7Note1;
        Panels[6][5] = channel7Note2;
        Panels[6][6] = channel7Note3;
        Panels[6][7] = channel7Note4;

        Panels[7][0] = channel8NoteM3;
        Panels[7][1] = channel8NoteM2;
        Panels[7][2] = channel8NoteM1;
        Panels[7][3] = channel8Note0;
        Panels[7][4] = channel8Note1;
        Panels[7][5] = channel8Note2;
        Panels[7][6] = channel8Note3;
        Panels[7][7] = channel8Note4;

        Panels[8][0] = channel9NoteM3;
        Panels[8][1] = channel9NoteM2;
        Panels[8][2] = channel9NoteM1;
        Panels[8][3] = channel9Note0;
        Panels[8][4] = channel9Note1;
        Panels[8][5] = channel9Note2;
        Panels[8][6] = channel9Note3;
        Panels[8][7] = channel9Note4;

        Panels[9][0] = channel10NoteM3;
        Panels[9][1] = channel10NoteM2;
        Panels[9][2] = channel10NoteM1;
        Panels[9][3] = channel10Note0;
        Panels[9][4] = channel10Note1;
        Panels[9][5] = channel10Note2;
        Panels[9][6] = channel10Note3;
        Panels[9][7] = channel10Note4;

        return Panels[channel - 1][note + 3];
    }

    /**
     * Hides all note elements by setting their visibility to false, if they exist.
     * <p>
     * This method iterates over a series of predefined note elements across
     * multiple channels and multiple categories (M3, M2, M1, 0, 1, 2, 3, 4).
     * Each note element is checked for nullability before setting its visibility
     * to false. Notes are categorized and named consistently based on their
     * respective channels and categories.
     */
    private void hideNotes() {

        if (channel1NoteM3 != null) {
            channel1NoteM3.setVisible(false);
        }
        if (channel2NoteM3 != null) {
            channel2NoteM3.setVisible(false);
        }
        if (channel3NoteM3 != null) {
            channel3NoteM3.setVisible(false);
        }
        if (channel4NoteM3 != null) {
            channel4NoteM3.setVisible(false);
        }
        if (channel5NoteM3 != null) {
            channel5NoteM3.setVisible(false);
        }
        if (channel6NoteM3 != null) {
            channel6NoteM3.setVisible(false);
        }
        if (channel7NoteM3 != null) {
            channel7NoteM3.setVisible(false);
        }
        if (channel8NoteM3 != null) {
            channel8NoteM3.setVisible(false);
        }
        if (channel9NoteM3 != null) {
            channel9NoteM3.setVisible(false);
        }
        if (channel10NoteM3 != null) {
            channel10NoteM3.setVisible(false);
        }

        if (channel1NoteM2 != null) {
            channel1NoteM2.setVisible(false);
        }
        if (channel2NoteM2 != null) {
            channel2NoteM2.setVisible(false);
        }
        if (channel3NoteM2 != null) {
            channel3NoteM2.setVisible(false);
        }
        if (channel4NoteM2 != null) {
            channel4NoteM2.setVisible(false);
        }
        if (channel5NoteM2 != null) {
            channel5NoteM2.setVisible(false);
        }
        if (channel6NoteM2 != null) {
            channel6NoteM2.setVisible(false);
        }
        if (channel7NoteM2 != null) {
            channel7NoteM2.setVisible(false);
        }
        if (channel8NoteM2 != null) {
            channel8NoteM2.setVisible(false);
        }
        if (channel9NoteM2 != null) {
            channel9NoteM2.setVisible(false);
        }
        if (channel10NoteM2 != null) {
            channel10NoteM2.setVisible(false);
        }


        if (channel1NoteM1 != null) {
            channel1NoteM1.setVisible(false);
        }
        if (channel2NoteM1 != null) {
            channel2NoteM1.setVisible(false);
        }
        if (channel3NoteM1 != null) {
            channel3NoteM1.setVisible(false);
        }
        if (channel4NoteM1 != null) {
            channel4NoteM1.setVisible(false);
        }
        if (channel5NoteM1 != null) {
            channel5NoteM1.setVisible(false);
        }
        if (channel6NoteM1 != null) {
            channel6NoteM1.setVisible(false);
        }
        if (channel7NoteM1 != null) {
            channel7NoteM1.setVisible(false);
        }
        if (channel8NoteM1 != null) {
            channel8NoteM1.setVisible(false);
        }
        if (channel9NoteM1 != null) {
            channel9NoteM1.setVisible(false);
        }
        if (channel10NoteM1 != null) {
            channel10NoteM1.setVisible(false);
        }

        if (channel1Note0 != null) {
            channel1Note0.setVisible(false);
        }
        if (channel2Note0 != null) {
            channel2Note0.setVisible(false);
        }
        if (channel3Note0 != null) {
            channel3Note0.setVisible(false);
        }
        if (channel4Note0 != null) {
            channel4Note0.setVisible(false);
        }
        if (channel5Note0 != null) {
            channel5Note0.setVisible(false);
        }
        if (channel6Note0 != null) {
            channel6Note0.setVisible(false);
        }
        if (channel7Note0 != null) {
            channel7Note0.setVisible(false);
        }
        if (channel8Note0 != null) {
            channel8Note0.setVisible(false);
        }
        if (channel9Note0 != null) {
            channel9Note0.setVisible(false);
        }
        if (channel10Note0 != null) {
            channel10Note0.setVisible(false);
        }

        if (channel1Note1 != null) {
            channel1Note1.setVisible(false);
        }
        if (channel2Note1 != null) {
            channel2Note1.setVisible(false);
        }
        if (channel3Note1 != null) {
            channel3Note1.setVisible(false);
        }
        if (channel4Note1 != null) {
            channel4Note1.setVisible(false);
        }
        if (channel5Note1 != null) {
            channel5Note1.setVisible(false);
        }
        if (channel6Note1 != null) {
            channel6Note1.setVisible(false);
        }
        if (channel7Note1 != null) {
            channel7Note1.setVisible(false);
        }
        if (channel8Note1 != null) {
            channel8Note1.setVisible(false);
        }
        if (channel9Note1 != null) {
            channel9Note1.setVisible(false);
        }
        if (channel10Note1 != null) {
            channel10Note1.setVisible(false);
        }

        if (channel1Note2 != null) {
            channel1Note2.setVisible(false);
        }
        if (channel2Note2 != null) {
            channel2Note2.setVisible(false);
        }
        if (channel3Note2 != null) {
            channel3Note2.setVisible(false);
        }
        if (channel4Note2 != null) {
            channel4Note2.setVisible(false);
        }
        if (channel5Note2 != null) {
            channel5Note2.setVisible(false);
        }
        if (channel6Note2 != null) {
            channel6Note2.setVisible(false);
        }
        if (channel7Note2 != null) {
            channel7Note2.setVisible(false);
        }
        if (channel8Note2 != null) {
            channel8Note2.setVisible(false);
        }
        if (channel9Note2 != null) {
            channel9Note2.setVisible(false);
        }
        if (channel10Note2 != null) {
            channel10Note2.setVisible(false);
        }

        if (channel1Note3 != null) {
            channel1Note3.setVisible(false);
        }
        if (channel2Note3 != null) {
            channel2Note3.setVisible(false);
        }
        if (channel3Note3 != null) {
            channel3Note3.setVisible(false);
        }
        if (channel4Note3 != null) {
            channel4Note3.setVisible(false);
        }
        if (channel5Note3 != null) {
            channel5Note3.setVisible(false);
        }
        if (channel6Note3 != null) {
            channel6Note3.setVisible(false);
        }
        if (channel7Note3 != null) {
            channel7Note3.setVisible(false);
        }
        if (channel8Note3 != null) {
            channel8Note3.setVisible(false);
        }
        if (channel9Note3 != null) {
            channel9Note3.setVisible(false);
        }
        if (channel10Note3 != null) {
            channel10Note3.setVisible(false);
        }

        if (channel1Note4 != null) {
            channel1Note4.setVisible(false);
        }
        if (channel2Note4 != null) {
            channel2Note4.setVisible(false);
        }
        if (channel3Note4 != null) {
            channel3Note4.setVisible(false);
        }
        if (channel4Note4 != null) {
            channel4Note4.setVisible(false);
        }
        if (channel5Note4 != null) {
            channel5Note4.setVisible(false);
        }
        if (channel6Note4 != null) {
            channel6Note4.setVisible(false);
        }
        if (channel7Note4 != null) {
            channel7Note4.setVisible(false);
        }
        if (channel8Note4 != null) {
            channel8Note4.setVisible(false);
        }
        if (channel9Note4 != null) {
            channel9Note4.setVisible(false);
        }
        if (channel10Note4 != null) {
            channel10Note4.setVisible(false);
        }
    }

    @Override
    public HarpViewNoteElement getHarpViewElement(int channel, int note) {
        return HarpViewNoteElementDesktopFX.getInstance(getNotePanel(channel, note));
    }

    @Override
    public void initNotes(NoteContainer[] noteContainers) {
        hideNotes();
        for (NoteContainer noteContainer : noteContainers) {

            Pane pane = getNotePanel(noteContainer.getChannel(), noteContainer.getNote());
            pane.setVisible(true);
            HarpViewNoteElementDesktopFX harpViewNoteElementDesktopFX = HarpViewNoteElementDesktopFX.getInstance(pane);

            if (noteContainer.isOverblow()) {
                pane.getStyleClass().add("overBlowNote");
            }
            if (noteContainer.isOverdraw()) {
                pane.getStyleClass().add("overDrawNote");
            }
            harpViewNoteElementDesktopFX.setNoteName(noteContainer.getNoteName());
            harpViewNoteElementDesktopFX.clear();
        }
    }
}
