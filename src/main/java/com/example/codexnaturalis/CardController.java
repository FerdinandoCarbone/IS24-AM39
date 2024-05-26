package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class CardController extends Pane {
    //@FXML private Button flipButton;
    @FXML private ImageView cardImageView;
    private Image frontImage;
    private Image backImage;
    private Card card;
    private boolean isFront;
    private final double cardHeight = CardDim.cardHeight * 3;
    private final double cardWidth = CardDim.cardWidth * 3;

    public CardController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Card.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        isFront = true;
        cardImageView.setFitHeight(cardHeight);
        cardImageView.setFitWidth(cardWidth);
        this.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                flipCard();
            }
        });

    }

    public CardController(Card card) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Card.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        setupCard(card);
        isFront = true;
        //flipButton.setOnAction(event -> flipCard());
    }

    public void setupCard(Card newCard) {
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

    public Card getCard() {
        return card;
    }

    public double getCardHeight() {
        return cardHeight;
    }

    public double getCardWidth() {
        return cardWidth;
    }
}
