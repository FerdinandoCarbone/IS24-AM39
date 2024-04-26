package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private PlayerDeckController playerDeck;
    @FXML
    private RowController row1;
    private Image cardToPlace = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/empty.jpg")));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerDeck.receiveCards();
    }

    @FXML
    public void selectCardToPlace(MouseEvent event) {
        cardToPlace = playerDeck.getImageFromClick(event);
    }

    @FXML
    public void placeCardOnSlot(MouseEvent event) {
        row1.setSlotImageWhenClicked(event, cardToPlace);
    }

}
