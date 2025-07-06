package de.schliweb.bluesharpbendingapp.model.harmonica;

import lombok.Getter;

import java.util.List;

/**
 * The {@code ChordHarmonica} class represents a chord on a harmonica.
 * A chord is defined by a set of tones (frequencies) that are produced by
 * blowing or drawing air through specific channels on the harmonica.
 * This class provides information about the chord's tones and the
 * channels involved in creating the chord.
 */
public class ChordHarmonica {

    /**
     * Represents the index of a note within a predefined scale or sequence on the harmonica.
     * This value is used to identify and retrieve the corresponding note's frequency
     * or other properties through the harmonica's methods.
     * <p>
     * The note index is typically associated with the position of a note
     * within a given channel and defines the specific tone produced
     * when air is blown or drawn through that channel.
     * <p>
     * It is a mandatory parameter for constructing a {@code ChordHarmonica} instance.
     */
    @Getter
    private final int noteIndex;

    /**
     * Represents the channels on the harmonica involved in forming a chord.
     * Each channel corresponds to a specific airflow path on the harmonica
     * that produces a tone when air is blown or drawn through it.
     * <p>
     * The list defines the active channels contributing to the chord, where
     * each integer value represents the channel number (e.g., 1, 2, 3, etc.).
     * <p>
     * This field is immutable and must be initialized through the constructor.
     */
    @Getter
    private final List<Integer> channels;
    /**
     * Represents the list of tones (frequencies) that define a chord on a harmonica.
     * Each tone corresponds to a specific frequency produced by blowing or drawing
     * air through an associated channel of the harmonica.
     * <p>
     * This list is created based on the channels and note index used to construct
     * the {@code ChordHarmonica} object. Each tone is calculated by retrieving
     * the note frequency for the respective channel using the associated harmonica instance.
     * <p>
     * The tones are immutable after the initialization of {@code ChordHarmonica}.
     */
    @Getter
    private final List<Double> tones= new java.util.ArrayList<>();

    public ChordHarmonica(Harmonica harmonica, List<Integer> channels, int noteIndex) {
        this.noteIndex = noteIndex;
        this.channels = channels;
        for (int channel : channels) {
            tones.add(harmonica.getNoteFrequency(channel, noteIndex));
        }
    }


    /**
     * Returns a string representation of the ChordHarmonica object.
     * The string includes the list of tones that define the chord.
     *
     * @return a string representation of the ChordHarmonica object, including its tones.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChordHarmonica{notes=[");

        for (int i = 0; i < tones.size(); i++) {
            double frequency = tones.get(i);
            String noteName = frequencyToNoteName(frequency);

            sb.append(noteName);
            if (i < tones.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}");
        return sb.toString();
    }


    private String frequencyToNoteName(Double aDouble) {
        return NoteLookup.getNoteName(aDouble);
    }
}
