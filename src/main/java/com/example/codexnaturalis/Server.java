package com.example.codexnaturalis;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    static HashMap<Socket, Player> clients = new HashMap<>();
    private static ArrayList<Match> matches = new ArrayList<>();
    private static int playerAcks;

    //private static boolean situationChanged;
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8098);
        playerAcks = 0;
        //situationChanged = false;
        System.out.println("Server started");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);
            // Gestisci la connessione in un thread separato
            new Thread(new ClientHandler(clientSocket)).start();
            /*if(Server.clients.size()%2==0){
                System.out.println("Server has now "+ Server.clients.size()+ " clients");
                /*for (Socket s: clients) {
                    s.getPort();
                }
            }*/
            while (playerAcks != clients.size() && playerAcks>=2) {
                try {
                    clients.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (playerAcks == clients.size() && playerAcks >= 2) {
                startMatch();
            }

        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private String inputLine;
        private boolean flagReply = false;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)

            ) {
                printGreetings(out);
                Server.createPlayerInstance(in.readLine(), clientSocket);
                while (true) {
                        System.out.println("Sono qui");
                    synchronized(clients) {
                        if(clients.size() >= 2 && flagReply == false) {
                            this.flagReply = getAck(out, in);
                        }
                    }
                    clients.notifyAll();
                    System.out.println("Received from client: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " " + " Message: " + inputLine);
                    out.println("Server response: " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void printGreetings(PrintWriter out) {
            out.println("Ciao giocatore, benvenuto in CodexNaturalis. Sviluppato dal Team AM39. Inserisci il tuo nome utente: ");
        }
    }

    private static synchronized void createPlayerInstance(String playerName, Socket clientSocket) throws IOException {
        Field playerField = new Field(5, 5);
        //TODO: Check Nickname già esistente
        //synchronized (Server.clients) {
        Player player = new Player(playerName, new Token(), playerField);
        Server.clients.put(clientSocket, player);
        if (Server.clients.size() == 1) player.setFirstPlayer(true);
        System.out.println("Il player " + player.getPlayerName() + " è stato creato");

        //}
        //Server.clients.notifyAll();
    }

    private static boolean getAck(PrintWriter out,BufferedReader in) throws IOException {
        out.println("Vuoi iniziare la partita con " + clients.size() + " giocatori? 1: si | 0: no");
        String inputLine = in.readLine();
        int reply = Integer.parseInt(inputLine);
        playerAcks += reply;
        return reply == 1;
    }
    private static void startMatch(){
        matches.add(new Match(new ScoreTracker()));
        for (Player p: clients.values()) {
            matches.getLast().addPlayer(p);
        }
        //matches.getLast().startMatch();
    }
}