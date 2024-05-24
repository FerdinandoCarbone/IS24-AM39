package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DrawingDeckTest {

    @Test
    void checkDeckEmptiness() throws IOException {
        DrawingDeck.generateDecks();
        assertFalse( DrawingDeck.checkDeckEmptiness(1) );
        assertFalse( DrawingDeck.checkDeckEmptiness(2) );

        while (DrawingDeck.getTotalResourceCard().size() > 0) {
            ResourceGoldCard card = DrawingDeck.drawCard(true);
        }
        while (DrawingDeck.getTotalGoldCard().size() > 0) {
            ResourceGoldCard card = DrawingDeck.drawCard(false);
        }

        assertTrue( DrawingDeck.checkDeckEmptiness(1) );
        assertTrue( DrawingDeck.checkDeckEmptiness(2) );
    }

    @Test
    void drawTwoObjectiveCardsTest() throws IOException {
        DrawingDeck.generateDecks();
        int previousDeckSize = DrawingDeck.getTotalObjectiveCards().size();
        DrawingDeck.drawTwoObjectiveCards();
        assertEquals(previousDeckSize-2, DrawingDeck.getTotalObjectiveCards().size());
    }

    @Test
    void drawCommonObjectiveTest() throws IOException {
        DrawingDeck.generateDecks();
        int previousDeckSize = DrawingDeck.getTotalObjectiveCards().size();
        DrawingDeck.drawCommonObjective();
        assertEquals(previousDeckSize-2, DrawingDeck.getTotalObjectiveCards().size());
    }

    @Test
    void reAddSecretObjectiveCardTest() throws IOException {
        DrawingDeck.generateDecks();
        int previousDeckSize = DrawingDeck.getTotalObjectiveCards().size();
        ArrayList<ObjectiveCard> list = DrawingDeck.drawTwoObjectiveCards();
        assertEquals(DrawingDeck.getTotalObjectiveCards().size(), previousDeckSize - 2 );
        DrawingDeck.reAddSecretObjectiveCard(list.getFirst());
        assertEquals(DrawingDeck.getTotalObjectiveCards().size(), previousDeckSize - 1);
    }

    @Test
    void drawCardTest() throws IOException {
        DrawingDeck.generateDecks();
        int previousResourceDeckSize = DrawingDeck.getTotalResourceCard().size();
        int previousGoldDeckSize = DrawingDeck.getTotalGoldCard().size();
        DrawingDeck.drawCard(true);
        DrawingDeck.drawCard(false);
        assertEquals(previousResourceDeckSize-1, DrawingDeck.getTotalResourceCard().size());
        assertEquals(previousGoldDeckSize-1, DrawingDeck.getTotalGoldCard().size());

    }
}