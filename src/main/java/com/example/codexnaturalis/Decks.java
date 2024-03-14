package com.example.codexnaturalis;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class Decks {

    //private CardDatabaseLoader cardLoader;
    private ArrayList<Card> cards = new ArrayList<>();

    public void printDeck() {
        /**
         * Print the deck
         */
    }
    public ArrayList<Card> getCards() {
        return cards;
    }
}
class DrawingDeck extends Decks{
    private static ArrayList<ResourceCard> totalResourceCard= new ArrayList<>();
    private static ArrayList<GoldCard> totalGoldCard = new ArrayList<>();
    private ArrayList<ObjectiveCard> totalObjectiveCards = new ArrayList<>();
    private static ArrayList<StarterCard> totalStartingCards = new ArrayList<>();

    public static ArrayList<StarterCard> getTotalStartingCards() {
        return totalStartingCards;
    }

    public static void shuffle() throws IOException {
        DrawingDeck.totalGoldCard = (ArrayList<GoldCard>)GoldCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/GoldCardDB.json");
        DrawingDeck.totalResourceCard = (ArrayList<ResourceCard>)ResourceCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/ResourceCardDB.json");
        DrawingDeck.totalStartingCards = (ArrayList<StarterCard>)StarterCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/StarterCardDB.json");
        //this.totalObjectiveCards = ;
        //this.totalStartingCards = ;
    }

    /**
     * Method called from the player who draws a card
     * @param cardType: defines the card that will be drawn, true for resource, false for gold
     * @return ResourceGoldCard, card that will be added to the playerDeck and removed from its deck
     */
    public static ResourceGoldCard drawCard(boolean cardType){
        Random rand = new Random();
        int z;
        ResourceGoldCard drewCard;
        if(cardType){
            z=rand.nextInt(totalResourceCard.size());
            drewCard= totalResourceCard.get(z);
            totalResourceCard.remove(z);
        }
        else {
            z=rand.nextInt(totalGoldCard.size());
            drewCard= totalGoldCard.get(z);
            totalGoldCard.remove(z);
        }
        return drewCard;
    }

    /**
     * The method generates the player deck by randomly choosing 2 cards
     * @return PlayerDeck
     */
    public static PlayerDeck generatePlayerDeck() {
        try {
            shuffle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Random rand = new Random();
        int z;
        ArrayList<ResourceGoldCard> deckGen = new ArrayList<>();
        StarterCard starterGen;
        for(int i=0;i<2;i++){
            z = rand.nextInt(totalResourceCard.size());
            deckGen.add(totalResourceCard.get(z));
            totalResourceCard.remove(z);
        }
        z = rand.nextInt(totalGoldCard.size());
        deckGen.add(totalGoldCard.get(z));
        totalGoldCard.remove(z);
        z = rand.nextInt(totalStartingCards.size());
        starterGen = totalStartingCards.get(z);
        return new PlayerDeck(deckGen, starterGen);
    }

}
    class PlayerDeck {
        /**
         *  cards: ArrayList of the cards the player has except the Secret Card and the Starter Card
         */
        private ArrayList<ResourceGoldCard> playerCards;
        /**
         *  starterCard: starter Card of the player
         */
        private StarterCard starterCard;
        /**
         * secretObjectiveCard: secret Objective Card of the player
         */
        private ObjectiveCard secretObjectiveCard;

        public PlayerDeck(ArrayList<ResourceGoldCard> playerCards, StarterCard starterCard) {
            this.playerCards = playerCards;
            this.starterCard = starterCard;
        }
        /**
         * Setter of starterCard
         * @param starterCard: starter Card of the player
         */
        public void setStarterCard(StarterCard starterCard) {
            this.starterCard = starterCard;
        }

        /**
         * Setter of secretObjectiveCard
         * @param secretObjectiveCard: secret Objective Card of the player
         */
        public void setSecretObjectiveCard(ObjectiveCard secretObjectiveCard) {
            this.secretObjectiveCard = secretObjectiveCard;
        }

        /**
         * Getter of cards
         * @return ArrayList of the cards the player has except the Secret Card and the Starter Card
         */
        public ArrayList<ResourceGoldCard> getPlayerCards() {
            return playerCards;
        }

        /**
         * Getter of the starter Card
         * @return starter Card of the player
         */
        public StarterCard getStarterCard() {
            return starterCard;
        }

        /**
         * Getter of
         * @return secret Objective Card of the player
         */
        public ObjectiveCard getSecretObjectiveCard() {
            return secretObjectiveCard;
        }
    }
