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
            } catch (HandShakeException | NotBoundException |NullPointerException |IOException e) {
                System.err.println(e.getMessage());
            }
        }
        else{
            socket=null;
            try{
                connectionAttempt();
            } catch (NotBoundException | HandShakeException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    public Socket connectionAttempt() throws HandShakeException, NotBoundException {
        int retryCount = 0;
        int milliseconds = 5000;
        Socket socket;
        while(true) {
            try {
                if(typeOfConnection){
                    remoteServerProxy = (RemoteServerMethodInterface) Naming.lookup("rmi://"+serverAddress+"/SERVER");
                    return null;
                }
                else {
                    socket = new Socket(serverAddress, port);
                    return socket;
                }
            } catch (IOException e) {
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
        LobbyCreationMessage handshakeACK;
        try{
            if(typeOfConnection) handshakeACK = remoteServerProxy.handShake(handshakeMessage);
            else {
                ioStream.getValue().writeObject(handshakeMessage);
                handshakeACK = (LobbyCreationMessage) ioStream.getKey().readObject();
            }
            switch(handshakeACK.getNumPlayer()) {
                case 0:
                    lobbyCreation(handshakeACK);
                    break;
                case 1,2,3:
                    System.out.println("Joined existing match...");
                    System.out.println("Waiting for everyone to join.");
                    break;
                default:
                    throw new TooManyPlayersException("Lobby is currently full. Wait for the match to end and try again");
            }
            System.out.println("CurrentPlayers: "+(handshakeACK.getNumPlayer()));
            ZakClient.setServerHandler(new ServerHandler(playerNick,clientID,this));
        } catch(HandShakeException | ClassNotFoundException e){
            System.err.println("There was an error during Handshake process: "+e.getMessage());
        }
    }
    
    private void lobbyCreation(LobbyCreationMessage msg) throws IOException, HandShakeException, StupidUserException {
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
            BroadCastStartingMessage bcStart;
            msg.setNumPlayer(desiredPlayerCount);
            msg.setSender(ZakClient.getPlayerNick());
            msg.setClientID(ZakClient.getClientID());
            if(!typeOfConnection){
                ioStream.getValue().writeObject(msg);
            }
            else{
                bcStart = remoteServerProxy.createLobby(msg);
            }
            System.out.println("Desired number of players:"+desiredPlayerCount);

        }
    }


    private boolean isTypeOfConnection(){
        return typeOfConnection;
    }
    public void setRmiPort(int rmiPort) {
    }
    public Pair<ObjectInputStream,ObjectOutputStream> getIoStream(){
        return ioStream;
    }

}
