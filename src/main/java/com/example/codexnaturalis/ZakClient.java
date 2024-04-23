package com.example.codexnaturalis;

import javafx.scene.text.TextBoundsType;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class ZakClient {

    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static ServerHandler serverComHandler;
    private static String playerNick;
    private static Player player;
    private static ArrayList<Player> otherPlayers;
    private static boolean currentGameStatus;
    private static boolean myTurn;
    static int port;
    static String serverAddress;
    static Socket socket;
    static UUID clientID;
    public static void main(String[] args) {

        port = Integer.parseInt(args[1]);
        serverAddress = args[0];

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
            serverComHandler = new ServerHandler(playerNick,clientID,out,in);
        } catch(ClassNotFoundException e){
            e.getMessage();
        }
    }
    private static void lobbyCreation(LobbyCreationMessage msg) throws IOException, HandShakeException, StupidUserException {
        int desiredPlayerCount = 0;
        int i;
        System.out.println("No match found. Creating a new one:\nHow many players will be playing?\nWrite a number between 2 and 4:");
        try{
            for (i = 0; i<3; i++) {
                desiredPlayerCount = Integer.parseInt(receiveInput());
                if (desiredPlayerCount >= 2 && desiredPlayerCount <= 4) break;
                System.out.println("Unacceptable value was input.\nWrite a number between 2 and 4: ");
                if (i == 2) throw new StupidUserException("u stupid bruh");
            }
        } catch (NumberFormatException e){
            throw new StupidUserException("Unacceptable value was input.\nWrite a number between 2 and 4");
        } catch (StupidUserException e) {
            e.getMessage();
            throw new HandShakeException("Something went wrong during connection");
        } finally{
            msg.setNumPlayer(desiredPlayerCount);
            msg.setSender(playerNick);
            msg.setClientID(clientID);
            out.writeObject(msg);
            System.out.println("Desired number of players:"+desiredPlayerCount);
        }
    }
    private static void initialClientSetup(String serverAddress,int port) throws IOException, ClassNotFoundException, StupidUserException, HandShakeException {
        clientID = UUID.randomUUID();
        currentGameStatus = false;
        myTurn = false;
        otherPlayers= new ArrayList<>();
        playerNick = playerGreeting();
        try {
            socket = connectionAttempt(serverAddress, port);
        } catch(HandShakeException e){
            e.getMessage();
        }
        out = new ObjectOutputStream(socket.getOutputStream());
        // Ora leggi la risposta dal server
        in = new ObjectInputStream(socket.getInputStream());
        // Inizializza la connessione
        startHandshake();
    }

    public static Socket connectionAttempt(String serverAddress,int port) throws HandShakeException {
        int retryCount = 0;
        Socket socket;
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
        return socket;
    }

    private static String playerGreeting(){
        //Scanner input = new Scanner(System.in);
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
        return receiveInput();
        /*Random rand = new Random();
        Integer val = rand.nextInt(10);
        return val.toString();*/
    }
    public static void initialMatchSetup(BroadCastStartingMessage initialMatchSetupMessage) throws IOException {
        try{
            Collection<Player> players;
            player = initialMatchSetupMessage.getPlayers().get(clientID);
            if(player == null) throw new WrongPlayerUUIDException("There was an error retrieving the info about the match");
            initialMatchSetupMessage.getPlayers().remove(clientID);
            players=initialMatchSetupMessage.getPlayers().values();
            for(Player p:players) otherPlayers.add(p);
            //todo:otherfields deve essere una hashmap con anche i players
        } catch (WrongPlayerUUIDException e){
            e.getMessage();
        }  finally {
            System.out.println("All players' fields were correctly received");

        }
    }
    private static void gameStart() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        currentGameStatus = true;
        new Thread(serverComHandler).start();
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //todo: GenericMessage Assembler
        while(currentGameStatus) selectPossibleActions();
    }
    private static void genericMessageAssembler() throws IOException, WrongMessageConversionException, ClassNotFoundException{
        Message message = null;
        serverComHandler.sendMessage(message);
    }
    private static void selectPossibleActions() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        System.out.println("What would you like to do:\n");
        printPossibleChoices(myTurn);
        int action = Integer.parseInt(receiveInput());
        clearConsole();
        switch (action){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                writeTextMessage();
                break;
            default:
                System.out.println("Wrong input: Input the number associated to the desired action");
        }
    }

    private static void writeTextMessage() throws IOException {
        TextMessage chatMessage;
        String recipient;
        String text;
        HashMap<Integer,Player> recipientChooser=new HashMap<>();
        int i=1;
        System.out.println("Who do you want to send the message to?");
        System.out.println(0 + " - Cancel");
        for(Player p: otherPlayers){
            recipientChooser.put(i,p);
            System.out.println(i+" - "+p.getPlayerName());
            ++i;
        }
        System.out.println(i + " - Everyone");
        int counter = recipientChooser.size()+1;
        do{
            i=Integer.parseInt(receiveInput());
            if(i<=counter&&i>0){
                text = receiveInput();
                recipient = recipientChooser.get(i).getPlayerName();
                if(i==counter) serverComHandler.sendMessage(new TextMessage(playerNick,clientID,text,recipient));
                else serverComHandler.sendMessage(new TextMessage(playerNick,clientID,text,"Everyone"));
                break;
            }
            else if(i==0) break;
            System.out.println("Invalid input: please select a valid option");
        }while(true);
    }

    private static void selectPossibleActions(Message message) throws IOException, ClassNotFoundException, WrongMessageConversionException {
        System.out.println("What would you like to do:\n");
        printPossibleChoices(myTurn);
        int action = Integer.parseInt(receiveInput());
        clearConsole();
        switch (action){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                writeTextMessage();
                break;
            case 6:
                //todo: match updater
                genericMessageAssembler();
                break;
            default:
                System.out.println("Wrong input: Input the number associated to the desired action");
        }
    }
    private static void printPossibleChoices(boolean myTurn) {
        String choices;
        //if(myTurn) clearConsole();
        //todo: Add ASCII art to write a TextMessage
        if (!myTurn){
            choices = "\n" +
                "\n" +
                " _____                           _____ \n" +
                "( ___ )                         ( ___ )\n" +
                " |   |~~~~~~~~~~~~~~~~~~~~~~~~~~~|   | \n" +
                " |   | [1] Other Players' Codex  |   | \n" +
                " |   | [2] Show Objective Cards  |   | \n" +
                " |   | [3] Show personal deck    |   | \n" +
                " |   | [4] Show personal Codex   |   | \n" +
                " |   | [5] Write to chat         |   | \n" +
                " |___|~~~~~~~~~~~~~~~~~~~~~~~~~~~|___| \n" +
                "(_____)                         (_____)\n" +
                "\n";
    }
        else {
            choices = "\n" +
                    "\n" +
                    " _____                           _____ \n" +
                    "( ___ )                         ( ___ )\n" +
                    " |   |~~~~~~~~~~~~~~~~~~~~~~~~~~~|   | \n" +
                    " |   | [1] Other Players' Codex  |   | \n" +
                    " |   | [2] Show Objective Cards  |   | \n" +
                    " |   | [3] Show personal deck    |   | \n" +
                    " |   | [4] Show personal Codex   |   | \n" +
                    " |   | [5] Write to chat         |   | \n" +
                    " |   | [6] Play turn             |   | \n" +
                    " |___|~~~~~~~~~~~~~~~~~~~~~~~~~~~|___| \n" +
                    "(_____)                         (_____)\n" +
                    "\n";
        }
        System.out.println(choices);
    }
    private static String receiveInput(){
        Scanner scanner= new Scanner(System.in);
        String input=null;
        do{
            try{
            input = scanner.nextLine();
            }catch (NoSuchElementException e){
                continue;
            }
        }while(Objects.equals(input, "\n") || input==null);
        //scanner.close();
        return input;
     }
    public static void setOtherFields(ArrayList<Player> otherPlayers) {
        ZakClient.otherPlayers = otherPlayers;
    }
    public static void genericTurnMessageHandler(GenericTurnMessage message){
        myTurn=true;
        //todo: Aggiornamento dello stato dei fields dei deck del player
        System.out.println("It's your turn:");
    }
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            System.out.print("\033[H\033[2J");
            System.out.flush();
            /*if (os.contains("Windows")) Runtime.getRuntime().exec("cls");
            else Runtime.getRuntime().exec("clear");*/

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

    public static void clientDisconnect() {
        serverComHandler.interrupt();
        System.err.println("Disconnected from server: Unable to establish a connection with server");
        System. exit(0);
    }
}
class ServerHandler extends Thread implements Runnable {
    private final String clientName;
    private final UUID clientID;
    private ObjectOutputStream outServer;
    private ObjectInputStream inServer;

