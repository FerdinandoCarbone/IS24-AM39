package com.example.codexnaturalis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Card implements Serializable {

    private int idCard;
    private String[] artRef;

    public Card(int idCard, String[] artRef) {
        this.idCard = idCard;
        this.artRef = artRef;
    }

    public int getIdCard() {
        return idCard;
    }
}
abstract class NonObjectiveCard extends Card {

    private ArrayList<Corner> frontCorners;
    private ArrayList<Corner> backCorners;
    private boolean isPlacedFront = true;

    public NonObjectiveCard(int idCard, String[] artRef, ArrayList<Corner> frontCorners, ArrayList<Corner> backCorners) {
        super(idCard, artRef);
        this.frontCorners = frontCorners;
        this.backCorners = backCorners;
    }

    public boolean checkAvailableCorner(int cornerIndex) throws Exception {
        boolean flagAvailable;

        if (cornerIndex < 0 || cornerIndex > 3) {
            throw new Exception("CORNER OUT OF BOUNDS");
        }

        if (isPlacedFront) {
            flagAvailable = frontCorners.get(cornerIndex).isAvailableCorner();
        } else {
            flagAvailable = backCorners.get(cornerIndex).isAvailableCorner();
        }
        return flagAvailable;
    }

    public void updateCornerToBusy(int cornerIndex) throws Exception {
        if (cornerIndex < 0 || cornerIndex > 3) {
            throw new Exception("CORNER OUT OF BOUNDS");
        }

        getCorners().get(cornerIndex).setAvailableCorner(false);
    }

    /**
     * Printa a console gli angoli frontali della carta
     */
    private void printFrontCorners() {
        System.out.println("Front Corners");
        System.out.print("[" + (frontCorners.get(3).isAvailableCorner()? "1" : "0") + "|" + (frontCorners.get(3).getResourceElement()) + "]");
        System.out.println("[" + (frontCorners.get(0).isAvailableCorner()? "1" : "0") + "|" + (frontCorners.get(0).getResourceElement()) + "]");
        System.out.print("[" + (frontCorners.get(2).isAvailableCorner()? "1" : "0") + "|" + (frontCorners.get(2).getResourceElement()) + "]");
        System.out.println("[" + (frontCorners.get(1).isAvailableCorner()? "1" : "0") + "|" + (frontCorners.get(1).getResourceElement()) + "]");
    }

    /**
     * Printa a console gli angoli posteriori della carta con [0] se non è disponibile e [1] se disponibile
     */
    private void printBackCorners() {
        System.out.println("Back Corners");
        System.out.print("[" + (backCorners.get(3).isAvailableCorner()? "1" : "0") + "|" + (backCorners.get(3).getResourceElement()) + "]");
        System.out.println("[" + (backCorners.get(0).isAvailableCorner()? "1" : "0") + "|" + (backCorners.get(0).getResourceElement()) + "]");
        System.out.print("[" + (backCorners.get(2).isAvailableCorner()? "1" : "0") + "|" + (backCorners.get(2).getResourceElement()) + "]");
        System.out.println("[" + (backCorners.get(1).isAvailableCorner()? "1" : "0") + "|" + (backCorners.get(1).getResourceElement()) + "]");
    }

    public void printCardFrontAndBack() {
        System.out.println("-----------------------------");
        if (this instanceof ResourceGoldCard) {
            System.out.println("Analisi carta " + (this instanceof GoldCard? "Oro " : "Risorsa ") + "#" + getIdCard());
        } else if (this instanceof StarterCard) {
            System.out.println("Analisi carta Starter " + "#" + getIdCard());
        }
        printFrontCorners();
        printBackCorners();
        System.out.println("-----------------------------");
    }

    /**
     * Prints card
     */
    public void printCard() {
            System.out.println("-----------------------------");
            if (this instanceof ResourceGoldCard) {
                System.out.println("Analisi carta " + (this instanceof GoldCard? "Oro " : "Risorsa ") + "#" + getIdCard());
            } else if (this instanceof StarterCard) {
                System.out.println("Analisi carta Starter " + "#" + getIdCard());
            }
            if (isPlacedFront) {
                printFrontCorners();
            } else {
                printBackCorners();
            }
            System.out.println("-----------------------------");

        }

    public ArrayList<Corner> getCorners() {
        if (isPlacedFront) {
           return frontCorners;
        } else {
            return backCorners;
        }
    }
    /**
     * Ritorna 1 se la carta è piazzata frontalmente sul tavolo, 0 altrimenti
     * @return
     */
    public boolean isPlacedFront() {
        return isPlacedFront;
    }

