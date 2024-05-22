package com.example.codexnaturalis;

import java.util.*;

public class Match {
    private final ArrayList<Player> players;
    private ArrayList<Player> winners = new ArrayList<>();
    private ArrayList<Player> finalWinners = new ArrayList<>();
    protected static HashMap<Player, Integer> hashObjectivePoints = new HashMap<>();
    private final ScoreTracker scoreTracker;
    private boolean isLastCycle = false;
    private static ArrayList<ObjectiveCard> commonObjectives;
    private ArrayList<ResourceGoldCard> publicCards = new ArrayList<>();
    private ArrayList<ResourceGoldCard> coveredCards;
    private int indexCurrentPlayer;
    private UUID matchID;
    private int[] previousElementMana;
    private int[] previousResourceMana;
    public HashMap<UUID, ArrayList<ObjectiveCard>> selectedSecrets = new HashMap<>();
    private ArrayList<UUID> playerIds = new ArrayList<>();



    /**
     * Constructor of Match
     * @param players:      ArrayList of all players in the match
     * @param scoreTracker: score tracker
     */
    public Match(ArrayList<Player> players, ScoreTracker scoreTracker) {
        this.players = players;
        System.out.println("Players currently playing: ");
        printPLayers();
        this.scoreTracker = scoreTracker;
        this.coveredCards = new ArrayList<>();
        this.matchID = UUID.randomUUID();
        coveredCards.add(0, DrawingDeck.drawCard(true));
        coveredCards.add(1, DrawingDeck.drawCard(false));
        Collections.shuffle(players);
        System.out.println("Players have been shuffled, here's the current playing order");
        printPLayers();
        for (Player player : players) playerIds.add(player.getPlayerID());
//        printPlayerIds();
    }

    public void printPLayers() {
        for (int i = 0; i < players.size(); i++) {
            System.out.println((i) + ") " + players.get(i).getPlayerName());
        }
    }

    public void printPlayerIds() {
        System.out.println("Here's the list of updated Ids:");
        for (int i = 0; i < playerIds.size(); i++) {
            System.out.println((i) + ") " + playerIds.get(i));
        }
    }


    /**
     * Chooses who will be the first player and updates its firstPlayer attribute
     *
     * @return Player, first player of the match
     */
    public StandardMatchMessage chooseRandomFirstPlayer() {

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
        //TODO: DA ELIMINARE
        //playingPlayer.setScore(20);

        return new StandardMatchMessage(publicCards, playingPlayer.getPlayerID(), playingPlayer.getPlayerName(), null, null, null);
    }

    public ArrayList<ObjectiveCard> getCommonObjectives() {
        return commonObjectives;
    }

