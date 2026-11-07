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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The result of mapping a set of multi-pitch note events onto harmonica channels for a single point
 * in time. Java pendant to the prototype's {@code MappingResult}.
 *
 * <p>It carries the list of active channels and a list of rejected detections (with a
 * human-readable reason), and produces the output line specified by the issue, e.g. {@code
 * time=0.42s active=[4B,5B,6B] confidence=0.91}.
 */
public final class MappingResult {

  /** A rejected detection together with the reason it was discarded. */
  public record Rejected(double midi, String reason) {}

  private final double timeSeconds;
  private final List<ActiveChannel> active;
  private final List<Rejected> rejected;

  /**
   * Creates an empty mapping result for the given time stamp.
   *
   * @param timeSeconds the time stamp in seconds
   */
  public MappingResult(double timeSeconds) {
    this(timeSeconds, new ArrayList<>());
  }

  /**
   * Creates a mapping result for the given time stamp with the given active channels.
   *
   * @param timeSeconds the time stamp in seconds
   * @param active the active channels (a defensive copy is made)
   */
  public MappingResult(double timeSeconds, List<ActiveChannel> active) {
    this.timeSeconds = timeSeconds;
    this.active = new ArrayList<>(active);
    this.rejected = new ArrayList<>();
  }

  /**
   * Returns the time stamp of this result in seconds.
   *
   * @return the time stamp in seconds
   */
  public double timeSeconds() {
    return timeSeconds;
  }

  /**
   * Returns the mutable list of active channels.
   *
   * @return the active channels
   */
  public List<ActiveChannel> active() {
    return active;
  }

  /**
   * Returns the mutable list of rejected detections.
   *
   * @return the rejected detections
   */
  public List<Rejected> rejected() {
    return rejected;
  }

  /**
   * Returns the list of active channel labels (e.g. {@code [4B, 5B, 6B]}).
   *
   * @return the active labels in channel order
   */
  public List<String> labels() {
    return active.stream().map(ActiveChannel::label).collect(Collectors.toList());
  }

  /**
   * Returns the mean confidence of the active channels, or {@code 0.0} if none are active.
   *
   * @return the mean confidence
   */
  public double confidence() {
    if (active.isEmpty()) {
      return 0.0;
    }
    return active.stream().mapToDouble(ActiveChannel::confidence).sum() / active.size();
  }

  /**
   * Produces the output line specified by the issue, e.g. {@code time=0.42s active=[4B,5B,6B]
   * confidence=0.91}.
   *
   * @return the formatted output line
   */
  public String format() {
    String joined = String.join(",", labels());
    return String.format(
        Locale.ROOT, "time=%.2fs  active=[%s]  confidence=%.2f", timeSeconds, joined, confidence());
  }
}
