package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Match {
    private ArrayList<Player> players;
    private Decks.ResourceDeck resourceDeck;
    private Decks.ObjectiveDeck objectiveDeck;
    private Decks.GoldDeck goldDeck;
    private Decks.StarterDeck starterDeck;
    private ScoreTracker scoreTracker;

    /**
     * Constructor of Match
     * @param players: ArrayList of all players in the match
     * @param resourceDeck: resource Deck
     * @param objectiveDeck: Objective deck
     * @param goldDeck: gold deck
     * @param starterDeck: starter deck
     * @param scoreTracker: score tracker
     */
    public Match(ArrayList<Player> players, Decks.ResourceDeck resourceDeck, Decks.ObjectiveDeck objectiveDeck, Decks.GoldDeck goldDeck, Decks.StarterDeck starterDeck, ScoreTracker scoreTracker) {
        this.players = players;
        this.resourceDeck = resourceDeck;
        this.objectiveDeck = objectiveDeck;
        this.goldDeck = goldDeck;
        this.starterDeck = starterDeck;
        this.scoreTracker = scoreTracker;
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
        int cartaSelezionata;
        int sceltaFronte;
        int riga, colonna;
        int sceltaAngolo;


        /*Il giocatore sceglie quale carta piazzare e come piazzarla*/
        cartaSelezionata = selectCard(playingPlayer);

        System.out.println("Seleziona se vuoi piazzarla di fronte o retro: 1) -> Fronte | 0) -> Retro");
        sceltaFronte = scanner.nextInt();
        System.out.println("Seleziona la riga della carta a cui vuoi attaccarti:");
        playingPlayer.printField();
        riga = scanner.nextInt();
        System.out.println("Seleziona la colonna della carta a cui vuoi attaccarti:");
        colonna = scanner.nextInt();

        /*Il giocatore vede gli angoli della carta che vuole piazzare*/
        System.out.println("Carta da piazzare: ");
        if (sceltaFronte == 1) {
            playingPlayer.getPlayerDeck().getCards().get(cartaSelezionata-1).printFrontCorners();
        } else {
            playingPlayer.getPlayerDeck().getCards().get(cartaSelezionata-1).printBackCorners();
        }
        /*Il giocatore vede la carta sul tavolo che ha scelto come base*/
        System.out.println("Carta selezionata sul tavolo:");
        if (playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot().getPiazzataInFronte()) {
            playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot().printFrontCorners();
        } else {
            playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot().printBackCorners();
        }

        //TODO: METTERE IL CONTROLLO PER EVITARE CHE IL GIOCATORE SCELGA UN ANGOLO NON DISPONIBILE
        System.out.println("Seleziona l'angolo della carta sul tavolo a cui vuoi attaccarti (a partire da in alto a dx in senso orario 0->3): ");
        sceltaAngolo = scanner.nextInt();
        /*La carta scelta dal giocatore viene piazzata in modo opportuna sul tavolo attaccata alla carta selezionata come base*/
        playingPlayer.placeCard(riga, colonna, playingPlayer.getPlayerDeck().getCards().get(cartaSelezionata-1), (sceltaFronte == 1), sceltaAngolo);
        /*Display del tavolo per controllare*/
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
     * Allows the player to draw a card from either the resource deck or gold deck
     * TODO: NOT COMPLETE
     * @param playingPlayer: player that is playing at the moment
     */
    private void drawCard(Player playingPlayer) {
        //TODO: finire la funzione della pesca
        int sceltaMazzo;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Pesca una carta dai mazzi:");
        System.out.println("1) Mazzo Resource");
        System.out.println("2) Mazzo Oro");
        sceltaMazzo = scanner.nextInt();

        //TODO: controllare che i mazzi abbiano ancora carte da pescare
        if (sceltaMazzo == 1) {
            /*Il giocatore aggiunge la carta in cima al mazzo risorsa al suo mazzo*/
            playingPlayer.getPlayerDeck().getCards().add(
                    resourceDeck.getResourceCards().getFirst()
            );
            /*Tolgo dal mazzo risorsa la prima carta pescata*/
            resourceDeck.getResourceCards().removeFirst();
        } else if (sceltaMazzo == 2) {
            /*Il giocatore aggiunge la carta in cima al mazzo risorsa al suo mazzo*/
            playingPlayer.getPlayerDeck().getCards().add(
                    goldDeck.getGoldCards().getFirst()
            );
            /*Tolgo dal mazzo risorsa la prima carta pescata*/
            goldDeck.getGoldCards().removeFirst();
        }
    }

    /**
     * Allows the player to choose its secret objective card, by giving it two randomly chosen
     * TODO: NON COMPLETE
     * @param playingPlayer: player that is playing at the moment
     * @param card1: first card to choose from
     * @param card2: second card to choose from
     */
    private void chooseSecretCard(Player playingPlayer, Card.ObjectiveCard card1, Card.ObjectiveCard card2) {
        Scanner scanner = new Scanner(System.in);
        int sceltaGiocatore;
        System.out.println("Scegli una Carta Obiettivo Segreto:");
        System.out.println("1) " + card1.getClass());
        System.out.println("2) " + card2.getClass());

        sceltaGiocatore = scanner.nextInt();
        playingPlayer.getPlayerDeck().setSecretObjectiveCard( (sceltaGiocatore == 1? card1 : card2) );

    }

    /**
     * Allows the playing player to analise its field
     * @param playingPlayer: player that is playing at the moment
     */
    private void FieldAnalysis(Player playingPlayer) {
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

    public void startMatch() {
        /*Elementi necessari per le scelte fatte dai players e per
        * prendere i comandi da riga di comando*/
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
                    FieldAnalysis(playingPlayer);
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


}
