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

import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.HarpTone.Direction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Deterministically maps multi-pitch note events ("which tones are sounding") onto harmonica
 * channels ("which channels/techniques are active"). Java port of the prototype's {@code
 * harp_mapping.py} (Roadmap Phase 2).
 *
 * <p>This component is intentionally ML-free: a {@link MultiPitchDetector} (Basic Pitch or the
 * existing {@code ChordDetector}) provides the raw pitches; this mapper applies the
 * harmonica-specific interpretation and plausibility rules:
 *
 * <ul>
 *   <li>only known harp tones of the current tuning are allowed (within a cents tolerance),
 *   <li>a channel cannot be blow and draw at the same time (at most one active tone per channel),
 *   <li>ambiguous pitches (e.g. G4 = 2D = 3B) are distributed onto separate free channels.
 * </ul>
 *
 * <p>The note layout of the Richter tuning is taken 1:1 from {@code RichterHarmonica.java}:
 *
 * <pre>
 *   HALF_TONES_OUT (blow) = {0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36}
 *   HALF_TONES_IN  (draw) = {0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33}
 * </pre>
 *
 * with base key C4 = MIDI 60. Index 0 of the arrays is a placeholder; the channels are 1..10. The
 * base MIDI is configurable so the mapper can be made key/tuning aware (see the integration
 * concept).
 */
public final class HarpPitchMapper {

  /** Half-tone offsets of the blow reeds per channel (index 0 unused). */
  private static final int[] HALF_TONES_OUT = {0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36};

  /** Half-tone offsets of the draw reeds per channel (index 0 unused). */
  private static final int[] HALF_TONES_IN = {0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33};

  /** Lowest harmonica channel. */
  public static final int CHANNEL_MIN = 1;

  /** Highest harmonica channel of a standard Richter harp. */
  public static final int CHANNEL_MAX = 10;

  /** Base MIDI note for a C harp: KEY.C == "C4" == MIDI 60. */
  public static final int KEY_C_BASE_MIDI = 60;

  /**
   * Returns the half-tone offset of the blow reed for the given channel.
   *
   * @param channel the channel (1..10)
   * @return the blow reed half-tone offset relative to the base MIDI
   */
  public static int halfToneOut(int channel) {
    return HALF_TONES_OUT[channel];
  }

  /**
   * Returns the half-tone offset of the draw reed for the given channel.
   *
   * @param channel the channel (1..10)
   * @return the draw reed half-tone offset relative to the base MIDI
   */
  public static int halfToneIn(int channel) {
    return HALF_TONES_IN[channel];
  }

  private final int baseMidi;
  private final MappingConfig config;
  private final List<HarpTone> table;

  /** Creates a mapper for a Richter C harp with the default mapping configuration. */
  public HarpPitchMapper() {
    this(KEY_C_BASE_MIDI, MappingConfig.defaultConfig());
  }

  /**
   * Creates a mapper for the given base MIDI (harp key) and configuration.
   *
   * @param baseMidi the MIDI note of channel-1 blow reference (C harp = 60)
   * @param config the mapping configuration
   */
  public HarpPitchMapper(int baseMidi, MappingConfig config) {
    this.baseMidi = baseMidi;
    this.config = config;
    this.table = buildTable(baseMidi, config.includeBends());
  }

  /**
   * Creates a mapper from a precomputed harp tone table (key/tuning aware). Used by {@link
   * #forHarmonica(Harmonica, MappingConfig)} so the mapper no longer assumes a fixed Richter-C
   * layout but takes the actually selected harp key and tuning from the Let's-Bend model.
   *
   * @param baseMidi the MIDI note of the channel-1 blow reed (informational)
   * @param config the mapping configuration
   * @param table the harp tone table to map against
   */
  public HarpPitchMapper(int baseMidi, MappingConfig config, List<HarpTone> table) {
    this.baseMidi = baseMidi;
    this.config = config;
    this.table = List.copyOf(table);
  }

