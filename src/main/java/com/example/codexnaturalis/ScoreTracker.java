package com.example.codexnaturalis;

import java.util.ArrayList;

/**
 * Score Tracker of the game for all players
 */
public class ScoreTracker {
    /**
     * Arraylist of all slots of the score tracker
     */
    private ArrayList<ScoreTrackerSlot> slots = new ArrayList<>(30);

    /**
     * Prints all slots in the score tracker and whether they are busy [1] or not [0]
     */
    public void printScoreTracker() {
        for (int i = 0; i < slots.size(); i++) {
            System.out.println(i + ") [" + (slots.get(i).isBusySlot()? "1" : "0") + "]");
        }
    }

    /**
     * Slots of the scoreTracker
     */
    public static class ScoreTrackerSlot {
        /**
         * Defines whether the slot is busy or not
         */
        private boolean busySlot = false;
        /**
         * Defines all the tokens in the slot if busy
         */
        private ArrayList<Token> tokensInSlot;

        /**
         * Constructor of ScoreTrackerSlot
         * @param busySlot: Defines whether the slot is busy or not
         * @param tokensInSlot: Defines all the tokens in the slot if busy
         */
        public ScoreTrackerSlot(boolean busySlot, ArrayList<Token> tokensInSlot) {
            this.busySlot = busySlot;
            this.tokensInSlot = tokensInSlot;
        }

        /**
         * Getter of busySlot
         * @return boolean, true if slot is busy, otherwise false
         */
        public boolean isBusySlot() {
            return busySlot;
        }

        /**
         * Getter of the tokens in a slot
         * @return ArrayList containing all the tokens in a slot
         */
        public ArrayList<Token> getTokensInSlot() {
            return tokensInSlot;
        }
    }
}
