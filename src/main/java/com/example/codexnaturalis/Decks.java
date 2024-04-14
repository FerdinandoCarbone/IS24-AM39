package com.example.codexnaturalis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

class DrawingDeck {
    private static ArrayList<ResourceCard> totalResourceCard= new ArrayList<>();
    private static ArrayList<GoldCard> totalGoldCard = new ArrayList<>();
    private static ArrayList<ObjectiveCard> totalObjectiveCards = new ArrayList<>();
    private static ArrayList<StarterCard> totalStartingCards = new ArrayList<>();
    private static boolean decksAreGenerated = false;

    public static void generateDecks() throws IOException {
        DrawingDeck.totalGoldCard = (ArrayList<GoldCard>)GoldCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/GoldCardDB.json");
        DrawingDeck.totalResourceCard = (ArrayList<ResourceCard>)ResourceCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/ResourceCardDB.json");
        DrawingDeck.totalStartingCards = (ArrayList<StarterCard>)StarterCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/StarterCardDB.json");
        DrawingDeck.totalObjectiveCards = (ArrayList<ObjectiveCard>)ObjectiveCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/ObjectiveCardDB.json");
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
    public static PlayerDeck generatePlayerDeck() throws IOException {
        if (!decksAreGenerated) {
            generateDecks();
            decksAreGenerated = true;
        }
        Random rand = new Random();
        int z;
        ArrayList<ResourceGoldCard> deckGen = new ArrayList<>();
        StarterCard starterGen;
        ObjectiveCard secretObjectiveGen;
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
        totalStartingCards.remove(z);
        z = rand.nextInt(totalObjectiveCards.size());
        secretObjectiveGen = totalObjectiveCards.get(z);
        totalObjectiveCards.remove(z);
        return new PlayerDeck(deckGen, starterGen, secretObjectiveGen);
    }

    /**
     * Checks whether the chosen deck still has cards
     * @param deckChoice: deck chosen from the player, 1 for Resource Deck, 2 for Gold deck
     * @return boolean, true if the deck is Empty, otherwise false
     */
    public static boolean checkDeckEmptiness(int deckChoice) {
        boolean isMazzoVuoto = false;

        if (deckChoice == 1 && totalResourceCard.isEmpty()) {
            System.out.println("ERRORE: MAZZO RISORSA VUOTO");
            isMazzoVuoto = true;
        } else if (deckChoice == 2 && totalGoldCard.isEmpty()) {
            System.out.println("ERRORE: MAZZO ORO VUOTO");
            isMazzoVuoto = true;
        }

        return isMazzoVuoto;
    }

    public static ArrayList<ResourceCard> getTotalResourceCard() {
        return totalResourceCard;
    }

    public static ArrayList<GoldCard> getTotalGoldCard() {
        return totalGoldCard;
    }

    public static ArrayList<ObjectiveCard> getTotalObjectiveCards() {
        return totalObjectiveCards;
    }

    public static ArrayList<StarterCard> getTotalStartingCards() {
        return totalStartingCards;
    }
}
    class PlayerDeck {
        /**
         *  cards: ArrayList of the cards the player has except the Secret Card and the Starter Card
         */
        private ArrayList<ResourceGoldCard> resourceGoldCards;
        /**
         *  starterCard: starter Card of the player
         */
        private StarterCard starterCard;
        /**
         * secretObjectiveCard: secret Objective Card of the player
         */
        private ObjectiveCard secretObjectiveCard;

        public PlayerDeck(ArrayList<ResourceGoldCard> resourceGoldCards, StarterCard starterCard, ObjectiveCard secretObjectiveCard) {

            this.resourceGoldCards = resourceGoldCards;
            this.starterCard = starterCard;
            this.secretObjectiveCard = secretObjectiveCard;
        }

        public void printResourceGoldCards(PrintWriter out) {
            for (int i = 1; i <= getResourceGoldCards().size(); i++) {
                out.println(i + ") " + getResourceGoldCards().get(i-1).getClass());
            }

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
        public ArrayList<ResourceGoldCard> getResourceGoldCards() {
            return resourceGoldCards;
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
