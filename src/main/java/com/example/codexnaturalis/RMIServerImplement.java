package com.example.codexnaturalis;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RMIServerImplement extends UnicastRemoteObject implements RemoteServerMethodInterface {
    Message rmiSnatcher;

    public RMIServerImplement() throws RemoteException {
        super();
        rmiSnatcher = null;
    }

    @Override
    public int getNumOfPlayers() throws RemoteException {
        return Server.getNumOfPlayers();
    }

    @Override
    public void createLobby(LobbyCreationMessage msg) {
        int desiredPlayerCount = msg.getNumPlayer();
        ServerConnectionManager.numPlayers = desiredPlayerCount;
        System.out.println("There will be " + desiredPlayerCount + " players");
        ServerConnectionManager.firstPlayer = true;
        try {
            joinLobby(msg);
        } catch (IOException E) {
            System.out.println("Error creating Lobby: " + E.getMessage());
        }
    }

    @Override
    public boolean joinLobby(LobbyCreationMessage msg) throws IOException {
        UUID clientID = msg.getClientID();
        String sender = msg.getSender();
        if (!ServerConnectionManager.hashClient.keySet().isEmpty())
            for (Player p : ServerConnectionManager.hashClient.values())
                if (p.getPlayerName().equals(sender)) return false;
        Player player;
        player = new Player(sender, new Token(), new Field(CardDim.matrixSize, CardDim.matrixSize), clientID);
        ServerConnectionManager.hashClient.put(clientID, player);
        ClientHandler handler = new RMIClientHandler(sender, clientID, Server.serverConMan);
        new Thread(handler).start();
        ServerConnectionManager.handlers.put(clientID, handler);
        System.out.println(sender + " joined the server");
        return true;
    }

    @Override
    public boolean callFor(UUID clientID) {
        RMIClientHandler handler;
        handler = (RMIClientHandler) Server.serverConMan.getHandlers().get(clientID);
        handler.setHeartBeat(true);
        return handler.hasToDeliver;
    }

    @Override
    public Message whatToCall(UUID clientID) {
        RMIClientHandler handler;
        handler = (RMIClientHandler) Server.serverConMan.getHandlers().get(clientID);
        Message msg = handler.queue.getFirst();
        handler.queue.removeFirst();
        if (handler.queue.isEmpty()) handler.setHasToDeliver(false);
        return msg;
    }

    @Override
    public void send(Message message) {
        UUID clientID = message.getClientID();
        RMIClientHandler handler = (RMIClientHandler) Server.serverConMan.getHandlers().get(clientID);
        try {
            handler.retrieveMessage(message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void keepAlive(UUID clientID) throws RemoteException, InterruptedException {
        RMIClientHandler handler = (RMIClientHandler) Server.serverConMan.getHandlers().get(clientID);
        handler.setHeartBeat(true);
        Thread.sleep(1000);
    }

    @Override
    public Message reHandShakeRMI(UUID matchID) throws RemoteException {
        if(matchID==null) return new Message("MATCHNOTSTARTED",null);
        else if (!matchID.equals(Server.match.getMatchID())) return new Message("FORBIDDEN", null);
        else {
            UUID currPlayerID;
            System.out.println("Debug0-0");
            if(Server.isCrashed()) currPlayerID = Server.getServerSaver().saveData.getMatchSave().getCurrentPlayerID();
            else currPlayerID = Server.match.getCurrentPlayerID();
            System.out.println("Debug0-1");
            BroadCastStartingMessage bcStart = new BroadCastStartingMessage("Server", currPlayerID, ServerConnectionManager.hashClient, Server.match.getCommonObjectives(), Server.match.selectedSecrets);
            HashMap<String,Boolean> currPlaying = new HashMap<>();
            for (int i =0;i<ServerConnectionManager.hashClient.size();i++){
                if (Server.match.getPlayerIds().get(i) != null) currPlaying.put(ServerConnectionManager.hashClient.get(Server.match.getPlayerIds().get(i)).getPlayerName(),true);
                else currPlaying.put(Server.match.getPlayers().get(i).getPlayerName(),false);}
            System.out.println("Debug0-2");
            bcStart.setCurrentlyPlaying(currPlaying);
            return bcStart;
        }
    }

    public Message getMessageTurn(UUID clientID) throws RemoteException {
        return new GenericTurnMessage("Server", null, Server.match.getCoveredCards(), Server.match.getPublicCards(), null);
    }

    @Override
    public void addDisconnectedPlayer(UUID clientID) throws IOException {
        if(Server.isCrashed()){
            String playerName=null;
            for(Player p: Server.match.getPlayers()) if(p.getPlayerID().equals(clientID)) playerName = p.getPlayerName();
            ServerConnectionManager.handlers.replace(clientID,new RMIClientHandler(playerName,clientID,Server.serverConMan));
            ClientHandler handler = ServerConnectionManager.handlers.get(clientID);
            if(ServerConnectionManager.hashClient.get(clientID).getPlayerDeck().getSecretObjectiveCard()!=null)handler.setSecretWasChosen(true);
            new Thread(handler).start();
        }
        if (ServerConnectionManager.hashClient.get(clientID) != null && !Server.match.getPlayerIds().contains(clientID)) Server.match.addDisconnectedPlayerId(clientID);
        String sender = ServerConnectionManager.hashClient.get(clientID).getPlayerName();
        TextMessage text = new TextMessage("Server",null,sender+" rejoined the server", "Everyone");
        text.setDisconnectedClient(sender);
        ServerConnectionManager.sendBroadCastMessage(text);
    }

    @Override
    public boolean isServerCrashed() throws RemoteException {
        return Server.isCrashed();
    }
}
