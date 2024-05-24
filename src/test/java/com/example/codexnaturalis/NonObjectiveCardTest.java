package com.example.codexnaturalis;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NonObjectiveCardTest {

    @Test
    void checkAvailableCornerTest() throws Exception {
        NonObjectiveCard testCard = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf)
        )), 10, Seed.Green);

        assertTrue(testCard.checkAvailableCorner(0));
        assertTrue(testCard.checkAvailableCorner(1));
        assertFalse(testCard.checkAvailableCorner(2));
        assertTrue(testCard.checkAvailableCorner(3));
    }

    @Test
    void checkAvailableCornerIOBException() {
        NonObjectiveCard testCard = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf)
        )), 10, Seed.Green);

        assertThrows(IndexOutOfBoundsException.class, () -> testCard.checkAvailableCorner(4));
    }

    @Test
    void updateCornerToBusyTest() throws IndexOutOfBoundsException {
        NonObjectiveCard testCard = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf)
        )), 10, Seed.Green);

        testCard.updateCornerToBusy(0);
        assertFalse(testCard.getCorners().get(0).isAvailableCorner());
    }

    @Test
    void updateCornerToBusyIOBException() throws IndexOutOfBoundsException {
        NonObjectiveCard testCard = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf)
        )), 10, Seed.Green);

        assertThrows(IndexOutOfBoundsException.class, () -> testCard.updateCornerToBusy(4));
    }

    @Test
    void updateCornerToFreeTest() throws IndexOutOfBoundsException {
        NonObjectiveCard testCard = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf)
        )), 10, Seed.Green);

        testCard.updateCornerToFree(2);
        assertTrue(testCard.getCorners().get(2).isAvailableCorner());
    }

    @Test
    void updateCornerToFreeIOBException() throws IndexOutOfBoundsException {
        NonObjectiveCard testCard = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.Leaf)
        )), 10, Seed.Green);

        assertThrows(IndexOutOfBoundsException.class, () -> testCard.updateCornerToFree(4));
    }

    @Test
    void allCornersAvailableTest() {
        NonObjectiveCard testCard = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty)
        )), 10, Seed.Green);
        NonObjectiveCard testCard2 = new ResourceCard(10, null, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceGoldCard.ResourceElement.Ink),
                new Corner(true, ResourceGoldCard.ResourceElement.empty),
                new Corner(false, ResourceGoldCard.ResourceElement.empty),
                new Corner(true, ResourceGoldCard.ResourceElement.empty)
        )), 10, Seed.Green);

        assertTrue(testCard.allCornersAvailable());
        assertFalse(testCard2.allCornersAvailable());
    }


}