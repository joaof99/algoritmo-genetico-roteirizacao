package com.genetico.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraficoServiceTest {
    private GraficoService graficoService;

    @BeforeEach
    void beforeEach() {
        graficoService = new GraficoService();
    }

    @Test
    @DisplayName("Deve gerar o gráfico do fitness corretamente")
    public void deveGerarOGraficoDoFitnessCorretamente() {
        var geracoes = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        var valoresFitness = new double[]{100, 80, 70, 40, 25, 20, 19, 17, 16, 15};

        graficoService.gerarGraficoEvolucaoFitness("evolucao_fitness_teste.png", geracoes, valoresFitness);

        var arquivoGrafico = new File("graficos/evolucao_fitness_teste.png");
        assertTrue(arquivoGrafico.exists());
        assertTrue(arquivoGrafico.delete());
    }

    @Test
    @DisplayName("Deve lancar exceção caso seja fornecido parâmetros inválidos")
    public void deveLancarExecaoCasoSejaFornecidoParametrosInvalidos() {
        var geracoes = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        var valoresFitness = new double[]{70, 40, 25, 20, 19, 17, 16, 15};

        var excecao = assertThrows(IllegalArgumentException.class,
                () -> graficoService.gerarGraficoEvolucaoFitness("evolucao_fitness_teste.png", geracoes, valoresFitness));

        var mensagemExecaoEsperada = "A quantidade de gerações deve ser iguais a quantidade de valores de fitness." +
                " Há 10 gerações e 8 valores de fitness";

        assertTrue(excecao.getMessage().contains(mensagemExecaoEsperada));
    }

    @Test
    @DisplayName("Índices das gerações devem estar em ordem crescente")
    public void indicesDasGeracoesDeveEstarEmOrdemCrescente() {
        var geracoes = new double[]{4, 4, 2, 1, 5, 5, 6, 7, 8, 9};
        var valoresFitness = new double[]{70, 40, 25, 20, 19, 17, 16, 15, 10, 20};

        var excecao = assertThrows(IllegalArgumentException.class,
                () -> graficoService.gerarGraficoEvolucaoFitness("evolucao_fitness_teste.png", geracoes, valoresFitness));

        assertTrue(excecao.getMessage().contains("Índice 0 com valor 4 não está na ordem. Aqui deveria ser: 0"));
    }

}
