package com.example.codexnaturalis;

public class WrongPlayerException extends Exception{
    public WrongPlayerException(String errorMessage) {
        super(errorMessage);
    }
}
class WrongPlayerUUIDException extends WrongPlayerException{
    public WrongPlayerUUIDException(String errorMessage) {
        super(errorMessage);
    }
}