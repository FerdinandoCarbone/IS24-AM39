package com.example.codexnaturalis;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.example.codexnaturalis.Colors.*;

public class Message implements Serializable {
    private String sender;
    private UUID clientID;
    public Message(String sender,UUID clientID){
        this.sender = sender;
        this.clientID = clientID;
    }
    public void setClientID(UUID clientID) {
        this.clientID = clientID;
    }
    public UUID getClientID() {
        return clientID;
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
class LobbyCreationMessage extends Message{
    private int numPlayer;
    public LobbyCreationMessage(String sender,UUID clientID,int numPlayer){
        super(sender,clientID);
        this.numPlayer = numPlayer;
    }
    public void setNumPlayer(int numPlayer){
        this.numPlayer = numPlayer;
    }

    public int getNumPlayer() {
        return numPlayer;
    }
}
class GenericTurnMessage extends Message{
    private ArrayList<ResourceGoldCard> drawnCard;
    private ArrayList<ResourceGoldCard> cardOnHand;
    private Pair<Integer,Integer> coordinates;
    public GenericTurnMessage(String sender, UUID ClientID,ArrayList<ResourceGoldCard> drawnCard,ArrayList<ResourceGoldCard> cardOnHand,Pair<Integer,Integer> coordinates) {
        super(sender, ClientID);
        this.cardOnHand = cardOnHand;
        this.drawnCard = drawnCard;
        this.coordinates = coordinates;
    }
    public ArrayList<ResourceGoldCard> getDrawnCard(){ return this.drawnCard;}

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public ArrayList<ResourceGoldCard> getCardOnHand() {
        return cardOnHand;
    }

    private void printCardsSideBySideFront(ArrayList<ResourceGoldCard> deck, int index1, int index2) {
        System.out.print(GREEN + deck.get(index1).getIdCard() + YELLOW + "[" + (deck.get(index1).getFrontCorners().get(3).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getFrontCorners().get(3).getResourceElement()) + "]" + "[" + (deck.get(index1).getFrontCorners().get(0).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getFrontCorners().get(0).getResourceElement()) + "]" + RESET);
        System.out.println(GREEN + deck.get(index2).getIdCard() + YELLOW + "[" + (deck.get(index2).getFrontCorners().get(3).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getFrontCorners().get(3).getResourceElement()) + "]" + "[" + (deck.get(index2).getFrontCorners().get(0).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getFrontCorners().get(0).getResourceElement()) + "]" + RESET);
        System.out.print(YELLOW + "  [" + (deck.get(index1).getFrontCorners().get(2).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getFrontCorners().get(2).getResourceElement()) + "]" + "[" + (deck.get(index1).getFrontCorners().get(1).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getFrontCorners().get(1).getResourceElement()) + "]" + RESET);
        System.out.println(YELLOW + "  [" + (deck.get(index2).getFrontCorners().get(2).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getFrontCorners().get(2).getResourceElement()) + "]" + "[" + (deck.get(index2).getFrontCorners().get(1).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getFrontCorners().get(1).getResourceElement()) + "]" + RESET);
        System.out.println(YELLOW + "Points given from card " + deck.get(index1).getIdCard() + ": " + deck.get(index1).getPoints() + RESET);
        if (deck.get(index1) instanceof GoldCard) {
            ((GoldCard) deck.get(index1)).printRequirements();
        }
        System.out.println(YELLOW + "Points given from card " + deck.get(index2).getIdCard() + ": " + deck.get(index2).getPoints() + RESET);
        if (deck.get(index2) instanceof GoldCard) {
            ((GoldCard) deck.get(index2)).printRequirements();
        }
    }
    private void printCardsSideBySideBack(ArrayList<ResourceGoldCard> deck, int index1, int index2) {
        System.out.print(GREEN + deck.get(index1).getIdCard() + YELLOW + "[" + (deck.get(index1).getBackCorners().get(3).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getBackCorners().get(3).getResourceElement()) + "]" + "[" + (deck.get(index1).getBackCorners().get(0).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getBackCorners().get(0).getResourceElement()) + "]" + RESET);
        System.out.println(GREEN + deck.get(index2).getIdCard() + YELLOW + "[" + (deck.get(index2).getBackCorners().get(3).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getBackCorners().get(3).getResourceElement()) + "]" + "[" + (deck.get(index2).getBackCorners().get(0).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getBackCorners().get(0).getResourceElement()) + "]" + RESET);
        System.out.print(YELLOW + "  [" + (deck.get(index1).getBackCorners().get(2).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getBackCorners().get(2).getResourceElement()) + "]" + "[" + (deck.get(index1).getBackCorners().get(1).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index1).getBackCorners().get(1).getResourceElement()) + "]" + RESET);
        System.out.println(YELLOW + "  [" + (deck.get(index2).getBackCorners().get(2).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getBackCorners().get(2).getResourceElement()) + "]" + "[" + (deck.get(index2).getBackCorners().get(1).isAvailableCorner()? "1" : "0") + "|" + (deck.get(index2).getBackCorners().get(1).getResourceElement()) + "]" + RESET);
    }
    public void printCoveredCards() throws ClassNotFoundException {
        System.out.println("HIDDEN CARDS");
        for(ResourceGoldCard drawn: drawnCard) printCardInBox(drawn,true);
        /*System.out.println("HiddenCard no box");
        printCardsSideBySideBack(drawnCard, 0, 1);*/
    }
    public void printPublicCards() throws ClassNotFoundException {
        System.out.println("PUBLIC CARDS");
        for(ResourceGoldCard onHand: cardOnHand) printCardInBox(onHand,false);
        /*printCardsSideBySideFront(cardOnHand, 0, 1);
        printCardsSideBySideFront(cardOnHand, 2, 3);*/
    }
    public static void printCardInBox(ResourceGoldCard card,boolean isHidden) {
        int width = 29; // Width of the box -- use an odd number greater than 27
        int height = 6; // Height of the box -- use an even number bigger than 3
        String whiteSpacer;
        String leftCorner,rightCorner;
        whiteSpacer= " ".repeat((width/2)-1);
        String cardType = (card instanceof GoldCard)? (YELLOW+"GoldCard:"+RESET):(RED+"ResourceCard:"+RESET);
        String seed = (card.getSeed()).toString();
        String seedColor = switch (seed) {
            case "Red" -> RED;
            case "Purple" -> PURPLE;
            case "Blue" -> BLUE;
            case "Green" -> GREEN;
            default -> RESET;
        };
        System.out.println(cardType+GREEN+ "#"+card.getIdCard()+" "+seedColor+seed+RESET);
        // Print top border
        System.out.print("+");
        for (int i = 0; i < width - 2; i++) {
            System.out.print("-");
        }
        System.out.println("+");
        //Print TopCorners
        if(isHidden) {
            leftCorner = "[" + (card.getBackCorners().get(3).isAvailableCorner() ? "1" : "0") + "|" + card.getBackCorners().get(3).getResourceElement() + "]";
            rightCorner = "[" + (card.getBackCorners().get(0).isAvailableCorner() ? "1" : "0") + "|" + (card.getBackCorners().get(0).getResourceElement()) + "]";
        }
        else{
            leftCorner="[" + (card.getFrontCorners().get(3).isAvailableCorner()? "1" : "0") + "|" + (card.getFrontCorners().get(3).getResourceElement()) + "]";
            rightCorner="[" + (card.getFrontCorners().get(0).isAvailableCorner()? "1" : "0") + "|" + (card.getFrontCorners().get(0).getResourceElement()) + "]";
        }
        System.out.print("|"+YELLOW+leftCorner+RESET);
        for (int i = 0; i < width -(rightCorner.length()+leftCorner.length()+2); i++) {
            System.out.print(" ");
        }
        System.out.println(YELLOW+rightCorner+RESET+"|");
        // Print sides with CardID
        for (int i = 1; i < height; i++) {
            System.out.print("|");
            System.out.print(whiteSpacer);
            if(i==height/2&&!isHidden){
                String tmpWhiteSpacer = " ";
                Integer z = (Integer)card.getPoints();
                tmpWhiteSpacer=tmpWhiteSpacer.concat(whiteSpacer);
                System.out.print(YELLOW +z+RESET);
                System.out.print(tmpWhiteSpacer.substring(z.toString().length()));
            }
            else System.out.print(whiteSpacer+" ");
            System.out.println("|");
        }
        //Print bottom Corners
        if(isHidden){
        leftCorner = "[" + (card.getBackCorners().get(2).isAvailableCorner()? "1" : "0") + "|" + (card.getBackCorners().get(2).getResourceElement()) + "]";
        rightCorner = "[" + (card.getBackCorners().get(1).isAvailableCorner()? "1" : "0") + "|" + (card.getBackCorners().get(1).getResourceElement()) + "]";
        }
        else{
            leftCorner= "[" + (card.getFrontCorners().get(2).isAvailableCorner()? "1" : "0") + "|" + (card.getFrontCorners().get(2).getResourceElement()) + "]";
            rightCorner="[" + (card.getFrontCorners().get(1).isAvailableCorner()? "1" : "0") + "|" + (card.getFrontCorners().get(1).getResourceElement()) + "]";;
        }
        System.out.print("|"+YELLOW+leftCorner+RESET);
        for (int i = 0; i < width -(rightCorner.length()+leftCorner.length()+2); i++) {
            System.out.print(" ");
        }
        System.out.println(YELLOW+rightCorner+RESET+"|");
        // Print bottom border
        System.out.print("+");
        for (int i = 0; i < width - 2; i++) {
            System.out.print("-");
        }
        System.out.println("+");
        if (card instanceof GoldCard &&!isHidden) {
            ((GoldCard) card).printRequirements();
        }
        System.out.println();
    }
}
class BroadCastStartingMessage extends Message{

