package com.example.codexnaturalis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends Pane implements Initializable {
    @FXML public CommandBoxController commands;
    @FXML public TextArea textArea;
    @FXML public Button sendButton;
    @FXML public Button turnButton;
    @FXML private PlayerDeckController playerDeck;
    @FXML private StructRightController struct;
    private CardController cardToRemove;
    private boolean readyToPlace = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Carico main");
        //todo: riattivare quando è il tuo turno
        turnButton.setDisable(true);
        try {
            playerDeck.receiveCards();
        } catch (IOException e) {
            printMessage(e.getMessage());
        }
        for (int i = 0; i < 3; i++) {
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

    private void printMessage(String message) {
        textArea.appendText(message);
    }

    public void chatWrite(ActionEvent actionEvent) {
        //send to clientHandler
        String s = sendButton.getText();
    }

    public void genericTurnSender(ActionEvent actionEvent) {
    }
}
