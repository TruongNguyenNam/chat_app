package com.example.chatappzalo.infrastructure.exceptions;

public class ErrorException extends RuntimeException{
    public ErrorException(String errorCode) {
        super(errorCode);
    }


}
