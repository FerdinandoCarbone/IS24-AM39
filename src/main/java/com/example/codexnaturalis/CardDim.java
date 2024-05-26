package com.example.codexnaturalis;

import java.util.UUID;

public class CardDim {
    static int matrixSize = 25;
    static double divider = 6;
    static double cardWidth = 492.0 / divider;
    static double cardHeight = 326.0 / divider;
    static double cornerWidth = 106.0 / divider;
    static double cornerHeight = 131.0 / divider;
    static double slotWidth = (492.0 / divider) * 2;
    static double slotHeight = (326.0 / divider) * 2;
    static double cornerSlotHeight = (131.0 / divider) * 2;
    static double deltaSlotWidth = (cardWidth - cornerWidth) * 2;
    static double scoreTrackerMulti = 1;
}
