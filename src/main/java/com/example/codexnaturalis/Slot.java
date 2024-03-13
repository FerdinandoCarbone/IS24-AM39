package com.example.codexnaturalis;

public class Slot {

    private int busySlot;
    private NonObjectiveCard cardSlot;

    public Slot() {
        this.busySlot = 0;
    }

    public void analisiSlot() {
        if (busySlot == 1) {
            System.out.println("Lo slot è occupato da una carta");
            cardSlot.toString();
        } else {
            System.out.println("Lo slot è libero");
        }
    }

    //GETTER
    public int getBusySlot() {
        return busySlot;
    }
    public NonObjectiveCard getCardSlot() {
        return cardSlot;
    }

    //SETTER
    public void setBusySlot(int busySlot) {
        this.busySlot = busySlot;
    }

    public void setCardSlot(NonObjectiveCard cardSlot) {
        this.cardSlot = cardSlot;
    }
}
