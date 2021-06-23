package org.poker.simulator;

import org.poker.simulator.interfaces.*;
import org.poker.simulator.interfaces.Rate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

public class RateManagement {
    private int iteration;

    private final int numberOfPlayers;

    private long[] rates;

    private int rateUps;

    private Simulation simulation;

    private Collection<Integer> folded;

    private int player = 0;

    RateManagement(Simulation simulation){
        this.simulation = simulation;
        this.numberOfPlayers = simulation.numberOfPlayers;
        this.rateUps = 0;
        this.rates = new long[numberOfPlayers];
        this.folded = simulation.simulator.folded;
    }

    public int currentPlayer(){
        return player;
    }

    private int maxRate() {
        return (int) Arrays
                .stream(rates)
                .max()
                .orElse(0);
    }

    private boolean isBalanced(){
        return
                IntStream
                        .range(0, rates.length)
                        .filter(
                                index -> !folded.contains(index)
                        )
                        .reduce((index, acc) -> (acc == index)? acc : -1)
                        .orElse(-1) == -1;
    }

    public boolean hasNext() {
        return !(iteration >= numberOfPlayers && isBalanced());
    }

    public Rate next(){
        if (!hasNext()) {
            for (int i = 0; i < rates.length; i++) {
                simulation.iterator.bank += rates[i];
                rates[i] = 0;
            }
            return null;
        }
        try {
            if (folded.contains(player))
                return new Nullable() {};
            if (rateUps == 0)
                return new Betable(){
                    RateManagement management = RateManagement.this;

                    private void rateUp(){
                        management.rateUps++;
                    }

                    @Override
                    public RateManagement fold() {
                        management.folded.add(management.player);
                        return management;
                    }

                    @Override
                    public RateManagement check() {
                        return management;
                    }

                    @Override
                    public RateManagement bet(long value) {
                        management.rates[management.player] = value;
                        management.simulation.simulator.playersStack[management.player] -= value;
                        rateUp();
                        return management;
                    }
                };
            if (rateUps == 1)
                return new Raiseable() {
                    RateManagement management = RateManagement.this;

                    private void rateUp(){
                        management.rateUps++;
                    }

                    @Override
                    public RateManagement raise(long value) {
                        management.rates[management.player] += management.maxRate() + value;
                        management.simulation.simulator.playersStack[management.player] -= value;
                        rateUp();
                        return null;
                    }

                    @Override
                    public RateManagement coll() {
                        management.rates[management.player] += management.maxRate();
                        return management;
                    }

                    @Override
                    public RateManagement fold() {
                        management.folded.add(management.player);
                        return management;
                    }
                };
            if (rateUps == 2)
                return new Reraiseable() {
                    RateManagement management = RateManagement.this;

                    private void rateUp(){
                        management.rateUps++;
                    }

                    @Override
                    public RateManagement coll() {
                        management.rates[management.player] += management.maxRate();
                        return management;
                    }

                    @Override
                    public RateManagement fold() {
                        management.folded.add(management.player);
                        return management;
                    }

                    @Override
                    public RateManagement reraise(long value) {
                        management.rates[management.player] += management.maxRate() + value;
                        management.simulation.simulator.playersStack[management.player] -= value;
                        rateUp();
                        return null;
                    }
                };
            if (rateUps == 3)
                return new Collable() {
                    RateManagement management = RateManagement.this;

                    @Override
                    public RateManagement coll() {
                        management.rates[management.player] += management.maxRate();
                        return management;
                    }

                    @Override
                    public RateManagement fold() {
                        management.folded.add(management.player);
                        return management;
                    }
                };
        } finally {
            iteration++;
            player++;
            if (player >= numberOfPlayers)
                player = 0;
        }
        return null;
    }

    public SimulationIterator getIterator() {
        if (hasNext())
            return null;
        return getSimulation().getIterator();
    }

    public Simulation getSimulation() {
        if (hasNext())
            return null;
        return simulation;
    }
}
