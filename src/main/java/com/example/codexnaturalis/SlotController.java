package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class SlotController extends Pane {

    @FXML
    ImageView slotCardView = null;
    private final Image emptyImage =  new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/empty.jpg")));
    private boolean isEmpty = true;

    public SlotController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Slot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
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
}
