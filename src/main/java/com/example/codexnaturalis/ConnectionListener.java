package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionListener extends Thread implements Runnable{
    ServerConnectionManager serverComMan;
    public volatile boolean hasToRun;
    public ConnectionListener(ServerConnectionManager serverComMan){
        this.serverComMan = serverComMan;
        hasToRun = true;
    }
    public void setHasToRun(boolean value) {this.hasToRun = value;}

}
class SocketConnectionListener extends ConnectionListener {
    public ArrayList<Socket> sockets;
    public SocketConnectionListener(ServerConnectionManager serverComMan) {
        super(serverComMan);
        sockets = new ArrayList<>();
    }
@Override
    public void run() {
        while (true) {
            try {
                startListening();
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                System.err.println("There was an error listening for sockets: "+e.getMessage()+"\nRetrying...");
                continue;
            }
        }
    }
    private void startListening() throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("I am listening");
        Socket clientSocket = serverComMan.getServerSocket().accept();
        System.out.println("I heard");
        this.sockets.add(clientSocket);
        if(Server.gameStarted&& Server.match.getPlayerIds().contains(null) && !sockets.isEmpty()){
            ClientHandler tmpHand;
            UUID clientID;
            System.out.println("Accepted socket connection");
            Pair<ObjectInputStream, ObjectOutputStream> oIOStream = Server.serverConMan.acceptSocketRMIConnections(clientSocket,true);
            sockets.remove(clientSocket);
            if(oIOStream==null) return;
            clientID =ServerConnectionManager.reconnectingID;
            if(Server.isCrashed()){
                System.out.println("Reconnecting a client after a server crash...");
                String playerName=null;
                for(Player p: Server.match.getPlayers()) if(p.getPlayerID().equals(clientID)) playerName = p.getPlayerName();
                tmpHand = new SocketClientHandler(playerName,clientSocket,clientID,oIOStream,Server.serverConMan);
                if(ServerConnectionManager.hashClient.get(clientID).getPlayerDeck().getSecretObjectiveCard()!=null)tmpHand.setSecretWasChosen(true);
                new Thread(tmpHand).start();
                Server.serverConMan.getKickedIDs().remove(clientID);
                Server.getServerSaver().saveState();
                TextMessage text = new TextMessage("Server",null,playerName + " rejoined the server","Everyone");
                text.setDisconnectedClient(playerName);
                try{
                    ServerConnectionManager.sendBroadCastMessage(text);
                } catch (Exception e){
                    System.out.println("Sending broadcast issue:"+e.getMessage());
                }
                ServerConnectionManager.handlers.replace(clientID,tmpHand);
            }
            else{
                System.out.println("Reconnecting a client after a client crash...");
                SocketClientHandler tmpSCH = (SocketClientHandler) Server.serverConMan.getHandlers().get(clientID);
                tmpSCH.reset(oIOStream,clientSocket);
                tmpSCH.setHasToRun(true);
                tmpHand = tmpSCH;
            }
            tmpHand.reconnectionAlert();
        }
    }
}
class RMIConnectionListener extends ConnectionListener {

    RemoteServerMethodInterface remoteServerSkeleton;
    public String bindingName;
    private static final long serialVersionUID = 1L;
    public ExecutorService executorService = Executors.newCachedThreadPool();
    public RMIConnectionListener(ServerConnectionManager serverComMan) throws RemoteException, MalformedURLException, UnknownHostException {
        super(serverComMan);
        bindingName = "rmi://"+ InetAddress.getLocalHost()+"/"+ServerConnectionManager.serverName;
        remoteServerSkeleton = new RMIServerImplement();
        LocateRegistry.createRegistry(serverComMan.getRmiPort());
        Naming.rebind(ServerConnectionManager.getServerName(), remoteServerSkeleton);
    }
    @Override
    public void run(){
        while(hasToRun){
            /*if(Server.isCrashed()) {
                try {
                    ServerConnectionManager.sendBroadCastMessage(new ResetMatchMessage("Server",null,"OOPSIES",null));
                } catch (IOException e) {
                    System.out.println("Unable to send broadcast disconnecting message");
                }
            }*/
        }
    }
    public void shutRMIConnection() throws RemoteException, MalformedURLException, NotBoundException {
        UnicastRemoteObject.unexportObject(remoteServerSkeleton,true);
        Naming.unbind("rmi://localhost:"+serverComMan.getRmiPort()+"/Server");
    }
}
