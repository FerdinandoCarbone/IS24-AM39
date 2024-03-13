package com.example.codexnaturalis;

import java.util.ArrayList;

public class ResourceCard extends ResourceGoldCard {
    public ResourceCard(int idCard, String artRef, ArrayList<Corner> frontCorners, Resource backCentreResource) {
        super(idCard, artRef, frontCorners, backCentreResource);
    }
}
