package com.example.codexnaturalis;

import java.util.ArrayList;

public class ScoreTrackerSlot {
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
