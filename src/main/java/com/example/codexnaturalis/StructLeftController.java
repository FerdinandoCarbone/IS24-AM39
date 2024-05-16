package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static com.example.codexnaturalis.CardDim.*;

public class StructLeftController extends Pane {

    @FXML
    private SlotController c0;
    @FXML
    private SlotController c1;
    @FXML
    private SlotController c2;
    @FXML
    private SlotController c3;
    @FXML
    private SlotController c4;
    @FXML
    private SlotController c5;
    @FXML
    private SlotController c6;

    public StructLeftController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("StructLeft.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        c1.setLayoutX(cardWidth-cornerWidth);
        c1.setLayoutY(cardHeight-cornerHeight);
        c2.setLayoutY(2*cardHeight-2*cornerHeight);
        c3.setLayoutY(3*cardHeight-3*cornerHeight);
        c3.setLayoutX(cardWidth-cornerWidth);
        c4.setLayoutY(4*cardHeight-4*cornerHeight);
        c4.setLayoutX(cardWidth-cornerWidth);
        c5.setLayoutY(cardHeight-cornerHeight);
        c5.setLayoutX(2*cardWidth-2*cornerWidth);

        c6.setLayoutY(3*cardHeight-3*cornerHeight);
    }

}
