package com.example.codexnaturalis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Dealer {
    private ArrayList<ResourceCard> totalResourceCard = new ArrayList<ResourceCard>();
    private ArrayList<GoldCard> totalGoldCard = new ArrayList<GoldCard>();
    private ArrayList<ObjectiveCard> totalObjectiveCards = new ArrayList<ObjectiveCard>();
    private ArrayList<StartingCard> totalStartingCards = new ArrayList<StartingCard>();
    //private CardDatabaseLoader cardLoader;

    public Dealer() throws IOException {
        //this.cardLoader = new CardDatabaseLoader();
        this.totalGoldCard = (ArrayList<GoldCard>)GoldCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/GoldCardDB.json");
        this.totalResourceCard = (ArrayList<ResourceCard>)ResourceCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/ResourceCardDB.json");
        //this.totalObjectiveCards = ;
        //this.totalStartingCards = ;
    }
    public Deck shuffleAndCreateStartingDeck(){
        Random rand = new Random();
        int z;
        //totalGoldCard.get(0).ge
        ArrayList<GameCard> deckGen = new ArrayList<GameCard>();
        for(int i=0;i<2;i++){
            z = rand.nextInt(totalResourceCard.size());
            deckGen.add(totalResourceCard.get(z));
            totalResourceCard.remove(z);
        }
        z = rand.nextInt(totalGoldCard.size());
        deckGen.add(totalGoldCard.get(z));
        totalGoldCard.remove(z);
        return new Deck(deckGen);
    }
    public void printTotalCards(){
        for (Card card: totalResourceCard) {
            System.out.println(totalResourceCard);

        }

    }
}
