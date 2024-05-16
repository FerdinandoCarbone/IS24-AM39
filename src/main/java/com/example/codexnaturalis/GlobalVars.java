package com.example.codexnaturalis;

public class GlobalVars {
    static int matrixSize = 30+1;
    static double divider = matrixSize/3.875;
    static double cardWidth = 492.0 / divider;
    static double cardHeight = 326.0 / divider;
    static double cornerWidth = 106.0 / divider;
    static double cornerHeight = 131.0 / divider;
    static double deltaHeight = cardHeight - cornerHeight;
    static double deltaWidth = cardWidth - cornerWidth;
}
