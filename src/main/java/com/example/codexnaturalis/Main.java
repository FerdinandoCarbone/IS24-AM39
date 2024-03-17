package com.example.codexnaturalis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {

        Player zak = new Player("Zak", new Token(), new Field(5, 5));
        Player giorgio = new Player("giorgio", new Token(), new Field(5, 5));
        NewMatch match = new NewMatch(new ArrayList<>(Arrays.asList(zak, giorgio)), new ScoreTracker());
        match.startMatch();

    }
}