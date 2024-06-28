package com.example.codexnaturalis;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Arrays;

public class Launcher {
    /**
     * Given inputs, decides whether to launch server or client
     * @param args
     * @throws MalformedURLException
     * @throws RemoteException
     */
    public static void main(String[] args) throws MalformedURLException, RemoteException {
        int argNum = Arrays.stream(args).toList().size();
        try {
            if (args[0].equalsIgnoreCase("--server") && (argNum == 2 || argNum == 1)) Server.main(args);
            else if (args[0].equalsIgnoreCase(("--client")) && (argNum == 2 || argNum == 3 || argNum == 4))
                Client.main(args);
        } catch (Exception E) {
            printError(E);
            System.exit(0);
        }
        printError(null);
    }
    private static void printError(Exception e){
        if(e!= null){
            System.out.println("Wrong format: " + e.getMessage());
            e.printStackTrace();
        }
        else System.out.println("Wrong format:");
        System.out.println("To start a server instance pass parameters like this: \"java -jar CodexNaturalis.jar --server <Server Port>\"");
        System.out.println("To start a client instance pass parameters like this: \"java -jar CodexNaturalis.jar --client <Server Address> <Server Port>\"");
    }
}
