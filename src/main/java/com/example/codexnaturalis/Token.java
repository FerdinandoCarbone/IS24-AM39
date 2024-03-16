package com.example.codexnaturalis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TooManyListenersException;

/**
 * Token of players
 */
public class Token {
    /**
     * Defines the color of a token
     */
    /**
     * tokenColor: Defines the color of a token
     */
    private Color tokenColor;
    //static ArrayList<Integer> totalAvailableColors = (ArrayList<Integer>) Arrays.asList(new Integer[]{2, 2, 2, 2});

    public enum Color {
        Red,
        Blue,
        Green,
        Yellow

    }

    /**
     * Constructor of Token
     *
     * @param
     */
    public Token() throws IOException {
        this.tokenColor = pickColor();
    }

    public Color pickColor() throws IOException {
        Token.Color tokenColor = null;
        Integer setElem;
        int z = Server.clients.size();
        if (z > 4) {
            System.out.println("Una partita è già in corso\nCrearne una nuova?");
        }
        else {
            z--;
            switch (z % 4) {
                case 0: {
                    tokenColor = Color.Yellow;
                    break;
                }
                case 1: {
                    tokenColor = Color.Red;
                    break;
                }
                case 2: {
                    tokenColor = Color.Blue;
                    break;
                }
                case 3: {
                    tokenColor = Color.Green;
                    break;
                }
                default: tokenColor=Color.Red;
            }

        }
        return tokenColor;
    }
}