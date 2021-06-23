package org.poker.simulator.interfaces;

public interface Rate {
    default String[] possibleBets() {
        return new String[0];
    }
}

