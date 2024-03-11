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
    public enum Materials {
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
    public enum Resources{
        Mushroom,
        Leaf,
        Wolf,
        Butterfly,
    }
    ResourceCard(@JsonProperty("idCarta")int idCarta,@JsonProperty("artRef") String[] artReference,@JsonProperty("positionedPoints") int positionedPoint,@JsonProperty("seed") Seed seed, @JsonProperty("Corners")Corner[] corners) {
    super(idCarta, artReference, positionedPoint, seed, corners);

}
}
class StartingCard extends GameCard{
    public StartingCard(int idCarta, String[] artReference, int positionedPoint, Seed seed, Corner[] corners) {
        super(idCarta, artReference, positionedPoint, seed, corners);
    }

}
class GoldCard extends GameCard{


    public GoldCard(@JsonProperty("idCarta")int idCarta,@JsonProperty("artRef") String[] artReference,@JsonProperty("positionedPoints") int positionedPoint,@JsonProperty("seed") Seed seed, @JsonProperty("Corners")Corner[] corners) {
        super(idCarta, artReference, positionedPoint, seed, corners);
    }
    public void correctCornerCheck(){
        String[] x = new String[]{"Ink","Mushroom","Wolf","Feather"};
        if(this.getCorner(0).getCorners().equals(x)) System.out.println("YES");
    }
}
class Corner {
    private String[] corners;

    public Corner(@JsonProperty("UL")String ul,@JsonProperty("UR")String ur,@JsonProperty("BL")String bl,@JsonProperty("BR")String br){
        this.corners = new String[]{ul,ur,bl,br};
    }
    public String[] getCorners(){
        return this.corners.clone();
    }
    public void printAllCorners(){
        for (String i: this.corners
             ) {
            System.out.println(i);
        }
    }

}
