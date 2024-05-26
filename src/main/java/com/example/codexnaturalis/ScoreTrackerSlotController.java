package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import static com.example.codexnaturalis.CardDim.scoreTrackerMulti;

public class ScoreTrackerSlotController extends Pane {

    int offset = 15;
    HashMap<UUID, ImageView> playerTokenHash = new HashMap<>();

    public ScoreTrackerSlotController(ArrayList<Player> players) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/ScoreTrackerSlot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        for (int i = 0; i < players.size(); i++) {
            String color = players.get(i).getToken().getColor().toString();
            String url = "Assets/" + color + "Token" + ".png";
            ImageView tmpImgView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(url))));
            playerTokenHash.put(players.get(i).getPlayerID(), tmpImgView);
            tmpImgView.setFitHeight(scoreTrackerMulti * 30);
            tmpImgView.setFitWidth(scoreTrackerMulti * 30);
            tmpImgView.setPreserveRatio(true);
            switch (i) {
                case 1:
                    tmpImgView.setLayoutX(offset);
                    break;
                case 2:
                    tmpImgView.setLayoutY(offset);
                    break;
                case 3:
                    tmpImgView.setLayoutX(offset);
                    tmpImgView.setLayoutY(offset);
                    break;
                default:
                    break;
            }
            this.getChildren().add(tmpImgView);
        }

    }

}