    /**
     * Main method of match progression. Gets called from server. When receiving a message from Server, it gets deserialized, analyzed,
     * model is modified accordingly, and changes are sent back to server as a StandardMatchMessage
     * @param msg: Message coming from server containing changes to be made in the model
     * @return StandardMatchMessage, message containing changes of the model to be sent to server
     */
    public StandardMatchMessage genericTurn(GenericTurnMessage msg) {
        Player playingPlayer = getPlayerFromId(msg.getClientID());
        String playerName = playingPlayer.getPlayerName();

        System.out.println(Colors.GREEN + "\n--" + playerName + " is playing his turn--" + Colors.RESET);

        //PLACE CARD ON FIELD
        int row = msg.getCoordinates().getKey();
        int column = msg.getCoordinates().getValue();
        ResourceGoldCard cardToPlace = msg.getCardOnHand().getFirst();
        System.out.println(Colors.GREEN + "--Placing #" + cardToPlace.getIdCard() + " card on " + playerName + "'s field in [" + row + "][" + column + "]--" + Colors.RESET);
        previousElementMana = playingPlayer.getElementsMana();
        playingPlayer.placeCardAndRemoveFromDeck(row, column, cardToPlace);
        System.out.println(Colors.GREEN + "--Card placed--" + Colors.RESET);
        System.out.println("Check points sees: " + cardToPlace.getCoveredCornersWhenPlaced());
        playingPlayer.addScore(checkPoints(cardToPlace,playingPlayer, previousElementMana));

        System.out.println(Colors.GREEN + "--Check of cycle--" + Colors.RESET);
        //These 2 Ifs check if we are at the end of the cycle and if the playing player is the last on the cycle
        if (!isLastCycle) {
            if (playerIsWinner(playingPlayer)) {
                isLastCycle = true;
            }
        }
        if (isLastCycle && indexCurrentPlayer == playerIds.size() - 1) {
            lastRoundRoutine();
            EndMatchMessage endGame = new EndMatchMessage(null, msg.getClientID(), msg.getSender(), null, msg.getCardOnHand().getFirst(), msg.getCoordinates());
            endGame.setFinalWinners(finalWinners);
            return endGame;
        }

        //ADD THE DRAWN CARD TO THE PLAYER'S DECK AND REMOVE IT FROM WHERE IT WAS DRAWN
        ResourceGoldCard cardDrawn = msg.getDrawnCard().getFirst();
        int cardDrawnId = cardDrawn.getIdCard();
        System.out.println(Colors.GREEN + "--Adding the drawn card #" + cardDrawn.getIdCard() + " to " + playerName + "'s deck--" + Colors.RESET);
        boolean isResourceCard = cardDrawn instanceof ResourceCard;
        if (publicCards.contains(getPublicCardFromId(cardDrawnId))) {
            ResourceGoldCard tmpCard = getPublicCardFromId(cardDrawnId);
            int tmpCardPos = publicCards.indexOf(tmpCard);
            publicCards.remove(tmpCard);
            System.out.println(Colors.GREEN + "--Card #" + tmpCard.getIdCard() + " removed from public cards--" + Colors.RESET);
            playingPlayer.getPlayerDeck().getResourceGoldCards().add(tmpCard);
            System.out.println(Colors.GREEN + "--Card #" + tmpCard.getIdCard() + " added to --" + playingPlayer.getPlayerName() + "'s deck from public cards--" + Colors.RESET);
            ResourceGoldCard replacementCard = coveredCards.get(isResourceCard ? 0 : 1);
            publicCards.add(tmpCardPos, replacementCard);
            coveredCards.remove(replacementCard);
            System.out.println(Colors.GREEN + "--Card #" + replacementCard.getIdCard() + " added to public cards as replacement from covered cards--" + Colors.RESET);
            ResourceGoldCard cardAddedToCovered = DrawingDeck.drawCard(isResourceCard);
            coveredCards.add(isResourceCard ? 0 : 1, cardAddedToCovered);
            System.out.println(Colors.GREEN + "--Card #" + cardAddedToCovered.getIdCard() + " added to covered cards--" + Colors.RESET);
        } else {
            ResourceGoldCard tmpCard = getCoveredCardFromId(cardDrawnId);
            coveredCards.remove(tmpCard);
            System.out.println(Colors.GREEN + "--Card #" + tmpCard.getIdCard() + " removed from covered cards--" + Colors.RESET);
            ResourceGoldCard replacementCard = DrawingDeck.drawCard(isResourceCard);
            coveredCards.add(isResourceCard ? 0 : 1, replacementCard);
            System.out.println(Colors.GREEN + "--Card #" + replacementCard.getIdCard() + " added to covered cards--" + Colors.RESET);
        }
        System.out.println(Colors.GREEN + "--Card added--" + Colors.RESET);

        //SELECT INDEX OF NEXT PLAYER
        System.out.println(Colors.GREEN + "--Selecting next player--" + Colors.RESET);
        UUID currentPlayerId = playingPlayer.getPlayerID();
        int nextPlayerIndex = selectIndexNextPlayer(indexCurrentPlayer);
        Player nextPlayer = getPlayerFromId(playerIds.get(nextPlayerIndex));
        System.out.println(Colors.GREEN + "--" + nextPlayer.getPlayerName() + " is the next player--" + Colors.RESET);
        UUID nextPlayerId = nextPlayer.getPlayerID();
        indexCurrentPlayer = nextPlayerIndex;
        System.out.println(Colors.GREEN + "--Next player selected, it's" + playerName + "'s turn--" + Colors.RESET);
        /** Chiamare checkWinner. Se il flag è true arrivare all'ultimo giocatore e terminare il match.
         * Infine chiamare lastRoundRoutine*/
        StandardMatchMessage mex = new StandardMatchMessage(publicCards, currentPlayerId, playerName, nextPlayerId, msg.getCardOnHand().getFirst(), msg.getCoordinates());
        mex.setCurrPlayerPoints(playingPlayer.getScore());
        return mex;
    }

    /**
     * Given a card Id, returns the card with that id in the public cards
     * @param cardId: card id to look for
     * @return ResourceGoldCard, card in the public cards with that Id
     */
    private ResourceGoldCard getPublicCardFromId(int cardId) {
        ResourceGoldCard cardToReturn = null;
        for (ResourceGoldCard publicCard : publicCards) {
            if (publicCard.getIdCard() == cardId) {
                cardToReturn = publicCard;
            }
        }
        return cardToReturn;
    }

    /**
     * Given a card Id, returns the card with that id in the covered cards
     * @param cardId: card id to look for
     * @return ResourceGoldCard, card in the covered cards with that Id
     */
    private ResourceGoldCard getCoveredCardFromId(int cardId) {
        ResourceGoldCard cardToReturn = null;
        for (ResourceGoldCard publicCard : coveredCards) {
            if (publicCard.getIdCard() == cardId) {
                cardToReturn = publicCard;
            }
        }
        return cardToReturn;
    }

