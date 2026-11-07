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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Key, tuning and concert pitch changes must reach the chord engine (Basic Pitch mapping). */
class HarpControllerChordEngineTest {

  private HarpController harpController;
  private MicrophoneController microphoneController;

  @BeforeEach
  void setUp() {
    harpController =
        new HarpController(
            mock(MainModel.class),
            mock(ModelStorageService.class),
            mock(MainWindow.class),
            mock(ExecutorService.class));
    microphoneController = mock(MicrophoneController.class);
    harpController.setMicrophoneController(microphoneController);
  }

  @AfterEach
  void tearDown() {
    NoteLookup.setConcertPitch(440); // the default concert pitch
  }

  @Test
  void keySelectionUpdatesTheChordEngine() {
    harpController.handleKeySelection(2);
    verify(microphoneController).updateChordEngineHarmonica();
  }

  @Test
  void tuneSelectionUpdatesTheChordEngine() {
    harpController.handleTuneSelection(1);
    verify(microphoneController).updateChordEngineHarmonica();
  }

  @Test
  void concertPitchSelectionUpdatesTheChordEngine() {
    harpController.handleConcertPitchSelection(11); // 440 Hz, the default
    verify(microphoneController).updateChordEngineHarmonica();
  }
}
