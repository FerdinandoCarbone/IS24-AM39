package com.example.codexnaturalis;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class CardModel {


}
abstract class Card{

    private int idCarta;
    private String[] artReference;
    private int positionedPoint;


    Card(int idCarta, String[] artReference, int positionedPoint) {
        this.idCarta = idCarta;
        this.artReference = artReference;
        this.positionedPoint = positionedPoint;
    }
    public int getCardID(){
        return idCarta;
    }

}
class ObjectiveCard extends Card{

    public  ObjectiveCard(int idCarta, String[] artReference, int positionedPoint) {
        super(idCarta, artReference, positionedPoint);
    }
}
abstract class GameCard extends Card{
    private CardFace activeFace;
    private Seed seed;
    private Corner[] corners;
    public enum CornerElement {
        Ink,
        Papyrus,
        Feather,
        Empty
    }
    public GameCard(@JsonProperty("idCarta")int idCarta,@JsonProperty("artRef") String[] artReference,@JsonProperty("positionedPoints") int positionedPoint,@JsonProperty("seed") Seed seed, @JsonProperty("Corners")Corner[] corners) {
        super(idCarta, artReference, positionedPoint);
        this.activeFace = CardFace.FRONTFACE;
        this.corners = corners;
    }
    public CardFace getActiveFace(){
        return this.activeFace;
    }
    public void flipCard(){
        if(this.activeFace == CardFace.FRONTFACE) this.activeFace = CardFace.BACKFACE;
        else this.activeFace = CardFace.FRONTFACE;
    }
    public void printCorners() {
        List<Corner> list = Arrays.asList(this.corners);
            for (Corner z: list) {
                z.printAllCorners();
        }
    }
    public Corner getCorner(int i){
        return this.corners[0];
    }
}
class ResourceCard extends GameCard{
    private ResourceCorner[] corners;
    public enum CornerElement{
        Mushroom,
        Leaf,
        Wolf,
        Butterfly,
        Ink,
        Papyrus,
        Feather,
        Empty
    }
    ResourceCard(@JsonProperty("idCarta")int idCarta,@JsonProperty("artRef") String[] artReference,@JsonProperty("positionedPoints") int positionedPoint,@JsonProperty("seed") Seed seed, @JsonProperty("Corners")ResourceCorner[] corners) {
    super(idCarta, artReference, positionedPoint, seed, corners);

}
}
class StartingCard extends GameCard{
    public StartingCard(int idCarta, String[] artReference, int positionedPoint, Seed seed, Corner[] corners) {
        super(idCarta, artReference, positionedPoint, seed, corners);
    }

}
class GoldCard extends GameCard{


    public GoldCard(@JsonProperty("idCarta")int idCarta,@JsonProperty("artRef") String[] artReference,@JsonProperty("positionedPoints") int positionedPoint,@JsonProperty("seed") Seed seed, @JsonProperty("Corners")GoldCorner[] corners) {
        super(idCarta, artReference, positionedPoint, seed, corners);
    }
    /*public void correctCornerCheck(){
        String[] x = new String[]{"Ink","Mushroom","Wolf","Feather"};
        if(this.getCorner(0).getCorners().equals(x)) System.out.println("YES");
    }*/
}
class Corner{
    private GameCard.CornerElement[] corners;

    public GameCard.CornerElement[] getCorners(){
        return this.corners.clone();
    }
    public void printAllCorners(){
        for (GameCard.CornerElement i: this.corners
        ) {
            System.out.println(i);
        }
    }
}
class GoldCorner extends Corner{
    private GoldCard.CornerElement[] corners;
    public GoldCorner(@JsonProperty("UL") GoldCard.CornerElement ul, @JsonProperty("UR")GoldCard.CornerElement ur, @JsonProperty("BL")GoldCard.CornerElement bl, @JsonProperty("BR")GoldCard.CornerElement br){

        this.corners = new GoldCard.CornerElement[]{ul,ur,bl,br};
    }


}
class ResourceCorner extends Corner{

    private ResourceCard.CornerElement[] corners;
    public ResourceCorner(@JsonProperty("UL") ResourceCard.CornerElement ul, @JsonProperty("UR")ResourceCard.CornerElement ur, @JsonProperty("BL")ResourceCard.CornerElement bl, @JsonProperty("BR")ResourceCard.CornerElement br){

        this.corners = new ResourceCard.CornerElement[]{ul,ur,bl,br};
    }

}
