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
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MainController extends TabPane implements Initializable {
    public static TextArea textArea;
    public static Button turnButton;
    public static ComboBox comboBox;
    private boolean cardPlaced;
    private int deckChildIndex;
    private Player player;
    private static HashMap<String, Pair<Tab, FieldController>> tabMan;
    private SlotController lastUsedSlot;
    private ResourceGoldCardController cardToRemove;
    private ResourceGoldCard placedCardToSend;
    @FXML
    private CardController secretObjCard;
    @FXML
    private CardController publicObj1;
    @FXML
    private CardController publicObj2;
    @FXML
    public CommandBoxController commands;
    @FXML
    public Button sendButton;
    @FXML
    public ScrollPane fieldScrollPane;
    @FXML
    public HBox turnBOX;
    @FXML
    public TextField textField;
    @FXML
    public VBox vbox;
    @FXML
    public FieldController field2;
    @FXML
    public FieldController field3;
    @FXML
    public FieldController field4;
    @FXML
    public TabelloneController table;
    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;
    @FXML
    private Tab tab3;
    @FXML
    private PlayerDeckController playerDeck;
    @FXML
    private FieldController field;
    private boolean readyToPlace;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardPlaced = false;
        readyToPlace = false;
        cardToRemove = null;
        System.out.println("Loading...");
        fieldScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::middlePosition);
        });
        try {
            ZakClient.getSem().acquire();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        player = ZakClient.getPlayer();
        viewSetup();
        setupFieldSlots();
        cardsFromModel();
        setupRGCEvents();
        commands.command3.setDisable(true);
        secretObjCard.setupCard(player.getPlayerDeck().getSecretObjectiveCard());
        publicObj1.setupCard(player.getCommonObjCards().getFirst());
        publicObj2.setupCard(player.getCommonObjCards().get(1));
    }

    private void viewSetup() {
        textArea = new TextArea("Notification");
        textArea.setEditable(false);
        vbox.getChildren().addFirst(textArea);
        commands.command3.setOnAction(this::resetMove);
        comboBox = new ComboBox<>();
        comboBox.setPromptText("Select recipient Player");
        comboBox.getItems().add("Everyone");
        turnButton = new Button("Confirm Turn");
        turnButton.setDisable(true);
        turnButton.setOnAction(event -> genericTurnSender());
        turnBOX.getChildren().add(1, comboBox);
        turnBOX.getChildren().add(turnButton);
        ArrayList<Player> others = ZakClient.getOtherPlayers();
        Tab[] tabs = new Tab[]{tab1, tab2, tab3};
        FieldController[] fieldControllers = new FieldController[]{field2, field3, field4};
        tabMan = new HashMap<>();
        if(ZakClient.isCrashed()) field = FieldController.rebuildField(field,player);
        for (int i = 0; i < others.size(); i++) {
            String playerName = others.get(i).getPlayerName();
            comboBox.getItems().add(playerName);
            tabs[i].setText(playerName);
            tabs[i].setDisable(false);
            tabMan.put(playerName, new Pair<>(tabs[i], fieldControllers[i]));
            int middleSlot = CardDim.matrixSize/2;
            if(ZakClient.isCrashed()) field = FieldController.rebuildField(fieldControllers[i],others.get(i));
            else fieldControllers[i].fillField(middleSlot, middleSlot, others.get(i).getPlayerDeck().getStarterCard());
            System.out.println("Carta starter di " + others.get(i).getPlayerName() + " piazzata di " + (others.get(i).getPlayerDeck().getStarterCard().isPlacedFront() ? "Fronte" : "Retro"));
        }
    }

    private void setupRGCEvents() {
        playerDeck.getCard1().setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                playerDeck.getCard1().flipCard();
            } else if (event.getButton() == MouseButton.PRIMARY) selectRGCFromDeck(playerDeck.getCard1());
        });
        playerDeck.getCard2().setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                playerDeck.getCard2().flipCard();
            } else if (event.getButton() == MouseButton.PRIMARY) selectRGCFromDeck(playerDeck.getCard2());
        });
        playerDeck.getCard3().setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                playerDeck.getCard3().flipCard();
            } else if (event.getButton() == MouseButton.PRIMARY) selectRGCFromDeck(playerDeck.getCard3());
        });
    }

    private void selectRGCFromDeck(ResourceGoldCardController card) {
        if (card.getCard() == null) {
            System.out.println("EMPTY CARD, CAN'T DO MUCH WITH IT");
        } else {
            if (!cardPlaced) {
                System.out.println("CARD #" + card.getCard().getIdCard() + " SELECTED FROM DECK");
                cardToRemove = card;
                deckChildIndex = playerDeck.getChildren().indexOf(card);
                readyToPlace = true;
            } else {
                System.out.println("CARD ALREADY PLACED IN THIS TURN");
            }
        }
    }

    public void resetMove(ActionEvent event) {
        if (cardPlaced) {
            lastUsedSlot.toBack();
            ResourceGoldCardController tmp = (ResourceGoldCardController) playerDeck.getChildren().get(deckChildIndex);
            tmp.setupCard((ResourceGoldCard) lastUsedSlot.card);
            lastUsedSlot.setEmpty(true);
            player.undoMove(lastUsedSlot.coords.getKey(), lastUsedSlot.coords.getValue(), deckChildIndex);
            cardPlaced = false;
            readyToPlace = false;
            lastUsedSlot = null;
            commands.command3.setDisable(true);
        } else {
            System.out.println("NO CARD PLACED YET");
        }
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
        } catch (IOException | InterruptedException e) {
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
        GenericTurnMessage message = ZakClient.getServerHandler().getMessageTurn();
        ResourceGoldCardController newCardPos = (ResourceGoldCardController) playerDeck.getChildren().get(deckChildIndex);
        try {
            message.printCoveredCards();
            message.printPublicCards();
            ResourceGoldCard selectedCard;
            do {
                selectedCard = pickCard(message);
            } while (selectedCard == null);
            ZakClient.getServerHandler().setMessageTurn(null);
            System.out.println("CARDSELECTED: "+selectedCard.getIdCard());
            newCardPos.setupCard(selectedCard);
            player.getPlayerDeck().getResourceGoldCards().add(selectedCard);
            ZakClient.getServerHandler().sendMessage(new GenericTurnMessage(null, null, new ArrayList<>(Collections.singletonList(selectedCard)), new ArrayList<>(Collections.singletonList(placedCardToSend)), lastUsedSlot.coords));
            System.out.println("MESSAGE SENT: SELECTED CARD #" + selectedCard.getIdCard() + " AND CARD TO PLACE IN OTHERS #" + placedCardToSend.getIdCard());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        turnButton.setDisable(true);
        ZakClient.setMyTurn(false);
        cardPlaced = false;
        readyToPlace = false;
        lastUsedSlot = null;
        cardToRemove = null;
    }

    public static void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
        alert.close();
    }

    public ResourceGoldCard pickCard(GenericTurnMessage message) throws IOException {
        AtomicReference<ResourceGoldCard> card = new AtomicReference<>();
        ArrayList<ResourceGoldCard> selectables = new ArrayList<>();
        HashMap<Integer, ResourceGoldCard> cardPicker = new HashMap<>();
        ArrayList<SelectableCardController> drawingCards = new ArrayList<>();
        selectables.addAll(message.getDrawnCard());
        selectables.addAll(message.getCardOnHand());
        for (ResourceGoldCard selectableCard : selectables) {
            cardPicker.put(selectableCard.getIdCard(), selectableCard);
            drawingCards.add(new SelectableCardController(selectableCard, message.getCardOnHand().contains(selectableCard)));
        }
        Dialog<ResourceGoldCard> dialog = new Dialog<>();
        ToggleGroup buttonGroup = new ToggleGroup();
        HBox cardsHidden = new HBox();
        HBox cardsPublic = new HBox();
        VBox verticalContent = new VBox();
        for (SelectableCardController s : drawingCards) {
            s.selectButton.setToggleGroup(buttonGroup);
        }
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.setTitle("Please draw a card from deck:");
        cardsHidden.getChildren().addAll(drawingCards.subList(0, 2));
        cardsPublic.getChildren().addAll(drawingCards.subList(2, 6));
        verticalContent.getChildren().addAll(cardsHidden, cardsPublic);
        dialog.getDialogPane().setContent(verticalContent);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                RadioButton selectedRadioButton = (RadioButton) buttonGroup.getSelectedToggle();
                if (selectedRadioButton != null) {
                    Integer cardId = (Integer) selectedRadioButton.getUserData();
                    return cardPicker.get(cardId);
                }
            }
            return null;
        });
        Optional<ResourceGoldCard> result = dialog.showAndWait();
        if (result.isPresent()) {
            ResourceGoldCard selectedOption = result.get();
            card.set(result.get());
            //dialogClosedFuture.complete(selectedOption);
            System.out.println("Selected Option: " + selectedOption);
            return selectedOption;
        } else {
            throw new RuntimeException("Clicked confirm");
        }
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
                System.out.println("SLOT CLICKED");
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
        int starterFace = player.getPlayerDeck().getStarterCard().isPlacedFront() ? 0 : 1;
        Image starter = new Image(Objects.requireNonNull(getClass().getResourceAsStream(player.getPlayerDeck().getStarterCard().getArtRef()[starterFace])));
        field.centerSlot.setSlotCardView(starter);
        playerDeck.getChildren().remove(playerDeck.getStarterCard());
        field.centerSlot.toFront();
        field.centerSlot.setEmpty(false);
        player.printManas();
    }

    private void placeCardAndRemoveFromDeck(SlotController slotToPlace) throws Exception {
        if (readyToPlace) {
            if (cardToRemove.getCard() != null) {
                if (!cardPlaced) {
                    int row = slotToPlace.getCoords().getKey();
                    int col = slotToPlace.getCoords().getValue();
                    if (player.isCardAttachableToSlot(row, col)) {
                        if (slotToPlace.isEmpty()) {
                            if (cardToRemove.getCard() instanceof GoldCard && cardToRemove.getCard().isPlacedFront()) {
                                if (!player.requirementsAreFulfilled((GoldCard) cardToRemove.getCard())) {
                                    System.out.println("REQUIREMENTS FOR GOLD CARD NOT FULFILLED");
                                    return;
                                }
                            }
                            System.out.println("PIAZZATA CARTA #" + cardToRemove.getCard().getIdCard());
                            placedCardToSend = cardToRemove.getCard();
                            System.out.println("PlacedCardToSend: #" + placedCardToSend.getIdCard());
                            slotToPlace.setSlotCardView(cardToRemove.getShownImage());
                            readyToPlace = false;
                            cardPlaced = true;
                            slotToPlace.toFront();
                            slotToPlace.setEmpty(false);
                            slotToPlace.card = cardToRemove.getCard();
                            lastUsedSlot = slotToPlace;
                            player.placeCardAndRemoveFromDeck(row, col, cardToRemove.getCard());
                            playerDeck.resetCard(deckChildIndex);
                            player.printManas();
                            commands.command3.setDisable(false);
                        } else {
                            System.out.println("SLOT GIA' OCCUPATO");
                        }
                    } else {
                        System.out.println("CARD IS NOT ATTACHABLE TO THIS SLOT");
                    }
                } else {
                    System.out.println("CARD HAS ALREADY BEEN PLACED IN THIS TURN");
                }
            } else {
                System.out.println("EMPTY CARD NOT PLACEBLE");
            }
        } else {
            System.out.println("NO CARD SELECTED FROM DECK.");
        }
    }

    public static HashMap<String, Pair<Tab, FieldController>> getTabMan() {
        return tabMan;
    }
}
