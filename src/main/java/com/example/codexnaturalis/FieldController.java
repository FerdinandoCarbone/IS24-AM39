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
    private HashMap<Pair<Integer, Integer>, SlotController> fieldMap = new HashMap<>();

    public FieldController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Field.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        System.out.println("CREATING FIELD...");
        for (int r = 0; r < fieldSize; r++) {
            boolean dispariFlag = false;
            for (int c = 0, offsetC = 0; c < fieldSize; c += 2, offsetC++) {
                SlotController newSlot = new SlotController();
                if (r % 2 == 0) {
                    Pair<Integer, Integer> cords = new Pair<>(r, c);
                    newSlot.setCoords(cords);
                    fieldMap.put(cords, newSlot);
                    newSlot.setLayoutY(r *(CardDim.slotHeight - CardDim.cornerSlotHeight));
                    newSlot.setLayoutX(offsetC * (2*deltaSlotWidth));
                } else {
                    if (!dispariFlag) {
                        c++;
                        dispariFlag = true;
                    }
                    Pair<Integer, Integer> cords = new Pair<>(r, c);
                    newSlot.setCoords(cords);
                    fieldMap.put(cords, newSlot);
                    newSlot.setLayoutY(r *(CardDim.slotHeight - CardDim.cornerSlotHeight));
                    newSlot.setLayoutX((deltaSlotWidth) + (offsetC * (2*deltaSlotWidth)));
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

    public static FieldController rebuildField(FieldController field,Player player) {
        Field.Slot slot;
        for(int i=0;i< matrixSize; i++){
            for (int j = 0; j < matrixSize; j++) {
               slot = player.getPlayerField().getSlots()[i][j];
               if(!slot.isBusySlot()) continue;
               field.fillField(i,j,slot.getCardSlot());
            }
        }
        return field;
    }

    public void fillField(int row, int column, NonObjectiveCard cardToPlace) {
        Image img = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(cardToPlace.getArtRef()[cardToPlace.isPlacedFront() ? 0 : 1])));
        SlotController correspondingSlot = fieldMap.get(new Pair<>(row, column));
        correspondingSlot.setSlotCardView(img);
        correspondingSlot.toFront();
    }
    public int getFieldSize() {
        return fieldSize;
    }

    public HashMap<Pair<Integer, Integer>, SlotController> getFieldMap() {
        return fieldMap;
    }
}
