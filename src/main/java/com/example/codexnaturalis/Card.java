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
            System.out.println("[" + frontCorners.get(3).getAvailableCorner() + "][" + frontCorners.get(0).getAvailableCorner() + "]");
            System.out.println("[" + frontCorners.get(2).getAvailableCorner() + "][" + frontCorners.get(1).getAvailableCorner() + "]");
        }

        /**
         * Printa a console gli angoli posteriori della carta con [0] se non è disponibile e [1] se disponibile
         */
        public void printBackCorners() {
            System.out.println("[" + backCorners.get(3).getAvailableCorner() + "][" + backCorners.get(0).getAvailableCorner() + "]");
            System.out.println("[" + backCorners.get(2).getAvailableCorner() + "][" + backCorners.get(1).getAvailableCorner() + "]");
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

        public static class StarterCard extends NonObjectiveCard {

            private ArrayList<ResourceElement.Resource> backCentreResources;

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

            public static class ResourceCard extends ResourceGoldCard {
                public ResourceCard(int idCard, String artRef, ArrayList<Corner> frontCorners, ResourceElement.Resource backCentreResource) {
                    super(idCard, artRef, frontCorners, backCentreResource);
                }
            }

            public static class GoldCard extends ResourceGoldCard {

                private int points;
                private ArrayList<ResourceElement.Resource> requiredResources;

                //TODO: aggiungere l'attributo requisitiPunti


                public GoldCard(int idCarta, String artRef, ArrayList<Corner> frontCorners, ResourceElement.Resource backCentreResource, int points, ArrayList<ResourceElement.Resource> requiredResources) {
                    super(idCarta, artRef, frontCorners, backCentreResource);
                    this.points = points;
                    this.requiredResources = requiredResources;
                }
            }
        }
    }

    public abstract static class ObjectiveCard extends Card {
        private int points;

        public ObjectiveCard(int idCarta, String artRef, int points) {
            super(idCarta, artRef);
            this.points = points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }

        public static class ObjectiveCardObjectSet extends ObjectiveCard {

            private ArrayList<ResourceElement.Element> objectSet;

            public ObjectiveCardObjectSet(int idCarta, String artRef, int points, ArrayList<ResourceElement.Element> objectSet) {
                super(idCarta, artRef, points);
                this.objectSet = objectSet;
            }
        }

        public static class ObjectiveCardResourceSet extends ObjectiveCard {

            private ArrayList<ResourceElement.Resource> resouceSet;

            public ObjectiveCardResourceSet(int idCarta, String artRef, int points, ArrayList<ResourceElement.Resource> resouceSet) {
                super(idCarta, artRef, points);
                this.resouceSet = resouceSet;
            }
        }
    }
}
