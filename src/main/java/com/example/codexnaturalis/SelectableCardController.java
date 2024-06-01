package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SelectableCardController extends Pane {
    @FXML public ImageView cardImageView;
//    @FXML public RadioButton selectButton;
    public Image frontImage;
    public Image backImage;
    private Card card;

    public SelectableCardController(Card cardToSelect) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/selectableCard.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        setupCard(cardToSelect);
        cardImageView.setImage(frontImage);
    }
    public SelectableCardController(Card cardToSelect,boolean bool) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/selectableCard.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        card = cardToSelect;
        if(bool){
            frontImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[0])));
            backImage = null;
            cardImageView.setImage(frontImage);
        }
        else {
            backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[1])));
            frontImage = null;
            cardImageView.setImage(backImage);
        }

    }

    public Card getCard() {
            return card;
    }

    public void setupCard(Card newCard) {
        card = newCard;
        System.out.println(card);
        frontImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[0])));
        backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[1])));
    }
}
