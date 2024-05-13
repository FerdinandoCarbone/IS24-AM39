package com.example.codexnaturalis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LauncherController extends StackPane implements Initializable {
    @FXML private RadioButton socketOption;
    @FXML private RadioButton rmiOption;
    @FXML private TextField textField;
    @FXML private TextArea status;
    private ToggleGroup toggleGroup;
    private Boolean typeOfConnection;
    private String playerNick;

    public void handleToggle() {
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        typeOfConnection= !selectedRadioButton.equals(socketOption);
    }
    private String inputPlayerNick(){
        String playerNick="null";
        //javafx stuff
        setPlayerNick(playerNick);
        return playerNick;
    }
    public String getPlayerNick() {
        return playerNick;
    }

    public void setPlayerNick(String playerNick) {
        this.playerNick = playerNick;
    }

    public boolean isTypeOfConnection() {
        return typeOfConnection;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //status.setPrefRowCount(3);
        //status.setPrefColumnCount(16);
        typeOfConnection=null;
        toggleGroup = new ToggleGroup();
        socketOption.setToggleGroup(toggleGroup);
        rmiOption.setToggleGroup(toggleGroup);
        inputPlayerNick();
    }

    public void handleSubmitButtonAction() throws IOException {
        //ZakClient.setPlayerNick(textField.getText());
        status.clear();
        if (typeOfConnection == null){
            status.appendText("Select a connection type");
            status.setStyle("-fx-text-fill: red;");
        }
        else if(textField.getText()==null || textField.getText().isEmpty()){

            status.appendText("Invalid player nickname: please type another one and retry");
            status.setStyle("-fx-text-fill: red;");
        }
        else {
            //System.out.println("Submitted");
            status.appendText("Submitted");
            status.setStyle("-fx-text-fill: green;");
            //initialClientSetup()
            //if(connection attempts){
            Stage stage = HelloApplication.getStage();
           // Stage newStage= new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main.fxml"));
            stage.setTitle("Codex Naturalis by IS-AM39 - Game");
            final Parent root = fxmlLoader.load();
            final Scene scene = new Scene(root, Color.LIGHTGRAY);
            //newStage.setScene(scene);
            //newStage.show();
            stage.setScene(scene);
            //stage.close();
            //}
        /*
        * else{
        *
        *   status.appendText("retrieveError()");
            status.setStyle("-fx-text-fill: red;");
        * }
        *
        * */
        }
    }
}
