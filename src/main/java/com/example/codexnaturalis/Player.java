package com.example.codexnaturalis;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Player of the game
 */
public class Player implements Serializable {
    /**
     * Defines the player's name
     */
    private String playerName;
    /**
     * Defines the player's token
     */
    private UUID playerID;
    private Token token;
    /**
     * Defines the player's deck
     */
    private PlayerDeck playerDeck;
    /**
     * Defines whether the player is the first to start the game
     */
    private ArrayList<ObjectiveCard> commonObjCards;
    private boolean firstPlayer;
    /**
     * Defines the player's own field
     */
    private Field playerField;
    /**
     * Defines the score of the player
     */
    private int score;
    /**
     * resourceMana: Defines the amount of resources the player has
     * 0: Mushroom
     * 1: Leaf
     * 2: Wolf
     * 3: Butterfly
     */
    private int[] resourceMana;
    /**
     * elementsMana: Defines the amount of elements the player has
     * 0: Ink
     * 1: Papyrus
     * 2: Feather
     */
    private int[] elementsMana;
    /**
     * Defines if the player has played its first turn in the game
     */
    private boolean firstTurn = true;

    /**
     * Constructor of the Player class
     * @param playerName: Defines the player's name
     * @param token: Defines the player's token
     * @param playerField: Defines the player's own field
     */
    public Player(String playerName, Token token, Field playerField,UUID playerID) throws IOException {
        this.playerName = playerName;
        this.token = token;
        this.playerDeck = DrawingDeck.generatePlayerDeck();
        this.firstPlayer = false;
        this.playerField = playerField;
        this.resourceMana = new int[]{0,0,0,0};
        this.elementsMana = new int[]{0,0,0};
        this.playerID = playerID;
    }

    public Player(Token token, Field playerField) throws IOException {
        this.playerName = null;
        this.token = token;
        this.playerDeck = DrawingDeck.generatePlayerDeck();
        this.firstPlayer = false;
        this.playerField = playerField;
        this.resourceMana = new int[]{0,0,0,0};
        this.elementsMana = new int[]{0,0,0};
    }

    /**
     * Places the starter card at the center of the player's field
     * @param isFront: true if the card is faced with if front facing up, otherwise false
     */
    public void placeStarterCard(boolean isFront) {
        int r, c;
        r = c = playerField.getSlots().length / 2;
        StarterCard carta = playerDeck.getStarterCard();
        carta.setIsPlacedFront(isFront);
        playerField.getSlots()[r][c].setBusySlot(true);
        playerField.getSlots()[r][c].setCardSlot(carta);
        playerDeck.setStarterCard(null);
        //Update resources and elements manas
        for (int i = 0; i < 4; i++) {
            Corner corner = carta.getCorners().get(i);
            increaseResourceElementsMana(corner);
        }
    }

    /**
     * Prints the common objectiveCards from the match
     */
    public void printAllObjective() {
        for (ObjectiveCard c : commonObjCards) {
            c.printObjectiveCard();
        }
        playerDeck.getSecretObjectiveCard().printObjectiveCard();
    }


    /**
     * Prints the player field with its name
     */
    public void printFieldWithName() {
        System.out.println("-------------------------");
        System.out.println(playerName + "'s Codex");
        playerField.printField();
    }

    public void placeCard(int row, int column, ResourceGoldCard cardToPlace) {
        playerField.getSlots()[row][column].setCardSlot(cardToPlace);
        playerField.getSlots()[row][column].setBusySlot(true);
        try {
            //Check the corners of the placed card and add them to the manas
            for (int i = 0; i < 4; i++) {
                Corner corner = cardToPlace.getCorners().get(i);
                increaseResourceElementsMana(corner);
            }

            if (row != 0 && row != (playerField.getR() - 1) && column != 0 && column != (playerField.getC() - 1)) {
                for (int i = 0; i < 4; i++) {
                    updateAdjacentSlots(cardToPlace, row, column, i);
                }
            } else {
                if (row == 0) {
                    if (column != 0 && column != (playerField.getC() - 1)) {
                        updateAdjacentSlots(cardToPlace, row, column, 1);
                        updateAdjacentSlots(cardToPlace, row, column, 2);
                    } else if (column == (playerField.getC() - 1)) {
                        updateAdjacentSlots(cardToPlace, row, column, 2);
                    } else if (column == 0) {
                        updateAdjacentSlots(cardToPlace, row, column, 1);
                    }
                } else if (row == (playerField.getR() - 1)) {
                    if (column != 0 && column != (playerField.getC() - 1)) {
                        updateAdjacentSlots(cardToPlace, row, column, 0);
                        updateAdjacentSlots(cardToPlace, row, column, 3);
                    } else if (column == (playerField.getC() - 1)) {
                        updateAdjacentSlots(cardToPlace, row, column, 3);
                    } else if (column == 0) {
                        updateAdjacentSlots(cardToPlace, row, column, 0);
                    }
                } else if (column == 0) {
                    if (row != playerField.getR() - 1) {
                        updateAdjacentSlots(cardToPlace, row, column, 0);
                        updateAdjacentSlots(cardToPlace, row, column, 1);
                    }
                } else if (column == (playerField.getC() - 1)) {
                    if (row != playerField.getR() - 1) {
                        updateAdjacentSlots(cardToPlace, row, column, 2);
                        updateAdjacentSlots(cardToPlace, row, column, 3);
                    }
                }
            }
        } catch(IndexOutOfBoundsException e){
            System.err.println("There was an error placing the card. Try again.");
        }
    }

