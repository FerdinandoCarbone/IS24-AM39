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
    private ArrayList<ResourceGoldCard> drawnCard;
    private ArrayList<ResourceGoldCard> cardOnHand;
    private Pair<Integer,Integer> coordinates;
    public GenericTurnMessage(String sender, UUID ClientID,ArrayList<ResourceGoldCard> drawnCard,ArrayList<ResourceGoldCard> cardOnHand,Pair<Integer,Integer> coordinates) {
        super(sender, ClientID);
        this.cardOnHand = cardOnHand;
        this.drawnCard = drawnCard;
        this.coordinates = coordinates;
    }
    public ArrayList<ResourceGoldCard> getDrawnCard(){ return this.drawnCard;}

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public ArrayList<ResourceGoldCard> getCardOnHand() {
        return cardOnHand;
    }
    public int printDrawnCards(int i){
        for(ResourceGoldCard rGC: drawnCard){
            System.out.println("--------------[" + i + "]--------------");
            rGC.printBackCorners();
            ++i;
        }
        return i;
    }
    public int printPublicCards(int i){
        for(ResourceGoldCard rGC: drawnCard){
            System.out.print(i+": ");
            rGC.printCardFrontAndBack();
            ++i;
        }
        return i;
    }

}
class BroadCastStartingMessage extends Message{

    private HashMap<UUID, Player> players;
    private ArrayList<ObjectiveCard> selectedSecret;
    private HashMap<UUID,ArrayList<ObjectiveCard>> secretObjectiveCardSelector;
    private Boolean starterCardFace;
    public BroadCastStartingMessage(String sender, UUID ClientID, HashMap<UUID, Player> players, ArrayList<ObjectiveCard> commonObjectiveCards,HashMap<UUID,ArrayList<ObjectiveCard>> secretObjectiveCardSelector) {
        super(sender, ClientID);
        this.players = players;
        this.secretObjectiveCardSelector = secretObjectiveCardSelector;
        this.selectedSecret=null;
        this.starterCardFace = null;
    }

    public ArrayList<ObjectiveCard> getSecretObjectiveCards(UUID clientID) {
        return secretObjectiveCardSelector.get(clientID);
    }

    public Boolean getStarterCardFace() {
        return starterCardFace;
    }

    public void setStarterCardFace(Boolean starterCardFace) {
        this.starterCardFace = starterCardFace;
    }

    public void setSelectedSecret(ArrayList<ObjectiveCard> secretObjectiveCardSelector) {
        this.selectedSecret = secretObjectiveCardSelector;
    }

    public ObjectiveCard getSelectedSecret() {
        return selectedSecret.getFirst();
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
    private String recipient;
    public TextMessage(String sender, UUID ClientID, String textMex, String recipient) {
        super(sender, ClientID);
        this.textMessage = textMex;
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
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