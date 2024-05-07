package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static com.example.codexnaturalis.CardDim.*;

public class StructRightController extends Pane {

    /*@FXML
    private SlotController c0;
    @FXML
    private SlotController c1;
    @FXML
    private SlotController c2;*/
    private int value;

    private SlotController[][] slotGrid;

    public StructRightController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("StructLeft.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        value = 50;
        slotGrid = new SlotController[value][value];
        slotGridInit(value);
    }
    private void slotGridInit(int value) throws IOException {
        for (int i = 0; i < value; i++) {
            for (int j = 0; j < value; j++) {
                if((i%2==0&&j%2==1) || (i%2==1&&j%2==0)) continue;
                else {
                    this.slotGrid[i][j] = new SlotController();
                }
            }
        }
    }
}
