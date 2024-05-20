package com.example.codexnaturalis;

import java.io.Serializable;

public class Field implements Serializable {
    /**
     * Matrix containing slots
     */
    private final Slot[][] slots;
    private final int r;
    private final int c;

    /**
     * Constructor of Field
     * @param r number of Rows
     * @param c number of Columns
     */
    public Field(int r, int c) {
        this.r = r;
        this.c = c;
        this.slots = new Slot[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                slots[i][j] = new Slot();
            }
        }
    }

    public void printField() {
        for (int i = 0; i < getR(); i++) {
            for (int j = 0; j < getC(); j++) {
                System.out.print("[" + (getSlots()[i][j].isBusySlot() ? (getSlots()[i][j].getCardSlot().isPlacedFront()? "1" : "0") : "-") + "]");
            }
            System.out.println();
        }
    }


    public Slot[][] getSlots() {
        return slots;
    }

    public int getR() {
        return r;
    }

    public int getC() {
        return c;
    }

    public static class Slot implements Serializable{
        /**
         * Boolean that defines whether the slot is busy or not
         */
        private boolean busySlot = false;
        /**
         * Defines the card contained in the slot if busy
         */
        private NonObjectiveCard cardSlot = null;

        /**
         * Getter of busySlot
         * @return boolean, true if slot is busy, otherwise false
         */
        public boolean isBusySlot() {
            return busySlot;
        }

        /**
         * Getter of CardSlot
         * @return NonObjectiveCard contained in the busySlot
         */
        public NonObjectiveCard getCardSlot() {
            return cardSlot;
        }
        /**
         * Getter of CardSlot
         * @return NonObjectiveCard contained in the busySlot
         */
        public ResourceGoldCard getRGCardSlot() {
            return (ResourceGoldCard) cardSlot;
        }

        /**
         * Setter of busySlot
         * @param busySlot: defines whether the slot is busy or not
         */
        public void setBusySlot(boolean busySlot) {
            this.busySlot = busySlot;
        }

        /**
         * Setter oo cardSlot
         * @param cardSlot: defines the card contained in the slot if busy
         */
        public void setCardSlot(NonObjectiveCard cardSlot) {
            this.cardSlot = cardSlot;
        }
    }

}
