package com.example.codexnaturalis;

import java.util.*;

public class Match {
    private final ArrayList<Player> players;
    private ArrayList<Player> winners;
    private ArrayList<Player> finalWinners;
    protected static HashMap<Player, Integer> hashObjectivePoints = new HashMap<>();
    private final ScoreTracker scoreTracker;
    private boolean isLastCycle = false;
    private ArrayList<ObjectiveCard> commonObjectives;
    private ArrayList<ResourceGoldCard> publicCards = new ArrayList<>();
    private ArrayList<ResourceGoldCard> coveredCards;
    private int indexCurrentPlayer;



    /**
     * Constructor of Match
     * @param players: ArrayList of all players in the match
     * @param scoreTracker: score tracker
     */
    public Match(ArrayList<Player> players, ScoreTracker scoreTracker) {
        this.players = players;
        this.scoreTracker = scoreTracker;
        this.coveredCards = new ArrayList<>();
        coveredCards.add(0, DrawingDeck.drawCard(true));
        coveredCards.add(1, DrawingDeck.drawCard(false));
    }

    /**
     * Chooses who will be the first player and updates its firstPlayer attribute
     * @return Player, first player of the match
     */
    public StandardMatchMessage chooseRandomFirstPlayer() {
        Collections.shuffle(players);

        Player playingPlayer = players.getFirst();
        indexCurrentPlayer = 0;
        System.out.println(Colors.GREEN + playingPlayer.getPlayerName() + " is first to play!" + Colors.RESET);
        System.out.println(Colors.GREEN + "--Getting public cards, please wait--" + Colors.RESET);
        ResourceGoldCard publicCard1 = DrawingDeck.drawCard(true);
        ResourceGoldCard publicCard2 = DrawingDeck.drawCard(true);
        ResourceGoldCard publicCard3 = DrawingDeck.drawCard(false);
        ResourceGoldCard publicCard4 = DrawingDeck.drawCard(false);
        System.out.println(Colors.GREEN + "--Adding public cards--" + Colors.RESET);
        publicCards.add(publicCard1);
        publicCards.add(publicCard2);
        publicCards.add(publicCard3);
        publicCards.add(publicCard4);
        System.out.println(Colors.GREEN + "--Public cards successfully added to the match, here they are:" + Colors.RESET);
        for (ResourceGoldCard card : publicCards) {
            card.printCardFrontAndBack();
        }

        return new StandardMatchMessage(publicCards, playingPlayer.getPlayerID(), playingPlayer.getPlayerName(), null, null, null);
    }

