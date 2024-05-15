package com.example.codexnaturalis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class PortChoiceController extends Pane {

    @FXML
    LabelTextController ip;
    @FXML
    LabelTextController port;
    @FXML
    Button confirmBut;
    @FXML
    Label errorLabel;
    String ipValue;
    String portValue;

    public PortChoiceController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PortChoice.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();


        ip.label.setText("Set ip: ");
        ip.textField.setPromptText("localhost");
        ip.textField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        port.label.setText("Set port: ");
        port.textField.setPromptText("8081");
        port.textField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        this.setPadding(new Insets(10, 10, 10, 10));

        ip.setLayoutY(30);
        confirmBut.setLayoutX(180);
        confirmBut.setLayoutY(25);

        confirmBut.setOnAction((ActionEvent event) -> {checkFields();});
        errorLabel.setLayoutY(75);
        errorLabel.setLayoutX(15);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private boolean checkFields() {
        boolean allGood = true;
        String ipText = ip.textField.getText();
        String portText = port.textField.getText();

        if (portText.isEmpty()) {
            errorLabel.setText("Campo port vuoto");
            port.textField.clear();
            allGood = false;
        } else {
            portValue = portText.trim();
        }
        if (ipText.isEmpty()) {
            errorLabel.setText("Campo ip vuoto");
            ip.textField.clear();
            allGood = false;
        } else {
            ipValue = ipText.trim();
        }
        return allGood;

    }


}
