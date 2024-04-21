package com.example.codexnaturalis;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class ZakClient {

    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static ServerComHandler serverComHandler;
    private static String playerNick;
    private static Player player;
    private static ArrayList<Field> otherFields;
    private static boolean currentGameStatus;
    private static boolean myTurn;
    static Socket socket;
    static UUID clientID;
    public static void main(String[] args) {

        final int port = Integer.parseInt(args[1]);
        final String serverAddress = args[0];
        playerNick = playerGreeting() ;

        try {
            initialClientSetup(serverAddress,port);
        }catch(IOException | ClassNotFoundException | StupidUserException | HandShakeException e){
            e.getMessage();
            throw new RuntimeException("Please restart the client and try again");
        }
        try{
            //clearConsole();
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
            System.out.println("CurrentPlayers: "+(handshakeACK.getNumPlayer()));
            serverComHandler = new ServerComHandler(playerNick,clientID,out,in);
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
        int retryCount = 0;
        clientID = UUID.randomUUID();
        currentGameStatus = false;
        myTurn = false;
        otherFields= new ArrayList<>();
        while(true) {
            try {
                socket = new Socket(serverAddress, port);
                break;
            } catch (IOException e) {
                System.err.println("Unable to connect to the server: Trying to reconnect in 3s");
                retryCount++;
                if (retryCount >= 3) throw new HandShakeException("Unable to connect to the server: Host may be down");
            }
            try {
                Thread.sleep(5000); // Aspetta 5 secondi prima di tentare di riconnettersi
            } catch (InterruptedException ex) {
                // Gestisci l'eccezione
                ex.printStackTrace();
            }
        }
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
        //return in.nextLine();
        Random rand = new Random();
        Integer val = rand.nextInt(10);
        return val.toString();
    }
    private static void initialMatchSetup() throws IOException {
        BroadCastStartingMessage initialMatchSetupMessage = null;
        try{
            initialMatchSetupMessage = (BroadCastStartingMessage)in.readObject();
            if(initialMatchSetupMessage == null) throw new WrongMessageConversionException("Was not able to initialize Starting Field");
        } catch(ClassNotFoundException e){
            e.getMessage();
        } catch (WrongMessageConversionException e) {
            e.getMessage();
            throw new RuntimeException(e);
        }
        try{
            Collection<Player> players;
            player = initialMatchSetupMessage.getPlayers().get(clientID);
            if(player == null) throw new WrongPlayerUUIDException("There was an error retrieving the info about the match");
            initialMatchSetupMessage.getPlayers().remove(clientID);
            players=initialMatchSetupMessage.getPlayers().values();
            for(Player p:players) otherFields.add(p.getPlayerField());
            //todo:otherfields deve essere una hashmap con anche i players
        } catch (WrongPlayerUUIDException e){
            e.getMessage();
        }finally {
            System.out.println("All players' fields were correctly received");
            new Thread(serverComHandler).start();
        }
    }
    private static void gameStart() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        currentGameStatus = true;
        initialMatchSetup();
        //todo: GenericMessage Assembler
        while(currentGameStatus) possibleActions();
    }
    private static void genericMessageAssembler() throws IOException, WrongMessageConversionException, ClassNotFoundException{
        Message message = null;
        serverComHandler.sendMessage(message);
    }
    private static void possibleActions() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to do:\n");
        printPossibleChoices(myTurn);
        int action = Integer.parseInt(scanner.nextLine());
        switch (action){
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                System.out.println("Wrong input: Input the number associated to the desired action");
        }
    }
    private static void possibleActions(Message message) throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to do:\n");
        printPossibleChoices(myTurn);
        int action = Integer.parseInt(scanner.nextLine());
        switch (action){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                genericMessageAssembler();
            default:
                System.out.println("Wrong input: Input the number associated to the desired action");
        }
    }
    private static void printPossibleChoices(boolean myTurn){
        String choices = "\n" +
                "\n" +
                " _____                          _____ \n" +
                "( ___ )                        ( ___ )\n" +
                " |   |~~~~~~~~~~~~~~~~~~~~~~~~~~|   | \n" +
                " |   | [1] Other Players' Codex |   | \n" +
                " |___|~~~~~~~~~~~~~~~~~~~~~~~~~~|___| \n" +
                "(_____)                        (_____)\n" +
                "\n";
        choices.concat("\n" +
                "\n" +
                " _____                          _____ \n" +
                "( ___ )                        ( ___ )\n" +
                " |   |~~~~~~~~~~~~~~~~~~~~~~~~~~|   | \n" +
                " |   | [2] Show Objective Cards |   | \n" +
                " |___|~~~~~~~~~~~~~~~~~~~~~~~~~~|___| \n" +
                "(_____)                        (_____)\n" +
                "\n");
        String choice2 = "\n" +
                "\n" +
                " _____                        _____ \n" +
                "( ___ )                      ( ___ )\n" +
                " |   |~~~~~~~~~~~~~~~~~~~~~~~~|   | \n" +
                " |   | [3] Show personal deck |   | \n" +
                " |___|~~~~~~~~~~~~~~~~~~~~~~~~|___| \n" +
                "(_____)                      (_____)\n" +
                "\n";
        choice2.concat("\n" +
                "\n" +
                " _____                         _____ \n" +
                "( ___ )                       ( ___ )\n" +
                " |   |~~~~~~~~~~~~~~~~~~~~~~~~~|   | \n" +
                " |   | [4] Show personal Codex |   | \n" +
                " |___|~~~~~~~~~~~~~~~~~~~~~~~~~|___| \n" +
                "(_____)                       (_____)\n" +
                "\n");
        if(myTurn) clearConsole();
        System.out.println(choices+"\n"+choice2);
        if(myTurn) {
            System.out.println("\n" +
                    "\n" +
                    " _____                  _____ \n" +
                    "( ___ )                ( ___ )\n" +
                    " |   |~~~~~~~~~~~~~~~~~~|   | \n" +
                    " |   | [5] Play my Turn |   | \n" +
                    " |___|~~~~~~~~~~~~~~~~~~|___| \n" +
                    "(_____)                (_____)\n" +
                    "\n");
        }
    }

    public static void setOtherFields(ArrayList<Field> otherFields) {
        ZakClient.otherFields = otherFields;
    }
    public static void genericTurnMessageHandler(GenericTurnMessage message){
        myTurn=true;
        //todo: Aggiornamento dello stato dei fields dei deck del player
        System.out.println("It's your turn:");
    }
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) Runtime.getRuntime().exec("cls");
            else Runtime.getRuntime().exec("clear");

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isCurrentGameStatus() {
        return currentGameStatus;
    }
    public static void endOfTheGame() throws IOException {
        currentGameStatus=false;
        in.close();
        out.close();
    }
}
class ServerComHandler implements Runnable {
    private final String clientName;
    private final UUID clientID;
    private ObjectOutputStream outServer;
    private ObjectInputStream inServer;

