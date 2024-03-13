package com.example.codexnaturalis;

import java.util.ArrayList;

public class PlayerDeck {

    private ArrayList<NonObjectiveCard> cards;
    private StarterCard starterCard;
    private ObjectiveCard secretObjectiveCard;

    public PlayerDeck(ArrayList<NonObjectiveCard> cards, StarterCard starterCard) {
        this.cards = cards;
        this.starterCard = starterCard;
    }

    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
    }

    public void setCartaObiettivoSegreto(ObjectiveCard objectiveCardSegreto) {
        this.secretObjectiveCard = objectiveCardSegreto;
    }

    public ArrayList<NonObjectiveCard> getCards() {
        return cards;
    }

    public StarterCard getStarterCard() {
        return starterCard;
    }

    public ObjectiveCard getCartaObiettivoSegreto() {
        return secretObjectiveCard;
    }
}
