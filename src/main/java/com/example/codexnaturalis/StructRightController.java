package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static com.example.codexnaturalis.CardDim.*;

public class StructRightController extends Pane {

    @FXML
    private SlotController c0;
    @FXML
    private SlotController c1;
    @FXML
    private SlotController c2;

    public StructRightController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("StructLeft.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        c0.setLayoutX(cardWidth-cornerWidth);
        c1.setLayoutY(cardHeight-cornerHeight);
        c2.setLayoutY(2*cardHeight-2*cornerHeight);
        c2.setLayoutX(cardWidth-cornerWidth);

    }

}
