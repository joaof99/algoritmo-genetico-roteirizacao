package com.genetico;

import com.genetico.distancia.CalculadorDistancias;
import com.genetico.distancia.Haversine;
import com.genetico.exception.RotaClientException;
import com.genetico.factory.RotaClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            var enderecos = RotaClientFactory.getRotaClient().buscarEnderecosRota(10);
            CalculadorDistancias.inicializar(new Haversine(), enderecos);

            AlgoritmoGenetico algoritmoGenetico;

            algoritmoGenetico = new AlgoritmoGenetico.Builder()
                    .tamanhoPopulacao(30)
                    .qtdeGeracoes(50)
                    .qtdeGenesCromossomo(enderecos.size())
                    .chanceOcorrenciaMutacao(50)
                    .chanceOcorrenciaCrossover(50)
                    .build();

            var populacaoFinal = algoritmoGenetico.reproduzir();

            log.info("População final: ");
            populacaoFinal.imprimirPopulacao();
        } catch (RotaClientException e) {
            log.error("Algoritmo Genético não pôde inicializar. {}", e.getMessage());
            System.exit(1);
        }
    }
}