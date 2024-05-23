package com.example.codexnaturalis;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static com.example.codexnaturalis.DrawingDeck.drawCard;
import static com.example.codexnaturalis.DrawingDeck.generateDecks;
import static com.example.codexnaturalis.Seed.*;
import static org.junit.jupiter.api.Assertions.*;

class PointsTest{

    @RepeatedTest(1000)
    void testCalculateObjPoints() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(8, 8), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        Field.Slot[][] s = player.getPlayerField().getSlots();
        int i, j;
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        ResourceGoldCard card = drawCard(rand.nextBoolean());
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if ((i % 2 == 1 && j % 2 == 1) || (i % 2 == 0 && j % 2 == 0)) {
                    System.out.println("Index " + i + " and index " + j + " are compatible");
                    if (!player.isCardAttachableToSlot(i, j)) {
                        System.out.println("Card num. " + card.getIdCard() + " is attachable to slot [" + i + "]" + "[" + j + "]");
                        if (!s[i][j].isBusySlot()) {
                            System.out.println("Slot [" + i + "]" + "[" + j + "]" + " is not busy");
                            player.placeCard(i, j, card);
                            card = drawCard(rand.nextBoolean());
                        } else System.out.println("Slot [" + i + "]" + "[" + j + "]" + " is busy");
                    } else {
                        card = drawCard(rand.nextBoolean());
                        System.out.println("Card num. " + card.getIdCard() + " is not attachable to slot [" + i + "]" + "[" + j + "]");
                    }
                } else System.out.println("Index " + i + " and index " + j + " are uncompatible");
            }
        }
        System.out.println("Totalizzati " + Match.calculateArrObjPoints(player) + " punti dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");


        assertNotSame(-1, Match.calculateArrObjPoints(player));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, Match.calculateSimpleObjPoints(player, 102));
    }

    @RepeatedTest(10)
    void testCheckPoints() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(5, 5), UUID.randomUUID());
        Field f = player.getPlayerField();
        Field.Slot[][] s = f.getSlots();
        int i, j;
        int[] previousElementsMana;
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        ResourceGoldCard card= drawCard(rand.nextBoolean());
        for(i = 0; i < 5; i++){
            for(j = 0; j < 5; j++) {
                if((i%2==1 && j%2==1)||(i%2==0 && j%2==0)) {
                    System.out.println("Index " + i + " and index " + j + " are compatible");
                    if (!player.isCardAttachableToSlot(i, j)) {
                        System.out.println("Card num. " + card.getIdCard() + " is attachable to slot [" + i + "]" + "[" + j + "]");
                        if (!s[i][j].isBusySlot()) {
                            System.out.println("Slot [" + i + "]" + "[" + j + "]" + " is not busy");
                            System.out.println("La carta numero " + card.getIdCard() + " assegna " + card.getPoints() + " punti");
                            previousElementsMana = player.getElementsMana();
                            System.out.println("Si hanno sul tavolo " + previousElementsMana[0] + " Ink, " + previousElementsMana[1] + " Papyrus, " + previousElementsMana[2] + " Feather.");
                            card.setIsPlacedFront(rand.nextBoolean());
                            player.placeCard(i, j, card);
                            player.addScore(Match.checkPoints(card,player,previousElementsMana));
                            System.out.println("Il giocatore " + player.getPlayerName() + " ha totalizzato " + player.getScore() + " punti dopo questo turno");
                            card = drawCard(rand.nextBoolean());

                        } else System.out.println("Slot [" + i + "]" + "[" + j + "]" + " is busy");
                    } else{
                        card = drawCard(rand.nextBoolean());
                        System.out.println("Card num. " + card.getIdCard() + " is not attachable to slot [" + i + "]" + "[" + j + "]");
                    }
                } else System.out.println("Index " + i + " and index " + j + " are uncompatible");
            }
        } System.out.println("Totalizzati " + player.getScore() + " punti");
        assertNotSame(-1,player.getScore());
    }

    @RepeatedTest(200)
    void testCalculateObjPoints87() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(2, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints88() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Green);
        card2.setSeed(Green);
        card3.setSeed(Green);
        player.placeCard(1,1,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,3,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(2,objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints89() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Blue);
        card2.setSeed(Blue);
        card3.setSeed(Blue);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(2, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints90() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(90,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Purple);
        card2.setSeed(Purple);
        card3.setSeed(Purple);
        player.placeCard(1,1,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,3,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(2, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints91() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(91,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Green);
        player.placeCard(1,1,card1);
        player.placeCard(3,1,card2);
        player.placeCard(4,2,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(3, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints92() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(92,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Green);
        card2.setSeed(Green);
        card3.setSeed(Purple);
        player.placeCard(1,2,card1);
        player.placeCard(3,2,card2);
        player.placeCard(4,1,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(3, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints93() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(93,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Blue);
        card3.setSeed(Blue);
        player.placeCard(1,2,card1);
        player.placeCard(2,1,card2);
        player.placeCard(4,1,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(3, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints94() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(94, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Blue);
        card2.setSeed(Purple);
        card3.setSeed(Purple);
        player.placeCard(1,1,card1);
        player.placeCard(2,2,card2);
        player.placeCard(4,2,card3);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(3, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    /*@Test
    void testCalculateObjPoints95() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints96() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints97() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints98() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints99() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints100() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints101() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints102() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        player.placeCard(1,3,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,1,card3);

        assertSame(2, match1.calculateArrObjPoints(player));
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }*/
    @RepeatedTest(200)
    void testCalculateObjPoints88bis() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false), card4= drawCard(false), card5= drawCard(true);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(88,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(87,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Green);
        card2.setSeed(Green);
        card3.setSeed(Green);
        card4.setSeed(Green);
        card5.setSeed(Green);
        player.placeCard(1,1,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,3,card3);
        player.placeCard(4,4,card4);
        player.placeCard(5,5,card5);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(2, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints88ter() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1= drawCard(true), card2= drawCard(true), card3= drawCard(false), card4= drawCard(false), card5= drawCard(true), card6 = drawCard(true);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(87,null,2,null,Red,true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88,null,2,null,Green,true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2,null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Green);
        card2.setSeed(Green);
        card3.setSeed(Green);
        card4.setSeed(Green);
        card5.setSeed(Green);
        card6.setSeed(Green);
        player.placeCard(1,1,card1);
        player.placeCard(2,2,card2);
        player.placeCard(3,3,card3);
        player.placeCard(4,4,card4);
        player.placeCard(5,5,card5);
        player.placeCard(6,6,card6);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(4, objPoints);
        assertSame(0, match1.checkArrangements(player,87));
        assertSame(0, match1.checkArrangements(player,88));
        assertSame(0, match1.checkArrangements(player,89));
        assertSame(0, match1.checkArrangements(player,90));
        assertSame(0, match1.checkArrangements(player,91));
        assertSame(0, match1.checkArrangements(player,92));
        assertSame(0, match1.checkArrangements(player,93));
        assertSame(0, match1.checkArrangements(player,94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints94bis() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1 = drawCard(true), card2 = drawCard(true), card3 = drawCard(false), card4 = drawCard(false), card5 = drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(90, null, 2, null, Red, true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88, null, 2, null, Green, true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(94, null, 2, null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Blue);
        card2.setSeed(Purple);
        card3.setSeed(Purple);
        card4.setSeed(Purple);
        card5.setSeed(Purple);
        player.placeCard(1, 1, card1);
        player.placeCard(2, 2, card2);
        player.placeCard(4, 2, card3);
        player.placeCard(5, 3, card4);
        player.placeCard(6, 4, card5);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(3, objPoints);
        assertSame(0, match1.checkArrangements(player, 87));
        assertSame(0, match1.checkArrangements(player, 88));
        assertSame(0, match1.checkArrangements(player, 89));
        assertSame(0, match1.checkArrangements(player, 90));
        assertSame(0, match1.checkArrangements(player, 91));
        assertSame(0, match1.checkArrangements(player, 92));
        assertSame(0, match1.checkArrangements(player, 93));
        assertSame(0, match1.checkArrangements(player, 94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints94ter() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1 = drawCard(true), card2 = drawCard(true), card3 = drawCard(false), card4 = drawCard(false), card5 = drawCard(false), card6 = drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(94, null, 2, null, Red, true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(90, null, 2, null, Green, true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(89, null, 2, null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Blue);
        card2.setSeed(Purple);
        card3.setSeed(Purple);
        card4.setSeed(Purple);
        card5.setSeed(Purple);
        card6.setSeed(Purple);
        player.placeCard(1, 1, card1);
        player.placeCard(2, 2, card2);
        player.placeCard(4, 2, card3);
        player.placeCard(5, 3, card4);
        player.placeCard(6, 4, card5);
        player.placeCard(7, 5, card6);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(5, objPoints);
        assertSame(0, match1.checkArrangements(player, 87));
        assertSame(0, match1.checkArrangements(player, 88));
        assertSame(0, match1.checkArrangements(player, 89));
        assertSame(0, match1.checkArrangements(player, 90));
        assertSame(0, match1.checkArrangements(player, 91));
        assertSame(0, match1.checkArrangements(player, 92));
        assertSame(0, match1.checkArrangements(player, 93));
        assertSame(0, match1.checkArrangements(player, 94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @RepeatedTest(200)
    void testCalculateObjPoints94quat() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(10, 10), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1 = drawCard(true), card2 = drawCard(true), card3 = drawCard(false), card4 = drawCard(false), card5 = drawCard(false), card6 = drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(94, null, 2, null, Red, true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(88, null, 2, null, Green, true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(91, null, 2, null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Blue);
        card2.setSeed(Purple);
        card3.setSeed(Purple);
        card4.setSeed(Red);
        card5.setSeed(Red);
        card6.setSeed(Green);
        player.placeCard(1, 1, card1);
        player.placeCard(2, 2, card2);
        player.placeCard(4, 2, card3);
        player.placeCard(1, 3, card4);
        player.placeCard(3, 3, card5);
        player.placeCard(4, 4, card6);
        int objPoints = match1.calculateArrObjPoints(player);

        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(6, objPoints);
        assertSame(0, match1.checkArrangements(player, 87));
        assertSame(0, match1.checkArrangements(player, 88));
        assertSame(0, match1.checkArrangements(player, 89));
        assertSame(0, match1.checkArrangements(player, 90));
        assertSame(0, match1.checkArrangements(player, 91));
        assertSame(0, match1.checkArrangements(player, 92));
        assertSame(0, match1.checkArrangements(player, 93));
        assertSame(0, match1.checkArrangements(player, 94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }
    @Test
    void testCalculateObjPoints94quin() throws IOException {
        generateDecks();
        Player player = new Player("totti", new Token(Token.Color.Yellow), new Field(15, 15), UUID.randomUUID());
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Match match1 = new Match(players, null);
        ResourceGoldCard card1 = drawCard(true), card2 = drawCard(true), card3 = drawCard(false), card4 = drawCard(false), card5 = drawCard(false), card6 = drawCard(false);
        ResourceGoldCard card7 = drawCard(true), card8 = drawCard(true), card9 = drawCard(false), card10 = drawCard(false), card11 = drawCard(false), card12 = drawCard(false);
        ResourceGoldCard card13 = drawCard(true), card14 = drawCard(true), card15 = drawCard(false), card16 = drawCard(false);
        ObjectiveCard objCard1 = new ObjectiveCardCombo(94, null, 2, null, Red, true);
        ObjectiveCard objCard2 = new ObjectiveCardCombo(91, null, 2, null, Green, true);
        ObjectiveCard objCard3 = new ObjectiveCardCombo(88, null, 2, null, Blue, true);
        player.getPlayerDeck().setSecretObjectiveCard(objCard1);
        ArrayList<ObjectiveCard> commonObjectives = new ArrayList<>();
        commonObjectives.add(objCard2);
        commonObjectives.add(objCard3);
        match1.setCommonObjectives(commonObjectives);
        card1.setSeed(Red);
        card2.setSeed(Red);
        card3.setSeed(Red);
        card4.setSeed(Red);
        card5.setSeed(Red);
        card6.setSeed(Red);
        card7.setSeed(Green);
        card8.setSeed(Green);
        card9.setSeed(Green);
        card10.setSeed(Green);
        card11.setSeed(Green);
        card12.setSeed(Green);
        card13.setSeed(Green);
        card14.setSeed(Green);
        card15.setSeed(Green);
        card16.setSeed(Green);
        player.placeCard(0,0,card1);
        player.placeCard(2,0 , card2);
        player.placeCard(3, 1, card7);
        player.placeCard(4, 2, card8);
        player.placeCard(5, 3, card9);
        player.placeCard(6, 4, card10);
        player.placeCard(0, 5, card3);
        player.placeCard(2, 5, card4);
        player.placeCard(3, 6, card11);
        player.placeCard(4, 7, card12);
        player.placeCard(5, 8, card13);
        player.placeCard(0, 9, card5);
        player.placeCard(2, 9, card6);
        player.placeCard(3, 10, card14);
        player.placeCard(4, 11, card15);
        player.placeCard(5, 12, card16);

        player.getPlayerField().printField();

        int objPoints = Match.calculateArrObjPoints(player);

        /*System.out.println("La carta 1 in [0][0] appartiene a " + card1.getArrangements() + (card1.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card1.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 2 in [2][0] appartiene a " + card2.getArrangements() + (card2.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card2.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 3 in [0][5] appartiene a " + card3.getArrangements() + (card3.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card3.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 4 in [2][5] appartiene a " + card4.getArrangements() + (card4.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card4.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 5 in [0][9] appartiene a " + card5.getArrangements() + (card5.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card5.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 6 in [2][9] appartiene a " + card6.getArrangements() + (card6.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card6.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 7 in [3][1] appartiene a " + card7.getArrangements() + (card7.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card7.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 8 in [4][2] appartiene a " + card8.getArrangements() + (card8.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card8.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 9 in [5][3] appartiene a " + card9.getArrangements() + (card9.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card9.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 10 in [6][4] appartiene a " + card10.getArrangements() + (card10.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card10.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 11 in [3][6] appartiene a " + card11.getArrangements() + (card11.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card11.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 12in [4][7] appartiene a " + card12.getArrangements() + (card12.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card12.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 13 in [5][8] appartiene a " + card13.getArrangements() + (card13.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card13.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 14 in [3][10] appartiene a " + card14.getArrangements() + (card14.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card14.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 15 in [4][11] appartiene a " + card15.getArrangements() + (card15.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card15.getIsArranged()?" non disponibile":" disponibile"));
        System.out.println("La carta 16 in [5][12] appartiene a " + card16.getArrangements() + (card16.getArrangements()==1?" disposizione semplice": " disposizioni semplici")+ " e la carta è " + (card16.getIsArranged()?" non disponibile":" disponibile"));*/


        System.out.println("Totalizzati " + objPoints + " dalle disposizioni");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 95) + " punti dalla carta 95");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 96) + " punti dalla carta 96");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 97) + " punti dalla carta 97");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 98) + " punti dalla carta 98");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 99) + " punti dalla carta 99");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 100) + " punti dalla carta 100");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 101) + " punti dalla carta 101");
        System.out.println("Totalizzati " + Match.calculateSimpleObjPoints(player, 102) + " punti dalla carta 102");

        assertSame(11, objPoints);
        assertSame(0, match1.checkArrangements(player, 87));
        assertSame(0, match1.checkArrangements(player, 88));
        assertSame(0, match1.checkArrangements(player, 89));
        assertSame(0, match1.checkArrangements(player, 90));
        assertSame(0, match1.checkArrangements(player, 91));
        assertSame(0, match1.checkArrangements(player, 92));
        assertSame(0, match1.checkArrangements(player, 93));
        assertSame(0, match1.checkArrangements(player, 94));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 95));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 96));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 97));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 98));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 99));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 100));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 101));
        assertNotSame(-1, match1.calculateSimpleObjPoints(player, 102));

    }

}