package org.poker.analyzers;

import org.poker.core.*;
import org.poker.generators.CombinationGenerator;
import org.poker.generators.Generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CombinationAnalyzer extends PokerCore {
    public Combination find(Card[] deck, Function<Card[], Collection<Combination>> func, Function<Collection<Combination>, Combination> combinationFunc) {
        return combinationFunc.apply(func.apply(deck));
    }

    public Combination findMin(Collection<Combination> combinations) {
        if (combinations == null||combinations.size() < 1)
            return null;
        return combinations
            .stream()
            .filter(Objects::nonNull)
            .min(Combination::compareTo)
            .orElse(null);
    }

    public Combination findMax(Collection<Combination> combinations) {
        if (combinations == null||combinations.size() < 1)
            return null;
        return combinations
                .stream()
                .filter(Objects::nonNull)
                .max(Combination::compareTo)
                .orElse(null);
    }

    public Combination find(Collection<Combination> combinations, Predicate<Combination> test) {
        return
                combinations
                        .stream()
                        .filter(test)
                        .findAny()
                        .orElse(null);
    }

    public List<Combination> findAll(Collection<Combination> combinations, Predicate<Combination> test) {
        return
                combinations
                        .stream()
                        .filter(test)
                        .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean AnyMatch(Collection<Combination> combinations, Predicate<Combination> test) {
        return
                combinations
                        .stream()
                        .anyMatch(test);
    }

    public boolean AllMatch(Collection<Combination> combinations, Predicate<Combination> test) {
        return
                combinations
                        .stream()
                        .allMatch(test);
    }

    public Combination currentCombination(Card[] deck, int k) {
        Generator gen = CombinationGenerator.newBuilder()
                .setK(k)
                .build();
        return find(deck, gen::generate, this::findMin);
    }

    public Combination currentCombination(Card[] deck) {
        Generator gen = CombinationGenerator.newBuilder()
                .kEqualsNumberOfNulls(true)
                .build();
        return find(deck, gen::generate, this::findMin);
    }
}
