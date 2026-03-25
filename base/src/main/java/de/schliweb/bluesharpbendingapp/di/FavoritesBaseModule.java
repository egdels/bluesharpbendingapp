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
package de.schliweb.bluesharpbendingapp.di;

/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 */

import dagger.Module;
import dagger.Provides;
import de.schliweb.bluesharpbendingapp.favorites.FavoriteManager;
import de.schliweb.bluesharpbendingapp.favorites.FavoritesStorage;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;

/**
 * Base Dagger module used by the base AppComponent to provide a FavoriteManager without introducing
 * platform-specific file dependencies. This uses an in-memory storage that resets on process
 * restart. Desktop and Android override this with platform-specific modules
 * (DesktopFavoritesModule, FavoritesModule) that persist data.
 */
@Module
public class FavoritesBaseModule {

  @Provides
  @Singleton
  public FavoriteManager provideFavoriteManager(ExecutorService executorService) {
    FavoritesStorage inMemory =
        new FavoritesStorage() {
          private String json = "";

          @Override
          public String read() {
            return json;
          }

          @Override
          public void write(String s) {
            json = s == null ? "" : s;
          }
        };
    return new FavoriteManager(inMemory, executorService);
  }
}
