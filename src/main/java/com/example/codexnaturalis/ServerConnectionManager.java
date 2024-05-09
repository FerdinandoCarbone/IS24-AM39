package com.example.codexnaturalis;

import javafx.util.Pair;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

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

    public ServerConnectionManager(Pair<String,Integer> connectionInfo,int rmiPort) throws IOException {
        this.rmiPort = rmiPort;
        hashPlayer = new HashMap<>();
        hashClient = new HashMap<>();
        handlers = new HashMap<>();
        port = connectionInfo.getValue();
        serverName = connectionInfo.getKey();
        serverSocket=new ServerSocket(port);
        firstPlayer = false;
        numPlayers = 0;
        rmiListener = new RMIConnectionListener(this);
        socketListener = new SocketConnectionListener(this);
    }
    public void acceptConnection(boolean isReconnection) {
        rmiListener.start();
        socketListener.start();
        while (!firstPlayer || connectionCondition()) {
            try {
                acceptSocketRMIConnections(isReconnection);
            } catch(ClassNotFoundException|InterruptedException e){
                System.out.println("SERVER failure: " + e.getMessage());
            }
            catch(IOException e){
                System.err.println("SERVER failure: " + e.getMessage());
                //if (firstPlayer) continue;
            }
        }
        socketListener.setHasToRun(false);
    }
    public Pair<ObjectInputStream,ObjectOutputStream> acceptSocketRMIConnections(boolean isReconnection) throws IOException, ClassNotFoundException, InterruptedException {
        if(isReconnection) socketListener.setHasToRun(true);
        ObjectOutputStream out;
        ObjectInputStream in;
        Message clientJoinRequest;
        LobbyCreationMessage handshakeACK;
        Player player;
        if(socketListener.sockets.isEmpty()){
            if(isReconnection)socketListener.setHasToRun(false);
            return null;
        }
        Socket clientSocket = socketListener.sockets.getFirst();
        socketListener.sockets.removeFirst();
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        clientJoinRequest = (Message) in.readObject();
        if (!firstPlayer) {
            firstPlayer = true;
            handshakeACK = new LobbyCreationMessage(serverName,null,numPlayers);
            out.writeObject(handshakeACK);
            //todo: Timeout
            handshakeACK = (LobbyCreationMessage) in.readObject();
            numPlayers = handshakeACK.getNumPlayer();
            //playerCounter = numPlayers;
            System.out.println("There will be "+numPlayers+" players");
        }
        else{
            List<Player> players = getPlayers().stream().toList();
            Message tmp =new Message("!!++***++!!",null);
            for(int i=0;i<players.size();i++){
                System.out.println("OLD NICK: "+clientJoinRequest.getSender());
                if(players.get(i).getPlayerName().equals(clientJoinRequest.getSender())){
                    out.writeObject(tmp);
                    clientJoinRequest = (Message) in.readObject();
                    System.out.println("New user changed nick to: "+clientJoinRequest.getSender());
                    i=-1;
                }
                System.out.println("i:"+i);
            }
            handshakeACK = new LobbyCreationMessage(serverName, null, hashClient.size());
            try {
                out.writeObject(handshakeACK);
            } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
        if(hashClient.size()<=numPlayers && !isReconnection){
            String sender = clientJoinRequest.getSender();
            UUID clientID = clientJoinRequest.getClientID();
            player = new Player(sender,new Token(), new Field(5, 5),clientID);
            hashPlayer.put(player, clientSocket);
            hashClient.put(clientID,player);
            System.out.println(sender + " joined the server");
            ioStream = new Pair<>(in,out);
            ClientHandler handler = new SocketClientHandler(sender,clientSocket,clientID,ioStream,this);
            new Thread(handler).start();
            handlers.put(clientID, handler);
            return null;
        }
        else if(isReconnection){
            String sender = clientJoinRequest.getSender();
            UUID clientID = clientJoinRequest.getClientID();
            hashPlayer.replace(hashClient.get(clientID),clientSocket);
            System.out.println(sender + " rejoined the server");
            return new Pair<>(in,out);
        }
        return null;
    }

    public static void sendBroadCastMessage(Message message) throws IOException {
        for (ClientHandler handler : handlers.values()) {
            handler.sendMessage(message);
        }
    }
    public static void sendMessage(UUID clientID,Message message) throws IOException {
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
    public Pair<ObjectInputStream,ObjectOutputStream> getIoStream(){
        return ioStream;
    }
    public Collection<Player> getPlayers(){
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
    private boolean connectionCondition(){
        return hashClient.size() < numPlayers || numPlayers==0;
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
    /*private static void timeOutThrower(){
        final Duration timeout = Duration.ofSeconds(30);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<String> handler = executor.submit((Callable) () -> {
            return requestDataFromModem();
        });

        try {
            handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        executor.shutdownNow();
    }*/
}