    public ServerHandler(String clientName, UUID clientID, ObjectOutputStream outFromServer, ObjectInputStream inFromServer) throws IOException {
        this.clientName = clientName;
        this.clientID = clientID;
        this.outServer = outFromServer;
        this.inServer = inFromServer;
    }

    @Override
    public void run() {
        while(ZakClient.isCurrentGameStatus()) {
            try {

                messageReceiver();


            } catch (ClassNotFoundException | WrongMessageConversionException e) {
                System.out.println("ERRORE ServerCom HANDLER: " + e.getMessage());

            } catch(IOException e){
                System.out.println("ERRORE ServerCom HANDLER: " + e.getMessage());
                try {
                    throw new ClientAbruptlyDisconnectedException(clientName+" abruptly disconnected from server due to socket degradation: Attempting reconnection");
                } catch (ClientAbruptlyDisconnectedException ex) {
                    if(tryReconnectToServer()) continue;
                    //todo: reconnection attempt
                    clientDisconnected();
                }
            }
            try{
                if(ZakClient.socket.isClosed() && ZakClient.isCurrentGameStatus()) throw new ClientAbruptlyDisconnectedException(clientName+" abruptly disconnected from server: Attempting reconnection");
            }catch(ClientAbruptlyDisconnectedException e){
                if(tryReconnectToServer()) continue;
                //todo: reconnection attempt
                clientDisconnected();
            }
        }
    }

