package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GoldDeck implements canShuffle{
    private ArrayList<GoldCard> goldCards = new ArrayList<>(Arrays.asList(
    ));

    public void shuffleDeck() {
        Random rand = new Random();
        for (int i = 0; i < goldCards.size(); i++) {
            int randomIndex = rand.nextInt(goldCards.size());
            GoldCard tmpCarta = goldCards.get(randomIndex);
            goldCards.set(randomIndex, goldCards.get(i));
            goldCards.set(i, tmpCarta);
        }
        System.out.println("Mazzo Mischiato");
    }

    public void printDeck() {
        System.out.println("Gold deck:");
        for (int i = 0; i < goldCards.size(); i++) {
            System.out.println(i + ") [" + goldCards.get(i).getClass() + "]");
        }
    }

    public ArrayList<GoldCard> getGoldCards() {
        return goldCards;
    }


}
