package com.example.codexnaturalis;


import java.io.Serializable;

/**
 * Token of players
 */
public class Token implements Serializable {
    /**
     * Defines the color of a token
     */
    private Color tokenColor;

    public enum Color {
        Red,
        Blue,
        Green,
        Yellow,
        Black

    }

    /**
     * Constructor of Token
     */
    public Token() {
        this.tokenColor = pickColor();
    }

    public Token(Color color) {
        this.tokenColor = color;
    }

    public Color pickColor() {
        Token.Color tokenColor = null;
        int z = ServerConnectionManager.hashPlayer.size();
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
    public Color getColor() {
        return tokenColor;
    }
}