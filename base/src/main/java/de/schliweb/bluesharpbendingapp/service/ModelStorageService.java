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
package de.schliweb.bluesharpbendingapp.service;

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.microphone.AbstractMicrophone;
import de.schliweb.bluesharpbendingapp.model.training.AbstractTraining;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * Persists the user settings of {@link MainModel} in a platform {@link SettingsStore}.
 *
 * <p>Each setting has its own key. Choices from a fixed list are stored by their meaning (e.g.
 * {@code algorithm=MPM}, {@code tune=RICHTER}, {@code concertPitch=442}), not by their position in
 * the list, so reordering or extending a list does not change what a stored setting means. Unknown
 * or invalid values are ignored and the default applies.
 *
 * <p>Up to version 3.9 the settings were written as one line of list indices to a file ({@code
 * Model.tmp}). If the store is still empty, that file is read once, its settings are copied into
 * the store and the file is renamed to {@code Model.tmp.migrated}.
 */
public class ModelStorageService {

  /** Key of the format version; its presence marks an initialised store. */
  static final String VERSION_KEY = "settings.version";

  static final String VERSION = "1";

  /** Suffix of the legacy settings file once its content was copied into the store. */
  static final String MIGRATED_SUFFIX = ".migrated";

  /**
   * One persisted setting: its key, how to read and write the index in the model, and the stable
   * values of the list entries ({@code null}: the index itself is stored, e.g. for on/off switches
   * and the microphone, whose list depends on the connected devices).
   */
  private record Setting(
      String key,
      ToIntFunction<MainModel> getter,
      ObjIntConsumer<MainModel> setter,
      List<String> values) {}

  private static final List<Setting> SETTINGS =
      List.of(
          new Setting(
              "key",
              MainModel::getStoredKeyIndex,
              MainModel::setStoredKeyIndex,
              List.of(AbstractHarmonica.getSupporterKeys())),
          new Setting(
              "tune",
              MainModel::getStoredTuneIndex,
              MainModel::setStoredTuneIndex,
              List.of(AbstractHarmonica.getSupportedTunes())),
          new Setting(
              "concertPitch",
              MainModel::getStoredConcertPitchIndex,
              MainModel::setStoredConcertPitchIndex,
              List.of(NoteLookup.getSupportedConcertPitches())),
          new Setting(
              "notation",
              MainModel::getStoredNotationIndex,
              MainModel::setStoredNotationIndex,
              List.of("SHARP", "FLAT")),
          new Setting(
              "algorithm",
              MainModel::getStoredAlgorithmIndex,
              MainModel::setStoredAlgorithmIndex,
              List.of(AbstractMicrophone.getSupportedAlgorithms())),
          new Setting(
              "confidence",
              MainModel::getStoredConfidenceIndex,
              MainModel::setStoredConfidenceIndex,
              List.of(AbstractMicrophone.getSupportedConfidences())),
          new Setting(
              "microphoneIndex",
              MainModel::getStoredMicrophoneIndex,
              MainModel::setStoredMicrophoneIndex,
              null),
          new Setting(
              "chordEngine",
              MainModel::getStoredChordEngineIndex,
              MainModel::setStoredChordEngineIndex,
              List.of("CHORD_DETECTOR", "BASIC_PITCH")),
          new Setting(
              "chordConfidence",
              MainModel::getStoredChordConfidenceIndex,
              MainModel::setStoredChordConfidenceIndex,
              List.of(AbstractMicrophone.getSupportedChordConfidences())),
          new Setting(
              "showChordIndex",
              MainModel::getStoredShowChordIndex,
              MainModel::setStoredShowChordIndex,
              null),
          new Setting(
              "lockScreenIndex",
              MainModel::getStoredLockScreenIndex,
              MainModel::setStoredLockScreenIndex,
              null),
          new Setting(
              "training",
              MainModel::getStoredTrainingIndex,
              MainModel::setStoredTrainingIndex,
              List.of(AbstractTraining.getSupportedTrainings())),
          new Setting(
              "precision",
              MainModel::getStoredPrecisionIndex,
              MainModel::setStoredPrecisionIndex,
              List.of(AbstractTraining.getSupportedPrecisions())));

  private final SettingsStore store;

  private final Path legacyFile;

  /**
   * Creates the service.
   *
   * @param store the platform store for the settings
   * @param legacyFile the settings file of versions up to 3.9, read once if the store is empty;
   *     {@code null} if there is none
   */
  public ModelStorageService(SettingsStore store, Path legacyFile) {
    this.store = store;
    this.legacyFile = legacyFile;
  }

