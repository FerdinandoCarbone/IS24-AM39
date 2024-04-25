package com.example.codexnaturalis;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientImplement extends UnicastRemoteObject implements RemoteClientMethodInterface {
    public RMIClientImplement() throws RemoteException {
        super();
    }


}