  /**
   * Builds a key- and tuning-aware mapper from the currently selected {@link Harmonica} of the
   * Let's-Bend model. The harp tone table is derived from the harmonica's real note frequencies
   * (blow, draw and the available bends per channel) instead of assuming a fixed Richter-C table,
   * so different harp keys (C, A, ...) and different tunings (Richter, Country, Paddy Richter, ...)
   * are all supported through the same mapping interface.
   *
   * @param harmonica the currently selected harmonica (key and tuning)
   * @param config the mapping configuration ({@code null} uses {@link
   *     MappingConfig#defaultConfig()})
   * @return a mapper that maps detected pitches onto the channels of {@code harmonica}
   */
  public static HarpPitchMapper forHarmonica(Harmonica harmonica, MappingConfig config) {
    MappingConfig cfg = config != null ? config : MappingConfig.defaultConfig();
    List<HarpTone> tones = buildTableForHarmonica(harmonica, cfg.includeBends());
    int channel1Blow =
        (int) Math.round(HarpTone.freqToMidi(harmonica.getNoteFrequency(CHANNEL_MIN, 0)));
    return new HarpPitchMapper(channel1Blow, cfg, tones);
  }

  /**
   * Builds the harp tone table from a {@link Harmonica} using its real note frequencies. Iterates
   * the channels and adds the blow reed (note 0), the draw reed (note 1) and, if requested, the
   * playable draw bends (note 2..) and blow bends (note -1..). Overblows/overdraws are
   * intentionally excluded to keep the plausibility strict (mirrors {@link #buildTable(int,
   * boolean)}).
   *
   * @param harmonica the harmonica whose layout defines the table
   * @param includeBends whether to include the bend tones
   * @return the harp tone table for the given harmonica
   */
  public static List<HarpTone> buildTableForHarmonica(Harmonica harmonica, boolean includeBends) {
    List<HarpTone> tones = new ArrayList<>();
    for (int ch = CHANNEL_MIN; ch <= CHANNEL_MAX; ch++) {
      double blowFreq = harmonica.getNoteFrequency(ch, 0);
      double drawFreq = harmonica.getNoteFrequency(ch, 1);
      if (blowFreq > 0) {
        tones.add(
            new HarpTone(ch, Direction.BLOW, 0, (int) Math.round(HarpTone.freqToMidi(blowFreq))));
      }
      if (drawFreq > 0) {
        tones.add(
            new HarpTone(ch, Direction.DRAW, 0, (int) Math.round(HarpTone.freqToMidi(drawFreq))));
      }
      if (!includeBends) {
        continue;
      }
      for (int note = 2; note <= harmonica.getDrawBendingTonesCount(ch) + 1; note++) {
        double f = harmonica.getNoteFrequency(ch, note);
        if (f > 0) {
          tones.add(
              new HarpTone(ch, Direction.DRAW, note - 1, (int) Math.round(HarpTone.freqToMidi(f))));
        }
      }
      for (int note = -1; note >= -harmonica.getBlowBendingTonesCount(ch); note--) {
        double f = harmonica.getNoteFrequency(ch, note);
        if (f > 0) {
          tones.add(
              new HarpTone(ch, Direction.BLOW, -note, (int) Math.round(HarpTone.freqToMidi(f))));
        }
      }
    }
    return tones;
  }

  /**
   * Returns the (immutable) harp tone table used by this mapper.
   *
   * @return the harp tone table
   */
  public List<HarpTone> table() {
    return List.copyOf(table);
  }

