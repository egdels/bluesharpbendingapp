package de.schliweb.bluesharpbendingapp.di;
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

import dagger.Component;
import de.schliweb.bluesharpbendingapp.controller.MainController;
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.view.MainWindow;

import javax.inject.Singleton;

/**
 * Dagger component that provides application-wide dependencies.
 * This component is responsible for creating and providing instances
 * of various classes throughout the application.
 */
@Singleton
@Component(modules = {BaseModule.class, ControllerModule.class, ViewModule.class, MicrophoneModule.class})
public interface AppComponent {

    /**
     * Gets the MainController instance.
     *
     * @return the MainController instance
     */
    MainController getMainController();

    /**
     * Gets the ModelStorageService instance.
     *
     * @return the ModelStorageService instance
     */
    ModelStorageService getModelStorageService();

    /**
     * Gets the Microphone instance.
     *
     * @return the Microphone instance
     */
    Microphone getMicrophone();

    /**
     * Gets the MainWindow instance.
     *
     * @return the MainWindow instance
     */
    MainWindow getMainWindow();

    /**
     * Builder for creating instances of AppComponent.
     */
    @Component.Builder
    interface Builder {
        /**
         * Sets the BaseModule for this component.
         *
         * @param baseModule the BaseModule instance
         * @return this builder
         */
        Builder baseModule(BaseModule baseModule);

        /**
         * Sets the ViewModule for this component.
         *
         * @param viewModule the ViewModule instance
         * @return this builder
         */
        Builder viewModule(ViewModule viewModule);

        /**
         * Sets the MicrophoneModule for this component.
         *
         * @param microphoneModule the MicrophoneModule instance
         * @return this builder
         */
        Builder microphoneModule(MicrophoneModule microphoneModule);

        /**
         * Builds the AppComponent.
         *
         * @return the built AppComponent
         */
        AppComponent build();
    }
}
