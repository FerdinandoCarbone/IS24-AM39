package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class PlayerDeckController extends HBox {

    @FXML
    private CardController card1;
    @FXML
    private CardController card2;
    @FXML
    private CardController card3;
    @FXML
    private CardController starterCard;
    @FXML
    private CardController secretObjCard;
    @FXML
    private Button cardReceiverButton;

    public PlayerDeckController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlayerDeck.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        cardReceiverButton.setOnAction(event -> receiveCards());
    }

    public void receiveCards() {
        card1.setupCard(DrawingDeck.drawCard(true));
        card2.setupCard(DrawingDeck.drawCard(true));
        card3.setupCard(DrawingDeck.drawCard(false));
        starterCard.setupCard(DrawingDeck.getTotalStartingCards().getFirst());
        secretObjCard.setupCard(DrawingDeck.getTotalObjectiveCards().getFirst());
    }

    public Image getImageFromClick(MouseEvent event) {
        Image ret;
        double cw = 256.0;
        if (event.getX() < cw) {
            ret = card1.getShownImage();
        } else if (event.getX() >= cw && event.getX() < 2* cw) {
            ret = card2.getShownImage();
        } else if (event.getX() >= 2* cw && event.getX() < 3* cw) {
            ret = card3.getShownImage();
        } else if (event.getX() >= 3* cw && event.getX() < 4* cw) {
            ret = starterCard.getShownImage();
        } else {
            ret = secretObjCard.getShownImage();
        }
        return ret;
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

    public Button getCardReceiverButton() {
        return cardReceiverButton;
    }
}
