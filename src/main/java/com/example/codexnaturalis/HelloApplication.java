package com.example.codexnaturalis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DrawingDeck.generateDecks();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
        @SuppressWarnings("unused")
        AppController controller = fxmlLoader.getController();
        final Parent root = fxmlLoader.load();
        final Scene scene = new Scene(root);

        //Image icon = new Image("Assets/Cards/empty.jpg");
        //stage.getIcons().add(icon);
        //stage.setFullScreen(true);
        stage.setTitle("Codex Naturalis!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        HelloApplication.launch();
    }
}