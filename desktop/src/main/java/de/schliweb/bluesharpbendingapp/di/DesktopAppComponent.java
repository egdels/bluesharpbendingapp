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
import de.schliweb.bluesharpbendingapp.view.desktop.MainWindowDesktopFXController;

import javax.inject.Singleton;

/**
 * Dagger component for the Desktop application.
 * This component extends the base AppComponent and adds Desktop-specific modules.
 */
@Singleton
@Component(modules = {BaseModule.class, ControllerModule.class, ViewModule.class, MicrophoneModule.class, DesktopFavoritesModule.class})
public interface DesktopAppComponent extends AppComponent {

    /**
     * Injects dependencies into the specified MainWindowDesktopFXController instance.
     *
     * @param controller the MainWindowDesktopFXController instance to inject dependencies into
     */
    void inject(MainWindowDesktopFXController controller);

    /**
     * Builder for creating instances of DesktopAppComponent.
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
         * Sets the DesktopFavoritesModule for this component.
         *
         * @param desktopFavoritesModule the DesktopFavoritesModule instance
         * @return this builder
         */
        Builder desktopFavoritesModule(DesktopFavoritesModule desktopFavoritesModule);

        /**
         * Builds the DesktopAppComponent.
         *
         * @return the built DesktopAppComponent
         */
        DesktopAppComponent build();
    }
}
