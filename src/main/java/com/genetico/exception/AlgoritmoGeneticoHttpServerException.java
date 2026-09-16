package com.genetico.exception;

public class AlgoritmoGeneticoHttpServerException extends Exception{
    public AlgoritmoGeneticoHttpServerException(String message) {
        super(message);
    }

    public AlgoritmoGeneticoHttpServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
