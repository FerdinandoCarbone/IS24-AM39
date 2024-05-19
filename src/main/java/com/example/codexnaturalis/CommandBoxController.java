package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CommandBoxController extends VBox {
    @FXML public Button command3;
    public CommandBoxController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Command.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

}
