package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ZakServer {
    protected static HashMap<Player, Socket> hashPlayer = new HashMap<>();
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
        System.out.println("Match is about to start");
        String serverCommand;
        ArrayList<Player> players = new ArrayList<>(hashPlayer.keySet());
        match = new Match(players, new ScoreTracker());
        startingFieldClientSetup();
        welcomePlayer();
        gameStarted = true;
        while(gameStarted){
            serverCommand = getInput();
            interpreteInput(serverCommand);
        }
    }
    private static String getInput(){
        Scanner scanner= new Scanner(System.in);
        String input=null;
        do{
            System.out.print("Command: ");
            try{
                input = scanner.nextLine();
            }catch (NoSuchElementException e){
                continue;
            }
        }while(Objects.equals(input, "\n") || input==null);
        //scanner.close();
        return input;
    }

    private static void interpreteInput(String serverCommand) {
        switch(serverCommand.toLowerCase()){
            case "close":
                System.out.println("Server shutting down");
                System.exit(0);
                break;
            case "ban":
                break;
            case "restart":
                break;
            default: System.out.println("Unknown command");
        }
    }
    private void receiveInput(){

    }
    private static void startingFieldClientSetup() throws IOException{
        //todo: da spostare in match probabilmente
        BroadCastStartingMessage fieldSetupMessage;
        ArrayList<ObjectiveCard> commonObjectiveCard;
        commonObjectiveCard = DrawingDeck.getCommonObjective();
        match.setCommonObjectives(commonObjectiveCard);
        System.out.println("CommonObjectiveCards:");
        for(ObjectiveCard oc: commonObjectiveCard) oc.printCardAscii();
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
        sendBroadCastMessage(new TextMessage(ZakServer.serverName,null,text,"Everyone"));
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
    public static void sendMessage(UUID clientID,Message message) throws IOException {
        handlers.get(clientID).sendMessage(message);
    }

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
        do{
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
        }while (true);
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

    public void sendMessage(Message message) throws IOException {
        if(!(message instanceof TextMessage)) {
            message.setClientID(clientID);
            message.setSender(clientName);
        }
        outClient.writeObject(message);
    }
    private void textMessageHandler(TextMessage message) throws IOException {
        UUID recipientClientID=null;
        String recipient = message.getRecipient();
        //System.out.println(recipient+" "+ recipientClientID);
        if(Objects.equals(recipient, "Everyone")) ZakServer.sendBroadCastMessage(message);
         else{
             for(Player p:ZakServer.hashClient.values()){
                if(Objects.equals(p.getPlayerName(), recipient)) {
                    recipientClientID = p.getPlayerID();
                    break;
                }
             }
            ZakServer.sendMessage(recipientClientID,message);
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