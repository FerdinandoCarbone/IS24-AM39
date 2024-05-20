package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class SingleManaController extends VBox {
    public ImageView imageView;
    public Label points;
    public ResourceGoldCard.ResourceElement element;
    public SingleManaController(ResourceGoldCard.ResourceElement manaImg){
        element = manaImg;
        points = new Label("0");
        points.setAlignment(Pos.CENTER);
        imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/"+manaImg.toString()+".png"))));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);
        this.getChildren().addAll(imageView,points);
    }

    public void setPoints(Label points) {
        this.points = points;
    }

}
