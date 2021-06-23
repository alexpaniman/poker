package org.poker.analyzers.deprecated;

import org.poker.core.Card;

import java.math.BigInteger;
import java.util.*;
import org.poker.core.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.*;
@SuppressWarnings("unused")
public class PokerAnalyzer {
    /**
     * @throws IllegalArgumentException
     * @throws NullPointerException
     * @author AlexPaniman
     */
    private final Logger log;
    private static final int deckSize =7;
    static final boolean USE_HANDS = false;
    static final boolean DO_NOT_USE_HANDS = true;
    PokerAnalyzer () {
       log = new Logger();
    }
    PokerAnalyzer (Logger log) {
        this.log = log;
    }
    private BigInteger Factorial(int n){
        if (n > 1000) log.warning("Вычесление факториала из ", n);
        log.trace("Начало подсчёта факториала n = ", n);
        BigInteger ret = BigInteger.ONE;
        for (int i = 1; i <= n; ++i) ret = ret.multiply(BigInteger.valueOf(i));
        log.debug("Факториал из ", n, " -> ", ret);
        log.trace("Посчитан факториал из n = ", n, " -> ", ret);
        return ret;
    }
    protected BigInteger NumberOfCombinations(int n, int k) {
        log.trace("Начало подсчёта количества сочетаний из n = ",  n, " по k = ", k);
        if (n < k) {
            log.fatal("Попытка подсчёта количества сочетаний из ", n, " по ", k);
            throw new IllegalArgumentException("KIsBiggerThenN");
        }
        if (n > 10000 || k > 10) log.warning("Подсчёт количества сочетаний из ", n, " по ", k);
        BigInteger result = BigInteger.ONE;
        for(int i = n-k+1; i<=n; i++)
            result=result.multiply(BigInteger.valueOf(i));
        BigInteger finalResult = result.divide(Factorial(k));
        log.debug("Сочетания из ", n, " по ", k, " -> ", finalResult);
        log.trace("Посчитано количество сочетаний из n = ", n, " по k = ", k, " -> ", finalResult);
        return finalResult;
    }
    private int[] nextCombination(int N, int M, int[] arr){
        if (log.TRACE)
            log.trace("Генерация следующего сочетания из ", Arrays.toString(arr));
        if (arr == null)
        {
            arr = new int[M];
            for (int i = 0; i < M; i++)
                arr[i] = i + 1;
            if (log.TRACE)
                log.trace("Сгенерировано следующее сочетание из N = ", N, " по M (K) = ", M, " -> ", Arrays.toString(arr));
            return arr;
        }
        for (int i = M - 1; i >= 0; i--)
            if (arr[i] < N - M + i + 1)
            {
                arr[i]++;
                for (int j = i; j < M - 1; j++)
                    arr[j + 1] = arr[j] + 1;
                if (log.TRACE)
                    log.trace("Сгенерировано следующее сочетание из N = ", N, " по M (K) = ", M, " -> ", Arrays.toString(arr));
                return arr;
            }
            if (log.TRACE)
                log.trace("Сгенерировано следующее сочетание из N = ", N, " по M (K) = ", M, " -> null");
        return null;
    }
    byte AnalyzeGetCombination (Card[] deck) {
        if (log.TRACE)
            log.trace("Начат анализ колоды ", Arrays.toString(deck));
        if (deck == null) {
            log.fatal("Передана нулевая колода (AnalyzeGetCombination");
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
        byte quad = -1;
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
    Combination AnalyzeCombination (Card[] deck) {
        if (log.TRACE)
            log.trace("Начат анализ колоды ", Arrays.toString(deck));
        if (deck == null) {
            log.fatal("Передана нулевая колода (AnalyzeGetCombination");
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
        byte quad = -1;
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
                        if (card.getPlace() == 0) {
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
                    } else if (card.getPlace() == 0)
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
                    else if (card.getPlace() == 0)
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
                    if (place == 0)
                        max = (rank > max) ? rank : max;
                    priorityCards.add(card);
                } else {
                    if (place == 0)
                        kickerCards.add(card);
                }
            }
            if (max != Byte.MIN_VALUE)
                for (int i = 0; i < priorityCards.size(); i++)
                    if (priorityCards.get(i).getRank() == max
                            && priorityCards.get(i).getPlace() == 0) {
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
                if (card.getPlace() == 0)
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
                    if (card.getPlace() == 0)
                        kickerCards.add(card);
                }
                for (int i = 0; i < priorityCards.size(); i++) {
                    if (priorityCards.get(i).getPlace() == 0 && priorityCards.size() > 5) {
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
                    if (priorityCards.get(index).getPlace() == 0)
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
                            if (place == 0)
                                kickerCards.add(card);
                        } else {
                            priorityCards.add(card);
                            Used.add(rank);
                        }
                    }
                    else
                        if (place == 0)
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
                    if (priorityCards.get(index).getPlace() == 0)
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
                if (place == 0)
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
                if (place == 0)
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
                if (priorityCards.get(index).getPlace() == 0)
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
                    if (place == 0)
                        kickerCards.add(card);
            }
            return new Combination((byte) 1, priorityCards, kickerCards);
        }
        LinkedList<Card> kickerCards = new LinkedList<>();
        for (Card card : Deck) {
            byte place = card.getPlace();
            if (place == 0)
                kickerCards.add(card);
        }
        return new Combination((byte)0, null, kickerCards);
    }
    LinkedList<Combination> GetAllPossibleCombinations (final Card[] Deck, final int start[], final int[] end, final HashSet<Integer> except, boolean useHands, final int k){
            /*Инициализация*/
        if (log.TRACE)
            log.trace("Начало генерации всех возможных комбинаций. \n\t\tКолода : ", Arrays.toString(Deck), ". \n\t\tНачало перебора : ", Arrays.toString(start), ". \n\t\tКонец перебора : ", Arrays.toString(end), ". \n\t\tИсключения : ", (except != null)? except : "null", ". \n\t\tИспользовать руки : ", (useHands)? "Да":"Нет",". \n\t\tK = ", k, ".");
        if (log.DEBUG)
            log.debug("Получение всех возможных комбинаций. \n\t\tКолода : ", Arrays.toString(Deck), ". \n\t\tНачало перебора : ", Arrays.toString(start), ". \n\t\tКонец перебора : ", Arrays.toString(end), ". \n\t\tИсключения : ", (except != null)? except : "null", ". \n\t\tИспользовать руки : ", (useHands)? "Да":"Нет",". \n\t\tK = ", k, ".");
        if (Deck == null) {
            log.fatal("Передана нулевая колода");
            throw new NullPointerException("DeckCannotBeNull");
        }
        final int n = 52 - deckSize + k;
        final int[] startClone, endClone;
        LinkedList<Combination> Combinations = new LinkedList<>();
        Card[] deckClone;
        if (Deck.length == deckSize) deckClone = Deck.clone();
        else {
            log.fatal("Неправильная длина колоды : ", Deck.length, ". Ожидаемая : ", deckSize);
            throw new IllegalArgumentException("IllegalTypeOfDeck");
        }
        HashSet<Integer> Used = new HashSet<>();
        for (int i = 0; i < deckSize - k; i++)
            Used.add((int) Deck[i].getValue());
        int[] Using = new int[n];
        for (int i = Using.length - 1; i >= 0; i--) {
            if (Used.contains(i)) {
                for (int j = 0; j < 52; j++)
                    if (!Used.contains(j))
                        Using[i] = j; 
            } else
                Using[i] = i;
            Used.add(Using[i]); 
        }
        /*Проверки на правильность данных и их клонирование*/
        if (k>7||k<0) {
            log.fatal("Передано неправильное значение K = ", k);
            throw new IllegalArgumentException("IllegalK"); 
        }
        if (start != null) {
            if (start.length == k) startClone = start.clone();
            else {
                log.fatal("Неправильная длина [старта] = ", Arrays.toString(start), ". Ожидаемая длина = K (длина перебора) = ", k);
                throw new IllegalArgumentException("InconsistencyInTheSizeOfTheStartArrayAndK"); 
            }
        } else startClone = null;
        if (end != null) {
            if (end.length == k) endClone = end.clone();
            else {
                log.fatal("Неправильная длина [конца] = ", Arrays.toString(start), ". Ожидаемая длина = K (длина перебора) = ", k);
                throw new IllegalArgumentException("InconsistencyInTheSizeOfTheEndArrayAndK"); 
            } 
        } else endClone = null;
        log.trace("Все входные данные в норме");
        if (useHands && (Arrays.stream(Deck).limit(deckSize - k).filter(Objects::nonNull).filter(card -> card.getPlace() == 0).count() >= 2)) {
            log.error("Во всех анализируемых колодах будут генерироватся ещё две карты в руку. Это может привести к ошибкам так как в колоде уже есть две карты находящиеся в руке.");
            log.trace("Возможны ошибки!"); 
        }            
        final long all = NumberOfCombinations(n - ((except == null)? 0 : except.size()), k).longValue();
        double counter = 0;
        int percent = -1;
        //Тут будет хранится обрабатываемая комбинация
        int[] processedCombination =  startClone;
        //Тут будет хранится результат анализа
        Combination Comb;
        mainLoop: do {
            processedCombination = nextCombination(n, k, processedCombination);
            if (processedCombination == null)
                break;
            for (int i = deckSize - k, j = 0; i < deckSize; i++, j++) {
                if (except != null && except.contains(Using[processedCombination[j] - 1]))
                    continue mainLoop;
                if (useHands&&j<2)
                    deckClone[i] = new Card(Using[processedCombination[j] - 1], 0);
                else
                    deckClone[i] = new Card(Using[processedCombination[j] - 1], 1); 
            }
            Comb = AnalyzeCombination(deckClone);
            if (log.LOG&&log.test(Comb)){
                log.log("-------------------------------");
                log.log("Колода:");
                for (Card card: deckClone)
                    log.log("\t", card);
                log.log(Comb);
                log.log("_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_");
                log.flush();
                log.logCounterIncrement(); 
            }
            Combinations.add(Comb.clone());
            if ((int)((counter/all)*100) > percent) {
                percent = (int) round((counter/all)*100);
                if (log.ONLY_INFO) {
                    log.infoWithBackR("Генерация всех комбинаций", log.append, " : ", percent, " %");
                } else
                    log.info("Генерация всех комбинаций", log.append, " : ", percent, " %");
            }
            counter++;
            /*Проверка на окончание работы*/
        } while (!Arrays.equals(processedCombination,endClone));
        if (counter > Integer.MAX_VALUE) log.error("Сгенерированно очень много комбинаций : ", (int) counter, ". Размер результативного LinkedList будет подсчитан не верно.");
        if (log.ONLY_INFO) {
            log.infoWithBackR("Генерация всех комбинаций", log.append, " завершена. Сгенерированно : ", (int) counter);
            if (log.INFO)
                log.NL();
        } else {
            log.info("Генерация всех комбинаций", log.append, " завершена. Сгенерированно : ", (int) counter);
        }
        log.trace("Генерация завершена. Сгенерировано : ", (int) counter);
        return Combinations;
    }
    double[] GetProbability (final Card[] Deck, final int start[], final int[] end, final HashSet<Integer> except, final int k){
                        /*Инициализация*/
        if (log.TRACE)
            log.trace("Начат анализ всех возможных комбинаций. \n\t\tКолода : ", Arrays.toString(Deck), ". \n\t\tНачало перебора : ", Arrays.toString(start), ". \n\t\tКонец перебора : ", Arrays.toString(end), ". \n\t\tИсключения : ", (except != null)? except : "null",". \n\t\tK = ", k, ".");
        if (log.DEBUG)
            log.debug("Анализ всех возможных комбинаций. \n\t\tКолода : ", Arrays.toString(Deck), ". \n\t\tНачало перебора : ", Arrays.toString(start), ". \n\t\tКонец перебора : ", Arrays.toString(end), ". \n\t\tИсключения : ", (except != null)? except : "null", ". \n\t\tK = ", k, ".");
        if (Deck == null) {
            log.fatal("Передана нулевая колода.");
            throw new NullPointerException("DeckCannotBeNull");
        }
        final int n = 52 - deckSize + k;
        double[] Probability = new double[10];
        final int[] startClone, endClone;
        Card[] deckClone;
        if (Deck.length == deckSize) deckClone = Deck.clone();
        else {
            log.fatal("Неправильная длина колоды : ", Deck.length, ". Ожидаемая : ", deckSize);
            throw new IllegalArgumentException("IllegalTypeOfDeck");
        }
        HashSet<Integer> Used = new HashSet<>();
        for (int i = 0; i < deckSize - k; i++)
            Used.add((int) Deck[i].getValue());
        int[] Using = new int[n];
        for (int i = 0; i < Using.length; i++) {
            if (Used.contains(i)) {
                for (int j = 51; j >= 0; j--)
                    if (!Used.contains(j))
                        Using[i] = j;
            } else
                Using[i] = i;
            Used.add(Using[i]);
        }
                /*Проверки на правильность данных и их клонирование*/
        if (k>7||k<0) {
            log.fatal("Передано неправильное значение K = ", k);
            throw new IllegalArgumentException("IllegalK");
        }
        if (start != null) {
            if (start.length != k) {
                log.fatal("Неправильная длина [старта] = ", Arrays.toString(start), ". Ожидаемая длина = K (длина перебора) = ", k);
                throw new IllegalArgumentException("InconsistencyInTheSizeOfTheStartArrayAndK");
            } else startClone = start.clone();
        } else startClone = null;
        if (end != null) {
            if (end.length != k) {
                log.fatal("Неправильная длина [конца] = ", Arrays.toString(start), ". Ожидаемая длина = K (длина перебора) = ", k);
                throw new IllegalArgumentException("InconsistencyInTheSizeOfTheEndArrayAndK");
            } else endClone = end.clone();
        } else endClone = null;
        log.trace("Все входные данные в норме");
        long all = NumberOfCombinations(n + ((except == null)? 0:except.size()), k).longValue();
        if (all > Integer.MAX_VALUE) log.warning("Будет проанализированно очень много комбинаций : ", all);
        int percent = -1;
        double counter = 0;
        //Тут будет хранится обрабатываемая комбинация
        int[] processedCombination = startClone;
        //Тут будет хранится результат анализа
        mainLoop: do{
            processedCombination = nextCombination(n,k ,processedCombination);
            if (processedCombination == null)
                break;
            for (int i = deckSize - k, j = 0; i < deckSize; i++, j++) {
                if (except != null && except.contains(Using[processedCombination[j]-1]))
                    continue mainLoop;
                deckClone[i] = new Card(Using[processedCombination[j]-1], 1);
            }
            int Comb = AnalyzeGetCombination(deckClone);
            if (log.LOG) {
                Combination combination = new Combination((byte) Comb, null, null);
                if(log.test(combination)){
                    log.log("-------------------------------");
                    log.log(combination.toStringOnlyComb());
                    log.log("\tКолода: ");
                    for (Card card: deckClone)
                        log.log("\t\t", card);
                    log.log("_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_");
                    log.logCounterIncrement();
                    log.flush();
                }
            }
            Probability[Comb]++;
            if ((int)((counter/all)*100) > percent) {
                percent = (int) round((counter/all)*100);
                log.info("Анализ всех возможных комбинаций", log.append," : ", percent, " %");
            }
            counter++;
                    /*Проверка на окончание работы*/
        } while (!Arrays.equals(processedCombination,endClone));
                            /*Получение вероятностей*/
        double SumAll = Arrays.stream(Probability).sum();
        for (int i = 0; i < Probability.length; i++)
            Probability[i] = Probability[i]/SumAll;
        if (log.ONLY_INFO) {
            log.infoWithBackR("Анализ всех возможных комбинаций", log.append, " завершен. Проанализированно : ", SumAll);
            if (log.INFO)
                log.NL();
        } else {
            log.info("Анализ всех возможных комбинаций", log.append, " завершен. Проанализированно : ", SumAll);
        }
        log.trace("Анализ завершен. Проанализированно : ", SumAll);
        return Probability;
    }
    double[] GetWinProbabilityMonteCarlo (final Card[] Deck, final int opponents, final int k, final double rnd){
        if (log.TRACE)
            log.trace("Начат анализ шансов на победу методом Монте-Карло. \n\t\tКолода : ", Arrays.toString(Deck), ". \n\t\tКоличество оппоненетов : ", opponents, ". \n\t\tK = ", k, ". \n\t\tКоэффициент перебора : ", rnd, ".");
        if (log.DEBUG) 
            log.debug("Анализ шансов на победу методом Монте-Карло. \n\t\tКолода : ", Arrays.toString(Deck), ". \n\t\tКоличество оппоненетов : ", opponents, ". \n\t\tK = ", k, ". \n\t\tКоэффициент перебора : ", rnd, ".");
        if (Deck == null) {
            log.fatal("Передана нулевая колода");
            throw new NullPointerException("DeckCannotBeNull");
        }
        if (k>7||k<0) {
            log.fatal("Передано неправильное значение K = ", k);
            throw new IllegalArgumentException("IllegalK");
        }
        if (rnd > 20) log.warning("Передан очень большой коэффициент перебора : ", rnd, " = ", rnd*100, "%. Анализ может занять много времени и вероятно будет низкоэффективным.");
        if (rnd < 0) {
            log.fatal("Передан отрицательный коэффициент пребора = ",  rnd, ".");
            throw new IllegalArgumentException("RndCannotBeNegative");
        }
        if (rnd == 0) {
            log.fatal("Передан нулевой коэффициент.");
            throw new IllegalArgumentException("RndCannotBeNull");
        }
        if (opponents == 0) {
            log.error("Игрок играет сам с собой!");
            return new double[] {0, 0, 1};
        }
        if (opponents>22 || opponents<0) {
            log.fatal("Передано неправильное количество оппонентов : ", opponents);
            throw new IllegalArgumentException("WrongNumberOfOpponents");
        }
        Card[] deckTableClone = new Card[deckSize - 2 - k];
        Card[] deckHandClone = new Card[2];
        double[] WinCompare = new double[3];
        int n = 52 - deckSize + k;
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        log.trace("Для генерации псевдослучайных чисел используется Seed = ", seed);
        HashSet<Byte> Used = new HashSet<>();
        LinkedList<Byte> Using = new LinkedList<>();
        Combination Player;
        Card[] deckOpponents = new Card[deckSize];
        Card[] deckPlayer = new Card[deckSize];
        int tempHandCounter = 0, tempTableCounter = 0;
        for (int i = 0; i < deckSize-k; i++)
            if (Deck[i].getPlace()==0)
                deckHandClone[tempHandCounter++] = Deck[i].clone();
            else
                deckTableClone[tempTableCounter++] = Deck[i].clone();
        for (int i = 0; i < deckSize - k; i++)
            Used.add(Deck[i].getValue());
        for (byte i = 0; i < n; i++) {
            if (Used.contains(i)) {
                for (byte j = 51; j >= 0; j--)
                    if (!Used.contains(j)) {
                        Using.add(j);
                        Used.add(i);
                    }
            } else {
                Using.add(i);
                Used.add(i);
            }
        }
        tempHandCounter = 0;
        tempTableCounter = 0;
        for (Card card: deckTableClone){
            deckOpponents[tempTableCounter++] = card;
            deckPlayer[tempHandCounter++] = card;
        }
        for (Card card: deckHandClone){
            deckPlayer[tempHandCounter++] = card;
        }
        if (log.TRACE) {
            log.trace("Сформирован набор карт игрока : ", Arrays.toString(deckPlayer));
            log.trace("Сформирован набор карт оппонента : ", Arrays.toString(deckOpponents));
        }
        long MaxCounter = Math.round(NumberOfCombinations(n, k).longValue() * rnd);
        log.trace("Будет проанализированно : ", MaxCounter, " партий.");
        int currentRandom;
        int min = Integer.MAX_VALUE;
        int indN = n;
        double counter = 0;
        int percent = -1;
        for (long count = 0; count < MaxCounter; count++){
            for (int i = deckSize-k; i < deckSize; i++){
                currentRandom = random.nextInt(indN--);
                byte temp = Using.get(currentRandom);
                Using.remove(currentRandom);
                Using.add(temp);
                deckPlayer[i] = new Card(Using.get(currentRandom), 1);
            }
            Player = AnalyzeCombination(deckPlayer);
            if (log.LOG) {
                log.log("-------------------------------");
                log.log("Колода Игрока : ");
                for (Card card: deckPlayer)
                    log.log("\t", card);
                log.log(Player);
            }
            for (int i = deckSize - k, j = deckSize - k - 2; i < deckSize; i++, j++) {
                deckOpponents[j] = deckPlayer[i];
            }
            for (int opponent = 0; opponent < opponents; opponent++){
                for (int i = deckSize-2; i < deckSize; i++){
                    currentRandom = random.nextInt(indN--);
                    byte temp = Using.get(currentRandom);
                    Using.remove(currentRandom);
                    Using.add(temp);
                    deckOpponents[i] = new Card(Using.get(currentRandom), 0);
                }
                Combination OpponentCombination = AnalyzeCombination(deckOpponents);
                if (log.LOG) {
                    log.log("Колода", ((opponents == 1)? "" : opponent), " противника : ");
                    for (Card card: deckOpponents)
                        log.log("\t",card);
                    log.log(OpponentCombination);
                }
                min = Math.min(Player.compareTo(OpponentCombination), min);
                min = (int) Math.signum((double) min);
                if (min == -1) break;
            }
            if (log.LOG) {
                log.log("\n\t\t\tРезультат партии : ", ((min == -1)? "поражение." : ((min == 0)? "ничья." : "победа.")));
                log.log("_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_");
                log.logCounterIncrement();
                log.flush();
            }
            if ((int)((counter/MaxCounter)*100) > percent) {
                percent = (int) round((counter/MaxCounter)*100);
                if (log.ONLY_INFO)
                    log.infoWithBackR("Анализируются партии, которые могут быть разыграны : ", percent, " %");
                else
                    log.info("Анализируются партии, которые могут быть разыграны : ", percent, " %");
            }
            counter++;
            WinCompare[min+1]++;
            min = Integer.MAX_VALUE;
            indN = n;
        }
        if (!log.ONLY_INFO) {
            log.info("Анализ возможных партий", log.append, " завершен. Проанализированно : ", MaxCounter);
        } else {
            log.infoWithBackR("Анализ возможных партий", log.append, " завершен. Проанализированно : ", MaxCounter);
            if (log.INFO)
                log.NL();
        }
        log.trace("Анализ шансов на победу методом Монте-Карло завершён. Было проанализировано ", MaxCounter, " возможных партий.");
        for (int i = 0; i<WinCompare.length; i++)
            WinCompare[i] = WinCompare[i]/MaxCounter;
        return WinCompare;
    }
    double[] GetWinProbability (double[] Player, double[] Opponent) {
        if (log.TRACE)
            log.trace("Начато вычесление шансов на победу из \n", Arrays.toString(Player), "\n", Arrays.toString(Opponent));
        if (log.TRACE)
            log.trace("Вычесление шансов на победу из \n", Arrays.toString(Player), "\n", Arrays.toString(Opponent));
        if (Player == null || Opponent == null) {
            log.fatal("Передан нулевой массив. Аргументы : \n", Arrays.toString(Player), "\n", Arrays.toString(Opponent));
            throw new NullPointerException("PlayerAndOpponentCannotBeNull");
        }
        double[] WinProbability = new double[3];
        for (int i = 0; i < Player.length; i++) {
            double player = Player[i];
            for (int j = 0; j < Opponent.length; j++) {
                double opponent = Opponent[j];
                WinProbability[(i < j)? 0 : ((i == j)? 1 : 2)] += opponent * player;
            }
        }
        if (log.TRACE)
            log.trace("Вычесление завершено. Результат -> ", Arrays.toString(WinProbability));
        return WinProbability;
    }
    static double[] GetWinProbability (double[] WinProbability, int Opponents){
        Logger log = new Logger();
        if (log.TRACE)
            log.trace("Начато обобщение шансов на победу : ", Arrays.toString(WinProbability), " для ", Opponents, " оппонентов.");
        if (log.DEBUG)
            log.debug("Обобщение шансов на победу : ", Arrays.toString(WinProbability), " для ", Opponents, " оппонентов.");
        if (Opponents > 22) log.warning("Передано слишком большое число оппонентов (больше чем технически может играть в покер).");
        if (Opponents < 0) {
            log.fatal("Передано отрицательное количество оппонентов");
            throw new IllegalArgumentException("OpponentsCannotBeNegative");
        }
        if (Opponents == 0) {
            log.error("Игрок играет сам с собой!");
            if (log.TRACE)
                log.trace("Вычесление завершено. Результат -> [0, 0, 1]");
            return new double[] {0, 0, 1};
        }
        if (Opponents == 1) {
            if (log.TRACE)
                log.trace("Вычесление завершено. Результат -> ", Arrays.toString(WinProbability));
            return WinProbability;
        }
        double[] WinProbabilityResult = new double[3];
        WinProbabilityResult[2] = pow(WinProbability[2], Opponents);
        WinProbabilityResult[1] = pow(1 - WinProbability[0], Opponents) - WinProbabilityResult[2];
        WinProbabilityResult[0] = 1 - (WinProbabilityResult[1] + WinProbabilityResult[2]);
        if (log.TRACE)
            log.trace("Вычесление завершено. Результат -> ", Arrays.toString(WinProbability));
        return  WinProbabilityResult;
    }
    double[] GetWinProbability (double[] Player, double[] Opponent, int Opponents) {
        return GetWinProbability(GetWinProbability(Player, Opponent), Opponent);
    }
    double[] GetWinProbability (Combination[] Player, Combination[] Opponent){
        if (log.TRACE)
            log.trace("Начато вычесление шансов на победу из \n", Arrays.toString(Player), "\n", Arrays.toString(Opponent));
        if (log.TRACE)
            log.trace("Вычесление шансов на победу из \n", Arrays.toString(Player), "\n", Arrays.toString(Opponent));
        if (Player == null || Opponent == null) {
            log.fatal("Передан нулевой массив. Аргументы : \n", Arrays.toString(Player), "\n", Arrays.toString(Opponent));
            throw new NullPointerException("PlayerAndOpponentCannotBeNull");
        }
        double[] WinProbability = new double[3];
        for (Combination player : Player) {
            for (Combination opponent : Opponent) {
                WinProbability[(int) signum((float) player.compareTo(opponent)) + 1]++;
            }
        }
        double all = Arrays.stream(WinProbability).sum();
        double[] result = Arrays.stream(WinProbability).map(el -> el/all).toArray();
        if (log.TRACE)
            log.trace("Вычесление завершено. Результат -> ", Arrays.toString(result));
        return result;
    }
    double[] GetWinProbability (LinkedList<Combination> Player, LinkedList<Combination> Opponent){
        if (log.TRACE)
            log.trace("Начато вычесление шансов на победу из \n", Player, "\n", Opponent);
        if (log.TRACE)
            log.trace("Вычесление шансов на победу из \n", Player, "\n", Opponent);
        if (Player == null || Opponent == null) {
            log.fatal("Передан нулевой массив. Аргументы : \n", Player, "\n", Opponent);
            throw new NullPointerException("PlayerAndOpponentCannotBeNull");
        }
        double[] WinProbability = new double[3];
        double count = 0;
        double all = Player.size()*Opponent.size();
        int percent = 0;
        for (int i = 0; i < Player.size(); i++) {
            Combination player = Player.get(i);
            for (int j = 0; j < Opponent.size(); j++) {
                Combination opponent = Opponent.get(j);
                WinProbability[(int) signum((float) player.compareTo(opponent)) + 1]++;
                if ((int)((count/all)*100) > percent) {
                    log.info("Сравниваются комбинации : ", percent, " %");
                    log.info("\r");
                    percent = (int) round((count/all)*100);
                }
                count++;
            }
        }
        double[] result = Arrays.stream(WinProbability).map(el -> el/all).toArray();
        if (log.TRACE)
            log.trace("Вычесление завершено. Результат -> ", result);
        return result;
    }
    double[] Simplify (LinkedList<Combination> combinations){
        double[] result = new double[10];
        combinations
                .parallelStream()
                .mapToInt(Combination::getCombination)
                .forEach(el -> result[el]++);
        double sumAll = Arrays
                .stream(result)
                .sum();
        for (int i = 0; i<result.length; i++)
            result[i] = result[i]/sumAll;
        return result;
    }
    double[] Simplify (double[] Probability) {
        if (Probability.length % 10 != 0) throw new IllegalArgumentException("IllegalProbabilitySize ("+Probability.length+")");
        double[] result = new double[10];
        double len = Probability.length/10;
        int count = 0;
        for (int i = 0; i < Probability.length; i++) {
            result[count] += Probability[i];
            if (i % len == 0) count++;
        }
        return result;
    }
    double[] GetWinProbability (final Card[] Deck, final int opponents, final int k, final int start[], final int[] end) {
       double[] WinProbability = new double[3];
       Card[] OpponentCards =
            Arrays.copyOf(
               Arrays
               .stream(Deck)
               .limit(deckSize - k)
               .filter(card -> card.getPlace() == 1)
               .map(Card::clone).toArray(Card[]::new)
            , 7);
       Card[] PlayerCards =
            Arrays.copyOf(
               Arrays
               .stream(Deck)
               .limit(deckSize - k)
               .map(Card::clone)
               .toArray(Card[]::new)
            ,7);
       HashSet<Integer> Used = Arrays
               .stream(Deck)
               .limit(deckSize - k)
               .mapToInt(Card::getValue)
               .boxed()
               .collect(Collectors.toCollection(HashSet::new));
       int n = 52 - deckSize + k - 2;
       int[] Using = IntStream
               .rangeClosed(0, 51)
               .filter(el -> !Used.contains(el))
               .toArray();
       int[] processedCombinations = (start == null)? null : start.clone();
       int[] endClone = (end == null)? null : end.clone();
       int[] tableCombination = null;
       do {
           processedCombinations = nextCombination(n, 2, processedCombinations);
           if (processedCombinations == null) break;
           HashSet<Integer> usedTable = new HashSet<>();
           for (int j = deckSize - k - 2, i = 0; j < deckSize - k; j++, i++) {
               Card card = new Card(Using[processedCombinations[i]-1], 0);
               OpponentCards[j] = card;
               usedTable.add((int)card.getValue());
           }
           int[] TableUsing = Arrays
                   .stream(Using)
                   .filter(Value -> !usedTable.contains(Value))
                   .toArray();
           while (true) {
               tableCombination = nextCombination(n - 2, k, tableCombination);
               if (tableCombination == null) break;
               for (int j = deckSize - k, c = 0; j < deckSize; j++, c++) {
                   OpponentCards[j] = new Card(TableUsing[tableCombination[c]-1], 1);
                   PlayerCards[j] = new Card(TableUsing[tableCombination[c]-1], 1);
               }
               Combination player = AnalyzeCombination(PlayerCards);
               Combination opponent = AnalyzeCombination(OpponentCards);
               WinProbability[(int)signum((float) (player.compareTo(opponent)))+1]++;
           }
       } while (!Arrays.equals(processedCombinations, endClone));
       double all = Arrays
               .stream(WinProbability)
               .sum();
       IntStream
               .range(0, 3)
               .forEach(index -> WinProbability[index] = WinProbability[index]/all);
       return GetWinProbability(WinProbability, opponents);
    }
    Combination MinCombination (LinkedList<Combination> combinations) {
        if (combinations == null || combinations.size() == 0) return null;
        Combination min = new Combination((byte)10, null, null);
        for (Combination processedComb : combinations) {
            if ((int) signum((float) processedComb.compareTo(min)) == -1)
                min = processedComb;
        }
        return min.clone();
    }
    Combination CurrentCombination (Card[] Deck, int k){
        return MinCombination(GetAllPossibleCombinations(Deck, null, null, null, DO_NOT_USE_HANDS, k));
    }
    double[] GetWinProbability (Card[] Deck, boolean useHands, int k) {
        try {
            log.append = " игрока";
            return GetWinProbability(
                    GetAllPossibleCombinations(
                            Deck,
                            null,
                            null,
                            null,
                            DO_NOT_USE_HANDS,
                            k
                    ),
                    GetAllPossibleCombinations(
                            Arrays.copyOf(
                                    Arrays
                                            .stream(Deck)
                                            .limit(deckSize - k)
                                            .filter(Objects::nonNull)
                                            .filter(card -> card.getPlace() == 1)
                                            .map(Card::clone)
                                            .toArray(Card[]::new),
                                    7),
                            null,
                            null,
                            Arrays
                                    .stream(Deck)
                                    .limit(deckSize - k)
                                    .filter(Objects::nonNull)
                                    .filter(card -> card.getPlace() == 0)
                                    .mapToInt(Card::getValue)
                                    .boxed()
                                    .peek(ign -> log.append = " оппонента")
                                    .collect(Collectors.toCollection(HashSet::new)),
                            useHands,
                            k + 2
                    )
            );
        } finally {
            log.append = "";
        }
    }
    double[] GetWinProbabilitySimplified (Card[] Deck, boolean useHands, Function<LinkedList<Combination>, double[]> Simplifying, int k) {
        try {
            log.append = " игрока";
            return GetWinProbability(
                    Simplifying.apply(
                            GetAllPossibleCombinations(
                                    Deck,
                                    null,
                                    null,
                                    null,
                                    DO_NOT_USE_HANDS,
                                    k
                            )
                    ),
                    Simplifying.apply(
                            GetAllPossibleCombinations(
                                    Arrays.copyOf(
                                            Arrays
                                                    .stream(Deck)
                                                    .limit(deckSize - k)
                                                    .filter(Objects::nonNull)
                                                    .filter(card -> card.getPlace() == 1)
                                                    .map(Card::clone)
                                                    .toArray(Card[]::new),
                                            7),
                                    null,
                                    null,
                                    Arrays
                                            .stream(Deck)
                                            .limit(deckSize - k)
                                            .filter(Objects::nonNull)
                                            .filter(card -> card.getPlace() == 0)
                                            .mapToInt(Card::getValue)
                                            .boxed()
                                            .peek(ign -> log.append = " оппонента")
                                            .collect(Collectors.toCollection(HashSet::new)),
                                    useHands,
                                    k + 2
                            )
                    )
            );
        } finally {
            log.append = "";
        }
    }
    double[] GetOpponentProbability (Card[] Deck, int k) {
        return
            GetProbability(
                Arrays.copyOf(
                        Arrays
                                .stream(Deck)
                                .limit(deckSize - k)
                                .filter(Objects::nonNull)
                                .filter(card -> card.getPlace() == 1)
                                .map(Card::clone)
                                .toArray(Card[]::new),
                        7),
                null,
                null,
                Arrays
                        .stream(Deck)
                        .limit(deckSize - k)
                        .filter(Objects::nonNull)
                        .filter(card -> card.getPlace() == 0)
                        .mapToInt(Card::getValue)
                        .boxed()
                        .collect(Collectors.toCollection(HashSet::new)),
                k + 2
            );
    }
    LinkedList<Combination> GetAllOpponentCombinations (Card[] Deck, boolean useHands, int k) {
        return GetAllPossibleCombinations(
                Arrays.copyOf(
                        Arrays
                                .stream(Deck)
                                .limit(deckSize - k)
                                .filter(Objects::nonNull)
                                .filter(card -> card.getPlace() == 1)
                                .map(Card::clone)
                                .toArray(Card[]::new),
                        7),
                null,
                null,
                Arrays
                        .stream(Deck)
                        .limit(deckSize - k)
                        .filter(Objects::nonNull)
                        .filter(card -> card.getPlace() == 0)
                        .mapToInt(Card::getValue)
                        .boxed()
                        .collect(Collectors.toCollection(HashSet::new)),
                useHands,
                k + 2
        );
    }
}
