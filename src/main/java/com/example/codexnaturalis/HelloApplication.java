package com.example.codexnaturalis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    private static Stage stage;
    @Override
    public void start(Stage stageStart) throws IOException {
        DrawingDeck.generateDecks();
        stage=stageStart;
        // actual loader FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/launcher.fxml"));
        //debug loader
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/launcher.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Assets/RoundedLogo.png")));
        stage.getIcons().add(icon);
        stage.setTitle("Codex Naturalis by IS-AM39 - Launcher");
        stage.setResizable(true);
        final Parent root = fxmlLoader.load();
        final Scene scene = new Scene(root, Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        HelloApplication.launch();
    }
    public static Stage getStage(){
        return stage;
    }
}