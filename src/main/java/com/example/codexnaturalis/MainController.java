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
    private CardController cardToRemove;
    private boolean readyToPlace = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerDeck.receiveCards();
        for (int i = 0; i < 5; i++) {
            CardController tmpCard = (CardController) playerDeck.getChildren().get(i);
            System.out.println(tmpCard.getCard().getIdCard());
            tmpCard.setOnMouseClicked((MouseEvent mouseEvent) -> {
                cardToRemove = tmpCard;
                readyToPlace = true;
            });
        }
        for (int i = 0; i < row1.getChildren().size(); i++) {
            SlotController tmpSlot = (SlotController) row1.getChildren().get(i);
            tmpSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (readyToPlace) {
                    if (tmpSlot.isEmpty()) {
                        tmpSlot.setSlotCardView(cardToRemove.getShownImage());
                        playerDeck.getChildren().remove(cardToRemove);
                        readyToPlace = false;
                    } else {
                        System.out.println("SLOT GIA' OCCUPATO");
                    }
                } else {
                    System.out.println("PRIMA SELEZIONA UNA CARTA DAL DECK");
                }
            });
        }
    }

}
