package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/StructRight.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        value = 35;
        createGrid();
        /*c0.setLayoutX(cardWidth - cornerWidth);
        c1.setLayoutY(cardHeight - cornerHeight);
        c2.setLayoutY(2 * cardHeight - 2 * cornerHeight);
        c2.setLayoutX(cardWidth - cornerWidth);*/
        //placeCard(PlayerDeckController.getStarterCard());

    }


    private void createGrid() throws IOException {
        slotGrid = new SlotController[value][value];
        for (int i = 0; i < value; i++) {
            for (int j = 0; j < value; j++) {
                if ((i % 2 == 0 && j%2==0) || i%2==1 && j%2==1) {
                    slotGrid[i][j] = new SlotController();
                    slotGrid[i][j].setLayoutX(j*cardWidth-j*cornerWidth);
                    slotGrid[i][j].setLayoutY(i*cardHeight-i*cornerHeight);
                    this.getChildren().add(slotGrid[i][j]);
                }
                else slotGrid[i][j] = null;
            }
        }
    }
}
