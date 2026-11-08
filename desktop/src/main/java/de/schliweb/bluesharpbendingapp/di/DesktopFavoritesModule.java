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
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;

/**
 * Desktop-specific Dagger module that provides FavoriteManager storing favorites JSON in the data
 * directory of the application. Favorites of versions up to 3.9, kept in the legacy directory, are
 * copied there once.
 */
@Module
public class DesktopFavoritesModule {

  static final String FAVORITES_FILE = "favorites_v1.json";

  private final Path dataDirectory;

  private final Path legacyDirectory;

  /**
   * Creates the module.
   *
   * @param dataDirectory the directory for the favorites file
   * @param legacyDirectory the directory of versions up to 3.9, or {@code null}
   */
  public DesktopFavoritesModule(Path dataDirectory, Path legacyDirectory) {
    this.dataDirectory = dataDirectory;
    this.legacyDirectory = legacyDirectory;
  }

  @Provides
  @Singleton
  public FavoriteManager provideFavoriteManager(ExecutorService executorService) {
    Path path = dataDirectory.resolve(FAVORITES_FILE);
    migrateLegacyFavorites(legacyDirectory, path);
    return new FavoriteManager(new FileFavoritesStorage(path), executorService);
  }

  /**
   * Copies the favorites file of versions up to 3.9 to the new location, unless a file exists there
   * already. The old file stays in place.
   *
   * @param legacyDirectory the directory of versions up to 3.9, or {@code null}
   * @param target the new favorites file
   */
  static void migrateLegacyFavorites(Path legacyDirectory, Path target) {
    if (legacyDirectory == null || Files.exists(target)) {
      return;
    }
    Path legacy = legacyDirectory.resolve(FAVORITES_FILE);
    if (!Files.isRegularFile(legacy)) {
      return;
    }
    try {
      Files.createDirectories(target.getParent());
      Files.copy(legacy, target);
      LoggingUtils.logDebug("Favorites migrated", legacy + " -> " + target);
    } catch (IOException e) {
      LoggingUtils.logError("Failed to migrate favorites", e);
    }
  }
}
