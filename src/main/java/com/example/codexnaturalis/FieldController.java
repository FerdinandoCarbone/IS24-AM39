package com.example.codexnaturalis;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

import static com.example.codexnaturalis.CardDim.*;

public class FieldController extends Pane {

    SlotController centerSlot;
    final int numSlot = 2;
    final double totalHeight = 2*numSlot*(deltaHeight) + cardHeight;
    final double totalWidth = 2*numSlot*(deltaWidth) + cardWidth;
    private double scaleValue = 1.0;
    private final double zoomFactor = 1.1;

    public FieldController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Field.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        int cardPos = 0;
        System.out.println("CREATING FIELD...");
        for (int r = 0; r <= 2* numSlot; r++) {
            boolean extraCard = false;
            for (int offSetC = 0, counterC = 0; offSetC < numSlot; offSetC++, counterC++) {
                SlotController newSlot = new SlotController();
                newSlot.setPos(cardPos);
                cardPos++;
                newSlot.setLayoutY(r *(CardDim.cardHeight - CardDim.cornerHeight));
                if (r %2 == 0) {
                    newSlot.setLayoutX(counterC *(2*deltaWidth));
                    if (!extraCard) {
                        offSetC--;
                        extraCard = true;
                    }
                } else {
                    newSlot.setLayoutX((deltaWidth) + (counterC *(2*deltaWidth)));
                }
                this.getChildren().add(newSlot);
                if (r == numSlot && counterC == numSlot /2) {
                    newSlot.setSlotCardView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/center.jpg"))));
                    centerSlot = newSlot;
                    centerSlot.setEmpty(true);
                    centerSlot.setCenter(true);
                }
            }
        }
        centerSlot.toFront();
    }

    public int getNumSlot() {
        return numSlot;
    }

    public double getTotalHeight() {
        return totalHeight;
    }

    public double getTotalWidth() {
        return totalWidth;
    }
}
