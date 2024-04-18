package com.example.codexnaturalis;


public class HandShakeException extends Exception {
    public HandShakeException(String errorMessage) {
        super(errorMessage);
    }
}
class TooManyPlayersException extends HandShakeException {
    public TooManyPlayersException(String errorMessage) {
        super(errorMessage);
    }
}
class StupidUserException extends Exception{
    public StupidUserException(String errorMsg){
        super(errorMsg);
    }
}