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

import de.schliweb.bluesharpbendingapp.service.InMemorySettingsStore;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * The default pitch algorithm is MPM for new installations, while users who already stored a
 * selection (including the former default YIN) keep it.
 */
class DefaultAlgorithmTest {

  private static MainModel load(String stored) throws Exception {
    // Settings stored by versions up to 3.9 (legacy file) are migrated with their values.
    Path file = Files.createTempDirectory("model").resolve("Model.tmp");
    Files.writeString(file, stored, StandardCharsets.UTF_8);
    return new ModelStorageService(new InMemorySettingsStore(), file).readModel();
  }

  @Test
  void newInstallationDefaultsToMpm() {
    assertEquals(1, new MainModel().getStoredAlgorithmIndex());
    assertEquals(1, new MainModel().getSelectedAlgorithmIndex());
  }

  @Test
  void storedYinSelectionIsKept() throws Exception {
    MainModel model = load("[getStoredAlgorithmIndex:0, getStoredKeyIndex:4]");
    assertEquals(0, model.getStoredAlgorithmIndex());
    assertEquals(4, model.getStoredKeyIndex());
  }

  @Test
  void storedHybridSelectionIsKept() throws Exception {
    assertEquals(2, load("[getStoredAlgorithmIndex:2]").getStoredAlgorithmIndex());
  }
}
