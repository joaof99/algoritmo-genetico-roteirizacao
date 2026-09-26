package com.genetico.exception;

public class AlgoritmoGeneticoServidorException extends Exception{
    public AlgoritmoGeneticoServidorException(String message) {
        super(message);
    }

    public AlgoritmoGeneticoServidorException(String message, Throwable cause) {
        super(message, cause);
    }
}
