package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;

public class CommandBoxController extends TilePane {
    @FXML public Button command1;
    @FXML public Button command2;
    @FXML public Button command3;
    public CommandBoxController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Command.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        command1.setOnAction(event->switchCodex());
        command2.setOnAction(event -> switchDeck());
    }
    @FXML public void switchCodex(){

    }
    @FXML public void switchDeck(){
        //todo: robe da fare con player deck non con player deck
        //PlayerDeckController.getSecretObjCard();
    }
}
