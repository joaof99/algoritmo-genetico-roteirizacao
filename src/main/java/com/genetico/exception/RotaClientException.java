package com.genetico.exception;

public class RotaClientException extends RuntimeException{
    public RotaClientException(String message) {
        super(message);
    }

    public RotaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
