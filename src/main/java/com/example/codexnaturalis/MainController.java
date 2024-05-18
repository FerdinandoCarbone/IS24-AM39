package com.example.codexnaturalis;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController extends TabPane implements Initializable {
    public static TextArea textArea;
    public static Button turnButton;
    public static ComboBox comboBox;
    private boolean cardPlaced;
    private int deckChildIndex;
    private Player player;
    private Pair<Integer,Integer> lastCoords;
    private ResourceGoldCardController cardToRemove;
    @FXML public CommandBoxController commands;
    @FXML public Button sendButton;
    @FXML public ScrollPane fieldScrollPane;
    @FXML public HBox turnBOX;
    @FXML public TextField textField;
    @FXML public VBox vbox;
    @FXML public FieldController field2;
    @FXML public FieldController field3;
    @FXML public FieldController field4;
    @FXML public TabelloneController table;
    @FXML private Tab tab1;
    @FXML private Tab tab2;
    @FXML private Tab tab3;
    @FXML private PlayerDeckController playerDeck;
    @FXML private FieldController field;
    private boolean readyToPlace;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardPlaced=false;
        readyToPlace = false;
        cardToRemove=null;
        System.out.println("Loading...");
        fieldScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::middlePosition);
        });
        try{
        ZakClient.getSem().acquire();
        }
        catch(Exception e ){
            throw new RuntimeException();
        }
        player = ZakClient.getPlayer();
        viewSetup();
        setupFieldSlots();
        cardsFromModel();
        setupRGCEvents();
        playerDeck.setPadding(new Insets(25));
        playerDeck.setSpacing(20);
    }

    private void viewSetup() {
        textArea = new TextArea("Notification");
        textArea.setEditable(false);
        vbox.getChildren().add(2, textArea);
        commands.command3.setOnAction(event -> resetMove());
        comboBox = new ComboBox<>();
        comboBox.setPromptText("Select recipient Player");
        comboBox.getItems().add("Everyone");
        turnButton = new Button("Confirm Turn");
        turnButton.setDisable(true);
        turnButton.setOnAction(event -> genericTurnSender());
        turnBOX.getChildren().add(1, comboBox);
        turnBOX.getChildren().add(turnButton);
        ArrayList<Player> others = ZakClient.getOtherPlayers();
        Tab[] tabs = new Tab[]{tab1,tab2,tab3};
        HashMap<String, Tab> tabMan =new HashMap<>();
        for (int i = 0; i < others.size() ; i++) {
            String playerName = others.get(i).getPlayerName();
            comboBox.getItems().add(playerName);
            tabs[i].setText(playerName);
            tabs[i].setDisable(false);
            tabMan.put(playerName,tabs[i]);
        }
    }
    private void setupRGCEvents() {
        playerDeck.getCard1().setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                playerDeck.getCard1().flipCard();
            }
            selectRGCFromDeck(playerDeck.getCard1());
        });
        playerDeck.getCard2().setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                playerDeck.getCard2().flipCard();
            }
            selectRGCFromDeck(playerDeck.getCard2());
        });
        playerDeck.getCard3().setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                playerDeck.getCard3().flipCard();
            }
            selectRGCFromDeck(playerDeck.getCard3());
        });
    }
    private void selectRGCFromDeck(ResourceGoldCardController card) {
        if (!cardPlaced) {
            cardToRemove = card;
            deckChildIndex = playerDeck.getChildren().indexOf(card);
            readyToPlace = true;
        }
    }
    public void resetMove(){
        SlotController slotToReset = (SlotController) field.getChildren().get(field.getFieldMap().get(lastCoords));
        for(ResourceGoldCardController card : playerDeck.getAllPlayableDeck()) if(card.getCard()==null) card.setupCard((ResourceGoldCard) slotToReset.card);
        slotToReset.setEmpty(true);
        lastCoords = null;
        cardPlaced=false;
        readyToPlace =true;
    }

    private void cardsFromModel() {
        ArrayList<ResourceGoldCard> rgCards = player.getPlayerDeck().getResourceGoldCards();
        ArrayList<ObjectiveCard> objCards = player.getCommonObjCards();
        objCards.add(player.getPlayerDeck().getSecretObjectiveCard());
        playerDeck.getCard1().setupCard(rgCards.get(0));
        playerDeck.getCard2().setupCard(rgCards.get(1));
        playerDeck.getCard3().setupCard(rgCards.get(2));
        try {
            ZakClient.getSem().acquire();
            playerDeck.getObjCards().add(new CardController(objCards.get(0)));
            playerDeck.getObjCards().add(new CardController(objCards.get(1)));
            playerDeck.getObjCards().add(new CardController(objCards.get(2)));
        } catch(IOException | InterruptedException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        playerDeck.getStarterCard().setupCard(player.getPlayerDeck().getStarterCard());


    }

    private void middlePosition() {
        double totalHeight = field.getHeight();
        double visibleHeight = fieldScrollPane.getViewportBounds().getHeight();
        double middlePosition = (totalHeight - visibleHeight) / 2 / totalHeight * 1.7;
        fieldScrollPane.setVvalue(middlePosition);
        totalHeight = field.getWidth();
        visibleHeight = fieldScrollPane.getViewportBounds().getWidth();
        middlePosition = (totalHeight - visibleHeight) / 2 / totalHeight * 1.7;
        fieldScrollPane.setHvalue(middlePosition);
    }

    public static void printMessage(String message) {
        textArea.appendText(message);
    }

    public void chatWrite(ActionEvent actionEvent) throws IOException {
        //send to clientHandler
        String s = textField.getText();
        String recipient = (String) comboBox.getSelectionModel().getSelectedItem();
        TextMessage text = new TextMessage(ZakClient.getPlayerNick(), ZakClient.getClientID(), s, recipient);
        if (!text.getRecipient().equals("Everyone"))
            printMessage("\nYou to " + text.getRecipient() + ": " + text.getTextMessage());
        ZakClient.getServerHandler().sendMessage(new TextMessage(ZakClient.getPlayerNick(), ZakClient.getClientID(), s, recipient));
        textField.setText("");
    }

    public void genericTurnSender() {
    }

    public static void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
        alert.close();
    }

    public ResourceGoldCard pickCard() {
        ResourceGoldCard card = null;
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Java", "Java", "Python", "JavaScript");
        dialog.setTitle("Choice Dialog");
        dialog.setHeaderText("Select your favorite programming language:");
        dialog.setContentText("Language:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(language -> {
            System.out.println("Your favorite programming language: " + language);
        });
        dialog.close();
        return card;
    }

    public static void setTurnButton(boolean b) {
        turnButton.setDisable(b);
    }

    public static void updateOtherPlayers() {
        comboBox.getItems().removeAll();
        for (Player p : ZakClient.getOtherPlayers()) comboBox.getItems().add(p.getPlayerName());
        comboBox.getItems().add("Everyone");
    }
    private void setupFieldSlots() {
        for (int i = 0; i < field.getChildren().size(); i++) {
            SlotController tmpSlot = (SlotController) field.getChildren().get(i);
            tmpSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (cardPlaced) {
                    System.out.println("CARD ALREADY PLACED IN THIS TURN");
                } else {
                    if (readyToPlace) {
                        try {
                            placeCardAndRemoveFromDeck(tmpSlot);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("PRIMA SELEZIONA UNA CARTA DAL DECK");
                    }
                }
            });
        }
        int starterFace = player.getPlayerDeck().getStarterCard().isPlacedFront()?0:1;
        Image starter = new Image(Objects.requireNonNull(getClass().getResourceAsStream(player.getPlayerDeck().getStarterCard().getArtRef()[starterFace])));
        field.centerSlot.setSlotCardView(starter);
        playerDeck.getChildren().remove(playerDeck.getStarterCard());
        readyToPlace = false;
        field.centerSlot.toFront();
        player.printManas();
    }
    private void placeCardAndRemoveFromDeck(SlotController slotToPlace) throws Exception {
        if (cardToRemove.getCard() == null) {
            System.out.println("EMPTY CARD NOT PLACEBLE");
        } else {
            if (!cardPlaced) {
                int row = slotToPlace.getCoords().getKey();
                int col = slotToPlace.getCoords().getValue();
                lastCoords = slotToPlace.coords;
                if (player.isCardAttachableToSlot(row, col)) {
                    if (cardToRemove.getCard() instanceof GoldCard && cardToRemove.getCard().isPlacedFront()) {
                        if (!player.requirementsAreFulfilled((GoldCard) cardToRemove.getCard())) {
                            return;
                        }
                    }
                    if (slotToPlace.isEmpty()) {
                        slotToPlace.setSlotCardView(cardToRemove.getShownImage());
                        readyToPlace = false;
                        cardPlaced = true;
                        slotToPlace.toFront();
                        //Getting row of field
                        player.placeCardAndRemoveFromDeck(row, col, cardToRemove.getCard());
                        playerDeck.resetCard(deckChildIndex);
                        player.printManas();
                    } else {
                        System.out.println("SLOT GIA' OCCUPATO");
                    }
                }
            } else {
                System.out.println("CARD HAS ALREADY BEEN PLACED IN THIS TURN");
            }
        }
    }
}
