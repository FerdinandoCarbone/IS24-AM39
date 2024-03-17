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
            s.equals("Quanti giocatori ci saranno ?")) {
            flag = true;
        }

        return flag;
    }

}


