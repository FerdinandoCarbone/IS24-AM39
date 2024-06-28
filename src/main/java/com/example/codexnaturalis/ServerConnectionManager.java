package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerConnectionManager implements Serializable {
    //protected static HashMap<Player, Socket> hashPlayer;
    static HashMap<UUID, Player> hashClient;
    public static HashMap<UUID, ClientHandler> handlers;
    public static final Object lock = new Object();
    static boolean firstPlayer;
    static String serverName;
    static int port;
    private int rmiPort;
    static RMIConnectionListener rmiListener;
    static SocketConnectionListener socketListener;
    static int numPlayers;
    static ServerSocket serverSocket;
    public static UUID reconnectingID;
    private ArrayList<UUID> kickedIDs;

    public ServerConnectionManager(Pair<String, Integer> connectionInfo, int rmiPort, boolean isCrashed) throws IOException {
        if(!isCrashed) {
            //hashPlayer = new HashMap<>();
            hashClient = new HashMap<>();
            handlers = new HashMap<>();
            firstPlayer = false;
            numPlayers = 0;
        }
        this.rmiPort = rmiPort;
        port = connectionInfo.getValue();
        serverSocket = new ServerSocket(port);
        serverName = connectionInfo.getKey();
        reconnectingID = null;
        rmiListener = new RMIConnectionListener(this);
        socketListener = new SocketConnectionListener(this);
        kickedIDs = new ArrayList<>();
        System.out.println(InetAddress.getLocalHost());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                //String unbindingName = "rmi://"+"localhost"+"/"+serverName;
                Naming.unbind(serverName);
                UnicastRemoteObject.unexportObject(rmiListener.remoteServerSkeleton, true);
                System.out.println("RMI release.");
            } catch (RemoteException e) {
                System.err.println("RemoteError: "+ e.getMessage());
            } catch (MalformedURLException | NotBoundException e) {
                System.out.println("Unable to perform action:" + e.getMessage());
                throw new RuntimeException(e);
            }
            rmiListener.executorService.shutdown();
        }));
    }

    /**
     * Next two methods are strictly tied. rmiListener is always running Thread serving the RMI connected clients,
     * SocketListener Thread is opened only if there is a socket trying to connect.
     * Thinking of leaving it open accepting connections and dropping ones not necessary.
     * As of now it seems to be working fine, so will leave as is
     *
     * @param isReconnection- if the client is trying to reconnect after a crash or connection issue set true; else set to false
     */
    public void acceptConnection(boolean isReconnection) throws RemoteException, MalformedURLException {
        //System.out.println(isReconnection);
        rmiListener.start();
        socketListener.start();
        if(Server.isCrashed()) return;
        while (!firstPlayer || (hashClient.size() < numPlayers || numPlayers == 0) || isReconnection) {
            while (connectionCondition()) {
                try {
                    acceptSocketRMIConnections(null, isReconnection);
                } catch (ClassNotFoundException | InterruptedException e) {
                    System.out.println("SERVER failure: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("SERVER failure: " + e.getMessage());
                    //if (firstPlayer) continue;
                }
            }
        }
        //socketListener.setHasToRun(false);
    }

    public ArrayList<UUID> getKickedIDs() {
        return kickedIDs;
    }

    public void setKickedIDs(ArrayList<UUID> kickedIDs) {
        this.kickedIDs = kickedIDs;
    }

    /**
     * Method accepting incoming client connections to server, either Socket or RMI
     * @param socket: client socket
     * @param isReconnection: boolean indicating whether it's a reconnection after crash or initial connection
     * @return Pair containing ObjectInputStream and ObjectOutputStream used for communication between client and server
     */
    public Pair<ObjectInputStream, ObjectOutputStream> acceptSocketRMIConnections(Socket socket, boolean isReconnection) throws IOException, ClassNotFoundException, InterruptedException {
        //if (isReconnection) socketListener.setHasToRun(true);
        ObjectOutputStream out;
        ObjectInputStream in;
        Socket clientSocket;
        Message clientJoinRequest;
        LobbyCreationMessage handshakeACK;
        Player player;
        if (socketListener.sockets.isEmpty()) {
            if (isReconnection) {
                //System.out.println("DEBUG1");
                //socketListener.setHasToRun(false);
            }
            //System.out.println("DEBUG0");
            return null;
        }
        if (socket != null) clientSocket = socket;
        else {
            clientSocket = socketListener.sockets.getFirst();
            socketListener.sockets.remove(clientSocket);
        }
        //System.out.println("DEBUG2");
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        //clientSocket.setSoTimeout(10000);
        //System.out.println("Streams generated");
        clientJoinRequest = (Message) in.readObject();//prendo l'handshake Message
        /*Snippet intended for kicking players when trying to get in a game they do not belong to*/
        if(isReconnection&&socket!=null){
            ArrayList<String> playerNames = new ArrayList<>();
            for(Player p : hashClient.values()) playerNames.add(p.getPlayerName());
            if(!playerNames.contains(clientJoinRequest.getSender())) {
                out.writeObject(new TextMessage(null,null,null,null));
                out.flush();
                socket.close();
                return null;
            }
        }
        if(clientJoinRequest.isReconnectServerCrash()) {
                //System.out.println("Entered here");
                out.writeObject(new ResetMatchMessage("Server", null, "I crashed", null));
                return null;
        }
        if (!firstPlayer && !isReconnection) {
            firstPlayer = true;
            handshakeACK = new LobbyCreationMessage(serverName, null, numPlayers);
            out.writeObject(handshakeACK);
            handshakeACK = (LobbyCreationMessage) in.readObject();
            numPlayers = handshakeACK.getNumPlayer();
            //playerCounter = numPlayers;
            System.out.println("There will be " + numPlayers + " players");
        }
        else {
            ArrayList<Player> players = new ArrayList<>(getPlayers());
            Message tmp = new Message("!!++***++!!", null);
            for (int i = 0; i < players.size(); i++) {
                /**
                 * In this code snippet I am managing the reconnection after a crash of the client
                 * Firstly I am checking whether there is a player with the same ID as the one the reconnecting client has
                 */
                System.out.println("Iterating:"+players.get(i).getPlayerID()+"Connecting Player:"+clientJoinRequest.getClientID());
                if (players.get(i).getPlayerID().compareTo(clientJoinRequest.getClientID()) == 0) {
                    /**
                     * here I am checking if the matchID the client and server have match.(If a player disconnected an hour ago and
                     * the match he was in ended and another one started, the player won't be able to join as he will be kicked/prompted to restart the client)
                     * Otherwise his reconnection attempt will take place
                     */
                    if (clientJoinRequest.getMatchID() == null) {
                        System.out.println("DEBUG3");
                        break;
                    }
                    else if (clientJoinRequest.getMatchID().equals(Server.match.getMatchID())) {
                        System.out.println(clientJoinRequest.getSender() + " is trying to reconnect");
                        break;
                    }
                    /**
                     * else the player is kicked and his save file is removed
                     */
                    else {
                        System.out.println(Colors.PURPLE + "Forbidden" + Colors.RESET);
                        out.writeObject(new Message("FORBIDDEN", null));
                        return null;
                    }
                }
                /**
                 * Checking if the player has submitted an already taken username
                 * This functionality is Only usable when starting clients on different machines
                 * or in different directories
                 */
                if (players.get(i).getPlayerName().equals(clientJoinRequest.getSender())) {
                    System.out.println("HERE");
                    out.writeObject(tmp);
                    clientJoinRequest = (Message) in.readObject();
                    i = -1;
                }
            }
            /**
             * If a new player (with a different ID as ones who are currently connected) tries to connect he will be receiving the number of players
             * Required for the connection of player after the lobby is created
             */
            if (!Arrays.stream((players.stream().map(Player::getPlayerID).toArray(UUID[]::new))).toList().contains(clientJoinRequest.getClientID())) {
                handshakeACK = new LobbyCreationMessage(serverName, null, hashClient.size());
                try {
                    System.out.println("Sto inviando i dati");
                    out.writeObject(handshakeACK);
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        /**
         * Code snipped reserved for first time connection with the server, before the match starts
         */
        //System.out.println(Thread.currentThread().getName());
        if (hashClient.size() <= numPlayers && !isReconnection && Thread.currentThread().getName().compareToIgnoreCase("main") == 0) {
            String sender = clientJoinRequest.getSender();
            UUID clientID = clientJoinRequest.getClientID();
            player = new Player(sender, new Field(CardDim.matrixSize, CardDim.matrixSize), clientID);
            //hashPlayer.put(player, clientSocket);
            hashClient.put(clientID, player);
            System.out.println(sender + " joined the server");
            Pair<ObjectInputStream, ObjectOutputStream> ioStream = new Pair<>(in, out);
            ClientHandler handler = new SocketClientHandler(sender, clientSocket, clientID, ioStream, this);
            new Thread(handler).start();
            handlers.put(clientID, handler);
            return null;
        }
        /**
         * If the connection is a reconnection socket information is updated
         */
        else if (isReconnection) {
            UUID currPlayerID=null;
            String sender = clientJoinRequest.getSender();
            UUID clientID = clientJoinRequest.getClientID();
            reconnectingID = clientID;
            if (Server.match == null) {
                currPlayerID = clientID;
                out.writeObject(new LobbyCreationMessage("MATCHNOTSTARTED", null, getNumPlayers()));
                System.out.println(sender + " rejoined the server");
                return new Pair<>(in, out);
            }
            else {
                if(!Server.isCrashed() || Server.restartMatchCondition()) currPlayerID = Server.match.getCurrentPlayerID();
                else if(Server.isCrashed()) {
                    if(!checkIfAllNull(Server.match.getPlayerIds())) {
                        Server.match.getPlayerIds().set(Server.match.getPlayers().indexOf(hashClient.get(clientID)),clientID);
                        int index = Server.match.selectIndexNextPlayer(hashClient.size()-1);
                        currPlayerID=Server.match.getPlayerIds().get(index);
                    }
                    else {
                        int index = -1;
                        do{
                           index= Server.match.getPlayers().indexOf(hashClient.get(clientID));
                        }while(index==-1);
                        Server.match.getPlayerIds().set(index,clientID);
                    }
                }
                BroadCastStartingMessage bcStart = new BroadCastStartingMessage("Server", currPlayerID, ServerConnectionManager.hashClient, Server.match.getCommonObjectives(), Server.match.selectedSecrets);
                HashMap<String,Boolean> currPlaying = new HashMap<>();
                for (int i =0;i<hashClient.size();i++)
                    if (Server.match.getPlayerIds().get(i) != null) currPlaying.put(hashClient.get(Server.match.getPlayerIds().get(i)).getPlayerName(),true);
                    else currPlaying.put(Server.match.getPlayers().get(i).getPlayerName(),false);
                bcStart.setCurrentlyPlaying(currPlaying);
                out.writeObject(bcStart);
                if (hashClient.get(clientID).getPlayerDeck().getSecretObjectiveCard() == null) {
                    BroadCastStartingMessage selector = (BroadCastStartingMessage) in.readObject();
                    Server.match.putBackOtherSecretObjectiveCard(clientID, selector.getSelectedSecret());
                    ServerConnectionManager.hashClient.get(clientID).placeStarterCard(selector.getStarterCardFace());
                    handlers.get(clientID).setSecretWasChosen(true);
                }
                else if (hashClient.get(clientID) != null && !Server.match.getPlayerIds().contains(clientID))
                    Server.match.addDisconnectedPlayerId(clientID);
                if ((Server.match.allNonNullIds()>2 &&Server.isCrashed()) || (currPlayerID!=null && clientID.compareTo(currPlayerID) == 0)) {
                    System.out.println(Colors.PURPLE+"Sending over GenericTurnMessage"+Colors.RESET);
                    Message msg = new GenericTurnMessage("Server", currPlayerID, Server.match.getCoveredCards(), Server.match.getPublicCards(), null);
                    out.writeObject(msg);
                }
            }
            //System.out.println("Current:" + currPlayerID + "\n" + "Reconnecting player:" + clientID);
            System.out.println(sender + " rejoined the server");
            TextMessage text = new TextMessage("Server",null,sender + " rejoined the server","Everyone");
            text.setDisconnectedClient(sender);
            try{
            if(!Server.isCrashed())sendBroadCastMessage(text);
            } catch (Exception e){
                System.out.println("Sending broadcast issue in ServerConnectionManager:"+e.getMessage());
            }
            System.out.println("in:"+ in +"out:"+ out);
            return new Pair<>(in, out);
        }
        return null;
    }

    public boolean checkIfAllNull(ArrayList<UUID> objs) {
        for(UUID ob:objs) if(ob!=null) return false;
        return true;
    }

    /**
     * Sends message to all ClientHandlers who will manage the communication with client's ServerHandlers and call their specific methods
     *
     * @param message - message object to be sent
     * @throws IOException -
     */
    public synchronized static void sendBroadCastMessage(Message message) throws IOException {
        for (int i=0;i<Server.match.getPlayerIds().size();i++) {
            UUID id=Server.match.getPlayerIds().get(i);
            if (id!=null) handlers.get(id).sendMessage(message);
        }
    }

    /**
     * Ssends a message to a specific ClientHandler given its clientID
     *
     * @param clientID - Identifier for a specific Client
     * @param message  - message object to send
     * @throws IOException -
     */
    public static void sendMessage(UUID clientID, Message message) throws IOException {
        for (UUID u : handlers.keySet()) {
            System.out.println(u);
        }
        handlers.get(clientID).sendMessage(message);
    }

    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    public static int getPort() {
        return port;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public static String getServerName() {
        return serverName;
    }

    public static Collection<Player> getPlayers() {
        return hashClient.values();
    }

    public HashMap<UUID, Player> getHashClient() {
        return hashClient;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public HashMap<UUID, ClientHandler> getHandlers() {
        return handlers;
    }

    private boolean connectionCondition() {
        synchronized (lock) {
            for (ClientHandler h : handlers.values()) {
                if (h instanceof SocketClientHandler && h.getReconnect()) return false;
            }
        }
        if (hashClient.size() == numPlayers && numPlayers != 0) return false;
        return true;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public static void setFirstPlayer(boolean firstPlayer) {
        ServerConnectionManager.firstPlayer = firstPlayer;
    }

    public static boolean isFirstPlayer() {
        return firstPlayer;
    }

    public static void setNumPlayers(int numPlayers) {
        ServerConnectionManager.numPlayers = numPlayers;
    }

    public static SocketConnectionListener getSocketListener() {
        return socketListener;
    }
}
