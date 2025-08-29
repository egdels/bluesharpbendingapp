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
import de.schliweb.bluesharpbendingapp.app.MainActivity;

import javax.inject.Singleton;

/**
 * Dagger component for the Android application.
 * This component extends the base AppComponent and adds Android-specific modules.
 */
@Singleton
@Component(modules = {BaseModule.class, ControllerModule.class, ViewModule.class, MicrophoneModule.class, FavoritesModule.class})
public interface AndroidAppComponent extends AppComponent {

    de.schliweb.bluesharpbendingapp.favorites.FavoriteManager provideFavoriteManager();

    /**
     * Injects dependencies into the specified MainActivity instance.
     *
     * @param activity the MainActivity instance to inject dependencies into
     */
    void inject(MainActivity activity);

    /**
     * Builder for creating instances of AndroidAppComponent.
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
         * Sets the FavoritesModule for this component.
         *
         * @param favoritesModule the FavoritesModule instance
         * @return this builder
         */
        Builder favoritesModule(FavoritesModule favoritesModule);


        /**
         * Builds the AndroidAppComponent.
         *
         * @return the built AndroidAppComponent
         */
        AndroidAppComponent build();
    }
}
