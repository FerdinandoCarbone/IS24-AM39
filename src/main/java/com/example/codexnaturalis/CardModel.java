package com.example.codexnaturalis;

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

    public GameCard(int idCarta, String[] artReference, int positionedPoint) {
        super(idCarta, artReference, positionedPoint);
        this.activeFace = CardFace.FRONTFACE;
    }
}
class ResourceCard extends GameCard{
    ResourceCard(int idCarta, String[] artReference, int positionedPoint) {
    super(idCarta, artReference, positionedPoint);
}
}
class StartingCard extends GameCard{

    public StartingCard(int idCarta, String[] artReference, int positionedPoint) {
        super(idCarta, artReference, positionedPoint);

    }
}
class GoldCard extends GameCard{

    public GoldCard(int idCarta, String[] artReference, int positionedPoint) {
        super(idCarta, artReference, positionedPoint);
    }
}

