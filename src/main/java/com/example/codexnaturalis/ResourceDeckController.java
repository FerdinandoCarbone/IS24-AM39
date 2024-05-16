package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class ResourceDeckController extends Pane {

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
    private final int offset = 5;
    private final double height = GlobalVars.cardHeight*2;
    private final double width = GlobalVars.cardWidth*2;

    private void setupImgsStyle() {
        for (int i = 0; i < 4; i++) {
            ImageView tmp = (ImageView) this.getChildren().get(i);
            tmp.setStyle("-fx-border-color: black");
            tmp.setFitHeight(height);
            tmp.setFitWidth(width);
            tmp.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);\n");
            tmp.setLayoutY(offset*i);
        }
        img1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/RRCB.jpg"))));
        img2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/PRCB.jpg"))));
        img3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/GRCB.jpg"))));
        img4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/BRCB.jpg"))));
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
        public1.setupCard(DrawingDeck.drawCard(true));
        public2.setupCard(DrawingDeck.drawCard(true));
    }

    public ResourceDeckController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ResourceDeck.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();
        setupPublicCards();
        setupImgsStyle();
        setupPublicsStyle();

        public1.setLayoutY(offset*3 + height + 2*offset);
        public2.setLayoutY(offset*3 + 2*height + 4*offset);
    }


    public double getDeckHeight() {
        return height;
    }

    public double getDeckWidth() {
        return width;
    }
}
