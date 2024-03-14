package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {

    private int idCard;
    private String[] artRef;

    public Card(int idCard, String[] artRef) {
        this.idCard = idCard;
        this.artRef = artRef;
    }
    //TODO: DA CANCELLARE PROVVISORIO
    public Card() {

    }
}
abstract class NonObjectiveCard extends Card {

        private ArrayList<Corner> frontCorners;
        private ArrayList<Corner> backCorners;
        private boolean isPlacedFront = true;

        public NonObjectiveCard(int idCarta, String[] artRef, ArrayList<Corner> frontCorners, ArrayList<Corner> backCorners) {
            super(idCarta, artRef);
            this.frontCorners = frontCorners;
            this.backCorners = backCorners;
        }
    //TODO: DA CANCELLARE PROVVISORIO
    public NonObjectiveCard(ArrayList<Corner> frontCorners) {
        super();
        this.frontCorners = frontCorners;
    }

    /**
         * Printa a console gli angoli frontali della carta con [0] se non è disponibile e [1] se disponibile
         */
        public void printFrontCorners() {
            System.out.println("[" + frontCorners.get(3).isAvailableCorner() + "][" + frontCorners.get(0).isAvailableCorner() + "]");
            System.out.println("[" + frontCorners.get(2).isAvailableCorner() + "][" + frontCorners.get(1).isAvailableCorner() + "]");
        }

        /**
         * Printa a console gli angoli posteriori della carta con [0] se non è disponibile e [1] se disponibile
         */
        public void printBackCorners() {
            System.out.println("[" + backCorners.get(3).isAvailableCorner() + "][" + backCorners.get(0).isAvailableCorner() + "]");
            System.out.println("[" + backCorners.get(2).isAvailableCorner() + "][" + backCorners.get(1).isAvailableCorner() + "]");
        }

        /**
         * Ritorna 1 se la carta è piazzata frontalmente sul tavolo, 0 altrimenti
         *
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
             * @param idCarta:             ID of the card
             * @param artRef:              Art Reference of the Card
             * @param frontCorners:        An ArrayList of the 4 front corners
             * @param backCorners:         Defines the corners in the back of the card
             * @param backCentreResources: ArrayList of the possible Resources on the back of the card
             */
            public StarterCard(int idCarta, String[] artRef, ArrayList<Corner> frontCorners, ArrayList<Corner> backCorners, ArrayList<ResourceGoldCard.ResourceElement> backCentreResources) {
                super(idCarta, artRef, frontCorners, backCorners);
                this.backCentreResources = backCentreResources;
            }
            //TODO: DA CANCELLARE PROVVISORIO
            public StarterCard() {
                super(new ArrayList<Corner>(Arrays.asList(
                        new Corner(ResourceGoldCard.ResourceElement.Ink),
                        new Corner(ResourceGoldCard.ResourceElement.Ink),
                        new Corner(ResourceGoldCard.ResourceElement.Ink),
                        new Corner(ResourceGoldCard.ResourceElement.Ink)
                )));
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
public ResourceGoldCard(int idCard, String[] artRef, int points, ArrayList<Corner> frontCorners, Seed seed) {
    super(idCard, artRef, frontCorners, frontCorners);
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
                public ResourceCard(@JsonProperty("idCarta")int idCard,@JsonProperty("artRef") String[] artRef, @JsonProperty("positionedPoints") int points , @JsonProperty("Corners") ArrayList<Corner> frontCorners,@JsonProperty("seed") Seed seed) {
                    super(idCard, artRef, points, frontCorners, seed);
                }
                /*ResourceCard(@JsonProperty("idCarta")int idCarta,@JsonProperty("artRef") String[] artReference,@JsonProperty("positionedPoints") int positionedPoint,@JsonProperty("seed") Seed seed, @JsonProperty("Corners")ResourceCorner[] corners) {
                    super(idCarta, artReference, positionedPoint, seed, corners);

                }*/
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
               /* public GoldCard(int idCarta, String[] artRef, ArrayList<Corner> frontCorners, Seed seed, ArrayList<ResourceGoldCard.ResourceElement> requiredResources) {
                    super(idCarta, artRef, frontCorners,seed);
                    this.requiredResources = requiredResources;
                }
                */
                public GoldCard(@JsonProperty("idCarta")int idCard,@JsonProperty("artRef") String[] artRef, @JsonProperty("positionedPoints")int points, @JsonProperty("Corners") ArrayList<Corner> frontCorners,@JsonProperty("seed") Seed seed, @JsonProperty("requiredResources") ArrayList<ResourceGoldCard.ResourceElement> requiredResources) {
                    super(idCard, artRef, points, frontCorners, seed);
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
         * @param idCarta: ID of the card
         * @param artRef:  Art Reference of the Card
         * @param points:  points given to the player when placing the card
         */
        public ObjectiveCard(int idCarta, String[] artRef, int points) {
            super(idCarta, artRef);
            this.points = points;
        }
        //TODO: DA CANCELLARE
        public ObjectiveCard() {

        }

        /**
         * Setter of points
         *
         * @param points: points given to the player when placing the card
         */
        public void setPoints(int points) {
            this.points = points;
        }

        /**
         * Getter of points
         *
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
class ObjectiveCardObjectSet extends ObjectiveCard {

            private ArrayList<ResourceGoldCard.ResourceElement> elementSet;

            /**
             * Constructor of ObjectiveCardObjectSet
             * @param idCarta: ID of the card
             * @param artRef: Art Reference of the Card
             * @param points: points given to the player when placing the card
             * @param elementSet: ArrayList of the elements required
             */
            public ObjectiveCardObjectSet(int idCarta, String[] artRef, int points, ArrayList<ResourceGoldCard.ResourceElement> elementSet) {
                super(idCarta, artRef, points);
                this.elementSet = elementSet;
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
             * @param idCarta: ID of the card
             * @param artRef: Art Reference of the Card
             * @param points: points given to the player when placing the card
             * @param resourceSet: ArrayList of the resources required
             */
            public ObjectiveCardResourceSet(int idCarta, String[] artRef, int points, ArrayList<ResourceGoldCard.ResourceElement> resourceSet) {
                super(idCarta, artRef, points);
                this.resourceSet = resourceSet;
            }
        }

