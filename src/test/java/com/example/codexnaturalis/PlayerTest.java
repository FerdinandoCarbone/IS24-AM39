package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void placeStarterCardFront() throws IOException {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player("pippo", new Token(Token.Color.Red), new Field(3, 3), UUID.randomUUID());
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        StarterCard card = player.getPlayerDeck().getStarterCard();
        ArrayList<ResourceGoldCard.ResourceElement> resourceElementsInCard = new ArrayList<>();
        for (Corner c : card.getCorners()) {
            if (c.isAvailableCorner() && !c.getResourceElement().equals(ResourceGoldCard.ResourceElement.empty)) {
                resourceElementsInCard.add(c.getResourceElement());
            }
        }

        int[] resourceMana = new int[4];
        int[] elementsMana = new int[3];

        for (ResourceGoldCard.ResourceElement r : resourceElementsInCard) {
            switch (r) {
                case Mushroom -> resourceMana[0]++;
                case Leaf -> resourceMana[1]++;
                case Wolf -> resourceMana[2]++;
                case Butterfly -> resourceMana[3]++;
                case Ink -> elementsMana[0]++;
                case Papyrus -> elementsMana[1]++;
                case Feather -> elementsMana[2]++;
            }
        }

        Field field = player.getPlayerField();
        int r, c;
        r = c = field.getSlots().length/2;
        player.placeStarterCard(true);
        assertTrue(card.isPlacedFront());
        assertTrue(field.getSlots()[r][c].isBusySlot());
        assertEquals(card, field.getSlots()[r][c].getCardSlot());
        for (int i = 0; i < 4; i++) {
            assertEquals(resourceMana[i], player.getResourceMana()[i]);
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(resourceMana[i], player.getResourceMana()[i]);
        }
    }

    @Test
    void placeStarterCardBack() throws IOException {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player("pluto", new Token(Token.Color.Red), new Field(3, 3), UUID.randomUUID());
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        StarterCard card = player.getPlayerDeck().getStarterCard();
        card.printCardFrontAndBack();
        ArrayList<ResourceGoldCard.ResourceElement> resourceElementsInCard = new ArrayList<>();

        for (ResourceGoldCard.ResourceElement r : card.getBackCentreResources()) {
            resourceElementsInCard.add(r);
        }
        for (Corner c : card.getBackCorners()) {
            if (c.isAvailableCorner() && !c.getResourceElement().equals(ResourceGoldCard.ResourceElement.empty)) {
                resourceElementsInCard.add(c.getResourceElement());
            }
        }

        int[] resourceMana = new int[4];
        int[] elementsMana = new int[3];

        for (ResourceGoldCard.ResourceElement r : resourceElementsInCard) {
            switch (r) {
                case Mushroom -> resourceMana[0]++;
                case Leaf -> resourceMana[1]++;
                case Wolf -> resourceMana[2]++;
                case Butterfly -> resourceMana[3]++;
                case Ink -> elementsMana[0]++;
                case Papyrus -> elementsMana[1]++;
                case Feather -> elementsMana[2]++;
            }
        }

        Field field = player.getPlayerField();
        int r, c;
        r = c = field.getSlots().length/2;
        player.placeStarterCard(false);
        player.printManas();
        assertFalse(card.isPlacedFront());
        assertTrue(field.getSlots()[r][c].isBusySlot());
        assertEquals(card, field.getSlots()[r][c].getCardSlot());
        for (int i = 0; i < 4; i++) {
            assertEquals(resourceMana[i], player.getResourceMana()[i]);
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(resourceMana[i], player.getResourceMana()[i]);
        }
    }

    @Test
    void placeCardSwitchOffset1() throws Exception {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(5, 5));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(3, 3, card);
        System.out.println(player.getPlayerField().getSlots()[3][3].isBusySlot());
        assertTrue(player.getPlayerField().getSlots()[3][3].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[3][3].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }

    @Test
    void placeCardOffset2() throws Exception {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(0, 2, card);
        assertTrue(player.getPlayerField().getSlots()[0][2].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[0][2].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }

    @Test
    void placeCardCornerToPlace2AndRemoveFromDeck() throws Exception {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(2, 2, card);
        assertTrue(player.getPlayerField().getSlots()[2][2].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[2][2].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }

    @Test
    void placeCardCornerToPlace4AndRemoveFromDeck() throws Exception {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(0, 0, card);
        assertTrue(player.getPlayerField().getSlots()[0][0].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[0][0].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }

    @Test
    void addScoreResourceElementTest() throws IOException {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        int oldPoints = player.getScore();
        player.addScore(1);
        assertEquals(player.getScore(), oldPoints + 1);
        int randomIndex1 = new Random().nextInt(4);
        int oldMana1 = player.getResourceMana()[randomIndex1];
        player.addResourceMana(1, randomIndex1);
        assertEquals(player.getResourceMana()[randomIndex1], oldMana1 + 1);
        int randomIndex2 = new Random().nextInt(3);
        int oldMana2 = player.getElementsMana()[randomIndex2];
        player.addElementsMana(1, randomIndex2);
        assertEquals(player.getElementsMana()[randomIndex2], oldMana2 + 1);
    }

    @Test
    void isCardAttachableToSlotTest() throws IOException {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(5, 5));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        ResourceGoldCard cardToPlace = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Ink),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(false, ResourceGoldCard.ResourceElement.empty)
        )), 10, Seed.Red);

        player.getPlayerField().getSlots()[2][2].setCardSlot(cardToPlace);
        player.getPlayerField().getSlots()[2][2].setBusySlot(true);
        assertTrue(player.isCardAttachableToSlot(1, 3));
        assertTrue(player.isCardAttachableToSlot(3, 3));
        assertFalse(player.isCardAttachableToSlot(1, 1));
        assertFalse(player.isCardAttachableToSlot(3, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> player.isCardAttachableToSlot(-1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> player.isCardAttachableToSlot(0, 9));

    }

    @Test
    void decreaseResourceElementsManaTest() throws IOException {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(5, 5));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        player.placeStarterCard(true);
        for (Corner c : player.getPlayerDeck().getStarterCard().getCorners()) {
            player.decreaseResourceElementsMana(c);
        }
        assertEquals(0, player.getResourceMana()[0]);
        assertEquals(0, player.getResourceMana()[1]);
        assertEquals(0, player.getResourceMana()[2]);
        assertEquals(0, player.getResourceMana()[3]);
        assertEquals(0, player.getElementsMana()[0]);
        assertEquals(0, player.getElementsMana()[1]);
        assertEquals(0, player.getElementsMana()[2]);

    }

    @Test
    void undoMoveTest() throws IOException {
        DrawingDeck drawingDeck = new DrawingDeck();
        Player player = new Player(new Token(Token.Color.Red), new Field(5, 5));
        player.setPlayerDeck(drawingDeck.generatePlayerDeck());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(player.getPlayerDeck().getStarterCard());
        cards.addAll(player.getPlayerDeck().getResourceGoldCards());
        cards.add(player.getPlayerDeck().getSecretObjectiveCard());

        player.placeStarterCard(true);
        ResourceGoldCard cardToPlace = player.getPlayerDeck().getResourceGoldCards().getFirst();
        player.placeCardAndRemoveFromDeck(3, 3, cardToPlace);
        int prevScore = player.getScore();
        player.undoMove(3, 3, 0, prevScore);

        assertEquals(prevScore, player.getScore());
        assertEquals(player.getPlayerDeck().getResourceGoldCards().getFirst(), cardToPlace);
        assertNull(player.getPlayerField().getSlots()[3][3].getCardSlot());
        assertFalse(player.getPlayerField().getSlots()[3][3].isBusySlot());

    }

}