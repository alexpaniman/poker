package org.poker.simulator.interfaces;

import org.poker.simulator.RateManagement;

public interface Raiseable extends Collable {
    RateManagement raise(long value);

    @Override
    default String[] possibleBets() {
        return new String[]{
                "raise",
                "coll",
                "fold"
        };
    }
}
