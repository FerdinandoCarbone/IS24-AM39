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
    @FXML public RadioButton selectButton;
    private Image frontImage;
    private Image backImage;
    private Card card;

    public SelectableCardController(Card cardToSelect) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/selectableCard.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        setupCard(cardToSelect);
        cardImageView.setImage(frontImage);
        /*HBox hbox = new HBox(cardImageView,flipButton);
        this.getChildren().add(hbox);*/

    }
    public SelectableCardController(Card cardToSelect,boolean bool) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/selectableCard.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        card = cardToSelect;
        if(bool){
            frontImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[0])));
            selectButton.setUserData(1);
            cardImageView.setImage(frontImage);
        }
        else {
            backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[1])));
            selectButton.setUserData(2);
            cardImageView.setImage(backImage);
        }
        /*HBox hbox = new HBox(cardImageView,flipButton);
        this.getChildren().add(hbox);*/

    }
    public static ArrayList<SelectableCardController> toSelectableArraylist(ArrayList<Card> cards) throws IOException {
        ArrayList<SelectableCardController> selectables = new ArrayList<>();
        for(Card c : cards){
            selectables.add(new SelectableCardController(c));
        }
        return selectables;
    }

    public Card getCard() {
            return card;
    }

    public void setupCard(Card newCard) {
        card = newCard;
        selectButton.setUserData(card.getIdCard());
        frontImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[0])));
        backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getArtRef()[1])));
    }
}
