package com.example.codexnaturalis;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.UUID;

public class MatchMessage {
    private ArrayList<ResourceGoldCard> publicCardsNewState;
    private UUID currentPlayerId;
    private UUID nextPlayerId;
    private ResourceGoldCard placedCard;
    private Pair<Integer, Integer> coords;

    public MatchMessage(ArrayList<ResourceGoldCard> publicCardsNewState,UUID currentPlayerId, UUID nextPlayerId, ResourceGoldCard placedCard, Pair<Integer, Integer> coords) {
        this.publicCardsNewState = publicCardsNewState;
        this.currentPlayerId = currentPlayerId;
        this.nextPlayerId = nextPlayerId;
        this.placedCard = placedCard;
        this.coords = coords;
    }

    public ArrayList<ResourceGoldCard> getPublicCardsNewState() {
        return publicCardsNewState;
    }

    public UUID getNextPlayerId() {
        return nextPlayerId;
    }
}
