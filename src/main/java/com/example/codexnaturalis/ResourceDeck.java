package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ResourceDeck implements canShuffle{

    public ArrayList<ResourceCard> resourceCards = new ArrayList<>(Arrays.asList(
    ));

    public void shuffleDeck() {
        Random rand = new Random();
        for (int i = 0; i < resourceCards.size(); i++) {
            int randomIndex = rand.nextInt(resourceCards.size());
            ResourceCard tmpCarta = resourceCards.get(randomIndex);
            resourceCards.set(randomIndex, resourceCards.get(i));
            resourceCards.set(i, tmpCarta);
        }
        System.out.println("Deck Shuffled");
    }

    public void printMazzo() {
        System.out.println("Resource Cards:");
        for (int i = 0; i < resourceCards.size(); i++) {
            System.out.println(i + ") [" + resourceCards.get(i).getClass() + "]");
        }
    }

    public ArrayList<ResourceCard> getResourceCards() {
        return resourceCards;
    }
}
