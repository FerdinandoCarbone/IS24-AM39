package com.example.codexnaturalis;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
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
    public static List<ObjectiveCardCombo> loadCardsFromFile(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ObjectiveCardCombo> cards = objectMapper.readValue(new File(filename), new TypeReference<List<ObjectiveCardCombo>>() {
        });
        return cards;
    }
}
class ObjectiveCardResourceSetDatabaseLoader extends CardDatabaseLoader {
    public static List<ObjectiveCardResourceSet> loadCardsFromFile(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ObjectiveCardResourceSet> cards = objectMapper.readValue(new File(filename), new TypeReference<List<ObjectiveCardResourceSet>>() {
        });
        return cards;
    }
}
class StarterCardDatabaseLoader extends CardDatabaseLoader {
    public static List<StarterCard> loadCardsFromFile(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<StarterCard> cards = objectMapper.readValue(new File(filename), new TypeReference<List<StarterCard>>() {
        });
        return cards;
    }
}