package com.example.codexnaturalis;

import java.util.*;

public class Match {
    private ArrayList<Player> players;
    private ScoreTracker scoreTracker;
    private boolean lastRound = false;
    private ArrayList<ObjectiveCard> commonObjectives;
    private ArrayList<ResourceGoldCard> publicCards;
    private int indexPlayingPlayer;

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

    public void setCommonObjectives(ArrayList<ObjectiveCard> commonObjectives) {
        this.commonObjectives = commonObjectives;
    }

    /**
     * Main function that starts the match. Also, only public method
     */
//    public void startMatch() throws Exception {
//        int sceltaGiocatore;
//        boolean playerHaPiazzatoLaCarta = false;
//        /*Si inizia scegliendo in modo casuale il giocatore iniziale*/
//        Player playingPlayer = chooseRandomFirstPlayer();
//        Socket playingPlayerSocket = ZakServer.hashPlayer.get(playingPlayer);
//        //todo: qui facciamo partire la scelta della carta obiettivo segreto
//        //todo: secretObjectiveChoice();
//        in = new BufferedReader(new InputStreamReader(playingPlayerSocket.getInputStream()));
//        out = new PrintWriter(playingPlayerSocket.getOutputStream(), true);
//
//        /*Creo il choice container per le scelte del giocatore*/
//        choices = new ChoiceManager(in, out);
//
//        /*Il giocatore piazza la sua carta iniziale*/
//        placeStarterCard(playingPlayer);
//
//        /*Il giocatore sceglie cosa fare*/
//        sceltaGiocatore = choices.chooseFromMenu(false);
//
//        while (!lastRound) {
//
//            while (sceltaGiocatore != -1) {
//
//                /*Il giocatore ha scelto di piazzare la sua carta*/
//                if (sceltaGiocatore == 1 && !playerHaPiazzatoLaCarta) {
//
//                    /*Player sta piazzando la carta, questo boolean impedisce che il giocatore
//                    * faccia di nuovo questa azione nello stesso turno*/
//                    playerHaPiazzatoLaCarta = true;
//
//                    /*Piazza una carta dal mazzo*/
//                    placeCard(playingPlayer);
//
//                    //TODO: aumentare i punti nello scoreTracker
//
//                    /*Il giocatore ora deve pescare una carta dai due mazzi risorsa od oro*/
//                    drawCard(playingPlayer);
//                } else if (sceltaGiocatore == 2) {
//                    /*Il giocatore sceglie una carta dando come input la sua riga e colonna*/
//                    fieldAnalysis(playingPlayer);
//                }
//
//                /*Il giocatore sceglie cosa fare*/
//                sceltaGiocatore = choices.chooseFromMenu(playerHaPiazzatoLaCarta);
//            }
//
//            if (checkWinner(playingPlayer)) {
//                lastRound = true;
//            } else {
//                /*Playing player ora passa al prossimo giocatore*/
//                playingPlayer = selectNextPlayer(playingPlayer);
//                playerHaPiazzatoLaCarta = false;
//
//                /*Il giocatore sceglie cosa fare*/
//                sceltaGiocatore = choices.chooseFromMenu(playerHaPiazzatoLaCarta);
//
//            }
//
//        }
//        //TODO
//
//        lastRoundRoutine();
//
//    }

    /**
     * Chooses who will be the first player and updates its firstPlayer attribute
     * @return Player, first player of the match
     */
    private MatchMessage chooseRandomFirstPlayer() {
        Collections.shuffle(players);

        Player playingPlayer = players.getFirst();
        indexPlayingPlayer = 0;
        playingPlayer.setFirstPlayer(true);
        playingPlayer.setFirstTurn(false);
        System.out.println(playingPlayer.getPlayerName() + " è il primo a giocare!");
        publicCards.add(DrawingDeck.drawCard(true));
        publicCards.add(DrawingDeck.drawCard(true));
        publicCards.add(DrawingDeck.drawCard(false));
        publicCards.add(DrawingDeck.drawCard(false));

        return new MatchMessage(publicCards, playingPlayer.getPlayerID(), null, null, null);
    }

