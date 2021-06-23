package org.poker.analyzers;

import org.poker.core.Card;
import org.poker.core.Combination;
import org.poker.core.PokerCore;
import org.poker.probability.WinProbability;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.signum;

public class GetWinProbabilityExhaustiveSearch extends PokerCore implements Analyzer {

    private int k;

    private int opponents;

    private boolean generalization;

    private final int NUMBER_OF_NULLS = -1;

    private GetWinProbabilityExhaustiveSearch() {
    }

    private double[] compareAll (Card[] Opponent, Comparable<Combination> player, LinkedList<Integer> stack, int opponents, double[] res) {
        int[] processedComb = null;
        int size = stack.size();
        if (opponents == 0) {
            return new double[3];
        } else {
            int first = -1;
            int second = -1;
            while ((processedComb = nextCombination(size, 2, processedComb)) != null) {
                for (int i = DECK_SIZE - 2, c = 0; i < DECK_SIZE; i++, c++) {
                    int index = processedComb[c];
                    Opponent[i] = new Card(stack.get(index), 0);
                    if (first == -1)
                        first = index;
                    else
                        second = index;
                }
                res[(int)signum((float)(player.compareTo(AnalyzeCombination(Opponent))))+1]++;
                Integer i1 = stack.get(first);
                Integer i2 = stack.get(second);
                stack.remove(i1);
                stack.remove(i2);
                double[] res2 = compareAll(Opponent, player, stack, opponents - 1, res);
                IntStream
                        .range(0, res2.length)
                        .forEach(i -> res[i] += res2[i]);
                stack.add(first, i1);
                stack.add(second, i2);
            }
            return res;
        }
    }

    @Override
    public WinProbability analyze(Card[] deck) {
        if (!Objects.nonNull(deck))
            throw new NullPointerException("DeckCannotBeNull");
        if (k == NUMBER_OF_NULLS)
            k = (int)
                    Arrays
                            .stream(deck)
                            .filter(obj -> !Objects.nonNull(obj))
                            .count();
        LinkedList<Integer> stack = IntStream
                .range(0, 52)
                .filter(
                        cardValue -> Arrays
                                .stream(deck)
                                .limit(DECK_SIZE - k)
                                .filter(Objects::nonNull)
                                .mapToInt(Card::getValue)
                                .allMatch(IntCard -> IntCard != cardValue)
                )
                .boxed()
                .collect(
                        Collectors.toCollection(LinkedList::new)
                );
        Card[] PlayerDeck =
                Arrays.copyOf(
                        Arrays
                                .stream(deck)
                                .limit(DECK_SIZE - k)
                                .filter(Objects::nonNull)
                                .map(Card::clone)
                                .toArray(Card[]::new),
                        7);
        Card[] OpponentDeck =
                Arrays.copyOf(
                        Arrays
                                .stream(deck)
                                .limit(DECK_SIZE - k)
                                .filter(Objects::nonNull)
                                .filter(card -> card.getPlace() == 1)
                                .map(Card::clone)
                                .toArray(Card[]::new),
                        7);
        int n = stack.size();
        int[] processedComb = null;
        int[] ind = new int[k];
        Integer[] values = new Integer[k];
        double[] winProbability = new double[3];
        while ((processedComb = nextCombination(n, k, processedComb)) != null) {
            for (int cInd = 0, plInd = DECK_SIZE - k, opInd = plInd - 2; plInd < DECK_SIZE; cInd++, plInd++, opInd++) {
                int index = processedComb[cInd];
                Card card = new Card(stack.get(index), 1);
                PlayerDeck[plInd] = card;
                OpponentDeck[opInd] = card;
                ind[cInd] = index;
            }
            Combination pl = AnalyzeCombination(PlayerDeck);
            for (int i = 0; i < k; i++) {
                values[i] = stack.get(ind[i]);
            }
            for (Integer i : values) {
                stack.remove(i);
            }
            double[] winPrb = compareAll(OpponentDeck, pl, stack, (generalization? 1 : opponents), new double[3]);
            for (int i = 0; i < winPrb.length; i++) {
                double d = winPrb[i];
                winProbability[i] = d;
            }
            for (int i = 0; i < k; i++) {
                stack.add(ind[i], values[i]);
            }
        }
        double all =
                Arrays
                        .stream(winProbability)
                        .sum();
        IntStream
                .range(0, winProbability.length)
                .forEach(i -> winProbability[i] /= all);
        if (generalization)
            return Generalization(new WinProbability(winProbability), opponents);
        else
            return new WinProbability(winProbability);
    }

    public static Builder newBuilder() {
        return new GetWinProbabilityExhaustiveSearch().new Builder();
    }

    public class Builder {
        private Builder() {

        }

        public Builder setK(int k) {
            if (k > 7 || k < 0)
                throw new IllegalArgumentException("IllegalKValue : "+k);
            GetWinProbabilityExhaustiveSearch.this.k = k;
            return this;
        }

        public Builder setOpponents(int opponents) {
            if (opponents <= 0)
                throw new IllegalArgumentException("IllegalNumberOfOpponents : "+opponents);
            GetWinProbabilityExhaustiveSearch.this.opponents = opponents;
            return this;
        }

        public Builder generalization(boolean use) {
            GetWinProbabilityExhaustiveSearch.this.generalization = use;
            return this;
        }

        public Builder kEqualsNumberOfNulls(boolean use) {
            if (use)
                GetWinProbabilityExhaustiveSearch.this.k = NUMBER_OF_NULLS;
            return this;
        }

        public Builder kEqualsAll(boolean use) {
            if (use)
                GetWinProbabilityExhaustiveSearch.this.k = DECK_SIZE;
            return this;
        }
    }

}
