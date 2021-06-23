package org.poker.core;

@SuppressWarnings({"unused","WeakerAccess"})
public class Card implements Comparable<Card>, Cloneable {
    public final static int TABLE = 1;
    public final static int HAND = 0;
    private byte value;
    private byte place;

    public Card(byte value, byte place){
        if (value>51||value<0)
            throw new IllegalArgumentException("UnableToCreateCardFromWrongValue("+value+")");
        if (place>1||place<0)
            throw new IllegalArgumentException("UnableToCreateCardFromWrongPlace("+place+")");
        this.value = value;
        this.place = place;
    }

    public Card(int value, int place){
        this((byte)value, (byte)place);
    }

    public Card(Card anotherCard){
        if (anotherCard==null)
            throw new IllegalArgumentException("UnableToCreateCardFromNull");
        this.value = anotherCard.getValue();
        this.place = anotherCard.getPlace();
    }

    public byte getValue(){
        return this.value;
    }

    public byte getPlace(){
        return this.place;
    }

    public byte getSuit(){
        return (byte)(this.value/13);
    }

    public byte getRank(){
        return (byte)(this.value%13);
    }

    public boolean equals(Card anotherCard){
        return compareTo(anotherCard)==0;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        switch(getRank()){
            case 0:
                build.append("Двойка");
                break;
            case 1:
                build.append("Тройка");
                break;
            case 2:
                build.append("Четвёрка");
                break;
            case 3:
                build.append("Пятёрка");
                break;
            case 4:
                build.append("Шестёрка");
                break;
            case 5:
                build.append("Семёрка");
                break;
            case 6:
                build.append("Восьмёрка");
                break;
            case 7:
                build.append("Девятка");
                break;
            case 8:
                build.append("Десятка");
                break;
            case 9:
                build.append("Валет");
                break;
            case 10:
                build.append("Дама");
                break;
            case 11:
                build.append("Король");
                break;
            case 12:
                build.append("Туз");
                break;
        }
        switch(getSuit()){
            case 0:
                build.append("Треф");
                break;
            case 1:
                build.append("Пик");
                break;
            case 2:
                build.append("Черв");
                break;
            case 3:
                build.append("Бубн");
                break;
        }
        if(  getRank()==12
           ||getRank()==11
           ||getRank()== 9 )
            build.append("овый");
        else
            build.append("овая");
        build.append("(");
        if (getPlace()==1)
            build.append("Стол");
        else
            build.append("Рука");
        build.append(")");
        return build.toString();
    }

    @Override
    public int compareTo(Card anotherCard) {
        return Integer.compare(getRank(), anotherCard.getRank());
    }

    @Override
    public boolean equals(Object anotherObject){
        if (anotherObject instanceof Card)
            return equals((Card) anotherObject);
        else
            return false;
    }

    @Override
    public Card clone(){
        try {
            return (Card) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Card(this);
        }
    }
}


