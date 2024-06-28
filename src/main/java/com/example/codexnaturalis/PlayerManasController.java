package com.example.codexnaturalis;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
        contents.setStyle("-fx-background-color: #d0d0d0; -fx-background-radius: 15");
    }

    /**
     * Setup of mana and points GUI
     */
    private void setupManaView() {
        controllers = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ResourceGoldCard.ResourceElement tmp = ResourceGoldCard.ResourceElement.values()[i];
            SingleManaController tmpController = new SingleManaController(tmp);
            if (i == 4 || i == 5 || i == 6) {
                tmpController.setPadding(new Insets(3, 3, 3, 3));
            } else {
                tmpController.setPadding(new Insets(6, 3, 6, 3));
            }
            controllers.add(tmpController);
        }
        VBox pointsBox = new VBox();
        pointsBox.setAlignment(Pos.CENTER);
        Label pointsLabel = new Label("Points:");
        pointsLabel.setStyle("-fx-font-weight: bold");
        actualPoints = new Label("0");
        actualPoints.setStyle("-fx-font-weight: bold");
        pointsBox.getChildren().addAll(pointsLabel,actualPoints);
        pointsBox.setPadding(new Insets(0, 0, 0, 10));

        Insets standardPadding = new Insets(0, 5, 0, 5);

        VBox mushLeaf = new VBox();
        mushLeaf.getChildren().addAll(controllers.get(0),controllers.get(1));
        mushLeaf.setPadding(standardPadding);

        VBox wolfButt = new VBox();
        wolfButt.getChildren().addAll(controllers.get(2),controllers.get(3));
        wolfButt.setPadding(standardPadding);

        VBox els = new VBox();
        els.getChildren().addAll(controllers.get(4),controllers.get(5), controllers.get(6));
        els.setPadding(standardPadding);

        contents.getChildren().add(pointsBox);
        contents.getChildren().add(mushLeaf);
        contents.getChildren().add(wolfButt);
        contents.getChildren().add(els);
        for (int i = 0; i < contents.getChildren().size(); i++) {
            VBox tmp = (VBox) contents.getChildren().get(i);
            tmp.setAlignment(Pos.CENTER);
        }
        this.getChildren().add(contents);
    }

    public Label getActualPoints() {
        return actualPoints;
    }

    public ArrayList<SingleManaController> getControllers() {
        return controllers;
    }
}
