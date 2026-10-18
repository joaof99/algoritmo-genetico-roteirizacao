package com.genetico.aginterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.genetico.model.EstatisticasFitness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoteirizacaoBodyBuilderTest {
    private final RoteirizacaoBodyBuilder roteirizacaoBodyBuilder = new RoteirizacaoBodyBuilder();

    @Test
    @DisplayName("Deve montar o corpo da conclusão com os fitness, a melhor rota e o melhor fitness de cada geração")
    void deveMontarCorpoDaConclusaoComFitnessMelhorRotaEMelhorFitnessDeCadaGeracao() throws JsonProcessingException {
        var corpo = roteirizacaoBodyBuilder.montarConclusao(new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});

        assertEquals(
                "{\"melhor_fitness_populacao_inicial\":100.0,\"melhor_fitness_populacao_final\":40.0,\"melhor_rota\":[30,10,20],\"melhores_fitness_por_geracao\":[100.0,70.0,40.0]}",
                corpo
        );
    }

    @Test
    @DisplayName("Deve montar o corpo da falha com a mensagem informada")
    void deveMontarCorpoDaFalhaComMensagemInformada() throws JsonProcessingException {
        var corpo = roteirizacaoBodyBuilder.montarFalha("Distância não encontrada entre os endereços 1 e 2");

        assertEquals("{\"mensagem\":\"Distância não encontrada entre os endereços 1 e 2\"}", corpo);
    }
}
