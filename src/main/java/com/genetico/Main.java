package com.genetico;

import com.genetico.service.RotaClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        var algoritmoGenetico = new AlgoritmoGenetico.Builder()
                .tamanhoPopulacao(30)
                .qtdeGeracoes(50)
                .chanceOcorrenciaMutacao(50)
                .chanceOcorrenciaMutacao(50)
                .metodoCalculoDistancia(new Haversine())
                .build();

        var populacaoFinal = algoritmoGenetico.reproduzir();
        log.info("População final: ");
        populacaoFinal.imprimirPopulacao();
    }
}