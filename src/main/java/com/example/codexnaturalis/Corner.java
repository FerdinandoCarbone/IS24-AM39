package com.example.codexnaturalis;

public class Corner {

    private int availableCorner;
    private RisorsaOggetto resourceObject;

    public Corner(int availableCorner, RisorsaOggetto risorsa) {
        this.availableCorner = availableCorner;
        this.resourceObject = risorsa;
    }

    public void setAvailableCorner(int availableCorner) {
        this.availableCorner = availableCorner;
    }

    public int getAvailableCorner() {
        return availableCorner;
    }

    public RisorsaOggetto getRisorsa() {
        return resourceObject;
    }
}
