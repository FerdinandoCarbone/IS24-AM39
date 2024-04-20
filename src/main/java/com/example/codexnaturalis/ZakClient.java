package com.example.codexnaturalis;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ZakClient {

    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String playerNick;
    private static Player player;
    private static ArrayList<Field> otherFields;
    private static boolean currentGameStatus;
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
        try{
            gameStart();
        } catch(IOException | ClassNotFoundException | WrongMessageConversionException e){
            e.getMessage();
        }

    }

    private static void startHandshake() throws IOException, ClassNotFoundException, HandShakeException, StupidUserException {
        Message handshakeMessage = new Message(playerNick,clientID);
        out.writeObject(handshakeMessage);
        try{
            LobbyCreationMessage handshakeACK = (LobbyCreationMessage) in.readObject();
            System.out.println("CurrentPlayers: "+handshakeACK.getNumPlayer());
            switch(handshakeACK.getNumPlayer()) {
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
            System.out.println("Number of players:"+desiredPlayerCount);
        }
    }
    private static void initialClientSetup(String serverAddress,int port) throws IOException, ClassNotFoundException, StupidUserException, HandShakeException {
        clientID = UUID.randomUUID();
        socket  = new Socket(serverAddress, port);
        currentGameStatus = false;
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
    private static void initialMatchSetup() throws IOException, ClassNotFoundException {
        BroadCastStartingMessage initialMatchSetupMessage;
        initialMatchSetupMessage = (BroadCastStartingMessage)in.readObject();
        try{
            Collection<Player> players;
            player = initialMatchSetupMessage.getPlayers().get(clientID);
            if(player == null) throw new WrongPlayerUUIDException("There was an error retrieving the info about the match");
            initialMatchSetupMessage.getPlayers().remove(clientID);
            players=initialMatchSetupMessage.getPlayers().values();
            for(Player p:players) otherFields.add(p.getPlayerField());
        } catch (WrongPlayerUUIDException e){
            e.getMessage();
        }
    }
    private static void gameStart() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        currentGameStatus = true;
        initialMatchSetup();
        //todo: GenericMessage Assembler
        genericMessageAssembler();

    }
    private static void genericMessageAssembler() throws IOException, WrongMessageConversionException, ClassNotFoundException{
        while(currentGameStatus){
            messageReceiver();
        }
    }
    private static void endOfTheGame(EndGameMessage message) throws IOException {
        String winner = message.getWinner();
        System.out.println(winner+" ha vinto la partita\nGrazie per aver giocato\n");
        in.close();
        out.close();
    }
    private static void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) in.readObject();
        Class a = message.getClass();
        switch (a.getName()){
            case "GenericTurnMessage":
            case "TextMessage":
                textMessageHandler((TextMessage) message);
            case "BroadCastStandardMessage":
            case "EndGameMessage":
                currentGameStatus = false;
                endOfTheGame((EndGameMessage)message);
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }

    private static void textMessageHandler(TextMessage message) {
        System.out.println(message.getSender()+": "+message.getTextMessage());
    }

    public static void setOtherFields(ArrayList<Field> otherFields) {
        ZakClient.otherFields = otherFields;
    }
}


