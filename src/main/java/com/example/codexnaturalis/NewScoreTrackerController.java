package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import static com.example.codexnaturalis.CardDim.scoreTrackerMulti;

public class NewScoreTrackerController extends Pane {
    @FXML
    ImageView scoreTracker;
    ArrayList<ScoreTrackerSlotController> tokenSlots = new ArrayList<>();
    double widthTracker = 375 * scoreTrackerMulti;
    double heightTracker = 732 * scoreTrackerMulti;
    double c0 = 0.23;
    double c1 = 0.45;
    double c2 = 0.67;
    double c3 = 0.78;
    double c4 = 0.57;
    double c5 = 0.35;
    double c6 = 0.13;

    public NewScoreTrackerController(ArrayList<Player> players) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/ScoreTracker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        scoreTracker.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Tabellone.png"))));
        scoreTracker.setFitHeight(heightTracker);
        scoreTracker.setFitWidth(widthTracker);
        for (int i = 0; i < 30; i++) {
            ScoreTrackerSlotController tmpSlotController = new ScoreTrackerSlotController(players);
            if (i != 0) {
                for (ImageView j : tmpSlotController.playerTokenHash.values()) {
                    j.setVisible(false);
                }
            }
            this.getChildren().add(tmpSlotController);
            tokenSlots.add(tmpSlotController);
        }

        tokenSlots.getFirst().setLayoutX(c0 * widthTracker);
        tokenSlots.get(1).setLayoutX(c1 * widthTracker);
        tokenSlots.get(2).setLayoutX(c2 * widthTracker);
        tokenSlots.getFirst().setLayoutY(0.9 * heightTracker);
        tokenSlots.get(1).setLayoutY(0.9 * heightTracker);
        tokenSlots.get(2).setLayoutY(0.9 * heightTracker);
        double offsetX = 0;
        boolean dxToSx = true;
        int lastSlotVisited = 0;
        for (int i = 3, counter = 0, yMulti = 0; i < 19; i++, counter++) {
            switch (counter) {
                case 0:
                    offsetX = widthTracker * c3;
                    break;
                case 1:
                    offsetX = widthTracker * c4;
                    break;
                case 2:
                    offsetX = widthTracker * c5;
                    break;
                case 3:
                    offsetX = widthTracker * c6;
                    break;
                default:
                    break;
            }
            if (dxToSx) {
                tokenSlots.get(i).setLayoutX(offsetX);
            } else {
                tokenSlots.get(lastSlotVisited - counter).setLayoutX(offsetX);
            }
            tokenSlots.get(i).setLayoutY((0.8 * heightTracker) - yMulti*0.11*heightTracker);
            if (counter == 3) {
                counter = -1;
                yMulti++;
                dxToSx = !dxToSx;
                lastSlotVisited = i+4;
            }
        }

        tokenSlots.get(21).setLayoutY((0.8 * heightTracker) - 3.9*0.11*heightTracker);
        tokenSlots.get(19).setLayoutY((0.8 * heightTracker) - 3.9*0.11*heightTracker);
        tokenSlots.get(22).setLayoutY((0.8 * heightTracker) - 4.8*0.11*heightTracker);
        tokenSlots.get(28).setLayoutY((0.8 * heightTracker) - 4.8*0.11*heightTracker);
        tokenSlots.get(23).setLayoutY((0.8 * heightTracker) - 5.8*0.11*heightTracker);
        tokenSlots.get(27).setLayoutY((0.8 * heightTracker) - 5.8*0.11*heightTracker);
        tokenSlots.get(24).setLayoutY((0.8 * heightTracker) - 6.5*0.11*heightTracker);
        tokenSlots.get(25).setLayoutY((0.8 * heightTracker) - 6.7*0.11*heightTracker);
        tokenSlots.get(26).setLayoutY((0.8 * heightTracker) - 6.5*0.11*heightTracker);

        tokenSlots.get(21).setLayoutX(widthTracker * c6);
        tokenSlots.get(22).setLayoutX(widthTracker * c6);
        tokenSlots.get(23).setLayoutX(widthTracker * c6);

        tokenSlots.get(19).setLayoutX(widthTracker * c3);
        tokenSlots.get(28).setLayoutX(widthTracker * c3);
        tokenSlots.get(27).setLayoutX(widthTracker * c3);

        tokenSlots.get(24).setLayoutX(c0 * widthTracker);
        tokenSlots.get(25).setLayoutX(c1 * widthTracker);
        tokenSlots.get(26).setLayoutX(c2 * widthTracker);

        tokenSlots.get(29).setLayoutX(c1 * widthTracker);
        tokenSlots.get(29).setLayoutY(0.2 * heightTracker);

        tokenSlots.get(20).setLayoutX(c1 * widthTracker);
        tokenSlots.get(20).setLayoutY(0.32 * heightTracker);



    }

    public void moveToken(UUID idPlayer, int points) {

        ScoreTrackerSlotController oldSlot = null;
        ScoreTrackerSlotController newSlot = tokenSlots.get(points);

        for (ScoreTrackerSlotController t : tokenSlots) {
            ImageView playerToken = t.playerTokenHash.get(idPlayer);
            if (playerToken.isVisible()) {
                oldSlot = t;
                break;
            }
        }

        if (tokenSlots.indexOf(oldSlot) == points) {
            System.out.println(oldSlot == newSlot);
            System.out.println("Nothing changed in tracker...");
        } else {
            assert oldSlot != null;
            oldSlot.playerTokenHash.get(idPlayer).setVisible(false);
            newSlot.playerTokenHash.get(idPlayer).setVisible(true);
        }
    }

}

















