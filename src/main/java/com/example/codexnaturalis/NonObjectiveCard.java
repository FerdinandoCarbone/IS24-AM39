package com.example.codexnaturalis;

import java.util.ArrayList;

public abstract class NonObjectiveCard extends Card {

    private ArrayList<Corner> frontCorners;
    private ArrayList<Corner> backCorners;
    private int isPlacedFront = 1;

    public NonObjectiveCard(int idCarta, String artRef, ArrayList<Corner> frontCorners, ArrayList<Corner> backCorners) {
        super(idCarta, artRef);
        this.frontCorners = frontCorners;
        this.backCorners = backCorners;
    }

    /**
     * Printa a console gli angoli frontali della carta con [0] se non è disponibile e [1] se disponibile
     */
    public void printFrontCorners() {
        System.out.println("[" + frontCorners.get(3).getAvailableCorner() + "][" + frontCorners.get(0).getAvailableCorner() + "]");
        System.out.println("[" + frontCorners.get(2).getAvailableCorner() + "][" + frontCorners.get(1).getAvailableCorner() + "]");
    }

    /**
     * Printa a console gli angoli posteriori della carta con [0] se non è disponibile e [1] se disponibile
     */
    public void printBackCorners() {
        System.out.println("[" + backCorners.get(3).getAvailableCorner() + "][" + backCorners.get(0).getAvailableCorner() + "]");
        System.out.println("[" + backCorners.get(2).getAvailableCorner() + "][" + backCorners.get(1).getAvailableCorner() + "]");
    }

    /**
     * Ritorna 1 se la carta è piazzata frontalmente sul tavolo, 0 altrimenti
     * @return
     */
    public int getPiazzataInFronte() {
        return isPlacedFront;
    }

    public ArrayList<Corner> getFrontCorners() {
        return frontCorners;
    }

    public ArrayList<Corner> getBackCorners() {
        return backCorners;
    }

    public void setIsPlacedFront(int isPlacedFront) {
        this.isPlacedFront = isPlacedFront;
    }
}
