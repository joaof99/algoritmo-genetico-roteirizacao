package com.genetico.crossover;

import com.genetico.distancia.GerenciadorDistancias;
import com.genetico.factory.RandomizadorFactory;
import com.genetico.model.Cromossomo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.OptionalDouble;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class CrossoverPMXTest {
    private MockedStatic<GerenciadorDistancias> gerenciadorDistancias;
    private MockedStatic<RandomizadorFactory> randomizadorFactory;
    private Random random;
    private CrossoverPMX crossoverPMX;

    @BeforeEach
    void setUp() {
        gerenciadorDistancias = Mockito.mockStatic(GerenciadorDistancias.class);
        gerenciadorDistancias.when(() -> GerenciadorDistancias.obterDistanciaEntreEnderecos(anyInt(), anyInt()))
                .thenReturn(OptionalDouble.of(1));

        randomizadorFactory = Mockito.mockStatic(RandomizadorFactory.class);
        random = Mockito.mock(Random.class);
        randomizadorFactory.when(RandomizadorFactory::getRandomizador)
                .thenReturn(random);

        crossoverPMX = new CrossoverPMX();
    }

    @AfterEach
    void tearDown() {
        randomizadorFactory.close();
        gerenciadorDistancias.close();
    }

    @ParameterizedTest
    @MethodSource("casosDeCrossover")
    @DisplayName("Deve gerar filhos PMX com os genes esperados")
    void deveGerarFilhosPmxComOsGenesEsperados(
            int[] genesPai1,
            int[] genesPai2,
            int[] pontosDeCorte,
            int[] genesEsperadosFilho1,
            int[] genesEsperadosFilho2) {
        when(random.nextInt(anyInt()))
                .thenReturn(pontosDeCorte[0], pontosDeCorte[1]);

        var filhos = crossoverPMX.realizarCrossover(new Cromossomo(genesPai1), new Cromossomo(genesPai2));

        assertArrayEquals(genesEsperadosFilho1, filhos[0].getGenes());
        assertArrayEquals(genesEsperadosFilho2, filhos[1].getGenes());
    }

    private static Stream<Arguments> casosDeCrossover() {
        return Stream.of(
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        new int[]{0, 4, 3, 2, 1, 7, 6, 5, 8, 9},
                        new int[]{3, 5},
                        new int[]{0, 4, 2, 3, 1, 7, 6, 5, 8, 9},
                        new int[]{0, 1, 3, 2, 4, 5, 6, 7, 8, 9}
                ),
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7},
                        new int[]{0, 3, 2, 1, 4, 5, 6, 7},
                        new int[]{0, 3},
                        new int[]{0, 3, 2, 1, 4, 5, 6, 7},
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7}
                ),
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        new int[]{0, 3, 2, 1, 4, 5, 6, 7, 8, 9},
                        new int[]{1, 5},
                        new int[]{0, 3, 2, 1, 4, 5, 6, 7, 8, 9},
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
                )
        );
    }

    @Test
    @DisplayName("Deve rejeitar pais com quantidades diferentes de genes")
    void deveRejeitarPaisComQuantidadesDiferentesDeGenes() {
        var pai1 = new Cromossomo(new int[]{0, 1, 2});
        var pai2 = new Cromossomo(new int[]{0, 1});

        assertThrows(IllegalArgumentException.class, () -> crossoverPMX.realizarCrossover(pai1, pai2));
    }
}
