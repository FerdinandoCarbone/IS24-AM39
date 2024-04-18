package com.example.codexnaturalis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ZakServer {

    static HashMap<Player, Socket> hashPlayer = new HashMap<>();
    static HashMap<UUID, Socket> hashClient = new HashMap<>();
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
            while (firstPlayer || hashClient.size()<=numPlayers) {
                acceptConnections();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("PROBLEMA SERVER");
        }

    }
    public static void acceptConnections() throws IOException, ClassNotFoundException {
        ObjectOutputStream out;
        ObjectInputStream in;
        Message clientJoinRequest;
        LobbyCreationMessage handshakeACK;
        Socket clientSocket = serverSocket.accept();
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        clientJoinRequest = (Message) in.readObject();
        if (!firstPlayer) {
            firstPlayer=true;
            handshakeACK = new LobbyCreationMessage(serverName,null,numPlayers);
            out.writeObject(handshakeACK);
            handshakeACK = (LobbyCreationMessage) in.readObject();
            numPlayers = handshakeACK.getNumPlayer();
        }
        else{
            handshakeACK = new LobbyCreationMessage(serverName,null, hashClient.size());
            out.writeObject(handshakeACK);
        }
        if(hashClient.size()<=numPlayers){
            hashClient.put(clientJoinRequest.getClientID(),clientSocket);
            System.out.println(clientJoinRequest.getSender() + " si è unito al server");
            new Thread(new ClientHandler(handshakeACK.getSender(),clientSocket)).start();
        }
    }
    public static void serverStart(int port) throws IOException {
        numPlayers = 0;
        counterAcks = 0;
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
        if (counterAcks == numPlayers) {
            System.out.println("CHECK ACKS");
            matchReady = true;
        }

        if (matchReady) {
            System.out.println("Tutti i giocatori sono pronti");
            System.out.println("La partita sta per cominciare...");

            ArrayList<Player> players = new ArrayList<>(hashPlayer.keySet());

            match = new Match(players, new ScoreTracker());
            match.startMatch();
        }

    }

}

class ClientHandler implements Runnable {
    private Socket socket;
    private boolean welcomeFlag = false;
    private boolean ackFlag = false;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    String clientName;
    Player player;

    public ClientHandler(String clientName,Socket socket) throws IOException {
        this.clientName = clientName;
        this.socket = socket;
        this.player = new Player(new Token(), new Field(5, 5));
        ZakServer.hashPlayer.put(player, socket);
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            do {
                if (!welcomeFlag) {
                    welcomePlayer();
                }
                if (!ackFlag) {
                    readyToPlay();
                }



            } while (true);

        } catch (Exception e) {
            System.out.println("ERRORE CLIENT HANDLER");
        }
    }

    public void welcomePlayer() throws IOException {
        System.out.println(clientName + " aggiunto ai giocatori");
        //out.println("Sei stato aggiunto ai giocatori");
        welcomeFlag = true;
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
