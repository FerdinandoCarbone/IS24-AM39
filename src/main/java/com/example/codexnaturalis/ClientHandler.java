package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ClientHandler extends Thread implements Runnable {
    private ServerConnectionManager connMan;
    private final String clientName;
    private final UUID clientID;
    private boolean secretWasChosen;
    public ClientHandler(String clientName,UUID clientID,ServerConnectionManager connMan){
        this.clientName=clientName;
        this.clientID=clientID;
        this.connMan = connMan;
        this.secretWasChosen = false;
    }

    public boolean getSecretWasChosen() {
        return this.secretWasChosen;
    }

    public void sendMessage(Message message) throws IOException{
        System.out.println("wrong function");
    }
    public String getClientName(){
        return clientName;
    }

    public UUID getClientID() {
        return clientID;
    }

    public ServerConnectionManager getConnMan() {
        return connMan;
    }
    public void textMessageHandler(TextMessage message) throws IOException {
        UUID recipientClientID=null;
        String recipient = message.getRecipient();
        //System.out.println(recipient+" "+ recipientClientID);
        if(Objects.equals(recipient, "Everyone")) getConnMan().sendBroadCastMessage(message);
        else{
            for(Player p: getConnMan().getPlayers()){
                if(Objects.equals(p.getPlayerName(), recipient)) {
                    recipientClientID = p.getPlayerID();
                    break;
                }
            }
            getConnMan().sendMessage(recipientClientID,message);
        }
        System.out.println("\n"+message.getSender()+" to "+ message.getRecipient()+": "+message.getTextMessage());
        System.out.print("Command: ");

    }
    public void broadCastMessageHandler(BroadCastStandardMessage message) {}
    public void genericTurnMessageHandler(GenericTurnMessage message) throws IOException {
        StandardMatchMessage newStatus = ZakServer.match.genericTurn(message);
        GenericTurnMessage newTurn = new GenericTurnMessage("Server",null,ZakServer.match.getCoveredCards(),newStatus.getPublicCardsNewState(),null);
        ServerConnectionManager.sendBroadCastMessage(newStatus);
        ServerConnectionManager.sendMessage(newStatus.getNextPlayerId(),newTurn);
    }
    public void endOfTheGame(EndGameMessage message){
        ZakServer.gameStarted = false;
        ZakServer.match=null;
        //todo: match reset and restart function to initialize everything
    }
    public void secretObjectiveSelector(BroadCastStartingMessage message){
        ServerConnectionManager.hashClient.get(clientID).getPlayerDeck().setSecretObjectiveCard(message.getSelectedSecret());
        ArrayList<Player> players = ZakServer.match.getPlayers();
        for(Player p: players){
            if(p.getPlayerID().equals(getClientID())){
                p.placeStarterCard(message.getStarterCardFace());
            }
        }
        this.secretWasChosen=true;
    }
}
class RMIClientHandler extends ClientHandler{

    Message rmiDeliverer;
    volatile boolean hasToDeliver;
    public RMIClientHandler(String clientName, UUID clientID, ServerConnectionManager connMan) {
        super(clientName, clientID, connMan);
        rmiDeliverer=null;
        hasToDeliver=false;
    }
    @Override
    public void run(){
        while(true){
            while(!hasToDeliver) Thread.onSpinWait();
        }
    }
    @Override
    public void sendMessage(Message msg){
        if(!(msg instanceof TextMessage)) {
            msg.setClientID(getClientID());
            msg.setSender(getClientName());
        }
        rmiDeliverer=msg;
        hasToDeliver=true;
    }
    public void retrieveMessage(Message message) throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.","");
        switch (messageType){
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStandardMessage":
                broadCastMessageHandler((BroadCastStandardMessage) message);
                break;
            case "BroadCastStartingMessage":
                secretObjectiveSelector((BroadCastStartingMessage) message);
                break;
            case "EndGameMessage":
                endOfTheGame((EndGameMessage)message);
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }
    public Message getRmiDeliverer() {
        return rmiDeliverer;
    }

    public void setHasToDeliver(boolean hasToDeliver) {
        this.hasToDeliver = hasToDeliver;
    }
}


class SocketClientHandler extends ClientHandler{
    private Socket socket;
    private ObjectOutputStream outClient;
    private ObjectInputStream inClient;
    public SocketClientHandler(String clientName,Socket socket,UUID clientID, Pair<ObjectInputStream,ObjectOutputStream> iostream,ServerConnectionManager connMan) throws IOException {
        super(clientName, clientID,connMan);
        this.socket = socket;
        this.outClient = iostream.getValue();
        this.inClient = iostream.getKey();
    }

    @Override
    public void run() {
        do{
            try {
                messageReceiver();
            } catch(IOException | ClassNotFoundException | WrongMessageConversionException e){
                System.out.println("ERRORE CLIENT HANDLER");
                e.getMessage();
            }
            try{
                if(!ZakServer.gameStarted && socket.isClosed()) throw new ClientAbruptlyDisconnectedException(getClientName()+" abruptly disconnected: Attempting reconnection");
            }catch(ClientAbruptlyDisconnectedException e){
                if(tryReconnectClient()) continue;
                //todo: reconnection attempt
                clientDisconnected();
            }
        }while (true);
    }
    private boolean tryReconnectClient(){
        boolean result=true;
        Pair<ObjectInputStream,ObjectOutputStream> oIOstream;
        try {
            oIOstream = getConnMan().acceptSocketRMIConnections(true);
            outClient= oIOstream.getValue();
            inClient = oIOstream.getKey();
        } catch (Exception e){
            result = false;
        }
        return result;
    }
    private void clientDisconnected(){
        ServerConnectionManager.hashPlayer.remove(ServerConnectionManager.hashClient.get(getClientID()));
        ServerConnectionManager.hashClient.remove(getClientID());
        //todo: chiamata a match per rimuovere player
        ZakServer.stopThread(getClientID());
    }
    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inClient.readObject();
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.","");
        switch (messageType){
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStartingMessage":
                secretObjectiveSelector((BroadCastStartingMessage) message);
                break;
            case "BroadCastStandardMessage":
                broadCastMessageHandler((BroadCastStandardMessage) message);
                break;
            case "EndGameMessage":
                endOfTheGame((EndGameMessage)message);
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }

    @Override
    public void broadCastMessageHandler(BroadCastStandardMessage message) {
    }
    @Override
    public void sendMessage(Message message) throws IOException {
        if(!(message instanceof TextMessage)) {
            message.setClientID(getClientID());
            message.setSender(getClientName());
        }
        outClient.writeObject(message);
    }
}
