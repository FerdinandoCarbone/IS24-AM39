package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class SingleManaController extends HBox {
    public ImageView imageView;
    public Label points;
    public ResourceGoldCard.ResourceElement element;
    public SingleManaController(ResourceGoldCard.ResourceElement manaImg){
        element = manaImg;
        points = new Label("0");
        points.setAlignment(Pos.CENTER);
        points.setStyle("-fx-font-size: 15; -fx-font-weight: bold");
        points.setPadding(new Insets(0, 0, 0, 2));
        imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/"+manaImg.toString()+".png"))));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        this.getChildren().addAll(imageView,points);
    }

    public void setPoints(int playerPoints) {
        points.setText(String.valueOf(playerPoints));
    }


}
