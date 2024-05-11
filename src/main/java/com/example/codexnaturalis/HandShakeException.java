package com.example.codexnaturalis;


public class HandShakeException extends Exception {
    public HandShakeException() {
        super();
    }
}
class TooManyPlayersException extends HandShakeException {
    public TooManyPlayersException(String errorMessage) {
        super();
    }
}
class StupidUserException extends Exception{
    public StupidUserException(String errorMsg){
        super(errorMsg);
    }
}
class ClientAbruptlyDisconnectedException extends Exception{
    public ClientAbruptlyDisconnectedException(String errorMessage) {
        super(errorMessage);
    }

}