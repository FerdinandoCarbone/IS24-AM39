package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class NewMatch {

    private ArrayList<Player> players;
    private ScoreTracker scoreTracker;
    /**
     * Constructor of Match
     * @param players: ArrayList of all players in the match
     * @param scoreTracker: score tracker
     */
    public NewMatch(ArrayList<Player> players, ScoreTracker scoreTracker) {
        this.players = players;
        this.scoreTracker = scoreTracker;
    }

    public NewMatch(ScoreTracker scoreTracker) {
        this.players = new ArrayList<>();
        this.scoreTracker = scoreTracker;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * Main function that starts the match. Also, only public method
     */
    public void startMatch() {
        Scanner scanner = new Scanner(System.in);
        int sceltaGiocatore;
        int flagCartaGiocata = 0;

        /*Si inizia scegliendo in modo casuale il giocatore iniziale*/
        int indiceGiocatoreInGioco = randomIndex();
        Player playingPlayer = chooseFirstPlayer(indiceGiocatoreInGioco);
        /*Inizia la partita con la carta iniziale piazzata dal primo giocatore*/
        placeStarterCard(playingPlayer);
        sceltaGiocatore = chooseFromMenu();

        while (true) {
            while (sceltaGiocatore != -1) {

                if (sceltaGiocatore == 1) {

                    /*Cambio il flag della carta giocata per evitare che il giocatore ne piazzi un'altra nello stesso turno di gioco*/
                    flagCartaGiocata = 1;
                    /*Piazza una carta dal mazzo*/
                    placeCard(playingPlayer);
                    /*Il giocatore ora deve pescare una carta dai due mazzi risorsa od oro*/
                    drawCard(playingPlayer);
                } else if (sceltaGiocatore == 2) {
                    /*Il giocatore sceglie una carta dando come input la sua riga e colonna*/
                    fieldAnalysis(playingPlayer);
                }

                System.out.println("Cosa vuoi fare ?");
                if (flagCartaGiocata == 0) {
                    System.out.println("1) Piazza una Carta");
                    System.out.println("2) Analizza il tavolo");
                } else {
                    System.out.println("-1) Finisci il turno");
                    System.out.println("2) Analizza il tavolo");
                }
                sceltaGiocatore = scanner.nextInt();

            }

            indiceGiocatoreInGioco = selectIndexNextPlayer(indiceGiocatoreInGioco);
            playingPlayer = selectNextPlayer(indiceGiocatoreInGioco);
            sceltaGiocatore = chooseFromMenu();

        }

    }

    /**
     * Casually chooses the index of the first player of the match
     * @return int, index of the first player
     */
    private int randomIndex() {
        Random random = new Random();
        int giocatoreIniziale = random.nextInt(players.size());
        return giocatoreIniziale;
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
    private void placeStarterCard(Player playingPlayer) {
        Scanner scanner = new Scanner(System.in);
        int sceltaFronte;

        System.out.println("Place starter Card...");
        playingPlayer.printStarterCard();
        System.out.println("Seleziona se vuoi piazzare la carta iniziale di fronte o retro: 1) -> Fronte | 0) -> Retro");
        sceltaFronte = scanner.nextInt();
        playingPlayer.placeStarterCard(sceltaFronte == 1);
        playingPlayer.printField();
    }
    /**
     * Prints a menu giving choices to the player that playing
     * @return int, player's choice
     */
    private int chooseFromMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cosa vuoi fare ?");
        System.out.println("1) Piazza una Carta");
        System.out.println("2) Analizza il tavolo");
        return scanner.nextInt();
    }

    /**
     * Lets the player select a card from its deck to place on its field
     * @param playingPlayer: player that is playing at the moment
     * @return int, defines the number of the card in the player's deck
     */
    private int selectCard(Player playingPlayer) {
        Scanner scanner = new Scanner(System.in);
        int sceltaCarta;

        playingPlayer.printDeck();
        System.out.println("Seleziona il numero della carta che vuoi piazzare: ");
        sceltaCarta = scanner.nextInt();

        return sceltaCarta;
    }

    /**
     * By using auxiliary private methods, allows the player the place a card on its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void placeCard(Player playingPlayer) {
        Scanner scanner = new Scanner(System.in);
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
        sceltaCarta = selectCard(playingPlayer);
        cartaNelMazzo = playingPlayer.getPlayerDeck().getPlayerCards().get(sceltaCarta - 1);
        System.out.println("Seleziona se vuoi piazzarla di fronte o retro: 1) -> Fronte | 0) -> Retro");
        sceltaFronte = scanner.nextInt();

        /*Checks se esiste una carta in questo slot*/
        while (!cardFlag) {
            playingPlayer.printField();
            System.out.println("Seleziona la riga della carta a cui vuoi attaccarti:");
            riga = scanner.nextInt();
            System.out.println("Seleziona la colonna della carta a cui vuoi attaccarti:");
            colonna = scanner.nextInt();

            cardFlag = checkSlotHasCard(playingPlayer, riga, colonna);
        }

        cartaNelTavolo = playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot();

        /*Il giocatore vede gli angoli della carta che vuole piazzare*/
        System.out.println("Carta da piazzare: ");
        if (sceltaFronte == 1) {
            cartaNelMazzo.printFrontCorners();
        } else {
            cartaNelMazzo.printBackCorners();
        }
        /*Il giocatore vede la carta sul tavolo che ha scelto come base*/
        System.out.println("Carta selezionata sul tavolo:");
        if (cartaNelTavolo.isPlacedFront()) {
            cartaNelTavolo.printFrontCorners();
        } else {
            cartaNelTavolo.printBackCorners();
        }

        //Check della disponibilità dell'angolo
        while (!cornerFlag) {
            System.out.println("Seleziona l'angolo della carta sul tavolo a cui vuoi attaccarti (a partire da in alto a dx in senso orario 0->3): ");
            sceltaAngolo = scanner.nextInt();
            cornerFlag = checkCornerLegitness(cartaNelTavolo, sceltaAngolo);
        }

        /*La carta scelta dal giocatore viene piazzata in modo opportuna sul tavolo attaccata alla carta selezionata come base*/
        playingPlayer.placeCard(riga, colonna, cartaNelMazzo, (sceltaFronte == 1), sceltaAngolo);
        /*Display del tavolo per controllare*/
        playingPlayer.printField();
    }

    /**
     * Checks whether the slot in the player's field has a card
     * @param playingPlayer
     * @param r
     * @param c
     * @return boolean, true if there is a card in the slot, otherwise false
     */
    public boolean checkSlotHasCard(Player playingPlayer, int r, int c) {
        boolean flag = true;
        if (!playingPlayer.getPlayerField().getSlots()[r][c].isBusySlot()) {
            System.out.println("ERRORE: SCELTO UNO SLOT VUOTO");
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
    private boolean checkCornerLegitness(NonObjectiveCard card, int corner) {
        boolean flag = true;
        if (card.isPlacedFront()) {
            if (!card.getFrontCorners().get(corner).isAvailableCorner()) {
                flag = false;
                System.out.println("ERRORE: ANGOLO NON DISPONIBILE");
            }
        } else {
            if (!card.getBackCorners().get(corner).isAvailableCorner()) {
                flag = false;
                System.out.println("ERRORE: ANGOLO NON DISPONIBILE");
            }
        }
        return flag;

    }

    /**
     * Allows the player to draw a card from either the resource deck or gold deck
     * @param playingPlayer: player that is playing at the moment
     */
    private void drawCard(Player playingPlayer) {
        //TODO: finire la funzione della pesca
        int sceltaMazzo = -1;
        boolean flagMazzo = false;
        Scanner scanner = new Scanner(System.in);

        while (flagMazzo == false) {
            System.out.println("Pesca una carta dai mazzi:");
            System.out.println("1) Mazzo Resource");
            System.out.println("2) Mazzo Oro");
            sceltaMazzo = scanner.nextInt();
            flagMazzo = true;

            if (sceltaMazzo == 1 && DrawingDeck.getTotalResourceCard().isEmpty()) {
                System.out.println("ERRORE: MAZZO RISORSA VUOTO");
                flagMazzo = false;
            } else if (sceltaMazzo == 2 && DrawingDeck.getTotalGoldCard().isEmpty()) {
                System.out.println("ERRORE: MAZZO ORO VUOTO");
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
    private void fieldAnalysis(Player playingPlayer) {
        int riga, colonna;
        Scanner scanner = new Scanner(System.in);

        playingPlayer.printField();
        System.out.println("Scegli la riga della carta che vuoi analizzare: ");
        riga = scanner.nextInt();
        System.out.println("Scegli la colonna della carta che vuoi analizzare: ");
        colonna = scanner.nextInt();
        playingPlayer.getPlayerField().cardAnalysis(riga, colonna);
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
    private Player selectNextPlayer(int index) {
        Player nextPlayer;
        nextPlayer = players.get(index);
        System.out.println("Prossimo turno... \n Tocca a " + nextPlayer.getPlayerName());
        return nextPlayer;
    }

}
