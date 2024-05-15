package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class ResourceGoldCardController extends Pane {
    @FXML
    private Button flipButton;
    @FXML
    private ImageView cardImageView;
    private Image frontImage;
    private Image backImage;
    private ResourceGoldCard card;
    private boolean isFront;
    private final double cardHeight = GlobalVars.cardHeight * 1.5;
    private final double cardWidth = GlobalVars.cardWidth * 1.5;

    public ResourceGoldCardController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Card.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        isFront = true;
        flipButton.setOnAction(event -> flipCard());
        cardImageView.setFitHeight(cardHeight);
        cardImageView.setFitWidth(cardWidth);
        flipButton.setLayoutX(GlobalVars.cardWidth - GlobalVars.cornerWidth);
        flipButton.setLayoutY(GlobalVars.cornerHeight);
//        flipButton.setGraphic();
    }

    public void setupCard(ResourceGoldCard newCard) {
        card = newCard;
        frontImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[0])));
        backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[1])));
        cardImageView.setImage(isFront ? frontImage : backImage);
    }

    public void flipCard() {
        if (isFront) {
            cardImageView.setImage(backImage);
            isFront = false;
        } else {
            cardImageView.setImage(frontImage);
            isFront = true;
        }
        card.setIsPlacedFront(isFront);
    }

    public Image getShownImage() {
        if (isFront) {
            return frontImage;
        } else {
            return backImage;
        }
    }

    public void setCard(ResourceGoldCard card) {
        this.card = card;
    }

    public ImageView getCardImageView() {
        return cardImageView;
    }

    public boolean isFront() {
        return isFront;
    }

    public ResourceGoldCard getCard() {
        return card;
    }

    public double getCardHeight() {
        return cardHeight;
    }

    public double getCardWidth() {
        return cardWidth;
    }
}
