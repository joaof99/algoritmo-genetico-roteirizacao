package com.genetico;

import com.genetico.exception.AlgoritmoGeneticoHttpServerException;
import com.genetico.handler.AlgoritmoGeneticoHttpHandler;
import com.genetico.http.Servidor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            new Servidor(8081, "localhost", new AlgoritmoGeneticoHttpHandler()).iniciarServidor();
        } catch (AlgoritmoGeneticoHttpServerException e) {
            log.error("Algoritmo Genético não pôde inicializar. {}", e.getMessage());
            System.exit(1);
        }
    }
}