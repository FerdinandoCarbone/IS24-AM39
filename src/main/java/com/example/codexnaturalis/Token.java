package com.example.codexnaturalis;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*Token of players*/
public class Token implements Serializable {
    /* Defines the color of a token*/
    private final Color tokenColor;

    public enum Color {
        Red,
        Blue,
        Green,
        Yellow,
        Black

    }

    /**

     Constructor of Token*/
    public Token() {
        this.tokenColor = pickColor();
    }

    public Token(Color color) {
        this.tokenColor = color;
    }

    public Color pickColor() {
        ArrayList<Color> colors = new ArrayList<>(Arrays.asList(Color.values()));
        colors.remove(Color.Black);
        Color randomColor = colors.get(new Random().nextInt(colors.size()));
        colors.remove(randomColor);
        return randomColor;
//        Token.Color tokenColor = null;
//        int z = ServerConnectionManager.hashClient.size();
//        if (z > 4) {
//            System.out.println("Una partita è già in corso\nCrearne una nuova?");
//        }
//        else {
//            z--;
//            switch (z % 4) {
//                case 0: {
//                    tokenColor = Color.Yellow;
//                    break;
//                }
//                case 1: {
//                    tokenColor = Color.Red;
//                    break;
//                }
//                case 2: {
//                    tokenColor = Color.Blue;
//                    break;
//                }
//                case 3: {
//                    tokenColor = Color.Green;
//                    break;
//                }
//                default: tokenColor=Color.Red;
//            }
//
//        }
//        return tokenColor;
    }
    public Color getColor() {
        return tokenColor;
    }
}