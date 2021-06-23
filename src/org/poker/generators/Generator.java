package org.poker.generators;
import org.poker.core.Card;
import org.poker.core.Combination;
import java.util.List;

public interface Generator {
    List<Combination> generate(Card[] deck);
}
