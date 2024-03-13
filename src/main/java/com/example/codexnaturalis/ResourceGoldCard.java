package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class ResourceGoldCard extends NonObjectiveCard {


    private Resource backCentreResource;

    public ResourceGoldCard(int idCard, String artRef, ArrayList<Corner> frontCorners, Resource backCentreResource) {
        super(idCard, artRef, frontCorners, new ArrayList<>(Arrays.asList(
                new Corner(1, null),
                new Corner(1, null),
                new Corner(1, null),
                new Corner(1, null)
        )));
        this.backCentreResource = backCentreResource;
    }
}
