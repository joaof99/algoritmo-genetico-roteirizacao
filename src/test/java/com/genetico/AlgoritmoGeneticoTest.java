package com.genetico;

import com.genetico.model.Cromossomo;
import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;
import com.genetico.service.GraficoServiceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;

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

        var distanciasFixas = inicializarDistanciasFixas();

        calculadorDistancias.when(() -> CalculadorDistancias.getEnderecoId(anyInt()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        calculadorDistancias.when(() -> CalculadorDistancias.obterDistanciaEntreEnderecos(anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    int indiceCidadeOrigem = invocation.getArgument(0);
                    int indiceCidadeDestino = invocation.getArgument(1);
                    return distanciasFixas[indiceCidadeOrigem][indiceCidadeDestino];
                });

        graficoServiceFactory.when(GraficoServiceFactory::getGraficoService)
                .thenReturn(mock(GraficoService.class));

        var populacaoInicial = new Populacao(30, 80, 80);
        var algoritmoGenetico = new AlgoritmoGenetico(50, populacaoInicial);

        var populacaoFinal = algoritmoGenetico.reproduzir();

        var cromossomosPopulacaoFinal = populacaoFinal.getCromossomos();

        var cromossomosEsperados = Arrays.copyOf(cromossomosPopulacaoFinal, cromossomosPopulacaoFinal.length);
        Arrays.sort(cromossomosPopulacaoFinal, Comparator.comparingDouble(Cromossomo::getFitness));


        var melhorCromossomoPopulacaoInicial = populacaoInicial.getCromossomos()[0];
        var melhorCromossomoPopulacaoFinal = populacaoFinal.getCromossomos()[0];

        var melhorFitnessPopulacaoInicial = melhorCromossomoPopulacaoInicial.getFitness();
        var melhorFitnessPopulacaoFinal = melhorCromossomoPopulacaoFinal.getFitness();

        assertTrue(melhorFitnessPopulacaoFinal < melhorFitnessPopulacaoInicial, "População não evoluiu");
        assertEquals(30, populacaoFinal.getCromossomos().length);
        assertArrayEquals(cromossomosEsperados, populacaoFinal.getCromossomos());

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