    /**
     * Given a row, column, and a card, it places the said card and removes it from the players deck
     * @param row: row of the placed card
     * @param column: column of the placed card
     * @param cardToPlace: card to place
     * @throws Exception
     */
    public void placeCardAndRemoveFromDeck(int row, int column, ResourceGoldCard cartaDaPiazzare) throws Exception {
        playerField.getSlots()[row][column].setCardSlot(cartaDaPiazzare);
        playerField.getSlots()[row][column].setBusySlot(true);

        //Check the corners of the placed card and add them to the manas
        for (int i = 0; i < 4; i++) {
            Corner corner = cartaDaPiazzare.getCorners().get(i);
            increaseResourceElementsMana(corner);
        }

        if (row != 0 && row != (playerField.getR()-1) && column != 0 && column != (playerField.getC()-1)) {
            for (int i = 0; i < 4; i++) {
                updateAdjacentSlots(cartaDaPiazzare, row, column, i);
            }
        } else {
            if (row == 0) {
                if (column != 0 && column != (playerField.getC()-1)) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 1);
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 2);
                } else if (column == (playerField.getC()-1)) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 2);
                } else if (column == 0) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 1);
                }
            } else if (row == (playerField.getR()-1)) {
                if (column != 0 && column != (playerField.getC()-1)) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 0);
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 3);
                } else if (column == (playerField.getC()-1)) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 3);
                } else if (column == 0) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 0);
                }
            } else if (column == 0) {
                if (row != playerField.getR() - 1) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 0);
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 1);
                }
            } else if (column == (playerField.getC()-1)) {
                if (row != playerField.getR() - 1) {
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 2);
                    updateAdjacentSlots(cartaDaPiazzare, row, column, 3);
                }
            }
        }

        //Remove the placed card from the player's deck
        playerDeck.getResourceGoldCards().remove(cardToPlace);

    }

    /**
     * Given a card to place in the field, one of its corners, the slot's row and column, updates the corner
     * of the adjacent card
     * @param cardToPlace: card place on the field
     * @param selectedRow: Row of the placed card
     * @param selectedColumn: Column of the placed card
     * @param corner: corner of the placed card to check
     * @throws Exception
     */
    private void updateAdjacentSlots(ResourceGoldCard cardToPlace,int selectedRow, int selectedColumn, int corner) throws IndexOutOfBoundsException {
        int rowToCheck = selectedRow + calculateOffSetR(corner);
        int columnToCheck = selectedColumn + calculateOffSetC(corner);
        /* Check if the adjacent slot is busy. If busy update the availability of the adjacent card's corner and
        * update the availability of the placed card. Also update resourceMana and elementsMana
        *  */
        if (playerField.getSlots()[rowToCheck][columnToCheck].isBusySlot()) {
            NonObjectiveCard coveredCard = playerField.getSlots()[rowToCheck][columnToCheck].getCardSlot();
            int coveredCornerIndex = findCornerToPlace(corner);
            Corner coveredCorner = coveredCard.getCorners().get(coveredCornerIndex);
            decreaseResourceElementsMana(coveredCorner);
            cardToPlace.updateCornerToBusy(corner);
            coveredCard.updateCornerToBusy(coveredCornerIndex);
            cardToPlace.coveredCornersWhenPlaced++;
        }
    }

    /**
     * Given a Corner, analyses its content and updates the player's manas
     * @param corner: corner to check
     */

    private void increaseResourceElementsMana(Corner corner) {
        switch (corner.getResourceElement()) {
            case Mushroom -> resourceMana[0]++;
            case Leaf -> resourceMana[1]++;
            case Wolf -> resourceMana[2]++;
            case Butterfly -> resourceMana[3]++;
            case Ink -> elementsMana[0]++;
            case Papyrus -> elementsMana[1]++;
            case Feather -> elementsMana[2]++;
        }
    }


    /**
     * Given a Corner, analyses its content and updates the player's manas
     * @param corner: corner to check
     */
    private void decreaseResourceElementsMana(Corner corner) {
        switch (corner.getResourceElement()) {
            case Mushroom -> resourceMana[0]--;
            case Leaf -> resourceMana[1]--;
            case Wolf -> resourceMana[2]--;
            case Butterfly -> resourceMana[3]--;
            case Ink -> elementsMana[0]--;
            case Papyrus -> elementsMana[1]--;
            case Feather -> elementsMana[2]--;
        }
    }

    /**
     * Checks is a card is attachable to the adjacent slots
     * @param row: row of placed card
     * @param column: column of placed card
     * @return boolean, true if card can be placed, false otherwise
     */
    public boolean isCardAttachableToSlot(int row, int column) {
        boolean flag = true;
        for (int i = 0; i < 4; i++) {
            int rowToCheck = calculateOffSetR(i);
            int columnToCheck = calculateOffSetC(i);
            Field.Slot adjacentSlot = playerField.getSlots()[rowToCheck][columnToCheck];
            if (adjacentSlot.getCardSlot().getCorners().get(findCornerToPlace(i)).isAvailableCorner()) {
                flag = false;
                break;
            }
        }
        return flag;
    }


    /**
     * Let A be a card on the field, the player wants to place card B over one if A's corners. Finds which one of
     * B's corners will be updated
     * @param cornerOfPlacedCard: integer defining A's corner where B will be placed (UR[0], BR[1], BL[2], UL[3])
     * @return int, defines B's corner that will be later updated (UR[0], BR[1], BL[2], UL[3])
     */
    private int findCornerToPlace(int cornerOfPlacedCard) {
        return switch (cornerOfPlacedCard) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 0;
            case 3 -> 1;
            default -> -1;
        };
    }
    /**
     * Calculates the row where the new card will be placed based on the card that's already on the player's field
     * @param corner: integer defining the corner of the card already on the field (UR[0], BR[1], BL[2], UL[3])
     * @return int, defines the row offset of the card that will be placed
     */
    protected int calculateOffSetR(int corner) {
        int offSetR = 0;
        switch (corner) {
            case 0, 3:
                offSetR = -1;
                break;
            case 1, 2:
                offSetR = 1;
                break;
            default:
        }
        return offSetR;
    }

    /**
     * Calculates the column where the new card will be placed based on the card that's already on the player's field
     * @param corner: integer defining the corner of the card already on the field (UR[0], BR[1], BL[2], UL[3])
     * @return int, defines the column offset of the card that will be placed
     */
    protected int calculateOffSetC(int corner) {
        int offSetC = 0;
        switch (corner) {
            case 0, 1:
                offSetC = 1;
                break;
            case 2, 3:
                offSetC = -1;
                break;
            default:
        }
        return offSetC;
    }

    /**
     * Adds points to the player score
     * @param points: points to be added
     */
    public void addScore(int points) {
        score += points;
    }

    /**
     * Adds number of resources available to the player
     * @param mana: number of resources to be added
     * @param index: type of Resource
     */
    public void addResourceMana(int mana, int index) {
        resourceMana[index] += mana;
    }

    /**
     * Adds number of elements available to the player
     * @param mana: number of elements to be added
     * @param index: type of Element
     */
    public void addElementsMana(int mana, int index) {
        elementsMana[index] += mana;
    }
    //SETTERS AND GETTERS
    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * Given an array list of cards, allow the player to choose one
     * @param cards: cards form which the player will choose
     * @return ObjectiveCard, chosen from the player
     * @throws StupidUserException
     */
    public ObjectiveCard chooseSecretObj(ArrayList<ObjectiveCard> cards) throws StupidUserException {
        System.out.println("Choose a secret objective card: ");
        int i=1;
        int choice;
        while (true) {
            for (ObjectiveCard c : cards) {
                System.out.println(i + ": ");
                c.printObjectiveCard();
                i++;
            }
            try{
                choice = Integer.parseInt(ZakClient.receiveInput());
            } catch (Exception e){
                System.out.println("Invalid input: try again");
                continue;
            }
            if(choice>=1 && choice<=2) break;
            else if(choice==3)throw new StupidUserException("Too many wrong input were given");
        }
        playerDeck.setSecretObjectiveCard(cards.get(choice-1));
        return cards.get(choice-1);
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public void setCommonObjCards(ArrayList<ObjectiveCard> cards) {
        this.commonObjCards = cards;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerDeck getPlayerDeck() {
        return playerDeck;
    }

    public Field getPlayerField() {
        return playerField;
    }

    public int getScore() {
        return score;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }

    public int[] getResourceMana() {
        return resourceMana;
    }

    public int[] getElementsMana() {
        return elementsMana;
    }

    public UUID getPlayerID() {
        return playerID;
    }

}
