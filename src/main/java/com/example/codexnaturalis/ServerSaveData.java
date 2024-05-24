package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class ServerSaveData implements Serializable {
    private Match matchSave;
    private HashMap<Player, Socket> hashPlayer;
    private HashMap<UUID, Player> hashClient;
    private HashMap<UUID, ClientHandler> handlers;
    private boolean firstPlayer;
    private int port;
    private int numPlayers;
    private boolean gameStarted;
    private boolean empty;
    public ServerSaveData(){
        empty = true;
    }

    public void setInitialSave(int port,int numPlayers){
        setPort(port);
        setNumPlayers(numPlayers);
    }
    public void setSave(Boolean gameStarted,HashMap<Player, Socket> hashPlayer, HashMap<UUID, Player> hashClient, HashMap<UUID, ClientHandler> handlers,Match match){
        setHashClient(hashClient);
        setGameStarted(gameStarted);
        setMatchSave(match);
        setHandlers(handlers);
        setHashPlayer(hashPlayer);

    }
    public int getNumPlayers() {
        return numPlayers;
    }

    public boolean isEmpty() {
        return empty;
    }

    public HashMap<UUID, ClientHandler> getHandlers() {
        return handlers;
    }

    public int getPort() {
        return port;
    }

    public void setEmpty(boolean empty) {
        empty = empty;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }


    public boolean isFirstPlayer() {
        return firstPlayer;
    }

    public void setHashClient(HashMap<UUID, Player> hashClient) {
        this.hashClient = hashClient;
    }

    public HashMap<UUID, Player> getHashClient() {
        return hashClient;
    }

    public Match getMatchSave() {
        return matchSave;
    }


    public HashMap<Player, Socket> getHashPlayer() {
        return hashPlayer;
    }

    public void setHandlers(HashMap<UUID, ClientHandler> handlers) {
        this.handlers = handlers;
    }

    public void setHashPlayer(HashMap<Player, Socket> hashPlayer) {
        this.hashPlayer = hashPlayer;
    }

    public void setMatchSave(Match matchSave) {
        this.matchSave = matchSave;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
