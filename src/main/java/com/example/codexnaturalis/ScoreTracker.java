package com.example.codexnaturalis;

import java.util.ArrayList;

public class ScoreTracker {
    private ArrayList<ScoreTrackerSlot> slots = new ArrayList<>(30);

    public void printScoreTracker() {
        for (int i = 0; i < slots.size(); i++) {
            System.out.println(i + ") [" + slots.get(i).getBusySlot() + "]");
        }
    }
}
