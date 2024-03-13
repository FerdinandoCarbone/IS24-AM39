package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Match {
    private ArrayList<Player> players;
    private ResourceDeck resourceDeck;
    private ObjectiveDeck objectiveDeck;
    private GoldDeck goldDeck;
    private StarterDeck starterDeck;
    private ScoreTracker scoreTracker;

    public Match(ArrayList<Player> players, ResourceDeck resourceDeck, ObjectiveDeck objectiveDeck, GoldDeck goldDeck, StarterDeck starterDeck, ScoreTracker scoreTracker) {
        this.players = players;
        this.resourceDeck = resourceDeck;
        this.objectiveDeck = objectiveDeck;
        this.goldDeck = goldDeck;
        this.starterDeck = starterDeck;
        this.scoreTracker = scoreTracker;
    }

    /**
     * Sceglie casualmente l'indice (0-index) del giocatore che inizia la partita e che quindi avrà il "Token Nero"
     * @return
     */
    private int randomIndex() {
        Random random = new Random();
        int giocatoreIniziale = random.nextInt(players.size());
        return giocatoreIniziale;
    }

    private Player chooseFirstPlayer(int playingPlayerIndex) {
        Player playingPlayer = players.get(playingPlayerIndex);
        playingPlayer.setFirstPlayer(true);
        System.out.println(playingPlayer.getPlayerName() + " is the first to play!");
        return playingPlayer;
    }

    private void placeStarterCard(Player playingPlayer) {
        Scanner scanner = new Scanner(System.in);
        int sceltaFronte;

        System.out.println("Place starter Card...");
        playingPlayer.printStarterCard();
        System.out.println("Seleziona se vuoi piazzare la carta iniziale di fronte o retro: 1) -> Fronte | 0) -> Retro");
        sceltaFronte = scanner.nextInt();
        playingPlayer.placeStarterCard(sceltaFronte);
        playingPlayer.printField();
    }

    private int selectCard(Player playingPlayer) {
        Scanner scanner = new Scanner(System.in);
        int sceltaCarta;

        playingPlayer.printDeck();
        System.out.println("Seleziona il numero della carta che vuoi piazzare: ");
        sceltaCarta = scanner.nextInt();

        return sceltaCarta;
    }

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
        if (playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot().getPiazzataInFronte() == 1) {
            playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot().printFrontCorners();
        } else {
            playingPlayer.getPlayerField().getSlots()[riga][colonna].getCardSlot().printBackCorners();
        }

        //TODO: METTERE IL CONTROLLO PER EVITARE CHE IL GIOCATORE SCELGA UN ANGOLO NON DISPONIBILE
        System.out.println("Seleziona l'angolo della carta sul tavolo a cui vuoi attaccarti (a partire da in alto a dx in senso orario 0->3): ");
        sceltaAngolo = scanner.nextInt();
        /*La carta scelta dal giocatore viene piazzata in modo opportuna sul tavolo attaccata alla carta selezionata come base*/
        playingPlayer.placeCard(riga, colonna, playingPlayer.getPlayerDeck().getCards().get(cartaSelezionata-1), sceltaFronte, sceltaAngolo);
        /*Display del tavolo per controllare*/
        playingPlayer.printField();
    }

    private int chooseFromMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cosa vuoi fare ?");
        System.out.println("1) Piazza una Carta");
        System.out.println("2) Analizza il tavolo");
        return scanner.nextInt();
    }

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

    private void chooseSecretCard(Player playingPlayer, ObjectiveCard carta1, ObjectiveCard carta2) {
        Scanner scanner = new Scanner(System.in);
        int sceltaGiocatore;
        System.out.println("Scegli una Carta Obiettivo Segreto:");
        System.out.println("1) " + carta1.getClass());
        System.out.println("2) " + carta2.getClass());

        sceltaGiocatore = scanner.nextInt();
        playingPlayer.getPlayerDeck().setCartaObiettivoSegreto( (sceltaGiocatore == 1? carta1 : carta2) );

    }

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

    private int selectIndexNextPlayer(int indiceCorrente) {
        int indiceProssimo = indiceCorrente + 1;
        if (indiceProssimo >= players.size()) {
            indiceProssimo = 0;
        }
        return indiceProssimo;
    }

    private Player selectNextPlayer(int indice) {
        Player nextPlayer;
        nextPlayer = players.get(indice);
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
