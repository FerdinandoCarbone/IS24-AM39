package com.example.codexnaturalis;

import java.io.IOException;
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
                if(!hasToRun) Thread.onSpinWait();
                startListening();
            } catch (IOException e) {
                System.err.println("There was an error listening for sockets: "+e.getMessage()+"\nRetrying...");
            }
        }
    }
    private void startListening() throws IOException {
        this.sockets.add(serverComMan.getServerSocket().accept());
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
