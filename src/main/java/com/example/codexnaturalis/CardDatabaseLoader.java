package com.example.codexnaturalis;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public abstract class CardDatabaseLoader{

}

class ResourceCardDatabaseLoader extends CardDatabaseLoader {
    public static List<ResourceCard> loadCardsFromFile(String filename) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<ResourceCard> cards = objectMapper.readValue(new File(filename), new TypeReference<List<ResourceCard>>() {
        });
        return cards;
    }
}
class GoldCardDatabaseLoader extends CardDatabaseLoader {
    public static List<GoldCard> loadCardsFromFile(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<GoldCard> cards = objectMapper.readValue(new File(filename), new TypeReference<List<GoldCard>>() {
        });
        return cards;
        }
    }
class ObjectiveCardDatabaseLoader extends CardDatabaseLoader {
    public static List<ObjectiveCard> loadCardsFromFile(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectiveCard[] cards = objectMapper.readValue(new File(filename), ObjectiveCard[].class);
        return Arrays.asList(cards);
    }
}
class StartingCardDatabaseLoader extends CardDatabaseLoader {
    public static List<StartingCard> loadCardsFromFile(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StartingCard[] cards = objectMapper.readValue(new File(filename), StartingCard[].class);
        return Arrays.asList(cards);
    }
}