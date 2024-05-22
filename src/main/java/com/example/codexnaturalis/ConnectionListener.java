package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

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
            }
        }
    }
    private void startListening() throws IOException, ClassNotFoundException, InterruptedException {
        Socket clientSocket = serverComMan.getServerSocket().accept();
        this.sockets.add(clientSocket);
        if(Server.gameStarted&&Server.match.getPlayerIds().contains(null)&&!sockets.isEmpty()){
            System.out.println("Accepted socket connection");
            Pair<ObjectInputStream, ObjectOutputStream> oIOStream = Server.serverConMan.acceptSocketRMIConnections(clientSocket,true);
            SocketClientHandler tmpHand = (SocketClientHandler)Server.serverConMan.getHandlers().get(ServerConnectionManager.reconnectingID);
            tmpHand.reset(oIOStream,clientSocket);
            sockets.remove(clientSocket);
            tmpHand.setHasToRun(true);
            tmpHand.reconnectionAlert();
        }
    }
}
class RMIConnectionListener extends ConnectionListener {

    RemoteServerMethodInterface remoteServerSkeleton;
    public RMIConnectionListener(ServerConnectionManager serverComMan) throws RemoteException, MalformedURLException {
        super(serverComMan);
        remoteServerSkeleton = new RMIServerImplement();
        LocateRegistry.createRegistry(serverComMan.getRmiPort());
        Naming.rebind(ServerConnectionManager.getServerName(), remoteServerSkeleton);
    }
    @Override
    public void run(){
        while(hasToRun){

        }
    }
    public void shutRMIConnection() throws RemoteException, MalformedURLException, NotBoundException {
        UnicastRemoteObject.unexportObject(remoteServerSkeleton,true);
        Naming.unbind("rmi://localhost:"+serverComMan.getRmiPort()+"/Server");
    }
}
