package com.example.codexnaturalis;

import javafx.scene.text.TextBoundsType;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class ZakClient {

    private static Pair<ObjectInputStream,ObjectOutputStream> ioStream;
    private static Pair<String,Integer> connectionInfo;
    private static ServerHandler serverHandler;
    private static String playerNick;
    private static Player player;
    private static ArrayList<Player> otherPlayers;
    private static volatile boolean currentGameStatus;
    private static boolean myTurn;
    private static UUID clientID;
    public static void main(String[] args) {

        Integer port=null;
        String serverAddress=null;
        if(!args[1].isBlank()) port = Integer.parseInt(args[1]);
        if(!args[0].isBlank()) serverAddress = args[0];
        else{
            System.err.println("Missing arguments\nMake sure to start the client with Server Address and Port as parameters\ni.e. java Client localhost 8081");
            System.exit(0);
        }
        try {
            initialClientSetup(serverAddress,port);
        }catch(IOException | ClassNotFoundException | StupidUserException | HandShakeException e){
            System.err.println("Client Setup error: "+e.getMessage());
            throw new RuntimeException("Please restart the client and try again");
        }
        try{
            //clearConsole();
            gameStart();
        } catch(IOException | ClassNotFoundException | WrongMessageConversionException e){
            System.err.println("Game Start error: "+e.getMessage());
        }
    }

    private static void initialClientSetup(String serverAddress,int port) throws IOException, ClassNotFoundException, StupidUserException, HandShakeException {
        ConnectionManger connMan = null;
        clientID = UUID.randomUUID();
        currentGameStatus = false;
        myTurn = false;
        otherPlayers= new ArrayList<>();
        connectionInfo = new Pair<>(serverAddress,port);
        playerNick = playerGreeting();
        int i=0;
        do{
            if(i==3) throw new StupidUserException("Too many bad failed attempts: Closing client");
            System.out.println("How would you like to connect?");
            System.out.println("0 - Cancel");
            System.out.println("1 - Socket");
            System.out.println("2 - RMI");
            switch(getIntInput(2,false)){
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    connMan = new ConnectionManger(false,connectionInfo);
                    break;
                case 2:
                    connMan = new ConnectionManger(true,connectionInfo);
                    break;
                default:
                    i++;
                    System.out.println("Not a valid input: Try again");
            }
            if (connMan!=null) break;
        }while(i<=3);
        connMan.connectionSetup();
        connMan.doHandShake();
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
            ObjectiveCard chosenCard;
            Collection<Player> players;
            boolean cardFace;
            player = initialMatchSetupMessage.getPlayers().get(clientID);
            if(player == null) throw new WrongPlayerUUIDException("There was an error retrieving the info about the match");
            initialMatchSetupMessage.getPlayers().remove(clientID);
            players=initialMatchSetupMessage.getPlayers().values();
            otherPlayers.addAll(players);
            //todo:otherfields deve essere una hashmap con anche i players
            chosenCard = player.chooseSecretObj(initialMatchSetupMessage.getSecretObjectiveCards(clientID));
            ArrayList<ObjectiveCard> tmpList = new ArrayList<>(Collections.singletonList(chosenCard));
            initialMatchSetupMessage.setSelectedSecret(tmpList);
            System.out.println("How do you want to face the starting card");
            System.out.println("1 - face Up\n2 - face Down");
            switch(getIntInput(2,false)){
                case 1:
                    cardFace=true;
                    break;
                case 2:
                    cardFace=false;
                    break;
                default: throw new IOException("There was an error trying to read the string");
            }
            initialMatchSetupMessage.setStarterCardFace(cardFace);
            serverHandler.sendMessage(initialMatchSetupMessage);
            player.placeStarterCard(cardFace);
        } catch (WrongPlayerUUIDException e){
            e.getMessage();
        } catch (StupidUserException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("All players' fields were correctly received");
            currentGameStatus = true;
        }
    }
    private static void gameStart() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        new Thread(serverHandler).start();
        while(!(currentGameStatus && serverHandler.wasFirstBroadCastReceived())){
            /*try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){
                System.out.println(e.getMessage());}*/
            Thread.onSpinWait();
        }
        while(currentGameStatus) selectPossibleActions();
    }

    private static void genericMessageAssembler() throws IOException, WrongMessageConversionException, ClassNotFoundException {
        clearConsole();
        ResourceGoldCard placedCard;
        GenericTurnMessage message = serverHandler.getMessageTurn();
        Pair<Integer, Integer> coordinates;
        int row, column;
        boolean face;
        ResourceGoldCard selectedCard;
        if(player.allCornersEmpty(player.getPlayerDeck().getStarterCard())){
            System.out.println("StarterCard:");
            player.getPlayerDeck().getStarterCard().printCard();
        }
        ArrayList<ResourceGoldCard> playerDeck = player.getPlayerDeck().getResourceGoldCards();
        while(true){
            player.printFieldWithName();
            System.out.println("What would you like to do?");
            System.out.println("0 - Cancel");
            System.out.println("1 - Analyze field");
            System.out.println("2 - Play a card");
            int j = getIntInput(2, false);
            if (j != 1 && j != 2 && j!=0) {
                System.out.println("Not a valid input");
                continue;
            }
            if(j==1){
                coordinates = getCoords(false);
                player.fieldAnalysis(coordinates.getKey(), coordinates.getValue());
            }
            else if(j==0) return;
            else break;
        }
        while (true) {
            player.getPlayerDeck().printResourceGoldCards();
            player.printManas();
            System.out.println("What card would you like to place? ");
            placedCard = playerDeck.get(getIntInput(playerDeck.size(), true));
            if (placedCard.getIdCard() > 40 && !player.requirementsAreFulfilled((GoldCard) placedCard)) {
                System.out.println("You do not possess enough materials or resources to place this card: choose another one");
                continue;
            }
            break;
        }
        while (true){
            System.out.println("What face would you like to play?");
            System.out.println("1 - Front");
            System.out.println("2 - Back");
            int i = getIntInput(2, false);
            if (i != 1 && i != 2) {
                System.out.println("Not a valid input");
                continue;
            }
            face = i == 1;
            placedCard.setIsPlacedFront(face);
            break;
        }
        while(true){
            coordinates = getCoords(true);
            row = coordinates.getKey();
            column = coordinates.getValue();
            if (!player.isCardAttachableToSlot(row, column)) {
                System.out.println("This slot is not available. Select another one");
                continue;
            }
            break;
        }
        player.placeCardAndRemoveFromDeck(row, column, placedCard);
        message.printPublicCards(message.printDrawnCards(1));//covered
        ArrayList<ResourceGoldCard> selectable = new ArrayList<>();
        selectable.addAll(message.getDrawnCard());
        selectable.addAll(message.getCardOnHand());
        System.out.println("Select a card to draw from public deck: ");
        int selected=getIntInput(selectable.size(),true);
        selectedCard=selectable.get(selected);
        player.getPlayerDeck().getResourceGoldCards().add(selectedCard);
        message = new GenericTurnMessage(null,null,new ArrayList<>(Collections.singletonList(selectedCard)),new ArrayList<>(Collections.singletonList(placedCard)),coordinates);
        //todo: update points
        serverHandler.sendMessage(message);
        serverHandler.setMessageTurn(null);
        myTurn=false;
        clearConsole();
    }

    private static Pair<Integer, Integer> getCoords(boolean mode) {
        int fieldSize = player.getPlayerField().getSlots().length;
        Pair<Integer, Integer> coordinates;
        while (true) {
            int row,column;
            System.out.println("Select a row:");
            row = getIntInput(fieldSize, false);
            System.out.println("Select a column:");
            column = getIntInput(fieldSize,false);
            coordinates = new Pair<>(row, column);
            if (mode) {
                if (player.getPlayerField().getSlots()[row][column].isBusySlot()) {
                    System.out.println("This slot is not available. Select another one");
                    continue;
                }
            }
            else{
                if (!player.getPlayerField().getSlots()[row][column].isBusySlot()) {
                    System.out.println("This slot is not available. Select another one");
                    continue;
                }
            }
            break;
        }
        return coordinates;
    }

    private static void printPlayerField() {
        for (Player p : otherPlayers) {
            p.printFieldWithName();
        }
    }

    private static void writeTextMessage() throws IOException {
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
                System.out.println("Please input your message:");
                text = receiveInput();
                if(i==counter) serverHandler.sendMessage(new TextMessage(playerNick,clientID,text,"Everyone"));
                else{
                    recipient = recipientChooser.get(i).getPlayerName();
                    serverHandler.sendMessage(new TextMessage(playerNick,clientID,text,recipient));
                }
                break;
            }
            else if(i==0) break;
            System.out.println("Invalid input: please select a valid option");
        }while(true);
    }

    private static void selectPossibleActions() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        System.out.println("What would you like to do:\n");
        printPossibleChoices();
        int action;
        try{
        action = Integer.parseInt(receiveInput());}
        catch(Exception e){
            System.out.println("Invalid input: try again");
            return;
        }
        clearConsole();
        switch (action){
            case 1:
                printPlayerField();
                break;
            case 2:
                player.printAllObjective();
                break;
            case 3:
                player.getPlayerDeck().printResourceGoldCards();
                break;
            case 4:
                player.printFieldWithName();
                break;
            case 5:
                writeTextMessage();
                break;
            case 6:
                //todo: match updater
                if(serverHandler.getMessageTurn() != null) genericMessageAssembler();
                else System.out.println("Wrong input: Input the number associated to the desired action");
                break;
            default:
                System.out.println("Wrong input: Input the number associated to the desired action");
        }
    }
    private static void printPossibleChoices() {
        String choices;
        //if(myTurn) clearConsole();
        if (!myTurn){
            choices =
                " _____                           _____ \n" +
                "( ___ )                         ( ___ )\n" +
                " |   |~~~~~~~~~~~~~~~~~~~~~~~~~~~|   | \n" +
                " |   | [1] Other Players' Codex  |   | \n" +
                " |   | [2] Show Objective Cards  |   | \n" +
                " |   | [3] Show personal deck    |   | \n" +
                " |   | [4] Show personal Codex   |   | \n" +
                " |   | [5] Write to chat         |   | \n" +
                " |___|~~~~~~~~~~~~~~~~~~~~~~~~~~~|___| \n" +
                "(_____)                         (_____)\n";
    }
        else {
            choices =
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
                    "(_____)                         (_____)\n";
        }
        System.out.println(choices);
    }
    public static String receiveInput(){
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
    public static void genericTurnMessageHandler() {
        myTurn=true;
        //todo: Aggiornamento dello stato dei fields dei deck del player
        //System.lineSeparator();
        //clearConsole();
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
        System.out.println("To start a new game, restart the client");
        System.exit(0);

    }

    public static void clientDisconnect() {
        serverHandler.interrupt();
        System.err.println("Disconnected from server: Unable to establish a connection with server");
        System. exit(0);
    }

    public static Pair<String, Integer> getConnectionInfo() {
        return connectionInfo;
    }

    public static ServerHandler getServerHandler() {
        return serverHandler;
    }

    public static void setServerHandler(ServerHandler serverHandler) {
        ZakClient.serverHandler = serverHandler;
    }

    public static UUID getClientID() {
        return clientID;
    }

    public static String getPlayerNick() {
        return playerNick;
    }
    private static int getIntInput(int range,boolean type){
        Integer thingToParse=null;
        while(true){
            try {
                thingToParse = Integer.parseInt(receiveInput());
            } catch (Exception e){
                System.out.println("Invalid input: try again");
                continue;
            }
            if(thingToParse<=range && thingToParse>=0) break;
        }
        if(type)return thingToParse -1;
        else return  thingToParse;
    }

    public static ArrayList<Player> getOtherPlayers() {
        return otherPlayers;
    }
    public static Player getPlayer(){return  player;}
}
