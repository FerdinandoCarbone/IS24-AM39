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
    private final Image scoreTracker =  new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/Tabellone.png")));
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

    public void moveToken(TokenController token, int score) {
        TokenSlotController tokenSlot = slots.get(score);
        tokenSlot.getTokens().add(token);
        tokenSlot.setNewVisibleToken(token);
    }


//    public void moveToken(TokenController t, int score) {
//        switch (score) {
//            case 1:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s1.removeTokenFromSlot(t);
//                s1.setNewVisibleToken(t);
//                t.setCurrentSlot(s1);
//            case 2:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s2.setNewVisibleToken(t);
//                t.setCurrentSlot(s2);
//            case 3:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s3.setNewVisibleToken(t);
//                t.setCurrentSlot(s3);
//            case 4:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s4.setNewVisibleToken(t);
//                t.setCurrentSlot(s4);
//            case 5:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s5.setNewVisibleToken(t);
//                t.setCurrentSlot(s5);
//            case 6:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s6.setNewVisibleToken(t);
//                t.setCurrentSlot(s6);
//            case 7:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s7.setNewVisibleToken(t);
//                t.setCurrentSlot(s7);
//            case 8:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s8.setNewVisibleToken(t);
//                t.setCurrentSlot(s8);
//            case 9:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s9.setNewVisibleToken(t);
//                t.setCurrentSlot(s9);
//            case 10:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s10.setNewVisibleToken(t);
//                t.setCurrentSlot(s10);
//            case 11:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s11.setNewVisibleToken(t);
//                t.setCurrentSlot(s11);
//            case 12:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s12.setNewVisibleToken(t);
//                t.setCurrentSlot(s12);
//            case 13:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s13.setNewVisibleToken(t);
//                t.setCurrentSlot(s13);
//            case 14:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s14.setNewVisibleToken(t);
//                t.setCurrentSlot(s14);
//            case 15:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s15.setNewVisibleToken(t);
//                t.setCurrentSlot(s15);
//            case 16:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s16.setNewVisibleToken(t);
//                t.setCurrentSlot(s16);
//            case 17:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s17.setNewVisibleToken(t);
//                t.setCurrentSlot(s17);
//            case 18:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s18.setNewVisibleToken(t);
//                t.setCurrentSlot(s18);
//            case 19:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s19.setNewVisibleToken(t);
//                t.setCurrentSlot(s19);
//            case 20:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s20.setNewVisibleToken(t);
//                t.setCurrentSlot(s20);
//            case 21:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s21.setNewVisibleToken(t);
//                t.setCurrentSlot(s21);
//            case 22:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s22.setNewVisibleToken(t);
//                t.setCurrentSlot(s22);
//            case 23:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s23.setNewVisibleToken(t);
//                t.setCurrentSlot(s23);
//            case 24:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s24.setNewVisibleToken(t);
//                t.setCurrentSlot(s24);
//            case 25:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s25.setNewVisibleToken(t);
//                t.setCurrentSlot(s25);
//            case 26:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s26.setNewVisibleToken(t);
//                t.setCurrentSlot(s26);
//            case 27:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s27.setNewVisibleToken(t);
//                t.setCurrentSlot(s27);
//            case 28:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s28.setNewVisibleToken(t);
//                t.setCurrentSlot(s28);
//            case 29:
//                if (t.getCurrentSlot().getTokenSlotImageView().equals(t.getTokenImageView())) {
//                    t.getCurrentSlot().setPreviousVisibleToken();
//                }
//                s29.setNewVisibleToken(t);
//                t.setCurrentSlot(s29);
//            default:
//                break;
//        }
//    }
}
