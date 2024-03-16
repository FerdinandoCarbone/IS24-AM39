package com.example.codexnaturalis;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        String serverHostname = "localhost";
        int serverPort = 8098;
        //boolean hasSetNickname;
        try (
                Socket socket = new Socket(serverHostname, serverPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String userInputLine;
            System.out.println("Server response: " + in.readLine());
            do{
                userInputLine = userInput.readLine();
                out.println(userInputLine);

                System.out.println("Server response: " + in.readLine());
            }
            while (true);
        }
    }
}
