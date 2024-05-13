package com.example.codexnaturalis;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

class DrawingDeck {
    private static ArrayList<ResourceCard> totalResourceCard = new ArrayList<>();
    private static ArrayList<GoldCard> totalGoldCard = new ArrayList<>();
    private static ArrayList<ObjectiveCard> totalObjectiveCards = new ArrayList<>();
    private static ArrayList<ObjectiveCardCombo> totalObjectiveComboCards = new ArrayList<>();
    private static ArrayList<ObjectiveCardResourceSet> totalObjectiveResourceSetCards = new ArrayList<>();
    private static ArrayList<StarterCard> totalStartingCards = new ArrayList<>();
    private static boolean decksAreGenerated = false;

    public static void generateDecks() throws IOException {
        DrawingDeck.totalGoldCard = (ArrayList<GoldCard>)GoldCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/GoldCardDB.json");
        DrawingDeck.totalResourceCard = (ArrayList<ResourceCard>)ResourceCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/ResourceCardDB.json");
        DrawingDeck.totalStartingCards = (ArrayList<StarterCard>)StarterCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/StarterCardDB.json");
        DrawingDeck.totalObjectiveComboCards = (ArrayList<ObjectiveCardCombo>)ObjectiveCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/ObjectiveCardDB.json");
        DrawingDeck.totalObjectiveResourceSetCards = (ArrayList<ObjectiveCardResourceSet>)ObjectiveCardResourceSetDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/ObjectiveCardResourceSetDB.json");
        DrawingDeck.totalObjectiveCards.addAll(totalObjectiveComboCards);
        DrawingDeck.totalObjectiveCards.addAll(totalObjectiveResourceSetCards);

        Collections.shuffle(DrawingDeck.totalGoldCard);
        Collections.shuffle(DrawingDeck.totalResourceCard);
        Collections.shuffle(DrawingDeck.totalStartingCards);
        Collections.shuffle(DrawingDeck.totalObjectiveCards);
    }

    public static ArrayList<ObjectiveCard> drawTwoObjectiveCards() {

        ArrayList<ObjectiveCard> cards = new ArrayList<>();
        cards.add(totalObjectiveCards.get(0));
        totalObjectiveCards.remove(0);
        cards.add(totalObjectiveCards.get(1));
        totalObjectiveCards.remove(1);
        return cards;
    }

    public static ArrayList<ObjectiveCard> drawCommonObjective() {
        ArrayList<ObjectiveCard> commonObj = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            commonObj.add(totalObjectiveCards.getFirst());
            totalObjectiveCards.removeFirst();
        }
        return commonObj;
    }
    public static void reAddSecretObjectiveCard(ObjectiveCard objectiveCard){
        totalObjectiveCards.addLast(objectiveCard);
    }

    /**
     * Method called from the player who draws a card
     *
     * @param cardType: defines the card that will be drawn, true for resource, false for gold
     * @return ResourceGoldCard, card that will be added to the playerDeck and removed from its deck
     */
    public static ResourceGoldCard drawCard(boolean cardType) {
        ResourceGoldCard drewCard;
        if (cardType) {
            drewCard = totalResourceCard.getFirst();
            totalResourceCard.removeFirst();
        } else {
            drewCard = totalGoldCard.getFirst();
            totalGoldCard.removeFirst();
        }
        return drewCard;
    }

    /**
     * The method generates the player deck by randomly choosing 2 cards
     *
     * @return PlayerDeck
     */
    public static PlayerDeck generatePlayerDeck() throws IOException {
        if (!decksAreGenerated) {
            generateDecks();
            decksAreGenerated = true;
        }
        ArrayList<ResourceGoldCard> deckGen = new ArrayList<>();
        StarterCard starterGen;
        for (int i = 0; i < 2; i++) {
            deckGen.add(totalResourceCard.getFirst());
            totalResourceCard.removeFirst();
        }
        deckGen.add(totalGoldCard.getFirst());
        totalGoldCard.removeFirst();
        starterGen = totalStartingCards.getFirst();
        totalStartingCards.removeFirst();
        return new PlayerDeck(deckGen, starterGen);
    }

    /**
     * Checks whether the chosen deck still has cards
     *
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

class PlayerDeck implements Serializable {
    /**
     * cards: ArrayList of the cards the player has except the Secret Card and the Starter Card
     */
    private ArrayList<ResourceGoldCard> resourceGoldCards;
    /**
     * starterCard: starter Card of the player
     */
    private StarterCard starterCard;
    /**
     * secretObjectiveCard: secret Objective Card of the player
     */
    private ObjectiveCard secretObjectiveCard;

    public PlayerDeck(ArrayList<ResourceGoldCard> resourceGoldCards, StarterCard starterCard) {

        this.resourceGoldCards = resourceGoldCards;
        this.starterCard = starterCard;
        this.secretObjectiveCard = null;
    }

    public void printResourceGoldCards() {
        for (int i = 1; i <= getResourceGoldCards().size(); i++) {
            System.out.println("[" + i + "]- " + getResourceGoldCards().get(i - 1).getIdCard());
            getResourceGoldCards().get(i - 1).printCardFrontAndBack();
        }

    }

    /**
     * Setter of starterCard
     *
     * @param starterCard: starter Card of the player
     */
    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
    }

        /**
         * Setter of secretObjectiveCard
         * @param secretObjectiveCard: secret Objective Card of the player
         */
        /*public void removeUnusedSecretObjectiveCard(ObjectiveCard secretObjectiveCard) {
            this.secretObjectiveCard.remove(secretObjectiveCard);
            DrawingDeck.reAddSecretObjectiveCard(secretObjectiveCard);
        }*/

    /**
     * Getter of cards
     *
     * @return ArrayList of the cards the player has except the Secret Card and the Starter Card
     */
    public ArrayList<ResourceGoldCard> getResourceGoldCards() {
        return resourceGoldCards;
    }

    /**
     * Getter of the starter Card
     *
     * @return starter Card of the player
     */
    public StarterCard getStarterCard() {
        return starterCard;
    }

    /**
     * Getter of
     *
     * @return secret Objective Card of the player
     */
    public ObjectiveCard getSecretObjectiveCard() {
        return secretObjectiveCard;
    }

    public void setSecretObjectiveCard(ObjectiveCard cardToKeep) {
        this.secretObjectiveCard = cardToKeep;
    }
}
