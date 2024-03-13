package com.example.codexnaturalis;

import java.util.ArrayList;

public class StarterCard extends NonObjectiveCard {

    private ArrayList<Resource> backCentreResources;

    public StarterCard(int idCarta, String artRef, ArrayList<Corner> frontCorners, ArrayList<Corner> backCorners, ArrayList<Resource> backCentreResources) {
        super(idCarta, artRef, frontCorners, backCorners);
        this.backCentreResources = backCentreResources;
    }
}
