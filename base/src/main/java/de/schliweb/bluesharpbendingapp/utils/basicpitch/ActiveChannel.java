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

/**
 * A harmonica channel that has been recognised as active after the plausibility filtering of {@link
 * HarpPitchMapper}. Java pendant to the prototype's {@code ActiveChannel}.
 *
 * @param tone the matched harmonica tone (channel/direction/bend)
 * @param confidence the confidence of this active channel in {@code [0, 1]}
 * @param centsOff the (signed) deviation in cents between the detected pitch and the harp tone
 */
public record ActiveChannel(HarpTone tone, double confidence, double centsOff) {

  /**
   * Returns the short label of the underlying harp tone (e.g. {@code 4B}).
   *
   * @return the label
   */
  public String label() {
    return tone.label();
  }
}
