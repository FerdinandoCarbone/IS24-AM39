package com.example.codexnaturalis;

import javafx.application.Platform;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.codexnaturalis.Client.*;

public class ConnectionManger {
    private Pair<ObjectInputStream, ObjectOutputStream> ioStream;
    boolean typeOfConnection;
    static String serverAddress;
    static int port;
    Socket socket;
    static RemoteServerMethodInterface remoteServerProxy;

    public ConnectionManger(boolean typeOfConnection, Pair<String, Integer> connectionInfo) {
        this.typeOfConnection = typeOfConnection;
        serverAddress = connectionInfo.getKey();
        port = connectionInfo.getValue();
        socket = null;
        remoteServerProxy = null;
    }

    /**
     * Basic operations to prepare the connection process
     *
     * @return returns a boolean: false if connectionSetup fails, true if successful
     */
    public boolean connectionSetup() {
        if (!typeOfConnection) {
            remoteServerProxy = null;
            ObjectInputStream in;
            ObjectOutputStream out;

            try {
                socket = connectionAttempt();
                System.out.println(Colors.PURPLE + "Sono USCITO DA CONNECTION ATTEMPT" + socket + Colors.RESET);
                InputStream sInStream = socket.getInputStream();
                OutputStream sOutStream = socket.getOutputStream();
                System.out.println(Colors.PURPLE + "HO CREATO GLI STREAM" + socket + Colors.RESET);
                out = new ObjectOutputStream(sOutStream);
                in = new ObjectInputStream(sInStream);
                ioStream = new Pair<>(in, out);
                System.out.println(Colors.PURPLE + "Ho generato oIOStreams" + Colors.RESET);
            } catch (HandShakeException | NullPointerException | IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
        } else {
            socket = null;
            try {
                connectionAttempt();
            } catch (HandShakeException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * attempts connection with server
     *
     * @return socket if a socket connection is attempted, null if rmi connection
     * @throws HandShakeException thrown if an issue was encountered
     */
    public Socket connectionAttempt() throws HandShakeException {
        int retryCount = 0;
        int milliseconds = 5000;
        Socket socket;
        while (true) {
            try {
                if (typeOfConnection) {
                    remoteServerProxy = (RemoteServerMethodInterface) Naming.lookup("rmi://" + serverAddress + "/Server");
                    return null;
                } else {
                    socket = new Socket(serverAddress, port);
                    System.out.println(Colors.PURPLE + "Ho generato il socket" + socket + Colors.RESET);
                    return socket;
                }
            } catch (IOException | NotBoundException e) {
                System.err.println("Unable to connect to the server: Trying to reconnect in " + milliseconds / 1000 + "s");
                retryCount++;
                if (retryCount >= 3) throw new HandShakeException();
            }
            try {
                Thread.sleep(milliseconds); // wait before retrying
            } catch (InterruptedException ex) {
                System.err.println("Connection error:" + ex.getMessage());
            }
        }
    }

    /**
     * Starts handshake process
     */
    public void doHandShake() {
        UUID clientID = Client.getClientID();
        String playerNick = Client.getPlayerNick();
        try {
            if (isCrashed()) reHandShake(playerNick, clientID);
            else startHandShake(playerNick, clientID);
        } catch (StupidUserException | IOException | HandShakeException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Handshake processor
     *
     * @param playerNick nickname of player
     * @param clientID   client UUID
     * @throws IOException         thrown by sockets if they have issues sending/receiving messages
     * @throws HandShakeException  thrown if issues are encountered during handshake
     * @throws StupidUserException thrown when user input invalid info multiple times
     */
    private void startHandShake(String playerNick, UUID clientID) throws IOException, HandShakeException, StupidUserException {
        int numOfUsers;
        try {
            if (!typeOfConnection) {
                numOfUsers = socketHandshakeInit(playerNick, clientID);
            } else {
                numOfUsers = remoteServerProxy.getNumOfPlayers();
                Client.setServerHandler(new ServerRMIHandler(playerNick, clientID, this));
            }
            lobbyCreatorCaller(playerNick, clientID, numOfUsers);
            System.out.println("Waiting for everyone to join.");
            //if (isGuiSelector()) LauncherController.alert("Waiting for everyone to join.");
        } catch (HandShakeException e) {
            System.err.println("There was an error during Handshake process: " + e.getMessage());
        }
    }

    /**
     * calls lobbyCreation() if first player is connecting
     *
     * @param numOfUsers number of users returned by the server
     * @throws IOException         thrown by sockets if they have issues sending/receiving messages
     * @throws HandShakeException  thrown if issues are encountered during handshake
     * @throws StupidUserException thrown when user input invalid info multiple times
     */
    private void lobbyCreatorCaller(String playerNick, UUID clientID, int numOfUsers) throws StupidUserException, HandShakeException, IOException {
        switch (numOfUsers) {
            case 0:
                lobbyCreation();
                break;
            case 1, 2, 3:
                if (typeOfConnection) {
                    //here it's also done the nickname check for rmi players
                    while (!remoteServerProxy.joinLobby(new LobbyCreationMessage(playerNick, clientID, numOfUsers))) {
                        playerNick = nickRetype();
                    }
                }
                if (!isGuiSelector()) {
                    System.out.println("Joined existing match...");
                    System.out.println("CurrentPlayers: " + numOfUsers);
                } else {
                    LauncherController.alert("Joined existing match..." + "\nCurrentPlayers: " + numOfUsers, true);
                }
                break;
            default:
                throw new TooManyPlayersException("Lobby is currently full. Wait for the match to end and try again");
        }
    }

    /**
     * @param playerNick user nickname
     * @param clientID   clientID
     * @return returns desired number of players for the match, initially input by lobby creator player
     */
    private int socketHandshakeInit(String playerNick, UUID clientID) throws HandShakeException {
        //server listens for this message in order to attempt a new connection
        Message handshakeMessage = new Message(playerNick, clientID);
        //used to identify which game the user was initially in. In case of a reconnection the value is not null else it is
        handshakeMessage.setMatchID(Client.getMatchID());
        //Server response after first client join request
        Message ackMessage = null;
        //if server accepts client connection and client is deemed addable to match will return number of players chosen by first player
        LobbyCreationMessage handshakeACK = null;
        try {
            ioStream.getValue().writeObject(handshakeMessage);
            ackMessage = (Message) ioStream.getKey().readObject();
            while (ackMessage.getClientID() == null && Objects.equals(ackMessage.getSender(), "!!++***++!!")) {
                playerNick = nickRetype();
                handshakeMessage.setSender(playerNick);
                System.out.println("Handshake: " + handshakeMessage.getSender());
                handshakeMessage.setClientID(clientID);
                ioStream.getValue().reset();
                ioStream.getValue().writeObject(handshakeMessage);
                ackMessage = (Message) ioStream.getKey().readObject();
            }
            handshakeACK = (LobbyCreationMessage) ackMessage;
            Client.setServerHandler(new ServerSocketHandler(playerNick, clientID, this));
        } catch (IOException | ClassNotFoundException e) {
            // System.out.println();
            throw new HandShakeException("Something went wrong during handshake process: " + e.getMessage());
        }
        return handshakeACK.getNumPlayer();
    }

    /**
     * Allows user to retype the nickname if another player on server has a same nickname
     *
     * @return new player nickname and sets it as such
     */
    private String nickRetype() throws IOException {
        String playerNick;
        System.out.println("Username already taken: choose another one");
        System.out.print("New username: ");
        do {
            if (!isGuiSelector()) playerNick = receiveInput();
            else
                playerNick = LauncherController.askStringInputToUser("Username already taken: choose another one", "New username: ");
        }while(Client.isValidNick(playerNick));
        System.out.println("NickRetype: " + playerNick);
        // File (or directory) with old name
        File file = new File("savedata/" + getPlayerNick() + "-matchinfo.cdxn");
        // File (or directory) with new name
        File file2 = new File("savedata/" + playerNick + "-matchinfo.cdxn");
        if (file2.exists())
            return getPlayerNick();
        if (!file.renameTo(file2)) {
            throw new IOException("Encountered problems while changing player nickname");
        }
        Client.setPlayerNick(playerNick);
        /*Client.setClientID(Client.uuidGen());
        writeConnectionTypeOnFile(typeOfConnection?2:1);*/
        System.out.println("PlayerNick in client:" + Client.getPlayerNick());
        return playerNick;
    }

    /**
     * Lobby is created by first player by input how many players will be playing
     *
     * @return returns the desired player count as integer
     * @throws IOException         -
     * @throws HandShakeException  thrown if there is a problem during handshake
     * @throws StupidUserException thrown if player repeatedly inputs invalid data
     */
    private int lobbyCreation() throws IOException, HandShakeException, StupidUserException {
        int desiredPlayerCount = 0;
        System.out.println("No match found. Creating a new one:\nHow many players will be playing?\nWrite a number between 2 and 4:");
        try {
            desiredPlayerCount = inputNumberOfDesiredPlayer();
        } catch (NumberFormatException e) {
            throw new StupidUserException("Unacceptable value was input.\nWrite a number between 2 and 4");
        } catch (StupidUserException e) {
            System.out.println(e.getMessage());
            throw new HandShakeException();
        } finally {
            LobbyCreationMessage msg = new LobbyCreationMessage(null, null, 0);
            msg.setNumPlayer(desiredPlayerCount);
            msg.setSender(Client.getPlayerNick());
            msg.setClientID(Client.getClientID());
            if (!typeOfConnection) {
                ioStream.getValue().writeObject(msg);
            } else {
                remoteServerProxy.createLobby(msg);
            }
            System.out.println("Desired number of players:" + desiredPlayerCount);
        }
        return desiredPlayerCount;
    }

    /**
     * takes input from user for desired player number
     *
     * @return number input from user
     * @throws StupidUserException thrown if player repeatedly inputs invalid data
     */
    private int inputNumberOfDesiredPlayer() throws StupidUserException {
        int desiredPlayerCount = 0;
        int i = 0;
        String warningMessage = "No match found. Creating a new one:\nHow many players will be playing?\nWrite a number between 2 and 4:";

        while (true) {
            if (!Client.isGuiSelector()) {
                if (i > 0) System.out.println("Write a number between 2 and 4: ");
                desiredPlayerCount = getIntInput(4, false);
            } else {
                desiredPlayerCount = LauncherController.askIntInputToUser(warningMessage, "Number of players:");
            }
            if (desiredPlayerCount >= 2 && desiredPlayerCount <= 4) break;
            System.out.println("Unacceptable value was input");
            warningMessage = warningMessage.replaceFirst("No match found. Creating a new one:\nHow many players will be playing?", "Unacceptable value was input");
            if (i == 2) throw new StupidUserException("u stupid bruh");
            i++;
        }
        return desiredPlayerCount;
    }


    private boolean isTypeOfConnection() {
        return typeOfConnection;
    }

    public void setRmiPort(int rmiPort) {
    }

    public Pair<ObjectInputStream, ObjectOutputStream> getIoStream() {
        return ioStream;
    }

    public RemoteServerMethodInterface getRemoteServerProxy() {
        return remoteServerProxy;
    }

    /**
     * Reconnection method that handles reconnections after a client crash, socket pipes brakes and other connections issues.
     * Wrote this @4am (did not sleep because I had issues with synchronization and resetting the socket graph)...
     * I would really love to make it smaller and more modular, but I am too scared to touch it, because it works... fully commented though.
     * Apologies to whoever will be trying to read it, and good luck debugging it!
     */
    //todo: Divide this
    public void reHandShake(String playerNick, UUID clientID) {
        //client join request after crash
        Message handshakeMessage = new Message(playerNick, clientID);
        handshakeMessage.setMatchID(Client.getMatchID());
        BroadCastStartingMessage handshakeACKInfo = null;
        Message tmpMessage;
        boolean amPlayerInTurn;
        try {
            /*
             * If branch reserved for rmi reconnection
             */
            if (typeOfConnection) {
                tmpMessage = remoteServerProxy.reHandShakeRMI(Client.getMatchID());
                Client.setServerHandler(new ServerRMIHandler(playerNick, clientID, this));
                /*
                 * execution stops here if reconnection with rmi is made in another game the user initially started
                 */
                if (tmpMessage.getSender().equals("FORBIDDEN")) {
                    throw new NotSameMatchException("A different match is being played: Wait for the current match to end");
                }
                /*
                 * execution stops here if reconnection with rmi is made before game has started
                 */
                else if (tmpMessage.getSender().equals("MATCHNOTSTARTED")) {
                    System.out.println("Welcome back " + playerNick);
                    System.out.println("Waiting for other players to join...");
                    Client.getServerHandler().start();
                    return;
                }
                /*
                 * reconnecting with rmi in an existing and already running game. Retrieving match info
                 */
                else {
                    handshakeACKInfo = (BroadCastStartingMessage) tmpMessage;
                    Client.getServerHandler().setMessageTurn((GenericTurnMessage) remoteServerProxy.getMessageTurn(clientID));
                    remoteServerProxy.addDisconnectedPlayer(clientID);
                }

            }
            /*
             * If branch reserved for socket reconnection
             */
            else {
                System.out.println(Colors.PURPLE + "Starting Handshake" + Colors.RESET);
                ioStream.getValue().writeObject(handshakeMessage);
                //System.out.println("Flushing stream");
                tmpMessage = (Message) ioStream.getKey().readObject();
                System.out.println(tmpMessage.getClass());
                Client.setServerHandler(new ServerSocketHandler(playerNick, clientID, this));
                /*
                 * execution stops here if reconnection with socket is made in another game the user initially started
                 */
                if (tmpMessage.getSender().equals("FORBIDDEN")) {
                    throw new NotSameMatchException("A different match is being played: Wait for the current match to end");
                }
                /*
                 * execution stops here if reconnection with socket is made before game has started
                 */
                else if (tmpMessage.getSender().equals("MATCHNOTSTARTED")) {
                    //LobbyCreationMessage beforeStartReConnectionMessage = (LobbyCreationMessage) tmpMessage;
                    System.out.println("Welcome back " + playerNick);
                    Client.getServerHandler().start();
                    return;
                }
                /*
                 * Retrieving match information
                 */
                handshakeACKInfo = (BroadCastStartingMessage) tmpMessage;
                System.out.println("CASTED");
                //System.out.println(Colors.PURPLE+ "Settato correttamente" +Colors.RESET);
            }
            System.out.println(Colors.PURPLE + "Setting Up field..." + Colors.RESET);
            System.out.println(Colors.PURPLE + "Retrieving Current turn info..." + Colors.RESET);
            /*
             * True if current reconnecting player has to play after reconnection
             * */
            amPlayerInTurn = clientID.compareTo(handshakeACKInfo.getClientID()) == 0;
            /*
             * InitialMatchSetup after a reconnection. All information is resent from server back to client
             * */
            Client.setPlayer(handshakeACKInfo.getPlayers().get(getClientID()));
            handshakeACKInfo.getPlayers().remove(getClientID());
            ArrayList<Player> players = new ArrayList<>(handshakeACKInfo.getPlayers().values());
            Client.setOtherPlayers(players);
            Client.setCurrentlyPlayingPlayers(handshakeACKInfo.getCurrentlyPlaying());
            /*
             * If player disconnected before he was able to set his Starter and secret Objective cards, he will be sent here\
             * */
            if (getPlayer().getPlayerDeck().getSecretObjectiveCard() == null) {
                /*For rmi is necessary to restart the handler here, because the heartbeat keepalive function needs to kick in*/
                System.out.println(Colors.PURPLE + "Choosing card..." + Colors.RESET);
                if (typeOfConnection) Client.getServerHandler().start();
                handshakeACKInfo = secretSelector(handshakeACKInfo);
                if (!typeOfConnection) ioStream.getValue().writeObject(handshakeACKInfo);
                else remoteServerProxy.send(handshakeACKInfo);
            }
            if (Client.isGuiSelector()) Client.getSem().release();//todo: check if breaks tui
            Client.getServerHandler().setFirstBroadCastWasReceived(true);
            /*
             * Socket still needs to retrieve his GenericTurn Message. Here info is retrieved
             */
            if (!typeOfConnection && amPlayerInTurn) {
                GenericTurnMessage msg = (GenericTurnMessage) ioStream.getKey().readObject();
                Client.getServerHandler().setMessageTurn(msg);
            }
            /*
             * ServerHandlers are restarted here if needed
             * */
            if (!Client.getServerHandler().isAlive()) Client.getServerHandler().start();
            /*
             * Current myTurn flag is set here
             */
            setMyTurn(amPlayerInTurn);
            //System.out.println("Done!");
            if (isGuiSelector()) {
                Client.getSem().release();
                LauncherController.loadGameScene();
            }
            if (amPlayerInTurn && typeOfConnection) {
                if (isGuiSelector()) {
                    MainController.alert("It's your turn",false);
                } else System.out.println("\nIt's your turn!");
            }
        } catch (Exception e) {
            System.out.println("Error while reconnecting after crash: " + e.getMessage());
            if (e.getClass().equals(NotSameMatchException.class)) {
                System.out.println("Save data can be deleted: deleting it now...");
                String filePath = "savedata/" + playerNick + "-matchInfo.cdxn";
                File file = new File(filePath);
                boolean deleted = file.delete();
                if (!deleted) System.out.println("Unable to delete save file");
            }
            clientDisconnect();
        }
        if (isGuiSelector()) MainController.alert("All players' fields were correctly received",true);
        else System.out.println("All players' fields were correctly received");
        Client.setCurrentGameStatus(true);
    }

    /**
     * During reconnection, if user was using a rmi this function will be called
     *
     * @return a pair containing a message and a boolean retrieved from server
     * @throws NotSameMatchException thrown if server is currently playing a different match to the one the player is trying to reconnect
     * @throws RemoteException       thrown when rmi fails to return requested messages
     */
    private Pair<Boolean, Message> rmiReconnect(Message handshakeMessage) throws NotSameMatchException, RemoteException {
        BroadCastStartingMessage handshakeACKInfo;
        Message tmpMessage;
        String playerNick = handshakeMessage.getSender();
        UUID clientID = handshakeMessage.getClientID();
        tmpMessage = remoteServerProxy.reHandShakeRMI(Client.getMatchID());
        Client.setServerHandler(new ServerRMIHandler(playerNick, clientID, this));
        /*
         * execution stops here if reconnection with rmi is made in another game the user initially started
         */
        if (tmpMessage.getSender().equals("FORBIDDEN")) {
            throw new NotSameMatchException("A different match is being played: Wait for the current match to end");
        }
        /*
         * execution stops here if reconnection with rmi is made before game has started
         */
        else if (tmpMessage.getSender().equals("MATCHNOTSTARTED")) {
            System.out.println("Welcome back " + playerNick);
            System.out.println("Waiting for other players to join...");
            Client.getServerHandler().start();
            return new Pair<>(false, tmpMessage);
        }
        /*
         * reconnecting with rmi in an existing and already running game. Retrieving match info
         */
        else {
            handshakeACKInfo = (BroadCastStartingMessage) tmpMessage;
            Client.getServerHandler().setMessageTurn((GenericTurnMessage) remoteServerProxy.getMessageTurn(clientID));
        }
        return new Pair<>(true, handshakeACKInfo);
    }

    /**
     * Essential piece of code for Client.initialMatchSetup() and reHandshake() methods. Lets you choose your secret objective card and face up or down of starting card
     */
    public static BroadCastStartingMessage secretSelector(BroadCastStartingMessage handshakeACKInfo) throws IOException, StupidUserException, InterruptedException {
        ObjectiveCard chosenCard;
       /* Semaphore sam = new Semaphore(0);
        if(isGuiSelector()) sam.acquire();*/
        chosenCard = Client.getPlayer().chooseSecretObj(handshakeACKInfo.getSecretObjectiveCards(Client.getClientID()));
        ArrayList<ObjectiveCard> tmpList = new ArrayList<>(Collections.singletonList(chosenCard));
        handshakeACKInfo.setSelectedSecret(tmpList);
        //todo PRINT StarterCard?
        Client.getPlayer().getPlayerDeck().getStarterCard().printCardFrontAndBack();
        //if (Client.isGuiSelector());
        System.out.println("How do you want to face the starting card");
        System.out.println("1 - face Up\n2 - face Down");
        Boolean cardFace;
        do {
            try {
                cardFace = selectStarterCardFace();
            } catch (IOException e) {
                cardFace=null;
            }
        }while(cardFace==null);
        handshakeACKInfo.setStarterCardFace(cardFace);
        return handshakeACKInfo;
    }

    /**
     * StarterCard face selector
     *
     * @return returns true if starterCard faces up, false if faces down
     * @throws IOException          thrown if an invalid input is made
     * @throws InterruptedException thrown if semaphore can't acquire thread
     */
    private static boolean selectStarterCardFace() throws IOException, InterruptedException {
        AtomicInteger i = new AtomicInteger();
        Semaphore sem = new Semaphore(0);
        if (Client.isGuiSelector()) {
            StarterCard cardStarter;
            cardStarter = Client.getPlayer().getPlayerDeck().getStarterCard();
            if (!Client.isCrashed()) {
                Platform.runLater(() -> {
                    try {
                        i.set(LauncherController.selectAStarterCardDialog(cardStarter, "Select a face:"));
                        sem.release();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                sem.acquire();
            } else {
                i.set(LauncherController.selectAStarterCardDialog(cardStarter, "Select a face:"));
            }
        } else {
            i.set(getIntInput(2, false));
        }
        return switch (i.get()) {
            case 1 -> true;
            case 2 -> false;
            default -> throw new IOException("There was an error trying to read the string");
        };
    }
}
