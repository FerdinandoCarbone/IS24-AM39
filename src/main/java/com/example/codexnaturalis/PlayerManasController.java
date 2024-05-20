package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerManasController extends Pane {
    private HBox contents;
    private Label actualPoints;
    private ArrayList<SingleManaController> controllers;
    public PlayerManasController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/PlayerManasBar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        contents = new HBox();
        setupManaView();
    }

    private void setupManaView() {
        controllers = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ResourceGoldCard.ResourceElement tmp = ResourceGoldCard.ResourceElement.values()[i];
            controllers.add(new SingleManaController(tmp));
        }
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Label pointsLabel = new Label("Points:");
        actualPoints = new Label("0");
        actualPoints.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(pointsLabel,actualPoints);
        contents.getChildren().addAll(controllers);
        contents.getChildren().add(vBox);
        this.getChildren().add(contents);
    }


}
