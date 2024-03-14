/*package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;

public class Deck {
    private ArrayList<GameCard> deckCards = new ArrayList<GameCard>();

    public Deck(ArrayList<GameCard> deckCards){
        this.deckCards = deckCards;
    }
    public static void throwTooManyCardsException() throws TooManyCardsException {
        throw new TooManyCardsException("Troppe carte nel mazzo");
    }
    public void addToDeck(){
        try {
            if(this.deckCards.size() >= 3) throwTooManyCardsException();

        } catch (TooManyCardsException e) {
            System.out.println("Errore: " + e.getMessage());
        }


    }
    public void printAllDeck(){
        for (GameCard i: this.deckCards) {
            System.out.println(i.getClass().toString()+" con ID: "+i.getCardID()+" Faccia del tipo: "+i.getActiveFace());
        }
        System.out.println(this.deckCards.size());
    }
    public GameCard getCardinDeck(int i){
        return this.deckCards.get(i);
    }
    public void printCards(){
        for (int i : this.getIDCardsInDeck()) {
            System.out.println(i);
        }
    }
    public int[] getIDCardsInDeck(){
        int[] deck = new int[3];
       for (int i=0;i<3;i++){
           deck[i] = deckCards.get(i).getCardID();
           System.out.println(i);
       }

       return deck;
    }
}

class TooManyCardsException extends Exception {
    public TooManyCardsException(String message) {
        super(message);
    }
}
*/
