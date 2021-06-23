package org.poker.simulator;

import org.poker.core.Card;
import org.poker.core.Combination;
import org.poker.core.PokerCore;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Simulation extends PokerCore {
    private Card[] simulationCurrentPart;

    final int numberOfPlayers;

    PokerGameSimulator simulator;

    SimulationIterator iterator;

    Simulation(SimulationIterator iterator, Card[] simulated){
        this.iterator = iterator;
        this.simulator = iterator.simulator;
        this.simulationCurrentPart = simulated;
        this.numberOfPlayers = iterator.numberOfPlayers;
    }

    public Card[] getPlayerHand(int playerIndex) {
        playerIndex+=1;
        if (playerIndex > numberOfPlayers || playerIndex < 0)
            throw new IllegalArgumentException("IllegalPlayerIndex : "+playerIndex);
        return new Card[]{
                simulationCurrentPart[(playerIndex-1)*2].clone(),
                simulationCurrentPart[(playerIndex-1)*2 + 1].clone()
        };
    }

    public Card[] getTable() {
        Card[] table = new Card[simulationCurrentPart.length - numberOfPlayers*2];
        for (int t = 0, i = numberOfPlayers*2; i < simulationCurrentPart.length; t++, i++)
            table[t] = simulationCurrentPart[i].clone();
        return table;
    }

    public Card[] getPlayerCards(int player) {
        return
                Stream.concat(
                        Arrays.stream(getPlayerHand(player)),
                        Arrays.stream(getTable())
                )
                        .toArray(Card[]::new);
    }

    public Card[] getPlayerCardsSized(int player) {
        return Arrays.copyOf(getPlayerCards(player), 7);
    }

    public Map<Integer, Combination> getSortedCombinationsMap(){
        Map<Integer, Combination> combinations = new TreeMap<>();
        for (int i = 0; i < numberOfPlayers; i++)
            combinations.put(i, AnalyzeCombination(getPlayerCardsSized(i)));
        return combinations
                .entrySet()
                .stream()
                .sorted(
                        Map.Entry.comparingByValue(Comparator.reverseOrder())
                )
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, HashMap::new
                        )
                );
    }

    public RateManagement rateManage() {
        return new RateManagement(this);
    }

    public SimulationIterator getIterator() {
        return iterator;
    }
}