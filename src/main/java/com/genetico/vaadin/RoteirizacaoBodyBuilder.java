package com.genetico.vaadin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.EstatisticasFitness;

import java.io.IOException;
import java.util.LinkedHashMap;

public class RoteirizacaoBodyBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String montar(AlgoritmoGeneticoRequest request, EstatisticasFitness estatisticasFitness) throws IOException {
        var corpo = new LinkedHashMap<String, Object>();
        corpo.put("melhor_fitness", estatisticasFitness.melhorFitnessPopulacaoFinal());
        corpo.put("pior_fitness", estatisticasFitness.melhorFitnessPopulacaoInicial());
        corpo.put("qtde_geracoes", request.quantidadeGeracoes());
        corpo.put("tamanho_populacao", request.tamanhoPopulacao());
        corpo.put("prob_crossover", request.chanceOcorrenciaCrossover());
        corpo.put("prob_mutacao", request.chanceOcorrenciaMutacao());

        return OBJECT_MAPPER.writeValueAsString(corpo);
    }
}
