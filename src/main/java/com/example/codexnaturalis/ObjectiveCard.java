package com.example.codexnaturalis;

public abstract class ObjectiveCard extends Card {
    private int points;

    public ObjectiveCard(int idCarta, String artRef, int points) {
        super(idCarta, artRef);
        this.points = points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
