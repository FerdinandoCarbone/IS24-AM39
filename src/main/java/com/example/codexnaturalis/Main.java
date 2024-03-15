package com.example.codexnaturalis;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        Player p1 = new Player("Giorgio", new Token("Rosso"), new Field(5, 5));
        Player p2 = new Player("Matteo", new Token("Rosso"), new Field(5, 5));
        NewMatch match = new NewMatch(new ArrayList<>(Arrays.asList(p1, p2)), new ScoreTracker());
        match.startMatch();
    }

}