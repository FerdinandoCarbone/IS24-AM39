package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;

import static com.example.codexnaturalis.CardDim.cardHeight;
import static com.example.codexnaturalis.CardDim.cardWidth;

public class SlotController extends Pane {

    @FXML
    ImageView slotCardView = null;
    NonObjectiveCard card = null;
    Pair<Integer, Integer> coords;
    private final Image emptyImage =  new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/empty.jpg")));
    private boolean isEmpty = true;
    private boolean isCenter = false;

    public SlotController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Slot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        slotCardView.setFitHeight(cardHeight);
        slotCardView.setFitWidth(cardWidth);
    }

    public void setSlotCardView(Image slotImage) {
        if (!slotImage.equals(emptyImage)) {
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
}
