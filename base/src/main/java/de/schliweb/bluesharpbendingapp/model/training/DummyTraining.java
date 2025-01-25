package de.schliweb.bluesharpbendingapp.model.training;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;

/**
 * The DummyTraining class is a specific implementation of the AbstractTraining class.
 * It represents a placeholder or basic training mode that provides a predefined set
 * of half-tone intervals for training purposes. This class can be used for testing
 * or as a default training mode.
 */
public class DummyTraining extends AbstractTraining {

    /**
     * A static array defining predefined half-tone intervals.
     * This array is utilized to represent a sequence of half-tone steps,
     * commonly used in musical training or simulation scenarios.
     * In the context of the DummyTraining class specifically, it highlights
     * the intervals as {0, 1, 2}.
     */
    private static final int[] HALF_TONES = {0, 1, 2};

    /**
     * Constructs an instance of DummyTraining using the specified key.
     *
     * @param key the musical key to be used for this training. It defines the
     *            specific tonal context in which the training operates.
     */
    protected DummyTraining(AbstractHarmonica.KEY key) {
        super(key);
    }

    @Override
    int[] getHalfTones() {
        return HALF_TONES;
    }

    @Override
    public String getTrainingName() {
        return TRAINING.DUMMY.name();
    }
}
