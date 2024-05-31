package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LauncherController extends StackPane implements Initializable {
    @FXML
    public Button connectButton;
    @FXML
    private RadioButton socketOption;
    @FXML
    private RadioButton rmiOption;
    @FXML
    private TextField textField;
    @FXML
    private TextArea status;
    private ToggleGroup toggleGroup;
    private Boolean typeOfConnection;
    private String playerNick;
    private int clientSetupState;


    public void handleToggle() {
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        typeOfConnection = !selectedRadioButton.equals(socketOption);
    }

    private String inputPlayerNick() {
        String playerNick = "null";
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
        typeOfConnection = null;
        toggleGroup = new ToggleGroup();
        socketOption.setToggleGroup(toggleGroup);
        rmiOption.setToggleGroup(toggleGroup);
    }

    public static int askIntInputToUser(String message, String request) {
        AtomicInteger input = new AtomicInteger();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("An input is required:");
        dialog.setHeaderText(message);
        dialog.setContentText(request);
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            System.err.println("No input provided. Default value returned.");
            return 0;
        }
        result.ifPresent(number -> {
            try {
                input.set(Integer.parseInt(number));
            } catch (NumberFormatException E) {
                System.err.println("Wrong input provided. Default value returned.");
                input.set(0);
            }
        });
        System.out.println(input);
        dialog.close();
        return input.get();
    }

    public static String askStringInputToUser(String message, String request) {
        AtomicReference<String> input = new AtomicReference<>();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("An input is required:");
        dialog.setHeaderText(message);
        dialog.setContentText(request);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input::set);
        return input.get();
    }

    public static void loadGameScene() throws IOException {
        Stage stage = Client.getStage();
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("fxml/main.fxml"));
        stage.setTitle("Codex Naturalis by IS-AM39 - Game");
        final Parent root = fxmlLoader.load();
        final Scene scene = new Scene(root, Color.LIGHTGRAY);
        stage.setResizable(true);
        stage.setScene(scene);
    }

    public void printStatus(String message, String color) {
        status.clear();
        status.appendText(message);
        status.setStyle("-fx-text-fill: " + color + ";");
    }

    public static void alert(String message,boolean wait) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        if(wait)alert.showAndWait();
        else alert.show();
    }

    public void handleSubmitButtonAction() {
        status.clear();
        String input = textField.getText();

        if (Client.isValidNick(input)) {
            return;
        }

        if (typeOfConnection == null) {
            printStatus("Select a connection type", "red");
        } else if (input == null || input.isEmpty()) {
            printStatus("Invalid player nickname: please type another one and retry", "red");
        } else {
            //String[] args = new String[]{"localhost", "8081", "gui"};
            Client.setPlayerNick(input);
            //Client.clientStart(args);
            clientSetupState = Client.initialClientSetup();
            printStatus("Submitted", "green");
            switch (clientSetupState) {
                case 0:
                    Client.setConnectionType(typeOfConnection);
                    printStatus("No save file found - Start as new Player", "green");
                    break;
                case -1:
                    Client.setConnectionType(typeOfConnection);
                    printStatus("No save file found and unable to save - Start as new Player", "yellow");
                    break;
                case 1:
                    printStatus("Fallback to the previous' match connection type:", "yellow");
                    break;
            }
            printStatus("Attempting connection", "blue");
            Client.start();
            printStatus("Waiting for everyone to join","blue");
            connectButton.setDisable(true);
        }

    }
    public static int selectAStarterCardDialog(Card card,String whatToSelect) throws IOException {
        SelectableCardController selectableFront = new SelectableCardController(card,true);
        SelectableCardController selectableRear = new SelectableCardController(card,false);
        ArrayList<SelectableCardController> selectables = new ArrayList<>();
        selectables.add(selectableFront);
        selectables.add(selectableRear);
        Dialog<String> dialog = new Dialog<>();
        ToggleGroup buttonGroup = new ToggleGroup();
        HBox content = new HBox();
        for(SelectableCardController s: selectables){
            s.selectButton.setToggleGroup(buttonGroup);
        }
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.setTitle(whatToSelect);
        content.getChildren().addAll(selectables);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                RadioButton selectedRadioButton = (RadioButton) buttonGroup.getSelectedToggle();
                if (selectedRadioButton != null) {
                    return selectedRadioButton.getUserData().toString();
                }
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String selectedOption = result.get();
            System.out.println("Selected Option: " + selectedOption);
            return Integer.parseInt(selectedOption);
        } else {
            return -1;
        }
    }

    public static Integer selectACardDialog(ArrayList<Card> cards,String whatToSelect) throws IOException {
        ArrayList<SelectableCardController> selectables= new ArrayList<>();
        for(Card card: cards){
            System.out.println(card.getArtRef()[0]);
            System.out.println(card.getArtRef()[1]);
            selectables.add(new SelectableCardController(card));
        }
        Dialog<Integer> dialog = new Dialog<>();
        ToggleGroup buttonGroup = new ToggleGroup();
        HBox content = new HBox();
        for(SelectableCardController s: selectables){
            s.selectButton.setToggleGroup(buttonGroup);
        }
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.setTitle(whatToSelect);
        content.getChildren().addAll(selectables);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                RadioButton selectedRadioButton = (RadioButton) buttonGroup.getSelectedToggle();
                if (selectedRadioButton != null) {
                    return (Integer) selectedRadioButton.getUserData();
                }
            }
            return null;
        });
        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            Integer selectedOption = result.get();
            //dialogClosedFuture.complete(selectedOption);
            System.out.println("Selected Option: " + selectedOption);
            return selectedOption;
        } else {
            //dialogClosedFuture.complete(0);
            return 0; // Restituisci il valore selezionato
        }
    }
    public static Boolean nameSaveTaken(String message,String whatToSelect) throws IOException {
        Dialog<Boolean> dialog = new Dialog<>();
        AtomicReference<Boolean> returnValue = new AtomicReference<>();
        ButtonType okButtonType = new ButtonType("Load", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Overwrite", ButtonBar.ButtonData.OK_DONE);
        dialog.setTitle(message);
        dialog.setContentText(whatToSelect);

        dialog.getDialogPane().getButtonTypes().addAll(okButtonType,cancelButtonType);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return true;
            }
            else if (dialogButton == cancelButtonType) {
                return false;
            }
            return null;
        });
        Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent()) {
            Boolean selectedOption = result.get();
            System.out.println("Selected Option: " + selectedOption);
            return selectedOption;
        } else {
            System.out.println("Error occurred: fallback to new user");
            return false;
        }
    }

}
