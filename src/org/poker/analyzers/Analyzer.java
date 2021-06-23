package org.poker.analyzers;

import org.poker.core.Card;
import org.poker.probability.Probability;

public interface Analyzer {
    Probability analyze(Card[] deck);
}
