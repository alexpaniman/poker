package org.poker.probability;

import java.util.Arrays;

public class WinProbability implements Probability{
    private double[] probability = new double[3];

    public WinProbability(double... args) {
        if (args.length != 3)
            throw new IllegalArgumentException("IllegalNumberOfArguments : "+args.length);
        System.arraycopy(args, 0, probability, 0, 3);
    }

    @Override
    public double[] getAll() {
        double[] result = new double[3];
        System.arraycopy(probability, 0, result, 0, 3);
        return result;
    }

    @Override
    public double getCurrent(int Index) {
        if (Index > 2 || Index < 0)
            throw new IndexOutOfBoundsException("Index : "+Index+". Size : "+3+".");
        return probability[Index];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean maxProbability = Arrays
                .stream(probability)
                .anyMatch(el -> el == 1);
        for (int i = 2; i >= 0; i--) {
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
            builder.append((maxProbability)? ((probability[i]==1)? "100" : "0,0") : String.format("%1$018.15f", probability[i]*100));
            builder.append("%\n");
        }
        return builder.toString();
    }
}
