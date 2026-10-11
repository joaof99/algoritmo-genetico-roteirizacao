package com.genetico.vaadin;

import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.EstatisticasFitness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoteirizacaoBodyBuilderTest {
    private final RoteirizacaoBodyBuilder roteirizacaoBodyBuilder = new RoteirizacaoBodyBuilder();

    @Test
    @DisplayName("Deve montar o corpo da requisição com os campos esperados pela tabela do Vaadin")
    void deveMontarCorpoRequisicaoComOsCamposEsperados() throws IOException {
        var request = new AlgoritmoGeneticoRequest(List.of(), List.of(), 10, 50, 5, 80);
        var estatisticasFitness = new EstatisticasFitness(100.0, 40.0);

        var corpo = roteirizacaoBodyBuilder.montar(request, estatisticasFitness);

        assertEquals(
                "{\"melhor_fitness_populacao_inicial\":100.0,\"melhor_fitness_populacao_final\":40.0,\"qtde_geracoes\":50,\"tamanho_populacao\":10,\"prob_crossover\":80,\"prob_mutacao\":5}",
                corpo
        );
    }
}
