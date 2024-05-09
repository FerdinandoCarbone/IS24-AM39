package com.example.codexnaturalis;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface RemoteServerMethodInterface extends Remote {

    int getNumOfPlayers() throws RemoteException;

    void createLobby(LobbyCreationMessage msg) throws IOException,RemoteException;

    boolean joinLobby(LobbyCreationMessage lobbyCreationMessage) throws IOException,RemoteException;

    boolean callFor(UUID clientID) throws RemoteException;
    Message whatToCall(UUID clientID) throws RemoteException;

    void send(Message message) throws RemoteException;

    void keepAlive(UUID clientID) throws RemoteException, InterruptedException;

    Message reHandShakeRMI() throws RemoteException;

    Message getMessageTurn(UUID clientID)throws RemoteException;
}

