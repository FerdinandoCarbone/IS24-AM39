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
    static NewMatch match;

    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(8081)) {

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
                System.out.println("Connessione accettata");
            }

        } catch (IOException e) {
            System.out.println("PROBLEMA SERVER");
        }

    }

}

class ClientHandler extends Thread {
    private Socket socket;
    private boolean welcomeFlag = false;
    private boolean ackFlag = false;
    String clientName;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream(), true);

            String clientResponse;
            int clientReady;

            do {

                if (!ZakServer.firstPlayer) {
                    ZakServer.firstPlayer = true;
                    outputToClient.println("Quanti giocatori ci saranno ?");
                    ZakServer.numPlayers = Integer.parseInt(inputFromClient.readLine());
                }
                if (!welcomeFlag) {
                    welcomePlayer(inputFromClient, outputToClient);
                }
                if (!ackFlag) {
                    readyToPlay(inputFromClient, outputToClient);
                }
                if (ZakServer.counterAcks == ZakServer.numPlayers && !ZakServer.matchReady) {
                    ZakServer.matchReady = true;
                    System.out.println("Tutti i giocatori sono pronti");
                    System.out.println("La partita sta per cominciare...");
                    ZakServer.match = new NewMatch(ZakServer.players, new ScoreTracker());
                    ZakServer.match.startMatch();
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
        clientResponse = in.readLine();
        clientName = clientResponse;
        clientName = clientResponse;
        ZakServer.players.add(new Player(clientResponse, new Token(), new Field(5, 5)));
        System.out.println(clientResponse + " aggiunto ai giocatori");
        out.println("Sei stato aggiunto ai giocatori");
        welcomeFlag = true;
    }

    public void readyToPlay(BufferedReader in, PrintWriter out) throws IOException {
        int clientReady;
        out.println("Sei pronto a giocare ? 1) -> si | 0) -> no");
        clientReady = Integer.parseInt(in.readLine());
        if (clientReady == 1) {
            System.out.println(clientName + " è pronto a giocare");
            ZakServer.counterAcks++;
            ackFlag = true;
        }
    }

}
