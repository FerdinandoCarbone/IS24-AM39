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
import java.util.Objects;

public class PlayerDeckController extends VBox {

    @FXML private ResourceGoldCardController card1;
    @FXML private ResourceGoldCardController card2;
    @FXML private ResourceGoldCardController card3;
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
    public void resetCard(int childIndex) {
        ResourceGoldCardController cardToReset = (ResourceGoldCardController) this.getChildren().get(childIndex);
        Image emptyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/empty.png")));
        cardToReset.getCardImageView().setImage(emptyImage);
        cardToReset.setFrontImage(emptyImage);
        cardToReset.setBackImage(emptyImage);
        cardToReset.setCard(null);
    }
    public ResourceGoldCardController getCard1() {
        return card1;
    }

    public ResourceGoldCardController getCard2() {
        return card2;
    }

    public ResourceGoldCardController getCard3() {
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
    public void setCard1(ResourceGoldCardController card1) {
        this.card1 = card1;
    }

    public void setCard2(ResourceGoldCardController card2) {
        this.card2 = card2;
    }

    public void setCard3(ResourceGoldCardController card3) {
        this.card3 = card3;
    }
    //    public Button getCardReceiverButton() {
//        return cardReceiverButton;
//    }
}
