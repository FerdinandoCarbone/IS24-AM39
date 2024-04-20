package com.example.codexnaturalis;

public class WrongMessageException extends Exception{
    public WrongMessageException(String errorMessage) {
        super(errorMessage);
    }
}
class WrongMessageConversionException extends WrongMessageException{

    public WrongMessageConversionException(String errorMessage) {
        super(errorMessage);
    }
}