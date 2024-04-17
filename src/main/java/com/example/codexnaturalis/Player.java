//DA CANCELLARE
package com.example.codexnaturalis;

import java.io.IOException;

/**
 * Player of the game
 */
public class Player {
    /**
     * Defines the player's name
     */
    private String playerName;
    /**
     * Defines the player's token
     */
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
     */
    private int[] resourceMana;
    /**
     * elementsMana: Defines the amount of elements the player has
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
    public Player(String playerName, Token token, Field playerField) throws IOException {
        this.playerName = playerName;
        this.token = token;
        this.playerDeck = DrawingDeck.generatePlayerDeck();
        this.firstPlayer = false;
        this.playerField = playerField;
        this.resourceMana = new int[]{0,0,0,0};
        this.elementsMana = new int[]{0,0,0};
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
        //TODO: aggiornare i mana
    }

    /**
     * Let A be a card already on the field and B a card that the player wants to place on top of B
     * @param rCartaPiazzata: row of card A
     * @param cCartaPiazzata: column of card B
     * @param cartaDaPiazzare: card B
     * @param angoloCartaPiazzata: Defines the angle of card A where B will be placed
     */
    public void placeCard(int rCartaPiazzata, int cCartaPiazzata, NonObjectiveCard cartaDaPiazzare, int angoloCartaPiazzata) throws Exception {
        NonObjectiveCard cartaPiazzata = playerField.getSlots()[rCartaPiazzata][cCartaPiazzata].getCardSlot();
        
        if (cartaPiazzata.checkAvailableCorner(angoloCartaPiazzata)) {
            int offSetR = calculateOffSetR(angoloCartaPiazzata);
            int offSetC = calculateOffSetC(angoloCartaPiazzata);
            int rCartaDaPiazzare = rCartaPiazzata + offSetR;
            int cCartaDaPiazzare = cCartaPiazzata + offSetC;
            int angoloOccupatoCartaDaPiazzare = findCornerToPlace(angoloCartaPiazzata);

            playerField.getSlots()[rCartaDaPiazzare][cCartaDaPiazzare].setCardSlot(cartaDaPiazzare);
            playerField.getSlots()[rCartaDaPiazzare][cCartaDaPiazzare].setBusySlot(true);

            cartaPiazzata.updateCorner(angoloCartaPiazzata);
            cartaDaPiazzare.updateCorner(angoloOccupatoCartaDaPiazzare);

            playerDeck.getResourceGoldCards().remove(cartaDaPiazzare);
            System.out.println("Carta " + cartaDaPiazzare.getClass() + " piazzata nello slot [" + rCartaDaPiazzare + "][" + cCartaDaPiazzare + "]." );

            //TODO: AGGIUNGERE PUNTI/ELEMENTS/RESOURCES QUANDO SI PIAZZA LA CARTA

        } else {
            throw new RuntimeException("ANGOLO NON DISPONIBILE");
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
}
