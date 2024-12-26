package de.schliweb.bluesharpbendingapp.model.training;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;

public class DummyTraining extends AbstractTraining {

    private static final int[] HALF_TONES = {0, 1, 2};

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
