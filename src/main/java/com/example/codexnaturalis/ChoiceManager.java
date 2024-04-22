//package com.example.codexnaturalis;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//public class ChoiceManager {
//
//    private BufferedReader in;
//    private PrintWriter out;
//
//    public ChoiceManager(BufferedReader in, PrintWriter out) {
//        this.in = in;
//        this.out = out;
//    }
//
//    /**
//     * Chooses whether the user wants to place the front or back face up
//     * @return boolean, true if face is up, otherwise false
//     */
//    public boolean chooseFrontOrBack() throws IOException {
//        int scelta = -1;
//        while (scelta != 0 && scelta != 1) {
//            out.println("Seleziona se vuoi piazzare la carta di fronte o retro: 1) -> Fronte | 0) -> Retro");
//            try {
//                scelta = Integer.parseInt(in.readLine());
//            } catch (NumberFormatException e) {
//                out.println("NOT A NUMBER: RIPROVA");
//                scelta = -1;
//                continue;
//            }
//            if (scelta != 0 && scelta != 1) {
//                out.println("ERRORE: SCELTA OUT OF BOUNDS");
//            }
//        }
//        return scelta == 1;
//    }
//
//    /**
//     * Prints a menu giving choices to the player that playing
//     * @param endTurn: if true, gives the end turn menu, if false it gives the option to place the card
//     * @return int, player's choice
//     */
//    public int chooseFromMenu(boolean endTurn) throws IOException {
//        int scelta = -2;
//
//        if (!endTurn) {
//            while (scelta != 1 && scelta != 2) {
//                out.println("Cosa vuoi fare ?");
//                out.println("1) Piazza una Carta");
//                out.println("2) Analizza il tavolo");
//                try {
//                    scelta = Integer.parseInt(in.readLine());
//                } catch (NumberFormatException e) {
//                    out.println("NOT A NUMBER: RIPROVA");
//                    scelta = -2;
//                    continue;
//                }
//                if (scelta != 1 && scelta != 2) {
//                    out.println("ERRORE: SCELTA OUT OF BOUNDS");
//                }
//            }
//        } else {
//            while (scelta != -1 && scelta != 2) {
//                out.println("Cosa vuoi fare ?");
//                out.println("-1) Finisci il turno");
//                out.println("2) Analizza il tavolo");
//                try {
//                    scelta = Integer.parseInt(in.readLine());
//                } catch (NumberFormatException e) {
//                    out.println("NOT A NUMBER: RIPROVA");
//                    scelta = -2;
//                    continue;
//                }
//                if (scelta != -1 && scelta != 2) {
//                    out.println("ERRORE: SCELTA OUT OF BOUNDS");
//                }
//            }
//
//        }
//
//        return scelta;
//    }
//    /**
//     * The player chooses which card from its deck to place
//     * @return int, the player's choice
//     */
//    public int chooseCard() throws IOException {
//        int scelta = -1;
//        while (scelta < 1 || scelta > 3) {
//            out.println("Seleziona il numero della carta che vuoi piazzare: ");
//            try {
//                scelta = Integer.parseInt(in.readLine());
//            } catch (NumberFormatException e) {
//                out.println("NOT A NUMBER: RIPROVA");
//                scelta = -1;
//                continue;
//            }
//            if (scelta < 1 || scelta > 3) {
//                out.println("ERRORE: INDICE OUT OF BOUNDS");
//            }
//        }
//        return scelta;
//
//    }
//    /**
//     * The player chooses the row in its field
//     * @return int, defines the row on the field
//     */
//    public int chooseRow(Field field) throws IOException {
//        int scelta = -1;
//        while ((scelta < 0 || scelta >= field.getR())) {
//            out.println("Seleziona la riga:");
//            try {
//                scelta = Integer.parseInt(in.readLine());
//            } catch (NumberFormatException e) {
//                out.println("NOT A NUMBER: RIPROVA");
//                scelta = -1;
//                continue;
//            }
//            if (scelta < 0 || scelta >= field.getR()) {
//                out.println("ERRORE: RIGA OUT OF BOUNDS");
//            }
//
//        }
//        return scelta;
//    }
//    /**
//     * The player chooses the column in its field
//     * @return int, defines the column on the field
//     */
//    public int chooseColumn(Field field) throws IOException {
//        int scelta = -1;
//        while (scelta < 0 || scelta >= field.getC()) {
//            out.println("Seleziona la colonna:");
//            try {
//                scelta = Integer.parseInt(in.readLine());
//            } catch (NumberFormatException e) {
//                out.println("NOT A NUMBER: RIPROVA");
//                scelta = -1;
//                continue;
//            }
//            if (scelta < 0 || scelta >= field.getC()) {
//                out.println("ERRORE: COLONNA OUT OF BOUNDS");
//            }
//        }
//        return scelta;
//    }
//
//    /**
//     * The player chooses the corner where he wants to place the card
//     * @param cardInField: Card that's on the table
//     * @return int: corner chosen from the card in the field
//     */
//    public int chooseCorner(NonObjectiveCard cardInField) throws IOException {
//        boolean cornerFlag = false;
//        int scelta = 0;
//        while (!cornerFlag) {
//            scelta = -1;
//            while (scelta < 0 || scelta > 3) {
//                out.println("Seleziona l'angolo della carta sul tavolo a cui vuoi attaccarti (a partire da in alto a dx in senso orario 0->3): ");
//                try {
//                    scelta = Integer.parseInt(in.readLine());
//                } catch (NumberFormatException e) {
//                    out.println("NOT A NUMBER: RIPROVA");
//                    scelta = -1;
//                    continue;
//                }
//                if (scelta < 0 || scelta > 3) {
//                    out.println("ERRORE: INDICE CORNER OUT OF BOUNDS");
//                }
//            }
//            cornerFlag = checkCornerLegitness(cardInField, scelta);
//        }
//        return scelta;
//    }
//    /**
//     * Checks whether the card's corner is available
//     * @param card: card to check
//     * @param corner: corner to check
//     * @return boolean, true if corner is available, otherwise false
//     */
//    private boolean checkCornerLegitness(NonObjectiveCard card, int corner) {
//        boolean flag = true;
//        if (card.isPlacedFront()) {
//            if (!card.getFrontCorners().get(corner).isAvailableCorner()) {
//                flag = false;
//                out.println("ERRORE: ANGOLO NON DISPONIBILE");
//            }
//        } else {
//            if (!card.getBackCorners().get(corner).isAvailableCorner()) {
//                flag = false;
//                out.println("ERRORE: ANGOLO NON DISPONIBILE");
//            }
//        }
//        return flag;
//
//    }
//
//    /**
//     * The player chooses which deck to draw from
//     * @return int, choice of deck, 1 for Resource Deck, 2 for Gold deck
//     */
//    public int chooseDecksToDraw() throws IOException {
//        int scelta = -1;
//        boolean flagMazzo = false;
//        while (scelta != 1 && scelta != 2) {
//            out.println("Pesca una carta dai mazzi:");
//            out.println("1) Mazzo Resource");
//            out.println("2) Mazzo Oro");
//            try {
//                scelta = Integer.parseInt(in.readLine());
//            } catch (NumberFormatException e) {
//                out.println("NOT A NUMBER: RIPROVA");
//                scelta = -1;
//                continue;
//            }
//
//            if (scelta != 1 && scelta != 2) {
//                out.println("ERRORE: SCELTA OUT OF BOUNDS");
//            }
//        }
//        return scelta;
//    }
//
//    public void setIn(BufferedReader in) {
//        this.in = in;
//    }
//
//    public void setOut(PrintWriter out) {
//        this.out = out;
//    }
//}
