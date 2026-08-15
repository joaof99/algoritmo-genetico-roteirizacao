package com.genetico;

import com.genetico.model.Cromossomo;
import com.genetico.service.GraficoService;
import com.genetico.service.GraficoServiceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class AlgoritmoGeneticoTest {
    private MockedStatic<CalculadorDistancias> calculadorDistancias;
    private MockedStatic<GraficoServiceFactory> graficoServiceFactory;

    @BeforeEach
    void setUp() {
        calculadorDistancias = Mockito.mockStatic(CalculadorDistancias.class);
        graficoServiceFactory = Mockito.mockStatic(GraficoServiceFactory.class);
    }

    @AfterEach
    void tearDown() {
        calculadorDistancias.close();
        graficoServiceFactory.close();
    }

    @Test
    @DisplayName("Reprodução deve gerar população de tamanho fixo, ordenada e com fitness melhorado")
    public void reproducaoDasPopulacoesDeveOcorrerDeFormaCorreta() {
        calculadorDistancias.when(CalculadorDistancias::getQuantidadeEnderecos).thenReturn(10);
        calculadorDistancias.when(() -> CalculadorDistancias.getEnderecoId(anyInt()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var distanciasFixas = inicializarDistanciasFixas();

        calculadorDistancias.when(() -> CalculadorDistancias.obterDistanciaEntreEnderecos(anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    int indiceCidadeOrigem = invocation.getArgument(0);
                    int indiceCidadeDestino = invocation.getArgument(1);
                    return distanciasFixas[indiceCidadeOrigem][indiceCidadeDestino];
                });

        var graficoService = mock(GraficoService.class);

        graficoServiceFactory.when(GraficoServiceFactory::getGraficoService)
                .thenReturn(graficoService);

        var melhoresFitnessCaptor = ArgumentCaptor.forClass(double[].class);

        var algoritmoGenetico = new AlgoritmoGenetico(30, 50, 80, 80);
        var populacaoFinal = algoritmoGenetico.reproduzir();

        verify(graficoService).gerarGraficoEvolucaoFitness(
                any(),
                melhoresFitnessCaptor.capture()
        );


        var cromossomosPopulacaoFinal = populacaoFinal.getCromossomos();

        var cromossomosOrdenados = Arrays.copyOf(cromossomosPopulacaoFinal, cromossomosPopulacaoFinal.length);
        Arrays.sort(cromossomosOrdenados, Comparator.comparingDouble(Cromossomo::getFitness));

        var melhoresFitness = melhoresFitnessCaptor.getValue();
        var melhorFitnessPopulacaoInicial = melhoresFitness[0];

        var melhorFitnessEncontrado = Arrays.stream(melhoresFitness)
                .min()
                .orElseThrow();

        assertTrue(melhorFitnessEncontrado < melhorFitnessPopulacaoInicial, "Não houve evolução no algoritmo genético");
        assertEquals(30, populacaoFinal.getCromossomos().length);
        assertArrayEquals(cromossomosOrdenados, populacaoFinal.getCromossomos());
    }

    private static double[][] inicializarDistanciasFixas() {
        return new double[][]{
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
    }
}
