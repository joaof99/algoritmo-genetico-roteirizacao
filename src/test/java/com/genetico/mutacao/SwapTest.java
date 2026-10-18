package com.genetico.mutacao;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class SwapTest {
    private MockedStatic<GerenciadorDistancias> gerenciadorDistancias;
    private MockedStatic<RandomizadorFactory> randomizadorFactory;
    private Random random;
    private Swap swap;

    @BeforeEach
    void setUp() {
        gerenciadorDistancias = Mockito.mockStatic(GerenciadorDistancias.class);
        gerenciadorDistancias.when(() -> GerenciadorDistancias.obterDistanciaEntreEnderecos(anyInt(), anyInt()))
                .thenReturn(OptionalDouble.of(1));

        randomizadorFactory = Mockito.mockStatic(RandomizadorFactory.class);
        random = Mockito.mock(Random.class);
        randomizadorFactory.when(RandomizadorFactory::getRandomizador)
                .thenReturn(random);

        swap = new Swap();
    }

    @AfterEach
    void tearDown() {
        randomizadorFactory.close();
        gerenciadorDistancias.close();
    }

    @ParameterizedTest
    @MethodSource("casosDeMutacaoSwap")
    @DisplayName("Deve trocar os genes dos índices sorteados")
    void deveTrocarOsGenesDosIndicesSorteados(
            int[] genesIniciais,
            int[] indicesSorteados,
            int[] genesEsperados) {
        when(random.nextInt(1, genesIniciais.length))
                .thenReturn(indicesSorteados[0], indicesSorteados[1]);

        var cromossomo = new Cromossomo(genesIniciais);

        swap.realizarMutacao(cromossomo);

        assertArrayEquals(genesEsperados, cromossomo.getGenes());
    }

    private static Stream<Arguments> casosDeMutacaoSwap() {
        return Stream.of(
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        new int[]{1, 2},
                        new int[]{0, 2, 1, 3, 4, 5, 6, 7, 8, 9}
                ),
                Arguments.of(
                        new int[]{0, 2, 1, 4, 3, 7, 6, 5, 8, 9},
                        new int[]{2, 3},
                        new int[]{0, 2, 4, 1, 3, 7, 6, 5, 8, 9}
                ),
                Arguments.of(
                        new int[]{0, 3, 2, 1, 6, 5, 4, 7, 8, 9},
                        new int[]{1, 7},
                        new int[]{0, 7, 2, 1, 6, 5, 4, 3, 8, 9}
                )
        );
    }

    @Test
    @DisplayName("Deve sortear novos índices quando os dois índices forem iguais")
    void deveSortearNovosIndicesQuandoOsDoisIndicesForemIguais() {
        when(random.nextInt(1, 5))
                .thenReturn(2, 2, 1, 4);

        var cromossomo = new Cromossomo(new int[]{0, 1, 2, 3, 4});

        swap.realizarMutacao(cromossomo);

        assertArrayEquals(new int[]{0, 4, 2, 3, 1}, cromossomo.getGenes());
    }
}
