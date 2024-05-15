package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class LabelTextController extends HBox {

    @FXML
    Label label;
    @FXML
    TextField textField;

    public LabelTextController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LabelText.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        this.setPadding(new Insets(10, 10, 10, 10));
        label.setMaxWidth(50);
        label.setMinWidth(50);
        textField.setMaxWidth(100);
        textField.setMinWidth(100);
    }

}
