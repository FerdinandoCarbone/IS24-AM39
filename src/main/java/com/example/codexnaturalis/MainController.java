package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;


public class MainController implements Initializable {
    public MainController() throws IOException {
    }
    @FXML
    private PlayerDeckController playerDeck;
    @FXML
    private FieldController field;
    @FXML
    private ResourceDeckController resourceDeck;
    @FXML
    private GoldDeckController goldDeck;
    @FXML
    private ObjectiveDeckController objectiveDeck;
    @FXML
    Pane mainPane;
    @FXML
    private Button nextTurnButton;
    @FXML
    private Button drawCardButton;

    private final Player player = new Player("Pippo", new Token(Token.Color.Blue), new Field(GlobalVars.matrixSize, GlobalVars.matrixSize), UUID.randomUUID());
    private ResourceGoldCardController cardToRemove;
    private boolean readyToPlace = false;
    private boolean starterPlaced = false;
    private boolean starterSelected = false;
    private final double edgeMinDistance = 25;
    private final double bottomEdgeToDeck = 100;
    private final double fieldToDeck = GlobalVars.cardWidth;
    private boolean cardPlaced = false;
    private boolean cardDrawn = false;
    private int deckChildIndex;

    private void setupPlayerDeck() {
        playerDeck.getCard1().setupCard(player.getPlayerDeck().getResourceGoldCards().get(0));
        playerDeck.getCard2().setupCard(player.getPlayerDeck().getResourceGoldCards().get(1));
        playerDeck.getCard3().setupCard(player.getPlayerDeck().getResourceGoldCards().get(2));
        playerDeck.getStarterCard().setupCard(player.getPlayerDeck().getStarterCard());
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
    private void setupStarterEvent() {
        playerDeck.getStarterCard().setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                playerDeck.getStarterCard().flipCard();
            }
            readyToPlace = true;
            starterSelected = true;
            System.out.println("CARTA STARTER SELEZIONATA");
        });
    }
    private void setupDecks() {
        for (int i = 0; i < resourceDeck.getChildren().size(); i++) {
            resourceDeck.getChildren().get(i).setCursor(Cursor.HAND);
            goldDeck.getChildren().get(i).setCursor(Cursor.HAND);
            resourceDeck.getChildren().get(i).setOnMouseClicked((MouseEvent event) -> {
                publicCardDrawCheck();
            });
            goldDeck.getChildren().get(i).setOnMouseClicked((MouseEvent event) -> {
                publicCardDrawCheck();
            });

        }
    }
    private void setupFieldSlots() {
        for (int i = 0; i < field.getChildren().size(); i++) {
            SlotController tmpSlot = (SlotController) field.getChildren().get(i);
            tmpSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (starterPlaced) {
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
                } else {
                    System.out.println("ATTENZIONE, METTERE PRIMA LA CARTA STARTER");
                }
            });
        }
    }
    private void setupCenterFieldSlot() {
        field.centerSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (starterSelected) {
                try {
                    placeStarterCardAndRemoveFromDeck(field.centerSlot);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                starterPlaced = true;
            } else {
                System.out.println("SELEZIONE PRIMA LA CARTA STARTER DAL DECK");
            }
        });
    }
    private void setupNextTurnButton() {
        nextTurnButton.setOnMouseClicked((MouseEvent event) -> {
            if (!cardPlaced || !cardDrawn) {
                if (!cardPlaced) {
                    System.out.println("CARD STILL NEEDS TO BE PLACED IN THIS TURN");
                }
                if (!cardDrawn) {
                    System.out.println("CARD STILL NEEDS TO BE DRAWN IN THIS TURN");
                }
            } else {
                System.out.println("NEXT TURN TRIGGERED");
                cardPlaced = false;
                cardDrawn = false;
            }
        });
    }
    private void setupDrawCardButton() {
        drawCardButton.setOnMouseClicked((MouseEvent event) -> {
            publicCardDrawCheck();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPlayerDeck();
        setupRGCEvents();
        setupStarterEvent();
        setupDecks();
        setupFieldSlots();
        setupCenterFieldSlot();
        setupNextTurnButton();
        setupDrawCardButton();

        field.setLayoutX(edgeMinDistance);
        field.setLayoutY(edgeMinDistance);

        playerDeck.setLayoutY(1080 - playerDeck.getContainerHeight() - bottomEdgeToDeck);
        playerDeck.setLayoutX(edgeMinDistance);

        resourceDeck.setLayoutX(field.getTotalWidth() + edgeMinDistance + fieldToDeck);
        resourceDeck.setRotate(-90.0);

        goldDeck.setLayoutX(field.getTotalWidth() + edgeMinDistance + fieldToDeck);
        goldDeck.setLayoutY(resourceDeck.getDeckWidth() + edgeMinDistance);
        goldDeck.setRotate(-90.0);

        objectiveDeck.setLayoutX(field.getTotalWidth() + edgeMinDistance + fieldToDeck);
        objectiveDeck.setLayoutY(2*resourceDeck.getDeckWidth() + 2*edgeMinDistance);
        objectiveDeck.setRotate(-90.0);

        nextTurnButton.setLayoutX(edgeMinDistance + playerDeck.getContainerWidth() + edgeMinDistance);
        nextTurnButton.setLayoutY(1080 - bottomEdgeToDeck - edgeMinDistance);
        nextTurnButton.setText("Next Turn");

        drawCardButton.setLayoutX(edgeMinDistance + playerDeck.getContainerWidth() + edgeMinDistance);
        drawCardButton.setLayoutY(1080 - bottomEdgeToDeck - 3*edgeMinDistance);
        drawCardButton.setText("Draw Card");
    }

    private void selectRGCFromDeck(ResourceGoldCardController card) {
        if (starterPlaced) {
            if (!cardPlaced) {
                cardToRemove = card;
                deckChildIndex = playerDeck.getChildren().indexOf(card);
                readyToPlace = true;
            }
        } else {
            System.out.println("SELEZIONE PRIMA LA CARTA STARTER");
        }
    }

    private void placeCardAndRemoveFromDeck(SlotController slotToPlace) throws Exception {
        if (cardToRemove.getCard() == null) {
            System.out.println("EMPTY CARD NOT PLACEBLE");
        } else {
            if (!cardPlaced) {
                int row = slotToPlace.getCoords().getKey();
                int col = slotToPlace.getCoords().getValue();
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

    private void placeStarterCardAndRemoveFromDeck(SlotController slotToPlace) throws Exception {
        player.printFieldWithName();
        if (slotToPlace.isEmpty()) {
            slotToPlace.setSlotCardView(playerDeck.getStarterCard().getShownImage());
            playerDeck.getChildren().remove(playerDeck.getStarterCard());
            readyToPlace = false;
            slotToPlace.toFront();
            player.placeStarterCard(true);
            player.printFieldWithName();
        } else {
            System.out.println("SLOT GIA' OCCUPATO");
        }
    }

    private void publicCardDrawCheck() {
        if (!cardPlaced) {
            System.out.println("TO DRAW YOU NEED TO FIRST DRAW A CARD");
        } else {
            if (!cardDrawn) {
                cardDrawn = true;
                Random random = new Random();
                ResourceGoldCardController tmpCard = (ResourceGoldCardController) playerDeck.getChildren().get(deckChildIndex);
                tmpCard.setupCard(DrawingDeck.drawCard(random.nextBoolean()));
            } else {
                System.out.println("CARD ALREADY DRAWN IN THIS TURN");
            }

        }
    }

}
