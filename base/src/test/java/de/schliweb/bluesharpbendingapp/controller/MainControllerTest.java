package de.schliweb.bluesharpbendingapp.controller;
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

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class MainControllerTest {

    private MainController mainController;
    private MainModel model;
    private Microphone microphone;
    private ModelStorageService modelStorageService;
    private MainWindow mainWindow;
    private MicrophoneController microphoneController;
    private HarpController harpController;
    private TrainingController trainingController;

    @BeforeEach
    void setup() {
        model = mock(MainModel.class);
        microphone = mock(Microphone.class);
        modelStorageService = mock(ModelStorageService.class);
        mainWindow = mock(MainWindow.class);
        harpController = mock(HarpController.class);
        trainingController = mock(TrainingController.class);
        microphoneController = mock(MicrophoneController.class);

        when(modelStorageService.readModel()).thenReturn(model);
        /*
        mainController = new MainController(
                model,
                mainWindow,
                modelStorageService,
                microphoneController
        );*/
    }

    // Commented out due to handleAlgorithmSelection method not existing in MainController
     /*
     @Test
     void testHandleAlgorithmSelectionUpdatesModel() {
         int algorithmIndex = 2;

         mainController.handleAlgorithmSelection(algorithmIndex);

         verify(model).setStoredAlgorithmIndex(algorithmIndex);
         verify(model).setSelectedAlgorithmIndex(algorithmIndex);
     }

     @Test
     void testHandleAlgorithmSelectionUpdatesMicrophone() {
         int algorithmIndex = 1;

         mainController.handleAlgorithmSelection(algorithmIndex);

         verify(microphone).close();
         verify(microphone).setAlgorithm(algorithmIndex);
         verify(microphone).open();
     }

     @Test
     void testHandleAlgorithmSelectionStoresModel() {
         int algorithmIndex = 3;

         mainController.handleAlgorithmSelection(algorithmIndex);

         verify(modelStorageService).storeModel(model);
     }
     @Test
     void testHandleAlgorithmSelectionLogsCorrectly() {
         int algorithmIndex = 2;

         // Call method
         mainController.handleAlgorithmSelection(algorithmIndex);

         // No mock verification is performed, but this ensures that execution passes and logs
         assertDoesNotThrow(() -> mainController.handleAlgorithmSelection(algorithmIndex));
     }
     */

}
