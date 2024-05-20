package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class TokenController extends Pane {
    @FXML
    private ImageView TokenImageView;
    private TokenSlotController currentSlot;

    public TokenController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Token.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

    }

    public void setTokenColor(Token token) {
        switch (token.getColor()) {
            case Red -> TokenImageView.setImage(new Image("Assets/RedToken.png"));
            case Yellow -> TokenImageView.setImage(new Image("Assets/YellowToken.png"));
            case Green -> TokenImageView.setImage(new Image("Assets/GreenToken.png"));
            case Blue -> TokenImageView.setImage(new Image("Assets/BlueToken.png"));
            case Black -> TokenImageView.setImage(new Image("Assets/BlackToken.png"));
        }
    }


    public ImageView getTokenImageView() {
        return TokenImageView;
    }

    public void setCurrentSlot(TokenSlotController slotController) {
        this.currentSlot = slotController;
    }
    public TokenSlotController getCurrentSlot() {
        return currentSlot;
    }
}
