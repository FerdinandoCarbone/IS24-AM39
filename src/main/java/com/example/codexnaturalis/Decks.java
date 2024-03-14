package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class Decks {
    public static class StarterDeck {
        /**
         * starterCards: ArrayList of ALL starter Cards
         */
        private ArrayList<Card.NonObjectiveCard.StarterCard> starterCards = new ArrayList<>(Arrays.asList(
        ));

        /**
         * Getter of starterCards
         * @return ArrayList of starterCards
         */
        public ArrayList<Card.NonObjectiveCard.StarterCard> getStarterCards() {
            return starterCards;
        }
    }

    public static class PlayerDeck {

        private ArrayList<Card.NonObjectiveCard> cards;
        private Card.NonObjectiveCard.StarterCard starterCard;
        private Card.ObjectiveCard secretObjectiveCard;

        /**
         * Constructor of PlayerDeck
         * @param cards: ArrayList of the cards the player has except the Secret Card and the Starter Card
         * @param starterCard: starter Card of the player
         * @param secretObjectiveCard: secret Objective Card of the player
         */
        public PlayerDeck(ArrayList<Card.NonObjectiveCard> cards, Card.NonObjectiveCard.StarterCard starterCard, Card.ObjectiveCard secretObjectiveCard) {
            this.cards = cards;
            this.starterCard = starterCard;
            this.secretObjectiveCard = secretObjectiveCard;
        }

        /**
         * Setter of starterCard
         * @param starterCard: starter Card of the player
         */
        public void setStarterCard(Card.NonObjectiveCard.StarterCard starterCard) {
            this.starterCard = starterCard;
        }

        /**
         * Setter of secretObjectiveCard
         * @param secretObjectiveCard: secret Objective Card of the player
         */
        public void setSecretObjectiveCard(Card.ObjectiveCard secretObjectiveCard) {
            this.secretObjectiveCard = secretObjectiveCard;
        }

        /**
         * Getter of cards
         * @return ArrayList of the cards the player has except the Secret Card and the Starter Card
         */
        public ArrayList<Card.NonObjectiveCard> getCards() {
            return cards;
        }

        /**
         * Getter of the starter Card
         * @return starter Card of the player
         */
        public Card.NonObjectiveCard.StarterCard getStarterCard() {
            return starterCard;
        }

        /**
         * Getter of
         * @return secret Objective Card of the player
         */
        public Card.ObjectiveCard getSecretObjectiveCard() {
            return secretObjectiveCard;
        }
    }

    public static class ObjectiveDeck {

        private ArrayList<Card.ObjectiveCard> objectiveCards = new ArrayList<>();

        /**
         * Getter of ObjectiveDeck
         * @return
         */
        public ArrayList<Card.ObjectiveCard> getObjectiveCards() {
            return objectiveCards;
        }
    }

    public static class GoldDeck implements canShuffle{
        private ArrayList<Card.NonObjectiveCard.ResourceGoldCard.GoldCard> goldCards = new ArrayList<>();

        /**
         * Shuffles the deck
         */
        public void shuffleDeck() {
            Random rand = new Random();
            for (int i = 0; i < goldCards.size(); i++) {
                int randomIndex = rand.nextInt(goldCards.size());
                Card.NonObjectiveCard.ResourceGoldCard.GoldCard tmpCarta = goldCards.get(randomIndex);
                goldCards.set(randomIndex, goldCards.get(i));
                goldCards.set(i, tmpCarta);
            }
            System.out.println("Mazzo Mischiato");
        }

        /**
         * Prints the deck
         */
        public void printDeck() {
            System.out.println("Gold deck:");
            for (int i = 0; i < goldCards.size(); i++) {
                System.out.println(i + ") [" + goldCards.get(i).getClass() + "]");
            }
        }

        /**
         * Getter of gold cards
         * @return ArrayList of all gold cards
         */
        public ArrayList<Card.NonObjectiveCard.ResourceGoldCard.GoldCard> getGoldCards() {
            return goldCards;
        }


    }

    public static class ResourceDeck implements canShuffle{

        public ArrayList<Card.NonObjectiveCard.ResourceGoldCard.ResourceCard> resourceCards = new ArrayList<>(Arrays.asList(
        ));

        /**
         * Shuffles the deck
         */
        public void shuffleDeck() {
            Random rand = new Random();
            for (int i = 0; i < resourceCards.size(); i++) {
                int randomIndex = rand.nextInt(resourceCards.size());
                Card.NonObjectiveCard.ResourceGoldCard.ResourceCard tmpCarta = resourceCards.get(randomIndex);
                resourceCards.set(randomIndex, resourceCards.get(i));
                resourceCards.set(i, tmpCarta);
            }
            System.out.println("Deck Shuffled");
        }

        /**
         * Prints the deck
         */
        public void printDeck() {
            System.out.println("Resource Cards:");
            for (int i = 0; i < resourceCards.size(); i++) {
                System.out.println(i + ") [" + resourceCards.get(i).getClass() + "]");
            }
        }
        /**
         * Getter of resource cards
         * @return ArrayList of all resource cards
         */
        public ArrayList<Card.NonObjectiveCard.ResourceGoldCard.ResourceCard> getResourceCards() {
            return resourceCards;
        }
    }
}
