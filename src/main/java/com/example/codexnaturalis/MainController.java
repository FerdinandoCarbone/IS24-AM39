package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    @FXML
    private PlayerDeckController playerDeck;
    @FXML
    private FieldController field;
    @FXML
    private ZoomPaneController zoomPane;
    @FXML
    Pane mainPane;
    @FXML
    Button zoomButton;
    @FXML
    PopPortChoiceController portChoice;
    private CardController cardToRemove;
    private boolean readyToPlace = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        playerDeck.receiveCards();

//        for (int i = 0; i < 5; i++) {
//            CardController tmpCard = (CardController) playerDeck.getChildren().get(i);
//            tmpCard.setOnMouseClicked((MouseEvent mouseEvent) -> {
//                cardToRemove = tmpCard;
//                readyToPlace = true;
//            });
//        }
//
//        for (int i = 0; i < field.getChildren().size(); i++) {
//            SlotController tmpSlot = (SlotController) field.getChildren().get(i);
//            tmpSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
//                if (readyToPlace) {
//                    if (tmpSlot.isEmpty()) {
//                        tmpSlot.setSlotCardView(cardToRemove.getShownImage());
//                        playerDeck.getChildren().remove(cardToRemove);
//                        readyToPlace = false;
//                        tmpSlot.toFront();
//                    } else {
//                        System.out.println("SLOT GIA' OCCUPATO");
//                    }
//                } else {
//                    System.out.println("PRIMA SELEZIONA UNA CARTA DAL DECK");
//                }
//            });
//
//        }
//
//        playerDeck.setLayoutY(field.getTotalHeight() + cardHeight);
//        zoomPane.setLayoutX(field.getTotalWidth() + cornerWidth);
//
//        zoomButton.setLayoutX(field.getTotalWidth() + cornerWidth);
//        zoomButton.setLayoutY(zoomPane.getTotalHeight() + cornerHeight);
    }

}
