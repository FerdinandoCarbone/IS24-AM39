package com.example.codexnaturalis;

import java.io.IOException;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) throws IOException {
        String[] test = new String[2];
        Dealer dealer = new Dealer();
        Player player1 = new Player("Pippo",0,dealer.shuffleAndCreateStartingDeck());
        /*ArrayList<GameCard> deckCards = new ArrayList<GameCard>();
        ResourceCard rcard1 = new ResourceCard(10,test, 0,Seed.Blue);
        ResourceCard rcard2 = new ResourceCard(15,test,0,Seed.Green);
        //GoldCard gcard1 = new GoldCard(29,test,2,Seed.Green);
        deckCards.add(rcard1);
        deckCards.add(rcard2);
        deckCards.add(gcard1);
        Deck deckPlayer1 = new Deck(deckCards);
        deckPlayer1.printAllDeck();
        System.out.println("\nNEW SET:");
        deckPlayer1.printAllDeck();*/
        //dealer.printTotalCards();
        player1.getDeck().printAllDeck();
        //player1.getDeck().getCardinDeck(2).
    }
}

