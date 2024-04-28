package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.UUID;

import static com.example.codexnaturalis.ZakClient.receiveInput;

public class ConnectionManger {
    private Pair<ObjectInputStream,ObjectOutputStream> ioStream;
    boolean typeOfConnection;
    static String serverAddress;
    static int port;
    Socket socket;
    static RemoteServerMethodInterface remoteServerProxy;

    public ConnectionManger(boolean typeOfConnection,Pair<String,Integer> connectionInfo){
        this.typeOfConnection = typeOfConnection;
        serverAddress = connectionInfo.getKey();
        port = connectionInfo.getValue();
        socket=null;
        remoteServerProxy=null;
    }
   /* public ConnectionManger(boolean typeOfConnection,Pair<String,Integer> connectionInfo){
        this.typeOfConnection = typeOfConnection;
        serverAddress = connectionInfo.getKey();
        port = connectionInfo.getValue();
        socket=null;
        remoteServerProxy=null;
    }*/
    public void connectionSetup() throws IOException {
        if (!(typeOfConnection)) {
            remoteServerProxy=null;
            try {
                socket = connectionAttempt();
                InputStream sInStream = socket.getInputStream();
                OutputStream sOutStream = socket.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(sOutStream);
                ObjectInputStream in = new ObjectInputStream(sInStream);
                ioStream =  new Pair<>(in,out);
            } catch (HandShakeException | NullPointerException | IOException e) {
                System.err.println(e.getMessage());
            }
        }
        else{
            socket=null;
            try{
                connectionAttempt();
            } catch ( HandShakeException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    public Socket connectionAttempt() throws HandShakeException {
        int retryCount = 0;
        int milliseconds = 5000;
        Socket socket;
        while(true) {
            try {
                if(typeOfConnection){
                    remoteServerProxy = (RemoteServerMethodInterface) Naming.lookup("rmi://" + serverAddress + "/Server");
                    return null;
                }
                else {
                    socket = new Socket(serverAddress, port);
                    return socket;
                }
            } catch (IOException| NotBoundException e) {
                System.err.println("Unable to connect to the server: Trying to reconnect in "+milliseconds/1000+ "s");
                retryCount++;
                if (retryCount >= 3) throw new HandShakeException("Unable to connect to the server: Host may be down");
            }
            try {
                Thread.sleep(milliseconds); // wait before retrying
            } catch (InterruptedException ex) {
                System.err.println("Connection error:"+ ex.getMessage());
            }
        }
    }
    public void doHandShake(){
        UUID clientID = ZakClient.getClientID();
        String playerNick = ZakClient.getPlayerNick();
        try {
            startHandShake(playerNick, clientID);
        } catch (StupidUserException | IOException | HandShakeException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void startHandShake(String playerNick, UUID clientID) throws IOException, HandShakeException, StupidUserException {
        Message handshakeMessage = new Message(playerNick,clientID);
        LobbyCreationMessage handshakeACK=null;
        int numOfUsers=0;
        try{
            if(typeOfConnection) {
                numOfUsers= remoteServerProxy.getNumOfPlayers();
                ZakClient.setServerHandler(new ServerRMIHandler(playerNick,clientID,this));
            }
            else {
                ioStream.getValue().writeObject(handshakeMessage);
                handshakeACK = (LobbyCreationMessage) ioStream.getKey().readObject();
                ZakClient.setServerHandler(new ServerSocketHandler(playerNick,clientID,this));
                numOfUsers=handshakeACK.getNumPlayer();
            }
            switch(numOfUsers) {
                case 0:
                    numOfUsers=lobbyCreation();
                    break;
                case 1,2,3:
                    if(typeOfConnection) remoteServerProxy.joinLobby(new LobbyCreationMessage(playerNick,clientID,numOfUsers));
                    System.out.println("Joined existing match...");
                    System.out.println("Waiting for everyone to join.");
                    break;
                default:
                    throw new TooManyPlayersException("Lobby is currently full. Wait for the match to end and try again");
            }
            System.out.println("CurrentPlayers: "+numOfUsers);
        } catch(HandShakeException | ClassNotFoundException e){
            System.err.println("There was an error during Handshake process: "+e.getMessage());
        }
    }
    private int lobbyCreation() throws IOException, HandShakeException, StupidUserException {
        int desiredPlayerCount = 0;
        int i;
        System.out.println("No match found. Creating a new one:\nHow many players will be playing?\nWrite a number between 2 and 4:");
        try{
            for (i = 0; i<3; i++) {
                desiredPlayerCount = Integer.parseInt(receiveInput());
                if (desiredPlayerCount >= 2 && desiredPlayerCount <= 4) break;
                System.out.println("Unacceptable value was input.\nWrite a number between 2 and 4: ");
                if (i == 2) throw new StupidUserException("u stupid bruh");
            }
        } catch (NumberFormatException e){
            throw new StupidUserException("Unacceptable value was input.\nWrite a number between 2 and 4");
        } catch (StupidUserException e) {
            e.getMessage();
            throw new HandShakeException("Something went wrong during connection");
        } finally{
            LobbyCreationMessage msg = new LobbyCreationMessage(null,null,0);
            msg.setNumPlayer(desiredPlayerCount);
            msg.setSender(ZakClient.getPlayerNick());
            msg.setClientID(ZakClient.getClientID());
            if(!typeOfConnection){
                ioStream.getValue().writeObject(msg);
            }
            else{
                remoteServerProxy.createLobby(msg);
            }
            System.out.println("Desired number of players:"+desiredPlayerCount);
        }
        return desiredPlayerCount;
    }


    private boolean isTypeOfConnection(){
        return typeOfConnection;
    }
    public void setRmiPort(int rmiPort) {
    }
    public Pair<ObjectInputStream,ObjectOutputStream> getIoStream(){
        return ioStream;
    }

    public RemoteServerMethodInterface getRemoteServerProxy() {
        return remoteServerProxy;
    }
}
