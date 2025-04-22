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
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;

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
    public GridPane harpGrid;
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
     * The `overlayContainer` is an AnchorPane that serves as a container for overlay elements
     * displayed dynamically on the harp view interface.
     * <p>
     * This pane is designed to adjust its dimensions based on the size of the `harpGrid`,
     * ensuring that any overlay content it holds aligns correctly with the underlying layout.
     * The pane is primarily utilized for visual enhancements and user interactions in the
     * harp view interface, such as highlighting or displaying additional information.
     * <p>
     * The `overlayContainer` is configured and updated during the initialization process in
     * the `initialize` method.
     */
    @FXML
    private AnchorPane overlayContainer;
    /**
     * A Pane element within the HarpViewDesktopFXController that displays an
     * enlarged version of a selected harp view element.
     * <p>
     * The enlargedPane is dynamically shown and manipulated during the interaction
     * with specific notes or elements within the harp grid. It is used to provide
     * a focused view of a selected component and can be closed when clicked.
     * <p>
     * This variable is managed and initialized as part of the controller's layout
     * setup and event handling logic.
     */
    @FXML
    private Pane enlargedPane;

    /**
     * Initializes the layout and event handlers for the harp view controller.
     * <p>
     * This method configures the user interface elements within the harp view,
     * ensuring that the sizes of the overlay container are dynamically bound to
     * the dimensions of the harp grid. It also sets up mouse click event handlers
     * for individual panes within the harp grid to manage user interactions.
     * <p>
     * Specifically:
     * - The `overlayContainer` is dynamically resized to match the dimensions of
     * the `harpGrid` by binding its width and height properties to those of the grid.
     * - Event handlers are attached to certain panes within the `harpGrid` to
     * detect mouse clicks, excluding panes that have the `channel` style class.
     * Clicking on such panes triggers a call to the `handlePaneClick` method for further processing.
     * - A click event is also attached to the `enlargedPane` to close it when it is clicked.
     */
    @FXML
    public void initialize() {
        overlayContainer.prefWidthProperty().bind(harpGrid.widthProperty());
        overlayContainer.prefHeightProperty().bind(harpGrid.heightProperty());

        harpGrid.getChildren().stream().filter(Pane.class::isInstance).filter(node -> !node.getStyleClass().contains("channel")).forEach(pane -> pane.setOnMouseClicked(this::handlePaneClick));

        enlargedPane.setOnMouseClicked(e -> closeEnlargedPane());
    }

    /**
     * Handles the mouse click event on a Pane within the harp grid.
     * If the overlay container is visible, it closes the enlarged pane.
     * Otherwise, it shows the enlarged pane corresponding to the clicked Pane.
     *
     * @param event the MouseEvent triggered by clicking on the Pane
     */
    private void handlePaneClick(MouseEvent event) {
        Pane clickedPane = (Pane) event.getSource();
        if (overlayContainer.isVisible()) {
            closeEnlargedPane();
            return;
        }
        showEnlargedPane(clickedPane);
    }

    /**
     * Displays an enlarged representation of a given Pane within the overlay container.
     * This method adjusts the size and position of the enlarged pane to be proportional
     * to the dimensions of the harp grid, making it dynamically responsive while centering
     * it within the overlay container.
     *
     * @param originalPane the Pane from the harp grid that is being enlarged and displayed.
     */
    private void showEnlargedPane(Pane originalPane) {

        HarpViewNoteElementDesktopFX originalInstance = HarpViewNoteElementDesktopFX.getInstance(originalPane);
        originalInstance.setEnlargedPane(enlargedPane);

        enlargedPane.setUserData(originalPane);

        DoubleBinding size = Bindings.createDoubleBinding(() -> Math.min(harpGrid.getWidth(), harpGrid.getHeight()) * 0.6, harpGrid.widthProperty(), harpGrid.heightProperty());

        enlargedPane.prefWidthProperty().unbind();
        enlargedPane.prefHeightProperty().unbind();

        enlargedPane.prefWidthProperty().bind(size);
        enlargedPane.prefHeightProperty().bind(size);

        enlargedPane.translateXProperty().bind(Bindings.createDoubleBinding(() -> (overlayContainer.getWidth() - enlargedPane.getWidth()) / 2, overlayContainer.widthProperty(), enlargedPane.widthProperty()));
        enlargedPane.translateYProperty().bind(Bindings.createDoubleBinding(() -> (overlayContainer.getHeight() - enlargedPane.getHeight()) / 2, overlayContainer.heightProperty(), enlargedPane.heightProperty()));

        overlayContainer.setVisible(true);
        enlargedPane.setVisible(true);
        animateEnlargedPane(enlargedPane);
    }

    /**
     * Animates the enlargement of a specified Pane using a scaling animation.
     * The Pane is scaled up from a smaller size to its full size over a specified duration.
     *
     * @param pane the Pane to be animated with the enlargement effect
     */
    private void animateEnlargedPane(Pane pane) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), pane);
        scaleTransition.setFromX(0.1);
        scaleTransition.setFromY(0.1);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    /**
     * Closes the enlarged pane within the overlay container, if visible, using a scaling animation.
     * <p>
     * This method checks if the `overlayContainer` is currently visible. If it is:
     * - A scaling animation is applied to the `enlargedPane`, reducing its size to minimal proportions.
     * - Once the animation completes, the visibility of the `enlargedPane` is set to false.
     * - If the `enlargedPane`'s user data contains a reference to another pane, it resets the
     * enlarged pane reference for that specific pane.
     * - Finally, the visibility of the `overlayContainer` is set to false.
     * <p>
     * The animation duration is set to 200 milliseconds, creating a smooth shrinking effect.
     */
    private void closeEnlargedPane() {
        if (overlayContainer.isVisible()) {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), enlargedPane);
            scaleTransition.setToX(0.1);
            scaleTransition.setToY(0.1);
            scaleTransition.setOnFinished(e -> {
                enlargedPane.setVisible(false);

                // Check if userData is an instance of Pane
                if (enlargedPane.getUserData() instanceof Pane pane) {
                    HarpViewNoteElementDesktopFX.getInstance(pane).setEnlargedPane(null);
                }

                overlayContainer.setVisible(false);
            });
            scaleTransition.play();
        }
    }

    /**
     * Retrieves the pane corresponding to the specified channel and note.
     *
     * @param channel the channel number (1-10) for which the Pane is to be retrieved
     * @param note    the note value (-3 to 4) for which the Pane is to be retrieved
     * @return the Pane associated with the specified channel and note
     */
    private Pane getNotePanel(int channel, int note) {
        Pane[][] panes = new Pane[10][8];

        panes[0][0] = channel1NoteM3;
        panes[0][1] = channel1NoteM2;
        panes[0][2] = channel1NoteM1;
        panes[0][3] = channel1Note0;
        panes[0][4] = channel1Note1;
        panes[0][5] = channel1Note2;
        panes[0][6] = channel1Note3;
        panes[0][7] = channel1Note4;

        panes[1][0] = channel2NoteM3;
        panes[1][1] = channel2NoteM2;
        panes[1][2] = channel2NoteM1;
        panes[1][3] = channel2Note0;
        panes[1][4] = channel2Note1;
        panes[1][5] = channel2Note2;
        panes[1][6] = channel2Note3;
        panes[1][7] = channel2Note4;

        panes[2][0] = channel3NoteM3;
        panes[2][1] = channel3NoteM2;
        panes[2][2] = channel3NoteM1;
        panes[2][3] = channel3Note0;
        panes[2][4] = channel3Note1;
        panes[2][5] = channel3Note2;
        panes[2][6] = channel3Note3;
        panes[2][7] = channel3Note4;

        panes[3][0] = channel4NoteM3;
        panes[3][1] = channel4NoteM2;
        panes[3][2] = channel4NoteM1;
        panes[3][3] = channel4Note0;
        panes[3][4] = channel4Note1;
        panes[3][5] = channel4Note2;
        panes[3][6] = channel4Note3;
        panes[3][7] = channel4Note4;

        panes[4][0] = channel5NoteM3;
        panes[4][1] = channel5NoteM2;
        panes[4][2] = channel5NoteM1;
        panes[4][3] = channel5Note0;
        panes[4][4] = channel5Note1;
        panes[4][5] = channel5Note2;
        panes[4][6] = channel5Note3;
        panes[4][7] = channel5Note4;

        panes[5][0] = channel6NoteM3;
        panes[5][1] = channel6NoteM2;
        panes[5][2] = channel6NoteM1;
        panes[5][3] = channel6Note0;
        panes[5][4] = channel6Note1;
        panes[5][5] = channel6Note2;
        panes[5][6] = channel6Note3;
        panes[5][7] = channel6Note4;

        panes[6][0] = channel7NoteM3;
        panes[6][1] = channel7NoteM2;
        panes[6][2] = channel7NoteM1;
        panes[6][3] = channel7Note0;
        panes[6][4] = channel7Note1;
        panes[6][5] = channel7Note2;
        panes[6][6] = channel7Note3;
        panes[6][7] = channel7Note4;

        panes[7][0] = channel8NoteM3;
        panes[7][1] = channel8NoteM2;
        panes[7][2] = channel8NoteM1;
        panes[7][3] = channel8Note0;
        panes[7][4] = channel8Note1;
        panes[7][5] = channel8Note2;
        panes[7][6] = channel8Note3;
        panes[7][7] = channel8Note4;

        panes[8][0] = channel9NoteM3;
        panes[8][1] = channel9NoteM2;
        panes[8][2] = channel9NoteM1;
        panes[8][3] = channel9Note0;
        panes[8][4] = channel9Note1;
        panes[8][5] = channel9Note2;
        panes[8][6] = channel9Note3;
        panes[8][7] = channel9Note4;

        panes[9][0] = channel10NoteM3;
        panes[9][1] = channel10NoteM2;
        panes[9][2] = channel10NoteM1;
        panes[9][3] = channel10Note0;
        panes[9][4] = channel10Note1;
        panes[9][5] = channel10Note2;
        panes[9][6] = channel10Note3;
        panes[9][7] = channel10Note4;

        return panes[channel - 1][note + 3];
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
        List<Pane[]> allNotes = List.of(new Pane[]{channel1NoteM3, channel2NoteM3, channel3NoteM3, channel4NoteM3, channel5NoteM3, channel6NoteM3, channel7NoteM3, channel8NoteM3, channel9NoteM3, channel10NoteM3}, new Pane[]{channel1NoteM2, channel2NoteM2, channel3NoteM2, channel4NoteM2, channel5NoteM2, channel6NoteM2, channel7NoteM2, channel8NoteM2, channel9NoteM2, channel10NoteM2}, new Pane[]{channel1NoteM1, channel2NoteM1, channel3NoteM1, channel4NoteM1, channel5NoteM1, channel6NoteM1, channel7NoteM1, channel8NoteM1, channel9NoteM1, channel10NoteM1}, new Pane[]{channel1Note0, channel2Note0, channel3Note0, channel4Note0, channel5Note0, channel6Note0, channel7Note0, channel8Note0, channel9Note0, channel10Note0}, new Pane[]{channel1Note1, channel2Note1, channel3Note1, channel4Note1, channel5Note1, channel6Note1, channel7Note1, channel8Note1, channel9Note1, channel10Note1}, new Pane[]{channel1Note2, channel2Note2, channel3Note2, channel4Note2, channel5Note2, channel6Note2, channel7Note2, channel8Note2, channel9Note2, channel10Note2}, new Pane[]{channel1Note3, channel2Note3, channel3Note3, channel4Note3, channel5Note3, channel6Note3, channel7Note3, channel8Note3, channel9Note3, channel10Note3}, new Pane[]{channel1Note4, channel2Note4, channel3Note4, channel4Note4, channel5Note4, channel6Note4, channel7Note4, channel8Note4, channel9Note4, channel10Note4});
        for (Pane[] notes : allNotes) {
            for (Pane note : notes) {
                if (note != null) {
                    note.setVisible(false);
                }
            }
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

            harpViewNoteElementDesktopFX.init();

            if (noteContainer.isOverblow()) harpViewNoteElementDesktopFX.setOverblow();
            if (noteContainer.isOverdraw()) harpViewNoteElementDesktopFX.setOverdraw();

            harpViewNoteElementDesktopFX.setNoteName(noteContainer.getNoteName());
            harpViewNoteElementDesktopFX.clear();
        }
    }
}
