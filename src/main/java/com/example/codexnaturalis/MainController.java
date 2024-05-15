package com.example.codexnaturalis;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController extends Pane implements Initializable {
    @FXML public CommandBoxController commands;
    public static TextArea textArea;
    @FXML public Button sendButton;
    public static Button turnButton;
    @FXML public ScrollPane fieldScrollPane;
    @FXML public HBox turnBOX;
    @FXML private PlayerDeckController playerDeck;
    @FXML private StructRightController struct;
    private CardController cardToRemove;
    private boolean readyToPlace = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Carico main");
        fieldScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::middlePosition);
        });
        //todo: riattivare quando è il tuo turno
        turnButton = new Button("Confirm Turn");
        turnButton.setDisable(true);
        turnButton.setOnAction(event->genericTurnSender());
        turnBOX.getChildren().add(turnButton);
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
        playerDeck.setPadding(new Insets(25));
        playerDeck.setSpacing(20);
    }

    private void middlePosition() {
        double totalHeight = struct.getHeight();
        double visibleHeight = fieldScrollPane.getViewportBounds().getHeight();
        double middlePosition = (totalHeight-visibleHeight) / 2 /totalHeight*1.7;
        fieldScrollPane.setVvalue(middlePosition);
        totalHeight = struct.getWidth();
        visibleHeight = fieldScrollPane.getViewportBounds().getWidth();
        middlePosition = (totalHeight-visibleHeight) / 2 /totalHeight*1.7;
        fieldScrollPane.setHvalue(middlePosition);
    }

    public static void printMessage(String message) {
        textArea.appendText(message);
    }

    public void chatWrite(ActionEvent actionEvent) {
        //send to clientHandler
        String s = sendButton.getText();
    }

    public void genericTurnSender() {
    }
    public static void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
        alert.close();
    }
    public ResourceGoldCard pickCard(){
        ResourceGoldCard card=null;
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Java", "Java", "Python", "JavaScript");
        dialog.setTitle("Choice Dialog");
        dialog.setHeaderText("Select your favorite programming language:");
        dialog.setContentText("Language:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(language -> {
            System.out.println("Your favorite programming language: " + language);
        });
        dialog.close();
        return card;
    }
    public static void setTurnButton(boolean b){
        turnButton.setDisable(b);
    }
}
