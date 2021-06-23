package org.poker.simulator;

import org.poker.core.Card;
import org.poker.core.Combination;
import org.poker.core.PokerCore;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NewSimulator extends PokerCore {
    private int smallBlind = 4;
    private int iteration = 0;
    private Card[][] hands;
    private Card[] table;
    private int numberOfPlayers;
    private int upSRate;
    private int bank = 0;
    private int[] playersMoney;
    private int[] playersRates;
    private int[] maxWin;
    private int dealer = 0;
    private int currentPlayer = 0;
    private Collection<Integer> folded = new HashSet<>();
    private int bigBlind = 8;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        NewSimulator simulator = new NewSimulator(2);
        while (true) {
            int counter = 0;
            for (int i = 0; i < simulator.numberOfPlayers; i++) {
                System.out.println("\tPlayer "+i+" deck : ");
                for (Card card : simulator.getPlayerCards(i))
                    System.out.println("\t\t"+card);
                System.out.println("His stack -> "+simulator.playersMoney[i]+"$");
            }
            while (counter < 2){
                boolean success = false;
                System.out.print("Player "+simulator.getCurrentPlayer()+" : ");
                String str = scan.nextLine();
                if (str.matches("bet \\d+"))
                    success = simulator.setRate(Rate.BET, Integer.parseInt(str.substring(4, str.length())));
                if (str.matches("raise \\d+"))
                    success = simulator.setRate(Rate.RAISE, Integer.parseInt(str.substring(6, str.length())));
                if (str.matches("reraise \\d+"))
                    success = simulator.setRate(Rate.RERAISE, Integer.parseInt(str.substring(8, str.length())));
                if (str.matches("coll.*"))
                    success = simulator.setRate(Rate.COLL, 0);
                if (str.matches("fold.*"))
                    success = simulator.setRate(Rate.FOLD, 0);
                if (str.matches("check.*"))
                    success = simulator.setRate(Rate.CHECK, 0);
                System.out.println(" -> "+(success ? "successfully" : "unsuccessfully"));
                counter += success ? 1 : 0;
            }
            simulator.next();
        }
    }

    NewSimulator(int numberOfPlayers) {
        this.table = new Card[5];
        this.playersRates = new int[numberOfPlayers];
        this.numberOfPlayers = numberOfPlayers;
        this.playersMoney = new int[]{300, 800};
        this.hands = new Card[numberOfPlayers][2];
        this.maxWin = new int[numberOfPlayers];
        newSimulation();
    }

    void setPlayersMoney(int[] arr){
        for (int i = 0; i < playersMoney.length; i++) {
            playersMoney[i] = arr[i];
            maxWin[i] = arr[i] * numberOfPlayers;
        }
    }

    void newSimulation() {
        List<Integer> usingStack =
                IntStream
                    .range(0, 52)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
        Random random = new Random();
        int n = usingStack.size();
        for (int i = 0; i < numberOfPlayers; i++)
            for (int j = 0; j < 2; j++) {
                Integer integer = usingStack.remove(random.nextInt(n--));
                hands[i][j] = new Card(integer, 0);
                usingStack.add(integer);
            }
        for (int i = 0; i < 5; i++) {
            Integer integer = usingStack.remove(random.nextInt(n--));
            table[i] = new Card(integer, 1);
            usingStack.add(integer);
        }
        currentPlayer = dealer + 1 >= numberOfPlayers ? 0 : dealer + 1;
        IntStream
                .range(0, numberOfPlayers)
                .filter(pl -> playersMoney[pl] == 0)
                .boxed()
                .forEach(i -> folded.add(i));
        for (int i = 0, pl = dealer + 1 >= numberOfPlayers ? 0 : dealer + 1; i < 2; i++, pl = pl + 1 >= numberOfPlayers ? 0 : pl + 1){
            int blind = i == 0 ? smallBlind : bigBlind;
            playersMoney[pl] -= blind;
            bank += blind;
            playersRates[pl] += blind;
        }
    }

    Card[] getPlayerCards(int player) {
        return Stream
                .concat(
                        Stream
                            .of(
                                    hands[player][0],
                                    hands[player][1]
                            ),
                        Arrays
                            .stream(table)
                            .limit(
                                    iteration == 0 ?
                                            0 : (iteration == 1 ?
                                                3 : (iteration == 2 ?
                                                    5 : (iteration == 3 ?
                                                        6 : 7
                                            )
                                        )
                                    )
                            )
                )
                .filter(Objects::nonNull)
                .toArray(Card[]::new);
    }

    void coll(){
        int rateCurr =
                Arrays
                        .stream(playersRates)
                        .max()
                        .orElse(0)
                        -
                        playersRates[currentPlayer];
        int realRate = Math.min(playersMoney[currentPlayer], rateCurr);
        playersMoney[currentPlayer] -= realRate;
        playersRates[currentPlayer] += realRate;
        bank += realRate;
        currentPlayer++;
    }

    public void fold() {
        folded.add(currentPlayer++);
    }

    public void check(){
        currentPlayer++;
    }

    public boolean setPotRate(Rate rate){
        return setRate(rate, bank);
    }

    public boolean setAllInRate(Rate rate){
        return setRate(rate, playersMoney[currentPlayer]);
    }

    public int getCurrentPlayer() {
        if (currentPlayer >= numberOfPlayers)
            currentPlayer = 0;
        return currentPlayer;
    }

    public void bet(int value) {
        bank += value;
        upSRate++;
        playersMoney[currentPlayer] -= value;
        playersRates[currentPlayer++] += value;
    }

    public void raise(int value) {
        coll();
        currentPlayer--;
        bet(value);
    }

    public void reraise(int value) {
        raise(value);
    }

    public boolean canBet(int value){
        if (!canFold())
            return false;
        if (upSRate == 0){
            return value <= playersMoney[currentPlayer];
        } else
            return false;
    }

    public boolean canRaise(int value){
        if (!canFold())
            return false;
        if (upSRate == 1){
            return value <= playersMoney[currentPlayer];
        } else
            return false;
    }

    public boolean canReraise(int value){
        if (!canFold())
            return false;
        if (upSRate == 2){
            return value <= playersMoney[currentPlayer];
        } else
            return false;
    }

    public boolean canColl(){
        return upSRate > 0;
    }

    public boolean canCheck(){
        if (!canFold())
            return false;
        IntSummaryStatistics stat = IntStream
                .range(0, playersRates.length)
                .filter(pl -> !folded.contains(pl))
                .summaryStatistics();
        return stat.getMax() == stat.getMin();
    }

    public boolean canFold(){
        return !folded.contains(currentPlayer);
    }

    public boolean setRate(Rate rate, int value) {
        if (currentPlayer >= numberOfPlayers)
            currentPlayer = 0;
        if (folded.contains(currentPlayer) || playersMoney[currentPlayer] == 0){
            currentPlayer++;
            return true;
        }
        switch(rate){
            case RERAISE:
                if (canReraise(value))
                    reraise(value);
                else
                    return false;
                break;
            case RAISE:
                if (canRaise(value))
                    raise(value);
                else
                    return false;
                break;
            case BET:
                if (canBet(value))
                    bet(value);
                else
                    return false;
                break;
            case COLL:
                if (canColl())
                    coll();
                else
                    return false;
                break;
            case FOLD:
                if (canFold())
                    fold();
                else
                    return false;
                break;
            case CHECK:
                if (canCheck())
                    check();
                else
                    return false;
                break;
        }
        return true;
    }

    public void next() {
        iteration++;
        upSRate = 0;
        currentPlayer = dealer + 1 >= numberOfPlayers ? 0 : dealer + 1;
        if (iteration > 4){
            Map<Integer, Combination> combinationMap = new HashMap<>();
            for (int i = 0; i < numberOfPlayers; i++)
                combinationMap.put(i, AnalyzeCombination(getPlayerCards(i)));
            combinationMap = combinationMap
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
            combinationMap
                    .keySet()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .takeWhile(key -> bank > 0)
                    .forEach(key -> {
                        int win = Math.min(maxWin[key], bank);
                        playersMoney[key] += win;
                        bank -= win;
                    });
            iteration = 0;
            IntStream
                    .range(0, playersRates.length)
                    .forEach(i -> playersRates[i] = 0);
            dealer++;
            if (dealer >= numberOfPlayers)
                dealer = 0;
            currentPlayer = dealer + 1 >= numberOfPlayers ? 0 : dealer + 1;
            newSimulation();
            IntStream
                    .range(0, numberOfPlayers)
                    .filter(pl -> playersMoney[pl] == 0)
                    .boxed()
                    .forEach(i -> folded.add(i));
            for (int i = 0, pl = dealer + 1 >= numberOfPlayers ? 0 : dealer + 1; i < 1; i++, pl = pl + 1 >= numberOfPlayers ? 0 : pl + 1){
                int blind = i == 0 ? smallBlind : bigBlind;
                playersMoney[pl] -= blind;
                bank += blind;
                playersRates[pl] += blind;
            }
        }
    }
}
