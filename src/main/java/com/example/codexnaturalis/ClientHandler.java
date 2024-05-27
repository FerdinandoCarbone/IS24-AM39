package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class ClientHandler extends Thread implements Runnable {
    private ServerConnectionManager connMan;
    public boolean reconnect;
    private final String clientName;
    private final UUID clientID;
    private boolean secretWasChosen;
    public volatile boolean hasToRun;

    public ClientHandler(String clientName, UUID clientID, ServerConnectionManager connMan) {
        this.clientName = clientName;
        this.clientID = clientID;
        this.connMan = connMan;
        this.secretWasChosen = false;
        this.hasToRun = true;
        this.reconnect = false;
    }

    public void setSecretWasChosen(boolean secretWasChosen) {
        this.secretWasChosen = secretWasChosen;
    }

    public void clientDisconnected() {
        if (Server.match != null) {
            StandardMatchMessage newTurnStatus = Server.match.removeDisconnectedPlayer(clientID);
            UUID nextPlayer = newTurnStatus.getNextPlayerId();
            Server.getServerSaver().saveState();
            try {
                //custom use of sender: used to identify the winner
                if (newTurnStatus.getClientID() == null) {
                    ServerConnectionManager.sendBroadCastMessage(new TextMessage("Server:", null, "only you in the match", "Everyone"));
                    ServerConnectionManager.sendBroadCastMessage((new EndMatchMessage(null, null, newTurnStatus.getSender(), null, null, null)));
                    //match ended with no issues --> save data can be reset
                    Server.getServerSaver().resetSave();
                    endOfTheGame();
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while sending winning message");
            }
            try {
                if (nextPlayer != null)
                    ServerConnectionManager.sendMessage(nextPlayer, new GenericTurnMessage("Server", null, Server.match.getCoveredCards(), Server.match.getPublicCards(), null));
            } catch (IOException e) {
                System.err.println(e.getMessage() + ": Error while sending new Generic turn message ");
            }
        }
        Server.stopThread(getClientID());
    }

    public boolean getSecretWasChosen() {
        return this.secretWasChosen;
    }

    public void sendMessage(Message message) throws IOException {
        System.out.println("wrong function");
    }

    public void reconnectionAlert() throws IOException {
        ServerConnectionManager.sendBroadCastMessage(new TextMessage("Server",null,clientName+" rejoined the match","Everyone"));
    }

    public String getClientName() {
        return clientName;
    }

    public UUID getClientID() {
        return clientID;
    }

    public ServerConnectionManager getConnMan() {
        return connMan;
    }

    public void textMessageHandler(TextMessage message) throws IOException {
        UUID recipientClientID = null;
        String recipient = message.getRecipient();
        //System.out.println(recipient+" "+ recipientClientID);
        if (Objects.equals(recipient, "Everyone")) getConnMan().sendBroadCastMessage(message);
        else {
            for (Player p : getConnMan().getPlayers()) {
                if (Objects.equals(p.getPlayerName(), recipient)) {
                    recipientClientID = p.getPlayerID();
                    break;
                }
            }
            getConnMan().sendMessage(recipientClientID, message);
        }
        System.out.println("\n" + message.getSender() + " to " + message.getRecipient() + ": " + message.getTextMessage());
        System.out.print("Command: ");

    }
public void reset(){

}
    public void broadCastMessageHandler(BroadCastStandardMessage message) {
    }

    public void genericTurnMessageHandler(GenericTurnMessage message) throws IOException, ClassNotFoundException {
        StandardMatchMessage newStatus = Server.match.genericTurn(message);
        Server.getServerSaver().saveState();
        System.out.println("currentPlayer:" + newStatus.getClientID());
        if (newStatus instanceof EndMatchMessage) {
            ServerConnectionManager.sendBroadCastMessage(newStatus);
            //match ended with no issues --> save data can be reset
            Server.getServerSaver().resetSave();
            endOfTheGame();
            return;
        }
        ArrayList<ResourceGoldCard> coveredCards = Server.match.getCoveredCards();
        ArrayList<ResourceGoldCard> publicCards = newStatus.getPublicCardsNewState();
        for (ResourceGoldCard c : coveredCards) System.out.println(Colors.BLUE + c.getIdCard() + Colors.RESET);
        for (ResourceGoldCard c : publicCards) System.out.println(Colors.RED + c.getIdCard() + Colors.RESET);
        GenericTurnMessage newTurn = new GenericTurnMessage("Server", newStatus.getNextPlayerId(), coveredCards, publicCards, null);
        newTurn.printCoveredCards();
        newTurn.printPublicCards();
        ServerConnectionManager.sendBroadCastMessage(newStatus);
        ServerConnectionManager.sendMessage(newStatus.getNextPlayerId(), newTurn);
    }

    public void endOfTheGame() {
        Server.gameStarted = false;
        Server.match = null;
        System.out.println("Winner:" + Server.match.getFinalWinners()+"\nRestart server to play a new game");
        System.exit(0);
        //todo: match reset and restart function to initialize everything
    }

    public void secretObjectiveSelector(BroadCastStartingMessage message) {
        ObjectiveCard cardToKeep = message.getSelectedSecret();
        ServerConnectionManager.hashClient.get(clientID).getPlayerDeck().setSecretObjectiveCard(cardToKeep);
        Server.match.putBackOtherSecretObjectiveCard(clientID, cardToKeep);
        //ArrayList<Player> players = Server.match.getPlayers();
        ServerConnectionManager.hashClient.get(clientID).placeStarterCard(message.getStarterCardFace());
        this.secretWasChosen = true;
    }

    public void setHasToRun(boolean b) {
        this.hasToRun = b;
    }

    public boolean getReconnect() {
        return reconnect;
    }
}

class RMIClientHandler extends ClientHandler {

    Message rmiDeliverer;
    ArrayList<Message> queue;
    private volatile boolean heartBeat;
    volatile boolean hasToDeliver;

    public RMIClientHandler(String clientName, UUID clientID, ServerConnectionManager connMan) {
        super(clientName, clientID, connMan);
        rmiDeliverer = null;
        hasToDeliver = false;
        queue = new ArrayList<>();
        heartBeat = false;
    }

    @Override
    public void reset(){
        hasToRun = false;
        queue = new ArrayList<>();
        hasToDeliver = false;
        rmiDeliverer=null;
    }
    @Override
    public void run() {
        while (true) {
            while(!hasToRun) Thread.onSpinWait();
            try {
                if (getSecretWasChosen()) {
                    heartBeat = false;
                    Thread.sleep(10000);
                }
                if (!heartBeat)
                    throw new ClientAbruptlyDisconnectedException("Client " + getClientName() + " disconnected: Trying to reconnect...");
            } catch (InterruptedException e) {
                System.err.println("Thread Sleep issue:" + e.getMessage());
            } catch (ClientAbruptlyDisconnectedException e) {
                System.err.println(e.getMessage());
                if (tryReconnectToClient()) continue;
                try {
                    TextMessage disconnectionNotify = new TextMessage("Server", null, getClientName() + " disconnected from the server and was unable to reconnect", "Everyone");
                    disconnectionNotify.setDisconnectedClient(getClientName());
                    ServerConnectionManager.sendBroadCastMessage(disconnectionNotify);
                } catch (IOException ex) {
                    System.err.println("Unable to broadcast disconnection Message");
                }
                clientDisconnected();
            }
        }
    }

    private boolean tryReconnectToClient() {
        for (int i = 0; i < 3; i++) {
            if (heartBeat) return true;
            System.err.println("Failed to reconnect: retrying in 7s");
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
        reset();
        return false;
    }

    @Override
    public void sendMessage(Message msg) {
        queue.addLast(msg);
        if (!hasToDeliver) hasToDeliver = true;
        if (msg instanceof StandardMatchMessage) {
            System.out.println("Sending updates to:" + getClientName());
        }
        //todo: reconnection attempt
        else if (!(msg instanceof TextMessage)) {
            msg.setClientID(getClientID());
            msg.setSender(getClientName());
        }
    }

    public void retrieveMessage(Message message) throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.", "");
        switch (messageType) {
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStandardMessage":
                broadCastMessageHandler((BroadCastStandardMessage) message);
                break;
            case "BroadCastStartingMessage":
                secretObjectiveSelector((BroadCastStartingMessage) message);
                break;
            /*case "Message":
                tryReconnectToClient();
                break;*/
            default:
                throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }

    public Message getRmiDeliverer() {
        return queue.getFirst();
    }

    public void setHasToDeliver(boolean hasToDeliver) {
        this.hasToDeliver = hasToDeliver;
    }

    public void setHeartBeat(boolean b) {
        this.heartBeat = b;
    }
}


class SocketClientHandler extends ClientHandler {
    private Socket socket;
    private ObjectOutputStream outClient;
    private ObjectInputStream inClient;
    private Semaphore samviseGamgee;

    public SocketClientHandler(String clientName, Socket socket, UUID clientID, Pair<ObjectInputStream, ObjectOutputStream> iostream, ServerConnectionManager connMan) throws IOException {
        super(clientName, clientID, connMan);
        this.socket = socket;
        this.outClient = iostream.getValue();
        this.inClient = iostream.getKey();
        this.reconnect = false;
        this.samviseGamgee = new Semaphore(0);
    }
    public void reset(Pair<ObjectInputStream, ObjectOutputStream> iostream, Socket socket) {
        this.inClient = iostream.getKey();
        this.outClient = iostream.getValue();
        this.socket = socket;
        System.out.println("This shit was reset");
    }

    public Semaphore getSamviseGamgee() {
        return samviseGamgee;
    }

    @Override
    public void run() {
        while (true) {
            try {
                messageReceiver();
            } catch (IOException | ClassNotFoundException | WrongMessageConversionException e) {
                if (!getConnMan().getServerSocket().isClosed()) {
                    reconnect = true;
                } else {
                    System.err.println("Client Handler failure: " + e.getMessage());
                    reconnect = false;
                    break;
                }
            }
            try {
                /*Match has started but player disconnected branch*/
                if (Server.gameStarted && reconnect) {
                    throw new ClientAbruptlyDisconnectedException("Client " + getClientName() + " abruptly disconnected: Attempting reconnection");
                }
                /*Match has not yet started*/
                else if (!Server.gameStarted && reconnect && (Server.match == null || Server.match.getFinalWinners().isEmpty())) {
                    System.out.println("Riconnessione dopo crash prima inizio partita");
                    throw new ClientAbruptlyDisconnectedException(getClientName() + " abruptly disconnected: Attempting reconnection");
                }
                /*match has ended successfully*/
                else if (!reconnect && !Server.match.getFinalWinners().isEmpty()) {
                    System.out.println("Partita finita");
                    break;
                }
            } catch (ClientAbruptlyDisconnectedException e) {
                System.err.println(e.getMessage());
                if (tryReconnectClient()) continue;
                reconnect = false;
                break;
            }
        }
        clientDisconnected();
    }

    private boolean tryReconnectClient() {
        int i;
        System.err.println(Colors.PURPLE+"Trying to re-establish a connection with client:"+Colors.RESET);
        Pair<ObjectInputStream, ObjectOutputStream> oIOstream = null;
        try {
            for (i = 0; i < 3; i++) {
                try {
                    oIOstream = getConnMan().acceptSocketRMIConnections(null, true);
                }
                catch (Exception e) {
                    System.out.println();
                    System.err.println("Unable to establish a connection: an exception was thrown - retrying in 7s : "+e.getMessage());
                    Thread.sleep(7000);
                }
                if (oIOstream == null) {
                    if (i == 2) {
                        try {
                            ServerConnectionManager.sendBroadCastMessage(new TextMessage("Server", null, getClientName() + " disconnected from the server and was unable to reconnect", "Everyone"));
                        } catch (IOException e) {
                            System.err.println("Unable to send broadcast message after disconnection");
                        }
                        reconnect = true;
                        hasToRun = false;
                        return false;
                    }
                    else {
                        System.err.println("Unable to establish a connection: returned null - retrying in 7s");
                        Thread.sleep(7000);
                    }
                }
                else {
                    System.out.println("Generated new Streams");
                    outClient = oIOstream.getValue();
                    inClient = oIOstream.getKey();
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.err.println("An error occurred during reconnection setup");
        }
        this.reconnect = false;
        return true;
    }

    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inClient.readObject();
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.", "");
        switch (messageType) {
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStartingMessage":
                secretObjectiveSelector((BroadCastStartingMessage) message);
                break;
            case "BroadCastStandardMessage":
                broadCastMessageHandler((BroadCastStandardMessage) message);
                break;
            default:
                throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }

    public void resilience() throws IOException {
    }

    @Override
    public boolean getReconnect() {
        return this.reconnect;
    }

    @Override
    public void broadCastMessageHandler(BroadCastStandardMessage message) {
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        if (message instanceof StandardMatchMessage) {
            System.out.println("Sending Updates to" + getClientName());
        } else if (!(message instanceof TextMessage)) {
            message.setClientID(getClientID());
            message.setSender(getClientName());
        }
        outClient.writeObject(message);
        outClient.flush();
        outClient.reset();
    }

    public Socket getSocket() {
        return socket;
    }
}
