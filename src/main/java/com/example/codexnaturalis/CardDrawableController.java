package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class CardDrawableController extends Pane {
    @FXML
    private Button drawGoldButton;
    @FXML
    private Button drawResButton;
    @FXML
    private CardController cardR1;
    @FXML
    private CardController cardR2;
    @FXML
    private CardController cardG1;
    @FXML
    private CardController cardG2;


    public CardDrawableController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CardDrawable.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

    }

    public void showDrawableCards() {   //da pescare dalle carte già disposte a terra dei due mazzi
        cardR1.setupCard(DrawingDeck.drawCard(true));
        cardR2.setupCard(DrawingDeck.drawCard(true));
        cardG1.setupCard(DrawingDeck.drawCard(false));
        cardG2.setupCard(DrawingDeck.drawCard(false));
    }

    public void restore(int p){
        switch (p){
            case 2:
                cardG1.setupCard(DrawingDeck.drawCard(false));
                break;
            case 3:
                cardG2.setupCard(DrawingDeck.drawCard(false));
                break;
            case 0:
                cardR1.setupCard(DrawingDeck.drawCard(true));
                break;
            case 1:
                cardR2.setupCard(DrawingDeck.drawCard(true));
                break;
        }
    }



    public CardController getCard1() {
        return cardR1;
    }

    public CardController getCard2() {
        return cardR2;
    }

    public CardController getCard3() {
        return cardG1;
    }

    public CardController getCard4() {
        return cardG2;
    }

    public Button getDrawGoldButton() { return drawGoldButton; }

    public Button getDrawResButton() { return drawResButton; }
}
