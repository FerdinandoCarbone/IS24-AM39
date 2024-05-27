package com.example.codexnaturalis;

import javafx.application.Platform;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ServerHandler extends Thread implements Runnable {
    private final String clientName;
    private Semaphore samvise;
    private static final CountDownLatch dialogClosedLatch = new CountDownLatch(1);
    private final UUID clientID;
    private Pair<String,Integer> connectionInfo;
    private GenericTurnMessage messageTurn;
    private final ConnectionManger connMan;
    private volatile boolean firstBroadCastWasReceived;
    private String welcomeText;
    public ServerHandler(String clientName, UUID clientID,ConnectionManger connMan){
        this.clientName = clientName;
        this.clientID = clientID;
        this.connMan = connMan;
        this.connectionInfo = Client.getConnectionInfo();
        this.firstBroadCastWasReceived=false;
        this.messageTurn=null;
        this.samvise = new Semaphore(0);
    }

    public Semaphore getSemaphore() {
        return samvise;
    }
    public UUID getClientID() {
        return clientID;
    }
    public ConnectionManger getConnMan() {
        return connMan;
    }
    public String getClientName() {
        return clientName;
    }
    public boolean wasFirstBroadCastReceived() {
        return firstBroadCastWasReceived;
    }
    public void setFirstBroadCastWasReceived(boolean firstBroadCastWasReceived) {
        this.firstBroadCastWasReceived = firstBroadCastWasReceived;
    }
    public void textMessageHandler(TextMessage message) throws IOException, InterruptedException {
        AtomicReference<String> sender = new AtomicReference<>();
        sender.set(message.getSender());
        if(message.getDisconnectedClient()!=null) updateOtherPlayers(message);
        if(Objects.equals(sender.get(), getClientName())) sender.set("You");
        if(Client.isGuiSelector()){
            Platform.runLater(()->{
                if(!wasFirstBroadCastReceived()) welcomeText = message.getTextMessage();
                else MainController.printMessage("\n"+sender.get()+": "+message.getTextMessage());
                });
        }
        else{
            System.out.println("\n"+sender.get()+": "+message.getTextMessage());
        }
        if(message.getTextMessage().contains("kicked")) System.exit(0);

    }

    private void updateOtherPlayers(TextMessage message) {
        if(Client.isCrashed()){
            Client.setCrashed(false);
            return;
        }
        String newStatusPlayer= message.getDisconnectedClient();
        HashMap<String,Boolean> otherPlayers = Client.getCurrentlyPlayingPlayers();
        if(message.getTextMessage().contains("rejoined")) otherPlayers.replace(newStatusPlayer,true);
        else otherPlayers.replace(newStatusPlayer,false);
        if(Client.isGuiSelector())Platform.runLater(MainController::updateOtherPlayers);
    }

    public void genericTurnMessageHandler(GenericTurnMessage message){
        this.messageTurn = message;
        Client.genericTurnMessageHandler();
    }
    public void sendMessage(Message message) throws IOException {

    }
    public void broadCastStartingMessageHandler(BroadCastStartingMessage initialMatchSetupMessage) throws IOException {
        try{
            if(initialMatchSetupMessage.equals(null)) throw new WrongMessageConversionException("Was not able to initialize Starting Field");
        } catch (WrongMessageConversionException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
         Client.initialMatchSetup(initialMatchSetupMessage);
    }
    public void clientDisconnected() {
        Client.clientDisconnect();
    }
    public void bcsHandler(BroadCastStandardMessage message) throws InterruptedException {
        if( message.starterCards==null){
            setFirstBroadCastWasReceived(true);
            HashMap<String,Boolean> playingPlayer = message.getCurrPlaying();
            Client.setCurrentlyPlayingPlayers(playingPlayer);
            return;
        }
        else if(Client.isCrashed()) {
            setFirstBroadCastWasReceived(true);
        }
        HashMap<UUID,StarterCard> hashStart= message.starterCards;
        hashStart.remove(getClientID());
        AtomicBoolean face = new AtomicBoolean();
        for(Player p: Client.getOtherPlayers()){
            face.set(hashStart.get(p.getPlayerID()).isPlacedFront());
            p.placeStarterCard(face.get());
        }
        if(!wasFirstBroadCastReceived()) {
            Semaphore sam = new Semaphore(0);
            if(!wasFirstBroadCastReceived()) {
                if (Client.isGuiSelector()) {
                    Platform.runLater(() -> {
                        try {
                            LauncherController.alert("Server"+": "+welcomeText,true);
                            LauncherController.loadGameScene();
                            sam.release();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    sam.acquire();
                }
            }
            setFirstBroadCastWasReceived(true);
        }
    }
    public void universalStatusUpdater(StandardMatchMessage newStatus){
        UUID oldPlayer= newStatus.getClientID();
        //System.out.println("Updating game status" + oldPlayer+" Points: "+ newStatus.getCurrPlayerPoints());
        if (Client.isGuiSelector()) Platform.runLater(() -> MainController.scoreTracker.moveToken(oldPlayer, newStatus.getCurrPlayerPoints()));
        ResourceGoldCard placedCard= newStatus.getPlacedCard();
        Pair<Integer,Integer> coords = newStatus.getCoords();
        if(getClientID().equals(oldPlayer)){
            AtomicInteger score = new AtomicInteger();
            score.set(newStatus.getCurrPlayerPoints());
            Client.getPlayer().setScore(score.get());
            if(Client.isGuiSelector()) Platform.runLater(()->{
                MainController.manaBar.getActualPoints().setText(String.valueOf(score));
            });
        }
        else {
            ArrayList<Player> players = Client.getOtherPlayers();
            for (Player p : players) {
                if (p.getPlayerID().equals(oldPlayer)) {
                    //System.out.println("Player found:");
                    p.placeCard(coords.getKey(), coords.getValue(), placedCard);
                    //System.out.println("In Server Handler: " + placedCard.getCoveredCornersWhenPlaced());
                    p.setScore(newStatus.getCurrPlayerPoints());
                    if(Client.isGuiSelector()){
                        Platform.runLater(()->{
                        MainController.getTabMan().get(p.getPlayerName()).getValue().fillField(coords.getKey(), coords.getValue(),placedCard);});
                    }
                    break;
                }
            }
        }
    }
    public GenericTurnMessage getMessageTurn() {
        return messageTurn;
    }
    public void winnerDeclaration(EndMatchMessage message) throws WrongMessageConversionException {
        ArrayList<Player> players = message.getFinalWinners();
        String winString=null;
        int i = players.size();
        if(message.getSender().equals(clientName)) {
            winString = clientName + " is the winner of this game";
            i=-1;
        }
        switch(i){
            case 1:
                winString = players.getFirst().getPlayerName() + " is the winner of this game";
                break;
            case 2,3,4:
                winString = "There was a Draw between: \n";
                for (Player p : players){
                    winString.concat(p.getPlayerName()+" ");
                }
            case -1:
                break;
            default: throw new WrongMessageConversionException("There was a problem declaring the winner");
        }
        if(Client.isGuiSelector()) {
            String finalWinString = winString;
            Platform.runLater(()->MainController.alert(finalWinString +"\nThank you for playing",true));
        }
        System.out.println(winString+"\nThank you for playing");
    }
    public void setMessageTurn(GenericTurnMessage messageTurn) {
        this.messageTurn = messageTurn;
    }
    public void restartClient() {
        String kickString = "Server crashed: please try restarting client with same username to try and rejoin match";
        System.out.println(kickString);
        if(Client.isGuiSelector()) Platform.runLater(()->MainController.alert(kickString,true));
        System.exit(0);
        /*final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if(!currentJar.getName().endsWith(".jar"))
            return;
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());
        String args="";
        for(String s: Arrays.stream(Client.clientArgs).toList()) args = args.concat(s);
        command.add(args);
        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);*/
    }
}

class ServerSocketHandler extends ServerHandler {

    private Socket socket;
    private ObjectOutputStream outServer;
    private ObjectInputStream inServer;
    private Boolean hasToRun;

    public ServerSocketHandler(String clientName, UUID clientID,ConnectionManger connMan) throws IOException {
        super(clientName,clientID,connMan);
        this.outServer = connMan.getIoStream().getValue();
        this.inServer = connMan.getIoStream().getKey();
        this.hasToRun = true;
        this.socket = connMan.socket;
    }
    @Override
    public void run() {
        while(true){
            try {
                if(hasToRun) messageReceiver(null);
            } catch (ClassNotFoundException e) {
                System.out.println("ServerComHandler ClassNotFoundError: " + e.getMessage());

            } catch(IOException e){
                System.out.println("ServerComHandler IOError: " + e.getMessage());
                try {
                    throw new ClientAbruptlyDisconnectedException(getClientName()+" abruptly disconnected from server due to socket degradation: Attempting reconnection");
                } catch (ClientAbruptlyDisconnectedException ex) {
                    if(tryReconnectToServer()) continue;
                    clientDisconnected();
                }
            } catch (InterruptedException | WrongMessageConversionException e) {
                throw new RuntimeException(e);
            }
            try{
                if(socket.isClosed() && Client.isCurrentGameStatus()) throw new ClientAbruptlyDisconnectedException(getClientName()+" abruptly disconnected from server: Attempting reconnection");
            }catch(ClientAbruptlyDisconnectedException e){
                if(tryReconnectToServer()) continue;
                clientDisconnected();
            }
        }
    }


    private boolean tryReconnectToServer()  {
        boolean result=true;
        try {
            this.socket = getConnMan().connectionAttempt();
            outServer= new ObjectOutputStream(socket.getOutputStream());
            inServer = new ObjectInputStream(socket.getInputStream());
            System.out.println("Waiting to see if server crashed");
            boolean isServerCrashed=false;
            try{
               Message retryConnection = new Message(getClientName(),getClientID());
               retryConnection.setReconnectServerCrash(true);
                outServer.writeObject(retryConnection);
                outServer.flush();
                Message mex = (Message) inServer.readObject();
                if(mex instanceof ResetMatchMessage){
                    isServerCrashed=true;
                    hasToRun = false;
                }
                else messageReceiver(mex);
            } catch(Exception e){
                System.err.println("Server is not crashed:" +e.getMessage());
            }
            if(isServerCrashed) {
                //System.out.println("Server crashed: to restart client please press select an option and press Enter");
                restartClient();
                /*getConnMan().setIoStream(new Pair<>(inServer,outServer));
                getConnMan().reHandShake(getClientName(),getClientID());*/
            }
            //System.out.println("Done");
            hasToRun = true;
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    private void messageReceiver(Message inputmex) throws IOException, ClassNotFoundException, WrongMessageConversionException, InterruptedException {
        Message message;
        if(inputmex==null)message = (Message) inServer.readObject();
        else message = inputmex;
        if(message==null) return;
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.","");
        //System.out.println(messageType);
        switch (messageType){
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStandardMessage":
                bcsHandler((BroadCastStandardMessage) message);
                break;
            case "BroadCastStartingMessage":
                broadCastStartingMessageHandler((BroadCastStartingMessage) message);
                break;
            case "EndMatchMessage":
                endOfTheGame((EndMatchMessage)message);
                break;
            case "LobbyCreationMessage":
                break;
            case "StandardMatchMessage":
                universalStatusUpdater((StandardMatchMessage) message);
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server: "+a.getName()+" is not Handled");
        }
    }
    @Override
    public void sendMessage(Message message) throws IOException {
        message.setSender(getClientName());
        message.setClientID(getClientID());
        outServer.writeObject(message);
        outServer.flush();
        outServer.reset();
    }

    private void endOfTheGame(EndMatchMessage message) throws IOException, WrongMessageConversionException {
        winnerDeclaration(message);
        outServer.close();
        inServer.close();
        Client.endOfTheGame();
    }


}
class ServerRMIHandler extends ServerHandler{

    RemoteServerMethodInterface remoteProxy;
    public ServerRMIHandler(String clientName, UUID clientID,ConnectionManger connman) {
        super(clientName, clientID,connman);
        this.remoteProxy = connman.getRemoteServerProxy();
    }

    @Override
    public void run(){
        while(true){
            try {
                if(remoteProxy.callFor(getClientID())) messageReceiver(remoteProxy.whatToCall(getClientID()));
                remoteProxy.keepAlive(getClientID());
            } catch(RemoteException e){
                System.err.println("ServerRMIHandler: "+e.getMessage());
                if(tryReconnectToServer()) continue;
                //todo: reconnection attempt
                clientDisconnected();
            } catch(IOException |WrongMessageConversionException e){
                System.err.println("IOException: " +e.getMessage() );
            } catch (InterruptedException e) {
                System.err.println("InterruptedException: " +e.getMessage() );
            }
        }
    }
    private boolean tryReconnectToServer()  {
        boolean result=true;
        try {
            getConnMan().connectionAttempt();
            if(remoteProxy.isServerCrashed()) restartClient();
        } catch (HandShakeException e){
            result = false;
        }
        catch(IOException e){
            restartClient();
        }
        return result;
    }
    private void messageReceiver(Message message) throws WrongMessageConversionException, IOException, InterruptedException {
        Class<? extends Message> a = message.getClass();
        String messageType = a.getName().replaceFirst("com.example.codexnaturalis.","");
        //System.out.println(messageType);
        switch (messageType){
            case "GenericTurnMessage":
                genericTurnMessageHandler((GenericTurnMessage) message);
                break;
            case "TextMessage":
                textMessageHandler((TextMessage) message);
                break;
            case "BroadCastStandardMessage":
                bcsHandler((BroadCastStandardMessage) message);
                break;
            case "BroadCastStartingMessage":
                broadCastStartingMessageHandler((BroadCastStartingMessage) message);
                break;
            case "EndMatchMessage":
                endOfTheGame((EndMatchMessage)message);
                break;
            case "LobbyCreationMessage":
                break;
            case "StandardMatchMessage":
                universalStatusUpdater((StandardMatchMessage) message);
                break;
            default: throw new WrongMessageConversionException("Something went wrong while communicating with the server: "+a.getName()+" is not Handled");
        }
    }
    @Override
    public void sendMessage(Message message){
        message.setSender(getClientName());
        message.setClientID(getClientID());
        try {
            remoteProxy.send(message);
        }catch (RemoteException e){
            System.err.println(e.getMessage());
        }
    }
    private void endOfTheGame(EndMatchMessage message) throws WrongMessageConversionException {
        winnerDeclaration(message);
        Client.endOfTheGame();
    }
}
