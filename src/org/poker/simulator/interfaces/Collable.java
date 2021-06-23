package org.poker.simulator.interfaces;

import org.poker.simulator.RateManagement;

public interface Collable extends Foldable {
    RateManagement coll();

    @Override
    default String[] possibleBets() {
        return new String[]{
                "coll",
                "fold"
        };
    }
}