    private MatchMessage genericTurn(GenericTurnMessage msg) throws Exception {
        Player playingPlayer = getPlayerFromId(msg.getClientID());

        //PLACE CARD ON FIELD
        int row = msg.getCoordinates().getKey();
        int column = msg.getCoordinates().getValue();
        playingPlayer.placeCardAndRemoveFromDeck(row, column, msg.getCardOnHand());

        //ADD THE DRAWN CARD TO THE PLAYER'S DECK AND REMOVE IT FROM WHERE IT WAS DRAWN
        boolean isResourceCard = msg.getDrawnCard() instanceof ResourceCard;
        if (publicCards.contains(msg.getDrawnCard())) {
            publicCards.remove(msg.getDrawnCard());
            ResourceGoldCard replacementCard = DrawingDeck.drawCard(isResourceCard);
            publicCards.add(replacementCard);
            playingPlayer.getPlayerDeck().getResourceGoldCards().add(msg.getDrawnCard());
        } else {
            playingPlayer.getPlayerDeck().getResourceGoldCards().add(DrawingDeck.drawCard(isResourceCard));
        }

        //SELECT INDEX OF NEXT PLAYER
        UUID currentPlayerId = playingPlayer.getPlayerID();
        int nextPlayerIndex = selectIndexNextPlayer(indexPlayingPlayer);
        Player nextPlayer = players.get(nextPlayerIndex);
        UUID nextPlayerId = nextPlayer.getPlayerID();
        /** Chiamare checkWinner. Se il flag è true arrivare all'ultimo giocatore e terminare il match.
         * Infine chiamare lastRoundRoutine*/
        return new MatchMessage(publicCards, currentPlayerId, nextPlayerId, msg.getCardOnHand(), msg.getCoordinates());
    }

    private Player getPlayerFromId(UUID playerId) {
        Player player = null;
        for (Player p : players) {
            if (p.getPlayerID().equals(playerId)) {
                player = p;
                break;
            }
        }
        return player;
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
    public void checkPoints(ResourceGoldCard playedCard, Player currentPlayer) throws Exception {
        int id = playedCard.getIdCard();
        int pts = playedCard.getPoints();
        int coveredCorners;

         /** se il mazzo è risorsa: controllare numero punti carta ed eventualmente assegnarli*/
         if(0<id && id<41 && pts){
             currentPlayer.addScore(pts);
        }
         /** se il mazzo è oro: controllo numero di punti carta e id per criterio:*/
         else if(40<id && id<81){
            switch (id) {
                /** se idCard==41||51||63||71 Feather*/
                case 41,51,63,71:
                    currentPlayer.addScore(currentPlayer.getElementsMana()[2]*pts);
                /** se idCard==42||53||61||73  Ink*/
                case 42,53,61,73:
                    currentPlayer.addScore(currentPlayer.getElementsMana()[0]*pts);
                /** se idCard==43||52||62||72 Papyrus*/
                case 43,52,62,72:
                    currentPlayer.addScore(currentPlayer.getElementsMana()[1]*pts);
                /** se idCard==44||45||46||54||55||56||64||65||66||74||75||76 conta angoli coperti*/
                case 44,45,46,54,55,56,64,65,66,74,75,76:
                    currentPlayer.addScore(coveredCorners*pts);
                /** altrimenti assegna punti*/
                default:
                    currentPlayer.addScore(pts);
            }
         }
    }
    /**
     * Last Round Routine
     */
    private void lastRoundRoutine() {
        checkTotalPoints();
        declareWinner();
        //Dichiara il vincitore
    }
    /** Somma il punteggio ottenuto dalle care risorsa e oro a quello ottenuto dalle carte obiettivo*/
    private void checkTotalPoints() {
        int p=players.size();
        int i;
        int s;
            for(i=0;i<p;i++) {
               s = players[i].getScore+checkExtraPoints();
            }

    }
    private int checkExtraPoints(Player p) {
        int extraPoints=0;
        /** Punti obiettivi personali
         * Punti obiettivi comuni
         * extraPoints=punti obiettivo personale + punti obiettivi comuni
         */



        return extraPoints;
    }

    private Player declareWinner() {

        Player player = null;

        return player;
    }
}


