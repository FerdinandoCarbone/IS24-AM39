package com.example.codexnaturalis;

import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        String[] test = new String[2];
        ArrayList<ResourceCard> RCards = new ArrayList<ResourceCard>();
        ArrayList<GoldCard> GCards = new ArrayList<GoldCard>();
        ResourceCard rcard1 = new ResourceCard(10,test, 0);
        ResourceCard rcard2 = new ResourceCard(15,test,0);
        GoldCard gcard1 = new GoldCard(29,test,2);
        RCards.add(rcard1);
        RCards.add(rcard2);
        GCards.add(gcard1);
        Deck deckPlayer1 = new Deck(RCards,GCards);
        deckPlayer1.printCards();
        System.out.println(gcard1.getCardID());
    }
}