    public ArrayList<Corner> getFrontCorners() {
        return frontCorners;
    }

    public ArrayList<Corner> getBackCorners() {
        return backCorners;
    }

    public void setIsPlacedFront(boolean isPlacedFront) {
        this.isPlacedFront = isPlacedFront;
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
}
abstract class ResourceGoldCard extends NonObjectiveCard {

    private int points;
    private Seed seed;
    //private ResourceElement backCentreResource;
    public enum ResourceElement{
        Mushroom,
        Leaf,
        Wolf,
        Butterfly,
        Ink,
        Papyrus,
        Feather,
        empty
    }

    public ResourceGoldCard(int idCard, String[] artRef, ArrayList<Corner> frontCorners, int points, Seed seed) {
        super(idCard, artRef, frontCorners, new ArrayList<>(Arrays.asList(
                new Corner(true, ResourceElement.empty),
                new Corner(true, ResourceElement.empty),
                new Corner(true, ResourceElement.empty),
                new Corner(true, ResourceElement.empty)
        )));
        this.points = points;
        this.seed = seed;
    }
}
/**
 * ResourceCard: subClass of ResourceGoldCard. No extra fields
 */
class ResourceCard extends ResourceGoldCard {
    /**
     * Constructor of ResourceCard
     *
     * @param idCard:             ID of the card
     * @param artRef:             Art Reference of the Card
     * @param frontCorners:       An ArrayList of the 4 front corners
     */
    public ResourceCard(@JsonProperty("idCard")int idCard,@JsonProperty("artRef") String[] artRef, @JsonProperty("frontCorners") ArrayList<Corner> frontCorners, @JsonProperty("points") int points ,@JsonProperty("seed") Seed seed) {
        super(idCard, artRef, frontCorners, points, seed);
    }
}

/**
 * GoldCard: subClass of ResourceGoldCard.
 * points: defines the amount of points given from the card
 * requiredResources: defines the required Resources the player needs to have to place the Gold Card
 */
class GoldCard extends ResourceGoldCard {

    private ArrayList<ResourceGoldCard.ResourceElement> requiredResources;

    //TODO: aggiungere l'attributo requisitiPunti

    /**
     * Constructor of GoldCard
     *
     * @param idCard:            ID of the card
     * @param artRef:             Art Reference of the Card
     * @param frontCorners:       An ArrayList of the 4 front corners
     */
    public GoldCard(@JsonProperty("idCard")int idCard,@JsonProperty("artRef") String[] artRef, @JsonProperty("frontCorners") ArrayList<Corner> frontCorners, @JsonProperty("points")int points, @JsonProperty("seed") Seed seed, @JsonProperty("requiredResources") ArrayList<ResourceGoldCard.ResourceElement> requiredResources) {
        super(idCard, artRef, frontCorners, points, seed);
        this.requiredResources = requiredResources;
    }

}


/**
 * ObjectiveCard: SubClass of Card
 * Extra Fields: points
 */
//TODO: DA METTERE ASTRATTA
 class ObjectiveCard extends Card {
     private int points;

    /**
     * Constructor of ObjectiveCard
     *
     * @param idCard: ID of the card
     * @param artRef:  Art Reference of the Card
     * @param points:  points given to the player when placing the card
     */
    public ObjectiveCard(@JsonProperty("idCard") int idCard, @JsonProperty("artRef") String[] artRef, @JsonProperty("points") int points) {
        super(idCard, artRef);
        this.points = points;
    }

    public void printObjectiveCard() {
        System.out.println("-----------------------------");
        System.out.println("Objective Card #" + getIdCard());
        System.out.println("Points: " + getPoints());
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
            public ObjectiveCardCombo(@JsonProperty("idCard") int idCard, @JsonProperty("artRef") String[] artRef, @JsonProperty("points") int points, @JsonProperty("seed") Seed seed,@JsonProperty("type") boolean type) {
                super(idCard, artRef, points);
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
    public ObjectiveCardResourceSet(@JsonProperty("idCard") int idCard, @JsonProperty("artRef") String[] artRef, @JsonProperty("points") int points,@JsonProperty("requiredMaterials")  ArrayList<ResourceGoldCard.ResourceElement> resourceSet) {
        super(idCard, artRef, points);
        this.resourceSet = resourceSet;
    }
}