    /**
     * Given a UUID, return the Player in the match with that ID
     * @param playerId: player Id to look for
     * @return Player, player with the given Id
     */
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
     * Called in server, for each player two Objective Cards are Drawn and presented to the player
     * @return HashMap of UUID and ArrayList of ObjectiveCards from which the player chooses the card he wants to keep
     */
    public HashMap<UUID, ArrayList<ObjectiveCard>> getTwoSecretObjectiveCards() {

        for (Player p : players) {
            ArrayList<ObjectiveCard> cards = DrawingDeck.drawTwoObjectiveCards();
            selectedSecrets.put(p.getPlayerID(), cards);
        }
        return selectedSecrets;
    }

    /**
     * Called when the player chooses which Objective card from the 2 drawn wants to keep
     * @param clientID: Id of the client that is choosing
     * @param cardToKeep
     */
    public void putBackOtherSecretObjectiveCard(UUID clientID, ObjectiveCard cardToKeep) {
        ObjectiveCard cardToDiscard = null;
        for (ObjectiveCard c : selectedSecrets.get(clientID)) if (!c.equals(cardToKeep)) cardToDiscard = c;
        selectedSecrets.get(clientID).remove(cardToDiscard);
        DrawingDeck.reAddSecretObjectiveCard(cardToDiscard);
    }

    public void addDisconnectedPlayerId(UUID playerToAddId) {
        Player playerToAdd = getPlayerFromId(playerToAddId);
        int playerToAddIndex = players.indexOf(playerToAdd);
        playerIds.set(playerToAddIndex, playerToAddId);
//        printPlayerIds();
    }

    public StandardMatchMessage removeDisconnectedPlayer(UUID disconnectedPlayerId) {
        Player playerToRemove = getPlayerFromId(disconnectedPlayerId);
        int playerToRemoveIndex = players.indexOf(playerToRemove);
        System.out.println(Colors.GREEN + "--" + playerToRemove.getPlayerName() + " disconnected, removing from playerIds--" + Colors.RESET);
        UUID currentPlayerId = playerIds.get(indexCurrentPlayer);
        Player currentPlayer = getPlayerFromId(currentPlayerId);
        System.out.println(Colors.GREEN + "--" + currentPlayer.getPlayerName() + " is playing--" + Colors.RESET);
        //Se il giocatore si disconnette prima di giocare il suo turno
        if (playerToRemove.equals(currentPlayer)) {
            System.out.println(Colors.GREEN + "--Choosing next player--" + Colors.RESET);
            UUID nextPlayerId = playerIds.get(selectIndexNextPlayer(indexCurrentPlayer));
            Player nextPlayer = getPlayerFromId(nextPlayerId);
            indexCurrentPlayer = playerIds.indexOf(nextPlayer.getPlayerID());
            System.out.println(Colors.GREEN + "--" + nextPlayer.getPlayerName() + " is the next player--" + Colors.RESET);
            playerIds.set(playerToRemoveIndex, null);
            System.out.println(Colors.GREEN + "--" + playerToRemove.getPlayerName() + " removed from players--" + Colors.RESET);
//            printPlayerIds();
            //Se rimane solo un giocatore allora invio un messaggio con uno UUID null di convenzione indicante la presenza di un solo giocatore
            if (onlyOnePlayerRemaining()) {
                System.out.println("ONYL ONE PLAYER REMAINING");
                return new CurrentPlayerDisconnectedMessage(publicCards, null, getPlayerFromId(playerIds.getFirst()).getPlayerName(), playerIds.getFirst());
            }
            return new CurrentPlayerDisconnectedMessage(publicCards, disconnectedPlayerId, playerToRemove.getPlayerName(), nextPlayerId);
        } else {
            playerIds.set(playerToRemoveIndex, null);
            System.out.println(Colors.GREEN + "--" + playerToRemove.getPlayerName() + " removed from playerIds--" + Colors.RESET);
//            printPlayerIds();
            //Se rimane solo un giocatore allora invio un messaggio con uno UUID univoco di convenzione indicante la presenza di un solo giocatore
            if (onlyOnePlayerRemaining()) {
                System.out.println("ONYL ONE PLAYER REMAINING");
                return new CurrentPlayerDisconnectedMessage(publicCards, null, getPlayerFromId(playerIds.getFirst()).getPlayerName(), playerIds.getFirst());
            }
            return new notCurrentPlayerDisconnectedMessage(publicCards, disconnectedPlayerId, playerToRemove.getPlayerName());
        }

    }

    private boolean onlyOnePlayerRemaining() {
        int counterPlayers = 0;
        for (int i = 0; i < playerIds.size(); i++) {
            if (playerIds.get(i) != null) counterPlayers++;
        }
        return counterPlayers == 1;
    }

