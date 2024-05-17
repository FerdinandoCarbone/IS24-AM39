package com.example.codexnaturalis;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class TabelloneController extends Pane {
    @FXML
    ImageView tabView;
    private final Image tabellone =  new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Tabellone.png")));

    public TabelloneController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Tabellone.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        tabView.setImage(tabellone);
    }

}
