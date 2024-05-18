package com.example.codexnaturalis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class ZakClient extends Application{

    private static boolean crashed;
    private static Pair<String, Integer> connectionInfo;
    private static boolean connectionType;
    private static ServerHandler serverHandler;
    private static String playerNick;
    private static Player player;
    private static ArrayList<Player> otherPlayers;
    private static volatile boolean currentGameStatus;
    private static boolean myTurn;
    private static UUID clientID;
    private static UUID matchID;
    private static boolean guiSelector;
    private static Stage stage;
    private static Semaphore sem;

    public static void main(String[] args) {
        sem=new Semaphore(0);
        clientStart(args);
        initialClientSetup();
        start();
    }

    /**
     * Calls basic starting client methods like connection, handshake etc.
     * Finally calls for gameStart() which starts game loop
     */
    public static void start() {
        try {
            instanceConManAndHandshake();
        }
        catch (IOException | ClassNotFoundException | StupidUserException | HandShakeException e) {
            System.err.println("Client Setup error: " + e.getMessage());
            if (guiSelector) {
                Platform.runLater(() -> {
                    LauncherController.alert(e.getMessage(),true);
                    stage.close();
                });
            } else throw new RuntimeException("Please restart the client and try again");
        }
        try {
            //clearConsole();
            gameStart();
        } catch (IOException | ClassNotFoundException | WrongMessageConversionException e) {
            System.err.println("Game Start error " + e.getLocalizedMessage() + ": " + e.getMessage());
            if (guiSelector) {
                Platform.runLater(() -> {
                    LauncherController.alert(e.getMessage(),true);
                    stage.close();
                });
            } else throw new RuntimeException("Please restart the client and try again");
        }
    }

    /**
     * Connection type is chosen by player, then Connection Manager is initialized and Handshake with server are attempted here
     *
     * @throws IOException            thrown when unable to write save data on file
     * @throws ClassNotFoundException thrown if there was an issue converting messages
     * @throws StupidUserException    thrown when users repeatedly input wrong data
     * @throws HandShakeException     thrown if there was an issue while doing the handshake
     */
    public static void instanceConManAndHandshake() throws IOException, ClassNotFoundException, StupidUserException, HandShakeException {
        ConnectionManger connMan = null;
        if (!guiSelector) connMan = chooseConnectionType();
        else connMan = new ConnectionManger(connectionType, connectionInfo);
        if (!crashed) appendStringOnFile("ConnectionType:" + (!connMan.typeOfConnection ? "false" : "true"));
        connMan.connectionSetup();
        connMan.doHandShake();
        //LauncherController.printStatus("Connection Success","green");
    }

    /**
     * As soon as the client starts, this function is called. Initializes connection info and type of playing interface
     *
     * @param args arguments passed to the executable
     */
    public static void clientStart(String[] args) {
        int numOfPar = Arrays.stream(args).toList().size();
        String portStandard = "8081";
        System.out.println("numOfPar:" + numOfPar + " port:" + portStandard);
        guiSelector = false;
        switch (numOfPar) {
            case 0:
                System.err.println("Missing arguments\nMake sure to start the client with Server Address and Port as parameters\ni.e. java Client localhost 8081");
                System.exit(0);
                break;
            case 1:
                System.out.println("No port number was input\nFallback to 8081");
                connectionInfo = setArgValues(args[0], portStandard);
                System.out.println("Missing interface argument - Using TUI:");
                break;
            case 2:
                connectionInfo = setArgValues(args[0], args[1]);
                System.out.println("Missing interface argument - Using TUI:");
                break;
            case 3:
                connectionInfo = setArgValues(args[0], args[1]);
                if (args[2].equalsIgnoreCase("gui")) {
                    guiSelector = true;
                    //todo:ENTRYPOINT JAVAFXGUI
                    launch();
                    System.exit(0);
                    /*try{
                        //todo: REMOVE IN FINAL BUILD AND CHANGE PATH
                        //ProcessBuilder processBuilderCompile = new ProcessBuilder("javac","-d","out/dev/HelloApp","src/main/java/com/example/codexnaturalis/HelloApplication.java");
                        //processBuilderCompile.start().waitFor();
                        //todo: REMOVE IN FINAL BUILD AND CHANGE PATH
                        ProcessBuilder processBuilder = new ProcessBuilder("java","-cp",System.getProperty("java.class.path"),"com.example.codexnaturalis.HelloApplication");
                        processBuilder.inheritIO();
                        //processBuilder.redirectErrorStream(true);
                        Process process = processBuilder.start();
                        int exitCode = process.waitFor();
                        System.out.println(exitCode);
                        if(exitCode==0) System.exit(exitCode);
                        else throw new IOException("An error was encountered starting the GUI: fallback to TUI");
                    } catch(IOException e){
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                        guiSelector=false;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }*/
                } else if (args[2].equalsIgnoreCase("tui")) guiSelector = false;
                else {
                    System.out.println("Invalid argument " + args[2] + ": Accepted values are 'gui' or 'tui' " + "\nFallback using TUI");
                    guiSelector = false;
                }
                break;
            default:
                throw new RuntimeException("Something went wrong while starting");
        }
    }

    /**
     * Initializes connectionInfo pair, required as reference for the server and passes it to clientStart
     *
     * @param serverAddress - serverIP
     * @param port          - Server port
     * @return connectionInfo pair and passes it to clientStart
     */
    private static Pair<String, Integer> setArgValues(String serverAddress, String port) {
        int connectionPort;
        try {
            connectionPort = Integer.parseInt(port);
        } catch (Exception e) {
            System.out.println("Invalid argument: port number must be an integer\nFallback to 8081");
            connectionPort = 8081;
        }
        return new Pair<>(serverAddress, connectionPort);
    }

    /**
     * Function will ask the player the type of connection they would like to use to reach server.
     * Some of client's critical components are initialized here such as the UUID and crashed flag
     */
    public static int initialClientSetup() {
        crashed = false;
        currentGameStatus = false;
        myTurn = false;
        otherPlayers = new ArrayList<>();
        if (!guiSelector) playerNick = playerGreeting();
        return uuidGen();

    }

    /**
     * Instantiates a new connection manger for the client
     *
     * @return connection Manager instance
     * @throws StupidUserException thrown if user input invalid data multiple times
     */

    public static ConnectionManger chooseConnectionType() throws StupidUserException {
        ConnectionManger connMan = null;
        int connectionInt = 0;
        int i = 0;
        do {
            if (crashed) {
                System.out.println("Reconnecting...");
                connMan = new ConnectionManger(connectionType, connectionInfo);
                break;
            }
            if (i == 3) throw new StupidUserException("Too many bad failed attempts: Closing client");
            System.out.println("How would you like to connect?");
            System.out.println("0 - Cancel");
            System.out.println("1 - Socket");
            System.out.println("2 - RMI");
            connectionInt = getIntInput(2, false);
            switch (connectionInt) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    connMan = new ConnectionManger(false, connectionInfo);
                    break;
                case 2:
                    connMan = new ConnectionManger(true, connectionInfo);
                    break;
                default:
                    i++;
                    System.out.println("Not a valid input: Try again");
            }
        } while (connMan == null);
        return connMan;
    }

    /**
     * Save file writer
     *
     * @param contentToAppend - String to append inside the save file
     * @throws IOException - thrown if an error is encountered while writing the save file
     */
    public static void appendStringOnFile(String contentToAppend) throws IOException {
        String fileName = "savedata/" + playerNick + "-matchInfo.cdxn";
        StringBuilder existingContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            existingContent.append(line).append(System.lineSeparator());
        }
        existingContent.append(contentToAppend).append(System.lineSeparator());
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        // Write the combined content back to the file
        writer.write(existingContent.toString());
        //System.out.println("Content appended to file successfully.");
        reader.close();
        writer.close();
    }

    /**
     * Method responsible for initializing the UUID. 'crashed' and 'clientID' in Client are also initialized here, according to what save file reports.
     *
     * @return - Returns -1 if no save file was found and was unable to write create a new one, a random new UUID is generated.
     * Returns 0 if a save file was read correctly and matchID was initialized.
     * Returns 1 if a new save file is created and a new random UUID was correctly generated
     */
    public static int uuidGen() {
        String fileName = "savedata/" + playerNick + "-matchInfo.cdxn";
        ArrayList<String> content = new ArrayList<>();
        UUID newID = null;
        try {
            File newDir = new File("savedata");
            if (!newDir.exists()) {
                throw new FileNotFoundException("Unable to find the save data dir");
            }
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.add(line);
            }
            newID = UUID.fromString(content.getFirst().replaceFirst("ClientID:", ""));
            content.removeFirst();
            crashed = true;
            connectionType = Objects.equals(content.getFirst().replaceFirst("ConnectionType:", ""), "true");
            content.removeFirst();
            try {
                matchID = UUID.fromString(content.getFirst().replaceFirst("MatchID:", ""));
            } catch (Exception e) {
                matchID = null;
            }
            // Close the BufferedReader and FileReader
            bufferedReader.close();
            fileReader.close();
            //savefile found
            clientID = newID;
            return 1;
        } catch (IOException e) {
            System.out.println("No savefile found - Start as new Player");
        }
        try {
            Path directory = Paths.get("savedata");
            if (Files.notExists(directory)) {
                Files.createDirectory(directory);
                switch (Files.exists(directory) ? 1 : 2) {
                    case 1:
                        System.out.println("here");
                        break;
                    case 2:
                        if (!playWithoutReconnectionQuestion())
                            throw new FileNotFoundException("Cannot create directory");
                        break;
                }
            } else{
                FileOutputStream outputStream = new FileOutputStream(fileName);
                newID = UUID.randomUUID();
                String myIDString = "ClientID:" + newID;
                byte[] bytes = myIDString.getBytes();
                outputStream.write(bytes);
                outputStream.close();
                crashed = false;
                matchID = null;
                clientID = newID;
                return 0;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            Platform.runLater(() -> stage.close());
        } catch (IOException e) {
            System.out.println("Error while trying to write file");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        clientID = UUID.randomUUID();
        return -1;
    }

    /**
     * Asks user if he wants to play without saving/ability to reconnect
     * @return true if yes, false if no
     * @throws InterruptedException if semaphore is unable to acquire thread
     */
    private static boolean playWithoutReconnectionQuestion() throws InterruptedException {
        Semaphore sem = new Semaphore(0);
        AtomicReference<String> response = new AtomicReference<>();
        response.set("no");
        while (true) {
            if (isGuiSelector()) {
                Platform.runLater(()->{
                    response.set(LauncherController.askStringInputToUser("Unable to create the savefile dir:", "Do you want to play anyway without the possibility of reconnection? (yes or no)").toLowerCase());
                    sem.release();
                });
                sem.acquire();
            } else {
                System.out.println("Unable to create the savefile dir:\nDo you want to play anyway without the possibility of reconnection?");
                response.set(receiveInput().toLowerCase());
            }
            if(!(response.get().equals("no")||response.get().equals("yes"))){
                if(isGuiSelector()) Platform.runLater(()->LauncherController.alert("Invalid Input",false));
                else System.out.println("Invalid Input");
            }
            else break;;
        }
        return response.get().equals("yes");
    }

    /**
     * Basic Player greeting function that runs as soon as the client starts:
     * Player will be greeted and will be choosing his nick here
     */
    private static String playerGreeting() {
        System.out.println("Welcome Player to:");
        System.out.println("""
                 _____                                                              _____\s
                ( ___ )------------------------------------------------------------( ___ )
                 |   |                                                              |   |\s
                 |   |   .-._   .-._.    .                                          |   |\s
                 |   | ..' (_)`-'        /    `--.  .-.                             |   |\s
                 |   | |      .-._..-../   .-.   \\/                                 |   |\s
                 |   | |    _(   )(   /  ./.-'_  /\\                                 |   |\s
                 |   | `.    )`-'  `-'-..(__.'.-'  `-.                              |   |\s
                 |   |   `--'.-.                                     .              |   |\s
                 |   |         /  |         /                       /    .-.        |   |\s
                 |   |        /\\  | .-. ---/---)  (   ).--..-.     /     `-' .      |   |\s
                 |   |       /  \\ |(  |   /   (    ) /    (  |    /     /   / \\     |   |\s
                 |   |  .-' /    \\| `-'-'/     `--':/      `-'-'_/_.-_.(__./ ._)    |   |\s
                 |   | (__.'      `.                                      /         |   |\s
                 |___|                                                              |___|\s
                (_____)------------------------------------------------------------(_____)

                """);
        System.out.println("Please enter your nickname:");
        return receiveInput();
    }

    /**
     * This method lets you choose your secret Objective card, and lets you place your starter card face up or down
     *
     * @param initialMatchSetupMessage, is a message from server that has required match details in order to start the game
     *                                  such as own and other players' fields, nicks, points etc
     * @throws IOException, if something goes wrong while facing the starting card up or down
     */
    public static void initialMatchSetup(BroadCastStartingMessage initialMatchSetupMessage) throws IOException {
        try {
            Collection<Player> players;
            matchID = initialMatchSetupMessage.getMatchID();
            appendStringOnFile("MatchID:" + matchID.toString());
            player = initialMatchSetupMessage.getPlayers().get(clientID);
            if(isGuiSelector())sem.release();
            if (player == null)
                throw new WrongPlayerUUIDException("There was an error retrieving the info about the match");
            initialMatchSetupMessage.getPlayers().remove(clientID);
            players = initialMatchSetupMessage.getPlayers().values();
            otherPlayers.addAll(players);
            initialMatchSetupMessage = ConnectionManger.secretSelector(initialMatchSetupMessage);
            if(isGuiSelector()) sem.release();
            serverHandler.sendMessage(initialMatchSetupMessage);
            player.placeStarterCard(initialMatchSetupMessage.getStarterCardFace());
            if(isGuiSelector()) sem.release();
        } catch (WrongPlayerUUIDException e) {
            System.out.println(e.getMessage());
        } catch (StupidUserException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {

            System.out.println("All players' fields were correctly received");
            currentGameStatus = true;
        }
    }

    /**
     * The serverHandler thread is started. This thread will be handling all communication with server. Was this thread to crash for
     * whatever the reason, the client will try to restart it in order to reconnect with the server.
     * This function also stops the main thread until the server makes available all needed information to start the game(field,other players,common Objectives etc.)
     * via the BroadCastMessage.
     * As soon as the game is started, the user will be able to interact with his own field and other players'
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws WrongMessageConversionException, will be thrown if there was an issue casting the messages
     */
    public static void gameStart() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        if (!crashed) new Thread(serverHandler).start();
        if (!isGuiSelector()) {
            while (!(currentGameStatus && serverHandler.wasFirstBroadCastReceived())) {
                Thread.onSpinWait();
            }
            while (currentGameStatus) selectPossibleActions();
        }
    }

    /**
     * Allows player to play his turn. The user will be interacting with the TUI in order to create a
     * message that will be sent to the server via his ServerComHandler. Player is able to place cards, analyze Field and draw cards
     * from DrawingDeck from here
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void genericMessageAssembler() throws IOException, ClassNotFoundException {
        clearConsole();
        ResourceGoldCard placedCard;
        GenericTurnMessage message = serverHandler.getMessageTurn();
        Pair<Integer, Integer> coordinates;
        int row, column;
        boolean face;
        ResourceGoldCard selectedCard = null;
        if (player.allCornersEmpty(player.getPlayerDeck().getStarterCard())) {
            System.out.println("StarterCard:");
            player.getPlayerDeck().getStarterCard().printCard();
        }
        ArrayList<ResourceGoldCard> playerDeck = player.getPlayerDeck().getResourceGoldCards();
        while (true) {
            player.printFieldWithName();
            System.out.println("What would you like to do?");
            System.out.println("0 - Cancel");
            System.out.println("1 - Analyze field");
            System.out.println("2 - Play a card");
            int j = getIntInput(2, false);
            if (j != 1 && j != 2 && j != 0) {
                System.out.println("Not a valid input");
                continue;
            }
            if (j == 1) {
                coordinates = getCoordinates(false);
                player.fieldAnalysis(coordinates.getKey(), coordinates.getValue());
            } else if (j == 0) return;
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
        while (true) {
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
        while (true) {
            coordinates = getCoordinates(true);
            row = coordinates.getKey();
            column = coordinates.getValue();
            if (!player.isCardAttachableToSlot(row, column)) {
                System.out.println("This slot is not available. Select another one");
                continue;
            }
            break;
        }
        player.placeCardAndRemoveFromDeck(row, column, placedCard);
        message.printCoveredCards();
        message.printPublicCards();
        ArrayList<ResourceGoldCard> selectable = new ArrayList<>();
        selectable.addAll(message.getDrawnCard());
        selectable.addAll(message.getCardOnHand());

        ArrayList<Integer> allIds = new ArrayList<>();
        for (ResourceGoldCard card : selectable) allIds.add(card.getIdCard());
        /*allIds.add(message.getDrawnCard().get(0).getIdCard());
        allIds.add(message.getDrawnCard().get(1).getIdCard());
        allIds.add(message.getCardOnHand().get(0).getIdCard());
        allIds.add(message.getCardOnHand().get(1).getIdCard());
        allIds.add(message.getCardOnHand().get(2).getIdCard());
        allIds.add(message.getCardOnHand().get(3).getIdCard());*/
        int idSelected = selectCardIdToDrawn(allIds);
        for (ResourceGoldCard card : selectable)
            if (card.getIdCard() == idSelected) {
                selectedCard = card;
                break;
            }
        //selectedCard=selectable.get(getCardIndexFromId(idSelected, allIds));
        player.getPlayerDeck().getResourceGoldCards().add(selectedCard);
        message = new GenericTurnMessage(null, null, new ArrayList<>(Collections.singletonList(selectedCard)), new ArrayList<>(Collections.singletonList(placedCard)), coordinates);
        //todo: update points
        serverHandler.sendMessage(message);
        serverHandler.setMessageTurn(null);
        myTurn = false;
        if (isGuiSelector()) {
            Platform.runLater(() -> {
                MainController.setTurnButton(true);
            });
        }
        clearConsole();
    }

    /**
     * Given an array of card ids, gets input from the player. Input has to be within the values of array
     *
     * @param ids: arrays from which the player can choose
     * @return int, input of the player
     */
    public static int selectCardIdToDrawn(ArrayList<Integer> ids) {
        Integer choice = null;
        while (true) {
            System.out.print("Select a card to draw from public deck: ");
            try {
                choice = Integer.parseInt(receiveInput());
            } catch (Exception e) {
                System.out.println("Invalid input: try again");
                continue;
            }
            if (ids.contains(choice)) break;
            System.out.println("Invalid input: try again");
        }
        return choice;
    }

    /**
     * Given an id and an array of ids, returns the position of the id in the array
     *
     * @param id:  id chosen
     * @param ids: arrays of all ids
     * @return int, position of id in ids
     */
    public static int getCardIndexFromId(int id, ArrayList<Integer> ids) {
        int pos = -1;
        for (int i : ids) {
            if (i == id) {
                pos = ids.indexOf(i);
                break;
            }
        }
        return pos;
    }

    /**
     * @param mode set false when analyzing field, set true when placing a card
     * @return Pair of coordinates of a field slot
     */
    private static Pair<Integer, Integer> getCoordinates(boolean mode) {
        int fieldSize = player.getPlayerField().getSlots().length;
        Pair<Integer, Integer> coordinates;
        while (true) {
            int row, column;
            System.out.println("Select a row:");
            row = getIntInput(fieldSize, false);
            System.out.println("Select a column:");
            column = getIntInput(fieldSize, false);
            coordinates = new Pair<>(row, column);
            if (mode) {
                if (player.getPlayerField().getSlots()[row][column].isBusySlot()) {
                    System.out.println("You cannot place a card in a busy slot. Select another one");
                    continue;
                }
            } else {
                if (!player.getPlayerField().getSlots()[row][column].isBusySlot()) {
                    System.out.println("This slot is empty, you cannot analyze it. Select another one");
                    continue;
                }
            }
            break;
        }
        return coordinates;
    }

    /**
     * Will access the other players' fields and current points and will print them
     */
    private static void printPlayerField() {
        for (Player p : otherPlayers) {
            p.printFieldWithName();
        }
    }

    /**
     * Advanced Functionality for the project: Basic Chat functionality
     * that allows users to send messages to everyone or to a specific user
     *
     * @throws IOException, will be thrown when the Scanner has issues reading the System.in
     */
    private static void writeTextMessage() throws IOException {
        String recipient;
        String text;
        HashMap<Integer, Player> recipientChooser = new HashMap<>();
        int i = 1;
        System.out.println("Who do you want to send the message to?");
        System.out.println(0 + " - Cancel");
        for (Player p : otherPlayers) {
            recipientChooser.put(i, p);
            System.out.println(i + " - " + p.getPlayerName());
            ++i;
        }
        System.out.println(i + " - Everyone");
        int counter = recipientChooser.size() + 1;
        do {
            i = Integer.parseInt(receiveInput());
            if (i <= counter && i > 0) {
                System.out.println("Please input your message:");
                text = receiveInput();
                if (i == counter) serverHandler.sendMessage(new TextMessage(playerNick, clientID, text, "Everyone"));
                else {
                    recipient = recipientChooser.get(i).getPlayerName();
                    serverHandler.sendMessage(new TextMessage(playerNick, clientID, text, recipient));
                }
                break;
            } else if (i == 0) break;
            System.out.println("Invalid input: please select a valid option");
        } while (true);
    }

    /**
     * Core TUI element. This component allows the user to interact with their or other players' field, write messages in the chat,
     * show his own deck.
     * If myTurn is true will allow the player to play his turn.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void selectPossibleActions() throws IOException, ClassNotFoundException {
        printPossibleChoices();
        System.out.print("What would you like to do: ");
        int action;
        try {
            action = Integer.parseInt(receiveInput());
        } catch (Exception e) {
            System.out.println("Invalid input: try again");
            return;
        }
        clearConsole();
        System.out.println();
        switch (action) {
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
                if (serverHandler.getMessageTurn() != null && myTurn) genericMessageAssembler();
                else System.out.println("Wrong input: Input the number associated to the desired action");
                break;
            default:
                System.out.println("Wrong input: Input the number associated to the desired action");
        }
    }

    /**
     * Prints possible actions the user is capable of doing
     */
    private static void printPossibleChoices() {
        String choices;
        //if(myTurn) clearConsole();
        if (!myTurn) {
            choices =
                    """
                             _____                           _____\s
                            ( ___ )                         ( ___ )
                             |   |~~~~~~~~~~~~~~~~~~~~~~~~~~~|   |\s
                             |   | [1] Other Players' Codex  |   |\s
                             |   | [2] Show Objective Cards  |   |\s
                             |   | [3] Show personal deck    |   |\s
                             |   | [4] Show personal Codex   |   |\s
                             |   | [5] Write to chat         |   |\s
                             |___|~~~~~~~~~~~~~~~~~~~~~~~~~~~|___|\s
                            (_____)                         (_____)
                            """;
        } else {
            choices =
                    """
                             _____                           _____\s
                            ( ___ )                         ( ___ )
                             |   |~~~~~~~~~~~~~~~~~~~~~~~~~~~|   |\s
                             |   | [1] Other Players' Codex  |   |\s
                             |   | [2] Show Objective Cards  |   |\s
                             |   | [3] Show personal deck    |   |\s
                             |   | [4] Show personal Codex   |   |\s
                             |   | [5] Write to chat         |   |\s
                             |   | [6] Play turn             |   |\s
                             |___|~~~~~~~~~~~~~~~~~~~~~~~~~~~|___|\s
                            (_____)                         (_____)
                            """;
        }
        System.out.println(choices);
    }

    /**
     * @return string input from user. Handles all possible exceptions
     */
    public static String receiveInput() {
        Scanner scanner = new Scanner(System.in);
        String input = null;
        do {
            try {
                input = scanner.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage() + ": Scanner issue");
            }
        } while (Objects.equals(input, "\n") || input == null);
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Quitting client");
            clientDisconnect();
        }
        return input;
    }

    /**
     * Sets myTurn to true if it's the user's turn to play.
     * Will clear the screen and reprint printPossibleAction()
     */
    public static void genericTurnMessageHandler() {
        myTurn = true;
        //System.lineSeparator();
        //clearConsole();
        if (isGuiSelector()) {

            Platform.runLater(() -> {
                MainController.setTurnButton(false);
            });
        }
        System.out.println("\nIt's your turn:");
        if(isGuiSelector()) Platform.runLater(()->MainController.alert("It's your turn"));
        System.out.print("What do you want to do? ");

    }

    /**
     * Clears the screen using JVM escape string
     */
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            System.out.print("\033[H\033[2J");
            System.out.flush();
            /*if (os.contains("Windows")) Runtime.getRuntime().exec("cls");
            else Runtime.getRuntime().exec("clear");*/

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean isCurrentGameStatus() {
        return currentGameStatus;
    }

    /**
     * Ends game and shuts off client
     */
    public static void endOfTheGame() {
        currentGameStatus = false;
        System.out.println("To start a new game, restart the client");
        System.exit(0);

    }

    /**
     * Handles client disconnection and closes the client.
     * The client will call this function after 15s of retrying to reconnect to server
     */
    public static void clientDisconnect() {
        if (serverHandler != null && serverHandler.isAlive()) serverHandler.interrupt();
        System.err.println("Disconnected from server: Unable to establish a connection with server");
        System.exit(0);
    }

    /**
     * @return ServerAddress:Port pair
     */
    public static Pair<String, Integer> getConnectionInfo() {
        return connectionInfo;
    }

    /**
     * This function is called by the connection manager to set the handler pointer
     */
    public static void setServerHandler(ServerHandler serverHandler) {
        ZakClient.serverHandler = serverHandler;
    }

    public static UUID getClientID() {
        return clientID;
    }

    public static String getPlayerNick() {
        return playerNick;
    }

    public static void setPlayerNick(String s) {
        playerNick = s;
    }

    /**
     * Function that lets you input a number in a specific range and handles all exceptions that Integer.parseInt() doesn't handle
     *
     * @param range, If the input number is N, it is considered acceptable if 0<=N<=range
     * @param type,  true: if you want to get an index (number typed - 1), false: if you want to retrieve the actual input number
     * @return int n typed in by user
     */
    public static int getIntInput(int range, boolean type) {
        Integer thingToParse = null;
        while (true) {
            try {
                thingToParse = Integer.parseInt(receiveInput());
            } catch (Exception e) {
                System.out.println("Invalid input: try again");
                continue;
            }
            if (thingToParse <= range && thingToParse >= 0) break;
        }
        if (type) return thingToParse - 1;
        else return thingToParse;
    }

    public static ArrayList<Player> getOtherPlayers() {
        return otherPlayers;
    }

    public static ServerHandler getServerHandler() {
        return serverHandler;
    }

    public static void setPlayer(Player player) {
        ZakClient.player = player;
    }

    public static void setOtherPlayers(ArrayList<Player> otherPlayers) {
        ZakClient.otherPlayers = otherPlayers;
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setCurrentGameStatus(boolean b) {
        currentGameStatus = b;
    }

    public static void setMyTurn(boolean b) {
        myTurn = b;
    }

    public static boolean isCrashed() {
        return crashed;
    }

    public static void setClientID(UUID uuid) {
        clientID = uuid;
    }

    public static UUID getMatchID() {
        return matchID;
    }

    public static void setConnectionType(boolean b) {
        connectionType = b;
    }

    public static boolean isGuiSelector() {
        return guiSelector;
    }

    public static Semaphore getSem() {
        return sem;
    }

    @Override
    public void start(Stage stageStart) throws Exception {
        DrawingDeck.generateDecks();
        stage=stageStart;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/launcher.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedLogo.png")));
        stage.getIcons().add(icon);
        stage.setTitle("Codex Naturalis by IS-AM39 - Launcher");
        stage.setResizable(false);
        final Parent root = fxmlLoader.load();
        final Scene scene = new Scene(root, Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }
    public static Stage getStage(){
        return stage;
    }
}