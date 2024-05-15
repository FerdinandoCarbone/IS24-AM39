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
    final int fieldSize = 5;
    final double totalHeight = 2* fieldSize *(deltaHeight) + cardHeight;
    final double totalWidth = 2* fieldSize *(deltaWidth) + cardWidth;
    private HashMap<Pair<Integer, Integer>, Integer> fieldMap = new HashMap<>();

    public FieldController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Field.fxml"));
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
                    newSlot.setSlotCardView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/center.jpg"))));
                    centerSlot = newSlot;
                    centerSlot.setEmpty(true);
                    centerSlot.setCenter(true);
                }
            }
        }

        centerSlot.toFront();


//        for (int r = 0; r <= 2* fieldSize; r++) {
//            boolean extraCard = false;
//            for (int offSetC = 0, counterC = 0; offSetC < fieldSize; offSetC++, counterC++) {
//                SlotController newSlot = new SlotController();
//                newSlot.setPos(cardPos);
//                cardPos++;
//                newSlot.setLayoutY(r *(CardDim.cardHeight - CardDim.cornerHeight));
//                if (r %2 == 0) {
//                    newSlot.setLayoutX(counterC *(2*deltaWidth));
//                    if (!extraCard) {
//                        offSetC--;
//                        extraCard = true;
//                    }
//                } else {
//                    newSlot.setLayoutX((deltaWidth) + (counterC *(2*deltaWidth)));
//                }
//                this.getChildren().add(newSlot);
//                if (r == fieldSize && counterC == fieldSize /2) {
//                    newSlot.setSlotCardView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Cards/center.jpg"))));
//                    centerSlot = newSlot;
//                    centerSlot.setEmpty(true);
//                    centerSlot.setCenter(true);
//                }
//            }
//        }
//        centerSlot.toFront();
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
}
