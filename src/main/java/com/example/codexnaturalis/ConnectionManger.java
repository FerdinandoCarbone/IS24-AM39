package com.example.codexnaturalis;

import javafx.util.Pair;
import com.example.codexnaturalis.Colors.*;
import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static com.example.codexnaturalis.ZakClient.*;

public class ConnectionManger {
    private Pair<ObjectInputStream, ObjectOutputStream> ioStream;
    boolean typeOfConnection;
    static String serverAddress;
    static int port;
    Socket socket;
    static RemoteServerMethodInterface remoteServerProxy;

    public ConnectionManger(boolean typeOfConnection, Pair<String, Integer> connectionInfo) {
        this.typeOfConnection = typeOfConnection;
        serverAddress = connectionInfo.getKey();
        port = connectionInfo.getValue();
        socket = null;
        remoteServerProxy = null;
    }

    /* public ConnectionManger(boolean typeOfConnection,Pair<String,Integer> connectionInfo){
         this.typeOfConnection = typeOfConnection;
         serverAddress = connectionInfo.getKey();
         port = connectionInfo.getValue();
         socket=null;
         remoteServerProxy=null;
     }*/
    public boolean connectionSetup() {
        if (!(typeOfConnection)) {
            remoteServerProxy = null;
            try {
                socket = connectionAttempt();
                System.out.println(Colors.PURPLE+ "Sono USCITO DA CONNECTION ATTEMPT"+socket+Colors.RESET);
                InputStream sInStream = socket.getInputStream();
                OutputStream sOutStream = socket.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(sOutStream);
                ObjectInputStream in = new ObjectInputStream(sInStream);
                ioStream = new Pair<>(in, out);
                System.out.println(Colors.PURPLE+ "Ho generato oIOStreams" +Colors.RESET);
            } catch (HandShakeException | NullPointerException | IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
        } else {
            socket = null;
            try {
                connectionAttempt();
            } catch (HandShakeException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public Socket connectionAttempt() throws HandShakeException {
        int retryCount = 0;
        int milliseconds = 5000;
        Socket socket;
        while (true) {
            try {
                if (typeOfConnection) {
                    remoteServerProxy = (RemoteServerMethodInterface) Naming.lookup("rmi://" + serverAddress + "/Server");
                    return null;
                }
                else {
                    socket = new Socket(serverAddress, port);
                    System.out.println(Colors.PURPLE+ "Ho generato il socket"+socket +Colors.RESET);
                    return socket;
                }
            } catch (IOException | NotBoundException e) {
                System.err.println("Unable to connect to the server: Trying to reconnect in " + milliseconds / 1000 + "s");
                retryCount++;
                if (retryCount >= 3) throw new HandShakeException();
            }
            try {
                Thread.sleep(milliseconds); // wait before retrying
            } catch (InterruptedException ex) {
                System.err.println("Connection error:" + ex.getMessage());
            }
        }
    }

    public void doHandShake() {
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
        Message handshakeMessage = new Message(playerNick, clientID);
        handshakeMessage.setMatchID(ZakClient.getMatchID());
        LobbyCreationMessage handshakeACK;
        int numOfUsers;
        try {
            if (typeOfConnection) {
                numOfUsers = remoteServerProxy.getNumOfPlayers();
                ZakClient.setServerHandler(new ServerRMIHandler(playerNick, clientID, this));
            }
            else {
                ioStream.getValue().writeObject(handshakeMessage);
                Message ackMessage = (Message) ioStream.getKey().readObject();
                while (ackMessage.getClientID() == null && Objects.equals(ackMessage.getSender(), "!!++***++!!")) {
                    playerNick = nickRetype();
                    handshakeMessage.setSender(playerNick);
                    System.out.println("Handshake: " + handshakeMessage.getSender());
                    handshakeMessage.setClientID(clientID);
                    ioStream.getValue().reset();
                    ioStream.getValue().writeObject(handshakeMessage);
                    ackMessage = (Message) ioStream.getKey().readObject();
                }
                handshakeACK = (LobbyCreationMessage) ackMessage;
                ZakClient.setServerHandler(new ServerSocketHandler(playerNick, clientID, this));
                numOfUsers = handshakeACK.getNumPlayer();
            }
            switch (numOfUsers) {
                case 0:
                    numOfUsers = lobbyCreation();
                    break;
                case 1, 2, 3:
                    if (typeOfConnection) {
                        while (!remoteServerProxy.joinLobby(new LobbyCreationMessage(playerNick, clientID, numOfUsers))) {
                            playerNick = nickRetype();
                        }
                    }
                    System.out.println("Joined existing match...");
                    break;
                default:
                    throw new TooManyPlayersException("Lobby is currently full. Wait for the match to end and try again");
            }
            System.out.println("Waiting for everyone to join.");
            System.out.println("CurrentPlayers: " + numOfUsers);
        } catch (HandShakeException | ClassNotFoundException e) {
            System.err.println("There was an error during Handshake process: " + e.getMessage());
        }
    }

    private String nickRetype() throws IOException {
        System.out.println("Username already taken: choose another one");
        System.out.print("New username: ");
        String playerNick = receiveInput();
        System.out.println("NickRetype: " + playerNick);
        ZakClient.setPlayerNick(playerNick);
        /*ZakClient.setClientID(ZakClient.uuidGen());
        writeConnectionTypeOnFile(typeOfConnection?2:1);*/
        System.out.println("PlayerNick in client:" + ZakClient.getPlayerNick());
        return playerNick;
    }

    private int lobbyCreation() throws IOException, HandShakeException, StupidUserException {
        int desiredPlayerCount = 0;
        int i;
        System.out.println("No match found. Creating a new one:\nHow many players will be playing?\nWrite a number between 2 and 4:");
        try {
            for (i = 0; i < 3; i++) {
                desiredPlayerCount = getIntInput(4, false);
                if (desiredPlayerCount >= 2 && desiredPlayerCount <= 4) break;
                System.out.println("Unacceptable value was input.\nWrite a number between 2 and 4: ");
                if (i == 2) throw new StupidUserException("u stupid bruh");
            }
        } catch (NumberFormatException e) {
            throw new StupidUserException("Unacceptable value was input.\nWrite a number between 2 and 4");
        } catch (StupidUserException e) {
            System.out.println(e.getMessage());
            throw new HandShakeException();
        } finally {
            LobbyCreationMessage msg = new LobbyCreationMessage(null, null, 0);
            msg.setNumPlayer(desiredPlayerCount);
            msg.setSender(ZakClient.getPlayerNick());
            msg.setClientID(ZakClient.getClientID());
            if (!typeOfConnection) {
                ioStream.getValue().writeObject(msg);
            } else {
                remoteServerProxy.createLobby(msg);
            }
            System.out.println("Desired number of players:" + desiredPlayerCount);
        }
        return desiredPlayerCount;
    }


    private boolean isTypeOfConnection() {
        return typeOfConnection;
    }

    public void setRmiPort(int rmiPort) {
    }

    public Pair<ObjectInputStream, ObjectOutputStream> getIoStream() {
        return ioStream;
    }

    public RemoteServerMethodInterface getRemoteServerProxy() {
        return remoteServerProxy;
    }

    public void reHandShake() {
        UUID clientID = ZakClient.getClientID();
        String playerNick = ZakClient.getPlayerNick();
        Message handshakeMessage = new Message(playerNick, clientID);
        handshakeMessage.setMatchID(ZakClient.getMatchID());
        BroadCastStartingMessage handshakeACKInfo;
        Message tmpMessage;
        boolean amPlayerInTurn=false;
        try {
            if (typeOfConnection) {
                tmpMessage = remoteServerProxy.reHandShakeRMI(ZakClient.getMatchID());
                if(tmpMessage.getSender().equals("FORBIDDEN")){
                    throw new NotSameMatchException("A different match is being played: Wait for the current match to end");
                }
                handshakeACKInfo = (BroadCastStartingMessage) tmpMessage;
                ZakClient.setServerHandler(new ServerRMIHandler(playerNick, clientID, this));
                ZakClient.getServerHandler().setMessageTurn((GenericTurnMessage)remoteServerProxy.getMessageTurn(clientID));

            }
            else {
                System.out.println(Colors.PURPLE+ "Starting Handshake" +Colors.RESET);
                ioStream.getValue().writeObject(handshakeMessage);
                System.out.println("Flushing stream");
                tmpMessage = (Message) ioStream.getKey().readObject();
                System.out.println(tmpMessage.getClass());
                if(tmpMessage.getSender().equals("FORBIDDEN")){
                    throw new NotSameMatchException("A different match is being played: Wait for the current match to end");
                }
                handshakeACKInfo = (BroadCastStartingMessage) tmpMessage;
                System.out.println("CASTED");
                ZakClient.setServerHandler(new ServerSocketHandler(playerNick, clientID, this));
                //System.out.println(Colors.PURPLE+ "Settato correttamente" +Colors.RESET);
            }
            System.out.println(Colors.PURPLE+ "Setting Up field..." +Colors.RESET);
            System.out.println(Colors.PURPLE+ "Retrieving Current turn info..." +Colors.RESET);
            amPlayerInTurn = clientID.compareTo(handshakeACKInfo.getClientID()) == 0;
            System.out.println("MyClientID:"+ clientID+"\n"+"Received:"+handshakeACKInfo.getClientID()+"\n"+"bool:"+amPlayerInTurn);
            ZakClient.setPlayer(handshakeACKInfo.getPlayers().get(getClientID()));
            handshakeACKInfo.getPlayers().remove(getClientID());
            ArrayList<Player> players = new ArrayList<>(handshakeACKInfo.getPlayers().values());
            ZakClient.setOtherPlayers(players);
            ZakClient.getServerHandler().setFirstBroadCastWasReceived(true);
            if(!typeOfConnection&&amPlayerInTurn){
                GenericTurnMessage msg= (GenericTurnMessage) ioStream.getKey().readObject();
                ZakClient.getServerHandler().setMessageTurn(msg);
            }
            ZakClient.getServerHandler().start();
            setMyTurn(amPlayerInTurn);
            //System.out.println("Done!");
            if(amPlayerInTurn&&typeOfConnection) System.out.println("\nIt's your turn!");
        } catch(Exception e){
            System.out.println("Error while reconnecting after crash: "+e.getMessage());
            if (e.getClass().equals(NotSameMatchException.class)) {
                System.out.println("Save data can be deleted: deleting it now...");
                String filePath = "savedata/" + playerNick + "-matchInfo.cdxn";
                File file = new File(filePath);
                boolean deleted = file.delete();
                if (!deleted) System.out.println("Unable to delete save file");
            }
            clientDisconnect();
        }
        System.out.println("All players' fields were correctly received");
        ZakClient.setCurrentGameStatus(true);
    }
}
