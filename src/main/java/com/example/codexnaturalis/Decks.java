package com.example.codexnaturalis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class Decks {
    public static class StarterDeck {
        private ArrayList<Card.NonObjectiveCard.StarterCard> starterCards = new ArrayList<>(Arrays.asList(
        ));

        public ArrayList<Card.NonObjectiveCard.StarterCard> getStarterCards() {
            return starterCards;
        }
    }

    public static class PlayerDeck {

        private ArrayList<Card.NonObjectiveCard> cards;
        private Card.NonObjectiveCard.StarterCard starterCard;
        private Card.ObjectiveCard secretObjectiveCard;

        public PlayerDeck(ArrayList<Card.NonObjectiveCard> cards, Card.NonObjectiveCard.StarterCard starterCard) {
            this.cards = cards;
            this.starterCard = starterCard;
        }

        public void setStarterCard(Card.NonObjectiveCard.StarterCard starterCard) {
            this.starterCard = starterCard;
        }

        public void setCartaObiettivoSegreto(Card.ObjectiveCard objectiveCardSegreto) {
            this.secretObjectiveCard = objectiveCardSegreto;
        }

        public ArrayList<Card.NonObjectiveCard> getCards() {
            return cards;
        }

        public Card.NonObjectiveCard.StarterCard getStarterCard() {
            return starterCard;
        }

        public Card.ObjectiveCard getCartaObiettivoSegreto() {
            return secretObjectiveCard;
        }
    }

    public static class ObjectiveDeck {

        private ArrayList<Card.ObjectiveCard> objectiveCards = new ArrayList<>();

        public ArrayList<Card.ObjectiveCard> getObjectiveCards() {
            return objectiveCards;
        }
    }

    public static class GoldDeck implements canShuffle{
        private ArrayList<Card.NonObjectiveCard.ResourceGoldCard.GoldCard> goldCards = new ArrayList<>(Arrays.asList(
        ));

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

        public void printDeck() {
            System.out.println("Gold deck:");
            for (int i = 0; i < goldCards.size(); i++) {
                System.out.println(i + ") [" + goldCards.get(i).getClass() + "]");
            }
        }

        public ArrayList<Card.NonObjectiveCard.ResourceGoldCard.GoldCard> getGoldCards() {
            return goldCards;
        }


    }

    public static class ResourceDeck implements canShuffle{

        public ArrayList<Card.NonObjectiveCard.ResourceGoldCard.ResourceCard> resourceCards = new ArrayList<>(Arrays.asList(
        ));

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

        public void printMazzo() {
            System.out.println("Resource Cards:");
            for (int i = 0; i < resourceCards.size(); i++) {
                System.out.println(i + ") [" + resourceCards.get(i).getClass() + "]");
            }
        }

        public ArrayList<Card.NonObjectiveCard.ResourceGoldCard.ResourceCard> getResourceCards() {
            return resourceCards;
        }
    }
}
