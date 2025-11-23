package com.genetico;

import com.genetico.model.Cromossomo;
import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ReproducaoTest {

    @Test
    @DisplayName("Reprodução deve gerar população de tamanho fixo, ordenada e com fitness melhorado")
    public void reproducaoDasPopulacoesDeveOcorrerDeFormaCorreta() {
        var populacaoInicial = new Populacao(30, 80, 80);

        var reproducao = new Reproducao(50, populacaoInicial, new GraficoService("graficos_fitness_teste", "evolucao_fitness_teste.png"));

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

    @AfterAll
    public static void afterAll() {
        excluirDiretoriosTeste();
    }

    private static void excluirDiretoriosTeste() {
        File diretorioGraficos = null;

        try {
            diretorioGraficos = new File("graficos_fitness_teste");

            if (diretorioGraficos.exists() && diretorioGraficos.isDirectory()) {
                for (var arquivo : Objects.requireNonNull(diretorioGraficos.listFiles())) {
                    Files.delete(arquivo.toPath());
                }

                Files.delete(diretorioGraficos.toPath());
            }
        } catch (IOException e) {
            System.out.println("Houve um erro de entrada e saída ao deletar " + diretorioGraficos.getPath());
            throw new RuntimeException(e);
        }
    }
}
