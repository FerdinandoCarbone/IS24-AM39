package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import java.io.IOException;

public class CardInfoBoxController {
    public CardInfoBoxController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/CardInfoBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
}
