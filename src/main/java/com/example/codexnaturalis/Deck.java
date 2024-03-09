package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;

public class Deck {
    private ArrayList<ResourceCard> RCards = new ArrayList<ResourceCard>();
    private ArrayList<GoldCard> GCards = new ArrayList<GoldCard>();

    public Deck(ArrayList<ResourceCard> RCards, ArrayList<GoldCard> GCards){
        this.RCards = RCards;
        this.GCards = GCards;
    }

    public static void throwTooManyCardsException() throws TooManyCardsException {
        throw new TooManyCardsException("Troppe carte nel mazzo");
    }
    public void addToDeck(){
        try {
            if(this.RCards.size() + this.GCards.size() != 3) throwTooManyCardsException();

        } catch (TooManyCardsException e) {
            System.out.println("Errore: " + e.getMessage());
        }


    }
    public void printCards(){
        for (int i : this.getCards()) {
            System.out.println(i);
        }
    }
    public int[] getCards(){
        int[] deck = new int[3];
        int z = RCards.size();
       for (int i=0;i<z;i++){
           deck[i] = RCards.get(i).getCardID();
       }
       for (int i=0; i<GCards.size();i++){
           deck[z+i] = GCards.get(i).getCardID();
           }
       return deck;
    }
}

class TooManyCardsException extends Exception {
    public TooManyCardsException(String message) {
        super(message);
    }
}

