package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class PlayerDeckController extends HBox {

    @FXML
    private CardController card1;
    @FXML
    private CardController card2;
    @FXML
    private CardController card3;
    @FXML
    private CardController starterCard;
    @FXML
    private CardController secretObjCard;
    private final Image emptyImage =  new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/empty.jpg")));
    private int position = 0 ;


    public PlayerDeckController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlayerDeck.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

    }

    public void receiveCards() {
        card1.setupCard(DrawingDeck.drawCard(true));
        card2.setupCard(DrawingDeck.drawCard(true));
        card3.setupCard(DrawingDeck.drawCard(false));
        starterCard.setupCard(DrawingDeck.getTotalStartingCards().getFirst());
        secretObjCard.setupCard(DrawingDeck.getTotalObjectiveCards().getFirst());
    }
    public void drawGoldCard(CardDrawableController cardDrawable) { //TO DO: non le prendo dal mazzo random, ma dalla carta che è girata in DecksController
        ResourceGoldCard CardX = (ResourceGoldCard) cardDrawable.getDeckGoldController().getCard();
        switch (position){
            case 0:
                card1.setupCard(CardX);

                break;
            case 1:
                card2.setupCard(CardX);
                break;
            case 2:
                card3.setupCard(CardX);
                break;
            case 3:
                starterCard.setupCard(CardX);
                break;
        }
        cardDrawable.redraw(false);
    }
    public void drawResCard(CardDrawableController cardDrawable) { //TO DO: non le prendo dal mazzo random, ma dalla carta che è girata in DecksController
        ResourceGoldCard CardX = (ResourceGoldCard) cardDrawable.getDeckResController().getCard();
        switch (position){
            case 0:
                card1.setupCard(CardX);

                break;
            case 1:
                card2.setupCard(CardX);
                break;
            case 2:
                card3.setupCard(CardX);
                break;
            case 3:
                starterCard.setupCard(CardX);
                break;
        }
        cardDrawable.redraw(true);
    }

    public void setEmpty(int pos){
        position=pos;
        switch (pos){
            case 0:
                card1.setupEmptyCard();
                break;
            case 1:
                card2.setupEmptyCard();
                break;
            case 2:
                card3.setupEmptyCard();
                break;
            case 3:
                starterCard.setupEmptyCard();
                break;
            default:
                throw new RuntimeException();
        }
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

    public CardController getStarterCard() {
        return starterCard;
    }

    public CardController getSecretObjCard() {
        return secretObjCard;
    }


}
