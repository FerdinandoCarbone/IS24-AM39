package com.example.codexnaturalis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.example.codexnaturalis.Colors.*;

public class Card implements Serializable {
    /*String YELLOW = "\u001B[33m";
    String RESET = "\u001B[0m";*/
    private int idCard;
    private String[] artRef;

    public Card(int idCard, String[] artRef) {
        this.idCard = idCard;
        this.artRef = artRef;
    }

    public int getIdCard() {
        return idCard;
    }

    public String[] getArtRef() {
        return artRef;
    }

}
/**
 * StarterCard: SubClass of NonObjectiveCard
 * Added Fields: backCenterResources
 */
class StarterCard extends NonObjectiveCard {
    private ArrayList<ResourceGoldCard.ResourceElement> backCentreResources;

    /**
     * Constructor of StarterCard
     *
     * @param idCard:             ID of the card
     * @param artRef:              Art Reference of the Card
     * @param frontCorners:        An ArrayList of the 4 front corners
     * @param backCorners:         Defines the corners in the back of the card
     * @param backCentreResources: ArrayList of the possible Resources on the back of the card
     */
    public StarterCard(@JsonProperty("idCard") int idCard, @JsonProperty("artRef") String[] artRef, @JsonProperty("frontCorners") ArrayList<Corner> frontCorners, @JsonProperty("backCorners") ArrayList<Corner> backCorners, @JsonProperty("backCenter") ArrayList<ResourceGoldCard.ResourceElement> backCentreResources) {
        super(idCard, artRef, frontCorners, backCorners);
        this.backCentreResources = backCentreResources;
    }

    public ArrayList<ResourceGoldCard.ResourceElement> getBackCentreResources() {
        return backCentreResources;
    }
}


/**
 * ObjectiveCard: SubClass of Card
 * Extra Fields: points
 */
 class ObjectiveCard extends Card {
     private int points;
     private String asciiArt;
    /**
     * Constructor of ObjectiveCard
     *
     * @param idCard: ID of the card
     * @param artRef:  Art Reference of the Card
     * @param points:  points given to the player when placing the card
     */
    public ObjectiveCard(@JsonProperty("idCard") int idCard, @JsonProperty("artRef") String[] artRef, @JsonProperty("points") int points,@JsonProperty("asciiArt") String asciiArt) {
        super(idCard, artRef);
        this.points = points;
        this.asciiArt = asciiArt;
    }

    public void printObjectiveCard() {
        System.out.println("-----------------------------");
        System.out.println("Objective Card #" + getIdCard());
        System.out.println("Points: " + getPoints());
        printCardAscii();
    }
    private void printCardAscii(){
        String s = colorCorrector(asciiArt);
        System.out.println(s);
    }

    private String colorCorrector(String s) {
        String correctedString="";
        String z;
        String color;
        /*ArrayList<Integer> redIndexes = new ArrayList<>();
        ArrayList<Integer> blueIndexes = new ArrayList<>();
        ArrayList<Integer> greenIndexes = new ArrayList<>();
        ArrayList<Integer> purpleIndexes = new ArrayList<>();
        ArrayList<Integer> inkIndexes = new ArrayList<>();
        ArrayList<Integer> scrollIndexes = new ArrayList<>();
        ArrayList<Integer> featherIndexes = new ArrayList<>();

        StringBuilder sb = new StringBuilder(correctedString);*/
        for (int i = 0; i < s.length(); i++) {
            z = String.valueOf(s.charAt(i));
            color = Colors.RESET;
            if(s.charAt(i)=='|' || s.charAt(i)=='+'||s.charAt(i)=='-' || s.charAt(i)==' '){
                correctedString=correctedString.concat(z);
                continue;
            }
            else if (s.charAt(i) == 'M' || s.charAt(i) == 'R') {
                color=Colors.RED;
            } else if (s.charAt(i) == 'L' || s.charAt(i) == 'G') {
                color=Colors.GREEN;
            } else if (s.charAt(i) == 'W'||(s.charAt(i) == 'B'&& this instanceof ObjectiveCardCombo)) {
                color=Colors.BLUE;
            } else if (s.charAt(i) == 'P'||(s.charAt(i) == 'B'&& this instanceof ObjectiveCardResourceSet)) {
                color=Colors.PURPLE;
            } else if (s.charAt(i) == 'I') {
                color=Colors.BLACK_BG;
            } else if (s.charAt(i) == 'S') {
                color=Colors.YELLOW;
            }
            else if (s.charAt(i) == 'F'){
                color = Colors.WHITE_BG;
                z=Colors.BLACK+z;
            }
            correctedString=correctedString.concat(color+z+Colors.RESET);
        }
        return correctedString;
    }

    /**
     * Setter of points
     * @param points: points given to the player when placing the card
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Getter of points
     * @return value of points
     */
    public int getPoints() {
        return points;
    }
}
/**
 * ObjectiveCardObjectSet: SubClass of ObjectiveCard
 * Extra Fields: elementSet
 */
class ObjectiveCardCombo extends ObjectiveCard {

            private Seed seed;
            private boolean type;

            /**
             * Constructor of ObjectiveCardObjectSet
             * @param idCard: ID of the card
             * @param artRef: Art Reference of the Card
             * @param points: points given to the player when placing the card
             */
            public ObjectiveCardCombo(@JsonProperty("idCard") int idCard, @JsonProperty("artRef") String[] artRef, @JsonProperty("points") int points,@JsonProperty("asciiArt") String asciiArt, @JsonProperty("seed") Seed seed,@JsonProperty("type") boolean type) {
                super(idCard, artRef, points,asciiArt);
                this.seed = seed;
                this.type = type;
            }
        }

/**
 * ObjectiveCardResourceSet: SubClass of ObjectiveCard
 * Extra Fields: resourceSet
 */
class ObjectiveCardResourceSet extends ObjectiveCard {

    private ArrayList<ResourceGoldCard.ResourceElement> resourceSet;

    /**
     * Constructor of ObjectiveCardResourceSet
     * @param idCard: ID of the card
     * @param artRef: Art Reference of the Card
     * @param points: points given to the player when placing the card
     * @param resourceSet: ArrayList of the resources required
     */
    public ObjectiveCardResourceSet(@JsonProperty("idCard") int idCard, @JsonProperty("artRef") String[] artRef, @JsonProperty("points") int points,@JsonProperty("asciiArt") String asciiArt,@JsonProperty("requiredMaterials")  ArrayList<ResourceGoldCard.ResourceElement> resourceSet) {
        super(idCard, artRef, points,asciiArt);
        this.resourceSet = resourceSet;
    }
}

