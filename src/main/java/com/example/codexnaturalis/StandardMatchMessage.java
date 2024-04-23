package com.example.codexnaturalis;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.UUID;

public class StandardMatchMessage {
    private ArrayList<ResourceGoldCard> publicCardsNewState;
    private UUID currentPlayerId;
    private UUID nextPlayerId;
    private ResourceGoldCard placedCard;
    private Pair<Integer, Integer> coords;

    public StandardMatchMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, UUID nextPlayerId, ResourceGoldCard placedCard, Pair<Integer, Integer> coords) {
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

class EndMatchMessage extends StandardMatchMessage{
    public EndMatchMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, UUID nextPlayerId, ResourceGoldCard placedCard, Pair<Integer, Integer> coords) {
        super(publicCardsNewState, currentPlayerId, nextPlayerId, placedCard, coords);
    }
}

class CurrentPlayerDisconnectedMessage extends StandardMatchMessage{

    public CurrentPlayerDisconnectedMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, UUID nextPlayerId) {
        super(publicCardsNewState, currentPlayerId, nextPlayerId, null, null);
    }
}

class notCurrentPlayerDisconnectedMessage extends CurrentPlayerDisconnectedMessage {

    public notCurrentPlayerDisconnectedMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId) {
        super(publicCardsNewState, currentPlayerId, null);
    }
}

