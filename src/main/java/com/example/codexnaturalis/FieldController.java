package com.example.codexnaturalis;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static com.example.codexnaturalis.CardDim.*;

public class FieldController extends Pane {

    SlotController centerSlot;
    final int fieldSize = matrixSize;
    final double totalHeight = fieldSize *(deltaHeight) + cardHeight;
    final double totalWidth = fieldSize *(deltaWidth) + cardWidth;
    private HashMap<Pair<Integer, Integer>, Integer> fieldMap = new HashMap<>();

    public FieldController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Field.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        System.out.println("CREATING FIELD...");
        int childIndex = 0;
        for (int r = 0; r < fieldSize; r++) {
            boolean dispariFlag = false;
            for (int c = 0, offsetC = 0; c < fieldSize; c += 2, offsetC++, childIndex++) {
                SlotController newSlot = new SlotController();
                if (r % 2 == 0) {
                    Pair<Integer, Integer> cords = new Pair<>(r, c);
                    newSlot.setCoords(cords);
                    fieldMap.put(cords, childIndex);
                    newSlot.setLayoutY(r *(CardDim.cardHeight - CardDim.cornerHeight));
                    newSlot.setLayoutX(offsetC * (2*deltaWidth));
                } else {
                    if (!dispariFlag) {
                        c++;
                        dispariFlag = true;
                    }
                    Pair<Integer, Integer> cords = new Pair<>(r, c);
                    newSlot.setCoords(cords);
                    fieldMap.put(cords, childIndex);
                    newSlot.setLayoutY(r *(CardDim.cardHeight - CardDim.cornerHeight));
                    newSlot.setLayoutX((deltaWidth) + (offsetC * (2*deltaWidth)));
                }
                this.getChildren().add(newSlot);
                if (r == fieldSize/2 && c == fieldSize/2) {
                    newSlot.setSlotCardView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/center.png"))));
                    centerSlot = newSlot;
                    centerSlot.setEmpty(true);
                    centerSlot.setCenter(true);
                }
            }
        }
        centerSlot.toFront();
    }

    public void fillField(int row, int column, NonObjectiveCard cardToPlace) {
        Image img = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(cardToPlace.getArtRef()[cardToPlace.isPlacedFront() ? 0 : 1])));
        int correspondingChildIndex = fieldMap.get(new Pair<>(row, column));
        SlotController correspondingSlot = (SlotController) this.getChildren().get(correspondingChildIndex);
        correspondingSlot.setSlotCardView(img);
    }
    public int getFieldSize() {
        return fieldSize;
    }

    public double getTotalHeight() {
        return totalHeight;
    }

    public double getTotalWidth() {
        return totalWidth;
    }

    public HashMap<Pair<Integer, Integer>, Integer> getFieldMap() {
        return fieldMap;
    }
}
