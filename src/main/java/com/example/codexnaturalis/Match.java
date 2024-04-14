package com.example.codexnaturalis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Match {
    private ArrayList<Player> players;
    private ScoreTracker scoreTracker;
    BufferedReader in;
    PrintWriter out;
    private boolean lastRound = false;
    private ChoiceManager choices;

    /**
     * Constructor of Match
     * @param players: ArrayList of all players in the match
     * @param scoreTracker: score tracker
     */
    public Match(ArrayList<Player> players, ScoreTracker scoreTracker) {
        this.players = players;
        this.scoreTracker = scoreTracker;
    }

    public Match(ScoreTracker scoreTracker) {
        this.players = new ArrayList<>();
        this.scoreTracker = scoreTracker;
    }

    /**
     * Main function that starts the match. Also, only public method
     */
    public void startMatch() throws Exception {
        int sceltaGiocatore;
        boolean playerHaPiazzatoLaCarta = false;
        /*Si inizia scegliendo in modo casuale il giocatore iniziale*/
        Player playingPlayer = chooseRandomFirstPlayer();
        Socket playingPlayerSocket = ZakServer.hashPlayer.get(playingPlayer);
        in = new BufferedReader(new InputStreamReader(playingPlayerSocket.getInputStream()));
        out = new PrintWriter(playingPlayerSocket.getOutputStream(), true);

        /*Creo il choice container per le scelte del giocatore*/
        choices = new ChoiceManager(in, out);

        /*Il giocatore piazza la sua carta iniziale*/
        placeStarterCard(playingPlayer);

        /*Il giocatore sceglie cosa fare*/
        sceltaGiocatore = choices.chooseFromMenu(false);

        while (!lastRound) {

            while (sceltaGiocatore != -1) {

                /*Il giocatore ha scelto di piazzare la sua carta*/
                if (sceltaGiocatore == 1 && !playerHaPiazzatoLaCarta) {

                    /*Player sta piazzando la carta, questo boolean impedisce che il giocatore
                    * faccia di nuovo questa azione nello stesso turno*/
                    playerHaPiazzatoLaCarta = true;

                    /*Piazza una carta dal mazzo*/
                    placeCard(playingPlayer);

                    //TODO: aumentare i punti nello scoreTracker

                    /*Il giocatore ora deve pescare una carta dai due mazzi risorsa od oro*/
                    drawCard(playingPlayer);
                } else if (sceltaGiocatore == 2) {
                    /*Il giocatore sceglie una carta dando come input la sua riga e colonna*/
                    fieldAnalysis(playingPlayer);
                }

                /*Il giocatore sceglie cosa fare*/
                sceltaGiocatore = choices.chooseFromMenu(playerHaPiazzatoLaCarta);
            }

            if (checkWinner(playingPlayer)) {
                lastRound = true;
            } else {
                /*Playing player ora passa al prossimo giocatore*/
                playingPlayer = selectNextPlayer(playingPlayer);
                playerHaPiazzatoLaCarta = false;

                /*Il giocatore sceglie cosa fare*/
                sceltaGiocatore = choices.chooseFromMenu(playerHaPiazzatoLaCarta);

            }

        }
        //TODO

        lastRoundRoutine();

    }

    /**
     * Chooses who will be the first player and updates its firstPlayer attribute
     * @return Player, first player of the match
     */
    private Player chooseRandomFirstPlayer() {

        int randomIndex = new Random().nextInt(players.size());

        Player playingPlayer = players.get(randomIndex);
        playingPlayer.setFirstPlayer(true);
        playingPlayer.setFirstTurn(false);
        System.out.println(playingPlayer.getPlayerName() + " è il primo a giocare!");
        return playingPlayer;
    }

    /**
     * places the player's starter card on its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void placeStarterCard(Player playingPlayer) throws IOException {

        if (playingPlayer.getPlayerDeck().getStarterCard() == null) {
            throw new RuntimeException("CARTA STARTER NON ESISTENTE PER " + playingPlayer.getPlayerName());
        }

        out.println("Piazza la carta iniziale...");
        playingPlayer.getPlayerDeck().getStarterCard().printCardFrontAndBack(out);
        playingPlayer.placeStarterCard(choices.chooseFrontOrBack());
        playingPlayer.getPlayerField().printField(out);
    }

    /**
     * Lets the player select a card from its deck to place on its field
     * @param playingPlayer: player that is playing at the moment
     * @return int, defines the number of the card in the player's deck
     */
    private ResourceGoldCard selectCard(Player playingPlayer) throws IOException {
        //TODO: fare il check dei requisiti se ci sono
        ResourceGoldCard cartaScelta;
        ArrayList<ResourceGoldCard> carteInMano = playingPlayer.getPlayerDeck().getResourceGoldCards();

        playingPlayer.getPlayerDeck().printResourceGoldCards(out);
        int sceltaPlayer = choices.chooseCard();
        cartaScelta = carteInMano.get(sceltaPlayer - 1);
        return cartaScelta;
    }

    /**
     * By using auxiliary private methods, allows the player the place a card on its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void placeCard(Player playingPlayer) throws Exception {
        int riga = -1;
        int colonna = -1;
        int sceltaAngolo;
        Field field = playingPlayer.getPlayerField();
        boolean slotHaLaCarta = false;

        /*Il giocatore sceglie quale carta piazzare*/
        ResourceGoldCard cartaDaPiazzare = selectCard(playingPlayer);

        /*Il giocatore sceglie come piazzare la carta*/
        boolean isSceltaFronte = choices.chooseFrontOrBack();
        cartaDaPiazzare.setIsPlacedFront(isSceltaFronte);

        /*Il giocatore sceglie la riga e colonna della carta a cui si attaccherà*/
        while (!slotHaLaCarta) {
            out.println("Seleziona riga e colonna della carta a cui ti attaccherai sul tavolo...");
            field.printField(out);
            riga = choices.chooseRow(field);
            colonna = choices.chooseColumn(field);
            /*Checks se esiste una carta in questo slot*/
            Field.Slot slotToCheck = field.getSlots()[riga][colonna];
            slotHaLaCarta = slotToCheck.isBusySlot();
        }

        NonObjectiveCard cartaNelTavolo = playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot();

        /*Il giocatore vede gli angoli della carta che vuole piazzare*/
        out.println("Carta da piazzare: ");
        cartaDaPiazzare.printCard(out);

        /*Il giocatore vede la carta sul tavolo che ha scelto come base*/
        out.println("Carta selezionata sul tavolo:");
        cartaNelTavolo.printCard(out);

        /*Il giocatore sceglie quale angolo occupare della carta piazzata*/
        sceltaAngolo = choices.chooseCorner(cartaNelTavolo);

        /*La carta scelta dal giocatore viene piazzata in modo opportuna sul tavolo attaccata alla carta selezionata come base*/
        playingPlayer.placeCard(riga, colonna, cartaDaPiazzare, sceltaAngolo);

        //TODO: se la carta da punti aumentarli nello scoreTracker
        /*Display del tavolo per controllare*/
        playingPlayer.getPlayerField().printField(out);
    }

    /**
     * Allows the player to draw a card from either the resource deck or gold deck
     * @param playingPlayer: player that is playing at the moment
     */
    private void drawCard(Player playingPlayer) throws IOException {
        //TODO: finire la funzione della pesca
        //TODO: il giocatore deve poter pescare anche dalle 4 carte che ci sono accanto ai mazzi
        //TODO: se il giocatore pesca da una delle quattro carte, questa deve essere rimpiazzata dal corrispettivo mazzo
        int sceltaMazzo = -1;
        boolean isMazzoVuoto = true;

        while (isMazzoVuoto) {
            /*Il giocatore sceglie da quale mazzo pescare*/
            sceltaMazzo = choices.chooseDecksToDraw();

            /*Se il mazzo è esaurito, isMazzoVuoto rimane true e verrà chiesto di nuovo al giocatore di scegliere*/
            isMazzoVuoto = DrawingDeck.checkDeckEmptiness(sceltaMazzo);
        }

        /*Viene pescata la carta dal mazzo scelto, aggiunta al mazzo del player*/
        playingPlayer.getPlayerDeck().getResourceGoldCards().add(
                DrawingDeck.drawCard(sceltaMazzo == 1)
        );

    }

    /**
     * Allows the playing player to analise its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void fieldAnalysis(Player playingPlayer) throws IOException {
        int riga = -1;
        int colonna = -1;
        boolean slotHaLaCarta = false;
        Field field = playingPlayer.getPlayerField();

        while (!slotHaLaCarta) {
            field.printField(out);
            riga = choices.chooseRow(field);
            colonna = choices.chooseColumn(field);
            /*Checks se esiste una carta in questo slot*/
            Field.Slot slotToCheck = field.getSlots()[riga][colonna];
            slotHaLaCarta = slotToCheck.isBusySlot();
        }

        NonObjectiveCard carta = field.getSlots()[riga][colonna].getCardSlot();

        out.println("Analisi della carta nello slot [" + riga + "][" + colonna + "].");
        carta.printCard(out);
    }

    /**
     * Given an index, selects the next player that will be playing after the current player
     * @param currentPlayer: current player that's playing
     * @return Player, next player in line
     */
    private Player selectNextPlayer(Player currentPlayer) throws IOException {
        int index = selectIndexNextPlayer(players.indexOf(currentPlayer));

        Player nextPlayer = players.get(index);
        Socket nextPlayerSocket = ZakServer.hashPlayer.get(nextPlayer);
        System.out.println("Prossimo turno... \n Tocca a " + nextPlayer.getPlayerName());

        in = new BufferedReader(new InputStreamReader(nextPlayerSocket.getInputStream()));
        out = new PrintWriter(nextPlayerSocket.getOutputStream(), true);

        choices.setIn(in);
        choices.setOut(out);

        if (nextPlayer.isFirstTurn()) {
            placeStarterCard(nextPlayer);
            nextPlayer.setFirstTurn(false);
        }
        return nextPlayer;
    }

    /**
     * Selects the index of the next player to play
     * @param currentIndex: defines the current index of the current player
     * @return int, defines the index of the next player in line
     */
    public int selectIndexNextPlayer(int currentIndex) {
        int indiceProssimo = currentIndex + 1;
        if (indiceProssimo >= players.size()) {
            indiceProssimo = 0;
        }
        return indiceProssimo;
    }

    /**
     * Adds a player to the Arraylist
     * @param player: player to be added
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Checks if a player has reached at least 20 points
     */
    public boolean checkWinner(Player playingPlayer) {
        boolean winnerFlag = false;
        if (playingPlayer.getScore() >= 20) {
            winnerFlag = true;
            System.out.println(playingPlayer.getPlayerName() + " ha raggiunto " + playingPlayer.getScore() + " punti!");
        }
        return  winnerFlag;
    }

    /**
     * Last Round Routine
     */
    private void lastRoundRoutine() {
        //TODO
        //Si fa il giro dei giocatori rimanenti
        checkTotalPoints();
        declareWinner();
        //Dichiara il vincitore
    }

    private void checkTotalPoints() {
        //TODO

    }

    private Player declareWinner() {
        //TODO
        Player player = null;

        return player;
    }
}


