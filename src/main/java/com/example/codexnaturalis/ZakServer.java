package com.example.codexnaturalis;

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
    static int numPlayers;
    static int counterAcks;
    static boolean firstPlayer = false;
    static boolean matchReady = false;
    static Match match;
    static ServerSocket serverSocket;
    static String serverName;
    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);


        try{
            serverStart(port);
            while (hashClient.size()<numPlayers) {
                acceptConnections();
            }
        } catch (IOException | ClassNotFoundException   e) {
            System.out.println("PROBLEMA SERVER: "+e.getMessage());
        }
        try{
            checkStart();
        }catch(Exception e){
            e.getMessage();
        }

    }
    public static void acceptConnections() throws IOException, ClassNotFoundException {
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
        }
        else{
            handshakeACK = new LobbyCreationMessage(serverName,null, hashClient.size());
            out.writeObject(handshakeACK);
        }
        if(hashClient.size()<=numPlayers){
            player = new Player(new Token(), new Field(5, 5));
            hashPlayer.put(player, clientSocket);
            hashClient.put(clientJoinRequest.getClientID(),player);
            System.out.println(clientJoinRequest.getSender() + " si è unito al server");
            new Thread(new ClientHandler(handshakeACK.getSender(),clientSocket,clientJoinRequest.getClientID())).start();
        }
    }
    public static void serverStart(int port) throws IOException {
        numPlayers = 0;
        serverSocket = new ServerSocket(port);
        serverName = "SERVER";
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
    public static void checkStart() throws Exception {
        System.out.println("Tutti i giocatori sono pronti");
        System.out.println("La partita sta per cominciare...");
        ArrayList<Player> players = new ArrayList<>(hashPlayer.keySet());
        match = new Match(players, new ScoreTracker());
        startingFieldClientSetup();
        match.startMatch();


    }
    private static void startingFieldClientSetup() throws IOException{
        BroadCastStartingMessage fieldSetupMessage;
        ArrayList<ObjectiveCard> commonObjectiveCard;
        commonObjectiveCard = DrawingDeck.getCommonObjective();
        match.setCommonObjectives(commonObjectiveCard);
        fieldSetupMessage = new BroadCastStartingMessage(ZakServer.serverName,null,ZakServer.hashClient,commonObjectiveCard);
        sendBroadCastMessage(fieldSetupMessage);
        sendStartingCards();

    }
    private static void sendStartingCards() throws IOException {
        GenericTurnMessage message;
        Collection<Player> players = hashClient.values();
        ObjectOutputStream out;
        for(Player p: players){
            message = new GenericTurnMessage(serverName,null,p.getPlayerDeck().getStarterCard(), null,null);
            out = new ObjectOutputStream(hashPlayer.get(p).getOutputStream());
            out.writeObject(message);
        }
    }
    private static void sendBroadCastMessage(Message message) throws IOException {
        Collection<Socket> clientsSockets = hashPlayer.values();
        ObjectOutputStream out;
        for(Socket s: clientsSockets){
            out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(message);
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private boolean welcomeFlag = false;
    private boolean ackFlag = false;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private String clientName;

    public ClientHandler(String clientName,Socket socket,UUID clientID) throws IOException {
        this.clientName = clientName;
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            welcomePlayer();
            do {
                if (!ackFlag) {
                    startingFieldSetup();
                }
            } while (true);

        } catch (Exception e) {
            System.out.println("ERRORE CLIENT HANDLER");
        }
    }

    public void welcomePlayer() throws IOException {


    }

    public void readyToPlay() throws Exception {
        int clientReady;
        //out.println("Sei pronto a giocare ? 1) -> si | 0) -> no");
        try {
            clientReady = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("NOT A NUMBER");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (clientReady == 1) {
            System.out.println(clientName + " è pronto a giocare");
            ZakServer.counterAcks++;
            ackFlag = true;
            ZakServer.checkStart();
        }
    }

}
