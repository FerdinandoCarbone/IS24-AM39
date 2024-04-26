package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class SlotController extends Pane {

    @FXML
    ImageView slotCardView;

    public SlotController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Slot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void setSlotCardView(Image slotImage) {
        this.slotCardView.setImage(slotImage);
    }
}
