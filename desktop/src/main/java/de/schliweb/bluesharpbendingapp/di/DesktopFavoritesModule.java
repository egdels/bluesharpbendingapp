package de.schliweb.bluesharpbendingapp.di;
/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import dagger.Module;
import dagger.Provides;
import de.schliweb.bluesharpbendingapp.favorites.FavoriteManager;
import de.schliweb.bluesharpbendingapp.favorites.FileFavoritesStorage;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

/**
 * Desktop-specific Dagger module that provides FavoriteManager storing favorites JSON
 * in the same directory used for the model and logs (tempDirectory).
 */
@Module
public class DesktopFavoritesModule {

    private final String tempDirectory;

    public DesktopFavoritesModule(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    @Provides
    @Singleton
    public FavoriteManager provideFavoriteManager(ExecutorService executorService) {
        Path path = Paths.get(tempDirectory, "favorites_v1.json");
        return new FavoriteManager(new FileFavoritesStorage(path), executorService);

    }
}
