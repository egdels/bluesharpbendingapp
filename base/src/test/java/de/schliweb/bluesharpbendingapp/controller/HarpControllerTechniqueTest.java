/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package de.schliweb.bluesharpbendingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.ChordHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.PlayingTechniqueClassifier.Classification;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.PlayingTechniqueClassifier.Technique;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** The playing technique of the highlighted chord is derived and shown in the harp view. */
class HarpControllerTechniqueTest {

  private static final Harmonica C =
      AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);

  private static Classification classify(int note, Integer... channels) {
    return HarpController.classify(new ChordHarmonica(C, List.of(channels), note));
  }

  @Test
  void adjacentPairIsADoubleStop() {
    assertEquals(Technique.INTERVAL, classify(0, 1, 2).technique());
  }

  @Test
  void blowOneAndFourIsATongueBlockedOctave() {
    Classification c = classify(0, 1, 4);
    assertEquals(Technique.OCTAVE, c.technique());
    assertTrue(c.tongueBlocked());
    assertEquals(List.of("1B", "4B"), c.labels());
  }

  @Test
  void drawTwoAndFiveIsASplit() {
    // G4 and F5: not an octave
    Classification c = classify(1, 2, 5);
    assertEquals(Technique.SPLIT, c.technique());
    assertTrue(c.tongueBlocked());
  }

  @Test
  void threeAdjacentChannelsAreAChord() {
    Classification c = classify(0, 1, 2, 3);
    assertEquals(Technique.CHORD, c.technique());
    assertFalse(c.tongueBlocked());
  }

  private HarpView harpView;

  private HarpController controllerWithHarpView() throws Exception {
    MainModel model = mock(MainModel.class);
    when(model.getSelectedShowChordIndex()).thenReturn(1);
    when(model.getStoredKeyIndex()).thenReturn(AbstractHarmonica.KEY.C.ordinal());
    when(model.getStoredTuneIndex()).thenReturn(AbstractHarmonica.TUNE.RICHTER.ordinal());
    MainWindow window = mock(MainWindow.class);
    harpView = mock(HarpView.class);
    when(window.isHarpViewActive()).thenReturn(true);
    when(window.getHarpView()).thenReturn(harpView);
    HarpController controller =
        new HarpController(
            model, mock(ModelStorageService.class), window, mock(ExecutorService.class));
    Field containers = HarpController.class.getDeclaredField("noteContainers");
    containers.setAccessible(true);
    containers.set(controller, new NoteContainer[0]);
    return controller;
  }

  @Test
  void techniqueDisappearsAfterTheLastChordFrameWithoutFurtherFrames() throws Exception {
    HarpController controller = controllerWithHarpView();
    ChordDetectionResult octave = ChordDetectionResult.of(new double[] {261.63, 523.25}, 0.9);

    controller.updateHarpView(0, octave); // then silence: no more frames reach the controller
    Thread.sleep(HarpController.TECHNIQUE_TIMEOUT_MS + 300);

    ArgumentCaptor<Classification> shown = ArgumentCaptor.forClass(Classification.class);
    verify(harpView, times(2)).updatePlayingTechnique(shown.capture());
    assertEquals(Technique.OCTAVE, shown.getAllValues().get(0).technique());
    assertEquals(Technique.SILENCE, shown.getAllValues().get(1).technique());
  }

  @Test
  void techniqueStaysWhileChordFramesContinue() throws Exception {
    HarpController controller = controllerWithHarpView();
    ChordDetectionResult octave = ChordDetectionResult.of(new double[] {261.63, 523.25}, 0.9);

    for (int frame = 0; frame < 20; frame++) { // 1 s of chord frames
      controller.updateHarpView(0, octave);
      Thread.sleep(50);
    }

    verify(harpView, times(1)).updatePlayingTechnique(any());
  }

  @Test
  void techniqueIsShownOnlyWhenItChanges() throws Exception {
    MainModel model = mock(MainModel.class);
    when(model.getSelectedShowChordIndex()).thenReturn(1);
    when(model.getStoredKeyIndex()).thenReturn(AbstractHarmonica.KEY.C.ordinal());
    when(model.getStoredTuneIndex()).thenReturn(AbstractHarmonica.TUNE.RICHTER.ordinal());
    MainWindow window = mock(MainWindow.class);
    HarpView harpView = mock(HarpView.class);
    when(window.isHarpViewActive()).thenReturn(true);
    when(window.getHarpView()).thenReturn(harpView);
    HarpController controller =
        new HarpController(
            model, mock(ModelStorageService.class), window, mock(ExecutorService.class));
    Field containers = HarpController.class.getDeclaredField("noteContainers");
    containers.setAccessible(true);
    containers.set(controller, new NoteContainer[0]);

    ChordDetectionResult octave = ChordDetectionResult.of(new double[] {261.63, 523.25}, 0.9);
    controller.updateHarpView(0, octave);
    controller.updateHarpView(0, octave);
    controller.updateHarpView(0, new ChordDetectionResult(List.of(), 0.0));
    controller.updateHarpView(0, new ChordDetectionResult(List.of(), 0.0));

    ArgumentCaptor<Classification> shown = ArgumentCaptor.forClass(Classification.class);
    verify(harpView, times(2)).updatePlayingTechnique(shown.capture());
    assertEquals(Technique.OCTAVE, shown.getAllValues().get(0).technique());
    assertEquals(Technique.SILENCE, shown.getAllValues().get(1).technique());
    verify(harpView, times(2)).updatePlayingTechnique(any());
  }
}
