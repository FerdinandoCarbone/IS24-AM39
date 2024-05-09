package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ServerHandler extends Thread implements Runnable {
    private final String clientName;
    private final UUID clientID;
    private Pair<String,Integer> connectionInfo;
    private GenericTurnMessage messageTurn;
    private final ConnectionManger connMan;
    private volatile boolean firstBroadCastWasReceived;
    public ServerHandler(String clientName, UUID clientID,ConnectionManger connMan){
        this.clientName = clientName;
        this.clientID = clientID;
        this.connMan = connMan;
        this.connectionInfo = ZakClient.getConnectionInfo();
        this.firstBroadCastWasReceived=false;
        this.messageTurn=null;
    }
    public UUID getClientID() {
        return clientID;
    }
    public ConnectionManger getConnMan() {
        return connMan;
    }
    public String getClientName() {
        return clientName;
    }
    public boolean wasFirstBroadCastReceived() {
        return firstBroadCastWasReceived;
    }
    public void setFirstBroadCastWasReceived(boolean firstBroadCastWasReceived) {
        this.firstBroadCastWasReceived = firstBroadCastWasReceived;
    }
    public void textMessageHandler(TextMessage message) {
        String sender = message.getSender();
        if(Objects.equals(sender, getClientName())) sender="You";
        System.out.println(sender+": "+message.getTextMessage());
        if(message.getTextMessage().contains("kicked")) System.exit(0);
        if(!wasFirstBroadCastReceived()) setFirstBroadCastWasReceived(true);
    }
    public void genericTurnMessageHandler(GenericTurnMessage message){
        this.messageTurn = message;
        ZakClient.genericTurnMessageHandler();
    }
    public void sendMessage(Message message) throws IOException {

    }
    public void broadCastStartingMessageHandler(BroadCastStartingMessage initialMatchSetupMessage) throws IOException {
        try{
            if(initialMatchSetupMessage.equals(null)) throw new WrongMessageConversionException("Was not able to initialize Starting Field");
        } catch (WrongMessageConversionException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        ZakClient.initialMatchSetup(initialMatchSetupMessage);
    }
    public void clientDisconnected() {
        //todo: robe per chiudere i thread
        ZakClient.clientDisconnect();
    }
    public void bcsHandler(BroadCastStandardMessage message) {
        HashMap<UUID,StarterCard> hashStart= message.starterCards;
        hashStart.remove(getClientID());
        boolean face;
        for(Player p: ZakClient.getOtherPlayers()){
            face = hashStart.get(p.getPlayerID()).isPlacedFront();
            p.placeStarterCard(face);
        }
    }
    public void universalStatusUpdater(StandardMatchMessage newStatus){
        UUID oldPlayer= newStatus.getClientID();
        System.out.println("Updating game status" + oldPlayer+" Points: "+ newStatus.getCurrPlayerPoints());
        ResourceGoldCard placedCard= newStatus.getPlacedCard();
        Pair<Integer,Integer> coords= newStatus.getCoords();
        ArrayList<Player> players = ZakClient.getOtherPlayers();
        if(getClientID().equals(oldPlayer))ZakClient.getPlayer().setScore(newStatus.getCurrPlayerPoints());
        else {
            for (Player p : players) {
                if (p.getPlayerID().equals(oldPlayer)) {
                    System.out.println("Player found:");
                    p.placeCard(coords.getKey(), coords.getValue(), placedCard);
                    p.setScore(newStatus.getCurrPlayerPoints());
                    break;
                }
            }
        }
    }
    public GenericTurnMessage getMessageTurn() {
        return messageTurn;
    }
    public void winnerDeclaration(EndMatchMessage message) throws WrongMessageConversionException {
        ArrayList<Player> players = message.getFinalWinners();
        String winString;
        switch(players.size()){
            case 1:
                winString = players.getFirst().getPlayerName() + " is the winner of this game";
                break;
            case 2,3,4:
                winString = "There was a Draw between: \n";
                for (Player p : players){
                    winString.concat(p.getPlayerName()+" ");
                }
            default: throw new WrongMessageConversionException("There was a problem declaring the winner");
        }
        System.out.println(winString+"\nThank you for playing");
    }
    public void setMessageTurn(GenericTurnMessage messageTurn) {
        this.messageTurn = messageTurn;
    }
}

class ServerSocketHandler extends ServerHandler {

    private Socket socket;
    private ObjectOutputStream outServer;
    private ObjectInputStream inServer;

    public ServerSocketHandler(String clientName, UUID clientID,ConnectionManger connMan) throws IOException {
        super(clientName,clientID,connMan);
        this.outServer = connMan.getIoStream().getValue();
        this.inServer = connMan.getIoStream().getKey();
        this.socket = connMan.socket;
    }
    @Override
    public void run() {
        do{
            try {
                messageReceiver();
            } catch (ClassNotFoundException | WrongMessageConversionException e) {
                System.out.println("ServerComHandler error: " + e.getMessage());

            } catch(IOException e){
                System.out.println("ServerComHandler error: " + e.getMessage());
                try {
                    throw new ClientAbruptlyDisconnectedException(getClientName()+" abruptly disconnected from server due to socket degradation: Attempting reconnection");
                } catch (ClientAbruptlyDisconnectedException ex) {
                    if(tryReconnectToServer()) continue;
                    clientDisconnected();
                }
            }
            try{
                if(socket.isClosed() && ZakClient.isCurrentGameStatus()) throw new ClientAbruptlyDisconnectedException(getClientName()+" abruptly disconnected from server: Attempting reconnection");
            }catch(ClientAbruptlyDisconnectedException e){
                if(tryReconnectToServer()) continue;
                clientDisconnected();
            }
        }while(ZakClient.isCurrentGameStatus());
    }


    private boolean tryReconnectToServer()  {
        boolean result=true;
        Socket socket;
        try {
            socket = getConnMan().connectionAttempt();
            outServer= new ObjectOutputStream(socket.getOutputStream());
            inServer = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inServer.readObject();
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
                bcsHandler((BroadCastStandardMessage) message);
                break;
            case "BroadCastStartingMessage":
                broadCastStartingMessageHandler((BroadCastStartingMessage) message);
                break;
            case "EndMatchMessage":
                endOfTheGame((EndMatchMessage)message);
                break;
            case "LobbyCreationMessage":
                break;
            case "StandardMatchMessage":
                universalStatusUpdater((StandardMatchMessage) message);
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server: "+a.getName()+" is not Handled");
        }
    }
    @Override
    public void sendMessage(Message message) throws IOException {
        message.setSender(getClientName());
        message.setClientID(getClientID());
        outServer.writeObject(message);
    }

    private void endOfTheGame(EndMatchMessage message) throws IOException, WrongMessageConversionException {
        winnerDeclaration(message);
        outServer.close();
        inServer.close();
        ZakClient.endOfTheGame();
    }


}
class ServerRMIHandler extends ServerHandler{

    RemoteServerMethodInterface remoteProxy;
    public ServerRMIHandler(String clientName, UUID clientID,ConnectionManger connman) {
        super(clientName, clientID,connman);
        this.remoteProxy = connman.getRemoteServerProxy();
    }

    @Override
    public void run(){
        while(true){
            try {
                if(remoteProxy.callFor(getClientID())) messageReceiver(remoteProxy.whatToCall(getClientID()));
                remoteProxy.keepAlive(getClientID());
            } catch(RemoteException e){
                System.err.println("ServerRMIHandler: "+e.getMessage());
                if(tryReconnectToServer()) continue;
                //todo: reconnection attempt
                clientDisconnected();
            } catch(IOException |WrongMessageConversionException e){
                System.err.println("IOException: " +e.getMessage() );
            } catch (InterruptedException e) {
                System.err.println("InterruptedException: " +e.getMessage() );
            }
        }
    }
    private boolean tryReconnectToServer()  {
        boolean result=true;
        try {
            getConnMan().connectionAttempt();
        } catch (Exception e){
            result = false;
        }
        return result;
    }
    private void messageReceiver(Message message) throws WrongMessageConversionException, IOException {
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
                bcsHandler((BroadCastStandardMessage) message);
                break;
            case "BroadCastStartingMessage":
                broadCastStartingMessageHandler((BroadCastStartingMessage) message);
                break;
            case "EndMatchMessage":
                endOfTheGame((EndMatchMessage)message);
                break;
            case "LobbyCreationMessage":
                break;
            case "StandardMatchMessage":
                universalStatusUpdater((StandardMatchMessage) message);
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server: "+a.getName()+" is not Handled");
        }
    }
    @Override
    public void sendMessage(Message message){
        message.setSender(getClientName());
        message.setClientID(getClientID());
        try {
            remoteProxy.send(message);
        }catch (RemoteException e){
            System.err.println(e.getMessage());
        }
    }
    private void endOfTheGame(EndMatchMessage message) throws WrongMessageConversionException {
        winnerDeclaration(message);
        ZakClient.endOfTheGame();
    }
}
