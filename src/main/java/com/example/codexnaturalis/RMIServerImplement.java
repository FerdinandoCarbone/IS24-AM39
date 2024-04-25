package com.example.codexnaturalis;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerImplement extends UnicastRemoteObject implements RemoteServerMethodInterface {
    public RMIServerImplement() throws RemoteException {
        super();
    }

    @Override
    public LobbyCreationMessage handShake(Message message) throws RemoteException {
        return null;
    }
    @Override
    public BroadCastStartingMessage createLobby(LobbyCreationMessage msg) throws RemoteException{
        //todo:
        return null;
    }
}
