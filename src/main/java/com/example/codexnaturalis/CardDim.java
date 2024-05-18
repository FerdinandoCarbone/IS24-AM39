package com.example.codexnaturalis;

import java.util.UUID;

public class CardDim {
    static int matrixSize = 30+1;
    static double divider = matrixSize/1.875;
    static double cardWidth = 492.0 / divider;
    static double cardHeight = 326.0 / divider;
    static double cornerWidth = 106.0 / divider;
    static double cornerHeight = 131.0 / divider;
    static double deltaHeight = cardHeight - cornerHeight;
    static double deltaWidth = cardWidth - cornerWidth;
}
