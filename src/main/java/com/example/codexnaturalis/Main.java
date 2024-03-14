package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        Player p1 = new Player("Giorgio", new Token("Rosso"), new Field(5, 5));
        Match match = new Match(new ArrayList<>(Arrays.asList(
                p1
        )), new ScoreTracker());

        match.startMatch();

    }
}