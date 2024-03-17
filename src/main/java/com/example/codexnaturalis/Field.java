package com.example.codexnaturalis;

import java.io.PrintWriter;

public class Field {
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

    /**
     * Dati la riga r e la colonna c, controlla se lo slot [r][c] è occupato, in caso printa la toString() della carta occupante
     * @param r riga dello slot
     * @param c colonna dello slot
     */
    public void cardAnalysis(int r, int c, PrintWriter out) {
        if (slots[r][c].isBusySlot() == false) {
            out.println("ERORRE: SLOT NON OCCUPATO DA NESSUNA CARTA.");
            return;
        }
        NonObjectiveCard carta = slots[r][c].getCardSlot();
        out.println("Analisi della carta nello slot [" + r + "][" + c + "].");
        out.println("idCard: " + carta.getIdCard());
        if (carta.isPlacedFront()) {
            carta.printFrontCorners(out);
        } else {
            carta.printBackCorners(out);
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

    public static class Slot {
        /**
         * Boolean that defines whether the slot is busy or not
         */
        private boolean busySlot = false;
        /**
         * Defines the card contained in the slot if busy
         */
        private NonObjectiveCard cardSlot = null;

        /**
         * Analysis of the slot
         */
        public void analysisSlot() {
            if (busySlot) {
                System.out.println("Lo slot è occupato da una carta");
                cardSlot.toString();
            } else {
                System.out.println("Lo slot è libero");
            }
        }

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
