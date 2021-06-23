package org.poker.analyzers;

import org.poker.core.Card;

@SuppressWarnings("unused")
public class ThreadPokerAnalyzer {

    private final Card[] Deck;

    private final int[] start;

    private final int[] end;

    private final int k;

    ThreadPokerAnalyzer(Card[] Deck, final int start[], final int[] end, final int k){
        this.Deck = Deck.clone();
        this.start = start.clone();
        this.end = end.clone();
        this.k = k;
    }
}
