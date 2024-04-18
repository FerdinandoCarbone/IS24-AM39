package com.example.codexnaturalis;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class ZakClient {

    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String playerNick;
    static Socket socket;
    static UUID clientID;
    public static void main(String[] args) {

        final int port = Integer.parseInt(args[1]);
        final String serverAddress = args[0];
        playerNick = playerGreeting() ;

        try {
            initialClientSetup(serverAddress,port);
        }catch(IOException | ClassNotFoundException | StupidUserException | HandShakeException e){
            System.out.println("Unable to establish a connection with server");
        }
        //todo: GenericMessage Assembler


    }

    private static void startHandshake() throws IOException, ClassNotFoundException, HandShakeException, StupidUserException {
        Message handshakeMessage = new Message(playerNick,clientID);
        out.writeObject(handshakeMessage);
        try{
            LobbyCreationMessage handshakeACK = (LobbyCreationMessage) in.readObject();
            switch(handshakeACK.numPlayer) {
                case 0:
                    lobbyCreation(handshakeACK);
                     break;
                case 1,2,3:
                    System.out.println("Joined existing match...");
                    System.out.println("Waiting for everyone to join.");
                    break;
                default:
                    throw new TooManyPlayersException("Lobby is currently full. Wait for the match to end and try again");
            }
        } catch(ClassNotFoundException e){
            e.getMessage();
        }
    }
    private static void lobbyCreation(LobbyCreationMessage msg) throws IOException, HandShakeException, StupidUserException {
        int desiredPlayerCount = 0;
        int i;
        System.out.println("No match found. Creating a new one:\nHow many players will be playing?\nWrite a value between 2 and 4:");
        try(Scanner scanner = new Scanner(System.in);) {
            for (i = 0; i<3; i++) {
                desiredPlayerCount = Integer.parseInt(scanner.nextLine());
                if (desiredPlayerCount >= 2 && desiredPlayerCount <= 4) break;
                else  System.out.println("Unacceptable value was input.\nWrite a number between 2 and 4: ");
                if (i == 2) throw new StupidUserException("u stupid bruh");
            }
        } catch (NumberFormatException e){
            e.getMessage();
            throw new StupidUserException("Unacceptable value was input.\nWrite a number between 2 and 4");
        } catch (StupidUserException e) {
            e.getMessage();
            throw new HandShakeException("Something went wrong during connection");
        } finally{
            msg.setNumPlayer(desiredPlayerCount);
            msg.setSender(playerNick);
            msg.setClientID(clientID);
            out.writeObject(msg);
        }
    }
    private static void initialClientSetup(String serverAddress,int port) throws IOException, ClassNotFoundException, StupidUserException, HandShakeException {
        clientID = UUID.randomUUID();
        socket  = new Socket(serverAddress, port);
        // OutputStream
        out = new ObjectOutputStream(socket.getOutputStream());
        // Ora leggi la risposta dal server
        in = new ObjectInputStream(socket.getInputStream());
        // Inizializza la connessione
        startHandshake();
    }
    private static String playerGreeting(){
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome Player to:");
        System.out.println("\n" +
                "\n" +
                " _____                                                              _____ \n" +
                "( ___ )------------------------------------------------------------( ___ )\n" +
                " |   |                                                              |   | \n" +
                " |   |   .-._   .-._.    .                                          |   | \n" +
                " |   | ..' (_)`-'        /    `--.  .-.                             |   | \n" +
                " |   | |      .-._..-../   .-.   \\/                                 |   | \n" +
                " |   | |    _(   )(   /  ./.-'_  /\\                                 |   | \n" +
                " |   | `.    )`-'  `-'-..(__.'.-'  `-.                              |   | \n" +
                " |   |   `--'.-.                                     .              |   | \n" +
                " |   |         /  |         /                       /    .-.        |   | \n" +
                " |   |        /\\  | .-. ---/---)  (   ).--..-.     /     `-' .      |   | \n" +
                " |   |       /  \\ |(  |   /   (    ) /    (  |    /     /   / \\     |   | \n" +
                " |   |  .-' /    \\| `-'-'/     `--':/      `-'-'_/_.-_.(__./ ._)    |   | \n" +
                " |   | (__.'      `.                                      /         |   | \n" +
                " |___|                                                              |___| \n" +
                "(_____)------------------------------------------------------------(_____)\n" +
                "\n");
        System.out.println("Please enter your nickname:");
        return in.nextLine();
    }

    static boolean validStrings(String s) {
        return s.equals("INPUT");
        /*if (s.equals("Inserisci il nome utente che userai per la partita: ") ||
            s.equals("Sei pronto a giocare ? 1) -> si | 0) -> no") ||
            s.equals("Quanti giocatori ci saranno ?") ||
            s.equals("Seleziona se vuoi piazzare la carta iniziale di fronte o retro: 1) -> Fronte | 0) -> Retro") ||
            s.equals("2) Analizza il tavolo") ||
            s.equals("Seleziona la riga della carta a cui vuoi attaccarti:") ||
            s.equals("Seleziona la colonna della carta a cui vuoi attaccarti:") ||
            s.equals("Seleziona l'angolo della carta sul tavolo a cui vuoi attaccarti (a partire da in alto a dx in senso orario 0->3): ") ||
            s.equals("2) Mazzo Oro") ||
            s.equals("Scegli la riga della carta che vuoi analizzare: ") ||
            s.equals("Scegli la colonna della carta che vuoi analizzare: ") ||
            s.equals("Seleziona il numero della carta che vuoi piazzare: ") ||
            s.equals("Seleziona se vuoi piazzarla di fronte o retro: 1) -> Fronte | 0) -> Retro")) {
            flag = true;
        }

        return flag;*/
    }

}


