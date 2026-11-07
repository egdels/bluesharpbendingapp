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
package de.schliweb.bluesharpbendingapp.utils.basicpitch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.utils.basicpitch.HarpTone.Direction;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.PlayingTechniqueClassifier.Classification;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.PlayingTechniqueClassifier.Technique;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link PlayingTechniqueClassifier}. */
class PlayingTechniqueClassifierTest {

  private static ActiveChannel ch(int channel, Direction direction, int midi) {
    return new ActiveChannel(new HarpTone(channel, direction, 0, midi), 1.0, 0.0);
  }

  @Test
  void emptyOrNullIsSilence() {
    assertEquals(
        Technique.SILENCE,
        PlayingTechniqueClassifier.classify((List<ActiveChannel>) null).technique());
    assertEquals(Technique.SILENCE, PlayingTechniqueClassifier.classify(List.of()).technique());
    assertEquals(
        Technique.SILENCE, PlayingTechniqueClassifier.classify((MappingResult) null).technique());
  }

  @Test
  void singleChannelIsSingleNote() {
    Classification c = PlayingTechniqueClassifier.classify(List.of(ch(4, Direction.BLOW, 72)));
    assertEquals(Technique.SINGLE_NOTE, c.technique());
    assertFalse(c.tongueBlocked());
    assertEquals(List.of("4B"), c.labels());
  }

  @Test
  void enharmonicAlternativesAreSingleNote() {
    // G4 (midi 67) exists twice on a Richter C harp (2D and 3B); the mapper reports both channels
    // as active, but it is still a single sounding note.
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(ch(2, Direction.DRAW, 67), ch(3, Direction.BLOW, 67)));
    assertEquals(Technique.SINGLE_NOTE, c.technique());
    assertFalse(c.tongueBlocked());
    assertEquals(List.of("2D", "3B"), c.labels());
  }

  @Test
  void adjacentSameDirectionIsInterval() {
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(ch(4, Direction.BLOW, 72), ch(5, Direction.BLOW, 76)));
    assertEquals(Technique.INTERVAL, c.technique());
    assertFalse(c.tongueBlocked());
  }

  @Test
  void octaveSplit1B4BIsOctave() {
    // C-harp: 1B = C4 (midi 60), 4B = C5 (midi 72) -> octave, tongue-blocked split.
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(ch(1, Direction.BLOW, 60), ch(4, Direction.BLOW, 72)));
    assertEquals(Technique.OCTAVE, c.technique());
    assertTrue(c.tongueBlocked());
    assertEquals(List.of("1B", "4B"), c.labels());
  }

  @Test
  void octaveSplit1D4DIsOctave() {
    // C-harp: 1D = D4 (midi 62), 4D = D5 (midi 74) -> octave.
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(ch(1, Direction.DRAW, 62), ch(4, Direction.DRAW, 74)));
    assertEquals(Technique.OCTAVE, c.technique());
    assertTrue(c.tongueBlocked());
  }

  @Test
  void nonAdjacentNonOctaveIsSplit() {
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(ch(2, Direction.BLOW, 67), ch(5, Direction.BLOW, 76)));
    assertEquals(Technique.SPLIT, c.technique());
    assertTrue(c.tongueBlocked());
  }

  @Test
  void threeContiguousSameDirectionIsChord() {
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(
                ch(1, Direction.BLOW, 60), ch(2, Direction.BLOW, 64), ch(3, Direction.BLOW, 67)));
    assertEquals(Technique.CHORD, c.technique());
    assertFalse(c.tongueBlocked());
  }

  @Test
  void threeNonContiguousSameDirectionIsSplit() {
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(
                ch(1, Direction.BLOW, 60), ch(2, Direction.BLOW, 64), ch(4, Direction.BLOW, 72)));
    assertEquals(Technique.SPLIT, c.technique());
    assertTrue(c.tongueBlocked());
  }

  @Test
  void mixedDirectionsIsComplex() {
    Classification c =
        PlayingTechniqueClassifier.classify(
            List.of(ch(2, Direction.BLOW, 67), ch(3, Direction.DRAW, 71)));
    assertEquals(Technique.COMPLEX, c.technique());
  }
}
