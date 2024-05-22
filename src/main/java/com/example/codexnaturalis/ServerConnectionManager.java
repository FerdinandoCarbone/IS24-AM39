package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class ServerConnectionManager {
    protected static HashMap<Player, Socket> hashPlayer;
    static HashMap<UUID, Player> hashClient;
    public static HashMap<UUID, ClientHandler> handlers;
    private Pair<ObjectInputStream, ObjectOutputStream> ioStream;
    static boolean firstPlayer;
    static String serverName;
    static int port;
    private int rmiPort;
    static RMIConnectionListener rmiListener;
    static SocketConnectionListener socketListener;
    static int numPlayers;
    static ServerSocket serverSocket;
    static RemoteServerMethodInterface remoteServerSkeleton;
    public static UUID reconnectingID;

    public ServerConnectionManager(Pair<String, Integer> connectionInfo, int rmiPort) throws IOException {
        this.rmiPort = rmiPort;
        hashPlayer = new HashMap<>();
        hashClient = new HashMap<>();
        handlers = new HashMap<>();
        port = connectionInfo.getValue();
        serverName = connectionInfo.getKey();
        serverSocket = new ServerSocket(port);
        firstPlayer = false;
        numPlayers = 0;
        reconnectingID = null;
        rmiListener = new RMIConnectionListener(this);
        socketListener = new SocketConnectionListener(this);

        /*///////TEST
        remoteServerSkeleton = new RMIServerImplement();
        LocateRegistry.createRegistry(getRmiPort());
        Naming.rebind(ServerConnectionManager.getServerName(), remoteServerSkeleton);*/
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
        rmiListener.start();
        socketListener.start();
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
                System.out.println("DEBUG1");
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
        System.out.println("DEBUG2");
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("DEBUG3");
        //clientSocket.setSoTimeout(10000);
        clientJoinRequest = (Message) in.readObject();//prendo l'handshake Message
        if (!firstPlayer && !isReconnection) {
            firstPlayer = true;
            handshakeACK = new LobbyCreationMessage(serverName, null, numPlayers);
            out.writeObject(handshakeACK);
            handshakeACK = (LobbyCreationMessage) in.readObject();
            numPlayers = handshakeACK.getNumPlayer();
            //playerCounter = numPlayers;
            System.out.println("There will be " + numPlayers + " players");
        } else {
            ArrayList<Player> players = new ArrayList<>(getPlayers());
            Message tmp = new Message("!!++***++!!", null);
            for (int i = 0; i < players.size(); i++) {
                /**
                 * In this code snippet I am managing the reconnection after a crash of the client
                 * Firstly I am checking whether there is a player with the same ID as the one the reconnecting client has
                 */
                if (players.get(i).getPlayerID().compareTo(clientJoinRequest.getClientID()) == 0) {
                    /**
                     * here I am checking if the matchID the client and server have match.(If a player disconnected an hour ago and
                     * the match he was in ended and another one started, the player won't be able to join as he will be kicked/prompted to restart the client)
                     * Otherwise his reconnection attempt will take place
                     */
                    if (clientJoinRequest.getMatchID() == null) {
                        //
                        break;
                    } else if (clientJoinRequest.getMatchID().equals(Server.match.getMatchID())) {
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
            player = new Player(sender, new Token(), new Field(CardDim.matrixSize, CardDim.matrixSize), clientID);
            hashPlayer.put(player, clientSocket);
            hashClient.put(clientID, player);
            System.out.println(sender + " joined the server");
            ioStream = new Pair<>(in, out);
            ClientHandler handler = new SocketClientHandler(sender, clientSocket, clientID, ioStream, this);
            new Thread(handler).start();
            handlers.put(clientID, handler);
            return null;
        }
        /**
         * If the connection is a reconnection socket information is updated
         */
        else if (isReconnection) {
            UUID currPlayerID;
            String sender = clientJoinRequest.getSender();
            UUID clientID = clientJoinRequest.getClientID();
            reconnectingID = clientID;
            if (Server.match == null) {
                currPlayerID = clientID;
                out.writeObject(new LobbyCreationMessage("MATCHNOTSTARTED", null, getNumPlayers()));
                System.out.println(sender + " rejoined the server");
                return new Pair<>(in, out);
            } else {
                currPlayerID = Server.match.getCurrentPlayerID();
                BroadCastStartingMessage bcStart = new BroadCastStartingMessage("Server", currPlayerID, ServerConnectionManager.hashClient, Server.match.getCommonObjectives(), Server.match.selectedSecrets);
                ArrayList<String> currPlaying = new ArrayList<>();
                for (UUID id : Server.match.getPlayerIds())
                    if (id != null) currPlaying.add(hashClient.get(id).getPlayerName());
                bcStart.setCurrentlyPlaying(currPlaying);
                out.writeObject(bcStart);
                if (hashClient.get(clientID).getPlayerDeck().getSecretObjectiveCard() == null) {
                    BroadCastStartingMessage selector = (BroadCastStartingMessage) in.readObject();
                    Server.match.putBackOtherSecretObjectiveCard(clientID, selector.getSelectedSecret());
                    ServerConnectionManager.hashClient.get(clientID).placeStarterCard(selector.getStarterCardFace());
                    handlers.get(clientID).setSecretWasChosen(true);
                } else if (hashClient.get(clientID) != null && !Server.match.getPlayerIds().contains(clientID))
                    Server.match.addDisconnectedPlayerId(clientID);
                if (clientID.compareTo(currPlayerID) == 0) {
                    System.out.println("Sending over GenericTurnMessage");
                    Message msg = new GenericTurnMessage("Server", currPlayerID, Server.match.getCoveredCards(), Server.match.getPublicCards(), null);
                    out.writeObject(msg);
                }
            }
            System.out.println("Current:" + currPlayerID + "\n" + "Reconnecting player:" + clientID);
            hashPlayer.replace(hashClient.get(clientID), clientSocket);
            System.out.println(sender + " rejoined the server");
            TextMessage text = new TextMessage("Server",null,sender + " rejoined the server","Everyone");
            text.setDisconnectedClient(sender);
            sendBroadCastMessage(text);
            return new Pair<>(in, out);
        }
        return null;
    }

    /**
     * Sends message to all ClientHandlers who will manage the communication with client's ServerHandlers and call their specific methods
     *
     * @param message - message object to be sent
     * @throws IOException -
     */
    public static void sendBroadCastMessage(Message message) throws IOException {
        for (UUID id : Server.match.getPlayerIds()) {
            if (id != null) handlers.get(id).sendMessage(message);
        }
        //todo: accessi a null in reconnect
    }

    /**
     * Ssends a message to a specific ClientHandler given its clientID
     *
     * @param clientID - Identifier for a specific Client
     * @param message  - message object to send
     * @throws IOException -
     */
    public static void sendMessage(UUID clientID, Message message) throws IOException {
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

    public Pair<ObjectInputStream, ObjectOutputStream> getIoStream() {
        return ioStream;
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
        synchronized (handlers) {
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
