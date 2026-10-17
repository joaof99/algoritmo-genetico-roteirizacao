package com.genetico.aginterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.model.EstatisticasFitness;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoteirizacaoBodyBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String montarConclusao(EstatisticasFitness estatisticasFitness, int[] melhorRota) throws JsonProcessingException {
        var corpo = new LinkedHashMap<String, Object>();

        corpo.put("melhor_fitness_populacao_inicial", estatisticasFitness.melhorFitnessPopulacaoInicial());
        corpo.put("melhor_fitness_populacao_final", estatisticasFitness.melhorFitnessPopulacaoFinal());
        corpo.put("melhor_rota", melhorRota);

        return OBJECT_MAPPER.writeValueAsString(corpo);
    }

    public String montarFalha(String mensagem) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(Map.of("mensagem", mensagem));
    }
}
