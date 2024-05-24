package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerStateSaver {
    ServerSaveData saveData;
    Pair<FileInputStream, FileOutputStream> fileSaveStream;
    Pair<ObjectInputStream, ObjectOutputStream> saveFileRW;


    public ServerStateSaver() {
        initSaver();

    }

    private void initSaver() {
        Path path = Paths.get("serverSaveData");
        try {
            if (Files.notExists(path)) {
                System.out.println("Creating save dir...");
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create Directory");
        }
        //creating references to access file and save file itself
        String fileName = "serverSaveData/match-SaveData.serv";
        boolean saveFound = true;
        FileOutputStream fileSave;
        FileInputStream fileLoad;
        try {
            path = Paths.get(fileName);
            if(Files.notExists(path)) saveDataCreation(fileName);
            fileSave = new FileOutputStream(fileName);
            fileLoad = new FileInputStream(fileName);
            fileSaveStream = new Pair<>(fileLoad, fileSave);
            ObjectOutputStream tmpOutStream = new ObjectOutputStream(fileSave);
            ObjectInputStream tmpInStream = new ObjectInputStream(fileLoad);
            saveFileRW = new Pair<>(tmpInStream, tmpOutStream);
            saveFound = loadState();
        } catch (IOException e) {
            System.out.println("An error occurred:" + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // load state of save file in saveData Object
        // if file contains useful data, data is read and loaded
        if (saveFound) {
            System.out.println("Save data loaded correctly: " + (saveData.isEmpty() ? "data is empty, starting a new match" : "previous match data was found"));
            Server.setIsCrashed(!saveData.isEmpty());
        } else {
            System.out.println("No save file was found: will start a new match when requested to");
            Server.setIsCrashed(false);
        }
    }


    private void saveDataCreation(String fileName) {
        saveData = new ServerSaveData();
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(saveData);
            System.out.println("The save file was successfully created");
        } catch (IOException e) {
            System.err.println("An error occurred while trying to create a save file");
        }
    }

    public void saveInitialState() {
        saveData = new ServerSaveData();
        saveData.setInitialSave(ServerConnectionManager.getPort(), ServerConnectionManager.numPlayers);
    }

    public boolean saveState() {
        saveData.setMatchSave(Server.match);
        saveData.setGameStarted(Server.gameStarted);
        saveData.setHandlers(Server.serverConMan.getHandlers());
        saveData.setHashClient(Server.serverConMan.getHashClient());
        saveData.setFirstPlayer(ServerConnectionManager.firstPlayer);
        saveData.setEmpty(false);
        return save();
    }

    private boolean save() {
        try {
            saveFileRW.getValue().writeObject(saveData);
            saveFileRW.getValue().flush();
            saveFileRW.getValue().reset();
        } catch (Exception e) {
            System.err.println("Error occurred while saving");
            return false;
        }
        return true;
    }

    public boolean loadState() throws ClassNotFoundException {
        try {
            saveData = (ServerSaveData) saveFileRW.getKey().readObject();
        }
        catch(EOFException E){
            System.out.println("eof reached:"+ E.getMessage());
            return false;
        }
        catch (IOException e) {
            System.out.println("LoadState error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void retrieveNecessaryInfo() {
        Server.connectionInfo = new Pair<>("Server", saveData.getPort());
        Server.gameStarted = saveData.isGameStarted();

    }

    public void retrieveCrucial() {
        ServerConnectionManager.hashClient = saveData.getHashClient();
        ServerConnectionManager.handlers = saveData.getHandlers();
        ServerConnectionManager.hashPlayer = saveData.getHashPlayer();
        ServerConnectionManager.firstPlayer = saveData.isFirstPlayer();
        ServerConnectionManager.numPlayers = saveData.getNumPlayers();
        Server.match = saveData.getMatchSave();
    }

    public void resetSave() {
        saveData = new ServerSaveData();
        save();
    }
}
