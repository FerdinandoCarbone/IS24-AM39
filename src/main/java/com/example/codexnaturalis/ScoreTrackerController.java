package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ScoreTrackerController extends Pane {
    @FXML
    ImageView scoreTrackerImageView;
    private  Image scoreTracker =  new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Tabellone.png")));
    public ArrayList<TokenSlotController> slots = new ArrayList<>();

    public ScoreTrackerController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/ScoreTracker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        scoreTrackerImageView.setImage(scoreTracker);

        for (int i = 0; i < 30; i++) {
            slots.add(new TokenSlotController());
        }

    }

    public void moveToken(int pts, int indexPlayer){;
        setDisableAll(indexPlayer);
        slots.get(pts).setEnableToken(indexPlayer);
    }

    public void setDisableAll(int index) {
        for (int i = 0; i < 30; i++) {
            slots.get(i).setDisableToken(index);
        }
    }
}