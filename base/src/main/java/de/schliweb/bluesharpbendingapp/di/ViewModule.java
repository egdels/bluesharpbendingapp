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

import dagger.Module;
import dagger.Provides;
import de.schliweb.bluesharpbendingapp.view.MainWindow;

import javax.inject.Singleton;

/**
 * Dagger module that provides view-related dependencies for the application.
 * This module is responsible for providing the MainWindow instance.
 */
@Module
public class ViewModule {

    private final MainWindow mainWindow;

    /**
     * Constructs a new ViewModule with the specified MainWindow.
     *
     * @param mainWindow the MainWindow instance to provide
     */
    public ViewModule(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Provides a singleton instance of MainWindow.
     *
     * @return a singleton MainWindow instance
     */
    @Provides
    @Singleton
    public MainWindow provideMainWindow() {
        return mainWindow;
    }
}