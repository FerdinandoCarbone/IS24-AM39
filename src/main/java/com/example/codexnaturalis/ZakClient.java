package com.example.codexnaturalis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ZakClient {

    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost", 8081)) {

            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToServer = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            String inputClient;
            int inputClientInt;
            String serverResponse;

            do {
                serverResponse = inputFromServer.readLine();
                System.out.println(serverResponse);
                if (validStrings(serverResponse)) {
                    inputClient = scanner.nextLine();
                    outputToServer.println(inputClient);
                }

            } while (true);

        } catch (IOException e) {
            System.out.println("PROBLEMA CLIENT");
        }

    }

    static boolean validStrings(String s) {
        boolean flag = false;
        if (s.equals("Inserisci il nome utente che userai per la partita: ") ||
            s.equals("Sei pronto a giocare ? 1) -> si | 0) -> no") ||
            s.equals("Quanti giocatori ci saranno ?") ||
            s.equals("Seleziona se vuoi piazzare la carta iniziale di fronte o retro: 1) -> Fronte | 0) -> Retro") ||
            s.equals("2) Analizza il tavolo") ||
            s.equals("Seleziona la riga della carta a cui vuoi attaccarti:") ||
            s.equals("Seleziona la colonna della carta a cui vuoi attaccarti:") ||
            s.equals("Seleziona l'angolo della carta sul tavolo a cui vuoi attaccarti (a partire da in alto a dx in senso orario 0->3): ") ||
            s.equals("2) Mazzo Oro") ||
            s.equals("Scegli la riga della carta che vuoi analizzare: ") ||
            s.equals("Scegli la colonna della carta che vuoi analizzare: ") ||
            s.equals("Seleziona il numero della carta che vuoi piazzare: ") ||
            s.equals("Seleziona se vuoi piazzarla di fronte o retro: 1) -> Fronte | 0) -> Retro")) {
            flag = true;
        }

        return flag;
    }

}