  /**
   * Stores the settings of the model.
   *
   * @param model the model
   */
  public synchronized void storeModel(MainModel model) {
    LoggingContext.setComponent("ModelStorageService");
    LoggingUtils.logOperationStarted("Model storage");
    for (Setting setting : SETTINGS) {
      int index = setting.getter().applyAsInt(model);
      if (setting.values() == null) {
        store.put(setting.key(), Integer.toString(index));
      } else if (index >= 0 && index < setting.values().size()) {
        store.put(setting.key(), setting.values().get(index));
      } else {
        LoggingUtils.logWarning("Setting out of range, not stored", setting.key() + "=" + index);
      }
    }
    store.put(VERSION_KEY, VERSION);
    store.flush();
    LoggingUtils.logOperationCompleted("Model storage");
  }

  /**
   * Reads the settings into a new model. Settings that are missing or invalid keep their default.
   * On the first start after an update, the settings of the legacy file are migrated.
   *
   * @return the model
   */
  public synchronized MainModel readModel() {
    LoggingContext.setComponent("ModelStorageService");
    LoggingUtils.logOperationStarted("Model loading");
    MainModel model = new MainModel();
    if (store.get(VERSION_KEY) == null) {
      if (legacyFile != null && Files.isRegularFile(legacyFile)) {
        migrateLegacyFile(model);
      } else {
        LoggingUtils.logDebug("No stored settings", "Using defaults");
      }
    } else {
      for (Setting setting : SETTINGS) {
        String value = store.get(setting.key());
        if (value != null) {
          int index = toIndex(setting, value);
          if (index >= 0) {
            setting.setter().accept(model, index);
          } else {
            LoggingUtils.logWarning(
                "Unknown stored setting, using default", setting.key() + "=" + value);
          }
        }
      }
    }
    LoggingUtils.logOperationCompleted("Model loading");
    return model;
  }

  private static int toIndex(Setting setting, String value) {
    if (setting.values() == null) {
      try {
        int index = Integer.parseInt(value);
        return index >= 0 ? index : -1;
      } catch (NumberFormatException e) {
        return -1;
      }
    }
    return setting.values().indexOf(value);
  }

  /** Copies the settings of the legacy file into the model and the store, then renames it. */
  private void migrateLegacyFile(MainModel model) {
    String line;
    try (BufferedReader reader = Files.newBufferedReader(legacyFile, StandardCharsets.UTF_8)) {
      line = reader.readLine();
    } catch (IOException e) {
      LoggingUtils.logError("Failed to read legacy settings file", e);
      return;
    }
    if (line != null) {
      applyLegacyLine(model, line);
    }
    storeModel(model);
    try {
      Files.move(
          legacyFile,
          legacyFile.resolveSibling(legacyFile.getFileName() + MIGRATED_SUFFIX),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      // The store is initialised now, so the file is not read again anyway.
      LoggingUtils.logWarning("Could not rename legacy settings file", e.getMessage());
    }
    LoggingUtils.logDebug("Settings migrated from legacy file", legacyFile.toString());
  }

  /**
   * Applies a line of the legacy format, e.g. {@code [getStoredKeyIndex:4,
   * getStoredAlgorithmIndex:0]}. Entries that are unknown or not a number are ignored.
   *
   * @param model the model to update
   * @param line the line
   */
  static void applyLegacyLine(MainModel model, String line) {
    for (String entry : line.replace("[", "").replace("]", "").split(", ", -1)) {
      int colon = entry.indexOf(':');
      if (colon < 0) {
        continue;
      }
      String getter = entry.substring(0, colon).trim();
      int index;
      try {
        index = Integer.parseInt(entry.substring(colon + 1).trim());
      } catch (NumberFormatException e) {
        continue;
      }
      for (Setting setting : SETTINGS) {
        if (getter.equals(legacyGetterName(setting))) {
          setting.setter().accept(model, index);
        }
      }
    }
  }

  /** The getter name the legacy format used for a setting, e.g. {@code getStoredKeyIndex}. */
  private static String legacyGetterName(Setting setting) {
    String key = setting.key();
    String base = key.endsWith("Index") ? key.substring(0, key.length() - 5) : key;
    return "getStored" + Character.toUpperCase(base.charAt(0)) + base.substring(1) + "Index";
  }
}
