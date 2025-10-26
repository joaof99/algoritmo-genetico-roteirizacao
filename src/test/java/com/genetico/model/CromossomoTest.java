package com.genetico.model;

import com.genetico.CalculadorDistancias;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CromossomoTest {

    @ParameterizedTest
    @MethodSource("casosDeTesteParaCalculoFitness")
    @DisplayName("Valor do fitness deve ser calculado corretamente")
    public void valorDoFitnessDeveSeCalculadoCorretamente(int[] genesFixos, int[][] distanciasFixas, int fitnessEsperado) {
        try (var calculadorDeDistancias = mockStatic(CalculadorDistancias.class)) {
            calculadorDeDistancias.when(() -> CalculadorDistancias.obterDistanciaEntreDuasCidades(anyInt(), anyInt()))
                    .thenAnswer(invocation -> {
                        int indiceCidadeOrigem = invocation.getArgument(0);
                        int indiceCidadeDestino = invocation.getArgument(1);
                        return distanciasFixas[indiceCidadeOrigem][indiceCidadeDestino];
                    });

            assertEquals(fitnessEsperado, new Cromossomo(genesFixos).getFitness());
        }
    }

    private static Stream<Arguments> casosDeTesteParaCalculoFitness() {
        return Stream.of(
                Arguments.of(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        inicializarDistanciasFixas(),
                        100
                ),
                Arguments.of(new int[]{0, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                        inicializarDistanciasFixas(),
                        155
                )
        );
    }

    private static int[][] inicializarDistanciasFixas() {
        int[][] distancias = {
                {10, 10, 20, 30, 40, 50, 60, 70, 80, 90},
                {15, 15, 15, 25, 35, 45, 55, 65, 75, 85},
                {10, 10, 10, 30, 20, 30, 40, 50, 60, 70},
                {5, 5, 5, 5, 5, 15, 25, 35, 45, 55},
                {10, 10, 10, 10, 10, 10, 20, 30, 40, 50},
                {5, 5, 5, 5, 5, 5, 5, 15, 25, 35},
                {10, 10, 10, 10, 10, 10, 10, 10, 20, 30},
                {5, 5, 5, 5, 5, 5, 5, 5, 5, 15},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        };

        return distancias;
    }

    @Test
    @DisplayName(value = "Genes devem ser inicializados com o tamanho correto definido na constante")
    public void genesDevemSerInicializadosComOTamanhoCorreto() {
        assertEquals(Cromossomo.QTDE_MAXIMA_GENES, new Cromossomo().getGenes().length);
    }

    @Test
    @DisplayName(value = "Genes devem ser formatados corretamente com caracter delimitador: |")
    public void genesDevemSerFormatadosCorretamenteAoImprimir() {
        boolean impressaoCromossomoEstaNoPadrao = new Cromossomo()
                .formatarGenes()
                .matches("^(\\d+\\s\\|\\s)*\\d+$");

        assertTrue(impressaoCromossomoEstaNoPadrao);
    }

    @Test
    @DisplayName(value = "Não deve existir genes repetidos em um cromossomo")
    public void naoDeveExistirGenesRepetidosNoCromossomo() {
        var cromossomo = new Cromossomo();

        var genesSemRepeticao = new HashSet<>();

        for (var gene : cromossomo.getGenes()) {
            boolean foiAdicionado = genesSemRepeticao.add(gene);

            assertTrue(foiAdicionado);
        }
    }

    @Test
    @DisplayName(value = "O gene de origem do cromossomo deve sempre ser igual a 0")
    public void oGeneOrigemDoCromossomoDeveSerSempreZero() {
        assertEquals(0, new Cromossomo().getGenes()[0]);
    }

    @ParameterizedTest
    @MethodSource("casosDeTesteParaCrossoverPmx")
    @DisplayName("Deve realizar crossover PMX corretamente com diferentes combinações de genes")
    public void deveRealizarComSucessoOCrossoverPmx(
            int[] genesFixos1,
            int[] genesFixos2,
            int[] pontosCorteFixos,
            String genesEsperadosFilho1,
            String genesEsperadosFilho2) {
        var distanciasFixas = inicializarDistanciasFixas();

        try (var calculadorDeDistancias = mockStatic(CalculadorDistancias.class)) {
            calculadorDeDistancias.when(() -> CalculadorDistancias.obterDistanciaEntreDuasCidades(anyInt(), anyInt()))
                    .thenAnswer(invocation -> {
                        int indiceCidadeOrigem = invocation.getArgument(0);
                        int indiceCidadeDestino = invocation.getArgument(1);
                        return distanciasFixas[indiceCidadeOrigem][indiceCidadeDestino];
                    });

            var random = mock(Random.class);
            when(random.nextInt(anyInt())).thenReturn(pontosCorteFixos[0], pontosCorteFixos[1]);

            var pai1 = new Cromossomo(genesFixos1) {
                @Override
                public Random getRandomizador() {
                    return random;
                }
            };

            var pai2 = new Cromossomo(genesFixos2);

            var filhos = pai1.realizarCrossoverPmx(pai2);

            assertEquals(genesEsperadosFilho1, filhos[0].formatarGenes());
            assertEquals(genesEsperadosFilho2, filhos[filhos.length-1].formatarGenes());
        }
    }

    private static Stream<Arguments> casosDeTesteParaCrossoverPmx() {
        return Stream.of(
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        new int[]{0, 4, 3, 2, 1, 7, 6, 5, 8, 9},
                        new int[]{3, 5},
                        "0 | 4 | 2 | 3 | 1 | 7 | 6 | 5 | 8 | 9 | 200",
                        "0 | 1 | 3 | 2 | 4 | 5 | 6 | 7 | 8 | 9 | 100"
                ),
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7},
                        new int[]{0, 3, 2, 1, 4, 5, 6, 7},
                        new int[]{0, 3},
                        "0 | 3 | 2 | 1 | 4 | 5 | 6 | 7 | 105",
                        "0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 85"
                ),
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        new int[]{0, 3, 2, 1, 4, 5, 6, 7, 8, 9},
                        new int[]{1, 5},
                        "0 | 3 | 2 | 1 | 4 | 5 | 6 | 7 | 8 | 9 | 120",
                        "0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 100"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("casosDeTesteParaMutacaoSwap")
    @DisplayName("Deve realizar mutação swap corretamente com diferentes combinações de genes")
    public void deveRealizarMutacaoSwapCorretamente(int[] genesCromossomoInicial, int indiceAleatorioGene1, int indiceAleatorioGene2, String formatacaoEsperadaCromossomo) {
        var random = mock(Random.class);

        var cromossomo = new Cromossomo(genesCromossomoInicial) {
            @Override
            public Random getRandomizador() {
                return random;
            }

            @Override
            public int getFitness() {
                return 1000;
            }
        };

        when(random.nextInt(eq(1), anyInt()))
                .thenReturn(indiceAleatorioGene1)
                .thenReturn(indiceAleatorioGene2);

        cromossomo.realizarMutacaoSwap();
        assertEquals(formatacaoEsperadaCromossomo, cromossomo.formatarGenes());
    }

    private static Stream<Arguments> casosDeTesteParaMutacaoSwap() {
        return Stream.of(
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        1,
                        2,
                        "0 | 2 | 1 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 1000"
                ),
                Arguments.of(
                        new int[]{0, 2, 1, 4, 3, 7, 6, 5, 8, 9},
                        2,
                        3,
                        "0 | 2 | 4 | 1 | 3 | 7 | 6 | 5 | 8 | 9 | 1000"
                ),
                Arguments.of(
                        new int[]{0, 9, 3, 2, 4, 5, 6, 7, 8, 1},
                        0,
                        9,
                        "1 | 9 | 3 | 2 | 4 | 5 | 6 | 7 | 8 | 0 | 1000"
                ),
                Arguments.of(
                        new int[]{0, 3, 2, 1, 6, 5, 4, 7, 8, 9},
                        1,
                        7,
                        "0 | 7 | 2 | 1 | 6 | 5 | 4 | 3 | 8 | 9 | 1000"
                )
        );
    }
}
