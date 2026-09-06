package com.genetico.exception;

public class AlgoritmoGeneticoClientException extends Exception{
    public AlgoritmoGeneticoClientException(String message) {
        super(message);
    }

    public AlgoritmoGeneticoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