  /**
   * Builds the full frequency/MIDI table of a Richter harp for the given base MIDI.
   *
   * <p>Returns all known playable tones: blow and draw per channel and optionally the bends. Bends
   * are the semitones strictly between the two reeds of a channel (the higher reed is bent down).
   * Overblows/overdraws are intentionally not included to keep the plausibility strict.
   *
   * @param baseMidi the base MIDI note (C harp = 60)
   * @param includeBends whether to include bend tones
   * @return the harp tone table
   */
  public static List<HarpTone> buildTable(int baseMidi, boolean includeBends) {
    List<HarpTone> tones = new ArrayList<>();
    for (int ch = CHANNEL_MIN; ch <= CHANNEL_MAX; ch++) {
      int blow = baseMidi + HALF_TONES_OUT[ch];
      int draw = baseMidi + HALF_TONES_IN[ch];
      tones.add(new HarpTone(ch, Direction.BLOW, 0, blow));
      tones.add(new HarpTone(ch, Direction.DRAW, 0, draw));
      if (!includeBends) {
        continue;
      }
      int low = Math.min(blow, draw);
      int high = Math.max(blow, draw);
      Direction bentDirection = draw > blow ? Direction.DRAW : Direction.BLOW;
      int i = 1;
      for (int midi = high - 1; midi > low; midi--, i++) {
        tones.add(new HarpTone(ch, bentDirection, i, midi));
      }
    }
    return tones;
  }

  /**
   * Builds the default Richter C table (base MIDI 60).
   *
   * @param includeBends whether to include bend tones
   * @return the Richter C harp tone table
   */
  public static List<HarpTone> buildRichterCTable(boolean includeBends) {
    return buildTable(KEY_C_BASE_MIDI, includeBends);
  }

  /** Signed cents distance between two MIDI values (100 cents = one semitone). */
  private static double centsBetween(double midiA, double midiB) {
    return (midiA - midiB) * 100.0;
  }

  /** A possible match of a detected pitch onto a known harp tone. */
  private record ChannelMatch(
      HarpTone tone, double detectedMidi, double centsOff, double confidence) {}

  /**
   * Preference order for the candidate channels of a single detected pitch. Most accurate match
   * (cents) first; on a tie an unbent natural tone is preferred over a bend, a blow reed over a
   * draw reed, and finally the lower channel. This makes the conflict resolution deterministic and,
   * in particular, maps an ambiguous pitch such as G4 (= {@code 3B} blow ≡ {@code 2D} draw) to the
   * plainer blow channel {@code 3B}, so a C major chord resolves cleanly to {@code 1B, 2B, 3B}
   * instead of letting {@code 2D} block {@code 2B} (Phase-0 finding).
   */
  private static final Comparator<ChannelMatch> CANDIDATE_PREFERENCE =
      Comparator.comparingDouble((ChannelMatch m) -> Math.abs(m.centsOff()))
          .thenComparingInt(m -> m.tone().bend())
          .thenComparingInt(m -> m.tone().direction() == Direction.BLOW ? 0 : 1)
          .thenComparingInt(m -> m.tone().channel());

