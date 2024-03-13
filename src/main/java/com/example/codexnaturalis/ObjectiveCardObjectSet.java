package com.example.codexnaturalis;

import java.util.ArrayList;

public class ObjectiveCardObjectSet extends ObjectiveCard {

    private ArrayList<ObjectE> objectSet;

    public ObjectiveCardObjectSet(int idCarta, String artRef, int points, ArrayList<ObjectE> objectSet) {
        super(idCarta, artRef, points);
        this.objectSet = objectSet;
    }
}
