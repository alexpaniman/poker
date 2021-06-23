package org.poker.core;

import org.poker.probability.WinProbability;
import java.math.BigInteger;
import java.util.*;
import static java.lang.Math.pow;

public abstract class PokerCore {
    public final int DECK_SIZE = 7;

    private static BigInteger Factorial(int n){
        BigInteger ret = BigInteger.ONE;
        for (int i = 1; i <= n; ++i) ret = ret.multiply(BigInteger.valueOf(i));
        return ret;
    }

    protected BigInteger NumberOfCombinations(int n, int k) {
        if (n < k) {
            throw new IllegalArgumentException("KIsBiggerThenN");
        }
        BigInteger result = BigInteger.ONE;
        for(int i = n-k+1; i<=n; i++)
            result=result.multiply(BigInteger.valueOf(i));
        return result.divide(Factorial(k));
    }

    protected int[] nextCombination(int N, int M, int[] arr) {
        if (arr == null)
        {
            arr = new int[M];
            for (int i = 0; i < M; i++)
                arr[i] = i;
            return arr;
        }
        for (int i = M - 1; i >= 0; i--)
            if (arr[i] < N - M + i)
            {
                arr[i]++;
                for (int j = i; j < M - 1; j++)
                    arr[j + 1] = arr[j] + 1;
                return arr;
            }
        return null;
    }

    protected byte AnalyzeGetCombination(Card[] deck) {
        if (deck == null) {
            throw new NullPointerException("DeckCannotBeNull");
        }
        Card[] Deck = deck.clone();
        Arrays.sort(Deck);
        Map<Byte, Byte> Repeats = new HashMap<>();
        Map<Byte, Byte> Flush = new HashMap<>();
        Map<Byte, Byte> Street = new HashMap<>();
        Map<Byte, Map<Byte, Byte>> StreetFlush = new HashMap<>();
        LinkedList<Byte> Set = new LinkedList<>();
        LinkedList<Byte> Pair = new LinkedList<>();
        for (byte i=0; i<4; i++) StreetFlush.put(i, new HashMap<>());
        for (Card card: Deck){
            byte rank = card.getRank();
            byte suit = card.getSuit();
            byte contains = -1;
            /*Определение Стритов*/
            for (Map.Entry<Byte, Byte> entry: Street.entrySet()) {
                byte key = entry.getKey();
                byte value = entry.getValue();
                if (key + value == rank) {
                    contains = Street.put(key, (byte) (value + 1));
                    break;
                }
            }
            if (contains==-1)
                Street.put(rank, (byte) 1);
            contains=-1;
            /*Определение СтритРоялФлешей и СтритФлешей*/
            for (Map.Entry<Byte, Byte> entry: StreetFlush.get(suit).entrySet()){
                byte key = entry.getKey();
                byte value = entry.getValue();
                if (key + value == rank) {
                    contains = StreetFlush.get(suit).put(key, (byte) (value + 1));
                    break;
                }
            }
            if (contains==-1)
                StreetFlush.get(suit).put(rank, (byte) 1);
            /*Определение комбинаций состояших из повторов*/
            if (Repeats.containsKey(rank))
                Repeats.put(rank, (byte) (Repeats.get(rank)+1));
            else
                Repeats.put(rank, (byte) 1);
            /*Определение Флешей*/
            if (Flush.containsKey(suit))
                Flush.put(suit, (byte) (Flush.get(suit)+1));
            else
                Flush.put(suit, (byte) 1);
        }
        /*Тесты*/
        for (Map<Byte, Byte> map : StreetFlush.values()) {
            for (Map.Entry<Byte, Byte> entry : map.entrySet()) {
                byte value = entry.getValue();
                byte key = entry.getKey();
                if (value >= 5) return (byte) ((value+key-1==12)? 9 : 8);
            }
        }
        for (Map.Entry<Byte, Byte> entry: Repeats.entrySet()){
            byte value = entry.getValue();
            byte key = entry.getKey();
            if (value==2)
                Pair.add(key);
            if (value==3)
                Set.add(key);
            if (value==4)
                return (byte) 7;
        }
        if (Set.size()==2)
            return (byte) 6;
        if (Set.size()==1&&Pair.size()>=1)
            return (byte) 6;
        for (Byte value: Flush.values())
            if (value>=5) return (byte) 5;
        for (Byte value: Street.values())
            if (value>=5)
                return (byte) 4;
        if (Set.size()==1)
            return (byte) 3;
        if (Pair.size()>=2)
            return (byte) 2;
        if (Pair.size()==1)
            return (byte) 1;
        return (byte) 0;
    }

