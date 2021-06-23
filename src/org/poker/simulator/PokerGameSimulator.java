package org.poker.simulator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.poker.core.Card;
import org.poker.simulator.interfaces.*;

public class PokerGameSimulator {

    private Card[] fullSimulation;

    final long ante;

    long[] playersStack;

    Collection<Integer> folded = new HashSet<>();

    private long seed;

    private final int players;

    int dealer;

    int smallBlind;

    int bigBlind;

    private final long RANDOM = -2L;

    private final long CURRENT_MILLIS = -1L;

    public static void main(String[] args) {
        int players = 5;
        Scanner scan = new Scanner(System.in);
        scan.next();
        PokerGameSimulator simulator = new PokerGameSimulator(players, 0, new long[] {300, 100, 800, 289, 1000}, 0, -2L, 8, 4);
        while (true) {
            SimulationIterator iter = simulator
                    .simulate();
            Simulation s;
            while ((s = iter.next()) != null) {
                for (int plr = 0; plr < players; plr++) {
                    System.out.println("Player : " + plr + ". Deck -> ");
                    for (Card card : s.getPlayerCards(plr))
                        System.out.println("\t\t\t"+card);
                    System.out.println("\t\tStack -> "+s.simulator.playersStack[plr]+"\n");
                }
                RateManagement manage = s.rateManage();
                org.poker.simulator.interfaces.Rate r;
                int pl = 0;
                while (true) {
                    r = manage.next();
                    if (r == null)
                        break;
                    System.out.print(Arrays.toString(r.possibleBets()));
                    System.out.print("Player -> " + pl);
                    String str = scan.next();
                    if (r instanceof Nullable) {
                        System.out.println("Player : " + pl + " -> Nullable");
                        pl++;
                        continue;
                    }
                    if (str.matches("reraise.+"))
                        ((Reraiseable) r).reraise(Integer.parseInt(str.substring(7, str.length() - 1)));
                    if (str.matches("raise.+"))
                        ((Raiseable) r).raise(Integer.parseInt(str.substring(5, str.length() - 1)));
                    if (str.matches("bet.+"))
                        ((Betable) r).bet(Integer.parseInt(str.substring(3, str.length() - 1)));
                    if (str.matches("coll"))
                        ((Collable) r).coll();
                    if (str.matches("fold"))
                        ((Foldable) r).fold();
                    if (str.matches("check"))
                        ((Checkable) r).check();
                    pl++;
                }
            }
        }
    }

    Card[] getSimulation(){
        return fullSimulation;
    }

    int getPlayers() {
        return players;
    }

    PokerGameSimulator(int numberOfPlayers, long ante, long[] playersStack, int dealer, long seed, int  bigBlind, int smallBlind) {
        if (numberOfPlayers < 0 || numberOfPlayers > 23)
            throw new IllegalArgumentException("IllegalNumberOfOpponents : "+numberOfPlayers);
        if (seed < -2)
            throw new IllegalArgumentException("IllegalSeed : "+seed);
        if (ante < 0)
            throw new IllegalArgumentException("AnteCannotBeNegative");
        if (dealer > numberOfPlayers || dealer < 0)
            throw new IllegalArgumentException("IllegalDealer : "+dealer);
        if (playersStack == null)
            throw new NullPointerException("PlayersStackCannotBeNull");
        if (playersStack.length != numberOfPlayers)
            throw new IllegalArgumentException("IllegalPlayersStack : "+Arrays.toString(playersStack));
        if (smallBlind < 0 || bigBlind < 0)
            throw new IllegalArgumentException("BlindCannotBeNegative");
        this.playersStack = playersStack;
        this.ante = ante;
        this.dealer = dealer;
        this.players = numberOfPlayers;
        this.seed = seed;
        this.bigBlind = bigBlind;
        this.smallBlind = smallBlind;
    }

    public SimulationIterator simulate() {
        dealer ++;
        if (dealer >= players)
            dealer = 0;
        Card[] simulation = new Card[players*2+5];
        Random random = (seed == RANDOM)? new Random() : ((seed == CURRENT_MILLIS)? new Random(System.currentTimeMillis()) : new Random(seed));
        List<Integer> Deck = IntStream
                .range(0, 52)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < 52; i++)
            Deck.add(random.nextInt(52), Deck.remove(i));
        for (int i = 0; i < players*2; i++)
            simulation[i] = new Card(Deck.get(i), 0);
        for (int i = players*2; i < players*2+5; i++)
            simulation[i] = new Card(Deck.get(i), 1);
        fullSimulation = simulation;
        return new SimulationIterator(this);
    }

    private class IllegalPokerAction extends Throwable {
        IllegalPokerAction(String m) {
            super(m);
        }
    }
}
