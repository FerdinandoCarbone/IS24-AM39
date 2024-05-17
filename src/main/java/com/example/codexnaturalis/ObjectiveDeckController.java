package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class ObjectiveDeckController extends Pane {

    @FXML
    ImageView card1;
    @FXML
    ImageView card2;
    @FXML
    ImageView card3;
    @FXML
    ImageView card4;
    @FXML
    ImageView public1;
    @FXML
    ImageView public2;
    private int offset = 5;
    private double height = GlobalVars.cardHeight*2;
    private double width = GlobalVars.cardWidth*2;
    Image emptyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/RoundedCorners101b.png")));

    public ObjectiveDeckController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ObjectiveDeck.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();

        for (int i = 0; i < this.getChildren().size(); i++) {
            ImageView tmp = (ImageView) this.getChildren().get(i);
            tmp.setStyle("-fx-border-color: black");
            tmp.setImage(emptyImage);
            tmp.setFitHeight(height);
            tmp.setFitWidth(width);
            tmp.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);\n");
            if (i < 4) {
                tmp.setLayoutY(offset*i);
            }
        }

        public1.setLayoutY(offset*3 + height + 2*offset);
        public2.setLayoutY(offset*3 + 2*height + 4*offset);
    }




}
