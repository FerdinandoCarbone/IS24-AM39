package com.example.codexnaturalis;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    static ArrayList<Socket> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8098);
        System.out.println("Server started");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);
            // Gestisci la connessione in un thread separato
            new Thread(new ClientHandler(clientSocket)).start();
            if(Server.clients.size()%2==0){
                System.out.println("Server has now "+ Server.clients.size()+ " clients");
                for (Socket s: clients) {
                    s.getPort();
                }
            }

        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            Server.clients.add(clientSocket);
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    out.println("Server response: " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
