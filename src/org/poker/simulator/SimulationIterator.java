package org.poker.simulator;

import org.poker.core.Card;
import org.poker.core.Combination;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

class SimulationIterator implements Iterator<Simulation> {
    final int numberOfPlayers;

    Map<Integer, Combination> combinations;

    PokerGameSimulator simulator;

    private Card[] simulation;

    private int iteration = 0;

    long[] maxWin;

    long bank = 0;

    SimulationIterator(PokerGameSimulator simulator) {
        this.simulator = simulator;
        this.numberOfPlayers = simulator.getPlayers();
        this.simulation = simulator.getSimulation();
    }

    @Override
    public boolean hasNext() {
        return iteration < 4;
    }

    @Override
    public Simulation next() {
        if (!hasNext()) {
            for (Integer integer : combinations
                    .keySet()) {
                int i = integer;
                if (bank <= 0) {
                    break;
                }
                if (maxWin[i] < bank) {
                    simulator.playersStack[i] += maxWin[i];
                    bank -= maxWin[i];
                } else {
                    simulator.playersStack[i] += bank;
                    bank = 0;
                }
            }
            return null;
        }
        try {
            if (iteration == 3) {
                Simulation s = new Simulation(this, simulation);
                combinations = s.getSortedCombinationsMap();
                return s;
            }
            if (iteration == 2)
                return new Simulation(
                        this,
                        Arrays
                                .stream(simulation)
                                .limit(simulation.length - 1)
                                .toArray(Card[]::new)
                );
            if (iteration == 1)
                return new Simulation(
                        this,
                        Arrays
                                .stream(simulation)
                                .limit(simulation.length - 2)
                                .toArray(Card[]::new)
                );
            if (iteration == 0) {
                maxWin = new long[numberOfPlayers];
                for (int i = 0; i < numberOfPlayers; i++)
                    maxWin[i] = simulator.playersStack[i] * numberOfPlayers;
                for (int i = 0; i < simulator.playersStack.length; i++)
                    simulator.playersStack[i] -= simulator.ante;
                bank += simulator.ante * numberOfPlayers;
                for (int i = simulator.dealer + 1, iCr = i, c = 1; i < simulator.dealer + 2; i++, c++, iCr++) {
                    if (iCr >= numberOfPlayers)
                        iCr = 0;
                    int blind = (c == 1)? simulator.smallBlind : simulator.bigBlind;
                    simulator.playersStack[iCr] -= blind;
                    bank += blind;
                }
                return new Simulation(
                        this,
                        Arrays
                                .stream(simulation)
                                .limit(simulation.length - 5)
                                .toArray(Card[]::new)
                );
            }
            return null;
        } finally {
            iteration++;
        }
    }

    public PokerGameSimulator getGameSimulator(){
        return simulator;
    }
}
