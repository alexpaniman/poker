package org.poker.simulator.interfaces;

import org.poker.simulator.RateManagement;

public interface Betable extends Checkable {
    RateManagement bet(long value);

    @Override
    default String[] possibleBets() {
        return new String[]{
                "bet",
                "check",
                "fold"
        };
    }
}
