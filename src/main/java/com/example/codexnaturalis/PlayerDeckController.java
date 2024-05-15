package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PlayerDeckController extends HBox {

    @FXML
    private ResourceGoldCardController card1;
    @FXML
    private ResourceGoldCardController card2;
    @FXML
    private ResourceGoldCardController card3;
    @FXML
    private StarterCardController starterCard;
    @FXML
    private ObjectiveCardController secretObjCard;
    private final double deckHeight;

    public PlayerDeckController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlayerDeck.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        this.setStyle("-fx-background-color: #aff; -fx-background-radius: 10");
        deckHeight = card1.getCardHeight();
    }
    public ResourceGoldCardController getCard1() {
        return card1;
    }

    public ResourceGoldCardController getCard2() {
        return card2;
    }

    public ResourceGoldCardController getCard3() {
        return card3;
    }

    public StarterCardController getStarterCard() {
        return starterCard;
    }

    public ObjectiveCardController getSecretObjCard() {
        return secretObjCard;
    }

    public double getDeckHeight() {
        return deckHeight;
    }
}
