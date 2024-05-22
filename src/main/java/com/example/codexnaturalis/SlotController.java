package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;

import static com.example.codexnaturalis.CardDim.*;

public class SlotController extends Pane {

    @FXML
    ImageView slotCardView = null;
    @FXML
    Label emptyLabel;
    NonObjectiveCard card = null;
    Pair<Integer, Integer> coords;
    private boolean isEmpty = true;
    private boolean isCenter = false;

    public SlotController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Slot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        slotCardView.setFitHeight(slotHeight);
        slotCardView.setFitWidth(slotWidth);
        emptyLabel.setLayoutX(slotWidth/2 - 16);
        emptyLabel.setLayoutY(slotHeight/2 - 8);
        enableEmptyStuff();
        if (isEmpty) {
            this.setCursor(Cursor.HAND);
        } else {
            this.setCursor(Cursor.DEFAULT);
        }
    }

    public void enableEmptyStuff() {
        emptyLabel.setDisable(false);
        this.setStyle("-fx-background-radius: 25; -fx-border-color: GREY");
    }
    public void disableEmptyStuff() {
        emptyLabel.setDisable(true);
        this.setStyle("-fx-border-width: 0");
    }


    public void setSlotCardView(Image slotImage) {
        if (slotImage != null) {
            if (isEmpty) {
                this.slotCardView.setImage(slotImage);
                slotCardView.setCursor(Cursor.DEFAULT);
                isEmpty = false;
            }
        }
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
        if(empty) {
            enableEmptyStuff();
            slotCardView.setImage(null);
        }
    }
    public void setCoords(Pair<Integer, Integer> coords) {
        this.coords = coords;
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public Pair<Integer, Integer> getCoords() {
        return coords;
    }

    public Label getEmptyLabel() {
        return emptyLabel;
    }
}
