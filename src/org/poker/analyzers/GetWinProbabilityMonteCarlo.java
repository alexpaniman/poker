package org.poker.analyzers;

import org.poker.probability.WinProbability;
import org.poker.core.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GetWinProbabilityMonteCarlo extends PokerCore implements Analyzer, Cloneable {

    private int k;

    private double percent;

    private long seed;

    private int opponents;

    private boolean generalization;

    private boolean currentMillisAlways;

    private final int NUMBER_OF_NULLS = -1;

    private final long CURRENT_MILLIS = -1L;

    private final long RANDOM_SEED = -2;

    private GetWinProbabilityMonteCarlo (){

    }

    private LinkedList<Integer> elUp(LinkedList<Integer> integers, int index) {
        Integer el = integers.remove(index);
        integers.add(el);
        return integers;
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
        if (seed == CURRENT_MILLIS || currentMillisAlways)
            seed = System.currentTimeMillis();
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
        long all = NumberOfCombinations(n, k).longValue();
        for (int j = 0, i = n - k; j < (generalization? 1 : opponents); j++) {
            all = all * NumberOfCombinations(i, 2).longValue();
            i-=2;
        }
        long MaxCounter = (long) (all * percent);
        Random rnd = (seed == RANDOM_SEED)? new Random() : new Random(seed);
        int stackLevel = n - 1;
        int currentRandom;
        double[] winProbability = new double[3];
        for (long counter = 0; counter < MaxCounter; counter++) {
            for (int i = DECK_SIZE - k, oInd = i - 2; i < DECK_SIZE; i++, oInd++) {
                currentRandom = rnd.nextInt(stackLevel--);
                Card card = new Card(stack.get(currentRandom), 1);
                PlayerDeck[i] = card;
                OpponentDeck[oInd] = card;
                stack = elUp(stack, currentRandom);
            }
            Combination playerComb = AnalyzeCombination(PlayerDeck);
            int min = 1;
            for (int opponent = 0; opponent < (generalization? 1 : opponents); opponent++) {
                for (int i = DECK_SIZE - 2; i < DECK_SIZE; i++) {
                    currentRandom = rnd.nextInt(stackLevel--);
                    OpponentDeck[i] = new Card(stack.get(currentRandom), 1);
                    stack = elUp(stack, currentRandom);
                }
                min = (int) Math.min(
                        Math.signum(
                                playerComb.compareTo(AnalyzeCombination(OpponentDeck))
                        ),
                        (float) min
                );
            }
            winProbability[min+1]++;
            stackLevel = n - 1;
        }
        double sum = Arrays
                .stream(winProbability)
                .sum();
        IntStream
                .range(0, 3)
                .forEach(i -> winProbability[i]/=sum);
        if (!generalization)
            return new WinProbability(winProbability);
        else
            return Generalization(new WinProbability(winProbability), opponents);
    }

    public static Builder newBuilder() {
        return new GetWinProbabilityMonteCarlo().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder setOpponents(int opponents) {
            if (opponents <= 0)
                throw new IllegalArgumentException("IllegalNumberOfOpponents : "+opponents);
            GetWinProbabilityMonteCarlo.this.opponents = opponents;
            return this;
        }

        public Builder setK(int k) {
            if (k > 7 || k < -1)
                throw new IllegalArgumentException("IllegalKValue : "+k);
            GetWinProbabilityMonteCarlo.this.k = k;
            return this;
        }

        public Builder setSeed(long seed) {
            if (seed < 0)
                throw new IllegalArgumentException("Illegal Seed : "+seed);
            GetWinProbabilityMonteCarlo.this.seed = seed;
            return this;
        }

        public Builder setPercentageOfTotalSearch(double percent) {
            if (percent < 0)
                throw new IllegalArgumentException("NegativePercent : "+percent);
            GetWinProbabilityMonteCarlo.this.percent = percent;
            return this;
        }

        public Builder generalization(boolean use) {
            GetWinProbabilityMonteCarlo.this.generalization = use;
            return this;
        }

        public Builder kEqualsNumberOfNulls(boolean use) {
            if (use)
                GetWinProbabilityMonteCarlo.this.k = NUMBER_OF_NULLS;
            return this;
        }

        public Builder kEqualsAll(boolean use) {
            if (use)
                GetWinProbabilityMonteCarlo.this.k = DECK_SIZE;
            return this;
        }

        public Builder seedEqualsCurrentMillis(boolean use){
            if (use) {
                GetWinProbabilityMonteCarlo.this.seed = CURRENT_MILLIS;
            }
            return this;
        }

        public Builder seedEqualsCurrentMillisAlways(boolean use){
            GetWinProbabilityMonteCarlo.this.currentMillisAlways = use;
            return this;
        }

        public Builder randomSeed(boolean use) {
            if (use)
                seed = RANDOM_SEED;
            return this;
        }

        public GetWinProbabilityMonteCarlo build(){
            return GetWinProbabilityMonteCarlo.this.clone();
        }
    }

    public GetWinProbabilityMonteCarlo clone() {
        try {
            return (GetWinProbabilityMonteCarlo) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}