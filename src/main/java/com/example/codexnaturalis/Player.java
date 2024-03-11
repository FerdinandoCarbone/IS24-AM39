package com.example.codexnaturalis;

public class Player {
    private String nickName;
    private int playerID;
    private Deck deck;
    private PlayingField playingField;
    private int score;
    private int[] resourceMana;
    private int[] materialsMana;
    public Player(String nickName,int playerID,Deck deck){
        this.nickName = nickName;
        this.playerID = playerID;
        this.deck = deck;
        this.playingField = new PlayingField();
        this.resourceMana = new int[]{0,0,0,0};
        this.materialsMana = new int[]{0,0,0};

    }
    public String getNickName(){
        return nickName;
    }
    public int getPlayerID(){
        return playerID;
    }
    public int getScore(){
        return score;
    }
    public void addPoints(int i){
        score+=i;
    }
    public Deck getDeck() {
        return deck;
    }
}
