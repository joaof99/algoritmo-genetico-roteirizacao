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
        var distancias = new int[10][10];

        distancias[0][0] = 10;
        distancias[0][1] = 10;
        distancias[0][2] = 20;
        distancias[0][3] = 30;
        distancias[0][4] = 40;
        distancias[0][5] = 50;
        distancias[0][6] = 60;
        distancias[0][7] = 70;
        distancias[0][8] = 80;
        distancias[0][9] = 90;

        distancias[1][0] = 15;
        distancias[1][1] = 15;
        distancias[1][2] = 15;
        distancias[1][3] = 25;
        distancias[1][4] = 35;
        distancias[1][5] = 45;
        distancias[1][6] = 55;
        distancias[1][7] = 65;
        distancias[1][8] = 75;
        distancias[1][9] = 85;

        distancias[2][0] = 10;
        distancias[2][1] = 10;
        distancias[2][2] = 10;
        distancias[2][3] = 30;
        distancias[2][4] = 20;
        distancias[2][5] = 30;
        distancias[2][6] = 40;
        distancias[2][7] = 50;
        distancias[2][8] = 60;
        distancias[2][9] = 70;

        distancias[3][0] = 5;
        distancias[3][1] = 5;
        distancias[3][2] = 5;
        distancias[3][3] = 5;
        distancias[3][4] = 5;
        distancias[3][5] = 15;
        distancias[3][6] = 25;
        distancias[3][7] = 35;
        distancias[3][8] = 45;
        distancias[3][9] = 55;

        distancias[4][0] = 10;
        distancias[4][1] = 10;
        distancias[4][2] = 10;
        distancias[4][3] = 10;
        distancias[4][4] = 10;
        distancias[4][5] = 10;
        distancias[4][6] = 20;
        distancias[4][7] = 30;
        distancias[4][8] = 40;
        distancias[4][9] = 50;

        distancias[5][0] = 5;
        distancias[5][1] = 5;
        distancias[5][2] = 5;
        distancias[5][3] = 5;
        distancias[5][4] = 5;
        distancias[5][5] = 5;
        distancias[5][6] = 5;
        distancias[5][7] = 15;
        distancias[5][8] = 25;
        distancias[5][9] = 35;

        distancias[6][0] = 10;
        distancias[6][1] = 10;
        distancias[6][2] = 10;
        distancias[6][3] = 10;
        distancias[6][4] = 10;
        distancias[6][5] = 10;
        distancias[6][6] = 10;
        distancias[6][7] = 10;
        distancias[6][8] = 20;
        distancias[6][9] = 30;

        distancias[7][0] = 5;
        distancias[7][1] = 5;
        distancias[7][2] = 5;
        distancias[7][3] = 5;
        distancias[7][4] = 5;
        distancias[7][5] = 5;
        distancias[7][6] = 5;
        distancias[7][7] = 5;
        distancias[7][8] = 5;
        distancias[7][9] = 15;

        distancias[8][0] = 10;
        distancias[8][1] = 10;
        distancias[8][2] = 10;
        distancias[8][3] = 10;
        distancias[8][4] = 10;
        distancias[8][5] = 10;
        distancias[8][6] = 10;
        distancias[8][7] = 10;
        distancias[8][8] = 10;
        distancias[8][9] = 10;

        distancias[9][0] = 10;
        distancias[9][1] = 10;
        distancias[9][2] = 10;
        distancias[9][3] = 10;
        distancias[9][4] = 10;
        distancias[9][5] = 10;
        distancias[9][6] = 10;
        distancias[9][7] = 10;
        distancias[9][8] = 10;
        distancias[9][9] = 10;

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

        for (int gene : cromossomo.getGenes()) {
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

            assertEquals(genesEsperadosFilho1, filhos.getFirst().formatarGenes());
            assertEquals(genesEsperadosFilho2, filhos.getLast().formatarGenes());
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
