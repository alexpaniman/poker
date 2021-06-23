package org.poker.probability;

import java.util.Arrays;

public class CombinationProbability implements Probability{
    private double[] probability = new double[10];

    public CombinationProbability(double... args) {
        if (args.length != 10)
            throw new IllegalArgumentException("IllegalNumberOfArguments : "+args.length);
        System.arraycopy(args, 0, probability, 0, 10);
    }

    @Override
    public double[] getAll() {
        double[] result = new double[10];
        System.arraycopy(probability, 0, result, 0, 10);
        return result;
    }

    @Override
    public double getCurrent(int Index) {
        if (Index > 9 || Index < 0)
            throw new IndexOutOfBoundsException("Index : "+Index+". Size : "+10+".");
        return probability[Index];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean maxProbability = Arrays
                .stream(probability)
                .anyMatch(el -> el == 1);
        for (int j = 0; j < 2; j++){
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
            builder.append((maxProbability)? ((probability[j]==1)? "100" : "0,0") : String.format("%1$018.15f", probability[j]*100));
            builder.append("%\n");
        }
        return builder.toString();
    }
}
