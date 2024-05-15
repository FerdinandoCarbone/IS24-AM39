package com.example.codexnaturalis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;


public class MainController implements Initializable {
    @FXML
    private PlayerDeckController playerDeck;
    @FXML
    private FieldController field;
//    @FXML
//    private ZoomPaneController zoomPane;
    @FXML
    Pane mainPane;
//    @FXML
//    Button zoomButton;
//    @FXML
//    PopPortChoiceController portChoice;

    private final Player player = new Player("Pippo", new Token(), new Field(5, 5), UUID.randomUUID());

    private ResourceGoldCardController cardToRemove;
    private boolean readyToPlace = false;
    private boolean starterPlaced = false;
    private boolean starterSelected = false;

    public MainController() throws IOException {
    }

    public void setupPlayerDeck() {
        playerDeck.getCard1().setupCard(player.getPlayerDeck().getResourceGoldCards().get(0));
        playerDeck.getCard2().setupCard(player.getPlayerDeck().getResourceGoldCards().get(1));
        playerDeck.getCard3().setupCard(player.getPlayerDeck().getResourceGoldCards().get(2));
        playerDeck.getStarterCard().setupCard(player.getPlayerDeck().getStarterCard());
        playerDeck.getSecretObjCard().setupCard(player.getPlayerDeck().getSecretObjectiveCard());
    }

    public void setupRGCEvents() {
        playerDeck.getCard1().setOnMouseClicked((MouseEvent event) -> {
            selectRGCFromDeck(playerDeck.getCard1());
        });
        playerDeck.getCard2().setOnMouseClicked((MouseEvent event) -> {
            selectRGCFromDeck(playerDeck.getCard2());
        });
        playerDeck.getCard3().setOnMouseClicked((MouseEvent event) -> {
            selectRGCFromDeck(playerDeck.getCard3());
        });
    }
    public void setupStarterEvent() {
        playerDeck.getStarterCard().setOnMouseClicked((MouseEvent event) -> {
            readyToPlace = true;
            starterSelected = true;
            System.out.println("CARTA STARTER SELEZIONATA");
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPlayerDeck();
        setupRGCEvents();
        setupStarterEvent();

        for (int i = 0; i < field.getChildren().size(); i++) {
            SlotController tmpSlot = (SlotController) field.getChildren().get(i);
            tmpSlot.setOnMouseClicked((MouseEvent mouseEvent) -> {
                System.out.println(tmpSlot.getCoords());
                if (starterPlaced) {
                    if (readyToPlace) {
                        try {
                            placeCardAndRemoveFromDeck(tmpSlot);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("PRIMA SELEZIONA UNA CARTA DAL DECK");
                    }
                } else {
                    System.out.println("ATTENZIONE, METTERE PRIMA LA CARTA STARTER");
                }
            });
        }

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

        playerDeck.setLayoutY(1080 - playerDeck.getDeckHeight() - 100);
        playerDeck.setLayoutX(30);


    }

    private void selectRGCFromDeck(ResourceGoldCardController card) {
        if (starterPlaced) {
            cardToRemove = card;
            readyToPlace = true;
        } else {
            System.out.println("SELEZIONE PRIMA LA CARTA STARTER");
        }
    }

    private void placeCardAndRemoveFromDeck(SlotController slotToPlace) throws Exception {
        player.getPlayerField().printField();
        if (slotToPlace.isEmpty()) {
            slotToPlace.setSlotCardView(cardToRemove.getShownImage());
            playerDeck.getChildren().remove(cardToRemove);
            readyToPlace = false;
            slotToPlace.toFront();
            //Getting row of field
            int row = slotToPlace.getCoords().getKey();
            int col = slotToPlace.getCoords().getValue();
            player.placeCardAndRemoveFromDeck(row, col, cardToRemove.getCard());
            player.getPlayerField().printField();
        } else {
            System.out.println("SLOT GIA' OCCUPATO");
        }
    }

    private void placeStarterCardAndRemoveFromDeck(SlotController slotToPlace) throws Exception {
        player.getPlayerField().printField();
        if (slotToPlace.isEmpty()) {
            slotToPlace.setSlotCardView(playerDeck.getStarterCard().getShownImage());
            playerDeck.getChildren().remove(playerDeck.getStarterCard());
            readyToPlace = false;
            slotToPlace.toFront();
            player.placeStarterCard(true);
            player.getPlayerField().printField();
        } else {
            System.out.println("SLOT GIA' OCCUPATO");
        }
    }


}
