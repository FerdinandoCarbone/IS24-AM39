package com.example.codexnaturalis;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StandardMatchMessage extends Message {
    private ArrayList<ResourceGoldCard> publicCardsNewState;
    private UUID nextPlayerId;
    private ResourceGoldCard placedCard;
    private Pair<Integer, Integer> coords;

    public StandardMatchMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, String currentPlayerName, UUID nextPlayerId, ResourceGoldCard placedCard, Pair<Integer, Integer> coords) {
        super(currentPlayerName, currentPlayerId);
        this.publicCardsNewState = publicCardsNewState;
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

    public Pair<Integer, Integer> getCoords() {
        return coords;
    }
    public ResourceGoldCard getPlacedCard() {
        return placedCard;
    }
}

class EndMatchMessage extends StandardMatchMessage{
    public EndMatchMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, String playerName, UUID nextPlayerId, ResourceGoldCard placedCard, Pair<Integer, Integer> coords) {
        super(publicCardsNewState, currentPlayerId, playerName, nextPlayerId, placedCard, coords);
    }
}

class CurrentPlayerDisconnectedMessage extends StandardMatchMessage{

    public CurrentPlayerDisconnectedMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, String playerName, UUID nextPlayerId) {
        super(publicCardsNewState, currentPlayerId, playerName, nextPlayerId, null, null);
    }
}

class notCurrentPlayerDisconnectedMessage extends CurrentPlayerDisconnectedMessage {

    public notCurrentPlayerDisconnectedMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, String playerName) {
        super(publicCardsNewState, currentPlayerId, playerName, null);
    }
}