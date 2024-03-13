package com.example.codexnaturalis;

public class Corner {

    private boolean availableCorner;
    private ResourceElement resourceObject;

    public Corner(boolean availableCorner, ResourceElement risorsa) {
        this.availableCorner = availableCorner;
        this.resourceObject = risorsa;
    }

    public void setAvailableCorner(boolean availableCorner) {
        this.availableCorner = availableCorner;
    }

    public boolean getAvailableCorner() {
        return availableCorner;
    }

    public ResourceElement getRisorsa() {
        return resourceObject;
    }
}
