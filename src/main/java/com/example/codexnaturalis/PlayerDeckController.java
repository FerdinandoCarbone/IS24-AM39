package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class PlayerDeckController extends Pane {

    @FXML
    private ResourceGoldCardController card1;
    @FXML
    private ResourceGoldCardController card2;
    @FXML
    private ResourceGoldCardController card3;
    @FXML
    private StarterCardController starterCard;
//    @FXML
//    private ObjectiveCardController secretObjCard;
    private final double containerHeight;
    private final double containerWidth;

    private void setupCardsDistance() {
        for (int i = 0; i < this.getChildren().size(); i++) {
            this.getChildren().get(i).setLayoutX(i*(card1.getCardWidth()+10));
        }
    }

    public PlayerDeckController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlayerDeck.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        setupCardsDistance();
        containerHeight = card1.getCardHeight();
        containerWidth = 4*(card1.getCardWidth()+10)-10;

    }

    public void resetCard(int childIndex) {
        ResourceGoldCardController cardToReset = (ResourceGoldCardController) this.getChildren().get(childIndex);
        Image emptyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/empty.png")));
        cardToReset.getCardImageView().setImage(emptyImage);
        cardToReset.setFrontImage(emptyImage);
        cardToReset.setBackImage(emptyImage);
        cardToReset.setCard(null);
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

//    public ObjectiveCardController getSecretObjCard() {
//        return secretObjCard;
//    }

    public double getContainerHeight() {
        return containerHeight;
    }
    public double getContainerWidth() {return containerWidth;}
}
