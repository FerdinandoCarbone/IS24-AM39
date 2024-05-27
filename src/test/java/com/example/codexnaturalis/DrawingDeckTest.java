package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DrawingDeckTest {

    @Test
    void checkDeckEmptiness() throws IOException {
        DrawingDeck deck = new DrawingDeck();
        assertFalse( deck.checkDeckEmptiness(1) );
        assertFalse( deck.checkDeckEmptiness(2) );

        while (deck.getTotalResourceCard().size() > 0) {
            ResourceGoldCard card = deck.drawCard(true);
        }
        while (deck.getTotalGoldCard().size() > 0) {
            ResourceGoldCard card = deck.drawCard(false);
        }

        assertTrue( deck.checkDeckEmptiness(1) );
        assertTrue( deck.checkDeckEmptiness(2) );
    }

    @Test
    void drawTwoObjectiveCardsTest() throws IOException {
        DrawingDeck deck = new DrawingDeck();
        int previousDeckSize = deck.getTotalObjectiveCards().size();
        deck.drawTwoObjectiveCards();
        assertEquals(previousDeckSize-2, deck.getTotalObjectiveCards().size());
    }

    @Test
    void drawCommonObjectiveTest() throws IOException {
        DrawingDeck deck = new DrawingDeck();
        int previousDeckSize = deck.getTotalObjectiveCards().size();
        deck.drawCommonObjective();
        assertEquals(previousDeckSize-2, deck.getTotalObjectiveCards().size());
    }

    @Test
    void reAddSecretObjectiveCardTest() throws IOException {
        DrawingDeck deck = new DrawingDeck();
        int previousDeckSize = deck.getTotalObjectiveCards().size();
        ArrayList<ObjectiveCard> list = deck.drawTwoObjectiveCards();
        assertEquals(deck.getTotalObjectiveCards().size(), previousDeckSize - 2 );
        deck.reAddSecretObjectiveCard(list.getFirst());
        assertEquals(deck.getTotalObjectiveCards().size(), previousDeckSize - 1);
    }

    @Test
    void drawCardTest() throws IOException {
        DrawingDeck deck = new DrawingDeck();
        int previousResourceDeckSize = deck.getTotalResourceCard().size();
        int previousGoldDeckSize = deck.getTotalGoldCard().size();
        deck.drawCard(true);
        deck.drawCard(false);
        assertEquals(previousResourceDeckSize-1, deck.getTotalResourceCard().size());
        assertEquals(previousGoldDeckSize-1, deck.getTotalGoldCard().size());

    }
}