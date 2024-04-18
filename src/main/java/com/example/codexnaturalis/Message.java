package com.example.codexnaturalis;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private String sender;
    private UUID clientID;
    public Message(String sender,UUID ClientID){
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
    int numPlayer;
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

