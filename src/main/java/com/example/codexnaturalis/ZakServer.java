package com.example.codexnaturalis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ZakServer {

    static ArrayList<Player> players = new ArrayList<>();
    static int numPlayers = -1;
    static int counterAcks = 0;
    static boolean firstPlayer = false;
    static boolean matchReady = false;
    static Match match;

    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(8081)) {

            while (true) {


                if (!firstPlayer) {
                    System.out.println("Aspettando la prima connessione");
                    firstPlayer = true;
                    Socket firstClientSocket = serverSocket.accept();
                    System.out.println("Connessione accettata");
                    BufferedReader in = new BufferedReader(new InputStreamReader(firstClientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(firstClientSocket.getOutputStream(), true);

                    out.println("Quanti giocatori ci saranno ?");
                    out.println("INPUT");
                    numPlayers = Integer.parseInt(in.readLine());
                    new Thread(new ClientHandler(firstClientSocket)).start();
                }

                while (players.size() != numPlayers) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connessione accettata");
                    new Thread(new ClientHandler(clientSocket)).start();
                }

            }

        } catch (IOException e) {
            System.out.println("PROBLEMA SERVER");
        }

    }

    public static void checkStart() throws IOException {
        if (counterAcks == numPlayers) {
            System.out.println("CHECK ACKS");
            matchReady = true;
        }

        if (matchReady) {
            System.out.println("Tutti i giocatori sono pronti");
            System.out.println("La partita sta per cominciare...");

            match = new Match(players, new ScoreTracker());
            match.startMatch();
        }

    }

}

class ClientHandler implements Runnable {
    private Socket socket;
    private boolean welcomeFlag = false;
    private boolean ackFlag = false;
    String clientName;
    Player player;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.player = new Player(socket, new Token(), new Field(5, 5));
        ZakServer.players.add(player);
    }

    @Override
    public void run() {
        try {
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true);

            do {
                if (!welcomeFlag) {
                    welcomePlayer(inputFromClient, outputToClient);
                }
                if (!ackFlag) {
                    readyToPlay(inputFromClient, outputToClient);
                }



            } while (true);

        } catch (IOException e) {
            System.out.println("ERRORE CLIENT HANDLER");
        }
    }

    public void welcomePlayer(BufferedReader in, PrintWriter out) throws IOException {
        String clientResponse;
        out.println("Benvenuto in CodexNaturalis");
        out.println("Inserisci il nome utente che userai per la partita: ");
        out.println("INPUT");
        clientResponse = in.readLine();
        clientName = clientResponse;
        player.setPlayerName(clientName);
        System.out.println(clientResponse + " aggiunto ai giocatori");
        out.println("Sei stato aggiunto ai giocatori");
        welcomeFlag = true;
    }

    public void readyToPlay(BufferedReader in, PrintWriter out) throws IOException {
        int clientReady;
        out.println("Sei pronto a giocare ? 1) -> si | 0) -> no");
        out.println("INPUT");
        clientReady = Integer.parseInt(in.readLine());
        if (clientReady == 1) {
            System.out.println(clientName + " è pronto a giocare");
            ZakServer.counterAcks++;
            ackFlag = true;
            ZakServer.checkStart();
        }
    }

}