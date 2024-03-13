package com.example.codexnaturalis;

import java.util.ArrayList;

public class ObjectiveCardResourceSet extends ObjectiveCard{

    private ArrayList<Resource> resouceSet;

    public ObjectiveCardResourceSet(int idCarta, String artRef, int points, ArrayList<Resource> resouceSet) {
        super(idCarta, artRef, points);
        this.resouceSet = resouceSet;
    }
}