  /**
   * Maps detected note events onto harp channels and applies the plausibility rules.
   *
   * @param events the detected multi-pitch events (the amplitude is used as per-note confidence)
   * @param timeSeconds the time stamp for the output
   * @return the mapping result
   */
  public MappingResult mapNotes(List<NoteEvent> events, double timeSeconds) {
    MappingResult result = new MappingResult(timeSeconds);

    // Step 1: collect all plausible candidates (within tolerance) per event, sorted by accuracy
    // (cents). This keeps the ambiguity that one tone (e.g. G4) can be both 2D and 3B.
    List<List<ChannelMatch>> candidatesPerEvent = new ArrayList<>();
    for (NoteEvent ev : events) {
      List<ChannelMatch> matches = new ArrayList<>();
      for (HarpTone tone : table) {
        double cents = centsBetween(ev.midi(), tone.midi());
        if (Math.abs(cents) <= config.toleranceCents()) {
          matches.add(new ChannelMatch(tone, ev.midi(), cents, ev.amplitude()));
        }
      }
      if (matches.isEmpty()) {
        // Rule: only known harp tones are allowed.
        result
            .rejected()
            .add(new MappingResult.Rejected(ev.midi(), "no known harp tone within tolerance"));
        continue;
      }
      matches.sort(CANDIDATE_PREFERENCE);
      candidatesPerEvent.add(matches);
    }

    // Step 2: assignment with conflict resolution. Rule "a channel cannot be blow and draw at the
    // same time" = at most one active tone per channel. Rather than a greedy per-event choice
    // (which
    // is sensitive to the input order and breaks either the blow or the draw triad for the
    // ambiguous
    // G4 = 2D = 3B), the assignment is solved globally: among all conflict-free channel assignments
    // we pick the one that recognises the most events; ties are broken by the smallest total cents
    // deviation and then by the per-event candidate preference (blow/unbent, see
    // CANDIDATE_PREFERENCE). This recognises a full C major blow triad as 1B,2B,3B and a full draw
    // triad as 1D,2D,3D alike.
    ChannelMatch[] assignment = assignChannels(candidatesPerEvent);

    for (int i = 0; i < candidatesPerEvent.size(); i++) {
      if (assignment[i] == null) {
        // No conflict-free channel could be assigned to this event -> channel conflict.
        ChannelMatch best = candidatesPerEvent.get(i).get(0);
        result
            .rejected()
            .add(
                new MappingResult.Rejected(
                    best.detectedMidi(), "channel conflict " + best.tone().label() + " rejected"));
      }
    }

    // Step 3: re-add enharmonic alternatives. A pitch such as G4 exists twice on a Richter harp
    // (2D draw ≡ 3B blow) and it is impossible to tell from the audio alone which channel the
    // player is using. Every candidate of an assigned event that produces exactly the same MIDI
    // note on a still-free channel is therefore shown as active as well, so both 2D and 3B light
    // up for a single G4. Channels already used by other events stay untouched, so an unambiguous
    // chord (e.g. 1B,2B,3B) is not polluted with a conflicting 2D.
    List<ChannelMatch> active = new ArrayList<>();
    Set<Integer> usedChannels = new HashSet<>();
    for (ChannelMatch m : assignment) {
      if (m != null) {
        active.add(m);
        usedChannels.add(m.tone().channel());
      }
    }
    for (int i = 0; i < candidatesPerEvent.size(); i++) {
      ChannelMatch assigned = assignment[i];
      if (assigned == null) {
        continue;
      }
      for (ChannelMatch alternative : candidatesPerEvent.get(i)) {
        if (alternative.tone().midi() == assigned.tone().midi()
            && usedChannels.add(alternative.tone().channel())) {
          active.add(alternative);
        }
      }
    }

    active.stream()
        .sorted(Comparator.comparingInt(m -> m.tone().channel()))
        .forEach(
            m -> result.active().add(new ActiveChannel(m.tone(), m.confidence(), m.centsOff())));
    return result;
  }

  /**
   * Solves the channel assignment globally: assigns each detected event to one of its candidate
   * channels (or leaves it unassigned) so that no channel is used twice and, lexicographically, (1)
   * the number of assigned events is maximal, (2) the total absolute cents deviation is minimal and
   * (3) the summed per-event candidate preference index is minimal (favouring blow/unbent tones).
   * The search space is tiny in practice (a few simultaneous pitches), so an exhaustive
   * backtracking is appropriate.
   *
   * @param candidatesPerEvent for each event its candidate matches, pre-sorted by preference
   * @return an array (aligned with {@code candidatesPerEvent}) of the chosen match per event, or
   *     {@code null} where the event could not be assigned conflict-free
   */
  private static ChannelMatch[] assignChannels(List<List<ChannelMatch>> candidatesPerEvent) {
    Assignment best = new Assignment(candidatesPerEvent.size());
    search(
        candidatesPerEvent, 0, new ChannelMatch[candidatesPerEvent.size()], new HashSet<>(), best);
    return best.assignment;
  }

