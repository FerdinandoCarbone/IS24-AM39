package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerDeckController extends VBox {

    @FXML
    private CardController card1;
    @FXML
    private CardController card2;
    @FXML
    private CardController card3;
    private CardController starterCard;
    private ArrayList<CardController> objCards;
//    @FXML
//    private Button cardReceiverButton;

    public PlayerDeckController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/PlayerDeck.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        //this.setSpacing(168.0);
        fxmlLoader.load();
        objCards = new ArrayList<>();
        starterCard = new CardController();

//        cardReceiverButton.setOnAction(event -> receiveCards());
    }

    public void setNotPlayableCards(CardController starterCard,ArrayList<CardController> objectiveCards) throws IOException {
        this.starterCard = starterCard;
    }

    public CardController getCard1() {
        return card1;
    }

    public CardController getCard2() {
        return card2;
    }

    public CardController getCard3() {
        return card3;
    }

    public  CardController getStarterCard() {
        return starterCard;
    }

    public  ArrayList<CardController> getObjCards() {
        return objCards;
    }
    public CardController getSecretObjCard(){
        return objCards.getLast();
    }
    public ArrayList<CardController> getCommonObj(){
        ArrayList<CardController> common=new ArrayList<>();
        common.add(objCards.get(0));
        common.add(objCards.get(1));
        return common;
    }
    public void setCard1(CardController card1) {
        this.card1 = card1;
    }

    public void setCard2(CardController card2) {
        this.card2 = card2;
    }

    public void setCard3(CardController card3) {
        this.card3 = card3;
    }
    //    public Button getCardReceiverButton() {
//        return cardReceiverButton;
//    }
}
