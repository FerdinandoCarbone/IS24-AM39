package com.example.codexnaturalis;

public class Player {

    private String playerName;
    private Token token;
    private Decks.PlayerDeck playerDeck;
    private boolean firstPlayer;
    private Field playerField;
    private int punteggio;

    public Player(String playerName, Token token, Decks.PlayerDeck playerDeck, Field playerField) {
        this.playerName = playerName;
        this.token = token;
        this.playerDeck = playerDeck;
        this.firstPlayer = false;
        this.playerField = playerField;
    }


    /**
     * Piazza la carta Iniziale al centro del tavolo del Player
     * @param fronte true se la carta è piazzata di fronte o di retro
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
     * Sia A una carta già piazzata sul tavolo, si vuole piazzare sopra di essa una carta B.
     * @param rCartaPiazzata riga della carta A
     * @param cCartaPiazzata colonna della carta B
     * @param cartaDaPiazzare carta B
     * @param fronte indica come sarà piazzata B, true è fronte false retro
     * @param angoloCartaPiazzata indica l'angolo della carta A dove verrà piazzata B
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
     * Printa su riga di comando la situazione del tavolo dove [0] indica uno slot non occupato, [1] altrimenti
     */
    public void printField() {
        System.out.println("-------------------------");
        for (int i = 0; i < playerField.getR(); i++) {
            for (int j = 0; j < playerField.getC(); j++) {
                System.out.print("[" + playerField.getSlots()[i][j].getBusySlot() + "]");
            }
            System.out.println();
        }
    }

    /**
     * Printa su riga di comando lo stato del mazzo del giocatore
     */
    public void printDeck() {
        System.out.println(":::Mazzo di " + playerName + ":::");
        for (int i = 1; i <= playerDeck.getCards().size(); i++) {
            System.out.println(i + ") " + playerDeck.getCards().get(i-1).getClass());
        }
    }

    public void printStarterCard() {
        System.out.println("Carta iniziale di " + playerName);
        System.out.println(playerDeck.getStarterCard().toString());
    }


    /**
     * Ritorna true se lo Slot è occupato, false altrimenti
     * @param r riga dello Slot
     * @param c colonna dello Slot
     * @return
     */
    private boolean checkBusySlot(int r, int c) {
        boolean flag;
        if (playerField.getSlots()[r][c].getBusySlot()) {
            System.out.println("ERRORE: SLOT GIA' OCCUPATO");
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * Ritorna true se la carta è nel mazzo del giocatore, false altrimenti
     * @param carta
     * @return
     */
    private boolean checkCardInDeck(Card.NonObjectiveCard carta) {
        boolean flag;
        if (!playerDeck.getCards().contains(carta)) {
            System.out.println("ERRORE: CARTA NON DISPONIBILE NEL MAZZO");
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * Ritorna true se l'angolo della carta è disponibile, false altrimenti
     * @param carta
     * @param fronte
     * @param angolo
     * @return
     */
    private boolean checkAvailableCorner(Card.NonObjectiveCard carta, boolean fronte, int angolo) {
        boolean flag;
        if (fronte) {
            flag = carta.getFrontCorners().get(angolo).getAvailableCorner();
        } else {
            flag = carta.getBackCorners().get(angolo).getAvailableCorner();
        }
        return flag;
    }

    /**
     * Aggiorna lo stato di un angolo. Per esempio se viene piazzato una carta A sopra l'angolo in alto a destra di una carta B,
     * l'angolo in alto a destra di B verrà aggiornato a 0 mentre l'angolo in basso a sinistra della carta A verra aggiornato a 0
     * @param carta
     * @param angolo
     */
    private void updateCorner(Card.NonObjectiveCard carta, int angolo) {
        if (carta.getPiazzataInFronte()) {
            carta.getFrontCorners().get(angolo).setAvailableCorner(false);
        } else {
            carta.getBackCorners().get(angolo).setAvailableCorner(false);
        }
    }

    /**
     * Data una carta A nel tavolo, voglio piazzare una carta B sopra di essa. Allora l'angolo che verrà aggiornato dell'angolo B sarà
     * uguale all'indice dell'angolo occupato di A sommato a 2
     * @param angoloPiazzato
     * @return
     */
    private int findCornerToPlace(int angoloPiazzato) {
        return switch (angoloPiazzato) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 0;
            case 3 -> 1;
            default -> -1;
        };
    }

    /**
     * Calcola la riga dove sarà piazzata la nuova carta in base alla posizione della carta già sul tavolo
     * @param angolo
     * @return
     */
    private int calculateOffSetR(int angolo) {
        int offSetR = 0;
        switch (angolo) {
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
     * Calcola la colonna dove sarà piazzata la nuova carta in base alla posizione della carta già sul tavolo
     * @param angolo
     * @return
     */
    private int calculateOffSetC(int angolo) {
        int offSetC = 0;
        switch (angolo) {
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


    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Decks.PlayerDeck getPlayerDeck() {
        return playerDeck;
    }

    public Field getPlayerField() {
        return playerField;
    }
}
