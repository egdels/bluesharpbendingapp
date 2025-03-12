package de.schliweb.bluesharpbendingapp.webapp;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * The HarmonicaWeb class represents a web-compatible representation of a harmonica,
 * allowing for the selection of key and tuning. It supports conversion to a Harmonica object.
 * This class is immutable and implements the Serializable interface.
 */
@Getter
public class HarmonicaWeb implements Serializable {
    /**
     * A unique identifier for the Serializable class version.
     * This field is used during deserialization to verify that the sender and receiver
     * of a serialized object have loaded classes for that object that are compatible
     * with respect to serialization. If the class does not explicitly declare a
     * serialVersionUID, the JVM will generate one automatically, which may result
     * in unexpected issues during deserialization if the class definition changes.
     */
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * The selected key for the harmonica in the web application representation.
     * This corresponds to a musical key in which the harmonica is tuned and
     * serves as a required parameter when converting the web representation
     * to a harmonica object.
     * <p>
     * This field is immutable and is initialized during object construction,
     * ensuring thread safety and consistency of the object's state.
     */
    private final String selectedKey;
    /**
     * The selected tuning for the harmonica in the web application representation.
     * This specifies the tuning setup of the harmonica, determining its sound configuration
     * and behavior. It serves as a required parameter when converting the web representation
     * to a harmonica object.
     * <p>
     * This field is immutable and is initialized during object construction,
     * ensuring thread safety and consistency of the object's state.
     */
    private final String selectedTune;

    /**
     * Constructs a new instance of the HarmonicaWeb class with the specified key and tuning.
     *
     * @param selectedKey  The selected musical key for the harmonica. This determines the specific key
     *                     in which the harmonica is tuned.
     * @param selectedTune The selected tuning setup for the harmonica. This specifies the sound configuration
     *                     and behavior of the harmonica.
     */
    public HarmonicaWeb(String selectedKey, String selectedTune) {
        this.selectedKey = selectedKey;
        this.selectedTune = selectedTune;
    }

    /**
     * Constructs a new instance of the HarmonicaWeb class using enumerations for the key and tuning.
     *
     * @param key  The key of the harmonica as an enumeration of AbstractHarmonica.KEY. This represents
     *             the musical key in which the harmonica is tuned.
     * @param tune The tuning of the harmonica as an enumeration of AbstractHarmonica.TUNE. This represents
     *             the sound configuration or tuning setup of the instrument.
     */
    public HarmonicaWeb(AbstractHarmonica.KEY key, AbstractHarmonica.TUNE tune) {
        this(key.name(), tune.name());
    }


    /**
     * Converts the current configuration of the HarmonicaWeb instance
     * into a Harmonica object based on the selected key and tuning.
     *
     * @return a Harmonica instance created with the selected key and tuning configuration
     */
    public Harmonica toHarmonica() {
        return AbstractHarmonica.create(selectedKey, selectedTune);
    }


    /**
     * Retrieves the name of the note for the specified channel and note index
     * based on the current harmonica configuration.
     *
     * @param channelIndex the index of the channel on the harmonica
     * @param noteIndex the index of the note within the specified channel
     * @return the name of the note corresponding to the given channel and note index
     */
    public String getNoteName (int channelIndex, int noteIndex) {
        return toHarmonica().getNoteName(channelIndex, noteIndex);
    }

    /**
     * Determines whether the specified note on a given channel of the harmonica is an overblow.
     *
     * @param channelIndex the index of the channel on the harmonica
     * @param noteIndex the index of the note within the specified channel
     * @return true if the specified note is an overblow; false otherwise
     */
    public boolean isOverblow(int channelIndex, int noteIndex) {
        return toHarmonica().isOverblow(channelIndex, noteIndex);
    }

    /**
     * Determines whether the specified note on a given channel of the harmonica
     * is an overdraw by delegating the check to the underlying harmonica object.
     *
     * @param channelIndex the index of the channel on the harmonica
     * @param noteIndex    the index of the note within the specified channel
     * @return true if the specified note is an overdraw; false otherwise
     */
    public boolean isOverdraw(int channelIndex, int noteIndex) {
        return toHarmonica().isOverdraw(channelIndex, noteIndex);
    }
}
