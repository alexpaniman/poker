package org.poker.simulator.interfaces;

import org.poker.simulator.RateManagement;

public interface Reraiseable extends Collable {
    RateManagement reraise(long value);

    @Override
    default String[] possibleBets() {
        return new String[]{
                "reraise",
                "coll",
                "fold"
        };
    }
}
