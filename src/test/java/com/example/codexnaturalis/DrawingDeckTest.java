package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.io.IOException;

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
}