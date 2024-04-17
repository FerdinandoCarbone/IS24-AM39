package com.example.codexnaturalis;



/**
 * Token of players
 */
public class Token {
    /**
     * Defines the color of a token
     */
    private Color tokenColor;

    public enum Color {
        Red,
        Blue,
        Green,
        Yellow

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
        int z = ZakServer.hashPlayer.size();
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