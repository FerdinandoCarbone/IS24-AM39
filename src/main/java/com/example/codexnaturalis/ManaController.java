package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;


/**
 * resourceMana: Defines the amount of resources the player has
 * 0: Mushroom
 * 1: Leaf
 * 2: Wolf
 * 3: Butterfly

 * elementsMana: Defines the amount of elements the player has
 * 0: Ink
 * 1: Papyrus
 * 2: Feather
 */


public class ManaController extends VBox {
    private int score , mushroom, leaf, wolf, butterfly, ink, papyrus, feather;
    @FXML
    Label scoreLabel, mushroomLabel, leafLabel, wolfLabel, butterflyLabel, inkLabel, papyrusLabel, featherLabel;

    public ManaController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Mana.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

    }

    public void displayScore(){
        scoreLabel.setText("score: " + score);
        scoreLabel.setFont(Font.font("Verdana", 20));
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setTextAlignment(TextAlignment.LEFT);
    }
    public void displayResEle(){
        mushroomLabel.setText("mushroom: "  + mushroom);
        mushroomLabel.setTextFill(Color.BLUE);
        mushroomLabel.setTextAlignment(TextAlignment.LEFT);
        leafLabel.setText("leaf: " + leaf);
        leafLabel.setTextFill(Color.BLUE);
        leafLabel.setTextAlignment(TextAlignment.LEFT);
        wolfLabel.setText("wolf: " + wolf);
        wolfLabel.setTextFill(Color.BLUE);
        wolfLabel.setTextAlignment(TextAlignment.LEFT);
        butterflyLabel.setText("butterfly: " + butterfly);
        butterflyLabel.setTextFill(Color.BLUE);
        butterflyLabel.setTextAlignment(TextAlignment.LEFT);
        inkLabel.setText("ink: " + ink);
        inkLabel.setTextAlignment(TextAlignment.LEFT);
        papyrusLabel.setText("papyrus: " + papyrus);
        papyrusLabel.setTextAlignment(TextAlignment.LEFT);
        featherLabel.setText("feather: " + feather);
        featherLabel.setTextAlignment(TextAlignment.LEFT);
    }

    public void setScore(int score){
        this.score = score;
    }
    public void setEleMana(int ink, int papyrus, int feather){
        this.ink = ink;
        this.papyrus = papyrus;
        this.feather = feather;
    }
    public void setResMana(int mushroom, int leaf, int wolf, int butterfly){
        this.mushroom = mushroom;
        this.leaf = leaf;
        this.wolf = wolf;
        this.butterfly = butterfly;
    }

}
