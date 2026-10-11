package com.genetico.service;

import com.genetico.AlgoritmoGenetico;
import com.genetico.distancia.CalculoDistanciaAPI;
import com.genetico.distancia.GerenciadorDistancias;
import com.genetico.factory.VaadinClientFactory;
import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.Endereco;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoteirizacaoService {
    private static final Logger log = LogManager.getLogger();

    public void executar(AlgoritmoGeneticoRequest request) {
        GerenciadorDistancias.inicializar(new CalculoDistanciaAPI(), request);

        var algoritmoGenetico = new AlgoritmoGenetico.Builder()
                .tamanhoPopulacao(request.tamanhoPopulacao())
                .qtdeGeracoes(request.quantidadeGeracoes())
                .enderecos(request.enderecos().toArray(Endereco[]::new))
                .chanceOcorrenciaMutacao(request.chanceOcorrenciaMutacao())
                .chanceOcorrenciaCrossover(request.chanceOcorrenciaCrossover())
                .build();

        var populacaoFinal = algoritmoGenetico.reproduzir();

        log.info("População final:");
        populacaoFinal.imprimirPopulacao();

        VaadinClientFactory.getVaadinClient().enviarResultadoRoteirizacao(request, algoritmoGenetico.getEstatisticasFitness());
    }
}
