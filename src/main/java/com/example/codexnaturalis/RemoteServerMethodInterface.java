package com.example.codexnaturalis;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServerMethodInterface extends Remote {
    LobbyCreationMessage handShake(Message handshakeMessage) throws RemoteException;
    BroadCastStartingMessage createLobby(LobbyCreationMessage msg) throws RemoteException;
}

