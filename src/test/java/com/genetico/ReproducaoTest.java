package com.genetico;

import com.genetico.model.Cromossomo;
import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ReproducaoTest {

    @Test
    @DisplayName("Reprodução deve gerar população de tamanho fixo, ordenada e com fitness melhorado")
    public void reproducaoDasPopulacoesDeveOcorrerDeFormaCorreta() {
        var populacaoInicial = new Populacao(30, 80, 80);

        var reproducao = new Reproducao(50, populacaoInicial, mock(GraficoService.class));

        var populacaoFinal = reproducao.reproduzir();

        var cromossomosPopulacaoFinal = populacaoFinal.getCromossomos();

        var cromossomosEsperados = Arrays.copyOf(cromossomosPopulacaoFinal, cromossomosPopulacaoFinal.length);
        Arrays.sort(cromossomosPopulacaoFinal, Comparator.comparingInt(Cromossomo::getFitness));

        var melhorFitnessPopulacaoInicial = populacaoInicial.getCromossomos()[0].getFitness();
        var melhorFitnessPopulacaoFinal = populacaoFinal.getCromossomos()[0].getFitness();

        assertTrue(melhorFitnessPopulacaoFinal < melhorFitnessPopulacaoInicial, "População não evoluiu");
        assertEquals(30, populacaoFinal.getCromossomos().length);
        assertArrayEquals(cromossomosEsperados, populacaoFinal.getCromossomos());
    }
}
