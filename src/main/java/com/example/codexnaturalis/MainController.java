package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private CardDrawableController cardDrawable;

    @FXML
    private PlayerDeckController playerDeck;
    @FXML
    private StructRightController struct;
    @FXML
    private StructRightController struct2, struct3, struct4;
    private CardController cardToRemove;
    private CardController cardToTake;
    @FXML
    private TabelloneController table;
    private boolean readyToPlace = false;
    private int pos = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        playerDeck.receiveCards();
        cardDrawable.showDrawableCards();

        cardDrawable.getDeckGoldController().getChooseButton().setOnAction(event -> playerDeck.drawGoldCard(cardDrawable));
        cardDrawable.getDeckResController().getChooseButton().setOnAction(event -> playerDeck.drawResCard(cardDrawable));


        for (int i = 0; i < 4; i++) {
            CardController tmpCard = (CardController) playerDeck.getChildren().get(i);
            int finalI = i;
            tmpCard.setOnMouseClicked((MouseEvent mouseEvent) -> {
                cardToRemove = tmpCard;
                readyToPlace = true;
                pos= finalI;
            });
        }
        for (int i = 0; i < struct.getChildren().size(); i++) {
            SlotController tmpSlot = (SlotController) struct.getChildren().get(i);
            tmpSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (readyToPlace) {
                    if (tmpSlot.isEmpty()) {
                        tmpSlot.setSlotCardView(cardToRemove.getShownImage());
                        if(pos==3) playerDeck.getChildren().remove(cardToRemove);
                        playerDeck.setEmpty(pos);
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

        //mazzo drawable
        for (int i = 0; i < 4; i++) {
            CardController tmpCard = (CardController) cardDrawable.getChildren().get(i);
            int finalI = i;
            tmpCard.setOnMouseClicked((MouseEvent mouseEvent) -> {
                cardToTake = tmpCard;
                ResourceGoldCard cardX = (ResourceGoldCard) cardToTake.getCard();
                switch (pos){
                    case 0:
                        playerDeck.getCard1().setupCard(cardX);
                        break;
                    case 1:
                        playerDeck.getCard2().setupCard(cardX);
                        break;
                    case 2:
                        playerDeck.getCard3().setupCard(cardX);
                        break;
                }
                cardDrawable.restore(finalI);
            });
        }
    }

}
