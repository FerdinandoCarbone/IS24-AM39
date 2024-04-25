package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

public class ClientHandler extends Thread implements Runnable {
    private ServerConnectionManager connMan;
    private final String clientName;
    private final UUID clientID;
    public ClientHandler(String clientName,UUID clientID,ServerConnectionManager connMan){
        this.clientName=clientName;
        this.clientID=clientID;
        this.connMan = connMan;
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
}
class RMIClientHandler extends ClientHandler{
    public RMIClientHandler(String clientName, UUID clientID,ServerConnectionManager connman) {
        super(clientName, clientID,connman);
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
            oIOstream = getConnMan().acceptSocketConnections(true);
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
        ZakServer.stopThread(getClientID());
    }
    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inClient.readObject();
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.","");
        switch (messageType){
            case "GenericTurnMessage":
                GenericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
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

    private void broadCastMessageHandler(BroadCastStandardMessage message) {
    }

    private void GenericTurnMessageHandler(GenericTurnMessage message) {
    }
    @Override
    public void sendMessage(Message message) throws IOException {
        if(!(message instanceof TextMessage)) {
            message.setClientID(getClientID());
            message.setSender(getClientName());
        }
        outClient.writeObject(message);
    }
    private void textMessageHandler(TextMessage message) throws IOException {
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
        //todo:chat functionality
        System.out.println("\n"+message.getSender()+" to "+ message.getRecipient()+": "+message.getTextMessage());
        System.out.print("Command: ");

    }
    private void endOfTheGame(EndGameMessage message){
        ZakServer.gameStarted = false;
        ZakServer.match=null;
        //todo: match reset and restart function to initialize everything
    }
}
