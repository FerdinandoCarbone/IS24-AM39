package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ServerStateSaver implements Serializable{
    private ServerSaveData saveData;
    private String fileName = "serverSaveData/match-SaveData.serv";
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

        boolean saveFound = true;
        try {
            saveFound = loadState();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // load state of save file in saveData Object
        // if file contains useful data, data is read and loaded
        if (saveFound) {
            System.out.println("Save data loaded correctly: " + (saveData.isEmpty() ? "data is empty, starting a new match" : "previous match data was found"));
            if(!saveData.isEmpty()) {
                System.out.println("Do you want to start a new match or load the previous one?\n0 - New match\n1 - Load save");
                int choice = Server.getIntInput(1,false);
                switch (choice){
                    case 0:
                        Server.setIsCrashed(false);
                        break;
                    case 1:
                        Server.setIsCrashed(true);
                        break;
                }
            }
        } else {
            System.out.println("No save file was found: will start a new match when requested to");
            Server.setIsCrashed(false);
        }
    }


    private void saveDataCreation(String fileName) {
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
        saveData.setHandlersSize(Server.serverConMan.getHandlers().size());
        saveData.setHashClient(Server.serverConMan.getHashClient());
        //saveData.setKicked(Server.serverConMan.getKickedIDs());
        saveData.setFirstPlayer(ServerConnectionManager.firstPlayer);
        saveData.setNumPlayers(ServerConnectionManager.numPlayers);
        saveData.setEmpty(false);
        return save();
    }

    private boolean save() {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(saveData);
            objectOut.flush();
            objectOut.reset();
        } catch (Exception e) {
            System.err.println("Error occurred while saving" + e.getMessage());
            return false;
        }
        System.out.println(Colors.GREEN + "SAVE SUCCESSFULL" + Colors.RESET);
        try{
            loadState();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return true;
    }

    public boolean loadState() throws ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            saveData = (ServerSaveData) objectIn.readObject();
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

    public void retrieveNecessaryStartingInfo() {
        Server.connectionInfo = new Pair<>("Server", saveData.getPort());
        Server.gameStarted = saveData.isGameStarted();

    }

    public void retrieveCrucial() {
        ServerConnectionManager.hashClient = saveData.getHashClient();
        ServerConnectionManager.handlers = new HashMap<>();
        ArrayList<UUID> ids = new ArrayList<>(ServerConnectionManager.hashClient.keySet());
        for(int i =0;i< saveData.getHandlersSize();i++) ServerConnectionManager.handlers.put(ids.get(i),null);
        //ServerConnectionManager.hashPlayer = saveData.getHashPlayer();
        ServerConnectionManager.firstPlayer = saveData.isFirstPlayer();
        ServerConnectionManager.numPlayers = saveData.getNumPlayers();
        Server.gameStarted = saveData.isGameStarted();
        Server.match = saveData.getMatchSave();
        //Server.serverConMan.setKickedIDs(saveData.getKicked());
    }

    public void resetSave() throws IOException {
        Files.delete(Paths.get(fileName));
    }
    public ServerSaveData getSaveData(){
        return saveData;
    }
}
