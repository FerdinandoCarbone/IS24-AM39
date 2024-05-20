package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;

public class TokenSlotController extends Pane {
    @FXML
    private ImageView visibleToken;
    private ArrayList<TokenController> tokens = new ArrayList<>(4);

    public TokenSlotController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/TokenSlot.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

    }

    public void setNewVisibleToken(TokenController token) {
        tokens.addFirst(token);
        this.visibleToken = tokens.getFirst().getTokenImageView();

    }

    public void setPreviousVisibleToken() {
        tokens.removeFirst();
        this.visibleToken = tokens.getFirst().getTokenImageView();
    }

    public void removeTokenFromSlot(TokenController tokenController) {
        tokens.remove(tokenController);
    }

    public ImageView getVisibleToken() {
        return visibleToken;
    }

    public ArrayList<TokenController> getTokens() {
        return tokens;
    }

    public ImageView getTokenSlotImageView() {
        return visibleToken;
    }
}