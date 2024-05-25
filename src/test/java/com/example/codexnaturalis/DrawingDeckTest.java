package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DrawingDeckTest {

    @Test
    void checkDeckEmptiness() throws IOException {

        Player player = new Player(new Token(), new Field(5, 5));
        DrawingDeck deck = new DrawingDeck();

        deck.generateDecks();
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
}