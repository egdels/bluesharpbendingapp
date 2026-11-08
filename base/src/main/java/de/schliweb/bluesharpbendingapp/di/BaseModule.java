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

import dagger.Module;
import dagger.Provides;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import de.schliweb.bluesharpbendingapp.service.SettingsStore;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Singleton;

/**
 * Dagger module that provides base dependencies for the application. This module is responsible for
 * configuring how instances of various classes are created and managed throughout the application.
 */
@Module
public class BaseModule {

  private final SettingsStore settingsStore;
  private final Path legacySettingsFile;

  /**
   * Constructs a new BaseModule.
   *
   * @param settingsStore the platform store for the user settings
   * @param legacySettingsFile the settings file of versions up to 3.9, migrated once into the
   *     store; {@code null} if there is none
   */
  public BaseModule(SettingsStore settingsStore, Path legacySettingsFile) {
    this.settingsStore = settingsStore;
    this.legacySettingsFile = legacySettingsFile;
  }

  // In Dagger, bindings are done through @Binds methods or directly in the @Module annotation
  // No configure() method is needed

  /**
   * Provides a singleton instance of ModelStorageService.
   *
   * @return a singleton ModelStorageService instance
   */
  @Provides
  @Singleton
  public ModelStorageService provideModelStorageService() {
    return new ModelStorageService(settingsStore, legacySettingsFile);
  }

  /**
   * Provides a singleton instance of the MainModel by reading it using the provided
   * ModelStorageService.
   *
   * @param modelStorageService the service used to read and provide the MainModel instance
   * @return a singleton instance of MainModel
   */
  @Provides
  @Singleton
  public MainModel provideMainModel(ModelStorageService modelStorageService) {
    return modelStorageService.readModel();
  }

  /**
   * Provides a singleton ExecutorService for asynchronous operations.
   *
   * @return a singleton ExecutorService instance
   */
  @Provides
  @Singleton
  public ExecutorService provideExecutorService() {
    return Executors.newCachedThreadPool();
  }
}