    /**
     * Selects the index of the next player to play
     *
     * @param currentIndex: defines the current index of the current player
     * @return int, defines the index of the next player in line
     */
    public int selectIndexNextPlayer(int currentIndex) {
//        printPlayerIds();
        int indiceProssimo = currentIndex + 1;
        if (indiceProssimo >= playerIds.size()) {
            indiceProssimo = 0;
        }
        System.out.println("INDEX: " + indiceProssimo);
        while (playerIds.get(indiceProssimo) == null) {
            indiceProssimo++;
            if (indiceProssimo >= playerIds.size()) {
                indiceProssimo = 0;
            }
            System.out.println("INDEX: " + indiceProssimo);
        }
        System.out.println(Colors.GREEN + "NEXT PLAYER INDEX: "  + indiceProssimo + Colors.RESET);
        return indiceProssimo;
    }

    /**
     * Checks if a player has reached at least 20 points
     */
    public boolean playerIsWinner(Player playingPlayer) {
        boolean winnerFlag = false;
        if (playingPlayer.getScore() >= 20) {
            winnerFlag = true;
            System.out.println(playingPlayer.getPlayerName() + " ha raggiunto " + playingPlayer.getScore() + " punti!");
        }
        return winnerFlag;
    }

    public static int checkPoints(ResourceGoldCard playedCard, Player currentPlayer, int[] previousElementMana) {
        int id = playedCard.getIdCard();
        int pts = playedCard.getPoints();
        int points=0;

        /** se il mazzo è risorsa: controllare numero punti carta ed eventualmente assegnarli*/
        if (0 < id && id < 41) {
            if(playedCard.isPlacedFront()){
                points = pts;
                return points;
            }


        }
        /** se il mazzo è oro: controllo numero di punti carta e id per criterio:*/
        else if (40 < id && id < 81) {
            if(playedCard.isPlacedFront()) {
                switch (id) {
                    /** se idCard==41||51||63||71 Feather*/
                    case 41, 51, 63, 71:
                        points = previousElementMana[2] * pts;
                        return points;
                    /** se idCard==42||53||61||73  Ink*/
                    case 42, 53, 61, 73:
                        points = previousElementMana[0] * pts;
                        return points;
                    /** se idCard==43||52||62||72 Papyrus*/
                    case 43, 52, 62, 72:
                        points = previousElementMana[1] * pts;
                        return points;
                    /** se idCard==44||45||46||54||55||56||64||65||66||74||75||76 conta angoli coperti*/
                    case 44, 45, 46, 54, 55, 56, 64, 65, 66, 74, 75, 76:
                        points = playedCard.getCoveredCornersWhenPlaced() * pts;
                        return points;
                    /** altrimenti assegna punti*/
                    default:
                        points = pts;
                        return points;
                }
            }
            else
                return 0;
        }
      return 0;
    }

    /**
     * Last Round Routine
     */
    private void lastRoundRoutine() {
        addObjectiveTotalPoints();
        declareWinnerOrDraw();
    }

    /**
     * Somma il punteggio ottenuto dalle care risorsa e oro a quello ottenuto dalle carte obiettivo
     */
    private void addObjectiveTotalPoints() {
        int obj = 0;
        for (Player p : players) {
            obj = checkExtraPoints(p);
            p.addScore(obj);
        }
    }

    /**
     * calculate objective points and add into the array
     */
    protected static int calculateSimpleObjPoints(Player p, int id) {
        int points = 0;
        if (86 < id && id < 103) {
            switch (id) {
                /** se idCard is ripetizioni semi*/
                case 95, 96, 97, 98:
                    //95 3funghi, 96 3foglie, 97 3lupo, 98 3farfalle. tutto x2 punti
                    switch (id) {
                        case 95:
                            points = (p.getResourceMana()[0] / 3) * 2;
                            p.addScore(points);
                            return points;
                        case 96:
                            points = (p.getResourceMana()[1] / 3) * 2;
                            p.addScore(points);
                            return points;
                        case 97:
                            points = (p.getResourceMana()[2] / 3) * 2;
                            p.addScore(points);
                            return points;
                        case 98:
                            points = (p.getResourceMana()[3] / 3) * 2;
                            p.addScore(points);
                            return points;
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
                            points = min * 3;
                            p.addScore(points);
                            return points;
                        // 2 pergamene
                        case 100:
                            points = (p.getElementsMana()[1] / 2) * 2;
                            p.addScore(points);
                            return points;
                        // 2 ink
                        case 101:
                            points = (p.getElementsMana()[0] / 2) * 2;
                            p.addScore(points);
                            return points;
                        // 2 piume
                        case 102:
                            points = (p.getElementsMana()[2] / 2) * 2;
                            p.addScore(points);
                            return points;

                        default:
                            break;


                    }
                default:
                    break;
            }
        }return points;
    }
    protected static int calculateArrObjPoints(Player p) {
        int points = 0;
        points += checkArrangements(p, p.getPlayerDeck().getSecretObjectiveCard().getIdCard());
        for(ObjectiveCard o: commonObjectives){
            points += checkArrangements(p,o.getIdCard());
        }
        int r = p.getPlayerField().getR(), c = p.getPlayerField().getC();
        int i, j;
        Field.Slot[][] s;
        s = p.getPlayerField().getSlots();
        int arrangedCards = 0;
        for (i = 0; i < r; i++) {
            for (j = 0; j < c; j++) {
                if (s[i][j].isBusySlot() &&
                        !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                    if (((ResourceGoldCard) s[i][j].getCardSlot()).arrangements != 0 &&
                            ((ResourceGoldCard)s[i][j].getCardSlot()).getIsArrangeable())
                        arrangedCards++;
                }
            }
        }
        points += (arrangedCards/3)*2;
        return points;
    }
    protected static int checkArrangements(Player p, int id){
        int points=0;
        int r = p.getPlayerField().getR(), c = p.getPlayerField().getC();
        int i, j;
        Field.Slot[][] s;
        s = p.getPlayerField().getSlots();
        ResourceGoldCard card;
        switch (id) {
            case 87:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Red) &&
                                    card.getIsArrangeable() &&
                                    (i + 1 < r && 0 < j - 1) && (s[i + 1][j - 1].isBusySlot()) &&
                                    (s[i+1][j-1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j-1].getRGCardSlot()).getSeed().equals(Seed.Red) &&
                                    (i + 2 < r && 0 < j - 2) && (s[i + 2][j - 2].isBusySlot()) &&
                                    (s[i+2][j-2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j-2].getRGCardSlot()).getSeed().equals(Seed.Red)
                            ) card.arrangements++;

