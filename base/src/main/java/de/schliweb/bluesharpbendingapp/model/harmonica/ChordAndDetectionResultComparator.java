package de.schliweb.bluesharpbendingapp.model.harmonica;

import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A comparator that compares objects of type {@code ChordHarmonica} and
 * {@code ChordDetectionResult} based on the number of notes they contain and
 * their associated frequencies. The comparison is primarily based on the number
 * of notes, followed by their musical frequencies if necessary.
 * <p>
 * This comparator supports comparing objects that represent chords or musical
 * tone detections. The comparison involves checking both the note counts and
 * the sets of frequencies (interpreted into musical note names, ignoring order).
 * The comparator does not directly compare the objects as is, but instead
 * evaluates their note content and frequency information.
 */
public class ChordAndDetectionResultComparator implements Comparator<Object> {

    /**
     * Compares two objects, which must be either instances of {@code ChordHarmonica}
     * or {@code ChordDetectionResult}. The comparison is based on the number of notes,
     * followed by a comparison of their frequencies.
     *
     * @param o1 the first object to compare, must be of type {@code ChordHarmonica}
     *           or {@code ChordDetectionResult}.
     * @param o2 the second object to compare, must be of type {@code ChordHarmonica}
     *           or {@code ChordDetectionResult}.
     * @return a negative integer, zero, or a positive integer as the first object is
     *         less than, equal to, or greater than the second object based on note count
     *         and frequencies.
     * @throws IllegalArgumentException if either {@code o1} or {@code o2} are not of type
     *                                  {@code ChordHarmonica} or {@code ChordDetectionResult}.
     */
    @Override
    public int compare(Object o1, Object o2) {
        // Prüfen, ob die Objekte von den erwarteten Typen sind
        if (!(o1 instanceof ChordHarmonica) && !(o1 instanceof ChordDetectionResult)) {
            throw new IllegalArgumentException("Das erste Objekt muss vom Typ ChordHarmonica oder ChordDetectionResult sein.");
        }
        if (!(o2 instanceof ChordHarmonica) && !(o2 instanceof ChordDetectionResult)) {
            throw new IllegalArgumentException("Das zweite Objekt muss vom Typ ChordHarmonica oder ChordDetectionResult sein.");
        }

        // Anzahl der Noten vergleichen
        int size1 = getNoteCount(o1);
        int size2 = getNoteCount(o2);

        if (size1 != size2) {
            return Integer.compare(size1, size2);
        }

        List<Double> frequencies1 = getFrequencies(o1);
        List<Double> frequencies2 = getFrequencies(o2);

        if (areFrequenciesEqual(frequencies1, frequencies2)) {
            return 0;
        }

        // Falls Frequenzen ungleich sind, unterscheide nach der Summe der Frequenzen
        double sum1 = frequencies1.stream().mapToDouble(Double::doubleValue).sum();
        double sum2 = frequencies2.stream().mapToDouble(Double::doubleValue).sum();
        return Double.compare(sum1, sum2);
    }

    /**
     * Determines whether two lists of frequencies represent the same set of musical notes.
     * The frequencies in each list are compared based on their note names obtained
     * from the NoteLookup utility. The comparison is order-insensitive.
     *
     * @param frequencies1 the first list of frequencies to compare, each frequency represented as a Double
     * @param frequencies2 the second list of frequencies to compare, each frequency represented as a Double
     * @return true if the two lists represent the same set of note names, false otherwise
     */
    private boolean areFrequenciesEqual(List<Double> frequencies1, List<Double> frequencies2) {
        List<Double> copyFrequency1 = new java.util.ArrayList<>(frequencies1);
        List<Double> copyFrequency2 = new java.util.ArrayList<>(frequencies2);

        // TODO   O(n log n)
        Collections.sort(copyFrequency1);
        Collections.sort(copyFrequency2);
        for (int i = 0; i < copyFrequency1.size(); i++) {
            double f1 = copyFrequency1.get(i);
            double f2 = copyFrequency2.get(i);
            String n1 = NoteLookup.getNoteName(f1);
            String n2 = NoteLookup.getNoteName(f2);
            assert n1 != null;
            if (!n1.equals(n2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines the number of notes (tones or pitches) present in the given object.
     * The object must be either an instance of {@code ChordHarmonica} or {@code ChordDetectionResult}.
     * An exception is thrown if the object is of an unsupported type.
     *
     * @param obj the object to evaluate, expected to be of type {@code ChordHarmonica} or {@code ChordDetectionResult}.
     * @return the number of notes (tones or pitches) in the object.
     * @throws IllegalArgumentException if the object is not of type {@code ChordHarmonica} or {@code ChordDetectionResult}.
     */
    private int getNoteCount(Object obj) {
        if (obj instanceof ChordHarmonica chordHarmonica) {
            return chordHarmonica.getTones().size();
        }
        if (obj instanceof ChordDetectionResult detectionResult) {
            return detectionResult.pitches().size();
        }
        throw new IllegalArgumentException("Unbekannter Objekttyp: " + obj.getClass().getName());
    }

    /**
     * Retrieves a list of frequencies based on the provided object, which must be
     * either an instance of {@code ChordHarmonica} or {@code ChordDetectionResult}.
     * If the object is of type {@code ChordHarmonica}, the method returns its tones.
     * If it is of type {@code ChordDetectionResult}, the method returns its pitches.
     * An exception is thrown for unsupported object types.
     *
     * @param obj the object from which to retrieve the frequencies. Must be
     *            of type {@code ChordHarmonica} or {@code ChordDetectionResult}.
     * @return a {@code List} of {@code Double} values representing the frequencies
     *         (tones or pitches) contained in the specified object.
     * @throws IllegalArgumentException if the object is not of type {@code ChordHarmonica}
     *                                  or {@code ChordDetectionResult}.
     */
    private List<Double> getFrequencies(Object obj) {
        if (obj instanceof ChordHarmonica chordHarmonica) {
            return chordHarmonica.getTones();
        }
        if (obj instanceof ChordDetectionResult detectionResult) {
            return detectionResult.pitches();
        }
        throw new IllegalArgumentException("Unbekannter Objekttyp: " + obj.getClass().getName());
    }
}


