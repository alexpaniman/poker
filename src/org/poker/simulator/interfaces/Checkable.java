package org.poker.simulator.interfaces;

import org.poker.simulator.RateManagement;

public interface Checkable extends Foldable {
    RateManagement check();

    @Override
    default String[] possibleBets() {
        return new String[]{
                "check",
                "fold"
        };
    }
}
