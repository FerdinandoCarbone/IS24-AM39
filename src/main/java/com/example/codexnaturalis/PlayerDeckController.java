package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class PlayerDeckController extends VBox {

    @FXML
    private CardController card1;
    @FXML
    private CardController card2;
    @FXML
    private CardController card3;
    private CardController starterCard;
    private CardController secretObjCard;
//    @FXML
//    private Button cardReceiverButton;

    public PlayerDeckController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/PlayerDeck.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        //this.setSpacing(168.0);
        fxmlLoader.load();

//        cardReceiverButton.setOnAction(event -> receiveCards());
    }

    public void receiveCards() throws IOException {
        card1.setupCard(DrawingDeck.drawCard(true));
        card2.setupCard(DrawingDeck.drawCard(true));
        card3.setupCard(DrawingDeck.drawCard(false));
        starterCard = new CardController();
        secretObjCard = new CardController();
        starterCard.setupCard(DrawingDeck.getTotalStartingCards().getFirst());
        secretObjCard.setupCard(DrawingDeck.getTotalObjectiveCards().getFirst());
    }

    public CardController getCard1() {
        return card1;
    }

    public CardController getCard2() {
        return card2;
    }

    public CardController getCard3() {
        return card3;
    }

    public CardController getStarterCard() {
        return starterCard;
    }

    public CardController getSecretObjCard() {
        return secretObjCard;
    }

//    public Button getCardReceiverButton() {
//        return cardReceiverButton;
//    }
}
