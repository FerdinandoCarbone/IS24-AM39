package com.example.codexnaturalis;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ServerSaveData implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    private Match matchSave;
    private ArrayList<UUID> kicked;
    private HashMap<Player, Socket> hashPlayer;
    private HashMap<UUID, Player> hashClient;
    private DrawingDeck drawingDeck;
    private int handlersSize;
    private boolean firstPlayer;
    private int port;
    private int numPlayers;
    private boolean gameStarted;
    private boolean empty;

    public ServerSaveData() {
        empty = true;
    }

    public void setInitialSave(int port, int numPlayers) {
        setPort(port);
        setNumPlayers(numPlayers);
    }

    public void setSave(Boolean gameStarted, HashMap<Player, Socket> hashPlayer, HashMap<UUID, Player> hashClient, int handlers, Match match) {
        setHashClient(hashClient);
        setGameStarted(gameStarted);
        setMatchSave(match);
        setHandlersSize(handlers);
        setHashPlayer(hashPlayer);

    }

    public ArrayList<UUID> getKicked() {
        return kicked;
    }

    public void setKicked(ArrayList<UUID> kicked) {
        this.kicked = kicked;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getHandlersSize() {
        return handlersSize;
    }

    public int getPort() {
        return port;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
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

    public void setDrawingDeck(DrawingDeck deck) {
        drawingDeck = deck;
    }

    public DrawingDeck getDrawingDeck() {
        return drawingDeck;
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
        return this.matchSave;
    }


    public HashMap<Player, Socket> getHashPlayer() {
        return hashPlayer;
    }

    public void setHandlersSize(int handlers) {
        this.handlersSize = handlers;
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
