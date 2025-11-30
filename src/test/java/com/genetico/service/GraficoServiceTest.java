package com.genetico.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class GraficoServiceTest {
    private GraficoService graficoService;
    private final String nomePastaGraficosTeste = "graficos_fitness_teste";
    private final String nomeArquivoGraficoTeste = "evolucao_fitness_teste";

    @BeforeEach
    void beforeEach() {
        var dataAtualFixa = LocalDateTime.of(2025, 11, 29, 16, 5, 3);

        try (MockedStatic<LocalDateTime> dataTeste = mockStatic(LocalDateTime.class)) {
            dataTeste.when(LocalDateTime::now).thenReturn(dataAtualFixa);

            graficoService = new GraficoService(nomePastaGraficosTeste, nomeArquivoGraficoTeste);
        }
    }

    @Test
    @DisplayName("Deve gerar o gráfico do fitness corretamente")
    public void deveGerarOGraficoDoFitnessCorretamente() {
        var geracoes = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        var valoresFitness = new double[]{100, 80, 70, 40, 25, 20, 19, 17, 16, 15};

        graficoService.gerarGraficoEvolucaoFitness(geracoes, valoresFitness);

        var nomeArquivoEsperado = graficoService.getNomePastaGraficos() + "/" + nomeArquivoGraficoTeste + "29_11_2025_16_05_03.png";
        var arquivoGrafico = new File(nomeArquivoEsperado);
        assertTrue(arquivoGrafico.exists());
        assertTrue(arquivoGrafico.delete());
    }

    @Test
    @DisplayName("Deve lancar exceção caso seja fornecido quantidades inválidas de gerações e fitness")
    public void deveLancarExecaoCasoSejaFornecidoQuantidadesInvalidasDeGeracoesEFitness() {
        var geracoes = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        var valoresFitness = new double[]{70, 40, 25, 20, 19, 17, 16, 15};

        var excecao = assertThrows(IllegalArgumentException.class, () -> graficoService.gerarGraficoEvolucaoFitness(geracoes, valoresFitness));
        assertTrue(excecao.getMessage().contains("A quantidade de gerações deve ser iguais a quantidade de valores de fitness. Há 10 gerações e 8 valores de fitness"));
    }

    @Test
    @DisplayName("Deve lancar exceção caso seja fornecido nome de arquivo inválido")
    public void deveLancarExecaoCasoSejaFornecidoNomeDeArquivoInvalido() {
        var excecao = assertThrows(IllegalArgumentException.class, () -> new GraficoService("graficos_fitness_teste", "arquivo.txt"));
        assertTrue(excecao.getMessage().contains("Nome do arquivo não deve possuir extensão, por padrão será .png"));
    }

    @Test
    @DisplayName("Índices das gerações devem estar em ordem crescente")
    public void indicesDasGeracoesDeveEstarEmOrdemCrescente() {
        var geracoes = new double[]{4, 4, 2, 1, 5, 5, 6, 7, 8, 9};
        var valoresFitness = new double[]{70, 40, 25, 20, 19, 17, 16, 15, 10, 20};

        var excecao = assertThrows(IllegalArgumentException.class, () -> graficoService.gerarGraficoEvolucaoFitness(geracoes, valoresFitness));

        assertTrue(excecao.getMessage().contains("Índice 0 com valor 4 não está na ordem. Aqui deveria ser: 0"));
    }

}
