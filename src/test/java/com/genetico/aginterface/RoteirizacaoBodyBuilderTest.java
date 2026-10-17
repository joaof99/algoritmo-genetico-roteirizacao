package com.genetico.aginterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.genetico.model.EstatisticasFitness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoteirizacaoBodyBuilderTest {
    private final RoteirizacaoBodyBuilder roteirizacaoBodyBuilder = new RoteirizacaoBodyBuilder();

    @Test
    @DisplayName("Deve montar o corpo da conclusão com os fitness e a melhor rota")
    void deveMontarCorpoDaConclusaoComFitnessEMelhorRota() throws JsonProcessingException {
        var corpo = roteirizacaoBodyBuilder.montarConclusao(new EstatisticasFitness(100.0, 40.0), new int[]{30, 10, 20});

        assertEquals(
                "{\"melhor_fitness_populacao_inicial\":100.0,\"melhor_fitness_populacao_final\":40.0,\"melhor_rota\":[30,10,20]}",
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
