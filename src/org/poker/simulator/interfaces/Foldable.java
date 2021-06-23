package org.poker.simulator.interfaces;

import org.poker.simulator.RateManagement;

public interface Foldable extends Rate {
    RateManagement fold();

    @Override
    default String[] possibleBets() {
        return new String[]{
                "fold"
        };
    }
}
