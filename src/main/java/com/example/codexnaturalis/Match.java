package com.example.codexnaturalis;

import java.util.*;

public class Match {
    private ArrayList<Player> players;
    private ArrayList<Player> winners;
    private ArrayList<Integer> objectivePoints;
    private ScoreTracker scoreTracker;
    private boolean lastRound = false;
    private boolean isLastCycle = false;
    private ArrayList<ObjectiveCard> commonObjectives;
    private ArrayList<ResourceGoldCard> publicCards;
    private int indexCurrentPlayer;


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
     * Chooses who will be the first player and updates its firstPlayer attribute
     * @return Player, first player of the match
     */
    private StandardMatchMessage chooseRandomFirstPlayer() {
        Collections.shuffle(players);

        Player playingPlayer = players.getFirst();
        indexCurrentPlayer = 0;
        playingPlayer.setFirstPlayer(true);
        playingPlayer.setFirstTurn(false);
        System.out.println(playingPlayer.getPlayerName() + " è il primo a giocare!");
        publicCards.add(DrawingDeck.drawCard(true));
        publicCards.add(DrawingDeck.drawCard(true));
        publicCards.add(DrawingDeck.drawCard(false));
        publicCards.add(DrawingDeck.drawCard(false));

        return new StandardMatchMessage(publicCards, playingPlayer.getPlayerID(), null, null, null);
    }