                            if (card.getSeed().equals(Seed.Red) &&
                                    card.getIsArrangeable() &&
                                    (0 < i - 1 && j + 1 < c) && (s[i - 1][j + 1].isBusySlot()) &&
                                    (s[i-1][j+1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j+1].getRGCardSlot()).getSeed().equals(Seed.Red) &&
                                    (i + 1 < r && 0 < j - 1) && (s[i + 1][j - 1].isBusySlot()) &&
                                    (s[i+1][j-1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j-1].getRGCardSlot()).getSeed().equals(Seed.Red)
                            ) card.arrangements++;
                            if (card.getSeed().equals(Seed.Red) &&
                                    card.getIsArrangeable() &&
                                    (0 < i - 1 && j + 1 < c) && (s[i - 1][j + 1].isBusySlot()) &&
                                    (s[i-1][j+1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j+1].getRGCardSlot()).getSeed().equals(Seed.Red) &&
                                    (0 < i - 2 && j + 2 < c) && (s[i - 2][j + 2].isBusySlot()) &&
                                    (s[i - 2][j + 2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j+2].getRGCardSlot()).getSeed().equals(Seed.Red)
                            ) card.arrangements++;
                        }
                    }
                }
            case 88:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Green) &&
                                    card.getIsArrangeable() &&
                                    (i + 1 < r && j + 1 < c) && (s[i + 1][j + 1].isBusySlot()) &&
                                    (s[i+1][j+1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j+1].getRGCardSlot()).getSeed().equals(Seed.Green) &&
                                    (i + 2 < r && j + 2 < c) && (s[i + 2][j + 2].isBusySlot()) &&
                                    (s[i + 2][j + 2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j+2].getRGCardSlot()).getSeed().equals(Seed.Green)
                            ) card.arrangements++;
                            if (card.getSeed().equals(Seed.Green) &&
                                    card.getIsArrangeable() &&
                                    (0 < i - 1 && 0 < j - 1) && (s[i - 1][j - 1].isBusySlot()) &&
                                    (s[i - 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j-1].getRGCardSlot()).getSeed().equals(Seed.Green) &&
                                    (i + 1 < r && j + 1 < c) && (s[i + 1][j + 1].isBusySlot()) &&
                                    (s[i + 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j+1].getRGCardSlot()).getSeed().equals(Seed.Green)
                            ) card.arrangements++;
                            if (card.getSeed().equals(Seed.Green) &&
                                    card.getIsArrangeable() &&
                                    (0 < i - 1 && 0 < j - 1) && (s[i - 1][j - 1].isBusySlot()) &&
                                    (s[i - 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j-1].getRGCardSlot()).getSeed().equals(Seed.Green) &&
                                    (0 < i - 2 && 0 < j - 2) && (s[i - 2][j - 2].isBusySlot()) &&
                                    (s[i - 2][j-2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j-2].getRGCardSlot()).getSeed().equals(Seed.Green)
                            ) card.arrangements++;
                        }
                    }
                }
            case 89:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Blue) &&
                                    card.getIsArrangeable()&&
                                    (i + 1 < r && 0 < j - 1) && (s[i + 1][j - 1].isBusySlot()) &&
                                    (s[i + 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j-1].getRGCardSlot()).getSeed().equals(Seed.Blue) &&
                                    (i + 2 < r && 0 < j - 2) && (s[i + 2][j - 2].isBusySlot()) &&
                                    (s[i + 2][j-2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j-2].getRGCardSlot()).getSeed().equals(Seed.Blue)
                            ) card.arrangements++;
                            if (card.getSeed().equals(Seed.Blue) &&
                                    card.getIsArrangeable() &&
                                    (0 < i - 1 && j + 1 < c) && (s[i - 1][j + 1].isBusySlot()) &&
                                    (s[i - 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j+1].getRGCardSlot()).getSeed().equals(Seed.Blue) &&
                                    (i + 1 < r && 0 < j - 1) && (s[i + 1][j - 1].isBusySlot()) &&
                                    (s[i + 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j-1].getRGCardSlot()).getSeed().equals(Seed.Blue)
                            ) card.arrangements++;
                            if (card.getSeed().equals(Seed.Blue) &&
                                    card.getIsArrangeable() &&
                                    (0 < i - 1 && j + 1 < c) && (s[i - 1][j + 1].isBusySlot()) &&
                                    (s[i - 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j+1].getRGCardSlot()).getSeed().equals(Seed.Blue) &&
                                    (0 < i - 2 && j + 2 < c) && (s[i - 2][j + 2].isBusySlot()) &&
                                    (s[i - 2][j + 2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j+2].getRGCardSlot()).getSeed().equals(Seed.Blue)
                            ) card.arrangements++;
                        }
                    }
                }
            case 90:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Purple) &&
                                    card.getIsArrangeable() &&
                                    (i + 1 < r && j + 1 < c) && (s[i + 1][j + 1].isBusySlot()) &&
                                    (s[i + 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j+1].getRGCardSlot()).getSeed().equals(Seed.Purple) &&
                                    (i + 2 < r && j + 2 < c) && (s[i + 2][j + 2].isBusySlot()) &&
                                    (s[i + 2][j + 2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j+2].getRGCardSlot()).getSeed().equals(Seed.Purple)
                            ) card.arrangements++;
                            if (card.getSeed().equals(Seed.Purple) &&
                                    card.getIsArrangeable() &&
                                    (0 < i - 1 && 0 < j - 1) && (s[i - 1][j - 1].isBusySlot()) &&
                                    (s[i - 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j-1].getRGCardSlot()).getSeed().equals(Seed.Purple) &&
                                    (i + 1 < r && j + 1 < c) && (s[i + 1][j + 1].isBusySlot()) &&
                                    (s[i + 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j+1].getRGCardSlot()).getSeed().equals(Seed.Purple)
                            ) card.arrangements++;
                            if (card.getSeed().equals(Seed.Purple) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 1 && 0 < j - 1) && (s[i - 1][j - 1].isBusySlot()) &&
                                    (s[i - 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j-1].getRGCardSlot()).getSeed().equals(Seed.Purple) &&
                                    (0 < i - 2 && 0 < j - 2) && (s[i - 2][j - 2].isBusySlot()) &&
                                    (s[i - 2][j - 2].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j-2].getRGCardSlot()).getSeed().equals(Seed.Purple)
                            ) card.arrangements++;
                        }
                    }
                }
            case 91:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Red) &&
                                    card.getIsArrangeable()&&
                                    (i + 2 < r) && (s[i + 2][j].isBusySlot()) &&
                                    (s[i + 2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j].getRGCardSlot()).getSeed().equals(Seed.Red) &&
                                    (i + 3 < r && j + 1 < c) && (s[i + 3][j + 1].isBusySlot()) &&
                                    (s[i + 3][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+3][j+1].getRGCardSlot()).getSeed().equals(Seed.Green)
                            ) {
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i + 2][j].getRGCardSlot()).setIsArrangeable(false);
                                (s[i + 2][j].getRGCardSlot()).arrangements=0;
                                (s[i + 3][j + 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 3][j + 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                            if (card.getSeed().equals(Seed.Red) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 2) && (s[i - 2][j].isBusySlot()) &&
                                    (s[i - 2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j].getRGCardSlot()).getSeed().equals(Seed.Red) &&
                                    (i + 1 < r && j + 1 < c) && (s[i + 1][j + 1].isBusySlot()) &&
                                    (s[i + 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j+1].getRGCardSlot()).getSeed().equals(Seed.Green)
                            ) {
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 2][j].getRGCardSlot()).setIsArrangeable(false);
                                (s[i - 2][j].getRGCardSlot()).arrangements=0;
                                (s[i + 1][j + 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 1][j + 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                            if (card.getSeed().equals(Seed.Green) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 1 && 0 < j - 1) && (s[i - 1][j - 1].isBusySlot()) &&
                                    (s[i - 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j-1].getRGCardSlot()).getSeed().equals(Seed.Red) &&
                                    (0 < i - 2) && (s[i - 2][j - 1].isBusySlot()) &&
                                    (s[i - 2][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j-1].getRGCardSlot()).getSeed().equals(Seed.Red)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 1][j - 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 1][j - 1].getRGCardSlot().arrangements=0;
                                (s[i - 2][j - 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 2][j - 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                        }
                    }
                }
            case 92:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Green) &&
                                    card.getIsArrangeable()&&
                                    (i + 2 < r) && (s[i + 2][j].isBusySlot()) &&
                                    (s[i + 2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j].getRGCardSlot()).getSeed().equals(Seed.Green) &&
                                    (i + 3 < r && 0 < j - 1) && (s[i + 3][j - 1].isBusySlot()) &&
                                    (s[i + 3][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+3][j-1].getRGCardSlot()).getSeed().equals(Seed.Purple)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i + 2][j].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 2][j].getRGCardSlot().arrangements=0;
                                (s[i + 3][j - 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 3][j - 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                            if (card.getSeed().equals(Seed.Green) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 2) && (s[i - 2][j].isBusySlot()) &&
                                    (s[i - 2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j].getRGCardSlot()).getSeed().equals(Seed.Green) &&
                                    (i + 1 < r && 0 < j - 1) && (s[i + 1][j - 1].isBusySlot()) &&
                                    (s[i + 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j-1].getRGCardSlot()).getSeed().equals(Seed.Purple)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 2][j].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 2][j].getRGCardSlot().arrangements=0;
                                (s[i + 1][j - 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 1][j - 1].getRGCardSlot().arrangements=0;
                                points+= 3;
                            }
                            if (card.getSeed().equals(Seed.Purple) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 1 && j + 1 < c) && (s[i - 1][j + 1].isBusySlot()) &&
                                    (s[i - 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j+1].getRGCardSlot()).getSeed().equals(Seed.Green) &&
                                    (0 < i - 3) && (s[i - 3][j + 1].isBusySlot()) &&
                                    (s[i - 3][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-3][j+1].getRGCardSlot()).getSeed().equals(Seed.Green)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 1][j + 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 1][j + 1].getRGCardSlot().arrangements=0;
                                (s[i - 3][j + 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 3][j + 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                        }
                    }
                }
            case 93:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Red) &&
                                    card.getIsArrangeable()&&
                                    (i + 1 < r && 0 < j - 1) && (s[i + 1][j - 1].isBusySlot()) &&
                                    (s[i + 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j-1].getRGCardSlot()).getSeed().equals(Seed.Blue) &&
                                    (i + 3 < r) && (s[i + 3][j - 1].isBusySlot()) &&
                                    (s[i + 3][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+3][j-1].getRGCardSlot()).getSeed().equals(Seed.Blue)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i + 1][j-1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 1][j-1].getRGCardSlot().arrangements=0;
                                (s[i + 3][j - 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 3][j - 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                            if (card.getSeed().equals(Seed.Blue) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 1 && j + 1 < c) && (s[i - 1][j + 1].isBusySlot()) &&
                                    (s[i - 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j+1].getRGCardSlot()).getSeed().equals(Seed.Red) &&
                                    (i + 2 < r) && (s[i + 2][j].isBusySlot()) &&
                                    (s[i + 2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j].getRGCardSlot()).getSeed().equals(Seed.Blue)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 1][j+1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 1][j+1].getRGCardSlot().arrangements=0;
                                (s[i + 2][j].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 2][j].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                            if (card.getSeed().equals(Seed.Blue) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 2) && (s[i - 2][j].isBusySlot()) &&
                                    (s[i - 2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j].getRGCardSlot()).getSeed().equals(Seed.Blue) &&
                                    (0 < i - 3 && j + 1 < c) && (s[i - 3][j + 1].isBusySlot()) &&
                                    (s[i - 3][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-3][j+1].getRGCardSlot()).getSeed().equals(Seed.Red)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 2][j].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 2][j].getRGCardSlot().arrangements=0;
                                (s[i - 3][j + 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 3][j].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                        }
                    }
                }
            case 94:
                for (i = 0; i < r; i++) {
                    for (j = 0; j < c; j++) {
                        if (s[i][j].isBusySlot() && !s[i][j].getCardSlot().getClass().getName().equals("com.example.codexnaturalis.StarterCard")) {
                            card = (ResourceGoldCard) s[i][j].getCardSlot();
                            if (card.getSeed().equals(Seed.Blue) &&
                                    card.getIsArrangeable()&&
                                    (i + 1 < r && j + 1 < c) && (s[i + 1][j + 1].isBusySlot()) &&
                                    (s[i + 1][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+1][j+1].getRGCardSlot()).getSeed().equals(Seed.Purple) &&
                                    (i + 3 < r) && (s[i + 3][j + 1].isBusySlot()) &&
                                    (s[i + 3][j + 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+3][j+1].getRGCardSlot()).getSeed().equals(Seed.Purple)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i + 1][j + 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 1][j + 1].getRGCardSlot().arrangements=0;
                                (s[i + 3][j + 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 3][j + 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                            if (card.getSeed().equals(Seed.Purple) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 1 && 0 < j - 1) && (s[i - 1][j - 1].isBusySlot()) &&
                                    (s[i - 1][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-1][j-1].getRGCardSlot()).getSeed().equals(Seed.Blue) &&
                                    (i + 2 < r) && (s[i + 2][j].isBusySlot()) &&
                                    (s[i + 2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i+2][j].getRGCardSlot()).getSeed().equals(Seed.Purple)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 1][j - 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 1][j - 1].getRGCardSlot().arrangements=0;
                                (s[i + 2][j].getRGCardSlot()).setIsArrangeable(false);
                                s[i + 2][j].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                            if (card.getSeed().equals(Seed.Purple) &&
                                    card.getIsArrangeable()&&
                                    (0 < i - 2) && (s[i - 2][j].isBusySlot()) &&
                                    (s[i-2][j].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-2][j].getRGCardSlot()).getSeed().equals(Seed.Purple) &&
                                    (0 < i - 3 && 0 < j - 1) && (s[i - 3][j - 1].isBusySlot()) &&
                                    (s[i - 3][j - 1].getRGCardSlot()).getIsArrangeable() &&
                                    (s[i-3][j-1].getRGCardSlot()).getSeed().equals(Seed.Blue)
                            ){
                                card.setIsArrangeable(false);
                                card.arrangements=0;
                                (s[i - 2][j].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 2][j].getRGCardSlot().arrangements=0;
                                (s[i - 3][j - 1].getRGCardSlot()).setIsArrangeable(false);
                                s[i - 3][j - 1].getRGCardSlot().arrangements=0;
                                points += 3;
                            }
                        }
                    }
                }
            default: break;
        } return points;
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
        int extraPoints = 0;
        /** calculates extraPoints from the secret Objective Card in case it has a simple condition */
        extraPoints += calculateSimpleObjPoints(p,p.getPlayerDeck().getSecretObjectiveCard().getIdCard());
        /** calculates extraPoints from the common Objective Cards in case they have a simple condition */
        for(ObjectiveCard o: commonObjectives){
            extraPoints += calculateSimpleObjPoints(p,o.getIdCard());
        }
        /** calculates extraPoints from all (secret and common) of the Objective Cards in case they have an arrangement condition */
        extraPoints += calculateArrObjPoints(p);

        /** aggiornare l'array (mappa): objectivePoints */
        hashObjectivePoints.put(p, extraPoints);

        return extraPoints;
    }


    private void declareWinnerOrDraw() {

        int maxScore = 0;
        int draw = 0;
        Player playerWin = null;

        /** find max points */
        for (Player p : players) {
            if (p.getScore() > maxScore) {
                maxScore = p.getScore();
                playerWin = p;
            }
        }
        winners.add(playerWin);

        /** check if a draw exists */
        for (Player pDraw : players) {
            if (pDraw.getScore() == maxScore && pDraw != playerWin) {
                draw++;
                winners.add(pDraw);
            }
        }

        if (draw != 0) drawWinners();
        else declareWinners();
    }

    private void drawWinners() {

        int MaxObjPoint = 0;
        int objPoint = 0;
        Player playerObjWin = null;

        /** find max objective points in winners[] */
        for (Player p : winners) {
            objPoint = hashObjectivePoints.get(p);
            if (objPoint > MaxObjPoint) {
                MaxObjPoint = objPoint;
                playerObjWin = p;
            }
        }
        finalWinners.add(playerObjWin);

        /** check if a draw exists */
        for (Player p : winners) {
            if (hashObjectivePoints.get(p) == MaxObjPoint && p != playerObjWin) {
                finalWinners.add(p);
            }
        }
        /** print draw winners */
        System.out.println("DRAW BETWEEN: ");
        int s = finalWinners.size();
        for (int i = 0; i < s; i++) {
            Player p = finalWinners.get(i);
            System.out.println(p.getPlayerName());
        }
    }

    /**
     * print winner
     */
    private void declareWinners() {
        finalWinners.removeAll(finalWinners.stream().toList());
        finalWinners.addAll(winners);
        int s = winners.size();
        for (Player p : winners) {
            System.out.println("WINNER: " + p.getPlayerName());
        }
    }

    public ArrayList<ResourceGoldCard> getCoveredCards() {
        return coveredCards;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getFinalWinners() {
        return finalWinners;
    }

    public void setCommonObjectives(ArrayList<ObjectiveCard> commonObjectives) {
        this.commonObjectives = commonObjectives;
        for (Player p : players) p.setCommonObjCards(commonObjectives);
    }

    public UUID getCurrentPlayerID() {
        return players.get(indexCurrentPlayer).getPlayerID();
    }

    public ArrayList<ResourceGoldCard> getPublicCards() {
        return publicCards;
    }

    public UUID getMatchID() {
        return matchID;
    }

    public ArrayList<UUID> getPlayerIds() {
        return playerIds;
    }
}


