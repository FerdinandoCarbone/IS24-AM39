package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

import static com.example.codexnaturalis.GlobalVars.*;

public class ZoomPaneController extends Pane {

    @FXML
    ImageView c0;
    @FXML
    ImageView c1;
    @FXML
    ImageView c2;
    @FXML
    ImageView c3;
    @FXML
    ImageView c4;
    private final int resizer = 2;
    private final double totalHeight = resizer*(cardHeight + 2*deltaHeight);
    private final double totalWidth = resizer*(cardWidth + 2*deltaWidth);


    public ZoomPaneController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZoomPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        for (int i = 0; i < this.getChildren().size(); i++) {
            ImageView tmpImg = (ImageView) this.getChildren().get(i);
            tmpImg.setFitHeight(cardHeight*resizer);
            tmpImg.setFitWidth(cardWidth*resizer);
            tmpImg.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedCards/empty.png"))));
        }

        c1.setLayoutX(resizer*2*deltaWidth);
        c2.setLayoutX(resizer*deltaWidth);
        c2.setLayoutY(resizer*deltaHeight);
        c3.setLayoutY(resizer*2*deltaHeight);
        c4.setLayoutX(resizer*2*deltaWidth);
        c4.setLayoutY(resizer*2*deltaHeight);
    }

    public double getTotalHeight() {
        return totalHeight;
    }

    public double getTotalWidth() {
        return totalWidth;
    }

}