    public StandardMatchMessage genericTurn(GenericTurnMessage msg) {
        Player playingPlayer = getPlayerFromId(msg.getClientID());
        String playerName = playingPlayer.getPlayerName();

        System.out.println(Colors.GREEN + "--" + playerName + " is playing his turn--" + Colors.RESET);

        //PLACE CARD ON FIELD
        int row = msg.getCoordinates().getKey();
        int column = msg.getCoordinates().getValue();
        ResourceGoldCard cardToPlace = msg.getCardOnHand().getFirst();
        System.out.println(Colors.GREEN + "--Placing #" + cardToPlace.getIdCard() + " card on " + playerName + "'s field in [" + row + "][" + column + "]--" + Colors.RESET);
        playingPlayer.placeCardAndRemoveFromDeck(row, column, cardToPlace);
        System.out.println(Colors.GREEN + "--Card placed--" + Colors.RESET);

        System.out.println(Colors.GREEN + "--Check of cycle--" + Colors.RESET);
        //These 2 Ifs check if we are at the end of the cycle and if the playing player is the last on the cycle
        if (!isLastCycle) {
            if (playerIsWinner(msg.getCardOnHand().getFirst(), playingPlayer)) {
                isLastCycle = true;
            }
        }
        if (isLastCycle && indexCurrentPlayer == players.size() - 1) {
            return new EndMatchMessage(null, msg.getClientID(), msg.getSender(), null, msg.getCardOnHand().getFirst(), msg.getCoordinates());
        }

        //ADD THE DRAWN CARD TO THE PLAYER'S DECK AND REMOVE IT FROM WHERE IT WAS DRAWN
        ResourceGoldCard cardDrawn = msg.getDrawnCard().getFirst();
        System.out.println(Colors.GREEN + "--Adding the drawn card #" + cardDrawn.getIdCard() + " to " + playerName + "'s deck--" + Colors.RESET);
        boolean isResourceCard = cardDrawn instanceof ResourceCard;
        if (publicCards.contains(cardDrawn)) {
            publicCards.remove(cardDrawn);
            System.out.println(Colors.GREEN + "--Card #" + cardDrawn.getIdCard() + " removed from public cards--" + Colors.RESET);
            playingPlayer.getPlayerDeck().getResourceGoldCards().add(cardDrawn);
            System.out.println(Colors.GREEN + "--Card #" + cardDrawn.getIdCard() + " added to --" + playingPlayer.getPlayerName() + "'s deck from public cards--" + Colors.RESET);
            ResourceGoldCard replacementCard = coveredCards.get(isResourceCard ? 0 : 1);
            publicCards.add(replacementCard);
            coveredCards.remove(replacementCard);
            System.out.println(Colors.GREEN + "--Card #" + replacementCard.getIdCard() + " added to public cards as replacement from covered cards--" + Colors.RESET);
            coveredCards.add(isResourceCard ? 0 : 1, DrawingDeck.drawCard(isResourceCard));
        } else {
            coveredCards.remove(cardDrawn);
            System.out.println(Colors.GREEN + "--Card #" + cardDrawn.getIdCard() + " removed from covered cards--" + Colors.RESET);
            ResourceGoldCard replacementCard = DrawingDeck.drawCard(isResourceCard);
            coveredCards.add(isResourceCard ? 0 : 1, DrawingDeck.drawCard(isResourceCard));
            System.out.println(Colors.GREEN + "--Card #" + replacementCard.getIdCard() + " added to covered cards--" + Colors.RESET);
        }
        System.out.println(Colors.GREEN + "--Card added--" + Colors.RESET);

        //SELECT INDEX OF NEXT PLAYER
        System.out.println(Colors.GREEN + "--Selecting next player--" + Colors.RESET);
        UUID currentPlayerId = playingPlayer.getPlayerID();
        int nextPlayerIndex = selectIndexNextPlayer(indexCurrentPlayer);
        Player nextPlayer = players.get(nextPlayerIndex);
        System.out.println(Colors.GREEN + "--" + nextPlayer.getPlayerName() + " is the next player--" + Colors.RESET);
        UUID nextPlayerId = nextPlayer.getPlayerID();
        indexCurrentPlayer = nextPlayerIndex;
        System.out.println(Colors.GREEN + "--Next player selected, it's" + playerName + "'s turn--" + Colors.RESET);
        /** Chiamare checkWinner. Se il flag è true arrivare all'ultimo giocatore e terminare il match.
         * Infine chiamare lastRoundRoutine*/
        return new StandardMatchMessage(publicCards, currentPlayerId, playerName, nextPlayerId,  msg.getCardOnHand().getFirst(), msg.getCoordinates());
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

    public HashMap<UUID, ArrayList<ObjectiveCard>> getTwoSecretObjectiveCards() {
        HashMap<UUID, ArrayList<ObjectiveCard>> hash = new HashMap<>();
        for (Player p : players) {
            ArrayList<ObjectiveCard> cards = DrawingDeck.drawTwoObjectiveCards();
            hash.put(p.getPlayerID(), cards);
        }
        return hash;
    }

    public StandardMatchMessage removeDisconnectedPlayer(UUID disconnectedPlayerId) {
        Player playerToRemove = getPlayerFromId(disconnectedPlayerId);
        System.out.println(Colors.GREEN + "--" + playerToRemove.getPlayerName() + " disconnected, removing from players--" + Colors.RESET);
        Player currentPlayer = players.get(indexCurrentPlayer);
        System.out.println(Colors.GREEN + "--" + currentPlayer.getPlayerName() + " is playing--" + Colors.RESET);
        //Se il giocatore si disconnette prima di giocare il suo turno
        if (playerToRemove.equals(currentPlayer)) {
            System.out.println(Colors.GREEN + "--Choosing next player--" + Colors.RESET);
            Player nextPlayer = players.get(selectIndexNextPlayer(indexCurrentPlayer));
            UUID nextPlayerId = nextPlayer.getPlayerID();
            indexCurrentPlayer = players.indexOf(nextPlayer);
            System.out.println(Colors.GREEN + "--" + nextPlayer.getPlayerName() + " is the next player--" + Colors.RESET);
            players.remove(playerToRemove);
            System.out.println(Colors.GREEN + "--" + playerToRemove.getPlayerName() + " removed from players--" + Colors.RESET);
            return new CurrentPlayerDisconnectedMessage(publicCards, disconnectedPlayerId, playerToRemove.getPlayerName(), nextPlayerId);
        } else {
            players.remove(playerToRemove);
            System.out.println(Colors.GREEN + "--" + playerToRemove.getPlayerName() + " removed from players--" + Colors.RESET);
            return new notCurrentPlayerDisconnectedMessage(publicCards,  disconnectedPlayerId, playerToRemove.getPlayerName());
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
        addObjectiveTotalPoints();
        declareWinnerOrDraw();
    }

    /** Somma il punteggio ottenuto dalle care risorsa e oro a quello ottenuto dalle carte obiettivo*/
    private void addObjectiveTotalPoints() {
        int obj = 0;
            for(Player p: players) {
               obj = checkExtraPoints(p);
               p.addScore(obj);
            }
    }

    /** calculate objective points and add into the array */
    private int calculateObjPoints(Player p, int id) {
        int points = 0;
        if (86 < id && id < 103) {
            switch (id) {
                /** se idCard is ripetizioni semi*/
                case 95, 96, 97, 98:
                    //95 3funghi, 96 3foglie, 97 3lupo, 98 3farfalle. tutto x2 punti
                    switch (id) {
                        case 95:
                            p.addScore((p.getResourceMana()[0] / 3) * 2);
                        case 96:
                            p.addScore((p.getResourceMana()[1] / 3) * 2);
                        case 97:
                            p.addScore((p.getResourceMana()[2] / 3) * 2);
                        case 98:
                            p.addScore((p.getResourceMana()[3] / 3) * 2);
                        default:
                            break;
                    }
                    /** se idCard is ripetizioni elementi*/
                case 99, 100, 101, 102:
                    switch (id) {
                        //piuma, ink, pergamena
                        case 99:
                            int i, min = p.getElementsMana()[0];
                            for (i = 1; i < 3; i++) {
                                if (p.getElementsMana()[i] < min) {
                                    min = p.getElementsMana()[i];
                                }
                            }
                            p.addScore(min * 2);
                            // 2 pergamene
                        case 100:
                            p.addScore((p.getElementsMana()[1] / 2) * 2);
                            // 2 ink
                        case 101:
                            p.addScore((p.getElementsMana()[0] / 2) * 2);
                            // 2 piume
                        case 102:
                            p.addScore((p.getElementsMana()[2] / 2) * 2);

                        default:
                            break;
                    }

                    /** se idCard is disposizioni grafiche*/
                case 87, 88, 89, 90, 91, 92, 93, 94:
                    int r = p.getPlayerField().getR(), c = p.getPlayerField().getC();
                    int i, j;
                    Field.Slot[][] s;
                    s = p.getPlayerField().getSlots();
                    ResourceGoldCard card;
                    switch (id) {
                        case 87:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((0< id1 && id1 <11)||(40< id1 && id1 <51)) &&
                                                (i+1<r && 0<j-1) && (s[i+1][j-1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<51)) &&
                                                (i+2<r && 0<j-2) && (s[i+2][j-2].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard()<51))
                                        ) card.arrangements++;
                                        if(((0< id1 && id1 <11)||(40< id1 && id1 <51)) &&
                                                (0<i-1 && j+1<c) && (s[i-1][j+1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<51)) &&
                                                (i+1<r && 0<j-1) && (s[i+1][j-1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<51))
                                        ) card.arrangements++;
                                        if(((0< id1 && id1 <11)||(40< id1 && id1 <51)) &&
                                                (0<i-1 && j+1<c) && (s[i-1][j+1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<51)) &&
                                                (0<i-2 && j+2<c) && (s[i-2][j+2].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard()<51))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                        case 88:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((10< id1 && id1 <21)||(50< id1 && id1 <61)) &&
                                                (i+1<r && j+1<c) && (s[i+1][j+1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<61)) &&
                                                (i+2<r && j+2<c) && (s[i+2][j+2].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard()<61))
                                        ) card.arrangements++;
                                        if(((10< id1 && id1 <21)||(50< id1 && id1 <61)) &&
                                                (0<i-1 && 0<j-1) && (s[i-1][j-1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<61)) &&
                                                (i+1<r && j+1<c) && (s[i+1][j+1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<61))
                                        ) card.arrangements++;
                                        if(((10< id1 && id1 <21)||(50< id1 && id1 <61)) &&
                                                (0<i-1 && 0<j-1) && (s[i-1][j-1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<61)) &&
                                                (0<i-2 && 0<j-2) && (s[i-2][j-2].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard()<61))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                        case 89:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((20< id1 && id1 <31)||(60< id1 && id1 <71)) &&
                                                (i+1<r && 0<j-1) && (s[i+1][j-1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<71)) &&
                                                (i+2<r && 0<j-2) && (s[i+2][j-2].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j-2].getCardSlot()).getIdCard()<71))
                                        ) card.arrangements++;
                                        if(((20< id1 && id1 <31)||(60< id1 && id1 <71)) &&
                                                (0<i-1 && j+1<c) && (s[i-1][j+1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<71)) &&
                                                (i+1<r && 0<j-1) && (s[i+1][j-1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<71))
                                        ) card.arrangements++;
                                        if(((20< id1 && id1 <31)||(60< id1 && id1 <71)) &&
                                                (0<i-1 && j+1<c) && (s[i-1][j+1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<71)) &&
                                                (0<i-2 && j+2<c) && (s[i-2][j+2].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j+2].getCardSlot()).getIdCard()<71))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                        case 90:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((30< id1 && id1 <41)||(70< id1 && id1 <81)) &&
                                                (i+1<r && j+1<c) && (s[i+1][j+1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<81)) &&
                                                (i+2<r && j+2<c) && (s[i+2][j+2].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j+2].getCardSlot()).getIdCard()<81))
                                        ) card.arrangements++;
                                        if(((30< id1 && id1 <41)||(70< id1 && id1 <81)) &&
                                                (0<i-1 && 0<j-1) && (s[i-1][j-1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<81)) &&
                                                (i+1<r && j+1<c) && (s[i+1][j+1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<21))
                                        ) card.arrangements++;
                                        if(((30< id1 && id1 <41)||(70< id1 && id1 <81)) &&
                                                (0<i-1 && 0<j-1) && (s[i-1][j-1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<81)) &&
                                                (0<i-2 && 0<j-2) && (s[i-2][j-2].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j-2].getCardSlot()).getIdCard()<81))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                        case 91:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((0< id1 && id1 <11)||(40< id1 && id1 <51)) &&
                                                (i+2<r) && (s[i+2][j].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<51)) &&
                                                (i+3<r && j+1<c) && (s[i+3][j+1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard()<61))
                                        ) card.arrangements++;
                                        if(((0< id1 && id1 <11)||(40< id1 && id1 <51)) &&
                                                (0<i-2) && (s[i-2][j].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<51)) &&
                                                (i+1<r && j+1<c) && (s[i+1][j+1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<61))
                                        ) card.arrangements++;
                                        if(((10< id1 && id1 <21)||(50< id1 && id1 <61)) &&
                                                (0<i-1 && 0<j-1) && (s[i-1][j-1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<51)) &&
                                                (0<i-2) && (s[i-2][j-1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-2][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j-1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-2][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j-1].getCardSlot()).getIdCard()<51))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                        case 92:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((10< id1 && id1 <21)||(50< id1 && id1 <61)) &&
                                                (i+2<r) && (s[i+2][j].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<61)) &&
                                                (i+3<r && 0<j-1) && (s[i+3][j-1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard()<81))
                                        ) card.arrangements++;
                                        if(((10< id1 && id1 <21)||(50< id1 && id1 <61)) &&
                                                (0<i-2) && (s[i-2][j].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<61)) &&
                                                (i+1<r && 0<j-1) && (s[i+1][j-1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<81))
                                        ) card.arrangements++;
                                        if(((30< id1 && id1 <41)||(70< id1 && id1 <81)) &&
                                                (0<i-1 && j+1<c) && (s[i-1][j+1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<61)) &&
                                                (0<i-3) && (s[i-3][j+1].isBusySlot()) &&
                                                ((10<((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard()<21)||
                                                        (50<((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard()<61))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                        case 93:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((0< id1 && id1 <11)||(40< id1 && id1 <51)) &&
                                                (i+1<r && 0<j-1) && (s[i+1][j-1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j-1].getCardSlot()).getIdCard()<71)) &&
                                                (i+3<r) && (s[i+3][j-1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+3][j-1].getCardSlot()).getIdCard()<71))
                                        ) card.arrangements++;
                                        if(((20< id1 && id1 <31)||(60< id1 && id1 <71)) &&
                                                (0<i-1 && j+1<c) && (s[i-1][j+1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j+1].getCardSlot()).getIdCard()<51)) &&
                                                (i+2<r) && (s[i+2][j].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<71))
                                        ) card.arrangements++;
                                        if(((20< id1 && id1 <31)||(60< id1 && id1 <71)) &&
                                                (0<i-2) && (s[i-2][j].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<71)) &&
                                                (0<i-3 && j+1<c) && (s[i-3][j+1].isBusySlot()) &&
                                                ((0<((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard()<11)||
                                                        (40<((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-3][j+1].getCardSlot()).getIdCard()<51))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                        case 94:
                            for (i = 0; i <r ; i++) {
                                for (j = 0; j <c ; j++) {
                                    if(s[i][j].isBusySlot()){
                                        card =(ResourceGoldCard) s[i][j].getCardSlot();
                                        int id1 = card.getIdCard();
                                        if(((20< id1 && id1 <31)||(60< id1 && id1 <71)) &&
                                                (i+1<r && j+1<c) && (s[i+1][j+1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+1][j+1].getCardSlot()).getIdCard()<81)) &&
                                                (i+3<r) && (s[i+3][j+1].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+3][j+1].getCardSlot()).getIdCard()<81))
                                        ) card.arrangements++;
                                        if(((30< id1 && id1 <41)||(70< id1 && id1 <81)) &&
                                                (0<i-1 && 0<j-1) && (s[i-1][j-1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-1][j-1].getCardSlot()).getIdCard()<71)) &&
                                                (i+2<r) && (s[i+2][j].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i+2][j].getCardSlot()).getIdCard()<81))
                                        ) card.arrangements++;
                                        if(((30< id1 && id1 <41)||(70< id1 && id1 <81)) &&
                                                (0<i-2) && (s[i-2][j].isBusySlot()) &&
                                                ((30<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<41)||
                                                        (70<((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-2][j].getCardSlot()).getIdCard()<81)) &&
                                                (0<i-3 && 0<j-1) && (s[i-3][j-1].isBusySlot()) &&
                                                ((20<((ResourceGoldCard) s[i-3][j-1].getCardSlot()).getIdCard() &&
                                                        ((ResourceGoldCard) s[i-3][j-1].getCardSlot()).getIdCard()<31)||
                                                        (60<((ResourceGoldCard) s[i-3][j-1].getCardSlot()).getIdCard() &&
                                                                ((ResourceGoldCard) s[i-3][j-1].getCardSlot()).getIdCard()<71))
                                        ) card.arrangements++;
                                    }
                                }
                            }
                    }

                    int arrangedCards=0;
                    for(i=0; i<r; i++){
                        for(j=0; j<c; j++) {
                            if(s[i][j].isBusySlot()) {
                                if (((ResourceGoldCard)s[i][j].getCardSlot()).arrangements != 0)
                                    arrangedCards++;
                            }
                        }
                    } points=(arrangedCards/3)*3;
            }
        }
        return points;
    }

    public boolean areAllSecretObjectiveSet() {
        int counter = 0;
        List<ClientHandler> handlersList = ServerConnectionManager.handlers.values().stream().toList();
        for (ClientHandler ch : handlersList) {
            if (ch.getSecretWasChosen()) {
                ++counter;
            }
        }
        return counter == handlersList.size();
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

        /** aggiornare l'array (mappa): objectivePoints */
        hashObjectivePoints.put(p, extraPoints);

        return extraPoints;
    }

    private void declareWinnerOrDraw() {

        int maxScore =0;
        int draw = 0;
        Player playerWin = null;

        /** find max points */
        for(Player p : players){
            if(p.getScore()> maxScore){
                maxScore = p.getScore();
                playerWin = p;
            }
        }
        winners.add(playerWin);

        /** check if a draw exists */
        for(Player pDraw : players){
            if(pDraw.getScore()== maxScore && pDraw != playerWin){
                draw++;
                winners.add(pDraw);
            }
        }

        if(draw !=0) drawWinners();
        else declareWinners();
    }

    private void drawWinners() {

        int MaxObjPoint=0;
        int objPoint=0;
        Player playerObjWin = null;

        /** find max objective points in winners[] */
        for(Player p : winners){
            objPoint = hashObjectivePoints.get(p);
            if(objPoint > MaxObjPoint){
                MaxObjPoint = objPoint;
                playerObjWin = p;
            }
        }
        finalWinners.add(playerObjWin);

        /** check if a draw exists */
        for(Player p : winners){
            if(hashObjectivePoints.get(p) == MaxObjPoint && p != playerObjWin){
                finalWinners.add(p);
            }
        }
        /** print draw winners */
        System.out.println("DRAW BETWEEN: ");
        int s = finalWinners.size();
        for(int i = 0; i < s; i++){
            Player p = finalWinners.get(i);
            System.out.println(p.getPlayerName());
        }
    }

    /** print winner */
    private void declareWinners() {
        int s = winners.size();
        for(int i = 0; i < s; i++){
            Player p = winners.get(i);
            System.out.println("WINNER: " + p.getPlayerName());
        }
    }

    public ArrayList<ResourceGoldCard> getCoveredCards() {
        return coveredCards;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setCommonObjectives(ArrayList<ObjectiveCard> commonObjectives) {
        this.commonObjectives = commonObjectives;
        for(Player p: players) p.setCommonObjCards(commonObjectives);
    }

}


