package com.genetico;

import com.genetico.service.RotaClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        CalculadorDistancias.inicializar(new RotaClient(), new Haversine());
        var populacaoFinal = new AlgoritmoGenetico(30, 50, 50, 50).reproduzir();
        log.info("População final: ");
        populacaoFinal.imprimirPopulacao();
    }
}