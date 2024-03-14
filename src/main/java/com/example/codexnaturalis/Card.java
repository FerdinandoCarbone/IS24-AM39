package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;

public class Card {

    private int idCard;
    private String artRef;

    public Card(int idCard, String artRef) {
        this.idCard = idCard;
        this.artRef = artRef;
    }

    public abstract static class NonObjectiveCard extends Card {

        private ArrayList<Corner> frontCorners;
        private ArrayList<Corner> backCorners;
        private boolean isPlacedFront = true;

        public NonObjectiveCard(int idCarta, String artRef, ArrayList<Corner> frontCorners, ArrayList<Corner> backCorners) {
            super(idCarta, artRef);
            this.frontCorners = frontCorners;
            this.backCorners = backCorners;
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
         * @return
         */
        public boolean getPiazzataInFronte() {
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

        /**
         * StarterCard: SubClass of NonObjectiveCard
         * Added Fields: backCenterResources
         */
        public static class StarterCard extends NonObjectiveCard {
            private ArrayList<ResourceElement.Resource> backCentreResources;

            /**
             * Constructor of StarterCard
             * @param idCarta: ID of the card
             * @param artRef: Art Reference of the Card
             * @param frontCorners: An ArrayList of the 4 front corners
             * @param backCorners: Defines the corners in the back of the card
             * @param backCentreResources: ArrayList of the possible Resources on the back of the card
             */
            public StarterCard(int idCarta, String artRef, ArrayList<Corner> frontCorners, ArrayList<Corner> backCorners, ArrayList<ResourceElement.Resource> backCentreResources) {
                super(idCarta, artRef, frontCorners, backCorners);
                this.backCentreResources = backCentreResources;
            }
        }

        public abstract static class ResourceGoldCard extends NonObjectiveCard {


            private ResourceElement.Resource backCentreResource;

            public ResourceGoldCard(int idCard, String artRef, ArrayList<Corner> frontCorners, ResourceElement.Resource backCentreResource) {
                super(idCard, artRef, frontCorners, new ArrayList<>(Arrays.asList(
                        new Corner(true, null),
                        new Corner(true, null),
                        new Corner(true, null),
                        new Corner(true, null)
                )));
                this.backCentreResource = backCentreResource;
            }

            /**
             * ResourceCard: subClass of ResourceGoldCard. No extra fields
             */
            public static class ResourceCard extends ResourceGoldCard {
                /**
                 * Constructor of ResourceCard
                 * @param idCard: ID of the card
                 * @param artRef: Art Reference of the Card
                 * @param frontCorners: An ArrayList of the 4 front corners
                 * @param backCentreResource: The Resource in the back of the Card
                 */
                public ResourceCard(int idCard, String artRef, ArrayList<Corner> frontCorners, ResourceElement.Resource backCentreResource) {
                    super(idCard, artRef, frontCorners, backCentreResource);
                }
            }

            /**
             * GoldCard: subClass of ResourceGoldCard.
             * points: defines the amount of points given from the card
             * requiredResources: defines the required Resources the player needs to have to place the Gold Card
             */
            public static class GoldCard extends ResourceGoldCard {

                private int points;
                private ArrayList<ResourceElement.Resource> requiredResources;

                //TODO: aggiungere l'attributo requisitiPunti

                /**
                 * Constructor of GoldCard
                 * @param idCarta: ID of the card
                 * @param artRef: Art Reference of the Card
                 * @param frontCorners: An ArrayList of the 4 front corners
                 * @param backCentreResource: The Resource in the back of the Card
                 * @param points: points given to the player when placing the card
                 * @param requiredResources: defines the required Resources the player needs to have to place the Gold Card
                 */
                public GoldCard(int idCarta, String artRef, ArrayList<Corner> frontCorners, ResourceElement.Resource backCentreResource, int points, ArrayList<ResourceElement.Resource> requiredResources) {
                    super(idCarta, artRef, frontCorners, backCentreResource);
                    this.points = points;
                    this.requiredResources = requiredResources;
                }
            }
        }
    }

    /**
     * ObjectiveCard: SubClass of Card
     * Extra Fields: points
     */
    public abstract static class ObjectiveCard extends Card {
        private int points;

        /**
         * Constructor of ObjectiveCard
         * @param idCarta: ID of the card
         * @param artRef: Art Reference of the Card
         * @param points: points given to the player when placing the card
         */
        public ObjectiveCard(int idCarta, String artRef, int points) {
            super(idCarta, artRef);
            this.points = points;
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

        /**
         * ObjectiveCardObjectSet: SubClass of ObjectiveCard
         * Extra Fields: elementSet
         */
        public static class ObjectiveCardObjectSet extends ObjectiveCard {

            private ArrayList<ResourceElement.Element> elementSet;

            /**
             * Constructor of ObjectiveCardObjectSet
             * @param idCarta: ID of the card
             * @param artRef: Art Reference of the Card
             * @param points: points given to the player when placing the card
             * @param elementSet: ArrayList of the elements required
             */
            public ObjectiveCardObjectSet(int idCarta, String artRef, int points, ArrayList<ResourceElement.Element> elementSet) {
                super(idCarta, artRef, points);
                this.elementSet = elementSet;
            }
        }

        /**
         * ObjectiveCardResourceSet: SubClass of ObjectiveCard
         * Extra Fields: resourceSet
         */
        public static class ObjectiveCardResourceSet extends ObjectiveCard {

            private ArrayList<ResourceElement.Resource> resourceSet;

            /**
             * Constructor of ObjectiveCardResourceSet
             * @param idCarta: ID of the card
             * @param artRef: Art Reference of the Card
             * @param points: points given to the player when placing the card
             * @param resourceSet: ArrayList of the resources required
             */
            public ObjectiveCardResourceSet(int idCarta, String artRef, int points, ArrayList<ResourceElement.Resource> resourceSet) {
                super(idCarta, artRef, points);
                this.resourceSet = resourceSet;
            }
        }
    }
}
