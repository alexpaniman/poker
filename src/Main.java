import org.poker.analyzers.Analyzer;
import org.poker.analyzers.GetWinProbabilityMonteCarlo;
import org.poker.core.Card;
import org.poker.probability.Probability;
import org.poker.probability.WinProbability;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        Card[] Deck /**/;/*/= {
                new Card(8,1),
                new Card(51,1),
                new Card(6,1),
                new Card(19,1),
                new Card(34,1),
                new Card(17,0),
                new Card(4,0),
        };/**/
        Scanner in = new Scanner(System.in);
        /*for (int i = 0; i<5; i++)
              Deck[i] = new Card(in.nextInt()-2+(in.nextInt()-1)*13, in.nextInt()-1);*/
        Deck = readDeck(in);
        if (Deck.length != 7) Deck = Arrays.copyOf(Deck, 7);
        for (int i = 0; i < Deck.length; i++) {
            Card card = Deck[i];
            System.out.println(i+".\t" + card);
        }
        Analyzer analyzer;
        analyzer = GetWinProbabilityMonteCarlo.newBuilder()
                .setK(2)
                .setSeed(100000000000L)
                .setOpponents(2)
                .generalization(false)
                .setPercentageOfTotalSearch(0.000001)
                .build();
        Probability p = analyzer.analyze(Deck);
        System.out.println(p);
        analyzer = GetWinProbabilityMonteCarlo.newBuilder()
                .setK(2)
                .setSeed(100000000000L)
                .setOpponents(2)
                .generalization(true)
                .setPercentageOfTotalSearch(0.0001)
                .build();
        p = analyzer.analyze(Deck);
        System.out.println((WinProbability) p);
        //for (int i = 0; i < 10; i++) {
         //   Analyzer a = new GetWinProbabilityMonteCarlo(3, 1*Math.pow(10, -i), System.currentTimeMillis(), 1);
         //   System.out.println(a.analyze(Deck));
       // }
        //Logger log = new Logger(System.out, Logger.STANDARD_DATE_FORMAT);
        //log.setAll(false);
        //log.INFO = true;
        //log.setLogPower(1);
        //PokerAnalyzer analyzer = new PokerAnalyzer(log);
        //PokerAnalyzerAPI.api(PokerAnalyzerAPI.Type.a);
        //analyzer.GetWinProbability(Deck, false, 2);
        //System.out.println(analyzer.AnalyzeCombination(Deck));
        //System.out.println(analyzer.CurrentCombination(Deck, 1));
        //System.out.println(WinProbabilityInfo(analyzer.GetWinProbability(Deck, PokerAnalyzer.USE_HANDS, 1)));
        //System.out.println(WinProbabilityInfo(analyzer.GetWinProbability(Deck, 1, 3, null, null)));/*WinProbabilityInfo(analyzer.GetWinProbability(new double[]{1, 0, 0}, new double[]{0, 0.1, 0.9})));*/
        //*System.out.println("\n"+/*ProbabilityInfo(*/analyzer.GetAllPossibleCombinations(Deck, null, null, null/*/ Stream.of(1, 2, 8, 25, 37, 6).collect(Collectors.toCollection(HashSet::new))/**/, false, 2);
        //System.out.println(WinProbabilityInfo(analyzer.GetWinProbabilityMonteCarlo(Deck, 1, 1, 5)));
    }
    static String WinProbabilityInfo(double[] winProbability){
        StringBuilder builder = new StringBuilder();
        boolean maxProbability = Arrays
                .stream(winProbability)
                .anyMatch(el -> el == 1);
        for (int i = winProbability.length - 1; i >= 0; i--) {
            switch (i){
                case 0:
                    builder.append("Поражение");
                    break;
                case 1:
                    builder.append("Ничья    ");
                    break;
                case 2:
                    builder.append("Победа   ");
                    break;
            }
            builder.append(" - ");
            builder.append((maxProbability)? ((winProbability[i]==1)? "100" : "0,0") : String.format("%1$018.15f", winProbability[i]*100));
            builder.append("%\n");
        }
        return builder.toString();
    }
    static String ProbabilityInfo(double[] Probability){
        StringBuilder builder = new StringBuilder();
        boolean maxProbability = Arrays.stream(Probability).anyMatch(el -> el == 1);
        for (int j = 0; j < Probability.length; j++){
            switch(j){
                case 0:
                    builder.append("СтаршаяКарта ");
                    break;
                case 1:
                    builder.append("Пара         ");
                    break;
                case 2:
                    builder.append("ДвеПары      ");
                    break;
                case 3:
                    builder.append("Сет          ");
                    break;
                case 4:
                    builder.append("Стрит        ");
                    break;
                case 5:
                    builder.append("Флеш         ");
                    break;
                case 6:
                    builder.append("ФуллХаус     ");
                    break;
                case 7:
                    builder.append("Каре         ");
                    break;
                case 8:
                    builder.append("СтритФлеш    ");
                    break;
                case 9:
                    builder.append("СтритРоялФлеш");
                    break;
            }
            builder.append(" - ");
            builder.append((maxProbability)? ((Probability[j]==1)? "100" : "0,0") : String.format("%1$018.15f", Probability[j]*100));
            builder.append("%\n");
        }
        return builder.toString();
    }
    static Card[] readDeck (Scanner in) {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<Integer> resultLines = new ArrayList<>();
        while (in.hasNext()) {
            String s = in.next();
            if (s.matches("(.*[Ee][Nn][Dd].*)|(.*[Кк][Оо][Нн][Ее][Цц].*)|([~+|\\-%&$#*^@!\\\\/)(?\\[\\]=_\"\',<>.]+)|(~-+~)")) break;
            lines.add(s);
        }
        if (resultLines.size() % 3 != 0) throw new IllegalArgumentException("WrongNumberOfParameters");
        Pattern pattern;
        Matcher matcher;
        int counter = 1;
        for (String line : lines) {
            if (counter == 1) {
                pattern = Pattern.compile("[AaТт].*");
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    resultLines.add(12);
                    counter++;
                    continue;
                }
                pattern = Pattern.compile("[QqДд].*");
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    resultLines.add(10);
                    counter++;
                    continue;
                }
                pattern = Pattern.compile("[KkКк].*");
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    resultLines.add(11);
                    counter++;
                    continue;
                }
                pattern = Pattern.compile("[JjВв].*");
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    resultLines.add(9);
                    counter++;
                    continue;
                }
                pattern = Pattern.compile("10.*");
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    resultLines.add(8);
                    counter++;
                    continue;
                }
                if (isDigit(String.valueOf(line.charAt(0)))) {
                    int value = Integer.valueOf(String.valueOf(line.charAt(0))) - 2;
                    resultLines.add(value);
                    counter++;
                    continue;
                }
                counter++;
                continue;
            }
            if (counter == 2) {
                pattern = Pattern.compile("[ПпSs].*");
                matcher = pattern.matcher(line);
                if (matcher.matches())
                    resultLines.add(1);
                pattern = Pattern.compile("[ЧчHh].*");
                matcher = pattern.matcher(line);
                if (matcher.matches())
                    resultLines.add(2);
                pattern = Pattern.compile("[DdБб].*");
                matcher = pattern.matcher(line);
                if (matcher.matches())
                    resultLines.add(3);
                pattern = Pattern.compile("[CcТт].*");
                matcher = pattern.matcher(line);
                if (matcher.matches())
                    resultLines.add(0);
                counter++;
                continue;
            }
            if (counter == 3) {
                pattern = Pattern.compile(".*[HhРр].*");
                matcher = pattern.matcher(line);
                if (matcher.matches())
                    resultLines.add(0);
                pattern = Pattern.compile(".*[TtСс].*");
                matcher = pattern.matcher(line);
                if (matcher.matches())
                    resultLines.add(1);
                counter++;
                if (counter >= 3)
                    counter = 1;
            }
        }
        Card[] arr = new Card[resultLines.size()/3];
        int index = 0;
        for (int i = 0; i < resultLines.size(); i+=3) {
            int rank = resultLines.get(i);
            int suit = resultLines.get(i+1);
            int place = resultLines.get(i+2);
            arr[index++] = new Card(rank + suit * 13, place);
        }
        return arr;
    }
    private static boolean isDigit(String s){
        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
