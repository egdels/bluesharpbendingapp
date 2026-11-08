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
package de.schliweb.bluesharpbendingapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.service.InMemorySettingsStore;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ModelStorageServiceTest {

  @TempDir Path dir;

  @Test
  void emptyStoreWithoutLegacyFileGivesDefaults() {
    MainModel model =
        new ModelStorageService(new InMemorySettingsStore(), dir.resolve("Model.tmp")).readModel();
    MainModel defaults = new MainModel();
    assertEquals(defaults.getStoredAlgorithmIndex(), model.getStoredAlgorithmIndex());
    assertEquals(defaults.getStoredKeyIndex(), model.getStoredKeyIndex());
    assertEquals(defaults.getStoredConcertPitchIndex(), model.getStoredConcertPitchIndex());
  }

  @Test
  void settingsAreStoredByMeaningAndReadBack() {
    InMemorySettingsStore store = new InMemorySettingsStore();
    MainModel model = new MainModel();
    model.setStoredAlgorithmIndex(2); // HYBRID
    model.setStoredKeyIndex(0); // first KEY constant
    model.setStoredTuneIndex(6);
    model.setStoredConcertPitchIndex(9); // 440
    model.setStoredNotationIndex(1); // flat
    model.setStoredChordEngineIndex(1); // Basic Pitch
    model.setStoredMicrophoneIndex(3);
    model.setStoredShowChordIndex(1);
    new ModelStorageService(store, null).storeModel(model);

    assertEquals("HYBRID", store.get("algorithm"));
    assertEquals("440", store.get("concertPitch"));
    assertEquals("FLAT", store.get("notation"));
    assertEquals("BASIC_PITCH", store.get("chordEngine"));
    assertEquals("3", store.get("microphoneIndex"));

    MainModel read = new ModelStorageService(store, null).readModel();
    assertEquals(2, read.getStoredAlgorithmIndex());
    assertEquals(0, read.getStoredKeyIndex());
    assertEquals(6, read.getStoredTuneIndex());
    assertEquals(9, read.getStoredConcertPitchIndex());
    assertEquals(1, read.getStoredNotationIndex());
    assertEquals(1, read.getStoredChordEngineIndex());
    assertEquals(3, read.getStoredMicrophoneIndex());
    assertEquals(1, read.getStoredShowChordIndex());
  }

  @Test
  void unknownOrInvalidValuesKeepTheDefault() {
    InMemorySettingsStore store = new InMemorySettingsStore();
    new ModelStorageService(store, null).storeModel(new MainModel());
    store.put("algorithm", "SOMETHING_NEW");
    store.put("concertPitch", "999");
    store.put("microphoneIndex", "-1");
    store.put("showChordIndex", "abc");

    MainModel read = new ModelStorageService(store, null).readModel();
    MainModel defaults = new MainModel();
    assertEquals(defaults.getStoredAlgorithmIndex(), read.getStoredAlgorithmIndex());
    assertEquals(defaults.getStoredConcertPitchIndex(), read.getStoredConcertPitchIndex());
    assertEquals(defaults.getStoredMicrophoneIndex(), read.getStoredMicrophoneIndex());
    assertEquals(defaults.getStoredShowChordIndex(), read.getStoredShowChordIndex());
  }

  @Test
  void legacyFileIsMigratedOnceAndRenamed() throws Exception {
    Path legacy = dir.resolve("Model.tmp");
    // The line written by version 3.9 on the user's machine.
    Files.writeString(
        legacy,
        "[getStoredTuneIndex:6, getStoredKeyIndex:4, getStoredAlgorithmIndex:0,"
            + " getStoredMicrophoneIndex:1, getStoredConcertPitchIndex:11,"
            + " getStoredPrecisionIndex:0, getStoredConfidenceIndex:0,"
            + " getStoredChordConfidenceIndex:3, getStoredChordEngineIndex:1,"
            + " getStoredLockScreenIndex:0, getStoredShowChordIndex:1, getStoredTrainingIndex:4,"
            + " getStoredNotationIndex:0]",
        StandardCharsets.UTF_8);
    InMemorySettingsStore store = new InMemorySettingsStore();

    MainModel model = new ModelStorageService(store, legacy).readModel();
    assertEquals(0, model.getStoredAlgorithmIndex());
    assertEquals(4, model.getStoredKeyIndex());
    assertEquals(1, model.getStoredMicrophoneIndex());
    assertEquals(11, model.getStoredConcertPitchIndex());
    assertEquals(1, model.getStoredChordEngineIndex());
    assertEquals(1, model.getStoredShowChordIndex());
    assertEquals(4, model.getStoredTrainingIndex());

    assertEquals("YIN", store.get("algorithm"));
    assertEquals("442", store.get("concertPitch"));
    assertFalse(Files.exists(legacy), "legacy file must be renamed");
    assertTrue(Files.exists(dir.resolve("Model.tmp.migrated")));

    // A later start reads the store, even if an old file shows up again.
    Files.writeString(legacy, "[getStoredAlgorithmIndex:2]", StandardCharsets.UTF_8);
    assertEquals(0, new ModelStorageService(store, legacy).readModel().getStoredAlgorithmIndex());
  }

  @Test
  void emptyLegacyFileGivesDefaultsAndInitialisesTheStore() throws Exception {
    Path legacy = dir.resolve("Model.tmp");
    Files.writeString(legacy, "", StandardCharsets.UTF_8);
    InMemorySettingsStore store = new InMemorySettingsStore();
    MainModel model = new ModelStorageService(store, legacy).readModel();
    assertEquals(new MainModel().getStoredAlgorithmIndex(), model.getStoredAlgorithmIndex());
    assertEquals("MPM", store.get("algorithm"));
  }

  @Test
  void storeIsEmptyBeforeTheFirstStore() {
    InMemorySettingsStore store = new InMemorySettingsStore();
    new ModelStorageService(store, null).readModel();
    assertNull(store.get("algorithm"));
  }
}
