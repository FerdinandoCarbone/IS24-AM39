package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ResourceCardTest {

    @Test
    void checkAvailableFrontCorner() throws Exception {

        ResourceCard card = new ResourceCard(1, new String[]{""}, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Mushroom),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty)
        )), 10, Seed.Red);


        assertTrue(card.checkAvailableCorner(0));
        assertFalse(card.checkAvailableCorner(2));
        assertTrue(card.checkAvailableCorner(3));
        assertThrows(Exception.class, () -> card.checkAvailableCorner(4));
        assertThrows(Exception.class, () -> card.checkAvailableCorner(-1));
    }

    @Test
    void checkAvailableBackCorner() throws Exception {

        ResourceCard card = new ResourceCard(1, new String[]{""}, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Mushroom),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty)
        )), 10, Seed.Red);
        card.setIsPlacedFront(false);


        assertTrue(card.checkAvailableCorner(0));
        assertTrue(card.checkAvailableCorner(2));
        assertTrue(card.checkAvailableCorner(3));
    }


    @Test
    void updateFrontCorner() throws Exception {
        ResourceCard card = new ResourceCard(1, new String[]{""}, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Mushroom),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty)
        )), 10, Seed.Red);

        assertThrows(Exception.class, () -> card.updateCorner(10));
        assertThrows(Exception.class, () -> card.updateCorner(-1));
        assertTrue(card.getFrontCorners().get(0).isAvailableCorner());
        card.updateCorner(0);
        assertFalse(card.getFrontCorners().get(0).isAvailableCorner());
    }

    @Test
    void updateBackCorner() throws Exception {
        ResourceCard card = new ResourceCard(1, new String[]{""}, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Mushroom),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty)
        )), 10, Seed.Red);
        card.setIsPlacedFront(false);

        assertTrue(card.getBackCorners().get(0).isAvailableCorner());
        card.updateCorner(0);
        assertFalse(card.getBackCorners().get(0).isAvailableCorner());
    }

}