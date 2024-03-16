package com.example.codexnaturalis;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Server {
    static HashMap<Socket,Player> clients = new HashMap<>();
    static ArrayList<Integer> totalAvailableColors = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        totalAvailableColors = (ArrayList<Integer>) Arrays.asList( new Integer[]{2, 2, 2, 2, 1});
        ServerSocket serverSocket = new ServerSocket(8098);
        System.out.println("Server started");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);
            // Gestisci la connessione in un thread separato
            new Thread(new ClientHandler(clientSocket)).start();
            if(Server.clients.size()%2==0){
                /*System.out.println("Server has now "+ Server.clients.size()+ " clients");
                /*for (Socket s: clients) {
                    s.getPort();
                }*/
            }

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
                createPlayerInstance(in.readLine());

                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: "+ clientSocket.getInetAddress()+":"+clientSocket.getPort()+" " + " Message: " + inputLine);
                    out.println("Server response: " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private Player createPlayerInstance(String playerName){
            Field playerField=new Field(5,5);
            //TODO: Bloccare la risorsa per evitare accesso parallelo
            Token.Color tokenColor;
            Integer setElem;
            if(Server.clients.size()==1) {
                tokenColor = Token.Color.Black;
                totalAvailableColors.removeLast();
            }
            else{
                Random rand = new Random();
                int z = rand.nextInt(3);
                switch(rand){
                    case 1: {
                        setElem = totalAvailableColors.get(z);
                        setElem--;
                        totalAvailableColors.set(4,setElem);
                        break;
                    }
                    case 2: {
                        totalAvailableColors[2]--;
                        break;
                    }
                    case 3: {
                        totalAvailableColors[2]--;
                        break;
                    }
                    default:
                }
                setElem = totalAvailableColors.get(4);
                setElem--;
                totalAvailableColors.set(4,setElem);
            }
            Player player = new Player(playerName,new Token(tokenColor),playerField);
            Server.clients.put(clientSocket,player);
            return player ;
        }
        private void printGreetings(PrintWriter out){
            out.println("Ciao giocatore, benvenuto in CodexNaturalis.\nSviluppato dal Team AM39\n\nInserisci il tuo nome utente: ");
        }
    }
}
