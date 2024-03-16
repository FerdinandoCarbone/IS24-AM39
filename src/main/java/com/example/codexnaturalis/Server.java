package com.example.codexnaturalis;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Server {
    static HashMap<Socket,Player> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8098);
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

        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private String inputLine;
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

                Server.createPlayerInstance(in.readLine(),clientSocket);

                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: "+ clientSocket.getInetAddress()+":"+clientSocket.getPort()+" " + " Message: " + inputLine);
                    out.println("Server response: " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void printGreetings(PrintWriter out){
            out.println("Ciao giocatore, benvenuto in CodexNaturalis. Sviluppato dal Team AM39. Inserisci il tuo nome utente: ");
        }
    }
    private static synchronized void createPlayerInstance(String playerName, Socket clientSocket) throws IOException {
        Field playerField=new Field(5,5);
        //TODO: Check Nickname già esistente
        //synchronized (Server.clients) {
            Player player = new Player(playerName, new Token(), playerField);
            Server.clients.put(clientSocket, player);
            if(Server.clients.size()==1) player.setFirstPlayer(true);
            System.out.println("Il player "+ player.getPlayerName() + " è stato creato");
        //}
        //Server.clients.notifyAll();
    }
}
