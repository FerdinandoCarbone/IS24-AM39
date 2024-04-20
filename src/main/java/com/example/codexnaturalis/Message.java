package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Message implements Serializable {
    private String sender;
    private UUID clientID;
    public Message(String sender,UUID clientID){
        this.sender = sender;
        this.clientID = clientID;
    }
    public void setClientID(UUID clientID) {
        this.clientID = clientID;
    }

    public UUID getClientID() {
        return clientID;
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
class LobbyCreationMessage extends Message{
    private int numPlayer;
    public LobbyCreationMessage(String sender,UUID clientID,int numPlayer){
        super(sender,clientID);
        this.numPlayer = numPlayer;
    }
    public void setNumPlayer(int numPlayer){
        this.numPlayer = numPlayer;
    }

    public int getNumPlayer() {
        return numPlayer;
    }
}
class GenericTurnMessage extends Message{
    private Card drawnCard;
    private Card cardOnHand;
    private Pair<Integer,Integer> coordinates;
    public GenericTurnMessage(String sender, UUID ClientID,Card drawnCard,Card cardOnHand,Pair<Integer,Integer> coordinates) {
        super(sender, ClientID);
        this.cardOnHand = cardOnHand;
        this.drawnCard = drawnCard;
        this.coordinates = coordinates;
    }
    public Card getDrawnCard(){ return this.drawnCard;}

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public Card getCardOnHand() {
        return cardOnHand;
    }
}
class BroadCastStartingMessage extends Message{

    private HashMap<UUID, Player> players;
    private ArrayList<ObjectiveCard> commonObjectiveCards;
    //private List<ResourceGoldCard> publicCards;
    //private StarterCard starterCard;

    public BroadCastStartingMessage(String sender, UUID ClientID, HashMap<UUID, Player> players, ArrayList<ObjectiveCard> commonObjectiveCards) {
        super(sender, ClientID);
        this.players = players;
//      this.publicCards = publicCards
//      this.starterCard = starterCard;
        this.commonObjectiveCards = commonObjectiveCards;
    }

    public ArrayList<ObjectiveCard> getCommonObjectiveCards() {
        return commonObjectiveCards;
    }

    public HashMap<UUID, Player> getPlayers() {
        return players;
    }
}
class BroadCastStandardMessage extends Message{

    public BroadCastStandardMessage(String sender, UUID ClientID) {
        super(sender, ClientID);
    }
}
class TextMessage extends Message{
    private String textMessage;
    public TextMessage(String sender, UUID ClientID,String textMex) {
        super(sender, ClientID);
        this.textMessage = textMex;
    }

    public String getTextMessage() {
        return textMessage;
    }
}
class EndGameMessage extends Message{
    private String winner;
    public EndGameMessage(String sender, UUID ClientID,String winner) {
        super(sender, ClientID);
        this.winner = winner;
    }

    public String getWinner() {
        return this.winner;
    }
}