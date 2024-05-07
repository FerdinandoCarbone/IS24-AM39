package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends TilePane implements Initializable {
    @FXML
    public CommandBoxController commands;
    @FXML
    public Button switchCards;
    @FXML
    private PlayerDeckController playerDeck;
    @FXML
    private StructRightController struct;
    private CardController cardToRemove;
    private boolean readyToPlace = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerDeck.receiveCards();
        for (int i = 0; i < 5; i++) {
            CardController tmpCard = (CardController) playerDeck.getChildren().get(i);
            tmpCard.setOnMouseClicked((MouseEvent mouseEvent) -> {
                cardToRemove = tmpCard;
                readyToPlace = true;
            });
        }
        for (int i = 0; i < struct.getChildren().size(); i++) {
            SlotController tmpSlot = (SlotController) struct.getChildren().get(i);
            tmpSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (readyToPlace) {
                    if (tmpSlot.isEmpty()) {
                        tmpSlot.setSlotCardView(cardToRemove.getShownImage());
                        playerDeck.getChildren().remove(cardToRemove);
                        readyToPlace = false;
                        tmpSlot.toFront();
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
