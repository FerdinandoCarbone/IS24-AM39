package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ClientHandler extends Thread implements Runnable {
    private ServerConnectionManager connMan;
    public boolean reconnect;
    private final String clientName;
    private final UUID clientID;
    private boolean secretWasChosen;
    public boolean hasToRun;

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
        StandardMatchMessage newTurnStatus = ZakServer.match.removeDisconnectedPlayer(clientID);
        UUID nextPlayer = newTurnStatus.getNextPlayerId();
        try{
            ServerConnectionManager.sendMessage(nextPlayer, new GenericTurnMessage("Server",null,ZakServer.match.getCoveredCards() ,ZakServer.match.getPublicCards(),null));
        }
        catch(IOException e){
            System.err.println(e.getMessage()+": Error while sending new Generic turn message ");
        }
        ZakServer.stopThread(getClientID());
    }

    public boolean getSecretWasChosen() {
        return this.secretWasChosen;
    }

    public void sendMessage(Message message) throws IOException {
        System.out.println("wrong function");
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

    public void broadCastMessageHandler(BroadCastStandardMessage message) {
    }

    public void genericTurnMessageHandler(GenericTurnMessage message) throws IOException, ClassNotFoundException {
        StandardMatchMessage newStatus = ZakServer.match.genericTurn(message);
        System.out.println("currentPlayer:" + newStatus.getClientID());
        if (newStatus instanceof EndMatchMessage) {
            ServerConnectionManager.sendBroadCastMessage(newStatus);
            return;
        }
        ArrayList<ResourceGoldCard> coveredCards = ZakServer.match.getCoveredCards();
        ArrayList<ResourceGoldCard> publicCards = newStatus.getPublicCardsNewState();
        for (ResourceGoldCard c : coveredCards) System.out.println(Colors.BLUE + c.getIdCard() + Colors.RESET);
        for (ResourceGoldCard c : publicCards) System.out.println(Colors.RED + c.getIdCard() + Colors.RESET);
        GenericTurnMessage newTurn = new GenericTurnMessage("Server", newStatus.getNextPlayerId(), coveredCards, publicCards, null);
        newTurn.printCoveredCards();
        newTurn.printPublicCards();
        ServerConnectionManager.sendBroadCastMessage(newStatus);
        ServerConnectionManager.sendMessage(newStatus.getNextPlayerId(), newTurn);
    }

    public void endOfTheGame(EndGameMessage message) {
        ZakServer.gameStarted = false;
        ZakServer.match = null;
        //todo: match reset and restart function to initialize everything
    }

    public void secretObjectiveSelector(BroadCastStartingMessage message) {
        ObjectiveCard cardToKeep = message.getSelectedSecret();
        ServerConnectionManager.hashClient.get(clientID).getPlayerDeck().setSecretObjectiveCard(cardToKeep);
        ZakServer.match.putBackOtherSecretObjectiveCard(clientID, cardToKeep);
        //ArrayList<Player> players = ZakServer.match.getPlayers();
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
    public void run() {
        while (hasToRun) {
            try {
                heartBeat = false;
                Thread.sleep(10000);
                if (!heartBeat)
                    throw new ClientAbruptlyDisconnectedException("Client " + getClientName() + " disconnected: Trying to reconnect...");
            } catch (InterruptedException e) {
                System.err.println("Thread Sleep issue:" + e.getMessage());
            } catch (ClientAbruptlyDisconnectedException e) {
                System.err.println(e.getMessage());
                if (tryReconnectToClient()) continue;
                try {
                    ServerConnectionManager.sendBroadCastMessage(new TextMessage("Server", null, getClientName() + " disconnected from the server and was unable to reconnect", "Everyone"));
                } catch (IOException ex) {
                    System.err.println("Unable to broadcast disconnection Message");
                }
                clientDisconnected();
            }
            // Thread.onSpinWait();
        }
    }

    private boolean tryReconnectToClient() {
        for (int i = 0; i < 3; i++) {
            if (heartBeat) return heartBeat;
            System.err.println("Failed to reconnect: retrying in 7s");
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
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
            case "EndGameMessage":
                endOfTheGame((EndGameMessage) message);
                break;
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

    public SocketClientHandler(String clientName, Socket socket, UUID clientID, Pair<ObjectInputStream, ObjectOutputStream> iostream, ServerConnectionManager connMan) throws IOException {
        super(clientName, clientID, connMan);
        this.socket = socket;
        this.outClient = iostream.getValue();
        this.inClient = iostream.getKey();
        this.reconnect = false;
    }

    @Override
    public void run() {
        do {
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
                if (ZakServer.gameStarted && reconnect) {
                    throw new ClientAbruptlyDisconnectedException("Client " + getClientName() + " abruptly disconnected: Attempting reconnection");
                } else if (!ZakServer.gameStarted && reconnect && (ZakServer.match == null || ZakServer.match.getFinalWinners().isEmpty())) {
                    System.out.println("Riconnessione dopo crash prima inizio partita");
                    throw new ClientAbruptlyDisconnectedException(getClientName() + " abruptly disconnected: Attempting reconnection");
                } else if (!reconnect && !ZakServer.match.getFinalWinners().isEmpty()) {
                    System.out.println("Partita finita");
                    clientDisconnected();
                }
            } catch (ClientAbruptlyDisconnectedException e) {
                System.err.println(e.getMessage());
                try {
                    if (tryReconnectClient()) {
                        //outClient.reset();
                        continue;
                    }
                } catch (IOException ex) {
                    System.out.println("Unable to send match status to disconnected player: " + getClientName());
                }
                clientDisconnected();
            }
        } while (hasToRun);
    }

    private boolean tryReconnectClient() throws IOException {
        boolean result = true;
        System.err.println("Trying to re-establish a connection with client:");
        Pair<ObjectInputStream, ObjectOutputStream> oIOstream;
        try {
            for (int i = 0; i < 3; i++) {
                oIOstream = getConnMan().acceptSocketRMIConnections(true);
                if (oIOstream == null) {
                    if (i == 2) throw new Exception("No socket enabled client was able to connect");
                    else {
                        System.err.println("Unable to establish a connection - retrying in 7s");
                        Thread.sleep(7000);
                        continue;
                    }
                }
                outClient = oIOstream.getValue();
                inClient = oIOstream.getKey();
                break;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            ServerConnectionManager.sendBroadCastMessage(new TextMessage("Server", null, getClientName() + " disconnected from the server and was unable to reconnect", "Everyone"));
            result = false;
        }
        this.reconnect = !result;
        return result;
    }

    private void messageReceiver() throws IOException, ClassNotFoundException, WrongMessageConversionException {
        Message message = (Message) inClient.readObject();
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.", "");
        switch (messageType) {
            case "Message":
                resilience(message);
                break;
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
            case "EndGameMessage":
                endOfTheGame((EndGameMessage) message);
                break;
            default:
                throw new WrongMessageConversionException("Something went wrong while communicating with the server");
        }
    }

    private void resilience(Message message) {

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
    }
}
