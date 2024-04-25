package com.example.codexnaturalis;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIConnectionListener extends Thread implements Runnable {
    ServerConnectionManager serverComMan;
    boolean hasToRun;
    RemoteServerMethodInterface remoteServerSkeleton;
    public RMIConnectionListener(ServerConnectionManager serverComMan) throws RemoteException, MalformedURLException {
        this.serverComMan = serverComMan;
        hasToRun = true;
        remoteServerSkeleton = new RMIServerImplement();
        LocateRegistry.createRegistry(serverComMan.getRmiPort());
        Naming.rebind(serverComMan.getServerName(), remoteServerSkeleton);
    }
    @Override
    public void run(){
        while(hasToRun){

        }
    }
    public void setHasToRun(boolean value) {this.hasToRun = value;}
}
