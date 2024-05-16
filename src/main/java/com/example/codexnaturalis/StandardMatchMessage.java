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
    private Integer currPlayerPoints;
    public StandardMatchMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, String currentPlayerName, UUID nextPlayerId, ResourceGoldCard placedCard, Pair<Integer, Integer> coords) {
        super(currentPlayerName, currentPlayerId);
        this.publicCardsNewState = publicCardsNewState;
        this.nextPlayerId = nextPlayerId;
        this.placedCard = placedCard;
        this.coords = coords;
        this.currPlayerPoints = 0;
    }

    public Integer getCurrPlayerPoints() {
        return currPlayerPoints;
    }

    public void setCurrPlayerPoints(Integer currPlayerPoints) {
        this.currPlayerPoints = currPlayerPoints;
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
    ArrayList<Player> finalWinners;
    public EndMatchMessage(ArrayList<ResourceGoldCard> publicCardsNewState, UUID currentPlayerId, String playerName, UUID nextPlayerId, ResourceGoldCard placedCard, Pair<Integer, Integer> coords) {
        super(publicCardsNewState, currentPlayerId, playerName, nextPlayerId, placedCard, coords);
        this.finalWinners=new ArrayList<>();
    }

    public ArrayList<Player> getFinalWinners() {
        return finalWinners;
    }

    public void setFinalWinners(ArrayList<Player> finalWinners) {
        this.finalWinners = finalWinners;
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