package com.example.codexnaturalis;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

class DrawingDeck implements Serializable{
    private ArrayList<ResourceCard> totalResourceCard = new ArrayList<>();
    private ArrayList<GoldCard> totalGoldCard = new ArrayList<>();
    private ArrayList<ObjectiveCard> totalObjectiveCards = new ArrayList<>();
    private  ArrayList<ObjectiveCardCombo> totalObjectiveComboCards = new ArrayList<>();
    private  ArrayList<ObjectiveCardResourceSet> totalObjectiveResourceSetCards = new ArrayList<>();
    private  ArrayList<StarterCard> totalStartingCards = new ArrayList<>();

    public DrawingDeck() throws IOException {
        generateDecks();
    }
    public void generateDecks() throws IOException {
        this.totalGoldCard = (ArrayList<GoldCard>)GoldCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/GoldCardDB.json");
        this.totalResourceCard = (ArrayList<ResourceCard>)ResourceCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/ResourceCardDB.json");
        this.totalStartingCards = (ArrayList<StarterCard>)StarterCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/StarterCardDB.json");
        this.totalObjectiveComboCards = (ArrayList<ObjectiveCardCombo>)ObjectiveCardDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/ObjectiveCardDB.json");
        this.totalObjectiveResourceSetCards = (ArrayList<ObjectiveCardResourceSet>)ObjectiveCardResourceSetDatabaseLoader.loadCardsFromFile("src/main/resources/com/example/codexnaturalis/jsons/ObjectiveCardResourceSetDB.json");
        this.totalObjectiveCards.addAll(totalObjectiveComboCards);
        this.totalObjectiveCards.addAll(totalObjectiveResourceSetCards);

        Collections.shuffle(this.totalGoldCard, new Random());
        Collections.shuffle(this.totalResourceCard, new Random());
        Collections.shuffle(this.totalStartingCards, new Random());
        Collections.shuffle(this.totalObjectiveCards, new Random());
    }

    /**
     * Given a card, it adds it back to its deck
     * @param cardToReAdd: card to add
     */
    public void reAddCards(Card cardToReAdd) {
        if (cardToReAdd instanceof ResourceCard) {
            totalResourceCard.add((ResourceCard)cardToReAdd);
        } else if (cardToReAdd instanceof GoldCard) {
            totalGoldCard.add((GoldCard)cardToReAdd);
        } else if (cardToReAdd instanceof ObjectiveCard) {
            totalObjectiveCards.add((ObjectiveCard)cardToReAdd);
        } else if (cardToReAdd instanceof StarterCard) {
            totalStartingCards.add((StarterCard)cardToReAdd);
        }
    }

    /**
     * When called, returns an ArrayList containing 2 Objective Cards
     * @return 2 Objective Cards, from which the player will choose his secret one to keep
     */
    public ArrayList<ObjectiveCard> drawTwoObjectiveCards() {

        ArrayList<ObjectiveCard> cards = new ArrayList<>();
        cards.add(totalObjectiveCards.getFirst());
        totalObjectiveCards.removeFirst();
        cards.add(totalObjectiveCards.getFirst());
        totalObjectiveCards.removeFirst();
        return cards;
    }

    /**
     * When called, returns an ArrayList of ObjectiveCards
     * @return List of ObjectiveCards, which will be used as the common objectives during the match
     */
    public ArrayList<ObjectiveCard> drawCommonObjective() {
        ArrayList<ObjectiveCard> commonObj = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            commonObj.add(totalObjectiveCards.getLast());
            totalObjectiveCards.removeLast();
        }
        return commonObj;
    }

    /**
     * Called when the player chooses his secret objective card, the other one is reAdded to the total objective cards
     * @param objectiveCard which has to be added to the Objective Cards Deck
     */
    public void reAddSecretObjectiveCard(ObjectiveCard objectiveCard){
        totalObjectiveCards.addLast(objectiveCard);
    }

    /**
     * Method called from the player who draws a card
     * @param cardType: defines the card that will be drawn, true for resource, false for gold
     * @return ResourceGoldCard, card that will be added to the playerDeck and removed from its deck
     */
    public ResourceGoldCard drawCard(boolean cardType) {
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
     * When called generates the player deck, containing the resource gold cards and the starter card
     * @return PlayerDeck used in the constructor of Player
     */
    public PlayerDeck generatePlayerDeck() throws IOException {
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
    public boolean checkDeckEmptiness(int deckChoice) {
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

    public ArrayList<ResourceCard> getTotalResourceCard() {
        return totalResourceCard;
    }

    public ArrayList<GoldCard> getTotalGoldCard() {
        return totalGoldCard;
    }

    public ArrayList<ObjectiveCard> getTotalObjectiveCards() {
        return totalObjectiveCards;
    }

    public ArrayList<StarterCard> getTotalStartingCards() {
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
