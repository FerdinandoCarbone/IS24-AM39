package com.example.codexnaturalis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PopPortChoiceController extends Pane {

    @FXML
    Button popButton;

    public PopPortChoiceController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PopPortChoice.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        this.setPadding(new Insets(10, 10, 10, 10));

        final Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("FUNZIONA");

        Pane popupPane = new Pane();
        popupPane.getChildren().add(new PortChoiceController());

        Scene popupScene = new Scene(popupPane);
        popupStage.setScene(popupScene);

        popButton.setOnAction((ActionEvent event) -> {popupStage.show();});


    }

}
