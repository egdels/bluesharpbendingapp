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
import static org.junit.jupiter.api.Assertions.assertFalse;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

@ExtendWith(ApplicationExtension.class)
class HarpViewNoteElementDesktopFXTest {

  /** NoteContainer highlights on every chord frame; one clear() must remove the highlight. */
  @Test
  void clearRemovesChordHighlightAfterRepeatedHighlighting() {
    Pane pane = new Pane(new Line(), new Label("C4"));
    HarpViewNoteElementDesktopFX element = HarpViewNoteElementDesktopFX.getInstance(pane);

    for (int i = 0; i < 5; i++) {
      element.highlightAsChord();
    }
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(
        1,
        pane.getStyleClass().stream().filter("highlight-chord"::equals).count(),
        "highlight-chord must be added only once");

    element.clear();
    WaitForAsyncUtils.waitForFxEvents();
    assertFalse(pane.getStyleClass().contains("highlight-chord"), "chord highlight must be gone");
  }
}
