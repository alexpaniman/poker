package org.poker.generators;

import org.poker.core.*;
import java.util.*;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class CombinationGenerator extends PokerCore implements Generator, Cloneable{
    private int k;

    private List<Card> Except = new ArrayList<>();

    private int NUMBER_OF_NULLS = -1;

    private CombinationGenerator() {

    }

    public static Builder newBuilder() {
        return new CombinationGenerator().new Builder();
    }

    @Override
    public List<Combination> generate(Card[] deck) {
        /*Инициализация*/
        if (k == NUMBER_OF_NULLS)
            k = (int) Arrays
                    .stream(deck)
                    .filter(obj -> !Objects.nonNull(obj))
                    .count();
        if (deck == null) {
            throw new NullPointerException("DeckCannotBeNull");
        }
        LinkedList<Combination> Combinations = new LinkedList<>();
        Card[] deckClone;
        if (deck.length == DECK_SIZE) deckClone = deck.clone();
        else {
            throw new IllegalArgumentException("IllegalTypeOfDeck");
        }
        int[] Using = IntStream
                .range(0, 52)
                .filter(
                        cardValue ->
                                Arrays
                                        .stream(deck)
                                        .limit(DECK_SIZE - k)
                                        .filter(Objects::nonNull)
                                        .mapToInt(Card::getValue)
                                        .allMatch(IntCard -> IntCard != cardValue)
                                        &&  (
                                           Except == null
                                        || Except
                                                .stream()
                                                .filter(Objects::nonNull)
                                                .mapToInt(Card::getValue)
                                                .allMatch(IntCard -> IntCard != cardValue)
                                )
                )
                .toArray();
        final int n = Using.length;
        /*Проверки на правильность данных и их клонирование*/
        if (k>7||k<0) {
            throw new IllegalArgumentException("IllegalK");
        }
        //Тут будет хранится обрабатываемая комбинация
        int[] processedCombination = null;
        while(true) {
            processedCombination = nextCombination(n, k, processedCombination);
            if (processedCombination == null)
                break;
            for (int i = DECK_SIZE - k, j = 0; i < DECK_SIZE; i++, j++) {
                deckClone[i] = new Card(Using[processedCombination[j]], Card.TABLE);
            }
            Combinations.add(AnalyzeCombination(deckClone).clone());
            /*Проверка на окончание работы*/
        }
        return Combinations;
    }

    public  class Builder {
        private Builder() {

        }

        public Builder setK (int k) {
            if (k < 0 || k > 7)
                throw new IllegalArgumentException("IllegalK : "+k);
            CombinationGenerator.this.k = k;
            return this;
        }

        public Builder kEqualsNumberOfNulls (boolean use) {
            if (use)
                CombinationGenerator.this.k = NUMBER_OF_NULLS;
            return this;
        }

        public Builder addException(Card card) {
            if (card == null)
                throw new NullPointerException("CardCannotBeNull");
            CombinationGenerator.this.Except.add(card);
            return this;
        }

        public Builder addAllExceptions(Collection<Card> cards) {
            if (cards == null)
                throw new NullPointerException("CardsCannotBeNull");
            CombinationGenerator.this.Except.addAll(cards);
            return this;
        }

        public Builder kEqualsAll(boolean use) {
            if (use)
                CombinationGenerator.this.k = DECK_SIZE;
            return this;
        }

        public CombinationGenerator build() {
            return CombinationGenerator.this.clone();
        }
    }

    @Override
    public CombinationGenerator clone() {
        try {
            return (CombinationGenerator) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
