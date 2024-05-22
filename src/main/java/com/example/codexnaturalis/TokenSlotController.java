package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class TokenSlotController extends Pane {
    @FXML
    private ImageView token1, token2, token3, token4;
    public TokenSlotController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/TokenSlot.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();


        token1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/" + Client.getPlayer().getToken().getColor().toString() + "Token.png"))));
        //token1.setVisible(false);
        token2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/" + Client.getOtherPlayers().get(0).getToken().getColor().toString() + "Token.png"))));
        //token2.setVisible(false);
        if (Client.getOtherPlayers().size() >1){
            token3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/" + Client.getOtherPlayers().get(1).getToken().getColor().toString() + "Token.png"))));
            //token3.setVisible(false);
        }
        if(Client.getOtherPlayers().size()>2) {
            token4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/" + Client.getOtherPlayers().get(2).getToken().getColor().toString() + "Token.png"))));
            //token4.setVisible(false);
        }

    }

    public void setEnableToken(int index){
        switch (index) {
            case 0:
                token1.setVisible(true);
                break;
            case 1:
                token2.setVisible(true);
                break;
            case 2:
                token3.setVisible(true);
                break;
            case 3:
                token4.setVisible(true);
                break;
        }
    }
    public void setDisableToken(int index){
        switch (index) {
            case 0:
                token1.setVisible(false);
                break;
            case 1:
                token2.setVisible(false);
                break;
            case 2:
                token3.setVisible(false);
                break;
            case 3:
                token4.setVisible(false);
                break;
        }
    }


    public ImageView getToken1() {
        return token1;
    }

    public ImageView getToken2() {
        return token2;
    }
    public ImageView getToken3() {
        return token3;
    }
    public ImageView getToken4() {
        return token4;
    }
}
