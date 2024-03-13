package com.example.codexnaturalis;

import java.util.ArrayList;

public class ScoreTracker {
    private ArrayList<ScoreTrackerSlot> slots = new ArrayList<>(30);

    public void printScoreTracker() {
        for (int i = 0; i < slots.size(); i++) {
            System.out.println(i + ") [" + slots.get(i).getBusySlot() + "]");
        }
    }

    public static class ScoreTrackerSlot {
        private int busySlot = 0;
        private ArrayList<Token> tokensInSlot;

        public ScoreTrackerSlot(int busySlot, ArrayList<Token> tokensInSlot) {
            this.busySlot = busySlot;
            this.tokensInSlot = tokensInSlot;
        }

        public int getBusySlot() {
            return busySlot;
        }

        public ArrayList<Token> getTokensInSlot() {
            return tokensInSlot;
        }
    }
}
