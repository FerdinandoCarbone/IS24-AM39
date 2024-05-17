package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class GoldDeckController extends Pane {

    @FXML
    ImageView img1;
    @FXML
    ImageView img2;
    @FXML
    ImageView img3;
    @FXML
    ImageView img4;
    @FXML
    ResourceGoldCardController public1;
    @FXML
    ResourceGoldCardController public2;
    private int offset = 5;
    private double height = GlobalVars.cardHeight*2;
    private double width = GlobalVars.cardWidth*2;

    private void setupImgsStyle() {
        for (int i = 0; i < 4; i++) {
            ImageView tmp = (ImageView) this.getChildren().get(i);
            tmp.setStyle("-fx-border-color: black");
            tmp.setFitHeight(height);
            tmp.setFitWidth(width);
            tmp.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);\n");
            tmp.setLayoutY(offset*i);
        }
        img1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/RoundedCorners41b.png"))));
        img2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/RoundedCorners51b.png"))));
        img3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/RoundedCorners61b.png"))));
        img4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/RoundedCorners71b.png"))));
    }
    private void setupPublicsStyle() {
        for (int i = 4; i < this.getChildren().size(); i++) {
            ResourceGoldCardController tmp = (ResourceGoldCardController) this.getChildren().get(i);
            tmp.setStyle("-fx-border-color: black");
            tmp.getCardImageView().setFitHeight(height);
            tmp.getCardImageView().setFitWidth(width);
            tmp.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);\n");
        }
    }
    private void setupPublicCards() {
        public1.setupCard(DrawingDeck.drawCard(false));
        public2.setupCard(DrawingDeck.drawCard(false));
    }

    public GoldDeckController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldDeck.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();
        setupPublicCards();
        setupImgsStyle();
        setupPublicsStyle();

        public1.setLayoutY(offset*3 + height + 2*offset);
        public2.setLayoutY(offset*3 + 2*height + 4*offset);
    }




}
