package com.example.codexnaturalis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class Match {

    private ArrayList<Player> players;
    private ScoreTracker scoreTracker;
    BufferedReader in;
    PrintWriter out;
    private boolean lastRound = false;

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
    public void startMatch() throws IOException {
        int sceltaGiocatore;
        int flagCartaGiocata = 0;
        /*Si inizia scegliendo in modo casuale il giocatore iniziale*/
        int indiceGiocatoreInGioco = randomIndex();
        Player playingPlayer = chooseFirstPlayer(indiceGiocatoreInGioco);
        playingPlayer.setFirstTurn(false);
        /*Inizia la partita con la carta iniziale piazzata dal primo giocatore*/

        in = new BufferedReader(new InputStreamReader(playingPlayer.getPlayerSocket().getInputStream()));
        out = new PrintWriter(playingPlayer.getPlayerSocket().getOutputStream(), true);
        placeStarterCard(playingPlayer, in, out);

        sceltaGiocatore = chooseFromMenu(in, out);

        while (!lastRound) {
            while (sceltaGiocatore != -1) {

                if (sceltaGiocatore == 1) {

                    /*Cambio il flag della carta giocata per evitare che il giocatore ne piazzi un'altra nello stesso turno di gioco*/
                    flagCartaGiocata = 1;
                    /*Piazza una carta dal mazzo*/
                    placeCard(playingPlayer, in, out);
                    /*Il giocatore ora deve pescare una carta dai due mazzi risorsa od oro*/
                    drawCard(playingPlayer, in, out);
                } else if (sceltaGiocatore == 2) {
                    /*Il giocatore sceglie una carta dando come input la sua riga e colonna*/
                    fieldAnalysis(playingPlayer, in, out);
                }

                out.println("Cosa vuoi fare ?");
                if (flagCartaGiocata == 0) {
                    out.println("1) Piazza una Carta");
                    out.println("2) Analizza il tavolo");
                } else {
                    out.println("-1) Finisci il turno");
                    out.println("2) Analizza il tavolo");
                }
                sceltaGiocatore = Integer.parseInt(in.readLine());

            }

            if (checkWinner(playingPlayer)) {
                lastRound = true;
            } else {
                indiceGiocatoreInGioco = selectIndexNextPlayer(indiceGiocatoreInGioco);

                playingPlayer = selectNextPlayer(indiceGiocatoreInGioco);
                sceltaGiocatore = chooseFromMenu(in, out);
            }

        }
        //TODO

        lastRoundRoutine();

    }

    /**
     * Casually chooses the index of the first player of the match
     * @return int, index of the first player
     */
    private int randomIndex() {
        Random random = new Random();
        return random.nextInt(players.size());
    }

    /**
     * Chooses who will be the first player and updates its firstPlayer attribute
     * @param playingPlayerIndex: index of the randomly chosen first player
     * @return Player, first player of the match
     */
    private Player chooseFirstPlayer(int playingPlayerIndex) {

        Player playingPlayer = players.get(playingPlayerIndex);
        playingPlayer.setFirstPlayer(true);
        System.out.println(playingPlayer.getPlayerName() + " is the first to play!");
        return playingPlayer;
    }

    /**
     * places the player's starter card on its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void placeStarterCard(Player playingPlayer, BufferedReader in, PrintWriter out) throws IOException {
        int sceltaFronte;

        out.println("Place starter Card...");
        playingPlayer.printStarterCard(out);
        out.println("Seleziona se vuoi piazzare la carta iniziale di fronte o retro: 1) -> Fronte | 0) -> Retro");
        //Questa avverrà nel client
        //sceltaFronte = scanner.nextInt();
        sceltaFronte = Integer.parseInt(in.readLine());
        playingPlayer.placeStarterCard(sceltaFronte == 1, out);
        playingPlayer.printField(out);
    }
    /**
     * Prints a menu giving choices to the player that playing
     * @return int, player's choice
     */
    private int chooseFromMenu(BufferedReader in, PrintWriter out) throws IOException {
        out.println("Cosa vuoi fare ?");
        out.println("1) Piazza una Carta");
        out.println("2) Analizza il tavolo");
        return Integer.parseInt(in.readLine());
        //return scanner.nextInt();
    }

    /**
     * Lets the player select a card from its deck to place on its field
     * @param playingPlayer: player that is playing at the moment
     * @return int, defines the number of the card in the player's deck
     */
    private int selectCard(Player playingPlayer, BufferedReader in, PrintWriter out) throws IOException {
        int sceltaCarta;

        playingPlayer.printDeck(out);
        out.println("Seleziona il numero della carta che vuoi piazzare: ");
        sceltaCarta = Integer.parseInt(in.readLine());

        return sceltaCarta;
    }

    /**
     * By using auxiliary private methods, allows the player the place a card on its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void placeCard(Player playingPlayer, BufferedReader in, PrintWriter out) throws IOException {
        int sceltaCarta;
        int sceltaFronte;
        int riga = -1;
        int colonna = -1;
        int sceltaAngolo = -1;
        NonObjectiveCard cartaNelMazzo;
        NonObjectiveCard cartaNelTavolo;
        boolean cornerFlag = false;
        boolean cardFlag = false;


        /*Il giocatore sceglie quale carta piazzare e come piazzarla*/
        sceltaCarta = selectCard(playingPlayer, in, out);
        cartaNelMazzo = playingPlayer.getPlayerDeck().getPlayerCards().get(sceltaCarta - 1);
        out.println("Seleziona se vuoi piazzarla di fronte o retro: 1) -> Fronte | 0) -> Retro");
        sceltaFronte = Integer.parseInt(in.readLine());

        /*Checks se esiste una carta in questo slot*/
        while (!cardFlag) {
            playingPlayer.printField(out);
            out.println("Seleziona la riga della carta a cui vuoi attaccarti:");
            riga = Integer.parseInt(in.readLine());
            out.println("Seleziona la colonna della carta a cui vuoi attaccarti:");
            colonna = Integer.parseInt(in.readLine());

            cardFlag = checkSlotHasCard(playingPlayer, riga, colonna, out);
        }

        cartaNelTavolo = playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot();

        /*Il giocatore vede gli angoli della carta che vuole piazzare*/
        out.println("Carta da piazzare: ");
        if (sceltaFronte == 1) {
            cartaNelMazzo.printFrontCorners(out);
        } else {
            cartaNelMazzo.printBackCorners(out);
        }
        /*Il giocatore vede la carta sul tavolo che ha scelto come base*/
        out.println("Carta selezionata sul tavolo:");
        if (cartaNelTavolo.isPlacedFront()) {
            cartaNelTavolo.printFrontCorners(out);
        } else {
            cartaNelTavolo.printBackCorners(out);
        }

        //Check della disponibilità dell'angolo
        while (!cornerFlag) {
            out.println("Seleziona l'angolo della carta sul tavolo a cui vuoi attaccarti (a partire da in alto a dx in senso orario 0->3): ");
            sceltaAngolo = Integer.parseInt(in.readLine());
            cornerFlag = checkCornerLegitness(cartaNelTavolo, sceltaAngolo, out);
        }

        /*La carta scelta dal giocatore viene piazzata in modo opportuna sul tavolo attaccata alla carta selezionata come base*/
        playingPlayer.placeCard(riga, colonna, cartaNelMazzo, (sceltaFronte == 1), sceltaAngolo);
        /*Display del tavolo per controllare*/
        playingPlayer.printField(out);
    }

    /**
     * Checks whether the slot in the player's field has a card
     * @param playingPlayer
     * @param r
     * @param c
     * @return boolean, true if there is a card in the slot, otherwise false
     */
    public boolean checkSlotHasCard(Player playingPlayer, int r, int c, PrintWriter out) {
        boolean flag = true;
        if (!playingPlayer.getPlayerField().getSlots()[r][c].isBusySlot()) {
            out.println("ERRORE: SCELTO UNO SLOT VUOTO");
            flag = false;
        }
        return flag;
    }

    /**
     * Checks whether the card's corner is available
     * @param card: card to check
     * @param corner: corner to check
     * @return boolean, true if corner is available, otherwise false
     */
    private boolean checkCornerLegitness(NonObjectiveCard card, int corner, PrintWriter out) {
        boolean flag = true;
        if (card.isPlacedFront()) {
            if (!card.getFrontCorners().get(corner).isAvailableCorner()) {
                flag = false;
                out.println("ERRORE: ANGOLO NON DISPONIBILE");
            }
        } else {
            if (!card.getBackCorners().get(corner).isAvailableCorner()) {
                flag = false;
                out.println("ERRORE: ANGOLO NON DISPONIBILE");
            }
        }
        return flag;

    }

    /**
     * Allows the player to draw a card from either the resource deck or gold deck
     * @param playingPlayer: player that is playing at the moment
     */
    private void drawCard(Player playingPlayer, BufferedReader in, PrintWriter out) throws IOException {
        //TODO: finire la funzione della pesca
        int sceltaMazzo = -1;
        boolean flagMazzo = false;

        while (flagMazzo == false) {
            out.println("Pesca una carta dai mazzi:");
            out.println("1) Mazzo Resource");
            out.println("2) Mazzo Oro");
            sceltaMazzo = Integer.parseInt(in.readLine());
            flagMazzo = true;

            if (sceltaMazzo == 1 && DrawingDeck.getTotalResourceCard().isEmpty()) {
                out.println("ERRORE: MAZZO RISORSA VUOTO");
                flagMazzo = false;
            } else if (sceltaMazzo == 2 && DrawingDeck.getTotalGoldCard().isEmpty()) {
                out.println("ERRORE: MAZZO ORO VUOTO");
                flagMazzo = false;
            }

        }

        if (sceltaMazzo == 1) {
            /*Il giocatore aggiunge la carta in cima al mazzo risorsa al suo mazzo*/
            playingPlayer.getPlayerDeck().getPlayerCards().add(
                    DrawingDeck.drawCard(true)
            );
        } else if (sceltaMazzo == 2) {
            /*Il giocatore aggiunge la carta in cima al mazzo risorsa al suo mazzo*/
            playingPlayer.getPlayerDeck().getPlayerCards().add(
                    DrawingDeck.drawCard(false)
            );
        }
    }

    /**
     * Allows the playing player to analise its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void fieldAnalysis(Player playingPlayer, BufferedReader in, PrintWriter out) throws IOException {
        int riga, colonna;

        playingPlayer.printField(out);
        out.println("Scegli la riga della carta che vuoi analizzare: ");
        riga = Integer.parseInt(in.readLine());
        out.println("Scegli la colonna della carta che vuoi analizzare: ");
        colonna = Integer.parseInt(in.readLine());
        playingPlayer.getPlayerField().cardAnalysis(riga, colonna, out);
    }

    /**
     * Selects the index of the next player to play
     * @param currentIndex: defines the current index of the current player
     * @return int, defines the index of the next player in line
     */
    private int selectIndexNextPlayer(int currentIndex) {
        int indiceProssimo = currentIndex + 1;
        if (indiceProssimo >= players.size()) {
            indiceProssimo = 0;
        }
        return indiceProssimo;
    }

    /**
     * Given an index, selects the next player that will be playing after the current player
     * @param index: index of the next player that will play, calculated from another private method
     * @return Player, next player in line
     */
    private Player selectNextPlayer(int index) throws IOException {
        Player nextPlayer;
        nextPlayer = players.get(index);
        System.out.println("Prossimo turno... \n Tocca a " + nextPlayer.getPlayerName());
        in = new BufferedReader(new InputStreamReader(nextPlayer.getPlayerSocket().getInputStream()));
        out = new PrintWriter(nextPlayer.getPlayerSocket().getOutputStream(), true);
        if (nextPlayer.isFirstTurn()) {
            placeStarterCard(nextPlayer, in, out);
            nextPlayer.setFirstTurn(false);
        }
        return nextPlayer;
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
    private boolean checkWinner(Player playingPlayer) {
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


