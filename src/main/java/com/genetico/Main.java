package com.genetico;

import com.genetico.distancia.Haversine;
import com.genetico.exception.RotaClientException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            AlgoritmoGenetico algoritmoGenetico;

            algoritmoGenetico = new AlgoritmoGenetico.Builder()
                    .tamanhoPopulacao(30)
                    .qtdeGeracoes(50)
                    .chanceOcorrenciaMutacao(50)
                    .chanceOcorrenciaCrossover(50)
                    .metodoCalculoDistancia(new Haversine())
                    .build();

            var populacaoFinal = algoritmoGenetico.reproduzir();
            log.info("População final: ");
            populacaoFinal.imprimirPopulacao();
        } catch (RotaClientException e) {
            log.error("Algoritmo Genético não pode inicializar: {}", e.getMessage());
            System.exit(1);
        }
    }
}