package com.example.codexnaturalis;

import java.util.ArrayList;

public class GoldCard extends ResourceGoldCard {

    private int points;
    private ArrayList<Resource> requiredResources;

    //TODO: aggiungere l'attributo requisitiPunti


    public GoldCard(int idCarta, String artRef, ArrayList<Corner> frontCorners, Resource backCentreResource, int points, ArrayList<Resource> requiredResources) {
        super(idCarta, artRef, frontCorners, backCentreResource);
        this.points = points;
        this.requiredResources = requiredResources;
    }
}