    private HashMap<UUID, Player> players;
    private ArrayList<ObjectiveCard> selectedSecret;
    private HashMap<UUID,ArrayList<ObjectiveCard>> secretObjectiveCardSelector;
    private Boolean starterCardFace;
    public BroadCastStartingMessage(String sender, UUID ClientID, HashMap<UUID, Player> players, ArrayList<ObjectiveCard> commonObjectiveCards,HashMap<UUID,ArrayList<ObjectiveCard>> secretObjectiveCardSelector) {
        super(sender, ClientID);
        this.players = players;
        this.secretObjectiveCardSelector = secretObjectiveCardSelector;
        this.selectedSecret=null;
        this.starterCardFace = null;
    }

    public ArrayList<ObjectiveCard> getSecretObjectiveCards(UUID clientID) {
        return secretObjectiveCardSelector.get(clientID);
    }

    public Boolean getStarterCardFace() {
        return starterCardFace;
    }

    public void setStarterCardFace(Boolean starterCardFace) {
        this.starterCardFace = starterCardFace;
    }

    public void setSelectedSecret(ArrayList<ObjectiveCard> secretObjectiveCardSelector) {
        this.selectedSecret = secretObjectiveCardSelector;
    }

    public ObjectiveCard getSelectedSecret() {
        return selectedSecret.getFirst();
    }

    public HashMap<UUID, Player> getPlayers() {
        return players;
    }
}
class BroadCastStandardMessage extends Message{
    HashMap<UUID,StarterCard> starterCards;
    public BroadCastStandardMessage(String sender, UUID ClientID,HashMap<UUID,StarterCard> starterCards) {
        super(sender, ClientID);
        this.starterCards = starterCards;
    }
}
class TextMessage extends Message{
    private String textMessage;
    private String recipient;
    public TextMessage(String sender, UUID ClientID, String textMex, String recipient) {
        super(sender, ClientID);
        this.textMessage = textMex;
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getTextMessage() {
        return textMessage;
    }
}
class EndGameMessage extends Message{
    private String winner;
    public EndGameMessage(String sender, UUID ClientID,String winner) {
        super(sender, ClientID);
        this.winner = winner;
    }

    public String getWinner() {
        return this.winner;
    }
}