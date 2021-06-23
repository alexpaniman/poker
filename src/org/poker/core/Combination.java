package org.poker.core;

import java.util.LinkedList;

@SuppressWarnings({"unused","WeakerAccess"})
public class Combination implements Comparable<Combination>, Cloneable{
    public static final Combination IMPOSSIBLE_MAX = new Combination((byte) 10, null, null);
    public static final Combination IMPOSSIBLE_MIN = new Combination((byte) -1, null, null);
    final private byte combination;
    final private LinkedList<Card> priority;
    final private LinkedList<Card> kicker;
    public Combination(byte combination, LinkedList<Card> priority, LinkedList<Card> kicker){
        this.combination = combination;
        if (priority!=null) {
            this.priority = new LinkedList<>();
            for (Card card : priority)
                this.priority.add(card.clone());
        } else this.priority = null;
        if (kicker!=null) {
            this.kicker = new LinkedList<>();
            for (Card card : kicker)
                this.kicker.add(card.clone());
        } else this.kicker = null;
    }

    public Combination(Combination anotherCombination){
        if (anotherCombination == null)
            throw new IllegalArgumentException("UnableToCreateCombinationFromNull");
        this.combination = anotherCombination.getCombination();
        this.priority = anotherCombination.getPriority();
        this.kicker = anotherCombination.getKicker();
    }

    public LinkedList<Card> priorityClone(){
        if (priority == null)
            return null;
        LinkedList<Card> PriorityClone = new LinkedList<>();
        for (Card card: priority)
            PriorityClone.add(card.clone());
        return PriorityClone;
    }

    public LinkedList<Card> kickerClone(){
        if (kicker == null)
            return null;
        LinkedList<Card> KickerClone = new LinkedList<>();
        for (Card card: kicker)
            KickerClone.add(card.clone());
        return KickerClone;
    }

    public byte getCombination(){
        return this.combination;
    }

    public LinkedList<Card> getPriority() {
        return priorityClone();
    }

    public LinkedList<Card> getKicker(){
        return kickerClone();
    }

    @Override
    public int compareTo(Combination anotherCombination) {
       if(getCombination() == anotherCombination.getCombination()) {
           if (priority!=null&&priority.size()!=0)
                for (int i = 0; i < priority.size(); i++)
                    if (anotherCombination.priority.size()>i&&(!priority.get(i).equals(anotherCombination.priority.get(i))))
                        return priority.get(i).compareTo(anotherCombination.priority.get(i));
           if (kicker!=null&&kicker.size()!=0)
                for (int i = 0; i < kicker.size(); i++)
                    if (anotherCombination.kicker.size()>i&&(!kicker.get(i).equals(anotherCombination.kicker.get(i))))
                         return kicker.get(i).compareTo(anotherCombination.kicker.get(i));
           return 0;
       }
       else
           return Byte.compare(getCombination(), anotherCombination.getCombination());
    }

    public boolean equals(Combination anotherCombination){
        return compareTo(anotherCombination)==0;
    }

    @Override
    public boolean equals(Object anotherObject){
        if (anotherObject instanceof Combination) {
            return equals((Combination) anotherObject);
        }
        else
            return false;
    }

    @Override
    public Combination clone(){
        try {
            return (Combination) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Combination(this);
        }
    }

    @Override
    public String toString(){
        StringBuilder build = new StringBuilder();
        switch(getCombination()){
            case 0:
                build.append("СтаршаяКарта");
                break;
            case 1:
                build.append("Пара");
                break;
            case 2:
                build.append("ДвеПары");
                break;
            case 3:
                build.append("Сет");
                break;
            case 4:
                build.append("Стрит");
                break;
            case 5:
                build.append("Флеш");
                break;
            case 6:
                build.append("ФуллХаус");
                break;
            case 7:
                build.append("Каре");
                break;
            case 8:
                build.append("СтритФлеш");
                break;
            case 9:
                build.append("СтритРоялФлеш");
                break;
        }
        build.append("\n");
        if (priority == null || priority.size() == 0)
            build.append("\tПриоритет: —\n");
        else {
            build.append("\tПриоритет:\n");
            for (Card card : priority)
                build.append("\t\t".concat(card.toString()).concat("\n"));
        }
        if (kicker == null || kicker.size() == 0) {
            build.append("\tКикер: —\n");
            return build.toString();
        }
        build.append("\tКикер:\n");
        for(Card card: kicker)
            build.append("\t\t".concat(card.toString()).concat("\n"));
        return build.toString();
    }
    public String toStringOnlyComb(){
        StringBuilder build = new StringBuilder();
        switch(getCombination()){
            case 0:
                build.append("СтаршаяКарта");
                break;
            case 1:
                build.append("Пара");
                break;
            case 2:
                build.append("ДвеПары");
                break;
            case 3:
                build.append("Сет");
                break;
            case 4:
                build.append("Стрит");
                break;
            case 5:
                build.append("Флеш");
                break;
            case 6:
                build.append("ФуллХаус");
                break;
            case 7:
                build.append("Каре");
                break;
            case 8:
                build.append("СтритФлеш");
                break;
            case 9:
                build.append("СтритРоялФлеш");
                break;
        }
        return build.toString();
    }
}
