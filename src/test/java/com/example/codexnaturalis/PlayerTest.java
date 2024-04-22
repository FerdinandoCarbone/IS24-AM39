package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void placeStarterCardFront() throws IOException {
        Player player = new Player("pippo", new Token(Token.Color.Red), new Field(3, 3), UUID.randomUUID());
        StarterCard card = player.getPlayerDeck().getStarterCard();
        Field field = player.getPlayerField();
        int r, c;
        r = c = field.getSlots().length/2;
        player.placeStarterCard(true);
        assertTrue(card.isPlacedFront());
        assertTrue(field.getSlots()[r][c].isBusySlot());
        assertEquals(card, field.getSlots()[r][c].getCardSlot());
        assertNull(player.getPlayerDeck().getStarterCard());
    }

    @Test
    void placeStarterCardBack() throws IOException {
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        StarterCard card = player.getPlayerDeck().getStarterCard();
        Field field = player.getPlayerField();
        int r, c;
        r = c = field.getSlots().length/2;
        player.placeStarterCard(false);
        assertFalse(card.isPlacedFront());
        assertTrue(field.getSlots()[r][c].isBusySlot());
        assertEquals(card, field.getSlots()[r][c].getCardSlot());
        assertNull(player.getPlayerDeck().getStarterCard());

    }

    @Test
    void placeCardSwitchOffset1() throws Exception {
        Player player = new Player(new Token(Token.Color.Red), new Field(5, 5));
        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        int randomAngolo = 0;
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(1, 3, card);
        assertTrue(player.getPlayerField().getSlots()[1][3].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[1][3].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }
    @Test
    void placeCardOffset2() throws Exception {
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        int randomAngolo = 2;
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(0, 2, card);
        assertTrue(player.getPlayerField().getSlots()[0][2].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[0][2].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }
    @Test
    void placeCardCornerToPlace2AndRemoveFromDeck() throws Exception {
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        int randomAngolo = 1;
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(2, 2, card);
        assertTrue(player.getPlayerField().getSlots()[2][2].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[2][2].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }
    @Test
    void placeCardCornerToPlace4AndRemoveFromDeck() throws Exception {
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        ResourceGoldCard card = player.getPlayerDeck().getResourceGoldCards().get((new Random()).nextInt(2));
        int originalDeckSize = player.getPlayerDeck().getResourceGoldCards().size();
        int randomAngolo = 3;
        player.placeStarterCard(true);
        player.placeCardAndRemoveFromDeck(0, 0, card);
        assertTrue(player.getPlayerField().getSlots()[0][0].isBusySlot());
        assertEquals(card, player.getPlayerField().getSlots()[0][0].getCardSlot());
        assertEquals(originalDeckSize - 1, player.getPlayerDeck().getResourceGoldCards().size());
    }

    @Test
    void addScore() throws IOException {
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        int oldPoints = player.getScore();
        player.addScore(1);
        assertEquals(player.getScore(), oldPoints + 1);
    }

    @Test
    void addResourceMana() throws IOException {
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        int randomIndex = new Random().nextInt(4);
        int oldMana = player.getResourceMana()[randomIndex];
        player.addResourceMana(1, randomIndex);
        assertEquals(player.getResourceMana()[randomIndex], oldMana + 1);
    }

    @Test
    void addElementsMana() throws IOException {
        Player player = new Player(new Token(Token.Color.Red), new Field(3, 3));
        int randomIndex = new Random().nextInt(3);
        int oldMana = player.getElementsMana()[randomIndex];
        player.addElementsMana(1, randomIndex);
        assertEquals(player.getElementsMana()[randomIndex], oldMana + 1);
    }
}