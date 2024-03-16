package com.example.codexnaturalis;

/**
 * Token of players
 */
public class Token {
    /**
     * Defines the color of a token
     */
    private Color tokenColor;
    public enum Color{
        Red,
        Blue,
        Green,
        Yellow,
        Black

    }
    /*public Color pickColor(){
        Color token;

        Server.clients.forEach((socket, player) -> {
            Token tok = player.getToken();
            switch(tok.tokenColor){
                case Color.Red: {
                    totalAvailableColors[0]--;
                    break;
                }
                case Color.Blue: {
                    totalAvailableColors[1]--;
                    break;
                }
                case Color.Green: {
                    totalAvailableColors[2]--;
                    break;
                }
                default: throw new IllegalStateException("Unexpected value: " + tok);
            }
            
        });
        return token
    }*/
    /**
     * Constructor of Token
     * @param tokenColor: Defines the color of a token
     */
    public Token(Color tokenColor) {
        this.tokenColor = tokenColor;
    }
}