    public ServerComHandler(String clientName, UUID clientID, ObjectOutputStream outFromServer, ObjectInputStream inFromServer) throws IOException {
        this.clientName = clientName;
        this.clientID = clientID;
        this.outServer = outFromServer;
        this.inServer = inFromServer;
    }

    @Override
    public void run() {
        try {
            while(ZakClient.isCurrentGameStatus()){
                messageReceiver();
            }

        } catch (Exception e) {
            System.out.println("ERRORE ServerCom HANDLER");
            e.getMessage();
        }
    }

    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inServer.readObject();
        Class<? extends Message> a = message.getClass();
        switch (a.getName()){
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
            case "TextMessage":
                textMessageHandler((TextMessage) message);
            case "BroadCastStandardMessage":
            case "EndGameMessage":
                endOfTheGame((EndGameMessage)message);
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }
    public void sendMessage(Message message) throws IOException {
        message.setSender(clientName);
        message.setClientID(clientID);
        outServer.writeObject(message);
    }
    private void textMessageHandler(TextMessage message) {
        System.out.println(message.getSender()+": "+message.getTextMessage());
    }
    private void genericTurnMessageHandler(GenericTurnMessage message){
        ZakClient.genericTurnMessageHandler(message);
    }
    private void endOfTheGame(EndGameMessage message) throws IOException {
        String winner = message.getWinner();
        System.out.println(winner+" ha vinto la partita\nGrazie per aver giocato\n");
        ZakClient.endOfTheGame();
    }
}

