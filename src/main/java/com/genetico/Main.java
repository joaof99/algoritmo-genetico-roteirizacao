package com.genetico;

import com.genetico.distancia.CalculadorDistancias;
import com.genetico.distancia.CalculoDistanciaAPI;
import com.genetico.exception.AlgoritmoGeneticoClientException;
import com.genetico.factory.AlgoritmoGeneticoClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            var algoritmoGeneticoResponse = AlgoritmoGeneticoClientFactory.getAlgoritmoGeneticoClient().buscarDadosAlgoritmoGenetico(3);
            CalculadorDistancias.inicializar(new CalculoDistanciaAPI(), algoritmoGeneticoResponse);

            AlgoritmoGenetico algoritmoGenetico;

            algoritmoGenetico = new AlgoritmoGenetico.Builder()
                    .tamanhoPopulacao(30)
                    .qtdeGeracoes(50)
                    .qtdeGenesCromossomo(algoritmoGeneticoResponse.enderecos().size())
                    .chanceOcorrenciaMutacao(50)
                    .chanceOcorrenciaCrossover(50)
                    .build();

            var populacaoFinal = algoritmoGenetico.reproduzir();

            log.info("População final: ");
            populacaoFinal.imprimirPopulacao();
        } catch (AlgoritmoGeneticoClientException e) {
            log.error("Algoritmo Genético não pôde inicializar. {}", e.getMessage());
            System.exit(1);
        }
    }
}