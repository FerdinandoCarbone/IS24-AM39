package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class RowController extends HBox {

    @FXML
    private SlotController c0;
    @FXML
    private SlotController c1;
    @FXML
    private SlotController c2;

    public RowController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Row.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

}
