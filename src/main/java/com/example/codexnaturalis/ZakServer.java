package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class ZakServer {
    static boolean gameStarted = false;
    static Match match;
    static ServerConnectionManager serverConMan;
    static Pair<String, Integer> connectionInfo;

    //static int playerCounter = 1;
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int port = 8081;
        if (!args[0].isBlank()) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("An invalid port number was input\nFallback to 8081");
            }

            connectionInfo = new Pair<>("Server", port);
        } else {
            System.err.println("Cannot start server: Start server with an integer parameter as port");
            System.exit(0);
        }
        serverSetupProcedure();

    }

    /**
     * As soon as the server starts, this function is called. Function will print its ASCII Art and set up some of its critical components to allow client connections.
     * The ServerConnectionManager object is initialized, which will handle all communications with clients (sockets and RMI).
     * A soon as every player is connected match will start
     */
    private static void serverSetupProcedure() throws RemoteException, MalformedURLException {
        try {
            serverStart();
        } catch (Exception e) {
            System.err.println("Server Failure: " + e.getMessage());
        }
        serverConMan.acceptConnection(false);
        try {
            matchStart();
        } catch (Exception e) {
            System.err.println("Server Failure: " + e.getMessage());
        }
    }

    /**
     * Prints ASCII Art and initializes ServerConnectionManager
     *
     * @throws IOException
     */
    public static void serverStart() throws IOException {
        gameStarted = false;
        serverConMan = new ServerConnectionManager(connectionInfo, 1099);
        System.out.println(
                """
                         _____                                                                      _____\s
                        ( ___ )--------------------------------------------------------------------( ___ )
                         |   |                                                                      |   |\s
                         |   |   ____          _           _   _       _                   _ _      |   |\s
                         |   |  / ___|___   __| | _____  _| \\ | | __ _| |_ _   _ _ __ __ _| (_)___  |   |\s
                         |   | | |   / _ \\ / _` |/ _ \\ \\/ /  \\| |/ _` | __| | | | '__/ _` | | / __| |   |\s
                         |   | | |__| (_) | (_| |  __/>  <| |\\  | (_| | |_| |_| | | | (_| | | \\__ \\ |   |\s
                         |   |  \\____\\___/ \\__,_|\\___/_/\\_\\_| \\_|\\__,_|\\__|\\__,_|_|  \\__,_|_|_|___/ |   |\s
                         |   | / ___|  ___ _ ____   _____ _ __                                      |   |\s
                         |   | \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|                                     |   |\s
                         |   |  ___) |  __/ |   \\ V /  __/ |                                        |   |\s
                         |   | |____/ \\___|_|    \\_/ \\___|_|                                        |   |\s
                         |___|                                                                      |___|\s
                        (_____)--------------------------------------------------------------------(_____)""");
        System.out.println("- developed by Team AM39");
    }

    /**
     * Selects CommonObjective Cards, generates the model object(match) and Calls startingFieldClientSetup(). As soon as every client chooses his secret Objective card and places
     * their StarterCard, This function sends a Player greeting TextMessage and the game is started by sending a GenericTurnMessage to the first player.
     *
     * @throws Exception, thrown for a general failure in server. reports to severSetupProcedure or Main.
     *                    The Thread stops here in the while(true) so that the person hosting the server can give it some commands
     */
    public static void matchStart() throws Exception {
        String serverCommand;
        ArrayList<Player> players = new ArrayList<>(serverConMan.getPlayers());
        match = new Match(players, new ScoreTracker());
        startingFieldClientSetup();
        System.out.println("Match is about to start: Waiting for all players to choose a secret objective");
        while (!match.areAllSecretObjectiveSet()) Thread.onSpinWait();
        welcomePlayer();
        gameStarted = true;
        System.out.println("Match has began");
        StandardMatchMessage stdMessage = match.chooseRandomFirstPlayer();
        GenericTurnMessage message = new GenericTurnMessage(connectionInfo.getKey(), stdMessage.getClientID(), match.getCoveredCards(), stdMessage.getPublicCardsNewState(), null); //match loop starts here
        ServerConnectionManager.sendMessage(stdMessage.getClientID(), message);
        while (true) {
            serverCommand = getInput();
            interpretInput(serverCommand);
        }
    }

    /**
     * Sends a fieldSetupMessage which sends over to all clients the commonObjectiveCards and the Pair of secretObjectiveCards for the player to choose
     *
     * @throws IOException
     */
    private static void startingFieldClientSetup() throws IOException {
        BroadCastStartingMessage fieldSetupMessage;
        ArrayList<ObjectiveCard> commonObjectiveCard;
        commonObjectiveCard = DrawingDeck.drawCommonObjective();
        match.setCommonObjectives(commonObjectiveCard);
        fieldSetupMessage = new BroadCastStartingMessage(connectionInfo.getKey(), null, serverConMan.getHashClient(), commonObjectiveCard, match.getTwoSecretObjectiveCards());
        fieldSetupMessage.setMatchID(match.getMatchID());
        ServerConnectionManager.sendBroadCastMessage(fieldSetupMessage);
    }

    /**
     * Generates Player greeting TextMessage while sending over to all Players the newly set StarterCards
     *
     * @throws IOException
     */
    private static void welcomePlayer() throws IOException {
        String text = "Match is about to start\nPlayers:\n";
        HashMap<UUID, StarterCard> hashStart = new HashMap<>();
        Collection<Player> players = serverConMan.getPlayers();
        for (Player p : players) {
            hashStart.put(p.getPlayerID(), p.getPlayerDeck().getStarterCard());
            System.out.println(hashStart.get(p.getPlayerID()));
            text = text.concat(p.getPlayerName() + "\n");
        }
        ServerConnectionManager.sendBroadCastMessage(new TextMessage(connectionInfo.getKey(), null, text, "Everyone"));
        ServerConnectionManager.sendBroadCastMessage(new BroadCastStandardMessage(connectionInfo.getKey(), null, hashStart));


    }

    /**
     * stops ClientHandler thread that handles connection with client whose clientID is provided
     *
     * @param clientID clientID belonging to the client whose clientHandler thread needs to be killed
     */
    public static void stopThread(UUID clientID) {
        //todo: fare le opportune modifiche a match
        if (serverConMan.getHandlers().values().size() == 1) {
            ServerConnectionManager.setNumPlayers(0);
            if (ServerConnectionManager.isFirstPlayer()) ServerConnectionManager.setFirstPlayer(false);
        }
        try {
            serverConMan.getHandlers().get(clientID).setHasToRun(false);
        } catch (Exception e) {
            System.err.println("Unable to stop process:" + e.getMessage());
        }
        try {
            String playerName = ServerConnectionManager.hashClient.get(clientID).getPlayerName();
            ServerConnectionManager.hashPlayer.remove(ServerConnectionManager.hashClient.get(clientID));
            ServerConnectionManager.hashClient.remove(clientID);
            System.out.println(playerName + " left the game and was unable to reconnect");
            System.out.print("Command:");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        serverConMan.getHandlers().remove(clientID);
    }

    private static String getInput() {
        Scanner scanner = new Scanner(System.in);
        String input = null;
        do {
            System.out.print("Command: ");
            try {
                input = scanner.nextLine();
            } catch (NoSuchElementException e) {
                continue;
            }
        } while (Objects.equals(input, "\n") || input == null);
        //scanner.close();
        return input;
    }

    /**
     * Interprets the command given to the server and calls other methods
     *
     * @param serverCommand the command given to the server
     * @throws IOException
     * @throws NotBoundException
     */
    private static void interpretInput(String serverCommand) {
        switch (serverCommand.toLowerCase()) {
            case "close":
                System.out.println("Server shutting down");
                System.exit(0);
                break;
            case "kick":
                /*List<Player> players = serverConMan.getPlayers().stream().toList();
                int i=1;
                System.out.println("What player would you like to kick from match?");
                for(Player p: players) {
                    System.out.println(i+ " - "+ p.getPlayerName());
                    ++i;
                }
                i=getIntInput(players.size(),false);
                kick(players.get(i-1).getPlayerID());*/
                break;
            case "restart":
                //todo: cose inutili
                /*for(ClientHandler p: ServerConnectionManager.handlers.values()){
                    p.setHasToRun(false);
                }
                ServerConnectionManager.handlers.clear();
                for(Socket s:ServerConnectionManager.hashPlayer.values()) s.close();
                ServerConnectionManager.hashPlayer.clear();
                ServerConnectionManager.serverSocket.close();
                ServerConnectionManager.rmiListener.shutRMIConnection();
                ServerConnectionManager.rmiListener.setHasToRun(false);
                match = null;
                serverConMan = null;
                gameStarted = false;
                serverSetupProcedure();*/
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    /**
     * Kicks a player from a match given its clientID
     *
     * @param clientID : belongs to the user the server needs to kick
     */
    private static void kick(UUID clientID) {
        try {
            ClientHandler h = serverConMan.getHandlers().get(clientID);
            h.sendMessage(new TextMessage("Server", null, h.getClientName() + " has been kicked from server", h.getClientName()));
            h.setHasToRun(false);
        } catch (Exception e) {
            System.err.println("Unable to stop process:" + e.getMessage());
        }
        try {
            String playerName = ServerConnectionManager.hashClient.get(clientID).getPlayerName();
            ServerConnectionManager.hashPlayer.remove(ServerConnectionManager.hashClient.get(clientID));
            ServerConnectionManager.hashClient.remove(clientID);
            System.out.println(playerName + " was kicked from server");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        serverConMan.getHandlers().remove(clientID);
    }

    public static int getNumOfPlayers() {
        return serverConMan.getNumPlayers();
    }

    private static int getIntInput(int range, boolean type) {
        Integer thingToParse = null;
        while (true) {
            try {
                thingToParse = Integer.parseInt(getInput());
            } catch (Exception e) {
                System.out.println("Invalid input: try again");
                continue;
            }
            if (thingToParse <= range && thingToParse >= 0) break;
        }
        if (type) return thingToParse - 1;
        else return thingToParse;
    }

}
