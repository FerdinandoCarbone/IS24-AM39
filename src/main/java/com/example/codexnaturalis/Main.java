package com.example.codexnaturalis;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Player p1 = new Player("Giorgio", new Token("Rosso"), new Field(5, 5));
        p1.getPlayerDeck().getStarterCard().printFrontCorners();
    }
}