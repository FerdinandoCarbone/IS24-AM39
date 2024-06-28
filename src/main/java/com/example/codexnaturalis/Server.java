package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class Server {
    static boolean gameStarted = false;
    static Match match;
    private static boolean isCrashed;
    static ServerConnectionManager serverConMan;
    static Pair<String, Integer> connectionInfo;
    private static ServerStateSaver serverSaver;
    private static String[] serverArgs;

    //static int playerCounter = 1;
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        serverArgs = args;
        int port = 8081;
        checkForSaveData();
        if(isCrashed) serverSaver.retrieveNecessaryStartingInfo();
        else if (args.length==1) {
            connectionInfo = new Pair<>("Server", port);
        } else if (args.length== 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("An invalid port number was input\nFallback to 8081");
                port = 8081;
            }

            connectionInfo = new Pair<>("Server", port);
        }

        else {
            System.err.println("Cannot start server: Start server with an integer parameter as port");
            System.exit(0);
        }
        serverSetupProcedure();

    }

    private static void checkForSaveData() {
        serverSaver = new ServerStateSaver();
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
        if(isCrashed)Collections.fill(match.getPlayerIds(), null);
        serverConMan.acceptConnection(isCrashed);
        try {
            if(!isCrashed)matchStart();
            else matchRestart();
        } catch (Exception e) {
            System.err.println("Server Failure: " + e.getMessage());
        }
    }

    /**
     * Method managing server actions when match is restarting
     * @throws IOException
     */
    private static void matchRestart() throws IOException {
        while (!restartMatchCondition()) Thread.onSpinWait();
        reWelcomePlayer();
        serverIdle();
    }

    /**
     * Checks if condition of match restart are met
     * @return true if match can restart, false otherwise
     */
    public static boolean restartMatchCondition() {
        int counterIDs = 0;
        int counterHandlers = 0;
        ArrayList<UUID> ids = match.getPlayerIds();
        for (UUID id : ids) {
            if (id != null) ++counterIDs;
            if (ServerConnectionManager.handlers.get(id)!=null) ++counterHandlers;
        }
        //System.out.println("CounterID:"+counterIDs+"handlers:"+ counterHandlers);
        return counterIDs >= 2 && counterHandlers>=2;
    }

    /**
     * If disconnected player is re added to the match, displays welcome message again
     * @throws IOException
     */
    private static void reWelcomePlayer() throws IOException {
        isCrashed = false;
        BroadCastStandardMessage bds = new BroadCastStandardMessage(null, null, null);
        HashMap<String, Boolean> currPlaying = new HashMap<>();
        for (int i = 0; i < ServerConnectionManager.hashClient.size(); i++){
            if (Server.match.getPlayerIds().get(i) != null)
                currPlaying.put(ServerConnectionManager.hashClient.get(Server.match.getPlayerIds().get(i)).getPlayerName(), true);
            else currPlaying.put(Server.match.getPlayers().get(i).getPlayerName(), false);}
        bds.setCurrPlaying(currPlaying);
        ServerConnectionManager.sendBroadCastMessage(bds);
        ServerConnectionManager.sendMessage(match.getCurrentPlayerID(),new GenericTurnMessage(connectionInfo.getKey(), match.getCurrentPlayerID(), match.getCoveredCards(), match.getPublicCards(), null)); //match loop starts here
    }


    /**
     * Prints ASCII Art and initializes ServerConnectionManager
     *
     * @throws IOException
     */
    public static void serverStart() throws IOException {
        gameStarted = false;
        serverConMan = new ServerConnectionManager(connectionInfo, 1099,isCrashed);
        if(isCrashed) {
            serverSaver.retrieveCrucial();
        }
        serverSaver.saveInitialState();
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
        ArrayList<Player> players = new ArrayList<>(ServerConnectionManager.getPlayers());
        match = new Match(players, new ScoreTracker());
        startingFieldClientSetup();
        System.out.println("Match is about to start: Waiting for all players to choose a secret objective");
        while (!match.areAllSecretObjectiveSet()) Thread.onSpinWait();
        serverSaver.saveState();
        welcomePlayer();
        gameStarted = true;
        System.out.println("Match has began");
        StandardMatchMessage stdMessage = match.chooseRandomFirstPlayer();
        GenericTurnMessage message = new GenericTurnMessage(connectionInfo.getKey(), stdMessage.getClientID(), match.getCoveredCards(), stdMessage.getPublicCardsNewState(), null); //match loop starts here
        ServerConnectionManager.sendMessage(stdMessage.getClientID(), message);
        serverSaver.saveState();
        serverIdle();
    }

    private static void serverIdle() {
        String serverCommand;
        while (true) {
            serverCommand = getInput();
            interpretInput(serverCommand);
        }
    }

    public static ServerStateSaver getServerSaver() {
        return serverSaver;
    }

    /**
     * Sends a fieldSetupMessage which sends over to all clients the commonObjectiveCards and the Pair of secretObjectiveCards for the player to choose
     *
     * @throws IOException
     */
    private static void startingFieldClientSetup() throws IOException {
        BroadCastStartingMessage fieldSetupMessage;
        ArrayList<ObjectiveCard> commonObjectiveCard;
        commonObjectiveCard = match.getDeck().drawCommonObjective();
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
        for (UUID p : match.getPlayerIds()) {
            hashStart.put(p, serverConMan.getHashClient().get(p).getPlayerDeck().getStarterCard());
            System.out.println(hashStart.get(p));
            text = text.concat(serverConMan.getHashClient().get(p).getPlayerName() + "\n");
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
        ClientHandler tmpHand = serverConMan.getHandlers().get(clientID);
        if (serverConMan.getHandlers().values().size() == 1) {
            //ServerConnectionManager.setNumPlayers(0);
            if (ServerConnectionManager.isFirstPlayer()) ServerConnectionManager.setFirstPlayer(false);
        }
        try {
            String playerName = ServerConnectionManager.hashClient.get(clientID).getPlayerName();
            System.out.println(playerName + " left the game or was unable to reconnect");
            System.out.print("Command:");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (tmpHand instanceof SocketClientHandler) {
            try {
                ((SocketClientHandler) tmpHand).getSocket().close();
                ServerConnectionManager.getSocketListener().setHasToRun(true);
            } catch(IOException e){
                System.err.println("Unable to close socket on disconnect");
            }
            tmpHand.setHasToRun(false);

        }
    }

    /**
     * Gets input from user
     * @return
     */
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
                /*try {
                    resetServer();
                }
                catch(Exception e){
                    System.err.println("An error occurred while resetting: "+ e.getMessage()+"\nForcing restart...");
                    System.exit(0);
                }*/
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

    public static int getIntInput(int range, boolean type) {
        Integer thingToParse = null;
        while (true) {
            try {
                thingToParse = Integer.parseInt(getInput());
            } catch (Exception e) {
                System.out.println("Invalid input: try again");
                continue;
            }
            if (thingToParse <= range && thingToParse >= 0) break;
            System.out.println("Invalid input: try again");
        }
        if (type) return thingToParse - 1;
        else return thingToParse;
    }

    public static boolean isCrashed() {
        return isCrashed;
    }
    public static void setIsCrashed(boolean isCrashed) {
        Server.isCrashed = isCrashed;
    }
    private static void resetServer() throws IOException, NotBoundException {
        isCrashed = false;
        ServerConnectionManager.socketListener.interrupt();
        ServerConnectionManager.rmiListener.interrupt();
        for(ClientHandler h : ServerConnectionManager.handlers.values()) {
            if(h instanceof SocketClientHandler) ((SocketClientHandler) h).getSocket().close();
            h.interrupt();
        }
        ServerConnectionManager.serverSocket.close();
        Server.getServerSaver().resetSave();
        main(serverArgs);

    }

}