    protected Combination AnalyzeCombination(Card[] deck) {
        if (deck == null) {
            throw new NullPointerException("DeckCannotBeNull");
        }
        Card[] Deck = deck.clone();
        Arrays.sort(Deck);
        Map<Byte, Byte> Repeats = new HashMap<>();
        Map<Byte, Byte> Flush = new HashMap<>();
        Map<Byte, Byte> Street = new HashMap<>();
        Map<Byte, Map<Byte, Byte>> StreetFlush = new HashMap<>();
        LinkedList<Byte> Set = new LinkedList<>();
        LinkedList<Byte> Pair = new LinkedList<>();
        for (byte i = 0; i < 4; i++) StreetFlush.put(i, new HashMap<>());
        for (Card card : Deck) {
            byte rank = card.getRank();
            byte suit = card.getSuit();
            byte contains = -1;
            /*Определение Стритов*/
            for (Map.Entry<Byte, Byte> entry : Street.entrySet()) {
                byte key = entry.getKey();
                byte value = entry.getValue();
                if (key + value == rank) {
                    contains = Street.put(key, (byte) (value + 1));
                    break;
                }
            }
            if (contains == -1)
                Street.put(rank, (byte) 1);
            contains = -1;
            /*Определение СтритРоялФлешей и СтритФлешей*/
            for (Map.Entry<Byte, Byte> entry : StreetFlush.get(suit).entrySet()) {
                byte key = entry.getKey();
                byte value = entry.getValue();
                if (key + value == rank) {
                    contains = StreetFlush.get(suit).put(key, (byte) (value + 1));
                    break;
                }
            }
            if (contains == -1)
                StreetFlush.get(suit).put(rank, (byte) 1);
            /*Определение комбинаций состояших из повторов*/
            if (Repeats.containsKey(rank))
                Repeats.put(rank, (byte) (Repeats.get(rank) + 1));
            else
                Repeats.put(rank, (byte) 1);
            /*Определение Флешей*/
            if (Flush.containsKey(suit))
                Flush.put(suit, (byte) (Flush.get(suit) + 1));
            else
                Flush.put(suit, (byte) 1);
        }
        /*Тесты*/
        for (Map<Byte, Byte> map : StreetFlush.values()) {
            for (Map.Entry<Byte, Byte> entry : map.entrySet()) {
                byte value = entry.getValue();
                byte key = entry.getKey();
                HashSet<Integer> Priority = new HashSet<>();
                for (int i = key + value - 5; i < key + value; i++)
                    Priority.add(i);
                LinkedList<Card> priorityCards = new LinkedList<>();
                LinkedList<Card> kickerCards = new LinkedList<>();
                HashSet<Byte> added = new HashSet<>();
                int handCard = -1;
                for (Card card : Deck) {
                    byte rank = card.getRank();
                    if (Priority.contains((int) rank)) {
                        if (card.getPlace() == Card.HAND) {
                            byte repeats = Repeats.get(card.getRank());
                            if (repeats >= 3) {
                                kickerCards.add(card);
                                continue;
                            }
                            if (repeats >= 2
                                    && handCard != rank) {
                                handCard = rank;
                                kickerCards.add(card);
                                continue;
                            }
                            if (!added.contains(rank)) {
                                priorityCards.add(card);
                                added.add(rank);
                            }
                        } else if (!added.contains(rank)) {
                            priorityCards.add(card);
                            added.add(rank);
                        }
                    } else if (card.getPlace() == Card.HAND)
                        kickerCards.add(card);
                }
                byte min = Byte.MAX_VALUE;
                byte index = -1;
                while (priorityCards.size() > 5) {
                    for (byte i = 0; i < priorityCards.size(); i++) {
                        byte rank = priorityCards.get(i).getRank();
                        if (rank < min) {
                            min = rank;
                            index = i;
                        }
                    }
                    priorityCards.remove(index);
                }
                if (value >= 5)
                    return new Combination((byte) ((value + key - 1 == 12) ? 9 : 8), priorityCards, kickerCards);
            }
        }
        for (Map.Entry<Byte, Byte> entry : Repeats.entrySet()) {
            byte value = entry.getValue();
            byte key = entry.getKey();
            if (value == 2)
                Pair.add(key);
            if (value == 3)
                Set.add(key);
            if (value == 4) {
                LinkedList<Card> kickerCards = new LinkedList<>();
                LinkedList<Card> priorityCards = new LinkedList<>();
                for (Card card : Deck)
                    if (card.getRank() == key)
                        priorityCards.add(card);
                    else if (card.getPlace() == Card.HAND)
                        kickerCards.add(card);
                return new Combination((byte) 7, priorityCards, kickerCards);
            }
        }
        if (Set.size() == 2) {
            LinkedList<Card> priorityCards = new LinkedList<>();
            LinkedList<Card> kickerCards = new LinkedList<>();
            byte max = Byte.MIN_VALUE;
            for (Card card : Deck) {
                byte rank = card.getRank();
                byte place = card.getPlace();
                if (Set.contains(rank)) {
                    if (place == Card.HAND)
                        max = (rank > max) ? rank : max;
                    priorityCards.add(card);
                } else {
                    if (place == Card.HAND)
                        kickerCards.add(card);
                }
            }
            if (max != Byte.MIN_VALUE)
                for (int i = 0; i < priorityCards.size(); i++)
                    if (priorityCards.get(i).getRank() == max
                            && priorityCards.get(i).getPlace() == Card.HAND) {
                        kickerCards.add(priorityCards.get(i));
                        priorityCards.remove(i);
                        break;
                    }
            if (priorityCards.size() == 6) {
                byte min = Byte.MAX_VALUE;
                byte index = -1;
                for (byte i = 0; i < priorityCards.size(); i++)
                    if (priorityCards.get(i).getRank() < min) {
                        min = priorityCards.get(i).getRank();
                        index = i;
                    }
                priorityCards.remove(index);
            }
            return new Combination((byte) 6, priorityCards, kickerCards);
        }
        if (Set.size() == 1 && Pair.size() >= 1) {
            LinkedList<Card> priorityCards = new LinkedList<>();
            LinkedList<Card> kickerCards = new LinkedList<>();
            for (Card card : Deck) {
                byte rank = card.getRank();
                if (Set.contains(rank)
                        || Pair.contains(rank)) {
                    priorityCards.add(card);
                    continue;
                }
                if (card.getPlace() == Card.HAND)
                    kickerCards.add(card);
            }
            return new Combination((byte) 6, priorityCards, kickerCards);
        }
        for (Map.Entry<Byte, Byte> entry : Flush.entrySet()) {
            byte value = entry.getValue();
            byte key = entry.getKey();
            if (value >= 5) {
                LinkedList<Card> priorityCards = new LinkedList<>();
                LinkedList<Card> kickerCards = new LinkedList<>();
                for (Card card : Deck) {
                    if (card.getSuit() == key) {
                        priorityCards.add(card);
                        continue;
                    }
                    if (card.getPlace() == Card.HAND)
                        kickerCards.add(card);
                }
                for (int i = 0; i < priorityCards.size(); i++) {
                    if (priorityCards.get(i).getPlace() == Card.HAND && priorityCards.size() > 5) {
                        kickerCards.add(priorityCards.get(i));
                        priorityCards.remove(i);
                    }
                }
                while (priorityCards.size() > 5) {
                    int min = Integer.MAX_VALUE;
                    int index = -1;
                    for (int i = 0; i < priorityCards.size(); i++) {
                        byte rank = priorityCards.get(i).getRank();
                        if (rank < min) {
                            min = rank;
                            index = i;
                        }
                    }
                    if (priorityCards.get(index).getPlace() == Card.HAND)
                        kickerCards.add(priorityCards.get(index));
                    priorityCards.remove(index);
                }
                return new Combination((byte) 5, priorityCards, kickerCards);
            }
        }
        for (Map.Entry<Byte, Byte> entry: Street.entrySet()) {
            byte value = entry.getValue();
            byte key = entry.getKey();
            byte end = (byte) (key + value - 1);
            if (value >= 5) {
                LinkedList<Card> priorityCards = new LinkedList<>();
                LinkedList<Card> kickerCards = new LinkedList<>();
                HashSet<Byte> Used = new HashSet<>();
                for (Card card : Deck) {
                    byte rank = card.getRank();
                    byte place = card.getPlace();
                    if (end - 5 < rank && rank <= end) {
                        if (Used.contains(rank)) {
                            if (place == Card.HAND)
                                kickerCards.add(card);
                        } else {
                            priorityCards.add(card);
                            Used.add(rank);
                        }
                    }
                    else
                    if (place == Card.HAND)
                        kickerCards.add(card);
                }
                while (priorityCards.size() > 5) {
                    int min = Integer.MAX_VALUE;
                    int index = -1;
                    for (int i = 0; i < priorityCards.size(); i++) {
                        byte rank = priorityCards.get(i).getRank();
                        if (min > rank) {
                            index = i;
                            min = rank;
                        }
                    }
                    if (priorityCards.get(index).getPlace() == Card.HAND)
                        kickerCards.add(priorityCards.get(index));
                    priorityCards.remove(index);
                }
                return new Combination((byte) 4, priorityCards, kickerCards);
            }
        }
        if (Set.size() == 1) {
            byte setRank = Set.get(0);
            LinkedList<Card> priorityCards = new LinkedList<>();
            LinkedList<Card> kickerCards = new LinkedList<>();
            for (Card card : Deck) {
                byte rank = card.getRank();
                byte place = card.getPlace();
                if (rank == setRank) {
                    priorityCards.add(card);
                    continue;
                }
                if (place == Card.HAND)
                    kickerCards.add(card);
            }
            return new Combination((byte) 3, priorityCards, kickerCards);
        }
        if (Pair.size() >= 2) {
            LinkedList<Card> priorityCards = new LinkedList<>();
            LinkedList<Card> kickerCards = new LinkedList<>();
            for (Card card : Deck) {
                byte rank = card.getRank();
                byte place = card.getPlace();
                if (Pair.contains(rank)) {
                    priorityCards.add(card);
                    continue;
                }
                if (place == Card.HAND)
                    kickerCards.add(card);
            }
            while (priorityCards.size()>4){
                byte min = Byte.MAX_VALUE;
                byte index = -1;
                for (int i = 0; i < priorityCards.size(); i++) {
                    byte rank = priorityCards.get(i).getRank();
                    if (min > rank) {
                        min = rank;
                        index = (byte) i;
                    }
                }
                if (priorityCards.get(index).getPlace() == Card.HAND)
                    kickerCards.add(priorityCards.get(index));
                priorityCards.remove(index);
            }
            return new Combination((byte) 2, priorityCards, kickerCards);
        }
        if (Pair.size() == 1) {
            LinkedList<Card> priorityCards = new LinkedList<>();
            LinkedList<Card> kickerCards = new LinkedList<>();
            byte pairRank = Pair.get(0);
            for (Card card : Deck) {
                byte rank = card.getRank();
                byte place = card.getPlace();
                if (rank == pairRank)
                    priorityCards.add(card);
                else
                if (place == Card.HAND)
                    kickerCards.add(card);
            }
            return new Combination((byte) 1, priorityCards, kickerCards);
        }
        LinkedList<Card> kickerCards = new LinkedList<>();
        for (Card card : Deck) {
            byte place = card.getPlace();
            if (place == Card.HAND)
                kickerCards.add(card);
        }
        return new Combination((byte)0, null, kickerCards);
    }

    protected WinProbability Generalization(WinProbability prb, int opponents) {
        double[] WinProbabilityResult = new double[3];
        WinProbabilityResult[2] = pow(prb.getCurrent(2), opponents);
        WinProbabilityResult[1] = pow(1 - prb.getCurrent(0), opponents) - WinProbabilityResult[2];
        WinProbabilityResult[0] = 1 - (WinProbabilityResult[1] + WinProbabilityResult[2]);
        return new WinProbability(WinProbabilityResult);
    }

}
