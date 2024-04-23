//DA CANCELLARE
package com.example.codexnaturalis;

import java.io.IOException;
import java.io.Serializable;
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
     * @param fronte: true if the card is faced with if front facing up, otherwise false
     */
    public void placeStarterCard(boolean fronte) {
        int r, c;
        r = c = playerField.getSlots().length / 2;
        StarterCard carta = playerDeck.getStarterCard();
        carta.setIsPlacedFront(fronte);
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
     * TODO: DOCS
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
        playerDeck.getResourceGoldCards().remove(cartaDaPiazzare);

    }

    private void updateAdjacentSlots(ResourceGoldCard cartaDaPiazzare,int selectedRow, int selectedColumn, int corner) throws Exception {
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
            cartaDaPiazzare.updateCornerToBusy(corner);
            coveredCard.updateCornerToBusy(coveredCornerIndex);
            cartaDaPiazzare.coveredCornersWhenPlaced++;
        }
    }

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

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
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