    private void clientDisconnected() {
        //todo: robe per chiudere i thread
        ZakClient.clientDisconnect();
    }

    private boolean tryReconnectToServer()  {
        boolean result=true;
        Socket socket;
        try {
            socket = ZakClient.connectionAttempt(ZakClient.serverAddress, ZakClient.port);
            outServer= new ObjectOutputStream(socket.getOutputStream());
            inServer = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inServer.readObject();
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.","");
        switch (messageType){
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStandardMessage":
                break;
            case "BroadCastStartingMessage":
                broadCastStartingMessageHandler((BroadCastStartingMessage) message);
                break;
            case "EndGameMessage":
                endOfTheGame((EndGameMessage)message);
                break;
            case "LobbyCreationMessage":
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server: "+a.getName()+" is not Handled");
        }
    }
    private void broadCastStartingMessageHandler(BroadCastStartingMessage initialMatchSetupMessage) throws IOException {
        try{
            if(initialMatchSetupMessage == null) throw new WrongMessageConversionException("Was not able to initialize Starting Field");
        } catch (WrongMessageConversionException e) {
            e.getMessage();
            throw new RuntimeException(e);
        }
        ZakClient.initialMatchSetup(initialMatchSetupMessage);
    }
    public void sendMessage(Message message) throws IOException {
        message.setSender(clientName);
        message.setClientID(clientID);
        outServer.writeObject(message);
    }
    private void textMessageHandler(TextMessage message) {;
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

