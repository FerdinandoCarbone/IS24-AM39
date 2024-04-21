package com.example.codexnaturalis;

import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ZakServer {

    static HashMap<Player, Socket> hashPlayer = new HashMap<>();
    static HashMap<UUID, Player> hashClient = new HashMap<>();
    static HashMap<UUID, ClientHandler> handlers = new HashMap<>();

    static int numPlayers;
    static boolean firstPlayer = false;
    static boolean gameStarted = false;
    static Match match;
    static ServerSocket serverSocket;
    static String serverName;
    static int playerCounter = 1;
    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        try{
            serverStart(port);
        } catch(IOException e){
            e.getMessage();
        }
        while (!firstPlayer || hashClient.size() < numPlayers) {
        try {
            acceptConnections(false);
            } catch(ClassNotFoundException e){
                System.out.println("PROBLEMA SERVER: " + e.getMessage());
            }
        catch(IOException e){
                System.err.println("PROBLEMA SERVER: " + e.getMessage());
                //if (firstPlayer) continue;
            }
        }
            try{
            matchStart();
        }catch(Exception e){
            e.getMessage();
        }

    }
    public static Pair<ObjectInputStream,ObjectOutputStream> acceptConnections(boolean isReconnection) throws IOException, ClassNotFoundException {
        ObjectOutputStream out;
        ObjectInputStream in;
        Message clientJoinRequest;
        LobbyCreationMessage handshakeACK;
        Player player;
        Socket clientSocket = serverSocket.accept();
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        clientJoinRequest = (Message) in.readObject();
        if (!firstPlayer) {
            firstPlayer=true;
            handshakeACK = new LobbyCreationMessage(serverName,null,numPlayers);
            out.writeObject(handshakeACK);
            //todo: Timeout
            handshakeACK = (LobbyCreationMessage) in.readObject();
            numPlayers = handshakeACK.getNumPlayer();
            playerCounter = numPlayers;
            System.out.println("There will be "+numPlayers+" players");
        }
        else{
            handshakeACK = new LobbyCreationMessage(serverName, null, hashClient.size());
            try {
                out.writeObject(handshakeACK);
            } catch(RuntimeException e){
                e.getMessage();
            }
        }
        if(hashClient.size()<=numPlayers && !isReconnection){
            String sender = clientJoinRequest.getSender();
            UUID clientID = clientJoinRequest.getClientID();
            player = new Player(sender,new Token(), new Field(5, 5),clientID);
            hashPlayer.put(player, clientSocket);
            hashClient.put(clientID,player);
            System.out.println(sender + " joined the server");
            ClientHandler handler = new ClientHandler(sender,clientSocket,clientID, out, in);
            new Thread(handler).start();
            handlers.put(clientID, handler);
            return new Pair<>(in,out);
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
    public static void serverStart(int port) throws IOException {
        numPlayers = 0;
        serverSocket = new ServerSocket(port);
        serverName = "SERVER";
        gameStarted = false;
        System.out.println("\n" +
                "\n" +
                " _____                                                                      _____ \n" +
                "( ___ )--------------------------------------------------------------------( ___ )\n" +
                " |   |                                                                      |   | \n" +
                " |   |   ____          _           _   _       _                   _ _      |   | \n" +
                " |   |  / ___|___   __| | _____  _| \\ | | __ _| |_ _   _ _ __ __ _| (_)___  |   | \n" +
                " |   | | |   / _ \\ / _` |/ _ \\ \\/ /  \\| |/ _` | __| | | | '__/ _` | | / __| |   | \n" +
                " |   | | |__| (_) | (_| |  __/>  <| |\\  | (_| | |_| |_| | | | (_| | | \\__ \\ |   | \n" +
                " |   |  \\____\\___/ \\__,_|\\___/_/\\_\\_| \\_|\\__,_|\\__|\\__,_|_|  \\__,_|_|_|___/ |   | \n" +
                " |   | / ___|  ___ _ ____   _____ _ __                                      |   | \n" +
                " |   | \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|                                     |   | \n" +
                " |   |  ___) |  __/ |   \\ V /  __/ |                                        |   | \n" +
                " |   | |____/ \\___|_|    \\_/ \\___|_|                                        |   | \n" +
                " |___|                                                                      |___| \n" +
                "(_____)--------------------------------------------------------------------(_____)\n" +
                "\n");
        System.out.println("- developed by Team AM39");
    }
    public static void matchStart() throws Exception {
        System.out.println("La partita sta per cominciare");
        ArrayList<Player> players = new ArrayList<>(hashPlayer.keySet());
        match = new Match(players, new ScoreTracker());
        startingFieldClientSetup();
        welcomePlayer();
        gameStarted = true;
        match.startMatch();
    }
    private static void startingFieldClientSetup() throws IOException{
        //todo: da spostare in match probabilmente
        BroadCastStartingMessage fieldSetupMessage;
        ArrayList<ObjectiveCard> commonObjectiveCard;
        commonObjectiveCard = DrawingDeck.getCommonObjective();
        match.setCommonObjectives(commonObjectiveCard);
        fieldSetupMessage = new BroadCastStartingMessage(ZakServer.serverName,null,ZakServer.hashClient,commonObjectiveCard);
        sendBroadCastMessage(fieldSetupMessage);
    }
   private static void welcomePlayer() throws IOException {
        String text = "Match is about to start\nPlayers:\n";
        String firstPlayerStar = "";
        Collection<Player> players = hashClient.values();
        for(Player p: players){
            //if (p.isFirstTurn()) firstPlayerStar = " *";
            text=text.concat(p.getPlayerName()+firstPlayerStar+"\n");
            //firstPlayerStar = "";
        }
        sendBroadCastMessage(new TextMessage(ZakServer.serverName,null,text));
    }
    public static void sendBroadCastMessage(Message message) throws IOException {
        for (ClientHandler handler : handlers.values()) {
            handler.sendMessage(message);
        }
    }
    public static void stopThread(UUID clientID){
        //todo: fare le opportune modifiche a match
        handlers.get(clientID).interrupt();
        handlers.remove(clientID);
    }
   /* private static void sendStartingCards() throws IOException {
        GenericTurnMessage message;
        Collection<Player> players = hashClient.values();
        ObjectOutputStream out;
        for(Player p: players){
            message = new GenericTurnMessage(serverName,null,p.getPlayerDeck().getStarterCard(), null,null);
            out = new ObjectOutputStream(hashPlayer.get(p).getOutputStream());
            out.writeObject(message);
        }
    }*/
    //todo:funzione da avviare da match e inviare con sendMessage da ClientHandler


}

class ClientHandler extends Thread implements Runnable {
    private Socket socket;
    private boolean welcomeFlag = false;
    private boolean ackFlag = false;
    private ObjectOutputStream outClient;
    private ObjectInputStream inClient;
    private final String clientName;
    private final UUID clientID;

    public ClientHandler(String clientName,Socket socket,UUID clientID, ObjectOutputStream outFromServer, ObjectInputStream inToServer) throws IOException {
        this.clientName = clientName;
        this.socket = socket;
        this.outClient = outFromServer;
        this.inClient = inToServer;
        this.clientID = clientID;
    }

    @Override
    public void run() {
        while (ZakServer.gameStarted) {
        try {
                messageReceiver();
            } catch(IOException | ClassNotFoundException | WrongMessageConversionException e){
                System.out.println("ERRORE CLIENT HANDLER");
                e.getMessage();
            }
        try{
            if(!ZakServer.gameStarted && socket.isClosed()) throw new ClientAbruptlyDisconnectedException(clientName+" abruptly disconnected: Attempting reconnection");
        }catch(ClientAbruptlyDisconnectedException e){
                if(tryReconnectClient()) continue;
                //todo: reconnection attempt
                clientDisconnected();
            }
        }
    }
    private boolean tryReconnectClient(){
        boolean result=true;
        Pair<ObjectInputStream,ObjectOutputStream> oIOstream;
        try {
            oIOstream = ZakServer.acceptConnections(true);
            outClient= oIOstream.getValue();
            inClient = oIOstream.getKey();
        } catch (Exception e){
            result = false;
        }
        return result;
    }
    private void clientDisconnected(){
        ZakServer.hashPlayer.remove(ZakServer.hashClient.get(clientID));
        ZakServer.hashClient.remove(clientID);
        ZakServer.stopThread(clientID);
    }
    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inClient.readObject();
        Class<? extends Message> a = message.getClass();
        switch (a.getName()){
            case "GenericTurnMessage":
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStandardMessage":
                break;
            case "EndGameMessage":
                ZakServer.gameStarted = false;
                endOfTheGame((EndGameMessage)message);
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }
    public void sendMessage(Message message) throws IOException {
        outClient.writeObject(message);
    }
    private void textMessageHandler(TextMessage message) {
    }
    private void endOfTheGame(EndGameMessage message){

    }
}