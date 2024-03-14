package com.example.codexnaturalis;

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
    private Decks.PlayerDeck playerDeck;
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
     * Constructor of the Player class
     * @param playerName: Defines the player's name
     * @param token: Defines the player's token
     * @param playerDeck: Defines the player's deck
     * @param playerField: Defines the player's own field
     */
    public Player(String playerName, Token token, Decks.PlayerDeck playerDeck, Field playerField) {
        this.playerName = playerName;
        this.token = token;
        this.playerDeck = playerDeck;
        this.firstPlayer = false;
        this.playerField = playerField;
    }


    /**
     * Places the starter card at the center of the player's field
     * @param fronte: true if the card is faced with if front facing up, otherwise false
     */
    public void placeStarterCard(boolean fronte) {
        int r, c;
        r = c = playerField.getSlots().length / 2;
        Card.NonObjectiveCard.StarterCard carta = playerDeck.getStarterCard();
        carta.setIsPlacedFront(fronte);
        playerField.getSlots()[r][c].setBusySlot(true);
        playerField.getSlots()[r][c].setCardSlot(carta);
        playerDeck.setStarterCard(null);
        System.out.println("Carta " + carta.getClass() + " piazzata nello slot [" + r + "][" + c + "]." );
    }

    /**
     * Let A be a card already on the field and B a card that the player wants to place on top of B
     * @param rCartaPiazzata: row of card A
     * @param cCartaPiazzata: column of card B
     * @param cartaDaPiazzare: card B
     * @param fronte: defines how B will be placed, true for front facing up, false otherwise
     * @param angoloCartaPiazzata: Defines the angle of card A where B will be placed
     */
    public void placeCard(int rCartaPiazzata, int cCartaPiazzata, Card.NonObjectiveCard cartaDaPiazzare, boolean fronte, int angoloCartaPiazzata) {
        Card.NonObjectiveCard cartaPiazzata = playerField.getSlots()[rCartaPiazzata][cCartaPiazzata].getCardSlot();
        if (!checkCardInDeck(cartaDaPiazzare)) return;
        if (checkAvailableCorner(cartaPiazzata, cartaPiazzata.getPiazzataInFronte(), angoloCartaPiazzata)) {
            int offSetR = calculateOffSetR(angoloCartaPiazzata);
            int offSetC = calculateOffSetC(angoloCartaPiazzata);
            int rCartaDaPiazzare = rCartaPiazzata + offSetR;
            int cCartaDaPiazzare = cCartaPiazzata + offSetC;
            int angoloOccupatoCartaDaPiazzare = findCornerToPlace(angoloCartaPiazzata);

            playerField.getSlots()[rCartaDaPiazzare][cCartaDaPiazzare].setCardSlot(cartaDaPiazzare);
            playerField.getSlots()[rCartaDaPiazzare][cCartaDaPiazzare].setBusySlot(true);
            cartaDaPiazzare.setIsPlacedFront(fronte);

            updateCorner(cartaPiazzata, angoloCartaPiazzata);
            updateCorner(cartaDaPiazzare, angoloOccupatoCartaDaPiazzare);

            playerDeck.getCards().remove(cartaDaPiazzare);
            System.out.println("Carta " + cartaDaPiazzare.getClass() + " piazzata nello slot [" + rCartaDaPiazzare + "][" + cCartaDaPiazzare + "]." );
        }
    }


    /**
     * Prints the state of the player's field, [1] is a busy slot, [0] otherwise
     */
    public void printField() {
        System.out.println("-------------------------");
        for (int i = 0; i < playerField.getR(); i++) {
            for (int j = 0; j < playerField.getC(); j++) {
                System.out.print("[" + (playerField.getSlots()[i][j].isBusySlot()? "1" : "0") + "]");
            }
            System.out.println();
        }
    }

    /**
     * Prints the state of the player's deck
     */
    public void printDeck() {
        System.out.println(":::Mazzo di " + playerName + ":::");
        for (int i = 1; i <= playerDeck.getCards().size(); i++) {
            System.out.println(i + ") " + playerDeck.getCards().get(i-1).getClass());
        }
    }

    /**
     * Prints the details of the starter Card
     */
    public void printStarterCard() {
        System.out.println("Carta iniziale di " + playerName);
        System.out.println(playerDeck.getStarterCard().toString());
    }


    /**
     * Checks if a slot is busy
     * @param r slot's row
     * @param c slot's column
     * @return boolean, true if slot is busy, false otherwise
     */
    private boolean checkBusySlot(int r, int c) {
        boolean flag;
        if (playerField.getSlots()[r][c].isBusySlot()) {
            System.out.println("ERRORE: SLOT GIA' OCCUPATO");
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * Checks if the card is in the player's deck
     * @param card: card that will be checked
     * @return boolean, true if the card is in the player's deck, otherwise false
     */
    private boolean checkCardInDeck(Card.NonObjectiveCard card) {
        boolean flag;
        if (!playerDeck.getCards().contains(card)) {
            System.out.println("ERRORE: CARTA NON DISPONIBILE NEL MAZZO");
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * Checks if the corner is available
     * @param card: card that will be checked
     * @param inFront: boolean, true if the card is placed with the front facing up, otherwise false
     * @param corner: integer defining the corner that will be checked in order (UR[0], BR[1], BL[2], UL[3])
     * @return boolean, true if the corner is available, otherwise false
     */
    private boolean checkAvailableCorner(Card.NonObjectiveCard card, boolean inFront, int corner) {
        boolean flag;
        if (inFront) {
            flag = card.getFrontCorners().get(corner).isAvailableCorner();
        } else {
            flag = card.getBackCorners().get(corner).isAvailableCorner();
        }
        return flag;
    }

    /**
     * Updates the state of a card's corner. If card A is placed on the upper right (UR) corner of card B
     * the UR corner of B will be updated to [0] while the bottom left (BL) corner if A will be updated to [0]
     * @param card: card that will be updated
     * @param corner: integer defining the corner that will be updated (UR[0], BR[1], BL[2], UL[3])
     */
    private void updateCorner(Card.NonObjectiveCard card, int corner) {
        if (card.getPiazzataInFronte()) {
            card.getFrontCorners().get(corner).setAvailableCorner(false);
        } else {
            card.getBackCorners().get(corner).setAvailableCorner(false);
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
    private int calculateOffSetR(int corner) {
        int offSetR = 0;
        switch (corner) {
            case 0:
                offSetR = -1;
                break;
            case 1:
                offSetR = 1;
                break;
            case 2:
                offSetR = 1;
                break;
            case 3:
                offSetR = -1;
            default:
        }
        return offSetR;
    }

    /**
     * Calculates the column where the new card will be placed based on the card that's already on the player's field
     * @param corner: integer defining the corner of the card already on the field (UR[0], BR[1], BL[2], UL[3])
     * @return int, defines the column offset of the card that will be placed
     */
    private int calculateOffSetC(int corner) {
        int offSetC = 0;
        switch (corner) {
            case 0:
                offSetC = 1;
                break;
            case 1:
                offSetC = 1;
                break;
            case 2:
                offSetC = -1;
                break;
            case 3:
                offSetC = -1;
            default:
        }
        return offSetC;
    }

    /**
     * Setter of firstPlayer
     * @param firstPlayer: boolean, true if the Player is the first to play, false otherwise
     */
    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * Getter of player's name
     * @return String, defines the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Getter of playerDeck
     * @return PlayerDeck
     */
    public Decks.PlayerDeck getPlayerDeck() {
        return playerDeck;
    }

    /**
     * Getter of the player's field
     * @return FIELD
     */
    public Field getPlayerField() {
        return playerField;
    }
}
