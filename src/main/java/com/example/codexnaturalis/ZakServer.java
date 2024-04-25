package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class ZakServer {
    static boolean gameStarted = false;
    static Match match;
    static ServerConnectionManager serverConMan;
    static Pair<String,Integer> connectionInfo;
    //static int playerCounter = 1;
    public static void main(String[] args) {
        int port;
        if(!args[0].isBlank()) {
            port = Integer.parseInt(args[0]);
            connectionInfo = new Pair<>("Server", port);
        }
        else{
            System.err.println("Cannot start server: Start server with an integer parameter as port");
            System.exit(0);
        }
        try{
            serverStart(connectionInfo.getValue());
        } catch(Exception e){
            System.err.println("Server Failure: "+e.getMessage());
        }
        serverConMan.acceptConnection(false);
        try{
            matchStart();
        } catch(Exception e){
            System.err.println("Server Failure: "+e.getMessage());
        }

    }
    public static void serverStart(int port) throws IOException {
        gameStarted = false;
        serverConMan=new ServerConnectionManager(connectionInfo,1099);
        System.out.println(
                " _____                                                                      _____ \n" +
                "( ___ )--------------------------------------------------------------------( ___ )\n" +
                " |   |                                                                      |   | \n" +
                " |   |   ____          _           _   _       _                   _ _      |   | \n" +
                " |   |  / ___|___   __| | _____  _| \\ | | __ _| |_ _   _ _ __ __ _| (_)___  |   | \n" +
                " |   | | |   / _ \\ / _` |/ _ \\ \\/ /  \\| |/ _` | __| | | | '__/ _` | | / __| |   | \n" +
                " |   | | |__| (_) | (_| |  __/>  <| |\\  | (_| | |_| |_| | | | (_| | | \\__ \\ |   | \n" +
                " |   |  \\____\\___/ \\__,_|\\___/_/\\_\\_| \\_|\\__,_|\\__|\\__,_|_|  \\__,_|_|_|___/ |   | \n" +
                " |   | / ___|  ___ _ ____   _____ _ __                                      |   | \n" +
                " |   | \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|                                     |   | \n" +
                " |   |  ___) |  __/ |   \\ V /  __/ |                                        |   | \n" +
                " |   | |____/ \\___|_|    \\_/ \\___|_|                                        |   | \n" +
                " |___|                                                                      |___| \n" +
                "(_____)--------------------------------------------------------------------(_____)");
        System.out.println("- developed by Team AM39");
    }
    public static void matchStart() throws Exception {
        System.out.println("Match is about to start");
        String serverCommand;
        ArrayList<Player> players = new ArrayList<>(serverConMan.getPlayers());
        match = new Match(players, new ScoreTracker());
        startingFieldClientSetup();
        welcomePlayer();
        gameStarted = true;
        while(gameStarted){
            serverCommand = getInput();
            interpretInput(serverCommand);
        }
    }
    private static void startingFieldClientSetup() throws IOException{
        //todo: da spostare in match probabilmente
        BroadCastStartingMessage fieldSetupMessage;
        ArrayList<ObjectiveCard> commonObjectiveCard;
        commonObjectiveCard = DrawingDeck.getCommonObjective();
        match.setCommonObjectives(commonObjectiveCard);
        System.out.println("CommonObjectiveCards:");
        //for(ObjectiveCard oc: commonObjectiveCard) oc.printCardAscii();
        fieldSetupMessage = new BroadCastStartingMessage(connectionInfo.getKey(),null,serverConMan.getHashClient(),commonObjectiveCard);
        serverConMan.sendBroadCastMessage(fieldSetupMessage);
    }
   private static void welcomePlayer() throws IOException {
        String text = "Match is about to start\nPlayers:\n";
        String firstPlayerStar = "";
        Collection<Player> players = serverConMan.getPlayers();
        for(Player p: players){
            //if (p.isFirstTurn()) firstPlayerStar = " *";
            text=text.concat(p.getPlayerName()+firstPlayerStar+"\n");
            //firstPlayerStar = "";
        }
       serverConMan.sendBroadCastMessage(new TextMessage(connectionInfo.getKey(), null,text,"Everyone"));
    }
    public static void stopThread(UUID clientID){
        //todo: fare le opportune modifiche a match
        serverConMan.getHandlers().get(clientID).interrupt();
        serverConMan.getHandlers().remove(clientID);
    }
    private static String getInput(){
        Scanner scanner= new Scanner(System.in);
        String input=null;
        do{
            System.out.print("Command: ");
            try{
                input = scanner.nextLine();
            }catch (NoSuchElementException e){
                continue;
            }
        }while(Objects.equals(input, "\n") || input==null);
        //scanner.close();
        return input;
    }
    private static void interpretInput(String serverCommand) {
        switch(serverCommand.toLowerCase()){
            case "close":
                System.out.println("Server shutting down");
                System.exit(0);
                break;
            case "ban":
                break;
            case "restart":
                break;
            default: System.out.println("Unknown command");
        }
    }

}