    private StandardMatchMessage genericTurn(GenericTurnMessage msg) throws Exception {
        Player playingPlayer = getPlayerFromId(msg.getClientID());

        //PLACE CARD ON FIELD
        int row = msg.getCoordinates().getKey();
        int column = msg.getCoordinates().getValue();
        playingPlayer.placeCardAndRemoveFromDeck(row, column, msg.getCardOnHand());

        //These 2 Ifs check if we are at the end of the cycle and if the playing player is the last on the cycle
        if (!isLastCycle) {
            if (playerIsWinner(msg.getCardOnHand(), playingPlayer)) {
                isLastCycle = true;
            }
        }
        if (isLastCycle && indexCurrentPlayer == players.size() - 1) {
            return new EndMatchMessage(null, msg.getClientID(), null, msg.getCardOnHand(), msg.getCoordinates());
        }

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
        int nextPlayerIndex = selectIndexNextPlayer(indexCurrentPlayer);
        Player nextPlayer = players.get(nextPlayerIndex);
        UUID nextPlayerId = nextPlayer.getPlayerID();
        indexCurrentPlayer = nextPlayerIndex;
        /** Chiamare checkWinner. Se il flag è true arrivare all'ultimo giocatore e terminare il match.
         * Infine chiamare lastRoundRoutine*/
        return new StandardMatchMessage(publicCards, currentPlayerId, nextPlayerId, msg.getCardOnHand(), msg.getCoordinates());

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

    public StandardMatchMessage removeDisconnectedPlayer(UUID disconnectedPlayerId) {
        Player playerToRemove = getPlayerFromId(disconnectedPlayerId);
        Player currentPlayer = players.get(indexCurrentPlayer);
        //Se il giocatore si disconnette prima di giocare il suo turno
        if (playerToRemove.equals(currentPlayer)) {
            Player nextPlayer = players.get(selectIndexNextPlayer(indexCurrentPlayer));
            UUID nextPlayerId = nextPlayer.getPlayerID();
            indexCurrentPlayer = players.indexOf(nextPlayer);
            players.remove(playerToRemove);
            return new CurrentPlayerDisconnectedMessage(publicCards, disconnectedPlayerId, nextPlayerId);
        } else {
            players.remove(playerToRemove);
            return new notCurrentPlayerDisconnectedMessage(publicCards, disconnectedPlayerId);
        }

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
    public boolean playerIsWinner(ResourceGoldCard playedCard , Player playingPlayer) {
        boolean winnerFlag = false;
        if (checkPoints(playedCard, playingPlayer) >= 20) {
            winnerFlag = true;
            System.out.println(playingPlayer.getPlayerName() + " ha raggiunto " + playingPlayer.getScore() + " punti!");
        }
        return  winnerFlag;
    }
    public int checkPoints(ResourceGoldCard playedCard, Player currentPlayer) {
        int id = playedCard.getIdCard();
        int pts = playedCard.getPoints();

         /** se il mazzo è risorsa: controllare numero punti carta ed eventualmente assegnarli*/
         if(0<id && id<41){
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
                    currentPlayer.addScore(playedCard.getCoveredCornersWhenPlaced()*pts);
                /** altrimenti assegna punti*/
                default:
                    currentPlayer.addScore(pts);
            }
         } return currentPlayer.getScore();
    }
    /**
     * Last Round Routine
     */
    private void lastRoundRoutine() {
        checkTotalPoints();
        declareWinnerOrDraw();
    }

    /** Somma il punteggio ottenuto dalle care risorsa e oro a quello ottenuto dalle carte obiettivo*/
    private void checkTotalPoints() {
        int obj = 0;
            for(Player p: players) {
               obj = checkExtraPoints(p);
               p.addScore(obj);
            }
    }

    /** calculate objective points and add into the array */
    private int calculateObjPoints(Player p, int id){
        int points=0;
        if(87<id && id<102){
            switch (id) {       //TO DO: modificare addScore con nuovo metodo ?!?!?
                /** se idCard is ripetizioni semi*/
                case 95,96,97,98:
                    //95 3funghi, 96 3foglie, 97 3lupo, 98 3farfalle. tutto x2 punti
                    switch (id){
                        case 95: p.addScore(p.getResourceMana()[2]*2);  //TO DO: da cambiare tutti gli indici e applicare l'algoritmo di trio
                        case 96: p.addScore(p.getResourceMana()[2]*2);
                        case 97: p.addScore(p.getResourceMana()[2]*2);
                        case 98: p.addScore(p.getResourceMana()[2]*2);
                    }
                    /** se idCard is ripetizioni elementi*/
                case 99,100,101,102:
                    switch (id){
                        //piuma, ink, pergamena
                        case 99:  p.addScore(p.getElementsMana()[2]*2); //TO DO: da cambiare tutti gli indici e applicare l'algoritmo divisione
                            // 2 pergamene
                        case 100: p.addScore(p.getElementsMana()[2]*2);
                            // 2 ink
                        case 101: p.addScore(p.getElementsMana()[2]*2);
                            // 2 piume
                        case 102: p.addScore(p.getElementsMana()[2]*2);
                    }

                    /** se idCard is disposizioni grafiche*/
                case 87,88,89,90,91,92,93,94:
                    //TO DO: pensare alle disposizioni
            }
        }
        return points;
    }


    /**
     * calculate objective points and add into the array
     */
    private int checkExtraPoints(Player p) {
        int extraPoints=0;
        int extraCommonPoints=0;
        int extraPersonalPoints=0;
        int id=0;

        /** Punti obiettivi personali */
          id = p.getPlayerDeck().getSecretObjectiveCard().getIdCard();
          extraPersonalPoints = calculateObjPoints(p,id);
          //ferdinando ha detto di rivedere getSecretObjectiveCard perchè da sempre null    !!!!!

        /** Punti obiettivi comuni */  // commonObjectives è il vettore che contiene le 2 carte obiettivo comuni
        for(ObjectiveCard oc : commonObjectives){
            id = oc.getIdCard();
            extraCommonPoints = extraCommonPoints + calculateObjPoints(p,id);
        }

        /** extraPoints=punti obiettivo personale + punti obiettivi comuni */
        extraPoints = extraCommonPoints + extraPersonalPoints;

        /** TO DO: aggiornare l'array: objectivePoints */
        objectivePoints.add(extraPoints);

        return extraPoints;
    }

    private void declareWinnerOrDraw() {

        int totalScore=0;
        boolean draw =false;
        Player playerWin = null;

        /** find max points */
        for(Player p : players){
            if(p.getScore()>totalScore){
                totalScore = p.getScore();
                playerWin = p;
            }
        }
        winners.add(playerWin);

        /** check if a draw exists */
        int i=0;
        for(Player p2 : players){
            if(p2.getScore()==totalScore && p2!=playerWin){
                draw = true;
                winners.add(p2);
                i++;
            }
        }
        if(draw){
            drawWinners();
        }
        else
            declareWinners();
    }

    private void drawWinners() {
        //confronta i punti obiettivo dei giocatori in winners[]
        //salva winners per lavorarci e modifica i o il nome del vincitore
    }
    private void declareWinners() {
        //stampa vincitori con size
        int s = winners.size();
    }

}


