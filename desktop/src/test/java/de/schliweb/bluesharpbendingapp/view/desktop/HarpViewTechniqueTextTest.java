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
package de.schliweb.bluesharpbendingapp.view.desktop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.utils.I18nUtils;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.PlayingTechniqueClassifier.Classification;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.PlayingTechniqueClassifier.Technique;
import java.util.List;
import org.junit.jupiter.api.Test;

class HarpViewTechniqueTextTest {

  @Test
  void octaveShowsNameChannelsAndTongueBlock() {
    String text =
        HarpViewDesktopFXController.describeTechnique(
            new Classification(Technique.OCTAVE, true, List.of("1B", "4B")));
    assertEquals(
        I18nUtils.getString("harp.technique.octave")
            + " 1B 4B ("
            + I18nUtils.getString("harp.technique.tongueblock")
            + ")",
        text);
  }

  @Test
  void singleNotesAndSilenceShowNothing() {
    assertEquals(
        "",
        HarpViewDesktopFXController.describeTechnique(
            new Classification(Technique.SINGLE_NOTE, false, List.of("4D"))));
    assertEquals(
        "",
        HarpViewDesktopFXController.describeTechnique(
            new Classification(Technique.SILENCE, false, List.of())));
  }

  @Test
  void allTechniqueKeysExist() {
    for (String key : List.of("interval", "octave", "split", "chord", "tongueblock")) {
      assertTrue(I18nUtils.getResourceBundle().containsKey("harp.technique." + key), key);
    }
  }
}