  /** Recursive backtracking helper for {@link #assignChannels(List)}. */
  private static void search(
      List<List<ChannelMatch>> candidatesPerEvent,
      int index,
      ChannelMatch[] current,
      Set<Integer> usedChannels,
      Assignment best) {
    if (index == candidatesPerEvent.size()) {
      best.consider(current, candidatesPerEvent);
      return;
    }
    List<ChannelMatch> candidates = candidatesPerEvent.get(index);
    for (ChannelMatch m : candidates) {
      int channel = m.tone().channel();
      if (usedChannels.add(channel)) {
        current[index] = m;
        search(candidatesPerEvent, index + 1, current, usedChannels, best);
        usedChannels.remove(channel);
        current[index] = null;
      }
    }
    // Option: leave this event unassigned (its candidate channels are all needed elsewhere).
    current[index] = null;
    search(candidatesPerEvent, index + 1, current, usedChannels, best);
  }

  /** Holds and scores the best channel assignment found so far. */
  private static final class Assignment {
    private ChannelMatch[] assignment;
    private int count = -1;
    private double cents = Double.MAX_VALUE;
    private int preference = Integer.MAX_VALUE;

    Assignment(int size) {
      this.assignment = new ChannelMatch[size];
    }

    void consider(ChannelMatch[] current, List<List<ChannelMatch>> candidatesPerEvent) {
      int c = 0;
      double centsSum = 0.0;
      int prefSum = 0;
      for (int i = 0; i < current.length; i++) {
        ChannelMatch m = current[i];
        if (m == null) {
          continue;
        }
        c++;
        centsSum += Math.abs(m.centsOff());
        prefSum += candidatesPerEvent.get(i).indexOf(m);
      }
      boolean better =
          c > count
              || (c == count && centsSum < cents - 1e-9)
              || (c == count && Math.abs(centsSum - cents) <= 1e-9 && prefSum < preference);
      if (better) {
        count = c;
        cents = centsSum;
        preference = prefSum;
        assignment = current.clone();
      }
    }
  }

  /**
   * Convenience wrapper: maps a list of MIDI notes (confidence 1.0 each).
   *
   * @param timeSeconds the time stamp for the output
   * @param midiNotes the MIDI notes to map
   * @return the mapping result
   */
  public MappingResult mapMidiNotes(double timeSeconds, double... midiNotes) {
    List<NoteEvent> events = new ArrayList<>();
    for (double midi : midiNotes) {
      events.add(NoteEvent.of(midi, 1.0));
    }
    return mapNotes(events, timeSeconds);
  }

  /**
   * Convenience wrapper: maps a list of frequencies in Hz (confidence 1.0 each).
   *
   * @param timeSeconds the time stamp for the output
   * @param frequencies the frequencies in Hz
   * @return the mapping result
   */
  public MappingResult mapFrequencies(double timeSeconds, double... frequencies) {
    List<NoteEvent> events = new ArrayList<>();
    for (double freq : frequencies) {
      events.add(NoteEvent.of(HarpTone.freqToMidi(freq), 1.0));
    }
    return mapNotes(events, timeSeconds);
  }

  /**
   * Returns the base MIDI note (harp key) of this mapper.
   *
   * @return the base MIDI note
   */
  public int baseMidi() {
    return baseMidi;
  }

  /**
   * Returns the lowest MIDI note present in this mapper's harp tone table.
   *
   * @return the lowest table MIDI note, or {@link MappingConfig} default base if the table is empty
   */
  public int minTableMidi() {
    return table.stream().mapToInt(HarpTone::midi).min().orElse(baseMidi);
  }

  /**
   * Returns the highest MIDI note present in this mapper's harp tone table.
   *
   * @return the highest table MIDI note, or this mapper's base MIDI if the table is empty
   */
  public int maxTableMidi() {
    return table.stream().mapToInt(HarpTone::midi).max().orElse(baseMidi);
  }
}
