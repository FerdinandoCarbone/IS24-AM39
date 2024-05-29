package com.example.codexnaturalis;

public class NotSameMatchException extends HandShakeException{
    public NotSameMatchException(String s){
        super();
    }
}
class NewPlayerException extends NotSameMatchException{

    public NewPlayerException(String s) {
        super(s);
    }
}
